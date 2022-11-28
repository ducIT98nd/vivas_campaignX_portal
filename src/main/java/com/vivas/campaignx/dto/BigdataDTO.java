package com.vivas.campaignx.dto;

import java.io.Serializable;
import java.util.List;

public class BigdataDTO implements Serializable {
    private List<NotifyTargetGroupDTO> contents;

    public List<NotifyTargetGroupDTO> getContents() {
        return contents;
    }

    public void setContents(List<NotifyTargetGroupDTO> contents) {
        this.contents = contents;
    }
}
