package filter;

import easyfit.IFilter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateFormatter implements IFilter
{
    @Override
    public String apply(String s)
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