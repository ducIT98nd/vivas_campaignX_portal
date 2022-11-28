package com.vivas.campaignx.dto;

public class DisableMessageLimitDTO {

    private boolean zalo = false;
    private boolean email = false;
    private boolean digilife = false;
    private boolean checkDisableMessageLimitChannel = false;

    public boolean isZalo() {
        return zalo;
    }

    public void setZalo(boolean zalo) {
        this.zalo = zalo;
    }

    public boolean isEmail() {
        return email;
    }

    public void setEmail(boolean email) {
        this.email = email;
    }

    public boolean isDigilife() {
        return digilife;
    }

    public void setDigilife(boolean digilife) {
        this.digilife = digilife;
    }

    public boolean isCheckDisableMessageLimitChannel() {
        return checkDisableMessageLimitChannel;
    }

    public void setCheckDisableMessageLimitChannel(boolean checkDisableMessageLimitChannel) {
        this.checkDisableMessageLimitChannel = checkDisableMessageLimitChannel;
    }
}
