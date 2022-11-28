package com.vivas.campaignx.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vivas.campaignx.common.AppUtils;
import com.vivas.campaignx.dto.SimpleResponseDTO;
import com.vivas.campaignx.entity.MappingCriteria;
import com.vivas.campaignx.entity.TargetGroup;
import com.vivas.campaignx.entity.Users;
import com.vivas.campaignx.export.ExcelFileExporter;
import com.vivas.campaignx.service.*;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.util.IOUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping(value = "/TagetGroupManagerController")
public class TagetGroupManagerController {
    protected final Logger logger = LogManager.getLogger(this.getClass().getName());
    protected String pattern = "dd/MM/yyyy";

    @Value("${default_page_size}")
    private Integer defaultPageSize;

    @Autowired
    private TargetGroupService targetGroupService;

    @Autowired
    private MappingCriteriaService mappingCriteriaService;

    @Autowired
    private BigdataCriteriaService bigdataCriteriaService;

    @Autowired
    private EventCampaignService eventCampaignService;

    @Autowired
    private FrequencyCampaignService frequencyCampaignService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private PermissionUtils permissionUtils;

    private static final String topupEvent ="${campaignx.ductm}";

    @PreAuthorize("hasAuthority('list:targetgroup')")
    @RequestMapping(value = "targetGroupManager")
    public String targetGroupManager(Model model,
                                     @RequestParam(required = false) Integer pageSize,
                                     @RequestParam(required = false) Integer currentPage,
                                     @RequestParam(required = false) String targetName,
                                     @RequestParam(required = false) String createdUser,
                                     @RequestParam(required = false) String createdDate) {
        if (pageSize == null)
            pageSize = 20;
        if (currentPage == null)
            currentPage = 1;
        logger.info(String.format("search info: name=%s, createdUser=%s, createdDate=%s", targetName, createdUser, createdDate));

        Date createdDate1 = null;
        if(createdDate != null && createdDate != "") {
            createdDate1 = AppUtils.convertStringToDate(createdDate, pattern);
            createdDate = AppUtils.convertDateToString(createdDate1, pattern);
        }
        Page<TargetGroup> dataPage = null;
        UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        if (targetName == null  && createdUser == null  && createdDate == null ) {
            dataPage = targetGroupService.getAll(pageSize,currentPage);
        }else{
            dataPage = targetGroupService.getTargetGroup(pageSize,currentPage,targetName,createdUser,createdDate);
        }
        String roleName = permissionUtils.getUserRole();
        String userName = currentUser.getUsername();
        model.addAttribute("dataPage", dataPage);
        model.addAttribute("totalPages", dataPage.getTotalPages());
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("targetName",targetName);
        model.addAttribute("createdUser",createdUser);
        model.addAttribute("createdDate",createdDate);
        model.addAttribute("roleName",roleName);
        model.addAttribute("userName",userName);
        model.addAttribute("currentUser",currentUser.getUsername());

        return "TargetGroup/targetGroupManager";
    }

    @PreAuthorize("hasAuthority('export:list:targetgroup')")
    @RequestMapping(value = "exportTargetGroup")
    public void exportTargetGroup(HttpServletResponse response,
                                  @RequestParam(required = false) String targetName,
                                  @RequestParam(required = false) String createdUser,
                                  @RequestParam(required = false) String createdDate) throws IOException {

        logger.info(String.format("export info: name=%s, createdUser=%s, createdDate=%s", targetName, createdUser, createdDate));
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String dateString = df.format(date);
        String fileName = "Danh sach nhom doi tuong" + "_" + dateString + ".csv";
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        Date createdDate1 = null;
        UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        Optional<Users> optionalUsers = usersService.findUserByUsernameAndStatus(currentUser.getUsername());
        Users users = null;
        if(optionalUsers.isPresent()){
            users = optionalUsers.get();
        }

        if(createdDate != null && createdDate != "") {
            createdDate1 = AppUtils.convertStringToDate(createdDate, pattern);
            createdDate = AppUtils.convertDateToString(createdDate1, pattern);
        }
        List<TargetGroup> listTarget = null;
        if ((targetName == null || targetName.length()>0) && (createdUser == null || createdUser.length()>0) && createdDate == null)
            listTarget = targetGroupService.exportAll();
        else {
            listTarget = targetGroupService.exportTargetGroup(targetName, createdUser, createdDate);
        }
        ByteArrayInputStream stream = ExcelFileExporter.tagetGroupListToExcelFile(listTarget);
        IOUtils.copy(stream, response.getOutputStream());
    }

