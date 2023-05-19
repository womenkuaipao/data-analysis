package dyb.data.crawl.job.wojob.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Service
public class WoJobService {
    private Logger logger= LoggerFactory.getLogger(WoJobService.class);

    @Autowired
    private RestTemplate restTemplate;

    @PostConstruct
    public void initQueryWord(){

    }

//    public WoJobResult pageWoJob(String keyWord,Integer pageNo,Integer pageSize){
//        String url="https://cupidjob.51job.com/open/noauth/search-pc?api_key=51job&timestamp="
//                + (System.currentTimeMillis()/1000)
//                +"&keyword="+keyWord+"&searchType=2&function=&industry=&jobArea=080200&jobArea2=&landmark=&metro=&salary=&workYear=&degree=&companyType=&companySize=&jobType=&issueDate=&sortType=1&"
//                +"pageNum="+pageNo+"&requestId=c1ee08c6987c3fb15380e6e08caa2c21&pageSize=20&source=1&accountId=&pageCode=sou%7Csou%7Csoulb";
//    }

}
