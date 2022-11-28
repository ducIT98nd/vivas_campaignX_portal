package com.vivas.campaignx.controllers;

import com.vivas.campaignx.common.AppUtils;
import com.vivas.campaignx.dto.SimpleResponseDTO;
import com.vivas.campaignx.entity.MappingCriteria;
import com.vivas.campaignx.entity.SubTargetGroup;
import com.vivas.campaignx.repository.MappingCriteriaRepository;
import com.vivas.campaignx.service.SubTargetGroupService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/SubTargetGroupController")
public class SubTargetGroupController {

    private final Logger logger = LogManager.getLogger(this.getClass().getName());

    @Autowired
    private SubTargetGroupService subTargetGroupService;

    @Autowired
    private MappingCriteriaRepository mappingCriteriaRepository;

    @RequestMapping("/viewSubTargetGroup")
    public String viewSubTargetGroup(Model model, @Param("id") Integer id) {
        model.addAttribute("subSetCriteria","subSetCriteria_"+id);
        model.addAttribute("subConditionLevel1","subConditionLevel1_"+id);
        return "TargetGroup-sub/CriteriaSetup";
    }

    @RequestMapping("/GetRowLevel1Criteria")
    public String GetRowLevel1Criteria(Model model, @Param("currentId") Integer currentId, @Param("parentId") Integer parentId, @Param("subTreeId") Integer subTreeId) {
        model.addAttribute("buttonIdLevel1", "buttonSubTargetId_" + currentId);
        model.addAttribute("endRowIdLevel1", "endRowSubTargetId_" + currentId);
        model.addAttribute("rowIdLevel1", "rowSubTargetId_" + currentId);
        model.addAttribute("selectCriteriaID", "selectSubTargetCriteriaID_" + currentId);
        model.addAttribute("level1", "levelSubTarget1_" + currentId);
        model.addAttribute("currentNodeID", currentId);
        model.addAttribute("parentNodeId", parentId);
        model.addAttribute("subTreeId", subTreeId);
        return "TargetGroup-sub/rowLevel1Criteria";
    }

    @RequestMapping(value = "/getSubTargetGroupByCampaignId", method = RequestMethod.GET)
    @ResponseBody
    public String getSubTargetGroupByCampaignId(@RequestParam(required = true) Long frequencyCampaignId){
        SimpleResponseDTO res = new SimpleResponseDTO();
        List<SubTargetGroup> subTargetGroups = new ArrayList<>();
        try {
            subTargetGroups = subTargetGroupService.findByCampaignId(frequencyCampaignId);
            logger.info("list sub target group size: "+subTargetGroups.size());
            res.setCode(AppUtils.successCode);
            res.setData(subTargetGroups);
        }catch (Exception e){
            logger.info("Error while get list sub target group: " +e);
            res.setCode(AppUtils.errorCode);
        }
        return AppUtils.ObjectToJsonResponse(res);
    }

    @RequestMapping(value = "/getCriteriaMappingBySubGroupId", method = RequestMethod.GET)
    @ResponseBody
    public String getCriteriaMappingBySubGroupId(@RequestParam(required = true) Long subGroupId){
        SimpleResponseDTO res = new SimpleResponseDTO();
        List<MappingCriteria> mappingCriteriaList = new ArrayList<>();
        try {
            mappingCriteriaList = mappingCriteriaRepository.findAllByIdSubTargetGroupOrderByLevelCriteriaAscPositionAsc(subGroupId);
            logger.info("list mapping criteria by sub target group size: "+mappingCriteriaList.size());
            res.setCode(AppUtils.successCode);
            res.setData(mappingCriteriaList);
        }catch (Exception e){
            logger.info("Error while get list mapping criteria by sub target group: " +e);
            res.setCode(AppUtils.errorCode);
        }
        return AppUtils.ObjectToJsonResponse(res);
    }

    @RequestMapping(value = "/parseStringJSONCriteriaToArray", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String parseStringJSONCriteriaToArray(@RequestParam(required = false, name = "jsonSubGroup") String jsonSubGroup) {
        logger.info("json sub group: " + jsonSubGroup);
        SimpleResponseDTO res = new SimpleResponseDTO();
        JSONObject jsonObject = new JSONObject(jsonSubGroup);
        JSONArray jsonArrayData = jsonObject.getJSONArray("childs");
        List<MappingCriteria> listCriteria = subTargetGroupService.getListMappingFromJsonArray(jsonArrayData);
        res.setData(listCriteria);
        res.setCode(AppUtils.successCode);
        return AppUtils.ObjectToJsonResponse(res);
    }
}
