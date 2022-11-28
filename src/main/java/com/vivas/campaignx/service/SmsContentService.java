package com.vivas.campaignx.service;


import com.vivas.campaignx.dto.ViewSmsContentDTO;
import com.vivas.campaignx.entity.ConfigMtContent;
import com.vivas.campaignx.entity.SmsContent;
import com.vivas.campaignx.repository.ConfigMtContentRepository;
import com.vivas.campaignx.repository.SmsContentRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SmsContentService {

    protected final Logger logger = LogManager.getLogger(this.getClass().getName());

    private SmsContentRepository smsContentRepository;

    public SmsContentService(SmsContentRepository smsContentRepository) {
        this.smsContentRepository = smsContentRepository;
    }

    public Optional<SmsContent> findById (Long contentId){
        return smsContentRepository.findById(contentId);
    }

    public ViewSmsContentDTO findByIdToView(Long contentId){
        return smsContentRepository.findByIdToView(contentId);
    }

    public SmsContent saveSmsContent(String message, Integer mtCount, Integer unicode) {
        logger.info("=== save new sms content ===");
        SmsContent smsContent = new SmsContent();
        smsContent.setMessage(message);
        smsContent.setMtCount(mtCount);
        smsContent.setUnicode(unicode);
        SmsContent content = smsContentRepository.saveAndFlush(smsContent);
        return smsContent;
    }

    public void deleteById(Long contentId) {
        smsContentRepository.deleteById(contentId);
    }
}
