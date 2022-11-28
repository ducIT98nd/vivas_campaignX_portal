package com.vivas.campaignx.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "ROLE")
public class Role extends Auditable implements Serializable {
    @Id
    @Column(name = "ROLE_ID", unique = true, precision = 10, scale = 0)
    @SequenceGenerator(name = "roleGenerator", sequenceName = "SEQ_ROLE", allocationSize = 1)
    @GeneratedValue(generator = "roleGenerator", strategy = GenerationType.AUTO)
    private Long roleId;

    @Column(name = "ROLE_NAME")
    private String roleName;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "STATUS")
    private String status;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
