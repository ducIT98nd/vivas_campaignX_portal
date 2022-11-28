var eventConditionMap = new Map();

function validateStep2(){
    let check = true;

    let eventId = $("#event-id").val();
    if (eventId.length == 0){
        let message = comboboxRequiredErrorMessage("loại sự kiện");
        showTooltip(message, 'button-event-pick');
        check = false;
    } else {
        removeTooltipElementID('button-event-pick');
    }

    if ($("#event-condition-type").val() != 0){
        let listConditionValue = $(".condition-value");
        listConditionValue.each(function(index, element) {

            if (this.tagName == 'DIV'){
                return;
            }

            let localCheck = true;
            let value = element.value;
            let message = "";
            if (value.length == 0){
                if (element.classList.contains('condition-date')){
                    message = textRequiredErrorMessage("Ngày được chọn");
                } else {
                    message = textRequiredErrorMessage("Giá trị nhập");
                }

                localCheck = false;
            } else if (element.classList.contains('condition-date')) {
                const today = new Date().setHours(0, 0, 0, 0);
                let operator = $(element).closest(".div-data-input").find("select.select-operator.calendar").val();
                if (operator == "><"){
                    dateType = $(element).attr("data-order");
                    if(dateType == "start"){
                        let startDate = convertCalendarToDate(value);
                        if (startDate <= today) {
                            message = "Ngày bắt đầu không được nhỏ hơn hoặc bằng ngày hiện tại!";
                            localCheck = false;
                        }
                    } else if (dateType == "end") {
                        let endDate = convertCalendarToDate(value);
                        let startDateString = $(element).parent().find(".condition-date.condition-value:first").val();
                        let startDate = convertCalendarToDate(startDateString);

                        if (endDate <= today) {
                            message = "Ngày kết thúc không được nhỏ hơn hoặc bằng ngày hiện tại.";
                            localCheck = false;
                        } else if (startDate >= endDate) {
                            message = "Ngày bắt đầu không được lớn hơn hoặc bằng ngày kết thúc.";
                            localCheck = false;
                        }

                    }

                } else {
                    let startDate = convertCalendarToDate(value);
                    if (startDate <= today) {
                        message = "Ngày được chọn không được nhỏ hơn hoặc bằng ngày hiện tại.";
                        localCheck = false;
                    }
                }
            } else if (element.classList.contains('condition-number')) {
                const today = new Date().setHours(0, 0, 0, 0);
                let operator = $(element).closest(".div-data-input").find("select.select-operator.number").val();
                if (operator == "><"){
                    dateType = $(element).attr("data-order");
                    if (dateType == "end") {
                        let endNumber = value;
                        let startNumber = $(element).parent().find(".condition-number.condition-value:first").val();
                        if (Number(endNumber) <= Number(startNumber)) {
                            message = "Giá trị đầu không được lớn hơn giá trị cuối.";
                            localCheck = false;
                        }
                    }

                }
            }

            if (localCheck == true){
                if (this.tagName == 'SELECT'){
                    removeTooltipSelectPicker(element);
                } else {
                    removeTooltipElement(element);
                }
            } else {
                if (this.tagName == 'SELECT'){
                    showTooltipSelectpicker("Vui lòng chọn giá trị", element);
                } else {
                    showTooltipElement(message, element);
                }
                check = false;
            }
        });

    }

    let conditionRule = $("#event-condition-rule").val();
    //Trường hợp yêu cầu thỏa mãn tất cả điều kiện, các tiêu chí không được trùng nhau
    if (conditionRule == 2){
        let listConditionName = [];
        $("select.select-condition").each(function(index, element) {
            let name =  $(element).val();
            if (!listConditionName.includes(name)){
                listConditionName.push(name);
                removeTooltipSelectPicker(element);
            } else {
                let message = "Trường hợp yêu cầu thỏa mãn tất cả điều kiện, các tiêu chí không được trùng nhau.";
                showTooltipSelectpicker(message, element);
                $(element).parent().find("button.selectbox-condition").removeAttr('title');
                check = false;
            }
        });
    } else {
        $("select.select-condition").each(function(index, element) {
            removeTooltipSelectPicker(element);
        });
    }

    if (check == true){
        let listObject = [];
        let listCondition = $("#condition-container").find(".row_crit");
        $(listCondition).each(function(index, element) {

            let condition = new Object();
            let conditionName = $(element).find(".select-condition :selected").val();
            let operator = $(element).find(".select-operator :selected").val();
            let value = "";
            condition["conditionName"] = conditionName;
            condition["operator"] = operator;
            if (operator == "><") {
                $(element).find('.condition-value:not("div")').each(function(index, item) {
                    value += $(item).val() + "-";
                });
                value = value.substring(0, value.length - 1);
            } else {
                value = $(element).find('.condition-value:not("div")').val();
            }
            condition["value"] = value;
            listObject.push(condition);
        });

        //su kien tai khoan chính dưỡi ngưỡng, add thêm điều kiện < 1000 hoặc < 5000
        if (eventId == 2){
            let condition = new Object();
            condition["conditionName"] = "amount";
            condition["operator"] = "<";
            condition["value"] = $("#low-balance-amount").val();
            listObject.push(condition);
        }
        let jsonString = JSON.stringify(listObject);
        $("#event-condition").val(jsonString);
    } else {
        $("#event-condition").val("");
    }

    return check;
}

