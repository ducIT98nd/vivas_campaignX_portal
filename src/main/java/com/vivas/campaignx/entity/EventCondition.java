package com.vivas.campaignx.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "event_condition")
public class EventCondition {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "DATA_INPUT")
    private String dataInput;

    @Column(name = "bigdata_event_id")
    private Long bigdataEventId;

    @Column(name = "status")
    private Integer status;

    @Column(name = "json_field")
    private String jsonField;

    public String getJsonField() {
        return jsonField;
    }

    public void setJsonField(String jsonField) {
        this.jsonField = jsonField;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getBigdataEventId() {
        return bigdataEventId;
    }

    public void setBigdataEventId(Long bigdataEventId) {
        this.bigdataEventId = bigdataEventId;
    }

    public String getDataInput() {
        return dataInput;
    }

    public void setDataInput(String dataInput) {
        this.dataInput = dataInput;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
