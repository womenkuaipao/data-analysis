package dyb.data.crawl.job.wojob.service;

import com.fasterxml.jackson.core.type.TypeReference;
import dyb.data.crawl.configuration.SeedConfiguration;
import dyb.data.crawl.constant.Constant;
import dyb.data.crawl.job.wojob.dto.WoJobInfo;
import dyb.data.crawl.job.wojob.dto.WoJobPage;
import dyb.data.crawl.job.wojob.dto.WoJobResult;
import dyb.data.crawl.job.wojob.dto.WoJobResultBody;
import dyb.data.crawl.job.wojob.repository.WoJobTaskRepository;
import dyb.data.crawl.job.wojob.repository.domain.WoJobTask;
import dyb.data.crawl.repository.JobInfoRepository;
import dyb.data.crawl.repository.JobQueryWordRepository;
import dyb.data.crawl.repository.domain.JobInfo;
import dyb.data.crawl.repository.domain.JobQueryWord;
import dyb.data.crawl.selenium.SeleniumDevTool;
import dyb.data.crawl.tool.JacksonTool;
import dyb.data.crawl.tool.TimeUtil;
import dyb.data.crawl.tool.TimerTool;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class WoJobPageService {
    private Logger logger= LoggerFactory.getLogger(WoJobPageService.class);
    private List<WebElement> areaElement=new ArrayList<>();

    @Autowired
    private SeedConfiguration seedConfiguration;
    @Autowired
    private JobQueryWordRepository jobQueryWordRepository;
    @Autowired
    private WoJobTaskRepository woJobTaskRepository;
    @Autowired
    private WoJobService woJobService;
    @Autowired
    private JobInfoRepository jobInfoRepository;
    private AtomicBoolean responseDeal=new AtomicBoolean(false);

    public void download() throws ParseException {
        //创建selenium，增加结果监听器
        SeleniumDevTool seleniumDevTool=new SeleniumDevTool(Constant.chromeDriverPath,seedConfiguration.getWoJob());
        ChromeDriver driver = seleniumDevTool.getDevTool(seedConfiguration.getWoJobQueryPerfix(), this::delResponse);
        sleep(5000);
        chooseCity(driver);
        choosePublishTime(driver);
        List<WebElement> allArea = getAllArea(driver);
        Date todayStart = TimeUtil.getTodayStart();
        List<JobQueryWord> keyWords = jobQueryWordRepository.findAll();
        //循环----关键字
        for(JobQueryWord jobQueryWord:keyWords){
            inputKeyWord(driver,jobQueryWord.getKeyWord());
            clickPage(driver);
            //循环----区域
            for(WebElement areaElement:allArea){
                String areaName = areaElement.getText();
                if("热门地区".equals(areaName))continue;
                //判断今天是否执行过该查询
                List<WoJobTask> todayTasks = woJobTaskRepository.findByQueryKeyAndAreaAndCreateTime(jobQueryWord.getKeyWord(), areaName, todayStart);
                if(CollectionUtils.isEmpty(todayTasks)){
                    chooseArea(driver,areaElement);
                    chooseQuery(driver);
                    //等待下一页出现，下一页不可以点击时跳出
                    TimerTool.initTimer(10000);
                    while(true){
                        sleep(1);
                        if(responseDeal.get()||TimerTool.timerUp()){
                            responseDeal.set(false);
                            if(enableNextPage(driver)){
                                chooseNextPage(driver);
                            }else{
                                WoJobTask domain=new WoJobTask();
                                domain.setArea(areaName);
                                domain.setQueryKey(jobQueryWord.getKeyWord());
                                domain.setSuccess(true);
                                domain.setCreateTime(todayStart);
                                woJobTaskRepository.save(domain);
                                break;
                            }
                            TimerTool.resetTimer();
                        }
                    }
                }
            }
        }
    }

    private void delResponse(String response,String url){
        if(response.contains("function setCookie"))return;
        Map<String, String> urlParam = woJobService.getUrlParam(url);
        String keyword = urlParam.get("keyword");
        String pageNum = urlParam.get("pageNum");
        if(StringUtils.isEmpty(keyword))return;
        try {
            WoJobResult<WoJobResultBody> woJobResult = JacksonTool.json2Object(response, new TypeReference<WoJobResult<WoJobResultBody>>() {
            });
            //解析
            Integer totalCount=0;
            WoJobPage woJobPage= Optional.ofNullable(woJobResult).map(j->j.getResultbody()).map(j->j.getJob()).get();
            if(woJobPage!=null){
                List<WoJobInfo> items = woJobPage.getItems();
                totalCount = woJobPage.getTotalCount();
                if(CollectionUtils.isNotEmpty(items)){
                    List<JobInfo> jobInfos=new ArrayList<>();
                    for(WoJobInfo woJobInfo:items){
                        try {
                            woJobInfo.setKeyWord(keyword);
                            JobInfo jobInfo = woJobService.transJobInfo(woJobInfo);
                            jobInfos.add(jobInfo);
                        }catch (Exception e){
                            logger.error("转换wojob为domain异常",e);
                        }
                    }
                    jobInfoRepository.saveAll(jobInfos);
                }
            }
            logger.info("当前查询keyword【{}】,页数【{}】，总计【{}】",keyword,pageNum,totalCount);
        }catch (Exception e){
            logger.error("解析wojob结果失败",e);
            logger.info("异常url为【{}】,返回内容为【{}】", URLDecoder.decode(url),response);
        }finally {
            responseDeal.set(true);
        }
    }

    public void inputKeyWord(ChromeDriver driver,String keyWord){
        driver.findElement(By.id("keywordInput")).sendKeys(keyWord);
        sleep(5000);
    }

    public void chooseCity(ChromeDriver driver){
        driver.findElement(By.className("allcity")).click();
        sleep(1000);
        driver.findElement(By.xpath("//*[@id=\"pane-0\"]/div/div/div[7]/span")).click();
        sleep(1000);
        driver.findElement(By.xpath("//*[@id=\"dilog\"]/div/div[3]/span/button")).click();
        sleep(1000);
    }

    public void chooseQuery(ChromeDriver driver){
        driver.findElement(By.xpath("//*[@id=\"search_btn\"]")).click();
    }

    /**
     * 判断是否可以点击下一页
     * @param driver
     * @return
     */
    public boolean enableNextPage(ChromeDriver driver) {
        try {
            sleep(4000);
            WebElement element = driver.findElement(By.xpath("//*[@id=\"app\"]/div/div[2]/div/div/div[2]/div/div[2]/div/div[3]/div/div/div/button[2]"));
            String disabled = element.getDomProperty("disabled");
            if(StringUtils.isNotEmpty(disabled)&&("true".equals(disabled)||"disabled".equals(disabled))){
                return false;
            }
        }catch (Exception e){
            return false;
        }
        return true;
    }

    public void chooseNextPage(ChromeDriver driver){
        driver.findElement(By.xpath("//*[@id=\"app\"]/div/div[2]/div/div/div[2]/div/div[2]/div/div[3]/div/div/div/button[2]/i")).click();
    }

    /**
     * 点击空白
     * @param driver
     */
    public void clickPage(ChromeDriver driver){
        driver.findElement(By.xpath("/html/body")).click();
    }

    public List<WebElement> getAllArea(ChromeDriver driver){
        if(CollectionUtils.isEmpty(areaElement)){
            WebElement element = driver.findElement(By.xpath("//*[@id=\"app\"]/div/div[2]/div/div/div[1]/div[2]/div[1]/div[2]/div/div[2]/div[1]"));
            List<WebElement> areas = element.findElements(By.className("ch"));
            if(CollectionUtils.isNotEmpty(areas)){
                areaElement.addAll(areas);
            }
        }
        return areaElement;
    }

    public void chooseArea(ChromeDriver driver,WebElement currentArea){
        sleep(1000);
        currentArea.click();
    }

    public void choosePublishTime(ChromeDriver driver){
        driver.findElement(By.xpath("//*[@id=\"app\"]/div/div[2]/div/div/div[1]/div[2]/div[4]/span")).click();
        sleep(5000);
        driver.findElement(By.xpath("//*[@id=\"app\"]/div/div[2]/div/div/div[1]/div[2]/div[3]/div[5]/div/div[2]/div[1]/div[2]/p")).click();
        sleep(5000);
        driver.findElement(By.xpath("//*[@id=\"app\"]/div/div[2]/div/div/div[1]/div[2]/div[3]/div[5]/div[2]/p[4]/a")).click();

    }

    private void sleep(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
