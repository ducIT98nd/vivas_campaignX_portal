package com.vivas.campaignx.service.impl;

import com.vivas.campaignx.entity.PackageCodeCDR;
import com.vivas.campaignx.repository.PackageCodeRepository;
import com.vivas.campaignx.service.PackageCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PackageCodeServiceImpl implements PackageCodeService {

    @Autowired
    private PackageCodeRepository packageCodeRepository;

    @Override
    public List<String> findAllPackageCodeName() {
        return packageCodeRepository.findAllPackageCodeNames().stream().map(PackageCodeCDR::getPackageCodeName).collect(Collectors.toList());
    }
}
