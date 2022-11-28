package com.vivas.campaignx.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivas.campaignx.common.AppException;
import com.vivas.campaignx.common.AppUtils;
import com.vivas.campaignx.common.StaticVar;
import com.vivas.campaignx.dto.*;
import com.vivas.campaignx.entity.*;
import com.vivas.campaignx.repository.MappingCriteriaRepository;
import com.vivas.campaignx.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("event-campaign")
public class EventCampaignController {

    protected final Logger logger = LogManager.getLogger(this.getClass().getName());

    @Autowired
    private EventCampaignService eventCampaignService;

    @Autowired
    private BigdataEventService bigdataEventService;

    @Autowired
    private EventConditionService eventConditionService;

    @Autowired
    private TargetGroupService targetGroupService;

    @Autowired
    private SubTargetGroupService subTargetGroupService;

    @Autowired
    private SmsContentService smsContentService;

    @Autowired
    private SendingAccountService sendingAccountService;

    @Autowired
    private MappingCriteriaRepository mappingCriteriaRepository;

    @Autowired
    private PackageDataService packageDataService;

    @Autowired
    private PermissionUtils permissionUtils;

    @PreAuthorize("hasAnyAuthority('create:campaign')")
    @GetMapping("/create")
    public String viewCreate(Model model) {
        List<TargetGroup> targetGroupList = targetGroupService.findAllByStatusAndType();
        model.addAttribute("targetGroupList", targetGroupList);
        return "event-campaign/create-mainpage";
    }

    @PreAuthorize("hasAnyAuthority('create:campaign')")
    @PostMapping("/create-new")
    public String submitCreate(@RequestParam HashMap<String, String> reqParams, RedirectAttributes redirectAttributes,
                               @RequestParam(required = false, name = "data-Customer") MultipartFile dataCustomer,
                               @RequestParam(required = false, name = "blacklist-file") MultipartFile dataBlackList,
                               @RequestParam(required = false, name = "sub-target-group-name") List<String> listSubTargetGroupName,
                               @RequestParam(required = false, name = "json-sub-target-group") List<String> listJsonSubTargetGroup,
                               @RequestParam(required = false, name = "sub-target-group-priority") List<Integer> listSubTargetPriority,
                               @RequestParam(required = false, name = "message-content") List<String> messageContent) {

        SimpleResponseDTO res = new SimpleResponseDTO();
        String result;
        try {
            String campaignName = reqParams.get("campaign-name");
            String expected = reqParams.get("expected-approval-rate");
            Integer expectedApprovalRate = null;
            if (expected != null && expected.length() != 0) {
                expectedApprovalRate = Integer.parseInt(reqParams.get("expected-approval-rate"));
            }
            String campaignTarget = reqParams.get("campaign-target");
            String description = reqParams.get("campaign-description");
            String startDate = reqParams.get("start-date");
            String endDate = reqParams.get("end-date");
            String timeRange1Start = reqParams.get("time-range-1-start");
            String timeRange1End = reqParams.get("time-range-1-end");
            String timeRange2Start = reqParams.get("time-range-2-start");
            String timeRange2End = reqParams.get("time-range-2-end");
            Long packageGroup = Long.parseLong(reqParams.get("package-group"));
            Integer typeTargetGroup = (Integer.parseInt(reqParams.get("type-target-group")));
            String jsonTargetGroup = reqParams.get("json-Target-Group");
            Integer typeInputBlacklist = Integer.parseInt(reqParams.get("type-input-blacklist"));
            String disablePolicySendingTimeLimit = reqParams.get("disable-policy-sending-time-limit");
            String disablePolicyMessageLimit = reqParams.get("disable-policy-message-limit");
            String eventId = reqParams.get("event-id");
            String eventCondition = reqParams.get("event-condition");
            String eventQueueName = reqParams.get("event-queue-name");
            String eventConditionRule = reqParams.get("event-condition-rule");
            Integer eventCycle = null;
            if (reqParams.get("event-cycle") != null && reqParams.get("event-cycle").length() > 0) {
                eventCycle = Integer.parseInt(reqParams.get("event-cycle"));
            }
            Integer eventLimitPerDay = null;
            if (reqParams.get("event-limit-per-day") != null && reqParams.get("event-limit-per-day").length() > 0) {
                eventLimitPerDay = Integer.parseInt(reqParams.get("event-limit-per-day"));
            }

            Long blacklistTargetGroupId = null;
            if (typeInputBlacklist == 2) {
                blacklistTargetGroupId = Long.parseLong(reqParams.get("blacklist-target-group-id"));
            }
            String subTargetGroupRadio = reqParams.get("sub-target-group-radio");
            String isDuplicateMsisdn = reqParams.get("is-duplicate-msisdn");
            Long inputTargetGroupId = null;
            if (typeTargetGroup == 6) {
                inputTargetGroupId = Long.parseLong(reqParams.get("input-target-group-id"));
            }
            long idCampaign = eventCampaignService.creatNewCampaign(campaignName, expectedApprovalRate, campaignTarget, description, startDate, endDate, timeRange1Start,
                    timeRange1End, timeRange2Start, timeRange2End, packageGroup, typeTargetGroup, jsonTargetGroup, typeInputBlacklist, messageContent, disablePolicySendingTimeLimit,
                    disablePolicyMessageLimit, dataCustomer, dataBlackList, listSubTargetGroupName, listJsonSubTargetGroup, listSubTargetPriority,
                    blacklistTargetGroupId, subTargetGroupRadio, isDuplicateMsisdn, inputTargetGroupId, eventId, eventCondition, eventQueueName, eventConditionRule,
                    eventCycle, eventLimitPerDay);
            res.setMessage("Thêm mới chiến dịch sự kiện thành công. Bạn muốn gửi phê duyệt chiến dịch?");
            res.setCode(AppUtils.successCodeCampaignCreated);
            res.setData(idCampaign + "_1");
            result = AppUtils.ObjectToJsonResponse(res);
            redirectAttributes.addFlashAttribute("result", result);
        } catch (Exception e) {
            Throwable rootcause = AppUtils.getrootcause(e);
            if (rootcause instanceof AppException) {
                AppException apex = (AppException) rootcause;
                res.setMessage(apex.getMessage());
                res.setCode(AppUtils.errorCode);
                result = AppUtils.ObjectToJsonResponse(res);
                redirectAttributes.addFlashAttribute("result", result);
            } else {
                logger.error("Error insert new frequency campaign: ", e);
                res.setMessage("Không thể kết nối tới máy chủ. Kiểm tra lại kết nối internet và thử lại!");
                res.setCode(AppUtils.errorCode);
                result = AppUtils.ObjectToJsonResponse(res);
                redirectAttributes.addFlashAttribute("result", result);
            }
        }
        return "redirect:/campaignManager";
    }

