package dyb.data.crawl.repository;

import dyb.data.crawl.repository.domain.ScheduleInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ScheduleInfoRepository extends JpaRepository<ScheduleInfo,Integer>
        , PagingAndSortingRepository<ScheduleInfo,Integer>, JpaSpecificationExecutor<ScheduleInfo> {
    ScheduleInfo findBySearchKeyAndDataFromAndCenter(String key,String dataFrom,String center);
}
