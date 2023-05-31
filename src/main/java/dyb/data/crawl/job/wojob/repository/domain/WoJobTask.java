package dyb.data.crawl.job.wojob.repository.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name="wojob_task")
public class WoJobTask {
    @Id
    @Column(name="id")
    @SequenceGenerator(name="generator_wojob_task",sequenceName = "sequence_wojob_task",allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO,generator = "generator_wojob_task")
    private Long id;
    @Column(name="query_key")
    private String queryKey;
    @Column(name="area")
    private String area;
    @Column(name="create_time")
    private Date createTime;
    @Column(name="success")
    private Boolean success;
}
