package com.vivas.campaignx.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
public class CriteriaDTO {
    @JacksonXmlProperty(localName = "unit")
    private String unit;
    @JacksonXmlProperty(localName = "parentKey")
    private String parentKey;
    @JacksonXmlProperty(localName = "code")
    private String code;
    @JacksonXmlProperty(localName = "criteriaId")
    private Long criteriaId;
    @JacksonXmlProperty(localName = "operatorTime")
    private Integer operatorTime;
    @JacksonXmlProperty(localName = "name")
    private String name;
    @JacksonXmlProperty(localName = "startTime")
    private String startTime;
    @JacksonXmlProperty(localName = "position")
    private int position;
    @JacksonXmlProperty(localName = "endTime")
    private String endTime;
    @JacksonXmlProperty(localName = "value")
    private String value;
    @JacksonXmlProperty(localName = "valueEnd")
    private String valueEnd;
    @JacksonXmlProperty(localName = "key")
    private String key;
    @JacksonXmlProperty(localName = "operator")
    private String operator;
    @JacksonXmlProperty(localName = "serviceCode")
    private String serviceCode;
    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }
    public String getParentKey() {
        return parentKey;
    }
    public void setParentKey(String parentKey) {
        this.parentKey = parentKey;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }
    public Integer getOperatorTime() {
        return operatorTime;
    }
    public void setOperatorTime(Integer operatorTime) {
        this.operatorTime = operatorTime;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getStartTime() {
        return startTime;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }
    public String getEndTime() {
        return endTime;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getOperator() {
        return operator;
    }
    public void setOperator(String operator) {
        this.operator = operator;
    }
    public Long getCriteriaId() {
        return criteriaId;
    }
    public void setCriteriaId(Long criteriaId) {
        this.criteriaId = criteriaId;
    }

    public String getValueEnd() {
        return valueEnd;
    }

    public void setValueEnd(String valueEnd) {
        this.valueEnd = valueEnd;
    }

    @Override
    public String toString() {
        return "JsonDTO [unit=" + unit + ", parentKey=" + parentKey + ", code=" + code + ", criteriaId=" + criteriaId
                + ", operatorTime=" + operatorTime + ", name=" + name + ", startTime=" + startTime + ", position="
                + position + ", endTime=" + endTime + ", value=" + value + ", key=" + key + ", operator=" + operator
                + "]";
    }

    public String getValue1(){
        String result = "";
        if(operatorTime == 1) {
            result = name + " " + parseOperator(operator) + " " + parseValue(value, valueEnd) + " " + unit + " trong 60 ngày gần nhất ";
        }else if(operatorTime == 2){
            result = name + " " + parseOperator(operator) + " " + parseValue(value, valueEnd) + " " + unit + " trong 30 ngày gần nhất";
        }else if(operatorTime == 3){
            result = name + " " + parseOperator(operator) + " " + parseValue(value, valueEnd) + " " + unit + " trong 7 ngày gần nhất";
        }else if(operatorTime == 4){
            result = name + " " + parseOperator(operator) + " " + parseValue(value, valueEnd) + " " + unit + " trong tháng hiện tại";
        }else if(operatorTime == 5){
            result = name + " " + parseOperator(operator) + " " + parseValue(value, valueEnd) + " " + unit + " trong tháng trước";
        }else if(operatorTime == 6){
            result = name + " " + parseOperator(operator) + " " + parseValue(value, valueEnd) + " " + unit + " trong khoảng thời gian từ " + startTime + " đến " + endTime;
        }
        return result;
    }
    public String getValue2(){
        return name + " " + parseOperator(operator) + " " + parseValue(value, valueEnd) + " " + unit;
    }

    public String getValue3(){
        return name + " " + parseValue(value, valueEnd);
    }

    public String getValue6(){
        String status = name + " " + parseListValue(value);
        String statusView = status.substring(0,status.length()-1);
        return statusView;

    }

    public String getValue4(){

        return name.substring(0,name.length() - 12)+ " " + parseOperator(operator) + " " + parseValue(value, valueEnd);
    }

    public String getValue7(){
        return  name + " " + parseOperator(operator) + " " + parseValue(value, valueEnd);
    }

    public String getValue5(){
        return name + " " + parseOperator(operator) + " " + parseValue(value, valueEnd) + " "+unit.toLowerCase()+" gói " + serviceCode;
    }

    public String parseOperator(String operator){
        String value = "";
        if(operator.equals("=")){
            value = "bằng";
        }else if(operator.equals("!=")){
            value = "không bằng";
        }else if(operator.equals("<")){
            value = "nhỏ hơn";
        }else if(operator.equals(">")){
            value = "lớn hơn";
        }else if(operator.equals(">=")){
            value = "lớn hơn hoặc bằng";
        }else if(operator.equals("<=")){
            value = "nhỏ hơn hoặc bằng";
        }else if(operator.equals("is")){
            value = "là";
        }else if(operator.equals("isnot")){
            value = "không là";
        }else if(operator.equals("using")){
            value = "đang sử dụng";
        }else if(operator.equals("notInUse")){
            value = "đang không sử dụng";
        }else if(operator.equals("><")){
            value = "trong khoảng";
        }
        return value;
    }
    public String parseValue(String value, String valueEnd){
        String result = "";
        if(value.equals("active")){
            result = "hoạt động";
        }else if(value.equals("lock1")){
            result = "khóa một chiều";
        }else if(value.equals("lock2")){
            result = "khóa hai chiều";
        }else if(value.equals("recall")){
            result = "thu hồi số";
        }else if(value.equals("PREPAID")){
            result = "trả trước";
        }else if(value.equals("POSTPAID")){
            result = "trả sau";
        }else{
            if(valueEnd != null && valueEnd.length() > 0){
                result = " từ " + this.value + " đến " + this.valueEnd;
            }
            else result = this.value;
        }
        return result;
    }

    public String parseListValue(String value){
        String[] valueOne = value.split(";");
        String result = "";
        for(int i = 0; i < valueOne.length; i++) {
            String valueTmp = valueOne[i];
            if(valueTmp.equals("recall")) result += " Thu hồi số,";
            else if(valueTmp.equals("lock2")) result += " Khóa 2 chiều,";
            else if(valueTmp.equals("lock1")) result += " Khóa 1 chiều,";
            else if(valueTmp.equals("active")) result += " Hoạt động,";
        }

        return result;
    }

}
