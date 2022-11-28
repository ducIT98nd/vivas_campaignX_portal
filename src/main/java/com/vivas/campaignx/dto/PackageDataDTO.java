package com.vivas.campaignx.dto;

import java.math.BigDecimal;

public interface PackageDataDTO {

    Long getId();

    String getPackageName();

    Integer getPackageGroup();

    String getPackageContent();

    BigDecimal getPackagePrice();

    Integer getCycle();

    String getRegistrationSyntax();

    String getCancelSyntax();

    Integer getStatus();

    String getPackageCodeName();

    String getPackageCodeNames();

}
