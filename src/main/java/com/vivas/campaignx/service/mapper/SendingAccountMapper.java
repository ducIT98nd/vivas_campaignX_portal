package com.vivas.campaignx.service.mapper;

import com.vivas.campaignx.dto.SendingAccountDTO;
import com.vivas.campaignx.entity.SendingAccount;
import com.vivas.campaignx.response.SendingAccountResponse;
import com.vivas.campaignx.utils.DateTimeUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public final class SendingAccountMapper {

    public List<SendingAccountResponse> toDto(List<SendingAccountDTO> arg0) {
        if (CollectionUtils.isEmpty(arg0)) {
            return null;
        }

        List<SendingAccountResponse> list = new ArrayList<>(arg0.size());
        for (SendingAccountDTO sendingAccount : arg0) {
            list.add(toDto(sendingAccount));
        }

        return list;
    }

    public SendingAccountResponse toDto(SendingAccountDTO sendingAccountDTO) {
        if (Objects.isNull(sendingAccountDTO)) {
            return null;
        }

        SendingAccountResponse sendingAccountResponse = new SendingAccountResponse();
        sendingAccountResponse.setId(sendingAccountDTO.getId());
        sendingAccountResponse.setSenderAccount(sendingAccountDTO.getSenderAccount());
        sendingAccountResponse.setMediaChannel(this.getMediaChannel(sendingAccountDTO.getMediaChannel()));
        sendingAccountResponse.setCreatedBy(sendingAccountDTO.getCreatedBy());
        sendingAccountResponse.setCreatedDate(DateTimeUtils.DEFAULT_DATE_FORMATTER.format(sendingAccountDTO.getCreatedDate()));
        sendingAccountResponse.setStatus(this.getStatus(sendingAccountDTO.getStatus()));
        return sendingAccountResponse;
    }

    public SendingAccountResponse toDto(SendingAccount sendingAccount) {
        if (Objects.isNull(sendingAccount)) {
            return null;
        }

        SendingAccountResponse sendingAccountResponse = new SendingAccountResponse();
        sendingAccountResponse.setId(sendingAccount.getId());
        sendingAccountResponse.setSenderAccount(sendingAccount.getSenderAccount());
        sendingAccountResponse.setMediaChannel(this.getMediaChannel(sendingAccount.getMediaChannel()));
        sendingAccountResponse.setCreatedBy(sendingAccount.getCreatedBy());
        sendingAccountResponse.setCreatedDate(DateTimeUtils.DEFAULT_DATE_TIME_FORMATTER.format(sendingAccount.getCreatedDate()));
        sendingAccountResponse.setStatus(this.getStatus(sendingAccount.getStatus()));
        sendingAccountResponse.setModifiedBy(sendingAccount.getModifiedBy());
        if (sendingAccount.getLastModifiedDate() != null) {
            sendingAccountResponse.setLastModifiedDate(DateTimeUtils.DEFAULT_DATE_TIME_FORMATTER.format(sendingAccount.getLastModifiedDate()));
        }
        sendingAccountResponse.setMediaChannelNumber(sendingAccount.getMediaChannel());
        sendingAccountResponse.setStatusNumber(sendingAccount.getStatus());
        return sendingAccountResponse;
    }


    public String getMediaChannel(Integer mediaChannel) {
        if (mediaChannel == 1) {
            return "SMS";
        }
        throw new RuntimeException("Kênh truyền thông không hợp lệ.");
    }

    public String getStatus(Integer status) {
        switch (status) {
            case 0:
                return "Hoạt động";
            case 1:
                return "Tạm dừng";
            case 2:
                return "Ngừng hoạt động";
            default:
                throw new RuntimeException("Trạng thái không hợp lệ.");
        }
    }
}
