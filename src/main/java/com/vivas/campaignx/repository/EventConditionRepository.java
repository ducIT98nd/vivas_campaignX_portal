package com.vivas.campaignx.repository;

import com.vivas.campaignx.entity.BigdataEvent;
import com.vivas.campaignx.entity.EventCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventConditionRepository extends JpaRepository<EventCondition, Long> {

    List<EventCondition> findByStatus(Integer status);

    List<EventCondition> findByStatusAndBigdataEventId(Integer status, Long eventId);

    @Query(value = "select name from event_condition e where e.bigdata_event_id = :eventId and e.json_field = :jsonField",nativeQuery = true)
    String findEventNameByEventIdAndJSONField(Long eventId, String jsonField);
}
