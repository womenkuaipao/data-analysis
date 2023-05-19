package dyb.data.crawl.init;

import com.fasterxml.jackson.core.type.TypeReference;
import dyb.data.crawl.constant.TypeEnum;
import dyb.data.crawl.job.wojob.dto.JobType;
import dyb.data.crawl.repository.JobQueryWordRepository;
import dyb.data.crawl.repository.domain.JobQueryWord;
import dyb.data.crawl.tool.JacksonTool;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 从配置文件初始化job查询参数到数据库，供后期查询使用
 */
@Component
public class WoJobQueryWordInit implements ApplicationRunner {
    private Logger logger= LoggerFactory.getLogger(WoJobQueryWordInit.class);
    private List<String> filterType= Arrays.asList("后端开发","测试","通信技术开发与应用","电子/电器/仪器仪表","销售人员"
            ,"销售行政及商务","金融","生产/营运","质量安全","技工/普工","广告","影视/媒体","房地产开发","零售","家政服务","电商综合管理"
            ,"互联网产品经理","前端开发","人工智能","互联网设计","平面设计总监","数据","移动开发","游戏开发","运维/技术支持","业务运营");
    private JobQueryWordRepository jobQueryWordRepository;
    public WoJobQueryWordInit(JobQueryWordRepository jobQueryWordRepository){
        this.jobQueryWordRepository=jobQueryWordRepository;
    }
    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<JobQueryWord> jobQueryWords = jobQueryWordRepository.queryByDataFrom(TypeEnum.WOJOB.name());
        if(CollectionUtils.isEmpty(jobQueryWords)){
            String json = FileUtils.readFileToString(ResourceUtils.getFile("classpath:data/wo_job_career.json"), "utf8");
            if(StringUtils.isNotEmpty(json)){
                List<JobType> jobTypes = JacksonTool.json2Object(json, new TypeReference<List<JobType>>() {
                });
                Set<String> keyWords= jobTypes.stream().filter(j->filterType.contains(j.getValue())).map(j -> j.getSubList())
                        .filter(list -> !CollectionUtils.isEmpty(list))
                        .flatMap(list -> list.stream()).map(j->j.getValue()).collect(Collectors.toSet());
                List<JobQueryWord> domains = keyWords.stream().map(k -> JobQueryWord.builder().keyWord(k).dataFrom(TypeEnum.WOJOB.name()).build()).collect(Collectors.toList());
                jobQueryWordRepository.saveAll(domains);
            }
        }
    }
}
