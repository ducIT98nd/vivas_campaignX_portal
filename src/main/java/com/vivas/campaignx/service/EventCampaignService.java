package com.vivas.campaignx.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivas.campaignx.common.AppException;
import com.vivas.campaignx.common.AppUtils;
import com.vivas.campaignx.common.HttpUtil;
import com.vivas.campaignx.common.StaticVar;
import com.vivas.campaignx.dto.SmsContentDTO;
import com.vivas.campaignx.entity.EventCampaign;
import com.vivas.campaignx.entity.SmsContent;
import com.vivas.campaignx.entity.SubTargetGroup;
import com.vivas.campaignx.entity.TargetGroup;
import com.vivas.campaignx.repository.CampaignRepository;
import com.vivas.campaignx.repository.EventCampaignRepository;
import com.vivas.campaignx.repository.EventConditionRepository;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;

@Service
public class EventCampaignService {
    protected final Logger logger = LogManager.getLogger(this.getClass().getName());

    @Autowired
    private EventCampaignRepository eventCampaignRepository;

    @Autowired
    private EventConditionRepository eventConditionRepository;

    @Autowired
    private SmsContentService smsContentService;

    @Autowired
    private TargetGroupService targetGroupService;

    @Autowired
    private CampaignTargetTagService campaignTargetTagService;

    @Autowired
    private SubTargetGroupService subTargetGroupService;

    @Autowired
    private MappingCriteriaService mappingCriteriaService;

    @Value("${path_data_target_group}")
    private String path_Data_Target_Group;

    @Value("${path_data_blacklist}")
    private String path_data_blacklist;

    @Value("${clickhouse.adapter.url}")
    private String clickHouseUrl;

    @Value("${clickhouse.adapter.cacheTargetGroup}")
    private String pathAPICacheTargetGroup;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private CampaignManagerService campaignManagerService;

    @Autowired
    private HttpUtil httpUtil;

    public int countTargetGroupByStatus(Long idTargetGroup){
        return eventCampaignRepository.countTargetGroupByStatus(idTargetGroup);
    }

