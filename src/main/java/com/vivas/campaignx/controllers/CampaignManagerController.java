package com.vivas.campaignx.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vivas.campaignx.common.AppUtils;
import com.vivas.campaignx.common.StaticVar;
import com.vivas.campaignx.dto.*;
import com.vivas.campaignx.entity.*;
import com.vivas.campaignx.export.ExcelFileExporter;
import com.vivas.campaignx.repository.*;
import com.vivas.campaignx.service.*;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.util.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

@Controller
@RequestMapping("/campaignManager")
public class CampaignManagerController {
    protected final Logger logger = LogManager.getLogger(this.getClass().getName());
    protected String pattern = "dd-mm-yyyy";

    @Autowired
    private CampaignManagerService campaignManagerService;

    @Autowired
    private EventCampaignRepository eventCampaignRepository;

    @Autowired
    private FrequencyCampaignRepository frequencyCampaignRepository;

    @Autowired
    private FrequencyCampaignService frequencyCampaignService;

    @Autowired
    private EventCampaignService eventCampaignService;

    @Autowired
    private TargetGroupService targetGroupService;

    @Autowired
    private SubTargetGroupService subTargetGroupService;

    @Autowired
    private MappingCriteriaService mappingCriteriaService;

    @Autowired
    private PackageDataService packageDataService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private SendingAccountService sendingAccountService;

    @Autowired
    private PermissionUtils permissionUtils;

    @Autowired
    private SmsContentService smsContentService;

    @Autowired
    private FrequencyCampaignPedingRepository frequencyCampaignPedingRepository;

    @Autowired
    private BigdataEventService bigdataEventService;

    @Autowired
    private NotifyRepository notifyRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;


    @PreAuthorize("hasAuthority('search:view:campaign')")
    @GetMapping
    public String viewCampaignManager(Model model,
                                      @RequestParam(required = false) Integer pageSize,
                                      @RequestParam(required = false) Integer currentPage,
                                      @RequestParam(required = false) Integer type,
                                      @RequestParam(required = false) String campaignName,
                                      @RequestParam(required = false) String createdUser,
                                      @RequestParam(required = false) String createdDate,
                                      @RequestParam(required = false) String startDate,
                                      @RequestParam(required = false) String endDate,
                                      @RequestParam(required = false) String statusInit,
                                      @RequestParam(required = false) String statusApprove,
                                      @RequestParam(required = false) String statusReject,
                                      @RequestParam(required = false) String statusActive,
                                      @RequestParam(required = false) String statusPause,
                                      @RequestParam(required = false) String statusEnd
    ) {
        if (pageSize == null)
            pageSize = 20;
        if (currentPage == null)
            currentPage = 1;
        Page<CampaignManagerDto> campaignManagerDtos = null;
        if ((type == null || type == 0) && campaignName == null && createdUser == null && createdDate == null && startDate == null && endDate == null
                && statusInit == null && statusApprove == null && statusReject == null && statusActive == null && statusPause == null && statusEnd == null) {
            campaignManagerDtos = campaignManagerService.getAll(pageSize, currentPage);
        } else {
            campaignManagerDtos = campaignManagerService.findByCampaign(pageSize, currentPage, type, campaignName, createdUser, createdDate, startDate, endDate,
                    statusInit, statusApprove, statusReject, statusActive, statusPause, statusEnd);
        }
        UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String roleLogin = permissionUtils.getUserRole();
        logger.info("roleLogin:{}", roleLogin);
        logger.info("currentUser:{}", currentUser.getUsername());
        model.addAttribute("campaignManagerDtos", campaignManagerDtos);
        model.addAttribute("roleLogin", roleLogin);
        model.addAttribute("type", type);
        model.addAttribute("campaignName", campaignName);
        model.addAttribute("currentUser", currentUser.getUsername());
        model.addAttribute("createdUser", createdUser);
        model.addAttribute("createdDate", createdDate);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("statusInit", statusInit);
        model.addAttribute("statusApprove", statusApprove);
        model.addAttribute("statusReject", statusReject);
        model.addAttribute("statusActive", statusActive);
        model.addAttribute("statusPause", statusPause);
        model.addAttribute("statusEnd", statusEnd);

        model.addAttribute("totalPages", campaignManagerDtos.getTotalPages());
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("currentPage", currentPage);
        return "campaign/campaignManager";
    }

    @PreAuthorize("hasAnyAuthority('approve:campaign','refuse:campaign','stop:campaign','end:campaign','send:approve:campaign')")
    @GetMapping("/changeStatus")
    public String changeStatus(@RequestParam(required = true) Long id, @RequestParam(required = true) Integer status,
                               @RequestParam(required = true) Integer type, RedirectAttributes redirectAttributes) {
        UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        Integer userId = null;
        String roleLogin = permissionUtils.getUserRole();
        SimpleResponseDTO res = new SimpleResponseDTO();
        String result;
        Integer oldStatus = null;
        boolean notifyCacheEventCampaign = false;
        EventCampaign eventCampaign = null;
        String name = null;
        if (type == 1) {
            Optional<EventCampaign> eventCampaignOptional = campaignManagerService.findByIdEvent(id);
            if (eventCampaignOptional.isPresent()) {
                eventCampaign = eventCampaignOptional.get();
                name = eventCampaign.getCampaignName();
                Optional<Users> usersOptional = usersService.findUserByUsernameAndStatus(eventCampaign.getCreatedUser());
                if (usersOptional.isPresent()) {
                    userId = usersOptional.get().getUserId();
                    if (!(eventCampaign.getCreatedUser().equals(currentUser.getUsername())) && (!("Quản trị").equals(roleLogin) && !("Super Admin").equals(roleLogin))) {
                        return "redirect:/accessDenied";
                    }
                    oldStatus = eventCampaign.getStatus();
                    eventCampaign.setStatus(status);
                    eventCampaign.setStatusOld(oldStatus);
                    eventCampaign.setUpdatedUser(currentUser.getUsername());
                    eventCampaign.setUpdatedDate(new Date());
                    if (status == 4) {
                        eventCampaign.setEndDate(new Date());
                    }
                    eventCampaignRepository.save(eventCampaign);
                    res.setCode(AppUtils.successCode);
                    notifyCacheEventCampaign = true;
                }
            }
        } else if (type == 2) {
            Optional<FrequencyCampaign> frequencyCampaignOptional = campaignManagerService.findByIdFrequency(id);
            FrequencyCampaign frequencyCampaign = new FrequencyCampaign();
            if (frequencyCampaignOptional.isPresent()) {
                frequencyCampaign = frequencyCampaignOptional.get();
                name = frequencyCampaign.getCampaignName();
                Optional<Users> usersOptional = usersService.findUserByUsernameAndStatus(frequencyCampaign.getCreatedUser());
                if (usersOptional.isPresent()) {
                    userId = usersOptional.get().getUserId();
                    if (!(frequencyCampaign.getCreatedUser().equals(currentUser.getUsername())) && (!("Quản trị").equals(roleLogin) && !("Super Admin").equals(roleLogin))) {
                        return "redirect:/accessDenied";
                    }
                    oldStatus = frequencyCampaign.getStatus();
                    frequencyCampaign.setStatus(status);
                    frequencyCampaign.setStatusOld(oldStatus);
                    frequencyCampaign.setUpdatedUser(currentUser.getUsername());
                    frequencyCampaign.setUpdatedDate(new Date());
                    if (status == 0 && oldStatus == 2) {
                        FrequencyCampaignPending frequencyCampaignPending = cloneFrequencyCampaign(frequencyCampaign);
                        frequencyCampaignPedingRepository.save(frequencyCampaignPending);
                    }
                    if (status == 4) {
                        frequencyCampaign.setEndDate(new Date());
                    }
                    frequencyCampaignRepository.save(frequencyCampaign);

                    res.setCode(AppUtils.successCode);
                }

            }
        }
        List<UserRole> findByRoleId = userRoleRepository.findByRoleRoleId(10L);
        List<Users> findByIsRoot = usersService.findByIsRoot(true);
        List<Integer> listUserId = new ArrayList<>();
        for(UserRole userRole: findByRoleId) {
            listUserId.add(userRole.getUser().getUserId());
        }
        for(Users users: findByIsRoot) {
            listUserId.add(users.getUserId());
        }
        if (Objects.equals(status, StaticVar.CAMPAIGN_WAIT_TO_APPROVE)) {
            for(Integer userIdNoti : listUserId) {
                res.setMessage("Gửi yêu cầu phê duyệt chiến dịch thành công.");
                Notify notify = new Notify();
                notify.setCreatedDate(new Date());
                notify.setStatus(0L);
                notify.setSubject("Gửi phê duyệt chiến dịch");
                notify.setContent("Chiến dịch " +name+ " đang chờ bạn phê duyệt. Vui lòng vào <a style='display: inline !important; padding: 0px 0px !important' href=\"/campaignManager/detail-frequency-campaign/" + id + "?type=" + type + "\">đây</a> để xem chi tiết chiến dịch.");
                notify.setNotifyToUserId(userIdNoti.longValue());
                notifyRepository.save(notify);
            }
        }

        if (Objects.equals(status, StaticVar.CAMPAIGN_PAUSE_STATUS)) {
            res.setMessage("Tạm dừng chiến dịch thành công.");
        }
        if (Objects.equals(oldStatus, StaticVar.CAMPAIGN_PAUSE_STATUS)) {
            if (status.equals(StaticVar.CAMPAIGN_ACTIVE_STATUS)) {
                res.setMessage("Tái khởi tạo chiến dịch thành công.");
            }
        }
        if (oldStatus.equals(StaticVar.CAMPAIGN_WAIT_TO_APPROVE)) {
            if (!roleLogin.equals("Quản trị") && !roleLogin.equals("Super Admin") && status == 0) {
                return "redirect:/accessDenied";
            }
            if (status.equals(StaticVar.CAMPAIGN_ACTIVE_STATUS)) {
                res.setMessage("Phê duyệt chiến dịch thành công.");
                Notify notify = new Notify();
                notify.setCreatedDate(new Date());
                notify.setStatus(0L);
                notify.setSubject("Phê duyệt chiến dịch");
                notify.setContent("Chiến dịch " +name+ " đã được phê duyệt. Vui lòng vào <a style='display: inline !important; padding: 0px 0px !important' href=\"/campaignManager/detail-frequency-campaign/" + id + "?type=" + type + "\">đây</a> để xem chi tiết chiến dịch.");
                notify.setNotifyToUserId(userId.longValue());
                notifyRepository.save(notify);
                if (notifyCacheEventCampaign) {
                    eventCampaignService.cacheTargetGroup(eventCampaign);
                }
            }
            if (status.equals(StaticVar.CAMPAIGN_REJECT_STATUS)) {
                res.setMessage("Từ chối phê duyệt chiến dịch thành công.");
                Notify notify = new Notify();
                notify.setCreatedDate(new Date());
                notify.setStatus(0L);
                notify.setSubject("Phê duyệt chiến dịch");
                notify.setContent("Chiến dịch " +name+ " đã bị từ chối.  Vui lòng vào <a style='display: inline !important; padding: 0px 0px !important' href=\"/campaignManager/detail-frequency-campaign/" + id + "?type=" + type + "\">đây</a> để chỉnh sửa hoặc kết thúc chiến dịch.");
                notify.setNotifyToUserId(userId.longValue());
                notifyRepository.save(notify);
            }
        }
        if (status.equals(StaticVar.CAMPAIGN_END)) {
            res.setMessage("Kết thúc chiến dịch thành công.");
        }
        result = AppUtils.ObjectToJsonResponse(res);
        redirectAttributes.addFlashAttribute("result", result);
        return "redirect:/campaignManager";
    }

