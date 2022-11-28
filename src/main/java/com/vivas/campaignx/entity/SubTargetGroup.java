package com.vivas.campaignx.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "SUB_TARGET_GROUP")
public class SubTargetGroup {

    @Id
    @SequenceGenerator(name = "subTargetGroupGenerator", sequenceName = "SEQ_SUB_TARGET_GROUP", allocationSize = 1)
    @GeneratedValue(generator = "subTargetGroupGenerator", strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;
    @Column(name = "CAMPAIGN_ID")
    private Long campaignId;
    @Column(name = "NAME")
    private String name;
    @Column(name = "CHANNEL")
    private Integer channel;
    @Column(name = "CREATED_USER")
    private String createdUser;
    @Column(name = "UPDATED_USER")
    private String updatedUser;
    @Column(name = "CREATED_DATE")
    private Date createdDate;
    @Column(name = "UPDATED_DATE")
    private Date updatedDate;
    @Column(name = "STATUS")
    private Integer status;
    @Column(name = "QUERY")
    private String query;
    @Column(name = "QUANTITY_MSISDN")
    private Long quantityMsisdn;
    @Column(name = "DATA_JSON")
    private String dataJson;
    @Column(name = "PRIORITY")
    private Integer priority;
    @Column(name = "RATIO")
    private Double ratio;
    @Column(name = "CONTENT_ID")
    private Long contentId;
    @Column(name = "ACCOUNT_SENDING_ID")
    private Long accountSendingId;
    @Column(name = "package_data_id")
    private Long packageDataId;

    @Transient private List<MappingCriteria> mappingCriterias;

    @Transient private SmsContent smsContent;

    public SmsContent getSmsContent() {
        return smsContent;
    }

    public void setSmsContent(SmsContent smsContent) {
        this.smsContent = smsContent;
    }

    public List<MappingCriteria> getMappingCriterias() {
        return mappingCriterias;
    }

    public void setMappingCriterias(List<MappingCriteria> mappingCriterias) {
        this.mappingCriterias = mappingCriterias;
    }

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public String getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(String createdUser) {
        this.createdUser = createdUser;
    }

    public String getUpdatedUser() {
        return updatedUser;
    }

    public void setUpdatedUser(String updatedUser) {
        this.updatedUser = updatedUser;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Long getQuantityMsisdn() {
        return quantityMsisdn;
    }

    public void setQuantityMsisdn(Long quantityMsisdn) {
        this.quantityMsisdn = quantityMsisdn;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Double getRatio() {
        return ratio;
    }

    public void setRatio(Double ratio) {
        this.ratio = ratio;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public Long getAccountSendingId() {
        return accountSendingId;
    }

    public void setAccountSendingId(Long accountSendingId) {
        this.accountSendingId = accountSendingId;
    }

    public Long getPackageDataId() {
        return packageDataId;
    }

    public void setPackageDataId(Long packageDataId) {
        this.packageDataId = packageDataId;
    }
}
