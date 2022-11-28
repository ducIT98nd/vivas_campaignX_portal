package com.vivas.campaignx.response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class PackageDataResponse {
    private Long id;
    private String packageName;
    private String packageCode;
    private String packageGroup;
    private Integer packageGroupNumber;
    private String packageContent;
    private BigDecimal packagePrice;
    private String packagePriceCurrency;
    private Integer Cycle;
    private String registrationSyntax;
    private String cancelSyntax;
    private String status;
    private Integer statusNumber;
    private String packageCodeName;
    private List<Long> packageCodeIds = new ArrayList<>();
    private List<String> packageCodeNames = new ArrayList<>();

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

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageGroup() {
        return packageGroup;
    }

    public void setPackageGroup(String packageGroup) {
        this.packageGroup = packageGroup;
    }

    public Integer getPackageGroupNumber() {
        return packageGroupNumber;
    }

    public void setPackageGroupNumber(Integer packageGroupNumber) {
        this.packageGroupNumber = packageGroupNumber;
    }

    public String getPackageContent() {
        return packageContent;
    }

    public void setPackageContent(String packageContent) {
        this.packageContent = packageContent;
    }

    public BigDecimal getPackagePrice() {
        return packagePrice;
    }

    public void setPackagePrice(BigDecimal packagePrice) {
        this.packagePrice = packagePrice;
    }

    public Integer getCycle() {
        return Cycle;
    }

    public void setCycle(Integer cycle) {
        Cycle = cycle;
    }

    public String getRegistrationSyntax() {
        return registrationSyntax;
    }

    public void setRegistrationSyntax(String registrationSyntax) {
        this.registrationSyntax = registrationSyntax;
    }

    public String getCancelSyntax() {
        return cancelSyntax;
    }

    public void setCancelSyntax(String cancelSyntax) {
        this.cancelSyntax = cancelSyntax;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPackageCodeName() {
        return packageCodeName;
    }

    public void setPackageCodeName(String packageCodeName) {
        this.packageCodeName = packageCodeName;
    }

    public List<Long> getPackageCodeIds() {
        return packageCodeIds;
    }

    public void setPackageCodeIds(List<Long> packageCodeIds) {
        this.packageCodeIds = packageCodeIds;
    }

    public Integer getStatusNumber() {
        return statusNumber;
    }

    public void setStatusNumber(Integer statusNumber) {
        this.statusNumber = statusNumber;
    }

    public List<String> getPackageCodeNames() {
        return packageCodeNames;
    }

    public void setPackageCodeNames(List<String> packageCodeNames) {
        this.packageCodeNames = packageCodeNames;
    }

    public String getPackagePriceCurrency() {
        return packagePriceCurrency;
    }

    public void setPackagePriceCurrency(String packagePriceCurrency) {
        this.packagePriceCurrency = packagePriceCurrency;
    }
}
