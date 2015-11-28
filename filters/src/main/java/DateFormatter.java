package filter;

import easyfit.IConverter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateFormatter implements IConverter
{
    @Override
    public String convertExpected(String s)
    {
        return s;
    }
    
    @Override
    public String convertActual(String s)
    {
        try
        {
            Pattern p = Pattern.compile("^(\\d+)$");
            Matcher m = p.matcher(s);

            if (!m.matches()) return s;

            Date date = new Date(Long.parseLong(m.group(1)));

            return new SimpleDateFormat("yyyy-MM-dd").format(date);
        }
        catch(Exception ex)
        {
            return ex.getMessage();
        }
    }
}