    @PreAuthorize("hasAnyAuthority('update:campaign')")
    @PostMapping("/update-event-campaign")
    public String updateEventCampaign(@RequestParam HashMap<String, String> reqParams, RedirectAttributes redirectAttributes,
                                      @RequestParam(required = false, name = "data-Customer") MultipartFile dataCustomer,
                                      @RequestParam(required = false, name = "blacklist-file") MultipartFile dataBlackList,
                                      @RequestParam(required = false, name = "sub-target-group-name") List<String> listSubTargetGroupName,
                                      @RequestParam(required = false, name = "json-sub-target-group") List<String> listJsonSubTargetGroup,
                                      @RequestParam(required = false, name = "sub-target-group-priority") List<Integer> listSubTargetPriority,
                                      @RequestParam(required = false, name = "freq-weekday-value") String freqDaily,
                                      @RequestParam(required = false, name = "message-content") List<String> messageContent) {

        SimpleResponseDTO res = new SimpleResponseDTO();
        String result;
        try {
            UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
            String currentUserName = reqParams.get("currentUser");
            String roleLogin = permissionUtils.getUserRole();
            if (!(currentUserName.equals(currentUser.getUsername())) && (!("Quản trị").equals(roleLogin) && !("Super Admin").equals(roleLogin))) {
                return "redirect:/accessDenied";
            }
            Long idEventCampaign = Long.parseLong(reqParams.get("id-frequency-campaign"));
            String campaignName = reqParams.get("campaign-name");
            String expected = reqParams.get("expected-approval-rate");
            Integer expectedApprovalRate = null;
            if (expected != null && expected.length() != 0) {
                expectedApprovalRate = Integer.parseInt(reqParams.get("expected-approval-rate"));
            }
            String campaignTarget = reqParams.get("campaign-target");
            String description = reqParams.get("campaign-description");
            String startDate = reqParams.get("start-date");
            String endDate = reqParams.get("end-date");
            String timeRange1Start = reqParams.get("time-range-1-start");
            String timeRange1End = reqParams.get("time-range-1-end");
            String timeRange2Start = reqParams.get("time-range-2-start");
            String timeRange2End = reqParams.get("time-range-2-end");
            Long packageGroup = Long.parseLong(reqParams.get("package-group"));
            Integer typeTargetGroup = (Integer.parseInt(reqParams.get("type-target-group")));
            String jsonTargetGroup = reqParams.get("json-Target-Group");
            Integer typeInputBlacklist = Integer.parseInt(reqParams.get("type-input-blacklist"));
            String disablePolicySendingTimeLimit = reqParams.get("disable-policy-sending-time-limit");
            String disablePolicyMessageLimit = reqParams.get("disable-policy-message-limit");
            String eventId = reqParams.get("event-id");
            String eventCondition = reqParams.get("event-condition");
            String eventQueueName = reqParams.get("event-queue-name");
            String eventConditionRule = reqParams.get("event-condition-rule");
            Integer eventCycle = null;
            if (reqParams.get("event-cycle") != null && reqParams.get("event-cycle").length() > 0) {
                eventCycle = Integer.parseInt(reqParams.get("event-cycle"));
            }
            Integer eventLimitPerDay = null;
            if (reqParams.get("event-limit-per-day") != null && reqParams.get("event-limit-per-day").length() > 0) {
                eventLimitPerDay = Integer.parseInt(reqParams.get("event-limit-per-day"));
            }


            Long blacklistTargetGroupId = null;
            if (typeInputBlacklist == 2) {
                blacklistTargetGroupId = Long.parseLong(reqParams.get("blacklist-target-group-id"));
            }
            String subTargetGroupRadio = reqParams.get("sub-target-group-radio");
            String isDuplicateMsisdn = reqParams.get("is-duplicate-msisdn");
            Long inputTargetGroupId = null;
            if (typeTargetGroup == 6) {
                inputTargetGroupId = Long.parseLong(reqParams.get("input-target-group-id"));
            }
            Integer hasSubOld = Integer.valueOf(reqParams.get("hasSubOld"));
            long idCampaign = eventCampaignService.updateCampaign(idEventCampaign, campaignName, expectedApprovalRate, campaignTarget, description, startDate, endDate, timeRange1Start,
                    timeRange1End, timeRange2Start, timeRange2End, packageGroup, typeTargetGroup, jsonTargetGroup, typeInputBlacklist, messageContent, disablePolicySendingTimeLimit,
                    disablePolicyMessageLimit, dataCustomer, dataBlackList, listSubTargetGroupName, listJsonSubTargetGroup, listSubTargetPriority, blacklistTargetGroupId,
                    subTargetGroupRadio, isDuplicateMsisdn, inputTargetGroupId, eventId, eventCondition, eventQueueName, eventConditionRule, eventCycle, eventLimitPerDay, hasSubOld);

            res.setMessage("Chỉnh sửa chiến dịch sự kiện thành công. Bạn muốn gửi phê duyệt chiến dịch?");
            res.setCode(AppUtils.successCodeCampaignCreated);
            res.setData(idCampaign + "_1");
            result = AppUtils.ObjectToJsonResponse(res);
            redirectAttributes.addFlashAttribute("result", result);

        } catch (Exception e) {
            Throwable rootcause = AppUtils.getrootcause(e);
            if (rootcause instanceof AppException) {
                AppException apex = (AppException) rootcause;
                res.setMessage(apex.getMessage());
                res.setCode(AppUtils.errorCode);
                result = AppUtils.ObjectToJsonResponse(res);
                redirectAttributes.addFlashAttribute("result", result);
            } else {
                logger.error("Error insert new event campaign: ", e);
                res.setMessage("Không thể kết nối tới máy chủ. Kiểm tra lại kết nối internet và thử lại!");
                res.setCode(AppUtils.errorCode);
                result = AppUtils.ObjectToJsonResponse(res);
                redirectAttributes.addFlashAttribute("result", result);
            }
        }
        return "redirect:/campaignManager";
    }

