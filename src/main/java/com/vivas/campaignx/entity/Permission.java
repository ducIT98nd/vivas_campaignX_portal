package com.vivas.campaignx.entity;

import javax.persistence.*;
import java.sql.Date;

@Entity(name = "PERMISSION")
public class Permission extends Auditable {
    @Id
    @Column(name = "PERMISSION_ID", unique = true, precision = 10)
    @SequenceGenerator(name = "permissionGenerator", sequenceName = "SEQ_PERMISSIONS", allocationSize = 1)
    @GeneratedValue(generator = "permissionGenerator", strategy = GenerationType.AUTO)
    private Long permissionId;

    @Column(name = "ACTION_NAME")
    private String actionName;

    @Column(name = "ACTION_KEY")
    private String actionKey;


    @Column(name = "DESCRIPTION")
    private String description;

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getActionKey() {
        return actionKey;
    }

    public void setActionKey(String actionKey) {
        this.actionKey = actionKey;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
