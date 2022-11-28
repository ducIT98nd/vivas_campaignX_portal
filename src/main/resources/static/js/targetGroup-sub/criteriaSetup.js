
var arrayTreeSubTarget = new Map();
var mapSubTargetWithCriteria = new Map();
var setTreesubTargetCanceled = new Set();
var checkDataWhenSubCancleed = false;
$(document).ready(function () {
});

async function chonTieuChiSubTarget( arrayTreeId, criteriaID, criteriaName, idElement, selectCriteriaID, currentNodeID) {
    let subCriteriaTree = arrayTreeSubTarget.get(arrayTreeId);
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
                subCriteriaTree.find(currentNodeID).unit = "";
            }
            else subCriteriaTree.find(currentNodeID).unit = obj.unit;
            subCriteriaTree.find(currentNodeID).criteriaName = criteriaName;
            subCriteriaTree.find(currentNodeID).criteriaCode = obj.code;
            subCriteriaTree.find(currentNodeID).criteriaId = criteriaID;
            var htmlField = "";
            if (criteriaID == 1 || criteriaID == 2 || criteriaID == 6 || criteriaID == 7 || criteriaID == 65 || criteriaID == 66) {
                htmlField += "<div class=\"col-lg-3 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                    "                        <select id='operatorSub_"+currentNodeID+"' name='"+obj.code+"' onchange='changeOperator(this, "+currentNodeID+")' class=\"custom-select form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n" +
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
                        "                        <input id='valueSub_"+currentNodeID+"' onpaste=\"return false;\" step=\"any\" ondrop=\"return false;\" autocomplete=\"off\" type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+currentNodeID+"\" style=\"padding-left: 0px !important; display: none; padding-right: 0px !important;\">\n" +
                        "                        <input id='value_endSub_"+currentNodeID+"' onpaste=\"return false;\" step=\"any\" ondrop=\"return false;\" autocomplete=\"off\" type='number' name='' class=\"form-control\"  onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                }else {
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input id='valueSub_"+currentNodeID+"' onpaste=\"return false;\" step=\"any\" ondrop=\"return false;\" autocomplete=\"off\" type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+currentNodeID+"\" style=\"padding-left: 0px !important; display: none; padding-right: 0px !important;\">\n" +
                        "                        <input id='value_endSub_"+currentNodeID+"' onpaste=\"return false;\" step=\"any\" ondrop=\"return false;\" autocomplete=\"off\" type='number' name='' class=\"form-control\"  onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                }
                htmlField += "<div class=\"col-lg-2 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                    "                        <input id='unitSub_"+currentNodeID+"'  name='' class=\"form-control\" disabled placeholder=\" " + obj.unit + " \"/>\n" +
                    "                    </div>";
                $("#" + selectCriteriaID).after(htmlField);
                $("#operatorSub_"+currentNodeID).selectpicker({
                    style: 'btn-operator'
                });
            } else if (criteriaID == 3 || criteriaID == 4) {
                if (criteriaID == 3) {
                    htmlField += "<div class=\"col-lg-9 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n"
                    htmlField += "<div class='row' style='margin-left: 30px'>";

                    htmlField += "<div class='col-lg-2 custom-control custom-checkbox float-right' style='padding-top: 10px;'>";
                    htmlField += "<label class='control-label'>" +
                        "<input type='checkbox' class='form-check-input valueSub_"+currentNodeID+"' value='active'>" + "Hoạt động" +
                        "</label>";
                    htmlField += "</div>";

                    htmlField += "<div class='col-lg-2 custom-control custom-checkbox float-right' style='padding-top: 10px;'>";
                    htmlField += "<label class='control-label'>" +
                        "<input type='checkbox' class='form-check-input valueSub_"+currentNodeID+"' value='lock1'>" + "Khóa 1 chiều" +
                        "</label>";
                    htmlField += "</div>";

                    htmlField += "<div class='col-lg-2 custom-control custom-checkbox float-right' style='padding-top: 10px;'>";
                    htmlField += "<label class='control-label'>" +
                        "<input type='checkbox' class='form-check-input valueSub_"+currentNodeID+"' value='lock2'>" + "Khóa 2 chiều" +
                        "</label>";
                    htmlField += "</div>";

                    htmlField += "<div class='col-lg-2 custom-control custom-checkbox float-right' style='padding-top: 10px;'>";
                    htmlField += "<label class='control-label'>" +
                        "<input type='checkbox' class='form-check-input valueSub_"+currentNodeID+"' value='recall'>" + "Thu hồi số" +
                        "</label>";
                    htmlField += "</div>";

                    htmlField += "</div>";
                    htmlField += "</div>";
                    $("#" + selectCriteriaID).after(htmlField);
                } else if (criteriaID == 4) {
                    htmlField += "<div class=\"col-lg-3 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                        "                        <select id='valueSub_"+currentNodeID+"' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n" +
                        "                            <option value=\"PREPAID\">Trả trước</option>\n" +
                        "                            <option value=\"POSTPAID\">Trả sau</option>\n" +
                        "                        </select>\n" +
                        "                    </div>";

                    $("#" + selectCriteriaID).after(htmlField);
                    $("#valueSub_"+currentNodeID).selectpicker({
                        style: 'btn-operator'
                    });
                }
            } else if (criteriaID == 5) {
                htmlField += "<div class=\"col-lg-3 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                    "                        <select id='operatorSub_"+currentNodeID+"' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n" +
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
                                    "                        <select id='valueSub_"+currentNodeID+"' name='' class=\"form-control selectpicker \" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n" ;
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
                $("#operatorSub_"+currentNodeID).selectpicker({
                    style: 'btn-operator'
                });
                $("#valueSub_"+currentNodeID).selectpicker({
                    style: 'btn-operator'
                });

            } else if (criteriaID == 56) {
                htmlField += "<div class=\"col-lg-3 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                    "                        <select id='operatorSub_"+currentNodeID+"' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n" +
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
                            "                        <select id='valueSub_"+currentNodeID+"' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n" +
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
                $("#operatorSub_"+currentNodeID).selectpicker({
                    style: 'btn-operator'
                });
                $("#valueSub_"+currentNodeID).selectpicker({
                    style: 'btn-operator'
                });

            } else if(criteriaID == 57 || criteriaID == 58){
                htmlField += "<div class=\"col-lg-2 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                    "                        <select id='operatorSub_"+currentNodeID+"' name='' class=\"form-control selectpicker\" onchange='changeOperator(this, "+currentNodeID+")' data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n" +
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
                        "                        <input id='valueSub_"+currentNodeID+"' onpaste=\"return false;\" step=\"any\" ondrop=\"return false;\" autocomplete=\"off\" type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+currentNodeID+"\" style=\"padding-left: 0px !important; display: none; padding-right: 0px !important;\">\n" +
                        "                        <input id='value_endSub_"+currentNodeID+"' onpaste=\"return false;\" step=\"any\" ondrop=\"return false;\" autocomplete=\"off\" type='number' name='' class=\"form-control\"  onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                }else {
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input id='valueSub_"+currentNodeID+"' onpaste=\"return false;\" step=\"any\" ondrop=\"return false;\" autocomplete=\"off\" type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+currentNodeID+"\" style=\"padding-left: 0px !important; display: none; padding-right: 0px !important;\">\n" +
                        "                        <input id='value_endSub_"+currentNodeID+"' onpaste=\"return false;\" step=\"any\" ondrop=\"return false;\" autocomplete=\"off\" type='number' name='' class=\"form-control\"  onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
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
                            "                        <select id='mainServiceSub_"+currentNodeID+"' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n";
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
                $("#operatorSub_"+currentNodeID).selectpicker({
                    style: 'btn-operator'
                });
                $("#mainServiceSub_"+currentNodeID).selectpicker({
                    style: 'btn-operator'
                });

            } else if (criteriaID == 60) {
                htmlField += "<div class=\"col-lg-3 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                    "                        <select id='operatorSub_"+currentNodeID+"' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n" +
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
                            "                        <select id='valueSub_"+currentNodeID+"' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n" +
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
                $("#operatorSub_"+currentNodeID).selectpicker({
                    style: 'btn-operator'
                });
                $("#valueSub_"+currentNodeID).selectpicker({
                    style: 'btn-operator'
                });
            }  else if(criteriaID == 61 || criteriaID == 62){
                htmlField += "<div class=\"col-lg-2 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                    "                        <select id='operatorSub_"+currentNodeID+"' name='' class=\"form-control selectpicker\" onchange='changeOperator(this, "+currentNodeID+")' data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n" +
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
                        "                        <input id='valueSub_"+currentNodeID+"' onpaste=\"return false;\" step=\"any\" ondrop=\"return false;\" autocomplete=\"off\" type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+currentNodeID+"\" style=\"padding-left: 0px !important; display: none; padding-right: 0px !important;\">\n" +
                        "                        <input id='value_endSub_"+currentNodeID+"' onpaste=\"return false;\" step=\"any\" ondrop=\"return false;\" autocomplete=\"off\" type='number' name='' class=\"form-control\"  onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                }else {
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input id='valueSub_"+currentNodeID+"' onpaste=\"return false;\" step=\"any\" ondrop=\"return false;\" autocomplete=\"off\" type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+currentNodeID+"\" style=\"padding-left: 0px !important; display: none; padding-right: 0px !important;\">\n" +
                        "                        <input id='value_endSub_"+currentNodeID+"' onpaste=\"return false;\" step=\"any\" ondrop=\"return false;\" autocomplete=\"off\" type='number' name='' class=\"form-control\"  onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
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
                            "                        <select id='mainServiceSub_"+currentNodeID+"' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n" ;
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
                $("#operatorSub_"+currentNodeID).selectpicker({
                    style: 'btn-operator'
                });
                $("#mainServiceSub_"+currentNodeID).selectpicker({
                    style: 'btn-operator'
                });
            }else {
                htmlField += "<div class=\"col-lg-2 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                    "                        <select id='operatorSub_"+currentNodeID+"' name='' class=\"form-control selectpicker\" onchange='changeOperator(this, "+currentNodeID+")' data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n" +
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
                        "                        <input id='valueSub_"+currentNodeID+"' type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+currentNodeID+"\" style=\"padding-left: 0px !important; display: none; padding-right: 0px !important;\">\n" +
                        "                        <input id='value_endSub_"+currentNodeID+"' type='number' name='' class=\"form-control\"  onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                }else {
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input id='valueSub_"+currentNodeID+"' type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+currentNodeID+"\" style=\"padding-left: 0px !important; display: none; padding-right: 0px !important;\">\n" +
                        "                        <input id='value_endSub_"+currentNodeID+"' type='number' name='' class=\"form-control\"  onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                }

                htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                    "                        <input id='' name='' class=\"form-control\" disabled placeholder=\" " + obj.unit + " \"/>\n" +
                    "                    </div>";

                var setupDate = 'setupDateSub_' + selectCriteriaID;
                var idStartDate = 'setupStartDateSub_' + currentNodeID;
                var idEndDate = 'setupEndDateSub_' + currentNodeID;
                htmlField += "<div class=\"col-lg-2 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                    "                        <select id='operatorTimeSub_"+currentNodeID+"' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\" style=\"padding-left: 0px !important; padding-right: 0px !important;\" data-placeholder=\"Chọn giá trị\" onchange=\"changeTime(this, '" + setupDate + "')\">\n" +
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
                $("#operatorSub_"+currentNodeID).selectpicker({
                    style: 'btn-operator'
                });
                $("#operatorTimeSub_"+currentNodeID).selectpicker({
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


function changeTime(obj, setupDate) {
    var value = $(obj).val();
    if (value == 6) {
        $("#" + setupDate).show();
    } else {
        $("#" + setupDate).hide();

    }
}


function addCriteriaSubTarget(subTreeId, obj, currentNodeID) {
    $("#buttonDelete_"+currentNodeID).removeAttr("disabled");
    criteriaIDSequence++;
    axios.get('/SubTargetGroupController/GetRowLevel1Criteria?currentId='+criteriaIDSequence +'&parentId=0&subTreeId='+subTreeId)
        .then(function (response) {
            $("#" + obj).after(response.data);
        })
        .catch(function (error) {
            console.log(error);
        });
    arrayTreeSubTarget.get(subTreeId).insert("0", criteriaIDSequence);
   /* console.log(criteriaTree);*/
}

function deleteCriteriaSubTarget(id,obj1, obj2, currentNodeID) {
    let subCriteriaTree = arrayTreeSubTarget.get(id);
    $("#" + obj1).remove();
    $("#" + obj2).remove();
    let nodeParent = subCriteriaTree.find(currentNodeID).parent;
    subCriteriaTree.remove(currentNodeID);
    if(nodeParent.children.length == 1){
        let nodeChild = nodeParent.children[0];
        $("#buttonDelete_"+nodeChild.key).attr("disabled", "disabled");
    }
}
function getHashValueSubTarget(currentNode){
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

function checkCriteriaSubTarget(obj){
    try {
        let subCriteriaTree = arrayTreeSubTarget.get(obj);
        console.log(subCriteriaTree);

        let countCriteria = 0;

        for (let currentNode of subCriteriaTree.preOrderTraversal()) {
            if (currentNode.key != 0) {
                let b = $("#buttonSubTargetId_" + currentNode.key).text().trim();
                if (b != 'Chọn tham số') {
                    countCriteria++;
                }
            }
        }
        if (countCriteria == 0) {
            for (let currentNode of subCriteriaTree.preOrderTraversal()) {
                if (currentNode.key != 0) {
                    let message = "Tiêu chí bổ sung chưa được thiết lập.";
                    showToolipCriteria(message, "buttonSubTargetId_" + currentNode.key);
                }
            }
            return false;
        } else {
            let flag = true;
            let stackLevel3 = new Stack();
            let stackLevel2 = new Stack();
            let setNode = new Set();
            //Validate
            for (let currentNode of subCriteriaTree.preOrderTraversal()) {
                let b = $("#buttonSubTargetId_" + currentNode.key).text().trim();
                if (b == 'Chọn tham số') {
                    flag = false;
                    let message = "Vui lòng chọn tham số.";
                    showToolipCriteria(message, "buttonSubTargetId_" + currentNode.key);
                } else {
                    if (currentNode.key != 0) {
                        let boolCheckValue = true;
                        if (currentNode.criteriaId != 3 && currentNode.criteriaId != 4 && currentNode.criteriaId != 5 && currentNode.criteriaId != 56 && currentNode.criteriaId != 60) {
                            let value = $("#valueSub_" + currentNode.key).val();
                            if (value == null || value.length <= 0) {
                                boolCheckValue = false;
                                let message = "Giá trị nhập không được để trống.";
                                showToolipParamCriteria(message, "valueSub_" + currentNode.key);
                            }
                            let operator = $("#operatorSub_" + currentNode.key).val();
                            if (operator == '><') {
                                let valueEnd = $("#value_endSub_" + currentNode.key).val();
                                if (valueEnd == null || valueEnd.length <= 0) {
                                    boolCheckValue = false;
                                    let message = "Giá trị nhập không được để trống.";
                                    showToolipParamCriteria(message, "value_endSub_" + currentNode.key);
                                } else {
                                    if (parseInt(value) > parseInt(valueEnd)) {
                                        boolCheckValue = false;
                                        let message = "Giá trị đầu không được lớn hơn giá trị cuối.";
                                        showToolipParamCriteria(message, "value_endSub_" + currentNode.key);
                                        showToolipParamCriteria(message, "valueSub_" + currentNode.key);
                                    }
                                }
                            }
                        } else if (currentNode.criteriaId == 3) {
                            let value = "";
                            $(".valueSub_" + currentNode.key + ":checkbox:checked").each(function () {
                                value += $(this).val();
                                value += ";"
                            });
                            if (value == null || value.length <= 0) {
                                boolCheckValue = false;
                                let message = "Vui lòng chọn trạng thái.";
                                showToolipCriteria(message, "buttonSubTargetId_" + currentNode.key);
                            }
                        }
                        if(flag) flag = boolCheckValue;
                        if (boolCheckValue) {
                            removeTooltipParamCriteria("value_endSub_" + currentNode.key);
                            removeTooltipParamCriteria("valueSub_" + currentNode.key);
                        }

                        let boolCheckTime = checkStartDateAndEndDateSubGroup(currentNode.criteriaId, currentNode.key);
                        if(flag) flag = boolCheckTime;
                        let hashValue = getHashValue(currentNode);
                        if (setNode.has(hashValue)) {
                            flag = false;
                            let message = "Trường hợp yêu cầu thỏa mãn tất cả điều kiện, các tiêu chí đồng cấp không được trùng nhau.";
                            showToolipCriteria(message, "buttonSubTargetId_" + currentNode.key);
                            setNode.add(hashValue);
                        } else setNode.add(hashValue);
                    }
                }
            }

            if (flag) {
                let finaldt = {
                    condition: "",
                    childs: []
                };
                let arrayData = [];
                //duyệt cây
                let condition = $("#conditionLevel1").val();
                finaldt.condition = condition;
                for (let currentNode of subCriteriaTree.postOrderTraversal()) {
                    console.log("currentNodeLevel: " + currentNode.level + " currentNodeKey: " + currentNode.key + " currentNodeName: " + currentNode.criteriaName + " currentNodeCriteriaId: " + currentNode.criteriaId);
                    if (currentNode.level == 1) {
                        let objDataLevel1 = {
                            condition: "",
                            data: "",
                            childs: []
                        };
                        //Khởi tạo obj của dòng hiện tại
                        let objtemp = createObjSub(arrayData, currentNode.criteriaId, currentNode.position, currentNode.criteriaCode, currentNode.criteriaName, currentNode.level, currentNode.parent.key, currentNode.key, currentNode.unit);
                        objDataLevel1.data = objtemp;
                        //lấy các dòng con ở trong stack
                        objDataLevel1.condition = $("#condition_level2_of_" + currentNode.key).val();
                        while (!stackLevel2.isEmpty()) {
                            let objstackLv2 = stackLevel2.pop();
                            objDataLevel1.childs.push(objstackLv2);
                        }
                        //add vào obj tổng
                        finaldt.childs.push(objDataLevel1);

                    } else if (currentNode.level == 2) {
                        let objDataLevel2 = {
                            condition: "",
                            data: "",
                            childs: []
                        };
                        //Khởi tạo obj của dòng hiện tại
                        let objtemp = createObjSub(arrayData, currentNode.criteriaId, currentNode.position, currentNode.criteriaCode, currentNode.criteriaName, currentNode.level, currentNode.parent.key, currentNode.key, currentNode.unit);
                        objDataLevel2.data = objtemp;
                        //lấy các dòng con ở trong stack
                        objDataLevel2.condition = $("#condition_level3_of_" + currentNode.key).val();
                        while (!stackLevel3.isEmpty()) {
                            let objstackLv3 = stackLevel3.pop();
                            objDataLevel2.childs.push(objstackLv3);
                        }
                        stackLevel2.push(objDataLevel2);

                    } else if (currentNode.level == 3) {
                        let objDataLevel3 = {
                            data: ""
                        };
                        //Khởi tạo obj của dòng hiện tại
                        let objData = createObjSub(arrayData, currentNode.criteriaId, currentNode.position, currentNode.criteriaCode, currentNode.criteriaName, currentNode.level, currentNode.parent.key, currentNode.key, currentNode.unit);
                        objDataLevel3.data = objData;
                        stackLevel3.push(objDataLevel3);
                    }
                }
                console.log("finalData: " + JSON.stringify(finaldt));
                let json = JSON.stringify(finaldt);
                $("#jsonSubTarget_" + obj).val(json);
                keyupCheckSubtarget(obj);
                mapSubTargetWithCriteria.set(obj, arrayData);
                console.log("mapSubTargetWithCriteria: " + mapSubTargetWithCriteria);
            }
            return flag;
        }
    } catch (e) {return false;}
}

function validateSubTarget(obj){
    let check = checkCriteriaSubTarget(obj);

    if(check)  {
        $("#modalSubTargetGroup_" + obj).modal("hide");
        setTreesubTargetCanceled.delete(obj);
        let groupName = $("#labelSubGroupName_" + obj).text();
        Swal.fire({
            position: 'top',
            type: 'success',
            title: 'Thông báo',
            text: "Thiết lập tiêu chí cho nhóm "+groupName+" thành công",
            showConfirmButton: false,
            timer: 3000
        })
    }
}

function createObjSub(arrayData, criteriaID, pos, code, name, level, parentKey, key, unit){
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
        let strOperator = $("#operatorSub_"+key).val();
        objLevel3.operator = strOperator;
        objLevel3.value = $("#valueSub_"+key).val();
        if(strOperator == '><') objLevel3.valueEnd = $("#value_endSub_"+key).val();
    } else if (criteriaID == 3) {
        let value = "";
        $(".valueSub_"+key+":checkbox:checked").each(function () {
            value += $(this).val();
            value += ";"
        });
        objLevel3.value = value.substring(0, value.length - 1);
    }  else if (criteriaID == 4) {
        objLevel3.value = $("#valueSub_"+key).val();
    } else if (criteriaID == 5) {
        objLevel3.operator = $("#operatorSub_"+key).val();
        objLevel3.value = $("#valueSub_"+key).val();
    } else if (criteriaID == 56) {
        objLevel3.operator = $("#operatorSub_"+key).val();
        objLevel3.value = $("#valueSub_"+key).val();
    } else if (criteriaID == 60) {
        objLevel3.operator = $("#operatorSub_"+key).val();
        objLevel3.value = $("#valueSub_"+key).val();
        objLevel3.serviceCode = $("#valueSub_"+key).val();
    } else if(criteriaID == 57 || criteriaID == 58){
        let strOperator = $("#operatorSub_"+key).val();
        objLevel3.operator = strOperator;
        objLevel3.value = $("#valueSub_"+key).val();
        if(strOperator == '><') objLevel3.valueEnd = $("#value_endSub_"+key).val();
        objLevel3.serviceCode = $("#mainServiceSub_"+key).val();
    } else if(criteriaID == 61 || criteriaID == 62){
        let strOperator = $("#operatorSub_"+key).val();
        objLevel3.operator = strOperator;
        objLevel3.value = $("#valueSub_"+key).val();
        if(strOperator == '><') objLevel3.valueEnd = $("#value_endSub_"+key).val();
        objLevel3.serviceCode = $("#mainServiceSub_"+key).val();
    } else {
        let strOperator = $("#operatorSub_"+key).val();
        objLevel3.operator = strOperator;
        objLevel3.value = $("#valueSub_"+key).val();
        if(strOperator == '><') objLevel3.valueEnd = $("#value_end_"+key).val();
        objLevel3.operatorTime = $("#operatorTimeSub_"+key).val();
        objLevel3.startTime = $("#setupStartDateSub_"+key).val();
        objLevel3.endTime = $("#setupEndDateSub_"+key).val();
    }
    arrayData.push(objLevel3);
    return objLevel3;
}

function checkStartDateAndEndDateSubGroup(criteriaID, key){
    let checkTime = true;
    if (criteriaID == 1 || criteriaID == 2 || criteriaID == 6 || criteriaID == 7 || criteriaID == 3 || criteriaID == 4 || criteriaID == 5 || criteriaID == 56 || criteriaID == 60 || criteriaID == 57 || criteriaID == 58 || criteriaID == 61 || criteriaID == 62 || criteriaID == 65 || criteriaID == 66) {

    } else {
        let operatorTime = $("#operatorTimeSub_"+key).val();
        if(operatorTime == 6){
            let startTime = $("#setupStartDateSub_"+key).val();
            if(startTime == null || startTime.length <= 0){
                showToolipCriteria("Thời điểm bắt đầu không được để trống.", "setupStartDateSub_"+key);
                checkTime = false;
            } else {
                removeTooltipParamCriteria("setupStartDateSub_" + key);
            }
            let endTime = $("#setupEndDateSub_"+key).val();
            if(endTime == null || endTime.length <= 0){
                showToolipCriteria("Thời điểm kết thúc không được để trống.", "setupEndDateSub_"+key);
                checkTime = false;
            }else {
                removeTooltipParamCriteria("setupEndDateSub_" + key);
            }
            if(checkTime){
                var scheStart = $("#setupStartDateSub_"+key).val();
                scheStart = scheStart.replace(new RegExp("/", 'g'), " ");
                scheStart = scheStart.split(" ");

                var scheStartDate = new Date(scheStart[2], scheStart[1] - 1, scheStart[0]);

                var scheEnd = $("#setupEndDateSub_"+key).val();
                scheEnd = scheEnd.replace(new RegExp("/", 'g'), " ");
                scheEnd = scheEnd.split(" ");
                var scheEndDate = new Date(scheEnd[2], scheEnd[1] - 1, scheEnd[0]);

                if (scheStartDate > scheEndDate) {
                    checkTime = false;
                    showToolipCriteria("Ngày bắt đầu không được lớn hơn ngày kết thúc.", "setupStartDateSub_"+key);
                    showToolipCriteria("Ngày bắt đầu không được lớn hơn ngày kết thúc.", "setupEndDateSub_"+key);
                }else {
                    removeTooltipParamCriteria("setupStartDateSub_" + key);
                    removeTooltipParamCriteria("setupEndDateSub_" + key);
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


