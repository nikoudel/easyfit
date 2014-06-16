/*******************************************************************************
 * Copyright (c) Nikolai Koudelia
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Nikolai Koudelia - initial API and implementation
 *******************************************************************************/
package easyfit;

import java.io.*;
import java.net.*;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Level;

/**
 * Handles the actual communication with SUT over http connection.
 */
public class HttpConnection
{
    private static final Logger logger = LogManager.getLogger();

    private final String charset = "UTF-8";
    private String baseURL;

    public HttpConnection(String baseURL) throws StopTestException
    {
        if (baseURL == null || baseURL == "")
        {
            throw new StopTestException(Strings.MissingBaseURL());
        }

        this.baseURL = baseURL;
    }

    public String get(Map<String, String> queryParameter) throws IOException, StopTestException
    {
        HttpURLConnection conn = configureConnection(queryParameter);
        
        conn.setRequestMethod("GET");

        try
        {
            String response = readStream(conn.getInputStream());

            log(conn, response, "");

            return response;
        }
        catch(ConnectException ex)
        {
            log(conn, "", ex);

            throw new StopTestException(Strings.ConnectionFailed() + ": " + conn.getURL(), ex);
        }
    }

    public String post(String data) throws IOException, StopTestException
    {
        HttpURLConnection conn = configureConnection(null);
        
        conn.setRequestMethod("POST");

        byte[] outputBytes = data.getBytes(charset);

        OutputStream os;

        try
        {
            os = conn.getOutputStream();
        }
        catch (java.net.ConnectException ex)
        {
            log(conn, data, ex);

            throw new StopTestException(Strings.ConnectionFailed() + ": " + conn.getURL(), ex);
        }

        try
        {
            os.write(outputBytes);
        }
        catch(Exception ex)
        {
            log(conn, data, ex);

            throw new StopTestException(Strings.UnexpectedFailure() + ": " + conn.getURL(), ex);
        }
        finally
        {
            if (os != null) os.close();
        }

        try
        {
            String response = readStream(conn.getInputStream());

            log(conn, data, response);

            return response;
        }
        catch(Exception ex)
        {
            log(conn, data, ex);

            throw new StopTestException(Strings.UnexpectedFailure() + ": " + conn.getURL(), ex);
        }
    }

    private HttpURLConnection configureConnection(Map<String, String> queryMap) throws IOException, UnsupportedEncodingException
    {
        // fiddler support:
        // Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 8888));
        // ... new URL(baseURL + query).openConnection(proxy);
            
        String query = urlEncode(queryMap, charset);

        HttpURLConnection conn = (HttpURLConnection) new URL(baseURL + query).openConnection();

        conn.setDoOutput(true);
        conn.setRequestProperty("Accept-Charset", charset);
        conn.setRequestProperty("Content-Type", "application/json; charset=" + charset);

        return conn;
    }

    private String urlEncode(Map<String, String> queryMap, String charset) throws UnsupportedEncodingException
    {
        if(queryMap == null || queryMap.isEmpty())
        {
            return "";
        }
        
        StringBuilder sb = new StringBuilder(queryMap.size() * 10); //approximation
        
        sb.append("?");
        
        for(Entry<String, String> entry : queryMap.entrySet())
        {
            sb.append(URLEncoder.encode(entry.getKey(), charset));
            sb.append("=");
            sb.append(URLEncoder.encode(entry.getValue(), charset));
            sb.append("&");
        }

        sb.deleteCharAt(sb.length() - 1); // delete the &
        
        return sb.toString();
    }

    private String readStream(InputStream inputStream) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();

        String line;

        while ((line = reader.readLine()) != null)
        {
            sb.append(line);
        }

        reader.close();

        return sb.toString();
    }

    private void log(Level level, HttpURLConnection conn, String msg1, String msg2, Throwable ex)
    {
        String logMessage = String.format("%s %s", conn.getRequestMethod(), conn.getURL());

        if(msg1 != "") logMessage += ": " + msg1;

        if(msg2 != "") logMessage += " -> " + msg2;

        logger.log(level, logMessage, ex);
    }

    private void log(HttpURLConnection conn, String msg1, String msg2)
    {
        log(Level.DEBUG, conn, msg1, msg2, null);
    }

    private void log(HttpURLConnection conn, String msg1, Throwable ex)
    {
        log(Level.ERROR, conn, msg1, "", ex);
    }
}
