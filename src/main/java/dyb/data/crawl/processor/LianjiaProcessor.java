package dyb.data.crawl.processor;

import com.fasterxml.jackson.core.type.TypeReference;
import dyb.data.crawl.repository.HouseInfoRepository;
import dyb.data.crawl.repository.domain.HouseInfo;
import dyb.data.crawl.tool.JacksonTool;
import org.jsoup.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.HtmlNode;
import us.codecraft.webmagic.selector.Selectable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class LianjiaProcessor implements PageProcessor {
    Logger logger= LoggerFactory.getLogger(LianjiaProcessor.class);
    private Site site = Site.me().setRetryTimes(3).setSleepTime(2000);
    public static String SITE_SEED="https://hz.5i5j.com/zufang/";

    @Autowired
    private HouseInfoRepository houseInfoRepository;

    @Override
    public void process(Page page) {
        Html html=page.getHtml();
        //获取下一页链接
        String nextPageLink = html.xpath("/html/body/div[6]/div[1]/div[3]/div[2]/a[1]").links().get();
        page.addTargetRequest(nextPageLink);
        //解析页面内容
        List<Selectable> pageContent = html.xpath("/html/body/div[6]/div[1]/div[2]/ul/li").nodes();
        if(!CollectionUtils.isEmpty(pageContent)){
            List<HouseInfo> houseInfoDos=new ArrayList<>();
            for(Selectable node:pageContent){
                //解析房源id
                String hash=null;
                try {
                    String giojson = node.css("div", "giojson").get();
                    Map<String, Object> jsonMap = null;
                    jsonMap = JacksonTool.json2Object(giojson, new TypeReference<Map<String, Object>>() {
                    });
                    hash = jsonMap.get("houseid_var").toString();
                }catch (Exception e){
                    logger.error("hash获取异常",e);
                    break;
                }
                //解析租房信息
                Selectable info=node.css(".listX");
                List<Selectable> list = info.css("p").nodes();
                String[] houseInfos = list.get(0).get().replace("<p><i class=\"i_01\"></i>", "").replace("</p>", "").split("·");
                //解析区域
                String areaInfo = list.get(1).toString().replace("<p><i class=\"i_02\"></i>", "");
                int index = areaInfo.indexOf("<a href");
                String areaName = areaInfo.substring(0, index).trim();
                //解析小区名称
                List<Selectable> localtionNodes = list.get(1).css("a").nodes();
                String community = localtionNodes.get(0).css("a", "text").toString().trim();
                //解析小区靠近区域
                String nearBy = localtionNodes.get(1).css("a", "text").toString().trim();
                String detailPageLink = localtionNodes.get(1).links().get();
                //出租价格
                String price=list.get(3).css("strong","text").toString().trim();
                HouseInfo houseInfo=new HouseInfo();
                houseInfo.setHouseName(community);
                houseInfo.setHash(hash);
                houseInfo.setPrice(Float.valueOf(price));
                houseInfo.setSource(1);
                houseInfo.setZone(areaName);
                houseInfo.setNearby(nearBy);
                houseInfo.setAreaSize(Integer.valueOf(houseInfos[1].replace("㎡","")));
//                houseInfo.setAreaSize();
                System.out.println(info);
            }
        }
    }

}