    @PreAuthorize("hasAnyAuthority('approve:campaign','refuse:campaign','stop:campaign','end:campign','send:approve:campaign')")
    @GetMapping("/changeStatusDetail")
    public String changeStatusDetail(@RequestParam(required = true) Long id, @RequestParam(required = true) Integer status,
                                     @RequestParam(required = true) Integer type, RedirectAttributes redirectAttributes) {
        UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        Integer userId = null;
        String roleLogin = permissionUtils.getUserRole();
        SimpleResponseDTO res = new SimpleResponseDTO();
        String result;
        Integer oldStatus = null;
        boolean notifyCacheEventCampaign = false;
        EventCampaign eventCampaign = null;
        String name = null;
        if (type == 1) {
            Optional<EventCampaign> eventCampaignOptional = campaignManagerService.findByIdEvent(id);
            if (eventCampaignOptional.isPresent()) {
                eventCampaign = eventCampaignOptional.get();
                name = eventCampaign.getCampaignName();
                Optional<Users> usersOptional = usersService.findUserByUsernameAndStatus(eventCampaign.getCreatedUser());
                if (usersOptional.isPresent()) {
                    userId = usersOptional.get().getUserId();
                    if (!(eventCampaign.getCreatedUser().equals(currentUser.getUsername())) && (!("Quản trị").equals(roleLogin) && !("Super Admin").equals(roleLogin))) {
                        return "redirect:/accessDenied";
                    }
                    oldStatus = eventCampaign.getStatus();
                    eventCampaign.setStatus(status);
                    eventCampaign.setStatusOld(oldStatus);
                    eventCampaign.setUpdatedUser(currentUser.getUsername());
                    eventCampaign.setUpdatedDate(new Date());
                    if (status == 4) {
                        eventCampaign.setEndDate(new Date());
                    }
                    eventCampaignRepository.save(eventCampaign);
                    res.setCode(AppUtils.successCode);
                    notifyCacheEventCampaign = true;
                }
            }
        } else if (type == 2) {
            Optional<FrequencyCampaign> frequencyCampaignOptional = campaignManagerService.findByIdFrequency(id);
            FrequencyCampaign frequencyCampaign = new FrequencyCampaign();
            if (frequencyCampaignOptional.isPresent()) {
                frequencyCampaign = frequencyCampaignOptional.get();
                name = frequencyCampaign.getCampaignName();
                Optional<Users> usersOptional = usersService.findUserByUsernameAndStatus(frequencyCampaign.getCreatedUser());
                if (usersOptional.isPresent()) {
                    userId = usersOptional.get().getUserId();
                    if (!(frequencyCampaign.getCreatedUser().equals(currentUser.getUsername())) && (!("Quản trị").equals(roleLogin) && !("Super Admin").equals(roleLogin))) {
                        return "redirect:/accessDenied";
                    }
                    oldStatus = frequencyCampaign.getStatus();
                    frequencyCampaign.setStatus(status);
                    frequencyCampaign.setStatusOld(oldStatus);
                    frequencyCampaign.setUpdatedUser(currentUser.getUsername());
                    frequencyCampaign.setUpdatedDate(new Date());
                    if (status == 0 && oldStatus == 2) {
                        FrequencyCampaignPending frequencyCampaignPending = cloneFrequencyCampaign(frequencyCampaign);
                        frequencyCampaignPedingRepository.save(frequencyCampaignPending);
                    }
                    if (status == 4) {
                        frequencyCampaign.setEndDate(new Date());
                    }
                    frequencyCampaignRepository.save(frequencyCampaign);

                    res.setCode(AppUtils.successCode);
                }

            }
        }
        List<UserRole> findByRoleId = userRoleRepository.findByRoleRoleId(10L);
        List<Users> findByIsRoot = usersService.findByIsRoot(true);
        List<Integer> listUserId = new ArrayList<>();
        for(UserRole userRole: findByRoleId) {
            listUserId.add(userRole.getUser().getUserId());
        }
        for(Users users: findByIsRoot) {
            listUserId.add(users.getUserId());
        }
        if (Objects.equals(status, StaticVar.CAMPAIGN_WAIT_TO_APPROVE)) {
            for(Integer userIdNoti : listUserId) {
                res.setMessage("Gửi yêu cầu phê duyệt chiến dịch thành công.");
                Notify notify = new Notify();
                notify.setCreatedDate(new Date());
                notify.setStatus(0L);
                notify.setSubject("Gửi phê duyệt chiến dịch");
                notify.setContent("Chiến dịch " +name+ " đang chờ bạn phê duyệt. Vui lòng vào <a style='display: inline !important; padding: 0px 0px !important' href=\"/campaignManager/detail-frequency-campaign/" + id + "?type=" + type + "\">đây</a> để xem chi tiết chiến dịch.");
                notify.setNotifyToUserId(userIdNoti.longValue());
                notifyRepository.save(notify);
            }
        }

        if (Objects.equals(status, StaticVar.CAMPAIGN_PAUSE_STATUS)) {
            res.setMessage("Tạm dừng chiến dịch thành công.");
        }
        if (Objects.equals(oldStatus, StaticVar.CAMPAIGN_PAUSE_STATUS)) {
            if (status.equals(StaticVar.CAMPAIGN_ACTIVE_STATUS)) {
                res.setMessage("Tái khởi tạo chiến dịch thành công.");
            }
        }
        if (oldStatus.equals(StaticVar.CAMPAIGN_WAIT_TO_APPROVE)) {
            if (!roleLogin.equals("Quản trị") && !roleLogin.equals("Super Admin")) {
                return "redirect:/accessDenied";
            }
            if (status.equals(StaticVar.CAMPAIGN_ACTIVE_STATUS)) {
                res.setMessage("Phê duyệt chiến dịch thành công.");
                Notify notify = new Notify();
                notify.setCreatedDate(new Date());
                notify.setStatus(0L);
                notify.setSubject("Phê duyệt chiến dịch");
                notify.setContent("Chiến dịch " +name+ " đã được phê duyệt. Vui lòng vào <a style='display: inline !important; padding: 0px 0px !important' href=\"/campaignManager/detail-frequency-campaign/" + id + "?type=" + type + "\">đây</a> để xem chi tiết chiến dịch.");
                notify.setNotifyToUserId(userId.longValue());
                notifyRepository.save(notify);
                if (notifyCacheEventCampaign) {
                    eventCampaignService.cacheTargetGroup(eventCampaign);
                }
            }
            if (status.equals(StaticVar.CAMPAIGN_REJECT_STATUS)) {
                res.setMessage("Từ chối phê duyệt chiến dịch thành công.");
                Notify notify = new Notify();
                notify.setCreatedDate(new Date());
                notify.setStatus(0L);
                notify.setSubject("Phê duyệt chiến dịch");
                notify.setContent("Chiến dịch " +name+ " đã bị từ chối.  Vui lòng vào <a style='display: inline !important; padding: 0px 0px !important' href=\"/campaignManager/detail-frequency-campaign/" + id + "?type=" + type + "\">đây</a> để chỉnh sửa hoặc kết thúc chiến dịch.");
                notify.setNotifyToUserId(userId.longValue());
                notifyRepository.save(notify);
            }
        }
        if (status.equals(StaticVar.CAMPAIGN_END)) {
            res.setMessage("Kết thúc chiến dịch thành công.");
        }
        result = AppUtils.ObjectToJsonResponse(res);
        redirectAttributes.addFlashAttribute("result", result);
        return "redirect:/campaignManager/detail-frequency-campaign/" + id + "?type=" + type;
    }

