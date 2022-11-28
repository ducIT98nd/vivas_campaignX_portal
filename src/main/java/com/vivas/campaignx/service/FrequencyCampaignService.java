package com.vivas.campaignx.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivas.campaignx.common.AppException;
import com.vivas.campaignx.common.AppUtils;
import com.vivas.campaignx.common.StaticVar;
import com.vivas.campaignx.dto.SmsContentDTO;
import com.vivas.campaignx.entity.FrequencyCampaign;
import com.vivas.campaignx.entity.SmsContent;
import com.vivas.campaignx.entity.SubTargetGroup;
import com.vivas.campaignx.entity.TargetGroup;
import com.vivas.campaignx.repository.*;
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
public class FrequencyCampaignService {
    protected final Logger logger = LogManager.getLogger(this.getClass().getName());

    @Autowired
    private FrequencyCampaignRepository frequencyCampaignRepository;

    @Autowired
    private SmsContentService smsContentService;

    @Autowired
    private SubTargetGroupService subTargetGroupService;

    @Autowired
    private SubTargetGroupRepository subTargetGroupRepository;

    @Autowired
    private SmsContentRepository smsContentRepository;

    @Autowired
    private TargetGroupService targetGroupService;

    @Autowired
    private MappingCriteriaRepository mappingCriteriaRepository;

    @Autowired
    private CampaignTargetTagService campaignTargetTagService;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private CampaignManagerService campaignManagerService;

    @Value("${path_data_target_group}")
    private String path_Data_Target_Group;

    @Value("${path_data_blacklist}")
    private String path_data_blacklist;


    public int countTargetGroupByStatus(Long idTargetGroup) {
        return frequencyCampaignRepository.countTargetGroupByStatus(idTargetGroup);
    }

