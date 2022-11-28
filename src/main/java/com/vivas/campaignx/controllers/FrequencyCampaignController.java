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
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.*;

@Controller
@RequestMapping("frequency-campaign")
public class FrequencyCampaignController {

    protected final Logger logger = LogManager.getLogger(this.getClass().getName());

    @Autowired
    private FrequencyCampaignService frequencyCampaignService;

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
    public FrequencyCampaignController() {
    }

    @Autowired
    private PermissionUtils permissionUtils;

    @Value("${default_page_size}")
    private Integer defaultPageSize;

    @Value("${path_blacklist_template}")
    private String pathTemplateBlacklist;

    @PreAuthorize("hasAnyAuthority('create:campaign')")
    @GetMapping("/create")
    public String viewCreate(Model model) {
        List<TargetGroup> targetGroupList = targetGroupService.findAllByStatusAndType();
        model.addAttribute("targetGroupList", targetGroupList);
        return "frequency-campaign/create-mainpage";
    }

    @PreAuthorize("hasAnyAuthority('create:campaign')")
    @PostMapping("/create-new")
    public String createNewFrequencyCampaign(@RequestParam HashMap<String, String> reqParams, RedirectAttributes redirectAttributes,
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
            String sendingFrequency = reqParams.get("sending-frequency");
            Long packageGroup = Long.parseLong(reqParams.get("package-group"));
            Integer monthlyByDayCb = Integer.parseInt(reqParams.get("monthly-by-day-checkbox"));
            String monthlyByWeekday = reqParams.get("monthly-by-weekday");
            String monthlyByWeekdayOrdinal = reqParams.get("monthly-by-weekday-ordinal");
            String specificDateValue = reqParams.get("specific-date-value");
            String customDateFreqType = reqParams.get("custom-date-freq-type");
            Integer typeTargetGroup = (Integer.parseInt(reqParams.get("type-target-group")));
            String jsonTargetGroup = reqParams.get("json-Target-Group");
            Integer typeInputBlacklist = Integer.parseInt(reqParams.get("type-input-blacklist"));
            String disablePolicySendingTimeLimit = reqParams.get("disable-policy-sending-time-limit");
            String disablePolicyMessageLimit = reqParams.get("disable-policy-message-limit");

            Integer dayInMount = null;
            if (sendingFrequency.equals("monthly") || customDateFreqType.equals("monthly")) {
                if (monthlyByDayCb == 1) {
                    dayInMount = Integer.parseInt(reqParams.get("day-in-month"));
                }
            }
            String customDateCheckbox = reqParams.get("custom-date-checkbox");
            Integer periodicNum = null;
            if (customDateCheckbox.equals("periodic")) {
                periodicNum = Integer.parseInt(reqParams.get("periodic-number"));
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
            logger.info("--- json sub size: " + listJsonSubTargetGroup.size());
            logger.info("--- message content size: " + messageContent.size());
            long idCampaign = frequencyCampaignService.creatNewCampaign(campaignName, expectedApprovalRate, campaignTarget, description, startDate, endDate, timeRange1Start,
                    timeRange1End, timeRange2Start, timeRange2End, sendingFrequency, packageGroup, monthlyByDayCb, monthlyByWeekday, monthlyByWeekdayOrdinal,
                    specificDateValue, customDateFreqType, typeTargetGroup, jsonTargetGroup, typeInputBlacklist, messageContent, disablePolicySendingTimeLimit,
                    disablePolicyMessageLimit, dataCustomer, dataBlackList, listSubTargetGroupName, listJsonSubTargetGroup, listSubTargetPriority, freqDaily,
                    dayInMount, customDateCheckbox, periodicNum, blacklistTargetGroupId, subTargetGroupRadio, isDuplicateMsisdn, inputTargetGroupId);
            res.setMessage("Thêm mới chiến dịch tần suất thành công. Bạn muốn gửi phê duyệt chiến dịch?");
            res.setCode(AppUtils.successCodeCampaignCreated);
            res.setData(idCampaign + "_2");
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

    @PreAuthorize("hasAnyAuthority('copy:campaign')")
    @PostMapping("/copy-new")
    public String copyNewFrequencyCampaign(@RequestParam HashMap<String, String> reqParams, RedirectAttributes redirectAttributes,
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
            Long idFrequencyCampaign = Long.parseLong(reqParams.get("id-frequency-campaign"));
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
            String sendingFrequency = reqParams.get("sending-frequency");
            Long packageGroup = Long.parseLong(reqParams.get("package-group"));
            Integer monthlyByDayCb = 1;
            if (reqParams.containsKey("monthly-by-day-checkbox") && reqParams.get("monthly-by-day-checkbox") != null) {
                monthlyByDayCb = Integer.parseInt(reqParams.get("monthly-by-day-checkbox"));
            }
            String monthlyByWeekday = reqParams.get("monthly-by-weekday");
            String monthlyByWeekdayOrdinal = reqParams.get("monthly-by-weekday-ordinal");
            String specificDateValue = reqParams.get("specific-date-value");
            String customDateFreqType = reqParams.get("custom-date-freq-type");
            Integer typeTargetGroup = (Integer.parseInt(reqParams.get("type-target-group")));
            String jsonTargetGroup = reqParams.get("json-Target-Group");
            Integer typeInputBlacklist = Integer.parseInt(reqParams.get("type-input-blacklist"));
            String disablePolicySendingTimeLimit = reqParams.get("disable-policy-sending-time-limit");
            String disablePolicyMessageLimit = reqParams.get("disable-policy-message-limit");

            Integer dayInMount = null;
            if (sendingFrequency.equals("monthly") || customDateFreqType.equals("monthly")) {
                if (monthlyByDayCb == 1) {
                    dayInMount = Integer.parseInt(reqParams.get("day-in-month"));
                }
            }
            String customDateCheckbox = reqParams.get("custom-date-checkbox");
            Integer periodicNum = null;
            if (customDateCheckbox.equals("periodic")) {
                periodicNum = Integer.parseInt(reqParams.get("periodic-number"));
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
            logger.info("--- json sub size: " + listJsonSubTargetGroup.size());
            logger.info("--- message content size: " + messageContent.size());
            long idCampaign = frequencyCampaignService.copyNewCampaign(idFrequencyCampaign, campaignName, expectedApprovalRate, campaignTarget, description, startDate, endDate, timeRange1Start,
                    timeRange1End, timeRange2Start, timeRange2End, sendingFrequency, packageGroup, monthlyByDayCb, monthlyByWeekday, monthlyByWeekdayOrdinal,
                    specificDateValue, customDateFreqType, typeTargetGroup, jsonTargetGroup, typeInputBlacklist, messageContent, disablePolicySendingTimeLimit,
                    disablePolicyMessageLimit, dataCustomer, dataBlackList, listSubTargetGroupName, listJsonSubTargetGroup, listSubTargetPriority, freqDaily,
                    dayInMount, customDateCheckbox, periodicNum, blacklistTargetGroupId, subTargetGroupRadio, isDuplicateMsisdn, inputTargetGroupId);

            res.setMessage("Thêm mới chiến dịch tần suất thành công. Bạn muốn gửi phê duyệt chiến dịch?");
            res.setCode(AppUtils.successCodeCampaignCreated);
            res.setData(idCampaign + "_2");
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
    @PostMapping("/update-frequency-campaign")
    public String updateFrequencyCampaign(@RequestParam HashMap<String, String> reqParams, RedirectAttributes redirectAttributes,
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
            Long idFrequencyCampaign = Long.parseLong(reqParams.get("id-frequency-campaign"));
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
            String sendingFrequency = reqParams.get("sending-frequency");
            Long packageGroup = Long.parseLong(reqParams.get("package-group"));
            Integer monthlyByDayCb = 1;
            if (reqParams.containsKey("monthly-by-day-checkbox") && reqParams.get("monthly-by-day-checkbox") != null) {
                monthlyByDayCb = Integer.parseInt(reqParams.get("monthly-by-day-checkbox"));
            }

            String monthlyByWeekday = reqParams.get("monthly-by-weekday");
            String monthlyByWeekdayOrdinal = reqParams.get("monthly-by-weekday-ordinal");
            String specificDateValue = reqParams.get("specific-date-value");
            String customDateFreqType = reqParams.get("custom-date-freq-type");
            Integer typeTargetGroup = (Integer.parseInt(reqParams.get("type-target-group")));
            String jsonTargetGroup = reqParams.get("json-Target-Group");
            Integer typeInputBlacklist = Integer.parseInt(reqParams.get("type-input-blacklist"));
            String disablePolicySendingTimeLimit = reqParams.get("disable-policy-sending-time-limit");
            String disablePolicyMessageLimit = reqParams.get("disable-policy-message-limit");

            Integer dayInMount = null;
            if (sendingFrequency.equals("monthly") || customDateFreqType.equals("monthly")) {
                if (monthlyByDayCb == 1) {
                    dayInMount = Integer.parseInt(reqParams.get("day-in-month"));
                }
            }
            String customDateCheckbox = reqParams.get("custom-date-checkbox");
            Integer periodicNum = null;
            if (!AppUtils.isStringNullOrEmpty(customDateCheckbox)) {
                if (customDateCheckbox.equals("periodic")) {
                    periodicNum = Integer.parseInt(reqParams.get("periodic-number"));
                }
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
            long idCampaign = frequencyCampaignService.updateCampaign(idFrequencyCampaign, campaignName, expectedApprovalRate, campaignTarget, description, startDate, endDate, timeRange1Start,
                    timeRange1End, timeRange2Start, timeRange2End, sendingFrequency, packageGroup, monthlyByDayCb, monthlyByWeekday, monthlyByWeekdayOrdinal,
                    specificDateValue, customDateFreqType, typeTargetGroup, jsonTargetGroup, typeInputBlacklist, messageContent, disablePolicySendingTimeLimit,
                    disablePolicyMessageLimit, dataCustomer, dataBlackList, listSubTargetGroupName, listJsonSubTargetGroup, listSubTargetPriority,
                    freqDaily, dayInMount, customDateCheckbox, periodicNum, blacklistTargetGroupId, subTargetGroupRadio, isDuplicateMsisdn, inputTargetGroupId, hasSubOld);

            res.setMessage("Chỉnh sửa chiến dịch tần suất thành công. Bạn muốn gửi phê duyệt chiến dịch?");
            res.setCode(AppUtils.successCodeCampaignCreated);
            res.setData(idCampaign + "_2");
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

    @GetMapping("/check-campaignName")
    public @ResponseBody
    Map<String, Object> checkCampaignName(@RequestParam("campaignName") String campaignName, Model model) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            Optional<FrequencyCampaign> frequencyCampaign = frequencyCampaignService.findCampaignByNameCaseSensitive(campaignName.toLowerCase());
            if (frequencyCampaign.isPresent()) {
                map.put("result", true);
            } else {
                map.put("result", false);
            }
        } catch (Exception e) {
            logger.error("Error while check campaign name: " + e);
        }
        return map;
    }

    @GetMapping(value = "/dowload-blacklist-template")
    public void downloadFileTemplate(HttpServletResponse response, Model model) {
        File file = new File(pathTemplateBlacklist);
        byte[] data;
        try {
            data = FileUtils.readFileToByteArray(file);
            // Thiết lập thông tin trả về
            String fileName = URLEncoder.encode("Danh_sach_thue_bao_file_mau.xlsx", "UTF-8");
            response.setContentType("application/octet-stream; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-disposition", "attachment; filename=" + fileName);
            response.setContentLength(data.length);
            InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(data));
            FileCopyUtils.copy(inputStream, response.getOutputStream());
        } catch (Exception e) {
            logger.info("error while download file template!");
            logger.error(e);
        }
    }

    @GetMapping("/view-edit-subTargetGroup/{id}")
    public String viewEditSubTargetGroup(Model model, @PathVariable("id") Long id) {
        Optional<SubTargetGroup> optionalSubTargetGroup = subTargetGroupService.findById(id);
        SubTargetGroup subTargetGroup = new SubTargetGroup();
        if (optionalSubTargetGroup.isPresent()) {
            subTargetGroup = optionalSubTargetGroup.get();
        }
        if (subTargetGroup.getChannel() == 1 || subTargetGroup.getChannel() == 4) {
            List<MappingCriteria> mappingCriteriaList = mappingCriteriaRepository.findAllByIdTargetGroupOrderByLevelCriteriaAscPositionAsc(id);
            model.addAttribute("mappingCriteriaList", mappingCriteriaList);
        }
        model.addAttribute("subTargetGroup", subTargetGroup);
        return "TargetGroup/EditTargetGroup";
    }

    @PostMapping("/create-sub-targetGroup")
    @ResponseBody
    public Map<String, Object> createTargetGroup(@RequestParam(name = "jsonData") String jsonData) {
        logger.info("=== save sub target group ===");
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            logger.info("jsonData" + jsonData);
            SubTargetGroup subTargetGroup = subTargetGroupService.saveJsonData(jsonData);
            Long idSub = null;
            targetGroupService.saveCriteriaSetup(jsonData, subTargetGroup.getId(), idSub);
            map.put("result", subTargetGroup.getId());

        } catch (Exception e) {
            logger.error("Error while create sub target group: " + e);
        }
        return map;
    }

    @PreAuthorize("hasAnyAuthority('update:campaign')")
    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String editCampaign(Model model, @RequestParam(required = true) Long frequencyCampaignId) throws AppException {

        FrequencyCampaign frequencyCampaign = new FrequencyCampaign();
        List<SendingAccount> sendingAccounts = new ArrayList<SendingAccount>();
        List<SubTargetGroupDTO> subTargetGroups = new ArrayList<>();
        SmsContent smsContent = new SmsContent();
        SendingTimeLimitChannelDTO sendingTimeLimitChannelDTO = new SendingTimeLimitChannelDTO();
        DisableMessageLimitDTO disableMessageLimitDTO = new DisableMessageLimitDTO();
        UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        SimpleResponseDTO res = new SimpleResponseDTO();
        String result;
        String roleLogin = permissionUtils.getUserRole();
        Optional<FrequencyCampaign> frequencyCampaignOptional = frequencyCampaignService.findById(frequencyCampaignId);
        sendingAccounts = sendingAccountService.findByStatus(StaticVar.SENDING_ACCOUNT_STATUS_ACTIVE);
        if (frequencyCampaignOptional.isPresent()) {
            frequencyCampaign = frequencyCampaignOptional.get();

            if (!(frequencyCampaign.getCreatedUser().equals(currentUser.getUsername())) && (!("Quản trị").equals(roleLogin) && !("Super Admin").equals(roleLogin))) {
                return "redirect:/accessDenied";
            }
            model.addAttribute("currentUser", currentUser.getUsername());
            if (frequencyCampaign.getPathFileDataCustomer() != null && frequencyCampaign.getPathFileDataCustomer().length() > 0) {
                model.addAttribute("fileMSISDN", true);
            } else model.addAttribute("fileMSISDN", false);

            if (frequencyCampaign.getBlackListPathFile() != null && frequencyCampaign.getBlackListPathFile().length() > 0) {
                model.addAttribute("fileBlacklist", true);
            } else model.addAttribute("fileBlacklist", false);

            List<TargetGroup> targetGroupList = targetGroupService.findAllByStatusAndType();
            model.addAttribute("targetGroupList", targetGroupList);
            if (frequencyCampaign.getIdTargetGroup() != null && frequencyCampaign.getTypeTargetGroup() == 6) {
                Optional<TargetGroup> targetGroup = targetGroupService.findById(frequencyCampaign.getIdTargetGroup());
                FrequencyCampaign finalFrequencyCampaign = frequencyCampaign;
                targetGroup.ifPresent(x->{
                    if(x.getStatus() != -99){
                        model.addAttribute("idTargetGroup", finalFrequencyCampaign.getIdTargetGroup());
                        model.addAttribute("hasDeleteTargetGroup", 0);
                    }else {
                        model.addAttribute("idTargetGroup", targetGroupList.get(0).getId());
                        model.addAttribute("hasDeleteTargetGroup", 1);
                    }
                });
            }
            if (frequencyCampaign.getBlackListTargetGroupId() != null && frequencyCampaign.getTypeTargetGroup() == 6) {
                Optional<TargetGroup> targetGroup = targetGroupService.findById(frequencyCampaign.getBlackListTargetGroupId());
                FrequencyCampaign finalFrequencyCampaign = frequencyCampaign;
                targetGroup.ifPresent(x->{
                    if(x.getStatus() != -99){
                        model.addAttribute("blacklistTargetGroupId", finalFrequencyCampaign.getBlackListTargetGroupId());
                        model.addAttribute("hasDeleteTargetGroupBL", 0);
                    }else {
                        model.addAttribute("blacklistTargetGroupId", targetGroupList.get(0).getId());
                        model.addAttribute("hasDeleteTargetGroupBL", 1);
                    }
                });
            }
            if (frequencyCampaign.getHasSubTargetGroup() == 1) {
                List<SubTargetGroup> subTargetGroup = subTargetGroupService.findByCampaignId(frequencyCampaign.getCampaignId());
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
                if (frequencyCampaign.getContentId() != null) {
                    Optional<SmsContent> smsContentOptional = smsContentService.findById(frequencyCampaign.getContentId());
                    if (smsContentOptional.isPresent()) {
                        smsContent = smsContentOptional.get();
                    }
                }
            }

            if (frequencyCampaign.getSendingTimeLimitChannel() != null) {
                sendingTimeLimitChannelDTO.setCheckSendingTimeLimitChannel(true);
                JSONObject jsonSendingTime = new JSONObject(frequencyCampaign.getSendingTimeLimitChannel());
                sendingTimeLimitChannelDTO.setZalo(jsonSendingTime.getBoolean("zalo"));
                sendingTimeLimitChannelDTO.setDigilife(jsonSendingTime.getBoolean("digilife"));
                sendingTimeLimitChannelDTO.setEmail(jsonSendingTime.getBoolean("email"));
            }

            if (frequencyCampaign.getDisableMessageLimit() != null) {
                disableMessageLimitDTO.setCheckDisableMessageLimitChannel(true);
                JSONObject jsonDisableMessage = new JSONObject(frequencyCampaign.getDisableMessageLimit());
                disableMessageLimitDTO.setZalo(jsonDisableMessage.getBoolean("zalo"));
                disableMessageLimitDTO.setDigilife(jsonDisableMessage.getBoolean("digilife"));
                disableMessageLimitDTO.setEmail(jsonDisableMessage.getBoolean("email"));
            }


            FrequencySendingDTO frequencySendingDTO = new FrequencySendingDTO(new JSONObject(frequencyCampaign.getFreeCfg()));
            if (frequencyCampaign.getFreeCfg() != null) {
                frequencySendingDTO = frequencySendingDTO.toDto(frequencyCampaign.getFreeCfg());
            }

            if (frequencyCampaign.getTypeTargetGroup() != 3) {
                Optional<TargetGroup> targetGroupOptional = targetGroupService.findById(frequencyCampaign.getIdTargetGroup());
                TargetGroup targetGroup = new TargetGroup();
                if (targetGroupOptional.isPresent()) {
                    targetGroup = targetGroupOptional.get();
                    List<MappingCriteria> mappingCriteriaList = mappingCriteriaRepository.findAllByIdTargetGroupOrderByLevelCriteriaAscPositionAsc(frequencyCampaign.getIdTargetGroup());
                    model.addAttribute("mappingCriteriaList", mappingCriteriaList);
                    if (mappingCriteriaList.size() > 0) {
                        model.addAttribute("typeLV1", mappingCriteriaList.get(0).getType());
                    }
                    model.addAttribute("targetGroup", targetGroup);
                }
            }
            List<PackageNameDTO> listPackage = packageDataService.findAllByStatusAndPackageGroupOrderByPackageName(1, frequencyCampaign.getPackageGroupId().intValue());
            model.addAttribute("listPackage", listPackage);
            model.addAttribute("frequencyCampaign", frequencyCampaign);
            if (frequencyCampaign.getHasSubTargetGroup() == 0) {

                SmsContentDTO smsContentDTO = new SmsContentDTO();
                smsContentDTO.setMessageContent(smsContent.getMessage());
                smsContentDTO.setCountMT(smsContent.getMtCount());
                smsContentDTO.setSendingAccount(frequencyCampaign.getAccountSendingId());
                smsContentDTO.setChannelMarketing(frequencyCampaign.getChannel());
                smsContentDTO.setUnicode(smsContent.getUnicode());
                smsContentDTO.setProductPackage(frequencyCampaign.getPackageDataId());
                smsContentDTO.setJsonSmsContentDTO(AppUtils.objectToJson(smsContentDTO));
                model.addAttribute("smsContent", smsContentDTO);
                model.addAttribute("subTargetGroups", subTargetGroups);
            } else {
                SmsContentDTO smsContentDTO = new SmsContentDTO();
                smsContentDTO.setMessageContent(" ");
                model.addAttribute("smsContent", smsContentDTO);
                model.addAttribute("subTargetGroups", subTargetGroups);
            }

            model.addAttribute("sendingAccounts", sendingAccounts);
            model.addAttribute("sendingTimeLimitChannelDTO", sendingTimeLimitChannelDTO);
            model.addAttribute("disableMessageLimitDTO", disableMessageLimitDTO);
            model.addAttribute("frequencySendingDTO", frequencySendingDTO);
        } else {
            res.setMessage("Không tìm thấy thông tin chiến dịch.");
            res.setCode(AppUtils.errorCode);
            result = AppUtils.ObjectToJsonResponse(res);
            model.addAttribute("result", result);
        }
        return "frequency-campaign/edit/edit-mainpage";
    }

    @PreAuthorize("hasAnyAuthority('copy:campaign')")
    @RequestMapping(value = "/copy", method = RequestMethod.GET)
    public String copyCampaign(Model model, @RequestParam(required = true) Long frequencyCampaignId) throws AppException {

        FrequencyCampaign frequencyCampaign = new FrequencyCampaign();
        List<SendingAccount> sendingAccounts = new ArrayList<SendingAccount>();
        List<SubTargetGroupDTO> subTargetGroups = new ArrayList<>();
        SmsContent smsContent = new SmsContent();
        SendingTimeLimitChannelDTO sendingTimeLimitChannelDTO = new SendingTimeLimitChannelDTO();
        DisableMessageLimitDTO disableMessageLimitDTO = new DisableMessageLimitDTO();


        SimpleResponseDTO res = new SimpleResponseDTO();
        String result;

        Optional<FrequencyCampaign> frequencyCampaignOptional = frequencyCampaignService.findById(frequencyCampaignId);
        sendingAccounts = sendingAccountService.findByStatus(StaticVar.SENDING_ACCOUNT_STATUS_ACTIVE);
        if (frequencyCampaignOptional.isPresent()) {
            frequencyCampaign = frequencyCampaignOptional.get();

            if (frequencyCampaign.getPathFileDataCustomer() != null && frequencyCampaign.getPathFileDataCustomer().length() > 0) {
                model.addAttribute("fileMSISDN", true);
            } else model.addAttribute("fileMSISDN", false);

            if (frequencyCampaign.getBlackListPathFile() != null && frequencyCampaign.getBlackListPathFile().length() > 0) {
                model.addAttribute("fileBlacklist", true);
            } else model.addAttribute("fileBlacklist", false);

            if (frequencyCampaign.getHasSubTargetGroup() == 1) {
                List<SubTargetGroup> subTargetGroup = subTargetGroupService.findByCampaignId(frequencyCampaign.getCampaignId());
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
                if (frequencyCampaign.getContentId() != null) {
                    Optional<SmsContent> smsContentOptional = smsContentService.findById(frequencyCampaign.getContentId());
                    if (smsContentOptional.isPresent()) {
                        smsContent = smsContentOptional.get();
                    }
                }
            }

            if (frequencyCampaign.getSendingTimeLimitChannel() != null) {
                sendingTimeLimitChannelDTO.setCheckSendingTimeLimitChannel(true);
                JSONObject jsonSendingTime = new JSONObject(frequencyCampaign.getSendingTimeLimitChannel());
                sendingTimeLimitChannelDTO.setZalo(jsonSendingTime.getBoolean("zalo"));
                sendingTimeLimitChannelDTO.setDigilife(jsonSendingTime.getBoolean("digilife"));
                sendingTimeLimitChannelDTO.setEmail(jsonSendingTime.getBoolean("email"));
            }

            if (frequencyCampaign.getDisableMessageLimit() != null) {
                disableMessageLimitDTO.setCheckDisableMessageLimitChannel(true);
                JSONObject jsonDisableMessage = new JSONObject(frequencyCampaign.getDisableMessageLimit());
                disableMessageLimitDTO.setZalo(jsonDisableMessage.getBoolean("zalo"));
                disableMessageLimitDTO.setDigilife(jsonDisableMessage.getBoolean("digilife"));
                disableMessageLimitDTO.setEmail(jsonDisableMessage.getBoolean("email"));
            }


            FrequencySendingDTO frequencySendingDTO = new FrequencySendingDTO(new JSONObject(frequencyCampaign.getFreeCfg()));
            if (frequencyCampaign.getFreeCfg() != null) {
                frequencySendingDTO = frequencySendingDTO.toDto(frequencyCampaign.getFreeCfg());
            }

            if (frequencyCampaign.getTypeTargetGroup() != 3) {
                Optional<TargetGroup> targetGroupOptional = targetGroupService.findById(frequencyCampaign.getIdTargetGroup());
                TargetGroup targetGroup = new TargetGroup();
                if (targetGroupOptional.isPresent()) {
                    targetGroup = targetGroupOptional.get();
                    List<MappingCriteria> mappingCriteriaList = mappingCriteriaRepository.findAllByIdTargetGroupOrderByLevelCriteriaAscPositionAsc(frequencyCampaign.getIdTargetGroup());
                    model.addAttribute("mappingCriteriaList", mappingCriteriaList);
                    if (mappingCriteriaList.size() > 0) {
                        model.addAttribute("typeLV1", mappingCriteriaList.get(0).getType());
                    }
                    model.addAttribute("targetGroup", targetGroup);
                }
            }
            List<TargetGroup> targetGroupList = targetGroupService.findAllByStatusAndType();
            model.addAttribute("targetGroupList", targetGroupList);
            if (frequencyCampaign.getIdTargetGroup() != null && frequencyCampaign.getTypeTargetGroup() == 6) {
                Optional<TargetGroup> targetGroup = targetGroupService.findById(frequencyCampaign.getIdTargetGroup());
                FrequencyCampaign finalFrequencyCampaign = frequencyCampaign;
                targetGroup.ifPresent(x->{
                    if(x.getStatus() != -99){
                        model.addAttribute("idTargetGroup", finalFrequencyCampaign.getIdTargetGroup());
                        model.addAttribute("hasDeleteTargetGroup", 0);
                    }else {
                        model.addAttribute("idTargetGroup", targetGroupList.get(0).getId());
                        model.addAttribute("hasDeleteTargetGroup", 1);
                    }
                });
            }
            if (frequencyCampaign.getBlackListTargetGroupId() != null && frequencyCampaign.getTypeTargetGroup() == 6) {
                Optional<TargetGroup> targetGroup = targetGroupService.findById(frequencyCampaign.getBlackListTargetGroupId());
                FrequencyCampaign finalFrequencyCampaign = frequencyCampaign;
                targetGroup.ifPresent(x->{
                    if(x.getStatus() != -99){
                        model.addAttribute("blacklistTargetGroupId", finalFrequencyCampaign.getBlackListTargetGroupId());
                        model.addAttribute("hasDeleteTargetGroupBL", 0);
                    }else {
                        model.addAttribute("blacklistTargetGroupId", targetGroupList.get(0).getId());
                        model.addAttribute("hasDeleteTargetGroupBL", 1);
                    }
                });
            }
            List<PackageNameDTO> listPackage = packageDataService.findAllByStatusAndPackageGroupOrderByPackageName(1, frequencyCampaign.getPackageGroupId().intValue());
            model.addAttribute("listPackage", listPackage);
            model.addAttribute("frequencyCampaign", frequencyCampaign);
            if (frequencyCampaign.getHasSubTargetGroup() == 0) {

                SmsContentDTO smsContentDTO = new SmsContentDTO();
                smsContentDTO.setMessageContent(smsContent.getMessage());
                smsContentDTO.setCountMT(smsContent.getMtCount());
                smsContentDTO.setSendingAccount(frequencyCampaign.getAccountSendingId());
                smsContentDTO.setChannelMarketing(frequencyCampaign.getChannel());
                smsContentDTO.setUnicode(smsContent.getUnicode());
                smsContentDTO.setProductPackage(frequencyCampaign.getPackageDataId());
                smsContentDTO.setJsonSmsContentDTO(AppUtils.objectToJson(smsContentDTO));

                model.addAttribute("accountSendingId", frequencyCampaign.getAccountSendingId());
                model.addAttribute("packageDataId", frequencyCampaign.getPackageDataId());
                model.addAttribute("smsContent", smsContentDTO);
                model.addAttribute("subTargetGroups", subTargetGroups);
            } else {
                SmsContentDTO smsContentDTO = new SmsContentDTO();
                smsContentDTO.setMessageContent(" ");
                model.addAttribute("smsContent", smsContentDTO);
                model.addAttribute("subTargetGroups", subTargetGroups);
            }

            model.addAttribute("sendingAccounts", sendingAccounts);
            model.addAttribute("sendingTimeLimitChannelDTO", sendingTimeLimitChannelDTO);
            model.addAttribute("disableMessageLimitDTO", disableMessageLimitDTO);
            model.addAttribute("frequencySendingDTO", frequencySendingDTO);
        } else {
            res.setMessage("Không tìm thấy thông tin chiến dịch.");
            res.setCode(AppUtils.errorCode);
            result = AppUtils.ObjectToJsonResponse(res);
            model.addAttribute("result", result);
        }
        return "frequency-campaign/copy/copy-mainpage";
    }

    @RequestMapping(value = "/check-duplicate-name", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String checkScenarioName(@RequestParam("campaignName") String campaignName, @RequestParam(name = "campaignId", required = false) Long campaignId) throws JsonProcessingException {
        Map<String, Object> map = new HashMap<String, Object>();
        logger.info("Request check duplicate scenarioName: " + campaignName + " --- " + campaignName);
        boolean result = frequencyCampaignService.checkDuplicateCampaignName(campaignName, campaignId);
        map.put("result", result);
        String jsonJackson = new ObjectMapper().writeValueAsString(map);
        return jsonJackson;
    }

}