    @PreAuthorize("hasAuthority('export:list:campaign')")
    @RequestMapping(value = "/exportCampaign")
    public void exportTargetGroup(HttpServletResponse response,
                                  @RequestParam(required = false) Integer pageSize,
                                  @RequestParam(required = false) Integer currentPage,
                                  @RequestParam(required = false) Integer type,
                                  @RequestParam(required = false) String campaignName,
                                  @RequestParam(required = false) String createdUser,
                                  @RequestParam(required = false) String createdDate,
                                  @RequestParam(required = false) String startDate,
                                  @RequestParam(required = false) String endDate,
                                  @RequestParam(required = false) String statusInit,
                                  @RequestParam(required = false) String statusApprove,
                                  @RequestParam(required = false) String statusReject,
                                  @RequestParam(required = false) String statusActive,
                                  @RequestParam(required = false) String statusPause,
                                  @RequestParam(required = false) String statusEnd) throws IOException {

        logger.info(String.format("export info: type=%s name=%s, createdUser=%s, createdDate=%s", type, campaignName, createdUser, createdDate));
        String fileName = "Danh_sach_chien_dich" + AppUtils.convertDateToString(new Date(), "ddMMyyyy") + "-" + AppUtils.randomNumber(0, 20) + ".csv";
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        Date createdDate1 = null;
        UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        if (createdDate != null && createdDate != "") {
            createdDate1 = AppUtils.convertStringToDate(createdDate, pattern);
            createdDate = AppUtils.convertDateToString(createdDate1, pattern);
        }
        List<CampaignManagerDto> campaignManagerDtoList = new ArrayList<>();
        if ((type == null || type == 0) && campaignName == null && createdUser == null && createdDate == null && startDate == null && endDate == null
                && statusInit == null && statusApprove == null && statusReject == null && statusActive == null && statusPause == null && statusEnd == null) {
            campaignManagerDtoList = campaignManagerService.exportAll(pageSize, currentPage);
        } else {
            campaignManagerDtoList = campaignManagerService.exportBySerach(pageSize, currentPage, type, campaignName, createdUser, createdDate, startDate, endDate,
                    statusInit, statusApprove, statusReject, statusActive, statusPause, statusEnd);
        }

        ByteArrayInputStream stream = ExcelFileExporter.campaignListToExcelFile(campaignManagerDtoList);
        IOUtils.copy(stream, response.getOutputStream());
    }


