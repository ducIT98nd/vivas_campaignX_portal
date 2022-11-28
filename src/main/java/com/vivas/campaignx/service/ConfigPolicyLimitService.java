package com.vivas.campaignx.service;

import com.vivas.campaignx.entity.ConfigPolicyLimit;
import com.vivas.campaignx.repository.ConfigPolicyLimitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ConfigPolicyLimitService {
    @Autowired
    private ConfigPolicyLimitRepository configPolicyLimitRepository;

    public List<ConfigPolicyLimit> findAll() {
        return configPolicyLimitRepository.findAll();
    }

    public String updateConfigPolicyLimit(HashMap<String, String> reqParams, List<String> startTimeLimitSMS, List<String> endTimeLimitSMS,
                                          List<String> startTimeLimitUSSD, List<String> endTimeLimitUSSD,
                                          List<String> startTimeLimitIVR, List<String> endTimeLimitIVR) {
        configPolicyLimitRepository.deleteAll();
        String limitMessagesPerDay = reqParams.get("limitMessagesPerDay");
        String limitMessagesPerDaySMS = reqParams.get("limitMessagesPerDaySMS");
        String limitMessagesPerDayUSSD = reqParams.get("limitMessagesPerDayUSSD");
        String limitMessagesPerDayIVR = reqParams.get("limitMessagesPerDayIVR");


        String limitMessagesPerDayEmail = reqParams.get("limitMessagesPerDayEmail");
        String limitMessagesPerDayZalo = reqParams.get("limitMessagesPerDayZalo");
        String limitMessagesPerDayDigiLife = reqParams.get("limitMessagesPerDayDigiLife");

        //email
        String startTimeLimitEmail1 = reqParams.get("start-time-limit-email-1");
        String endTimeLimitEmail1 = reqParams.get("end-time-limit-email-1");
        String startTimeLimitEmail2 = reqParams.get("start-time-limit-email-2");
        String endTimeLimitEmail2 = reqParams.get("end-time-limit-email-2");
        //zalo
        String startTimeLimitZalo1 = reqParams.get("start-time-limit-zalo-1");
        String endTimeLimitZalo1 = reqParams.get("end-time-limit-zalo-1");
        String startTimeLimitZalo2 = reqParams.get("start-time-limit-zalo-2");
        String endTimeLimitZalo2 = reqParams.get("end-time-limit-zalo-2");
        //digi
        String startTimeLimitDigilife1 = reqParams.get("start-time-limit-digilife-1");
        String endTimeLimitDigilife1 = reqParams.get("end-time-limit-digilife-1");
        String startTimeLimitDigilife2 = reqParams.get("start-time-limit-digilife-2");
        String endTimeLimitDigilife2 = reqParams.get("end-time-limit-digilife-2");

        List<ConfigPolicyLimit> configPolicyLimitList = new ArrayList<>();
        configPolicyLimitList.add(new ConfigPolicyLimit("limit-messages-per-day", limitMessagesPerDay));
        configPolicyLimitList.add(new ConfigPolicyLimit("limit-messages-per-day-sms", limitMessagesPerDaySMS));
        configPolicyLimitList.add(new ConfigPolicyLimit("limit-messages-per-day-ussd", limitMessagesPerDayUSSD));
        configPolicyLimitList.add(new ConfigPolicyLimit("limit-messages-per-day-ivr", limitMessagesPerDayIVR));
        configPolicyLimitList.add(new ConfigPolicyLimit("limit-messages-per-day-email", limitMessagesPerDayEmail));
        configPolicyLimitList.add(new ConfigPolicyLimit("limit-messages-per-day-zalo", limitMessagesPerDayZalo));
        configPolicyLimitList.add(new ConfigPolicyLimit("limit-messages-per-day-digilife", limitMessagesPerDayDigiLife));
        //sms
        if (startTimeLimitSMS.size() > 1) {
            configPolicyLimitList.add(new ConfigPolicyLimit("start-time-limit-sms-1", startTimeLimitSMS.get(0)));
            configPolicyLimitList.add(new ConfigPolicyLimit("start-time-limit-sms-2", startTimeLimitSMS.get(1)));
        } else configPolicyLimitList.add(new ConfigPolicyLimit("start-time-limit-sms-1", startTimeLimitSMS.get(0)));
        if (endTimeLimitSMS.size() > 1) {
            configPolicyLimitList.add(new ConfigPolicyLimit("end-time-limit-sms-1", endTimeLimitSMS.get(0)));
            configPolicyLimitList.add(new ConfigPolicyLimit("end-time-limit-sms-2", endTimeLimitSMS.get(1)));
        } else configPolicyLimitList.add(new ConfigPolicyLimit("end-time-limit-sms-1", endTimeLimitSMS.get(0)));

        //ussd
        if (startTimeLimitUSSD.size() > 1) {
            configPolicyLimitList.add(new ConfigPolicyLimit("start-time-limit-ussd-1", startTimeLimitUSSD.get(0)));
            configPolicyLimitList.add(new ConfigPolicyLimit("start-time-limit-ussd-2", startTimeLimitUSSD.get(1)));
        } else configPolicyLimitList.add(new ConfigPolicyLimit("start-time-limit-ussd-1", startTimeLimitUSSD.get(0)));
        if (endTimeLimitUSSD.size() > 1) {
            configPolicyLimitList.add(new ConfigPolicyLimit("end-time-limit-ussd-1", endTimeLimitUSSD.get(0)));
            configPolicyLimitList.add(new ConfigPolicyLimit("end-time-limit-ussd-2", endTimeLimitUSSD.get(1)));
        } else configPolicyLimitList.add(new ConfigPolicyLimit("end-time-limit-ussd-1", endTimeLimitUSSD.get(0)));


        //ivr
        if (startTimeLimitIVR.size() > 1) {
            configPolicyLimitList.add(new ConfigPolicyLimit("start-time-limit-ivr-1", startTimeLimitIVR.get(0)));
            configPolicyLimitList.add(new ConfigPolicyLimit("start-time-limit-ivr-2", startTimeLimitIVR.get(1)));
        } else  configPolicyLimitList.add(new ConfigPolicyLimit("start-time-limit-ivr-1", startTimeLimitIVR.get(0)));

        if (endTimeLimitIVR.size() > 1) {
            configPolicyLimitList.add(new ConfigPolicyLimit("end-time-limit-ivr-1", endTimeLimitIVR.get(0)));
            configPolicyLimitList.add(new ConfigPolicyLimit("end-time-limit-ivr-2", endTimeLimitIVR.get(1)));
        } else configPolicyLimitList.add(new ConfigPolicyLimit("end-time-limit-ivr-1", endTimeLimitIVR.get(0)));


        //email
        configPolicyLimitList.add(new ConfigPolicyLimit("start-time-limit-email-1", startTimeLimitEmail1));
        configPolicyLimitList.add(new ConfigPolicyLimit("end-time-limit-email-1", endTimeLimitEmail1));
        configPolicyLimitList.add(new ConfigPolicyLimit("start-time-limit-email-2", startTimeLimitEmail2));
        configPolicyLimitList.add(new ConfigPolicyLimit("end-time-limit-email-2", endTimeLimitEmail2));

        //zalo
        configPolicyLimitList.add(new ConfigPolicyLimit("start-time-limit-zalo-1", startTimeLimitZalo1));
        configPolicyLimitList.add(new ConfigPolicyLimit("end-time-limit-zalo-1", endTimeLimitZalo1));
        configPolicyLimitList.add(new ConfigPolicyLimit("start-time-limit-zalo-2", startTimeLimitZalo2));
        configPolicyLimitList.add(new ConfigPolicyLimit("end-time-limit-zalo-2", endTimeLimitZalo2));

        //digi
        configPolicyLimitList.add(new ConfigPolicyLimit("start-time-limit-digilife-1", startTimeLimitDigilife1));
        configPolicyLimitList.add(new ConfigPolicyLimit("end-time-limit-digilife-1", endTimeLimitDigilife1));
        configPolicyLimitList.add(new ConfigPolicyLimit("start-time-limit-digilife-2", startTimeLimitDigilife2));
        configPolicyLimitList.add(new ConfigPolicyLimit("end-time-limit-digilife-2", endTimeLimitDigilife2));
        configPolicyLimitRepository.saveAll(configPolicyLimitList);
        return null;
    }
}
