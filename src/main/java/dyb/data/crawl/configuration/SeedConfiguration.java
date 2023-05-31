package dyb.data.crawl.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
@PropertySource(value = "seed-url.properties")
public class SeedConfiguration {
    @Value("${my.family.house}")
    private String myFamilyHouse;
    @Value("${wo.job.seed}")
    private String woJob;
    @Value("${wo.job.query.perfix}")
    private String woJobQueryPerfix;
}
