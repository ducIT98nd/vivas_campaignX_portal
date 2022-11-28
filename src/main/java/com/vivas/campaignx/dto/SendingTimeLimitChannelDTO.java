package com.vivas.campaignx.dto;

import com.vivas.campaignx.common.AppException;
import com.vivas.campaignx.common.AppUtils;

public class SendingTimeLimitChannelDTO {

    private boolean zalo = false;
    private boolean email = false;
    private boolean digilife = false;
    private boolean checkSendingTimeLimitChannel = false;

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

    public boolean isCheckSendingTimeLimitChannel() {
        return checkSendingTimeLimitChannel;
    }

    public void setCheckSendingTimeLimitChannel(boolean checkSendingTimeLimitChannel) {
        this.checkSendingTimeLimitChannel = checkSendingTimeLimitChannel;
    }
}
