package dyb.data.crawl.repository;

import dyb.data.crawl.repository.domain.HouseInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface HouseInfoRepository extends JpaRepository<HouseInfo,Integer>
        , PagingAndSortingRepository<HouseInfo,Integer>, JpaSpecificationExecutor<HouseInfo> {
    /**
     * 判断对应的信息是否已经存在
     * @param hash
     * @return
     */
    public boolean existsByHash(String hash);
}
