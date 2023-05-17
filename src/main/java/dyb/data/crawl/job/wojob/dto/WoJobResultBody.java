package dyb.data.crawl.job.wojob.dto;

import lombok.Data;

@Data
public class WoJobResultBody {
    private Integer searchType;
    private WoJobPage job;
}
