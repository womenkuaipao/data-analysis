package dyb.data.crawl.job.wojob.processor;

import com.fasterxml.jackson.core.type.TypeReference;
import dyb.data.crawl.constant.TypeEnum;
import dyb.data.crawl.constant.ZoneEnum;
import dyb.data.crawl.job.wojob.dto.WoJobInfo;
import dyb.data.crawl.job.wojob.dto.WoJobPage;
import dyb.data.crawl.job.wojob.dto.WoJobResult;
import dyb.data.crawl.house.myfamily.processor.MyFamilyHouseProcessor;
import dyb.data.crawl.repository.JobInfoRepository;
import dyb.data.crawl.repository.LinkInfoRepository;
import dyb.data.crawl.repository.domain.JobInfo;
import dyb.data.crawl.tool.JacksonTool;
import dyb.data.crawl.tool.StringUtil;
import dyb.data.crawl.tool.TimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.text.ParseException;
import java.util.*;

@Component
@PropertySource(value="seed-url.properties")

public class WoJobProcessor  implements PageProcessor {
    private Logger logger= LoggerFactory.getLogger(MyFamilyHouseProcessor.class);
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    private int pageNo=1;
    private int pageSize=100;
    @Value("${wo.job.seed}")
    private String siteSeed;
    @Autowired
    private LinkInfoRepository linkInfoRepository;
    @Autowired
    private JobInfoRepository jobInfoRepository;
    private String resultPre="<html><head><meta name=\"color-scheme\" content=\"light dark\"></head><body><pre style=\"word-wrap: break-word; white-space: pre-wrap;\">";
    private String resultSuf="</pre></body></html>";
    @Override
    public void process(Page page) {
        String url = page.getUrl().get();
        Map<String, String> urlParam = getUrlParam(url);
        int totalCount=0;
        if(!url.equals(siteSeed)){
            try {
                String content = page.getHtml().get();
                String json = content.replace(resultPre, "").replace(resultSuf, "");
                WoJobResult woJobResult = JacksonTool.json2Object(json, new TypeReference<WoJobResult>() {
                });
                //解析
                WoJobPage woJobPage=Optional.ofNullable(woJobResult).map(j->j.getResultBody()).map(j->j.getJob()).get();
                if(woJobPage!=null){
                    List<WoJobInfo> items = woJobPage.getItems();
                    totalCount = woJobPage.getTotalCount();
                    if(!CollectionUtils.isEmpty(items)){
                        List<JobInfo> jobInfos=new ArrayList<>();
                        for(WoJobInfo woJobInfo:items){
                            JobInfo jobInfo = transJobInfo(woJobInfo);
                            jobInfos.add(jobInfo);
                        }
                        jobInfoRepository.saveAll(jobInfos);
                    }
                }
            }catch (Exception e){
                logger.error("解析WoJob结果异常",e);
            }
        }
        int pageNo=Integer.valueOf(urlParam.getOrDefault("pageNum","0"));
        //获取下一页参数
        int nextPageNo = nextPageNo(totalCount, pageNo);
        //下一个区域查询参数
        ZoneEnum nextZoneEnum=null;
        if(url.equals(siteSeed)){
            nextZoneEnum=ZoneEnum.ALL;
        }else{
            String jobAreaCode=urlParam.get("jobArea");
            ZoneEnum thisZone = ZoneEnum.getByWoCode(jobAreaCode);
            nextZoneEnum=thisZone.nextZone();
        }
        //

        page.addTargetRequest("https://cupidjob.51job.com/open/noauth/search-pc?api_key=51job&timestamp=1684236600&keyword=java&searchType=2&function=&industry=&jobArea=080200&jobArea2=&landmark=&metro=&salary=&workYear=&degree=&companyType=&companySize=&jobType=&issueDate=&sortType=1&pageNum=1&requestId=&pageSize=20&source=1&accountId=&pageCode=sou%7Csou%7Csoulb");
        System.out.println(page.getHtml());
        System.currentTimeMillis();
    }

    private int nextPageNo(int total,int pageNo){
        if(total==0){
            return -1;
        }
        int next=pageNo+1;
        if(pageNo*pageSize>total){
            return -1;
        }
        return next;
    }

    private JobInfo transJobInfo(WoJobInfo woJobInfo){
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
        jobInfo.setSource(TypeEnum.WOJOB.getType());
        jobInfo.setWorkYear(woJobInfo.getWorkYearString());
        try {
            Date date=TimeUtil.getDate(woJobInfo.getUpdataDateTime());
            jobInfo.setUpdateTime(date);
        } catch (ParseException e) {
            logger.warn("时间转换失败[{}]",woJobInfo.getUpdataDateTime(),e);
        }
        return jobInfo;
    }

    private Pair<Integer,Integer> getSalaryRange(String provideSalaryString){
        if(StringUtils.isAllEmpty(provideSalaryString)){
            try {
                int index = 0;
                if ((index = provideSalaryString.indexOf("·")) != -1) {
                    provideSalaryString = provideSalaryString.substring(0, index);
                }
                String[] split = provideSalaryString.split("-");
                String secondType = getSalaryDanwei(split[1]);
                int up = getSalary(split[1], secondType);
                String firstType = getSalaryDanwei(split[0]);
                if (StringUtils.isEmpty(firstType)) {
                    firstType = secondType;
                }
                int low = getSalary(split[0], firstType);
                return Pair.of(low, up);
            }catch (Exception e){
                logger.error("只关注解析正常的薪资,不正常内容为{}",provideSalaryString);
            }
        }
        return Pair.of(null,null);
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

    private Map<String,String> getUrlParam(String url){
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

//    private String buildNewUrl(int pageNo,String zoneCode){
//
//    }
}
