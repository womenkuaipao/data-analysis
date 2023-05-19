//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dyb.data.crawl.selenium;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.PlainText;

public class SeleniumDownloader implements Downloader, Closeable {
    private volatile WebDriverPool webDriverPool;
    private Logger logger = Logger.getLogger(this.getClass());
    private int sleepTime = 0;
    private int poolSize = 1;
    private static final String DRIVER_PHANTOMJS = "phantomjs";

    public SeleniumDownloader(String chromeDriverPath) {
        System.getProperties().setProperty("webdriver.chrome.driver", chromeDriverPath);
        System.setProperty("webdriver.chrome.bin", "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe");
    }

    public SeleniumDownloader() {
    }

    public SeleniumDownloader setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
        return this;
    }

    public Page download(Request request, Task task) {
        this.checkInit();

        WebDriver webDriver;
        try {
            webDriver = this.webDriverPool.get();
        } catch (InterruptedException var10) {
            this.logger.warn("interrupted", var10);
            return null;
        }

        this.logger.info("downloading page " + request.getUrl());
        webDriver.get(request.getUrl());

        try {
            Thread.sleep((long)this.sleepTime);
        } catch (InterruptedException var9) {
            var9.printStackTrace();
        }

        WebDriver.Options manage = webDriver.manage();
        Site site = task.getSite();
        if (site.getCookies() != null) {
            Iterator var6 = site.getCookies().entrySet().iterator();

            while(var6.hasNext()) {
                Map.Entry<String, String> cookieEntry = (Map.Entry)var6.next();
                Cookie cookie = new Cookie((String)cookieEntry.getKey(), (String)cookieEntry.getValue());
                manage.addCookie(cookie);
            }
        }

        WebElement webElement = webDriver.findElement(By.xpath("/html"));
        String content = webElement.getAttribute("outerHTML");
        Page page = new Page();
        page.setRawText(content);
        page.setHtml(new Html(content, request.getUrl()));
        page.setUrl(new PlainText(request.getUrl()));
        page.setRequest(request);
        this.webDriverPool.returnToPool(webDriver);
        return page;
    }

    private void checkInit() {
        if (this.webDriverPool == null) {
            synchronized(this) {
                this.webDriverPool = new WebDriverPool(this.poolSize);
            }
        }

    }

    public void setThread(int thread) {
        this.poolSize = thread;
    }

    public void close() throws IOException {
        if(webDriverPool!=null) {
            this.webDriverPool.closeAll();
        }
    }
}
