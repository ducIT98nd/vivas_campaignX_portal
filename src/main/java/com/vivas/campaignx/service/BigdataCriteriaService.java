package com.vivas.campaignx.service;

import com.vivas.campaignx.repository.BigdataCriteriaRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BigdataCriteriaService {
    protected final Logger logger = LogManager.getLogger(this.getClass().getName());
    @Autowired
    private BigdataCriteriaRepository repository;


}
