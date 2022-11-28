package com.vivas.campaignx.dto;

import com.vivas.campaignx.entity.EventCampaign;
import com.vivas.campaignx.entity.FrequencyCampaign;

import java.util.Date;

public class CampaignDTO {

    private int typeCampaign;

    private Long campaignId;
    private String campaignName;
    private String description;
    private Date createdDate;
    private Date updatedDate;
    private String createdUser;
    private String updatedUser;
    private Integer status;
    private Integer statusOld;
    private Date startDate;
    private Date endDate;
    private String timeRange1Start;
    private String timeRange1End;
    private Integer acceptanceRate;
    private String blackListPathFile;
    private Long idTargetGroup;
    private Integer channel;
    private String campaignTarget;
    private Integer copiedNumber;
    private String freeCfg;
    private String dataJson;
    private String timeRange2Start;
    private String timeRange2End;
    private Integer typeTargetGroup;
    private Long blackListTargetGroupId;
    private String subTargetGroupRadio;
    private Integer isDuplicateMsisdn;
    private String disableMessageLimit;
    private String sendingTimeLimitChannel;
    private Integer hasSubTargetGroup;
    private Integer typeInputBlacklist;
    private Long contentId;
    private String pathFileDataCustomer;
    private Long packageGroupId;
    private String sql;
    private Long packageDataId;
    private Long sendingAccountId;
    private Long mainGroupSize;
    private Double ratio;
    private String originalNameFileDataCustomer;
    private String originalNameFileBlacklist;

    //thuoc tinh danh rieng cho chien dich su kien

    private Long eventId;
    private String eventCondition;
    private Integer eventConditionRule;
    private Integer eventCycle;
    private Integer eventLimitPerDay;

    public Integer getEventConditionRule() {
        return eventConditionRule;
    }

    public void setEventConditionRule(Integer eventConditionRule) {
        this.eventConditionRule = eventConditionRule;
    }

    public Long getPackageDataId() {
        return packageDataId;
    }

    public void setPackageDataId(Long packageDataId) {
        this.packageDataId = packageDataId;
    }

    public Long getSendingAccountId() {
        return sendingAccountId;
    }

    public void setSendingAccountId(Long sendingAccountId) {
        this.sendingAccountId = sendingAccountId;
    }

    public int getTypeCampaign() {
        return typeCampaign;
    }

