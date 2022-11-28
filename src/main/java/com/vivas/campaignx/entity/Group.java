package com.vivas.campaignx.entity;

import javax.persistence.*;

@Entity(name = "GROUPS")
public class Group {
    @Id
    @Column(name = "GROUP_ID", unique = true, precision = 10)
    @SequenceGenerator(name = "groupGenerator", sequenceName = "SEQ_GROUPS", allocationSize = 1)
    @GeneratedValue(generator = "groupGenerator", strategy = GenerationType.AUTO)
    private Long groupID;

    @Column(name = "GROUP_NAME")
    private String groupName;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "STATUS")
    private Boolean status;

    public Long getGroupID() {
        return groupID;
    }

    public void setGroupID(Long groupID) {
        this.groupID = groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "{" +
                "\"groupID\" : " + groupID +
                ", \"groupName\" :\"" + groupName + "\"" +
                ", \"description\" :\"" + description + "\"" +
                ", \"status\" :"  + status +
                "}";
    }
}
