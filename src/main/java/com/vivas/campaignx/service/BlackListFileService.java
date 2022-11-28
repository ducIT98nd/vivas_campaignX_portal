package com.vivas.campaignx.service;

import com.vivas.campaignx.common.AppUtils;
import com.vivas.campaignx.common.HttpUtil;
import com.vivas.campaignx.entity.BlackList;
import com.vivas.campaignx.entity.BlackListFile;
import com.vivas.campaignx.repository.BlackListFileRepository;
import com.vivas.campaignx.repository.BlackListRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;

@Service
public class BlackListFileService {
    protected final Logger logger = LogManager.getLogger(this.getClass().getName());
    @Autowired
    private BlackListFileRepository blackListFileRepository;
    @Value("${path_data_blacklist}")
    private String path_data_blacklist;

    @Value("${sysCampaignXReloadBlackList}")
    private String reloadBlackList;

    @Autowired
    private BlackListRepository blackListRepository;

    @Autowired
    private HttpUtil httpUtil;

    public Page<BlackListFile> getAll(Integer pageSize, Integer currentPage) {
        Pageable paging = PageRequest.of(currentPage - 1, pageSize, Sort.by("updatedDate").descending());
        return blackListFileRepository.findAll(paging);
    }

    public Page<BlackListFile> getBlackList(Integer pageSize, Integer currentPage, String name, Integer status) {
        Pageable paging = PageRequest.of(currentPage - 1, pageSize);
        List<Integer> list = new ArrayList<>();
        if (status == 0) {
            list.add(0);
        } else if(status == 1) {
            list.add(1);
        } else if(status == 2) {
            list = Arrays.asList(0,1);
        }
        return blackListFileRepository.search(name,list,paging);
    }
    public void saveBlackList(String name, MultipartFile dataBlackList, String status, String userName ) {
        BlackListFile blackListFile = new BlackListFile();
        blackListFile.setName(name);
        if(status == null) {
            blackListFile.setStatus(1);
        } else blackListFile.setStatus(0);
        String pathDataBlackList = null;
        if (dataBlackList.getSize() > 0) {
            File dir1 = new File(path_data_blacklist);
            if (!dir1.exists()) {
                dir1.mkdirs();
            }
            pathDataBlackList = AppUtils.saveFile(dataBlackList, path_data_blacklist);
        }
        blackListFile.setBlackListPathFile(pathDataBlackList);
        blackListFile.setCreatedUser(userName);
        blackListFile.setUpdatedUser(userName);
        blackListFile.setCreatedDate(new Date());
        blackListFile.setUpdatedDate(new Date());
        BlackListFile blackListSave = blackListFileRepository.save(blackListFile);
        Set<String> msisdnList = AppUtils.readFileCSVToSet(blackListSave.getBlackListPathFile());
        for (String msisdn: msisdnList) {
            BlackList blackList = new BlackList();
            blackList.setMsisdn(msisdn);
            blackList.setCreatedDate(new Date());
            blackList.setFileId(blackListSave.getId());
            blackListRepository.save(blackList);
        }
        httpUtil.Get(reloadBlackList);
        logger.info("save success" );
    }

    public void editBlackList(BlackListFile blackListFile, HashMap<String, String> reqParams, MultipartFile dataBlackList, String currentUser ) {
        String status = reqParams.get("status");
        String name = reqParams.get("name");
        Long id = Long.parseLong(reqParams.get("id"));
        blackListFile.setName(name);
        if (status == null || status.equals("off")) {
            blackListFile.setStatus(1);
        } else if(status.equals("on")) {
            blackListFile.setStatus(0);
        }
        blackListFile.setUpdatedUser(currentUser);
        blackListFile.setUpdatedDate(new Date());
        String pathDataBlackList = null;
        if (dataBlackList.getSize() > 0) {
            File dir1 = new File(path_data_blacklist);
            if (!dir1.exists()) {
                dir1.mkdirs();
            }
            pathDataBlackList = AppUtils.saveFile(dataBlackList, path_data_blacklist);
            blackListRepository.deleteAllByFileId(id);
            blackListFile.setBlackListPathFile(pathDataBlackList);
            Set<String> msisdnList = AppUtils.readFileExcel1(pathDataBlackList);
            for (String msisdn: msisdnList) {
                BlackList blackList = new BlackList();
                blackList.setMsisdn(msisdn);
                blackList.setCreatedDate(new Date());
                blackList.setFileId(id);
                blackListRepository.save(blackList);
            }
        }
        blackListFileRepository.save(blackListFile);
        httpUtil.Get(reloadBlackList);
        logger.info("save success" );
    }

    public Optional<BlackListFile> findByNameAndStatus(String name) {
        return blackListFileRepository.findByNameAndStatus(name, 0);
    }

    public Long countByNameAndStatus(String name) {
        return blackListFileRepository.countByNameAndStatus(name, 0);
    }

    public Long countByNameAndStatusAndIdIsNot(String name, Long id) {
        return blackListFileRepository.countByNameAndStatusAndIdIsNot(name, 0, id);
    }

    public Optional<BlackListFile> findById(Long id) {
        return blackListFileRepository.findById(id);
    }
}
