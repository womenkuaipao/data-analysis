package dyb.data.crawl.job.wojob.processor;

import com.fasterxml.jackson.core.type.TypeReference;
import dyb.data.crawl.configuration.SeedConfiguration;
import dyb.data.crawl.constant.TypeEnum;
import dyb.data.crawl.constant.ZoneEnum;
import dyb.data.crawl.house.myfamily.processor.MyFamilyHouseProcessor;
import dyb.data.crawl.job.wojob.dto.*;
import dyb.data.crawl.job.wojob.service.WoJobService;
import dyb.data.crawl.repository.JobInfoRepository;
import dyb.data.crawl.repository.JobQueryWordRepository;
import dyb.data.crawl.repository.LinkInfoRepository;
import dyb.data.crawl.repository.domain.JobInfo;
import dyb.data.crawl.repository.domain.JobQueryWord;
import dyb.data.crawl.repository.domain.LinkInfo;
import dyb.data.crawl.tool.JacksonTool;
import dyb.data.crawl.tool.StringUtil;
import dyb.data.crawl.tool.TimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Deprecated
public class WoJobProcessor  implements PageProcessor {
    private Logger logger= LoggerFactory.getLogger(MyFamilyHouseProcessor.class);
    private Site site = Site.me().setRetryTimes(3).setSleepTime(6000);

    private int pageSize=100;

    @Autowired
    private SeedConfiguration seedConfiguration;
    @Autowired
    private LinkInfoRepository linkInfoRepository;
    @Autowired
    private JobInfoRepository jobInfoRepository;
    @Autowired
    private JobQueryWordRepository jobQueryWordRepository;
    @Autowired
    private WoJobService woJobService;


    private String resultPre="<html>\n" +
            " <head>\n" +
            "  <meta name=\"color-scheme\" content=\"light dark\">\n" +
            " </head>\n" +
            " <body>\n" +
            "  <pre style=\"word-wrap: break-word; white-space: pre-wrap;\">";
    private String resultSuf="</pre>\n" +
            " </body>\n" +
            "</html>";
    private List<String> keyWords=new ArrayList<>();
    @PostConstruct
    public void init(){
        List<JobQueryWord> jobQueryWords = jobQueryWordRepository.queryByDataFrom(TypeEnum.WOJOB.name());
        if(!CollectionUtils.isEmpty(jobQueryWords)){
            keyWords=jobQueryWords.stream().map(j->j.getKeyWord()).collect(Collectors.toList());
        }
    }
    @Override
    public void process(Page page) {
        String url = page.getUrl().get();
        //访问页面，构建cookie
        if(url.equals(seedConfiguration.getWoJob())){
            if(1==1)return;
            try {
                long dateStart = TimeUtil.getDateStart(new Date());
                long dateEnd = TimeUtil.getDateEnd(new Date());
                Integer count = linkInfoRepository.countByCreateTimeBetweenAndDataFrom(dateStart, dateEnd, TypeEnum.WOJOB.name());
                if(count!=null&&count>0){
                    logger.info("今日已检测过，不再检测");
                }
            }catch (Exception e){
                logger.error("检查wojob当天检索情况失败",e);
            }
            List<String> allRequestUrl = getFirstPageAllRequestUrl();
            page.addTargetRequests(allRequestUrl);
            saveLinkInfos(allRequestUrl);
        }else{
            try {
                Thread.sleep(1000);
                String content = page.getHtml().get();
                String json = content.replace(resultPre, "").replace(resultSuf, "");
                WoJobResult<WoJobResultBody> woJobResult = JacksonTool.json2Object(json, new TypeReference<WoJobResult<WoJobResultBody>>() {
                });
                //解析
                int total=0;
                WoJobPage woJobPage=Optional.ofNullable(woJobResult).map(j->j.getResultbody()).map(j->j.getJob()).get();
                if(woJobPage!=null){
                    List<WoJobInfo> items = woJobPage.getItems();
                    total=woJobPage.getTotalCount();
                    if(!CollectionUtils.isEmpty(items)){
                        List<JobInfo> jobInfos=new ArrayList<>();
                        for(WoJobInfo woJobInfo:items){
                            try {
                                JobInfo jobInfo = woJobService.transJobInfo(woJobInfo);
                                jobInfos.add(jobInfo);
                            }catch (Exception e){
                                logger.error("转换wojob为domain异常",e);
                            }
                        }
                        jobInfoRepository.saveAll(jobInfos);
                    }
                }
                //如果是首页，将分页后的url全部处理好并且入库
                Map<String, String> urlParam = woJobService.getUrlParam(url);
                if(firstPage(urlParam)){
                    int cycle=(total+pageSize-1)/pageSize;
                    List<String> nextPages=new ArrayList<>();
                    for(int i=2;i<=cycle;i++){
                        String pageUrl = getPageUrl(i, urlParam);
                        nextPages.add(pageUrl);
                    }
                    if(!CollectionUtils.isEmpty(nextPages)){
                        saveLinkInfos(nextPages);
                        page.addTargetRequests(nextPages);
                    }
                }
                Optional<LinkInfo> linkInfo = linkInfoRepository.findById(url);
                if(linkInfo.isPresent()){
                    LinkInfo domain = linkInfo.get();
                    domain.setSuccess(true);
                    linkInfoRepository.save(domain);
                }
            }catch (Exception e){
                logger.error("解析WoJob结果异常",e);
            }
        }
    }

//    @Override
//    public Site getSite() {
//        return site;
//    }

