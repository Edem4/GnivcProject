package com.service.logistservice.repository;

import com.service.logistservice.model.Events;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventsRepository extends JpaRepository<Events,Long> {
    @Query(value = "SELECT id, status, time_event, flight_id, company_name FROM (\n" +
            "    SELECT id, status, time_event, flight_id, company_name\n" +
            "    FROM (\n" +
            "        SELECT \n" +
            "            id, \n" +
            "            status, \n" +
            "            time_event, \n" +
            "            flight_id,\n" +
            "            company_name,\n" +
            "            ROW_NUMBER() OVER (PARTITION BY flight_id ORDER BY time_event DESC) as rn\n" +
            "        FROM events\n" +
            "        WHERE date_trunc('day', time_event) = current_date\n" +
            "    ) ranked_events\n" +
            "    WHERE rn = 1\n" +
            ") fin_event\n" +
            "WHERE status = :status AND company_name = :companyName", nativeQuery = true)
    List<Events> findLastEventsByStatus(@Param("status") String status ,@Param("companyName") String companyName);

}
