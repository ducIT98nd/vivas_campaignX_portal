package com.vivas.campaignx.dto;

import com.vivas.campaignx.entity.EventCondition;

import java.util.List;

public class EventDataDTO {

    private Long eventId;
    private String eventName;
    private String eventQueueName;
    private List<EventCondition> eventConditions;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventQueueName() {
        return eventQueueName;
    }

    public void setEventQueueName(String eventQueueName) {
        this.eventQueueName = eventQueueName;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public List<EventCondition> getEventConditions() {
        return eventConditions;
    }

    public void setEventConditions(List<EventCondition> eventConditions) {
        this.eventConditions = eventConditions;
    }
}
