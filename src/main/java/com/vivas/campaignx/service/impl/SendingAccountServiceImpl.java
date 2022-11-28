package com.vivas.campaignx.service.impl;

import com.vivas.campaignx.common.*;
import com.vivas.campaignx.entity.SendingAccount;
import com.vivas.campaignx.repository.SendingAccountRepository;
import com.vivas.campaignx.request.SendingAccountRequest;
import com.vivas.campaignx.response.SendingAccountResponse;
import com.vivas.campaignx.service.SendingAccountService;
import com.vivas.campaignx.service.mapper.SendingAccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SendingAccountServiceImpl implements SendingAccountService {

    @Autowired
    private SendingAccountRepository sendingAccountRepository;

    @Autowired
    private SendingAccountMapper sendingAccountMapper;

    @Override
    public Page<SendingAccountResponse> findAll(Integer pageSize, Integer currentPage, String senderAccount, Integer mediaChannel, Integer status) {
        Pageable paging = PageRequest.of(currentPage - 1, pageSize);
        return sendingAccountRepository.findAll(mediaChannel, senderAccount, status, paging).map(sendingAccountMapper::toDto);
    }

    @Override
    public ResponseEntity createSendingAccount(SendingAccountRequest sendingAccountRequest) {
        ResponseEntity responseEntity = new ResponseEntity();
        SendingAccount sendingAccount = new SendingAccount();
        sendingAccount.setSenderAccount(sendingAccountRequest.getSenderAccount());
        sendingAccount.setMediaChannel(sendingAccountRequest.getMediaChannel());
        sendingAccount.setStatus(sendingAccountRequest.getStatus());
        sendingAccountRepository.save(sendingAccount);
        responseEntity.setCode(CodeConstants.OK);
        responseEntity.setMessage(MessageConstants.SAVE_SENDING_ACCOUNT_SUCCESS);
        return responseEntity;
    }

    @Override
    public ResponseEntity getSendingAccountDetail(Long id) {
        ResponseEntity responseEntity = new ResponseEntity();
        Optional<SendingAccount> sendingAccountOptional = sendingAccountRepository.findById(id);
        if (sendingAccountOptional.isPresent()) {
            SendingAccountResponse sendingAccountResponse = sendingAccountMapper.toDto(sendingAccountOptional.get());
            responseEntity.setCode(CodeConstants.OK);
            responseEntity.setData(sendingAccountResponse);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity updateSendingAccount(SendingAccountRequest sendingAccountRequest) {
        ResponseEntity responseEntity = new ResponseEntity();
        Optional<SendingAccount> sendingAccountOptional = sendingAccountRepository.findById(sendingAccountRequest.getId());
        SendingAccount sendingAccount = sendingAccountOptional.get();
        sendingAccount.setLastModifiedDate(LocalDateTime.now());
        sendingAccount.setMediaChannel(sendingAccountRequest.getMediaChannel());
        sendingAccount.setSenderAccount(sendingAccountRequest.getSenderAccount());
        sendingAccount.setStatus(sendingAccountRequest.getStatus());
        sendingAccount.setModifiedBy(sendingAccountRequest.getModifiedBy());
        sendingAccountRepository.save(sendingAccount);

        responseEntity.setCode(CodeConstants.OK);
        responseEntity.setMessage(MessageConstants.UPDATE_SENDING_ACCOUNT_SUCCESS);
        return responseEntity;
    }

    @Override
    public ResponseEntity deleteSendingAccount(Long id) {
        ResponseEntity responseEntity = new ResponseEntity();

        Optional<SendingAccount> sendingAccountOptional = sendingAccountRepository.findById(id);
        if (!sendingAccountOptional.isPresent()) {
            responseEntity.setCode(CodeConstants.INTERNAL_SERVER_ERROR);
            responseEntity.setMessage(MessageConstants.SYSTEM_ERROR);
            return responseEntity;
        }

        SendingAccount sendingAccount = sendingAccountOptional.get();
        sendingAccount.setStatus(CommonConstants.STATUS_DELETED);
        sendingAccountRepository.save(sendingAccount);

        responseEntity.setCode(CodeConstants.OK);
        responseEntity.setMessage(MessageConstants.DELETE_SENDING_ACCOUNT_SUCCESS);
        return responseEntity;
    }

    @Override
    public List<SendingAccount> findByStatus(Integer status) {
        return sendingAccountRepository.findByStatus(status);
    }

    @Override
    public Optional<SendingAccount> findById(Long id) {
        return sendingAccountRepository.findById(id);
    }

    @Override
    public Optional<SendingAccount> checkName(String senderAccount) {
        return sendingAccountRepository.findBySenderAccountIgnoreCaseAndStatusNot(senderAccount,CommonConstants.STATUS_DELETED);
    }

    @Override
    public Optional<SendingAccount> checkNameIsNotId(String name, Long id) {
        return sendingAccountRepository.findBySenderAccountIgnoreCaseAndIdNotAndStatusNot(name, id, CommonConstants.STATUS_DELETED);
    }

    @Override
    public boolean validateSenderAccount(String senderAccount) {
        for (String s : senderAccount.split(RegexConstants.EMPTY)) {
            if (!RegexConstants.SENDER_ACCOUNT_REGEX.contains(s)) {
                return false;
            }
        }
        return true;
    }


}
