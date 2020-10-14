package dyb.data.crawl.webprocessor;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.downloader.selenium.SeleniumDownloader;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

public class BossProcessor implements PageProcessor {
	private Site site = Site.me().setRetryTimes(3).setSleepTime(100)
								.addHeader("user-agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36")
								.addHeader("referer", "https://www.zhipin.com/c101210100-p100101/");
	@Override
	public void process(Page page) {
//		try {
//			FileUtils.writeStringToFile(new File("C:\\Users\\HP\\Desktop\\新建文本文档 (4).html"), page.getHtml().toString());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		Html html=page.getHtml();
		List<Selectable> jobInfoList=html.xpath("//*[@id=\"main\"]/div/div[2]/ul").$("li").nodes();
		if(!CollectionUtils.isEmpty(jobInfoList)) {
			for(Selectable jobInfo:jobInfoList) {
				Selectable jobNameNode=jobInfo.xpath("/li/div/div[1]/div[1]/div/div[1]/span[1]/a");
				if(jobNameNode!=null) {
					String jobDetailLink=jobNameNode.links().get();
					String jobName=jobNameNode.$("a","title").get();
					String jobArea=jobInfo.xpath("/li/div/div[1]/div[1]/div/div[1]/span[2]/span/text()").get();
					String salaryRange=jobInfo.xpath("/li/div/div[1]/div[1]/div/div[2]/span/text()").get();
					String experience=jobInfo.xpath("/li/div/div[1]/div[1]/div/div[2]/p/text()").get();
					String company=jobInfo.xpath("/li/div/div[1]/div[2]/div/h3/a/text()").get();
					String industry=jobInfo.xpath("/li/div/div[1]/div[2]/div/p/a/text()").get();

					String financeString=jobInfo.xpath("/li/div/div[1]/div[2]/div/p").get();
					financeString=financeString.replace("</p>", "");
					String[] finaceArray=financeString.split("<em class=\"vline\"></em>");
					if(finaceArray!=null&&finaceArray.length>2) {
						String finace=finaceArray[1];
						String employees=finaceArray[2];
						System.out.println();
					}
					String welfare=jobInfo.xpath("/li/div/div[2]/div[2]/text()").get();
					System.out.println(jobDetailLink);
					System.out.println(jobName);
					System.out.println(jobArea);
					System.out.println(salaryRange);
					System.out.println(experience);
					System.out.println(company);
					System.out.println(industry);
					System.out.println(welfare);
					System.out.println("-------------------------------");
				}				
			}
		}
		try {			
			//查找下一页
			Selectable pageDiv=html.xpath("//*[@id=\"main\"]/div/div[2]/div[2]");
			String nextPage=pageDiv.$("a.next").links().get();
			if(!StringUtils.isEmpty(nextPage)) {
				page.addTargetRequest(nextPage);
			}
		}catch(Exception e) {
			
		}
		System.out.println("");
	}

	@Override
	public Site getSite() {
		return site;
	}

	public static void main(String[] args) throws URISyntaxException {
		System.getProperties().setProperty("proxySet", "true"); 	
		System.getProperties().setProperty("http.proxyHost", "218.26.204.66");
		System.getProperties().setProperty("http.proxyPort", "8080");

		HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
		httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(new Proxy("122.231.113.240",8080,"kuaipao","273338")));
		//		spider.setDownloader(httpClientDownloader);

		Request request = new Request("https://www.xxx.com/a/b");
		request.setMethod("GET");
		request.addHeader("user-agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36");
		request.addHeader("referer", "https://www.zhipin.com/c101210100-p100101/");
		
		String classPath=BossProcessor.class.getResource("/").toURI().getPath();
		System.out.println(BossProcessor.class.getResource("/").toURI().getPath());
		System.setProperty("selenuim_config",classPath+File.separator+"config.ini");
		Spider.create(new BossProcessor())
			.addUrl("https://www.zhipin.com/c101210100-p100101/?page=2&ka=page-2")
			.setDownloader(httpClientDownloader)
			.downloader(new SeleniumDownloader(classPath+"\\chrome\\chromedriver.exe")
					.setSleepTime(10000))
//			.addRequest(request)
			.thread(1)
			.run();
	}

}
