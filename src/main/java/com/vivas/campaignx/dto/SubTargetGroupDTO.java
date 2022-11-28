package com.vivas.campaignx.dto;

import com.vivas.campaignx.entity.SmsContent;
import com.vivas.campaignx.entity.SubTargetGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class SubTargetGroupDTO implements Serializable {


    private SubTargetGroup subTargetGroup;
    private SmsContentDTO smsContentDTO;

    public SubTargetGroup getSubTargetGroup() {
        return subTargetGroup;
    }

    public void setSubTargetGroup(SubTargetGroup subTargetGroup) {
        this.subTargetGroup = subTargetGroup;
    }

    public SmsContentDTO getSmsContentDTO() {
        return smsContentDTO;
    }

    public void setSmsContentDTO(SmsContentDTO smsContentDTO) {
        this.smsContentDTO = smsContentDTO;
    }

    @Override
    public String toString(){
        return "subTargetGroup.getName(): " + subTargetGroup.getName() + " smsContentDTO: " + smsContentDTO.getJsonSmsContentDTO();
    }
}