    @PreAuthorize("hasAnyAuthority('update:campaign')")
    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String editCampaign(Model model, @RequestParam(required = true) Long eventCampaignId, RedirectAttributes redirectAttributes) throws AppException {
        EventCampaign eventCampaign = new EventCampaign();
        List<SendingAccount> sendingAccounts = new ArrayList<SendingAccount>();
        List<SubTargetGroupDTO> subTargetGroups = new ArrayList<>();
        SmsContent smsContent = new SmsContent();
        SendingTimeLimitChannelDTO sendingTimeLimitChannelDTO = new SendingTimeLimitChannelDTO();
        DisableMessageLimitDTO disableMessageLimitDTO = new DisableMessageLimitDTO();
        SimpleResponseDTO res = new SimpleResponseDTO();
        String result;
        String roleLogin = permissionUtils.getUserRole();
        UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        Optional<EventCampaign> eventCampaignOptional = eventCampaignService.findById(eventCampaignId);
        sendingAccounts = sendingAccountService.findByStatus(StaticVar.SENDING_ACCOUNT_STATUS_ACTIVE);
        if (eventCampaignOptional.isPresent()) {
            eventCampaign = eventCampaignOptional.get();
            if (!(eventCampaign.getCreatedUser().equals(currentUser.getUsername())) && (!("Quản trị").equals(roleLogin) && !("Super Admin").equals(roleLogin))) {
                return "redirect:/accessDenied";
            }
            model.addAttribute("currentUser", currentUser.getUsername());
            EventDataDTO eventDataDTO = new EventDataDTO();
            BigdataEvent bigdataEvent = bigdataEventService.findById(eventCampaign.getEventId()).get();
            eventDataDTO.setEventId(eventCampaign.getEventId());
            eventDataDTO.setEventName(bigdataEvent.getEventName());
            eventDataDTO.setEventQueueName(bigdataEvent.getQueueName());
            List<EventCondition> eventConditions = eventConditionService.findByStatusAndBigdataEventId(1, eventCampaign.getEventId());
            eventDataDTO.setEventConditions(eventConditions);
            String strEventCondition = eventCampaign.getEventCondition();
            JSONArray jsonArrayEventCondition = new JSONArray(strEventCondition);
            List<String> eventConditionToViews = new ArrayList<>();
            for (int i = 0; i < jsonArrayEventCondition.length(); i++) {
                JSONObject eventCondition = jsonArrayEventCondition.getJSONObject(i);
                if (eventCondition.getString("conditionName").equals("amount") && (eventCampaign.getEventId().equals(2l))) {
                    //su kien tai khoan chính dưỡi ngưỡng, add value cho input riêng
                    model.addAttribute("lowBalanceAmount", eventCondition.getString("value"));
                } else {
                    String eventConditionToView = bigdataEventService.getViewEventCondition(eventDataDTO, eventCondition);
                    eventConditionToViews.add(eventConditionToView);
                }
            }

            if (eventCampaign.getPathFileDataCustomer() != null && eventCampaign.getPathFileDataCustomer().length() > 0) {
                model.addAttribute("fileMSISDN", true);
            } else model.addAttribute("fileMSISDN", false);

            if (eventCampaign.getBlackListPathFile() != null && eventCampaign.getBlackListPathFile().length() > 0) {
                model.addAttribute("fileBlacklist", true);
            } else model.addAttribute("fileBlacklist", false);
            List<TargetGroup> targetGroupList = targetGroupService.findAllByStatusAndType();
            model.addAttribute("targetGroupList", targetGroupList);
            if (eventCampaign.getIdTargetGroup() != null && eventCampaign.getTypeTargetGroup() == 6) {
                Optional<TargetGroup> targetGroup = targetGroupService.findById(eventCampaign.getIdTargetGroup());
                EventCampaign eventCampaignFinal = eventCampaign;
                targetGroup.ifPresent(x->{
                    if(x.getStatus() != -99){
                        model.addAttribute("idTargetGroup", eventCampaignFinal.getIdTargetGroup());
                        model.addAttribute("hasDeleteTargetGroup", 0);
                    }else {
                        model.addAttribute("idTargetGroup", targetGroupList.get(0).getId());
                        model.addAttribute("hasDeleteTargetGroup", 1);
                    }
                });
            }
            if (eventCampaign.getBlackListTargetGroupId() != null && eventCampaign.getTypeTargetGroup() == 6) {
                Optional<TargetGroup> targetGroup = targetGroupService.findById(eventCampaign.getBlackListTargetGroupId());
                EventCampaign eventCampaignFinal = eventCampaign;
                targetGroup.ifPresent(x->{
                    if(x.getStatus() != -99){
                        model.addAttribute("blacklistTargetGroupId", eventCampaignFinal.getBlackListTargetGroupId());
                        model.addAttribute("hasDeleteTargetGroupBL", 0);
                    }else {
                        model.addAttribute("blacklistTargetGroupId", targetGroupList.get(0).getId());
                        model.addAttribute("hasDeleteTargetGroupBL", 1);
                    }
                });
            }
            if (eventCampaign.getHasSubTargetGroup() == 1) {
                List<SubTargetGroup> subTargetGroup = subTargetGroupService.findByCampaignId(eventCampaign.getCampaignId());
                for (int i = 0; i < subTargetGroup.size(); i++) {
                    SmsContent smsContent1 = smsContentService.findById(subTargetGroup.get(i).getContentId()).get();
                    SmsContentDTO smsContentDTO = new SmsContentDTO();
                    smsContentDTO.setMessageContent(smsContent1.getMessage());
                    smsContentDTO.setCountMT(smsContent1.getMtCount());
                    smsContentDTO.setSendingAccount(subTargetGroup.get(i).getAccountSendingId());
                    smsContentDTO.setChannelMarketing(subTargetGroup.get(i).getChannel());
                    smsContentDTO.setUnicode(smsContent1.getUnicode());
                    smsContentDTO.setProductPackage(subTargetGroup.get(i).getPackageDataId());
                    smsContentDTO.setJsonSmsContentDTO(AppUtils.objectToJson(smsContentDTO));

                    SubTargetGroupDTO subTargetGroupDTO = new SubTargetGroupDTO();
                    subTargetGroupDTO.setSubTargetGroup(subTargetGroup.get(i));
                    subTargetGroupDTO.setSmsContentDTO(smsContentDTO);
                    subTargetGroups.add(subTargetGroupDTO);
                }
            } else {
                if (eventCampaign.getContentId() != null) {
                    Optional<SmsContent> smsContentOptional = smsContentService.findById(eventCampaign.getContentId());
                    if (smsContentOptional.isPresent()) {
                        smsContent = smsContentOptional.get();
                    }
                }
            }
            if (eventCampaign.getSendingTimeLimitChannel() != null) {
                sendingTimeLimitChannelDTO.setCheckSendingTimeLimitChannel(true);
                JSONObject jsonSendingTime = new JSONObject(eventCampaign.getSendingTimeLimitChannel());
                sendingTimeLimitChannelDTO.setZalo(jsonSendingTime.getBoolean("zalo"));
                sendingTimeLimitChannelDTO.setDigilife(jsonSendingTime.getBoolean("digilife"));
                sendingTimeLimitChannelDTO.setEmail(jsonSendingTime.getBoolean("email"));
            }

            if (eventCampaign.getDisableMessageLimit() != null) {
                disableMessageLimitDTO.setCheckDisableMessageLimitChannel(true);
                JSONObject jsonDisableMessage = new JSONObject(eventCampaign.getDisableMessageLimit());
                disableMessageLimitDTO.setZalo(jsonDisableMessage.getBoolean("zalo"));
                disableMessageLimitDTO.setDigilife(jsonDisableMessage.getBoolean("digilife"));
                disableMessageLimitDTO.setEmail(jsonDisableMessage.getBoolean("email"));
            }

            if (eventCampaign.getTypeTargetGroup() != 3) {
                Optional<TargetGroup> targetGroupOptional = targetGroupService.findById(eventCampaign.getIdTargetGroup());
                TargetGroup targetGroup = new TargetGroup();
                if (targetGroupOptional.isPresent()) {
                    targetGroup = targetGroupOptional.get();
                    List<MappingCriteria> mappingCriteriaList = mappingCriteriaRepository.findAllByIdTargetGroupOrderByLevelCriteriaAscPositionAsc(eventCampaign.getIdTargetGroup());
                    model.addAttribute("mappingCriteriaList", mappingCriteriaList);
                    if (mappingCriteriaList.size() > 0) {
                        model.addAttribute("typeLV1", mappingCriteriaList.get(0).getType());
                    }
                    model.addAttribute("targetGroup", targetGroup);
                }
            }
            List<PackageNameDTO> listPackage = packageDataService.findAllByStatusAndPackageGroupOrderByPackageName(1, eventCampaign.getPackageGroupId().intValue());
            model.addAttribute("eventCampaign", eventCampaign);
            model.addAttribute("startDate", AppUtils.convertDateToString(eventCampaign.getStartDate(), "dd/MM/yyyy"));
            model.addAttribute("endDate", AppUtils.convertDateToString(eventCampaign.getEndDate(), "dd/MM/yyyy"));
            model.addAttribute("listPackage", listPackage);
            if (eventCampaign.getHasSubTargetGroup() == 0) {
                SmsContentDTO smsContentDTO = new SmsContentDTO();
                smsContentDTO.setMessageContent(smsContent.getMessage());
                smsContentDTO.setCountMT(smsContent.getMtCount());
                smsContentDTO.setSendingAccount(eventCampaign.getAccountSendingId());
                smsContentDTO.setChannelMarketing(eventCampaign.getChannel());
                smsContentDTO.setUnicode(smsContent.getUnicode());
                smsContentDTO.setProductPackage(eventCampaign.getPackageDataId());
                smsContentDTO.setJsonSmsContentDTO(AppUtils.objectToJson(smsContentDTO));

                model.addAttribute("smsContent", smsContentDTO);
            } else {
                SmsContentDTO smsContentDTO = new SmsContentDTO();
                smsContentDTO.setMessageContent(" ");
                model.addAttribute("smsContent", smsContentDTO);
            }
            model.addAttribute("sendingAccounts", sendingAccounts);
            model.addAttribute("sendingTimeLimitChannelDTO", sendingTimeLimitChannelDTO);
            model.addAttribute("disableMessageLimitDTO", disableMessageLimitDTO);
            model.addAttribute("eventConditionToViews", eventConditionToViews);
            model.addAttribute("eventDataDTO", eventDataDTO);

        } else {
            res.setMessage("Không tìm thấy thông tin chiến dịch.");
            res.setCode(AppUtils.errorCode);
            result = AppUtils.ObjectToJsonResponse(res);
            model.addAttribute("result", result);
        }
        model.addAttribute("subTargetGroups", subTargetGroups);
        return "event-campaign/edit/edit-mainpage";
    }

