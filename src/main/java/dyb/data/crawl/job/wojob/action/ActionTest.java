package dyb.data.crawl.job.wojob.action;

import dyb.data.crawl.constant.Constant;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarResponse;
import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.By;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.List;

public class ActionTest {
    public static void main(String []args) throws InterruptedException {
        System.getProperties().setProperty("webdriver.chrome.driver", Constant.chromeDriverPath);
        System.setProperty("webdriver.chrome.bin", "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe");

        BrowserMobProxy proxy = new BrowserMobProxyServer();
        proxy.start(0);

        // get the Selenium proxy object
        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);

        // configure it as a desired capability
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(CapabilityType.PROXY, seleniumProxy);


        ChromeOptions option = new ChromeOptions();
//        option.setExperimentalOption("debuggerAddress", "127.0.0.1:9527");
//        option.addArguments("--disable-blink-features=AutomationControlled");
        ChromeDriver driver = new ChromeDriver(capabilities);
        proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
        proxy.newHar("https://we.51job.com/pc/search");
        driver.get("https://we.51job.com/pc/search");

        Har har = proxy.getHar();
        List<HarEntry> list =  har.getLog().getEntries();
        for (HarEntry harEntry : list){
            String url = harEntry.getRequest().getUrl();
//            if(!url.startsWith("你想获取的ajax响应地址")){
//                continue;
//            }
            HarResponse harResponse = harEntry.getResponse();
            String responseBody = harResponse.getContent().getText();
            System.out.println(responseBody);
        }

//        Actions action=new Actions(driver);
//        Thread.sleep(5000);
//        driver.findElement(By.className("btn-next")).click();
//        action.moveToElement(driver.findElement(By.id("kw"))).perform();
//        driver.findElement(By.className("setpref")).click();
    }
}