    @Transactional(value = "transactionManager",rollbackFor = Exception.class)
    public long creatNewCampaign(String campaignName, Integer expectedApprovalRate, String campaignTarget, String description,
                                 String startDate, String endDate, String timeRange1Start, String timeRange1End, String timeRange2Start,
                                 String timeRange2End, Long packageGroupId, Integer typeTargetGroup,
                                 String jsonTargetGroup, Integer typeInputBlacklist, List<String> messageContent,
                                 String disablePolicySendingTimeLimit, String disablePolicyMessageLimit,
                                 MultipartFile dataCustomer, MultipartFile dataBlackList, List<String> listSubTargetGroupName,
                                 List<String> listJsonSubTargetGroup, List<Integer> listSubTargetPriority,
                                 Long blacklistTargetGroupId, String subTargetGroupRadio, String isDuplicate, Long inputTargetGroupId,
                                 String eventId, String eventCondition, String eventQueueName, String eventConditionRule, Integer eventCycle,
                                 Integer eventLimitPerDay) throws AppException, JsonProcessingException {
        long idCampaign = 0;
        try {
            logger.info("=== create new event campaign ===");

            logger.info("campaignName = " + campaignName);
            String originalNameFileDataCustomer = null;
            String originalNameFileBlacklist = null;
            long start = System.currentTimeMillis();
            String pathDataCustomer = null;
            String pathDataBlacklist = null;
            if (dataCustomer.getSize() > 0) {
                File dir1 = new File(path_Data_Target_Group);
                if (!dir1.exists()) {
                    dir1.mkdirs();
                }
                pathDataCustomer = AppUtils.saveFile(dataCustomer, path_Data_Target_Group);
                originalNameFileDataCustomer = dataCustomer.getOriginalFilename();
                logger.info("originalNameFileDataCustomer: " + originalNameFileDataCustomer);
                logger.info("path file Data Customer: " + pathDataCustomer);
            }
            if (dataBlackList.getSize() > 0) {
                File dir1 = new File(path_data_blacklist);
                if (!dir1.exists()) {
                    dir1.mkdirs();
                }
                pathDataBlacklist = AppUtils.saveFile(dataBlackList, path_data_blacklist);
                originalNameFileBlacklist = dataBlackList.getOriginalFilename();
                logger.info("originalNameFileBlacklist: " + originalNameFileBlacklist);
                logger.info("path file blacklist: " + pathDataBlacklist);
            }

            Integer hasSubTargetGroup = null;
            if (subTargetGroupRadio.equals("yes")) {
                hasSubTargetGroup = 1;
            } else if (subTargetGroupRadio.equals("no")) {
                hasSubTargetGroup = 0;
            }
            Integer isDuplicateMsisdn = null;
            if (isDuplicate != null) {
                isDuplicateMsisdn = 1;
            } else {
                isDuplicateMsisdn = 0;
            }
            TargetGroup targetGroup = null;
            String queryTargetGroup = null;
            String jsonTargetGroupOld = null;
            if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_USE_CRITERIA) || Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA)) {
                queryTargetGroup = targetGroupService.genQuery(jsonTargetGroup);
                targetGroup = targetGroupService.saveTargetGroup(jsonTargetGroup,queryTargetGroup);
                inputTargetGroupId = targetGroup.getId();
                Long idSub = null;
                targetGroupService.saveCriteriaSetup(jsonTargetGroup, targetGroup.getId(),idSub);
            }else if(Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_EXISTED)){
                Optional<TargetGroup> targetGroup1 = targetGroupService.findById(inputTargetGroupId);
                if(targetGroup1.isPresent()){
                    if(Objects.equals(targetGroup1.get().getChannel(), StaticVar.TARGET_GROUP_USE_CRITERIA) || Objects.equals(targetGroup1.get().getChannel(), StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA)){
                        jsonTargetGroupOld = targetGroup1.get().getDataJson();
                        queryTargetGroup = targetGroup1.get().getQuery();
                    }
                }
            }
            if (subTargetGroupRadio.equals("no")) {
                String jsonContent = messageContent.get(0);
                ObjectMapper objectMapper = new ObjectMapper();
                SmsContentDTO smsContentDTO = objectMapper.readValue(jsonContent, SmsContentDTO.class);
                SmsContent smsContent = smsContentService.saveSmsContent( smsContentDTO.getMessageContent(), smsContentDTO.getCountMT(),
                        smsContentDTO.getUnicode());
                EventCampaign eventCampaign = saveEventCampaign(null, campaignName, expectedApprovalRate, campaignTarget, description, startDate,
                        endDate, timeRange1Start, timeRange1End, timeRange2Start, timeRange2End, inputTargetGroupId, typeTargetGroup, jsonTargetGroup,
                        typeInputBlacklist, smsContentDTO.getChannelMarketing(), pathDataBlacklist, disablePolicySendingTimeLimit, disablePolicyMessageLimit, blacklistTargetGroupId, subTargetGroupRadio,
                        isDuplicateMsisdn, hasSubTargetGroup, smsContent.getId(), pathDataCustomer, packageGroupId, queryTargetGroup,
                        eventId, eventCondition, eventQueueName, eventConditionRule,smsContentDTO.getSendingAccount(),smsContentDTO.getProductPackage(),
                        eventCycle, eventLimitPerDay, originalNameFileDataCustomer, originalNameFileBlacklist);
                idCampaign = eventCampaign.getCampaignId();
                campaignTargetTagService.saveCampaignTargetTag(campaignTarget, "insert");

                try {
                    if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_USE_CRITERIA)) { //dung tieu chi
                        campaignManagerService.countMSISDNFromMainGroupByJsonCriteriaAndSaveToDB(jsonTargetGroup, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 1l, eventCampaign.getCampaignId());
                    } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_FILEUPLOAD)) { //dung danh sach thue bao
                        campaignManagerService.countMSISDNFromMainGroupByFileAndSaveToDB(pathDataCustomer, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 1l, eventCampaign.getCampaignId());
                    } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA)) { // dung tap giao
                        campaignManagerService.countMSISDNFromMainGroupByFileJOINJsonCriteriaAndSaveToDB(pathDataCustomer, jsonTargetGroup, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 1l, eventCampaign.getCampaignId());
                    } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_EXISTED)) { // dung nhom doi tuong co san
                        Optional<TargetGroup> existsGroupOptional = targetGroupService.findById(inputTargetGroupId);
                        if (existsGroupOptional.isPresent()) {
                            TargetGroup existsGroup = existsGroupOptional.get();
                            if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_USE_CRITERIA))
                                campaignManagerService.countMSISDNFromMainGroupByJsonCriteriaAndSaveToDB(existsGroup.getDataJson(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 1l, eventCampaign.getCampaignId());
                            else if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_FILEUPLOAD))
                                campaignManagerService.countMSISDNFromMainGroupByFileAndSaveToDB(existsGroup.getPathFileMsisdn(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 1l, eventCampaign.getCampaignId());
                            else if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA))
                                campaignManagerService.countMSISDNFromMainGroupByFileJOINJsonCriteriaAndSaveToDB(existsGroup.getPathFileMsisdn(), existsGroup.getDataJson(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 1l, eventCampaign.getCampaignId());
                        }
                    }
                }catch (Exception e){
                    logger.info("count MSISDN by campaign id = {} fail!", eventCampaign.getCampaignId());
                    logger.error("error: ", e);
                }

            } else if (subTargetGroupRadio.equals("yes")) {
                for (String s: listJsonSubTargetGroup) {
                    logger.info("listJsonSubTargetGroup: " + s);
                }
                for (String s: listSubTargetGroupName) {
                    logger.info("listSubTargetGroupName: " + s);
                }
                for (String s: messageContent) {
                    logger.info("messageContent: " + s);
                }
                for (Integer in: listSubTargetPriority) {
                    logger.info("listSubTargetPriority: " + in);
                }

                Integer channelMktCampaign = null;
                Long contentIdCampaign = null;
                campaignTargetTagService.saveCampaignTargetTag(campaignTarget, "insert");
                Long accountSendingId = null;
                Long packageDataId = null;
                EventCampaign eventCampaign = saveEventCampaign(null, campaignName, expectedApprovalRate, campaignTarget, description, startDate,
                        endDate, timeRange1Start, timeRange1End, timeRange2Start, timeRange2End, inputTargetGroupId, typeTargetGroup, jsonTargetGroup,
                        typeInputBlacklist, channelMktCampaign, pathDataBlacklist, disablePolicySendingTimeLimit, disablePolicyMessageLimit, blacklistTargetGroupId, subTargetGroupRadio,
                        isDuplicateMsisdn, hasSubTargetGroup, contentIdCampaign, pathDataCustomer, packageGroupId, queryTargetGroup,
                        eventId, eventCondition, eventQueueName, eventConditionRule,accountSendingId,packageDataId, eventCycle, eventLimitPerDay, originalNameFileDataCustomer, originalNameFileBlacklist);
                idCampaign = eventCampaign.getCampaignId();
                Optional<TargetGroup> existsGroupOptional = null;

                try {
                    if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_USE_CRITERIA)) { //dung tieu chi
                        campaignManagerService.countMSISDNFromMainGroupByJsonCriteriaAndSaveToDB(jsonTargetGroup, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 1l, eventCampaign.getCampaignId());
                    } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_FILEUPLOAD)) { //dung danh sach thue bao
                        campaignManagerService.countMSISDNFromMainGroupByFileAndSaveToDB(pathDataCustomer, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 1l, eventCampaign.getCampaignId());
                    } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA)) { // dung tap giao
                        campaignManagerService.countMSISDNFromMainGroupByFileJOINJsonCriteriaAndSaveToDB(pathDataCustomer, jsonTargetGroup, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 1l, eventCampaign.getCampaignId());
                    } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_EXISTED)) { // dung nhom doi tuong co san
                        existsGroupOptional = targetGroupService.findById(inputTargetGroupId);
                        if (existsGroupOptional.isPresent()) {
                            TargetGroup existsGroup = existsGroupOptional.get();
                            if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_USE_CRITERIA))
                                campaignManagerService.countMSISDNFromMainGroupByJsonCriteriaAndSaveToDB(existsGroup.getDataJson(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 1l, eventCampaign.getCampaignId());
                            else if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_FILEUPLOAD))
                                campaignManagerService.countMSISDNFromMainGroupByFileAndSaveToDB(existsGroup.getPathFileMsisdn(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 1l, eventCampaign.getCampaignId());
                            else if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA))
                                campaignManagerService.countMSISDNFromMainGroupByFileJOINJsonCriteriaAndSaveToDB(existsGroup.getPathFileMsisdn(), existsGroup.getDataJson(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 1l, eventCampaign.getCampaignId());
                        }
                    }
                }catch (Exception e){
                    logger.info("count MSISDN by campaign id = {} fail!", eventCampaign.getCampaignId());
                    logger.error("error: ", e);
                }

                List<String> listSqlSub = new ArrayList<>();
                if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_USE_CRITERIA) || Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA)) {
                    listSqlSub = targetGroupService.genSubQuery(jsonTargetGroup,listJsonSubTargetGroup);
                }else if(Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_EXISTED)){
                    Optional<TargetGroup> targetGroup1 = targetGroupService.findById(inputTargetGroupId);
                    if(targetGroup1.isPresent()){
                        if(Objects.equals(targetGroup1.get().getChannel(), StaticVar.TARGET_GROUP_FILEUPLOAD)){
                            for(int i=0; i<listJsonSubTargetGroup.size();i++){
                                listSqlSub.add(targetGroupService.genQuery(listJsonSubTargetGroup.get(i)));
                            }
                        }else {
                            listSqlSub = targetGroupService.genSubQuery(jsonTargetGroupOld,listJsonSubTargetGroup);
                        }
                    }
                }else if(Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_FILEUPLOAD)){
                    for(int i=0; i<listJsonSubTargetGroup.size();i++){
                        listSqlSub.add(targetGroupService.genQuery(listJsonSubTargetGroup.get(i)));
                    }
                }
                Long idTarget = null;
                for (int i = 0; i < listJsonSubTargetGroup.size(); i++) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    SmsContentDTO smsContentDTO = objectMapper.readValue(messageContent.get(i + 1), SmsContentDTO.class);
                    SmsContent smsContent = smsContentService.saveSmsContent(smsContentDTO.getMessageContent(), smsContentDTO.getCountMT(),
                            smsContentDTO.getUnicode());
                    SubTargetGroup subTargetGroup = subTargetGroupService.saveSubTargetGroup(eventCampaign.getCampaignId(), listSubTargetGroupName.get(i), smsContentDTO.getChannelMarketing(),
                            listJsonSubTargetGroup.get(i), listSubTargetPriority.get(i), smsContent.getId(),listSqlSub.get(i),smsContentDTO.getSendingAccount(), smsContentDTO.getProductPackage());
                    targetGroupService.saveCriteriaSetup(listJsonSubTargetGroup.get(i),idTarget, subTargetGroup.getId());

                    try {
                        if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_USE_CRITERIA)) { //dung tieu chi
                            campaignManagerService.countMSISDNFromSubGroupByJsonCriteriaAndSaveToDB(jsonTargetGroup, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, listJsonSubTargetGroup.get(i), subTargetGroup.getId());
                        } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_FILEUPLOAD)) { //dung danh sach thue bao
                            campaignManagerService.countMSISDNFromSubGroupByFileAndSaveToDB(pathDataCustomer, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, listJsonSubTargetGroup.get(i), subTargetGroup.getId());
                        } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA)) { // dung tap giao
                            campaignManagerService.countMSISDNFromSubGroupByFileJOINJsonCriteriaAndSaveToDB(pathDataCustomer, jsonTargetGroup, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, listJsonSubTargetGroup.get(i), subTargetGroup.getId());
                        } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_EXISTED)) { // dung nhom doi tuong co san
                            if (existsGroupOptional.isPresent()) {
                                TargetGroup existsGroup = existsGroupOptional.get();
                                if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_USE_CRITERIA))
                                    campaignManagerService.countMSISDNFromSubGroupByJsonCriteriaAndSaveToDB(existsGroup.getDataJson(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, listJsonSubTargetGroup.get(i), subTargetGroup.getId());
                                else if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_FILEUPLOAD))
                                    campaignManagerService.countMSISDNFromSubGroupByFileAndSaveToDB(existsGroup.getPathFileMsisdn(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, listJsonSubTargetGroup.get(i), subTargetGroup.getId());
                                else if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA))
                                    campaignManagerService.countMSISDNFromSubGroupByFileJOINJsonCriteriaAndSaveToDB(existsGroup.getPathFileMsisdn(), existsGroup.getDataJson(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, listJsonSubTargetGroup.get(i), subTargetGroup.getId());
                            }
                        }
                    }catch (Exception e){
                        logger.info("count MSISDN sub group by subTargetGroup.getId() = {} fail!", subTargetGroup.getId());
                        logger.error("error: ", e);
                    }
                }
            }

            long end = System.currentTimeMillis();
            logger.info("-------- time save to DB(ms): " + (end - start) + " with campaign ID: " + idCampaign);
        } catch (Exception ex) {
            logger.info("error when save event campaign to db:", ex);
            throw new AppException("Có lỗi xảy ra khi tạo chiến dịch, vui lòng liên hệ ban quản trị!");
        }
        return idCampaign;
    }

    @Transactional(value = "transactionManager",rollbackFor = Exception.class)
    public long copyNewCampaign(Long id, String campaignName, Integer expectedApprovalRate, String campaignTarget, String description,
                                 String startDate, String endDate, String timeRange1Start, String timeRange1End, String timeRange2Start,
                                 String timeRange2End, Long packageGroupId, Integer typeTargetGroup,
                                 String jsonTargetGroup, Integer typeInputBlacklist, List<String> messageContent,
                                 String disablePolicySendingTimeLimit, String disablePolicyMessageLimit,
                                 MultipartFile dataCustomer, MultipartFile dataBlackList, List<String> listSubTargetGroupName,
                                 List<String> listJsonSubTargetGroup, List<Integer> listSubTargetPriority,
                                 Long blacklistTargetGroupId, String subTargetGroupRadio, String isDuplicate, Long inputTargetGroupId,
                                 String eventId, String eventCondition, String eventQueueName, String eventConditionRule, Integer eventCycle,
                                 Integer eventLimitPerDay) throws AppException, JsonProcessingException {
        long idCampaign = 0;
        try {
            logger.info("=== copy event campaign ===");
            logger.info("campaignName = " + campaignName);
            String originalNameFileDataCustomer = null;
            String originalNameFileBlacklist = null;
            long start = System.currentTimeMillis();
            Optional<EventCampaign> eventCampaignOptional = eventCampaignRepository.findById(id);
            EventCampaign eventCampaignOld = new EventCampaign();
            if (eventCampaignOptional.isPresent()) {
                eventCampaignOld = eventCampaignOptional.get();
            }

            String pathDataCustomer = null;
            String pathDataBlacklist = null;
            if(Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_FILEUPLOAD) || Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA)) {
                if (dataCustomer != null && dataCustomer.getSize() > 0) {
                    File dir1 = new File(path_Data_Target_Group);
                    if (!dir1.exists()) {
                        dir1.mkdirs();
                    }
                    pathDataCustomer = AppUtils.saveFile(dataCustomer, path_Data_Target_Group);
                    originalNameFileDataCustomer = dataCustomer.getOriginalFilename();
                    logger.info("originalNameFileDataCustomer: " + originalNameFileDataCustomer);
                    logger.info("path file Data Customer: " + pathDataCustomer);
                } else {
                    originalNameFileDataCustomer = eventCampaignOld.getOriginalNameFileDataCustomer();
                    pathDataCustomer = eventCampaignOld.getPathFileDataCustomer();
                }
            }
            if(typeInputBlacklist == 2) {
                if (dataBlackList != null && dataBlackList.getSize() > 0) {
                    File dir1 = new File(path_data_blacklist);
                    if (!dir1.exists()) {
                        dir1.mkdirs();
                    }
                    pathDataBlacklist = AppUtils.saveFile(dataBlackList, path_data_blacklist);
                    originalNameFileBlacklist = dataBlackList.getOriginalFilename();
                    logger.info("originalNameFileBlacklist: " + originalNameFileBlacklist);
                    logger.info("path file blacklist: " + pathDataBlacklist);
                } else {
                    originalNameFileBlacklist = eventCampaignOld.getOriginalNameFileBlacklist();
                    pathDataBlacklist = eventCampaignOld.getBlackListPathFile();
                }
            }

            Integer hasSubTargetGroup = null;
            if (subTargetGroupRadio.equals("yes")) {
                hasSubTargetGroup = 1;
            } else if (subTargetGroupRadio.equals("no")) {
                hasSubTargetGroup = 0;
            }
            Integer isDuplicateMsisdn = null;
            if (isDuplicate != null) {
                isDuplicateMsisdn = 1;
            } else {
                isDuplicateMsisdn = 0;
            }
            TargetGroup targetGroup = null;
            String queryTargetGroup = null;
            String jsonTargetGroupOld = null;
            if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_USE_CRITERIA) || Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA)) {
                queryTargetGroup = targetGroupService.genQuery(jsonTargetGroup);
                targetGroup = targetGroupService.saveTargetGroup(jsonTargetGroup,queryTargetGroup);
                inputTargetGroupId = targetGroup.getId();
                Long idSub = null;
                targetGroupService.saveCriteriaSetup(jsonTargetGroup, targetGroup.getId(),idSub);
            }else if(Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_EXISTED)){
                Optional<TargetGroup> targetGroup1 = targetGroupService.findById(inputTargetGroupId);
                if(targetGroup1.isPresent()){
                    if(Objects.equals(targetGroup1.get().getChannel(), StaticVar.TARGET_GROUP_USE_CRITERIA) || Objects.equals(targetGroup1.get().getChannel(), StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA)){
                        jsonTargetGroupOld = targetGroup1.get().getDataJson();
                        queryTargetGroup = targetGroup1.get().getQuery();
                    }
                }
            }
            if (subTargetGroupRadio.equals("no")) {
                String jsonContent = messageContent.get(0);
                ObjectMapper objectMapper = new ObjectMapper();
                SmsContentDTO smsContentDTO = objectMapper.readValue(jsonContent, SmsContentDTO.class);
                SmsContent smsContent = smsContentService.saveSmsContent( smsContentDTO.getMessageContent(), smsContentDTO.getCountMT(),
                        smsContentDTO.getUnicode());
                EventCampaign eventCampaign = saveEventCampaign(null, campaignName, expectedApprovalRate, campaignTarget, description, startDate,
                        endDate, timeRange1Start, timeRange1End, timeRange2Start, timeRange2End, inputTargetGroupId, typeTargetGroup, jsonTargetGroup,
                        typeInputBlacklist, smsContentDTO.getChannelMarketing(), pathDataBlacklist, disablePolicySendingTimeLimit, disablePolicyMessageLimit, blacklistTargetGroupId, subTargetGroupRadio,
                        isDuplicateMsisdn, hasSubTargetGroup, smsContent.getId(), pathDataCustomer, packageGroupId, queryTargetGroup,
                        eventId, eventCondition, eventQueueName, eventConditionRule,smsContentDTO.getSendingAccount(),smsContentDTO.getProductPackage(),
                        eventCycle, eventLimitPerDay, originalNameFileDataCustomer, originalNameFileBlacklist);
                idCampaign = eventCampaign.getCampaignId();
                campaignTargetTagService.saveCampaignTargetTag(campaignTarget, "insert");

                Optional<TargetGroup> existsGroupOptional = null;
                try {
                    if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_USE_CRITERIA)) { //dung tieu chi
                        campaignManagerService.countMSISDNFromMainGroupByJsonCriteriaAndSaveToDB(jsonTargetGroup, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 1l, eventCampaign.getCampaignId());
                    } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_FILEUPLOAD)) { //dung danh sach thue bao
                        campaignManagerService.countMSISDNFromMainGroupByFileAndSaveToDB(pathDataCustomer, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 1l, eventCampaign.getCampaignId());
                    } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA)) { // dung tap giao
                        campaignManagerService.countMSISDNFromMainGroupByFileJOINJsonCriteriaAndSaveToDB(pathDataCustomer, jsonTargetGroup, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 1l, eventCampaign.getCampaignId());
                    } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_EXISTED)) { // dung nhom doi tuong co san
                        existsGroupOptional = targetGroupService.findById(inputTargetGroupId);
                        if (existsGroupOptional.isPresent()) {
                            TargetGroup existsGroup = existsGroupOptional.get();
                            if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_USE_CRITERIA))
                                campaignManagerService.countMSISDNFromMainGroupByJsonCriteriaAndSaveToDB(existsGroup.getDataJson(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 1l, eventCampaign.getCampaignId());
                            else if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_FILEUPLOAD))
                                campaignManagerService.countMSISDNFromMainGroupByFileAndSaveToDB(existsGroup.getPathFileMsisdn(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 1l, eventCampaign.getCampaignId());
                            else if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA))
                                campaignManagerService.countMSISDNFromMainGroupByFileJOINJsonCriteriaAndSaveToDB(existsGroup.getPathFileMsisdn(), existsGroup.getDataJson(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 1l, eventCampaign.getCampaignId());
                        }
                    }
                }catch (Exception e){
                    logger.info("count MSISDN by campaign id = {} fail!", eventCampaign.getCampaignId());
                    logger.error("error: ", e);
                }

            } else if (subTargetGroupRadio.equals("yes")) {
                for (String s: listJsonSubTargetGroup) {
                    logger.info("listJsonSubTargetGroup: " + s);
                }
                for (String s: listSubTargetGroupName) {
                    logger.info("listSubTargetGroupName: " + s);
                }
                for (String s: messageContent) {
                    logger.info("messageContent: " + s);
                }
                for (Integer in: listSubTargetPriority) {
                    logger.info("listSubTargetPriority: " + in);
                }
                Integer channelMktCampaign = null;
                Long contentIdCampaign = null;
                campaignTargetTagService.saveCampaignTargetTag(campaignTarget, "insert");
                Long accountSendingId = null;
                Long packageDataId = null;
                EventCampaign eventCampaign = saveEventCampaign(null, campaignName, expectedApprovalRate, campaignTarget, description, startDate,
                        endDate, timeRange1Start, timeRange1End, timeRange2Start, timeRange2End, inputTargetGroupId, typeTargetGroup, jsonTargetGroup,
                        typeInputBlacklist, channelMktCampaign, pathDataBlacklist, disablePolicySendingTimeLimit, disablePolicyMessageLimit, blacklistTargetGroupId, subTargetGroupRadio,
                        isDuplicateMsisdn, hasSubTargetGroup, contentIdCampaign, pathDataCustomer, packageGroupId, queryTargetGroup,
                        eventId, eventCondition, eventQueueName, eventConditionRule,accountSendingId,packageDataId, eventCycle, eventLimitPerDay, originalNameFileDataCustomer, originalNameFileBlacklist);
                idCampaign = eventCampaign.getCampaignId();
                Optional<TargetGroup> existsGroupOptional = null;
                try {
                    if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_USE_CRITERIA)) { //dung tieu chi
                        campaignManagerService.countMSISDNFromMainGroupByJsonCriteriaAndSaveToDB(jsonTargetGroup, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 1l, eventCampaign.getCampaignId());
                    } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_FILEUPLOAD)) { //dung danh sach thue bao
                        campaignManagerService.countMSISDNFromMainGroupByFileAndSaveToDB(pathDataCustomer, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 1l, eventCampaign.getCampaignId());
                    } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA)) { // dung tap giao
                        campaignManagerService.countMSISDNFromMainGroupByFileJOINJsonCriteriaAndSaveToDB(pathDataCustomer, jsonTargetGroup, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 1l, eventCampaign.getCampaignId());
                    } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_EXISTED)) { // dung nhom doi tuong co san
                        existsGroupOptional = targetGroupService.findById(inputTargetGroupId);
                        if (existsGroupOptional.isPresent()) {
                            TargetGroup existsGroup = existsGroupOptional.get();
                            if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_USE_CRITERIA))
                                campaignManagerService.countMSISDNFromMainGroupByJsonCriteriaAndSaveToDB(existsGroup.getDataJson(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 1l, eventCampaign.getCampaignId());
                            else if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_FILEUPLOAD))
                                campaignManagerService.countMSISDNFromMainGroupByFileAndSaveToDB(existsGroup.getPathFileMsisdn(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 1l, eventCampaign.getCampaignId());
                            else if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA))
                                campaignManagerService.countMSISDNFromMainGroupByFileJOINJsonCriteriaAndSaveToDB(existsGroup.getPathFileMsisdn(), existsGroup.getDataJson(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 1l, eventCampaign.getCampaignId());
                        }
                    }
                }catch (Exception e){
                    logger.info("count MSISDN by campaign id = {} fail!", eventCampaign.getCampaignId());
                    logger.error("error: ", e);
                }

                List<String> listSqlSub = new ArrayList<>();
                if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_USE_CRITERIA) || Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA)) {
                    listSqlSub = targetGroupService.genSubQuery(jsonTargetGroup,listJsonSubTargetGroup);
                }else if(Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_EXISTED)){
                    Optional<TargetGroup> targetGroup1 = targetGroupService.findById(inputTargetGroupId);
                    if(targetGroup1.isPresent()){
                        if(Objects.equals(targetGroup1.get().getChannel(), StaticVar.TARGET_GROUP_FILEUPLOAD)){
                            for(int i=0; i<listJsonSubTargetGroup.size();i++){
                                listSqlSub.add(targetGroupService.genQuery(listJsonSubTargetGroup.get(i)));
                            }
                        }else {
                            listSqlSub = targetGroupService.genSubQuery(jsonTargetGroupOld,listJsonSubTargetGroup);
                        }
                    }
                }else if(Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_FILEUPLOAD)){
                    for(int i=0; i<listJsonSubTargetGroup.size();i++){
                        listSqlSub.add(targetGroupService.genQuery(listJsonSubTargetGroup.get(i)));
                    }
                }
                Long idTarget = null;
                for (int i = 0; i < listJsonSubTargetGroup.size(); i++) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    SmsContentDTO smsContentDTO = objectMapper.readValue(messageContent.get(i + 1), SmsContentDTO.class);
                    SmsContent smsContent = smsContentService.saveSmsContent(smsContentDTO.getMessageContent(), smsContentDTO.getCountMT(),
                            smsContentDTO.getUnicode());
                    SubTargetGroup subTargetGroup = subTargetGroupService.saveSubTargetGroup(eventCampaign.getCampaignId(), listSubTargetGroupName.get(i), smsContentDTO.getChannelMarketing(),
                            listJsonSubTargetGroup.get(i), listSubTargetPriority.get(i), smsContent.getId(),listSqlSub.get(i),smsContentDTO.getSendingAccount(), smsContentDTO.getProductPackage());
                    targetGroupService.saveCriteriaSetup(listJsonSubTargetGroup.get(i),idTarget, subTargetGroup.getId());
                    try {
                        if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_USE_CRITERIA)) { //dung tieu chi
                            campaignManagerService.countMSISDNFromSubGroupByJsonCriteriaAndSaveToDB(jsonTargetGroup, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, listJsonSubTargetGroup.get(i), subTargetGroup.getId());
                        } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_FILEUPLOAD)) { //dung danh sach thue bao
                            campaignManagerService.countMSISDNFromSubGroupByFileAndSaveToDB(pathDataCustomer, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, listJsonSubTargetGroup.get(i), subTargetGroup.getId());
                        } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA)) { // dung tap giao
                            campaignManagerService.countMSISDNFromSubGroupByFileJOINJsonCriteriaAndSaveToDB(pathDataCustomer, jsonTargetGroup, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, listJsonSubTargetGroup.get(i), subTargetGroup.getId());
                        } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_EXISTED)) { // dung nhom doi tuong co san
                            if (existsGroupOptional.isPresent()) {
                                TargetGroup existsGroup = existsGroupOptional.get();
                                if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_USE_CRITERIA))
                                    campaignManagerService.countMSISDNFromSubGroupByJsonCriteriaAndSaveToDB(existsGroup.getDataJson(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, listJsonSubTargetGroup.get(i), subTargetGroup.getId());
                                else if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_FILEUPLOAD))
                                    campaignManagerService.countMSISDNFromSubGroupByFileAndSaveToDB(existsGroup.getPathFileMsisdn(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, listJsonSubTargetGroup.get(i), subTargetGroup.getId());
                                else if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA))
                                    campaignManagerService.countMSISDNFromSubGroupByFileJOINJsonCriteriaAndSaveToDB(existsGroup.getPathFileMsisdn(), existsGroup.getDataJson(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, listJsonSubTargetGroup.get(i), subTargetGroup.getId());
                            }
                        }
                    }catch (Exception e){
                        logger.info("count MSISDN sub target id = {} fail!", subTargetGroup.getId());
                        logger.error("error: ", e);
                    }
                }
            }

            long end = System.currentTimeMillis();
            logger.info("-------- time save to DB(ms): " + (end - start) + " with campaign ID: " + id);
        } catch (Exception ex) {
            logger.info("error when save event campaign to db:", ex);
            throw new AppException("Có lỗi xảy ra khi tạo chiến dịch, vui lòng liên hệ ban quản trị!");
        }
        return idCampaign;
    }

    @Transactional(value = "transactionManager",rollbackFor = Exception.class)
    public long updateCampaign(Long idEventCampaign, String campaignName, Integer expectedApprovalRate, String campaignTarget, String description,
                               String startDate, String endDate, String timeRange1Start, String timeRange1End, String timeRange2Start,
                               String timeRange2End, Long packageGroupId, Integer typeTargetGroup,
                               String jsonTargetGroup, Integer typeInputBlacklist, List<String> messageContent,
                               String disablePolicySendingTimeLimit, String disablePolicyMessageLimit,
                               MultipartFile dataCustomer, MultipartFile dataBlackList, List<String> listSubTargetGroupName,
                               List<String> listJsonSubTargetGroup, List<Integer> listSubTargetPriority,
                               Long blacklistTargetGroupId, String subTargetGroupRadio, String isDuplicate, Long inputTargetGroupId,
                               String eventId, String eventCondition, String eventQueueName, String eventConditionRule, Integer eventCycle,
                               Integer eventLimiPerDay, Integer hasSubOld) throws AppException, JsonProcessingException {
        try {
            logger.info("=== update event campaign ===");
            logger.info("campaignName = " + campaignName);
            String originalNameFileDataCustomer = null;
            String originalNameFileBlacklist = null;
            long start = System.currentTimeMillis();
            Optional<EventCampaign> eventCampaignOptional = eventCampaignRepository.findById(idEventCampaign);
            EventCampaign eventCampaignOld = new EventCampaign();
            if (eventCampaignOptional.isPresent()) {
                eventCampaignOld = eventCampaignOptional.get();
            }
            String pathDataCustomer = null;
            String pathDataBlacklist = null;
            if(Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_FILEUPLOAD) || Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA)) {
                if (dataCustomer != null && dataCustomer.getSize() > 0) {
                    File dir1 = new File(path_Data_Target_Group);
                    if (!dir1.exists()) {
                        dir1.mkdirs();
                    }
                    pathDataCustomer = AppUtils.saveFile(dataCustomer, path_Data_Target_Group);
                    originalNameFileDataCustomer = dataCustomer.getOriginalFilename();
                    logger.info("originalNameFileDataCustomer: " + originalNameFileDataCustomer);
                    logger.info("path file Data Customer: " + pathDataCustomer);
                } else {
                    originalNameFileDataCustomer = eventCampaignOld.getOriginalNameFileDataCustomer();
                    pathDataCustomer = eventCampaignOld.getPathFileDataCustomer();
                }
            }
            if(typeInputBlacklist == 2) {
                if (dataBlackList != null && dataBlackList.getSize() > 0) {
                    File dir1 = new File(path_data_blacklist);
                    if (!dir1.exists()) {
                        dir1.mkdirs();
                    }
                    pathDataBlacklist = AppUtils.saveFile(dataBlackList, path_data_blacklist);
                    originalNameFileBlacklist = dataBlackList.getOriginalFilename();
                    logger.info("originalNameFileBlacklist: " + originalNameFileBlacklist);
                    logger.info("path file blacklist: " + pathDataBlacklist);
                } else {
                    originalNameFileBlacklist = eventCampaignOld.getOriginalNameFileBlacklist();
                    pathDataBlacklist = eventCampaignOld.getBlackListPathFile();
                }
            }

            Integer hasSubTargetGroup = null;
            if (subTargetGroupRadio.equals("yes")) {
                hasSubTargetGroup = 1;
            } else if (subTargetGroupRadio.equals("no")) {
                hasSubTargetGroup = 0;
            }
            Integer isDuplicateMsisdn = null;
            if (isDuplicate != null) {
                isDuplicateMsisdn = 1;
            } else {
                isDuplicateMsisdn = 0;
            }
            TargetGroup targetGroup = null;
            String queryTargetGroup = null;
            String jsonTargetGroupOld = null;
            if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_USE_CRITERIA) || Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA)) {
                queryTargetGroup = targetGroupService.genQuery(jsonTargetGroup);
                targetGroup = targetGroupService.saveTargetGroup(jsonTargetGroup,queryTargetGroup);
                inputTargetGroupId = targetGroup.getId();
                Long idSub = null;
                targetGroupService.saveCriteriaSetup(jsonTargetGroup, targetGroup.getId(),idSub);
            }else if(Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_EXISTED)){
                Optional<TargetGroup> targetGroup1 = targetGroupService.findById(inputTargetGroupId);
                if(targetGroup1.isPresent()){
                    if(Objects.equals(targetGroup1.get().getChannel(), StaticVar.TARGET_GROUP_USE_CRITERIA) || Objects.equals(targetGroup1.get().getChannel(), StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA)){
                        jsonTargetGroupOld = targetGroup1.get().getDataJson();
                        queryTargetGroup = targetGroup1.get().getQuery();
                    }
                }
            }
            if (subTargetGroupRadio.equals("no")) {
                Optional<EventCampaign> optionalEventCampaign = eventCampaignRepository.findById(idEventCampaign);
                optionalEventCampaign.ifPresent(x->{
                    if(0 == hasSubOld) {
                        smsContentService.deleteById(x.getContentId());
                    }else {
                        List<SubTargetGroup> subTargetGroupList = subTargetGroupService.findByCampaignId(idEventCampaign);
                        for(int i=0; i < subTargetGroupList.size();i++){
                            mappingCriteriaService.deleteAllByIdSubTargetGroup(subTargetGroupList.get(i).getId());
                            smsContentService.deleteById(subTargetGroupList.get(i).getContentId());
                        }
                        subTargetGroupService.deleteAllByCampaignId(idEventCampaign);
                    }
                });
                String jsonContent = messageContent.get(0);
                ObjectMapper objectMapper = new ObjectMapper();
                SmsContentDTO smsContentDTO = objectMapper.readValue(jsonContent, SmsContentDTO.class);
                SmsContent smsContent = smsContentService.saveSmsContent(smsContentDTO.getMessageContent(), smsContentDTO.getCountMT(),
                        smsContentDTO.getUnicode());
                EventCampaign eventCampaign = saveEventCampaign(idEventCampaign, campaignName, expectedApprovalRate, campaignTarget, description, startDate,
                        endDate, timeRange1Start, timeRange1End, timeRange2Start, timeRange2End, inputTargetGroupId, typeTargetGroup, jsonTargetGroup, typeInputBlacklist,
                        smsContentDTO.getChannelMarketing(), pathDataBlacklist, disablePolicySendingTimeLimit, disablePolicyMessageLimit, blacklistTargetGroupId,
                        subTargetGroupRadio, isDuplicateMsisdn, hasSubTargetGroup, smsContent.getId(), pathDataCustomer, packageGroupId, queryTargetGroup, eventId, eventCondition,
                        eventQueueName, eventConditionRule,smsContentDTO.getSendingAccount(), smsContentDTO.getProductPackage(), eventCycle, eventLimiPerDay, originalNameFileDataCustomer, originalNameFileBlacklist);
                campaignTargetTagService.saveCampaignTargetTag(campaignTarget, "update");
                try {
                    if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_USE_CRITERIA)) { //dung tieu chi
                        campaignManagerService.countMSISDNFromMainGroupByJsonCriteriaAndSaveToDB(jsonTargetGroup, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 1l, eventCampaign.getCampaignId());
                    } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_FILEUPLOAD)) { //dung danh sach thue bao
                        campaignManagerService.countMSISDNFromMainGroupByFileAndSaveToDB(pathDataCustomer, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 1l, eventCampaign.getCampaignId());
                    } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA)) { // dung tap giao
                        campaignManagerService.countMSISDNFromMainGroupByFileJOINJsonCriteriaAndSaveToDB(pathDataCustomer, jsonTargetGroup, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 1l, eventCampaign.getCampaignId());
                    } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_EXISTED)) { // dung nhom doi tuong co san
                        Optional<TargetGroup> existsGroupOptional = targetGroupService.findById(inputTargetGroupId);
                        if (existsGroupOptional.isPresent()) {
                            TargetGroup existsGroup = existsGroupOptional.get();
                            if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_USE_CRITERIA))
                                campaignManagerService.countMSISDNFromMainGroupByJsonCriteriaAndSaveToDB(existsGroup.getDataJson(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 1l, eventCampaign.getCampaignId());
                            else if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_FILEUPLOAD))
                                campaignManagerService.countMSISDNFromMainGroupByFileAndSaveToDB(existsGroup.getPathFileMsisdn(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 1l, eventCampaign.getCampaignId());
                            else if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA))
                                campaignManagerService.countMSISDNFromMainGroupByFileJOINJsonCriteriaAndSaveToDB(existsGroup.getPathFileMsisdn(), existsGroup.getDataJson(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 1l, eventCampaign.getCampaignId());
                        }
                    }
                }catch (Exception e){
                    logger.info("count MSISDN by campaign id = {} fail!", eventCampaign.getCampaignId());
                    logger.error("error: ", e);
                }

            } else if (subTargetGroupRadio.equals("yes")) {
                if(1 == hasSubOld) {
                    List<SubTargetGroup> subTargetGroupList = subTargetGroupService.findByCampaignId(idEventCampaign);
                    for(int i=0; i < subTargetGroupList.size();i++){
                        mappingCriteriaService.deleteAllByIdSubTargetGroup(subTargetGroupList.get(i).getId());
                        smsContentService.deleteById(subTargetGroupList.get(i).getContentId());
                    }
                    subTargetGroupService.deleteAllByCampaignId(idEventCampaign);
                }else {
                    Optional<EventCampaign> optionalEventCampaign = eventCampaignRepository.findById(idEventCampaign);
                    optionalEventCampaign.ifPresent(x->{
                        smsContentService.deleteById(x.getContentId());
                    });
                }
                Integer channelMktCampaign = null;
                Long contentIdCampaign = null;
                campaignTargetTagService.saveCampaignTargetTag(campaignTarget, "update");
                Long accountSendingId = null;
                Long packageDataId = null;
                EventCampaign eventCampaign = saveEventCampaign(idEventCampaign, campaignName, expectedApprovalRate, campaignTarget, description, startDate,
                        endDate, timeRange1Start, timeRange1End, timeRange2Start, timeRange2End, inputTargetGroupId, typeTargetGroup, jsonTargetGroup,
                        typeInputBlacklist, channelMktCampaign, pathDataBlacklist, disablePolicySendingTimeLimit, disablePolicyMessageLimit, blacklistTargetGroupId, subTargetGroupRadio,
                        isDuplicateMsisdn, hasSubTargetGroup, contentIdCampaign, pathDataCustomer, packageGroupId, queryTargetGroup,
                        eventId, eventCondition, eventQueueName, eventConditionRule, accountSendingId, packageDataId, eventCycle, eventLimiPerDay, originalNameFileDataCustomer, originalNameFileBlacklist);
                Optional<TargetGroup> existsGroupOptional = null;
                try {
                    if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_USE_CRITERIA)) { //dung tieu chi
                        campaignManagerService.countMSISDNFromMainGroupByJsonCriteriaAndSaveToDB(jsonTargetGroup, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 1l, eventCampaign.getCampaignId());
                    } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_FILEUPLOAD)) { //dung danh sach thue bao
                        campaignManagerService.countMSISDNFromMainGroupByFileAndSaveToDB(pathDataCustomer, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 1l, eventCampaign.getCampaignId());
                    } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA)) { // dung tap giao
                        campaignManagerService.countMSISDNFromMainGroupByFileJOINJsonCriteriaAndSaveToDB(pathDataCustomer, jsonTargetGroup, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 1l, eventCampaign.getCampaignId());
                    } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_EXISTED)) { // dung nhom doi tuong co san
                        existsGroupOptional = targetGroupService.findById(inputTargetGroupId);
                        if (existsGroupOptional.isPresent()) {
                            TargetGroup existsGroup = existsGroupOptional.get();
                            if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_USE_CRITERIA))
                                campaignManagerService.countMSISDNFromMainGroupByJsonCriteriaAndSaveToDB(existsGroup.getDataJson(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 1l, eventCampaign.getCampaignId());
                            else if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_FILEUPLOAD))
                                campaignManagerService.countMSISDNFromMainGroupByFileAndSaveToDB(existsGroup.getPathFileMsisdn(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 1l, eventCampaign.getCampaignId());
                            else if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA))
                                campaignManagerService.countMSISDNFromMainGroupByFileJOINJsonCriteriaAndSaveToDB(existsGroup.getPathFileMsisdn(), existsGroup.getDataJson(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 1l, eventCampaign.getCampaignId());
                        }
                    }
                }catch (Exception e){
                    logger.info("count MSISDN by campaign id = {} fail!", eventCampaign.getCampaignId());
                    logger.error("error: ", e);
                }

                List<String> listSqlSub = new ArrayList<>();
                if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_USE_CRITERIA) || Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA)) {
                    listSqlSub = targetGroupService.genSubQuery(jsonTargetGroup,listJsonSubTargetGroup);
                }else if(Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_EXISTED)){
                    Optional<TargetGroup> targetGroup1 = targetGroupService.findById(inputTargetGroupId);
                    if(targetGroup1.isPresent()){
                        if(Objects.equals(targetGroup1.get().getChannel(), StaticVar.TARGET_GROUP_FILEUPLOAD)){
                            for(int i=0; i<listJsonSubTargetGroup.size();i++){
                                listSqlSub.add(targetGroupService.genQuery(listJsonSubTargetGroup.get(i)));
                            }
                        }else {
                            listSqlSub = targetGroupService.genSubQuery(jsonTargetGroupOld,listJsonSubTargetGroup);
                        }
                    }
                }else if(typeTargetGroup == StaticVar.TARGET_GROUP_FILEUPLOAD){
                    for(int i=0; i<listJsonSubTargetGroup.size();i++){
                        listSqlSub.add(targetGroupService.genQuery(listJsonSubTargetGroup.get(i)));
                    }
                }
                Long idTarget = null;
                for (int i = 0; i < listJsonSubTargetGroup.size(); i++) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    SmsContentDTO smsContentDTO = objectMapper.readValue(messageContent.get(i + 1), SmsContentDTO.class);
                    SmsContent smsContent = smsContentService.saveSmsContent(smsContentDTO.getMessageContent(), smsContentDTO.getCountMT(),
                            smsContentDTO.getUnicode());
                    SubTargetGroup subTargetGroup = subTargetGroupService.saveSubTargetGroup(eventCampaign.getCampaignId(), listSubTargetGroupName.get(i), smsContentDTO.getChannelMarketing(),
                            listJsonSubTargetGroup.get(i), listSubTargetPriority.get(i), smsContent.getId(),listSqlSub.get(i),smsContentDTO.getSendingAccount(), smsContentDTO.getProductPackage());
                    targetGroupService.saveCriteriaSetup(listJsonSubTargetGroup.get(i),idTarget, subTargetGroup.getId());
                    try {
                        if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_USE_CRITERIA)) { //dung tieu chi
                            campaignManagerService.countMSISDNFromSubGroupByJsonCriteriaAndSaveToDB(jsonTargetGroup, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, listJsonSubTargetGroup.get(i), subTargetGroup.getId());
                        } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_FILEUPLOAD)) { //dung danh sach thue bao
                            campaignManagerService.countMSISDNFromSubGroupByFileAndSaveToDB(pathDataCustomer, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, listJsonSubTargetGroup.get(i), subTargetGroup.getId());
                        } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA)) { // dung tap giao
                            campaignManagerService.countMSISDNFromSubGroupByFileJOINJsonCriteriaAndSaveToDB(pathDataCustomer, jsonTargetGroup, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, listJsonSubTargetGroup.get(i), subTargetGroup.getId());
                        } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_EXISTED)) { // dung nhom doi tuong co san
                            if (existsGroupOptional.isPresent()) {
                                TargetGroup existsGroup = existsGroupOptional.get();
                                if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_USE_CRITERIA))
                                    campaignManagerService.countMSISDNFromSubGroupByJsonCriteriaAndSaveToDB(existsGroup.getDataJson(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, listJsonSubTargetGroup.get(i), subTargetGroup.getId());
                                else if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_FILEUPLOAD))
                                    campaignManagerService.countMSISDNFromSubGroupByFileAndSaveToDB(existsGroup.getPathFileMsisdn(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, listJsonSubTargetGroup.get(i), subTargetGroup.getId());
                                else if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA))
                                    campaignManagerService.countMSISDNFromSubGroupByFileJOINJsonCriteriaAndSaveToDB(existsGroup.getPathFileMsisdn(), existsGroup.getDataJson(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, listJsonSubTargetGroup.get(i), subTargetGroup.getId());
                            }
                        }
                    }catch (Exception e){
                        logger.info("count MSISDN sub target id = {} fail!", subTargetGroup.getId());
                        logger.error("error: ", e);
                    }
                }
            }

            long end = System.currentTimeMillis();
            logger.info("-------- time save to DB(ms): " + (end - start) + " with campaign ID: " + idEventCampaign);
        } catch (Exception ex) {
            logger.info("error when save event campaign to db:", ex);
            throw new AppException("Có lỗi xảy ra khi tạo chiến dịch, vui lòng liên hệ ban quản trị!");

        }
        return idEventCampaign;
    }

    public EventCampaign saveEventCampaign(Long campaignId, String campaignName, Integer expectedApprovalRate, String campaignTarget, String description,
                                           String startDate, String endDate, String timeRange1Start, String timeRange1End, String timeRange2Start,
                                           String timeRange2End, Long idTargetGroup, Integer typeTargetGroup, String jsonTargetGroup,
                                           Integer typeInputBlacklist, Integer channel, String blacklistPathFile,
                                           String sendingTimeLimitChannel, String disableMessageLimit, Long blacklistTargetGroupId, String subTargetGroupRadio,
                                           Integer isDuplicateMsisdn, Integer hasSubTargetGroup, Long contentId, String pathFileDataCustomer, Long packageGroupId,
                                           String sql, String eventId, String eventCondition, String eventQueueName, String eventConditionRule,Long accountSendingId,
                                           Long packageDataId, Integer eventCycle, Integer eventLimitPerDay, String originalNameFileDataCustomer, String originalNameFileBlacklist) {

        logger.info("=== save event campaign ===");
        UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        EventCampaign eventCampaign;
        if (campaignId == null){
            eventCampaign = new EventCampaign();
        } else {
            eventCampaign = eventCampaignRepository.findById(campaignId).get();
        }

        eventCampaign.setCampaignName(campaignName);
        eventCampaign.setDescription(description);
        if(campaignId == null) {
            eventCampaign.setCreatedDate(new Date());
            eventCampaign.setCreatedUser(currentUser.getUsername());
        }else {
            eventCampaign.setUpdatedDate(new Date());
            eventCampaign.setUpdatedUser(currentUser.getUsername());
        }
        eventCampaign.setStatus(1);
        eventCampaign.setStartDate(AppUtils.convertStringToDate(startDate, "dd/MM/yyyy"));
        eventCampaign.setEndDate((AppUtils.convertStringToDate(endDate, "dd/MM/yyyy")));
        eventCampaign.setTimeRange1Start(timeRange1Start);
        eventCampaign.setTimeRange1End(timeRange1End);
        eventCampaign.setTimeRange2Start(timeRange2Start);
        eventCampaign.setTimeRange2End(timeRange2End);
        eventCampaign.setAcceptanceRate(expectedApprovalRate);
        eventCampaign.setBlackListPathFile(blacklistPathFile);
        eventCampaign.setIdTargetGroup(idTargetGroup);
        eventCampaign.setChannel(channel);
        eventCampaign.setCampaignTarget(campaignTarget);
        eventCampaign.setDataJson(jsonTargetGroup);
        eventCampaign.setTypeTargetGroup(typeTargetGroup);
        eventCampaign.setBlackListTargetGroupId(blacklistTargetGroupId);
        eventCampaign.setSubTargetGroupRadio(subTargetGroupRadio);
        eventCampaign.setIsDuplicateMsisdn(isDuplicateMsisdn);
        eventCampaign.setDisableMessageLimit(disableMessageLimit);
        eventCampaign.setSendingTimeLimitChannel(sendingTimeLimitChannel);
        eventCampaign.setHasSubTargetGroup(hasSubTargetGroup);
        eventCampaign.setTypeInputBlacklist(typeInputBlacklist);
        eventCampaign.setContentId(contentId);
        eventCampaign.setPathFileDataCustomer(pathFileDataCustomer);
        eventCampaign.setPackageGroupId(packageGroupId);
        eventCampaign.setSql(sql);
        eventCampaign.setEventId(Long.parseLong(eventId));
        eventCampaign.setEventCondition(eventCondition);
        eventCampaign.setEventQueueName(eventQueueName);
        eventCampaign.setEventConditionRule(Integer.parseInt(eventConditionRule));
        eventCampaign.setAccountSendingId(accountSendingId);
        eventCampaign.setPackageDataId(packageDataId);
        eventCampaign.setEventLimitPerDay(eventLimitPerDay);
        eventCampaign.setEventCyle(eventCycle);
        eventCampaign.setOriginalNameFileDataCustomer(originalNameFileDataCustomer);
        eventCampaign.setOriginalNameFileBlacklist(originalNameFileBlacklist);

        EventCampaign campaign = eventCampaignRepository.saveAndFlush(eventCampaign);
        return campaign;
    }


    public Optional<EventCampaign> findById(Long id){
        return eventCampaignRepository.findById(id);
    }

    public String detailConditionEvent(Long eventId, JSONObject jsonObject){
        String htmlField = "";
        String conditionCode = jsonObject.getString("conditionName");
        String fullnameOfCondition = eventConditionRepository.findEventNameByEventIdAndJSONField(eventId, conditionCode);
        if(fullnameOfCondition == null || fullnameOfCondition.length() < 0) return " ";
        htmlField += "+ " + fullnameOfCondition;

        String operator = jsonObject.getString("operator");
        if(operator.equals("=")) htmlField += " bằng ";
        else if(operator.equals("!=")) htmlField += " không bằng ";
        else if(operator.equals("<")) htmlField += " nhỏ hơn ";
        else if(operator.equals(">")) htmlField += " lớn hơn ";
        else if(operator.equals("<=")) htmlField += " nhỏ hơn hoặc bằng ";
        else if(operator.equals(">=")) htmlField += " lớn hơn hoặc bằng ";
        else if(operator.equals("><")) htmlField += " trong khoảng ";

        String value = jsonObject.getString("value");
        htmlField += value;

        return htmlField;
    }

    public void cacheTargetGroup(EventCampaign eventCampaign){

        String url = clickHouseUrl + pathAPICacheTargetGroup;
        logger.info("Start call api cache target group event campaign {}", eventCampaign.getCampaignId());
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        if (eventCampaign.getTypeTargetGroup().equals(StaticVar.TARGET_GROUP_FILEUPLOAD)) {
            File file = new File(eventCampaign.getPathFileDataCustomer());
            builder.addBinaryBody("fileCustomer", file, ContentType.DEFAULT_BINARY, eventCampaign.getPathFileDataCustomer());
            //1 khong thiet lap 2//chon nhom doi tuong co san 3 tạo mới
            if(eventCampaign.getTypeInputBlacklist().equals(2)) {
                Optional<TargetGroup> targetGroup = targetGroupService.findById(eventCampaign.getBlackListTargetGroupId());
                File fileTargetGroup = new File(targetGroup.get().getPathFileMsisdn());
                builder.addBinaryBody("fileBlackList", fileTargetGroup, ContentType.DEFAULT_BINARY, eventCampaign.getPathFileDataCustomer());
            }
            if(eventCampaign.getTypeInputBlacklist().equals(3)) {
                File fileTargetGroup = new File(eventCampaign.getBlackListPathFile());
                builder.addBinaryBody("fileBlackList", fileTargetGroup, ContentType.DEFAULT_BINARY, eventCampaign.getPathFileDataCustomer());
            }
        } else if (eventCampaign.getTypeTargetGroup().equals(StaticVar.TARGET_GROUP_EXISTED)) {
            TargetGroup targetGroup = targetGroupService.findById(eventCampaign.getIdTargetGroup()).get();
            File file = new File(targetGroup.getPathFileMsisdn());
            builder.addBinaryBody("fileCustomer", file, ContentType.DEFAULT_BINARY, eventCampaign.getPathFileDataCustomer());
        } else if (eventCampaign.getTypeTargetGroup().equals(StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA)) {
            File file = new File(eventCampaign.getPathFileDataCustomer());
            builder.addBinaryBody("fileCustomer", file, ContentType.DEFAULT_BINARY, eventCampaign.getPathFileDataCustomer());
        }

        builder.addTextBody("eventCampaignId", eventCampaign.getCampaignId().toString(), ContentType.TEXT_PLAIN);
        HttpEntity entity = builder.build();
        String response = httpUtil.postWithEntity(url, entity);
        logger.info("Finish call api cache target group event campaign: {}", response);
    }

    public boolean checkDuplicateCampaignName(String campaignName, Long campaignId) {
        boolean result = true;
        List<Long> eventCampaign = campaignRepository.findByCampaignNameIgnoreCaseAndStatusNot(campaignName.trim(), StaticVar.CAMPAIGN_DELETE_STATUS);
        if (eventCampaign.size() > 0) {
            if (campaignId != null && eventCampaign.size() >= 1 && eventCampaign.get(0).equals(campaignId)) {
                result = true;
            } else {
                result = false;
            }
        }
        return result;
    }
}
