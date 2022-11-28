package com.vivas.campaignx.service;


import com.vivas.campaignx.entity.ConfigMtContent;
import com.vivas.campaignx.repository.ConfigMtContentRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConfigMtContentService {

    protected final Logger logger = LogManager.getLogger(this.getClass().getName());

    private ConfigMtContentRepository configMtContentRepository;

    public ConfigMtContentService (ConfigMtContentRepository configMtContentRepository) {
        this.configMtContentRepository = configMtContentRepository;
    }

    public List<ConfigMtContent> getListMT (){
        return configMtContentRepository.findAll();
    }

    public Optional<ConfigMtContent> getMtContentByAction (Integer action){
        return configMtContentRepository.findByAction(action);
    }

    public void updateMtContent (ConfigMtContent configMtContent){
        configMtContentRepository.save(configMtContent);
    }
}
