package dyb.data.crawl.schedule;

import dyb.data.crawl.configuration.SeedConfiguration;
import dyb.data.crawl.constant.Constant;
import dyb.data.crawl.constant.TypeEnum;
import dyb.data.crawl.house.myfamily.processor.MyFamilyHouseProcessor;
import dyb.data.crawl.repository.LinkInfoRepository;
import dyb.data.crawl.selenium.SeleniumDownloader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.QueueScheduler;

import java.util.List;

@Component
public class MyFamilyHouseSchedule {
    private Logger logger= LoggerFactory.getLogger(MyFamilyHouseSchedule.class);

    @Value("${my.family.house.start}")
    private boolean myFamilyStart;
    @Autowired
    private LinkInfoRepository linkInfoRepository;
    @Autowired
    private SeedConfiguration seedConfiguration;
    @Autowired
    private MyFamilyHouseProcessor myFamilyHouseProcessor;

    @Scheduled(initialDelay = 1000,fixedDelay = 3600*1000*24*1)
    public void syncMyFamilyHouse(){
        if(myFamilyStart) {
            long time=System.currentTimeMillis()-7*3600*1000*24;
            List<String> allFailedLinks = linkInfoRepository.getAllFailedLinks(TypeEnum.MY_FAMILY.name(), time);
            if(CollectionUtils.isEmpty(allFailedLinks)){
                allFailedLinks.add(seedConfiguration.getMyFamilyHouse());
            }
            new Thread(()->{
                Spider.create(myFamilyHouseProcessor)
                        .startUrls(allFailedLinks)
                        .setScheduler(new QueueScheduler())
                        .setDownloader(new SeleniumDownloader(Constant.chromeDriverPath))
                        .thread(1)
                        .run();

            }).start();
        }
    }
}