    @PreAuthorize("hasAuthority('view:frequency:campaign')")
    @GetMapping("/detail-frequency-campaign/{id}")
    public String viewFrequencyCampaign(Model model, @PathVariable("id") Long id, @RequestParam("type") Integer type) {
        CampaignDTO campaignDTO = new CampaignDTO();
        if (type == 2) {
            Optional<FrequencyCampaign> frequencyCampaignOptional = frequencyCampaignService.findById(id);
            if (frequencyCampaignOptional.isPresent()) {
                FrequencyCampaign frequencyCampaign = frequencyCampaignOptional.get();
                campaignDTO = new CampaignDTO().toDTO(frequencyCampaign);
            }
        } else if (type == 1) {
            Optional<EventCampaign> eventCampaignOptional = eventCampaignService.findById(id);
            if (eventCampaignOptional.isPresent()) {
                EventCampaign eventCampaign = eventCampaignOptional.get();
                campaignDTO = new CampaignDTO().toDTO(eventCampaign);
            }
        }
        model.addAttribute("type:", type);
        logger.info("Type Campaign: " + campaignDTO.getTypeCampaign());

        int typeBlacklist = 0;
        String pathBlackList = campaignDTO.getBlackListPathFile();
        Long idGroupBlackList = campaignDTO.getBlackListTargetGroupId();
        if (campaignDTO.getTypeInputBlacklist() == 3) {
            typeBlacklist = 3;
        } else if (campaignDTO.getTypeInputBlacklist() == 2) {
            typeBlacklist = 2;
            Optional<TargetGroup> targetGroupBlacklist = targetGroupService.findById(campaignDTO.getBlackListTargetGroupId());
            model.addAttribute("groupBlacklistName", targetGroupBlacklist.get().getName());
        }
        model.addAttribute("typeBlacklist", typeBlacklist);

        model.addAttribute("campaign", campaignDTO);
        model.addAttribute("status", campaignDTO.getStatus());
        model.addAttribute("statusOld", campaignDTO.getStatusOld());
        String sdate = AppUtils.convertDateToString(campaignDTO.getStartDate(), "dd/MM/yyyy");
        if (campaignDTO.getEndDate() != null) {
            String edate = AppUtils.convertDateToString(campaignDTO.getEndDate(), "dd/MM/yyyy");
            model.addAttribute("endDate", edate);
        }
        model.addAttribute("name", campaignDTO.getCampaignName());
        model.addAttribute("startDate", sdate);

        model.addAttribute("campaignId", campaignDTO.getCampaignId());

        if (campaignDTO.getTypeCampaign() == 1) {
            Integer eventCycle = campaignDTO.getEventCycle();
            Integer eventLimitPerday = campaignDTO.getEventLimitPerDay();
            model.addAttribute("eventCycle", eventCycle);
            model.addAttribute("eventLimitPerday", eventLimitPerday);

            String event = campaignDTO.getEventCondition();
            JSONArray jsonArrayEvent = new JSONArray(event);
            List<String> listEventToView = new ArrayList<>();

            Optional<BigdataEvent> bigdataEvent = bigdataEventService.findById(campaignDTO.getEventId());
            if(campaignDTO.getEventId() == 2l){
                JSONObject oJSON = new JSONObject();
                for(int temp = 0; temp < jsonArrayEvent.length(); temp ++){
                    if(jsonArrayEvent.getJSONObject(temp).getString("conditionName").equals("amount")) oJSON = jsonArrayEvent.getJSONObject(temp);
                }
                model.addAttribute("typeEvent", bigdataEvent.get().getEventName() + " " + oJSON.getString("value") + " VNĐ");
            }else model.addAttribute("typeEvent", bigdataEvent.get().getEventName());

            String htmlEventConditon = "";
            if (campaignDTO.getEventConditionRule() == 0) {
                htmlEventConditon = "<p style='margin-bottom: 0px'>Không sử dụng điều kiện</p>";
            } else if (campaignDTO.getEventConditionRule() == 1) {
                htmlEventConditon = "<p style='margin-bottom: 0px'>Thỏa mãn một trong số các điều kiện</p>";
            } else if (campaignDTO.getEventConditionRule() == 2) {
                htmlEventConditon = "<p style='margin-bottom: 0px'>Thỏa mãn tất cả điều kiện</p>";
            }

            listEventToView.add(htmlEventConditon);
            for (int i = 0; i < jsonArrayEvent.length(); i++) {
                String htmlEventToView = "";
                htmlEventToView += "<p style='margin-bottom: 0px; font-weight: lighter'>";
                htmlEventToView += eventCampaignService.detailConditionEvent(campaignDTO.getEventId(), jsonArrayEvent.getJSONObject(i));
                htmlEventToView += "</p>";
                listEventToView.add(htmlEventToView);
            }
            model.addAttribute("listEventToView", listEventToView);
        }
        List<String> listMainCriteriaToView = new ArrayList<>();
        if (campaignDTO.getTypeTargetGroup() == 1 || campaignDTO.getTypeTargetGroup() == 4) {
//            List<MappingCriteria> listMainCriteria = mappingCriteriaService.findAllByIdTargetGroup(campaignDTO.getIdTargetGroup());
//
//            boolean addConditionLevel1 = false;
//            boolean addConditionLevel2 = false;
//            boolean addConditionLevel3 = false;
//
//            for (int i = 0; i < listMainCriteria.size(); i++) {
//                String html = "";
//                MappingCriteria row = listMainCriteria.get(i);
//                if (row.getLevelCriteria() == 1) {
//                    if (!addConditionLevel1) {
//                        if (row.getType() == 1)
//                            html += "<p style='margin-bottom: 0px'>Thỏa mãn tất cả các điều kiện sau đây</p>";
//                        else
//                            html += "<p style='margin-bottom: 0px' >Thỏa mãn một trong số các điều kiện sau đây</p>";
//                        addConditionLevel1 = true;
//                    }
//                    html += "<p style='margin-bottom: 0px; font-weight: lighter'>+";
//                    html += mappingCriteriaService.detailCriteria(row.getSelectedValue());
//                    html += "</p>";
//                    addConditionLevel2 = false;
//                } else if (row.getLevelCriteria() == 2) {
//                    if (!addConditionLevel2) {
//                        if (row.getType() == 1)
//                            html += "<p style='margin-bottom: 0px; text-indent: 2em;'>Thỏa mãn tất cả các điều kiện sau đây</p>";
//                        else
//                            html += "<p style='margin-bottom: 0px; text-indent: 2em;' >Thỏa mãn một trong số các điều kiện sau đây</p>";
//                        addConditionLevel2 = true;
//                    }
//
//                    html += "<p style='margin-bottom: 0px; text-indent: 2em; font-weight: lighter'>+";
//                    html += mappingCriteriaService.detailCriteria(row.getSelectedValue());
//                    html += "</p>";
//                    addConditionLevel3 = false;
//                } else if (row.getLevelCriteria() == 3) {
//                    if (!addConditionLevel3) {
//                        if (row.getType() == 1)
//                            html += "<p style='margin-bottom: 0px; text-indent: 4em;'>Thỏa mãn tất cả các điều kiện sau đây</p>";
//                        else
//                            html += "<p style='margin-bottom: 0px; text-indent: 4em;' >Thỏa mãn một trong số các điều kiện sau đây</p>";
//                        addConditionLevel3 = true;
//                    }
//                    html += "<p style='margin-bottom: 0px; text-indent: 4em; font-weight: lighter'>+";
//                    html += mappingCriteriaService.detailCriteria(row.getSelectedValue());
//                    html += "</p>";
//                }
//                listMainCriteriaToView.add(html);
//            }
            model.addAttribute("targetGroupId", campaignDTO.getIdTargetGroup());
            List<MappingCriteria> criteriaList = targetGroupService.getListCriteriaLevel1(campaignDTO.getIdTargetGroup());
            if(criteriaList.size() > 0){
                MappingCriteria mappingCriteria = criteriaList.get(0);
                model.addAttribute("typeLevel1",mappingCriteria.getType());
            }
        }
        if (campaignDTO.getTypeTargetGroup() == 6) {
            Optional<TargetGroup> targetGroup = targetGroupService.findById(campaignDTO.getIdTargetGroup());
            if (targetGroup.isPresent()) {
                model.addAttribute("targetGroupOldName", targetGroup.get().getName());
            }
        }
        model.addAttribute("listMainCriteriaToView", listMainCriteriaToView);
        List<String> listSubGroupToView = new ArrayList<>();
        List<String> listContentToView = new ArrayList<>();
        String html = "";
        String htmlContent = "";
        Integer subTarget = null;
        if (campaignDTO.getSubTargetGroupRadio().equals("yes")) {
            subTarget = 1;
        } else if (campaignDTO.getSubTargetGroupRadio().equals("no")) {
            subTarget = 0;
        }
        model.addAttribute("subTarget", subTarget);
        if (campaignDTO.getSubTargetGroupRadio().equals("yes")) {
            List<SubTargetGroup> listSubGroup = subTargetGroupService.findByCampaignId(campaignDTO.getCampaignId());
            SubTargetGroup subTargetGroup;

            for (int i = 0; i < listSubGroup.size(); i++) {
                subTargetGroup = listSubGroup.get(i);
                html += "<tr>";
                html += "<td>" + subTargetGroup.getName() + " </td>\n ";
                html += "<td>";
                html += "<p style='margin-bottom: 0px'>Thỏa mãn tất cả các điều kiện sau đây</p>";
                List<MappingCriteria> listMappingCriteriaOfSubGroup = mappingCriteriaService.findAllByIdSubTargetGroup(subTargetGroup.getId());
                for (int j = 0; j < listMappingCriteriaOfSubGroup.size(); j++) {
                    html += mappingCriteriaService.detailCriteria(listMappingCriteriaOfSubGroup.get(j).getSelectedValue());
                    html += "</br>\n";
                }
                html += "</td>\n";
                if (subTargetGroup.getPriority() != null) {
                    html += "<td>" + subTargetGroup.getPriority() + "</td>";
                } else {
                    html += "<td>N/A</td>";
                }
                if (subTargetGroup.getQuantityMsisdn() == null) html += "<td>0 thuê bao</td>";
                else html += "<td>" + subTargetGroup.getQuantityMsisdn() + " thuê bao</td>";
                if (subTargetGroup.getRatio() == null) html += "<td>100%</td>";
                else html += "<td>" + subTargetGroup.getRatio() + " %</td>";
                html += "</tr>\n";

                /*=======================================*/
                Long accountId = subTargetGroup.getAccountSendingId();
                logger.info("account id : " + accountId);
                SendingAccount sendingAccount = sendingAccountService.findById(subTargetGroup.getAccountSendingId()).get();
                PackageData packageData = packageDataService.findById(subTargetGroup.getPackageDataId()).get();
                ViewSmsContentDTO smsContent = smsContentService.findByIdToView(subTargetGroup.getContentId());

                htmlContent += "<div class=\"col-md-6 p-r-10\">";
                htmlContent += "<label><b>" + subTargetGroup.getName() + "</b></label>";
                htmlContent += "<div class=\"ov_col_mes\">\n" +
                        "                                                    <div class=\"row\">\n" +
                        "                                                        <div class=\"col-md-6\">\n" +
                        "                                                            <label>Kênh truyền thông</label>\n" +
                        "                                                            <p>SMS</p>\n" +
                        "                                                        </div>\n" +
                        "                                                        <div class=\"col-md-6\">\n" +
                        "                                                            <label>Tài khoản gửi tin</label>\n" +
                        "                                                            <p>" + sendingAccount.getSenderAccount() + "</p>\n" +
                        "                                                        </div>\n" +
                        "                                                    </div>\n" +
                        "                                                    <div class=\"row\">\n" +
                        "                                                        <div class=\"col-md-6\">\n" +
                        "                                                            <label>Gói cước</label>\n" +
                        "                                                            <p>" + packageData.getPackageName() + "</p>\n" +
                        "                                                        </div>\n" +
                        "                                                    </div>\n" +
                        "                                                    <div class=\"row\">\n" +
                        "                                                        <div class=\"col-md-12\">\n" +
                        "                                                            <label>Nội dung</label>\n" +
                        "                                                            <p>" + smsContent.getMessage() + "</p>\n" +
                        "                                                        </div>\n" +
                        "                                                    </div>\n" +
                        "                                                </div>" +
                        "                                                </div>";
            }
            listSubGroupToView.add(html);
            listContentToView.add(htmlContent);
        } else if (campaignDTO.getSubTargetGroupRadio().equals("no")) {
            int channel = campaignDTO.getChannel();
            if (channel == 1) {
                ViewSmsContentDTO smsContent = smsContentService.findByIdToView(campaignDTO.getContentId());
                SendingAccount sendingAccount = sendingAccountService.findById(campaignDTO.getSendingAccountId()).get();
                PackageData packageData = packageDataService.findById(campaignDTO.getPackageDataId()).get();

                htmlContent += "<div class=\"col-md-6 p-r-10\">";
                htmlContent += "<div class=\"ov_col_mes\">\n" +
                        "                                                    <div class=\"row\">\n" +
                        "                                                        <div class=\"col-md-6\">\n" +
                        "                                                            <label>Kênh truyền thông</label>\n" +
                        "                                                            <p>SMS</p>\n" +
                        "                                                        </div>\n" +
                        "                                                        <div class=\"col-md-6\">\n" +
                        "                                                            <label>Tài khoản gửi tin</label>\n" +
                        "                                                            <p>" + sendingAccount.getSenderAccount() + "</p>\n" +
                        "                                                        </div>\n" +
                        "                                                    </div>\n" +
                        "                                                    <div class=\"row\">\n" +
                        "                                                        <div class=\"col-md-6\">\n" +
                        "                                                            <label>Gói cước</label>\n" +
                        "                                                            <p>" + packageData.getPackageName() + "</p>\n" +
                        "                                                        </div>\n" +
                        "                                                    </div>\n" +
                        "                                                    <div class=\"row\">\n" +
                        "                                                        <div class=\"col-md-12\">\n" +
                        "                                                            <label>Nội dung</label>\n" +
                        "                                                            <p>" + smsContent.getMessage() + "</p>\n" +
                        "                                                        </div>\n" +
                        "                                                    </div>\n" +
                        "                                                </div>" +
                        "                                                </div>";
                listContentToView.add(htmlContent);
            }
        }

        String sendingTimeLimitChannel = campaignDTO.getSendingTimeLimitChannel();
        String disableMessageLimit = campaignDTO.getDisableMessageLimit();
        boolean checkSendingTimeLimitChannel = true;
        boolean checkSendingTimeLimitZalo = false;
        boolean checkSendingTimeLimitDigiLife = false;
        boolean checkSendingTimeLimitEmail = false;

        boolean checkdisableMessageLimitChannel = true;
        boolean checkdisableMessageLimitZalo = false;
        boolean checkdisableMessageLimitDigiLife = false;
        boolean checkdisableMessageLimitEmail = false;
        if (sendingTimeLimitChannel != null && sendingTimeLimitChannel.length() > 0) {
            JSONObject jsonObject = new JSONObject(sendingTimeLimitChannel);
            checkSendingTimeLimitZalo = jsonObject.getBoolean("zalo");
            checkSendingTimeLimitDigiLife = jsonObject.getBoolean("digilife");
            checkSendingTimeLimitEmail = jsonObject.getBoolean("email");
            String sendingLimitChannel = "";
            if (checkSendingTimeLimitZalo == true) {
                sendingLimitChannel += "zalo, ";
            }
            if (checkSendingTimeLimitDigiLife == true) {
                sendingLimitChannel += "digilife, ";
            }
            if (checkSendingTimeLimitEmail == true) {
                sendingLimitChannel += "email, ";
            }
            model.addAttribute("sendingLimitChannel", sendingLimitChannel.substring(0, sendingLimitChannel.length() - 2));
        } else {
            checkSendingTimeLimitChannel = false;
        }

        if (disableMessageLimit != null && disableMessageLimit.length() > 0) {
            JSONObject jsonObject = new JSONObject(disableMessageLimit);
            checkdisableMessageLimitZalo = jsonObject.getBoolean("zalo");
            checkdisableMessageLimitDigiLife = jsonObject.getBoolean("digilife");
            checkdisableMessageLimitEmail = jsonObject.getBoolean("email");
            String messageLimitChannel = "";
            if (checkdisableMessageLimitZalo == true) {
                messageLimitChannel += "zalo, ";
            }
            if (checkdisableMessageLimitDigiLife == true) {
                messageLimitChannel += "digilife, ";
            }
            if (checkdisableMessageLimitEmail == true) {
                messageLimitChannel += "email, ";
            }
            model.addAttribute("messageLimitChannel", messageLimitChannel.substring(0, messageLimitChannel.length() - 2));

        } else {
            checkdisableMessageLimitChannel = false;
        }
        if (checkSendingTimeLimitChannel == false && checkdisableMessageLimitChannel == false) {
            Integer disablePolicy = 0;
            model.addAttribute("disablePolicy", disablePolicy);
        }
        String startDate = AppUtils.convertDateToString(campaignDTO.getStartDate(), "dd-MM-yyyy");
        if (campaignDTO.getEndDate() != null) {
            String endDate = AppUtils.convertDateToString(campaignDTO.getEndDate(), "dd-MM-yyyy");
            String timeSendCampaign = "Từ " + startDate + " đến " + endDate;
            model.addAttribute("timeSendCampaign", timeSendCampaign);
        } else {
            String timeSendCampaign = startDate;
            model.addAttribute("timeSendCampaign", timeSendCampaign);
        }

        if (campaignDTO.getTypeCampaign() == 2) {
            String frequency = campaignManagerService.getFrequency(campaignDTO.getFreeCfg());
            model.addAttribute("frequency", frequency);
        }
        UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String roleLogin = permissionUtils.getUserRole();
        model.addAttribute("roleLogin", roleLogin);
        model.addAttribute("currentUser", currentUser.getUsername());
        model.addAttribute("userName", campaignDTO.getCreatedUser());

        /*model.addAttribute("checkSendingTimeLimitZalo", checkSendingTimeLimitZalo);
        model.addAttribute("checkSendingTimeLimitDigiLife", checkSendingTimeLimitDigiLife);
        model.addAttribute("checkSendingTimeLimitEmail", checkSendingTimeLimitEmail);
        model.addAttribute("checkdisableMessageLimitZalo", checkdisableMessageLimitZalo);
        model.addAttribute("checkdisableMessageLimitDigiLife", checkdisableMessageLimitDigiLife);
        model.addAttribute("checkdisableMessageLimitEmail", checkdisableMessageLimitEmail);*/

        model.addAttribute("checkSendingTimeLimitChannel", checkSendingTimeLimitChannel);
        model.addAttribute("checkdisableMessageLimitChannel", checkdisableMessageLimitChannel);
        model.addAttribute("listSubGroupToView", listSubGroupToView);
        model.addAttribute("listContentToView", listContentToView);
        if (type == 1) {
            model.addAttribute("type", "Sự kiện");
            model.addAttribute("typeCode", "1");

        } else if (type == 2) {
            model.addAttribute("type", "Tần suất");
            model.addAttribute("typeCode", "2");
        }

        return "frequency-campaign/detail-campaign";
    }

