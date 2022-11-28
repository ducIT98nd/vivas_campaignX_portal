package com.vivas.campaignx.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivas.campaignx.common.AppException;
import com.vivas.campaignx.common.AppUtils;
import com.vivas.campaignx.dto.CountMSISDNDTO;
import com.vivas.campaignx.dto.SimpleResponseDTO;
import com.vivas.campaignx.entity.BigdataCriteria;
import com.vivas.campaignx.entity.MappingCriteria;
import com.vivas.campaignx.entity.TargetGroup;
import com.vivas.campaignx.repository.BigdataCriteriaRepository;
import com.vivas.campaignx.repository.MappingCriteriaRepository;
import com.vivas.campaignx.repository.TargetGroupRepository;
import com.vivas.campaignx.service.MappingCriteriaService;
import com.vivas.campaignx.service.TargetGroupService;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Controller
@RequestMapping("/TargetGroupController")
public class TargetGroupController {
    protected final Logger logger = LogManager.getLogger(this.getClass().getName());

    @Value("${path_Template}")
    private String path_Template;

    @Autowired
    private TargetGroupRepository targetGroupRepository;

    @Autowired
    private TargetGroupService targetGroupService;

    @Autowired
    private BigdataCriteriaRepository bigdataCriteriaRepository;

    @Autowired
    private MappingCriteriaRepository mappingCriteriaRepository;

    @Autowired
    private MappingCriteriaService mappingCriteriaService;

    @Value("${path_data_target_group}")
    private String path_data_target_group;

    @RequestMapping("/CriteriaSetup")
    public String View() {
        return "TargetGroup/CriteriaSetup";
    }


    @RequestMapping("/GetCriteriaFormat")
    public @ResponseBody
    String GetCriteriaFormat(@Param("id") Long id) {
        Optional<BigdataCriteria> obj = bigdataCriteriaRepository.findById(id);
        return obj.get().getValue();
    }

    @RequestMapping("/GetRowLevel1Criteria")
    public String GetRowLevel1Criteria(Model model, @Param("currentId") Integer currentId, @Param("parentId") Integer parentId) {
        model.addAttribute("buttonIdLevel1", "buttonId_" + currentId);
        model.addAttribute("endRowIdLevel1", "endRowId_" + currentId);
        model.addAttribute("rowIdLevel1", "rowId_" + currentId);
        model.addAttribute("selectCriteriaID", "selectCriteriaID_" + currentId);
        model.addAttribute("level1", "level1_" + currentId);
        model.addAttribute("currentNodeID", currentId);
        model.addAttribute("parentNodeId", parentId);
        return "TargetGroup/rowLevel1Criteria";
    }

    @RequestMapping("/GetRowLevel1CriteriaEdit")
    public String GetRowLevel1CriteriaEdit(Model model, @Param("currentId") Integer currentId,
                                           @Param("parentId") Integer parentId,
                                           @Param("criteriaParentId") Long criteriaParentId) {
        model.addAttribute("buttonIdLevel1", "buttonId_" + currentId);
        model.addAttribute("endRowIdLevel1", "endRowId_" + currentId);
        model.addAttribute("rowIdLevel1", "rowId_" + currentId);
        model.addAttribute("selectCriteriaID", "selectCriteriaID_" + currentId);
        model.addAttribute("level1", "level1_" + currentId);
        model.addAttribute("currentNodeID", currentId);
        model.addAttribute("parentNodeId", parentId);
        model.addAttribute("criteriaParentId", criteriaParentId);
        return "TargetGroup/rowLevel1CriteriaEdit";
    }

    @RequestMapping("/GetRowLevel2Criteria")
    public String GetRowLevel2Criteria(Model model, @Param("currentId") Integer currentId, @Param("parentId") Integer parentId) {
        model.addAttribute("buttonIdLevel2", "buttonId_" + currentId);
        model.addAttribute("rowIdLevel2", "rowId_" + currentId);
        model.addAttribute("selectCriteriaID", "selectCriteriaID_" + currentId);
        model.addAttribute("level2", "level2_" + currentId);
        model.addAttribute("currentNodeID", currentId);
        model.addAttribute("parentNodeId", parentId);
        return "TargetGroup/rowLevel2Criteria";
    }

