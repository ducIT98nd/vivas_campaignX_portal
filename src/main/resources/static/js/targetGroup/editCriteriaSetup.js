var criteriaTree = new CriteriaTree("0");
var criteriaIDSequence = 0;

$(document).ready(function () {
    var channel = $("#channel").val();
    if (channel == 1 || channel == 4) {
        generateCriteriaHTML();
    } else if(channel == 3 || channel == 6) {
        criteriaIDSequence++;
        axios.get('/TargetGroupController/GetRowLevel1Criteria?currentId='+criteriaIDSequence + '&parentId=0')
            .then(function (response) {
                $("#SetCriteria").after(response.data);
                $("#buttonDelete_"+criteriaIDSequence).attr("disabled", "disabled");
            })
            .catch(function (error) {
                console.log(error);
            });

        criteriaTree.insert("0", criteriaIDSequence);
    }
});

async function getListCriteriaMapping(targetGroupId) {
    let url = '/TargetGroupController/getListCriteriaMapping?targetGroupId=' + targetGroupId;
    try {
        let res = await fetch(url);
        return await res.json();
    } catch (error) {
        console.log(error);
    }
}

async function generateCriteriaHTML() {
    var targetGroupId = $("#targetGroupId").val();
    let listCriteria = await getListCriteriaMapping(targetGroupId);
    //lay dc list tieu chi, vong lap de gen html
    var arr = [];

    for (let criteria of listCriteria) {
        let criteriaNode = JSON.parse(criteria.selectedValue);
        criteriaTree.insert(criteriaNode.parentKey, criteriaNode.key);
        arr.push(criteriaNode.key);
        if (criteriaIDSequence < criteriaNode.key) criteriaIDSequence = criteriaNode.key;
    }
    arr.sort();
    criteriaIDSequence++;
    // criteriaIDSequence = arr[(arr.length)-1]
    let countLv1 = 0;
    let keyLv1 = null;
    for (let criteria of listCriteria) {
        if(criteria.levelCriteria == 1) {
            countLv1++;
            let criteriaNode = JSON.parse(criteria.selectedValue);
            keyLv1 = criteriaNode.key;
        }
        await processCriteria(criteria);
    }

    if(countLv1 == 1){
        $("#buttonDelete_" + keyLv1).prop("disabled", true);
    }

    for(let currentNode of criteriaTree.preOrderTraversal()){
        if(currentNode.key != 0){
            if(currentNode.children.length > 0) $("#buttonAddBranch_" + currentNode.key).prop("disabled", true);
        }
    }

}

async function processCriteria(criteria) {
    let criteriaNode = JSON.parse(criteria.selectedValue);
    let idDiv;
    let url;
    let criteriaButtonID;
    let criteriaSelectID;
    if (criteria.levelCriteria == 1) {
        idDiv = "SetCriteria";
        url = '/TargetGroupController/GetRowLevel1Criteria';
        criteriaButtonID = "buttonId_" + criteriaNode.key;
        criteriaSelectID = "selectCriteriaID_" + criteriaNode.key;
    } else if (criteria.levelCriteria == 2) {
        idDiv = "level1_" + criteriaNode.parentKey;
        criteriaButtonID = "buttonId_" + criteriaNode.key;
        criteriaSelectID = "selectCriteriaID_" + criteriaNode.key;
        if (criteria.position == 1) {
            url = '/TargetGroupController/GetRowLevel2CriteriaEdit';
        } else {
            url = '/TargetGroupController/GetRowSecondLevel2Criteria';
        }
    } else if (criteria.levelCriteria == 3) {
        idDiv = "level2_" + criteriaNode.parentKey;
        criteriaSelectID = "selectCriteriaID_" + criteriaNode.key;
        criteriaButtonID = "buttonId_" + criteriaNode.key;
        if (criteria.position == 1) {
            url = '/TargetGroupController/GetRowLevel3CriteriaEdit';
        } else {
            url = '/TargetGroupController/GetRowSecondLevel3Criteria';
        }
    }
    if (criteria.levelCriteria == 1) {
        await axios.get(url + '?currentId=' + criteriaNode.key + '&parentId=' + criteriaNode.parentKey )
            .then(function (response) {
                $("#" + idDiv).parent().append(response.data);
                editChonTieuChi(criteria.idBigdataCriteria, criteria.criteriaName, criteriaButtonID,
                    criteriaSelectID, criteriaNode.key, criteriaNode);
            })
            .catch(function (error) {
                console.log(error);
            });
    } else {
        await axios.get(url + '?currentId=' + criteriaNode.key + '&parentId=' + criteriaNode.parentKey + '&criteriaParentId=' + criteria.parentId)
            .then(function (response) {
                $("#" + idDiv).parent().append(response.data);
                if(criteria.levelCriteria == 2) {
                    $("#condition_level2_of_" + criteriaNode.parentKey).selectpicker({
                        style: 'btn-condition'
                    });
                }else if(criteria.levelCriteria == 3){
                    $("#condition_level3_of_" + criteriaNode.parentKey).selectpicker({
                        style: 'btn-condition'
                    });
                }
                editChonTieuChi(criteria.idBigdataCriteria, criteria.criteriaName, criteriaButtonID,
                    criteriaSelectID, criteriaNode.key, criteriaNode);
            })
            .catch(function (error) {
                console.log(error);
            });
    }

}

