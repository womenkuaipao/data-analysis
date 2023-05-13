package dyb.data.crawl.configuration;

import dyb.data.crawl.processor.LianjiaProcessor;
import dyb.data.crawl.webprocessor.BossProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.selenium.SeleniumDownloader;
import us.codecraft.webmagic.scheduler.FileCacheQueueScheduler;
import us.codecraft.webmagic.scheduler.PriorityScheduler;

@Configuration
public class SpiderConfiguration {
    public static final Logger logger= LoggerFactory.getLogger(SpiderConfiguration.class);
    private String fileCachePath="C:\\E\\Java\\myProject\\data-analysis";

    private LianjiaProcessor lianjiaProcessor;

    public SpiderConfiguration(LianjiaProcessor lianjiaProcessor){
        this.lianjiaProcessor=lianjiaProcessor;
    }

    @Bean
    public void createLianJianSprider(){
        Spider.create(lianjiaProcessor)
                .addUrl(LianjiaProcessor.SITE_SEED)
                .setScheduler(new FileCacheQueueScheduler(fileCachePath))
                .thread(1)
                .run();
    }

}
