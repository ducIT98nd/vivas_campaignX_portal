package com.vivas.campaignx.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "CONFIG_POLICY_LIMIT")
public class ConfigPolicyLimit {
    @Id
    @Column(name = "key")
    private String key;

    @Column(name = "value")
    private String value;

    public ConfigPolicyLimit(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public ConfigPolicyLimit() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
