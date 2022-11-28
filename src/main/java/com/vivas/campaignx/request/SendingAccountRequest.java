package com.vivas.campaignx.request;

public final class SendingAccountRequest {
    private Long id;
    private Integer mediaChannel;
    private String senderAccount;
    private Integer status;
    private String modifiedBy;

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMediaChannel() {
        return mediaChannel;
    }

    public void setMediaChannel(Integer mediaChannel) {
        this.mediaChannel = mediaChannel;
    }

    public String getSenderAccount() {
        return senderAccount;
    }

    public void setSenderAccount(String senderAccount) {
        this.senderAccount = senderAccount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
