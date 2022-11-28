package com.vivas.campaignx.entity;

import javax.persistence.*;
import java.util.Date;
@Entity
@Table(name = "event_campaign")
public class EventCampaign {
    @Id
    @SequenceGenerator(name = "campaignGenrator", sequenceName = "SEQ_CAMPAIGN", allocationSize = 1)
    @GeneratedValue(generator = "campaignGenrator", strategy = GenerationType.AUTO)
    @Column(name = "CAMPAIGN_ID")
    private Long campaignId;
    @Column(name = "campaign_name")
    private String campaignName;
    @Column(name = "description")
    private String description;
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "updated_date")
    private Date updatedDate;
    @Column(name = "created_user")
    private String createdUser;
    @Column(name = "updated_user")
    private String updatedUser;
    @Column(name = "status")
    private Integer status;
    @Column(name = "status_old")
    private Integer statusOld;
    @Column(name = "start_date")
    private Date startDate;
    @Column(name = "end_date")
    private Date endDate;
    @Column(name = "ACCEPTANCE_RATE")
    private Integer acceptanceRate;
    @Column(name = "blacklist_path_file")
    private String blackListPathFile;
    @Column(name = "id_target_group")
    private Long idTargetGroup;
    @Column(name = "channel")
    private Integer channel;
    @Column(name = "id_campaign_policy")
    private Long idCampaignPolicy;
    @Column(name = "campaign_target")
    private String campaignTarget;
    @Column(name = "copied_number")
    private Integer copiedNumber;
    @Column(name = "TIME_RANGE_1_START")
    private String timeRange1Start;
    @Column(name = "TIME_RANGE_1_END")
    private String timeRange1End;
    @Column(name = "TIME_RANGE_2_START")
    private String timeRange2Start;
    @Column(name = "TIME_RANGE_2_END")
    private String timeRange2End;
    @Column(name = "TYPE_TARGET_GROUP")
    private Integer typeTargetGroup;
    @Column(name = "BLACKLIST_TARGET_GROUP_ID")
    private Long blackListTargetGroupId;
    @Column(name = "SUB_TARGET_GROUP_RADIO")
    private String subTargetGroupRadio;
    @Column(name = "IS_DUPLICATE_MSISDN")
    private Integer isDuplicateMsisdn;
    @Column(name = "DISABLE_MESSAGE_LIMIT")
    private String disableMessageLimit;
    @Column(name = "SENDING_TIME_LIMIT_CHANNEL")
    private String sendingTimeLimitChannel;
    @Column(name = "HAS_SUB_TARGET_GROUP")
    private Integer hasSubTargetGroup;
    @Column(name = "TYPE_INPUT_BLACKLIST")
    private Integer typeInputBlacklist;
    @Column(name = "CONTENT_ID")
    private Long contentId;
    @Column(name = "DATA_CUSTOMER_PATH_FILE")
    private String pathFileDataCustomer;
    @Column(name = "package_group_id")
    private Long packageGroupId;
    @Column(name = "DATA_JSON")
    private String dataJson;
    @Column(name = "event_id")
    private Long eventId;
    @Column(name = "event_condition")
    private String eventCondition;
    @Column(name = "event_queue_name")
    private String eventQueueName;
    @Column(name = "sql")
    private String sql;
    @Column(name = "EVENT_CONDITION_RULE")
    private Integer eventConditionRule;
    @Column(name = "ACCOUNT_SENDING_ID")
    private Long accountSendingId;
    @Column(name = "package_data_id")
    private Long packageDataId;
    @Column(name = "main_group_size")
    private Long mainGroupSize;
    @Column(name = "ratio")
    private Double ratio;
    @Column(name = "event_limit_per_day")
    private Integer eventLimitPerDay;
    @Column(name = "event_cycle")
    private Integer eventCyle;
    @Column(name = "original_name_file_data_customer")
    private String originalNameFileDataCustomer;
    @Column(name = "original_name_file_blacklist")
    private String originalNameFileBlacklist;

    public Integer getEventCyle() {
        return eventCyle;
    }

    public void setEventCyle(Integer eventCyle) {
        this.eventCyle = eventCyle;
    }

    public Integer getEventLimitPerDay() {
        return eventLimitPerDay;
    }

    public void setEventLimitPerDay(Integer eventLimitPerDay) {
        this.eventLimitPerDay = eventLimitPerDay;
    }

    public Integer getEventConditionRule() {
        return eventConditionRule;
    }

    public void setEventConditionRule(Integer eventConditionRule) {
        this.eventConditionRule = eventConditionRule;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventCondition() {
        return eventCondition;
    }

    public void setEventCondition(String eventCondition) {
        this.eventCondition = eventCondition;
    }

    public String getEventQueueName() {
        return eventQueueName;
    }

    public void setEventQueueName(String eventQueueName) {
        this.eventQueueName = eventQueueName;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getAcceptanceRate() {
        return acceptanceRate;
    }

    public void setAcceptanceRate(Integer acceptanceRate) {
        this.acceptanceRate = acceptanceRate;
    }

    public String getBlackListPathFile() {
        return blackListPathFile;
    }

    public void setBlackListPathFile(String blackListPathFile) {
        this.blackListPathFile = blackListPathFile;
    }

    public Long getIdTargetGroup() {
        return idTargetGroup;
    }

    public void setIdTargetGroup(Long idTargetGroup) {
        this.idTargetGroup = idTargetGroup;
    }

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public Long getIdCampaignPolicy() {
        return idCampaignPolicy;
    }

    public void setIdCampaignPolicy(Long idCampaignPolicy) {
        this.idCampaignPolicy = idCampaignPolicy;
    }

    public String getCampaignTarget() {
        return campaignTarget;
    }

    public void setCampaignTarget(String campaignTarget) {
        this.campaignTarget = campaignTarget;
    }

    public Integer getCopiedNumber() {
        return copiedNumber;
    }

    public void setCopiedNumber(Integer copiedNumber) {
        this.copiedNumber = copiedNumber;
    }

    public String getTimeRange1Start() {
        return timeRange1Start;
    }

    public void setTimeRange1Start(String timeRange1Start) {
        this.timeRange1Start = timeRange1Start;
    }

    public String getTimeRange1End() {
        return timeRange1End;
    }

    public void setTimeRange1End(String timeRange1End) {
        this.timeRange1End = timeRange1End;
    }

    public String getTimeRange2Start() {
        return timeRange2Start;
    }

    public void setTimeRange2Start(String timeRange2Start) {
        this.timeRange2Start = timeRange2Start;
    }

    public String getTimeRange2End() {
        return timeRange2End;
    }

    public void setTimeRange2End(String timeRange2End) {
        this.timeRange2End = timeRange2End;
    }

    public Integer getTypeTargetGroup() {
        return typeTargetGroup;
    }

    public void setTypeTargetGroup(Integer typeTargetGroup) {
        this.typeTargetGroup = typeTargetGroup;
    }

    public Long getBlackListTargetGroupId() {
        return blackListTargetGroupId;
    }

    public void setBlackListTargetGroupId(Long blackListTargetGroupId) {
        this.blackListTargetGroupId = blackListTargetGroupId;
    }

    public String getSubTargetGroupRadio() {
        return subTargetGroupRadio;
    }

    public void setSubTargetGroupRadio(String subTargetGroupRadio) {
        this.subTargetGroupRadio = subTargetGroupRadio;
    }

    public Integer getIsDuplicateMsisdn() {
        return isDuplicateMsisdn;
    }

    public void setIsDuplicateMsisdn(Integer isDuplicateMsisdn) {
        this.isDuplicateMsisdn = isDuplicateMsisdn;
    }

    public String getDisableMessageLimit() {
        return disableMessageLimit;
    }

    public void setDisableMessageLimit(String disableMessageLimit) {
        this.disableMessageLimit = disableMessageLimit;
    }

    public String getSendingTimeLimitChannel() {
        return sendingTimeLimitChannel;
    }

    public void setSendingTimeLimitChannel(String sendingTimeLimitChannel) {
        this.sendingTimeLimitChannel = sendingTimeLimitChannel;
    }

    public Integer getHasSubTargetGroup() {
        return hasSubTargetGroup;
    }

    public void setHasSubTargetGroup(Integer hasSubTargetGroup) {
        this.hasSubTargetGroup = hasSubTargetGroup;
    }

    public Integer getTypeInputBlacklist() {
        return typeInputBlacklist;
    }

    public void setTypeInputBlacklist(Integer typeInputBlacklist) {
        this.typeInputBlacklist = typeInputBlacklist;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public String getPathFileDataCustomer() {
        return pathFileDataCustomer;
    }

    public void setPathFileDataCustomer(String pathFileDataCustomer) {
        this.pathFileDataCustomer = pathFileDataCustomer;
    }

    public Long getPackageGroupId() {
        return packageGroupId;
    }

    public void setPackageGroupId(Long packageGroupId) {
        this.packageGroupId = packageGroupId;
    }

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
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

    public Long getMainGroupSize() {
        return mainGroupSize;
    }

    public void setMainGroupSize(Long mainGroupSize) {
        this.mainGroupSize = mainGroupSize;
    }

    public Double getRatio() {
        return ratio;
    }

    public void setRatio(Double ratio) {
        this.ratio = ratio;
    }

    public Integer getStatusOld() {
        return statusOld;
    }

    public void setStatusOld(Integer statusOld) {
        this.statusOld = statusOld;
    }

    public String getOriginalNameFileDataCustomer() {
        return originalNameFileDataCustomer;
    }

    public void setOriginalNameFileDataCustomer(String originalNameFileDataCustomer) {
        this.originalNameFileDataCustomer = originalNameFileDataCustomer;
    }

    public String getOriginalNameFileBlacklist() {
        return originalNameFileBlacklist;
    }

    public void setOriginalNameFileBlacklist(String originalNameFileBlacklist) {
        this.originalNameFileBlacklist = originalNameFileBlacklist;
    }
}
