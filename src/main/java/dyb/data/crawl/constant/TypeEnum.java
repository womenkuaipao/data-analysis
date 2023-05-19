package dyb.data.crawl.constant;

public enum TypeEnum {
    MY_FAMILY(1),
    BAIDU_AREA(2),
    WOJOB(3);
    private Integer type;
    private TypeEnum(Integer type){
        this.type=type;
    }
    public Integer getType(){
        return this.type;
    }
}
