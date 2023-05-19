package dyb.data.crawl.repository.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name="job_info")
public class JobInfo {
    @Id
    @Column(name="id")
    private String id;
    @Column(name="longitude")
    private Float longitude;
    @Column(name="latitude")
    private Float latitude;
    @Column(name="job_name")
    private String jobName;
    @Column(name="company_name")
    private String companyName;
    /** 企业性质：民营还是国企 **/
    @Column(name="company_type")
    private String companyType;
    @Column(name="update_time")
    private Date updateTime;
    @Column(name="company_size")
    private String companySize;
    @Column(name="provide_salary")
    private String provideSalaryString;
    @Column(name="salary_low")
    private Integer salaryLow;
    @Column(name="salary_up")
    private Integer salaryUp;
    /** 全职还是兼职 **/
    @Column(name="job_type")
    private String jobType;
    @Column(name="job_tags",columnDefinition = "text")
    private String jobTags;
    @Column(name="data_from")
    private String dataFrom;
    /** 工作年限 **/
    @Column(name="work_year")
    private String workYear;
}
