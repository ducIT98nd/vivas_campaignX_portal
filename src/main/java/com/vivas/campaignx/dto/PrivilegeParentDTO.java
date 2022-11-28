package com.vivas.campaignx.dto;

import java.io.Serializable;
import java.util.List;

public class PrivilegeParentDTO implements Serializable {
    private Long privilegeParentId;

    private String privilegeName;

    private List<PrivilegeChildrenDTO> privilegeIds;

    public PrivilegeParentDTO() {
    }


    public void setPrivilegeParentId(Long privilegeParentId) {
        this.privilegeParentId = privilegeParentId;
    }

    public String getPrivilegeName() {
        return privilegeName;
    }

    public void setPrivilegeName(String privilegeName) {
        this.privilegeName = privilegeName;
    }

    public List<PrivilegeChildrenDTO> getPrivilegeIds() {
        return privilegeIds;
    }

    public void setPrivilegeIds(List<PrivilegeChildrenDTO> privilegeIds) {
        this.privilegeIds = privilegeIds;
    }
}
