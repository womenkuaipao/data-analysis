package dyb.data.crawl.schedule;

import dyb.data.crawl.area.baidumap.service.BaiduMapService;
import dyb.data.crawl.constant.AreaEnum;
import dyb.data.crawl.constant.CenterEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BaiduMapSchedule {
    private Logger logger= LoggerFactory.getLogger(BaiduMapSchedule.class);

    @Value("${baidu.map.area.start}")
    private boolean baiduAreaStart;
    @Autowired
    private BaiduMapService baiduMapService;

    @Scheduled(initialDelay = 1000,fixedDelay = 3600*1000*24*7)
    public void syncArea(){
        if(baiduAreaStart) {
            AreaEnum[] areas = AreaEnum.values();
            CenterEnum[] centers = CenterEnum.values();
            for (AreaEnum area : areas) {
                for (CenterEnum center : centers) {
                    baiduMapService.saveDataByQuery(area, center);
                }
            }
        }
    }
}
