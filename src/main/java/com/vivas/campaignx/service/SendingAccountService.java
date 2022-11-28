package com.vivas.campaignx.service;

import com.vivas.campaignx.common.ResponseEntity;
import com.vivas.campaignx.entity.SendingAccount;
import com.vivas.campaignx.request.SendingAccountRequest;
import com.vivas.campaignx.response.SendingAccountResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface SendingAccountService {

    Page<SendingAccountResponse> findAll(Integer pageSize, Integer currentPage, String senderAccount, Integer mediaChannel, Integer status);

    ResponseEntity createSendingAccount(SendingAccountRequest sendingAccountRequest);

    ResponseEntity getSendingAccountDetail(Long id);

    ResponseEntity updateSendingAccount(SendingAccountRequest sendingAccountRequest);

    ResponseEntity deleteSendingAccount(Long id);

    List<SendingAccount> findByStatus(Integer status);

    Optional<SendingAccount> findById(Long id);

    Optional<SendingAccount> checkName(String name);

    Optional<SendingAccount> checkNameIsNotId(String name, Long id);

    boolean validateSenderAccount(String senderAccount);
}
