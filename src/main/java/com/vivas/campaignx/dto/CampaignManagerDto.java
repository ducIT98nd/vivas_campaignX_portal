package com.vivas.campaignx.dto;

import java.util.Date;

public class CampaignManagerDto{
    private Long id;
    private String name;
    private Integer status;
    private Integer statusOld;
    private String startDate;
    private String endDate;
    private String userName;
    private String createdDate;
    private String createdDateConvert;
    private Integer type;

    public Integer getStatusOld() {
        return statusOld;
    }

    public void setStatusOld(Integer statusOld) {
        this.statusOld = statusOld;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedDateConvert() {
        return createdDateConvert;
    }

    public void setCreatedDateConvert(String createdDateConvert) {
        this.createdDateConvert = createdDateConvert;
    }
}
