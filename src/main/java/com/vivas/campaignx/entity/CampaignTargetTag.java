package com.vivas.campaignx.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "CAMPAIGN_TARGET_TAG")
public class CampaignTargetTag implements Serializable {

    @Id
    @Column(name = "id", unique = true, precision = 10, scale = 0)
    @SequenceGenerator(name = "targetTagGenerator", sequenceName = "SEQ_CAMPAIGN_TARGET_TAG", allocationSize = 1)
    @GeneratedValue(generator = "targetTagGenerator", strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "target_text", unique = true)
    private String targetText;
    @Column(name = "count")
    private Integer count;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTargetText() {
        return targetText;
    }

    public void setTargetText(String targetText) {
        this.targetText = targetText;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
