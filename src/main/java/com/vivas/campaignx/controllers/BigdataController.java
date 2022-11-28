package com.vivas.campaignx.controllers;

import com.vivas.campaignx.common.AppUtils;
import com.vivas.campaignx.common.HttpUtil;
import com.vivas.campaignx.common.enums.ErrorCodeEnum;
import com.vivas.campaignx.config.Conf;
import com.vivas.campaignx.config.UserPrinciple;
import com.vivas.campaignx.dto.BigdataDTO;
import com.vivas.campaignx.dto.NotifyTargetGroupDTO;
import com.vivas.campaignx.dto.CountMSISDNDTO;
import com.vivas.campaignx.dto.SimpleResponseDTO;
import com.vivas.campaignx.dto.UserDTO;
import com.vivas.campaignx.entity.Notify;
import com.vivas.campaignx.entity.TargetGroup;
import com.vivas.campaignx.export.ExcelFileExporter;
import com.vivas.campaignx.repository.NotifyRepository;
import com.vivas.campaignx.service.TargetGroupService;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.util.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.concurrent.Executor;

@Controller

public class BigdataController {
    protected final Logger logger = LogManager.getLogger(this.getClass().getName());

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private NotifyRepository notifyRepository;

    @Autowired
    private TargetGroupService targetGroupService;

    @Autowired
    private HttpUtil httpUtil;

    @Value("${clickhouse.adapter.url}")
    private String clickHouseUrl;

    @Value("${clickhouse.adapter.exportMSISDNByTargetGroup}")
    private String exportMSISDNByTargetGroup;

    @Value("${clickhouse.adapter.exportMSISDNByTargetGroupAndFile}")
    private String exportMSISDNByTargetGroupAndFile;

    @Value("${clickhouse.adapter.countMSISDNByJSONCriteriaAndSaveToDB}")
    private String countMSISDNByJSONCriteriaAndSaveToDB;

    @Value("${clickhouse.adapter.countMSISDNByJSONCriteriaAndFileAndSaveToDB}")
    private String countMSISDNByJSONCriteriaAndFileAndSaveToDB;

    @Value("${clickhouse.adapter.getAllMainProduct}")
    private String getAllMainProduct;

    @Value("${clickhouse.adapter.getAllServiceCode}")
    private String getAllServiceCode;

    @Value("${clickhouse.adapter.getAllCurrentPackage}")
    private String getAllCurrentPackage;


    @Autowired
    @Qualifier(value="taskExec")
    Executor executor;

    @Autowired
    RedisTemplate redisTemplate;

