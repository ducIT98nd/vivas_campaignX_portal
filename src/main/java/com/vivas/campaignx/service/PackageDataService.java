package com.vivas.campaignx.service;

import com.vivas.campaignx.common.ResponseEntity;
import com.vivas.campaignx.dto.PackageNameDTO;
import com.vivas.campaignx.entity.PackageData;
import com.vivas.campaignx.request.PackageDataRequest;
import com.vivas.campaignx.response.PackageDataResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface PackageDataService {

    Page<PackageDataResponse> findAll(Integer pageSize, Integer currentPage, String packageDataName, Integer packageGroup, String packageCodeName, Integer status);

    ResponseEntity createPackageData(PackageDataRequest packageDataRequest);

    ResponseEntity getPackageDataDetail(Long id);

    ResponseEntity updatePackageData(PackageDataRequest packageDataRequest);

    ResponseEntity deletePackageData(Long id);

    List<PackageNameDTO> findAllByStatusAndPackageGroupOrderByPackageName(Integer status, Integer packageGroup);

    Optional<PackageData> findById(Long id);
}
