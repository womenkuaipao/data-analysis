package dyb.data.crawl.webprocessor;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.io.FileUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.selenium.SeleniumDownloader;
import us.codecraft.webmagic.processor.PageProcessor;

public class BossProcessor implements PageProcessor {
	private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
	@Override
	public void process(Page page) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // 文章页，匹配 https://voice.hupu.com/nba/七位数字.html
		System.out.println(page.getHtml());
		try {
			FileUtils.writeStringToFile(new File("C:\\Users\\HP\\Desktop\\新建文本文档 (4).html"), page.getHtml().toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if (page.getUrl().regex("https://voice\\.hupu\\.com/nba/[0-9]{7}\\.html").match()) {
            page.putField("Title", page.getHtml().xpath("/html/body/div[4]/div[1]/div[1]/h1/text()").toString());
            page.putField("Content",
                    page.getHtml().xpath("/html/body/div[4]/div[1]/div[2]/div/div[2]/p/text()").all().toString());
        }
        // 列表页
        else {
            // 文章url
            page.addTargetRequests(
                    page.getHtml().xpath("/html/body/div[3]/div[1]/div[2]/ul/li/div[1]/h4/a/@href").all());
            // 翻页url
            page.addTargetRequests(
                    page.getHtml().xpath("/html/body/div[3]/div[1]/div[3]/a[@class='page-btn-prev']/@href").all());
        }
	}

	@Override
	public Site getSite() {
		return site;
	}

	public static void main(String[] args) throws URISyntaxException {
		String classPath=BossProcessor.class.getResource("/").toURI().getPath();
		System.out.println(BossProcessor.class.getResource("/").toURI().getPath());
		System.setProperty("selenuim_config",classPath+File.separator+"config.ini");
		Spider.create(new BossProcessor()).addUrl("https://www.zhipin.com/c101210100/?query=java&page=3&ka=page-3")
		.downloader(new SeleniumDownloader(classPath+"\\chrome\\chromedriver.exe").setSleepTime(5000)).thread(3).run();
	}

}
