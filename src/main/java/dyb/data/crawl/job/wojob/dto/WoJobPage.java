package dyb.data.crawl.job.wojob.dto;

import lombok.Data;

import java.util.List;

@Data
public class WoJobPage {
    private List<WoJobInfo> items;
    private Integer totalCount;
}
