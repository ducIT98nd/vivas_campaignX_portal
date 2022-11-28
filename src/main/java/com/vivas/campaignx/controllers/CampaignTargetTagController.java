package com.vivas.campaignx.controllers;

import com.vivas.campaignx.common.AppUtils;
import com.vivas.campaignx.common.enums.ErrorCodeEnum;
import com.vivas.campaignx.dto.SimpleResponseDTO;
import com.vivas.campaignx.entity.CampaignTargetTag;
import com.vivas.campaignx.service.CampaignTargetTagService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Controller
public class CampaignTargetTagController {

	protected final Logger logger = LogManager.getLogger(this.getClass().getName());

	private CampaignTargetTagService campaignTargetTagService;

	public CampaignTargetTagController(CampaignTargetTagService campaignTargetTagService){
        this.campaignTargetTagService = campaignTargetTagService;
	}

	@GetMapping(value = "get-campaign-target-tag", produces = "application/json; charset=utf-8")
	@ResponseBody
	private String getListTargetTag(){

        SimpleResponseDTO res = new SimpleResponseDTO();
        try{
            String[] listTargetTag = campaignTargetTagService.getListCampaignTargetTag();
            if (listTargetTag.length != 0){
                res.setCode(ErrorCodeEnum.SUCCESS.getCode());
                res.setData(listTargetTag);
                return AppUtils.ObjectToJsonResponse(res);
            }
            logger.info("list target tag size = {}", listTargetTag.length);
        } catch (Exception ex){
            logger.error("Error: ", ex);
        }
        res.setCode(ErrorCodeEnum.SUCCESS.getCode());
        res.setMessage("Có lỗi xảy ra khi lấy danh sách mục tiêu chiến dịch");
        return AppUtils.ObjectToJsonResponse(res);
	}
}