    private void saveLinkInfos(List<String> allLinks){
        List<LinkInfo> domains=new ArrayList<>();
        for(String link:allLinks){
            LinkInfo linkInfo=new LinkInfo();
            linkInfo.setSuccess(false);
            linkInfo.setUrl(link);
            linkInfo.setCreateTime(System.currentTimeMillis());
            linkInfo.setDataFrom(TypeEnum.WOJOB.name());
            domains.add(linkInfo);
        }
        linkInfoRepository.saveAll(domains);
    }

    /**
     * 构造所有参数的首页url，得分页查，一次性返回1000条，数据会被截断
     * @return
     */
    private List<String> getFirstPageAllRequestUrl(){
        List<String> allUrl=new ArrayList<>();
        for(ZoneEnum zoneEnum:ZoneEnum.values()){
            for(String keyWord:keyWords){
                String url = buildNewUrl(zoneEnum.getWoJobCode(), keyWord,1);
                allUrl.add(url);
            }
        }
        return allUrl;
    }

    private String getPageUrl(int pageNo,Map<String,String> urlParam){
        String jobArea2 = urlParam.get("jobArea2");
        String keyWord = urlParam.get("keyword");
        String newUrl=buildNewUrl(jobArea2,keyWord,pageNo);
        return newUrl;
    }

    private String buildNewUrl(String zoneCode,String keyWord,int pageNo){
        String url="https://we.51job.com/api/job/search-pc?api_key=51job&" +
                "timestamp="+(System.currentTimeMillis()/1000)+"&keyword="+keyWord+
                "&searchType=2&function=&industry=&jobArea=080201&jobArea2="+zoneCode+
                "&landmark=&metro=&salary=&workYear=&degree=&companyType=&companySize=" +
                "&jobType=&issueDate=3&sortType=0&pageNum="+pageNo+"&requestId=&pageSize="+pageSize +
                "&source=1&accountId=&pageCode=sou%7Csou%7Csoulb";
        return url;
    }

    private boolean firstPage(Map<String,String> urlParam){
        String pageNum = urlParam.get("pageNum");
        if(StringUtils.isNotEmpty(pageNum)){
            int page = Integer.valueOf(pageNum);
            if(page==1){
                return true;
            }
        }
        return false;
    }
}
