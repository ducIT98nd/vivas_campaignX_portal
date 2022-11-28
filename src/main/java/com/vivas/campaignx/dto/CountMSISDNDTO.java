package com.vivas.campaignx.dto;

import java.io.Serializable;

public class CountMSISDNDTO implements Serializable {
    private Long targetGroupID;
    private Long count;
    private Double ratio;

    public Long getTargetGroupID() {
        return targetGroupID;
    }

    public void setTargetGroupID(Long targetGroupID) {
        this.targetGroupID = targetGroupID;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Double getRatio() {
        return ratio;
    }

    public void setRatio(Double ratio) {
        this.ratio = ratio;
    }


    @Override
    public String toString() {
        return "CountMSISDNDTO{" +
                "count=" + count +
                ", ratio=" + ratio +
                '}';
    }
}