    @PreAuthorize("hasAuthority('delete:targetgroup')")
    @RequestMapping(value = "delete")
    public String delete(@RequestParam(required = true) Long id,
                                 @RequestParam(required = true) Integer channel,
                                 RedirectAttributes redirectAttributes) {
        logger.info("--- Action delete target group ---");
        String result;
        SimpleResponseDTO res = new SimpleResponseDTO();
        try {
            int countTargetInEventCampiagn = eventCampaignService.countTargetGroupByStatus(id);
            int countTagetInFrequencyCampaign = frequencyCampaignService.countTargetGroupByStatus(id);
            if(countTargetInEventCampiagn > 0 || countTagetInFrequencyCampaign > 0){
                res.setCode(AppUtils.errorCode);
                res.setMessage("Không thể xóa nhóm đối tượng được gán vào chiến dịch đang xử lý, chờ phê duyệt," +
                        " tạm dừng hoặc đang chạy." +
                        " Vui lòng thay đổi thông tin các chiến dịch trước khi xóa nhóm đối tượng.");
                result = AppUtils.ObjectToJsonResponse(res);
                redirectAttributes.addFlashAttribute("result", result);
                return "redirect:/TagetGroupManagerController/targetGroupManager";
            }
            targetGroupService.deleteTargetGroup(id);
            res.setCode(AppUtils.successCode);
            res.setMessage("Xóa nhóm đối tượng thành công");
            result = AppUtils.ObjectToJsonResponse(res);
            redirectAttributes.addFlashAttribute("result", result);
            return "redirect:/TagetGroupManagerController/targetGroupManager";
        }catch (Exception ex) {
                logger.error("Error while delete target group: ", ex);
            }
            logger.error("Khong tim thay target group id={}", id);
            res.setCode(AppUtils.errorCode);
            res.setMessage("Có lỗi xảy ra, vui lòng thử lại sau");
            result = AppUtils.ObjectToJsonResponse(res);
            redirectAttributes.addFlashAttribute("result", result);
            return "redirect:/TagetGroupManagerController/targetGroupManager";
        }

    @PreAuthorize("hasAuthority('detail:targetgroup')")
    @RequestMapping(value = "detailTargetGroup",method = RequestMethod.GET)
    public String detailTargetGroup(@RequestParam(required = true) Long id,Model model){
        logger.info("--- Action detail target group ---");
        Optional<TargetGroup> optionalTargetGroup = targetGroupService.findByIdAndStatusIsNot(id,-99);
        if(optionalTargetGroup.isPresent()){
            TargetGroup targetGroup = optionalTargetGroup.get();
            if(targetGroup.getDescription() == null || targetGroup.getDescription().length() == 0){
                model.addAttribute("description","N/A");
            }else {
                model.addAttribute("description",targetGroup.getDescription());
            }
            model.addAttribute("tagetGroup",targetGroup);
        }
        List<MappingCriteria> criteriaList = targetGroupService.getListCriteriaLevel1(id);
        if(criteriaList.size() > 0){
            MappingCriteria mappingCriteria = criteriaList.get(0);
            model.addAttribute("typeLevel1",mappingCriteria.getType());
        }
        UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String userName = currentUser.getUsername();
        String roleName = permissionUtils.getUserRole();
        model.addAttribute("roleName",roleName);
        model.addAttribute("userName",userName);
        model.addAttribute("currentUser",currentUser.getUsername());
        return "TargetGroup/detailTargetGroup";
    }

    @RequestMapping(value ="getHtmlField",method = RequestMethod.GET)
    public  @ResponseBody
    String getHtmlField(@RequestParam(required = true) Long id) throws JsonProcessingException {
        logger.info("--- Action get html field ---");
        String htmlField = targetGroupService.getHtmlField(id);
        return htmlField;
    }

    @RequestMapping(value ="getHtmlFieldCampaign",method = RequestMethod.GET)
    public  @ResponseBody
    String getHtmlFieldCampaign(@RequestParam(required = true) Long id) throws JsonProcessingException {
        logger.info("--- Action get html field ---");
        String htmlField = targetGroupService.getHtmlFieldCampaign(id);
        return htmlField;
    }

