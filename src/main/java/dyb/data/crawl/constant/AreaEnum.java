package dyb.data.crawl.constant;

public enum AreaEnum {
    YOUERYUAN(0,"幼儿园"),
    XIAOXUE(1,"小学"),
    CHUZHONG(2,"初中"),
    GAOZHONG(3,"高中"),
    DAXUE(4,"大学"),
//    DITIEZHAN(5,"地铁站"),
    JIUDIAN(6,"酒店"),
    BINGUAN(7,"宾馆"),
    DITIE(8,"地铁站");
    private Integer type;
    private String name;
    private AreaEnum(Integer type,String name){
        this.type=type;
        this.name=name;
    }
    public String getName(){
        return this.name;
    }
    public Integer getType(){
        return this.type;
    }
}
