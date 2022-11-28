package com.vivas.campaignx.dto;

import java.time.LocalDateTime;

public interface SendingAccountDTO {
    Long getId();

    String getSenderAccount();

    Integer getMediaChannel();

    LocalDateTime getCreatedDate();

    String getCreatedBy();

    Integer getStatus();

}
