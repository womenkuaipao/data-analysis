package dyb.data.crawl.repository;

import dyb.data.crawl.repository.domain.LinkInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LinkInfoRepository extends JpaRepository<LinkInfo,String>
        , PagingAndSortingRepository<LinkInfo,String>, JpaSpecificationExecutor<LinkInfo> {

    @Modifying
    @Transactional
    @Query(value = "update link_info set success=true where url=?1",nativeQuery = true)
    public void changeLinkStatus(String url);

    @Query(value = "select url from link_info where success=false and type=?1 and create_time>?2",nativeQuery = true)
    public List<String> getAllFailedLinks(Integer type,Long time);
}
