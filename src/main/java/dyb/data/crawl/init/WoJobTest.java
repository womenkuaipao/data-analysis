package dyb.data.crawl.init;

import dyb.data.crawl.job.wojob.processor.WoJobProcessor;
import dyb.data.crawl.selenium.SeleniumDownloader;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.QueueScheduler;

public class WoJobTest {
    private static String chromeDriverPath="C:\\E\\Java\\myProject\\chromedriver\\chromedriver.exe";
    public static void main(String[]args){
        String seed="https://we.51job.com/pc/search?keyword=java&searchType=2&sortType=0&metro=";
        Spider.create(new WoJobProcessor())
                .addUrl(seed)
                .setScheduler(new QueueScheduler())
                .setDownloader(new SeleniumDownloader(chromeDriverPath))
                .thread(1)
                .run();
    }
}
