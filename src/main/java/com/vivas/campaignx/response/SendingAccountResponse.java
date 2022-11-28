package com.vivas.campaignx.response;

public final class SendingAccountResponse {
    private Long id;
    private String senderAccount;
    private String mediaChannel;
    private String status;
    private String createdBy;
    private String createdDate;
    private String modifiedBy;
    private String lastModifiedDate;
    private Integer mediaChannelNumber;
    private Integer statusNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSenderAccount() {
        return senderAccount;
    }

    public void setSenderAccount(String senderAccount) {
        this.senderAccount = senderAccount;
    }

    public String getMediaChannel() {
        return mediaChannel;
    }

    public void setMediaChannel(String mediaChannel) {
        this.mediaChannel = mediaChannel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Integer getMediaChannelNumber() {
        return mediaChannelNumber;
    }

    public void setMediaChannelNumber(Integer mediaChannelNumber) {
        this.mediaChannelNumber = mediaChannelNumber;
    }

    public Integer getStatusNumber() {
        return statusNumber;
    }

    public void setStatusNumber(Integer statusNumber) {
        this.statusNumber = statusNumber;
    }
}
