package dyb.data.crawl.repository.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="baidu_area")
public class BaiduArea {
    @Id
    @Column(name="id")
    private String id;
    @Column(name="name")
    private String name;
    @Column(name="latitude")
    private Float latitude;
    @Column(name="longitude")
    private Float longitude;
    @Column(name="address")
    private String address;
    @Column(name="area")
    private String area;
    @Column(name="telephone")
    private String telephone;
    @Column(name="price")
    private String price;
    /** 营业时间 **/
    @Column(name="shop_hours")
    private String shopHours;
    /** 总体评分 **/
    @Column(name="overall_rating")
    private String overallRating;
    /** 口味评分 **/
    @Column(name="taste_rating")
    private String tasteRating;
    /** 评论数 **/
    @Column(name="comment_num")
    private String commentNum;
    @Column(name="query_key")
    private Integer queryKey;
}
