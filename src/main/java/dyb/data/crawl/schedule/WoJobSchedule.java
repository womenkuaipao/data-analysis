package dyb.data.crawl.schedule;

import dyb.data.crawl.configuration.SeedConfiguration;
import dyb.data.crawl.constant.Constant;
import dyb.data.crawl.constant.TypeEnum;
import dyb.data.crawl.job.wojob.processor.WoJobProcessor;
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

import java.util.ArrayList;
import java.util.List;

@Component
public class WoJobSchedule {
    private Logger logger= LoggerFactory.getLogger(WoJobSchedule.class);

    @Value("${wo.job.start}")
    private boolean woJobStart;
    @Autowired
    private LinkInfoRepository linkInfoRepository;
    @Autowired
    private SeedConfiguration seedConfiguration;
    @Autowired
    private WoJobProcessor woJobProcessor;

    @Scheduled(initialDelay = 1000,fixedDelay = 3600*1000*24*1)
    public void syncWoJob(){
        if(woJobStart) {
            List<String> allFailedLinks = linkInfoRepository.getAllFailedLinks(TypeEnum.WOJOB.name());
            if (CollectionUtils.isEmpty(allFailedLinks)) {
                allFailedLinks = new ArrayList<>();
            }
            allFailedLinks.add(0,seedConfiguration.getWoJob());
            Spider.create(woJobProcessor)
                    .startUrls(allFailedLinks)
                    .setScheduler(new QueueScheduler())
                    .setDownloader(new SeleniumDownloader(Constant.chromeDriverPath))
                    .thread(1)
                    .run();
        }
    }
}
