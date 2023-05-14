package dyb.data.crawl.init;

import dyb.data.crawl.processor.MyFamilyHouseProcessor;
import dyb.data.crawl.repository.LinkInfoRepository;
import dyb.data.crawl.selenium.SeleniumDownloader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.FileCacheQueueScheduler;
import us.codecraft.webmagic.scheduler.QueueScheduler;

import java.util.List;

@Component
public class ProcessInit implements ApplicationRunner {
    private Logger logger= LoggerFactory.getLogger(MyFamilyHouseProcessor.class);
    private String fileCachePath="C:\\E\\Java\\myProject\\data-analysis";
    private String chromeDriverPath="C:\\E\\Java\\myProject\\chromedriver\\chromedriver.exe";

    @Autowired
    private MyFamilyHouseProcessor myFamilyHouseProcessor;
    @Autowired
    private LinkInfoRepository linkInfoRepository;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        long time=System.currentTimeMillis()-7*3600*1000*24;
        List<String> allFailedLinks = linkInfoRepository.getAllFailedLinks(MyFamilyHouseProcessor.SITE_TYPE,time);
        if(CollectionUtils.isEmpty(allFailedLinks)){
            allFailedLinks.add(myFamilyHouseProcessor.getSiteSeed());
        }
        new Thread(()->{
            Spider.create(myFamilyHouseProcessor)
//                    .addUrl(myFamilyHouseProcessor.getSiteSeed())
                    .startUrls(allFailedLinks)
//                    .setScheduler(new FileCacheQueueScheduler(fileCachePath))
                    .setScheduler(new QueueScheduler())
                    .setDownloader(new SeleniumDownloader(chromeDriverPath))
                    .thread(1)
                    .run();

        }).start();
    }


}