    @RequestMapping("/GetRowLevel2CriteriaEdit")
    public String GetRowLevel2CriteriaEdit(Model model, @Param("currentId") Integer currentId,
                                           @Param("parentId") Integer parentId,
                                           @Param("criteriaParentId") Long criteriaParentId) {
        model.addAttribute("buttonIdLevel2", "buttonId_" + currentId);
        model.addAttribute("rowIdLevel2", "rowId_" + currentId);
        model.addAttribute("selectCriteriaID", "selectCriteriaID_" + currentId);
        model.addAttribute("level2", "level2_" + currentId);
        model.addAttribute("currentNodeID", currentId);
        model.addAttribute("parentNodeId", parentId);
        List<MappingCriteria> mappingCriteriaList = mappingCriteriaRepository.findAllByParentId(criteriaParentId);
        for (MappingCriteria mappingCriteria : mappingCriteriaList) {
            model.addAttribute("typeLV2", mappingCriteria.getType());
        }
        return "TargetGroup/rowLevel2CriteriaEdit";
    }

    @RequestMapping("/GetRowLevel3Criteria")
    public String GetRowLevel3Criteria(Model model, @Param("currentId") Integer currentId, @Param("parentId") Integer parentId) {
        model.addAttribute("buttonIdLevel3", "buttonId_" + currentId);
        model.addAttribute("rowIdLevel3", "rowId_" + currentId);
        model.addAttribute("selectCriteriaID", "selectCriteriaID_" + currentId);
        model.addAttribute("level3", "level3_" + currentId);
        model.addAttribute("currentNodeID", currentId);
        model.addAttribute("parentNodeId", parentId);
        return "TargetGroup/rowLevel3Criteria";
    }

    @RequestMapping("/GetRowLevel3CriteriaEdit")
    public String GetRowLevel3CriteriaEdit(Model model, @Param("currentId") Integer currentId,
                                           @Param("parentId") Integer parentId,
                                           @Param("criteriaParentId") Long criteriaParentId
            , @Param("criteriaNode") String criteriaNode) {
        model.addAttribute("buttonIdLevel3", "buttonId_" + currentId);
        model.addAttribute("rowIdLevel3", "rowId_" + currentId);
        model.addAttribute("selectCriteriaID", "selectCriteriaID_" + currentId);
        model.addAttribute("level3", "level3_" + currentId);
        model.addAttribute("currentNodeID", currentId);
        model.addAttribute("parentNodeId", parentId);
        model.addAttribute("criteriaNode", criteriaNode);
        logger.info("criteriaParentIdLv3:{}", criteriaParentId);
        List<MappingCriteria> mappingCriteriaList = mappingCriteriaRepository.findAllByParentId(criteriaParentId);
        for (MappingCriteria mappingCriteria : mappingCriteriaList) {
            model.addAttribute("typeLV3", mappingCriteria.getType());
        }
        return "TargetGroup/rowLevel3CriteriaEdit";
    }

    @RequestMapping("/GetRowSecondLevel2Criteria")
    public String GetRowSecondLevel2Criteria(Model model, @Param("currentId") Integer currentId, @Param("parentId") Integer parentId) {
        model.addAttribute("buttonIdLevel2", "buttonId_" + currentId);
        model.addAttribute("rowIdLevel2", "rowId_" + currentId);
        model.addAttribute("selectCriteriaID", "selectCriteriaID_" + currentId);
        model.addAttribute("level2", "level2_" + currentId);
        model.addAttribute("currentNodeID", currentId);
        model.addAttribute("parentNodeId", parentId);
        return "TargetGroup/rowSecondLevel2Criteria";
    }

    @RequestMapping("/GetRowSecondLevel3Criteria")
    public String GetRowSecondLevel3Criteria(Model model, @Param("currentId") Integer currentId, @Param("parentId") Integer parentId) {
        model.addAttribute("buttonIdLevel3", "buttonId_" + currentId);
        model.addAttribute("rowIdLevel3", "rowId_" + currentId);
        model.addAttribute("selectCriteriaID", "selectCriteriaID_" + currentId);
        model.addAttribute("level3", "level3_" + currentId);
        model.addAttribute("currentNodeID", currentId);
        model.addAttribute("parentNodeId", parentId);
        return "TargetGroup/rowSecondLevel3Criteria";
    }

