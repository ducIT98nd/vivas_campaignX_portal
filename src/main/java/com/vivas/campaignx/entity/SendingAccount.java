package com.vivas.campaignx.entity;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "sending_account")
@EntityListeners(AuditingEntityListener.class)
public class SendingAccount implements Serializable {

    @Id
    @Column(name = "id", unique = true, precision = 10)
    @SequenceGenerator(name = "sendingAccountGenerator", sequenceName = "SEQ_SENDING_ACCOUNT", allocationSize = 1)
    @GeneratedValue(generator = "sendingAccountGenerator", strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "media_channel")
    private Integer mediaChannel;

    @Column(name = "sender_account")
    private String senderAccount;

    @Column(name = "status")
    private Integer status;

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @Column(name = "modified_by")
    private String modifiedBy;

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
