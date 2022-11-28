package com.vivas.campaignx.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "CMS_REPORT_BY_CAMPTYPE")
public class CmsReportByCamptype {

    @Id
    @Column(name = "REPORTED_DATE")
    private Date reportedDate;
    @Column(name = "COUNT_RUNNING_CAMPAIGN")
    private Long countRunningCampaign;
    @Column(name = "SUCCESS_MESSAGE")
    private Long successMessage;
    @Column(name = "RESPONSE_MESSAGE")
    private Long responseMessage;
    @Column(name = "INVITATION_MESSAGE")
    private Long invitationMessage;
    @Column(name = "REVENUE")
    private Long revenue;
    @Column(name = "CAMPAIGN_TYPE")
    private Integer campaignType;
    @Column(name = "CAMPAIGN_STATUS")
    private Integer campaignStatus;

    public Date getReportedDate() {
        return reportedDate;
    }

    public void setReportedDate(Date reportedDate) {
        this.reportedDate = reportedDate;
    }

    public Long getCountRunningCampaign() {
        return countRunningCampaign;
    }

    public void setCountRunningCampaign(Long countRunningCampaign) {
        this.countRunningCampaign = countRunningCampaign;
    }

    public Long getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(Long successMessage) {
        this.successMessage = successMessage;
    }

    public Long getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(Long responseMessage) {
        this.responseMessage = responseMessage;
    }

    public Long getInvitationMessage() {
        return invitationMessage;
    }

    public void setInvitationMessage(Long invitationMessage) {
        this.invitationMessage = invitationMessage;
    }

    public Long getRevenue() {
        return revenue;
    }

    public void setRevenue(Long revenue) {
        this.revenue = revenue;
    }

    public Integer getCampaignType() {
        return campaignType;
    }

    public void setCampaignType(Integer campaignType) {
        this.campaignType = campaignType;
    }

    public Integer getCampaignStatus() {
        return campaignStatus;
    }

    public void setCampaignStatus(Integer campaignStatus) {
        this.campaignStatus = campaignStatus;
    }

}
