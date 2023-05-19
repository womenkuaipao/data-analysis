package dyb.data.crawl.job.wojob.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class WoJobResult<T> {
    private String status;
    private String message;
    @JsonAlias({"resultbody","resultBody"})
    private T resultbody;
}
