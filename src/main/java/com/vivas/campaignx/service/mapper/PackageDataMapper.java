package com.vivas.campaignx.service.mapper;

import com.vivas.campaignx.dto.PackageDataDTO;
import com.vivas.campaignx.entity.PackageCodeCDR;
import com.vivas.campaignx.entity.PackageData;
import com.vivas.campaignx.repository.PackageCodeRepository;
import com.vivas.campaignx.response.PackageDataResponse;
import com.vivas.campaignx.utils.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public final class PackageDataMapper {

    @Autowired
    private PackageCodeRepository packageCodeRepository;

    public PackageDataResponse toDto(PackageDataDTO packageDataDTO) {
        if (Objects.isNull(packageDataDTO)) {
            return null;
        }

        PackageDataResponse packageData = new PackageDataResponse();
        packageData.setId(packageDataDTO.getId());
        packageData.setPackageName(packageDataDTO.getPackageName());
        packageData.setPackageGroup(this.getPackageGroup(packageDataDTO.getPackageGroup()));
        packageData.setPackageCodeName(packageDataDTO.getPackageCodeNames());
        packageData.setPackagePrice(packageDataDTO.getPackagePrice());
        packageData.setCycle(packageDataDTO.getCycle());
        packageData.setRegistrationSyntax(packageDataDTO.getRegistrationSyntax());
        packageData.setCancelSyntax(packageDataDTO.getCancelSyntax());
        packageData.setStatus(this.getStatus(packageDataDTO.getStatus()));
        return packageData;
    }

    public PackageDataResponse toDto(PackageData packageData) {
        if (Objects.isNull(packageData)) {
            return null;
        }
        PackageDataResponse packageDataResponse = new PackageDataResponse();
        packageDataResponse.setId(packageData.getId());
        packageDataResponse.setPackageName(packageData.getPackageName());
        packageDataResponse.setPackageGroupNumber(packageData.getPackageGroup());
        packageDataResponse.setPackageGroup(this.getPackageGroup(packageData.getPackageGroup()));
        packageDataResponse.setPackageContent(packageData.getPackageContent());
        packageDataResponse.setPackagePrice(packageData.getPackagePrice());
        packageDataResponse.setPackagePriceCurrency(NumberUtils.convertBigDecimalToCurrency(packageData.getPackagePrice()));
        packageDataResponse.setCycle(packageData.getCycle());
        packageDataResponse.setRegistrationSyntax(packageData.getRegistrationSyntax());
        packageDataResponse.setCancelSyntax(packageData.getCancelSyntax());
        packageDataResponse.setStatusNumber(packageData.getStatus());
        packageDataResponse.setStatus(this.getStatus(packageData.getStatus()));
        List<PackageCodeCDR> packageCodeCDRS = packageCodeRepository.findAllByPackageDataId(packageData.getId());
        String packageCodeName = packageCodeCDRS.stream().map(PackageCodeCDR::getPackageCodeName).collect(Collectors.joining("; "));
        List<Long> packageCodeIds = packageCodeCDRS.stream().map(PackageCodeCDR::getId).collect(Collectors.toList());
        List<String> packageCodeNames = packageCodeCDRS.stream().map(PackageCodeCDR::getPackageCodeName).collect(Collectors.toList());
        packageDataResponse.setPackageCodeName(packageCodeName);
        packageDataResponse.setPackageCodeIds(packageCodeIds);
        packageDataResponse.setPackageCodeNames(packageCodeNames);
        packageDataResponse.setPackageCode(packageData.getPackageCode());
        return packageDataResponse;
    }

    public String getPackageGroup(Integer packageGroup) {
        switch (packageGroup) {
            case 1:
                return "DATA";
            case 2:
                return "KMCB";
            default:
                throw new RuntimeException("Nhóm gói cước không hợp lệ.");
        }
    }

    public String getStatus(Integer status) {
        switch (status) {
            case 1:
                return "Hoạt động";
            case 0:
                return "Tạm dừng";
            default:
                throw new RuntimeException("Trạng thái không hợp lệ.");
        }
    }

}
