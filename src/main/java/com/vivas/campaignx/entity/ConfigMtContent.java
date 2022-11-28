package com.vivas.campaignx.entity;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "CONFIG_MT_CONTENT")
public class ConfigMtContent {

    @Id
    @Column(name = "id", unique = true, precision = 10, scale = 0)
    private Long id;
    @Column(name = "action")
    private Integer action;
    @Column(name = "content")
    private String content;
    @Column(name = "action_description")
    private String actionDescription;

    @Column(name = "updated_date")
    private Date updatedDate;
    @Column(name = "updatedUser")
    private String updatedUser;

//    @Column(name = "number_mt")
//    private Integer numberMt;
//    @Column(name = "number_characters")
//    private Integer numberCharacters;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getActionDescription() {
        return actionDescription;
    }

    public void setActionDescription(String actionDescription) {
        this.actionDescription = actionDescription;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getUpdatedUser() {
        return updatedUser;
    }

    public void setUpdatedUser(String updatedUser) {
        this.updatedUser = updatedUser;
    }

//    public Integer getNumberMt() {
//        return numberMt;
//    }
//
//    public void setNumberMt(Integer numberMt) {
//        this.numberMt = numberMt;
//    }
//
//    public Integer getNumberCharacters() {
//        return numberCharacters;
//    }
//
//    public void setNumberCharacters(Integer numberCharacters) {
//        this.numberCharacters = numberCharacters;
 //   }
}
