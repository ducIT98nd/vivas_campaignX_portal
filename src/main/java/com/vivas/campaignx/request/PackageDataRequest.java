package com.vivas.campaignx.request;

import java.math.BigDecimal;
import java.util.List;

public final class PackageDataRequest {
    private Long id;
    private String packageCode;
    private String packageName;
    private Integer packageGroup;
    private List<String> packageCodeName;
    private String packageContent;
    private BigDecimal packagePrice;
    private Integer cycle;
    private String registrationSyntax;
    private String cancelSyntax;
    private Integer status;

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPackageName() {
        return packageName;
    }

    public Integer getPackageGroup() {
        return packageGroup;
    }

    public List<String> getPackageCodeName() {
        return packageCodeName;
    }

    public String getPackageContent() {
        return packageContent;
    }

    public BigDecimal getPackagePrice() {
        return packagePrice;
    }

    public Integer getCycle() {
        return cycle;
    }

    public String getRegistrationSyntax() {
        return registrationSyntax;
    }

    public String getCancelSyntax() {
        return cancelSyntax;
    }

    public Integer getStatus() {
        return status;
    }
}