function pickEvent (eventId){

    $("#condition-container").empty();
    $('#event-condition-type  option[value="0"]').prop('selected', true);

    $.ajax({
        type: 'GET',
        url: '/event/get-data?eventId=' + eventId,
        traditional: true,
        dataType: 'JSON',
        async: false,
        success: function (response) {
            if (response) {
                console.log(response);
                let data = response;
                $("#button-event-pick").html(data.eventName);
                let eventConditionList = data.eventConditions;
                eventConditionMap.clear();
                eventConditionList.forEach(element => {
                    eventConditionMap.set(element.jsonField.toString(), element);
                });

                //Neu su kien la tai khoan duoi nguong, enable chon muc gia 1000/5000
                if (eventId == 2){
                    $("#div-low-balance-amount").show();
                    $("#low-balance-amount").prop("disabled", false);
                } else {
                    $("#div-low-balance-amount").hide();
                    $("#low-balance-amount").prop("disabled", true);
                }
                $("#event-id").val(eventId);
                $("#event-queue-name").val(data.eventQueueName);
                $("#event-condition-rule").val(0);
            }
        },
        error: function (e) {
            console.log(e);
        },
    })
}

$(document).ready(function () {
    $('#event-condition-rule').on('change', function() {
        let eventId = $("#event-id").val();
        let conditionContainer = $("#condition-container");
        if (eventId.length != 0 && (this.value == 1 || this.value == 2)){
            if (conditionContainer.find(".row_crit").length == 0){
                addCondition();
            }
        } else {
            conditionContainer.empty();
        }
    });

    //ham xoa dieu kien
    $('#condition-container').on('click', '.delete-ic', function() {
        let conditionContainer = $("#condition-container");
        let numberOfCondition = conditionContainer.find(".row_crit").length;
        //chi remove neu so luong dieu kien dang co > 1
        if (numberOfCondition > 1){
            //tim div class chua dieu kien de remove
            let conditionContainer = $(this).closest(".row_crit");
            $(conditionContainer).remove();
        }
    });

    //lua chon dieu kien
    $('#condition-container').on('change', 'select.select-condition', function() {
        let dataInput = getConditionDataInput(this.value);
        let divDataInput = $(this).closest(".div-data-input");
        let inputType = dataInput.param.type;

        // xoa input cu
        divDataInput.find(".div-param").remove();
        divDataInput.find(".div-operator").remove();

        //load operator
        let displayOperator = dataInput.operator.display;
        let htmlOperator = "<div class=\"col-lg-3 div-operator\">" +
            "  <div class=\"form-group\">";
        if (inputType == 'calendar'){
            htmlOperator += "     <select class=\"form-control selectpicker select-operator calendar\" data-dropup-auto=\"false\" data-live-search=\"true\">";
        } else  if (inputType ==  'number'){
            htmlOperator += "     <select class=\"form-control selectpicker select-operator number\" data-dropup-auto=\"false\"  data-live-search=\"true\">";
        } else {
            htmlOperator += "     <select class=\"form-control selectpicker select-operator\" data-dropup-auto=\"false\"  data-live-search=\"true\">";
        }
        htmlOperator += "     </select>" +
            "  </div>" +
            "</div>";
        let divOperator = $.parseHTML(htmlOperator);
        divDataInput.append($(divOperator));
        if (displayOperator == "false"){
            $(divOperator).hide();
        }

        //them option cho operator
        let selectboxOperator = $(divOperator).find('.select-operator');
        let operatorList = dataInput.operator.value;
        operatorList.forEach(element => {
            if (inputType ==  'calendar'){
                $(selectboxOperator).append(new Option(convertOperatorCalendarInput(element), element));
            } else {
                $(selectboxOperator).append(new Option(convertOperatorTextInput(element), element));
            }

        });
        selectboxOperator.selectpicker({
            style: 'selectbox-condition',
        });
        selectboxOperator.selectpicker('refresh');

        //load input
        let htmlParam = "<div class=\"col-lg-5 div-param\">" +
            "   <div class=\"form-group\">";
        if (inputType ==  'number'){
            htmlParam += "      <input type=\"number\" min='0' onpaste=\"return pastePositiveNumber(event);\" onkeypress=\"return keyPressPositiveNumber(event);\" style='width: 80%' class=\"form-control condition-value\" placeholder=\"Nhập giá trị\">" +
                "      <span style='font-size: 12px'>VND</span>";
        } else if (inputType ==  'text'){
            htmlParam += "      <input type=\"text\" class=\"form-control condition-value\" placeholder=\"Nhập giá trị\">";
        } else if (inputType ==  'combobox'){
            htmlParam += "     <select class=\"form-control\ condition-value selectpicker\" data-dropup-auto=\"false\" " +
                "data-live-search=\"true\" data-size=\"6\">";
            let valueList;
            if (dataInput.param.value == '[list-package]') {
                valueList = getListPackage(2);

                valueList.forEach(element => {
                    htmlParam += "     <option value=\"" + element.id + "\">" + element.packageName + "</option>";
                });
            } else {
                valueList = dataInput.param.value;
                for (const [key, value] of Object.entries(valueList)) {
                    htmlParam += "     <option value=\"" + key + "\">" + value + "</option>";
                }
            }
            htmlParam += "     </select>";
        } else if (inputType ==  'calendar'){
            htmlParam += "      <input class=\"form-control condition-date condition-value\" placeholder='Chọn ngày'>";
        }
        htmlParam += "   </div>" +
            "</div>";
        let divParam = $.parseHTML(htmlParam);
        divDataInput.append($(divParam));
        let selectboxParam = $(divParam).find(".selectpicker");
        let firstOption = $(divParam).find(".selectpicker option:first");
        if (selectboxParam.length > 0){
            selectboxParam.val(firstOption.val());
            selectboxParam.selectpicker('refresh');
        }
    });

    $('#condition-container').on('focus',".condition-date", function(){
        $(this).datepicker({format: 'dd/mm/yyyy', todayHighlight: true}).keypress(function(event) {
            event.preventDefault(); // prevent keyboard writing but allowing value deletion
        }).bind('paste',function(e) {
            e.preventDefault()
        });
    });

    $('#condition-container').on('change',".select-operator .calendar", function(){
        if (this.value == "><") {
            //Operator trong khoang thi doi 1 input calendar thanh 2 input
            let divDataInput = $(this).closest(".div-data-input");
            divDataInput.find(".div-param").remove();
            //load input
            let htmlParam = "<div class=\"col-lg-5 div-param\">" +
                "   <div class=\"form-group\">" +
                "      <input class=\"form-control condition-date condition-value\" data-order=\"start\" style='width: 40%' placeholder='Từ ngày'>" +
                "      <input class=\"form-control condition-date condition-value\" data-order=\"end\" style='width: 40%' placeholder='Đến ngày'>" +
                "   </div>" +
                "</div>";
            let divParam = $.parseHTML(htmlParam);
            divDataInput.append($(divParam));
        } else {
            let divDataInput = $(this).closest(".div-data-input");
            divDataInput.find(".div-param").remove();
            //load input
            let htmlParam = "<div class=\"col-lg-5 div-param\">" +
                "   <div class=\"form-group\">" +
                "      <input class=\"form-control condition-date condition-value\" placeholder='Chọn ngày'>" +
                "   </div>" +
                "</div>";
            let divParam = $.parseHTML(htmlParam);
            divDataInput.append($(divParam));
        }
    });

    $('#condition-container').on('change',".select-operator .number", function(){
        if (this.value == "><") {
            //Operator trong khoang thi doi 1 input number thanh 2 input
            let divDataInput = $(this).closest(".div-data-input");
            divDataInput.find(".div-param").remove();
            //load input
            let htmlParam = "<div class=\"col-lg-5 div-param\">" +
                "   <div class=\"form-group\">" +
                "      <input class=\"form-control condition-number condition-value\" onpaste=\"return pastePositiveNumber(event);\" type=\"number\" min='0'data-order=\"start\" style='width: 40%' placeholder='Từ' " +
                " onkeypress=\"return keyPressPositiveNumber(event);\" >" +
                "      <input class=\"form-control condition-number condition-value\" onpaste=\"return pastePositiveNumber(event);\" type=\"number\" min='0'data-order=\"end\" style='width: 40%' placeholder='Đến'" +
                " onkeypress=\"return keyPressPositiveNumber(event);\" >" +
                "      <span style='font-size: 12px'>VND</span>" +
                "   </div>" +
                "</div>";
            let divParam = $.parseHTML(htmlParam);
            divDataInput.append($(divParam));
        } else {
            let divDataInput = $(this).closest(".div-data-input");
            divDataInput.find(".div-param").remove();
            //load input
            let htmlParam = "<div class=\"col-lg-5 div-param\">" +
                "   <div class=\"form-group\">" +
                "      <input class=\"form-control condition-number condition-value\" style=\"width: 80%\" type=\"number\" min='0' " +
                " onkeypress=\"return keyPressPositiveNumber(event);\" placeholder='Nhập giá trị'onpaste=\"return pastePositiveNumber(event);\">" +
                "      <span style='font-size: 12px'>VND</span>" +
                "   </div>" +
                "</div>";
            let divParam = $.parseHTML(htmlParam);
            divDataInput.append($(divParam));
        }
    });
});

