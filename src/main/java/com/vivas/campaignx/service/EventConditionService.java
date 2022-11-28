package com.vivas.campaignx.service;

import com.vivas.campaignx.entity.BigdataEvent;
import com.vivas.campaignx.entity.EventCondition;
import com.vivas.campaignx.repository.BigdataEventRepository;
import com.vivas.campaignx.repository.EventConditionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventConditionService {
    protected final Logger logger = LogManager.getLogger(this.getClass().getName());

    @Autowired
    private EventConditionRepository eventConditionRepository;

    public List<EventCondition> findByStatusAndBigdataEventId(Integer status, Long eventId) {
        return eventConditionRepository.findByStatusAndBigdataEventId(status, eventId);
    }
}
