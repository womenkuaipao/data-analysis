package dyb.data.crawl.job.wojob.dto;

import lombok.Data;

@Data
public class WoJobResult {
    private String status;
    private String message;
    private WoJobResultBody resultBody;
}
