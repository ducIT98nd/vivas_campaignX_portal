package com.vivas.campaignx.entity;

import javax.persistence.*;

@Entity
@Table(name = "SMS_CONTENT")
public class SmsContent {
    @Id
    @SequenceGenerator(name = "smsContentGenrator", sequenceName = "SEQ_SMS_CONTENT", allocationSize = 1)
    @GeneratedValue(generator = "smsContentGenrator", strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(name = "MESSAGE")
    private String message;
    @Column(name = "MT_COUNT")
    private Integer mtCount;
    @Column(name = "UNICODE")
    private Integer unicode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getMtCount() {
        return mtCount;
    }

    public void setMtCount(Integer mtCount) {
        this.mtCount = mtCount;
    }

    public Integer getUnicode() {
        return unicode;
    }

    public void setUnicode(Integer unicode) {
        this.unicode = unicode;
    }

}
