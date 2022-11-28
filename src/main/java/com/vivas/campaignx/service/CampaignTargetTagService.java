package com.vivas.campaignx.service;

import com.vivas.campaignx.entity.CampaignTargetTag;
import com.vivas.campaignx.repository.CampaignTargetTagRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CampaignTargetTagService {

	protected final Logger logger = LogManager.getLogger(this.getClass().getName());

	private CampaignTargetTagRepository campaignTargetTagRepository;

	@Autowired
	public CampaignTargetTagService(CampaignTargetTagRepository campaignTargetTagRepository){
		this.campaignTargetTagRepository = campaignTargetTagRepository;
	}

	public String[] getListCampaignTargetTag(){

		List<String> list = campaignTargetTagRepository.findAll().stream()
                                .map(CampaignTargetTag::getTargetText)
                                .collect(Collectors.toList());
		return list.toArray(new String[0]);
	}

    public void saveCampaignTargetTag(String campaignTargetTag, String type) {
        logger.info("--- save campaign target tag ---");
        List<String> listTargetTag = new ArrayList<String>(Arrays.asList(campaignTargetTag.split(",")));
        List<String> listTargetTextTag = campaignTargetTagRepository.findAllTargetText();
        for (int i = 0; i < listTargetTag.size(); i++) {
            if (type.equals("insert")) {
                if (!listTargetTextTag.contains(listTargetTag.get(i))) {
                    CampaignTargetTag campaignTargetTag1 = new CampaignTargetTag();
                    campaignTargetTag1.setTargetText(listTargetTag.get(i));
                    campaignTargetTag1.setCount(1);
                    campaignTargetTagRepository.saveAndFlush(campaignTargetTag1);
                } else {
                    CampaignTargetTag campaignTargetTag1 = campaignTargetTagRepository.findByTargetText(listTargetTag.get(i));
                    campaignTargetTag1.setCount(campaignTargetTag1.getCount() + 1);
                    campaignTargetTagRepository.saveAndFlush(campaignTargetTag1);
                }
            } else if (type.equals("update")) {
                if (!listTargetTextTag.contains(listTargetTag.get(i))) {
                    CampaignTargetTag campaignTargetTag1 = new CampaignTargetTag();
                    campaignTargetTag1.setTargetText(listTargetTag.get(i));
                    campaignTargetTag1.setCount(1);
                    campaignTargetTagRepository.saveAndFlush(campaignTargetTag1);
                }
            }
        }
    }
}