    public Optional<FrequencyCampaign> findCampaignByNameCaseSensitive(String campaignName) {
        return frequencyCampaignRepository.findCampaignByNameCaseSensitive(campaignName);
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public long creatNewCampaign(String campaignName, Integer expectedApprovalRate, String campaignTarget, String description,
                                 String startDate, String endDate, String timeRange1Start, String timeRange1End, String timeRange2Start,
                                 String timeRange2End, String sendingFrequency, Long packageGroupId, Integer monthlyByDayCb,
                                 String monthlyByWeekday, String monthlyByWeekdayOrdinal, String specificDateValue,
                                 String customDateFreqType, Integer typeTargetGroup, String jsonTargetGroup, Integer typeInputBlacklist,
                                 List<String> messageContent, String disablePolicySendingTimeLimit, String disablePolicyMessageLimit,
                                 MultipartFile dataCustomer, MultipartFile dataBlackList, List<String> listSubTargetGroupName,
                                 List<String> listJsonSubTargetGroup, List<Integer> listSubTargetPriority, String freqDaily,
                                 Integer dayInMount, String customDateCheckbox, Integer periodicNum, Long blacklistTargetGroupId,
                                 String subTargetGroupRadio, String isDuplicate, Long inputTargetGroupId) throws AppException, JsonProcessingException {
        long idCampaign = 0;
        try {

            logger.info("=== create new frequency campaign ===");
            String originalNameFileDataCustomer = null;
            String originalNameFileBlacklist = null;
            long start = System.currentTimeMillis();
            String pathDataCustomer = null;
            String pathDataBlacklist = null;
            if (dataCustomer != null && dataCustomer.getSize() > 0) {
                File dir1 = new File(path_Data_Target_Group);
                if (!dir1.exists()) {
                    dir1.mkdirs();
                }
                pathDataCustomer = AppUtils.saveFile(dataCustomer, path_Data_Target_Group);
                originalNameFileDataCustomer = dataCustomer.getOriginalFilename();
                logger.info("originalNameFileDataCustomer:" + originalNameFileDataCustomer);
                logger.info("path file Data Customer: " + pathDataCustomer);
            }
            if (dataBlackList != null && dataBlackList.getSize() > 0) {
                File dir1 = new File(path_data_blacklist);
                if (!dir1.exists()) {
                    dir1.mkdirs();
                }
                pathDataBlacklist = AppUtils.saveFile(dataBlackList, path_data_blacklist);
                originalNameFileBlacklist = dataBlackList.getOriginalFilename();
                logger.info("originalNameFileBlacklist: " + originalNameFileBlacklist);
                logger.info("path file blacklist: " + pathDataBlacklist);
            }
            if (sendingFrequency.equals("once")) {
                endDate = null;
            }
            String freCfg = genarateJsonConfigTime(sendingFrequency, freqDaily, dayInMount, monthlyByDayCb, monthlyByWeekday, monthlyByWeekdayOrdinal,
                    specificDateValue, customDateFreqType, customDateCheckbox, periodicNum);
            logger.info("config time {}", freCfg);
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
                targetGroup = targetGroupService.saveTargetGroup(jsonTargetGroup, queryTargetGroup);
                inputTargetGroupId = targetGroup.getId();
                Long idSub = null;
                targetGroupService.saveCriteriaSetup(jsonTargetGroup, targetGroup.getId(), idSub);
            } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_EXISTED)) {
                Optional<TargetGroup> targetGroup1 = targetGroupService.findById(inputTargetGroupId);
                if (targetGroup1.isPresent()) {
                    if (Objects.equals(targetGroup1.get().getChannel(), StaticVar.TARGET_GROUP_USE_CRITERIA) || Objects.equals(targetGroup1.get().getChannel(), StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA)) {
                        jsonTargetGroupOld = targetGroup1.get().getDataJson();
                        queryTargetGroup = targetGroup1.get().getQuery();
                    }
                }
            }
            if (subTargetGroupRadio.equals("no")) {
                String jsonContent = messageContent.get(0);
                ObjectMapper objectMapper = new ObjectMapper();
                SmsContentDTO smsContentDTO = objectMapper.readValue(jsonContent, SmsContentDTO.class);
                SmsContent smsContent = smsContentService.saveSmsContent(smsContentDTO.getMessageContent(), smsContentDTO.getCountMT(),
                        smsContentDTO.getUnicode());
                FrequencyCampaign frequencyCampaign = saveFrequencyCampaign(null, campaignName, expectedApprovalRate, campaignTarget, description, startDate,
                        endDate, timeRange1Start, timeRange1End, timeRange2Start, timeRange2End, inputTargetGroupId, freCfg, typeTargetGroup, jsonTargetGroup,
                        typeInputBlacklist, smsContentDTO.getChannelMarketing(), pathDataBlacklist, disablePolicySendingTimeLimit, disablePolicyMessageLimit, blacklistTargetGroupId, subTargetGroupRadio,
                        isDuplicateMsisdn, hasSubTargetGroup, smsContent.getId(), pathDataCustomer, packageGroupId, queryTargetGroup, smsContentDTO.getSendingAccount(), smsContentDTO.getProductPackage(), originalNameFileDataCustomer, originalNameFileBlacklist);
                campaignTargetTagService.saveCampaignTargetTag(campaignTarget, "insert");
                idCampaign = frequencyCampaign.getCampaignId();
                try {
                    if (typeTargetGroup.equals(StaticVar.TARGET_GROUP_USE_CRITERIA)) { //dung tieu chi
                        campaignManagerService.countMSISDNFromMainGroupByJsonCriteriaAndSaveToDB(jsonTargetGroup, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 2l, frequencyCampaign.getCampaignId());
                    } else if (typeTargetGroup.equals(StaticVar.TARGET_GROUP_FILEUPLOAD)) { //dung danh sach thue bao
                        campaignManagerService.countMSISDNFromMainGroupByFileAndSaveToDB(pathDataCustomer, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 2l, frequencyCampaign.getCampaignId());
                    } else if (typeTargetGroup.equals(StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA)) { // dung tap giao
                        campaignManagerService.countMSISDNFromMainGroupByFileJOINJsonCriteriaAndSaveToDB(pathDataCustomer, jsonTargetGroup, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 2l, frequencyCampaign.getCampaignId());
                    } else if (typeTargetGroup.equals(StaticVar.TARGET_GROUP_EXISTED)) { // dung nhom doi tuong co san
                        Optional<TargetGroup> existsGroupOptional = targetGroupService.findById(inputTargetGroupId);
                        if (existsGroupOptional.isPresent()) {
                            TargetGroup existsGroup = existsGroupOptional.get();
                            if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_USE_CRITERIA))
                                campaignManagerService.countMSISDNFromMainGroupByJsonCriteriaAndSaveToDB(existsGroup.getDataJson(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 2l, frequencyCampaign.getCampaignId());
                            else if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_FILEUPLOAD))
                                campaignManagerService.countMSISDNFromMainGroupByFileAndSaveToDB(existsGroup.getPathFileMsisdn(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 2l, frequencyCampaign.getCampaignId());
                            else if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA))
                                campaignManagerService.countMSISDNFromMainGroupByFileJOINJsonCriteriaAndSaveToDB(existsGroup.getPathFileMsisdn(), existsGroup.getDataJson(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 2l, frequencyCampaign.getCampaignId());
                        }
                    }
                } catch (Exception e) {
                    logger.info("count MSISDN by campaign id = {} fail!", frequencyCampaign.getCampaignId());
                    logger.error("error: ", e);
                }


            } else if (subTargetGroupRadio.equals("yes")) {
                Integer channelMktCampaign = null;
                Long contentIdCampaign = null;
                campaignTargetTagService.saveCampaignTargetTag(campaignTarget, "insert");
                Long accountSendingId = null;
                Long packageDataId = null;
                FrequencyCampaign frequencyCampaign = saveFrequencyCampaign(null, campaignName, expectedApprovalRate, campaignTarget, description, startDate,
                        endDate, timeRange1Start, timeRange1End, timeRange2Start, timeRange2End, inputTargetGroupId, freCfg, typeTargetGroup, jsonTargetGroup,
                        typeInputBlacklist, channelMktCampaign, pathDataBlacklist, disablePolicySendingTimeLimit, disablePolicyMessageLimit, blacklistTargetGroupId, subTargetGroupRadio,
                        isDuplicateMsisdn, hasSubTargetGroup, contentIdCampaign, pathDataCustomer, packageGroupId, queryTargetGroup, accountSendingId, packageDataId, originalNameFileDataCustomer, originalNameFileBlacklist);
                Optional<TargetGroup> existsGroupOptional = null;
                idCampaign = frequencyCampaign.getCampaignId();
                try {
                    if (typeTargetGroup.equals(StaticVar.TARGET_GROUP_USE_CRITERIA)) { //dung tieu chi
                        campaignManagerService.countMSISDNFromMainGroupByJsonCriteriaAndSaveToDB(jsonTargetGroup, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 2l, frequencyCampaign.getCampaignId());
                    } else if (typeTargetGroup.equals(StaticVar.TARGET_GROUP_FILEUPLOAD)) { //dung danh sach thue bao
                        campaignManagerService.countMSISDNFromMainGroupByFileAndSaveToDB(pathDataCustomer, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 2l, frequencyCampaign.getCampaignId());
                    } else if (typeTargetGroup.equals(StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA)) { // dung tap giao
                        campaignManagerService.countMSISDNFromMainGroupByFileJOINJsonCriteriaAndSaveToDB(pathDataCustomer, jsonTargetGroup, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 2l, frequencyCampaign.getCampaignId());
                    } else if (typeTargetGroup.equals(StaticVar.TARGET_GROUP_EXISTED)) { // dung nhom doi tuong co san
                        existsGroupOptional = targetGroupService.findById(inputTargetGroupId);
                        if (existsGroupOptional.isPresent()) {
                            TargetGroup existsGroup = existsGroupOptional.get();
                            if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_USE_CRITERIA))
                                campaignManagerService.countMSISDNFromMainGroupByJsonCriteriaAndSaveToDB(existsGroup.getDataJson(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 2l, frequencyCampaign.getCampaignId());
                            else if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_FILEUPLOAD))
                                campaignManagerService.countMSISDNFromMainGroupByFileAndSaveToDB(existsGroup.getPathFileMsisdn(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 2l, frequencyCampaign.getCampaignId());
                            else if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA))
                                campaignManagerService.countMSISDNFromMainGroupByFileJOINJsonCriteriaAndSaveToDB(existsGroup.getPathFileMsisdn(), existsGroup.getDataJson(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 2l, frequencyCampaign.getCampaignId());
                        }
                    }
                } catch (Exception e) {
                    logger.info("count MSISDN by campaign id = {} fail!", frequencyCampaign.getCampaignId());
                    logger.error("error: ", e);
                }

                List<String> listSqlSub = new ArrayList<>();
                if (typeTargetGroup.equals(StaticVar.TARGET_GROUP_USE_CRITERIA) || typeTargetGroup.equals(StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA)) {
                    listSqlSub = targetGroupService.genSubQuery(jsonTargetGroup, listJsonSubTargetGroup);
                } else if (typeTargetGroup.equals(StaticVar.TARGET_GROUP_EXISTED)) {
                    Optional<TargetGroup> targetGroup1 = targetGroupService.findById(inputTargetGroupId);
                    if (targetGroup1.isPresent()) {
                        if (Objects.equals(targetGroup1.get().getChannel(), StaticVar.TARGET_GROUP_FILEUPLOAD)) {
                            for (int i = 0; i < listJsonSubTargetGroup.size(); i++) {
                                listSqlSub.add(targetGroupService.genQuery(listJsonSubTargetGroup.get(i)));
                            }
                        } else {
                            listSqlSub = targetGroupService.genSubQuery(jsonTargetGroupOld, listJsonSubTargetGroup);
                        }
                    }
                } else if (typeTargetGroup.equals(StaticVar.TARGET_GROUP_FILEUPLOAD)) {
                    for (int i = 0; i < listJsonSubTargetGroup.size(); i++) {
                        listSqlSub.add(targetGroupService.genQuery(listJsonSubTargetGroup.get(i)));
                    }
                }
                Long idTarget = null;
                for (int i = 0; i < listJsonSubTargetGroup.size(); i++) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    SmsContentDTO smsContentDTO = objectMapper.readValue(messageContent.get(i + 1), SmsContentDTO.class);
                    SmsContent smsContent = smsContentService.saveSmsContent(smsContentDTO.getMessageContent(), smsContentDTO.getCountMT(),
                            smsContentDTO.getUnicode());
                    SubTargetGroup subTargetGroup = subTargetGroupService.saveSubTargetGroup(frequencyCampaign.getCampaignId(), listSubTargetGroupName.get(i), smsContentDTO.getChannelMarketing(),
                            listJsonSubTargetGroup.get(i), listSubTargetPriority.get(i), smsContent.getId(), listSqlSub.get(i), smsContentDTO.getSendingAccount(), smsContentDTO.getProductPackage());
                    targetGroupService.saveCriteriaSetup(listJsonSubTargetGroup.get(i), idTarget, subTargetGroup.getId());

                    try {
                        if (typeTargetGroup.equals(StaticVar.TARGET_GROUP_USE_CRITERIA)) { //dung tieu chi
                            campaignManagerService.countMSISDNFromSubGroupByJsonCriteriaAndSaveToDB(jsonTargetGroup, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, listJsonSubTargetGroup.get(i), subTargetGroup.getId());
                        } else if (typeTargetGroup.equals(StaticVar.TARGET_GROUP_FILEUPLOAD)) { //dung danh sach thue bao
                            campaignManagerService.countMSISDNFromSubGroupByFileAndSaveToDB(pathDataCustomer, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, listJsonSubTargetGroup.get(i), subTargetGroup.getId());
                        } else if (typeTargetGroup.equals(StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA)) { // dung tap giao
                            campaignManagerService.countMSISDNFromSubGroupByFileJOINJsonCriteriaAndSaveToDB(pathDataCustomer, jsonTargetGroup, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, listJsonSubTargetGroup.get(i), subTargetGroup.getId());
                        } else if (typeTargetGroup.equals(StaticVar.TARGET_GROUP_EXISTED)) { // dung nhom doi tuong co san
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
                    } catch (Exception e) {
                        logger.info("count MSISDN sub target id = {} fail!", subTargetGroup.getId());
                        logger.error("error: ", e);
                    }

                }
            }

            long end = System.currentTimeMillis();
            logger.info("-------- time save to DB(ms): " + (end - start) + " with campaign id = " + idCampaign);
        } catch (Exception ex) {
            logger.info("error when save frequency campaign to db:", ex);
            throw new AppException("Có lỗi xảy ra khi tạo chiến dịch, vui lòng liên hệ ban quản trị!");
        }
        return idCampaign;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public long copyNewCampaign(Long id, String campaignName, Integer expectedApprovalRate, String campaignTarget, String description,
                                String startDate, String endDate, String timeRange1Start, String timeRange1End, String timeRange2Start,
                                String timeRange2End, String sendingFrequency, Long packageGroupId, Integer monthlyByDayCb,
                                String monthlyByWeekday, String monthlyByWeekdayOrdinal, String specificDateValue,
                                String customDateFreqType, Integer typeTargetGroup, String jsonTargetGroup, Integer typeInputBlacklist,
                                List<String> messageContent, String disablePolicySendingTimeLimit, String disablePolicyMessageLimit,
                                MultipartFile dataCustomer, MultipartFile dataBlackList, List<String> listSubTargetGroupName,
                                List<String> listJsonSubTargetGroup, List<Integer> listSubTargetPriority, String freqDaily,
                                Integer dayInMount, String customDateCheckbox, Integer periodicNum, Long blacklistTargetGroupId,
                                String subTargetGroupRadio, String isDuplicate, Long inputTargetGroupId) throws AppException, JsonProcessingException {
        long idCampaign = 0;
        try {
            logger.info("=== copy frequency campaign ===");
            String originalNameFileDataCustomer = null;
            String originalNameFileBlacklist = null;
            long start = System.currentTimeMillis();
            String pathDataCustomer = null;
            String pathDataBlacklist = null;
            Optional<FrequencyCampaign> frequencyCampaignOptional = frequencyCampaignRepository.findById(id);
            FrequencyCampaign frequencyCampaignOld = new FrequencyCampaign();
            if (frequencyCampaignOptional.isPresent()) {
                frequencyCampaignOld = frequencyCampaignOptional.get();
            }
            if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_FILEUPLOAD) || Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA)) {
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
                    originalNameFileDataCustomer = frequencyCampaignOld.getOriginalNameFileDataCustomer();
                    pathDataCustomer = frequencyCampaignOld.getPathFileDataCustomer();
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
                    originalNameFileBlacklist = frequencyCampaignOld.getOriginalNameFileBlacklist();
                    pathDataBlacklist = frequencyCampaignOld.getBlackListPathFile();
                }
            }
            if (sendingFrequency.equals("once")) {
                endDate = null;
            }
            String freCfg = genarateJsonConfigTime(sendingFrequency, freqDaily, dayInMount, monthlyByDayCb, monthlyByWeekday, monthlyByWeekdayOrdinal,
                    specificDateValue, customDateFreqType, customDateCheckbox, periodicNum);
            logger.info("config time {}", freCfg);
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
                targetGroup = targetGroupService.saveTargetGroup(jsonTargetGroup, queryTargetGroup);
                inputTargetGroupId = targetGroup.getId();
                Long idSub = null;
                targetGroupService.saveCriteriaSetup(jsonTargetGroup, targetGroup.getId(), idSub);
            } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_EXISTED)) {
                Optional<TargetGroup> targetGroup1 = targetGroupService.findById(inputTargetGroupId);
                if (targetGroup1.isPresent()) {
                    if (targetGroup1.get().getChannel() == 1 || targetGroup1.get().getChannel() == 4) {
                        jsonTargetGroupOld = targetGroup1.get().getDataJson();
                        queryTargetGroup = targetGroup1.get().getQuery();
                    }
                }
            }
            if (subTargetGroupRadio.equals("no")) {
                String jsonContent = messageContent.get(0);
                ObjectMapper objectMapper = new ObjectMapper();
                SmsContentDTO smsContentDTO = objectMapper.readValue(jsonContent, SmsContentDTO.class);
                SmsContent smsContent = smsContentService.saveSmsContent(smsContentDTO.getMessageContent(), smsContentDTO.getCountMT(),
                        smsContentDTO.getUnicode());
                FrequencyCampaign frequencyCampaign = saveFrequencyCampaign(null, campaignName, expectedApprovalRate, campaignTarget, description, startDate,
                        endDate, timeRange1Start, timeRange1End, timeRange2Start, timeRange2End, inputTargetGroupId, freCfg, typeTargetGroup, jsonTargetGroup,
                        typeInputBlacklist, smsContentDTO.getChannelMarketing(), pathDataBlacklist, disablePolicySendingTimeLimit, disablePolicyMessageLimit, blacklistTargetGroupId, subTargetGroupRadio,
                        isDuplicateMsisdn, hasSubTargetGroup, smsContent.getId(), pathDataCustomer, packageGroupId, queryTargetGroup, smsContentDTO.getSendingAccount(), smsContentDTO.getProductPackage(), originalNameFileDataCustomer, originalNameFileBlacklist);
                idCampaign = frequencyCampaign.getCampaignId();
                campaignTargetTagService.saveCampaignTargetTag(campaignTarget, "insert");
                try {
                    if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_USE_CRITERIA)) { //dung tieu chi
                        campaignManagerService.countMSISDNFromMainGroupByJsonCriteriaAndSaveToDB(jsonTargetGroup, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 2l, frequencyCampaign.getCampaignId());
                    } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_FILEUPLOAD)) { //dung danh sach thue bao
                        campaignManagerService.countMSISDNFromMainGroupByFileAndSaveToDB(pathDataCustomer, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 2l, frequencyCampaign.getCampaignId());
                    } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA)) { // dung tap giao
                        campaignManagerService.countMSISDNFromMainGroupByFileJOINJsonCriteriaAndSaveToDB(pathDataCustomer, jsonTargetGroup, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 2l, frequencyCampaign.getCampaignId());
                    } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_EXISTED)) { // dung nhom doi tuong co san
                        Optional<TargetGroup> existsGroupOptional = targetGroupService.findById(inputTargetGroupId);
                        if (existsGroupOptional.isPresent()) {
                            TargetGroup existsGroup = existsGroupOptional.get();
                            if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_USE_CRITERIA))
                                campaignManagerService.countMSISDNFromMainGroupByJsonCriteriaAndSaveToDB(existsGroup.getDataJson(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 2l, frequencyCampaign.getCampaignId());
                            else if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_FILEUPLOAD))
                                campaignManagerService.countMSISDNFromMainGroupByFileAndSaveToDB(existsGroup.getPathFileMsisdn(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 2l, frequencyCampaign.getCampaignId());
                            else if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA))
                                campaignManagerService.countMSISDNFromMainGroupByFileJOINJsonCriteriaAndSaveToDB(existsGroup.getPathFileMsisdn(), existsGroup.getDataJson(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 2l, frequencyCampaign.getCampaignId());
                        }
                    }
                } catch (Exception e) {
                    logger.info("count MSISDN by campaign id = {} fail!", frequencyCampaign.getCampaignId());
                    logger.error("error: ", e);
                }


            } else if (subTargetGroupRadio.equals("yes")) {
                Integer channelMktCampaign = null;
                Long contentIdCampaign = null;
                campaignTargetTagService.saveCampaignTargetTag(campaignTarget, "insert");
                Long accountSendingId = null;
                Long packageDataId = null;
                FrequencyCampaign frequencyCampaign = saveFrequencyCampaign(null, campaignName, expectedApprovalRate, campaignTarget, description, startDate,
                        endDate, timeRange1Start, timeRange1End, timeRange2Start, timeRange2End, inputTargetGroupId, freCfg, typeTargetGroup, jsonTargetGroup,
                        typeInputBlacklist, channelMktCampaign, pathDataBlacklist, disablePolicySendingTimeLimit, disablePolicyMessageLimit, blacklistTargetGroupId, subTargetGroupRadio,
                        isDuplicateMsisdn, hasSubTargetGroup, contentIdCampaign, pathDataCustomer, packageGroupId, queryTargetGroup, accountSendingId, packageDataId, originalNameFileDataCustomer, originalNameFileBlacklist);
                idCampaign = frequencyCampaign.getCampaignId();
                Optional<TargetGroup> existsGroupOptional = null;

                try {

                    if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_USE_CRITERIA)) { //dung tieu chi
                        campaignManagerService.countMSISDNFromMainGroupByJsonCriteriaAndSaveToDB(jsonTargetGroup, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 2l, frequencyCampaign.getCampaignId());
                    } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_FILEUPLOAD)) { //dung danh sach thue bao
                        campaignManagerService.countMSISDNFromMainGroupByFileAndSaveToDB(pathDataCustomer, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 2l, frequencyCampaign.getCampaignId());
                    } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA)) { // dung tap giao
                        campaignManagerService.countMSISDNFromMainGroupByFileJOINJsonCriteriaAndSaveToDB(pathDataCustomer, jsonTargetGroup, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 2l, frequencyCampaign.getCampaignId());
                    } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_EXISTED)) { // dung nhom doi tuong co san
                        existsGroupOptional = targetGroupService.findById(inputTargetGroupId);
                        if (existsGroupOptional.isPresent()) {
                            TargetGroup existsGroup = existsGroupOptional.get();
                            if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_USE_CRITERIA))
                                campaignManagerService.countMSISDNFromMainGroupByJsonCriteriaAndSaveToDB(existsGroup.getDataJson(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 2l, frequencyCampaign.getCampaignId());
                            else if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_FILEUPLOAD))
                                campaignManagerService.countMSISDNFromMainGroupByFileAndSaveToDB(existsGroup.getPathFileMsisdn(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 2l, frequencyCampaign.getCampaignId());
                            else if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA))
                                campaignManagerService.countMSISDNFromMainGroupByFileJOINJsonCriteriaAndSaveToDB(existsGroup.getPathFileMsisdn(), existsGroup.getDataJson(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 2l, frequencyCampaign.getCampaignId());
                        }
                    }
                } catch (Exception e) {
                    logger.info("count MSISDN by campaign id = {} fail!", frequencyCampaign.getCampaignId());
                    logger.error("error: ", e);
                }

                List<String> listSqlSub = new ArrayList<>();
                if (typeTargetGroup == StaticVar.TARGET_GROUP_USE_CRITERIA || typeTargetGroup == StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA) {
                    listSqlSub = targetGroupService.genSubQuery(jsonTargetGroup, listJsonSubTargetGroup);
                } else if (typeTargetGroup == StaticVar.TARGET_GROUP_EXISTED) {
                    Optional<TargetGroup> targetGroup1 = targetGroupService.findById(inputTargetGroupId);
                    if (targetGroup1.isPresent()) {
                        if (targetGroup1.get().getChannel() == StaticVar.TARGET_GROUP_FILEUPLOAD) {
                            for (int i = 0; i < listJsonSubTargetGroup.size(); i++) {
                                listSqlSub.add(targetGroupService.genQuery(listJsonSubTargetGroup.get(i)));
                            }
                        } else {
                            listSqlSub = targetGroupService.genSubQuery(jsonTargetGroupOld, listJsonSubTargetGroup);
                        }
                    }
                } else if (typeTargetGroup == StaticVar.TARGET_GROUP_FILEUPLOAD) {
                    for (int i = 0; i < listJsonSubTargetGroup.size(); i++) {
                        listSqlSub.add(targetGroupService.genQuery(listJsonSubTargetGroup.get(i)));
                    }
                }
                Long idTarget = null;
                for (int i = 0; i < listJsonSubTargetGroup.size(); i++) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    SmsContentDTO smsContentDTO = objectMapper.readValue(messageContent.get(i + 1), SmsContentDTO.class);
                    SmsContent smsContent = smsContentService.saveSmsContent(smsContentDTO.getMessageContent(), smsContentDTO.getCountMT(),
                            smsContentDTO.getUnicode());
                    SubTargetGroup subTargetGroup = subTargetGroupService.saveSubTargetGroup(frequencyCampaign.getCampaignId(), listSubTargetGroupName.get(i), smsContentDTO.getChannelMarketing(),
                            listJsonSubTargetGroup.get(i), listSubTargetPriority.get(i), smsContent.getId(), listSqlSub.get(i), smsContentDTO.getSendingAccount(), smsContentDTO.getProductPackage());
                    targetGroupService.saveCriteriaSetup(listJsonSubTargetGroup.get(i), idTarget, subTargetGroup.getId());
                    try {
                        if (typeTargetGroup == StaticVar.TARGET_GROUP_USE_CRITERIA) { //dung tieu chi
                            campaignManagerService.countMSISDNFromSubGroupByJsonCriteriaAndSaveToDB(jsonTargetGroup, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, listJsonSubTargetGroup.get(i), subTargetGroup.getId());
                        } else if (typeTargetGroup == StaticVar.TARGET_GROUP_FILEUPLOAD) { //dung danh sach thue bao
                            campaignManagerService.countMSISDNFromSubGroupByFileAndSaveToDB(pathDataCustomer, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, listJsonSubTargetGroup.get(i), subTargetGroup.getId());
                        } else if (typeTargetGroup == StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA) { // dung tap giao
                            campaignManagerService.countMSISDNFromSubGroupByFileJOINJsonCriteriaAndSaveToDB(pathDataCustomer, jsonTargetGroup, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, listJsonSubTargetGroup.get(i), subTargetGroup.getId());
                        } else if (typeTargetGroup == StaticVar.TARGET_GROUP_EXISTED) { // dung nhom doi tuong co san
                            if (existsGroupOptional.isPresent()) {
                                TargetGroup existsGroup = existsGroupOptional.get();
                                if (existsGroup.getChannel() == StaticVar.TARGET_GROUP_USE_CRITERIA)
                                    campaignManagerService.countMSISDNFromSubGroupByJsonCriteriaAndSaveToDB(existsGroup.getDataJson(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, listJsonSubTargetGroup.get(i), subTargetGroup.getId());
                                else if (existsGroup.getChannel() == StaticVar.TARGET_GROUP_FILEUPLOAD)
                                    campaignManagerService.countMSISDNFromSubGroupByFileAndSaveToDB(existsGroup.getPathFileMsisdn(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, listJsonSubTargetGroup.get(i), subTargetGroup.getId());
                                else if (existsGroup.getChannel() == StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA)
                                    campaignManagerService.countMSISDNFromSubGroupByFileJOINJsonCriteriaAndSaveToDB(existsGroup.getPathFileMsisdn(), existsGroup.getDataJson(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, listJsonSubTargetGroup.get(i), subTargetGroup.getId());
                            }
                        }
                    } catch (Exception e) {
                        logger.info("count MSISDN sub target id = {} fail!", subTargetGroup.getId());
                        logger.error("error: ", e);
                    }

                }
            }

            long end = System.currentTimeMillis();
            logger.info("-------- time save to DB(ms): " + (end - start) + " with campaign id = " + id);
        } catch (Exception ex) {
            logger.info("error when save frequency campaign to db:", ex);
            throw new AppException("Có lỗi xảy ra khi tạo chiến dịch, vui lòng liên hệ ban quản trị!");

        }
        return idCampaign;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public long updateCampaign(Long idFrequencyCampaign, String campaignName, Integer expectedApprovalRate, String campaignTarget, String description,
                               String startDate, String endDate, String timeRange1Start, String timeRange1End, String timeRange2Start,
                               String timeRange2End, String sendingFrequency, Long packageGroupId, Integer monthlyByDayCb,
                               String monthlyByWeekday, String monthlyByWeekdayOrdinal, String specificDateValue, String customDateFreqType,
                               Integer typeTargetGroup, String jsonTargetGroup, Integer typeInputBlacklist, List<String> messageContent,
                               String disablePolicySendingTimeLimit, String disablePolicyMessageLimit, MultipartFile dataCustomer,
                               MultipartFile dataBlackList, List<String> listSubTargetGroupName, List<String> listJsonSubTargetGroup,
                               List<Integer> listSubTargetPriority, String freqDaily, Integer dayInMount, String customDateCheckbox, Integer periodicNum,
                               Long blacklistTargetGroupId, String subTargetGroupRadio, String isDuplicate, Long inputTargetGroupId, Integer hasSubOld) throws AppException, JsonProcessingException {
        try {
            logger.info("=== update frequency campaign ===");
            String originalNameFileDataCustomer = null;
            String originalNameFileBlacklist = null;
            long start = System.currentTimeMillis();
            Optional<FrequencyCampaign> frequencyCampaignOptional = frequencyCampaignRepository.findById(idFrequencyCampaign);
            FrequencyCampaign frequencyCampaignOld = new FrequencyCampaign();
            if (frequencyCampaignOptional.isPresent()) {
                frequencyCampaignOld = frequencyCampaignOptional.get();
            }
            String pathDataCustomer = null;
            String pathDataBlacklist = null;
            if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_FILEUPLOAD) || Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA)) {
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
                    originalNameFileDataCustomer = frequencyCampaignOld.getOriginalNameFileDataCustomer();
                    pathDataCustomer = frequencyCampaignOld.getPathFileDataCustomer();
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
                    originalNameFileBlacklist = frequencyCampaignOld.getOriginalNameFileBlacklist();
                    pathDataBlacklist = frequencyCampaignOld.getBlackListPathFile();
                }
            }
            if (sendingFrequency.equals("once")) {
                endDate = null;
            }
            String freCfg = genarateJsonConfigTime(sendingFrequency, freqDaily, dayInMount, monthlyByDayCb, monthlyByWeekday, monthlyByWeekdayOrdinal,
                    specificDateValue, customDateFreqType, customDateCheckbox, periodicNum);
            logger.info("config time {}", freCfg);
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
                targetGroup = targetGroupService.saveTargetGroup(jsonTargetGroup, queryTargetGroup);
                inputTargetGroupId = targetGroup.getId();
                Long idSub = null;
                targetGroupService.saveCriteriaSetup(jsonTargetGroup, targetGroup.getId(), idSub);
            } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_EXISTED)) {
                Optional<TargetGroup> targetGroup1 = targetGroupService.findById(inputTargetGroupId);
                if (targetGroup1.isPresent()) {
                    if (Objects.equals(targetGroup1.get().getChannel(), StaticVar.TARGET_GROUP_USE_CRITERIA) || Objects.equals(targetGroup1.get().getChannel(), StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA)) {
                        jsonTargetGroupOld = targetGroup1.get().getDataJson();
                        queryTargetGroup = targetGroup1.get().getQuery();
                    }
                }
            }
            if (subTargetGroupRadio.equals("no")) {
                Optional<FrequencyCampaign> optionalFrequencyCampaign = frequencyCampaignRepository.findById(idFrequencyCampaign);
                optionalFrequencyCampaign.ifPresent(x -> {
                    if (0 == hasSubOld) {
                        smsContentRepository.deleteById(x.getContentId());
                    } else {
                        List<SubTargetGroup> subTargetGroupList = subTargetGroupRepository.findAllByCampaignId(idFrequencyCampaign);
                        for (int i = 0; i < subTargetGroupList.size(); i++) {
                            mappingCriteriaRepository.deleteAllByIdSubTargetGroup(subTargetGroupList.get(i).getId());
                            smsContentRepository.deleteById(subTargetGroupList.get(i).getContentId());
                        }
                        subTargetGroupRepository.deleteAllByCampaignId(idFrequencyCampaign);
                    }
                });
                String jsonContent = messageContent.get(0);
                ObjectMapper objectMapper = new ObjectMapper();
                SmsContentDTO smsContentDTO = objectMapper.readValue(jsonContent, SmsContentDTO.class);
                SmsContent smsContent = smsContentService.saveSmsContent(smsContentDTO.getMessageContent(), smsContentDTO.getCountMT(),
                        smsContentDTO.getUnicode());
                FrequencyCampaign frequencyCampaign = saveFrequencyCampaign(idFrequencyCampaign, campaignName, expectedApprovalRate, campaignTarget, description, startDate,
                        endDate, timeRange1Start, timeRange1End, timeRange2Start, timeRange2End, inputTargetGroupId, freCfg, typeTargetGroup, jsonTargetGroup,
                        typeInputBlacklist, smsContentDTO.getChannelMarketing(), pathDataBlacklist, disablePolicySendingTimeLimit, disablePolicyMessageLimit, blacklistTargetGroupId, subTargetGroupRadio,
                        isDuplicateMsisdn, hasSubTargetGroup, smsContent.getId(), pathDataCustomer, packageGroupId, queryTargetGroup, smsContentDTO.getSendingAccount(), smsContentDTO.getProductPackage(), originalNameFileDataCustomer, originalNameFileBlacklist);
                campaignTargetTagService.saveCampaignTargetTag(campaignTarget, "update");

                try {
                    if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_USE_CRITERIA)) { //dung tieu chi
                        campaignManagerService.countMSISDNFromMainGroupByJsonCriteriaAndSaveToDB(jsonTargetGroup, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 2l, frequencyCampaign.getCampaignId());
                    } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_FILEUPLOAD)) { //dung danh sach thue bao
                        campaignManagerService.countMSISDNFromMainGroupByFileAndSaveToDB(pathDataCustomer, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 2l, frequencyCampaign.getCampaignId());
                    } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA)) { // dung tap giao
                        campaignManagerService.countMSISDNFromMainGroupByFileJOINJsonCriteriaAndSaveToDB(pathDataCustomer, jsonTargetGroup, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 2l, frequencyCampaign.getCampaignId());
                    } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_EXISTED)) { // dung nhom doi tuong co san
                        Optional<TargetGroup> existsGroupOptional = targetGroupService.findById(inputTargetGroupId);
                        if (existsGroupOptional.isPresent()) {
                            TargetGroup existsGroup = existsGroupOptional.get();
                            if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_USE_CRITERIA))
                                campaignManagerService.countMSISDNFromMainGroupByJsonCriteriaAndSaveToDB(existsGroup.getDataJson(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 2l, frequencyCampaign.getCampaignId());
                            else if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_FILEUPLOAD))
                                campaignManagerService.countMSISDNFromMainGroupByFileAndSaveToDB(existsGroup.getPathFileMsisdn(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 2l, frequencyCampaign.getCampaignId());
                            else if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA))
                                campaignManagerService.countMSISDNFromMainGroupByFileJOINJsonCriteriaAndSaveToDB(existsGroup.getPathFileMsisdn(), existsGroup.getDataJson(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 2l, frequencyCampaign.getCampaignId());
                        }
                    }
                } catch (Exception e) {
                    logger.info("count MSISDN by campaign id = {} fail!", frequencyCampaign.getCampaignId());
                    logger.error("error: ", e);
                }

            } else if (subTargetGroupRadio.equals("yes")) {
                if (1 == hasSubOld) {
                    List<SubTargetGroup> subTargetGroupList = subTargetGroupRepository.findAllByCampaignId(idFrequencyCampaign);
                    for (int i = 0; i < subTargetGroupList.size(); i++) {
                        mappingCriteriaRepository.deleteAllByIdSubTargetGroup(subTargetGroupList.get(i).getId());
                        smsContentRepository.deleteById(subTargetGroupList.get(i).getContentId());
                    }
                    subTargetGroupRepository.deleteAllByCampaignId(idFrequencyCampaign);
                } else {
                    Optional<FrequencyCampaign> optionalFrequencyCampaign = frequencyCampaignRepository.findById(idFrequencyCampaign);
                    optionalFrequencyCampaign.ifPresent(x -> {
                        smsContentRepository.deleteById(x.getContentId());
                    });
                }
                Integer channelMktCampaign = null;
                Long contentIdCampaign = null;
                campaignTargetTagService.saveCampaignTargetTag(campaignTarget, "update");
                Long accountSendingId = null;
                Long packageDataId = null;
                FrequencyCampaign frequencyCampaign = saveFrequencyCampaign(idFrequencyCampaign, campaignName, expectedApprovalRate, campaignTarget, description, startDate,
                        endDate, timeRange1Start, timeRange1End, timeRange2Start, timeRange2End, inputTargetGroupId, freCfg, typeTargetGroup, jsonTargetGroup,
                        typeInputBlacklist, channelMktCampaign, pathDataBlacklist, disablePolicySendingTimeLimit, disablePolicyMessageLimit, blacklistTargetGroupId, subTargetGroupRadio,
                        isDuplicateMsisdn, hasSubTargetGroup, contentIdCampaign, pathDataCustomer, packageGroupId, queryTargetGroup, accountSendingId, packageDataId, originalNameFileDataCustomer, originalNameFileBlacklist);
                Optional<TargetGroup> existsGroupOptional = null;


                try {
                    if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_USE_CRITERIA)) { //dung tieu chi
                        campaignManagerService.countMSISDNFromMainGroupByJsonCriteriaAndSaveToDB(jsonTargetGroup, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 2l, frequencyCampaign.getCampaignId());
                    } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_FILEUPLOAD)) { //dung danh sach thue bao
                        campaignManagerService.countMSISDNFromMainGroupByFileAndSaveToDB(pathDataCustomer, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 2l, frequencyCampaign.getCampaignId());
                    } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA)) { // dung tap giao
                        campaignManagerService.countMSISDNFromMainGroupByFileJOINJsonCriteriaAndSaveToDB(pathDataCustomer, jsonTargetGroup, typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 2l, frequencyCampaign.getCampaignId());
                    } else if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_EXISTED)) { // dung nhom doi tuong co san
                        existsGroupOptional = targetGroupService.findById(inputTargetGroupId);
                        if (existsGroupOptional.isPresent()) {
                            TargetGroup existsGroup = existsGroupOptional.get();
                            if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_USE_CRITERIA))
                                campaignManagerService.countMSISDNFromMainGroupByJsonCriteriaAndSaveToDB(existsGroup.getDataJson(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 2l, frequencyCampaign.getCampaignId());
                            else if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_FILEUPLOAD))
                                campaignManagerService.countMSISDNFromMainGroupByFileAndSaveToDB(existsGroup.getPathFileMsisdn(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 2l, frequencyCampaign.getCampaignId());
                            else if (Objects.equals(existsGroup.getChannel(), StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA))
                                campaignManagerService.countMSISDNFromMainGroupByFileJOINJsonCriteriaAndSaveToDB(existsGroup.getPathFileMsisdn(), existsGroup.getDataJson(), typeInputBlacklist.longValue(), pathDataBlacklist, blacklistTargetGroupId, 2l, frequencyCampaign.getCampaignId());
                        }
                    }
                } catch (Exception e) {
                    logger.info("count MSISDN by campaign id = {} fail!", frequencyCampaign.getCampaignId());
                    logger.error("error: ", e);
                }

                List<String> listSqlSub = new ArrayList<>();
                if (Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_USE_CRITERIA) || Objects.equals(typeTargetGroup, StaticVar.TARGET_GROUP_INTERSECT_FILEUPLOAD_AND_CRITERIA)) {
                    listSqlSub = targetGroupService.genSubQuery(jsonTargetGroup, listJsonSubTargetGroup);
                } else if (typeTargetGroup == 6) {
                    Optional<TargetGroup> targetGroup1 = targetGroupService.findById(inputTargetGroupId);
                    if (targetGroup1.isPresent()) {
                        if (Objects.equals(targetGroup1.get().getChannel(), StaticVar.TARGET_GROUP_FILEUPLOAD)) {
                            for (int i = 0; i < listJsonSubTargetGroup.size(); i++) {
                                listSqlSub.add(targetGroupService.genQuery(listJsonSubTargetGroup.get(i)));
                            }
                        } else {
                            listSqlSub = targetGroupService.genSubQuery(jsonTargetGroupOld, listJsonSubTargetGroup);
                        }
                    }
                } else if (typeTargetGroup.equals(StaticVar.TARGET_GROUP_FILEUPLOAD)) {
                    for (int i = 0; i < listJsonSubTargetGroup.size(); i++) {
                        listSqlSub.add(targetGroupService.genQuery(listJsonSubTargetGroup.get(i)));
                    }
                }
                Long idTarget = null;
                for (int i = 0; i < listJsonSubTargetGroup.size(); i++) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    SmsContentDTO smsContentDTO = objectMapper.readValue(messageContent.get(i + 1), SmsContentDTO.class);
                    SmsContent smsContent = smsContentService.saveSmsContent(smsContentDTO.getMessageContent(), smsContentDTO.getCountMT(),
                            smsContentDTO.getUnicode());
                    SubTargetGroup subTargetGroup = subTargetGroupService.saveSubTargetGroup(frequencyCampaign.getCampaignId(), listSubTargetGroupName.get(i), smsContentDTO.getChannelMarketing(),
                            listJsonSubTargetGroup.get(i), listSubTargetPriority.get(i), smsContent.getId(), listSqlSub.get(i), smsContentDTO.getSendingAccount(), smsContentDTO.getProductPackage());
                    targetGroupService.saveCriteriaSetup(listJsonSubTargetGroup.get(i), idTarget, subTargetGroup.getId());
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
                    } catch (Exception e) {
                        logger.info("count MSISDN sub target id = {} fail!", subTargetGroup.getId());
                        logger.error("error: ", e);
                    }
                }
            }

            long end = System.currentTimeMillis();
            logger.info("-------- time save to DB(ms): " + (end - start) + " with campaign id = " + idFrequencyCampaign);
        } catch (Exception ex) {
            logger.info("error when save frequency campaign to db:", ex);
            throw new AppException("Có lỗi xảy ra khi cập nhật chiến dịch, vui lòng liên hệ ban quản trị!");

        }
        return idFrequencyCampaign;
    }

    public String genarateJsonConfigTime(String sendingFrequency, String freqDaily,
                                         Integer dayInMount, Integer monthlyByDayCb,
                                         String monthlyByWeekday, String monthlyByWeekdayOrdinal,
                                         String specificDateValue, String customDateFreqType, String customDateCheckbox, Integer periodicNum) {
        String freCfg = null;

        logger.info("=== genarate sending frequency to json ===");
        JSONObject obj = new JSONObject();
        JSONObject obj1 = new JSONObject();
        JSONObject obj2 = new JSONObject();
        if (sendingFrequency.equals("once")) {
            obj.put("frequency", "once");
            obj.put("value", "");
            freCfg = obj.toString();
        } else if (sendingFrequency.equals("daily")) {
            obj.put("frequency", "daily");
            obj.put("value", "");
            freCfg = obj.toString();
        } else if (sendingFrequency.equals("weekly")) {
            obj.put("frequency", "weekly");
            obj.put("value", freqDaily);
            freCfg = obj.toString();
        } else if (sendingFrequency.equals("monthly")) {
            if (monthlyByDayCb == 1) {
                obj.put("frequency", "monthly1");
                obj.put("value", dayInMount);
                freCfg = obj.toString();
            } else if (monthlyByDayCb == 2) {
                obj.put("frequency", "monthly2");
                obj.put("value", monthlyByWeekday + monthlyByWeekdayOrdinal);
                freCfg = obj.toString();
            }
        } else if (sendingFrequency.equals("custom")) {
            if (customDateCheckbox.equals("specific")) {
                obj.put("frequency", "custom1");
                obj.put("value", specificDateValue.replace("/", ""));
                freCfg = obj.toString();
            } else if (customDateCheckbox.equals("periodic")) {
                if (customDateFreqType.equals("weekly")) {
                    obj.put("frequency", "weekly");
                    obj.put("value", freqDaily);

                    obj2.put("frequency", "custom2");
                    obj2.put("value", obj);
                    obj2.put("rate", periodicNum);
                    freCfg = obj2.toString();
                } else if (customDateFreqType.equals("monthly")) {
                    if (monthlyByDayCb == 1) {
                        obj.put("frequency", "monthly1");
                        obj.put("value", dayInMount);

                        obj2.put("frequency", "custom2");
                        obj2.put("rate", periodicNum);
                        obj2.put("value", obj);
                        freCfg = obj2.toString();

                    } else if (monthlyByDayCb == 2) {
                        obj.put("frequency", "monthly2");
                        obj.put("value", monthlyByWeekday + monthlyByWeekdayOrdinal);

                        obj2.put("frequency", "custom2");
                        obj2.put("rate", periodicNum);
                        obj2.put("value", obj);
                        freCfg = obj2.toString();
                    }
                } else if (customDateFreqType.equals("daily")) {
                    obj.put("frequency", "daily");
                    obj.put("value", "");

                    obj1.put("frequency", "custom2");
                    obj1.put("rate", periodicNum);
                    obj1.put("value", obj);
                    freCfg = obj1.toString();
                }
            }
        }
        return freCfg;


    }

    public FrequencyCampaign saveFrequencyCampaign(Long campaignId, String campaignName, Integer expectedApprovalRate, String campaignTarget, String description, String startDate,
                                                   String endDate, String timeRange1Start, String timeRange1End, String timeRange2Start, String timeRange2End,
                                                   Long idTargetGroup, String freCfg, Integer typeTargetGroup, String jsonTargetGroup, Integer typeInputBlacklist,
                                                   Integer channel, String blacklistPathFile, String sendingTimeLimitChannel, String disableMessageLimit,
                                                   Long blacklistTargetGroupId, String subTargetGroupRadio, Integer isDuplicateMsisdn, Integer hasSubTargetGroup,
                                                   Long contentId, String pathFileDataCustomer, Long packageGroupId, String sql, Long accountSendingId, Long packageDataId, String originalFileNameDataCustomer, String originalFileNameBlacklist) {

        logger.info("=== save frequency campaign ===");
        UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        FrequencyCampaign frequencyCampaign;
        if (campaignId == null) {
            frequencyCampaign = new FrequencyCampaign();
        } else {
            frequencyCampaign = frequencyCampaignRepository.findById(campaignId).get();
        }
        frequencyCampaign.setCampaignName(campaignName);
        frequencyCampaign.setDescription(description);
        if (campaignId == null) {
            frequencyCampaign.setCreatedDate(new Date());
            frequencyCampaign.setCreatedUser(currentUser.getUsername());
        } else {
            frequencyCampaign.setUpdatedDate(new Date());
            frequencyCampaign.setUpdatedUser(currentUser.getUsername());
        }
        frequencyCampaign.setStatus(1);
        frequencyCampaign.setStartDate(AppUtils.convertStringToDate(startDate, "dd/MM/yyyy"));
        if (endDate != null) {
            frequencyCampaign.setEndDate((AppUtils.convertStringToDate(endDate, "dd/MM/yyyy")));
        }
        frequencyCampaign.setTimeRange1Start(timeRange1Start);
        frequencyCampaign.setTimeRange1End(timeRange1End);
        frequencyCampaign.setTimeRange2Start(timeRange2Start);
        frequencyCampaign.setTimeRange2End(timeRange2End);
        frequencyCampaign.setAcceptanceRate(expectedApprovalRate);
        frequencyCampaign.setBlackListPathFile(blacklistPathFile);
        frequencyCampaign.setIdTargetGroup(idTargetGroup);
        frequencyCampaign.setChannel(channel);
        frequencyCampaign.setCampaignTarget(campaignTarget);
        frequencyCampaign.setFreeCfg(freCfg);
        frequencyCampaign.setDataJson(jsonTargetGroup);
        frequencyCampaign.setTypeTargetGroup(typeTargetGroup);
        frequencyCampaign.setBlackListTargetGroupId(blacklistTargetGroupId);
        frequencyCampaign.setSubTargetGroupRadio(subTargetGroupRadio);
        frequencyCampaign.setIsDuplicateMsisdn(isDuplicateMsisdn);
        frequencyCampaign.setDisableMessageLimit(disableMessageLimit);
        frequencyCampaign.setSendingTimeLimitChannel(sendingTimeLimitChannel);
        frequencyCampaign.setHasSubTargetGroup(hasSubTargetGroup);
        frequencyCampaign.setTypeInputBlacklist(typeInputBlacklist);
        frequencyCampaign.setContentId(contentId);
        frequencyCampaign.setPathFileDataCustomer(pathFileDataCustomer);
        frequencyCampaign.setPackageGroupId(packageGroupId);
        frequencyCampaign.setSql(sql);
        frequencyCampaign.setAccountSendingId(accountSendingId);
        frequencyCampaign.setPackageDataId(packageDataId);
        frequencyCampaign.setOriginalNameFileBlacklist(originalFileNameBlacklist);
        frequencyCampaign.setOriginalNameFileDataCustomer(originalFileNameDataCustomer);

        FrequencyCampaign campaign = frequencyCampaignRepository.saveAndFlush(frequencyCampaign);
        return campaign;
    }

    public Optional<FrequencyCampaign> findById(Long id) {
        return frequencyCampaignRepository.findById(id);
    }

    public void updateMainGroupSizeAndRatioByCampaignId(Long campaignId, Long groupSize, Double ratio) {
        frequencyCampaignRepository.updateMainGroupSizeAndRatioByCampaignId(campaignId, groupSize, ratio);
    }

    public boolean checkDuplicateCampaignName(String campaignName, Long campaignId) {

        boolean result = true;
        List<Long> frequencyCampaign = campaignRepository.findByCampaignNameIgnoreCaseAndStatusNot(campaignName.trim(), StaticVar.CAMPAIGN_DELETE_STATUS);
        if (frequencyCampaign.size() > 0) {
            if (campaignId != null && frequencyCampaign.size() >= 1 && frequencyCampaign.get(0).equals(campaignId)) {
                result = true;
            } else {
                result = false;
            }
        }
        return result;
    }
}
