package com.vivas.campaignx.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeneralResponse<T> {
	@JsonProperty("err_code")
    private Integer code;
	@JsonProperty("err_desc")
    private String message;

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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("err_code=").append(code);
        sb.append(", err_desc=").append(message);
        sb.append('}');
        return sb.toString();
    }
}
