package com.vivas.campaignx.controllers;

import com.vivas.campaignx.common.AttributeConstants;
import com.vivas.campaignx.common.CommonConstants;
import com.vivas.campaignx.common.CampaignXUtils;
import com.vivas.campaignx.common.ResponseEntity;
import com.vivas.campaignx.dto.PackageNameDTO;
import com.vivas.campaignx.entity.SendingAccount;
import com.vivas.campaignx.request.PackageDataRequest;
import com.vivas.campaignx.response.PackageDataResponse;
import com.vivas.campaignx.service.PackageCodeService;
import com.vivas.campaignx.service.PackageDataService;
import com.vivas.campaignx.utils.IgnoreWildCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@IgnoreWildCard
@RequestMapping("/package-datas")
@Controller
public class PackageDataController {

    @Autowired
    private PackageDataService packageDataService;

    @Autowired
    private PackageCodeService packageCodeService;

    @PreAuthorize("hasAuthority('view:search:packdata')")
    @RequestMapping("/")
    public String packageDataManager(
            Model model,
            @RequestParam(required = false, defaultValue = CommonConstants.DEFAULT_PAGE_SIZE) Integer pageSize,
            @RequestParam(required = false, defaultValue = CommonConstants.DEFAULT_CURRENT_PAGE) Integer currentPage,
            @RequestParam(required = false) String packageDataName,
            @RequestParam(required = false) Integer packageGroup,
            @RequestParam(required = false) String packageCodeName,
            @RequestParam(required = false) Integer status
    ) {
        Page<PackageDataResponse> packageDatas = packageDataService.findAll(pageSize, currentPage, packageDataName, packageGroup, packageCodeName, status);
        model.addAttribute(AttributeConstants.PACKAGE_DATA_NAME, packageDataName);
        model.addAttribute(AttributeConstants.PACKAGE_GROUP, packageGroup);
        model.addAttribute(AttributeConstants.PACKAGE_CODE_NAME, packageCodeName);
        model.addAttribute(AttributeConstants.STATUS, status);
        model.addAttribute(AttributeConstants.PACKAGE_DATAS, packageDatas);
        model.addAttribute(AttributeConstants.PAGE_SIZE, pageSize);
        model.addAttribute(AttributeConstants.CURRENT_PAGE, currentPage);
        model.addAttribute(AttributeConstants.TOTAL_PAGES, packageDatas.getTotalPages());
        model.addAttribute(AttributeConstants.MMP_UTILS, CampaignXUtils.getInstance());
        model.addAttribute(AttributeConstants.PACKAGE_CODE_NAMES, packageCodeService.findAllPackageCodeName());
        return "packageData/PackageData";
    }

    @PreAuthorize("hasAuthority('create:packdata')")
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity createPackageData(@RequestBody PackageDataRequest packageDataRequest) {
        return packageDataService.createPackageData(packageDataRequest);
    }

    @PreAuthorize("hasAuthority('detail:packdata')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity getPackageDataDetail(@PathVariable Long id) {
        return packageDataService.getPackageDataDetail(id);
    }

    @PreAuthorize("hasAuthority('update:packdata')")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity updatePackageData(@PathVariable Long id, @RequestBody PackageDataRequest PackageDataRequest) {
        PackageDataRequest.setId(id);
        return packageDataService.updatePackageData(PackageDataRequest);
    }

    @PreAuthorize("hasAuthority('delete:packdata')")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity deletePackageData(@PathVariable Long id) {
        return packageDataService.deletePackageData(id);
    }

    @RequestMapping(value = "/find-active", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public org.springframework.http.ResponseEntity<?> getListPackage(@RequestParam Integer packageGroup) {
        List<PackageNameDTO> packageNameDTOList = packageDataService.findAllByStatusAndPackageGroupOrderByPackageName(1, packageGroup);
        org.springframework.http.ResponseEntity responseEntity = new org.springframework.http.ResponseEntity<>(packageNameDTOList, HttpStatus.OK);
        return responseEntity;
    }
}
