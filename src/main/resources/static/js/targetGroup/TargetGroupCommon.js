async function chonTieuChi(criteriaID, criteriaName, idElement, selectCriteriaID, currentNodeID) {
    $("#" + idElement).html(criteriaName + "<i style=\"float: right\" class=\"fa fa-angle-down\"></i>");
    //Xóa những điều kiện đã hiện ra
    var classNameCondition = "class-" + selectCriteriaID;
    $("." + classNameCondition).remove();
    $("#buttonId_" + currentNodeID).removeAttr("title");
    $("#buttonId_" + currentNodeID).parent().removeClass("has-danger-1");

    await axios.get('/TargetGroupController/GetCriteriaFormat?id=' + criteriaID)
        .then(function (response) {
            console.log(response.data);
            var obj = response.data;
            if(criteriaID == 3 || criteriaID == 4 || criteriaID == 5 || criteriaID == 56 || criteriaID == 60){
                criteriaTree.find(currentNodeID).unit = "";
            }
            else criteriaTree.find(currentNodeID).unit = obj.unit;
            criteriaTree.find(currentNodeID).criteriaName = criteriaName;
            criteriaTree.find(currentNodeID).criteriaCode = obj.code;
            criteriaTree.find(currentNodeID).criteriaId = criteriaID;
            var htmlField = "";
            if (criteriaID == 1 || criteriaID == 2 || criteriaID == 6 || criteriaID == 7 || criteriaID == 65 || criteriaID == 66) {
                htmlField += "<div class=\"col-lg-3 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                    "                        <select id='operator_"+currentNodeID+"' name='"+obj.code+"' onchange='changeOperator(this, "+currentNodeID+")' class=\"custom-select form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n" +
                    "                            <option value=\"=\">Bằng</option>\n" +
                    "                            <option value=\"!=\">Không bằng</option>\n" +
                    "                            <option value=\"<\">Nhỏ hơn</option>\n" +
                    "                            <option value=\">\">Lớn hơn</option>\n" +
                    "                            <option value=\"<=\">Nhỏ hơn hoặc bằng</option>\n" +
                    "                            <option value=\">=\">Lớn hơn hoặc bằng</option>\n" +
                    "                            <option value=\"><\">Trong khoảng</option>\n" +
                    "                        </select>\n" +
                    "                    </div>";

                if(obj.unit == 'Phút' || obj.unit == 'MB'){
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input id='value_"+currentNodeID+"' ondrop=\"return false;\" autocomplete=\"off\" onpaste=\"return false;\" step=\"any\" type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+currentNodeID+"\" style=\"padding-left: 0px !important; display: none; padding-right: 0px !important;\">\n" +
                        "                        <input id='value_end_"+currentNodeID+"' ondrop=\"return false;\" autocomplete=\"off\" onpaste=\"return false;\" step=\"any\" type='number' name='' class=\"form-control\"  onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                }else {
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input id='value_"+currentNodeID+"' ondrop=\"return false;\" autocomplete=\"off\" onpaste=\"return false;\" type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+currentNodeID+"\" style=\"padding-left: 0px !important; display: none; padding-right: 0px !important;\">\n" +
                        "                        <input id='value_end_"+currentNodeID+"' ondrop=\"return false;\" autocomplete=\"off\" onpaste=\"return false;\" type='number' name='' class=\"form-control\"  onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                }
                htmlField += "<div class=\"col-lg-2 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                    "                        <input id='unit_"+currentNodeID+"'  name='' class=\"form-control\" disabled placeholder=\" " + obj.unit + " \"/>\n" +
                    "                    </div>";
                $("#" + selectCriteriaID).after(htmlField);
                $("#operator_"+currentNodeID).selectpicker({
                    style: 'btn-operator'
                });
            } else if (criteriaID == 3 || criteriaID == 4) {
                if (criteriaID == 3) {
                    htmlField += "<div class=\"col-lg-9 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n"
                    htmlField += "<div class='row' style='margin-left: 30px'>";

                    htmlField += "<div class='col-lg-2 custom-control custom-checkbox float-right' style='padding-top: 10px;'>";
                    htmlField += "<label class='control-label'>" +
                        "<input type='checkbox' class='form-check-input value_"+currentNodeID+"' value='active'>" + "Hoạt động" +
                        "</label>";
                    htmlField += "</div>";

                    htmlField += "<div class='col-lg-2 custom-control custom-checkbox float-right' style='padding-top: 10px;'>";
                    htmlField += "<label class='control-label'>" +
                        "<input type='checkbox' class='form-check-input value_"+currentNodeID+"' value='lock1'>" + "Khóa 1 chiều" +
                        "</label>";
                    htmlField += "</div>";

                    htmlField += "<div class='col-lg-2 custom-control custom-checkbox float-right' style='padding-top: 10px;'>";
                    htmlField += "<label class='control-label'>" +
                        "<input type='checkbox' class='form-check-input value_"+currentNodeID+"' value='lock2'>" + "Khóa 2 chiều" +
                        "</label>";
                    htmlField += "</div>";

                    htmlField += "<div class='col-lg-2 custom-control custom-checkbox float-right' style='padding-top: 10px;'>";
                    htmlField += "<label class='control-label'>" +
                        "<input type='checkbox' class='form-check-input value_"+currentNodeID+"' value='recall'>" + "Thu hồi số" +
                        "</label>";
                    htmlField += "</div>";

                    htmlField += "</div>";
                    htmlField += "</div>";
                    $("#" + selectCriteriaID).after(htmlField);
                } else if (criteriaID == 4) {
                    htmlField += "<div class=\"col-lg-3 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                        "                        <select id='value_"+currentNodeID+"' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n" +
                        "                            <option value=\"PREPAID\">Trả trước</option>\n" +
                        "                            <option value=\"POSTPAID\">Trả sau</option>\n" +
                        "                        </select>\n" +
                        "                    </div>";

                    $("#" + selectCriteriaID).after(htmlField);
                    $("#value_"+currentNodeID).selectpicker({
                        style: 'btn-operator'
                    });
                }
            } else if (criteriaID == 5) {
                htmlField += "<div class=\"col-lg-3 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                    "                        <select id='operator_"+currentNodeID+"' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n" +
                    "                            <option value=\"is\">Là</option>\n" +
                    "                            <option value=\"isnot\">Không là</option>\n" +
                    "                        </select>\n" +
                    "                    </div>";

                $.ajax({
                    type: 'GET',
                    url: '/BigdataController/getListMainProduct',
                    traditional: true,
                    dataType: 'JSON',
                    async: false,
                    success: function (response) {
                        if (response) {
                            console.log(response);
                            if (response.code==0){
                                let data = response.data;
                                htmlField += "<div class=\"col-lg-3 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                                    "                        <select id='value_"+currentNodeID+"' name='' class=\"form-control selectpicker \" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n" ;
                                for(let i = 0; i < data.length; i++){
                                    htmlField +=  "                            <option value=\""+data[i]+"\">"+data[i]+"</option>\n";
                                }
                                htmlField +=   "                        </select>\n" +
                                    "                    </div>";
                            }
                        }
                    },
                    error: function (e) {
                        console.log(e);
                    },
                })

                $("#" + selectCriteriaID).after(htmlField);
                $("#operator_"+currentNodeID).selectpicker({
                    style: 'btn-operator'
                });
                $("#value_"+currentNodeID).selectpicker({
                    style: 'btn-operator'
                });

            } else if (criteriaID == 56) {
                htmlField += "<div class=\"col-lg-3 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                    "                        <select id='operator_"+currentNodeID+"' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n" +
                    "                            <option value=\"using\">Đang sử dụng</option>\n" +
                    "                            <option value=\"notInUse\">Đang không sử dụng</option>\n" +
                    "                        </select>\n" +
                    "                    </div>";

                htmlField += "<div class=\"col-lg-2 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                    "                        <input class=\"form-control\" disabled placeholder=\" gói DATA \"/>\n" +
                    "                    </div>";
                $.ajax({
                    url: '/package-datas/find-active?packageGroup=1',
                    type: 'get',
                    dataType: 'json',
                    async: false,
                    success: function (response) {
                        htmlField += "<div class=\"col-lg-3 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                            "                        <select id='value_"+currentNodeID+"' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n" +
                            "                            <option value=\"all\">Tất cả</option>\n";
                        response.forEach(element => {
                            htmlField +=  " <option value=\""+element.id+"\">"+element.packageName+"</option>\n";
                        });
                        htmlField +=   "                        </select>\n" +
                            "                    </div>";
                    },
                    error: function (e) {
                        console.log(e);
                    }
                });

                $("#" + selectCriteriaID).after(htmlField);
                $("#operator_"+currentNodeID).selectpicker({
                    style: 'btn-operator'
                });
                $("#value_"+currentNodeID).selectpicker({
                    style: 'btn-operator'
                });

            } else if(criteriaID == 57 || criteriaID == 58){
                htmlField += "<div class=\"col-lg-2 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                    "                        <select id='operator_"+currentNodeID+"' name='' class=\"form-control selectpicker\" onchange='changeOperator(this, "+currentNodeID+")' data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n" +
                    "                            <option value=\"=\">Bằng</option>\n" +
                    "                            <option value=\"!=\">Không bằng</option>\n" +
                    "                            <option value=\"<\">Nhỏ hơn</option>\n" +
                    "                            <option value=\">\">Lớn hơn</option>\n" +
                    "                            <option value=\"<=\">Nhỏ hơn hoặc bằng</option>\n" +
                    "                            <option value=\">=\">Lớn hơn hoặc bằng</option>\n" +
                    "                            <option value=\"><\">Trong khoảng</option>\n" +
                    "                        </select>\n" +
                    "                    </div>";

                if(obj.unit == 'Phút' || obj.unit == 'MB'){
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input id='value_"+currentNodeID+"' ondrop=\"return false;\" autocomplete=\"off\" onpaste=\"return false;\" step=\"any\" type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+currentNodeID+"\" style=\"padding-left: 0px !important; display: none; padding-right: 0px !important;\">\n" +
                        "                        <input id='value_end_"+currentNodeID+"' ondrop=\"return false;\" autocomplete=\"off\" onpaste=\"return false;\" step=\"any\" type='number' name='' class=\"form-control\"  onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                }else {
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input id='value_"+currentNodeID+"' ondrop=\"return false;\" autocomplete=\"off\" onpaste=\"return false;\" type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+currentNodeID+"\" style=\"padding-left: 0px !important; display: none; padding-right: 0px !important;\">\n" +
                        "                        <input id='value_end_"+currentNodeID+"' ondrop=\"return false;\" autocomplete=\"off\" onpaste=\"return false;\" type='number' name='' class=\"form-control\"  onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                }

                htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                    "                        <input id='' name='' class=\"form-control\" disabled placeholder=\" " + obj.unit + " \"/>\n" +
                    "                    </div>";

                $.ajax({
                    url: '/package-datas/find-active?packageGroup=1',
                    type: 'get',
                    dataType: 'json',
                    async: false,
                    success: function (response) {
                        htmlField += "<div class=\"col-lg-3 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                            "                        <select id='mainService_"+currentNodeID+"' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n";
                        response.forEach(element => {
                            htmlField +=  " <option value=\""+element.id+"\">"+element.packageName+"</option>\n";

                        });
                        htmlField +=   "                        </select>\n" +
                            "                    </div>";

                    },
                    error: function (e) {
                        console.log(e);
                    }
                });

                $("#" + selectCriteriaID).after(htmlField);
                $("#operator_"+currentNodeID).selectpicker({
                    style: 'btn-operator'
                });
                $("#mainService_"+currentNodeID).selectpicker({
                    style: 'btn-operator'
                });

            } else if (criteriaID == 60) {
                htmlField += "<div class=\"col-lg-3 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                    "                        <select id='operator_"+currentNodeID+"' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n" +
                    "                            <option value=\"using\">Đang sử dụng</option>\n" +
                    "                            <option value=\"notInUse\">Đang không sử dụng</option>\n" +
                    "                        </select>\n" +
                    "                    </div>";

                htmlField += "<div class=\"col-lg-2 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                    "                        <input class=\"form-control\" disabled placeholder=\" gói KMCB \"/>\n" +
                    "                    </div>";

                $.ajax({
                    url: '/package-datas/find-active?packageGroup=2',
                    type: 'get',
                    dataType: 'json',
                    async: false,
                    success: function (response) {
                        htmlField += "<div class=\"col-lg-3 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                            "                        <select id='value_"+currentNodeID+"' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n" +
                            "                            <option value=\"all\">Tất cả</option>\n";
                        response.forEach(element => {
                            htmlField +=  " <option value=\""+element.id+"\">"+element.packageName+"</option>\n";

                        });
                        htmlField +=   "                        </select>\n" +
                            "                    </div>";

                    },
                    error: function (e) {
                        console.log(e);
                    }
                });

                $("#" + selectCriteriaID).after(htmlField);
                $("#operator_"+currentNodeID).selectpicker({
                    style: 'btn-operator'
                });
                $("#value_"+currentNodeID).selectpicker({
                    style: 'btn-operator'
                });
            }  else if(criteriaID == 61 || criteriaID == 62){
                htmlField += "<div class=\"col-lg-2 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                    "                        <select id='operator_"+currentNodeID+"' name='' class=\"form-control selectpicker\" onchange='changeOperator(this, "+currentNodeID+")' data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n" +
                    "                            <option value=\"=\">Bằng</option>\n" +
                    "                            <option value=\"!=\">Không bằng</option>\n" +
                    "                            <option value=\"<\">Nhỏ hơn</option>\n" +
                    "                            <option value=\">\">Lớn hơn</option>\n" +
                    "                            <option value=\"<=\">Nhỏ hơn hoặc bằng</option>\n" +
                    "                            <option value=\">=\">Lớn hơn hoặc bằng</option>\n" +
                    "                            <option value=\"><\">Trong khoảng</option>\n" +
                    "                        </select>\n" +
                    "                    </div>";

                if(obj.unit == 'Phút' || obj.unit == 'MB'){
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input id='value_"+currentNodeID+"' ondrop=\"return false;\" autocomplete=\"off\" onpaste=\"return false;\" step=\"any\" type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+currentNodeID+"\" style=\"padding-left: 0px !important; display: none; padding-right: 0px !important;\">\n" +
                        "                        <input id='value_end_"+currentNodeID+"' ondrop=\"return false;\" autocomplete=\"off\" onpaste=\"return false;\" step=\"any\" type='number' name='' class=\"form-control\"  onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                }else {
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input id='value_"+currentNodeID+"' ondrop=\"return false;\" autocomplete=\"off\" onpaste=\"return false;\" type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+currentNodeID+"\" style=\"padding-left: 0px !important; display: none; padding-right: 0px !important;\">\n" +
                        "                        <input id='value_end_"+currentNodeID+"' ondrop=\"return false;\" autocomplete=\"off\" onpaste=\"return false;\" type='number' name='' class=\"form-control\"  onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                }

                htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                    "                        <input id='' name='' class=\"form-control\" disabled placeholder=\" " + obj.unit + " \"/>\n" +
                    "                    </div>";

                $.ajax({
                    url: '/package-datas/find-active?packageGroup=2',
                    type: 'get',
                    dataType: 'json',
                    async: false,
                    success: function (response) {
                        htmlField += "<div class=\"col-lg-3 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                            "                        <select id='mainService_"+currentNodeID+"' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n";
                        response.forEach(element => {
                            htmlField +=  " <option value=\""+element.id+"\">"+element.packageName+"</option>\n";

                        });
                        htmlField +=   "                        </select>\n" +
                            "                    </div>";

                    },
                    error: function (e) {
                        console.log(e);
                    }
                });

                $("#" + selectCriteriaID).after(htmlField);
                $("#operator_"+currentNodeID).selectpicker({
                    style: 'btn-operator'
                });
                $("#mainService_"+currentNodeID).selectpicker({
                    style: 'btn-operator'
                });
            }else {
                htmlField += "<div class=\"col-lg-2 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                    "                        <select id='operator_"+currentNodeID+"' name='' class=\"form-control selectpicker\" onchange='changeOperator(this, "+currentNodeID+")' data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n" +
                    "                            <option value=\"=\">Bằng</option>\n" +
                    "                            <option value=\"!=\">Không bằng</option>\n" +
                    "                            <option value=\"<\">Nhỏ hơn</option>\n" +
                    "                            <option value=\">\">Lớn hơn</option>\n" +
                    "                            <option value=\"<=\">Nhỏ hơn hoặc bằng</option>\n" +
                    "                            <option value=\">=\">Lớn hơn hoặc bằng</option>\n" +
                    "                            <option value=\"><\">Trong khoảng</option>\n" +
                    "                        </select>\n" +
                    "                    </div>";

                if(obj.unit == 'Phút' || obj.unit == 'MB'){
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input id='value_"+currentNodeID+"'ondrop=\"return false;\" autocomplete=\"off\"  onpaste=\"return false;\" step=\"any\" type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+currentNodeID+"\" style=\"padding-left: 0px !important; display: none; padding-right: 0px !important;\">\n" +
                        "                        <input id='value_end_"+currentNodeID+"' ondrop=\"return false;\" autocomplete=\"off\" onpaste=\"return false;\" step=\"any\"  type='number' name='' class=\"form-control\"  onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                }else {
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input id='value_"+currentNodeID+"' ondrop=\"return false;\" autocomplete=\"off\" onpaste=\"return false;\" type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+currentNodeID+"\" style=\"padding-left: 0px !important; display: none; padding-right: 0px !important;\">\n" +
                        "                        <input id='value_end_"+currentNodeID+"' ondrop=\"return false;\" autocomplete=\"off\" onpaste=\"return false;\" type='number' name='' class=\"form-control\"  onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                }

                htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                    "                        <input id='' name='' class=\"form-control\" disabled placeholder=\" " + obj.unit + " \"/>\n" +
                    "                    </div>";

                var setupDate = 'setupDate_' + selectCriteriaID;
                var idStartDate = 'setupStartDate_' + currentNodeID;
                var idEndDate = 'setupEndDate_' + currentNodeID;
                htmlField += "<div class=\"col-lg-2 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                    "                        <select id='operatorTime_"+currentNodeID+"' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\" style=\"padding-left: 0px !important; padding-right: 0px !important;\" data-placeholder=\"Chọn giá trị\" onchange=\"changeTime(this, '" + setupDate + "')\">\n" +
                    "                            <option value=\"1\">Trong 60 ngày gần nhất</option>\n" +
                    "                            <option value=\"2\">Trong 30 ngày gần nhất</option>\n" +
                    "                            <option value=\"3\">Trong 7 ngày gần nhất</option>\n" +
                    "                            <option value=\"4\">Trong tháng hiện tại</option>\n" +
                    "                            <option value=\"5\">Trong tháng trước</option>\n" +
                    "                            <option value=\"6\">Trong khoảng thời gian</option>\n" +
                    "                        </select>\n" +
                    "                    </div>";
                htmlField += "<div class=\"col-lg-2 " + classNameCondition + " \" style='display: none' id='"+setupDate+"'>\n";
                htmlField += "<div class=\"row \" >\n"
                htmlField += "<div class=\"col-lg-6 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                    "                        <input name='' id='"+idStartDate+"' ondrop=\"return false;\" autocomplete=\"off\" onpaste=\"return false;\" class=\"form-control\" readonly style=\"padding-left: 5px !important; padding-right: 0px !important;\" type=\"text\" placeholder='Từ ngày' id=\"example-date-input\">\n" +
                    "                    </div>";

                htmlField += "<div class=\"col-lg-6 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                    "                        <input  name='' id='"+idEndDate+"' ondrop=\"return false;\" autocomplete=\"off\" onpaste=\"return false;\" class=\"form-control\" readonly style=\"padding-left: 5px !important; padding-right: 0px !important;\" type=\"text\"  placeholder='Đến ngày' id=\"example-date-input\">\n" +
                    "                    </div>";
                htmlField += "</div>";
                htmlField += "</div>";
                $("#" + selectCriteriaID).after(htmlField);
                $("#operator_"+currentNodeID).selectpicker({
                    style: 'btn-operator'
                });
                $("#operatorTime_"+currentNodeID).selectpicker({
                    style: 'btn-operator'
                });

                $("#"+idStartDate).datepicker({
                    format: 'dd/mm/yyyy'
                }).keypress(function(event) {
                    event.preventDefault(); // prevent keyboard writing but allowing value deletion
                });

                $("#"+idStartDate).change(function () {
                    removeTooltipParamCriteria(this.id);
                });

                $("#"+idEndDate).datepicker({
                    format: 'dd/mm/yyyy'
                }).keypress(function(event) {
                    event.preventDefault(); // prevent keyboard writing but allowing value deletion
                });

                $("#"+idEndDate).change(function () {
                    removeTooltipParamCriteria(this.id);
                });
            }

        })

        .catch(function (error) {
            console.log(error);
        });
}

function clickSelectParam(e) {
    let cursorY = e.clientY;
    let height = screen.height;
    if(cursorY > height/2) $(".tieu_chi>.multi-level").css("bottom", "110%");
    else $(".tieu_chi>.multi-level").css("bottom", "");

}

function changeTime(obj, setupDate) {
    var value = $(obj).val();
    if (value == 6) {
        $("#" + setupDate).show();
    } else {
        $("#" + setupDate).hide();
    }
}

function addCriteria(obj, currentNodeID) {
    $("#buttonDelete_"+currentNodeID).removeAttr("disabled");
    criteriaIDSequence++;
    axios.get('/TargetGroupController/GetRowLevel1Criteria?currentId='+criteriaIDSequence + '&parentId=0')
        .then(function (response) {
            $("#" + obj).after(response.data);
        })
        .catch(function (error) {
            console.log(error);
        });
    criteriaTree.insert("0", criteriaIDSequence);
    /* console.log(criteriaTree);*/
}

function addCriteriaLevel2(objCurrent, obj, parentKey) {
    criteriaIDSequence++;
    if(criteriaTree.find(parentKey).children.length <= 0){
        $("#buttonAddBranch_" + parentKey).prop("disabled", true);
        axios.get('/TargetGroupController/GetRowLevel2Criteria?currentId='+criteriaIDSequence + '&parentId=' + parentKey)
            .then(function (response) {
                $("#" + obj).after(response.data);
                $("#condition_level2_of_"+parentKey).selectpicker({
                    style: 'btn-condition'
                });
            })
            .catch(function (error) {
                console.log(error);
            });
    }else {
        axios.get('/TargetGroupController/GetRowSecondLevel2Criteria?currentId='+criteriaIDSequence + '&parentId=' + parentKey)
            .then(function (response) {
                $("#" + obj).after(response.data);
            })
            .catch(function (error) {
                console.log(error);
            });
    }

    criteriaTree.insert(parentKey, criteriaIDSequence);
}

function addCriteriaLevel3(objCurrent, obj, parentKey) {
    criteriaIDSequence++;
    if(criteriaTree.find(parentKey).children.length <= 0) {
        $("#buttonAddBranch_" + parentKey).prop("disabled", true);
        axios.get('/TargetGroupController/GetRowLevel3Criteria?currentId='+criteriaIDSequence + '&parentId=' + parentKey)
            .then(function (response) {
                $("#" + obj).after(response.data);
                $("#condition_level3_of_"+parentKey).selectpicker({
                    style: 'btn-condition'
                });
            })
            .catch(function (error) {
                console.log(error);
            });
    }else {
        axios.get('/TargetGroupController/GetRowSecondLevel3Criteria?currentId='+criteriaIDSequence + '&parentId=' + parentKey)
            .then(function (response) {
                $("#" + obj).after(response.data);
            })
            .catch(function (error) {
                console.log(error);
            });
    }
    criteriaTree.insert(parentKey, criteriaIDSequence);
}


function deleteCriteria(obj1, obj2, currentNodeID) {
    $("#" + obj1).remove();
    $("#" + obj2).remove();
    let nodeParent = criteriaTree.find(currentNodeID).parent;
    criteriaTree.remove(currentNodeID);
    if(nodeParent.level == null && nodeParent.children.length == 1){
        let nodeChild = nodeParent.children[0];
        $("#buttonDelete_"+nodeChild.key).attr("disabled", "disabled");
    }

    if(nodeParent.level == 1){
        let arrayChildsLv2 = nodeParent.children;
        if(arrayChildsLv2.length == 0){
            $("#buttonAddBranch_"+nodeParent.key).removeAttr("disabled");
            $("#div_condition_level2_of_" + nodeParent.key).remove();
        }
    }

    if(nodeParent.level == 2){
        let arrayChildsLv3 = nodeParent.children;
        if(arrayChildsLv3.length == 0){
            $("#buttonAddBranch_"+nodeParent.key).removeAttr("disabled");
            $("#div_condition_level3_of_" + nodeParent.key).remove();
        }
    }
}

function getHashValue(currentNode){
    let hashValue = null;
    if(currentNode.criteriaId == 1 || currentNode.criteriaId == 2 || currentNode.criteriaId == 3 || currentNode.criteriaId == 4 || currentNode.criteriaId == 6 || currentNode.criteriaId == 7){
        hashValue = currentNode.criteriaCode;
    }else if(currentNode.criteriaId == 5){
        let value = $("#value_" + currentNode.key).val();
        hashValue = currentNode.criteriaCode + "_" + value;
    }else if(currentNode.criteriaId == 56 || currentNode.criteriaId == 60 ){
        let serviceCode = $("#value_" + currentNode.key).val();
        hashValue = currentNode.criteriaCode + "_" + serviceCode;

    }else if(currentNode.criteriaId == 57 || currentNode.criteriaId == 58 || currentNode.criteriaId == 61 || currentNode.criteriaId == 62){
        let serviceCode = $("#mainService_" + currentNode.key).val();
        hashValue = currentNode.criteriaCode + "_" + serviceCode;
    } else {
        let opTime = $("#operatorTime_"+currentNode.key).val();
        if(opTime == 6){
            let sTime = $("#setupStartDate_" + currentNode.criteriaId).val();
            let eTime = $("#setupEndDate_" + currentNode.criteriaId).val()
            hashValue = currentNode.criteriaCode + "-" + sTime + "-" + eTime;
        }else {
            hashValue = currentNode.criteriaCode + "-" + opTime;
        }
    }
    return hashValue;
}



function submitCriteriaSetup(){

    try {
        let flag = true;
        let stackLevel3 = new Stack();
        let stackLevel2 = new Stack();

        let mapLevel1 = new Map();
        let setValueLv1 = new Set();

        let setValueLv3 = new Set();
        let setNodeLevel1 = new Set();

        //Validate
        let checkCriteriaIsNull = 0;
        for( let currentNode of criteriaTree.preOrderTraversal()){
            if(currentNode.key != 0){
                let b = $("#buttonId_" + currentNode.key).text().trim();
                if(b == 'Chọn tham số'){
                    let message = "Vui lòng chọn tham số.";
                    showToolipCriteria(message, "buttonId_"+currentNode.key);
                    checkCriteriaIsNull ++;
                }else {
                    removeTooltipParamCriteria("buttonId_"+currentNode.key);
                    let boolCheckValue = true;
                    if (currentNode.criteriaId != 3 && currentNode.criteriaId != 4 && currentNode.criteriaId != 5 && currentNode.criteriaId != 56 && currentNode.criteriaId != 60) {
                        let value = $("#value_" + currentNode.key).val();
                        if (value == null || value.length <= 0) {
                            boolCheckValue = false;
                            let message = "Giá trị nhập không được để trống.";
                            showToolipParamCriteria(message, "value_" + currentNode.key);
                        }
                        let operator = $("#operator_" + currentNode.key).val();
                        if(operator == '><'){
                            let valueEnd = $("#value_end_" + currentNode.key).val();
                            if(valueEnd == null || valueEnd.length <= 0){
                                boolCheckValue = false;
                                let message = "Giá trị nhập không được để trống.";
                                showToolipParamCriteria(message, "value_end_" + currentNode.key);
                            }else {
                                if(parseInt(value) > parseInt(valueEnd)){
                                    boolCheckValue = false;
                                    let message = "Giá trị đầu không được lớn hơn giá trị cuối.";
                                    showToolipParamCriteria(message, "value_end_" + currentNode.key);
                                    showToolipParamCriteria(message, "value_" + currentNode.key);
                                }
                            }
                        }
                    } else if(currentNode.criteriaId == 3) {
                        let value = "";
                        $(".value_" + currentNode.key + ":checkbox:checked").each(function () {
                            value += $(this).val();
                            value += ";"
                        });
                        if (value == null || value.length <= 0) {
                            boolCheckValue = false;
                            let message = "Vui lòng chọn trạng thái.";
                            showToolipCriteria(message, "buttonId_" + currentNode.key);
                        }
                    }
                    if(flag) flag = boolCheckValue;
                    if(boolCheckValue){
                        removeTooltipParamCriteria("value_end_" + currentNode.key);
                        removeTooltipParamCriteria("value_" + currentNode.key);
                    }

                    let boolCheckTime = checkStartDateAndEndDate(currentNode.criteriaId, currentNode.key);
                    if(flag) flag = boolCheckTime;
                }
                if(currentNode.level == 1) setNodeLevel1.add(currentNode);
            }
        }
        if(checkCriteriaIsNull > 0) flag = false;
        else {
            let conditionLv1 = $("#conditionLevel1").val();
            let mapValidData = new Map();
            for( let currentNode of criteriaTree.preOrderTraversal()){
                if(currentNode.key != 0) {
                    let hashValue = getHashValue(currentNode);
                    if (!mapValidData.get(hashValue)) { // nếu map chưa có key => thêm key, value vào map
                        mapValidData.set(hashValue, currentNode);
                    } else { // nếu map có key => lấy value theo key và kiểm tra
                        let nodeOld = mapValidData.get(hashValue);
                        if (nodeOld.level == 1 && currentNode.level == 1 && conditionLv1 == "AND"){ // trường hợp 2 node đồng cấp level 1
                            flag = false;
                            let message = "Trường hợp yêu cầu thỏa mãn tất cả điều kiện, các tiêu chí đồng cấp không được trùng nhau.";
                            showToolipCriteria(message, "buttonId_"+currentNode.key);
                        }else {
                            if(flag) removeTooltipParamCriteria("buttonId_"+currentNode.key);

                            if(nodeOld.level == currentNode.level && currentNode.level != 1){ // trường hợp 2 node đồng cấp nhưng không phải level 1
                                if(nodeOld.parent.key == currentNode.parent.key){ // trường hợp chung parent (chung nhánh)
                                    let condition = null;
                                    if(currentNode.parent.level == 1) condition = $("#condition_level2_of_" + currentNode.parent.key).val();
                                    else if(currentNode.parent.level == 2) condition = $("#condition_level3_of_" + currentNode.parent.key).val();
                                    if(condition == "AND"){
                                        flag = false;
                                        let message = "Trường hợp yêu cầu thỏa mãn tất cả điều kiện, các tiêu chí đồng cấp không được trùng nhau.";
                                        showToolipCriteria(message, "buttonId_"+currentNode.key);
                                    }
                                }else { // trường hợp khác nhánh thì tìm đến level cao hơn
                                    let levelOfNode = null;
                                    let keyOfNode = null;
                                    let condition = null;
                                    let nodeParentOfNodeOld = nodeOld.parent;
                                    let nodeParentOfCurrentNode = currentNode.parent;
                                    while(true){
                                        if(nodeParentOfNodeOld.parent.key == nodeParentOfCurrentNode.parent.key){
                                            levelOfNode = nodeParentOfCurrentNode.parent.level;
                                            keyOfNode = nodeParentOfCurrentNode.parent.key;
                                            break;
                                        }else {
                                            nodeParentOfNodeOld = nodeParentOfNodeOld.parent;
                                            nodeParentOfCurrentNode = nodeParentOfCurrentNode.parent;
                                        }
                                    }
                                    if(keyOfNode == 0) condition = $("#conditionLevel1").val();
                                    else if(levelOfNode == 1) condition = $("#condition_level2_of_" + keyOfNode).val();
                                    else if(levelOfNode == 2) condition = $("#condition_level3_of_" + keyOfNode).val();
                                    if(condition == "AND"){
                                        flag = false;
                                        let message = "Trường hợp yêu cầu thỏa mãn tất cả điều kiện của tiêu chí cha hoặc tiêu chí cao hơn, các tiêu chí con khác nhánh không được trùng nhau.";
                                        showToolipCriteria(message, "buttonId_"+currentNode.key);
                                    }
                                }
                            }
                            else if(nodeOld.level != currentNode.level){
                                if(nodeOld.level == 1 && currentNode.level == 2){
                                    let condition = $("#conditionLevel1").val();
                                    if(nodeOld.key == currentNode.parent.key) {
                                        flag = false;
                                        let message = "Tiêu chí con không được trùng với tiêu chí cha và cấp cao hơn.";
                                        showToolipCriteria(message, "buttonId_"+currentNode.key);
                                    }else {
                                        if(condition == "AND"){
                                            flag = false;
                                            let message = "Trường hợp yêu cầu thỏa mãn tất cả điều kiện của tiêu chí cha, tiêu chí con không được trùng với tiêu chí cha và các tiêu chí đồng cấp của cha (dù cùng hay khác nhánh).";
                                            showToolipCriteria(message, "buttonId_"+currentNode.key);
                                        }
                                    }
                                }
                                else if(nodeOld.level == 2 && currentNode.level == 1){
                                    let condition = $("#conditionLevel1").val();
                                    if(nodeOld.parent.key == currentNode.key) {
                                        flag = false;
                                        let message = "Tiêu chí con không được trùng với tiêu chí cha và cấp cao hơn.";
                                        showToolipCriteria(message, "buttonId_"+nodeOld.key);
                                    }else {
                                        if(condition == "AND") {
                                            flag = false;
                                            let message = "Trường hợp yêu cầu thỏa mãn tất cả điều kiện của tiêu chí cha, tiêu chí con không được trùng với tiêu chí cha và các tiêu chí đồng cấp của cha (dù cùng hay khác nhánh).";
                                            showToolipCriteria(message, "buttonId_" + nodeOld.key);
                                        }
                                    }
                                }
                                else if(nodeOld.level == 2 && currentNode.level == 3){
                                    if(currentNode.parent.key == nodeOld.key) { // trường hợp cùng nhánh level 2
                                        flag = false;
                                        let message = "Tiêu chí con không được trùng với tiêu chí cha và cấp cao hơn.";
                                        showToolipCriteria(message, "buttonId_"+currentNode.key);
                                    }else if(currentNode.parent.key != nodeOld.key){
                                        if(currentNode.parent.parent.key == nodeOld.parent.key){// trường hợp cùng nhánh level 1
                                            let condition = $("#condition_level2_of_" + nodeOld.parent.key).val();
                                            if(condition == "AND"){
                                                flag = false;
                                                let message = "Trường hợp yêu cầu thỏa mãn tất cả điều kiện của tiêu chí cha, tiêu chí con không được trùng với tiêu chí cha và các tiêu chí đồng cấp của cha (dù cùng hay khác nhánh).";
                                                showToolipCriteria(message, "buttonId_"+currentNode.key);
                                            }
                                        }else if(currentNode.parent.parent.key != nodeOld.parent.key){
                                            let condition = $("#conditionLevel1").val();
                                            if(condition == "AND"){
                                                flag = false;
                                                let message = "Trường hợp yêu cầu thỏa mãn tất cả điều kiện của tiêu chí cha và tiêu chí cao hơn, các tiêu chí con không được trùng với tiêu chí cha và các tiêu chí đồng cấp của cha (dù cùng hay khác nhánh). Cũng như với cấp cao hơn.";
                                                showToolipCriteria(message, "buttonId_"+currentNode.key);
                                            }
                                        }
                                    }
                                }else if(nodeOld.level == 3 && currentNode.level == 2){
                                    if(nodeOld.parent.key == currentNode.key) { // trường hợp cùng nhánh level 2
                                        flag = false;
                                        let message = "Tiêu chí con không được trùng với tiêu chí cha và cấp cao hơn.";
                                        showToolipCriteria(message, "buttonId_"+nodeOld.key);
                                    }else if(nodeOld.parent.key != currentNode.key){
                                        if(nodeOld.parent.parent.key == currentNode.parent.key){// trường hợp cùng nhánh level 1
                                            let condition = $("#condition_level2_of_" + currentNode.parent.key).val();
                                            if(condition == "AND"){
                                                flag = false;
                                                let message = "Trường hợp yêu cầu thỏa mãn tất cả điều kiện của tiêu chí cha, tiêu chí con không được trùng với tiêu chí cha và các tiêu chí đồng cấp của cha (dù cùng hay khác nhánh).";
                                                showToolipCriteria(message, "buttonId_"+nodeOld.key);
                                            }

                                        }else if(nodeOld.parent.parent.key != currentNode.parent.key){
                                            let condition = $("#conditionLevel1").val();
                                            if(condition == "AND"){
                                                flag = false;
                                                let message = "Trường hợp yêu cầu thỏa mãn tất cả điều kiện của tiêu chí cha và tiêu chí cao hơn, các tiêu chí con không được trùng với tiêu chí cha và các tiêu chí đồng cấp của cha (dù cùng hay khác nhánh). Cũng như với cấp cao hơn.";
                                                showToolipCriteria(message, "buttonId_"+nodeOld.key);
                                            }
                                        }
                                    }
                                }else if(nodeOld.level == 1 && currentNode.level == 3){
                                    if(currentNode.parent.parent.key == nodeOld.key){
                                        flag = false;
                                        let message = "Tiêu chí con không được trùng với tiêu chí cha và cấp cao hơn.";
                                        showToolipCriteria(message, "buttonId_"+currentNode.key);
                                    }else if(currentNode.parent.parent.key != nodeOld.key) {
                                        let condition = $("#conditionLevel1").val();
                                        if(condition == "AND"){
                                            flag = false;
                                            let message = "Trường hợp yêu cầu thỏa mãn tất cả điều kiện của tiêu chí cha và tiêu chí cao hơn, các tiêu chí con không được trùng với tiêu chí cha và các tiêu chí đồng cấp của cha (dù cùng hay khác nhánh). Cũng như với cấp cao hơn.";
                                            showToolipCriteria(message, "buttonId_"+currentNode.key);
                                        }
                                    }
                                }else if(nodeOld.level == 3 && currentNode.level == 1){
                                    if(nodeOld.parent.parent.key == currentNode.key){
                                        flag = false;
                                        let message = "Tiêu chí con không được trùng với tiêu chí cha và cấp cao hơn.";
                                        showToolipCriteria(message, "buttonId_"+nodeOld.key);
                                    }else if(nodeOld.parent.parent.key != currentNode.key) {
                                        let condition = $("#conditionLevel1").val();
                                        if(condition == "AND") {
                                            flag = false;
                                            let message = "Trường hợp yêu cầu thỏa mãn tất cả điều kiện của tiêu chí cha và tiêu chí cao hơn, các tiêu chí con không được trùng với tiêu chí cha và các tiêu chí đồng cấp của cha (dù cùng hay khác nhánh). Cũng như với cấp cao hơn.";
                                            showToolipCriteria(message, "buttonId_" + nodeOld.key);
                                        }
                                    }
                                }
                            }
                        }
                        if(flag) mapValidData.set(hashValue, currentNode);
                    }
                }
            }
        }
        if(flag) {
            let finaldt = {
                condition: "",
                childs: []
            };
            //duyệt cây
            let condition = $("#conditionLevel1").val();
            finaldt.condition = condition;
            for( let currentNode of criteriaTree.postOrderTraversal()){
                console.log("currentNodeLevel: "+currentNode.level +" currentNodeKey: " + currentNode.key + " currentNodeName: " + currentNode.criteriaName + " currentNodeCriteriaId: " + currentNode.criteriaId);
                if(currentNode.level == 1){
                    let objDataLevel1 = {
                        condition: "",
                        data: "",
                        childs: []
                    };
                    //Khởi tạo obj của dòng hiện tại
                    let objtemp = createObj(currentNode.criteriaId, currentNode.position, currentNode.criteriaCode, currentNode.criteriaName, currentNode.level, currentNode.parent.key, currentNode.key, currentNode.unit);
                    objDataLevel1.data = objtemp;
                    //lấy các dòng con ở trong stack
                    objDataLevel1.condition = $("#condition_level2_of_"+currentNode.key).val();
                    while(!stackLevel2.isEmpty()){
                        let objstackLv2 = stackLevel2.pop();
                        objDataLevel1.childs.push(objstackLv2);
                    }
                    //add vào obj tổng
                    finaldt.childs.push(objDataLevel1);

                }else if(currentNode.level == 2){
                    let objDataLevel2 = {
                        condition: "",
                        data: "",
                        childs: []
                    };
                    //Khởi tạo obj của dòng hiện tại
                    let objtemp = createObj(currentNode.criteriaId, currentNode.position, currentNode.criteriaCode, currentNode.criteriaName, currentNode.level, currentNode.parent.key, currentNode.key, currentNode.unit);
                    objDataLevel2.data = objtemp;
                    //lấy các dòng con ở trong stack
                    objDataLevel2.condition = $("#condition_level3_of_"+currentNode.key).val();
                    while(!stackLevel3.isEmpty()){
                        let objstackLv3 = stackLevel3.pop();
                        objDataLevel2.childs.push(objstackLv3);
                    }
                    stackLevel2.push(objDataLevel2);

                }else if(currentNode.level == 3){
                    let objDataLevel3 = {
                        data: ""
                    };
                    //Khởi tạo obj của dòng hiện tại
                    let objData = createObj(currentNode.criteriaId, currentNode.position, currentNode.criteriaCode, currentNode.criteriaName, currentNode.level, currentNode.parent.key, currentNode.key, currentNode.unit);
                    objDataLevel3.data = objData;
                    stackLevel3.push(objDataLevel3);
                }
            }
            console.log("finalData: " + JSON.stringify(finaldt));
            let json = JSON.stringify(finaldt)
            $("#jsonData").val(json);
        }
        return flag;
    } catch (e){
        return false;
    }
}

function createObj(criteriaID, pos, code, name, level, parentKey, key, unit){
    const objLevel3 = {
        key: "",
        parentKey: "",
        criteriaId: "",
        position: "",
        code: "",
        name: "",
        operator: "",
        value: "",
        valueEnd: "",
        operatorTime: "",
        startTime: "",
        endTime: "",
        unit: "",
        serviceCode: ""
    };
    objLevel3.parentKey = parentKey;
    objLevel3.key = key;
    objLevel3.criteriaId = criteriaID;
    objLevel3.code = code;
    objLevel3.name = name;
    objLevel3.position = pos;
    objLevel3.unit = unit;
    if (criteriaID == 1 || criteriaID == 2 || criteriaID == 6 || criteriaID == 7 || criteriaID == 65 || criteriaID == 66) {
        let strOperator = $("#operator_"+key).val();
        objLevel3.operator = strOperator;
        objLevel3.value = $("#value_"+key).val();
        if(strOperator == '><') objLevel3.valueEnd = $("#value_end_"+key).val();
    } else if (criteriaID == 3) {
        let value = "";
        $(".value_"+key+":checkbox:checked").each(function () {
            value += $(this).val();
            value += ";"
        });
        objLevel3.value = value.substring(0, value.length - 1);
    }  else if (criteriaID == 4) {
        objLevel3.value = $("#value_"+key).val();
    } else if (criteriaID == 5) {
        objLevel3.operator = $("#operator_"+key).val();
        objLevel3.value = $("#value_"+key).val();
    } else if (criteriaID == 56) {
        objLevel3.operator = $("#operator_"+key).val();
        objLevel3.value = $("#value_"+key).val();
    } else if (criteriaID == 60) {
        objLevel3.operator = $("#operator_"+key).val();
        objLevel3.value = $("#value_"+key).val();
    } else if(criteriaID == 57 || criteriaID == 58){
        let strOperator = $("#operator_"+key).val();
        objLevel3.operator = strOperator;
        objLevel3.value = $("#value_"+key).val();
        if(strOperator == '><') objLevel3.valueEnd = $("#value_end_"+key).val();
        objLevel3.serviceCode = $("#mainService_"+key).val();
    } else if(criteriaID == 61 || criteriaID == 62){
        let strOperator = $("#operator_"+key).val();
        objLevel3.operator = strOperator;
        objLevel3.value = $("#value_"+key).val();
        if(strOperator == '><') objLevel3.valueEnd = $("#value_end_"+key).val();
        objLevel3.serviceCode = $("#mainService_"+key).val();
    } else {
        let strOperator = $("#operator_"+key).val();
        objLevel3.operator = strOperator;
        objLevel3.value = $("#value_"+key).val();
        if(strOperator == '><') objLevel3.valueEnd = $("#value_end_"+key).val();
        objLevel3.operatorTime = $("#operatorTime_"+key).val();
        objLevel3.startTime = $("#setupStartDate_"+key).val();
        objLevel3.endTime = $("#setupEndDate_"+key).val();
    }
    return objLevel3;
}

function checkStartDateAndEndDate(criteriaID, key){
    let checkTime = true;
    if (criteriaID == 1 || criteriaID == 2 || criteriaID == 6 || criteriaID == 7 || criteriaID == 3 || criteriaID == 4 || criteriaID == 5 || criteriaID == 56 || criteriaID == 60 || criteriaID == 57 || criteriaID == 58 || criteriaID == 61 || criteriaID == 62 || criteriaID == 65 || criteriaID == 66) {

    } else {
        let operatorTime = $("#operatorTime_"+key).val();
        if(operatorTime == 6){
            let startTime = $("#setupStartDate_"+key).val();
            if(startTime == null || startTime.length <= 0){
                showToolipCriteria("Thời điểm bắt đầu không được để trống.", "setupStartDate_"+key);
                checkTime = false;
            } else {
                removeTooltipParamCriteria("setupStartDate_" + key);
            }
            let endTime = $("#setupEndDate_"+key).val();
            if(endTime == null || endTime.length <= 0){
                showToolipCriteria("Thời điểm kết thúc không được để trống.", "setupEndDate_"+key);
                checkTime = false;
            }else {
                removeTooltipParamCriteria("setupEndDate_" + key);
            }
            if(checkTime){
                var scheStart = $("#setupStartDate_"+key).val();
                scheStart = scheStart.replace(new RegExp("/", 'g'), " ");
                scheStart = scheStart.split(" ");

                var scheStartDate = new Date(scheStart[2], scheStart[1] - 1, scheStart[0]);

                var scheEnd = $("#setupEndDate_"+key).val();
                scheEnd = scheEnd.replace(new RegExp("/", 'g'), " ");
                scheEnd = scheEnd.split(" ");
                var scheEndDate = new Date(scheEnd[2], scheEnd[1] - 1, scheEnd[0]);

                if (scheStartDate > scheEndDate) {
                    checkTime = false;
                    showToolipCriteria("Ngày bắt đầu không được lớn hơn ngày kết thúc.", "setupStartDate_"+key);
                    showToolipCriteria("Ngày bắt đầu không được lớn hơn ngày kết thúc.", "setupEndDate_"+key);
                }else {
                    removeTooltipParamCriteria("setupStartDate_" + key);
                    removeTooltipParamCriteria("setupEndDate_" + key);
                }
            }
        }
    }
    return checkTime;
}

function changeOperator(obj, key){
    let operator = $(obj).val();
    if(operator == '><'){
        $("#div_value_end_" + key).show();
    }else {
        $("#div_value_end_" + key).hide();
    }
}