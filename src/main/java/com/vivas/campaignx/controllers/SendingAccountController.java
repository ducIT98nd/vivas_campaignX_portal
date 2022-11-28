package com.vivas.campaignx.controllers;

import com.vivas.campaignx.common.AttributeConstants;
import com.vivas.campaignx.common.CommonConstants;
import com.vivas.campaignx.common.ResponseEntity;
import com.vivas.campaignx.entity.SendingAccount;
import com.vivas.campaignx.request.SendingAccountRequest;
import com.vivas.campaignx.response.SendingAccountResponse;
import com.vivas.campaignx.service.SendingAccountService;
import com.vivas.campaignx.utils.IgnoreWildCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@IgnoreWildCard
@Controller
@RequestMapping("/sending-accounts")
public class SendingAccountController {

    @Autowired
    private SendingAccountService sendingAccountService;

    @PreAuthorize("hasAuthority('view:search:sender')")
    @RequestMapping("/manager")
    public String sendingAccountManager(
            Model model,
            @RequestParam(required = false, defaultValue = CommonConstants.DEFAULT_PAGE_SIZE) Integer pageSize,
            @RequestParam(required = false, defaultValue = CommonConstants.DEFAULT_CURRENT_PAGE) Integer currentPage,
            @RequestParam(required = false) String senderAccount,
            @RequestParam(required = false) Integer mediaChannel,
            @RequestParam(required = false) Integer status
    ) {
        Page<SendingAccountResponse> sendingAccounts = sendingAccountService.findAll(pageSize, currentPage, senderAccount, mediaChannel, status);
        model.addAttribute(AttributeConstants.SENDER_ACCOUNT, senderAccount);
        model.addAttribute(AttributeConstants.MEDIA_CHANNEL, mediaChannel);
        model.addAttribute(AttributeConstants.STATUS, status);
        model.addAttribute(AttributeConstants.SENDING_ACCOUNTS, sendingAccounts);
        model.addAttribute(AttributeConstants.PAGE_SIZE, pageSize);
        model.addAttribute(AttributeConstants.CURRENT_PAGE, currentPage);
        model.addAttribute(AttributeConstants.TOTAL_PAGES, sendingAccounts.getTotalPages());

        return "sendingAccount/SendingAccount";
    }

    @PreAuthorize("hasAuthority('create:sender')")
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity createSendingAccount(@RequestBody SendingAccountRequest sendingAccountRequest) {
        return sendingAccountService.createSendingAccount(sendingAccountRequest);
    }

    @PreAuthorize("hasAuthority('detail:sender')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity getSendingAccountDetail(@PathVariable Long id) {
        return sendingAccountService.getSendingAccountDetail(id);
    }

    @PreAuthorize("hasAuthority('update:sender')")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity updateSendingAccount(@PathVariable Long id, @RequestBody SendingAccountRequest sendingAccountRequest) {
        UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        sendingAccountRequest.setId(id);
        sendingAccountRequest.setModifiedBy(currentUser.getUsername());
        return sendingAccountService.updateSendingAccount(sendingAccountRequest);
    }

    @PreAuthorize("hasAuthority('delete:sender')")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity deleteSendingAccount(@PathVariable Long id) {
        return sendingAccountService.deleteSendingAccount(id);
    }

    @RequestMapping(value = "/get-list-active", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public org.springframework.http.ResponseEntity<?> getListActiveSendingAccount() {
        List<SendingAccount> sendingAccountList = sendingAccountService.findByStatus(0);
        org.springframework.http.ResponseEntity responseEntity = new org.springframework.http.ResponseEntity<>(sendingAccountList, HttpStatus.OK);
        return responseEntity;
    }

    @RequestMapping(value = "/containSenderAccount", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> containSenderAccount(Model model, @RequestParam String name) {
        Map<String, Object> map = new HashMap<String, Object>();
        Optional<SendingAccount> sendingAccountOptional = sendingAccountService.checkName(name);
        if (sendingAccountOptional.isPresent()) {
            map.put("result", true);
        }
        else map.put("result", false);
        return map;
    }

    @RequestMapping(value = "/containSenderAccountIsNotId/{id}", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> containSenderAccountIsNotId(Model model, @RequestParam String name, @PathVariable("id") Long id) {
        Map<String, Object> map = new HashMap<String, Object>();
        Optional<SendingAccount> sendingAccountOptional = sendingAccountService.checkNameIsNotId(name, id);
        if (sendingAccountOptional.isPresent()) {
            map.put("result", true);
        }
        else map.put("result", false);
        return map;
    }


    @RequestMapping(value = "/validateSenderAccount", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> validateSenderAccount(Model model, @RequestParam String name) {
        Map<String, Object> map = new HashMap<String, Object>();
        boolean check = sendingAccountService.validateSenderAccount(name);
        if (check == false) {
            map.put("result", false);
        }
        else map.put("result", true);
        return map;
    }
}
