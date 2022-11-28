package com.vivas.campaignx.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "TARGET_GROUP")
public class TargetGroup implements Serializable {

    @Id
    @SequenceGenerator(name = "targetGroupGenerator", sequenceName = "SEQ_TARGET_GROUP", allocationSize = 1)
    @GeneratedValue(generator = "targetGroupGenerator", strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;
    @Column(name = "NAME")
    private String name;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "CHANNEL")
    private Integer channel;
    @Column(name = "PATH_FILE_MSISDN")
    private String pathFileMsisdn;
    @Column(name = "CREATED_USER")
    private String createdUser;
    @Column(name = "UPDATED_USER")
    private String updatedUser;
    @Column(name = "CREATED_DATE")
    private Date createdDate;
    @Column(name = "UPDATED_DATE")
    private Date updatedDate;
    @Column(name = "STATUS")
    private Integer status;
    @Column(name = "COPIED_NUMBER")
    private Integer copiedNumber;
    @Column(name = "QUERY")
    private String query;
    @Column(name = "type")
    private Integer type;
    @Column(name = "QUANTITY_MSISDN")
    private Long quantityMsisdn;
    @Column(name = "DATA_JSON")
    private String dataJson;
    @Column(name = "WHOLE_NETWORK")
    private Double wholeNetwork;
    @Column(name = "original_name_file_data_customer")
    private String originalNameFileDataCustomer;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Double getWholeNetwork() {
        return wholeNetwork;
    }

    public void setWholeNetwork(Double wholeNetwork) {
        this.wholeNetwork = wholeNetwork;
    }

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public String getPathFileMsisdn() {
        return pathFileMsisdn;
    }

    public void setPathFileMsisdn(String pathFileMsisdn) {
        this.pathFileMsisdn = pathFileMsisdn;
    }

    public String getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(String createdUser) {
        this.createdUser = createdUser;
    }

    public String getUpdatedUser() {
        return updatedUser;
    }

    public void setUpdatedUser(String updatedUser) {
        this.updatedUser = updatedUser;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCopiedNumber() {
        return copiedNumber;
    }

    public void setCopiedNumber(Integer copiedNumber) {
        this.copiedNumber = copiedNumber;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Long getQuantityMsisdn() {
        return quantityMsisdn;
    }

    public void setQuantityMsisdn(Long quantityMsisdn) {
        this.quantityMsisdn = quantityMsisdn;
    }

    public String getOriginalNameFileDataCustomer() {
        return originalNameFileDataCustomer;
    }

    public void setOriginalNameFileDataCustomer(String originalNameFileDataCustomer) {
        this.originalNameFileDataCustomer = originalNameFileDataCustomer;
    }
}
