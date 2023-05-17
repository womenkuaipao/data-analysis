package dyb.data.crawl.area.baidumap.dto;

import lombok.Data;

@Data
public class SearchArea {
    private String name;
    private Location location;
    private String address;
    private String area;
    private String telephone;
    private String price;
    private String shop_hours;
    private String overall_rating;
    private String taste_rating;
    private String comment_num;
    private String uid;
}
