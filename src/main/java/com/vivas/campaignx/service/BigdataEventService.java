package com.vivas.campaignx.service;

import com.vivas.campaignx.dto.EventDataDTO;
import com.vivas.campaignx.dto.PackageNameDTO;
import com.vivas.campaignx.entity.BigdataEvent;
import com.vivas.campaignx.entity.EventCondition;
import com.vivas.campaignx.repository.BigdataEventRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BigdataEventService {
    protected final Logger logger = LogManager.getLogger(this.getClass().getName());

    @Autowired
    private BigdataEventRepository bigdataEventRepository;

    @Autowired
    private PackageDataService packageDataService;

    public List<BigdataEvent> findByStatus(Integer status) {
        return bigdataEventRepository.findByStatus(status);
    }

    public Optional<BigdataEvent> findById(Long id) {
        return bigdataEventRepository.findById(id);
    }

    public String getViewEventCondition(EventDataDTO dto, JSONObject eventCondition){

        StringBuilder htmlConditionField = new StringBuilder();

        String conditionName = eventCondition.getString("conditionName");
        String operator = eventCondition.getString("operator");
        String value = eventCondition.getString("value");

        List<EventCondition> conditions = dto.getEventConditions();
        EventCondition ecPos0 = null;
        for(int i = 0; i < conditions.size(); i++){
            EventCondition temp = conditions.get(i);
            if(conditionName.equals(temp.getJsonField())){
                ecPos0 = conditions.get(i);
                htmlConditionField.append("<option selected value=\"").append(temp.getJsonField()).append("\">").append(temp.getName()).append("</option>\n");
            }else htmlConditionField.append("<option value=\"").append(temp.getJsonField()).append("\">").append(temp.getName()).append("</option>\n");
        }

        String jsonCondition = ecPos0.getDataInput();
        JSONObject jsonObjectCondition = new JSONObject(jsonCondition);

        JSONObject jsonObjectOperator = jsonObjectCondition.getJSONObject("operator");
        String typeDisplay = jsonObjectOperator.getString("display");
        JSONArray arrayOperator = jsonObjectOperator.getJSONArray("value");

        JSONObject jsonObjectParam = jsonObjectCondition.getJSONObject("param");
        String typeParam = jsonObjectParam.getString("type");

        StringBuilder html = new StringBuilder("<div class=\"col-lg-12 row_crit\">\n" +
                "          <div class=\"row\">\n" +
                "                <div class=\"col-lg-11\">");

        html.append("<div class=\"row div-data-input\">\n" + "                                <div class=\"col-lg-4\">\n" + "                                    <div class=\"form-group\">\n" + "                                        <div class=\"dropdown bootstrap-select form-control custom-select select-condition\">\n" + "                                            <select class=\"form-control custom-select selectpicker select-condition\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n").append(htmlConditionField).append("                                            </select>\n").append("                                        </div>\n").append("                                    </div>\n").append("                                </div>\n");

        if(typeDisplay.equals("true")) {
            html.append("                                <div class=\"col-lg-3 div-operator\">\n");

        } else {
            html.append("                                <div style=\"display: none\" class=\"col-lg-3 div-operator\">\n");
        }

        if (typeParam.equals("calendar")) {
            html.append("                                    <div class=\"form-group\">\n" + "                                            <select class=\"form-control selectpicker select-operator calendar\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n");
        } else if (typeParam.equals("number")) {
            html.append("                                    <div class=\"form-group\">\n" + "                                            <select class=\"form-control selectpicker select-operator number\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n");
        } else {
            html.append("                                    <div class=\"form-group\">\n" + "                                            <select class=\"form-control selectpicker select-operator\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n");
        }

        if (typeParam.equals("calendar")) {
            html.append(convertOperatorCalendarInput(arrayOperator, operator));
        } else {
            html.append(convertOperatorTextInput(arrayOperator, operator));
        }

        html.append("                                            </select>\n" + "                                        </div>\n" + "                                </div>\n");

        html.append("<div class=\"col-lg-5 div-param\">   " + "   <div class=\"form-group\">     ");

        if (typeParam.equals("number")){
            if (operator.equals("><")) {
                String[] splitValue = value.split("-");
                html.append("      <input class=\"form-control condition-number condition-value\" onpaste=\"return pastePositiveNumber(event);\" type=\"number\" min='0'data-order=\"start\" style='width: 40%' placeholder='Từ' " + " onkeypress=\"return keyPressPositiveNumber(event);\" value=\"").append(splitValue[0]).append("\">");
                html.append("      <input class=\"form-control condition-number condition-value\" onpaste=\"return pastePositiveNumber(event);\" type=\"number\" min='0'data-order=\"end\" style='width: 40%' placeholder='Đến'" + " onkeypress=\"return keyPressPositiveNumber(event);\"  value=\"").append(splitValue[1]).append("\">");
            } else {
                html.append("      <input type=\"number\" min='0' style=\"width: 80%\" class=\"form-control condition-value\" " + " onpaste=\"return pastePositiveNumber(event);\" onkeypress=\"return keyPressPositiveNumber(event);\" value=\"").append(value).append("\">");
            }
            html.append("   <span style='font-size: 12px'>VND</span> ");

        } else if (typeParam.equals("text")){
            html.append("      <input type=\"text\" class=\"form-control condition-value\" value=\"").append(value).append("\">");
        } else if (typeParam.equals("combobox")){
            html.append("     <select class=\"form-control condition-value\">");
            Object valueParam = jsonObjectParam.get("value");
            if (valueParam.equals("[list-package]")) {
                List<PackageNameDTO> packageNameDTOList = packageDataService.findAllByStatusAndPackageGroupOrderByPackageName(1, 2);
                for(int i = 0; i < packageNameDTOList.size(); i++){
                    if(value.equals(packageNameDTOList.get(i).getId())) html.append("     <option selected value=\"").append(packageNameDTOList.get(i).getId()).append("\">").append(packageNameDTOList.get(i).getPackageName()).append("</option>");
                    else html.append("     <option value=\"").append(packageNameDTOList.get(i).getId()).append("\">").append(packageNameDTOList.get(i).getPackageName()).append("</option>");
                }
            } else {
                JSONObject jsonValueParam = jsonObjectParam.getJSONObject("value");
                for(String key : jsonValueParam.keySet()){
                    if(value.equals(key))  html.append("     <option selected value=\"").append(key).append("\">").append(jsonValueParam.getString(key)).append("</option>");
                    else html.append("     <option value=\"").append(key).append("\">").append(jsonValueParam.getString(key)).append("</option>");
                }
            }
            html.append("     </select>");
        } else if (typeParam.equals("calendar")){
            if (operator.equals("><")) {
                String[] splitValue = value.split("-");
                html.append("      <input class=\"form-control condition-date condition-value\" data-order=\"start\" style='width: 40%' placeholder='Từ ngày' value=\"").append(splitValue[0]).append("\">");
                html.append("      <input class=\"form-control condition-date condition-value\" data-order=\"end\" style='width: 40%' placeholder='Đến ngày' value=\"").append(splitValue[1]).append("\">");
            } else {
                html.append("      <input class=\"form-control condition-date condition-value\" value=\"").append(value).append("\">");
            }
        }

        html.append("   </div>" + "</div>");
        html.append("</div>");
        html.append("</div>");
        html.append("<div class=\"col-lg-1\">\n" + "    <div class=\"row\">\n" + "         <div class=\"act_crit\">\n" + "              <button type=\"button\"\n" + "                      class=\"btn btn_ksn btn_ksn_delete btn_cl_black duplicate_btn duplicate-ic\"\n" + "                      onclick=\"addCondition()\" data-toggle=\"tooltip\" data-placement=\"top\"\n" + "                      title=\"Thêm sự kiện\">\n" + "                     <i class=\"duplicate_ic\"></i>\n" + "              </button>\n" + "              <button type=\"button\" class=\"btn btn_ksn btn_ksn_delete btn_cl_black delete-ic\"\n" + "                      data-toggle=\"tooltip\" data-placement=\"top\" title=\"Xóa sự kiện\">\n" + "                     <i class=\"delete_ic\"></i>\n" + "              </button>\n" + "          </div>\n" + "     </div>\n" + " </div>");
        html.append("</div>\n" + "</div>");
        return html.toString();
    }

    private String convertOperatorCalendarInput(JSONArray arrayOperator, String operator){

        StringBuilder option = new StringBuilder();
        for(int i = 0; i < arrayOperator.length(); i++){
            String op = arrayOperator.getString(i);
            if(op.equals("=")){
                if(operator.equals("=")) option.append("<option selected value=\"=\">Bằng</option>");
                else option.append("<option value=\"=\">Bằng</option>");
            }else if(op.equals(">")) {
                if(operator.equals(">")) option.append("<option selected value=\">\">Sau</option>");
                else option.append("<option value=\">\">Lớn hơn</option>");
            }else if(op.equals("<")){
                if(operator.equals("<")) option.append("<option selected value=\"<\">trước</option>");
                else option.append("<option value=\"<\">Nhỏ hơn</option>");
            }else if(op.equals("><")){
                if(operator.equals("><")) option.append("<option selected value=\"><\">Trong khoảng thời gian</option>");
                else option.append("<option value=\"><\">Trong khoảng thời gian</option>");
            }
        }

        return option.toString();
    }

    private String convertOperatorTextInput(JSONArray arrayOperator, String operator){

        StringBuilder option = new StringBuilder();
        for(int i = 0; i < arrayOperator.length(); i++){
            String op = arrayOperator.getString(i);
            if(op.equals("=")){
                if(operator.equals("=")) option.append("<option selected value=\"=\">Bằng</option>");
                else option.append("<option value=\"=\">Bằng</option>");
            }else if(op.equals(">")) {
                if(operator.equals(">")) option.append("<option selected value=\">\">Lớn hơn</option>");
                else option.append("<option value=\">\">Lớn hơn</option>");
            }else if(op.equals("<")){
                if(operator.equals("<")) option.append("<option selected value=\"<\">Nhỏ hơn</option>");
                else option.append("<option value=\"<\">Nhỏ hơn</option>");
            }else if(op.equals("><")){
                if(operator.equals("><")) option.append("<option selected value=\"><\">Trong khoảng</option>");
                else option.append("<option value=\"><\">Trong khoảng</option>");
            } else if(op.equals("!=")){
                if(operator.equals("!=")) option.append("<option selected value=\"!=\">Khác</option>");
                else option.append("<option value=\"!=\">Khác</option>");
            } else if(op.equals(">=")){
                if(operator.equals(">=")) option.append("<option selected value=\">=\">Lớn hơn hoặc bằng</option>");
                else option.append("<option value=\">=\">Lớn hơn hoặc bằng</option>");
            } else if(op.equals("<=")){
                if(operator.equals("<=")) option.append("<option selected value=\"<=\">Nhỏ hơn hoặc bằng</option>");
                else option.append("<option value=\"<=\">Nhỏ hơn hoặc bằng</option>");
            }
        }

        return option.toString();
    }
}
