package com.vivas.campaignx.service;

import com.vivas.campaignx.entity.MappingCriteria;
import com.vivas.campaignx.entity.SubTargetGroup;
import com.vivas.campaignx.repository.SubTargetGroupRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SubTargetGroupService {
    protected final Logger logger = LogManager.getLogger(this.getClass().getName());
    @Autowired
    private SubTargetGroupRepository subTargetGroupRepository;

    public Optional<SubTargetGroup> findById(Long id) {
        return subTargetGroupRepository.findById(id);
    }

    public SubTargetGroup saveJsonData(String jsonData) {
        SubTargetGroup subTargetGroup = new SubTargetGroup();
        subTargetGroup.setDataJson(jsonData);
        return subTargetGroup;
    }

    public List<SubTargetGroup> findByCampaignId(Long campaignId) {
        return subTargetGroupRepository.findAllByCampaignId(campaignId);
    }

    public SubTargetGroup saveSubTargetGroup(Long campaignId, String name, Integer channel, String dataJson, Integer priority,
                                             Long contentId,String sql,Long accountSendingId, Long packageDataId) {

        logger.info("=== save new sub target group ===");
        SubTargetGroup subTargetGroup = new SubTargetGroup();
        subTargetGroup.setCampaignId(campaignId);
        subTargetGroup.setName(name);
        subTargetGroup.setChannel(channel);
        subTargetGroup.setDataJson(dataJson);
        subTargetGroup.setPriority(priority);
        subTargetGroup.setContentId(contentId);
        subTargetGroup.setQuery(sql);
        subTargetGroup.setPackageDataId(packageDataId);
        subTargetGroup.setAccountSendingId(accountSendingId);
        SubTargetGroup subTargetGroup1 = subTargetGroupRepository.saveAndFlush(subTargetGroup);
        return subTargetGroup1;
    }
    void deleteAllByCampaignId(Long campaignId){
        subTargetGroupRepository.deleteAllByCampaignId(campaignId);
    }

    public List<MappingCriteria> getListMappingFromJsonArray(JSONArray jsonArrayData ) {
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
            mappingLV1.setLevelCriteria(1);
            listMapping.add(mappingLV1);
        }
        return listMapping;
    }

}
