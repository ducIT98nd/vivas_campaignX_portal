package com.vivas.campaignx.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "BLACKLIST_FILE")
public class BlackListFile {
    @Id
    @SequenceGenerator(name = "blackListGenerator", sequenceName = "SEQ_BLACKLIST", allocationSize = 1)
    @GeneratedValue(generator = "blackListGenerator", strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;
    @Column(name = "name")
    private String name;
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
    @Column(name = "BLACK_LIST_PATH_FILE")
    private String blackListPathFile;

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

    public String getBlackListPathFile() {
        return blackListPathFile;
    }

    public void setBlackListPathFile(String blackListPathFile) {
        this.blackListPathFile = blackListPathFile;
    }
}
