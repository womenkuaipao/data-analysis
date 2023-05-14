package dyb.data.crawl.baidu_api;

import dyb.data.crawl.baidu_api.dto.BaiduResult;
import dyb.data.crawl.baidu_api.dto.SearchArea;
import dyb.data.crawl.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@PropertySource(value ="ak-secret.properties")
public class BaiduMapService {
    private Logger logger= LoggerFactory.getLogger(BaiduMapService.class);
    @Value("${baidu.ak}")
    private String baiduAk;

    @Autowired
    private RestTemplate restTemplate;

    public BaiduResult<SearchArea> searchArea(String query,String tag,int pageSize,int pageNo){
        if(pageSize>20){
            throw new BusinessException("百度最大页面大小不超过20");
        }
        String url="https://api.map.baidu.com/place/v2/search?query="
                +query+"&region=杭州&output=json&ak="
                +baiduAk+"&page_num="+pageNo+"&page_size="+pageSize;
        BaiduResult<SearchArea> body = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<BaiduResult<SearchArea>>() {
        }).getBody();
        return body;
    }
}