    @PreAuthorize("hasAuthority('view:frequency:campaign')")
    @RequestMapping(value = "getHtmlField", method = RequestMethod.GET)
    public @ResponseBody
    String getHtmlField(@RequestParam(required = true) Long id) throws JsonProcessingException {
        logger.info("--- Action get html field ---");
        String htmlField = targetGroupService.getHtmlField(id);
        return htmlField;
    }

    @PreAuthorize("hasAuthority('export:detail:campaign')")
    @GetMapping("/exportpdf/{id}")
    public String exportCampaign(Model model, @PathVariable("id") Long id, @RequestParam("type") Integer type) {
        CampaignDTO campaignDTO = new CampaignDTO();
        if (type == 2) {
            Optional<FrequencyCampaign> frequencyCampaignOptional = frequencyCampaignService.findById(id);
            if (frequencyCampaignOptional.isPresent()) {
                FrequencyCampaign frequencyCampaign = frequencyCampaignOptional.get();
                campaignDTO = new CampaignDTO().toDTO(frequencyCampaign);
            }
        } else if (type == 1) {
            Optional<EventCampaign> eventCampaignOptional = eventCampaignService.findById(id);
            if (eventCampaignOptional.isPresent()) {
                EventCampaign eventCampaign = eventCampaignOptional.get();
                campaignDTO = new CampaignDTO().toDTO(eventCampaign);
            }
        }
        model.addAttribute("type:", type);
        logger.info("Type Campaign: " + campaignDTO.getTypeCampaign());

        int typeBlacklist = 0;
        String pathBlackList = campaignDTO.getBlackListPathFile();
        Long idGroupBlackList = campaignDTO.getBlackListTargetGroupId();
        if (campaignDTO.getTypeInputBlacklist() == 3) {
            typeBlacklist = 3;
        } else if (campaignDTO.getTypeInputBlacklist() == 2) {
            typeBlacklist = 2;
            Optional<TargetGroup> targetGroupBlacklist = targetGroupService.findById(campaignDTO.getBlackListTargetGroupId());
            model.addAttribute("groupBlacklistName", targetGroupBlacklist.get().getName());
        }
        model.addAttribute("typeBlacklist", typeBlacklist);

        model.addAttribute("campaign", campaignDTO);
        model.addAttribute("status", campaignDTO.getStatus());
        model.addAttribute("statusOld", campaignDTO.getStatusOld());
        String sdate = AppUtils.convertDateToString(campaignDTO.getStartDate(), "dd/MM/yyyy");
        if (campaignDTO.getEndDate() != null) {
            String edate = AppUtils.convertDateToString(campaignDTO.getEndDate(), "dd/MM/yyyy");
            model.addAttribute("endDate", edate);
        }
        model.addAttribute("name", campaignDTO.getCampaignName());
        model.addAttribute("startDate", sdate);

        model.addAttribute("campaignId", campaignDTO.getCampaignId());

        if (campaignDTO.getTypeCampaign() == 1) {
            Integer eventCycle = campaignDTO.getEventCycle();
            Integer eventLimitPerday = campaignDTO.getEventLimitPerDay();
            if (eventCycle != null) {
                model.addAttribute("eventCycle", eventCycle + " Ngày");
            } else {
                model.addAttribute("eventCycle", "N/A");

            }
            if (eventLimitPerday != null) {
                model.addAttribute("eventLimitPerday", eventLimitPerday + " sự kiện");
            } else {
                model.addAttribute("eventLimitPerday", "N/A");

            }
            Optional<BigdataEvent> bigdataEvent = bigdataEventService.findById(campaignDTO.getEventId());
            String event = campaignDTO.getEventCondition();
            JSONArray jsonArrayEvent = new JSONArray(event);
            List<String> listEventToView = new ArrayList<>();

            if(campaignDTO.getEventId() == 2l){
                JSONObject oJSON = new JSONObject();
                for(int temp = 0; temp < jsonArrayEvent.length(); temp ++){
                    if(jsonArrayEvent.getJSONObject(temp).getString("conditionName").equals("amount")) oJSON = jsonArrayEvent.getJSONObject(temp);
                }
                model.addAttribute("typeEvent", bigdataEvent.get().getEventName() + " " + oJSON.getString("value") + " VNĐ");
            }else model.addAttribute("typeEvent", bigdataEvent.get().getEventName());

            String htmlEventConditon = "";
            if (campaignDTO.getEventConditionRule() == 0) {
                htmlEventConditon = "<p style='margin-bottom: 0px'>Không sử dụng điều kiện</p>";
            } else if (campaignDTO.getEventConditionRule() == 1) {
                htmlEventConditon = "<p style='margin-bottom: 0px'>Thỏa mãn một trong số các điều kiện</p>";
            } else if (campaignDTO.getEventConditionRule() == 2) {
                htmlEventConditon = "<p style='margin-bottom: 0px'>Thỏa mãn tất cả điều kiện</p>";
            }

            listEventToView.add(htmlEventConditon);
            for (int i = 0; i < jsonArrayEvent.length(); i++) {
                String htmlEventToView = "";
                htmlEventToView += "<p style='margin-bottom: 0px; font-weight: lighter'>";
                htmlEventToView += eventCampaignService.detailConditionEvent(campaignDTO.getEventId(), jsonArrayEvent.getJSONObject(i));
                htmlEventToView += "</p>";
                listEventToView.add(htmlEventToView);
            }
            model.addAttribute("listEventToView", listEventToView);
        }
        List<String> listMainCriteriaToView = new ArrayList<>();
        if (campaignDTO.getTypeTargetGroup() == 1 || campaignDTO.getTypeTargetGroup() == 4) {
            List<MappingCriteria> listMainCriteria = mappingCriteriaService.findAllByIdTargetGroup(campaignDTO.getIdTargetGroup());

            boolean addConditionLevel1 = false;
            boolean addConditionLevel2 = false;
            boolean addConditionLevel3 = false;

            for (int i = 0; i < listMainCriteria.size(); i++) {
                String html = "";
                MappingCriteria row = listMainCriteria.get(i);
                if (row.getLevelCriteria() == 1) {
                    if (!addConditionLevel1) {
                        if (row.getType() == 1)
                            html += "<p style='margin-bottom: 0px'>Thỏa mãn tất cả các điều kiện sau đây</p>";
                        else
                            html += "<p style='margin-bottom: 0px' >Thỏa mãn một trong số các điều kiện sau đây</p>";
                        addConditionLevel1 = true;
                    }
                    html += "<p style='margin-bottom: 0px; font-weight: lighter'>+";
                    html += mappingCriteriaService.detailCriteria(row.getSelectedValue());
                    html += "</p>";
                    addConditionLevel2 = false;
                } else if (row.getLevelCriteria() == 2) {
                    if (!addConditionLevel2) {
                        if (row.getType() == 1)
                            html += "<p style='margin-bottom: 0px; text-indent: 2em;'>Thỏa mãn tất cả các điều kiện sau đây</p>";
                        else
                            html += "<p style='margin-bottom: 0px; text-indent: 2em;' >Thỏa mãn một trong số các điều kiện sau đây</p>";
                        addConditionLevel2 = true;
                    }

                    html += "<p style='margin-bottom: 0px; text-indent: 2em; font-weight: lighter'>+";
                    html += mappingCriteriaService.detailCriteria(row.getSelectedValue());
                    html += "</p>";
                    addConditionLevel3 = false;
                } else if (row.getLevelCriteria() == 3) {
                    if (!addConditionLevel3) {
                        if (row.getType() == 1)
                            html += "<p style='margin-bottom: 0px; text-indent: 4em;'>Thỏa mãn tất cả các điều kiện sau đây</p>";
                        else
                            html += "<p style='margin-bottom: 0px; text-indent: 4em;' >Thỏa mãn một trong số các điều kiện sau đây</p>";
                        addConditionLevel3 = true;
                    }
                    html += "<p style='margin-bottom: 0px; text-indent: 4em; font-weight: lighter'>+";
                    html += mappingCriteriaService.detailCriteria(row.getSelectedValue());
                    html += "</p>";
                }
                listMainCriteriaToView.add(html);
            }

        }
        if (campaignDTO.getTypeTargetGroup() == 6) {
            Optional<TargetGroup> targetGroup = targetGroupService.findById(campaignDTO.getIdTargetGroup());
            if (targetGroup.isPresent()) {
                model.addAttribute("targetGroupOldName", targetGroup.get().getName());
            }
        }
        model.addAttribute("listMainCriteriaToView", listMainCriteriaToView);
        List<String> listSubGroupToView = new ArrayList<>();
        List<String> listContentToView = new ArrayList<>();
        String html = "";
        String htmlContent = "";
        Integer subTarget = null;
        if (campaignDTO.getSubTargetGroupRadio().equals("yes")) {
            subTarget = 1;
        } else if (campaignDTO.getSubTargetGroupRadio().equals("no")) {
            subTarget = 0;
        }
        model.addAttribute("subTarget", subTarget);
        if (campaignDTO.getSubTargetGroupRadio().equals("yes")) {
            List<SubTargetGroup> listSubGroup = subTargetGroupService.findByCampaignId(campaignDTO.getCampaignId());
            SubTargetGroup subTargetGroup;

            for (int i = 0; i < listSubGroup.size(); i++) {
                subTargetGroup = listSubGroup.get(i);
                html += "<tr>";
                html += "<td>" + subTargetGroup.getName() + " </td>\n ";
                html += "<td>";
                html += "<p style='margin-bottom: 0px'>Thỏa mãn tất cả các điều kiện sau đây</p>";
                List<MappingCriteria> listMappingCriteriaOfSubGroup = mappingCriteriaService.findAllByIdSubTargetGroup(subTargetGroup.getId());
                for (int j = 0; j < listMappingCriteriaOfSubGroup.size(); j++) {
                    html += mappingCriteriaService.detailCriteria(listMappingCriteriaOfSubGroup.get(j).getSelectedValue());
                    html += "</br>\n";
                }
                html += "</td>\n";
                if (subTargetGroup.getPriority() != null) {
                    html += "<td>" + subTargetGroup.getPriority() + "</td>";
                } else {
                    html += "<td>N/A</td>";
                }
                if (subTargetGroup.getQuantityMsisdn() == null) html += "<td>0 thuê bao</td>";
                else html += "<td>" + subTargetGroup.getQuantityMsisdn() + " thuê bao</td>";
                if (subTargetGroup.getRatio() == null) html += "<td>100 %</td>";
                else html += "<td>" + subTargetGroup.getRatio() + " %</td>";
                html += "</tr>\n";

                /*=======================================*/
                Long accountId = subTargetGroup.getAccountSendingId();
                logger.info("account id : " + accountId);
                SendingAccount sendingAccount = sendingAccountService.findById(subTargetGroup.getAccountSendingId()).get();
                PackageData packageData = packageDataService.findById(subTargetGroup.getPackageDataId()).get();
                ViewSmsContentDTO smsContent = smsContentService.findByIdToView(subTargetGroup.getContentId());

                htmlContent += "<div class=\"col-md-6 p-r-10\">";
                htmlContent += "<label><b>" + subTargetGroup.getName() + "</b></label>";
                htmlContent += "<div class=\"ov_col_mes\">\n" +
                        "                                                    <div class=\"row\">\n" +
                        "                                                        <div class=\"col-md-6\">\n" +
                        "                                                            <label>Kênh truyền thông</label>\n" +
                        "                                                            <p>SMS</p>\n" +
                        "                                                        </div>\n" +
                        "                                                        <div class=\"col-md-6\">\n" +
                        "                                                            <label>Tài khoản gửi tin</label>\n" +
                        "                                                            <p>" + sendingAccount.getSenderAccount() + "</p>\n" +
                        "                                                        </div>\n" +
                        "                                                    </div>\n" +
                        "                                                    <div class=\"row\">\n" +
                        "                                                        <div class=\"col-md-6\">\n" +
                        "                                                            <label>Gói cước</label>\n" +
                        "                                                            <p>" + packageData.getPackageName() + "</p>\n" +
                        "                                                        </div>\n" +
                        "                                                    </div>\n" +
                        "                                                    <div class=\"row\">\n" +
                        "                                                        <div class=\"col-md-12\">\n" +
                        "                                                            <label>Nội dung</label>\n" +
                        "                                                            <p>" + smsContent.getMessage() + "</p>\n" +
                        "                                                        </div>\n" +
                        "                                                    </div>\n" +
                        "                                                </div>" +
                        "                                                </div>";
            }
            listSubGroupToView.add(html);
            listContentToView.add(htmlContent);
        } else if (campaignDTO.getSubTargetGroupRadio().equals("no")) {
            int channel = campaignDTO.getChannel();
            if (channel == 1) {
                ViewSmsContentDTO smsContent = smsContentService.findByIdToView(campaignDTO.getContentId());
                SendingAccount sendingAccount = sendingAccountService.findById(campaignDTO.getSendingAccountId()).get();
                PackageData packageData = packageDataService.findById(campaignDTO.getPackageDataId()).get();

                htmlContent += "<div class=\"col-md-6 p-r-10\">";
                htmlContent += "<div class=\"ov_col_mes\">\n" +
                        "                                                    <div class=\"row\">\n" +
                        "                                                        <div class=\"col-md-6\">\n" +
                        "                                                            <label>Kênh truyền thông</label>\n" +
                        "                                                            <p>SMS</p>\n" +
                        "                                                        </div>\n" +
                        "                                                        <div class=\"col-md-6\">\n" +
                        "                                                            <label>Tài khoản gửi tin</label>\n" +
                        "                                                            <p>" + sendingAccount.getSenderAccount() + "</p>\n" +
                        "                                                        </div>\n" +
                        "                                                    </div>\n" +
                        "                                                    <div class=\"row\">\n" +
                        "                                                        <div class=\"col-md-6\">\n" +
                        "                                                            <label>Gói cước</label>\n" +
                        "                                                            <p>" + packageData.getPackageName() + "</p>\n" +
                        "                                                        </div>\n" +
                        "                                                    </div>\n" +
                        "                                                    <div class=\"row\">\n" +
                        "                                                        <div class=\"col-md-12\">\n" +
                        "                                                            <label>Nội dung</label>\n" +
                        "                                                            <p>" + smsContent.getMessage() + "</p>\n" +
                        "                                                        </div>\n" +
                        "                                                    </div>\n" +
                        "                                                </div>" +
                        "                                                </div>";
                listContentToView.add(htmlContent);
            }
        }

        String sendingTimeLimitChannel = campaignDTO.getSendingTimeLimitChannel();
        String disableMessageLimit = campaignDTO.getDisableMessageLimit();
        model.addAttribute("sendingTimeLimitChannel", sendingTimeLimitChannel);
        model.addAttribute("disableMessageLimit", disableMessageLimit);

        boolean checkSendingTimeLimitChannel = true;
        boolean checkSendingTimeLimitZalo = false;
        boolean checkSendingTimeLimitDigiLife = false;
        boolean checkSendingTimeLimitEmail = false;

        boolean checkdisableMessageLimitChannel = true;
        boolean checkdisableMessageLimitZalo = false;
        boolean checkdisableMessageLimitDigiLife = false;
        boolean checkdisableMessageLimitEmail = false;
        if (sendingTimeLimitChannel != null && sendingTimeLimitChannel.length() > 0) {
            JSONObject jsonObject = new JSONObject(sendingTimeLimitChannel);
            checkSendingTimeLimitZalo = jsonObject.getBoolean("zalo");
            checkSendingTimeLimitDigiLife = jsonObject.getBoolean("digilife");
            checkSendingTimeLimitEmail = jsonObject.getBoolean("email");
            String sendingLimitChannel = "";
            if (checkSendingTimeLimitZalo == true) {
                sendingLimitChannel += "zalo, ";
            }
            if (checkSendingTimeLimitDigiLife == true) {
                sendingLimitChannel += "digilife, ";
            }
            if (checkSendingTimeLimitEmail == true) {
                sendingLimitChannel += "email, ";
            }
            if (sendingLimitChannel != "") {
                model.addAttribute("sendingLimitChannel", "Khung giờ gửi tin từng kênh truyền thông ngoài nhà mạng:" + sendingLimitChannel.substring(0, sendingLimitChannel.length() - 2));
            }
        } else {
            checkSendingTimeLimitChannel = false;
        }

        if (disableMessageLimit != null && disableMessageLimit.length() > 0) {
            JSONObject jsonObject = new JSONObject(disableMessageLimit);
            checkdisableMessageLimitZalo = jsonObject.getBoolean("zalo");
            checkdisableMessageLimitDigiLife = jsonObject.getBoolean("digilife");
            checkdisableMessageLimitEmail = jsonObject.getBoolean("email");
            String messageLimitChannel = "";
            if (checkdisableMessageLimitZalo == true) {
                messageLimitChannel += "zalo, ";
            }
            if (checkdisableMessageLimitDigiLife == true) {
                messageLimitChannel += "digilife, ";
            }
            if (checkdisableMessageLimitEmail == true) {
                messageLimitChannel += "email, ";
            }
            if (messageLimitChannel != "") {
                model.addAttribute("messageLimitChannel", "Giới hạn thông điệp từng kênh truyền thông ngoài nhà mạng:" + messageLimitChannel.substring(0, messageLimitChannel.length() - 2));
            }

        } else {
            checkdisableMessageLimitChannel = false;
        }
        if (checkSendingTimeLimitChannel == false && checkdisableMessageLimitChannel == false) {
            Integer disablePolicy = 0;
            model.addAttribute("disablePolicy", disablePolicy);
        }
        String startDate = AppUtils.convertDateToString(campaignDTO.getStartDate(), "dd-MM-yyyy");
        if (campaignDTO.getEndDate() != null) {
            String endDate = AppUtils.convertDateToString(campaignDTO.getEndDate(), "dd-MM-yyyy");
            String timeSendCampaign = "Từ " + startDate + " đến " + endDate;
            model.addAttribute("timeSendCampaign", timeSendCampaign);
        } else {
            String timeSendCampaign = startDate;
            model.addAttribute("timeSendCampaign", timeSendCampaign);
        }

        if (campaignDTO.getTypeCampaign() == 2) {
            String frequency = campaignManagerService.getFrequency(campaignDTO.getFreeCfg());
            model.addAttribute("frequency", frequency);
        }
        UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String roleLogin = permissionUtils.getUserRole();
        model.addAttribute("roleLogin", roleLogin);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userName", campaignDTO.getCreatedUser());

        /*model.addAttribute("checkSendingTimeLimitZalo", checkSendingTimeLimitZalo);
        model.addAttribute("checkSendingTimeLimitDigiLife", checkSendingTimeLimitDigiLife);
        model.addAttribute("checkSendingTimeLimitEmail", checkSendingTimeLimitEmail);
        model.addAttribute("checkdisableMessageLimitZalo", checkdisableMessageLimitZalo);
        model.addAttribute("checkdisableMessageLimitDigiLife", checkdisableMessageLimitDigiLife);
        model.addAttribute("checkdisableMessageLimitEmail", checkdisableMessageLimitEmail);*/

        model.addAttribute("checkSendingTimeLimitChannel", checkSendingTimeLimitChannel);
        model.addAttribute("checkdisableMessageLimitChannel", checkdisableMessageLimitChannel);
        model.addAttribute("listSubGroupToView", listSubGroupToView);
        model.addAttribute("listContentToView", listContentToView);
        if (type == 1) {
            model.addAttribute("type", "Sự kiện");
            model.addAttribute("typeCode", "1");

        } else if (type == 2) {
            model.addAttribute("type", "Tần suất");
            model.addAttribute("typeCode", "2");
        }
        return "frequency-campaign/exportPdfFrequencyCampaign";
    }

