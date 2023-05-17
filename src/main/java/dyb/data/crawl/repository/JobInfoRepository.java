package dyb.data.crawl.repository;

import dyb.data.crawl.repository.domain.JobInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface JobInfoRepository extends JpaRepository<JobInfo,String>
        , PagingAndSortingRepository<JobInfo,String>, JpaSpecificationExecutor<JobInfo> {
}
