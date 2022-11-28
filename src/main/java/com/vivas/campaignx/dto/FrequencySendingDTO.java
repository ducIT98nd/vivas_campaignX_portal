package com.vivas.campaignx.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.vivas.campaignx.common.AppException;
import com.vivas.campaignx.common.AppUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.bridge.MessageUtil;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FrequencySendingDTO {

    protected final Logger logger = LogManager.getLogger(this.getClass().getName());

    private JSONObject jsonObjectValue;
    private boolean checkSendMessageExactDay;
    private boolean checkSendMessageEveryDay;
    private boolean checkMonthlyByDay;
    private boolean checkMonthlyByWeekday;
    private List<String> choosenValue;
    private Integer valueMonthlyByWeekday;
    private Integer valueMonthlyByWeekdayOrdinal;
    private Integer valueMonthlyByDay;
    private String valueExactDay;
    private Integer numberPeriodic;
    private Integer unitPeriodic;

    public FrequencySendingDTO(JSONObject jsonObjectValue) {
        this.jsonObjectValue = jsonObjectValue;
        this.choosenValue = new ArrayList<>();
        this.checkSendMessageExactDay = false;
        this.checkSendMessageEveryDay = false;
        this.checkMonthlyByDay = false;
        this.checkMonthlyByWeekday = false;
        this.valueMonthlyByDay = null;
        this.numberPeriodic = null;
        this.unitPeriodic = null;
    }

    public Integer getFrequencyType() {
        int frequencyType = 0;
        String frequency = this.jsonObjectValue.getString("frequency");
        if(frequency != null) {
            if (frequency.equals("once")) {
                frequencyType = 0;
            } else if (frequency.equals("daily")) {
                frequencyType = 1;
            } else if (frequency.equals("weekly")) {
                frequencyType = 2;
            } else if (frequency.equals("monthly1")) {
                frequencyType = 3;
            }else if(frequency.equals("monthly2")) {
                frequencyType = 4;
            }else if(frequency.equals("custom1")){
                frequencyType = 5;
            } else if (frequency.equals("custom2")) {
                frequencyType = 6;
            }
        }
        return frequencyType;
    }

    public void setCheckSendMessageExactDay(boolean checkSendMessageExactDay) {
        this.checkSendMessageExactDay = checkSendMessageExactDay;
    }

    public void setCheckSendMessageEveryDay(boolean checkSendMessageEveryDay) {
        this.checkSendMessageEveryDay = checkSendMessageEveryDay;
    }

    public void setChoosenValue(List<String> choosenValue) {
        this.choosenValue = choosenValue;
    }

    public void setValueMonthlyByDay(Integer valueMonthlyByDay) {
        this.valueMonthlyByDay = valueMonthlyByDay;
    }

    public void setCheckMonthlyByDay(boolean checkMonthlyByDay) {
        this.checkMonthlyByDay = checkMonthlyByDay;
    }

    public void setCheckMonthlyByWeekday(boolean checkMonthlyByWeekday) {
        this.checkMonthlyByWeekday = checkMonthlyByWeekday;
    }

    public void setValueMonthlyByWeekday(Integer valueMonthlyByWeekday) {
        this.valueMonthlyByWeekday = valueMonthlyByWeekday;
    }

    public void setValueMonthlyByWeekdayOrdinal(Integer valueMonthlyByWeekdayOrdinal) {
        this.valueMonthlyByWeekdayOrdinal = valueMonthlyByWeekdayOrdinal;
    }

    public void setValueExactDay(String valueExactDay) {
        this.valueExactDay = valueExactDay;
    }

    public void setNumberPeriodic(Integer numberPeriodic) {
        this.numberPeriodic = numberPeriodic;
    }

    public void setUnitPeriodic(Integer unitPeriodic) {
        this.unitPeriodic = unitPeriodic;
    }

    public String getValueExactDay() {
        return valueExactDay;
    }

    public boolean getCheckSendMessageExactDay(){
        return this.checkSendMessageExactDay;
    }

    public boolean getCheckSendMessageEveryDay(){
        return this.checkSendMessageEveryDay;
    }

    public Integer getNumberPeriodic(){
        return this.numberPeriodic;
    }

    public Integer getUnitPeriodic(){
        return this.unitPeriodic;
    }

    public List<String> getListChoosenDay(){
        return this.choosenValue;
    }

    public boolean getCheckMonthlyByDay(){
        return this.checkMonthlyByDay;
    }

    public Integer getValueMonthlyByDay(){
        return this.valueMonthlyByDay;
    }

    public boolean getCheckMonthlyByWeekday(){
        return this.checkMonthlyByWeekday;
    }

    public Integer getValueMonthlyByWeekday(){
        return this.valueMonthlyByWeekday;
    }

    public Integer getValueMonthlyByWeekdayOrdinal(){
        return this.valueMonthlyByWeekdayOrdinal;
    }


    public FrequencySendingDTO toDto (String frequencySending) throws AppException {
        JSONObject jsonValue = new JSONObject(frequencySending);
        FrequencySendingDTO frequencySendingDTO = new FrequencySendingDTO(jsonValue);
        String frequency = jsonValue.getString("frequency");

        if(frequency.equals("once")) {

        }else if(frequency.equals("daily")) {

        } else if(frequency.equals("weekly")) {
            List<String> lstDay = new ArrayList<>();
            String value = jsonValue.getString("value");
            String[] arrValue = value.split(",");
            lstDay = Arrays.asList(arrValue);
            frequencySendingDTO.setChoosenValue(lstDay);
        } else if(frequency.equals("monthly1")){
            int value = jsonValue.getInt("value");
            frequencySendingDTO.setCheckMonthlyByDay(true);
            frequencySendingDTO.setValueMonthlyByDay(value);
        } else if(frequency.equals("monthly2")){
            String value = jsonValue.getString("value");
            frequencySendingDTO.setCheckMonthlyByWeekday(true);
            frequencySendingDTO.setValueMonthlyByWeekday(Integer.parseInt(String.valueOf(value.charAt(0))));
            frequencySendingDTO.setValueMonthlyByWeekdayOrdinal(Integer.parseInt(String.valueOf(value.charAt(1))));
        } else if(frequency.equals("custom1")){
            frequencySendingDTO.setCheckSendMessageExactDay(true);
            String value = jsonValue.getString("value");
            String[] arrValue = value.split(",");
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < arrValue.length; i++){
                sb.append(AppUtils.formatStringDate( arrValue[i], "ddMMyyyy", "dd/MM/yyyy"));
                sb.append(", ");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            frequencySendingDTO.setValueExactDay(sb.toString());

        }else if(frequency.equals("custom2")){
            frequencySendingDTO.setCheckSendMessageEveryDay(true);
            int rate = jsonValue.getInt("rate");
            frequencySendingDTO.setNumberPeriodic(rate);
            JSONObject jsonUnit = jsonValue.getJSONObject("value");
            String time = jsonUnit.getString("frequency");
            if(time.equals("daily")){
                frequencySendingDTO.setUnitPeriodic(1);
            }else if(time.equals("weekly")){
                frequencySendingDTO.setUnitPeriodic(2);

                List<String> lstDay = new ArrayList<>();
                String value = jsonUnit.getString("value");
                String[] arrValue = value.split(",");
                lstDay = Arrays.asList(arrValue);
                frequencySendingDTO.setChoosenValue(lstDay);
            }else if(time.equals("monthly1")){
                frequencySendingDTO.setCheckMonthlyByDay(true);
                frequencySendingDTO.setUnitPeriodic(3);
                int valueTime = jsonUnit.getInt("value");
                frequencySendingDTO.setValueMonthlyByDay(valueTime);
            }else if(time.equals("monthly2")){
                frequencySendingDTO.setCheckMonthlyByWeekday(true);
                frequencySendingDTO.setUnitPeriodic(3);
                String valueTime = jsonUnit.getString("value");
                frequencySendingDTO.setValueMonthlyByWeekday(Integer.parseInt(String.valueOf(valueTime.charAt(0))));
                frequencySendingDTO.setValueMonthlyByWeekdayOrdinal(Integer.parseInt(String.valueOf(valueTime.charAt(1))));
            }
        }
        return frequencySendingDTO;
    }

}
