package com.vivas.campaignx.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vivas.campaignx.common.AppUtils;
import com.vivas.campaignx.common.enums.ErrorCodeEnum;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimpleResponseDTO<T> {
    public Integer code;
    public String message;
    public String url;
    public T data;

    public static SimpleResponseDTO<?> errorWithMessage(String message) {
        SimpleResponseDTO<?> simpleResponseDTO = new SimpleResponseDTO<>();
        simpleResponseDTO.setCode(ErrorCodeEnum.SYSTEM_ERROR.getCode());
        simpleResponseDTO.setMessage(message);
        return simpleResponseDTO;
    }

    public static SimpleResponseDTO<?> successWithMessage(String message) {
        SimpleResponseDTO<?> simpleResponseDTO = new SimpleResponseDTO<>();
        simpleResponseDTO.setCode(ErrorCodeEnum.SUCCESS.getCode());
        simpleResponseDTO.setMessage(message);
        return simpleResponseDTO;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
    
}
