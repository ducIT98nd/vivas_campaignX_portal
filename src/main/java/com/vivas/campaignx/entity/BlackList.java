package com.vivas.campaignx.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "BLACKLIST")
public class BlackList {
    @Id
    @SequenceGenerator(name = "blackListGenerator", sequenceName = "SEQ_BLACKLIST", allocationSize = 1)
    @GeneratedValue(generator = "blackListGenerator", strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MSISDN")
    private String msisdn;

    @Column(name = "CREATED_DATE")
    private Date createdDate;

    @Column(name = "FILE_ID")
    private Long fileId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }
}
