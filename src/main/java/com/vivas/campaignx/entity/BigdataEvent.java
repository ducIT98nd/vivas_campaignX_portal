package com.vivas.campaignx.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "BIGDATA_EVENT")
public class BigdataEvent {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "level_1")
    private String level1;

    @Column(name = "level_2")
    private String level2;

    @Column(name = "event_name")
    private String eventName;

    @Column(name = "status")
    private Integer status;

    @Column(name = "queue_name")
    private String queueName;

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLevel1() {
        return level1;
    }

    public void setLevel1(String level1) {
        this.level1 = level1;
    }

    public String getLevel2() {
        return level2;
    }

    public void setLevel2(String level2) {
        this.level2 = level2;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
