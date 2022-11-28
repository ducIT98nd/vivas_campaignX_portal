package com.vivas.campaignx.entity;

import javax.persistence.*;

@Entity(name = "GROUP_PERMISSION")
public class GroupPermission {
    @Id
    @Column(name = "ID", unique = true, precision = 10)
    @SequenceGenerator(name = "groupPermissionGenerator", sequenceName = "SEQ_GROUP_PERMISSION", allocationSize = 1)
    @GeneratedValue(generator = "groupPermissionGenerator", strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERMISSION_ID", nullable = false)
    private Permission permission;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }
}