    public void setTypeCampaign(int typeCampaign) {
        this.typeCampaign = typeCampaign;
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

    public String getFreeCfg() {
        return freeCfg;
    }

    public void setFreeCfg(String freeCfg) {
        this.freeCfg = freeCfg;
    }

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
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

    public Integer getAcceptanceRate() {
        return acceptanceRate;
    }

    public void setAcceptanceRate(Integer acceptanceRate) {
        this.acceptanceRate = acceptanceRate;
    }

    public Integer getEventCycle() {
        return eventCycle;
    }

    public void setEventCycle(Integer eventCycle) {
        this.eventCycle = eventCycle;
    }

    public Integer getEventLimitPerDay() {
        return eventLimitPerDay;
    }

    public void setEventLimitPerDay(Integer eventLimitPerDay) {
        this.eventLimitPerDay = eventLimitPerDay;
    }

    public CampaignDTO toDTO(FrequencyCampaign frequencyCampaign){
        CampaignDTO dto = new CampaignDTO();
        dto.setTypeCampaign(2);
        dto.setCampaignId(frequencyCampaign.getCampaignId());
        dto.setCampaignName(frequencyCampaign.getCampaignName());
        if(frequencyCampaign.getDescription() == null || frequencyCampaign.getDescription().length() <= 0) dto.setDescription("N/A");
        else dto.setDescription(frequencyCampaign.getDescription());
        dto.setCreatedDate(frequencyCampaign.getCreatedDate());
        dto.setUpdatedDate(frequencyCampaign.getUpdatedDate());
        dto.setCreatedUser(frequencyCampaign.getCreatedUser());
        dto.setUpdatedUser(frequencyCampaign.getUpdatedUser());
        dto.setStatus(frequencyCampaign.getStatus());
        dto.setStatusOld(frequencyCampaign.getStatusOld());
        dto.setStartDate(frequencyCampaign.getStartDate());
        dto.setEndDate(frequencyCampaign.getEndDate());
        dto.setTimeRange1Start(frequencyCampaign.getTimeRange1Start());
        dto.setTimeRange1End(frequencyCampaign.getTimeRange1End());
        dto.setBlackListPathFile(frequencyCampaign.getBlackListPathFile());
        dto.setIdTargetGroup(frequencyCampaign.getIdTargetGroup());
        dto.setChannel(frequencyCampaign.getChannel());
        if(frequencyCampaign.getCampaignTarget() == null || frequencyCampaign.getCampaignTarget().length() <= 0) dto.setCampaignTarget("N/A");
        else dto.setCampaignTarget(frequencyCampaign.getCampaignTarget());
        dto.setCopiedNumber(frequencyCampaign.getCopiedNumber());
        dto.setFreeCfg(frequencyCampaign.getFreeCfg());
        dto.setDataJson(frequencyCampaign.getDataJson());
        dto.setTimeRange2Start(frequencyCampaign.getTimeRange2Start());
        dto.setTimeRange2End(frequencyCampaign.getTimeRange2End());
        dto.setTypeTargetGroup(frequencyCampaign.getTypeTargetGroup());
        dto.setBlackListTargetGroupId(frequencyCampaign.getBlackListTargetGroupId());
        dto.setSubTargetGroupRadio(frequencyCampaign.getSubTargetGroupRadio());
        dto.setIsDuplicateMsisdn(frequencyCampaign.getIsDuplicateMsisdn());
        dto.setSendingTimeLimitChannel(frequencyCampaign.getSendingTimeLimitChannel());
        dto.setHasSubTargetGroup(frequencyCampaign.getHasSubTargetGroup());
        dto.setTypeInputBlacklist(frequencyCampaign.getTypeInputBlacklist());
        dto.setContentId(frequencyCampaign.getContentId());
        if(frequencyCampaign.getAcceptanceRate() != null) dto.setAcceptanceRate(frequencyCampaign.getAcceptanceRate().intValue());
        dto.setPathFileDataCustomer(frequencyCampaign.getPathFileDataCustomer());
        dto.setDisableMessageLimit(frequencyCampaign.getDisableMessageLimit());
        dto.setPackageGroupId(frequencyCampaign.getPackageGroupId());
        dto.setSendingAccountId(frequencyCampaign.getAccountSendingId());
        dto.setPackageDataId(frequencyCampaign.getPackageDataId());
        if(frequencyCampaign.getMainGroupSize() == null) dto.setMainGroupSize(0l);
        else dto.setMainGroupSize(frequencyCampaign.getMainGroupSize());
        if(frequencyCampaign.getRatio() == null) dto.setRatio(0.00);
        else dto.setRatio(frequencyCampaign.getRatio());
        dto.setOriginalNameFileDataCustomer(frequencyCampaign.getOriginalNameFileDataCustomer());
        dto.setOriginalNameFileBlacklist(frequencyCampaign.getOriginalNameFileBlacklist());
        return dto;
    }

    public CampaignDTO toDTO(EventCampaign eventCampaign){
        CampaignDTO dto = new CampaignDTO();
        dto.setTypeCampaign(1);
        dto.setCampaignId(eventCampaign.getCampaignId());
        dto.setCampaignName(eventCampaign.getCampaignName());
        if(eventCampaign.getDescription() == null || eventCampaign.getDescription().length() <= 0) dto.setDescription("N/A");
        else dto.setDescription(eventCampaign.getDescription());
        dto.setCreatedDate(eventCampaign.getCreatedDate());
        dto.setUpdatedDate(eventCampaign.getUpdatedDate());
        dto.setCreatedUser(eventCampaign.getCreatedUser());
        dto.setUpdatedUser(eventCampaign.getUpdatedUser());
        dto.setStatus(eventCampaign.getStatus());
        dto.setStatusOld(eventCampaign.getStatusOld());
        dto.setStartDate(eventCampaign.getStartDate());
        dto.setEndDate(eventCampaign.getEndDate());
        dto.setTimeRange1Start(eventCampaign.getTimeRange1Start());
        dto.setTimeRange1End(eventCampaign.getTimeRange1End());
        dto.setBlackListPathFile(eventCampaign.getBlackListPathFile());
        dto.setIdTargetGroup(eventCampaign.getIdTargetGroup());
        dto.setChannel(eventCampaign.getChannel());
        dto.setCampaignTarget(eventCampaign.getCampaignTarget());
        dto.setCopiedNumber(eventCampaign.getCopiedNumber());
        dto.setDataJson(eventCampaign.getDataJson());
        dto.setTimeRange2Start(eventCampaign.getTimeRange2Start());
        dto.setTimeRange2End(eventCampaign.getTimeRange2End());
        dto.setTypeTargetGroup(eventCampaign.getTypeTargetGroup());
        dto.setBlackListTargetGroupId(eventCampaign.getBlackListTargetGroupId());
        dto.setSubTargetGroupRadio(eventCampaign.getSubTargetGroupRadio());
        dto.setIsDuplicateMsisdn(eventCampaign.getIsDuplicateMsisdn());
        dto.setSendingTimeLimitChannel(eventCampaign.getSendingTimeLimitChannel());
        dto.setHasSubTargetGroup(eventCampaign.getHasSubTargetGroup());
        dto.setTypeInputBlacklist(eventCampaign.getTypeInputBlacklist());
        dto.setContentId(eventCampaign.getContentId());
        if(eventCampaign.getAcceptanceRate() != null) dto.setAcceptanceRate(eventCampaign.getAcceptanceRate().intValue());
        dto.setPathFileDataCustomer(eventCampaign.getPathFileDataCustomer());
        dto.setDisableMessageLimit(eventCampaign.getDisableMessageLimit());
        dto.setEventId(eventCampaign.getEventId());
        dto.setEventCondition(eventCampaign.getEventCondition());
        dto.setEventConditionRule(eventCampaign.getEventConditionRule());
        dto.setPackageGroupId(eventCampaign.getPackageGroupId());
        dto.setSendingAccountId(eventCampaign.getAccountSendingId());
        dto.setPackageDataId(eventCampaign.getPackageDataId());
        if(eventCampaign.getMainGroupSize() == null) dto.setMainGroupSize(0l);
        else dto.setMainGroupSize(eventCampaign.getMainGroupSize());
        if(eventCampaign.getRatio() == null) dto.setRatio(0.00);
        else dto.setRatio(eventCampaign.getRatio());
        dto.setOriginalNameFileDataCustomer(eventCampaign.getOriginalNameFileDataCustomer());
        dto.setOriginalNameFileBlacklist(eventCampaign.getOriginalNameFileBlacklist());
        dto.setEventCycle(eventCampaign.getEventCyle());
        dto.setEventLimitPerDay(eventCampaign.getEventLimitPerDay());
        return dto;
    }
}
