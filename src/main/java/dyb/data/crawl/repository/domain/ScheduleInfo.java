package dyb.data.crawl.repository.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name="schedule_info")
public class ScheduleInfo {
    @Id
    @Column(name="id")
    @SequenceGenerator(name="generator_job_info",sequenceName = "sequence_job_info",allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO,generator = "generator_job_info")
    private Integer id;
    @Column(name="search_key")
    private String searchKey;
    @Column(name="create_time")
    private Date createTime;
    @Column(name="total")
    private Integer total;
    @Column(name="success")
    private Boolean success;
    @Column(name="type")
    private Integer type;
    @Column(name="center")
    private String center;
}
