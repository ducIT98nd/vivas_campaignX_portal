package com.vivas.campaignx.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivas.campaignx.common.AppUtils;
import com.vivas.campaignx.common.HttpUtil;
import com.vivas.campaignx.dto.CountMSISDNDTO;
import com.vivas.campaignx.dto.CriteriaDTO;
import com.vivas.campaignx.entity.EventCampaign;
import com.vivas.campaignx.entity.FrequencyCampaign;
import com.vivas.campaignx.entity.MappingCriteria;
import com.vivas.campaignx.entity.TargetGroup;
import com.vivas.campaignx.repository.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class TargetGroupService {
    protected final Logger logger = LogManager.getLogger(this.getClass().getName());
    @Value("${path_data_target_group}")
    private String path_data_target_group;

    private boolean condition;

    @Autowired
    private MappingCriteriaService mappingCriteriaService;

    @Autowired
    private TargetGroupRepository targetGroupRepository;

    @Autowired
    private MappingCriteriaRepository mappingCrieriaRepository;

    @Autowired
    private EventCampaignRepository eventCampaignRepository;

    @Autowired
    private FrequencyCampaignRepository frequencyCampaignRepository;

    @Autowired
    private PackageDataRepository packageDataRepository;

    @Autowired
    private HttpUtil httpUtil;

    @Value("${clickhouse.adapter.url}")
    private String clickHouseUrl;

    @Value("${clickhouse.adapter.countMSISDNByJSONCriteriaAndSaveToDB}")
    private String countMSISDNByJSONCriteriaAndSaveToDB;

    @Value("${clickhouse.adapter.countMSISDNByJSONCriteria}")
    private String countMSISDNByJSONCriteria;

    @Value("${clickhouse.adapter.genQuery}")
    private String genQuery;

    @Value("${clickhouse.adapter.countMSISDNByJSONCriteriaAndFileAndSaveToDB}")
    private String countMSISDNByJSONCriteriaAndFileAndSaveToDB;

    @Value("${clickhouse.adapter.countMSISDNByJSONCriteriaAndFile}")
    private String countMSISDNByJSONCriteriaAndFile;

    @Value("${clickhouse.adapter.queryCountMSISDNTotal}")
    private String queryCountMSISDNTotal;

    @Value("${clickhouse.adapter.genSubQuery}")
    private String genSubQuery;

    public Page<TargetGroup> getAll(Integer pageSize, Integer currentPage) {
        Pageable paging = PageRequest.of(currentPage - 1, pageSize);
        return targetGroupRepository.getAll(paging);
    }

    public Page<TargetGroup> getTargetGroup(Integer pageSize, Integer currentPage, String targetName, String createdUser, String createdDate) {
        Pageable paging = PageRequest.of(currentPage - 1, pageSize);
        String p_targetName = !AppUtils.isStringNullOrEmpty(targetName) ? targetName.toLowerCase().trim() : targetName;
        String p_createdUser = !AppUtils.isStringNullOrEmpty(createdUser) ? createdUser.toLowerCase().trim() : createdUser;
        return targetGroupRepository.getTargetGroup(removeAccent(p_targetName), createdDate, removeAccent(p_createdUser), paging);
    }

    public List<TargetGroup> exportAll() {
        return targetGroupRepository.exportAll();
    }

    public List<TargetGroup> exportTargetGroup(String targetName, String createdUser, String createdDate) {
        String p_targetName = !AppUtils.isStringNullOrEmpty(targetName) ? targetName.toLowerCase().trim() : targetName;
        String p_createdUser = !AppUtils.isStringNullOrEmpty(createdUser) ? createdUser.toLowerCase().trim() : createdUser;
        return targetGroupRepository.exportTargetGroup(removeAccent(p_targetName), createdDate, removeAccent(p_createdUser));
    }

    private String removeAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replaceAll("Đ", "D").replace("đ", "d");
    }

    public void deleteTargetGroup(Long id) {
        targetGroupRepository.deleteTargetGroup(id);
    }

    public Optional<TargetGroup> findByIdAndStatusIsNot(Long id, Integer status) {
        return targetGroupRepository.findByIdAndStatusNot(id, status);
    }

    public Long save(String name, String description, String channel, String status,
                     MultipartFile dataTargetGroup, String jsonData, String groupSize, String wholeNetwork) throws IOException {
        UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        TargetGroup targetGroup = new TargetGroup();
        targetGroup.setName(name);
        targetGroup.setDescription(description);
        targetGroup.setChannel(Integer.parseInt(channel));
        targetGroup.setCreatedDate(new Date());
        targetGroup.setUpdatedDate(new Date());
        targetGroup.setStatus(Integer.parseInt(status));
        targetGroup.setCreatedUser(currentUser.getUsername());
        targetGroup.setType(0);
        targetGroup.setDataJson(jsonData);
        String pathDataTargetGroup = null;
        String originalFileName = null;
        if (dataTargetGroup.getSize() > 0) {
            File dir1 = new File(path_data_target_group);
            if (!dir1.exists()) {
                dir1.mkdirs();
            }
            pathDataTargetGroup = AppUtils.saveFile(dataTargetGroup, path_data_target_group);
            originalFileName = dataTargetGroup.getOriginalFilename();
        }
        targetGroup.setUpdatedUser(currentUser.getUsername());
        if (channel.equals("1") || channel.equals("4")) {
            String response = genQuery(jsonData);
            logger.info("query:", response);
            targetGroup.setDataJson(jsonData);
            targetGroup.setQuery(response);
        }
        if (channel.equals("3") || channel.equals("4")) {
            targetGroup.setPathFileMsisdn(pathDataTargetGroup);
            targetGroup.setOriginalNameFileDataCustomer(originalFileName);
        }
        Long msisdn = 0L;
        targetGroup.setQuantityMsisdn(Long.parseLong(groupSize));
        if (channel.equals("3")) {
            msisdn = AppUtils.readFileCSV(pathDataTargetGroup);

            String response1 = queryCountMSISDNTotal();
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
                wholeNetWork = (double) Math.round((((double )msisdn/totalMsisdn)*100) * 100) / 100;

            }
            targetGroup.setQuantityMsisdn(msisdn);
            targetGroup.setWholeNetwork(wholeNetWork);

        }
        targetGroup = targetGroupRepository.save(targetGroup);
        if(channel.equals("1")) {
            String response = queryByJSONAndTargetGroupId(targetGroup.getDataJson(), targetGroup.getId());
            logger.info("response from api: " + response);
        }
        if(channel.equals("4")) {
            String response = queryByFileAndJsonAndTargetGroupId(targetGroup.getId(), targetGroup.getDataJson(), targetGroup.getPathFileMsisdn());
            logger.info("response from api: " + response);
        }
        return targetGroup.getId();
    }

    public Long update(String id, String name, String description, String channel, String status, MultipartFile dataTargetGroup, String jsonData, String groupSize, String wholeNetwork) throws IOException {
        UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        Optional<TargetGroup> targetGroupOptional = targetGroupRepository.findById(Long.parseLong(id));
        TargetGroup targetGroup = new TargetGroup();
        if (targetGroupOptional.isPresent()) {
            targetGroup = targetGroupOptional.get();
        }
        targetGroup.setName(name);
        targetGroup.setDescription(description);
        targetGroup.setChannel(Integer.parseInt(channel));
        targetGroup.setUpdatedDate(new Date());
        targetGroup.setStatus(Integer.parseInt(status));
        targetGroup.setCreatedUser(currentUser.getUsername());
        targetGroup.setUpdatedUser(currentUser.getUsername());
        targetGroup.setDataJson(jsonData);
        String pathDataTargetGroup = null;
        String originalFileName = null;
        if (!dataTargetGroup.isEmpty()) {
            if (dataTargetGroup.getSize() > 0) {
                File dir1 = new File(path_data_target_group);
                if (!dir1.exists()) {
                    dir1.mkdirs();
                }
                pathDataTargetGroup = AppUtils.saveFile(dataTargetGroup, path_data_target_group);
                originalFileName = dataTargetGroup.getOriginalFilename();
            }
            if (channel.equals("3") || channel.equals("4")) {
                targetGroup.setPathFileMsisdn(pathDataTargetGroup);
                targetGroup.setOriginalNameFileDataCustomer(originalFileName);
            }
        }

        if (channel.equals("3")) {
            long msisdn = AppUtils.readFileCSV(pathDataTargetGroup);

            String response1 = queryCountMSISDNTotal();
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
                wholeNetWork = (double) Math.round((((double )msisdn/totalMsisdn)*100) * 100) / 100;

            }
            targetGroup.setQuantityMsisdn(msisdn);
            targetGroup.setWholeNetwork(wholeNetWork);

        }
        targetGroup = targetGroupRepository.save(targetGroup);
        if(channel.equals("1")) {
            String response = queryByJSONAndTargetGroupId(targetGroup.getDataJson(), targetGroup.getId());
            logger.info("response from api: " + response);
        }
        if(channel.equals("4")) {
            String response = queryByFileAndJsonAndTargetGroupId(targetGroup.getId(), targetGroup.getDataJson(), targetGroup.getPathFileMsisdn());
            logger.info("response from api: " + response);
        }
        return targetGroup.getId();
    }

    public Long saveCopy(String name, String description, String channel, String status,
                     MultipartFile dataTargetGroup, String jsonData, Long groupSize, Double wholeNetwork,String pathFileMsisdnOld) throws IOException {
        UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        TargetGroup targetGroup = new TargetGroup();
        targetGroup.setName(name);
        targetGroup.setDescription(description);
        targetGroup.setChannel(Integer.parseInt(channel));
        targetGroup.setCreatedDate(new Date());
        targetGroup.setUpdatedDate(new Date());
        targetGroup.setStatus(Integer.parseInt(status));
        targetGroup.setCreatedUser(currentUser.getUsername());
        targetGroup.setDataJson(jsonData);
        targetGroup.setType(0);
        String pathDataTargetGroup = null;
        String originalFileName = null;
        if (dataTargetGroup != null && dataTargetGroup.getSize() > 0) {
            File dir1 = new File(path_data_target_group);
            if (!dir1.exists()) {
                dir1.mkdirs();
            }
            pathDataTargetGroup = AppUtils.saveFile(dataTargetGroup, path_data_target_group);
            originalFileName = dataTargetGroup.getOriginalFilename();
        }else{
            pathDataTargetGroup = pathFileMsisdnOld;
        }
        targetGroup.setUpdatedUser(currentUser.getUsername());
        if (channel.equals("1") || channel.equals("4")) {
            String response = genQuery(jsonData);
            logger.info("query:", response);
            targetGroup.setDataJson(jsonData);
            targetGroup.setQuery(response);
        }
        if (channel.equals("3") || channel.equals("4")) {
            targetGroup.setPathFileMsisdn(pathDataTargetGroup);
            targetGroup.setOriginalNameFileDataCustomer(originalFileName);

        }

        if (channel.equals("3")) {
            long msisdn = AppUtils.readFileCSV(pathDataTargetGroup);

            String response1 = queryCountMSISDNTotal();
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
                wholeNetWork = (double) Math.round((((double )msisdn/totalMsisdn)*100) * 100) / 100;

            }
            targetGroup.setQuantityMsisdn(msisdn);
            targetGroup.setWholeNetwork(wholeNetWork);

        }else {
            targetGroup.setQuantityMsisdn(groupSize);
            targetGroup.setWholeNetwork(wholeNetwork);
        }
        targetGroup = targetGroupRepository.save(targetGroup);
        if(channel.equals("1")) {
            String response = queryByJSONAndTargetGroupId(targetGroup.getDataJson(), targetGroup.getId());
            logger.info("response from api: " + response);
        }
        if(channel.equals("4")) {
            String response = queryByFileAndJsonAndTargetGroupId(targetGroup.getId(), targetGroup.getDataJson(), targetGroup.getPathFileMsisdn());
            logger.info("response from api: " + response);
        }
        return targetGroup.getId();
    }

    public long countByName(String name) {
        return targetGroupRepository.countByName(name);
    }

    public long countByNameNotInIds(String name, Long id) {
        return targetGroupRepository.countByNameNotInIds(name, id);
    }

    public boolean saveCriteriaSetup(String jsonData, Long targetGroupId, Long idSubTargetGroup) {
        logger.info("json: " + jsonData);
        JSONObject jsonObject = new JSONObject(jsonData);
        JSONArray jsonArrayData = jsonObject.getJSONArray("childs");
        Map<Integer, Long> mapValueKeyInJsonWithKeyinDB = new HashMap<>();
        //Duyet level 1
        List<MappingCriteria> listMapping = new ArrayList<>();
        listMapping = getListMappingFromJsonArray(jsonArrayData, targetGroupId, idSubTargetGroup, 1);
        for (int i = 0; i < listMapping.size(); i++) {
            MappingCriteria temp = listMapping.get(i);
            String con = jsonObject.getString("condition");
            if (con.equals("AND")) {
                temp.setType(1);
            } else {
                temp.setType(2);
            }
            MappingCriteria o = mappingCrieriaRepository.saveAndFlush(temp);
            JSONObject objTemp = new JSONObject(temp.getSelectedValue());
            mapValueKeyInJsonWithKeyinDB.put(objTemp.getInt("key"), o.getId());
        }
        System.out.println(mapValueKeyInJsonWithKeyinDB);
        //Duyet level 2
        listMapping.clear();
        for (int i = 0; i < jsonArrayData.length(); i++) {
            JSONObject jsonObjElementLV1 = jsonArrayData.getJSONObject(i);
            JSONArray arrObjLV2 = jsonObjElementLV1.getJSONArray("childs");
            if (arrObjLV2.length() > 0) {
                System.out.println(arrObjLV2.toString());
                String con = jsonObjElementLV1.getString("condition");
                listMapping = getListMappingFromJsonArray(arrObjLV2, targetGroupId, idSubTargetGroup, 2);
                for (int j = 0; j < listMapping.size(); j++) {
                    MappingCriteria temp = listMapping.get(j);
                    System.out.println(temp.toString());
                    if (con.equals("AND")) {
                        temp.setType(1);
                    } else {
                        temp.setType(2);
                    }
                    JSONObject objTemp = new JSONObject(temp.getSelectedValue());
                    temp.setParentId(mapValueKeyInJsonWithKeyinDB.get(objTemp.getInt("parentKey")));
                    MappingCriteria o = mappingCrieriaRepository.saveAndFlush(temp);
                    mapValueKeyInJsonWithKeyinDB.put(objTemp.getInt("key"), o.getId());
                }
            }
        }
        System.out.println(mapValueKeyInJsonWithKeyinDB);
        //Duyet level 3
        listMapping.clear();
        //lap mang level 1
        for (int i = 0; i < jsonArrayData.length(); i++) {
            JSONObject jsonObjElementLV1 = jsonArrayData.getJSONObject(i);
            //lay mang level 2
            JSONArray arrObjLV2 = jsonObjElementLV1.getJSONArray("childs");
            if (arrObjLV2.length() > 0) {
                //lap mang level 2
                for (int j = 0; j < arrObjLV2.length(); j++) {
                    JSONObject jsonObjElementLV2 = arrObjLV2.getJSONObject(j);
                    //lay mang level 3
                    JSONArray arrObjLV3 = jsonObjElementLV2.getJSONArray("childs");
                    if(arrObjLV3.length() > 0) {
                        String conLv3 = jsonObjElementLV2.getString("condition");
                        listMapping = getListMappingFromJsonArray(arrObjLV3, targetGroupId, idSubTargetGroup, 3);
                        for (int k = 0; k < listMapping.size(); k++) {
                            MappingCriteria temp = listMapping.get(k);
                            if (conLv3.equals("AND")) {
                                temp.setType(1);
                            } else {
                                temp.setType(2);
                            }
                            JSONObject objTemp = new JSONObject(temp.getSelectedValue());
                            temp.setParentId(mapValueKeyInJsonWithKeyinDB.get(objTemp.getInt("parentKey")));
                            MappingCriteria o = mappingCrieriaRepository.saveAndFlush(temp);
                        }
                    }
                }
            }
        }
        System.out.println(mapValueKeyInJsonWithKeyinDB);
        mapValueKeyInJsonWithKeyinDB.clear();
        return true;
    }

    public static List<MappingCriteria> getListMappingFromJsonArray(JSONArray jsonArrayData, Long targetGroupId, Long idSubTargetGroup, Integer level) {
        List<MappingCriteria> listMapping = new ArrayList<>();
        for (int i = 0; i < jsonArrayData.length(); i++) {
            JSONObject jsonObjElement = jsonArrayData.getJSONObject(i);
            MappingCriteria mappingLV1 = new MappingCriteria();
            JSONObject objValueCurrent = jsonObjElement.getJSONObject("data");
            mappingLV1.setCriteriaName(objValueCurrent.getString("name"));
            mappingLV1.setIdBigdataCriteria(objValueCurrent.getLong("criteriaId"));
            mappingLV1.setPosition(objValueCurrent.getLong("position"));
            mappingLV1.setUnit(objValueCurrent.getString("unit"));
            mappingLV1.setSelectedValue(objValueCurrent.toString());
            mappingLV1.setIdTargetGroup(targetGroupId);
            mappingLV1.setIdSubTargetGroup(idSubTargetGroup);
            mappingLV1.setLevelCriteria(level);
            listMapping.add(mappingLV1);

        }
        return listMapping;
    }


    public List<MappingCriteria> getListCriteriaLevel1(Long targetGroupId) {
        return mappingCrieriaRepository.getAllCriteriaLevel1ByIdTargetGroup(targetGroupId);
    }

    public String getHtmlField(Long targetGroupId) throws JsonProcessingException {
        List<MappingCriteria> criteriaListLevel1 = mappingCrieriaRepository.getAllCriteriaLevel1ByIdTargetGroup(targetGroupId);
        List<MappingCriteria> criteriaListLevel2 = mappingCrieriaRepository.getAllCriteriaLevel2ByIdTargetGroup(targetGroupId);
        List<MappingCriteria> criteriaListLevel3 = mappingCrieriaRepository.getAllCriteriaLevel3ByIdTargetGroup(targetGroupId);
        StringBuilder htmlField = new StringBuilder();
        String resultJoin = "";
        /*=============*/
        HashMap<Long, List<MappingCriteria>> mappingCriteriaLevel2 = new HashMap<>();
        for (int j = 0; j < criteriaListLevel2.size(); j++) {
            MappingCriteria mappingCriteriaLv2 = criteriaListLevel2.get(j);
            List<MappingCriteria> criteriaList;
            if (mappingCriteriaLevel2.containsKey(mappingCriteriaLv2.getParentId())) {
                criteriaList = mappingCriteriaLevel2.get(mappingCriteriaLv2.getParentId());
            } else {
                criteriaList = new ArrayList<>();
            }
            criteriaList.add(mappingCriteriaLv2);
            mappingCriteriaLevel2.put(mappingCriteriaLv2.getParentId(), criteriaList);
        }
        /*=============*/
        HashMap<Long, List<MappingCriteria>> mappingCriteriaLevel3 = new HashMap<>();
        for (int j = 0; j < criteriaListLevel3.size(); j++) {
            MappingCriteria mappingCriteriaLv3 = criteriaListLevel3.get(j);
            List<MappingCriteria> criteriaList;
            if (mappingCriteriaLevel3.containsKey(mappingCriteriaLv3.getParentId())) {
                criteriaList = mappingCriteriaLevel3.get(mappingCriteriaLv3.getParentId());
            } else {
                criteriaList = new ArrayList<>();
            }
            criteriaList.add(mappingCriteriaLv3);
            mappingCriteriaLevel3.put(mappingCriteriaLv3.getParentId(), criteriaList);
        }
        /*=============*/
        List<MappingCriteria> criteriaListNew2 = null;
        for (int i = 0; i < criteriaListLevel1.size(); i++) {
            MappingCriteria mappingCriteriaLv1 = criteriaListLevel1.get(i);
            ObjectMapper objectMapper1 = new ObjectMapper();
            CriteriaDTO criteriaDTO1 = objectMapper1.readValue(mappingCriteriaLv1.getSelectedValue(), CriteriaDTO.class);
            if (criteriaDTO1.getCriteriaId() == 1 || criteriaDTO1.getCriteriaId() == 2 ||
                    criteriaDTO1.getCriteriaId() == 6 || criteriaDTO1.getCriteriaId() == 7 || criteriaDTO1.getCriteriaId() == 65 || criteriaDTO1.getCriteriaId() == 66) {
                htmlField.append("<li style=\"width:100%\" >\n");
                htmlField.append("<div>\n");
                htmlField.append("<p class='dieu-kien dk-lv1'><i class='ti-check'></i>").append(criteriaDTO1.getValue2()).append("</p>\n");
                htmlField.append("</div>\n");
            } else if (criteriaDTO1.getCriteriaId() == 3 || criteriaDTO1.getCriteriaId() == 4) {
                htmlField.append("<li style=\"width:100%\" >\n");
                htmlField.append("<div>\n");
                htmlField.append("<p class='dieu-kien dk-lv1'><i class='ti-check'></i>").append(criteriaDTO1.getValue3()).append("</p>\n");
                htmlField.append("</div>\n");
            } else if (criteriaDTO1.getCriteriaId() == 5 || criteriaDTO1.getCriteriaId() == 56 ||
                    criteriaDTO1.getCriteriaId() == 60) {
                if(criteriaDTO1.getCriteriaId() == 56 || criteriaDTO1.getCriteriaId() == 60) {
                    String packageName = null;
                    if(criteriaDTO1.getValue().equals("all")) packageName = ":Tất cả";
                    else packageName = packageDataRepository.findById(Long.parseLong(criteriaDTO1.getValue())).get().getPackageName();
                    criteriaDTO1.setValue(packageName);
                }
                htmlField.append("<li style=\"width:100%\" >\n");
                htmlField.append("<div>\n");
                htmlField.append("<p class='dieu-kien dk-lv1'><i class='ti-check'></i>").append(criteriaDTO1.getValue4()).append("</p>\n");
                htmlField.append("</div>\n");
            } else if (criteriaDTO1.getCriteriaId() == 57 || criteriaDTO1.getCriteriaId() == 58
                    || criteriaDTO1.getCriteriaId() == 61 || criteriaDTO1.getCriteriaId() == 62) {
                String packageName = packageDataRepository.findById(Long.parseLong(criteriaDTO1.getServiceCode())).get().getPackageName();
                criteriaDTO1.setServiceCode(packageName);
                htmlField.append("<li style=\"width:100%\" >\n");
                htmlField.append("<div>\n");
                htmlField.append("<p class='dieu-kien dk-lv1'><i class='ti-check'></i>").append(criteriaDTO1.getValue5()).append("</p>\n");
                htmlField.append("</div>\n");
            } else {
                htmlField.append("<li style=\"width:100%\" >\n");
                htmlField.append("<div>\n");
                htmlField.append("<p class='dieu-kien dk-lv1'><i class='ti-check'></i>").append(criteriaDTO1.getValue1()).append("</p>\n");
                htmlField.append("</div>\n");
            }
            List<MappingCriteria> criteriaListLv2 = mappingCriteriaLevel2.get(mappingCriteriaLv1.getId());
            if (criteriaListLv2 != null) {
                if (criteriaListLv2.get(0).getType() == 1) {
                    resultJoin = "Thỏa mãn tất cả các điều kiện sau đây:";
                } else if (criteriaListLv2.get(0).getType() == 2) {
                    resultJoin = "Thỏa mãn một trong số các điều kiện sau đây:";
                }
                htmlField.append("<ul>\n");
                htmlField.append("<li><span style='margin-left: 6px'>").append(resultJoin).append("</span></li>\n");
                for (int j = 0; j < criteriaListLv2.size(); j++) {
                    MappingCriteria mappingCriteriaLv2 = criteriaListLv2.get(j);
                    ObjectMapper objectMapper2 = new ObjectMapper();
                    CriteriaDTO criteriaDTO2 = objectMapper2.readValue(mappingCriteriaLv2.getSelectedValue(), CriteriaDTO.class);
                    if (criteriaDTO2.getCriteriaId() == 1 || criteriaDTO2.getCriteriaId() == 2 ||
                            criteriaDTO2.getCriteriaId() == 6 || criteriaDTO2.getCriteriaId() == 7 || criteriaDTO2.getCriteriaId() == 65 || criteriaDTO2.getCriteriaId() == 66) {
                        htmlField.append("<li>\n");
                        htmlField.append("<div>\n");
                        htmlField.append("<p class='dieu-kien dk-lv2'><i class='ti-check'></i>").append(criteriaDTO2.getValue2()).append("</p>\n");
                        htmlField.append("</div>\n");
                    } else if (criteriaDTO2.getCriteriaId() == 3 || criteriaDTO2.getCriteriaId() == 4) {
                        htmlField.append("<li>\n");
                        htmlField.append("<div>\n");
                        htmlField.append("<p class='dieu-kien dk-lv2'><i class='ti-check'></i>").append(criteriaDTO2.getValue3()).append("</p>\n");
                        htmlField.append("</div>\n");
                    } else if (criteriaDTO2.getCriteriaId() == 5 || criteriaDTO2.getCriteriaId() == 56 ||
                            criteriaDTO2.getCriteriaId() == 60) {
                        if(criteriaDTO2.getCriteriaId() == 56 || criteriaDTO2.getCriteriaId() == 60) {
                            String packageName = null;
                            if(criteriaDTO2.getValue().equals("all")) packageName = ":Tất cả";
                            else packageName = packageDataRepository.findById(Long.parseLong(criteriaDTO2.getValue())).get().getPackageName();
                            criteriaDTO2.setValue(packageName);
                        }
                        htmlField.append("<li>\n");
                        htmlField.append("<div>\n");
                        htmlField.append("<p class='dieu-kien dk-lv2'><i class='ti-check'></i>").append(criteriaDTO2.getValue4()).append("</p>\n");
                        htmlField.append("</div>\n");
                    } else if (criteriaDTO2.getCriteriaId() == 57 || criteriaDTO2.getCriteriaId() == 58
                            || criteriaDTO2.getCriteriaId() == 61 || criteriaDTO2.getCriteriaId() == 62) {
                        String packageName = packageDataRepository.findById(Long.parseLong(criteriaDTO2.getServiceCode())).get().getPackageName();
                        criteriaDTO2.setServiceCode(packageName);
                        htmlField.append("<li>\n");
                        htmlField.append("<div>\n");
                        htmlField.append("<p class='dieu-kien dk-lv1'><i class='ti-check'></i>").append(criteriaDTO2.getValue5()).append("</p>\n");
                        htmlField.append("</div>\n");
                    } else {
                        htmlField.append("<li>\n");
                        htmlField.append("<div>\n");
                        htmlField.append("<p class='dieu-kien dk-lv2'><i class='ti-check'></i> ").append(criteriaDTO2.getValue1()).append("</p>\n");
                        htmlField.append("</div>\n");
                    }
                    List<MappingCriteria> criteriaListLv3 = mappingCriteriaLevel3.get(mappingCriteriaLv2.getId());
                    if (criteriaListLv3 != null) {
                        if (criteriaListLv3.get(0).getType() == 1) {
                            resultJoin = "Thỏa mãn tất cả các điều kiện sau đây";
                        } else if (criteriaListLv3.get(0).getType() == 2) {
                            resultJoin = "Thỏa mãn một trong số các điều kiện sau đây";
                        }
                        htmlField.append("<ul>\n");
                        htmlField.append("<li><span style='margin-left: 6px'>").append(resultJoin).append("</span></li>\n");
                        for (int k = 0; k < criteriaListLv3.size(); k++) {
                            MappingCriteria mappingCriteriaLv3 = criteriaListLv3.get(k);
                            ObjectMapper objectMapper3 = new ObjectMapper();
                            logger.info("mappingCriteriaLv3.getSelectedValue(): "  + mappingCriteriaLv3.getSelectedValue());
                            CriteriaDTO criteriaDTO3 = objectMapper3.readValue(mappingCriteriaLv3.getSelectedValue(), CriteriaDTO.class);
                            if (criteriaDTO3.getCriteriaId() == 1 || criteriaDTO3.getCriteriaId() == 2 ||
                                    criteriaDTO3.getCriteriaId() == 6 || criteriaDTO3.getCriteriaId() == 7 || criteriaDTO3.getCriteriaId() == 65 || criteriaDTO3.getCriteriaId() == 66) {
                                htmlField.append("<li>\n");
                                htmlField.append("<div>\n");
                                htmlField.append("<p class='dieu-kien dk-lv2'><i class='ti-check'></i> ").append(criteriaDTO3.getValue2()).append("</p>\n");
                                htmlField.append("</div>\n");
                                htmlField.append("</li>\n");
                            } else if (criteriaDTO3.getCriteriaId() == 3 || criteriaDTO3.getCriteriaId() == 4) {
                                htmlField.append("<li>\n");
                                htmlField.append("<div>\n");
                                htmlField.append("<p class='dieu-kien dk-lv2'><i class='ti-check'></i>").append(criteriaDTO3.getValue3()).append("</p>\n");
                                htmlField.append("</div>\n");
                                htmlField.append("</li>\n");
                            } else if (criteriaDTO3.getCriteriaId() == 5 || criteriaDTO3.getCriteriaId() == 56 ||
                                    criteriaDTO3.getCriteriaId() == 60) {
                                if(criteriaDTO3.getCriteriaId() == 56 || criteriaDTO3.getCriteriaId() == 60) {
                                    String packageName = null;
                                    if(criteriaDTO3.getValue().equals("all")) packageName = ":Tất cả";
                                    else packageName = packageDataRepository.findById(Long.parseLong(criteriaDTO3.getValue())).get().getPackageName();
                                    criteriaDTO3.setValue(packageName);
                                }
                                htmlField.append("<li>\n");
                                htmlField.append("<div>\n");
                                htmlField.append("<p class='dieu-kien dk-lv2'><i class='ti-check'></i>").append(criteriaDTO3.getValue4()).append("</p>\n");
                                htmlField.append("</div>\n");
                                htmlField.append("</li>\n");
                            } else if (criteriaDTO3.getCriteriaId() == 57 || criteriaDTO3.getCriteriaId() == 58
                                    || criteriaDTO3.getCriteriaId() == 61 || criteriaDTO3.getCriteriaId() == 62) {
                                String packageName = packageDataRepository.findById(Long.parseLong(criteriaDTO3.getServiceCode())).get().getPackageName();
                                criteriaDTO3.setServiceCode(packageName);
                                htmlField.append("<li>\n");
                                htmlField.append("<div>\n");
                                htmlField.append("<p class='dieu-kien dk-lv1'><i class='ti-check'></i>").append(criteriaDTO3.getValue5()).append("</p>\n");
                                htmlField.append("</div>\n");
                            } else {
                                htmlField.append("<li>\n");
                                htmlField.append("<div>\n");
                                htmlField.append("<p class='dieu-kien dk-lv2'><i class='ti-check'></i>").append(criteriaDTO3.getValue1()).append("</p>\n");
                                htmlField.append("</div>\n");
                                htmlField.append("</li>\n");
                            }
                        }
                        htmlField.append("</ul>\n");
                    }
                    htmlField.append("</li>\n");
                }
                htmlField.append("</ul>\n");
            }
            htmlField.append("</li>\n");
        }
        return htmlField.toString();
    }

    public String getHtmlFieldCampaign(Long targetGroupId) throws JsonProcessingException {
        List<MappingCriteria> criteriaListLevel1 = mappingCrieriaRepository.getAllCriteriaLevel1ByIdTargetGroup(targetGroupId);
        List<MappingCriteria> criteriaListLevel2 = mappingCrieriaRepository.getAllCriteriaLevel2ByIdTargetGroup(targetGroupId);
        List<MappingCriteria> criteriaListLevel3 = mappingCrieriaRepository.getAllCriteriaLevel3ByIdTargetGroup(targetGroupId);
        StringBuilder htmlField = new StringBuilder();
        String resultJoin = "";
        /*=============*/
        HashMap<Long, List<MappingCriteria>> mappingCriteriaLevel2 = new HashMap<>();
        for (int j = 0; j < criteriaListLevel2.size(); j++) {
            MappingCriteria mappingCriteriaLv2 = criteriaListLevel2.get(j);
            List<MappingCriteria> criteriaList;
            if (mappingCriteriaLevel2.containsKey(mappingCriteriaLv2.getParentId())) {
                criteriaList = mappingCriteriaLevel2.get(mappingCriteriaLv2.getParentId());
            } else {
                criteriaList = new ArrayList<>();
            }
            criteriaList.add(mappingCriteriaLv2);
            mappingCriteriaLevel2.put(mappingCriteriaLv2.getParentId(), criteriaList);
        }
        /*=============*/
        HashMap<Long, List<MappingCriteria>> mappingCriteriaLevel3 = new HashMap<>();
        for (int j = 0; j < criteriaListLevel3.size(); j++) {
            MappingCriteria mappingCriteriaLv3 = criteriaListLevel3.get(j);
            List<MappingCriteria> criteriaList;
            if (mappingCriteriaLevel3.containsKey(mappingCriteriaLv3.getParentId())) {
                criteriaList = mappingCriteriaLevel3.get(mappingCriteriaLv3.getParentId());
            } else {
                criteriaList = new ArrayList<>();
            }
            criteriaList.add(mappingCriteriaLv3);
            mappingCriteriaLevel3.put(mappingCriteriaLv3.getParentId(), criteriaList);
        }
        /*=============*/
        List<MappingCriteria> criteriaListNew2 = null;
        for (int i = 0; i < criteriaListLevel1.size(); i++) {
            MappingCriteria mappingCriteriaLv1 = criteriaListLevel1.get(i);
            ObjectMapper objectMapper1 = new ObjectMapper();
            CriteriaDTO criteriaDTO1 = objectMapper1.readValue(mappingCriteriaLv1.getSelectedValue(), CriteriaDTO.class);
            if (criteriaDTO1.getCriteriaId() == 1 || criteriaDTO1.getCriteriaId() == 2 ||
                    criteriaDTO1.getCriteriaId() == 6 || criteriaDTO1.getCriteriaId() == 7 || criteriaDTO1.getCriteriaId() == 65 || criteriaDTO1.getCriteriaId() == 66) {
                htmlField.append("<p style='margin-bottom: 0px; font-weight: lighter'>+");
                htmlField.append(criteriaDTO1.getValue2());
                htmlField.append("</p>\n");
            } else if (criteriaDTO1.getCriteriaId() == 3) {
                htmlField.append("<p style='margin-bottom: 0px; font-weight: lighter'>+");
                htmlField.append(criteriaDTO1.getValue6());
                htmlField.append("</p>\n");
            } else if (criteriaDTO1.getCriteriaId() == 4) {
                htmlField.append("<p style='margin-bottom: 0px; font-weight: lighter'>+");
                htmlField.append(criteriaDTO1.getValue3());
                htmlField.append("</p>\n");
            }else if (criteriaDTO1.getCriteriaId() == 5 ) {
                htmlField.append("<p style='margin-bottom: 0px; font-weight: lighter'>+");
                htmlField.append(criteriaDTO1.getValue7());
                htmlField.append("</p>\n");
            }else if (criteriaDTO1.getCriteriaId() == 56 ||
                    criteriaDTO1.getCriteriaId() == 60) {
                if(criteriaDTO1.getCriteriaId() == 56 || criteriaDTO1.getCriteriaId() == 60) {
                    String packageName = null;
                    if(criteriaDTO1.getValue().equals("all")) packageName = ":Tất cả";
                    else packageName = packageDataRepository.findById(Long.parseLong(criteriaDTO1.getValue())).get().getPackageName();
                    criteriaDTO1.setValue(packageName);
                }
                htmlField.append("<p style='margin-bottom: 0px;  font-weight: lighter'>+");
                htmlField.append(criteriaDTO1.getValue4());
                htmlField.append("</p>\n");
            } else if (criteriaDTO1.getCriteriaId() == 57 || criteriaDTO1.getCriteriaId() == 58
                    || criteriaDTO1.getCriteriaId() == 61 || criteriaDTO1.getCriteriaId() == 62) {
                String packageName = packageDataRepository.findById(Long.parseLong(criteriaDTO1.getServiceCode())).get().getPackageName();
                criteriaDTO1.setServiceCode(packageName);
                htmlField.append("<p style='margin-bottom: 0px; font-weight: lighter'>+");
                htmlField.append(criteriaDTO1.getValue5());
                htmlField.append("</p>\n");
            } else {
                htmlField.append("<p style='margin-bottom: 0px; font-weight: lighter'>+");
                htmlField.append(criteriaDTO1.getValue1());
                htmlField.append("</p>\n");
            }
            List<MappingCriteria> criteriaListLv2 = mappingCriteriaLevel2.get(mappingCriteriaLv1.getId());
            if (criteriaListLv2 != null) {
                if (criteriaListLv2.get(0).getType() == 1) {
                    resultJoin = "<p style='margin-bottom: 0px; margin-left: 2em;'>Thỏa mãn tất cả các điều kiện sau đây</p>";
                } else if (criteriaListLv2.get(0).getType() == 2) {
                    resultJoin = "<p style='margin-bottom: 0px; margin-left: 2em;'>Thỏa mãn một trong số các điều kiện sau đây</p>";
                }
                htmlField.append(resultJoin).append("</span>\n");
                for (int j = 0; j < criteriaListLv2.size(); j++) {
                    MappingCriteria mappingCriteriaLv2 = criteriaListLv2.get(j);
                    ObjectMapper objectMapper2 = new ObjectMapper();
                    CriteriaDTO criteriaDTO2 = objectMapper2.readValue(mappingCriteriaLv2.getSelectedValue(), CriteriaDTO.class);
                    if (criteriaDTO2.getCriteriaId() == 1 || criteriaDTO2.getCriteriaId() == 2 ||
                            criteriaDTO2.getCriteriaId() == 6 || criteriaDTO2.getCriteriaId() == 7 || criteriaDTO2.getCriteriaId() == 65 || criteriaDTO2.getCriteriaId() == 66) {
                        htmlField.append("<p style='margin-bottom: 0px; margin-left: 2em; font-weight: lighter'>+");
                        htmlField.append(criteriaDTO2.getValue2());
                        htmlField.append("</p>\n");
                    } else if (criteriaDTO2.getCriteriaId() == 3) {
                        htmlField.append("<p style='margin-bottom: 0px; margin-left: 2em; font-weight: lighter'>+");
                        htmlField.append(criteriaDTO2.getValue6());
                        htmlField.append("</p>\n");
                    }else if (criteriaDTO2.getCriteriaId() == 4) {
                        htmlField.append("<p style='margin-bottom: 0px; margin-left: 2em; font-weight: lighter'>+");
                        htmlField.append(criteriaDTO2.getValue3());
                        htmlField.append("</p>\n");
                    } else if (criteriaDTO2.getCriteriaId() == 5 ) {
                        htmlField.append("<p style='margin-bottom: 0px; margin-left: 2em; font-weight: lighter'>+");
                        htmlField.append(criteriaDTO2.getValue7());
                        htmlField.append("</p>\n");
                    }else if (criteriaDTO2.getCriteriaId() == 56 ||
                            criteriaDTO2.getCriteriaId() == 60) {
                        if(criteriaDTO2.getCriteriaId() == 56 || criteriaDTO2.getCriteriaId() == 60) {
                            String packageName = null;
                            if(criteriaDTO2.getValue().equals("all")) packageName = ":Tất cả";
                            else packageName = packageDataRepository.findById(Long.parseLong(criteriaDTO2.getValue())).get().getPackageName();
                            criteriaDTO2.setValue(packageName);
                        }
                        htmlField.append("<p style='margin-bottom: 0px; margin-left: 2em; font-weight: lighter'>+");
                        htmlField.append(criteriaDTO2.getValue4());
                        htmlField.append("</p>\n");
                    } else if (criteriaDTO2.getCriteriaId() == 57 || criteriaDTO2.getCriteriaId() == 58
                            || criteriaDTO2.getCriteriaId() == 61 || criteriaDTO2.getCriteriaId() == 62) {
                        String packageName = packageDataRepository.findById(Long.parseLong(criteriaDTO2.getServiceCode())).get().getPackageName();
                        criteriaDTO2.setServiceCode(packageName);
                        htmlField.append("<p style='margin-bottom: 0px; margin-left: 2em; font-weight: lighter'>+");
                        htmlField.append(criteriaDTO2.getValue5());
                        htmlField.append("</p>\n");
                    } else {
                        htmlField.append("<p style='margin-bottom: 0px; margin-left: 2em; font-weight: lighter'>+");
                        htmlField.append(criteriaDTO2.getValue1());
                        htmlField.append("</p>\n");
                    }
                    List<MappingCriteria> criteriaListLv3 = mappingCriteriaLevel3.get(mappingCriteriaLv2.getId());
                    if (criteriaListLv3 != null) {
                        if (criteriaListLv3.get(0).getType() == 1) {
                            resultJoin = "<p style='margin-bottom: 0px; margin-left: 4em;'>Thỏa mãn tất cả các điều kiện sau đây</p>";
                        } else if (criteriaListLv3.get(0).getType() == 2) {
                            resultJoin = "<p style='margin-bottom: 0px; margin-left: 4em;'>Thỏa mãn một trong số các điều kiện sau đây</p>";
                        }
                        htmlField.append(resultJoin).append("</span>\n");
                        for (int k = 0; k < criteriaListLv3.size(); k++) {
                            MappingCriteria mappingCriteriaLv3 = criteriaListLv3.get(k);
                            ObjectMapper objectMapper3 = new ObjectMapper();
                            logger.info("mappingCriteriaLv3.getSelectedValue(): "  + mappingCriteriaLv3.getSelectedValue());
                            CriteriaDTO criteriaDTO3 = objectMapper3.readValue(mappingCriteriaLv3.getSelectedValue(), CriteriaDTO.class);
                            if (criteriaDTO3.getCriteriaId() == 1 || criteriaDTO3.getCriteriaId() == 2 ||
                                    criteriaDTO3.getCriteriaId() == 6 || criteriaDTO3.getCriteriaId() == 7 || criteriaDTO3.getCriteriaId() == 65 || criteriaDTO3.getCriteriaId() == 66) {
                                htmlField.append("<p style='margin-bottom: 0px; margin-left: 4em; font-weight: lighter'>+");
                                htmlField.append(criteriaDTO3.getValue2());
                                htmlField.append("</p>\n");
                            } else if (criteriaDTO3.getCriteriaId() == 3) {
                                htmlField.append("<p style='margin-bottom: 0px; margin-left: 4em; font-weight: lighter'>+");
                                htmlField.append(criteriaDTO3.getValue6());
                                htmlField.append("</p>\n");
                            }else if ( criteriaDTO3.getCriteriaId() == 4) {
                                htmlField.append("<p style='margin-bottom: 0px; margin-left: 4em; font-weight: lighter'>+");
                                htmlField.append(criteriaDTO3.getValue3());
                                htmlField.append("</p>\n");
                            } else if (criteriaDTO3.getCriteriaId() == 5 ) {
                                htmlField.append("<p style='margin-bottom: 0px; margin-left: 4em; font-weight: lighter'>+");
                                htmlField.append(criteriaDTO3.getValue7());
                                htmlField.append("</p>\n");
                            }else if (criteriaDTO3.getCriteriaId() == 56 ||
                                    criteriaDTO3.getCriteriaId() == 60) {
                                if(criteriaDTO3.getCriteriaId() == 56 || criteriaDTO3.getCriteriaId() == 60) {
                                    String packageName = null;
                                    if(criteriaDTO3.getValue().equals("all")) packageName = ":Tất cả";
                                    else packageName = packageDataRepository.findById(Long.parseLong(criteriaDTO2.getValue())).get().getPackageName();
                                    criteriaDTO3.setValue(packageName);
                                }
                                htmlField.append("<p style='margin-bottom: 0px; margin-left: 4em; font-weight: lighter'>+");
                                htmlField.append(criteriaDTO3.getValue4());
                                htmlField.append("</p>\n");
                            } else if (criteriaDTO3.getCriteriaId() == 57 || criteriaDTO3.getCriteriaId() == 58
                                    || criteriaDTO3.getCriteriaId() == 61 || criteriaDTO3.getCriteriaId() == 62) {
                                String packageName = packageDataRepository.findById(Long.parseLong(criteriaDTO3.getServiceCode())).get().getPackageName();
                                criteriaDTO3.setServiceCode(packageName);
                                htmlField.append("<p style='margin-bottom: 0px; margin-left: 4em; font-weight: lighter'>+");
                                htmlField.append(criteriaDTO3.getValue5());
                                htmlField.append("</p>\n");
                            } else {
                                htmlField.append("<p style='margin-bottom: 0px; margin-left: 4em; font-weight: lighter'>+");
                                htmlField.append(criteriaDTO3.getValue1());
                                htmlField.append("</p>\n");
                            }
                        }
                    }
                }
            }
        }
        return htmlField.toString();
    }

    public String queryByJSONAndTargetGroupId(String json, Long targetGroupId) {
        String jsonData = json;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("query", jsonData);
        jsonObject.put("targetGroupId", targetGroupId);
        String responseCL = httpUtil.PostWithJSON(clickHouseUrl + countMSISDNByJSONCriteriaAndSaveToDB, jsonObject.toString());
        JSONObject jsonCL = new JSONObject(responseCL);
        logger.info("jsonCL:{}", jsonCL);
        if (jsonCL.getInt("code") == 0) {
            logger.info("Lấy số thuê bao đáp ứng nhóm đối tượng thành công, kết quả sẽ được trả qua rabbit");
        } else {
            logger.info("Lỗi hệ thống. Không thể lấy số thuê bao đáp ứng của nhóm đối tượng {}");
        }
        return responseCL;
    }

    public String genQuery(String json) {
        String jsonData = json;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("jsonMainGroup", jsonData);
        String responseCL = httpUtil.genQuery(clickHouseUrl + genQuery, jsonObject.toString());
        JSONObject jsonCL = new JSONObject(responseCL);
        logger.info("jsonCL:{}", jsonCL);
        if (jsonCL.getInt("code") == 0) {
            logger.info("Lấy cau query thanh cong");
        } else {
            logger.info("Lỗi hệ thống. Không thể lấy cau query");
        }
        return jsonCL.getString("data");
    }

    public List<String> genSubQuery(String jsonTargetGroup, List<String> jsonSub) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("jsonMainGroup", jsonTargetGroup);
        jsonObject.put("jsonSubGroup", jsonSub.stream().toArray());
        String responseCL = httpUtil.genQuery(clickHouseUrl + genSubQuery, jsonObject.toString());
        JSONObject jsonCL = new JSONObject(responseCL);
        JSONArray arraySubGroup = jsonCL.getJSONArray("data");
        List<String> list = new ArrayList<>();
        for (int i = 0; i < arraySubGroup.length(); i++) {
            list.add(arraySubGroup.getString(i));
        }
        logger.info("jsonCL:{}", jsonCL);
        if (jsonCL.getInt("code") == 0) {
            logger.info("Lấy cau query thanh cong");
        } else {
            logger.info("Lỗi hệ thống. Không thể lấy cau query");
        }
        return list;
    }

    public String queryByFileAndJsonAndTargetGroupId(Long targetGroupId, String json, String pathFile) {
        String jsonData = json;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("query", jsonData);
        jsonObject.put("targetGroupId", targetGroupId);
        String responseCL = httpUtil.postWithJsonAndFile(clickHouseUrl + countMSISDNByJSONCriteriaAndFileAndSaveToDB, jsonObject.toString(), pathFile);
        JSONObject jsonCL = new JSONObject(responseCL);
        if (jsonCL.getInt("code") == 0) {
            logger.info("Lấy số thuê bao đáp ứng nhóm đối tượng thành công của tập giao {}, kết quả sẽ được trả qua rabbit", jsonCL);
        } else {
            logger.info("Lỗi hệ thống. Không thể lấy số thuê bao đáp ứng của nhóm đối tượng {}", jsonCL);
        }
        return responseCL;
    }

    public String queryByFileAndJson(String json, String pathFile) {
        String jsonData = json;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("query", jsonData);
        logger.info("=== jsonObject: "+jsonObject.toString());
        logger.info("=== pathFile: "+pathFile);
        String responseCL = httpUtil.postWithJsonAndFile(clickHouseUrl + countMSISDNByJSONCriteriaAndFile, jsonObject.toString(), pathFile);
        JSONObject jsonCL = new JSONObject(responseCL);
        logger.info("=== jsonCL: "+jsonCL);
        if (jsonCL.getInt("code") == 0) {
            logger.info("Lấy số thuê bao đáp ứng nhóm đối tượng thành công của tập giao {}", jsonCL);
        } else {
            logger.info("Lỗi hệ thống. Không thể lấy số thuê bao đáp ứng của nhóm đối tượng {}", jsonCL);
        }
        return responseCL;
    }

    public String queryCountMSISDNTotal() throws IOException {
        String responseCL = httpUtil.Get(clickHouseUrl + queryCountMSISDNTotal);
        JSONObject jsonCL = new JSONObject(responseCL);
        logger.info("jsonCL:{}", jsonCL);
        if (jsonCL.getInt("code") == 0) {
            logger.info("Lấy số tong thuê bao đáp ứng :  thành công");
        } else {
            logger.info("Lỗi hệ thống. Không thể lấy tổng số thuê bao đáp ứng");
        }
        return responseCL;
    }

    public Optional<TargetGroup> findById(Long targetGroupID) {
        return targetGroupRepository.findById(targetGroupID);
    }

    public List<TargetGroup> findAllByStatusAndType() {
        List<TargetGroup> targetGroupList = targetGroupRepository.findAllByStatusAndType();
        Collections.sort(targetGroupList, new Comparator<TargetGroup>() {
            @Override
            public int compare(TargetGroup o1, TargetGroup o2) {
                String name1 = null;
                String name2 = null;
                if(o1.getName() != null) name1 = Normalizer.normalize(o1.getName(), Normalizer.Form.NFD);
                if(o2.getName() != null) name2 = Normalizer.normalize(o2.getName(), Normalizer.Form.NFD);
                if (name1 == null) name1 = " ";
                if (name2 == null) name2 = " ";
                return name1.toLowerCase(Locale.ROOT).compareTo(name2.toLowerCase(Locale.ROOT));
            }
        });
        return targetGroupList;
    }

    public void saveTargetGroup(TargetGroup targetGroup) {
        targetGroupRepository.save(targetGroup);
    }

    public TargetGroup saveTargetGroup(String dataJson,String query) {

        logger.info("=== create new target group in frequency campaign");
        TargetGroup targetGroup = new TargetGroup();
        targetGroup.setDataJson(dataJson);
        targetGroup.setType(1);
        targetGroup.setQuery(query);
        TargetGroup targetGroup1 = targetGroupRepository.save(targetGroup);
        return targetGroup1;
    }

    public CountMSISDNDTO countMSISDNFromTargetGroupByJSONCriteria(String dataTargetGroup) {

        CountMSISDNDTO dto = new CountMSISDNDTO();

        //lấy số thuê bao thỏa mãn tập tiêu chí.
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("query", dataTargetGroup);
        String response1 = httpUtil.PostWithJSON(clickHouseUrl + countMSISDNByJSONCriteria, jsonObject.toString());
        JSONObject jsonResponse1 = new JSONObject(response1);
        logger.info("response when call API: " + countMSISDNByJSONCriteria + " = " + jsonResponse1);

        //lấy tổng số thuê bao toàn mạng
        String response2 = httpUtil.Get(clickHouseUrl + queryCountMSISDNTotal);
        JSONObject jsonResponse2 = new JSONObject(response2);
        logger.info("response when call API: " + queryCountMSISDNTotal + " = " + jsonResponse2);

        Double ratio = (double) Math.round(((double) jsonResponse1.getLong("data") / (double) jsonResponse2.getLong("data") ) *100.0 * 100.0)/100.0;

        dto.setCount(jsonResponse1.getLong("data"));
        dto.setRatio(ratio);
        logger.info("result: msisdn = {}, ratio = {}", dto.getCount(), dto.getRatio() );
        return dto;
    }

    public CountMSISDNDTO countMSISDNFromTargetGroupByFile(MultipartFile dataTargetGroup) {

        CountMSISDNDTO dto = new CountMSISDNDTO();

        String pathDataTargetGroup = null;
        if (dataTargetGroup.getSize() > 0) {
            File dir1 = new File(path_data_target_group);
            if (!dir1.exists()) {
                dir1.mkdirs();
            }
            pathDataTargetGroup = AppUtils.saveFile(dataTargetGroup, path_data_target_group);
        }

        //lấy tổng số thuê bao toàn mạng
        String response2 = httpUtil.Get(clickHouseUrl + queryCountMSISDNTotal);
        JSONObject jsonResponse2 = new JSONObject(response2);
        logger.info("response when call API: " + queryCountMSISDNTotal + " = " + jsonResponse2);

        Long msisdn = AppUtils.readFileCSV(pathDataTargetGroup);
        Double ratio = (double) Math.round(((double) msisdn / (double) jsonResponse2.getLong("data") ) *100.0 * 100.0)/100.0;
        dto.setCount(msisdn);
        dto.setRatio(ratio);
        logger.info("result: msisdn = {}, ratio = {}", dto.getCount(), dto.getRatio() );
        return dto;
    }

    public CountMSISDNDTO countMSISDNFromTargetGroupByFileJOINJSONCriteria(MultipartFile dataTargetGroup, String jsonData) {
        CountMSISDNDTO dto = new CountMSISDNDTO();
        String pathDataTargetGroup = null;
        if (dataTargetGroup.getSize() > 0) {
            File dir1 = new File(path_data_target_group);
            if (!dir1.exists()) {
                dir1.mkdirs();
            }
            pathDataTargetGroup = AppUtils.saveFile(dataTargetGroup, path_data_target_group);
        }
        String response = queryByFileAndJson(jsonData, pathDataTargetGroup);
        JSONObject jsonObject = new JSONObject(response);
        Long msisdn = jsonObject.getLong("data");

        //lấy tổng số thuê bao toàn mạng
        String response2 = httpUtil.Get(clickHouseUrl + queryCountMSISDNTotal);
        JSONObject jsonResponse2 = new JSONObject(response2);
        logger.info("response when call API: " + queryCountMSISDNTotal + " = " + jsonResponse2);

        Double ratio = (double) Math.round(((double) msisdn / (double) jsonResponse2.getLong("data") ) *100.0 * 100.0)/100.0;

        dto.setCount(msisdn);
        dto.setRatio(ratio);
        logger.info("result: msisdn = {}, ratio = {}", dto.getCount(), dto.getRatio() );
        return dto;
    }

    public CountMSISDNDTO countMSISDNFromEditTargetGroupByFile(MultipartFile dataTargetGroup, Long targetGroupId) {

        CountMSISDNDTO dto = new CountMSISDNDTO();
        Long msisdn = 0l;
        if(dataTargetGroup != null && dataTargetGroup.getSize() > 0){
            String pathDataTargetGroup = null;
            if (dataTargetGroup.getSize() > 0) {
                File dir1 = new File(path_data_target_group);
                if (!dir1.exists()) {
                    dir1.mkdirs();
                }
                pathDataTargetGroup = AppUtils.saveFile(dataTargetGroup, path_data_target_group);
            }

            msisdn = AppUtils.readFileCSV(pathDataTargetGroup);
        }else {
            try{
                msisdn = AppUtils.readFileCSV(targetGroupRepository.findById(targetGroupId).get().getPathFileMsisdn());
            }catch (Exception e){
                logger.error("error: ", e);
            }

        }

        //lấy tổng số thuê bao toàn mạng
        String response2 = httpUtil.Get(clickHouseUrl + queryCountMSISDNTotal);
        JSONObject jsonResponse2 = new JSONObject(response2);
        logger.info("response when call API: " + queryCountMSISDNTotal + " = " + jsonResponse2);

        Double ratio = (double) Math.round(((double) msisdn / (double) jsonResponse2.getLong("data") ) *100.0 * 100.0)/100.0;
        dto.setCount(msisdn);
        dto.setRatio(ratio);
        logger.info("result: msisdn = {}, ratio = {}", dto.getCount(), dto.getRatio() );
        return dto;

    }

    public CountMSISDNDTO countMSISDNFromEditTargetGroupByFileJOINJSONCriteria(MultipartFile dataTargetGroup, String jsonData, Long targetGroupId) {

        CountMSISDNDTO dto = new CountMSISDNDTO();
        String pathDataTargetGroup = null;
        if(dataTargetGroup != null && dataTargetGroup.getSize() > 0){
            if (dataTargetGroup.getSize() > 0) {
                File dir1 = new File(path_data_target_group);
                if (!dir1.exists()) {
                    dir1.mkdirs();
                }
                pathDataTargetGroup = AppUtils.saveFile(dataTargetGroup, path_data_target_group);
            }

        }else {
            pathDataTargetGroup = targetGroupRepository.findById(targetGroupId).get().getPathFileMsisdn();
        }

        String response = queryByFileAndJson(jsonData, pathDataTargetGroup);
        JSONObject jsonObject = new JSONObject(response);
        Long msisdn = jsonObject.getLong("data");

        //lấy tổng số thuê bao toàn mạng
        String response2 = httpUtil.Get(clickHouseUrl + queryCountMSISDNTotal);
        JSONObject jsonResponse2 = new JSONObject(response2);
        logger.info("response when call API: " + queryCountMSISDNTotal + " = " + jsonResponse2);

        Double ratio = (double) Math.round(((double) msisdn / (double) jsonResponse2.getLong("data") ) *100.0 * 100.0)/100.0;
        dto.setCount(msisdn);
        dto.setRatio(ratio);
        logger.info("result: msisdn = {}, ratio = {}", dto.getCount(), dto.getRatio() );
        return dto;

    }

    public Boolean countCampaign(Long targetGroupId) {
        boolean check;
        List<Integer> statusList = Arrays.asList(0,1,2,6);
        List<EventCampaign> eventCampaignList = eventCampaignRepository.findAllByIdTargetGroupAndStatusIn(targetGroupId,statusList);
        List<FrequencyCampaign> frequencyCampaignList = frequencyCampaignRepository.findAllByIdTargetGroupAndStatusIn(targetGroupId,statusList);
        if(eventCampaignList.size() > 0 || frequencyCampaignList.size() > 0) {
            check = true;
        } else check = false;
        return check;
    }
}
