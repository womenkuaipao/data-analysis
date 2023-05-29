package dyb.data.crawl.job.wojob.action;

import dyb.data.crawl.constant.Constant;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarResponse;
import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.List;

public class ActionTest2 {
    public static void main(String []args) throws InterruptedException {
        System.getProperties().setProperty("webdriver.chrome.driver", Constant.chromeDriverPath);
        System.setProperty("webdriver.chrome.bin", "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe");

        ChromeOptions option = new ChromeOptions();
        ChromeDriver driver = new ChromeDriver(option);
        driver.get("https://we.51job.com/pc/search");
    }
}
