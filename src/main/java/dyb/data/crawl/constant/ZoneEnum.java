package dyb.data.crawl.constant;

import org.springframework.util.StringUtils;

public enum ZoneEnum {
    /**
     * 只关注以下区域
     */
    ALL("所有","080200"),
   GONGSU("拱墅区","080201"),
   SHANGCHENG("上城区","080202"),
   XIHU("西湖区","080205"),
   BINJIANG("西湖区","080206"),
   YUHANG("余杭区","080207"),
   XIAOSHAN("萧山区","080208"),
   LINPING("临平区","080214"),
   QIANTANG("钱塘区","080215");
    private String name;
    private String woJobCode;
    private ZoneEnum(String name,String woJobCode){
        this.name=name;
        this.woJobCode=woJobCode;
    }
    public String getWoJobCode(){
        return this.woJobCode;
    }
    public ZoneEnum nextZone(){
        ZoneEnum[] values = ZoneEnum.values();
        boolean findNext=false;
        for(ZoneEnum zoneEnum:values){
            if(zoneEnum.equals(this)){
                findNext=true;
            }else if(findNext){
                return zoneEnum;
            }
        }
        return null;
    }

    public static ZoneEnum getByWoCode(String code){
        if(StringUtils.isEmpty(code))return ALL;
        ZoneEnum[] values = ZoneEnum.values();
        for(ZoneEnum zoneEnum:values){
            if(zoneEnum.getWoJobCode().equals(code)){
                return zoneEnum;
            }
        }
        return null;
    }
}