function getConditionDataInput(conditionField){
    let dataInput = JSON.parse(eventConditionMap.get(conditionField).dataInput);
    return dataInput;
}

function getListPackage(packageGroup) {
    let result;
    $.ajax({
        url: '/package-datas/find-active?packageGroup=' + packageGroup,
        type: 'get',
        dataType: 'json',
        async: false,
        success: function (response) {
            result = response;
        },
        error: function (e) {
            console.log(e);
        }
    });
    return result;
}

function convertOperatorTextInput (operator){
    switch (operator) {
        case "=":
            return "Bằng";
        case "!=":
            return "Không bằng";
        case ">":
            return "Lớn hơn";
        case "<":
            return "Nhỏ hơn";
        case ">=":
            return "Lớn hơn hoặc bằng";
        case "<=":
            return "Nhỏ hơn hoặc bằng";
        case "><":
            return "Trong khoảng";
        default:
            return "Unknown";
    }
}

function convertOperatorCalendarInput (operator){
    switch (operator) {
        case "=":
            return "Bằng";
        case ">":
            return "Sau";
        case "<":
            return "Trước";
        case "><":
            return "Trong khoảng thời gian";
        default:
            return "Unknown";
    }
}

function addCondition(){

    let conditionContainer = $("#condition-container");
    axios.get('/event/load-condition-selectbox')
        .then(function (response) {
            var domNodes = $.parseHTML(response.data);
            //add dieu kien moi
            $("#condition-container").append($(domNodes));

            //them option cho dieu kien
            let selectCondition = $(domNodes).find('.select-condition');
            console.log(eventConditionMap);
            for (const [key, condition] of eventConditionMap) {
                $(selectCondition).append(new Option(condition.name, condition.jsonField));
            }
            selectCondition.selectpicker({
                style: 'selectbox-condition'
            });
            selectCondition.selectpicker('refresh');
            selectCondition.prop('selected', 'selected').trigger('change');
        }).catch(function (error) {
        console.log(error);
    });

}