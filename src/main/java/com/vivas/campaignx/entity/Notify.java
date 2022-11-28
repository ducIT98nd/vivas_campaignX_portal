package com.vivas.campaignx.entity;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "NOTIFY")
public class Notify {
    @Id
    @Column(name = "ID", unique = true, precision = 10)
    @SequenceGenerator(name = "notifyGenerator", sequenceName = "SEQ_NOTIFY", allocationSize = 1)
    @GeneratedValue(generator = "notifyGenerator", strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "SUBJECT")
    private String subject;

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "NOTIFY_TO_USER_ID")
    private Long notifyToUserId;

    @Column(name = "STATUS")
    private Long status;

    @Column(name = "CREATED_DATE")
    private Date createdDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getNotifyToUserId() {
        return notifyToUserId;
    }

    public void setNotifyToUserId(Long notifyToUserId) {
        this.notifyToUserId = notifyToUserId;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
