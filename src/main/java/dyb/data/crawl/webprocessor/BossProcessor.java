package dyb.data.crawl.webprocessor;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Arrays;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.downloader.selenium.SeleniumDownloader;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;
import us.codecraft.webmagic.scheduler.PriorityScheduler;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

public class BossProcessor implements PageProcessor {
	private Site site = Site.me().setRetryTimes(3).setSleepTime(100)
			.addHeader("user-agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36")
			.addHeader("referer", "https://www.zhipin.com/c101210100-p100101/");
	@Override
	public void process(Page page) {
		Html html=page.getHtml();
		String pageUrl=page.getUrl().get();
		try {			
			if(pageUrl.contains("job_detail")) {//工作详情
				String jobName=html.xpath("//*[@id=\"main\"]/div[1]/div/div/div[2]/div[2]/h1/text()").get();
				String jobUrl=pageUrl.substring(0,pageUrl.indexOf(".html"));
				String jobId=jobUrl.substring(jobUrl.lastIndexOf("/")+1);
				String salaryRange=html.xpath("//*[@id=\"main\"]/div[1]/div/div/div[2]/div[2]/span/text()").get();
				String jobCity=html.xpath("//*[@id=\"main\"]/div[1]/div/div/div[2]/p/a/text()").get();
				String experienceAndEdu=html.xpath("//*[@id=\"main\"]/div[1]/div/div/div[2]/p").get();
				String[] arr=experienceAndEdu.split("<em class=\\\"dolt\\\"></em>");
				String experience=null,education=null;
				if(arr.length>=3) {
					experience=arr[1];
					education=arr[2].replace("</p>", "");
				}
				String jobDesc=html.xpath("//*[@id=\"main\"]/div[3]/div/div[2]/div[2]/div[1]/div/allText()").get();
				String companyLink=html.xpath("//*[@id=\"main\"]/div[3]/div/div[2]/div[2]/div[4]/a").links().get();
				String companyId=companyLink.substring(companyLink.lastIndexOf("/")+1).replace(".html", ""); 
				String jobAddress=html.xpath("//*[@id=\"main\"]/div[3]/div/div[2]/div[2]/div[5]/div/div[1]/text()").links().get();
				String jobAddressPic=html.xpath("//*[@id=\"main\"]/div[3]/div/div[2]/div[2]/div[5]/div/div[2]").$("div", "data-src").get();
				System.out.println();
			
			}else if(pageUrl.contains("gongsi")) {//公司详情

			
			}else {//分页信息
				List<Selectable> jobInfoList=html.xpath("//*[@id=\"main\"]/div/div[2]/ul").$("li").nodes();
				if(!CollectionUtils.isEmpty(jobInfoList)) {
					for(int i=0;i<jobInfoList.size();i++) {
						Selectable jobInfo=jobInfoList.get(i);
						List<String> links=new ArrayList<>();
						String jobDetailLink=jobInfo.xpath("/li/div/div[1]/div[1]/div/div[1]/span[1]/a").links().get();
						String companyLink=jobInfo.xpath("/li/div/div[1]/div[2]/div/h3/a").links().get();
						links.add(jobDetailLink);
						links.add(companyLink);
						page.addTargetRequests(links, i);
					}
				}
				Selectable pageDiv=html.xpath("//*[@id=\"main\"]/div/div[2]/div[3]");
				String nextPage=pageDiv.$("a.next").links().get();
				if(!StringUtils.isEmpty(nextPage)) {
					page.addTargetRequests(new ArrayList(Arrays.asList(nextPage)) ,100);
				}
			}
		}catch(Exception e) {
			
		}
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
		.addUrl("https://www.zhipin.com/c101210100-p100101/")
//		.setDownloader(httpClientDownloader)
		.setScheduler(new PriorityScheduler())
		.setDownloader(new SeleniumDownloader(classPath+"\\chrome\\chromedriver.exe")
				.setSleepTime(6000))
		//			.addRequest(request)
		.thread(1)
		.run();
	}

}
