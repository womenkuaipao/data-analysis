package dyb.data.crawl.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
    private static Logger logger= LoggerFactory.getLogger(TimeUtil.class);
    private static String defaultFormat="yyyy-MM-dd HH:mm:ss";

    /**
     * user default format
     * @param time
     * @return
     * @throws ParseException
     */
    public static Date getDate(String time) throws ParseException {
        SimpleDateFormat sf=new SimpleDateFormat(defaultFormat);
        Date date = sf.parse(time);
        return date;
    }
}
