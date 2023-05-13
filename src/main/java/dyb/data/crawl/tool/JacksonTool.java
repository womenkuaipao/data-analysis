package dyb.data.crawl.tool;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Map;

public class JacksonTool {
    public static <T> T json2Object(String json,TypeReference<T> type) throws IOException {
        ObjectMapper mapper=new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        mapper.configure(SerializationFeature.INDENT_OUTPUT,true);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.EAGER_DESERIALIZER_FETCH, false);
        return mapper.readValue(json, type);
    }

    public static void main(String[] args) throws IOException {
        String s="{\"module_var\":\"房源列表\",\"buttonname_var\":\"同小区在租房源\",\"vr_var\":\"否\",\"houseid_var\":\"41019580\",\"iconlocation_var\":\"1\",\"elementname_var\":\"整租·未来科技城·欧美金融城西溪丽晶居·3室\"}";
        Map<String,String> map=json2Object(s,new TypeReference<Map<String, String>>() {
        });
        System.out.println(map);
    }
}
