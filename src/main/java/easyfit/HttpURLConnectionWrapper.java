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

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

/**
 * Handles the actual communication with SUT over http connection.
 */
public class HttpURLConnectionWrapper
{
    //protected final Logger logger = LogManager.getLogger(SUTCaller.class.getName());

    private final String charset = "UTF-8";
    private String baseURL;

    public HttpURLConnectionWrapper(String action)
    {
        // todo: configuration
        this.baseURL = "http://localhost:56473/" + action + ".json";
    }

    public String get(Map<String, String> queryParameter) throws IOException
    {
        HttpURLConnection conn = configureConnection(queryParameter);
        
        conn.setRequestMethod("GET");

        return readStream(conn.getInputStream());
    }

    public String post(String data) throws IOException
    {
        HttpURLConnection conn = configureConnection(null);
        
        conn.setRequestMethod("POST");
            
        byte[] outputBytes = data.getBytes(charset);

        OutputStream os = conn.getOutputStream();

        try
        {
            os.write(outputBytes);
        }
        finally
        {
            if (os != null) os.close();
        }

        return readStream(conn.getInputStream());
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

        //logger.debug("readStream: " + sb.toString();

        return sb.toString();
    }
}
