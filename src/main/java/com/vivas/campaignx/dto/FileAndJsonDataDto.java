package com.vivas.campaignx.dto;

import org.springframework.web.multipart.MultipartFile;

public class FileAndJsonDataDto {
    private String data;
    private MultipartFile file;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile pathFile) {
        this.file = pathFile;
    }
}