    @PreAuthorize("hasAnyAuthority('copy:campaign')")
    @RequestMapping(value = "/copy", method = RequestMethod.GET)
    public String copyCampaign(Model model, @RequestParam(required = true) Long eventCampaignId) throws AppException {

        EventCampaign eventCampaign = new EventCampaign();
        List<SendingAccount> sendingAccounts = new ArrayList<SendingAccount>();
        List<SubTargetGroupDTO> subTargetGroups = new ArrayList<>();
        SmsContent smsContent = new SmsContent();
        SendingTimeLimitChannelDTO sendingTimeLimitChannelDTO = new SendingTimeLimitChannelDTO();
        DisableMessageLimitDTO disableMessageLimitDTO = new DisableMessageLimitDTO();
        SimpleResponseDTO res = new SimpleResponseDTO();
        String result;
        String roleLogin = permissionUtils.getUserRole();
        UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        Optional<EventCampaign> eventCampaignOptional = eventCampaignService.findById(eventCampaignId);
        sendingAccounts = sendingAccountService.findByStatus(StaticVar.SENDING_ACCOUNT_STATUS_ACTIVE);
        if (eventCampaignOptional.isPresent()) {
            eventCampaign = eventCampaignOptional.get();
            model.addAttribute("currentUser", currentUser.getUsername());
            EventDataDTO eventDataDTO = new EventDataDTO();
            BigdataEvent bigdataEvent = bigdataEventService.findById(eventCampaign.getEventId()).get();
            eventDataDTO.setEventId(eventCampaign.getEventId());
            eventDataDTO.setEventName(bigdataEvent.getEventName());
            eventDataDTO.setEventQueueName(bigdataEvent.getQueueName());
            List<EventCondition> eventConditions = eventConditionService.findByStatusAndBigdataEventId(1, eventCampaign.getEventId());
            eventDataDTO.setEventConditions(eventConditions);
            String strEventCondition = eventCampaign.getEventCondition();
            JSONArray jsonArrayEventCondition = new JSONArray(strEventCondition);
            List<String> eventConditionToViews = new ArrayList<>();
            for (int i = 0; i < jsonArrayEventCondition.length(); i++) {
                JSONObject eventCondition = jsonArrayEventCondition.getJSONObject(i);
                if (eventCondition.getString("conditionName").equals("amount") && (eventCampaign.getEventId().equals(2l))) {
                    //su kien tai khoan chính dưỡi ngưỡng, add value cho input riêng
                    model.addAttribute("lowBalanceAmount", eventCondition.getString("value"));
                } else {
                    String eventConditionToView = bigdataEventService.getViewEventCondition(eventDataDTO, eventCondition);
                    eventConditionToViews.add(eventConditionToView);
                }
            }

            if (eventCampaign.getPathFileDataCustomer() != null && eventCampaign.getPathFileDataCustomer().length() > 0) {
                model.addAttribute("fileMSISDN", true);
            } else model.addAttribute("fileMSISDN", false);

            if (eventCampaign.getBlackListPathFile() != null && eventCampaign.getBlackListPathFile().length() > 0) {
                model.addAttribute("fileBlacklist", true);
            } else model.addAttribute("fileBlacklist", false);
            List<TargetGroup> targetGroupList = targetGroupService.findAllByStatusAndType();
            model.addAttribute("targetGroupList", targetGroupList);
            if (eventCampaign.getIdTargetGroup() != null && eventCampaign.getTypeTargetGroup() == 6) {
                Optional<TargetGroup> targetGroup = targetGroupService.findById(eventCampaign.getIdTargetGroup());
                EventCampaign eventCampaignFinal = eventCampaign;
                targetGroup.ifPresent(x->{
                    if(x.getStatus() != -99){
                        model.addAttribute("idTargetGroup", eventCampaignFinal.getIdTargetGroup());
                        model.addAttribute("hasDeleteTargetGroup", 0);
                    }else {
                        model.addAttribute("idTargetGroup", targetGroupList.get(0).getId());
                        model.addAttribute("hasDeleteTargetGroup", 1);
                    }
                });
            }
            if (eventCampaign.getBlackListTargetGroupId() != null && eventCampaign.getTypeTargetGroup() == 6) {
                Optional<TargetGroup> targetGroup = targetGroupService.findById(eventCampaign.getBlackListTargetGroupId());
                EventCampaign eventCampaignFinal = eventCampaign;
                targetGroup.ifPresent(x->{
                    if(x.getStatus() != -99){
                        model.addAttribute("blacklistTargetGroupId", eventCampaignFinal.getBlackListTargetGroupId());
                        model.addAttribute("hasDeleteTargetGroupBL", 0);
                    }else {
                        model.addAttribute("blacklistTargetGroupId", targetGroupList.get(0).getId());
                        model.addAttribute("hasDeleteTargetGroupBL", 1);
                    }
                });
            }
            if (eventCampaign.getHasSubTargetGroup() == 1) {
                List<SubTargetGroup> subTargetGroup = subTargetGroupService.findByCampaignId(eventCampaign.getCampaignId());
                for (int i = 0; i < subTargetGroup.size(); i++) {
                    SmsContent smsContent1 = smsContentService.findById(subTargetGroup.get(i).getContentId()).get();
                    SmsContentDTO smsContentDTO = new SmsContentDTO();
                    smsContentDTO.setMessageContent(smsContent1.getMessage());
                    smsContentDTO.setCountMT(smsContent1.getMtCount());
                    smsContentDTO.setSendingAccount(subTargetGroup.get(i).getAccountSendingId());
                    smsContentDTO.setChannelMarketing(subTargetGroup.get(i).getChannel());
                    smsContentDTO.setUnicode(smsContent1.getUnicode());
                    smsContentDTO.setProductPackage(subTargetGroup.get(i).getPackageDataId());
                    smsContentDTO.setJsonSmsContentDTO(AppUtils.objectToJson(smsContentDTO));

                    SubTargetGroupDTO subTargetGroupDTO = new SubTargetGroupDTO();
                    subTargetGroupDTO.setSubTargetGroup(subTargetGroup.get(i));
                    subTargetGroupDTO.setSmsContentDTO(smsContentDTO);
                    subTargetGroups.add(subTargetGroupDTO);
                }
            } else {
                if (eventCampaign.getContentId() != null) {
                    Optional<SmsContent> smsContentOptional = smsContentService.findById(eventCampaign.getContentId());
                    if (smsContentOptional.isPresent()) {
                        smsContent = smsContentOptional.get();
                    }
                }
            }
            if (eventCampaign.getSendingTimeLimitChannel() != null) {
                sendingTimeLimitChannelDTO.setCheckSendingTimeLimitChannel(true);
                JSONObject jsonSendingTime = new JSONObject(eventCampaign.getSendingTimeLimitChannel());
                sendingTimeLimitChannelDTO.setZalo(jsonSendingTime.getBoolean("zalo"));
                sendingTimeLimitChannelDTO.setDigilife(jsonSendingTime.getBoolean("digilife"));
                sendingTimeLimitChannelDTO.setEmail(jsonSendingTime.getBoolean("email"));
            }

            if (eventCampaign.getDisableMessageLimit() != null) {
                disableMessageLimitDTO.setCheckDisableMessageLimitChannel(true);
                JSONObject jsonDisableMessage = new JSONObject(eventCampaign.getDisableMessageLimit());
                disableMessageLimitDTO.setZalo(jsonDisableMessage.getBoolean("zalo"));
                disableMessageLimitDTO.setDigilife(jsonDisableMessage.getBoolean("digilife"));
                disableMessageLimitDTO.setEmail(jsonDisableMessage.getBoolean("email"));
            }

            if (eventCampaign.getTypeTargetGroup() != 3) {
                Optional<TargetGroup> targetGroupOptional = targetGroupService.findById(eventCampaign.getIdTargetGroup());
                TargetGroup targetGroup = new TargetGroup();
                if (targetGroupOptional.isPresent()) {
                    targetGroup = targetGroupOptional.get();
                    List<MappingCriteria> mappingCriteriaList = mappingCriteriaRepository.findAllByIdTargetGroupOrderByLevelCriteriaAscPositionAsc(eventCampaign.getIdTargetGroup());
                    model.addAttribute("mappingCriteriaList", mappingCriteriaList);
                    if (mappingCriteriaList.size() > 0) {
                        model.addAttribute("typeLV1", mappingCriteriaList.get(0).getType());
                    }
                    model.addAttribute("targetGroup", targetGroup);
                }
            }
            List<PackageNameDTO> listPackage = packageDataService.findAllByStatusAndPackageGroupOrderByPackageName(1, eventCampaign.getPackageGroupId().intValue());
            model.addAttribute("eventCampaign", eventCampaign);
            model.addAttribute("startDate", AppUtils.convertDateToString(eventCampaign.getStartDate(), "dd/MM/yyyy"));
            model.addAttribute("endDate", AppUtils.convertDateToString(eventCampaign.getEndDate(), "dd/MM/yyyy"));
            model.addAttribute("listPackage", listPackage);
            if (eventCampaign.getHasSubTargetGroup() == 0) {
                SmsContentDTO smsContentDTO = new SmsContentDTO();
                smsContentDTO.setMessageContent(smsContent.getMessage());
                smsContentDTO.setCountMT(smsContent.getMtCount());
                smsContentDTO.setSendingAccount(eventCampaign.getAccountSendingId());
                smsContentDTO.setChannelMarketing(eventCampaign.getChannel());
                smsContentDTO.setUnicode(smsContent.getUnicode());
                smsContentDTO.setProductPackage(eventCampaign.getPackageDataId());
                smsContentDTO.setJsonSmsContentDTO(AppUtils.objectToJson(smsContentDTO));

                model.addAttribute("smsContent", smsContentDTO);
            } else {
                SmsContentDTO smsContentDTO = new SmsContentDTO();
                smsContentDTO.setMessageContent(" ");
                model.addAttribute("smsContent", smsContentDTO);
            }
            model.addAttribute("sendingAccounts", sendingAccounts);
            model.addAttribute("sendingTimeLimitChannelDTO", sendingTimeLimitChannelDTO);
            model.addAttribute("disableMessageLimitDTO", disableMessageLimitDTO);
            model.addAttribute("eventConditionToViews", eventConditionToViews);
            model.addAttribute("eventDataDTO", eventDataDTO);

        } else {
            res.setMessage("Không tìm thấy thông tin chiến dịch.");
            res.setCode(AppUtils.errorCode);
            result = AppUtils.ObjectToJsonResponse(res);
            model.addAttribute("result", result);
        }
        model.addAttribute("subTargetGroups", subTargetGroups);
        return "event-campaign/copy/copy-mainpage";
    }

