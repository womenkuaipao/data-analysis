package dyb.data.crawl.repository.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name="house_info")
public class HouseInfo {
    @Id
    @Column(name="id")
    @SequenceGenerator(name="generator_house_info",sequenceName = "sequence_house_info",allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO,generator = "generator_house_info")
    private Integer id;
    @Column(name="longitude")
    private Float longitude;
    @Column(name="latitude")
    private Float latitude;
    @Column(name="house_name")
    private String houseName;
    @Column(name="zone")
    private String zone;
    @Column(name="nearby")
    private String nearby;
    @Column(name="area_size")
    private Float areaSize;
    @Column(name="price")
    private Float price;
    @Column(name="create_time")
    private Date createTime;
    @Column(name="build_time")
    private Date buildTime;
    @Column(name="detail",columnDefinition = "text")
    private String detail;
    @Column(name="source")
    private Integer source;
    @Column(name="hash")
    private String hash;
}
