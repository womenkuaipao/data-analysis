package dyb.data.crawl.repository;

import dyb.data.crawl.repository.domain.JobQueryWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface JobQueryWordRepository extends JpaRepository<JobQueryWord,Integer>
        , PagingAndSortingRepository<JobQueryWord,Integer>, JpaSpecificationExecutor<JobQueryWord> {
    List<JobQueryWord> queryByDataFrom(String dataFrom);
}
