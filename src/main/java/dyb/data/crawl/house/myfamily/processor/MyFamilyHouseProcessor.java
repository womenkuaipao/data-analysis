package dyb.data.crawl.house.myfamily.processor;

import dyb.data.crawl.configuration.SeedConfiguration;
import dyb.data.crawl.constant.TypeEnum;
import dyb.data.crawl.repository.HouseInfoRepository;
import dyb.data.crawl.repository.LinkInfoRepository;
import dyb.data.crawl.repository.domain.HouseInfo;
import dyb.data.crawl.repository.domain.LinkInfo;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.*;

@Component
@PropertySource(value="seed-url.properties")
public class MyFamilyHouseProcessor implements PageProcessor {
    private Logger logger= LoggerFactory.getLogger(MyFamilyHouseProcessor.class);
    private Site site = Site.me().setRetryTimes(3).setSleepTime(10);

    @Autowired
    private SeedConfiguration seedConfiguration;
    @Autowired
    private HouseInfoRepository houseInfoRepository;
    @Autowired
    private LinkInfoRepository linkInfoRepository;

    @Override
    public void process(Page page) {
        Html html=page.getHtml();
        String pageUrl=page.getUrl().get();
        Optional<LinkInfo> linkInfo = linkInfoRepository.findById(pageUrl);
        if(linkInfo.isPresent()&&linkInfo.get().getSuccess()){
            return;
        }
        //详情页
        if(page.getUrl().regex("https://hz.5i5j.com/zufang/\\d+.html").match()){
            try {
                int index = pageUrl.lastIndexOf("/");
                String hash = pageUrl.substring(index + 1).replace(".html", "");
                boolean exist=houseInfoRepository.existsByHash(hash);
                if(exist){
                    linkInfoRepository.changeLinkStatus(pageUrl);
                    return;
                };
                String title=html.xpath("/html/body/div[5]/div[1]/div[1]/h1/text()").get();
                String price = html.xpath("/html/body/div[5]/div[2]/div[2]/div[1]/div[1]/div/p/span[1]/text()").get();
                String areaSize = html.xpath("/html/body/div[5]/div[2]/div[2]/div[1]/div[2]/div[2]/div/p[1]/text()").get();
                String community = html.xpath("/html/body/div[5]/div[2]/div[2]/div[2]/ul/li[1]/a/text()").get();
                String zone = html.xpath("/html/body/div[5]/div[2]/div[2]/div[2]/ul/li[8]/a[1]/text()").get();
                String nearBy = html.xpath("/html/body/div[5]/div[2]/div[2]/div[2]/ul/li[11]/a/text()").get();
                Date buildTime = transBuildTime(html.xpath("/html/body/div[5]/div[2]/div[2]/div[1]/div[2]/div[3]/div/p[2]/text()").get());
                Pair<Float, Float> floatFloatPair = transLongLati(html.xpath("/html/head/meta[8]").get());
                HouseInfo houseInfo = new HouseInfo();
                houseInfo.setAreaSize(Float.valueOf(areaSize));
                houseInfo.setHouseName(community);
                houseInfo.setZone(zone);
                houseInfo.setNearby(nearBy);
                houseInfo.setDataFrom(TypeEnum.MY_FAMILY.name());
                houseInfo.setPrice(Float.valueOf(price));
                houseInfo.setHash(hash);
                houseInfo.setBuildTime(buildTime);
                houseInfo.setLongitude(floatFloatPair.getLeft());
                houseInfo.setLatitude(floatFloatPair.getRight());
                houseInfo.setDetail(title);
                houseInfoRepository.save(houseInfo);
                linkInfoRepository.changeLinkStatus(pageUrl);
            }catch (Exception e){
                logger.error("数据解析失败",e);
            }
        }else {//列表页
            List<String> links=new ArrayList<>();
            //解析详情url
            List<Selectable> pageContent = html.xpath("/html/body/div[6]/div[1]/div[2]/ul/li").nodes();
            for(Selectable node:pageContent){
                String detailLink=node.css("div.listImg").css("a").links().get();
                links.add(detailLink);
            }
            //把下一页加入列表
            String nextPageLink = html.xpath("/html/body/div[6]/div[1]/div[3]/div[2]/a[1]").links().get();
            links.add(nextPageLink);
            page.addTargetRequests(links);
            //将url入库存储
            saveLinks(links);
            linkInfoRepository.changeLinkStatus(pageUrl);
        }
    }

    private Date transBuildTime(String content){
        if(!StringUtils.isEmpty(content)) {
            try {
                String year = content.substring(0, 4);
                Calendar c=Calendar.getInstance();
                c.set(Integer.valueOf(year),1,1);
                return c.getTime();
            }catch (Exception e){
                logger.error("建楼时间转换错误,内容为"+content,e);
            }
        }
        return null;
    }

    private Pair<Float,Float> transLongLati(String content){
        if(!StringUtils.isEmpty(content)){
            int index = content.indexOf("coord=");
            if(index!=-1){
                String[] split = content.substring(index + 6, content.length() - 2).split(",");
                return Pair.of(Float.valueOf(split[0]),Float.valueOf(split[1]));
            }
        }
        return null;
    }

    private synchronized void saveLinks(List<String> links){
        if(!CollectionUtils.isEmpty(links)){
            List<LinkInfo> linkInfos=new ArrayList<>();
            for(String link:links){
                LinkInfo linkInfo=new LinkInfo();
                linkInfo.setUrl(link);
                linkInfo.setSuccess(false);
                linkInfo.setDataFrom(TypeEnum.MY_FAMILY.name());
                linkInfo.setCreateTime(System.currentTimeMillis());
                linkInfos.add(linkInfo);
            }
            linkInfoRepository.saveAll(linkInfos);
        }
    }
}