    @PreAuthorize("hasAnyAuthority('copy:campaign')")
    @PostMapping("/copy-new")
    public String submitCopy(@RequestParam HashMap<String, String> reqParams, RedirectAttributes redirectAttributes,
                             @RequestParam(required = false, name = "data-Customer") MultipartFile dataCustomer,
                             @RequestParam(required = false, name = "blacklist-file") MultipartFile dataBlackList,
                             @RequestParam(required = false, name = "sub-target-group-name") List<String> listSubTargetGroupName,
                             @RequestParam(required = false, name = "json-sub-target-group") List<String> listJsonSubTargetGroup,
                             @RequestParam(required = false, name = "sub-target-group-priority") List<Integer> listSubTargetPriority,
                             @RequestParam(required = false, name = "message-content") List<String> messageContent) {

        SimpleResponseDTO res = new SimpleResponseDTO();
        String result;
        try {
            String campaignName = reqParams.get("campaign-name");
            String expected = reqParams.get("expected-approval-rate");
            Integer expectedApprovalRate = null;
            if (expected != null && expected.length() != 0) {
                expectedApprovalRate = Integer.parseInt(reqParams.get("expected-approval-rate"));
            }
            Long idEventCampaign = Long.parseLong(reqParams.get("id-frequency-campaign"));
            String campaignTarget = reqParams.get("campaign-target");
            String description = reqParams.get("campaign-description");
            String startDate = reqParams.get("start-date");
            String endDate = reqParams.get("end-date");
            String timeRange1Start = reqParams.get("time-range-1-start");
            String timeRange1End = reqParams.get("time-range-1-end");
            String timeRange2Start = reqParams.get("time-range-2-start");
            String timeRange2End = reqParams.get("time-range-2-end");
            Long packageGroup = Long.parseLong(reqParams.get("package-group"));
            Integer typeTargetGroup = (Integer.parseInt(reqParams.get("type-target-group")));
            String jsonTargetGroup = reqParams.get("json-Target-Group");
            Integer typeInputBlacklist = Integer.parseInt(reqParams.get("type-input-blacklist"));
            String disablePolicySendingTimeLimit = reqParams.get("disable-policy-sending-time-limit");
            String disablePolicyMessageLimit = reqParams.get("disable-policy-message-limit");
            String eventId = reqParams.get("event-id");
            String eventCondition = reqParams.get("event-condition");
            String eventQueueName = reqParams.get("event-queue-name");
            String eventConditionRule = reqParams.get("event-condition-rule");
            Integer eventCycle = null;
            if (reqParams.get("event-cycle") != null && reqParams.get("event-cycle").length() > 0) {
                eventCycle = Integer.parseInt(reqParams.get("event-cycle"));
            }
            Integer eventLimitPerDay = null;
            if (reqParams.get("event-limit-per-day") != null && reqParams.get("event-limit-per-day").length() > 0) {
                eventLimitPerDay = Integer.parseInt(reqParams.get("event-limit-per-day"));
            }

            Long blacklistTargetGroupId = null;
            if (typeInputBlacklist == 2) {
                blacklistTargetGroupId = Long.parseLong(reqParams.get("blacklist-target-group-id"));
            }
            String subTargetGroupRadio = reqParams.get("sub-target-group-radio");
            String isDuplicateMsisdn = reqParams.get("is-duplicate-msisdn");
            Long inputTargetGroupId = null;
            if (typeTargetGroup == 6) {
                inputTargetGroupId = Long.parseLong(reqParams.get("input-target-group-id"));
            }
            long idCampaign = eventCampaignService.copyNewCampaign(idEventCampaign, campaignName, expectedApprovalRate, campaignTarget, description, startDate, endDate, timeRange1Start,
                    timeRange1End, timeRange2Start, timeRange2End, packageGroup, typeTargetGroup, jsonTargetGroup, typeInputBlacklist, messageContent, disablePolicySendingTimeLimit,
                    disablePolicyMessageLimit, dataCustomer, dataBlackList, listSubTargetGroupName, listJsonSubTargetGroup, listSubTargetPriority,
                    blacklistTargetGroupId, subTargetGroupRadio, isDuplicateMsisdn, inputTargetGroupId, eventId, eventCondition, eventQueueName, eventConditionRule,
                    eventCycle, eventLimitPerDay);
            res.setMessage("Thêm mới chiến dịch sự kiện thành công. Bạn muốn gửi phê duyệt chiến dịch?");
            res.setCode(AppUtils.successCodeCampaignCreated);
            res.setData(idCampaign + "_1");
            result = AppUtils.ObjectToJsonResponse(res);
            redirectAttributes.addFlashAttribute("result", result);
        } catch (Exception e) {
            Throwable rootcause = AppUtils.getrootcause(e);
            if (rootcause instanceof AppException) {
                AppException apex = (AppException) rootcause;
                res.setMessage(apex.getMessage());
                res.setCode(AppUtils.errorCode);
                result = AppUtils.ObjectToJsonResponse(res);
                redirectAttributes.addFlashAttribute("result", result);
            } else {
                logger.error("Error insert new frequency campaign: ", e);
                res.setMessage("Không thể kết nối tới máy chủ. Kiểm tra lại kết nối internet và thử lại!");
                res.setCode(AppUtils.errorCode);
                result = AppUtils.ObjectToJsonResponse(res);
                redirectAttributes.addFlashAttribute("result", result);
            }
        }
        return "redirect:/campaignManager";
    }

    @RequestMapping(value = "/check-duplicate-name", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String checkScenarioName(@RequestParam("campaignName") String campaignName, @RequestParam(name = "campaignId", required = false) Long campaignId) throws JsonProcessingException {
        Map<String, Object> map = new HashMap<String, Object>();
        logger.info("Request check duplicate scenarioName: " + campaignName + " --- " + campaignName);
        boolean result = eventCampaignService.checkDuplicateCampaignName(campaignName, campaignId);
        map.put("result", result);
        String jsonJackson = new ObjectMapper().writeValueAsString(map);
        return jsonJackson;
    }
}
