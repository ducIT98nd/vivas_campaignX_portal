package com.vivas.campaignx.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class SmsContentDTO {
    @JacksonXmlProperty(localName = "channelMarketing")
    private Integer channelMarketing;
    @JacksonXmlProperty(localName = "sendingAccount")
    private Long sendingAccount;
    @JacksonXmlProperty(localName = "productPackage")
    private Long productPackage;
    @JacksonXmlProperty(localName = "messageContent")
    private String messageContent;
    @JacksonXmlProperty(localName = "countMT")
    private Integer countMT;
    @JacksonXmlProperty(localName = "unicode")
    private Integer unicode;

    private String jsonSmsContentDTO;

    public Integer getChannelMarketing() {
        return channelMarketing;
    }

    public void setChannelMarketing(Integer channelMarketing) {
        this.channelMarketing = channelMarketing;
    }

    public Long getSendingAccount() {
        return sendingAccount;
    }

    public void setSendingAccount(Long sendingAccount) {
        this.sendingAccount = sendingAccount;
    }

    public Long getProductPackage() {
        return productPackage;
    }

    public void setProductPackage(Long productPackage) {
        this.productPackage = productPackage;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public Integer getCountMT() {
        return countMT;
    }

    public void setCountMT(Integer countMT) {
        this.countMT = countMT;
    }

    public Integer getUnicode() {
        return unicode;
    }

    public void setUnicode(Integer unicode) {
        this.unicode = unicode;
    }

    public Integer getMessageLength() {
        return this.messageContent.length();
    }

    public String getJsonSmsContentDTO() {
        return jsonSmsContentDTO;
    }

    public void setJsonSmsContentDTO(String jsonSmsContentDTO) {
        this.jsonSmsContentDTO = jsonSmsContentDTO;
    }
}
