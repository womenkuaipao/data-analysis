package dyb.data.crawl.area.baidumap.service;

import com.fasterxml.jackson.core.type.TypeReference;
import dyb.data.crawl.area.baidumap.dto.BaiduResult;
import dyb.data.crawl.area.baidumap.dto.Location;
import dyb.data.crawl.area.baidumap.dto.SearchArea;
import dyb.data.crawl.constant.AreaEnum;
import dyb.data.crawl.constant.CenterEnum;
import dyb.data.crawl.constant.TypeEnum;
import dyb.data.crawl.exception.BusinessException;
import dyb.data.crawl.repository.BaiduAreaRepository;
import dyb.data.crawl.repository.ScheduleInfoRepository;
import dyb.data.crawl.repository.domain.BaiduArea;
import dyb.data.crawl.repository.domain.ScheduleInfo;
import dyb.data.crawl.tool.JacksonTool;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@PropertySource(value ="ak-secret.properties")
public class BaiduMapService {
    private Logger logger= LoggerFactory.getLogger(BaiduMapService.class);
    @Value("${baidu.ak}")
    private String baiduAk;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ScheduleInfoRepository scheduleInfoRepository;
    @Autowired
    private BaiduAreaRepository baiduAreaRepository;

    public BaiduResult<SearchArea> searchArea(String query,String center,int pageSize,int pageNo) throws Exception {
        if (pageSize > 20) {
            throw new BusinessException("百度最大页面大小不超过20");
        }
        String url = "https://api.map.baidu.com/place/v2/search?query="
                + query + "&region=杭州&output=json&ak="
                + baiduAk + "&page_num=" + pageNo + "&page_size=" + pageSize;
        if (StringUtils.isNotEmpty(center)) {
            url += "&center=" + center;
        }
        String body = null;
        try {
            Thread.sleep(30);
            body = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<String>() {
            }).getBody();
            BaiduResult<SearchArea> searchAreaBaiduResult = JacksonTool.json2Object(body, new TypeReference<BaiduResult<SearchArea>>() {
            });
            if(searchAreaBaiduResult.getTotal()==null){
                logger.info("百度返回:"+body);
            }
            return searchAreaBaiduResult;
        }catch (Exception e){
            logger.error("百度地图API接口调用失败，请求参数{},返回结果{}",url,body,e);
            throw e;
        }
    }

    public void saveDataByQuery(AreaEnum areaQuery, CenterEnum center){
        try {
            ScheduleInfo scheduleInfo = scheduleInfoRepository.findBySearchKeyAndTypeAndCenter(areaQuery.getName(), TypeEnum.BAIDU_AREA.getType(),center.getCenter());
            if (scheduleInfo == null || !scheduleInfo.getSuccess()) {
                if (scheduleInfo == null) {
                    scheduleInfo = new ScheduleInfo();
                    scheduleInfo.setSearchKey(areaQuery.getName());
                    scheduleInfo.setCreateTime(new Date());
                    scheduleInfo.setSuccess(false);
                    scheduleInfo.setType(TypeEnum.BAIDU_AREA.getType());
                    scheduleInfo.setCenter(center.getCenter());
                    scheduleInfo = scheduleInfoRepository.save(scheduleInfo);
                }
                logger.info("开始执行百度地图地点搜查,key={},center={}", areaQuery.getName(),center.getCenter());
                int pageSize = 20, pageNo = 0, total = 0;
                do {
                    BaiduResult<SearchArea> searchAreaBaiduResult = searchArea(areaQuery.getName(), center.getCenter(), pageSize, pageNo);
                    List<SearchArea> results = searchAreaBaiduResult.getResults();
                    if (!CollectionUtils.isEmpty(results)) {
                        List<BaiduArea> domains = new ArrayList<>();
                        for (SearchArea searchArea : results) {
                            BaiduArea baiduArea = new BaiduArea();
                            BeanUtils.copyProperties(searchArea, baiduArea);
                            baiduArea.setId(searchArea.getUid());
                            Location location = searchArea.getLocation();
                            baiduArea.setLatitude(location.getLat());
                            baiduArea.setLongitude(location.getLng());
                            baiduArea.setQueryKey(areaQuery.getType());
                            domains.add(baiduArea);
                        }
                        baiduAreaRepository.saveAll(domains);
                    }
                    total = searchAreaBaiduResult.getTotal();
                    pageNo++;
                } while (total > pageNo * pageSize);
                scheduleInfo.setSuccess(true);
                scheduleInfo.setTotal(total);
                scheduleInfoRepository.save(scheduleInfo);
            }
            logger.info("结束执行百度地图地点搜查key={},center={}", areaQuery.getName(),center.getCenter());
        }catch (Exception e){
            logger.error("执行百度地图地点搜查任务异常",e);
        }
    }
}
