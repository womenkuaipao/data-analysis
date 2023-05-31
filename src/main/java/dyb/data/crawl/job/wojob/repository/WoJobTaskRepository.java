package dyb.data.crawl.job.wojob.repository;

import dyb.data.crawl.job.wojob.repository.domain.WoJobTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

public interface WoJobTaskRepository  extends JpaRepository<WoJobTask,Long>
        , PagingAndSortingRepository<WoJobTask,Long>, JpaSpecificationExecutor<WoJobTask> {
    List<WoJobTask> findByCreateTime(Date time);
    List<WoJobTask> findByQueryKeyAndAreaAndCreateTime(String key,String areaName,Date time);
}
