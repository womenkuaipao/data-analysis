package dyb.data.crawl.tool;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
    private static Logger logger= LoggerFactory.getLogger(TimeUtil.class);
    private static String defaultFormat="yyyy-MM-dd HH:mm:ss";
    private static String yyyyMMdd="yyyy-MM-dd";

    /**
     * user default format
     * @param time
     * @return
     * @throws ParseException
     */
    public static Date getDate(String time) throws ParseException {
        if(StringUtils.isEmpty(time))return null;
        SimpleDateFormat sf=new SimpleDateFormat(defaultFormat);
        Date date = sf.parse(time);
        return date;
    }

    public static long getDateStart(Date date) throws ParseException {
        SimpleDateFormat sf=new SimpleDateFormat(yyyyMMdd);
        String time = sf.format(date);
        Date startDate = sf.parse(time);
        return startDate.getTime();

    }

    public static long getDateEnd(Date date) throws ParseException {
        return getDateStart(date)+3600000*24;
    }

    public static Date getTodayStart() throws ParseException {
        SimpleDateFormat sf=new SimpleDateFormat(yyyyMMdd);
        String time = sf.format(new Date());
        Date startDate = sf.parse(time);
        return startDate;
    }
}
