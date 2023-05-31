package dyb.data.crawl.job.wojob.service;

import dyb.data.crawl.constant.AreaEnum;
import dyb.data.crawl.constant.TypeEnum;
import dyb.data.crawl.job.wojob.dto.WoJobInfo;
import dyb.data.crawl.job.wojob.repository.WoJobTaskRepository;
import dyb.data.crawl.job.wojob.repository.domain.WoJobTask;
import dyb.data.crawl.repository.JobQueryWordRepository;
import dyb.data.crawl.repository.domain.JobInfo;
import dyb.data.crawl.repository.domain.JobQueryWord;
import dyb.data.crawl.tool.StringUtil;
import dyb.data.crawl.tool.TimeUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.*;

@Service
public class WoJobService {
    private Logger logger= LoggerFactory.getLogger(WoJobService.class);

    @Autowired
    private WoJobTaskRepository woJobTaskRepository;
    @Autowired
    private JobQueryWordRepository jobQueryWordRepository;

    public JobInfo transJobInfo(WoJobInfo woJobInfo){
        JobInfo jobInfo=new JobInfo();
        jobInfo.setId(woJobInfo.getJobId());
        jobInfo.setLongitude(StringUtil.string2Float(woJobInfo.getLon()));
        jobInfo.setLatitude(StringUtil.string2Float(woJobInfo.getLat()));
        jobInfo.setJobName(woJobInfo.getJobName());
        jobInfo.setCompanyName(woJobInfo.getCompanyName());
        jobInfo.setCompanyType(woJobInfo.getCompanyTypeString());
        jobInfo.setCompanySize(woJobInfo.getCompanySizeString());
        jobInfo.setProvideSalaryString(woJobInfo.getProvideSalaryString());
        Pair<Integer, Integer> salaryRange = getSalaryRange(woJobInfo.getProvideSalaryString());
        jobInfo.setSalaryLow(salaryRange.getFirst());
        jobInfo.setSalaryUp(salaryRange.getSecond());
        jobInfo.setJobTags(StringUtils.join(woJobInfo.getJobTags(),","));
        jobInfo.setDataFrom(TypeEnum.WOJOB.name());
        jobInfo.setWorkYear(woJobInfo.getWorkYearString());
        jobInfo.setKeyWord(woJobInfo.getKeyWord());
        jobInfo.setSaveTime(new Date());
        try {
            Date date=TimeUtil.getDate(woJobInfo.getUpdataDateTime());
            jobInfo.setUpdateTime(date);
        } catch (ParseException e) {
            logger.warn("时间转换失败[{}]",woJobInfo.getUpdataDateTime(),e);
        }
        return jobInfo;
    }

    private Pair<Integer,Integer> getSalaryRange(String provideSalaryString){
        if(StringUtils.isNotEmpty(provideSalaryString)){
            try {
                int index = 0;
                boolean yearSalary=false;
                if ((index = provideSalaryString.indexOf("·")) != -1) {
                    provideSalaryString = provideSalaryString.substring(0, index);
                }
                if(provideSalaryString.endsWith("/年")){
                    yearSalary=true;
                    provideSalaryString= provideSalaryString.replace("/年","");
                }
                String[] split = provideSalaryString.split("-");
                String secondType = getSalaryDanwei(split[1]);
                int up = getSalary(secondType,split[1]);
                String firstType = getSalaryDanwei(split[0]);
                if (StringUtils.isEmpty(firstType)) {
                    firstType = secondType;
                }
                int low = getSalary(firstType,split[0]);
                if(yearSalary){
                    return Pair.of(low/12,up/12);
                }else {
                    return Pair.of(low, up);
                }
            }catch (Exception e){
                logger.error("只关注解析正常的薪资,不正常内容为{}",provideSalaryString);
            }
        }
        return Pair.of(-1,-1);
    }

    private String getSalaryDanwei(String salary){
        if(salary.contains("千")){
            return "千";
        }else if(salary.contains("万")){
            return "万";
        }
        return "";
    }

    private int getSalary(String danwei,String s){
        Float valueOf = Float.valueOf(s.replace(danwei, ""));
        if(danwei.equals("千")){
            return (int)(valueOf.floatValue()*1000);
        }else if(danwei.equals("万")){
            return (int)(valueOf.floatValue()*10000);
        }
        return valueOf.intValue();
    }

    public Map<String,String> getUrlParam(String url){
        try {
            url=URLDecoder.decode(url,"utf8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        Map<String,String> params=new HashMap<>();
        if(StringUtils.isNotEmpty(url)){
            int index=url.indexOf("?");
            if(index!=-1){
                url=url.substring(index+1);
                String[] keyValues=url.split("&");
                for(String k:keyValues){
                    String[] split = k.split("=");
                    if(split.length==2){
                        params.put(split[0],split[1]);
                    }else{
                        params.put(split[0],null);
                    }
                }
            }
        }
        return params;
    }

    public String getUrlParam(String url,String param){
        Map<String, String> urlParam = getUrlParam(url);
        return urlParam.get(param);
    }
}
