package com.vivas.campaignx.service;

import com.vivas.campaignx.common.AppUtils;
import com.vivas.campaignx.common.HttpUtil;
import com.vivas.campaignx.dto.CampaignManagerDto;
import com.vivas.campaignx.dto.CountMSISDNDTO;
import com.vivas.campaignx.entity.EventCampaign;
import com.vivas.campaignx.entity.FrequencyCampaign;
import com.vivas.campaignx.entity.TargetGroup;
import com.vivas.campaignx.repository.EventCampaignRepository;
import com.vivas.campaignx.repository.FrequencyCampaignRepository;
import com.vivas.campaignx.repository.TargetGroupRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CampaignManagerService {
    protected final Logger logger = LogManager.getLogger(this.getClass().getName());
    @Autowired
    private EventCampaignRepository eventCampaignRepository;

    @Autowired
    private FrequencyCampaignRepository frequencyCampaignRepository;

    @Autowired
    private TargetGroupRepository targetGroupRepository;

    @Value("${path_data_target_group_tmp}")
    private String path_data_target_group_tmp;

    @Value("${path_data_target_group_tmp}")
    private String path_data_blacklist_tmp;

    @Autowired
    private HttpUtil httpUtil;

    @Value("${clickhouse.adapter.queryCountMSISDNTotal}")
    private String queryCountMSISDNTotal;

    @Value("${clickhouse.adapter.url}")
    private String clickHouseUrl;

    @Value("${clickhouse.adapter.countMSISDNFromMainGroupByFile}")
    private String countMSISDNFromMainGroupByFile;

    @Value("${clickhouse.adapter.countMSISDNFromMainGroupByJSONGroup}")
    private String countMSISDNFromMainGroupByJSONGroup;

    @Value("${clickhouse.adapter.countMSISDNFromMainGroupByJSONGroupIgnoreFileBlacklist}")
    private String countMSISDNFromMainGroupByJSONGroupIgnoreFileBlacklist;

    @Value("${clickhouse.adapter.countMSISDNFromMainGroupByJSONGroupIgnoreJSONBlacklist}")
    private String countMSISDNFromMainGroupByJSONGroupIgnoreJSONBlacklist;

    @Value("${clickhouse.adapter.countMSISDNFromMainGroupByJsonJOINFile}")
    private String countMSISDNFromMainGroupByJsonJOINFile;

    @Value("${clickhouse.adapter.countMSISDNFromMainGroupByJsonJOINFileIgnoreFileBlacklist}")
    private String countMSISDNFromMainGroupByJsonJOINFileIgnoreFileBlacklist;

    @Value("${clickhouse.adapter.countMSISDNFromMainGroupByJsonJOINFileIgnoreJSONBlacklist}")
    private String countMSISDNFromMainGroupByJsonJOINFileIgnoreJSONBlacklist;

    @Value("${clickhouse.adapter.countMSISDNFromSubGroupByFile}")
    private String countMSISDNFromSubGroupByFile;

    @Value("${clickhouse.adapter.countMSISDNFromSubGroupByJSONGroup}")
    private String countMSISDNFromSubGroupByJSONGroup;

    @Value("${clickhouse.adapter.countMSISDNFromSubGroupByJsonJOINFile}")
    private String countMSISDNFromSubGroupByJsonJOINFile;

    @Value("${clickhouse.adapter.countMSISDNFromMainGroupByFileAndSaveToDB}")
    private String countMSISDNFromMainGroupByFileAndSaveToDB;

    @Value("${clickhouse.adapter.countMSISDNFromMainGroupByJSONGroupAndSaveToDB}")
    private String countMSISDNFromMainGroupByJSONGroupAndSaveToDB;

    @Value("${clickhouse.adapter.countMSISDNFromMainGroupByJSONGroupIgnoreFileBlacklistAndSaveToDB}")
    private String countMSISDNFromMainGroupByJSONGroupIgnoreFileBlacklistAndSaveToDB;

    @Value("${clickhouse.adapter.countMSISDNFromMainGroupByJSONGroupIgnoreJSONBlacklistAndSaveToDB}")
    private String countMSISDNFromMainGroupByJSONGroupIgnoreJSONBlacklistAndSaveToDB;

    @Value("${clickhouse.adapter.countMSISDNFromMainGroupByJsonJOINFileAndSaveToDB}")
    private String countMSISDNFromMainGroupByJsonJOINFileAndSaveToDB;

    @Value("${clickhouse.adapter.countMSISDNFromMainGroupByJsonJOINFileIgnoreFileBlacklistAndSaveToDB}")
    private String countMSISDNFromMainGroupByJsonJOINFileIgnoreFileBlacklistAndSaveToDB;

    @Value("${clickhouse.adapter.countMSISDNFromMainGroupByJsonJOINFileIgnoreJSONBlacklistAndSaveToDB}")
    private String countMSISDNFromMainGroupByJsonJOINFileIgnoreJSONBlacklistAndSaveToDB;

    @Value("${clickhouse.adapter.countMSISDNFromSubGroupByFileAndSaveToDB}")
    private String countMSISDNFromSubGroupByFileAndSaveToDB;

    @Value("${clickhouse.adapter.countMSISDNFromSubGroupByJSONGroupAndSaveToDB}")
    private String countMSISDNFromSubGroupByJSONGroupAndSaveToDB;

    @Value("${clickhouse.adapter.countMSISDNFromSubGroupByJsonJOINFileAndSaveToDB}")
    private String countMSISDNFromSubGroupByJsonJOINFileAndSaveToDB;





    public Page<CampaignManagerDto> getAll(Integer pageSize, Integer currentPage) {
        List<EventCampaign> findAllEventCampaign = eventCampaignRepository.findAllByStatusIsNot(-99);
        List<FrequencyCampaign> findAllFrequencyCampaign = frequencyCampaignRepository.findAllByStatusIsNot(-99);
        List<CampaignManagerDto> campaignManagerDtoList = new ArrayList<>();

        Page<CampaignManagerDto> campaignManagerDtoPage = null;
        Pageable paging = PageRequest.of(currentPage - 1, pageSize, Sort.by("createdDate").descending());
        for (EventCampaign eventCampaign : findAllEventCampaign) {
            CampaignManagerDto campaignManagerDto = new CampaignManagerDto();
            convertCampaignEventToDto(campaignManagerDto, eventCampaign);
            campaignManagerDtoList.add(campaignManagerDto);
        }
        for (FrequencyCampaign frequencyCampaign : findAllFrequencyCampaign) {
            CampaignManagerDto campaignManagerDto = new CampaignManagerDto();
            convertCampaignFrequency(campaignManagerDto, frequencyCampaign);
            campaignManagerDtoList.add(campaignManagerDto);
        }
        Collections.sort(campaignManagerDtoList, (o1,o2) -> o2.getCreatedDateConvert().compareTo(o1.getCreatedDateConvert()));
        final int start = (int) paging.getOffset();
        final int end = Math.min((start + paging.getPageSize()), campaignManagerDtoList.size());
        List<CampaignManagerDto> subList = campaignManagerDtoList.subList(start, end);
        campaignManagerDtoPage = new PageImpl<>(campaignManagerDtoList.subList(start, end), paging, campaignManagerDtoList.size());
        return campaignManagerDtoPage;
    }

    public Page<CampaignManagerDto> findByCampaign(Integer pageSize, Integer currentPage, Integer type, String campaignName,
                                                   String createdUser, String createdDate, String startDate, String endDate,
                                                   String statusInit, String statusApprove, String statusReject, String statusActive, String statusPause, String statusEnd) {
        List<Integer> listStatus = listStatus(statusInit,statusApprove,statusReject,statusActive,statusPause,statusEnd);
        logger.info("listStatus:", listStatus);
        List<EventCampaign> findAllEventCampaign = eventCampaignRepository.search(campaignName, createdUser, createdDate, startDate, endDate,listStatus);
        List<FrequencyCampaign> findAllFrequencyCampaign = frequencyCampaignRepository.search(campaignName, createdUser, createdDate, startDate, endDate,listStatus);
        List<CampaignManagerDto> campaignManagerDtoList = new ArrayList<>();

        Page<CampaignManagerDto> campaignManagerDtoPage = null;
        Pageable paging = PageRequest.of(currentPage - 1, pageSize, Sort.by("createdDate").descending());

        if (type == null || type == 0) {
            for (EventCampaign eventCampaign : findAllEventCampaign) {
                CampaignManagerDto campaignManagerDto = new CampaignManagerDto();
                convertCampaignEventToDto(campaignManagerDto, eventCampaign);
                campaignManagerDtoList.add(campaignManagerDto);
            }
            for (FrequencyCampaign frequencyCampaign : findAllFrequencyCampaign) {
                CampaignManagerDto campaignManagerDto = new CampaignManagerDto();
                convertCampaignFrequency(campaignManagerDto, frequencyCampaign);
                campaignManagerDtoList.add(campaignManagerDto);
            }
            Collections.sort(campaignManagerDtoList, (o1,o2) -> o2.getCreatedDateConvert().compareTo(o1.getCreatedDateConvert()));
            final int start = (int) paging.getOffset();
            final int end = Math.min((start + paging.getPageSize()), campaignManagerDtoList.size());
            campaignManagerDtoPage = new PageImpl<>(campaignManagerDtoList.subList(start, end), paging, campaignManagerDtoList.size());
        } else if (type != null) {
            if (type == 1) {
                for (EventCampaign eventCampaign : findAllEventCampaign) {
                    CampaignManagerDto campaignManagerDto = new CampaignManagerDto();
                    convertCampaignEventToDto(campaignManagerDto, eventCampaign);
                    campaignManagerDtoList.add(campaignManagerDto);
                }
            }
            if (type == 2) {
                for (FrequencyCampaign frequencyCampaign : findAllFrequencyCampaign) {
                    CampaignManagerDto campaignManagerDto = new CampaignManagerDto();
                    convertCampaignFrequency(campaignManagerDto, frequencyCampaign);
                    campaignManagerDtoList.add(campaignManagerDto);
                }
            }
            Collections.sort(campaignManagerDtoList, (o1,o2) -> o2.getCreatedDateConvert().compareTo(o1.getCreatedDateConvert()));
            final int start = (int) paging.getOffset();
            final int end = Math.min((start + paging.getPageSize()), campaignManagerDtoList.size());
            campaignManagerDtoPage = new PageImpl<>(campaignManagerDtoList.subList(start, end), paging, campaignManagerDtoList.size());
        }
        return campaignManagerDtoPage;
    }

    public CampaignManagerDto convertCampaignEventToDto(CampaignManagerDto campaignManagerDto, EventCampaign eventCampaign) {
        campaignManagerDto.setId(eventCampaign.getCampaignId());
        campaignManagerDto.setName(eventCampaign.getCampaignName());
        campaignManagerDto.setStatus(eventCampaign.getStatus());
        campaignManagerDto.setStatusOld(eventCampaign.getStatusOld());
        campaignManagerDto.setStartDate(AppUtils.formatDefaultDate(eventCampaign.getStartDate()));
        if(eventCampaign.getEndDate() != null) {
            campaignManagerDto.setEndDate(AppUtils.formatDefaultDate(eventCampaign.getEndDate()));
        }
        campaignManagerDto.setCreatedDate(AppUtils.formatDefaultDate(eventCampaign.getCreatedDate()));
        campaignManagerDto.setCreatedDateConvert(AppUtils.formatDefaultDatetime1(eventCampaign.getCreatedDate()));
        campaignManagerDto.setUserName(eventCampaign.getCreatedUser());
        campaignManagerDto.setType(1);
        return campaignManagerDto;
    }

    public CampaignManagerDto convertCampaignFrequency(CampaignManagerDto campaignManagerDto, FrequencyCampaign frequencyCampaign) {
        campaignManagerDto.setId(frequencyCampaign.getCampaignId());
        campaignManagerDto.setName(frequencyCampaign.getCampaignName());
        campaignManagerDto.setStatus(frequencyCampaign.getStatus());
        campaignManagerDto.setStatusOld(frequencyCampaign.getStatusOld());
        campaignManagerDto.setStartDate(AppUtils.formatDefaultDate(frequencyCampaign.getStartDate()));
        if(frequencyCampaign.getEndDate() != null) {
        campaignManagerDto.setEndDate(AppUtils.formatDefaultDate(frequencyCampaign.getEndDate()));}
        campaignManagerDto.setCreatedDate(AppUtils.formatDefaultDate(frequencyCampaign.getCreatedDate()));
        campaignManagerDto.setCreatedDateConvert(AppUtils.formatDefaultDatetime1(frequencyCampaign.getCreatedDate()));
        campaignManagerDto.setUserName(frequencyCampaign.getCreatedUser());
        campaignManagerDto.setType(2);
        return campaignManagerDto;
    }

    public List<Integer> listStatus(String statusInit, String statusApprove, String statusReject, String statusActive, String statusPause, String statusEnd) {
        List<Integer> listStatus = new ArrayList<>();
        if (statusInit != null) {
            listStatus.add(1);
        }
        if (statusApprove != null) {
            listStatus.add(2);
        }
        if (statusReject != null) {
            listStatus.add(5);
        }
        if (statusActive != null) {
            listStatus.add(0);
        }
        if (statusPause != null) {
            listStatus.add(6);
        }
        if (statusEnd != null) {
            listStatus.add(4);
        }
        if (statusInit == null && statusApprove == null && statusReject == null
                && statusActive == null && statusPause == null && statusEnd == null) {
            listStatus = Arrays.asList(0,1,2,3,4,5,6,7,8);
        }
        return listStatus;
    }

    public Optional<EventCampaign> findByIdEvent(Long id) {
        return eventCampaignRepository.findById(id);
    }

    public Optional<FrequencyCampaign> findByIdFrequency(Long id) {
        return frequencyCampaignRepository.findById(id);
    }

    public List<CampaignManagerDto> exportAll(Integer pageSize, Integer currentPage) {
        List<EventCampaign> findAllEventCampaign = eventCampaignRepository.findAllByStatusIsNot(-99);
        List<FrequencyCampaign> findAllFrequencyCampaign = frequencyCampaignRepository.findAllByStatusIsNot(-99);
        List<CampaignManagerDto> campaignManagerDtoList = new ArrayList<>();
        for (EventCampaign eventCampaign : findAllEventCampaign) {
            CampaignManagerDto campaignManagerDto = new CampaignManagerDto();
            convertCampaignEventToDto(campaignManagerDto, eventCampaign);
            campaignManagerDtoList.add(campaignManagerDto);
        }
        for (FrequencyCampaign frequencyCampaign : findAllFrequencyCampaign) {
            CampaignManagerDto campaignManagerDto = new CampaignManagerDto();
            convertCampaignFrequency(campaignManagerDto, frequencyCampaign);
            campaignManagerDtoList.add(campaignManagerDto);
        }
        Collections.sort(campaignManagerDtoList, (o1,o2) -> o2.getCreatedDateConvert().compareTo(o1.getCreatedDateConvert()));
        return campaignManagerDtoList;
    }

    public List<CampaignManagerDto> exportBySerach(Integer pageSize, Integer currentPage, Integer type, String campaignName,
                                                   String createdUser, String createdDate, String startDate, String endDate,
                                                   String statusInit, String statusApprove, String statusReject, String statusActive, String statusPause, String statusEnd) {
        List<Integer> listStatus = listStatus(statusInit,statusApprove,statusReject,statusActive,statusPause,statusEnd);
        logger.info("listStatus:", listStatus);
        List<EventCampaign> findAllEventCampaign = eventCampaignRepository.search(campaignName, createdUser, createdDate, startDate, endDate,listStatus);
        List<FrequencyCampaign> findAllFrequencyCampaign = frequencyCampaignRepository.search(campaignName, createdUser, createdDate, startDate, endDate,listStatus);
        List<CampaignManagerDto> campaignManagerDtoList = new ArrayList<>();
        if (type == null || type == 0) {
            for (EventCampaign eventCampaign : findAllEventCampaign) {
                CampaignManagerDto campaignManagerDto = new CampaignManagerDto();
                convertCampaignEventToDto(campaignManagerDto, eventCampaign);
                campaignManagerDtoList.add(campaignManagerDto);
            }
            for (FrequencyCampaign frequencyCampaign : findAllFrequencyCampaign) {
                CampaignManagerDto campaignManagerDto = new CampaignManagerDto();
                convertCampaignFrequency(campaignManagerDto, frequencyCampaign);
                campaignManagerDtoList.add(campaignManagerDto);
            }
        } else if (type != null) {
            if (type == 1) {
                for (EventCampaign eventCampaign : findAllEventCampaign) {
                    CampaignManagerDto campaignManagerDto = new CampaignManagerDto();
                    convertCampaignEventToDto(campaignManagerDto, eventCampaign);
                    campaignManagerDtoList.add(campaignManagerDto);
                }
            }
            if (type == 2) {
                for (FrequencyCampaign frequencyCampaign : findAllFrequencyCampaign) {
                    CampaignManagerDto campaignManagerDto = new CampaignManagerDto();
                    convertCampaignFrequency(campaignManagerDto, frequencyCampaign);
                    campaignManagerDtoList.add(campaignManagerDto);
                }
            }
        }
        Collections.sort(campaignManagerDtoList, (o1,o2) -> o2.getCreatedDateConvert().compareTo(o1.getCreatedDateConvert()));
        return campaignManagerDtoList;
    }

    public String getFrequency(String json){
        JSONObject jsonObject = new JSONObject(json);
        StringBuilder sb = new StringBuilder();
        String frequency = jsonObject.getString("frequency");
        if(frequency.equals("once")){
            sb.append("Một lần");
        }else if(frequency.equals("daily")){
            sb.append("Hằng ngày");
        }else if(frequency.equals("weekly")){
            sb.append("Hằng tuần vào ");
            String value = jsonObject.getString("value");
            String[] arrValue = value.split(",");
            for(int i = 0; i < arrValue.length; i++){
                if(arrValue[i].equals("0")){
                    sb.append(" Thứ hai,");
                }else if(arrValue[i].equals("1")){
                    sb.append(" Thứ ba,");
                }else if(arrValue[i].equals("2")){
                    sb.append(" Thứ tư,");
                }else if(arrValue[i].equals("3")){
                    sb.append(" Thứ năm,");
                }else if(arrValue[i].equals("4")){
                    sb.append(" Thứ sáu,");
                }else if(arrValue[i].equals("5")){
                    sb.append(" Thứ bảy,");
                }else if(arrValue[i].equals("6")){
                    sb.append(" Chủ nhật,");
                }
            }
            sb.replace(sb.length()-1,sb.length(),"");
        }else if(frequency.equals("monthly1")){
            int value = jsonObject.getInt("value");
            sb.append("Hằng tháng vào ngày ");
            sb.append(value);
        }else if(frequency.equals("monthly2")){
            sb.append("Vào ");
            String value = jsonObject.getString("value");
            if(value.charAt(0) == '0'){
                sb.append("Thứ hai ");
            }else if(value.charAt(0) == '1'){
                sb.append("Thứ ba ");
            }else if(value.charAt(0) == '2'){
                sb.append("Thứ tư ");
            }else if(value.charAt(0) == '3'){
                sb.append("Thứ năm ");
            }else if(value.charAt(0) == '4'){
                sb.append("Thứ sáu ");
            }else if(value.charAt(0) == '5'){
                sb.append("Thứ bảy ");
            }else if(value.charAt(0) == '6'){
                sb.append("Chủ nhật ");
            }

            if(value.charAt(1) == '0'){
                sb.append("đầu tiên ");
            }else if(value.charAt(1) == '1'){
                sb.append("thứ 2 ");
            }else if(value.charAt(1) == '2'){
                sb.append("thứ 3 ");
            }else if(value.charAt(1) == '3'){
                sb.append("thứ 4 ");
            }else if(value.charAt(1) == '4'){
                sb.append("cuối cùng ");
            }
            sb.append("của tháng");
        }else if(frequency.equals("custom1")){
            sb.append("Vào các ngày: ");
            String value = jsonObject.getString("value");
            String[] arrValue = value.split(",");
            for(int i = 0; i < arrValue.length; i++){
                String dayFormat = AppUtils.formatStringDate( arrValue[i], "ddMMyyyy", "dd/MM/yyyy");
                sb.append(dayFormat);
                sb.append(", ");
            }
            sb.deleteCharAt(sb.length() - 2);
        }else if(frequency.equals("custom2")){
            int rate = jsonObject.getInt("rate");
            sb.append("Cứ mỗi ");
            sb.append(rate);
            JSONObject jsonUnit = jsonObject.getJSONObject("value");
            String time = jsonUnit.getString("frequency");
            if(time.equals("daily")){
                sb.append(" ngày/lần ");
            }else if(time.equals("weekly")){
                sb.append(" tuần/lần");
                sb.append(" vào");
                String valueTime = jsonUnit.getString("value");
                String[] arrValueTime = valueTime.split(",");
                for(int i = 0; i < arrValueTime.length; i++){
                    if(arrValueTime[i].equals("0")){
                        sb.append(" Thứ hai,");
                    }else if(arrValueTime[i].equals("1")){
                        sb.append(" Thứ ba,");
                    }else if(arrValueTime[i].equals("2")){
                        sb.append(" Thứ tư,");
                    }else if(arrValueTime[i].equals("3")){
                        sb.append(" Thứ năm,");
                    }else if(arrValueTime[i].equals("4")){
                        sb.append(" Thứ sáu,");
                    }else if(arrValueTime[i].equals("5")){
                        sb.append(" Thứ bảy,");
                    }else if(arrValueTime[i].equals("6")){
                        sb.append(" Chủ nhật,");
                    }
                }
                sb.deleteCharAt(sb.length() - 1);
                sb.append(" của tuần");
            }else if(time.equals("monthly1")){
                sb.append(" tháng/lần,");
                sb.append(" vào");
                Integer valueTime = jsonUnit.getInt("value");
                sb.append(" ngày ").append(valueTime).append(" của tháng ");
            }else if(time.equals("monthly2")){
                sb.append(" tháng/lần,");
                sb.append(" vào ");
                String valueTime = jsonUnit.getString("value");
                if(valueTime.charAt(0) == '0'){
                    sb.append("Thứ hai ");
                }else if(valueTime.charAt(0) == '1'){
                    sb.append("Thứ ba ");
                }else if(valueTime.charAt(0) == '2'){
                    sb.append("Thứ tư ");
                }else if(valueTime.charAt(0) == '3'){
                    sb.append("Thứ năm ");
                }else if(valueTime.charAt(0) == '4'){
                    sb.append("Thứ sáu ");
                }else if(valueTime.charAt(0) == '5'){
                    sb.append("Thứ bảy ");
                }else if(valueTime.charAt(0) == '6'){
                    sb.append("Chủ nhật ");
                }

                if(valueTime.charAt(1) == '0'){
                    sb.append("đầu tiên ");
                }else if(valueTime.charAt(1) == '1'){
                    sb.append("thứ 2 ");
                }else if(valueTime.charAt(1) == '2'){
                    sb.append("thứ 3 ");
                }else if(valueTime.charAt(1) == '3'){
                    sb.append("thứ 4 ");
                }else if(valueTime.charAt(1) == '4'){
                    sb.append("cuối cùng ");
                }
                sb.append("của tháng");
            }
        }

        return sb.toString();
    }
    //==================== service đếm Số MSISDN thỏa mãn nhóm chính ================

    public CountMSISDNDTO countMSISDNFromMainGroupByFile(MultipartFile dataTargetGroup, Long typeInputBlacklist, MultipartFile dataBlacklist, Long idGroupBlacklist){
        String pathDataTargetGroup = null;
        String pathDataBlacklist = null;
        CountMSISDNDTO result = new CountMSISDNDTO();
        if (dataTargetGroup != null) {
            if (dataTargetGroup.getSize() > 0) {
                File dir1 = new File(path_data_target_group_tmp);
                if (!dir1.exists()) {
                    dir1.mkdirs();
                }
                pathDataTargetGroup = AppUtils.saveFile(dataTargetGroup, path_data_target_group_tmp);
            }
        }
        if(typeInputBlacklist == 3) {
            if (dataBlacklist != null) {
                if (dataBlacklist.getSize() > 0) {
                    File dir1 = new File(path_data_blacklist_tmp);
                    if (!dir1.exists()) {
                        dir1.mkdirs();
                    }
                    pathDataBlacklist = AppUtils.saveFile(dataBlacklist, path_data_blacklist_tmp);
                    logger.info("black list by file: " + pathDataBlacklist);
                    long msisdn = countMSISDNFromFileDataIgnoreFileBlacklist(pathDataTargetGroup, pathDataBlacklist);
                    String responseCL = httpUtil.Get(clickHouseUrl + queryCountMSISDNTotal);
                    JSONObject jsonCL = new JSONObject(responseCL);

                    long total = jsonCL.getLong("data");
                    Double ratio = (double) Math.round(((double) msisdn / (double) total) * 100.0) / 100.0;
                    result.setCount(msisdn);
                    result.setRatio(ratio);
                }
            }
        }
        else if(typeInputBlacklist == 2) {
            if (idGroupBlacklist != null) {
                logger.info("black list by target group: " + idGroupBlacklist);
                Optional<TargetGroup> groupBlacklist = targetGroupRepository.findById(idGroupBlacklist);
                JSONObject jsonObjectData = new JSONObject();
                jsonObjectData.put("jsonBlacklist", groupBlacklist.get().getDataJson());
                String responseCL = httpUtil.postWithJsonAndFile(clickHouseUrl + countMSISDNFromMainGroupByFile, jsonObjectData.toString(), pathDataTargetGroup);
                logger.info("response from adapter click house: " + responseCL);
                JSONObject jsonObjectResponse = new JSONObject(responseCL);
                JSONObject jsonObjectDataResponse = jsonObjectResponse.getJSONObject("data");
                result.setCount(jsonObjectDataResponse.getLong("msisdn"));
                result.setRatio(jsonObjectDataResponse.getDouble("ratio"));
            }
        }
        else {
            String responseCL = httpUtil.Get(clickHouseUrl + queryCountMSISDNTotal);
            JSONObject jsonCL = new JSONObject(responseCL);

            long total = jsonCL.getLong("data");
            long msisdn = AppUtils.readFileCSV(pathDataTargetGroup);
            Double ratio =  (double) Math.round(((double)msisdn/(double)total)*100.0)/100.0;
            result.setCount(msisdn);
            result.setRatio(ratio);
        }
        return result;
    }

    public CountMSISDNDTO countMSISDNFromMainGroupByFile(String dataTargetGroup, Long typeInputBlacklist, MultipartFile dataBlacklist, Long idGroupBlacklist){
        String pathDataTargetGroup = dataTargetGroup;
        String pathDataBlacklist = null;
        CountMSISDNDTO result = new CountMSISDNDTO();

        if(typeInputBlacklist == 3) {
            if (dataBlacklist != null) {
                if (dataBlacklist.getSize() > 0) {
                    File dir1 = new File(path_data_blacklist_tmp);
                    if (!dir1.exists()) {
                        dir1.mkdirs();
                    }
                    pathDataBlacklist = AppUtils.saveFile(dataBlacklist, path_data_blacklist_tmp);
                    logger.info("black list by file: " + pathDataBlacklist);
                    long msisdn = countMSISDNFromFileDataIgnoreFileBlacklist(pathDataTargetGroup, pathDataBlacklist);
                    String responseCL = httpUtil.Get(clickHouseUrl + queryCountMSISDNTotal);
                    JSONObject jsonCL = new JSONObject(responseCL);

                    long total = jsonCL.getLong("data");
                    Double ratio = (double) Math.round(((double) msisdn / (double) total) * 100.0) / 100.0;
                    result.setCount(msisdn);
                    result.setRatio(ratio);
                }
            }
        }
        else if(typeInputBlacklist == 2) {
            if (idGroupBlacklist != null) {
                logger.info("black list by target group: " + idGroupBlacklist);
                Optional<TargetGroup> groupBlacklist = targetGroupRepository.findById(idGroupBlacklist);
                JSONObject jsonObjectData = new JSONObject();
                jsonObjectData.put("jsonBlacklist", groupBlacklist.get().getDataJson());
                String responseCL = httpUtil.postWithJsonAndFile(clickHouseUrl + countMSISDNFromMainGroupByFile, jsonObjectData.toString(), pathDataTargetGroup);
                logger.info("response from adapter click house: " + responseCL);
                JSONObject jsonObjectResponse = new JSONObject(responseCL);
                JSONObject jsonObjectDataResponse = jsonObjectResponse.getJSONObject("data");
                result.setCount(jsonObjectDataResponse.getLong("msisdn"));
                result.setRatio(jsonObjectDataResponse.getDouble("ratio"));
            }
        }
        else {
            String responseCL = httpUtil.Get(clickHouseUrl + queryCountMSISDNTotal);
            JSONObject jsonCL = new JSONObject(responseCL);

            long total = jsonCL.getLong("data");
            long msisdn = AppUtils.readFileCSV(pathDataTargetGroup);
            Double ratio =  (double) Math.round(((double)msisdn/(double)total)*100.0)/100.0;
            result.setCount(msisdn);
            result.setRatio(ratio);
        }
        return result;
    }

    public CountMSISDNDTO countMSISDNFromMainGroupByJsonCriteria(String jsonData, Long typeInputBlacklist, MultipartFile dataBlacklist, Long idGroupBlacklist){
        String pathDataBlacklist = null;
        CountMSISDNDTO result = new CountMSISDNDTO();
        if(typeInputBlacklist == 3) {
            if (dataBlacklist != null && dataBlacklist.getSize() > 0) {
                File dir1 = new File(path_data_blacklist_tmp);
                if (!dir1.exists()) {
                    dir1.mkdirs();
                }
                pathDataBlacklist = AppUtils.saveFile(dataBlacklist, path_data_blacklist_tmp);
                logger.info("black list by file blacklist: " + pathDataBlacklist);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("jsonMainGroup", jsonData);
                String responseCL = httpUtil.postWithJsonAndFile(clickHouseUrl + countMSISDNFromMainGroupByJSONGroupIgnoreFileBlacklist, jsonObject.toString(), pathDataBlacklist);
                logger.info("response from adapter click house: " + responseCL);
                JSONObject jsonObjectResponse = new JSONObject(responseCL);
                JSONObject jsonObjectData = jsonObjectResponse.getJSONObject("data");
                result.setCount(jsonObjectData.getLong("msisdn"));
                result.setRatio(jsonObjectData.getDouble("ratio"));
            }
        }else if(typeInputBlacklist == 2) {
            if (idGroupBlacklist != null) {
                logger.info("black list by target group: " + idGroupBlacklist);
                Optional<TargetGroup> groupBlacklist = targetGroupRepository.findById(idGroupBlacklist);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("jsonMainGroup", jsonData);
                jsonObject.put("jsonBlacklist", groupBlacklist.get().getDataJson());
                String responseCL = httpUtil.PostWithJSON(clickHouseUrl + countMSISDNFromMainGroupByJSONGroupIgnoreJSONBlacklist, jsonObject.toString());
                logger.info("response from adapter click house: " + responseCL);
                JSONObject jsonObjectResponse = new JSONObject(responseCL);
                JSONObject jsonObjectData = jsonObjectResponse.getJSONObject("data");
                result.setCount(jsonObjectData.getLong("msisdn"));
                result.setRatio(jsonObjectData.getDouble("ratio"));
            }
        }else{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("jsonMainGroup", jsonData);
            String responseCL = httpUtil.PostWithJSON(clickHouseUrl + countMSISDNFromMainGroupByJSONGroup, jsonObject.toString());
            logger.info("response from adapter click house: " + responseCL);
            JSONObject jsonObjectResponse = new JSONObject(responseCL);
            JSONObject jsonObjectData = jsonObjectResponse.getJSONObject("data");
            result.setCount(jsonObjectData.getLong("msisdn"));
            result.setRatio(jsonObjectData.getDouble("ratio"));
        }
        return result;
    }

    public CountMSISDNDTO countMSISDNFromMainGroupByFileJOINJsonCriteria(MultipartFile dataTargetGroup, String jsonData, Long typeInputBlacklist, MultipartFile dataBlacklist, Long idGroupBlacklist){

        String pathDataTargetGroup = null;
        String pathDataBlacklist = null;
        CountMSISDNDTO result = new CountMSISDNDTO();
        if (dataTargetGroup != null && dataTargetGroup.getSize() > 0) {
            File dir1 = new File(path_data_target_group_tmp);
            if (!dir1.exists()) {
                dir1.mkdirs();
            }
            pathDataTargetGroup = AppUtils.saveFile(dataTargetGroup, path_data_target_group_tmp);
        }
        if(typeInputBlacklist == 3) {
            if(dataBlacklist != null && dataBlacklist.getSize() > 0) {
                File dir1 = new File(path_data_target_group_tmp);
                if (!dir1.exists()) {
                    dir1.mkdirs();
                }
                pathDataBlacklist = AppUtils.saveFile(dataBlacklist, path_data_target_group_tmp);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("jsonMainGroup", jsonData);

                String responseCL = httpUtil.postWithJsonAndFileAndFileBlacklist(clickHouseUrl + countMSISDNFromMainGroupByJsonJOINFileIgnoreFileBlacklist, jsonObject.toString(), pathDataTargetGroup, pathDataBlacklist);
                logger.info("response from adapter click house: " + responseCL);

                JSONObject jsonObjectResponse = new JSONObject(responseCL);
                JSONObject jsonObjectData = jsonObjectResponse.getJSONObject("data");
                result.setCount(jsonObjectData.getLong("msisdn"));
                result.setRatio(jsonObjectData.getDouble("ratio"));
            }
        }else if(typeInputBlacklist == 2) {
            if (idGroupBlacklist != null) {
                logger.info("black list by target group: " + idGroupBlacklist);

                Optional<TargetGroup> groupBlacklist = targetGroupRepository.findById(idGroupBlacklist);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("jsonMainGroup", jsonData);
                jsonObject.put("jsonBlacklist", groupBlacklist.get().getDataJson());

                String responseCL = httpUtil.postWithJsonAndFile(clickHouseUrl + countMSISDNFromMainGroupByJsonJOINFileIgnoreJSONBlacklist, jsonObject.toString(), pathDataTargetGroup);
                logger.info("response from adapter click house: " + responseCL);

                JSONObject jsonObjectResponse = new JSONObject(responseCL);
                JSONObject jsonObjectData = jsonObjectResponse.getJSONObject("data");
                result.setCount(jsonObjectData.getLong("msisdn"));
                result.setRatio(jsonObjectData.getDouble("ratio"));
            }
        }else{
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("jsonMainGroup", jsonData);

                String responseCL = httpUtil.postWithJsonAndFile(clickHouseUrl + countMSISDNFromMainGroupByJsonJOINFile, jsonObject.toString(), pathDataTargetGroup);
                logger.info("response from adapter click house: " + responseCL);
                JSONObject jsonObjectResponse = new JSONObject(responseCL);
                JSONObject jsonObjectData = jsonObjectResponse.getJSONObject("data");
                result.setCount(jsonObjectData.getLong("msisdn"));
                result.setRatio(jsonObjectData.getDouble("ratio"));
            }
        return result;
    }

    public CountMSISDNDTO countMSISDNFromMainGroupByFileJOINJsonCriteria(String dataTargetGroup, String jsonData, Long typeInputBlacklist, MultipartFile dataBlacklist, Long idGroupBlacklist){

        String pathDataTargetGroup = dataTargetGroup;
        String pathDataBlacklist = null;
        CountMSISDNDTO result = new CountMSISDNDTO();

        if(typeInputBlacklist == 3) {
            if(dataBlacklist != null && dataBlacklist.getSize() > 0) {
                File dir1 = new File(path_data_target_group_tmp);
                if (!dir1.exists()) {
                    dir1.mkdirs();
                }
                pathDataBlacklist = AppUtils.saveFile(dataBlacklist, path_data_target_group_tmp);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("jsonMainGroup", jsonData);

                String responseCL = httpUtil.postWithJsonAndFileAndFileBlacklist(clickHouseUrl + countMSISDNFromMainGroupByJsonJOINFileIgnoreFileBlacklist, jsonObject.toString(), pathDataTargetGroup, pathDataBlacklist);
                logger.info("response from adapter click house: " + responseCL);

                JSONObject jsonObjectResponse = new JSONObject(responseCL);
                JSONObject jsonObjectData = jsonObjectResponse.getJSONObject("data");
                result.setCount(jsonObjectData.getLong("msisdn"));
                result.setRatio(jsonObjectData.getDouble("ratio"));
            }
        }else if(typeInputBlacklist == 2) {
            if (idGroupBlacklist != null) {
                logger.info("black list by target group: " + idGroupBlacklist);

                Optional<TargetGroup> groupBlacklist = targetGroupRepository.findById(idGroupBlacklist);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("jsonMainGroup", jsonData);
                jsonObject.put("jsonBlacklist", groupBlacklist.get().getDataJson());

                String responseCL = httpUtil.postWithJsonAndFile(clickHouseUrl + countMSISDNFromMainGroupByJsonJOINFileIgnoreJSONBlacklist, jsonObject.toString(), pathDataTargetGroup);
                logger.info("response from adapter click house: " + responseCL);

                JSONObject jsonObjectResponse = new JSONObject(responseCL);
                JSONObject jsonObjectData = jsonObjectResponse.getJSONObject("data");
                result.setCount(jsonObjectData.getLong("msisdn"));
                result.setRatio(jsonObjectData.getDouble("ratio"));
            }
        }else{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("jsonMainGroup", jsonData);

            String responseCL = httpUtil.postWithJsonAndFile(clickHouseUrl + countMSISDNFromMainGroupByJsonJOINFile, jsonObject.toString(), pathDataTargetGroup);
            logger.info("response from adapter click house: " + responseCL);
            JSONObject jsonObjectResponse = new JSONObject(responseCL);
            JSONObject jsonObjectData = jsonObjectResponse.getJSONObject("data");
            result.setCount(jsonObjectData.getLong("msisdn"));
            result.setRatio(jsonObjectData.getDouble("ratio"));
        }
        return result;
    }


    //==================== service đếm Số MSISDN thỏa mãn nhóm con ================

    public CountMSISDNDTO countMSISDNFromSubGroupByFile(MultipartFile dataTargetGroup, Long typeInputBlacklist, MultipartFile dataBlacklist, Long idGroupBlacklist, String jsonSubGroup){
        String pathDataTargetGroup = null;
        String pathDataBlacklist = null;
        CountMSISDNDTO result = new CountMSISDNDTO();
        if (dataTargetGroup != null) {
            if (dataTargetGroup.getSize() > 0) {
                File dir1 = new File(path_data_target_group_tmp);
                if (!dir1.exists()) {
                    dir1.mkdirs();
                }
                pathDataTargetGroup = AppUtils.saveFile(dataTargetGroup, path_data_target_group_tmp);
            }
        }
        if(typeInputBlacklist == 3) {
            if(dataBlacklist != null && dataBlacklist.getSize() > 0){
                File dir1 = new File(path_data_blacklist_tmp);
                if (!dir1.exists()) {
                    dir1.mkdirs();
                }
                pathDataBlacklist = AppUtils.saveFile(dataBlacklist, path_data_blacklist_tmp);
                logger.info("black list by file: " + pathDataBlacklist);

                JSONObject jsonData = new JSONObject();
                jsonData.put("typeBlacklist", "file");
                jsonData.put("jsonSubGroup", jsonSubGroup);

                String response = httpUtil.postWithJsonAndFileAndFileBlacklist(clickHouseUrl + countMSISDNFromSubGroupByFile, jsonData.toString(), pathDataTargetGroup, pathDataBlacklist);
                logger.info("response from adapter click house: " + response);
                JSONObject jsonObjectResponse = new JSONObject(response);
                JSONObject jsonObjectData = jsonObjectResponse.getJSONObject("data");
                result.setCount(jsonObjectData.getLong("msisdn"));
                result.setRatio(jsonObjectData.getDouble("ratio"));

            }
        }else if (typeInputBlacklist == 2) {
            if (idGroupBlacklist != null) {
                logger.info("black list by target group: " + idGroupBlacklist);
                Optional<TargetGroup> groupBlacklist = targetGroupRepository.findById(idGroupBlacklist);

                JSONObject jsonData = new JSONObject();
                jsonData.put("typeBlacklist", "group");
                jsonData.put("jsonSubGroup", jsonSubGroup);
                jsonData.put("jsonBlacklist", groupBlacklist.get().getDataJson());

                String response = httpUtil.postWithJsonAndFileAndFileBlacklist(clickHouseUrl + countMSISDNFromSubGroupByFile, jsonData.toString(), pathDataTargetGroup, pathDataBlacklist);
                logger.info("response from adapter click house: " + response);
                JSONObject jsonObjectResponse = new JSONObject(response);
                JSONObject jsonObjectData = jsonObjectResponse.getJSONObject("data");
                result.setCount(jsonObjectData.getLong("msisdn"));
                result.setRatio(jsonObjectData.getDouble("ratio"));
            }
        }else{
            JSONObject jsonData = new JSONObject();
            jsonData.put("typeBlacklist", "none");
            jsonData.put("jsonSubGroup", jsonSubGroup);
            String response = httpUtil.postWithJsonAndFileAndFileBlacklist(clickHouseUrl + countMSISDNFromSubGroupByFile, jsonData.toString(), pathDataTargetGroup, pathDataBlacklist);
            logger.info("response from adapter click house: " + response);
            JSONObject jsonObjectResponse = new JSONObject(response);
            JSONObject jsonObjectData = jsonObjectResponse.getJSONObject("data");
            result.setCount(jsonObjectData.getLong("msisdn"));
            result.setRatio(jsonObjectData.getDouble("ratio"));
        }
        return result;
    }

    public CountMSISDNDTO countMSISDNFromSubGroupByFile(String dataTargetGroup, Long typeInputBlacklist, MultipartFile dataBlacklist, Long idGroupBlacklist, String jsonSubGroup){
        String pathDataTargetGroup = dataTargetGroup;
        String pathDataBlacklist = null;
        CountMSISDNDTO result = new CountMSISDNDTO();

        if(typeInputBlacklist == 3) {
            if(dataBlacklist != null && dataBlacklist.getSize() > 0){
                File dir1 = new File(path_data_blacklist_tmp);
                if (!dir1.exists()) {
                    dir1.mkdirs();
                }
                pathDataBlacklist = AppUtils.saveFile(dataBlacklist, path_data_blacklist_tmp);
                logger.info("black list by file: " + pathDataBlacklist);

                JSONObject jsonData = new JSONObject();
                jsonData.put("typeBlacklist", "file");
                jsonData.put("jsonSubGroup", jsonSubGroup);

                String response = httpUtil.postWithJsonAndFileAndFileBlacklist(clickHouseUrl + countMSISDNFromSubGroupByFile, jsonData.toString(), pathDataTargetGroup, pathDataBlacklist);
                logger.info("response from adapter click house: " + response);
                JSONObject jsonObjectResponse = new JSONObject(response);
                JSONObject jsonObjectData = jsonObjectResponse.getJSONObject("data");
                result.setCount(jsonObjectData.getLong("msisdn"));
                result.setRatio(jsonObjectData.getDouble("ratio"));

            }
        }else if (typeInputBlacklist == 2) {
            if (idGroupBlacklist != null) {
                logger.info("black list by target group: " + idGroupBlacklist);
                Optional<TargetGroup> groupBlacklist = targetGroupRepository.findById(idGroupBlacklist);

                JSONObject jsonData = new JSONObject();
                jsonData.put("typeBlacklist", "group");
                jsonData.put("jsonSubGroup", jsonSubGroup);
                jsonData.put("jsonBlacklist", groupBlacklist.get().getDataJson());

                String response = httpUtil.postWithJsonAndFileAndFileBlacklist(clickHouseUrl + countMSISDNFromSubGroupByFile, jsonData.toString(), pathDataTargetGroup, pathDataBlacklist);
                logger.info("response from adapter click house: " + response);
                JSONObject jsonObjectResponse = new JSONObject(response);
                JSONObject jsonObjectData = jsonObjectResponse.getJSONObject("data");
                result.setCount(jsonObjectData.getLong("msisdn"));
                result.setRatio(jsonObjectData.getDouble("ratio"));
            }
        }else{
            JSONObject jsonData = new JSONObject();
            jsonData.put("typeBlacklist", "none");
            jsonData.put("jsonSubGroup", jsonSubGroup);
            String response = httpUtil.postWithJsonAndFileAndFileBlacklist(clickHouseUrl + countMSISDNFromSubGroupByFile, jsonData.toString(), pathDataTargetGroup, pathDataBlacklist);
            logger.info("response from adapter click house: " + response);
            JSONObject jsonObjectResponse = new JSONObject(response);
            JSONObject jsonObjectData = jsonObjectResponse.getJSONObject("data");
            result.setCount(jsonObjectData.getLong("msisdn"));
            result.setRatio(jsonObjectData.getDouble("ratio"));
        }
        return result;
    }

    public CountMSISDNDTO countMSISDNFromSubGroupByJsonCriteria(String jsonData, Long typeInputBlacklist, MultipartFile dataBlacklist, Long idGroupBlacklist, String jsonSubGroup){
        String pathDataBlacklist = null;
        CountMSISDNDTO result = new CountMSISDNDTO();
        if(typeInputBlacklist == 3) {
            if (dataBlacklist != null && dataBlacklist.getSize() > 0) {
                File dir1 = new File(path_data_blacklist_tmp);
                if (!dir1.exists()) {
                    dir1.mkdirs();
                }
                pathDataBlacklist = AppUtils.saveFile(dataBlacklist, path_data_blacklist_tmp);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("typeBlacklist", "file");
                jsonObject.put("jsonMainGroup", jsonData);
                jsonObject.put("jsonSubGroup", jsonSubGroup);
                String responseCL = httpUtil.postWithJsonAndFile(clickHouseUrl + countMSISDNFromSubGroupByJSONGroup, jsonObject.toString(), pathDataBlacklist);
                logger.info("response from adapter click house: " + responseCL);
                JSONObject jsonObjectResponse = new JSONObject(responseCL);
                JSONObject jsonObjectData = jsonObjectResponse.getJSONObject("data");
                result.setCount(jsonObjectData.getLong("msisdn"));
                result.setRatio(jsonObjectData.getDouble("ratio"));
            }
        }else if (typeInputBlacklist == 2) {
            if (idGroupBlacklist != null) {
                logger.info("black list by target group: " + idGroupBlacklist);
                Optional<TargetGroup> groupBlacklist = targetGroupRepository.findById(idGroupBlacklist);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("typeBlacklist", "group");
                jsonObject.put("jsonMainGroup", jsonData);
                jsonObject.put("jsonSubGroup", jsonSubGroup);
                jsonObject.put("jsonBlacklist", groupBlacklist.get().getDataJson());
                String responseCL = httpUtil.postWithParamJson(clickHouseUrl + countMSISDNFromSubGroupByJSONGroup, jsonObject.toString());
                logger.info("response from adapter click house: " + responseCL);
                JSONObject jsonObjectResponse = new JSONObject(responseCL);
                JSONObject jsonObjectData = jsonObjectResponse.getJSONObject("data");
                result.setCount(jsonObjectData.getLong("msisdn"));
                result.setRatio(jsonObjectData.getDouble("ratio"));
            }
        }else{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("typeBlacklist", "none");
            jsonObject.put("jsonMainGroup", jsonData);
            jsonObject.put("jsonSubGroup", jsonSubGroup);
            String responseCL = httpUtil.postWithParamJson(clickHouseUrl + countMSISDNFromSubGroupByJSONGroup, jsonObject.toString());
            logger.info("response from adapter click house: " + responseCL);
            JSONObject jsonObjectResponse = new JSONObject(responseCL);
            JSONObject jsonObjectData = jsonObjectResponse.getJSONObject("data");
            result.setCount(jsonObjectData.getLong("msisdn"));
            result.setRatio(jsonObjectData.getDouble("ratio"));
        }
        return result;
    }

    public CountMSISDNDTO countMSISDNFromSubGroupByFileJOINJsonCriteria(MultipartFile dataTargetGroup, String jsonData, Long typeInputBlacklist, MultipartFile dataBlacklist, Long idGroupBlacklist, String jsonSubGroup){

        String pathDataTargetGroup = null;
        String pathDataBlacklist = null;
        CountMSISDNDTO result = new CountMSISDNDTO();
        if (dataTargetGroup != null && dataTargetGroup.getSize() > 0) {
            File dir1 = new File(path_data_target_group_tmp);
            if (!dir1.exists()) {
                dir1.mkdirs();
            }
            pathDataTargetGroup = AppUtils.saveFile(dataTargetGroup, path_data_target_group_tmp);
        }
        if(typeInputBlacklist == 3) {
            if(dataBlacklist != null && dataBlacklist.getSize() > 0){
                File dir1 = new File(path_data_target_group_tmp);
                if (!dir1.exists()) {
                    dir1.mkdirs();
                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("typeBlacklist", "file");
                jsonObject.put("jsonMainGroup", jsonData);
                jsonObject.put("jsonSubGroup", jsonSubGroup);

                pathDataBlacklist = AppUtils.saveFile(dataBlacklist, path_data_target_group_tmp);

                String responseCL = httpUtil.postWithJsonAndFileAndFileBlacklist(clickHouseUrl + countMSISDNFromSubGroupByJsonJOINFile, jsonObject.toString(), pathDataTargetGroup, pathDataBlacklist);
                logger.info("response from adapter click house: " + responseCL);

                JSONObject jsonObjectResponse = new JSONObject(responseCL);
                JSONObject jsonObjectData = jsonObjectResponse.getJSONObject("data");
                result.setCount(jsonObjectData.getLong("msisdn"));
                result.setRatio(jsonObjectData.getDouble("ratio"));

            }
        }else if(typeInputBlacklist == 2) {
            if(idGroupBlacklist != null){
                logger.info("black list by target group: " + idGroupBlacklist);

                Optional<TargetGroup> groupBlacklist = targetGroupRepository.findById(idGroupBlacklist);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("typeBlacklist", "group");
                jsonObject.put("jsonMainGroup", jsonData);
                jsonObject.put("jsonSubGroup", jsonSubGroup);
                jsonObject.put("jsonBlacklist", groupBlacklist.get().getDataJson());

                String responseCL = httpUtil.postWithJsonAndFile(clickHouseUrl + countMSISDNFromSubGroupByJsonJOINFile, jsonObject.toString(), pathDataTargetGroup);
                logger.info("response from adapter click house: " + responseCL);

                JSONObject jsonObjectResponse = new JSONObject(responseCL);
                JSONObject jsonObjectData = jsonObjectResponse.getJSONObject("data");
                result.setCount(jsonObjectData.getLong("msisdn"));
                result.setRatio(jsonObjectData.getDouble("ratio"));
            }
        }else{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("typeBlacklist", "none");
            jsonObject.put("jsonMainGroup", jsonData);
            jsonObject.put("jsonSubGroup", jsonSubGroup);
            String responseCL = httpUtil.postWithJsonAndFile(clickHouseUrl + countMSISDNFromSubGroupByJsonJOINFile, jsonObject.toString(), pathDataTargetGroup);
            logger.info("response from adapter click house: " + responseCL);
            JSONObject jsonObjectResponse = new JSONObject(responseCL);
            JSONObject jsonObjectData = jsonObjectResponse.getJSONObject("data");
            result.setCount(jsonObjectData.getLong("msisdn"));
            result.setRatio(jsonObjectData.getDouble("ratio"));
        }

        return result;
    }

    public CountMSISDNDTO countMSISDNFromSubGroupByFileJOINJsonCriteria(String dataTargetGroup, String jsonData, Long typeInputBlacklist, MultipartFile dataBlacklist, Long idGroupBlacklist, String jsonSubGroup){

        String pathDataTargetGroup = dataTargetGroup;
        String pathDataBlacklist = null;
        CountMSISDNDTO result = new CountMSISDNDTO();
        if(typeInputBlacklist == 3) {
            if(dataBlacklist != null && dataBlacklist.getSize() > 0){
                File dir1 = new File(path_data_target_group_tmp);
                if (!dir1.exists()) {
                    dir1.mkdirs();
                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("typeBlacklist", "file");
                jsonObject.put("jsonMainGroup", jsonData);
                jsonObject.put("jsonSubGroup", jsonSubGroup);

                pathDataBlacklist = AppUtils.saveFile(dataBlacklist, path_data_target_group_tmp);

                String responseCL = httpUtil.postWithJsonAndFileAndFileBlacklist(clickHouseUrl + countMSISDNFromSubGroupByJsonJOINFile, jsonObject.toString(), pathDataTargetGroup, pathDataBlacklist);
                logger.info("response from adapter click house: " + responseCL);

                JSONObject jsonObjectResponse = new JSONObject(responseCL);
                JSONObject jsonObjectData = jsonObjectResponse.getJSONObject("data");
                result.setCount(jsonObjectData.getLong("msisdn"));
                result.setRatio(jsonObjectData.getDouble("ratio"));

            }
        }else if(typeInputBlacklist == 2) {
            if(idGroupBlacklist != null){
                logger.info("black list by target group: " + idGroupBlacklist);

                Optional<TargetGroup> groupBlacklist = targetGroupRepository.findById(idGroupBlacklist);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("typeBlacklist", "group");
                jsonObject.put("jsonMainGroup", jsonData);
                jsonObject.put("jsonSubGroup", jsonSubGroup);
                jsonObject.put("jsonBlacklist", groupBlacklist.get().getDataJson());

                String responseCL = httpUtil.postWithJsonAndFile(clickHouseUrl + countMSISDNFromSubGroupByJsonJOINFile, jsonObject.toString(), pathDataTargetGroup);
                logger.info("response from adapter click house: " + responseCL);

                JSONObject jsonObjectResponse = new JSONObject(responseCL);
                JSONObject jsonObjectData = jsonObjectResponse.getJSONObject("data");
                result.setCount(jsonObjectData.getLong("msisdn"));
                result.setRatio(jsonObjectData.getDouble("ratio"));
            }
        }else{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("typeBlacklist", "none");
            jsonObject.put("jsonMainGroup", jsonData);
            jsonObject.put("jsonSubGroup", jsonSubGroup);
            String responseCL = httpUtil.postWithJsonAndFile(clickHouseUrl + countMSISDNFromSubGroupByJsonJOINFile, jsonObject.toString(), pathDataTargetGroup);
            logger.info("response from adapter click house: " + responseCL);
            JSONObject jsonObjectResponse = new JSONObject(responseCL);
            JSONObject jsonObjectData = jsonObjectResponse.getJSONObject("data");
            result.setCount(jsonObjectData.getLong("msisdn"));
            result.setRatio(jsonObjectData.getDouble("ratio"));
        }

        return result;
    }

    //===================== đếm MSISDN trừ blacklist ===============

    private long countMSISDNFromFileDataIgnoreFileBlacklist(String pathData, String pathBlacklist){
        Set<String> setMsisdnDATA = AppUtils.readFileCSVToSet(pathData);
        Set<String> setMsisdnBlacklist = AppUtils.readFileCSVToSet(pathBlacklist);
        Set<String> setResult = setMsisdnDATA.stream().filter(s-> !setMsisdnBlacklist.contains(s)).collect(Collectors.toSet());
        return setResult.size();
    }

    //==================== service đếm Số MSISDN thỏa mãn nhóm chính và adapter sẽ save vào DB  ================

    public void countMSISDNFromMainGroupByFileAndSaveToDB(String pathDataTargetGroup, Long typeInputBlacklist, String pathDataBlacklist, Long idGroupBlacklist, Long campaignType, Long campaignId){

        if(typeInputBlacklist == 3) {
            if (pathDataBlacklist != null) {
                if (pathDataBlacklist.length() > 0) {

                    logger.info("black list by file: " + pathDataBlacklist);
                    long msisdn = countMSISDNFromFileDataIgnoreFileBlacklist(pathDataTargetGroup, pathDataBlacklist);
                    String responseCL = httpUtil.Get(clickHouseUrl + queryCountMSISDNTotal);
                    JSONObject jsonCL = new JSONObject(responseCL);

                    long total = jsonCL.getLong("data");
                    Double ratio = (double) Math.round(((double) msisdn / (double) total) * 100.0) / 100.0;


                    if(campaignType == 1){
                        frequencyCampaignRepository.updateMainGroupSizeAndRatioByCampaignId(campaignId, msisdn, ratio);
                    }else if(campaignType == 2){
                        eventCampaignRepository.updateMainGroupSizeAndRatioByCampaignId(campaignId, msisdn, ratio);
                    }
                }
            }
        }
        else if(typeInputBlacklist == 2) {
            if (idGroupBlacklist != null) {
                logger.info("black list by target group: " + idGroupBlacklist);
                Optional<TargetGroup> groupBlacklist = targetGroupRepository.findById(idGroupBlacklist);
                JSONObject jsonObjectData = new JSONObject();
                jsonObjectData.put("jsonBlacklist", groupBlacklist.get().getDataJson());
                jsonObjectData.put("campaignId", campaignId);
                jsonObjectData.put("campaignType", campaignType);

                String responseCL = httpUtil.postWithJsonAndFile(clickHouseUrl + countMSISDNFromMainGroupByFileAndSaveToDB, jsonObjectData.toString(), pathDataTargetGroup);
                logger.info("response from adapter click house: " + responseCL);
            }
        }
        else {
            String responseCL = httpUtil.Get(clickHouseUrl + queryCountMSISDNTotal);
            JSONObject jsonCL = new JSONObject(responseCL);
            long total = jsonCL.getLong("data");
            long msisdn = AppUtils.readFileCSV(pathDataTargetGroup);
            Double ratio =  (double) Math.round(((double)msisdn/(double)total)*100.0)/100.0;
            if(campaignType == 1){
                frequencyCampaignRepository.updateMainGroupSizeAndRatioByCampaignId(campaignId, msisdn, ratio);
            }else if(campaignType == 2){
                eventCampaignRepository.updateMainGroupSizeAndRatioByCampaignId(campaignId, msisdn, ratio);
            }
        }

    }

    public void countMSISDNFromMainGroupByJsonCriteriaAndSaveToDB(String jsonData, Long typeInputBlacklist, String pathDataBlacklist, Long idGroupBlacklist, Long campaignType, Long campaignId){
        if(typeInputBlacklist == 3) {
            if (pathDataBlacklist != null && pathDataBlacklist.length() > 0) {

                logger.info("black list by file blacklist: " + pathDataBlacklist);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("jsonMainGroup", jsonData);
                jsonObject.put("campaignId", campaignId);
                jsonObject.put("campaignType", campaignType);
                String responseCL = httpUtil.postWithJsonAndFile(clickHouseUrl + countMSISDNFromMainGroupByJSONGroupIgnoreFileBlacklistAndSaveToDB, jsonObject.toString(), pathDataBlacklist);
                logger.info("response from adapter click house: " + responseCL);
            }
        }else if(typeInputBlacklist == 2) {
            if (idGroupBlacklist != null) {
                logger.info("black list by target group: " + idGroupBlacklist);
                Optional<TargetGroup> groupBlacklist = targetGroupRepository.findById(idGroupBlacklist);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("jsonMainGroup", jsonData);
                jsonObject.put("jsonBlacklist", groupBlacklist.get().getDataJson());
                jsonObject.put("campaignId", campaignId);
                jsonObject.put("campaignType", campaignType);
                String responseCL = httpUtil.PostWithJSON(clickHouseUrl + countMSISDNFromMainGroupByJSONGroupIgnoreJSONBlacklistAndSaveToDB, jsonObject.toString());
                logger.info("response from adapter click house: " + responseCL);
            }
        }else{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("jsonMainGroup", jsonData);
            jsonObject.put("campaignId", campaignId);
            jsonObject.put("campaignType", campaignType);
            String responseCL = httpUtil.PostWithJSON(clickHouseUrl + countMSISDNFromMainGroupByJSONGroupAndSaveToDB, jsonObject.toString());
            logger.info("response from adapter click house: " + responseCL);
        }
    }

    public void countMSISDNFromMainGroupByFileJOINJsonCriteriaAndSaveToDB(String pathDataTargetGroup, String jsonData, Long typeInputBlacklist, String pathDataBlacklist, Long idGroupBlacklist, Long campaignType, Long campaignId){

        if(typeInputBlacklist == 3) {
            if(pathDataBlacklist != null && pathDataBlacklist.length() > 0) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("jsonMainGroup", jsonData);
                jsonObject.put("campaignId", campaignId);
                jsonObject.put("campaignType", campaignType);
                String responseCL = httpUtil.postWithJsonAndFileAndFileBlacklist(clickHouseUrl + countMSISDNFromMainGroupByJsonJOINFileIgnoreFileBlacklistAndSaveToDB, jsonObject.toString(), pathDataTargetGroup, pathDataBlacklist);
                logger.info("response from adapter click house: " + responseCL);
            }
        }else if(typeInputBlacklist == 2) {
            if (idGroupBlacklist != null) {
                logger.info("black list by target group: " + idGroupBlacklist);
                Optional<TargetGroup> groupBlacklist = targetGroupRepository.findById(idGroupBlacklist);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("jsonMainGroup", jsonData);
                jsonObject.put("jsonBlacklist", groupBlacklist.get().getDataJson());
                jsonObject.put("campaignId", campaignId);
                jsonObject.put("campaignType", campaignType);
                String responseCL = httpUtil.postWithJsonAndFile(clickHouseUrl + countMSISDNFromMainGroupByJsonJOINFileIgnoreJSONBlacklistAndSaveToDB, jsonObject.toString(), pathDataTargetGroup);
                logger.info("response from adapter click house: " + responseCL);
            }
        }else{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("jsonMainGroup", jsonData);
            jsonObject.put("campaignId", campaignId);
            jsonObject.put("campaignType", campaignType);
            String responseCL = httpUtil.postWithJsonAndFile(clickHouseUrl + countMSISDNFromMainGroupByJsonJOINFileAndSaveToDB, jsonObject.toString(), pathDataTargetGroup);
            logger.info("response from adapter click house: " + responseCL);
        }
    }

    //==================== service đếm Số MSISDN thỏa mãn nhóm con và adapter clickhouse save vào DB ================

    public void countMSISDNFromSubGroupByFileAndSaveToDB(String pathDataTargetGroup, Long typeInputBlacklist, String pathDataBlacklist, Long idGroupBlacklist, String jsonSubGroup, Long subGroupId){

        if(typeInputBlacklist == 3) {
            if(pathDataBlacklist != null && pathDataBlacklist.length() > 0){

                logger.info("black list by file: " + pathDataBlacklist);

                JSONObject jsonData = new JSONObject();
                jsonData.put("typeBlacklist", "file");
                jsonData.put("jsonSubGroup", jsonSubGroup);
                jsonData.put("subGroupId", subGroupId);
                String response = httpUtil.postWithJsonAndFileAndFileBlacklist(clickHouseUrl + countMSISDNFromSubGroupByFileAndSaveToDB, jsonData.toString(), pathDataTargetGroup, pathDataBlacklist);
                logger.info("response from adapter click house: " + response);
            }
        }else if (typeInputBlacklist == 2) {
            if (idGroupBlacklist != null) {
                logger.info("black list by target group: " + idGroupBlacklist);
                Optional<TargetGroup> groupBlacklist = targetGroupRepository.findById(idGroupBlacklist);

                JSONObject jsonData = new JSONObject();
                jsonData.put("typeBlacklist", "group");
                jsonData.put("jsonSubGroup", jsonSubGroup);
                jsonData.put("jsonBlacklist", groupBlacklist.get().getDataJson());
                jsonData.put("subGroupId", subGroupId);
                String response = httpUtil.postWithJsonAndFileAndFileBlacklist(clickHouseUrl + countMSISDNFromSubGroupByFileAndSaveToDB, jsonData.toString(), pathDataTargetGroup, pathDataBlacklist);
                logger.info("response from adapter click house: " + response);

            }
        }else{
            JSONObject jsonData = new JSONObject();
            jsonData.put("typeBlacklist", "none");
            jsonData.put("jsonSubGroup", jsonSubGroup);
            String response = httpUtil.postWithJsonAndFileAndFileBlacklist(clickHouseUrl + countMSISDNFromSubGroupByFileAndSaveToDB, jsonData.toString(), pathDataTargetGroup, pathDataBlacklist);
            logger.info("response from adapter click house: " + response);
        }
    }

    public void countMSISDNFromSubGroupByJsonCriteriaAndSaveToDB(String jsonData, Long typeInputBlacklist, String pathDataBlacklist, Long idGroupBlacklist, String jsonSubGroup, Long subGroupId){

        if(typeInputBlacklist == 3) {
            if (pathDataBlacklist != null && pathDataBlacklist.length() > 0) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("typeBlacklist", "file");
                jsonObject.put("jsonMainGroup", jsonData);
                jsonObject.put("jsonSubGroup", jsonSubGroup);
                jsonObject.put("subGroupId", subGroupId);
                String responseCL = httpUtil.postWithJsonAndFile(clickHouseUrl + countMSISDNFromSubGroupByJSONGroupAndSaveToDB, jsonObject.toString(), pathDataBlacklist);
                logger.info("response from adapter click house: " + responseCL);
            }
        }else if (typeInputBlacklist == 2) {
            if (idGroupBlacklist != null) {
                logger.info("black list by target group: " + idGroupBlacklist);
                Optional<TargetGroup> groupBlacklist = targetGroupRepository.findById(idGroupBlacklist);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("typeBlacklist", "group");
                jsonObject.put("jsonMainGroup", jsonData);
                jsonObject.put("jsonSubGroup", jsonSubGroup);
                jsonObject.put("jsonBlacklist", groupBlacklist.get().getDataJson());
                jsonObject.put("subGroupId", subGroupId);
                String responseCL = httpUtil.postWithParamJson(clickHouseUrl + countMSISDNFromSubGroupByJSONGroupAndSaveToDB, jsonObject.toString());
                logger.info("response from adapter click house: " + responseCL);
            }
        }else{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("typeBlacklist", "none");
            jsonObject.put("jsonMainGroup", jsonData);
            jsonObject.put("jsonSubGroup", jsonSubGroup);
            jsonObject.put("subGroupId", subGroupId);
            String responseCL = httpUtil.postWithParamJson(clickHouseUrl + countMSISDNFromSubGroupByJSONGroupAndSaveToDB, jsonObject.toString());
            logger.info("response from adapter click house: " + responseCL);
        }
    }

    public void countMSISDNFromSubGroupByFileJOINJsonCriteriaAndSaveToDB(String pathDataTargetGroup, String jsonData, Long typeInputBlacklist, String pathDataBlacklist, Long idGroupBlacklist, String jsonSubGroup, Long subGroupId){

        if(typeInputBlacklist == 3) {
            if(pathDataBlacklist != null && pathDataBlacklist.length() > 0){

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("typeBlacklist", "file");
                jsonObject.put("jsonMainGroup", jsonData);
                jsonObject.put("jsonSubGroup", jsonSubGroup);
                jsonObject.put("subGroupId", subGroupId);
                String responseCL = httpUtil.postWithJsonAndFileAndFileBlacklist(clickHouseUrl + countMSISDNFromSubGroupByJsonJOINFileAndSaveToDB, jsonObject.toString(), pathDataTargetGroup, pathDataBlacklist);
                logger.info("response from adapter click house: " + responseCL);
            }
        }else if(typeInputBlacklist == 2) {
            if(idGroupBlacklist != null){
                logger.info("black list by target group: " + idGroupBlacklist);

                Optional<TargetGroup> groupBlacklist = targetGroupRepository.findById(idGroupBlacklist);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("typeBlacklist", "group");
                jsonObject.put("jsonMainGroup", jsonData);
                jsonObject.put("jsonSubGroup", jsonSubGroup);
                jsonObject.put("jsonBlacklist", groupBlacklist.get().getDataJson());
                jsonObject.put("subGroupId", subGroupId);
                String responseCL = httpUtil.postWithJsonAndFile(clickHouseUrl + countMSISDNFromSubGroupByJsonJOINFileAndSaveToDB, jsonObject.toString(), pathDataTargetGroup);
                logger.info("response from adapter click house: " + responseCL);
            }
        }else{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("typeBlacklist", "none");
            jsonObject.put("jsonMainGroup", jsonData);
            jsonObject.put("jsonSubGroup", jsonSubGroup);
            jsonObject.put("subGroupId", subGroupId);
            String responseCL = httpUtil.postWithJsonAndFile(clickHouseUrl + countMSISDNFromSubGroupByJsonJOINFileAndSaveToDB, jsonObject.toString(), pathDataTargetGroup);
            logger.info("response from adapter click house: " + responseCL);
        }
    }


}
