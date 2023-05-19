package dyb.data.crawl.job.wojob.dto;

import lombok.Data;

import java.util.List;

@Data
public class JobType {
    private String code;
    private String value;
    private String isHot;
    private List<JobType> subList;
}