    @PreAuthorize("hasAuthority('create:targetgroup')")
    @GetMapping("/viewCreate")
    public String viewCreateTargetGroup() {
        return "TargetGroup/CreateTargetGroup";
    }

    @PreAuthorize("hasAuthority('create:targetgroup')")
    @PostMapping("/createTargetGroup")
    public String createTargetGroup(RedirectAttributes redirectAttributes, @RequestParam HashMap<String, String> reqParams,
                                    @RequestParam(required = false, name = "dataTargetGroup") MultipartFile dataTargetGroup,
                                    @RequestParam(name = "jsonData") String jsonData) {
        String name = (String) reqParams.get("nameTargetGroup");
        String description = (String) reqParams.get("description");
        String channel = (String) reqParams.get("channel");
        String status = (String) reqParams.get("status");
        String groupSize = (String) reqParams.get("groupSizeSave");
        String wholeNetwork = (String) reqParams.get("wholeNetworkSave");
        logger.info("channel" + channel);
        logger.info("groupSize" + groupSize);
        logger.info("wholeNetwork" + wholeNetwork);
        SimpleResponseDTO res = new SimpleResponseDTO();
        String result;
        try {
            logger.info("jsonData" + jsonData);
            Long targetGroupId = targetGroupService.save(name, description, channel, status, dataTargetGroup, jsonData, groupSize,wholeNetwork);
            Long idSub = null;
            if (channel.equals("1") || channel.equals("4")) {
                targetGroupService.saveCriteriaSetup(jsonData, targetGroupId,idSub);
            }

            res.setMessage("Thêm mới nhóm đối tượng thành công.");
            res.setCode(AppUtils.successCode);
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
                logger.error("Error insert new user group. Details: ", e);
                res.setMessage("Không thể kết nối tới máy chủ. Kiểm tra lại kết nối internet và thử lại!");
                res.setCode(AppUtils.errorCode);
                result = AppUtils.ObjectToJsonResponse(res);
                redirectAttributes.addFlashAttribute("result", result);
            }
        }
        return "redirect:/TagetGroupManagerController/targetGroupManager";
    }



    @RequestMapping(value = "/downloadFileTemplate", method = RequestMethod.GET)
    public void downloadFileTemplate(HttpServletResponse response, Model model) {
        File file = new File(path_Template);
        byte[] data;
        try {
            data = FileUtils.readFileToByteArray(file);
            String localDateTime = AppUtils.getLocalDateTimeByPattern();
            // Thiết lập thông tin trả về
            String fileName = URLEncoder.encode("Danh+sach+thue+bao_file+mau"+" "+localDateTime+".csv", "UTF-8");
            response.setContentType("application/octet-stream; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-disposition", "attachment; filename=" + fileName + " " +localDateTime);
            response.setContentLength(data.length);
            InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(data));
            FileCopyUtils.copy(inputStream, response.getOutputStream());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.info("error when download file template!");
            logger.info(e);
        }
    }

    @RequestMapping(value = "/checkTargetGroupName", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Boolean> checkTargetGroupName(Model model, @RequestParam(required = false) String name) {
        logger.info(String.format("action check user group: name=%s", name));
        Map<String, Boolean> result = new HashMap<>();
        Long a = targetGroupService.countByName(name);
        logger.info("count:", a);
        if (a > 0) result.put("data", true);
        else result.put("data", false);
        return result;
    }

    @RequestMapping(value = "/checkTargetGroupNameUpdate", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Boolean> checkTargetGroupNameUpdate(Model model, @RequestParam(required = false) String name, @RequestParam(required = false, name = "id") Long id) {
        logger.info(String.format("action check user group: name=%s", name));
        Map<String, Boolean> result = new HashMap<>();
        Long a = targetGroupService.countByNameNotInIds(name, id);
        logger.info("count:", a);
        if (a > 0) result.put("data", true);
        else result.put("data", false);
        return result;
    }

    @PreAuthorize("hasAuthority('update:targetgroup')")
    @GetMapping("viewEdit/{id}")
    public String viewEditTargetGroup(Model model, @PathVariable("id") Long id) {
        Optional<TargetGroup> targetGroupOptional = targetGroupService.findById(id);
        TargetGroup targetGroup = new TargetGroup();
        if (targetGroupOptional.isPresent()) {
            targetGroup = targetGroupOptional.get();
        }
        if (targetGroup.getChannel() == 1 || targetGroup.getChannel() == 4) {
            List<MappingCriteria> mappingCriteriaList = mappingCriteriaRepository.findAllByIdTargetGroupOrderByLevelCriteriaAscPositionAsc(id);
            model.addAttribute("mappingCriteriaList", mappingCriteriaList);
            if (mappingCriteriaList.size() > 0) {
                model.addAttribute("typeLV1",mappingCriteriaList.get(0).getType());
            }
        }
        model.addAttribute("targetGroup", targetGroup);
        return "TargetGroup/EditTargetGroup";
    }

    @PreAuthorize("hasAuthority('update:targetgroup')")
    @GetMapping("viewEditDetail/{id}")
    public String viewEditTargetGroupDetail(Model model, @PathVariable("id") Long id) {
        Optional<TargetGroup> targetGroupOptional = targetGroupService.findById(id);
        TargetGroup targetGroup = new TargetGroup();
        if (targetGroupOptional.isPresent()) {
            targetGroup = targetGroupOptional.get();
        }
        if (targetGroup.getChannel() == 1 || targetGroup.getChannel() == 4) {
            List<MappingCriteria> mappingCriteriaList = mappingCriteriaRepository.findAllByIdTargetGroupOrderByLevelCriteriaAscPositionAsc(id);
            model.addAttribute("mappingCriteriaList", mappingCriteriaList);
            if (mappingCriteriaList.size() > 0) {
                model.addAttribute("typeLV1",mappingCriteriaList.get(0).getType());
            }
        }
        model.addAttribute("targetGroup", targetGroup);
        return "TargetGroup/EditTargetGroupDetail";
    }

    @PreAuthorize("hasAuthority('update:targetgroup')")
    @PostMapping("/editTargetGroup")
    public String editTargetGroup(RedirectAttributes redirectAttributes, @RequestParam HashMap<String, String> reqParams,
                                  @RequestParam(required = false, name = "dataTargetGroup") MultipartFile dataTargetGroup,
                                  @RequestParam(name = "jsonData") String jsonData) {

        String name = (String) reqParams.get("nameTargetGroup");
        String description = (String) reqParams.get("description");
        String channel = (String) reqParams.get("channel");
        String status = (String) reqParams.get("status");
        String id = (String) reqParams.get("targetGroupId");
        String groupSize = (String) reqParams.get("groupSizeSave");
        String wholeNetwork = (String) reqParams.get("wholeNetworkSave");
        logger.info("channel" + channel);
        SimpleResponseDTO res = new SimpleResponseDTO();
        String result;
        try {
            logger.info("jsonData" + jsonData);
            Long targetGroupId = targetGroupService.update(id, name, description, channel, status, dataTargetGroup, jsonData,groupSize,wholeNetwork);
            if (channel.equals("1") || channel.equals("4")) {
                mappingCriteriaRepository.deleteAllByIdTargetGroup(targetGroupId);
                Long idSub = null;
                targetGroupService.saveCriteriaSetup(jsonData, targetGroupId,idSub);
            }
            res.setMessage("Chỉnh sửa nhóm đối tượng thành công.");
            res.setCode(AppUtils.successCode);
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
                logger.error("Error insert new user group. Details: ", e);
                res.setMessage("Không thể kết nối tới máy chủ. Kiểm tra lại kết nối internet và thử lại!");
                res.setCode(AppUtils.errorCode);
                result = AppUtils.ObjectToJsonResponse(res);
                redirectAttributes.addFlashAttribute("result", result);
            }
        }
        return "redirect:/TagetGroupManagerController/targetGroupManager";
    }


    @PreAuthorize("hasAuthority('update:targetgroup')")
    @PostMapping("/editTargetGroupDetail")
    public String editTargetGroupDetail(RedirectAttributes redirectAttributes, @RequestParam HashMap<String, String> reqParams,
                                  @RequestParam(required = false, name = "dataTargetGroup") MultipartFile dataTargetGroup,
                                  @RequestParam(name = "jsonData") String jsonData) {

        String name = (String) reqParams.get("nameTargetGroup");
        String description = (String) reqParams.get("description");
        String channel = (String) reqParams.get("channel");
        String status = (String) reqParams.get("status");
        String id = (String) reqParams.get("targetGroupId");
        String groupSize = (String) reqParams.get("groupSizeSave");
        String wholeNetwork = (String) reqParams.get("wholeNetworkSave");
        logger.info("channel" + channel);
        SimpleResponseDTO res = new SimpleResponseDTO();
        String result;
        try {
            logger.info("jsonData" + jsonData);
            Long targetGroupId = targetGroupService.update(id, name, description, channel, status, dataTargetGroup, jsonData,groupSize,wholeNetwork);
            if (channel.equals("1") || channel.equals("4")) {
                mappingCriteriaRepository.deleteAllByIdTargetGroup(targetGroupId);
                Long idSub = null;
                targetGroupService.saveCriteriaSetup(jsonData, targetGroupId,idSub);
            }
            res.setMessage("Chỉnh sửa nhóm đối tượng thành công.");
            res.setCode(AppUtils.successCode);
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
                logger.error("Error insert new user group. Details: ", e);
                res.setMessage("Không thể kết nối tới máy chủ. Kiểm tra lại kết nối internet và thử lại!");
                res.setCode(AppUtils.errorCode);
                result = AppUtils.ObjectToJsonResponse(res);
                redirectAttributes.addFlashAttribute("result", result);
            }
        }
        return "redirect:/TagetGroupManagerController/detailTargetGroup?id=" +id;
    }

    @PreAuthorize("hasAuthority('copy:targetgroup')")
    @GetMapping("copyTargetGroup/{id}")
    public String copyTargetGroup(Model model, @PathVariable("id") Long id) {
        Optional<TargetGroup> targetGroupOptional = targetGroupService.findById(id);
        TargetGroup targetGroup = new TargetGroup();
        if (targetGroupOptional.isPresent()) {
            targetGroup = targetGroupOptional.get();
        }
        if (targetGroup.getChannel() == 1 || targetGroup.getChannel() == 4) {
            List<MappingCriteria> mappingCriteriaList = mappingCriteriaRepository.findAllByIdTargetGroupOrderByLevelCriteriaAscPositionAsc(id);
            model.addAttribute("mappingCriteriaList", mappingCriteriaList);
            if (mappingCriteriaList.size() > 0) {
                model.addAttribute("typeLV1",mappingCriteriaList.get(0).getType());
            }
        }
        model.addAttribute("targetGroup", targetGroup);
        return "TargetGroup/copyTargetGroup";
    }

    @PreAuthorize("hasAuthority('copy:targetgroup')")
    @PostMapping("/saveCopyTargetGroup")
    public String saveCopyTargetGroup(RedirectAttributes redirectAttributes, @RequestParam HashMap<String, String> reqParams,
                                    @RequestParam(required = false, name = "dataTargetGroup") MultipartFile dataTargetGroup,
                                    @RequestParam(name = "jsonData") String jsonData) {
        String name = (String) reqParams.get("nameTargetGroup");
        String description = (String) reqParams.get("description");
        String channel = (String) reqParams.get("channel");
        String status = (String) reqParams.get("status");
        String groupSize = (String) reqParams.get("groupSizeSave");
        Long quantityMsisdn = null;
        if(groupSize != null && groupSize.length() > 0){
            quantityMsisdn = Long.parseLong(groupSize);
        }
        String wholeNetworkSave = (String) reqParams.get("wholeNetworkSave");
        Double wholeNetWork = null;
        if(wholeNetworkSave != null && wholeNetworkSave.length() > 0){
            wholeNetWork = Double.parseDouble(wholeNetworkSave);
        }
        String pathFileMsisdnOld = (String) reqParams.get("pathFileDataOld");
        SimpleResponseDTO res = new SimpleResponseDTO();
        String result;
        try {
            logger.info("jsonData" + jsonData);
            Long targetGroupId = targetGroupService.saveCopy(name, description, channel, status, dataTargetGroup, jsonData,quantityMsisdn,wholeNetWork,pathFileMsisdnOld);
            Long idSub = null;
            if (channel.equals("1") || channel.equals("4")) {
                targetGroupService.saveCriteriaSetup(jsonData, targetGroupId,idSub);
            }

            res.setMessage("Thêm mới nhóm đối tượng thành công.");
            res.setCode(AppUtils.successCode);
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
                logger.error("Error insert new user group. Details: ", e);
                res.setMessage("Không thể kết nối tới máy chủ. Kiểm tra lại kết nối internet và thử lại!");
                res.setCode(AppUtils.errorCode);
                result = AppUtils.ObjectToJsonResponse(res);
                redirectAttributes.addFlashAttribute("result", result);
            }
        }
        return "redirect:/TagetGroupManagerController/targetGroupManager";
    }
    @GetMapping("/findAllMappingCriteriaList/{targetGroupId}")
    public @ResponseBody
    List<MappingCriteria> findAllMappingCriteriaList(@PathVariable("targetGroupId") Long targetGroupId) {
        logger.info("action get list mapping with id: " + targetGroupId);
        List<MappingCriteria> mappingCriteriaList = mappingCriteriaRepository.findAllByIdTargetGroupOrderByLevelCriteriaAscPositionAsc(targetGroupId);
        return mappingCriteriaList;
    }


    @PreAuthorize("hasAuthority('export:msisdn:targetgroup')")
    @GetMapping("/downloadFileUpload/{targetGroupId}")
    public void downloadFileUpload(@PathVariable("targetGroupId") Long targetGroupId, HttpServletResponse response) {
        Optional<TargetGroup> targetGroupOptional = targetGroupService.findById(targetGroupId);
        TargetGroup targetGroup = new TargetGroup();
        if (targetGroupOptional.isPresent()) {
            targetGroup = targetGroupOptional.get();
        }
        File file = null;
        try {
            file = new File(targetGroup.getPathFileMsisdn());
        } catch (Exception e) {
            logger.info("file not found!");
            e.printStackTrace();
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
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            response.setContentLength(data.length);
            InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(data));
            FileCopyUtils.copy(inputStream, response.getOutputStream());

        } catch (Exception e) {
            logger.info("error when download file template!");
            logger.info(e);
        }
    }

    @RequestMapping(value = "getListCriteriaMapping", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String getListCriteriaMapping(@RequestParam(required = true) Long targetGroupId, Model model) throws JsonProcessingException, JsonProcessingException {
        logger.info("--- Action detail target group ---");
        List<MappingCriteria> mappingCriteria = mappingCriteriaRepository.getListByIdTargetGroup(targetGroupId);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(mappingCriteria);
    }

    //controller count MSISDN màn tạo mới nhóm đối tượng

    @PostMapping(value = "/countMSISDNFromTargetGroupByJSONCriteria")
    @ResponseBody
    public String countMSISDNFromTargetGroupByJSONCriteria(@RequestParam(required = false, name = "dataTargetGroup") String dataTargetGroup) {
        logger.info("action count MSISDN from target group by JSON criteria = {}", dataTargetGroup);
        SimpleResponseDTO res = new SimpleResponseDTO();
        res.setMessage("Upload thành công.");
        res.setCode(AppUtils.successCode);
        CountMSISDNDTO msisdn = targetGroupService.countMSISDNFromTargetGroupByJSONCriteria(dataTargetGroup);
        logger.info("msisdn: {}", msisdn);
        res.setData(msisdn);
        String result = AppUtils.ObjectToJsonResponse(res);
        return result;
    }

    @PostMapping("/countMSISDNFromTargetGroupByFile")
    @ResponseBody
    public String countMSISDNFromTargetGroupByFile(@RequestParam(required = false, name = "dataTargetGroup") MultipartFile dataTargetGroup) {
        logger.info("action count MSISDN from target group by file = {}", dataTargetGroup);
        SimpleResponseDTO res = new SimpleResponseDTO();
        res.setMessage("Upload thành công.");
        res.setCode(AppUtils.successCode);
        CountMSISDNDTO msisdn = targetGroupService.countMSISDNFromTargetGroupByFile(dataTargetGroup);
        res.setData(msisdn);
        String result = AppUtils.ObjectToJsonResponse(res);
        return result;
    }

    @PostMapping(value = "/countMSISDNFromTargetGroupByFileJOINJSONCriteria")
    @ResponseBody
    public String countMSISDNFromTargetGroupByFileJOINJSONCriteria( @RequestParam(required = false, name = "dataTargetGroup") MultipartFile dataTargetGroup,
                                                                    @RequestParam(required = false, name = "jsonData") String jsonData) {

        logger.info("action count MSISDN from target group by file = {} JOIN JSON criteria = {}", dataTargetGroup, jsonData);
        SimpleResponseDTO res = new SimpleResponseDTO();
        res.setMessage("Upload thành công.");
        res.setCode(AppUtils.successCode);
        CountMSISDNDTO msisdn = targetGroupService.countMSISDNFromTargetGroupByFileJOINJSONCriteria(dataTargetGroup, jsonData);
        res.setData(msisdn);
        String result = AppUtils.ObjectToJsonResponse(res);
        return result;
    }

    @RequestMapping(value = "/queryCountMSISDNByTargetGroupId", method = RequestMethod.GET)
    public @ResponseBody String queryCountMSISDN(@RequestParam(required = false) Long targetGroupID) {
        logger.info(String.format("action query MSISDN: targetGroupID=%s ", targetGroupID));
        SimpleResponseDTO res = new SimpleResponseDTO();
        String result;
        Optional<TargetGroup> optionalTargetGroup = targetGroupService.findById(targetGroupID);
        if(optionalTargetGroup.isPresent()){
            if(optionalTargetGroup.get().getChannel() == 3) {
                Long msisdn = AppUtils.readFileCSV(optionalTargetGroup.get().getPathFileMsisdn());
                res.setData(msisdn.intValue());

            }else if(optionalTargetGroup.get().getChannel() == 1){
                String response = targetGroupService.queryByJSONAndTargetGroupId(optionalTargetGroup.get().getDataJson(), targetGroupID);
                res.setData(0L);
            }else if(optionalTargetGroup.get().getChannel() == 4){
                String response = targetGroupService.queryByFileAndJsonAndTargetGroupId(targetGroupID, optionalTargetGroup.get().getDataJson(), optionalTargetGroup.get().getPathFileMsisdn());
                res.setData(0L);
            }
        }else {
            res.setMessage("Lỗi hệ thống. Không thể tìm thấy dữ liệu của nhóm đối tượng");
        }
        result = AppUtils.ObjectToJsonResponse(res);
        return result;
    }

    //controller count MSISDN màn chỉnh sửa nhóm đối tượng

    @PostMapping(value = "/countMSISDNFromEditTargetGroupByFile")
    @ResponseBody
    public String countMSISDNFromEditTargetGroupByFile( @RequestParam(required = false, name = "dataTargetGroup") MultipartFile dataTargetGroup,
                                                        @RequestParam(required = false, name = "targetGroupId") Long targetGroupId) {

        logger.info("action count MSISDN from edit target group by file = {} from target group id = {}", dataTargetGroup, targetGroupId);
        SimpleResponseDTO res = new SimpleResponseDTO();
        res.setMessage("Upload thành công.");
        res.setCode(AppUtils.successCode);
        CountMSISDNDTO msisdn = targetGroupService.countMSISDNFromEditTargetGroupByFile(dataTargetGroup, targetGroupId);
        res.setData(msisdn);
        String result = AppUtils.ObjectToJsonResponse(res);
        return result;
    }

    @PostMapping(value = "/countMSISDNFromEditTargetGroupByFileJOINJSONCriteria")
    @ResponseBody
    public String countMSISDNFromEditTargetGroupByFileJOINJSONCriteria( @RequestParam(required = false, name = "dataTargetGroup") MultipartFile dataTargetGroup,
                                                                        @RequestParam(required = false, name = "jsonCriteria") String jsonData,
                                                                        @RequestParam(required = false, name = "targetGroupId") Long targetGroupId) {

        logger.info("action count MSISDN edit from target group by file = {} JOIN JSON criteria = {}", dataTargetGroup, jsonData);
        SimpleResponseDTO res = new SimpleResponseDTO();
        res.setMessage("Upload thành công.");
        res.setCode(AppUtils.successCode);
        CountMSISDNDTO msisdn = targetGroupService.countMSISDNFromEditTargetGroupByFileJOINJSONCriteria(dataTargetGroup, jsonData, targetGroupId);
        res.setData(msisdn);
        String result = AppUtils.ObjectToJsonResponse(res);
        return result;
    }
}

