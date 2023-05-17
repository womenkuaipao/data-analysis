package dyb.data.crawl.tool;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringUtil {
    private static Logger logger= LoggerFactory.getLogger(StringUtil.class);

    public static Float string2Float(String value){
        if(StringUtils.isNotEmpty(value)){
            try {
                Float valueOf =Float.valueOf(value);
                return valueOf;
            }catch (Exception e){
                logger.error("转换float数据异常,value={}",value,e);
            }
        }
        return null;
    }
}
