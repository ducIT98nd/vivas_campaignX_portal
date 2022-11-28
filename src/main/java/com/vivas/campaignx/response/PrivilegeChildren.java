package com.vivas.campaignx.response;

public class PrivilegeChildren {
    private Long privilegeId;
    private String actionName;

    public PrivilegeChildren() {
    }

    public PrivilegeChildren(Long privilegeId, String actionName) {
        this.privilegeId = privilegeId;
        this.actionName = actionName;
    }

    public Long getPrivilegeId() {
        return privilegeId;
    }

    public void setPrivilegeId(Long privilegeId) {
        this.privilegeId = privilegeId;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }
}