    @RequestMapping(value = "/BigdataController/queryMSISDNAndExport", method = RequestMethod.GET)
    public @ResponseBody
    String queryCountMSISDNAndExport(@RequestParam(required = false) Long targetGroupID) {
        logger.info(String.format("action query MSISDN: targetGroupID=%s ", targetGroupID));
        SimpleResponseDTO res = new SimpleResponseDTO();
        String result;
        Optional<TargetGroup> optionalTargetGroup = targetGroupService.findById(targetGroupID);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        Long userId = ((UserPrinciple) principal).getUserId();
        if (optionalTargetGroup.isPresent()) {
            TargetGroup targetGroup = optionalTargetGroup.get();
                if (targetGroup.getChannel() == 1) {
                    String jsonData = targetGroup.getDataJson();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("targetGroupId", targetGroup.getId());
                    jsonObject.put("query", jsonData);
                    jsonObject.put("time", AppUtils.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
                    jsonObject.put("userId", userId);
                    String responseCL = httpUtil.PostWithJSON(clickHouseUrl + exportMSISDNByTargetGroup, jsonObject.toString());
                    logger.info("clickhouse adapter response: " + responseCL);
                    JSONObject jsonCL = new JSONObject(responseCL);
                    if (jsonCL.getInt("code") == 0) {
                        res.setMessage("Hệ thống đang xử lý dữ liệu. Danh sách thuê bao thuộc nhóm đối tượng sẽ được gửi về hộp tin của bạn trong vòng 24 giờ tiếp theo.");
                        res.setCode(ErrorCodeEnum.SUCCESS.getCode());
                    } else {
                        res.setMessage("Lỗi hệ thống. Không thể xuất danh sách thuê bao thuộc nhóm đối tượng");
                        res.setCode(ErrorCodeEnum.SYSTEM_ERROR.getCode());
                    }
                } else if (targetGroup.getChannel() == 4) {
                    String jsonData = targetGroup.getDataJson();

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("targetGroupId", targetGroup.getId());
                    jsonObject.put("query", jsonData);
                    jsonObject.put("time", AppUtils.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
                    jsonObject.put("userId", userId);
                    String responseCL = httpUtil.postWithJsonAndFile(clickHouseUrl + exportMSISDNByTargetGroupAndFile, jsonObject.toString(), targetGroup.getPathFileMsisdn());
                    logger.info("clickhouse adapter response: " + responseCL);
                    JSONObject jsonCL = new JSONObject(responseCL);
                    if (jsonCL.getInt("code") == 0) {
                        res.setMessage("Hệ thống đang xử lý dữ liệu. Danh sách thuê bao thuộc nhóm đối tượng sẽ được gửi về hộp tin của bạn trong vòng 24 giờ tiếp theo.");
                        res.setCode(ErrorCodeEnum.SUCCESS.getCode());
                    } else {
                        res.setMessage("Lỗi hệ thống. Không thể xuất danh sách thuê bao thuộc nhóm đối tượng");
                        res.setCode(ErrorCodeEnum.SYSTEM_ERROR.getCode());
                    }
                } else if (targetGroup.getChannel() == 3) {
                    Notify obj = new Notify();
                    obj.setSubject("Nhóm đối tượng");
                    obj.setContent("Danh sách thuê bao thuộc nhóm đối tượng " + targetGroup.getName() + " đã được xử lý xong. Vui lòng bấm vào <a style='display: inline !important; padding: 0px 0px !important' href=\"/BigdataController/exportFileDataCustomer?targetGroupId=" + targetGroup.getId() + "\">đây</a> để tải xuống");
                    obj.setNotifyToUserId(userId);
                    obj.setStatus(0l);
                    obj.setCreatedDate(new Date());
                    notifyRepository.save(obj);

                    BigdataDTO bigdataDTO = new BigdataDTO();

                    this.template.convertAndSend("/topic/notify", bigdataDTO);
                    res.setMessage("Hệ thống đang xử lý dữ liệu. Danh sách thuê bao thuộc nhóm đối tượng sẽ được gửi về hộp tin của bạn trong vòng 24 giờ tiếp theo.");
                    res.setCode(ErrorCodeEnum.SUCCESS.getCode());
                }

        } else {
            res.setMessage("Lỗi hệ thống. Không thể tìm thấy dữ liệu của nhóm đối tượng");
            res.setCode(ErrorCodeEnum.SYSTEM_ERROR.getCode());
        }
        result = AppUtils.ObjectToJsonResponse(res);
        return result;
    }
    @RequestMapping(value = "/BigdataController/checkIsProcessTargetGroupByUserId", method = RequestMethod.GET)
    public @ResponseBody
    String checkIsProcessTargetGroupByUserId(@RequestParam(required = false) Long targetGroupID) {
        logger.info(String.format("action query MSISDN: targetGroupID=%s ", targetGroupID));
        SimpleResponseDTO res = new SimpleResponseDTO();
        String result;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        Long userId = ((UserPrinciple) principal).getUserId();
        String key = Conf.PREFIX_KEY_NOTIFY_EXPORT_MSISDN + userId + ":" + targetGroupID;
        if (redisTemplate.hasKey(key)) { // trường hợp đang xử lý nhóm đối tượng đấy
            res.setCode(ErrorCodeEnum.SYSTEM_ERROR.getCode());
        } else { // trường hợp không xử lý
            res.setCode(ErrorCodeEnum.SUCCESS.getCode());
        }
        result = AppUtils.ObjectToJsonResponse(res);
        return result;
    }
    @RequestMapping(value = "/BigdataController/queryCountMSISDN", method = RequestMethod.GET)
    public @ResponseBody
    String queryCountMSISDNFromManagerPage(@RequestParam(required = false) Long targetGroupID) {
        logger.info(String.format("action query MSISDN: targetGroupID=%s ", targetGroupID));
        SimpleResponseDTO res = new SimpleResponseDTO();
        String result;
        Optional<TargetGroup> optionalTargetGroup = targetGroupService.findById(targetGroupID);
        if (optionalTargetGroup.isPresent()) {
            TargetGroup targetGroup = optionalTargetGroup.get();
            if (targetGroup.getChannel() == 1) {
                String jsonData = targetGroup.getDataJson();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("targetGroupId", targetGroup.getId());
                jsonObject.put("query", jsonData);
                String responseCL = httpUtil.PostWithJSON(clickHouseUrl + countMSISDNByJSONCriteriaAndSaveToDB, jsonObject.toString());
                JSONObject jsonCL = new JSONObject(responseCL);
                if (jsonCL.getInt("code") == 0) {
                    logger.info("Lấy số thuê bao đáp ứng nhóm đối tượng: {} thành công", targetGroup.getId());
                } else {
                    logger.info("Lỗi hệ thống. Không thể lấy số thuê bao đáp ứng của nhóm đối tượng {}", targetGroup.getId());
                }
            } else if (targetGroup.getChannel() == 4) {
                String jsonData = targetGroup.getDataJson();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("targetGroupId", targetGroup.getId());
                jsonObject.put("query", jsonData);
                String responseCL = httpUtil.postWithJsonAndFile(clickHouseUrl + countMSISDNByJSONCriteriaAndFileAndSaveToDB, jsonObject.toString(), targetGroup.getPathFileMsisdn());
                JSONObject jsonCL = new JSONObject(responseCL);
                if (jsonCL.getInt("code") == 0) {
                    logger.info("Lấy số thuê bao đáp ứng nhóm đối tượng: {} thành công", targetGroup.getId());
                } else {
                    logger.info("Lỗi hệ thống. Không thể lấy số thuê bao đáp ứng của nhóm đối tượng {}", targetGroup.getId());
                }
            } else if (targetGroup.getChannel() == 3) {
                executor.execute(()->{
                    Long countMsisdn = AppUtils.readFileExcel(targetGroup.getPathFileMsisdn());
                    CountMSISDNDTO dto = new CountMSISDNDTO();
                    dto.setTargetGroupID(targetGroup.getId());
                    dto.setCount(countMsisdn);
                    this.template.convertAndSend("/topic/countMSISDN", dto);
                });
            }
        } else {
            res.setMessage("Lỗi hệ thống. Không thể tìm thấy dữ liệu của nhóm đối tượng");
        }
        result = AppUtils.ObjectToJsonResponse(res);
        return result;
    }


    @RequestMapping(value = "/BigdataController/getListMainProduct", method = RequestMethod.GET)
    public @ResponseBody
    String getListMainProduct() {
        logger.info("action get list main product");
        SimpleResponseDTO res = new SimpleResponseDTO();
        String result;
        String responseCL = httpUtil.Get(clickHouseUrl + getAllMainProduct);
        JSONObject jsonCL = new JSONObject(responseCL);
        logger.info("result :" + responseCL);
        List<String> mainProduct = new ArrayList<>();
        JSONArray jsonArray = jsonCL.getJSONArray("data");
        for (int i = 0; i < jsonArray.length(); i++) {
            mainProduct.add(jsonArray.getString(i));
        }
        res.setCode(ErrorCodeEnum.SUCCESS.getCode());
        res.setData(mainProduct);
        result = AppUtils.ObjectToJsonResponse(res);
        return result;
    }

    @RequestMapping(value = "/BigdataController/getListService", method = RequestMethod.GET)
    public @ResponseBody
    String getListService() {
        logger.info("action get list service");
        SimpleResponseDTO res = new SimpleResponseDTO();
        String result;
        String responseCL = httpUtil.Get(clickHouseUrl + getAllServiceCode);
        JSONObject jsonCL = new JSONObject(responseCL);
        logger.info("result :" + responseCL);
        List<String> services = new ArrayList<>();
        JSONArray jsonArray = jsonCL.getJSONArray("data");
        for (int i = 0; i < jsonArray.length(); i++) {
            services.add(jsonArray.getString(i));
        }
        res.setCode(ErrorCodeEnum.SUCCESS.getCode());
        res.setData(services);
        result = AppUtils.ObjectToJsonResponse(res);
        return result;
    }

    @RequestMapping(value = "/BigdataController/getListCurrentPackage", method = RequestMethod.GET)
    public @ResponseBody
    String getListCurrentPackage() {
        logger.info("action get list current package");
        SimpleResponseDTO res = new SimpleResponseDTO();
        String result;
        String responseCL = httpUtil.Get(clickHouseUrl + getAllCurrentPackage);
        JSONObject jsonCL = new JSONObject(responseCL);
        logger.info("result :" + responseCL);
        List<String> services = new ArrayList<>();
        JSONArray jsonArray = jsonCL.getJSONArray("data");
        for (int i = 0; i < jsonArray.length(); i++) {
            services.add(jsonArray.getString(i));
        }
        res.setCode(ErrorCodeEnum.SUCCESS.getCode());
        res.setData(services);
        result = AppUtils.ObjectToJsonResponse(res);
        return result;
    }

    @GetMapping("/BigdataController/exportFileDataCustomer")
    public void exportFileDataCustomer(HttpServletResponse response, @RequestParam(required = false) Long targetGroupId) {
        logger.info("export data customer with info: targetGroupId:" + targetGroupId);
        File file = null;
        Optional<TargetGroup> optionalTargetGroup = targetGroupService.findById(targetGroupId);
        file = new File(optionalTargetGroup.get().getPathFileMsisdn());
        byte[] data;
        try {
            data = FileUtils.readFileToByteArray(file);
            String date = AppUtils.convertDateToString(new Date(), "dd/MM/yyyy hhmmss");
            String fileName = "Danh sach thue bao_" + optionalTargetGroup.get().getName() + "_" + date + ".csv";
            // Thiết lập thông tin trả về
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            response.setContentLength(data.length);
            InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(data));
            FileCopyUtils.copy(inputStream, response.getOutputStream());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.info("error when download file template!");
            logger.info(e);
        }

    }


}