    @PreAuthorize("hasAuthority('export:msisdn:targetgroup')")
    @RequestMapping(value = "/DownloadFileUpload/{id}", method = RequestMethod.GET)
    public void DownloadFileUpload(@PathVariable("id") Long id,
                                   HttpServletResponse response, Model model) {
        logger.info("download file upload target group with info: " + id);
        Optional<TargetGroup> optionalTargetGroup = targetGroupService.findById(id);
        TargetGroup targetGroup = new TargetGroup();
        if(optionalTargetGroup.isPresent()){
            targetGroup = optionalTargetGroup.get();
        }
        File file = null;
        try {
            file = new File(targetGroup.getPathFileMsisdn());
        } catch (Exception e) {
            logger.error("file not found :"+e);
        }
        byte[] data;
        try {
            data = FileUtils.readFileToByteArray(file);
            String localDateTime = AppUtils.getLocalDateTimeByPattern();
            String fileName = null;
            String originalFileName = targetGroup.getOriginalNameFileDataCustomer();
            if((AppUtils.getFileExtension(originalFileName).equals(".csv"))
                    || (AppUtils.getFileExtension(originalFileName).equals(".xls"))){
                fileName = originalFileName.substring(0,originalFileName.length() - 4);
            }else {
                fileName = originalFileName.substring(0,originalFileName.length() - 5);
            }
            fileName += " " + localDateTime + ".csv";

            // Thiết lập thông tin trả về
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            response.setContentLength(data.length);
            InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(data));
            FileCopyUtils.copy(inputStream, response.getOutputStream());
        } catch (Exception e) {
            logger.error("error when download file template: "+e);
        }
    }
    @PreAuthorize("hasAuthority('export:detail:targetgroup')")
    @RequestMapping(value = "exportPdf",method = RequestMethod.GET)
    public String exportPdf(@RequestParam(required = true) Long id,Model model){
        logger.info("--- Action detail target group ---");
        Optional<TargetGroup> optionalTargetGroup = targetGroupService.findById(id);
        if(optionalTargetGroup.isPresent()){
            TargetGroup targetGroup = optionalTargetGroup.get();
            model.addAttribute("tagetGroup",targetGroup);
        }
        return "TargetGroup/exportPdf";
    }

    @GetMapping(value = "/getGroupSizeAndWholeNetwork")
    @ResponseBody
    public Map<String,Object> getGroupSizeAndWholeNetwork(@RequestParam(required = false, name = "channel") Integer channel,
                                              @RequestParam(required = false, name = "id") Long id) throws IOException {
        logger.info("=== get group size and whole network ===");
        Map<String,Object> map = new HashMap<String,Object>();
        Optional<TargetGroup> optionalTargetGroup = targetGroupService.findById(id);
        TargetGroup targetGroup = new TargetGroup();
        if(optionalTargetGroup.isPresent()){
            targetGroup = optionalTargetGroup.get();
        }
        SimpleResponseDTO res = new SimpleResponseDTO();
        String response = null;
        JSONObject jsonObject;
        Long groupSize = null;
        if(channel == 1){
            response = targetGroupService.queryByJSONAndTargetGroupId(targetGroup.getDataJson(), id);
            logger.info("response from api: " + response);

        }else if(channel == 3){
            groupSize = AppUtils.readFileCSV(targetGroup.getPathFileMsisdn());
            map.put("groupSize",groupSize);
            String response1 = targetGroupService.queryCountMSISDNTotal();
            JSONObject jsonObject1 = new JSONObject(response1);
            Long totalMsisdn = null;
            if(jsonObject1.getInt("code") == 0){
                totalMsisdn = jsonObject1.getLong("data");
            }else{
                totalMsisdn = 0L;
                logger.info("--- error when count msisdn: {}",jsonObject1.getString("message"));
            }
            double wholeNetWork;
            if(totalMsisdn == 0){
                wholeNetWork = 0.0;
            }else{
                wholeNetWork = (double) Math.round((((double )groupSize/totalMsisdn)*100) * 100) / 100;

            }
            targetGroup.setWholeNetwork(wholeNetWork);
            targetGroup.setQuantityMsisdn(groupSize);
            targetGroupService.saveTargetGroup(targetGroup);
            map.put("wholeNetWork",wholeNetWork);
        }else if(channel == 4){
            response = targetGroupService.queryByFileAndJsonAndTargetGroupId(id, targetGroup.getDataJson(), targetGroup.getPathFileMsisdn());
            logger.info("response from api: " + response);
        }

        return map;
    }

    @RequestMapping(value = "/countCampaign", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> countCampaign(Model model, @RequestParam Long targetGroupId) {
        logger.info("action count campaign by targetGroupId: " + targetGroupId);
        Map<String, Object> map = new HashMap<String, Object>();
        boolean check = targetGroupService.countCampaign(targetGroupId);
        logger.info("check: " + check);
        if (check) map.put("result", true);
        else map.put("result", false);
        return map;
    }


}
