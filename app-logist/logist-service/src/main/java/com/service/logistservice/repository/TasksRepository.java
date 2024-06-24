package com.service.logistservice.repository;

import com.service.logistservice.model.Tasks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TasksRepository extends JpaRepository<Tasks, Long> {
       List<Tasks> findByCompanyName(String companyName);
       List<Tasks> findByDriverId(String driverId);

       @Query("SELECT e FROM Tasks e WHERE e.companyName = :companyName")
       Page<Tasks> findBySomeCompanyName(@Param("companyName") String companyName, Pageable pageable);

       @Query(value = "SELECT * FROM (SELECT * FROM tasks\n" +
               "        WHERE date_trunc('day', create_time) = current_date)ranked_tasks\n" +
               "        WHERE company_name=:companyName", nativeQuery = true)
       List<Tasks> findByTasksToday(@Param("companyName") String companyName);

}
