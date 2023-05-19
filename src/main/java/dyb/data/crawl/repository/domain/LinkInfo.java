package dyb.data.crawl.repository.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="link_info")
public class LinkInfo {
    @Id
    @Column(name="url",columnDefinition = "varchar(5120)")
    private String url;
    @Column(name="success")
    private Boolean success;
    @Column(name="data_from")
    private String dataFrom;
    @Column(name="create_time")
    private Long createTime;
}
