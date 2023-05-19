package dyb.data.crawl.repository.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name="job_query_word")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobQueryWord {
    @Id
    @Column(name="id")
    @SequenceGenerator(name="generator_job_query_word",sequenceName = "sequence_job_query_word",allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO,generator = "generator_job_query_word")
    private Integer id;
    @Column(name="key_word")
    private String keyWord;
    @Column(name="data_from")
    private String dataFrom;
}
