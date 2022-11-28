package com.vivas.campaignx.dto;

import org.springframework.web.multipart.MultipartFile;

public class ClickHouseUploadDto {
    private String json;
    private MultipartFile file;

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
