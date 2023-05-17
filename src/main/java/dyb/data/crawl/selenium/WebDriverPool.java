//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dyb.data.crawl.selenium;

import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class WebDriverPool {
    private Logger logger;
    private static final int DEFAULT_CAPACITY = 5;
    private final int capacity;
    private static final int STAT_RUNNING = 1;
    private static final int STAT_CLODED = 2;
    private AtomicInteger stat;
    private WebDriver mDriver;
    private boolean mAutoQuitDriver;
    private static final String DEFAULT_CONFIG_FILE = "/data/webmagic/webmagic-selenium/config.ini";
    private static final String DRIVER_FIREFOX = "firefox";
    private static final String DRIVER_CHROME = "chrome";
    private static final String DRIVER_PHANTOMJS = "phantomjs";
    protected static Properties sConfig;
    protected static DesiredCapabilities sCaps;
    private List<WebDriver> webDriverList;
    private BlockingDeque<WebDriver> innerQueue;

    public void configure() throws IOException {
        sConfig = new Properties();
        String configFile = "C:\\E\\Java\\myProject\\data-analysis\\src\\main\\java\\dyb\\data\\crawl\\data\\webmagic\\webmagic-selenium\\config.ini";
        if (System.getProperty("selenuim_config") != null) {
            configFile = System.getProperty("selenuim_config");
        }

        sConfig.load(new FileReader(configFile));
        sCaps = new DesiredCapabilities();
        sCaps.setJavascriptEnabled(true);
        sCaps.setCapability("takesScreenshot", false);
        String driver = sConfig.getProperty("driver", "phantomjs");
        if (driver.equals("phantomjs")) {
            if (sConfig.getProperty("phantomjs_exec_path") == null) {
                throw new IOException(String.format("Property '%s' not set!", "phantomjs.binary.path"));
            }

            sCaps.setCapability("phantomjs.binary.path", sConfig.getProperty("phantomjs_exec_path"));
            if (sConfig.getProperty("phantomjs_driver_path") != null) {
                System.out.println("Test will use an external GhostDriver");
                sCaps.setCapability("phantomjs.ghostdriver.path", sConfig.getProperty("phantomjs_driver_path"));
            } else {
                System.out.println("Test will use PhantomJS internal GhostDriver");
            }
        }

        ArrayList<String> cliArgsCap = new ArrayList();
        cliArgsCap.add("--web-security=false");
        cliArgsCap.add("--ssl-protocol=any");
        cliArgsCap.add("--ignore-ssl-errors=true");
        sCaps.setCapability("phantomjs.cli.args", cliArgsCap);
        sCaps.setCapability("phantomjs.ghostdriver.cli.args", new String[]{"--logLevel=" + (sConfig.getProperty("phantomjs_driver_loglevel") != null ? sConfig.getProperty("phantomjs_driver_loglevel") : "INFO")});
        if (this.isUrl(driver)) {
            sCaps.setBrowserName("phantomjs");
            this.mDriver = new RemoteWebDriver(new URL(driver), sCaps);
        } else if (driver.equals("firefox")) {
            this.mDriver = new FirefoxDriver(sCaps);
        } else if (driver.equals("chrome")) {
            this.mDriver = new ChromeDriver(sCaps);
        } else if (driver.equals("phantomjs")) {
            this.mDriver = new PhantomJSDriver(sCaps);
        }

    }

    private boolean isUrl(String urlString) {
        try {
            new URL(urlString);
            return true;
        } catch (MalformedURLException var3) {
            return false;
        }
    }

    public WebDriverPool(int capacity) {
        this.logger = Logger.getLogger(this.getClass());
        this.stat = new AtomicInteger(1);
        this.mDriver = null;
        this.mAutoQuitDriver = true;
        this.webDriverList = Collections.synchronizedList(new ArrayList());
        this.innerQueue = new LinkedBlockingDeque();
        this.capacity = capacity;
    }

    public WebDriverPool() {
        this(5);
    }

    public WebDriver get() throws InterruptedException {
        this.checkRunning();
        WebDriver poll = (WebDriver)this.innerQueue.poll();
        if (poll != null) {
            return poll;
        } else {
            if (this.webDriverList.size() < this.capacity) {
                synchronized(this.webDriverList) {
                    if (this.webDriverList.size() < this.capacity) {
                        try {
                            this.configure();
                            this.innerQueue.add(this.mDriver);
                            this.webDriverList.add(this.mDriver);
                        } catch (IOException var5) {
                            var5.printStackTrace();
                        }
                    }
                }
            }

            return (WebDriver)this.innerQueue.take();
        }
    }

    public void returnToPool(WebDriver webDriver) {
        this.checkRunning();
        this.innerQueue.add(webDriver);
    }

    protected void checkRunning() {
        if (!this.stat.compareAndSet(1, 1)) {
            throw new IllegalStateException("Already closed!");
        }
    }

    public void closeAll() {
        boolean b = this.stat.compareAndSet(1, 2);
        if (!b) {
            throw new IllegalStateException("Already closed!");
        } else {
            WebDriver webDriver;
            for(Iterator var2 = this.webDriverList.iterator(); var2.hasNext(); webDriver = null) {
                webDriver = (WebDriver)var2.next();
                this.logger.info("Quit webDriver" + webDriver);
                webDriver.quit();
            }

        }
    }
}