function editChonTieuChi(criteriaID, criteriaName, idElement, selectCriteriaID, currentNodeID, criteriaNode) {

    $("#" + idElement).html(criteriaName + "<i style=\"float: right\" class=\"fa fa-angle-down\"></i>");
    //Xóa những điều kiện đã hiện ra
    var classNameCondition = "class-" + selectCriteriaID;
    $("." + classNameCondition).remove();
    $("#buttonId_" + currentNodeID).parent().removeClass("has-danger-1");


    axios.get('/TargetGroupController/GetCriteriaFormat?id=' + criteriaID)
        .then(function (response) {
            console.log(response.data);
            var obj = response.data;
            if (criteriaID == 3 || criteriaID == 4 || criteriaID == 5 || criteriaID == 56 || criteriaID == 60) {
                criteriaTree.find(currentNodeID).unit = "";
            } else criteriaTree.find(currentNodeID).unit = obj.unit;
            criteriaTree.find(currentNodeID).criteriaName = criteriaName;
            criteriaTree.find(currentNodeID).criteriaCode = obj.code;
            criteriaTree.find(currentNodeID).criteriaId = criteriaID;
            criteriaTree.find(currentNodeID).operator = criteriaNode.operator;
            criteriaTree.find(currentNodeID).value = criteriaNode.value;
            criteriaTree.find(currentNodeID).valueEnd = criteriaNode.valueEnd;
            criteriaTree.find(currentNodeID).operatorTime = criteriaNode.operatorTime;
            criteriaTree.find(currentNodeID).startTime = criteriaNode.startTime;
            criteriaTree.find(currentNodeID).endTime = criteriaNode.endTime;
            criteriaTree.find(currentNodeID).serviceCode = criteriaNode.serviceCode;
            console.log("operator", criteriaNode.operator)
            var htmlField = "";
            if (criteriaID == 1 || criteriaID == 2 || criteriaID == 6 || criteriaID == 7 || criteriaID == 65 || criteriaID == 66) {
                htmlField += "<div class=\"col-lg-3 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                    "                        <select id='operator_" + currentNodeID + "' name='" + obj.code + "' onchange='changeOperator(this, "+currentNodeID+")' data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" class=\"form-control selectpicker\" >\n" +
                    "                            <option " + (criteriaNode.operator == '=' ? 'selected' : '') + " value=\"=\">Bằng</option>\n" +
                    "                            <option " + (criteriaNode.operator == '!=' ? 'selected' : '') + " value=\"!=\">Không bằng</option>\n" +
                    "                            <option " + (criteriaNode.operator == '<' ? 'selected' : '') + " value=\"<\">Nhỏ hơn</option>\n" +
                    "                            <option " + (criteriaNode.operator == '>' ? 'selected' : '') + " value=\">\">Lớn hơn</option>\n" +
                    "                            <option " + (criteriaNode.operator == '<=' ? 'selected' : '') + " value=\"<=\">Nhỏ hơn hoặc bằng</option>\n" +
                    "                            <option " + (criteriaNode.operator == '>=' ? 'selected' : '') + " value=\">=\">Lớn hơn hoặc bằng</option>\n" +
                    "                            <option " + (criteriaNode.operator == '><' ? 'selected' : '') + " value=\"><\">Trong khoảng</option>\n" +
                    "                        </select>\n" +
                    "                    </div>";

                if(obj.unit == 'Phút' || obj.unit == 'MB'){
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input id='value_" + currentNodeID + "' onpaste=\"return false;\" step=\"any\" ondrop=\"return false;\" autocomplete=\"off\" type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterDotByInputNumber(event)' value='" + criteriaNode.value + "' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                    if(criteriaNode.operator == '><' ){
                        htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+currentNodeID+"\" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                            "                        <input id='value_end_" + currentNodeID + "' onpaste=\"return false;\" step=\"any\" ondrop=\"return false;\" autocomplete=\"off\" type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterDotByInputNumber(event)' value='" + criteriaNode.valueEnd + "' placeholder=\"Nhập giá trị\"/>\n" +
                            "                    </div>";
                    }else {
                        htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+currentNodeID+"\" style=\"padding-left: 0px !important; display: none; padding-right: 0px !important;\">\n" +
                            "                        <input id='value_end_"+currentNodeID+"' onpaste=\"return false;\" step=\"any\" ondrop=\"return false;\" autocomplete=\"off\" type='number' name='' class=\"form-control\"  onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                            "                    </div>";
                    }
                }else {
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input id='value_" + currentNodeID + "' type=\"number\" onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" name='' class=\"form-control\" onkeypress='return blockCharacterByInputNumber(event)' value='" + criteriaNode.value + "' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                    if(criteriaNode.operator == '><' ){
                        htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+currentNodeID+"\" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                            "                        <input id='value_end_" + currentNodeID + "' type=\"number\" onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" name='' class=\"form-control\" onkeypress='return blockCharacterByInputNumber(event)' value='" + criteriaNode.valueEnd + "' placeholder=\"Nhập giá trị\"/>\n" +
                            "                    </div>";
                    }else {
                        htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+currentNodeID+"\" style=\"padding-left: 0px !important; display: none; padding-right: 0px !important;\">\n" +
                            "                        <input id='value_end_"+currentNodeID+"' type='number' name='' onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" class=\"form-control\"  onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                            "                    </div>";
                    }
                }

                htmlField += "<div class=\"col-lg-2 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                    "                        <input id='unit_" + currentNodeID + "'  name='' class=\"form-control\" disabled placeholder=\" " + obj.unit + " \"/>\n" +
                    "                    </div>";
                $("#" + selectCriteriaID).after(htmlField);
                $("#operator_"+currentNodeID).selectpicker({
                    style: 'btn-operator'
                });
            } else if (criteriaID == 3 || criteriaID == 4) {
                if (criteriaID == 3) {
                    let value = criteriaNode.value;
                    htmlField += "<div class=\"col-lg-9 " + classNameCondition + " \"  style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n"
                    htmlField += "<div class='row' style='margin-left: 30px'>";

                    htmlField += "<div class='col-lg-2 custom-control custom-checkbox float-right' style='padding-top: 10px;'>";
                    htmlField += "<label class='control-label'>" +
                        "<input type='checkbox' class='form-check-input value_" + currentNodeID + "' value='recall' " + (criteriaNode.value.indexOf('recall') != -1 ? 'checked' : '') + ">" + "Thu hồi số" +
                        "</label>";
                    htmlField += "</div>";

                    htmlField += "<div class='custom-control custom-checkbox float-right' style='padding-left: 50px; padding-right: 30px; padding-top: 10px;'>";
                    htmlField += "<label class='control-label'>" +
                        "<input type='checkbox' class='form-check-input value_" + currentNodeID + "' value='lock2' " + (criteriaNode.value.indexOf('lock2') != -1  ? 'checked' : '') + ">" + "Khóa 2 chiều" +
                        "</label>";
                    htmlField += "</div>";

                    htmlField += "<div class='custom-control custom-checkbox float-right' style='padding-left: 50px; padding-right: 30px; padding-top: 10px;'>";
                    htmlField += "<label class='control-label'>" +
                        "<input type='checkbox' class='form-check-input value_" + currentNodeID + "' value='lock1' " + (criteriaNode.value.indexOf('lock1') != -1 ? 'checked' : '') + ">" + "Khóa 1 chiều" +
                        "</label>";
                    htmlField += "</div>";

                    htmlField += "<div class='custom-control custom-checkbox float-right' style='padding-left: 50px; padding-right: 30px; padding-top: 10px;'>";
                    htmlField += "<label class='control-label'>" +
                        "<input type='checkbox' class='form-check-input value_" + currentNodeID + "' value='active' " + (criteriaNode.value.indexOf('active') != -1  ? 'checked' : '') + " >" + "Hoạt động" +
                        "</label>";
                    htmlField += "</div>";

                    htmlField += "</div>";
                    htmlField += "</div>";
                    $("#" + selectCriteriaID).after(htmlField);
                } else if (criteriaID == 4) {
                    htmlField += "<div class=\"col-lg-3 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                        "                        <select id='value_" + currentNodeID + "' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\">\n" +
                        "                            <option " + (criteriaNode.value == 'PREPAID' ? 'selected' : '') + " value=\"PREPAID\">Trả trước</option>\n" +
                        "                            <option " + (criteriaNode.value == 'POSTPAID' ? 'selected' : '') + " value=\"POSTPAID\">Trả sau</option>\n" +
                        "                        </select>\n" +
                        "                    </div>";
                    $("#" + selectCriteriaID).after(htmlField);
                    $("#value_"+currentNodeID).selectpicker({
                        style: 'btn-operator'
                    });

                }
            } else if (criteriaID == 5) {
                htmlField += "<div class=\"col-lg-3 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                    "                        <select id='operator_" + currentNodeID + "' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\">\n" +
                    "                            <option " + (criteriaNode.operator == 'is' ? 'selected' : '') + " value=\"is\">Là</option>\n" +
                    "                            <option " + (criteriaNode.operator == 'isnot' ? 'selected' : '') + " value=\"isnot\">Không là</option>\n" +
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
                                    "                        <select id='value_"+currentNodeID+"' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" >\n" ;
                                for(let i = 0; i < data.length; i++){
                                    htmlField +=  "     <option "+ (criteriaNode.value == data[i] ? 'selected' : '') +" value=\""+data[i]+"\">"+data[i]+"</option>\n";
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
                    "                        <select id='operator_" + currentNodeID + "' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" >\n" +
                    "                            <option " + (criteriaNode.operator == 'using' ? 'selected' : '') + " value=\"using\">Đang sử dụng</option>\n" +
                    "                            <option " + (criteriaNode.operator == 'notInUse' ? 'selected' : '') + " value=\"notInUse\">Đang không sử dụng</option>\n" +
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
                            htmlField +=  " <option "+ (criteriaNode.value == element.id ? 'selected' : '') +" value=\""+element.id+"\">"+element.packageName+"</option>\n";

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
            }
            else if(criteriaID == 57 || criteriaID == 58){
                htmlField += "<div class=\"col-lg-2 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                    "                        <select id='operator_" + currentNodeID + "' name='" + obj.code + "' onchange='changeOperator(this, "+currentNodeID+")' data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" class=\"form-control selectpicker\" >\n" +
                    "                            <option " + (criteriaNode.operator == '=' ? 'selected' : '') + " value=\"=\">Bằng</option>\n" +
                    "                            <option " + (criteriaNode.operator == '!=' ? 'selected' : '') + " value=\"!=\">Không bằng</option>\n" +
                    "                            <option " + (criteriaNode.operator == '<' ? 'selected' : '') + " value=\"<\">Nhỏ hơn</option>\n" +
                    "                            <option " + (criteriaNode.operator == '>' ? 'selected' : '') + " value=\">\">Lớn hơn</option>\n" +
                    "                            <option " + (criteriaNode.operator == '<=' ? 'selected' : '') + " value=\"<=\">Nhỏ hơn hoặc bằng</option>\n" +
                    "                            <option " + (criteriaNode.operator == '>=' ? 'selected' : '') + " value=\">=\">Lớn hơn hoặc bằng</option>\n" +
                    "                            <option " + (criteriaNode.operator == '><' ? 'selected' : '') + " value=\"><\">Trong khoảng</option>\n" +
                    "                        </select>\n" +
                    "                    </div>";

                if(obj.unit == 'Phút' || obj.unit == 'MB'){
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input id='value_" + currentNodeID + "' onpaste=\"return false;\" step=\"any\" ondrop=\"return false;\" autocomplete=\"off\" type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterDotByInputNumber(event)' value='" + criteriaNode.value + "' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                    if(criteriaNode.operator == '><' ){
                        htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+currentNodeID+"\" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                            "                        <input id='value_end_" + currentNodeID + "' onpaste=\"return false;\" step=\"any\" ondrop=\"return false;\" autocomplete=\"off\" type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterDotByInputNumber(event)' value='" + criteriaNode.valueEnd + "' placeholder=\"Nhập giá trị\"/>\n" +
                            "                    </div>";
                    }else {
                        htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+currentNodeID+"\" style=\"padding-left: 0px !important; display: none; padding-right: 0px !important;\">\n" +
                            "                        <input id='value_end_"+currentNodeID+"' onpaste=\"return false;\" step=\"any\" ondrop=\"return false;\" autocomplete=\"off\" type='number' name='' class=\"form-control\"  onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                            "                    </div>";
                    }
                }else {
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input id='value_" + currentNodeID + "' type=\"number\" onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" name='' class=\"form-control\" onkeypress='return blockCharacterByInputNumber(event)' value='" + criteriaNode.value + "' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                    if(criteriaNode.operator == '><' ){
                        htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+currentNodeID+"\" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                            "                        <input id='value_end_" + currentNodeID + "' type=\"number\" onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" name='' class=\"form-control\" onkeypress='return blockCharacterByInputNumber(event)' value='" + criteriaNode.valueEnd + "' placeholder=\"Nhập giá trị\"/>\n" +
                            "                    </div>";
                    }else {
                        htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+currentNodeID+"\" style=\"padding-left: 0px !important; display: none; padding-right: 0px !important;\">\n" +
                            "                        <input id='value_end_"+currentNodeID+"' type='number' name='' onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" class=\"form-control\"  onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                            "                    </div>";
                    }
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
                            htmlField +=  " <option "+ (parseInt(criteriaNode.serviceCode) == element.id ? 'selected' : '') +" value=\""+element.id+"\">"+element.packageName+"</option>\n";

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
                    "                        <select id='operator_"+currentNodeID+"' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" >\n" +
                    "                            <option " + (criteriaNode.operator == 'using' ? 'selected' : '') + " value=\"using\">Đang sử dụng</option>\n" +
                    "                            <option " + (criteriaNode.operator == 'notInUse' ? 'selected' : '') + " value=\"notInUse\">Đang không sử dụng</option>\n" +
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
                            htmlField +=  " <option "+ (criteriaNode.value == element.id ? 'selected' : '') +" value=\""+element.id+"\">"+element.packageName+"</option>\n";

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
                    "                        <select id='operator_" + currentNodeID + "' name='" + obj.code + "' onchange='changeOperator(this, "+currentNodeID+")' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" >\n" +
                    "                            <option " + (criteriaNode.operator == '=' ? 'selected' : '') + " value=\"=\">Bằng</option>\n" +
                    "                            <option " + (criteriaNode.operator == '!=' ? 'selected' : '') + " value=\"!=\">Không bằng</option>\n" +
                    "                            <option " + (criteriaNode.operator == '<' ? 'selected' : '') + " value=\"<\">Nhỏ hơn</option>\n" +
                    "                            <option " + (criteriaNode.operator == '>' ? 'selected' : '') + " value=\">\">Lớn hơn</option>\n" +
                    "                            <option " + (criteriaNode.operator == '<=' ? 'selected' : '') + " value=\"<=\">Nhỏ hơn hoặc bằng</option>\n" +
                    "                            <option " + (criteriaNode.operator == '>=' ? 'selected' : '') + " value=\">=\">Lớn hơn hoặc bằng</option>\n" +
                    "                            <option " + (criteriaNode.operator == '><' ? 'selected' : '') + " value=\"><\">Trong khoảng</option>\n" +
                    "                        </select>\n" +
                    "                    </div>";

                if(obj.unit == 'Phút' || obj.unit == 'MB'){
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input id='value_" + currentNodeID + "' onpaste=\"return false;\" step=\"any\" ondrop=\"return false;\" autocomplete=\"off\" type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterDotByInputNumber(event)' value='" + criteriaNode.value + "' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                    if(criteriaNode.operator == '><' ){
                        htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+currentNodeID+"\" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                            "                        <input id='value_end_" + currentNodeID + "' onpaste=\"return false;\" step=\"any\" ondrop=\"return false;\" autocomplete=\"off\" type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterDotByInputNumber(event)' value='" + criteriaNode.valueEnd + "' placeholder=\"Nhập giá trị\"/>\n" +
                            "                    </div>";
                    }else {
                        htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+currentNodeID+"\" style=\"padding-left: 0px !important; display: none; padding-right: 0px !important;\">\n" +
                            "                        <input id='value_end_"+currentNodeID+"' onpaste=\"return false;\" step=\"any\" ondrop=\"return false;\" autocomplete=\"off\" type='number' name='' class=\"form-control\"  onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                            "                    </div>";
                    }
                }else {
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input id='value_" + currentNodeID + "' type=\"number\" onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" name='' class=\"form-control\" onkeypress='return blockCharacterByInputNumber(event)' value='" + criteriaNode.value + "' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                    if(criteriaNode.operator == '><' ){
                        htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+currentNodeID+"\" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                            "                        <input id='value_end_" + currentNodeID + "' type=\"number\" onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" name='' class=\"form-control\" onkeypress='return blockCharacterByInputNumber(event)' value='" + criteriaNode.valueEnd + "' placeholder=\"Nhập giá trị\"/>\n" +
                            "                    </div>";
                    }else {
                        htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+currentNodeID+"\" style=\"padding-left: 0px !important; display: none; padding-right: 0px !important;\">\n" +
                            "                        <input id='value_end_"+currentNodeID+"' type='number' name='' onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" class=\"form-control\"  onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                            "                    </div>";
                    }
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
                            htmlField +=  " <option "+ (parseInt(criteriaNode.serviceCode) == element.id ? 'selected' : '') +" value=\""+element.id+"\">"+element.packageName+"</option>\n";

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
                    "                        <select id='operator_" + currentNodeID + "' name='' onchange='changeOperator(this, "+currentNodeID+")' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" >\n" +
                    "                            <option " + (criteriaNode.operator == '=' ? 'selected' : '') + " value=\"=\">Bằng</option>\n" +
                    "                            <option " + (criteriaNode.operator == '!=' ? 'selected' : '') + " value=\"!=\">Không bằng</option>\n" +
                    "                            <option " + (criteriaNode.operator == '<' ? 'selected' : '') + " value=\"<\">Nhỏ hơn</option>\n" +
                    "                            <option " + (criteriaNode.operator == '>' ? 'selected' : '') + " value=\">\">Lớn hơn</option>\n" +
                    "                            <option " + (criteriaNode.operator == '<=' ? 'selected' : '') + " value=\"<=\">Nhỏ hơn hoặc bằng</option>\n" +
                    "                            <option " + (criteriaNode.operator == '>=' ? 'selected' : '') + " value=\">=\">Lớn hơn hoặc bằng</option>\n" +
                    "                            <option " + (criteriaNode.operator == '><' ? 'selected' : '') + " value=\"><\">Trong khoảng</option>\n" +
                    "                        </select>\n" +
                    "                    </div>";

                if(obj.unit == 'Phút' || obj.unit == 'MB'){
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input id='value_" + currentNodeID + "' onpaste=\"return false;\" step=\"any\" ondrop=\"return false;\" autocomplete=\"off\" type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterDotByInputNumber(event)' value='" + criteriaNode.value + "' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                    if(criteriaNode.operator == '><' ){
                        htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+currentNodeID+"\" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                            "                        <input id='value_end_" + currentNodeID + "' onpaste=\"return false;\" step=\"any\" ondrop=\"return false;\" autocomplete=\"off\" type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterDotByInputNumber(event)' value='" + criteriaNode.valueEnd + "' placeholder=\"Nhập giá trị\"/>\n" +
                            "                    </div>";
                    }else {
                        htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+currentNodeID+"\" style=\"padding-left: 0px !important; display: none; padding-right: 0px !important;\">\n" +
                            "                        <input id='value_end_"+currentNodeID+"' onpaste=\"return false;\" step=\"any\" ondrop=\"return false;\" autocomplete=\"off\" type='number' name='' class=\"form-control\"  onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                            "                    </div>";
                    }
                }else {
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input id='value_" + currentNodeID + "' type=\"number\" onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" name='' class=\"form-control\" onkeypress='return blockCharacterByInputNumber(event)' value='" + criteriaNode.value + "' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                    if(criteriaNode.operator == '><' ){
                        htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+currentNodeID+"\" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                            "                        <input id='value_end_" + currentNodeID + "' type=\"number\" onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" name='' class=\"form-control\" onkeypress='return blockCharacterByInputNumber(event)' value='" + criteriaNode.valueEnd + "' placeholder=\"Nhập giá trị\"/>\n" +
                            "                    </div>";
                    }else {
                        htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+currentNodeID+"\" style=\"padding-left: 0px !important; display: none; padding-right: 0px !important;\">\n" +
                            "                        <input id='value_end_"+currentNodeID+"' type='number' name='' onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" class=\"form-control\"  onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                            "                    </div>";
                    }
                }

                htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                    "                        <input id='' name='' class=\"form-control\" disabled placeholder=\" " + obj.unit + " \"/>\n" +
                    "                    </div>";

                var setupDate = 'setupDate_' + selectCriteriaID;
                var idStartDate = 'setupStartDate_' + currentNodeID;
                var idEndDate = 'setupEndDate_' + currentNodeID;
                htmlField += "<div class=\"col-lg-2 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                    "                        <select id='operatorTime_" + currentNodeID + "' name='' class=\"form-control\" style=\"padding-left: 0px !important; padding-right: 0px !important;\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" data-placeholder=\"Chọn giá trị\" onchange=\"changeTime(this, '" + setupDate + "')\">\n" +
                    "                            <option " + (criteriaNode.operatorTime == 1 ? 'selected' : '') + " value=\"1\">Trong 60 ngày gần nhất</option>\n" +
                    "                            <option " + (criteriaNode.operatorTime == 2 ? 'selected' : '') + " value=\"2\">Trong 30 ngày gần nhất</option>\n" +
                    "                            <option " + (criteriaNode.operatorTime == 3 ? 'selected' : '') + " value=\"3\">Trong 7 ngày gần nhất</option>\n" +
                    "                            <option " + (criteriaNode.operatorTime == 4 ? 'selected' : '') + " value=\"4\">Trong tháng hiện tại</option>\n" +
                    "                            <option " + (criteriaNode.operatorTime == 5 ? 'selected' : '') + " value=\"5\">Trong tháng trước</option>\n" +
                    "                            <option " + (criteriaNode.operatorTime == 6 ? 'selected' : '') + " value=\"6\">Trong khoảng thời gian</option>\n" +
                    "                        </select>\n" +
                    "                    </div>";
                if(criteriaNode.operatorTime == 6 ) {
                    htmlField += "<div class=\"col-lg-2 " + classNameCondition + " \" id='" + setupDate + "'  >\n";
                    htmlField += "<div class=\"row \" >\n"
                    htmlField += "<div class=\"col-lg-6 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input name='' id='" + idStartDate + "' ondrop=\"return false;\" autocomplete=\"off\" onpaste=\"return false;\" class=\"form-control\" readonly type=\"text\" value='" + criteriaNode.startTime + "' placeholder='Từ ngày' id=\"example-date-input\">\n" +
                        "                    </div>";

                    htmlField += "<div class=\"col-lg-6 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input  name='' id='" + idEndDate + "' ondrop=\"return false;\" autocomplete=\"off\" onpaste=\"return false;\" class=\"form-control\" readonly type=\"text\" value='" + criteriaNode.endTime + "'  placeholder='Đến ngày' id=\"example-date-input\">\n" +
                        "                    </div>";
                }else {
                    htmlField += "<div class=\"col-lg-2 " + classNameCondition + " \" style='display: none' id='"+setupDate+"'>\n";
                    htmlField += "<div class=\"row \" >\n"
                    htmlField += "<div class=\"col-lg-6 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input name='' onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" id='"+idStartDate+"' class=\"form-control\" style=\"padding-left: 5px !important; padding-right: 0px !important;\" type=\"text\" placeholder='Từ ngày' id=\"example-date-input\">\n" +
                        "                    </div>";

                    htmlField += "<div class=\"col-lg-6 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input  name='' onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" id='"+idEndDate+"' class=\"form-control\" style=\"padding-left: 5px !important; padding-right: 0px !important;\" type=\"text\"  placeholder='Đến ngày' id=\"example-date-input\">\n" +
                        "                    </div>";
                }
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


