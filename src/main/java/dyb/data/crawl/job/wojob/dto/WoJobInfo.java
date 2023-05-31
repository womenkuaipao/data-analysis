package dyb.data.crawl.job.wojob.dto;

import lombok.Data;

import java.util.List;

@Data
public class WoJobInfo {
    private String jobId;
    private String jobName;
    private List<String> jobTags;
    private String provideSalaryString;
    private String updataDateTime;
    private String companyName;
    private String companyTypeString;
    private String companySizeString;
    private String workYearString;
    private String degreeString;
    private String lon;
    private String lat;
    private String termStr;
    private String keyWord;
}
