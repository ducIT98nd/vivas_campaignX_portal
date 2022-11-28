package com.vivas.campaignx.entity;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "package_data")
@EntityListeners(AuditingEntityListener.class)
public class PackageData implements Serializable {

    @Id
    @Column(name = "id", unique = true, precision = 10)
    @SequenceGenerator(name = "packageDataGenerator", sequenceName = "SEQ_PACKAGE_DATA", allocationSize = 1)
    @GeneratedValue(generator = "packageDataGenerator", strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "package_name")
    private String packageName;

    @Column(name = "package_code")
    private String packageCode;

    @Column(name = "package_group")
    private Integer packageGroup;

    @Column(name = "package_content")
    private String packageContent;

    @Column(name = "package_price")
    private BigDecimal packagePrice;

    @Column(name = "cycle")
    private Integer cycle;

    @Column(name = "registration_syntax")
    private String registrationSyntax;

    @Column(name = "cancel_syntax")
    private String cancelSyntax;

    @Column(name = "status")
    private Integer status;

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "modified_by")
    private String modifiedBy;

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

    public Integer getPackageGroup() {
        return packageGroup;
    }

    public void setPackageGroup(Integer packageGroup) {
        this.packageGroup = packageGroup;
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
        return cycle;
    }

    public void setCycle(Integer cycle) {
        this.cycle = cycle;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
}
