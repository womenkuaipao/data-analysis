package dyb.data.crawl.selenium;

import dyb.data.crawl.constant.Constant;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class WojoblogTest {
    public static void main(String[]args){
        System.getProperties().setProperty("webdriver.chrome.driver", Constant.chromeDriverPath);
        LoggingPreferences preferences=new LoggingPreferences();
        preferences.enable(LogType.PERFORMANCE, Level.ALL);

        ChromeOptions options=new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.setCapability("loggingPrefs",preferences);
        options.setCapability("goog:loggingPrefs",preferences);
        options.addArguments();

        ChromeDriver driver=new ChromeDriver(options);
        driver.get("https://we.51job.com/pc/search");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        LogEntries logs=driver.manage().logs().get(LogType.PERFORMANCE);
        List<String> list=new ArrayList<>();
        for(LogEntry entry:logs){
            list.add(entry.getMessage());
            System.out.println(entry.getMessage());
        }
        try {
            FileUtils.writeLines(new File("C:\\Users\\15766\\Desktop\\log.txt"),list);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
