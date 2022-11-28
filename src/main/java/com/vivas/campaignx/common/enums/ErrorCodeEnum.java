package com.vivas.campaignx.common.enums;

public enum ErrorCodeEnum {

    SUCCESS(0, "Thành công"),
    SYSTEM_ERROR(1, "Thất bại")
            ;
    private Integer code;
    private String message;

    ErrorCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
