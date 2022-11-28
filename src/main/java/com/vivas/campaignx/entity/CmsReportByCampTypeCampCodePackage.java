package com.vivas.campaignx.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "CMS_REPORT_BY_CAMPTYPE_CAMPCODE_PACKAGE")
public class CmsReportByCampTypeCampCodePackage {
    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "REPORTED_DATE")
    private Date reportedDate;
    @Column(name = "CAMPAIGN_CODE")
    private String campaignCode;
    @Column(name = "CAMPAIGN_NAME")
    private String campaignName;
    @Column(name = "PACKAGE")
    private String packages;
    @Column(name = "SUCCESS_MESSAGE")
    private Long successMessage;
    @Column(name = "INVITATION_MESSAGE")
    private Long invitationMessage;
    @Column(name = "REVENUE")
    private Long revenue;
    @Column(name = "CAMPAIGN_TYPE")
    private Integer campaignType;
    @Column(name = "CAMPAIGN_STATUS")
    private Integer campaignStatus;
    @Column(name = "SUCCESS_REGISTER_MESSAGE")
    private Long successRegisterMessage;
    @Column(name = "REGISTER_MESSAGE")
    private Long registerMessage;
    @Column(name = "CHANNEL")
    private Integer channel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSuccessRegisterMessage() {
        return successRegisterMessage;
    }

    public void setSuccessRegisterMessage(Long successRegisterMessage) {
        this.successRegisterMessage = successRegisterMessage;
    }

    public Long getRegisterMessage() {
        return registerMessage;
    }

    public void setRegisterMessage(Long registerMessage) {
        this.registerMessage = registerMessage;
    }

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public Date getReportedDate() {
        return reportedDate;
    }

    public void setReportedDate(Date reportedDate) {
        this.reportedDate = reportedDate;
    }

    public String getCampaignCode() {
        return campaignCode;
    }

    public void setCampaignCode(String campaignCode) {
        this.campaignCode = campaignCode;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public String getPackages() {
        return packages;
    }

    public void setPackages(String packages) {
        this.packages = packages;
    }

    public Long getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(Long successMessage) {
        this.successMessage = successMessage;
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
