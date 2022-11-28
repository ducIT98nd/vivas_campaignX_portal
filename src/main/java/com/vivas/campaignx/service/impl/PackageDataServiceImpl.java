package com.vivas.campaignx.service.impl;

import com.vivas.campaignx.common.*;
import com.vivas.campaignx.dto.PackageNameDTO;
import com.vivas.campaignx.entity.*;
import com.vivas.campaignx.repository.*;
import com.vivas.campaignx.request.PackageDataRequest;
import com.vivas.campaignx.response.PackageDataResponse;
import com.vivas.campaignx.service.PackageDataService;
import com.vivas.campaignx.service.mapper.PackageDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PackageDataServiceImpl implements PackageDataService {

    @Autowired
    private PackageDataRepository packageDataRepository;

    @Autowired
    private PackageCodeRepository packageCodeRepository;

    @Autowired
    private PackageDataMapper packageDataMapper;

    @Autowired
    private FrequencyCampaignRepository frequencyCampaignRepository;

    @Autowired
    private EventCampaignRepository eventCampaignRepository;

    @Autowired
    private SubTargetGroupRepository subTargetGroupRepository;

    @Transactional(readOnly = true)
    @Override
    public Page<PackageDataResponse> findAll(Integer pageSize, Integer currentPage, String packageDataName, Integer packageGroup, String packageCodeName, Integer status) {
        Pageable paging = PageRequest.of(currentPage - 1, pageSize);
        String packageDataNameFormat = CampaignXUtils.getInstance().formatStringForQuery(packageDataName);
        String packageCodeNameFormat = CampaignXUtils.getInstance().formatStringForQuery(packageCodeName);
        return packageDataRepository.findAllPackageData(packageDataNameFormat, packageGroup, packageCodeNameFormat, status, paging).map(packageDataMapper::toDto);
    }

    @Override
    public ResponseEntity createPackageData(PackageDataRequest packageDataRequest) {
        ResponseEntity responseEntity = new ResponseEntity();

        Optional<PackageData> packageDataOptional = packageDataRepository.findByPackageNameIgnoreCaseAndStatusNot(packageDataRequest.getPackageName(), CommonConstants.STATUS_DELETED);
        if (packageDataOptional.isPresent()) {
            responseEntity.setCode(CodeConstants.BAD_REQUEST);
            responseEntity.setMessage(MessageConstants.PACKAGE_NAME_ALREADY_EXISTS);
            return responseEntity;
        }
        PackageData packageData = new PackageData();
        savePackageData(packageDataRequest, packageData);
        savePackageCode(packageDataRequest, packageData);
        responseEntity.setCode(CodeConstants.OK);
        responseEntity.setMessage(MessageConstants.ADD_PACKAGE_DATA_SUCCESS);
        return responseEntity;
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity getPackageDataDetail(Long id) {
        ResponseEntity responseEntity = new ResponseEntity();
        Optional<PackageData> packageDataOptional = packageDataRepository.findById(id);
        if (packageDataOptional.isPresent()) {
            PackageDataResponse packageDataResponse = packageDataMapper.toDto(packageDataOptional.get());
            responseEntity.setCode(CodeConstants.OK);
            responseEntity.setData(packageDataResponse);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity updatePackageData(PackageDataRequest packageDataRequest) {
        ResponseEntity responseEntity = new ResponseEntity();

        Optional<PackageData> packageDataOptional = packageDataRepository.findById(packageDataRequest.getId());
        if (!packageDataOptional.isPresent()) {
            responseEntity.setCode(CodeConstants.INTERNAL_SERVER_ERROR);
            responseEntity.setMessage(MessageConstants.SYSTEM_ERROR);
            return responseEntity;
        }

        Optional<PackageData> packageDataExist = packageDataRepository
                .findByPackageNameIgnoreCaseAndIdNotAndStatusNot(packageDataRequest.getPackageName(), packageDataRequest.getId(), CommonConstants.STATUS_DELETED);
        if (packageDataExist.isPresent()) {
            responseEntity.setCode(CodeConstants.BAD_REQUEST);
            responseEntity.setMessage(MessageConstants.PACKAGE_NAME_ALREADY_EXISTS);
            return responseEntity;
        }

        PackageData packageData = packageDataOptional.get();
        packageCodeRepository.deleteByPackageDataId(packageData.getId());
        savePackageData(packageDataRequest, packageData);
        savePackageCode(packageDataRequest, packageData);

        responseEntity.setCode(CodeConstants.OK);
        responseEntity.setMessage(MessageConstants.UPDATE_PACKAGE_DATA_SUCCESS);
        return responseEntity;
    }

    private void savePackageCode(PackageDataRequest packageDataRequest, PackageData packageData) {
        List<PackageCodeCDR> packageCodeCDRS = new ArrayList<>();
        for (String packageCodeName : packageDataRequest.getPackageCodeName()) {
            PackageCodeCDR packageCodeCDR = new PackageCodeCDR();
            packageCodeCDR.setPackageCodeName(packageCodeName);
            packageCodeCDR.setPackageDataId(packageData.getId());
            packageCodeCDRS.add(packageCodeCDR);
        }
        packageCodeRepository.saveAll(packageCodeCDRS);
    }

    private void savePackageData(PackageDataRequest packageDataRequest, PackageData packageData) {
        packageData.setPackageName(packageDataRequest.getPackageName());
        packageData.setPackageGroup(packageDataRequest.getPackageGroup());
        packageData.setPackageContent(packageDataRequest.getPackageContent());
        packageData.setPackagePrice(packageDataRequest.getPackagePrice());
        packageData.setCycle(packageDataRequest.getCycle());
        packageData.setRegistrationSyntax(packageDataRequest.getRegistrationSyntax());
        packageData.setCancelSyntax(packageDataRequest.getCancelSyntax());
        packageData.setCycle(packageDataRequest.getCycle());
        packageData.setStatus(packageDataRequest.getStatus());
        packageData.setPackageCode(packageDataRequest.getPackageCode());
        packageDataRepository.save(packageData);
    }

    @Override
    public ResponseEntity deletePackageData(Long id) {
        ResponseEntity responseEntity = new ResponseEntity();

        Optional<PackageData> packageDataOptional = packageDataRepository.findById(id);
        if (!packageDataOptional.isPresent()) {
            responseEntity.setCode(CodeConstants.INTERNAL_SERVER_ERROR);
            responseEntity.setMessage(MessageConstants.SYSTEM_ERROR);
            return responseEntity;
        }
        List<Integer> listStatus = Arrays.asList(4,5);
        List<FrequencyCampaign> frequencyCampaigns = frequencyCampaignRepository.findAllByPackageDataIdAndStatusNotIn(id,listStatus);
        List<EventCampaign> eventCampaigns = eventCampaignRepository.findAllByPackageDataIdAndStatusNotIn(id,listStatus);
        List<SubTargetGroup> subTargetGroups = subTargetGroupRepository.findAllByPackageDataId(id);
        if(frequencyCampaigns.size() > 0 || eventCampaigns.size() > 0 || subTargetGroups.size() > 0) {
            responseEntity.setCode(CodeConstants.INTERNAL_SERVER_ERROR);
            responseEntity.setMessage(MessageConstants.DELETE_PACKAGE_DATA_SUCCESS_IN_CAMPAIGN);
        } else {
            PackageData packageData = packageDataOptional.get();
            packageData.setStatus(CommonConstants.STATUS_DELETED);
            packageDataRepository.save(packageData);
            responseEntity.setCode(CodeConstants.OK);
            responseEntity.setMessage(MessageConstants.DELETE_PACKAGE_DATA_SUCCESS);
        }
        return responseEntity;
    }

    public List<PackageNameDTO> findAllByStatusAndPackageGroupOrderByPackageName(Integer status, Integer packageGroup) {
        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "packageName").ignoreCase();
        return packageDataRepository.findAllByStatusAndPackageGroup(status, packageGroup, Sort.by(order));
    }

    @Override
    public Optional<PackageData> findById(Long id) {
        return packageDataRepository.findById(id);
    }
}
