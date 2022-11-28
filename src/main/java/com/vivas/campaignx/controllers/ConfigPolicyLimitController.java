package com.vivas.campaignx.controllers;

import com.vivas.campaignx.common.AppException;
import com.vivas.campaignx.common.AppUtils;
import com.vivas.campaignx.dto.SimpleResponseDTO;
import com.vivas.campaignx.entity.ConfigPolicyLimit;
import com.vivas.campaignx.service.ConfigPolicyLimitService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/config-policy")
public class ConfigPolicyLimitController {
    protected final Logger logger = LogManager.getLogger(this.getClass().getName());

    @Autowired
    private ConfigPolicyLimitService configPolicyLimitService;

    @PreAuthorize("hasAuthority('view:configpolicylimit')")
    @GetMapping
    public String view(Model model) {
        String limitMessagesPerDay = null;
        String limitMessagesPerDaySMS = null;
        String limitMessagesPerDayUSSD = null;
        String limitMessagesPerDayIVR = null;
        String limitMessagesPerDayEmail = null;
        String limitMessagesPerDayZalo = null;
        String limitMessagesPerDayDigilife = null;
        //sms
        String startTimeLimitSms1 = null;
        String endTimeLimitSms1 = null;
        String startTimeLimitSms2 = null;
        String endTimeLimitSms2 = null;
        //ussd
        String startTimeLimitUssd1 = null;
        String endTimeLimitUssd1 = null;
        String startTimeLimitUssd2 = null;
        String endTimeLimitUssd2 = null;
        //ivr
        String startTimeLimitIvr1 = null;
        String endTimeLimitIvr1 = null;
        String startTimeLimitIvr2 = null;
        String endTimeLimitIvr2 = null;
        //email
        String startTimeLimitEmail1 = null;
        String endTimeLimitEmail1 = null;
        String startTimeLimitEmail2 = null;
        String endTimeLimitEmail2 = null;
        //zalo
        String startTimeLimitZalo1 = null;
        String endTimeLimitZalo1 = null;
        String startTimeLimitZalo2 = null;
        String endTimeLimitZalo2 = null;
        //digi
        String startTimeLimitDigilife1 = null;
        String endTimeLimitDigilife1 = null;
        String startTimeLimitDigilife2 = null;
        String endTimeLimitDigilife2 = null;
        List<ConfigPolicyLimit> configPolicyLimitList = configPolicyLimitService.findAll();
        for (ConfigPolicyLimit configPolicyLimit: configPolicyLimitList) {
            if (configPolicyLimit.getKey().equals("limit-messages-per-day")) {
                limitMessagesPerDay = configPolicyLimit.getValue();
            }
            if (configPolicyLimit.getKey().equals("limit-messages-per-day-sms")) {
                limitMessagesPerDaySMS = configPolicyLimit.getValue();
            }
            if (configPolicyLimit.getKey().equals("limit-messages-per-day-ussd")) {
                limitMessagesPerDayUSSD = configPolicyLimit.getValue();
            }
            if (configPolicyLimit.getKey().equals("limit-messages-per-day-ivr")) {
                limitMessagesPerDayIVR = configPolicyLimit.getValue();
            }
            if (configPolicyLimit.getKey().equals("limit-messages-per-day-email")) {
                limitMessagesPerDayEmail = configPolicyLimit.getValue();
            }
            if (configPolicyLimit.getKey().equals("limit-messages-per-day-zalo")) {
                limitMessagesPerDayZalo = configPolicyLimit.getValue();
            }
            if (configPolicyLimit.getKey().equals("limit-messages-per-day-digilife")) {
                limitMessagesPerDayDigilife = configPolicyLimit.getValue();
            }
            //sms
            if (configPolicyLimit.getKey().equals("start-time-limit-sms-1")) {
                startTimeLimitSms1 = configPolicyLimit.getValue();
            }
            if (configPolicyLimit.getKey().equals("end-time-limit-sms-1")) {
                endTimeLimitSms1 = configPolicyLimit.getValue();
            }
            if (configPolicyLimit.getKey().equals("start-time-limit-sms-2")) {
                startTimeLimitSms2 = configPolicyLimit.getValue();
            }
            if (configPolicyLimit.getKey().equals("end-time-limit-sms-2")) {
                endTimeLimitSms2 = configPolicyLimit.getValue();
            }
            //ussd
            if (configPolicyLimit.getKey().equals("start-time-limit-ussd-1")) {
                startTimeLimitUssd1 = configPolicyLimit.getValue();
            }
            if (configPolicyLimit.getKey().equals("end-time-limit-ussd-1")) {
                endTimeLimitUssd1 = configPolicyLimit.getValue();
            }
            if (configPolicyLimit.getKey().equals("start-time-limit-ussd-2")) {
                startTimeLimitUssd2 = configPolicyLimit.getValue();
            }
            if (configPolicyLimit.getKey().equals("end-time-limit-ussd-2")) {
                endTimeLimitUssd2 = configPolicyLimit.getValue();
            }
            //ivr
            if (configPolicyLimit.getKey().equals("start-time-limit-ivr-1")) {
                startTimeLimitIvr1 = configPolicyLimit.getValue();
            }
            if (configPolicyLimit.getKey().equals("end-time-limit-ivr-1")) {
                endTimeLimitIvr1 = configPolicyLimit.getValue();
            }
            if (configPolicyLimit.getKey().equals("start-time-limit-ivr-2")) {
                startTimeLimitIvr2 = configPolicyLimit.getValue();
            }
            if (configPolicyLimit.getKey().equals("end-time-limit-ivr-2")) {
                endTimeLimitIvr2 = configPolicyLimit.getValue();
            }
            //email
            if (configPolicyLimit.getKey().equals("start-time-limit-email-1")) {
                startTimeLimitEmail1 = configPolicyLimit.getValue();
            }
            if (configPolicyLimit.getKey().equals("end-time-limit-email-1")) {
                endTimeLimitEmail1 = configPolicyLimit.getValue();
            }
            if (configPolicyLimit.getKey().equals("start-time-limit-email-2")) {
                startTimeLimitEmail2 = configPolicyLimit.getValue();
            }
            if (configPolicyLimit.getKey().equals("end-time-limit-email-2")) {
                endTimeLimitEmail2 = configPolicyLimit.getValue();
            }
            //zalo
            if (configPolicyLimit.getKey().equals("start-time-limit-zalo-1")) {
                startTimeLimitZalo1 = configPolicyLimit.getValue();
            }
            if (configPolicyLimit.getKey().equals("end-time-limit-zalo-1")) {
                endTimeLimitZalo1 = configPolicyLimit.getValue();
            }
            if (configPolicyLimit.getKey().equals("start-time-limit-zalo-2")) {
                startTimeLimitZalo2 = configPolicyLimit.getValue();
            }
            if (configPolicyLimit.getKey().equals("end-time-limit-zalo-2")) {
                endTimeLimitZalo2 = configPolicyLimit.getValue();
            }
            //digilife
            if (configPolicyLimit.getKey().equals("start-time-limit-digilife-1")) {
                startTimeLimitDigilife1 = configPolicyLimit.getValue();
            }
            if (configPolicyLimit.getKey().equals("end-time-limit-digilife-1")) {
                endTimeLimitDigilife1 = configPolicyLimit.getValue();
            }
            if (configPolicyLimit.getKey().equals("start-time-limit-digilife-2")) {
                startTimeLimitDigilife2 = configPolicyLimit.getValue();
            }
            if (configPolicyLimit.getKey().equals("end-time-limit-digilife-2")) {
                endTimeLimitDigilife2 = configPolicyLimit.getValue();
            }
        }
        model.addAttribute("limitMessagesPerDay", limitMessagesPerDay);
        model.addAttribute("limitMessagesPerDaySMS", limitMessagesPerDaySMS);
        model.addAttribute("limitMessagesPerDayUSSD", limitMessagesPerDayUSSD);
        model.addAttribute("limitMessagesPerDayIVR", limitMessagesPerDayIVR);
        model.addAttribute("limitMessagesPerDayEmail", limitMessagesPerDayEmail);
        model.addAttribute("limitMessagesPerDayZalo", limitMessagesPerDayZalo);
        model.addAttribute("limitMessagesPerDayDigilife", limitMessagesPerDayDigilife);
        model.addAttribute("startTimeLimitSms1", startTimeLimitSms1);
        model.addAttribute("endTimeLimitSms1", endTimeLimitSms1);
        model.addAttribute("startTimeLimitSms2", startTimeLimitSms2);
        model.addAttribute("endTimeLimitSms2", endTimeLimitSms2);
        model.addAttribute("startTimeLimitUssd1", startTimeLimitUssd1);
        model.addAttribute("endTimeLimitUssd1", endTimeLimitUssd1);
        model.addAttribute("startTimeLimitUssd2", startTimeLimitUssd2);
        model.addAttribute("endTimeLimitUssd2", endTimeLimitUssd2);
        model.addAttribute("startTimeLimitIvr1", startTimeLimitIvr1);
        model.addAttribute("endTimeLimitIvr1", endTimeLimitIvr1);
        model.addAttribute("startTimeLimitIvr2", startTimeLimitIvr2);
        model.addAttribute("endTimeLimitIvr2", endTimeLimitIvr2);
        model.addAttribute("startTimeLimitEmail1", startTimeLimitEmail1);
        model.addAttribute("endTimeLimitEmail1", endTimeLimitEmail1);
        model.addAttribute("startTimeLimitEmail2", startTimeLimitEmail2);
        model.addAttribute("endTimeLimitEmail2", endTimeLimitEmail2);
        model.addAttribute("startTimeLimitZalo1", startTimeLimitZalo1);
        model.addAttribute("endTimeLimitZalo1", endTimeLimitZalo1);
        model.addAttribute("startTimeLimitZalo2", startTimeLimitZalo2);
        model.addAttribute("endTimeLimitZalo2", endTimeLimitZalo2);
        model.addAttribute("startTimeLimitDigilife1", startTimeLimitDigilife1);
        model.addAttribute("endTimeLimitDigilife1", endTimeLimitDigilife1);
        model.addAttribute("startTimeLimitDigilife2", startTimeLimitDigilife2);
        model.addAttribute("endTimeLimitDigilife2", endTimeLimitDigilife2);
        return "sendingConfig/config-policy-limit";
    }