    private FrequencyCampaignPending cloneFrequencyCampaign(FrequencyCampaign frequencyCampaign) {
        ModelMapper modelMapper = new ModelMapper();
        FrequencyCampaignPending frequencyCampaignPending = modelMapper.map(frequencyCampaign, FrequencyCampaignPending.class);
        return frequencyCampaignPending;
    }

    @GetMapping("/exportDataCustomer")
    public void exportDataCustomer(HttpServletResponse response, @RequestParam(required = false) Long id, @RequestParam(required = false) Long type) {
        logger.info("export data cusotmer with info: campaign id:" + id + " type: " + type);
        String campaignName = "";
        String filePath = "";
        String originalName = "";
        if (type == 1) {
            Optional<EventCampaign> eventCampaignOptional = eventCampaignService.findById(id);
            EventCampaign eventCampaign = eventCampaignOptional.get();
            campaignName = eventCampaign.getCampaignName();
            filePath = eventCampaign.getPathFileDataCustomer();
            originalName = eventCampaign.getOriginalNameFileDataCustomer();
        } else if (type == 2) {
            Optional<FrequencyCampaign> frequencyCampaignOptional = frequencyCampaignService.findById(id);
            FrequencyCampaign frequencyCampaign = frequencyCampaignOptional.get();
            campaignName = frequencyCampaign.getCampaignName();
            filePath = frequencyCampaign.getPathFileDataCustomer();
            originalName = frequencyCampaign.getOriginalNameFileDataCustomer();
        }

        File file = new File(filePath);
        byte[] data;
        try {
            String localDateTime = AppUtils.getLocalDateTimeByPattern();
            data = FileUtils.readFileToByteArray(file);
            String fileName = "";
            if((AppUtils.getFileExtension(originalName).equals(".csv"))
                || (AppUtils.getFileExtension(originalName).equals(".xls"))){
                fileName = originalName.substring(0,originalName.length() - 4);
            }else {
                fileName = originalName.substring(0,originalName.length() - 5);
            }

            String fullFileName = URLEncoder.encode(fileName, "UTF-8");
            response.setContentType("application/octet-stream; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-disposition", "attachment; filename=" + fullFileName+" "+localDateTime+".csv");
            response.setContentLength(data.length);
            InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(data));
            FileCopyUtils.copy(inputStream, response.getOutputStream());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.info("error when download file template!");
            logger.info(e);
        }
    }

    @GetMapping("/exportDataBlacklist")
    public void exportDataBlacklist(HttpServletResponse response, @RequestParam(required = false) Long id, @RequestParam(required = false) Long type) {
        logger.info("export data blacklist with info: campaign id:" + id + " type: " + type);
        String campaignName = "";
        String filePath = "";
        String originalName = "";
        if (type == 1) {
            Optional<EventCampaign> eventCampaignOptional = eventCampaignService.findById(id);
            EventCampaign eventCampaign = eventCampaignOptional.get();
            campaignName = eventCampaign.getCampaignName();
            filePath = eventCampaign.getBlackListPathFile();
            originalName = eventCampaign.getOriginalNameFileBlacklist();
        } else if (type == 2) {
            Optional<FrequencyCampaign> frequencyCampaignOptional = frequencyCampaignService.findById(id);
            FrequencyCampaign frequencyCampaign = frequencyCampaignOptional.get();
            campaignName = frequencyCampaign.getCampaignName();
            filePath = frequencyCampaign.getBlackListPathFile();
            originalName = frequencyCampaign.getOriginalNameFileBlacklist();
        }


        File file = new File(filePath);
        byte[] data;
        try {
            String localDateTime = AppUtils.getLocalDateTimeByPattern();
            data = FileUtils.readFileToByteArray(file);
            String fileName = "";
            if((AppUtils.getFileExtension(originalName).equals(".csv"))
                    || (AppUtils.getFileExtension(originalName).equals(".xls"))){
                fileName = originalName.substring(0,originalName.length() - 4);
            }else {
                fileName = originalName.substring(0,originalName.length() - 5);
            }
            String fullFileName = URLEncoder.encode(fileName, "UTF-8");
            response.setContentType("application/octet-stream; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-disposition", "attachment; filename=" + fullFileName + " " +localDateTime+".csv");
            response.setContentLength(data.length);
            InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(data));
            FileCopyUtils.copy(inputStream, response.getOutputStream());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.info("error when download file template!");
            logger.info(e);
        }
    }

    //============================ lay so luong thue bao cua nhom chinh ==============================
    @PostMapping("/countMSISDNFromMainGroupByFile")
    @ResponseBody
    public String countMSISDNFromMainGroupByFile(@RequestParam(required = false, name = "dataTargetGroup") MultipartFile dataTargetGroup,
                                                 @RequestParam(required = false, name = "typeInputBlacklist") Long typeInputBlacklist,
                                                 @RequestParam(required = false, name = "dataBlacklist") MultipartFile dataBlacklist,
                                                 @RequestParam(required = false, name = "idGroupBlacklist") String idGroupBlacklist) {
        logger.info("action count MSISDN from main group by file");
        SimpleResponseDTO res = new SimpleResponseDTO();
        res.setMessage("Upload thành công.");
        res.setCode(AppUtils.successCode);
        Long idBlacklist = null;
        if (idGroupBlacklist != null && idGroupBlacklist.length() > 0 && !idGroupBlacklist.equals("null"))
            idBlacklist = Long.parseLong(idGroupBlacklist);
        CountMSISDNDTO msisdn = campaignManagerService.countMSISDNFromMainGroupByFile(dataTargetGroup, typeInputBlacklist, dataBlacklist, idBlacklist);
        logger.info("msisdn: {}", msisdn);
        res.setData(msisdn);
        String result = AppUtils.ObjectToJsonResponse(res);
        return result;
    }

    @PostMapping("/countMSISDNFromMainGroupByJSONCriteria")
    @ResponseBody
    public String countMSISDNFromMainGroupByJSONCriteria(@RequestParam(required = false, name = "jsonMainGroup") String json,
                                                         @RequestParam(required = false, name = "typeInputBlacklist") Long typeInputBlacklist,
                                                         @RequestParam(required = false, name = "dataBlacklist") MultipartFile dataBlacklist,
                                                         @RequestParam(required = false, name = "idGroupBlacklist") String idGroupBlacklist) {
        logger.info("action count MSISDN from main group by JSON criteria");
        SimpleResponseDTO res = new SimpleResponseDTO();
        res.setMessage("Upload thành công.");
        res.setCode(AppUtils.successCode);
        Long idBlacklist = null;
        if (idGroupBlacklist != null && idGroupBlacklist.length() > 0 && !idGroupBlacklist.equals("null"))
            idBlacklist = Long.parseLong(idGroupBlacklist);
        CountMSISDNDTO msisdn = campaignManagerService.countMSISDNFromMainGroupByJsonCriteria(json, typeInputBlacklist, dataBlacklist, idBlacklist);
        logger.info("msisdn: {}", msisdn);
        res.setData(msisdn);
        String result = AppUtils.ObjectToJsonResponse(res);
        return result;
    }

    @PostMapping("/countMSISDNFromMainGroupByFileJoinJSONCriteria")
    @ResponseBody
    public String countMSISDNFromMainGroupByFileJoinJSONCriteria(@RequestParam(required = false, name = "dataTargetGroup") MultipartFile dataTargetGroup,
                                                                 @RequestParam(required = false, name = "jsonMainGroup") String json,
                                                                 @RequestParam(required = false, name = "typeInputBlacklist") Long typeInputBlacklist,
                                                                 @RequestParam(required = false, name = "dataBlacklist") MultipartFile dataBlacklist,
                                                                 @RequestParam(required = false, name = "idGroupBlacklist") String idGroupBlacklist) {
        logger.info("action count MSISDN from main group by file Join JSON criteria");
        SimpleResponseDTO res = new SimpleResponseDTO();
        res.setMessage("Upload thành công.");
        res.setCode(AppUtils.successCode);
        Long idBlacklist = null;
        if (idGroupBlacklist != null && idGroupBlacklist.length() > 0 && !idGroupBlacklist.equals("null"))
            idBlacklist = Long.parseLong(idGroupBlacklist);
        CountMSISDNDTO msisdn = campaignManagerService.countMSISDNFromMainGroupByFileJOINJsonCriteria(dataTargetGroup, json, typeInputBlacklist, dataBlacklist, idBlacklist);
        logger.info("msisdn: {}", msisdn);
        res.setData(msisdn);
        String result = AppUtils.ObjectToJsonResponse(res);
        return result;
    }

    @PostMapping("/countMSISDNFromMainGroupByGroupId")
    @ResponseBody
    public String countMSISDNFromMainGroupByGroupId(@RequestParam(required = false, name = "dataTargetGroup") Long dataTargetGroup,
                                                    @RequestParam(required = false, name = "typeInputBlacklist") Long typeInputBlacklist,
                                                    @RequestParam(required = false, name = "dataBlacklist") MultipartFile dataBlacklist,
                                                    @RequestParam(required = false, name = "idGroupBlacklist") String idGroupBlacklist) {
        logger.info("action count MSISDN from main group by id " + dataTargetGroup);
        SimpleResponseDTO res = new SimpleResponseDTO();
        res.setMessage("Upload thành công.");
        res.setCode(AppUtils.successCode);
        Long idBlacklist = null;
        if (idGroupBlacklist != null && idGroupBlacklist.length() > 0 && !idGroupBlacklist.equals("null"))
            idBlacklist = Long.parseLong(idGroupBlacklist);
        TargetGroup targetGroup = targetGroupService.findById(dataTargetGroup).get();
        CountMSISDNDTO msisdn = null;
        if (targetGroup.getChannel() == 1) {
            String json = targetGroup.getDataJson();
            msisdn = campaignManagerService.countMSISDNFromMainGroupByJsonCriteria(json, typeInputBlacklist, dataBlacklist, idBlacklist);
        } else if (targetGroup.getChannel() == 3) {
            String path = targetGroup.getPathFileMsisdn();
            msisdn = campaignManagerService.countMSISDNFromMainGroupByFile(path, typeInputBlacklist, dataBlacklist, idBlacklist);
        } else if (targetGroup.getChannel() == 4) {
            String path = targetGroup.getPathFileMsisdn();
            String json = targetGroup.getDataJson();
            msisdn = campaignManagerService.countMSISDNFromMainGroupByFileJOINJsonCriteria(path, json, typeInputBlacklist, dataBlacklist, idBlacklist);
        }
        logger.info("msisdn: {}", msisdn);
        res.setData(msisdn);
        String result = AppUtils.ObjectToJsonResponse(res);
        return result;
    }

    //================================ lay so luong thue bao cua nhom con ======================

    @PostMapping("/countMSISDNSubMainGroupByFile")
    @ResponseBody
    public String countMSISDNSubMainGroupByFile(@RequestParam(required = false, name = "dataTargetGroup") MultipartFile dataTargetGroup,
                                                @RequestParam(required = false, name = "typeInputBlacklist") Long typeInputBlacklist,
                                                @RequestParam(required = false, name = "dataBlacklist") MultipartFile dataBlacklist,
                                                @RequestParam(required = false, name = "idGroupBlacklist") String idGroupBlacklist,
                                                @RequestParam(required = false, name = "jsonSubGroup") String jsonSubGroup) {
        logger.info("action count MSISDN from sub group by file");
        SimpleResponseDTO res = new SimpleResponseDTO();
        res.setMessage("Upload thành công.");
        res.setCode(AppUtils.successCode);
        Long idBlacklist = null;
        if (idGroupBlacklist != null && idGroupBlacklist.length() > 0 && !idGroupBlacklist.equals("null"))
            idBlacklist = Long.parseLong(idGroupBlacklist);
        CountMSISDNDTO msisdn = campaignManagerService.countMSISDNFromSubGroupByFile(dataTargetGroup, typeInputBlacklist, dataBlacklist, idBlacklist, jsonSubGroup);
        logger.info("msisdn: {}", msisdn);
        res.setData(msisdn);
        String result = AppUtils.ObjectToJsonResponse(res);
        return result;
    }

    @PostMapping("/countMSISDNFromSubGroupByJSONCriteria")
    @ResponseBody
    public String countMSISDNFromSubGroupByJSONCriteria(@RequestParam(required = false, name = "jsonMainGroup") String json,
                                                        @RequestParam(required = false, name = "typeInputBlacklist") Long typeInputBlacklist,
                                                        @RequestParam(required = false, name = "dataBlacklist") MultipartFile dataBlacklist,
                                                        @RequestParam(required = false, name = "idGroupBlacklist") String idGroupBlacklist,
                                                        @RequestParam(required = false, name = "jsonSubGroup") String jsonSubGroup) {
        logger.info("action count MSISDN from sub group by JSON criteria");
        SimpleResponseDTO res = new SimpleResponseDTO();
        res.setMessage("Upload thành công.");
        res.setCode(AppUtils.successCode);
        Long idBlacklist = null;
        if (idGroupBlacklist != null && idGroupBlacklist.length() > 0 && !idGroupBlacklist.equals("null"))
            idBlacklist = Long.parseLong(idGroupBlacklist);
        CountMSISDNDTO msisdn = campaignManagerService.countMSISDNFromSubGroupByJsonCriteria(json, typeInputBlacklist, dataBlacklist, idBlacklist, jsonSubGroup);
        logger.info("msisdn: {}", msisdn);
        res.setData(msisdn);
        String result = AppUtils.ObjectToJsonResponse(res);
        return result;
    }

    @PostMapping("/countMSISDNSubMainGroupByFileJoinJSONCriteria")
    @ResponseBody
    public String countMSISDNSubMainGroupByFileJoinJSONCriteria(@RequestParam(required = false, name = "dataTargetGroup") MultipartFile dataTargetGroup,
                                                                @RequestParam(required = false, name = "jsonMainGroup") String json,
                                                                @RequestParam(required = false, name = "typeInputBlacklist") Long typeInputBlacklist,
                                                                @RequestParam(required = false, name = "dataBlacklist") MultipartFile dataBlacklist,
                                                                @RequestParam(required = false, name = "idGroupBlacklist") String idGroupBlacklist,
                                                                @RequestParam(required = false, name = "jsonSubGroup") String jsonSubGroup) {
        logger.info("action count MSISDN from sub group by file Join JSON criteria");
        SimpleResponseDTO res = new SimpleResponseDTO();
        res.setMessage("Upload thành công.");
        res.setCode(AppUtils.successCode);
        Long idBlacklist = null;
        if (idGroupBlacklist != null && idGroupBlacklist.length() > 0 && !idGroupBlacklist.equals("null"))
            idBlacklist = Long.parseLong(idGroupBlacklist);
        CountMSISDNDTO msisdn = campaignManagerService.countMSISDNFromSubGroupByFileJOINJsonCriteria(dataTargetGroup, json, typeInputBlacklist, dataBlacklist, idBlacklist, jsonSubGroup);
        logger.info("msisdn: {}", msisdn);
        res.setData(msisdn);
        String result = AppUtils.ObjectToJsonResponse(res);
        return result;
    }

    @PostMapping("/countMSISDNSubMainGroupByGroupId")
    @ResponseBody
    public String countMSISDNSubMainGroupByGroupId(@RequestParam(required = false, name = "dataTargetGroup") Long dataTargetGroup,
                                                   @RequestParam(required = false, name = "typeInputBlacklist") Long typeInputBlacklist,
                                                   @RequestParam(required = false, name = "dataBlacklist") MultipartFile dataBlacklist,
                                                   @RequestParam(required = false, name = "idGroupBlacklist") String idGroupBlacklist,
                                                   @RequestParam(required = false, name = "jsonSubGroup") String jsonSubGroup) {
        logger.info("action count MSISDN from sub group by group id = " + dataTargetGroup);
        SimpleResponseDTO res = new SimpleResponseDTO();
        res.setMessage("Upload thành công.");
        res.setCode(AppUtils.successCode);
        Long idBlacklist = null;
        if (idGroupBlacklist != null && idGroupBlacklist.length() > 0 && !idGroupBlacklist.equals("null"))
            idBlacklist = Long.parseLong(idGroupBlacklist);
        TargetGroup targetGroup = targetGroupService.findById(dataTargetGroup).get();
        CountMSISDNDTO msisdn = null;
        if (targetGroup.getChannel() == 1) {
            String json = targetGroup.getDataJson();
            msisdn = campaignManagerService.countMSISDNFromSubGroupByJsonCriteria(json, typeInputBlacklist, dataBlacklist, idBlacklist, jsonSubGroup);
        } else if (targetGroup.getChannel() == 3) {
            String path = targetGroup.getPathFileMsisdn();
            msisdn = campaignManagerService.countMSISDNFromSubGroupByFile(path, typeInputBlacklist, dataBlacklist, idBlacklist, jsonSubGroup);
        } else if (targetGroup.getChannel() == 4) {
            String path = targetGroup.getPathFileMsisdn();
            String json = targetGroup.getDataJson();
            msisdn = campaignManagerService.countMSISDNFromSubGroupByFileJOINJsonCriteria(path, json, typeInputBlacklist, dataBlacklist, idBlacklist, jsonSubGroup);
        }
        logger.info("msisdn: {}", msisdn);
        res.setData(msisdn);
        String result = AppUtils.ObjectToJsonResponse(res);
        return result;
    }

}
