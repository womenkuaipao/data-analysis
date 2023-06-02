package dyb.data.crawl.selenium;

import dyb.data.crawl.job.wojob.service.WoJobPageService;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v109.network.Network;
import org.openqa.selenium.devtools.v109.network.model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Optional;
import java.util.function.BiConsumer;

public class SeleniumDevTool {
    private Logger logger= LoggerFactory.getLogger(SeleniumDevTool.class);
    private String seedUrl=null;
    public SeleniumDevTool(String chromeDriverPath,String seedUrl){
        System.getProperties().setProperty("webdriver.chrome.driver", chromeDriverPath);
//        System.setProperty("webdriver.chrome.bin", "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe");
        this.seedUrl=seedUrl;
    }

    public ChromeDriver getDevTool(String filterUrl, BiConsumer<String,String> consumer){
        ChromeOptions chromeOptions=new ChromeOptions();
        chromeOptions.setExperimentalOption("debuggerAddress", "127.0.0.1:9527");
        chromeOptions.addArguments("--remote-allow-origins=*");
        ChromeDriver driver=new ChromeDriver(chromeOptions);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
        DevTools devTools = driver.getDevTools();
        devTools.createSession();
        devTools.send(Network.enable(Optional.empty(),Optional.empty(),Optional.empty()));

        devTools.addListener(Network.responseReceived(),res->{
            try {
                if(res!=null) {
                    Response response = res.getResponse();
                    String url = response.getUrl();
                    if (url.contains(filterUrl)) {
                        String responseBody = devTools.send(Network.getResponseBody(res.getRequestId())).getBody();
                        consumer.accept(responseBody, url);
                    }
                }
            }catch (Exception e){
                logger.error("selenium 监听器发生异常",e);
            }
        });
        driver.get(seedUrl);
        return driver;
    }

}