    @PreAuthorize("hasAuthority('update:configpolicylimit')")
    @PostMapping("/update")
    public String updateConfigPolicy(@RequestParam HashMap<String, String> reqParams,
                                     @RequestParam(required = false, name = "start-time-limit-sms") List<String> startTimeLimitSMS,
                                     @RequestParam(required = false, name = "end-time-limit-sms") List<String> endTimeLimitSMS,
                                     @RequestParam(required = false, name = "start-time-limit-ussd") List<String> startTimeLimitUSSD,
                                     @RequestParam(required = false, name = "end-time-limit-ussd") List<String> endTimeLimitUSSD,
                                     @RequestParam(required = false, name = "start-time-limit-ivr") List<String> startTimeLimitIVR,
                                     @RequestParam(required = false, name = "end-time-limit-ivr") List<String> endTimeLimitIVR,
                                     RedirectAttributes redirectAttributes) {
        SimpleResponseDTO res = new SimpleResponseDTO();
        try {
            configPolicyLimitService.updateConfigPolicyLimit(reqParams,startTimeLimitSMS,endTimeLimitSMS,startTimeLimitUSSD,endTimeLimitUSSD,startTimeLimitIVR,endTimeLimitIVR);
            res.setCode(AppUtils.successCode);
            res.setMessage("Lưu chính sách chung thành công.");
            String result = AppUtils.ObjectToJsonResponse(res);
            redirectAttributes.addFlashAttribute("result", result);
        } catch (Exception ex) {
            logger.error("Error while upload config policy limit: ", ex);
            Throwable rootcause = AppUtils.getrootcause(ex);
            res.setCode(AppUtils.errorCode);
            if (rootcause instanceof AppException) {
                AppException apex = (AppException) rootcause;
                res.setMessage(apex.getMessage());
            } else {
                res.setMessage("Có lỗi xảy ra, vui lòng liên hệ ban quản trị!");
            }
        }
        return "redirect:/config-policy";
    }
}
