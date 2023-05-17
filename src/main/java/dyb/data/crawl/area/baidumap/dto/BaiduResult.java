package dyb.data.crawl.area.baidumap.dto;

import lombok.Data;

import java.util.List;

@Data
public class BaiduResult<T> {
    private Integer status;
    private String message;
    private Integer total;
    private List<T> results;
}
