package dyb.data.crawl.repository;

import dyb.data.crawl.repository.domain.BaiduArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BaiduAreaRepository extends JpaRepository<BaiduArea,String>
        , PagingAndSortingRepository<BaiduArea,String>, JpaSpecificationExecutor<BaiduArea> {
}
