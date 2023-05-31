package dyb.data.crawl.job.wojob.action;

import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

public class WoJobAction {
    public void keyWord(ChromeDriver driver, String searchKey){
        driver.findElement(By.id("keywordInput")).sendKeys(searchKey);
    }
}
