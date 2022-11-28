

function changeDefinition(){
    let channel = $("#type-target-group").val();
    if(channel == 1){
        $("#inputFileCustomer").hide();
        $("#inputCriteria").show();
        $("#inputTargetGroup").hide();
    }else if(channel == 2){
        $("#inputFileCustomer").show();
        $("#inputCriteria").show();
        $("#inputTargetGroup").hide();
    }else if(channel == 3){
        $("#inputFileCustomer").show();
        $("#inputCriteria").hide();
        $("#inputTargetGroup").hide();
    }else if(channel == 4){
        $("#inputFileCustomer").show();
        $("#inputCriteria").show();
        $("#inputTargetGroup").hide();
    }else if(channel == 5){
        $("#inputFileCustomer").show();
        $("#inputCriteria").hide();
        $("#inputTargetGroup").hide();
    }else if(channel == 6){
        $("#inputFileCustomer").hide();
        $("#inputCriteria").hide();
        $("#inputTargetGroup").show();
    }
}

// step 2 - thay doi loai blacklist
function changeInputBlacklist(){
    let typeInputBlacklist = $("#type-input-blacklist").val();
    if(typeInputBlacklist == 1) {
        $("#blacklistBySelect").hide();
        $("#blacklistByFile").hide();
    }else if(typeInputBlacklist == 2) {
        $("#blacklistBySelect").show();
        $("#blacklistByFile").hide();
    }else if(typeInputBlacklist == 3) {
        $("#blacklistBySelect").hide();
        $("#blacklistByFile").show();
    }
}

function clickSubTarget(obj){
    if(obj.value =='no'){
        $(".isDisabled").attr("disabled", "disabled");
    }else {
        $(".isDisabled").removeAttr("disabled");
        // disable nút xóa nhóm con nếu chỉ còn 1 nhóm con
        if(arrayTreeSubTarget.size == 1){
            for (const [keySubTarget, value] of arrayTreeSubTarget) {
                $("#btn-delete-sub-"+keySubTarget).prop("disabled", true);
            }
        }
    }
}

function getFileExtension(filename) {
    var ext = /^.+\.([^.]+)$/.exec(filename);
    return ext == null ? "" : ext[1];
}


function keyupCheckSubtarget(id){
    checkSubtarget(id);
}

function checkSubtarget(id){
    let subName = $("#subTargetGroupName_" + id).val();
    let subJSON = $("#jsonSubTarget_" + id).val();
    let isDuplicateMsisdn = $("#is-duplicate-msisdn").is(":checked");
    if(isDuplicateMsisdn){
        let subPriority = $("#subTargetGroupPriority_" + id).val();
        if((subName != null && subName.length > 0) && (subJSON != null && subJSON.length > 0) && (subPriority != null && subPriority.length > 0)){
            $("#checkSub_" + id).show();
        }else {
            $("#checkSub_" + id).hide();
        }
    }else {
        if((subName != null && subName.length > 0) && (subJSON != null && subJSON.length > 0)){
            $("#checkSub_" + id).show();
        }else {
            $("#checkSub_" + id).hide();
        }
    }
}

function saveMessageContent(id){

    const objMessageContent = {
        channelMarketing: '',
        sendingAccount: '',
        regisCommand: '',
        confirmCommand: '',
        productPackage: '',
        messageContent: '',
        countMT: '',
        unicode: ''
    };
    let channelMarketing = $("#channel-marketing_" + id).val();
    let sendingAccount = $("#sending-account_" + id).val();
    let regisCommand = $("#regis-command_" + id).val();
    let confirmCommand = $("#confirm-command_"+id).val();
    let productPackage = $("#product-package_" + id).val();
    let messageContent = $("#message-content_" + id).val();
    let countMT = $("#countMT_" + id).val();
    let unicode = $("#unicode_" + id).val();
    objMessageContent.channelMarketing = channelMarketing;
    objMessageContent.sendingAccount = sendingAccount;
    objMessageContent.regisCommand = regisCommand;
    objMessageContent.confirmCommand = confirmCommand;
    objMessageContent.productPackage = productPackage;
    objMessageContent.messageContent = messageContent;
    objMessageContent.countMT = countMT;
    objMessageContent.unicode = unicode;
    console.log("finalData message content: " + JSON.stringify(objMessageContent));
    let json = JSON.stringify(objMessageContent);
    $("#jsonSubMessageContent_" + id).val(json);
}

function countMT(id){
    let mess = $("#message-content_" + id).val();
    let posVNChar = checkVNCharacter(mess);
    let MTInfo = "[length] ký tự - [count] tin nhắn (MT)."
    let mtLength = mess.length;
    let isUnicode = 0;
    if (posVNChar != -1) {
        $("#unicode_" + id).val("8");
        isUnicode = 8;
    } else {
        $("#unicode_" + id).val("0");
        isUnicode = 0;
    }
    MTInfo = MTInfo.replace("[length]", mtLength);
    if (isUnicode == 0) {
        $("#message-content_" + id).attr('maxlength','612');
        if (mtLength == 0 || mess == "") {
            MTInfo = MTInfo.replace("[count]", 0);
            $("#countMT_" + id).val(0);
        } else if (mtLength >= 1 && mtLength <= 160) {
            MTInfo = MTInfo.replace("[count]", 1);
            $("#countMT_" + id).val(1);
        } else if (mtLength >= 161 && mtLength <= 306) {
            MTInfo = MTInfo.replace("[count]", 2);
            $("#countMT_" + id).val(2);
        } else if (mtLength >= 307 && mtLength <= 459) {
            MTInfo = MTInfo.replace("[count]", 3);
            $("#countMT_" + id).val(3);
        } else if (mtLength >= 460 && mtLength <= 612) {
            MTInfo = MTInfo.replace("[count]", 4);
            $("#countMT_" + id).val(4);
        }
    } else if (isUnicode == 8) {
        $("#message-content_" + id).attr('maxlength','268');
        if (mtLength == 0 || mess == "") {
            MTInfo = MTInfo.replace("[count]", 0);
            $("#countMT_" + id).val(0);
        } else if (mtLength >= 1 && mtLength <= 70) {
            MTInfo = MTInfo.replace("[count]", 1);
            $("#countMT_" + id).val(1);
        } else if (mtLength >= 71 && mtLength <= 134) {
            MTInfo = MTInfo.replace("[count]", 2);
            $("#countMT_" + id).val(2);
        } else if (mtLength >= 135 && mtLength <= 201) {
            MTInfo = MTInfo.replace("[count]", 3);
            $("#countMT_" + id).val(3);
        } else if (mtLength >= 202 && mtLength <= 268) {
            MTInfo = MTInfo.replace("[count]", 4);
            $("#countMT_" + id).val(4);
        }
    }
    $("#mt-count_"+id).text(MTInfo);
}

function countMTOnPaste(event){
    let clipboardData = event.clipboardData || window.clipboardData;
    let mess = clipboardData.getData('Text');

    //tìm postfix của element
    //nếu ko phân nhóm thì là one
    //có phân nhóm thì là số thứ tự các nhóm
    let id = event.target.id;
    id = id.substring(id.lastIndexOf("_") + 1, id.length);

    let posVNChar = checkVNCharacter(mess);
    let MTInfo = "[length] ký tự - [count] tin nhắn (MT)."
    let mtLength = mess.length;
    let isUnicode = 0;
    if (posVNChar != -1) {
        $("#unicode_" + id).val("8");
        isUnicode = 8;
    } else {
        $("#unicode_" + id).val("0");
        isUnicode = 0;
    }

    if (isUnicode == 0) {
        $("#message-content_" + id).attr('maxlength','612');
        if (mtLength == 0 || mess == "") {
            MTInfo = MTInfo.replace("[count]", 0);
            $("#countMT_" + id).val(0);
        } else if (mtLength >= 1 && mtLength <= 160) {
            MTInfo = MTInfo.replace("[count]", 1);
            $("#countMT_" + id).val(1);
        } else if (mtLength >= 161 && mtLength <= 306) {
            MTInfo = MTInfo.replace("[count]", 2);
            $("#countMT_" + id).val(2);
        } else if (mtLength >= 307 && mtLength <= 459) {
            MTInfo = MTInfo.replace("[count]", 3);
            $("#countMT_" + id).val(3);
        } else if (mtLength >= 460 && mtLength <= 612) {
            MTInfo = MTInfo.replace("[count]", 4);
            $("#countMT_" + id).val(4);
        } else if (mtLength > 612) {
            MTInfo = MTInfo.replace("[count]", 4);
            mtLength = 612;
            $("#countMT_" + id).val(4);
        }
    } else if (isUnicode == 8) {
        $("#message-content_" + id).attr('maxlength','268');
        if (mtLength == 0 || mess == "") {
            MTInfo = MTInfo.replace("[count]", 0);
            $("#countMT_" + id).val(0);
        } else if (mtLength >= 1 && mtLength <= 70) {
            MTInfo = MTInfo.replace("[count]", 1);
            $("#countMT_" + id).val(1);
        } else if (mtLength >= 71 && mtLength <= 134) {
            MTInfo = MTInfo.replace("[count]", 2);
            $("#countMT_" + id).val(2);
        } else if (mtLength >= 135 && mtLength <= 201) {
            MTInfo = MTInfo.replace("[count]", 3);
            $("#countMT_" + id).val(3);
        } else if (mtLength >= 202 && mtLength <= 268) {
            MTInfo = MTInfo.replace("[count]", 4);
            $("#countMT_" + id).val(4);
        } else if (mtLength > 268) {
            MTInfo = MTInfo.replace("[count]", 4);
            mtLength = 268;
            $("#countMT_" + id).val(4);
        }
    }
    MTInfo = MTInfo.replace("[length]", mtLength);
    $("#mt-count_"+id).text(MTInfo);
}


function DownloadFileDataCustomer(obj) {
    var file = document.getElementById('data-Customer').files[0];
    var filename = document.getElementById('data-Customer').files[0].name;
    var fileExtensionDataCustomer = getFileExtension(filename);
    if(fileExtensionDataCustomer == 'csv' || fileExtensionDataCustomer == 'xls'){
        filename = filename.substring(0,filename.length - 4)
    }else {
        filename = filename.substring(0,filename.length - 5)
    }
    let dateTime = getDateAndTime();
    var blob = new Blob([file]);
    var url  = URL.createObjectURL(blob);
    $(obj).attr({ 'download': filename+' '+dateTime+'.csv', 'href': url});
    filename = "";
}

function DownloadFileDataCustomerFrequencyEdit(obj) {
    if(document.getElementById('data-Customer').length > 0) {
        var file = document.getElementById('data-Customer').files[0];
        var filename = document.getElementById('data-Customer').files[0].name;
        var fileExtensionDataCustomer = getFileExtension(filename);
        if(fileExtensionDataCustomer == 'csv' || fileExtensionDataCustomer == 'xls'){
            filename = filename.substring(0,filename.length - 4)
        }else {
            filename = filename.substring(0,filename.length - 5)
        }
        let dateTime = getDateAndTime();
        var blob = new Blob([file]);
        var url = URL.createObjectURL(blob);
        $(obj).attr({'download': filename+' '+dateTime+'.csv', 'href': url});
        filename = "";
    }else {
        var campaignId = $("#campaignId").val();
        $("#link-download-data").attr("href", "/campaignManager/exportDataCustomer?id=" + campaignId + "&type=2");
    }
}
function DownloadFileDataCustomerEventEdit(obj) {
    if(document.getElementById('data-Customer').length > 0) {
        var file = document.getElementById('data-Customer').files[0];
        var filename = document.getElementById('data-Customer').files[0].name;
        var fileExtensionDataCustomer = getFileExtension(filename);
        if(fileExtensionDataCustomer == 'csv' || fileExtensionDataCustomer == 'xls'){
            filename = filename.substring(0,filename.length - 4)
        }else {
            filename = filename.substring(0,filename.length - 5)
        }
        let dateTime = getDateAndTime();
        var blob = new Blob([file]);
        var url = URL.createObjectURL(blob);
        $(obj).attr({'download': filename+' '+dateTime+'.csv', 'href': url});
        filename = "";
    }else {
        var campaignId = $("#campaignId").val();
        $("#link-download-data").attr("href", "/campaignManager/exportDataCustomer?id=" + campaignId + "&type=1");
    }
}

function DownloadFileDataBlacklist(obj) {
    var file = document.getElementById('blacklist-file').files[0];
    var filename = document.getElementById('blacklist-file').files[0].name;
    var fileExtensionDataCustomer = getFileExtension(filename);
    if(fileExtensionDataCustomer == 'csv' || fileExtensionDataCustomer == 'xls'){
        filename = filename.substring(0,filename.length - 4)
    }else {
        filename = filename.substring(0,filename.length - 5)
    }
    let dateTime = getDateAndTime();
    var blob = new Blob([file]);
    var url  = URL.createObjectURL(blob);
    $(obj).attr({ 'download': filename+' '+dateTime+'.csv', 'href': url});
    filename = "";
}

function DownloadFileDataBlacklistEdit(obj, type) {
    if(document.getElementById('blacklist-file').files.length != 0) {
        var file = document.getElementById('blacklist-file').files[0];
        var filename = document.getElementById('blacklist-file').files[0].name;
        var fileExtensionDataCustomer = getFileExtension(filename);
        if(fileExtensionDataCustomer == 'csv' || fileExtensionDataCustomer == 'xls'){
            filename = filename.substring(0,filename.length - 4)
        }else {
            filename = filename.substring(0,filename.length - 5)
        }
        let dateTime = getDateAndTime();
        var blob = new Blob([file]);
        var url = URL.createObjectURL(blob);
        $(obj).attr({'download': filename+' '+dateTime+'.csv', 'href': url});
        filename = "";
    }else {
        var campaignId = $("#campaignId").val();
        $("#link-download-blacklist").attr("href", "/campaignManager/exportDataBlacklist?id=" + campaignId + "&type=" + type);
    }
}

function getDateAndTime(){
    var today = new Date();
    var date = format_two_digits(today.getDate()) + '/' + format_two_digits((today.getMonth() + 1)) + '/' + today.getFullYear();
    var time = format_two_digits(today.getHours()) + ":" + format_two_digits(today.getMinutes()) + ":" + format_two_digits(today.getSeconds());
    var dateTime = date + ' ' + time;
    return dateTime;
}

function copyTargetGroup(id){
    mapDataSubGroup = new Map();
    if(arrayTreeSubTarget.size == 1){
        for (const [keySubTarget, value] of arrayTreeSubTarget) {
            $("#btn-delete-sub-"+keySubTarget).removeAttr("disabled");
        }
    }

    let dataCopy = mapSubTargetWithCriteria.get(id);
    subTargetGroup++;
    let htmlField ="<tr id=\"subTargetGroup_"+subTargetGroup+"\">\n" +
        "                                <td class=\"sgroup_col\">\n" +
        "                                    <div class=\"form-group\">\n" +
        "                                        <input class=\"form-control isDisabled\" name=\"sub-target-group-name\" maxlength='100' id=\"subTargetGroupName_"+subTargetGroup+"\" onkeyup=\"keyupCheckSubtarget("+subTargetGroup+")\" placeholder=\"Nhập tên nhóm\"\n" +
        "                                               type=\"text\">\n" +
        "                                    </div>\n" +
        "                                    <div class=\"btn-group btn_ss\">\n" +
        "                                        <button class=\"btn btn-rounded btn-primary isDisabled\" onclick=\"showSubTargetGroup('modalSubTargetGroup_"+subTargetGroup+"')\" type=\"button\">Thiết lập nhóm\n" +
        "                                        </button>\n" +
        "                                    </div>\n" +
        "                                </td>\n" +
        "                                <td>\n" +
        "                                    <div class=\"form-group\">\n" +
        "                                        <input class=\"form-control isDisabled\" onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" name='sub-target-group-priority' onkeypress='return blockCharacterByInputNumber(event)' id=\"subTargetGroupPriority_"+subTargetGroup+"\" onkeyup=\"keyupCheckSubtarget("+subTargetGroup+")\" placeholder=\"Nhập thứ tự ưu tiên\"\n" +
        "                                               type=\"number\">\n" +
        "                                    </div>\n" +
        "                                </td>\n" +
        "                                <td>\n" +
        "                                    <div class=\"form-group\" style=\"display:none;\" title=\"Đã thiết lập.\" id=\"checkSub_"+subTargetGroup+"\">\n" +
        "                                        <i class=\"ti-check\"></i>\n" +
        "                                    </div>\n" +
        "                                </td>\n" +
        "                                <td class=\"act_ksn\">\n" +
        "                                    <div class=\"btn-group\" style='margin-left: 20px'>\n" +
        "                                        <button class=\"btn btn_ksn btn_ksn_delete btn_cl_black isDisabled\" data-placement=\"top\"\n" +
        "                                                data-toggle=\"tooltip\" title=\"Nhân bản nhóm\" type=\"button\" onclick=\"copyTargetGroup("+subTargetGroup+")\">\n" +
        "                                            <i class=\"ti-files\"></i>\n" +
        "                                        </button>\n" +
        "                                        <button class=\"btn btn_ksn btn_ksn_delete btn_cl_black isDisabled\" data-placement=\"top\"\n" +
        "                                                data-toggle=\"tooltip\" title=\"Xóa nhóm\" id='btn-delete-sub-"+subTargetGroup+"' type=\"button\" onclick=\"deleteSubTarget('subTargetGroup_"+subTargetGroup+"')\">\n" +
        "                                            <i class=\"ti-trash\"></i>\n" +
        "                                        </button>\n" +
        "                                        <button class=\"btn btn_ksn btn_ksn_delete btn_cl_black isDisabled\" data-placement=\"top\"\n" +
        "                                                data-toggle=\"tooltip\" title=\"Thêm nhóm\" type=\"button\" onclick=\"addSubTargetGroup('subTargetGroup_"+subTargetGroup+"')\">\n" +
        "                                            <i class=\"ti-plus\"></i>\n" +
        "                                        </button>\n" +
        "                                    </div>\n" +
        "                                </td>\n" +
        "                            </tr>";
    $("#subTargetGroup_" + id).after(htmlField);

    let subTargetGroupName = $("#subTargetGroupName_" + id).val();
    if(subTargetGroupName != null && subTargetGroupName.length > 0){
        $("#subTargetGroupName_" + subTargetGroup).val(subTargetGroupName  + "_copy");
    }else $("#subTargetGroupName_" + subTargetGroup).val("Nhóm chưa đặt tên_copy");



    let htmlFieldSubModal = "<!--Modal-->\n" +
        "    <div class=\"modal bs-example-modal-lg pop_mlg popup_sg\" data-backdrop=\"static\" data-keyboard=\"false\" data-backdrop=\"static\" data-keyboard=\"false\" id=\"modalSubTargetGroup_"+subTargetGroup+"\"  role=\"dialog\" aria-labelledby=\"myLargeModalLabel\" aria-hidden=\"true\" style=\"display: none;\">\n" +
        "        <div class=\"modal-dialog modal-lg\">\n" +
        "            <div class=\"modal-content\">\n" +
        "                <div class=\"modal-header\">\n" +
        "                    <h4 class=\"modal-title\" id=\"labelSubGroupName_"+subTargetGroup+"\">Nhóm nhỏ</h4>\n" +
        "                    <h6 class=\"modal-title\" ><span class=\"red-clr\" style=\"font-weight: bold\">Quy mô nhóm:</span> <span id=\"msisdnSubGroup_"+subTargetGroup+"\">0 Thuê bao</span></h6>\n" +
        "                    <h6 class=\"modal-title\" ><span class=\"red-clr\" style=\"font-weight: bold\">Tỷ lệ so với nhóm đối tượng:</span><span id=\"ratioSubGroup_"+subTargetGroup+"\">0.00%</span></h6>" +
        "                </div>\n" +
        "                <div class=\"row modal-body p-t-30\">\n" +
        "                    <div class=\"col-lg-9 col-xlg-12 col-md-8 step3_GO_L L_card\">\n" +
        "\n" +
        "                        <div id=\"viewSubTargetGroup_"+subTargetGroup+"\"></div>\n" +
        "\n" +
        "                        <div class=\"btn_main_act btn_L m-t-20\">\n" +
        "                            <div class=\"btn-group\">\n" +
        "                                <button type=\"button\" class=\"btn btn-rounded btn-primary\" onclick=\"countMSISDNSubGroup("+subTargetGroup+")\"><i class=\"ti-reload\"></i> Cập nhật quy mô nhóm</button>\n" +
        "                            </div>\n" +
        "                        </div>\n" +
        "\n" +
        "                        <div class=\"btn_main_act btn_R m-t-20\">\n" +
        "                            <div class=\"btn-group\">\n" +
        "                                <button type=\"button\" class=\"btn btn-rounded btn-primary\" onclick='validateSubTarget("+subTargetGroup+")'>Lưu</button>\n" +
        "                            </div>\n" +
        "                            <div class=\"btn-group\">\n" +
        "                                <button type=\"button\" class=\"btn btn-rounded btn-danger\" onclick=\"closeModal("+subTargetGroup+")\">Hủy</button>\n" +
        "                            </div>\n" +
        "                        </div>\n" +
        "\n" +
        "                    </div>\n" +
        "                </div>\n" +
        "            </div>\n" +
        "            <!-- /.modal-content -->\n" +
        "        </div>\n" +
        "        <!-- /.modal-dialog -->\n" +
        "<input type=\"hidden\" id=\"jsonSubTarget_"+subTargetGroup+"\" name=\"json-sub-target-group\">" +
        "    </div>";
    $("#modalSubTargetGroup_" + id).after(htmlFieldSubModal);



    if(dataCopy == null || dataCopy.length <= 0) {
        $.ajax({
            type: 'GET',
            url: '/SubTargetGroupController/viewSubTargetGroup?id=' + subTargetGroup,
            traditional: true,
            async: false,
            success: function (response) {
                $("#viewSubTargetGroup_" + subTargetGroup).append(response);
            },
            error: function (e) {
                console.log(e);
            },
        });

        criteriaIDSequence++;

        $.ajax({
            type: 'GET',
            url: '/SubTargetGroupController/GetRowLevel1Criteria?currentId=' + criteriaIDSequence + '&parentId=0&subTreeId=' + subTargetGroup,
            traditional: true,
            async: false,
            success: function (response) {
                $("#subSetCriteria_" + subTargetGroup).after(response);
                $("#buttonDelete_" + criteriaIDSequence).attr("disabled", "disabled");
            },
            error: function (e) {
                console.log(e);
            },
        });

        let subCriteriaTree = new SubCriteriaTree("0");
        subCriteriaTree.insert("0", criteriaIDSequence);
        arrayTreeSubTarget.set(subTargetGroup, subCriteriaTree);
    }else {
        $.ajax({
            type: 'GET',
            url: '/SubTargetGroupController/viewSubTargetGroup?id=' + subTargetGroup,
            traditional: true,
            async: false,
            success: function (response) {
                $("#viewSubTargetGroup_" + subTargetGroup).append(response);
            },
            error: function (e) {
                console.log(e);
            },
        });

        let subCriteriaTree = new SubCriteriaTree("0");
        for(let i = 0; i < dataCopy.length; i++) {
            criteriaIDSequence++;
            $.ajax({
                type: 'GET',
                url: '/SubTargetGroupController/GetRowLevel1Criteria?currentId=' + criteriaIDSequence + '&parentId=0&subTreeId=' + subTargetGroup,
                traditional: true,
                async: false,
                success: function (response) {
                    console.log("response: " + response);
                    $("#subSetCriteria_" + subTargetGroup).after(response);
                    $("#buttonDelete_" + criteriaIDSequence).attr("disabled", "disabled");
                },
                error: function (e) {
                    console.log(e);
                },
            });
            subCriteriaTree.insert("0", criteriaIDSequence);

            var classNameCondition = "class-selectCriteriaID_" + criteriaIDSequence;

            let obj = dataCopy[i];
            let criteriaID = obj.criteriaId;
            if(criteriaID == 3 || criteriaID == 4 || criteriaID == 5 || criteriaID == 56 || criteriaID == 60){
                subCriteriaTree.find(criteriaIDSequence).unit = "";
            }
            else subCriteriaTree.find(criteriaIDSequence).unit = obj.unit;
            subCriteriaTree.find(criteriaIDSequence).criteriaName = obj.name;
            subCriteriaTree.find(criteriaIDSequence).criteriaCode = obj.code;
            subCriteriaTree.find(criteriaIDSequence).criteriaId = obj.criteriaId;
            var htmlFieldCopy = "";
            if (criteriaID == 1 || criteriaID == 2 || criteriaID == 6 || criteriaID == 7 || criteriaID == 65 || criteriaID == 66) {
                htmlFieldCopy += "<div class=\"col-lg-3 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                    "                        <select id='operatorSub_"+criteriaIDSequence+"' onchange='changeOperator(this, "+criteriaIDSequence+")' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n" +
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
                    htmlFieldCopy += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input step=\"any\" onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" id='valueSub_"+criteriaIDSequence+"' type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";

                    htmlFieldCopy += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+criteriaIDSequence+"\" style=\"padding-left: 0px !important; padding-right: 0px !important; display: none;\">\n" +
                        "                        <input step=\"any\" onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" id='value_endSub_"+criteriaIDSequence+"' type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                }else {
                    htmlFieldCopy += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" id='valueSub_"+criteriaIDSequence+"' type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";

                    htmlFieldCopy += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+criteriaIDSequence+"\" style=\"padding-left: 0px !important; padding-right: 0px !important; display: none;\">\n" +
                        "                        <input onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" id='value_endSub_"+criteriaIDSequence+"' type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                }



                htmlFieldCopy += "<div class=\"col-lg-2 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                    "                        <input id='unitSub_"+criteriaIDSequence+"'  name='' class=\"form-control\" disabled placeholder=\" " + obj.unit + " \"/>\n" +
                    "                    </div>";
                $("#selectSubTargetCriteriaID_" + criteriaIDSequence).after(htmlFieldCopy);
                $("#operatorSub_"+criteriaIDSequence).selectpicker({
                    style: 'btn-operator'
                });
            } else if (criteriaID == 3 || criteriaID == 4) {
                if (criteriaID == 3) {
                    htmlFieldCopy += "<div class=\"col-lg-9 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n";
                    htmlFieldCopy += "<div class='row' style='margin-left: 30px'>";

                    htmlFieldCopy += "<div class='col-lg-2 custom-control custom-checkbox float-right' style='padding-top: 10px;'>";
                    htmlFieldCopy += "<label class='control-label'>" +
                        "<input type='checkbox' class='form-check-input valueSub_"+criteriaIDSequence+"' value='recall'>" + "Thu hồi số" +
                        "</label>";
                    htmlFieldCopy += "</div>";

                    htmlFieldCopy += "<div class='custom-control custom-checkbox float-right' style='padding-left: 50px; padding-right: 30px; padding-top: 10px;'>";
                    htmlFieldCopy += "<label class='control-label'>" +
                        "<input type='checkbox' class='form-check-input valueSub_"+criteriaIDSequence+"' value='lock2'>" + "Khóa 2 chiều" +
                        "</label>";
                    htmlFieldCopy += "</div>";

                    htmlFieldCopy += "<div class='custom-control custom-checkbox float-right' style='padding-left: 50px; padding-right: 30px; padding-top: 10px;'>";
                    htmlFieldCopy += "<label class='control-label'>" +
                        "<input type='checkbox' class='form-check-input valueSub_"+criteriaIDSequence+"' value='lock1'>" + "Khóa 1 chiều" +
                        "</label>";
                    htmlFieldCopy += "</div>";

                    htmlFieldCopy += "<div class='custom-control custom-checkbox float-right' style='padding-left: 50px; padding-right: 30px; padding-top: 10px;'>";
                    htmlFieldCopy += "<label class='control-label'>" +
                        "<input type='checkbox' class='form-check-input valueSub_"+criteriaIDSequence+"' value='active'>" + "Hoạt động" +
                        "</label>";
                    htmlFieldCopy += "</div>";

                    htmlFieldCopy += "</div>";
                    htmlFieldCopy += "</div>";
                    $("#selectCriteriaID_" + criteriaIDSequence).after(htmlFieldCopy);
                } else if (criteriaID == 4) {
                    htmlFieldCopy += "<div class=\"col-lg-3 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                        "                        <select id='valueSub_"+criteriaIDSequence+"' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n" +
                        "                            <option value=\"PREPAID\">Trả trước</option>\n" +
                        "                            <option value=\"POSTPAID\">Trả sau</option>\n" +
                        "                        </select>\n" +
                        "                    </div>";
                    $("#selectSubTargetCriteriaID_" + criteriaIDSequence).after(htmlFieldCopy);
                    $("#valueSub_"+criteriaIDSequence).selectpicker({
                        style: 'btn-operator'
                    });
                }
            } else if (criteriaID == 5) {
                htmlFieldCopy += "<div class=\"col-lg-3 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                    "                        <select id='operatorSub_"+criteriaIDSequence+"' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n" +
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
                            if (response.code==0){
                                let data = response.data;
                                htmlFieldCopy += "<div class=\"col-lg-3 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                                    "                        <select id='valueSub_"+criteriaIDSequence+"' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n" ;
                                for(let i = 0; i < data.length; i++){
                                    htmlFieldCopy +=  "                            <option value=\""+data[i]+"\">"+data[i]+"</option>\n";
                                }
                                htmlFieldCopy +=   "                        </select>\n" +
                                    "                    </div>";
                            }
                        }
                    },
                    error: function (e) {
                        console.log(e);
                    },
                })

                $("#selectSubTargetCriteriaID_" + criteriaIDSequence).after(htmlFieldCopy);
                $("#operatorSub_"+criteriaIDSequence).selectpicker({
                    style: 'btn-operator'
                });
                $("#valueSub_"+criteriaIDSequence).selectpicker({
                    style: 'btn-operator'
                });

            } else if (criteriaID == 56) {
                htmlFieldCopy += "<div class=\"col-lg-3 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                    "                        <select id='operatorSub_"+criteriaIDSequence+"' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n" +
                    "                            <option value=\"using\">Đang sử dụng</option>\n" +
                    "                            <option value=\"notInUse\">Đang không sử dụng</option>\n" +
                    "                        </select>\n" +
                    "                    </div>";

                htmlFieldCopy += "<div class=\"col-lg-2 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                    "                        <input class=\"form-control\" disabled placeholder=\" gói DATA \"/>\n" +
                    "                    </div>";

                $.ajax({
                    url: '/package-datas/find-active?packageGroup=1',
                    type: 'get',
                    dataType: 'json',
                    async: false,
                    success: function (response) {
                        htmlFieldCopy += "<div class=\"col-lg-3 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                            "                        <select id='valueSub_"+criteriaIDSequence+"' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n" +
                            "                            <option value=\"all\">Tất cả</option>\n";
                        response.forEach(element => {
                            htmlFieldCopy +=  " <option value=\""+element.id+"\">"+element.packageName+"</option>\n";
                        });
                        htmlFieldCopy +=   " </select>\n" +
                            " </div>";
                    },
                    error: function (e) {
                        console.log(e);
                    }
                });
                $("#selectSubTargetCriteriaID_" + criteriaIDSequence).after(htmlFieldCopy);
                $("#operatorSub_"+criteriaIDSequence).selectpicker({
                    style: 'btn-operator'
                });
                $("#valueSub_"+criteriaIDSequence).selectpicker({
                    style: 'btn-operator'
                });

            } else if (criteriaID == 60) {
                htmlFieldCopy += "<div class=\"col-lg-3 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                    "                        <select id='operatorSub_"+criteriaIDSequence+"' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n" +
                    "                            <option value=\"using\">Đang sử dụng</option>\n" +
                    "                            <option value=\"notInUse\">Đang không sử dụng</option>\n" +
                    "                        </select>\n" +
                    "                    </div>";

                htmlFieldCopy += "<div class=\"col-lg-2 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                    "                        <input class=\"form-control\" disabled placeholder=\" gói KMCB \"/>\n" +
                    "                    </div>";

                $.ajax({
                    url: '/package-datas/find-active?packageGroup=2',
                    type: 'get',
                    dataType: 'json',
                    async: false,
                    success: function (response) {
                        htmlFieldCopy += "<div class=\"col-lg-3 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                            "                        <select id='valueSub_"+criteriaIDSequence+"' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n" +
                            "                            <option value=\"all\">Tất cả</option>\n";
                        response.forEach(element => {
                            htmlFieldCopy +=  " <option value=\""+element.id+"\">"+element.packageName+"</option>\n";

                        });
                        htmlFieldCopy +=   "                        </select>\n" +
                            "                    </div>";

                    },
                    error: function (e) {
                        console.log(e);
                    }
                });
                $("#selectSubTargetCriteriaID_" + criteriaIDSequence).after(htmlFieldCopy);
                $("#operatorSub_"+criteriaIDSequence).selectpicker({
                    style: 'btn-operator'
                });
                $("#valueSub_"+criteriaIDSequence).selectpicker({
                    style: 'btn-operator'
                });
            } else if(criteriaID == 57 || criteriaID == 58){
                htmlFieldCopy += "<div class=\"col-lg-2 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                    "                        <select id='operatorSub_"+criteriaIDSequence+"' onchange='changeOperator(this, "+criteriaIDSequence+")' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n" +
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
                    htmlFieldCopy += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input step=\"any\" onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" id='valueSub_"+criteriaIDSequence+"' type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";

                    htmlFieldCopy += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+criteriaIDSequence+"\" style=\"padding-left: 0px !important; padding-right: 0px !important; display: none;\">\n" +
                        "                        <input step=\"any\" onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" id='value_endSub_"+criteriaIDSequence+"' type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                }else {
                    htmlFieldCopy += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" id='valueSub_"+criteriaIDSequence+"' type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";

                    htmlFieldCopy += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+criteriaIDSequence+"\" style=\"padding-left: 0px !important; padding-right: 0px !important; display: none;\">\n" +
                        "                        <input onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" id='value_endSub_"+criteriaIDSequence+"' type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                }

                htmlFieldCopy += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                    "                        <input id='' name='' class=\"form-control\" disabled placeholder=\" " + obj.unit + " \"/>\n" +
                    "                    </div>";

                $.ajax({
                    url: '/package-datas/find-active?packageGroup=1',
                    type: 'get',
                    dataType: 'json',
                    async: false,
                    success: function (response) {
                        htmlFieldCopy += "<div class=\"col-lg-3 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                            "                        <select id='mainServiceSub_"+criteriaIDSequence+"' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n";
                        response.forEach(element => {
                            htmlFieldCopy +=  " <option value=\""+element.id+"\">"+element.packageName+"</option>\n";

                        });
                        htmlFieldCopy +=   "                        </select>\n" +
                            "                    </div>";

                    },
                    error: function (e) {
                        console.log(e);
                    }
                });
                $("#selectSubTargetCriteriaID_" + criteriaIDSequence).after(htmlFieldCopy);
                $("#operatorSub_"+criteriaIDSequence).selectpicker({
                    style: 'btn-operator'
                });
                $("#mainServiceSub_"+criteriaIDSequence).selectpicker({
                    style: 'btn-operator'
                });

            } else if(criteriaID == 61 || criteriaID == 62){
                htmlFieldCopy += "<div class=\"col-lg-2 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                    "                        <select id='operatorSub_"+criteriaIDSequence+"' onchange='changeOperator(this, "+criteriaIDSequence+")' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n" +
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
                    htmlFieldCopy += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input step=\"any\" onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" id='valueSub_"+criteriaIDSequence+"' type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";

                    htmlFieldCopy += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+criteriaIDSequence+"\" style=\"padding-left: 0px !important; padding-right: 0px !important; display: none;\">\n" +
                        "                        <input step=\"any\" onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" id='value_endSub_"+criteriaIDSequence+"' type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                }else {
                    htmlFieldCopy += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" id='valueSub_"+criteriaIDSequence+"' type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";

                    htmlFieldCopy += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_"+criteriaIDSequence+"\" style=\"padding-left: 0px !important; padding-right: 0px !important; display: none;\">\n" +
                        "                        <input onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" id='value_endSub_"+criteriaIDSequence+"' type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                }

                htmlFieldCopy += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                    "                        <input id='' name='' class=\"form-control\" disabled placeholder=\" " + obj.unit + " \"/>\n" +
                    "                    </div>";

                $.ajax({
                    url: '/package-datas/find-active?packageGroup=2',
                    type: 'get',
                    dataType: 'json',
                    async: false,
                    success: function (response) {
                        htmlFieldCopy += "<div class=\"col-lg-3 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                            "                        <select id='mainServiceSub_"+criteriaIDSequence+"' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n";
                        response.forEach(element => {
                            htmlFieldCopy +=  " <option value=\""+element.id+"\">"+element.packageName+"</option>\n";

                        });
                        htmlFieldCopy +=   "                        </select>\n" +
                            "                    </div>";

                    },
                    error: function (e) {
                        console.log(e);
                    }
                });
                $("#selectSubTargetCriteriaID_" + criteriaIDSequence).after(htmlFieldCopy);
                $("#operatorSub_"+criteriaIDSequence).selectpicker({
                    style: 'btn-operator'
                });
                $("#mainServiceSub_"+criteriaIDSequence).selectpicker({
                    style: 'btn-operator'
                });
            }else {
                htmlFieldCopy += "<div class=\"col-lg-2 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                    "                        <select id='operatorSub_" + criteriaIDSequence + "' onchange='changeOperator(this, " + criteriaIDSequence + ")' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n" +
                    "                            <option value=\"=\">Bằng</option>\n" +
                    "                            <option value=\"!=\">Không bằng</option>\n" +
                    "                            <option value=\"<\">Nhỏ hơn</option>\n" +
                    "                            <option value=\">\">Lớn hơn</option>\n" +
                    "                            <option value=\"<=\">Nhỏ hơn hoặc bằng</option>\n" +
                    "                            <option value=\">=\">Lớn hơn hoặc bằng</option>\n" +
                    "                            <option value=\"><\">Trong khoảng</option>\n" +
                    "                        </select>\n" +
                    "                    </div>";

                if (obj.unit == 'Phút' || obj.unit == 'MB') {
                    htmlFieldCopy += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input step=\"any\" onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" id='valueSub_" + criteriaIDSequence + "' type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";

                    htmlFieldCopy += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_" + criteriaIDSequence + "\" style=\"padding-left: 0px !important; padding-right: 0px !important; display: none;\">\n" +
                        "                        <input step=\"any\" onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" id='value_endSub_" + criteriaIDSequence + "' type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                } else {
                    htmlFieldCopy += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" id='valueSub_" + criteriaIDSequence + "' type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";

                    htmlFieldCopy += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_" + criteriaIDSequence + "\" style=\"padding-left: 0px !important; padding-right: 0px !important; display: none;\">\n" +
                        "                        <input onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" id='value_endSub_" + criteriaIDSequence + "' type=\"number\" name='' class=\"form-control\" onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";
                }

                htmlFieldCopy += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                    "                        <input id='' name='' class=\"form-control\" disabled placeholder=\" " + obj.unit + " \"/>\n" +
                    "                    </div>";

                var setupDate = 'setupDateSub_' + criteriaIDSequence;
                var idStartDate = 'setupStartDateSub_' + criteriaIDSequence;
                var idEndDate = 'setupEndDateSub_' + criteriaIDSequence;
                htmlFieldCopy += "<div class=\"col-lg-2 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                    "                        <select id='operatorTimeSub_" + criteriaIDSequence + "' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\" style=\"padding-left: 0px !important; padding-right: 0px !important;\" data-placeholder=\"Chọn giá trị\" onchange=\"changeTime(this, '" + setupDate + "')\">\n" +
                    "                            <option value=\"1\">Trong 60 ngày gần nhất</option>\n" +
                    "                            <option value=\"2\">Trong 30 ngày gần nhất</option>\n" +
                    "                            <option value=\"3\">Trong 7 ngày gần nhất</option>\n" +
                    "                            <option value=\"4\">Trong tháng hiện tại</option>\n" +
                    "                            <option value=\"5\">Trong tháng trước</option>\n" +
                    "                            <option value=\"6\">Trong khoảng thời gian</option>\n" +
                    "                        </select>\n" +
                    "                    </div>";
                htmlFieldCopy += "<div class=\"col-lg-2 " + classNameCondition + " \" style='display: none' id='" + setupDate + "'>\n";
                htmlFieldCopy += "<div class=\"row \" >\n"
                htmlFieldCopy += "<div class=\"col-lg-6 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                    "                        <input name='' onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" id='" + idStartDate + "' class=\"form-control\" type=\"text\" placeholder='Từ ngày' id=\"example-date-input\">\n" +
                    "                    </div>";

                htmlFieldCopy += "<div class=\"col-lg-6 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                    "                        <input  name='' onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" id='" + idEndDate + "' class=\"form-control\" type=\"text\"  placeholder='Đến ngày' id=\"example-date-input\">\n" +
                    "                    </div>";
                htmlFieldCopy += "</div>";
                htmlFieldCopy += "</div>";

                $("#selectSubTargetCriteriaID_" + criteriaIDSequence).after(htmlFieldCopy);

                $("#" + idStartDate).datepicker({format: 'dd/mm/yyyy'}).keypress(function (event) {
                    event.preventDefault(); // prevent keyboard writing but allowing value deletion
                }).bind('paste', function (e) {
                    e.preventDefault()
                });

                $("#" + idEndDate).datepicker({format: 'dd/mm/yyyy'}).keypress(function (event) {
                    event.preventDefault(); // prevent keyboard writing but allowing value deletion
                }).bind('paste', function (e) {
                    e.preventDefault()
                });

                $("#operatorSub_" + criteriaIDSequence).selectpicker({
                    style: 'btn-operator'
                });
                $("#operatorTimeSub_" + criteriaIDSequence).selectpicker({
                    style: 'btn-operator'
                });

            }

            $("#buttonSubTargetId_" + criteriaIDSequence).text(obj.name);

            if (criteriaID == 1 || criteriaID == 2 || criteriaID == 6 || criteriaID == 7 || criteriaID == 65 || criteriaID == 66) {
                $("#operatorSub_"+criteriaIDSequence).selectpicker({
                    style: 'btn-operator'
                });

                $("#operatorSub_"+criteriaIDSequence).val(obj.operator);
                $("#valueSub_"+criteriaIDSequence).val(obj.value);

                if(obj.operator == '><'){
                    $("#value_end_"+criteriaIDSequence).val(obj.valueEnd);
                    $("#div_value_end_"+criteriaIDSequence).show();
                }

                $("#operatorSub_"+criteriaIDSequence).selectpicker('refresh');
            } else if (criteriaID == 3) {
                let arrValueCopy = obj.value.split(";");
                let arrValue = document.getElementsByClassName("valueSub_"+ criteriaIDSequence);
                for(let i = 0; i < arrValue.length; i++){
                    if(arrValueCopy.includes(arrValue[i].value)){
                        arrValue[i].checked = true;
                    }
                }
            }  else if (criteriaID == 4) {
                $("#valueSub_"+criteriaIDSequence).selectpicker({
                    style: 'btn-operator'
                });
                $("#valueSub_"+criteriaIDSequence).val(obj.value);
                $("#valueSub_"+criteriaIDSequence).selectpicker('refresh');
            } else if (criteriaID == 5) {
                $("#operatorSub_"+criteriaIDSequence).selectpicker({
                    style: 'btn-operator'
                });
                $("#valueSub_"+criteriaIDSequence).selectpicker({
                    style: 'btn-operator'
                });

                $("#operatorSub_"+criteriaIDSequence).val(obj.operator);
                $("#valueSub_"+criteriaIDSequence).val(obj.value);

                $("#operatorSub_"+criteriaIDSequence).selectpicker('refresh');
                $("#valueSub_"+criteriaIDSequence).selectpicker('refresh');
            } else if (criteriaID == 56) {
                $("#operatorSub_"+criteriaIDSequence).selectpicker({
                    style: 'btn-operator'
                });
                $("#valueSub_"+criteriaIDSequence).selectpicker({
                    style: 'btn-operator'
                });

                $("#operatorSub_"+criteriaIDSequence).val(obj.operator);
                $("#valueSub_"+criteriaIDSequence).val(obj.value);

                $("#operatorSub_"+criteriaIDSequence).selectpicker('refresh');
                $("#valueSub_"+criteriaIDSequence).selectpicker('refresh');
            } else if (criteriaID == 60) {
                $("#operatorSub_"+criteriaIDSequence).selectpicker({
                    style: 'btn-operator'
                });
                $("#valueSub_"+criteriaIDSequence).selectpicker({
                    style: 'btn-operator'
                });

                $("#operatorSub_"+criteriaIDSequence).val(obj.operator);
                $("#valueSub_"+criteriaIDSequence).val(obj.value);

                $("#operatorSub_"+criteriaIDSequence).selectpicker('refresh');
                $("#valueSub_"+criteriaIDSequence).selectpicker('refresh');
            } else if(criteriaID == 57 || criteriaID == 58){

                $("#operatorSub_"+criteriaIDSequence).selectpicker({
                    style: 'btn-operator'
                });
                $("#mainServiceSub_"+criteriaIDSequence).selectpicker({
                    style: 'btn-operator'
                });
                $("#operatorSub_"+criteriaIDSequence).val(obj.operator);
                $("#valueSub_"+criteriaIDSequence).val(obj.value);
                if(obj.operator == '><'){
                    $("#value_endSub_"+criteriaIDSequence).val(obj.valueEnd);
                    $("#div_value_end_"+criteriaIDSequence).show();
                }
                $("#mainServiceSub_"+criteriaIDSequence).val(obj.serviceCode);

                $("#operatorSub_"+criteriaIDSequence).selectpicker('refresh');
                $("#mainServiceSub_"+criteriaIDSequence).selectpicker('refresh');
            } else if(criteriaID == 61 || criteriaID == 62){
                $("#operatorSub_"+criteriaIDSequence).selectpicker({
                    style: 'btn-operator'
                });
                $("#mainServiceSub_"+criteriaIDSequence).selectpicker({
                    style: 'btn-operator'
                });
                $("#operatorSub_"+criteriaIDSequence).val(obj.operator);
                $("#valueSub_"+criteriaIDSequence).val(obj.value);
                if(obj.operator == '><'){
                    $("#value_endSub_"+criteriaIDSequence).val(obj.valueEnd);
                    $("#div_value_end_"+criteriaIDSequence).show();
                }
                $("#mainServiceSub_"+criteriaIDSequence).val(obj.serviceCode);

                $("#operatorSub_"+criteriaIDSequence).selectpicker('refresh');
                $("#mainServiceSub_"+criteriaIDSequence).selectpicker('refresh');
            } else {
                $("#operatorSub_"+criteriaIDSequence).selectpicker({
                    style: 'btn-operator'
                });
                $("#operatorTimeSub_"+criteriaIDSequence).selectpicker({
                    style: 'btn-operator'
                });

                $("#operatorSub_"+criteriaIDSequence).val(obj.operator);
                $("#valueSub_"+criteriaIDSequence).val(obj.value);
                if(obj.operator == '><'){
                    $("#value_endSub_"+criteriaIDSequence).val(obj.valueEnd);
                    $("#div_value_end_"+criteriaIDSequence).show();
                }
                $("#operatorTimeSub_"+criteriaIDSequence).val(obj.operatorTime);

                if(obj.operatorTime == "6"){
                    $("#"+setupDate).show();
                    $("#setupStartDateSub_"+criteriaIDSequence).val(obj.startTime);
                    $("#setupEndDateSub_"+criteriaIDSequence).val(obj.endTime);
                }
                $("#operatorSub_"+criteriaIDSequence).selectpicker('refresh');
                $("#operatorTimeSub_"+criteriaIDSequence).selectpicker('refresh');
            }
        }

        let subName = $("#subTargetGroupName_" + subTargetGroup).val();
        let subJson = $("#jsonSubTarget_" + subTargetGroup).val();
        let subPrority = $("#subTargetGroupPriority_" + subTargetGroup).val();

        let isDuplicateMsisdn = $("#is-duplicate-msisdn").is(":checked");
        if(isDuplicateMsisdn){
            if(subName != null && subName.length > 0 && subJson != null && subJson.length > 0 && subPrority != null && subPrority.length > 0){
                $("#checkSub_" + subTargetGroup).show();
            }
        }else {
            if(subName != null && subName.length > 0 && subJson != null && subJson.length > 0){
                $("#checkSub_" + subTargetGroup).show();
            }
        }

        arrayTreeSubTarget.set(subTargetGroup, subCriteriaTree);

        checkCriteriaSubTarget(subTargetGroup);

    }
}


function showSubTargetGroup(modal){

    let modalId = modal.split("_")[1];
    let name = $("#subTargetGroupName_" + modalId ).val();
    if(name !== undefined && name != null && name.length > 0) $("#labelSubGroupName_"+ modalId ).text(name);
    else $("#labelSubGroupName_"+ modalId ).text("(Nhóm chưa đặt tên)");

    let groupSize = $("#groupSize").text();
    let ratio = $("#ratio").val();
    let msisdnSubGroup = $("#msisdnSubGroup_" + modalId ).text();
    let ratioSubGroup = $("#ratioSubGroup_" + modalId ).text();
    if(msisdnSubGroup.trim() == '0 Thuê bao'){

        var today = new Date();
        var date = today.getDate() + '-' + (today.getMonth() + 1) + '-' + today.getFullYear();
        var time = today.getHours() + ":" + today.getMinutes() + ":" + today.getSeconds();
        var dateTime = date + ' ' + time;

        $("#msisdnSubGroup_" + modalId ).text(groupSize + " Thuê bao");
        $("#ratioSubGroup_" + modalId ).text("100.00 %");
        $("#labelGroupSize_" + modalId).attr("title", "Cập nhật cuối: "+ dateTime);
        $("#labelRatio_" + modalId).attr("title", "Cập nhật cuối: "+ dateTime);
    }else {

    }
    //Nếu đã parse ra json thì load lại json
    let jsonData = $('#jsonSubTarget_' + modalId).val();
    if(jsonData != null && jsonData.length > 0){
        var formData = new FormData();
        formData.append("jsonSubGroup", jsonData);
        var csrfToken = $("#csrf-input").val();
        $('#subSetCriteria_' + modalId).nextAll('div').remove();

        $.ajax({
            type: "POST",
            enctype : 'multipart/form-data',
            url: '/SubTargetGroupController/parseStringJSONCriteriaToArray',
            headers: {
                'X-CSRF-Token': csrfToken
            },
            data: formData,
            processData: false,
            contentType: false,
            timeout: 120000,
            async: false,
            success: function (response) {
                let dataSelectedValue = JSON.parse(response).data;
                let arrayData = [];
                let subCriteriaTree = new SubCriteriaTree("0");
                for(let j = 0; j < dataSelectedValue.length; j++){
                    let criteriaNode = JSON.parse(dataSelectedValue[j].selectedValue);
                    arrayData.push(criteriaNode);
                    subCriteriaTree.insert("0", criteriaNode.key);
                    if(criteriaIDSequence < criteriaNode.key) criteriaIDSequence = criteriaNode.key;
                    processCriteriaSubGroup(subCriteriaTree, dataSelectedValue[j], 'viewSubTargetGroup_' + modalId, modalId);
                }
                arrayTreeSubTarget.set(parseInt(modalId), subCriteriaTree);
                mapSubTargetWithCriteria.set(modalId, arrayData);
                //disable button xoa tieu chi trong nhom con neu chi co 1 tieu chi
                if (arrayData.length ==  1){
                    $("#viewSubTargetGroup_" + modalId).find(".delete-sub-target").attr("disabled", "disabled");
                }
            },
            error: function (response) {
                console.log("da co loi xay ra:" +response)
            }
        })
    }


    $("#" + modal).modal('show');
}

function checkChangeData(){
    let check = true;
    for (const [key, value] of mapDataSubGroup) {
        let dataFromHTML;
        if (key == "sub-target-group-radio") {
            dataFromHTML = $('input[name="sub-target-group-radio"]:checked').val();
        } else if (key == "is-duplicate-msisdn") {
            dataFromHTML = $("#is-duplicate-msisdn").is(":checked");
        } else if (key == "package-group"){
            dataFromHTML = $("#package-group").val();
        }else {
            dataFromHTML = $("#" + key).val();
        }
        if(value != dataFromHTML) return false;
    }
    return check;
}

function changeTime(obj, setupDate) {
    var value = $(obj).val();
    if (value == 6) {
        $("#" + setupDate).show();
    } else {
        $("#" + setupDate).hide();

    }
}

function changeOperator(obj, key){
    let operator = $(obj).val();
    if(operator == '><'){
        $("#div_value_end_" + key).show();
    }else {
        $("#div_value_end_" + key).hide();
    }
}

function countMSISDNMainGroup(){
    var csrfToken = $("#csrf-input").val();
    let typeTargetGroup = $("#type-target-group").val();
    let typeInputBlacklist = $("#type-input-blacklist").val();
    Swal.fire({
        position: 'top',
        type: 'info',
        title: 'CampaignX Thông báo',
        text: "Hệ thống đang cập nhật số lượng thuê bao của nhóm đối tượng. Vui lòng chờ trong ít phút",
        showConfirmButton: false,
        timer: 5000
    })
    if(typeTargetGroup == 1){
        submitCriteriaSetup();
        let jsonMainGroup = $("#jsonData").val();
        let dataBlacklist = document.getElementById('blacklist-file').files[0];
        let idGroupBlacklist = $("#target-group-id").val();

        var formData = new FormData();
        formData.append("jsonMainGroup", jsonMainGroup);
        formData.append("typeInputBlacklist", typeInputBlacklist);
        formData.append("dataBlacklist", dataBlacklist);
        formData.append("idGroupBlacklist", idGroupBlacklist);

        $.ajax({
            type: "POST",
            enctype : 'multipart/form-data',
            url: '/campaignManager/countMSISDNFromMainGroupByJSONCriteria',
            headers: {
                'X-CSRF-Token': csrfToken
            },
            data: formData,
            processData: false,
            contentType: false,
            timeout: 120000,
            async: true,
            success: function (response) {
                console.log("response:" +response);
                let jsonData = JSON.parse(response);
                let dta = jsonData.data;
                $("#groupSize").text(dta.count);
                $("#ratio").text(dta.ratio + " %");
            },
            error: function (response) {
                console.log("da co loi xay ra:" +response)
            }
        })
    }else if(typeTargetGroup == 2){

    }else if(typeTargetGroup == 3){
        let dataTargetGroup = document.getElementById('data-Customer').files[0];
        let dataBlacklist = document.getElementById('blacklist-file').files[0];
        let idGroupBlacklist = $("#target-group-id").val();

        var formData = new FormData();
        formData.append("dataTargetGroup", dataTargetGroup);
        formData.append("typeInputBlacklist", typeInputBlacklist);
        formData.append("dataBlacklist", dataBlacklist);
        formData.append("idGroupBlacklist", idGroupBlacklist);

        $.ajax({
            type: "POST",
            enctype : 'multipart/form-data',
            url: '/campaignManager/countMSISDNFromMainGroupByFile',
            headers: {
                'X-CSRF-Token': csrfToken
            },
            data: formData,
            processData: false,
            contentType: false,
            timeout: 120000,
            async: true,
            success: function (response) {
                console.log("response:" +response);
                let jsonData = JSON.parse(response);
                let dta = jsonData.data;
                $("#groupSize").text(dta.count);
                $("#ratio").text(dta.ratio + " %");
            },
            error: function (response) {
                console.log("da co loi xay ra:" +response)
            }
        })
    }else if(typeTargetGroup == 4){

        submitCriteriaSetup();
        let jsonMainGroup = $("#jsonData").val();
        let dataTargetGroup = document.getElementById('data-Customer').files[0];
        let dataBlacklist = document.getElementById('blacklist-file').files[0];
        let idGroupBlacklist = $("#target-group-id").val();


        var formData = new FormData();
        formData.append("dataTargetGroup", dataTargetGroup);
        formData.append("jsonMainGroup", jsonMainGroup);
        formData.append("typeInputBlacklist", typeInputBlacklist);
        formData.append("dataBlacklist", dataBlacklist);
        formData.append("idGroupBlacklist", idGroupBlacklist);

        $.ajax({
            type: "POST",
            enctype : 'multipart/form-data',
            url: '/campaignManager/countMSISDNFromMainGroupByFileJoinJSONCriteria',
            headers: {
                'X-CSRF-Token': csrfToken
            },
            data: formData,
            processData: false,
            contentType: false,
            timeout: 120000,
            async: true,
            success: function (response) {
                console.log("response:" +response);
                let jsonData = JSON.parse(response);
                let dta = jsonData.data;
                $("#groupSize").text(dta.count);
                $("#ratio").text(dta.ratio + " %");
            },
            error: function (response) {
                console.log("da co loi xay ra:" +response)
            }
        })
    } else if(typeTargetGroup == 6){
        let jsonMainGroup = $("#input-target-group-id").val();
        let dataBlacklist = document.getElementById('blacklist-file').files[0];
        let idGroupBlacklist = $("#target-group-id").val();


        var formData = new FormData();
        formData.append("dataTargetGroup", jsonMainGroup);
        formData.append("typeInputBlacklist", typeInputBlacklist);
        formData.append("dataBlacklist", dataBlacklist);
        formData.append("idGroupBlacklist", idGroupBlacklist);

        $.ajax({
            type: "POST",
            enctype : 'multipart/form-data',
            url: '/campaignManager/countMSISDNFromMainGroupByGroupId',
            headers: {
                'X-CSRF-Token': csrfToken
            },
            data: formData,
            processData: false,
            contentType: false,
            timeout: 120000,
            async: true,
            success: function (response) {
                console.log("response:" +response);
                let jsonData = JSON.parse(response);
                let dta = jsonData.data;
                $("#groupSize").text(dta.count);
                $("#ratio").text(dta.ratio + " %");
            },
            error: function (response) {
                console.log("da co loi xay ra:" +response)
            }
        })
    }

    var today = new Date();
    var date = today.getDate() + '/' + (today.getMonth() + 1) + '/' + today.getFullYear();
    var time = format_two_digits(today.getHours()) + ":" + format_two_digits(today.getMinutes()) + ":" + format_two_digits(today.getSeconds());
    var dateTime = date + ' ' + time;

    $(".label-ratio").attr("title", "Cập nhật cuối: "+ dateTime);
    $(".label-groupSize").attr("title", "Cập nhật cuối: "+ dateTime);
}

function countMSISDNSubGroup(obj){

    let check = checkCriteriaSubTarget(obj);
    var csrfToken = $("#csrf-input").val();
    let jsonSubGroup = $("#jsonSubTarget_"+obj).val();
    let typeTargetGroup = $("#type-target-group").val();
    let typeInputBlacklist = $("#type-input-blacklist").val();

    if(check){
        Swal.fire({
            position: 'top',
            type: 'info',
            title: 'Thông báo',
            text: "Hệ thống đang cập nhật số lượng thuê bao của nhóm đối tượng. Vui lòng chờ trong ít phút.",
            showConfirmButton: false,
            timer: 5000
        });

        if(typeTargetGroup == 1){
            submitCriteriaSetup();
            let jsonMainGroup = $("#jsonData").val();
            let dataBlacklist = document.getElementById('blacklist-file').files[0];
            let idGroupBlacklist = $("#target-group-id").val();

            var formData = new FormData();
            formData.append("jsonMainGroup", jsonMainGroup);
            formData.append("typeInputBlacklist", typeInputBlacklist);
            formData.append("dataBlacklist", dataBlacklist);
            formData.append("idGroupBlacklist", idGroupBlacklist);
            formData.append("jsonSubGroup", jsonSubGroup);

            $.ajax({
                type: "POST",
                enctype : 'multipart/form-data',
                url: '/campaignManager/countMSISDNFromSubGroupByJSONCriteria',
                headers: {
                    'X-CSRF-Token': csrfToken
                },
                data: formData,
                processData: false,
                contentType: false,
                timeout: 120000,
                async: true,
                success: function (response) {
                    console.log("response:" +response);
                    let jsonData = JSON.parse(response);
                    let dta = jsonData.data;
                    $("#msisdnSubGroup_" + obj).text(dta.count + " Thuê bao");
                    $("#ratioSubGroup_" + obj).text(dta.ratio + " %");

                },
                error: function (response) {
                    console.log("da co loi xay ra:" +response)
                }
            })
        }else if(typeTargetGroup == 2){

        }else if(typeTargetGroup == 3){
            let dataTargetGroup = document.getElementById('data-Customer').files[0];
            let dataBlacklist = document.getElementById('blacklist-file').files[0];
            let idGroupBlacklist = $("#target-group-id").val();

            var formData = new FormData();
            formData.append("dataTargetGroup", dataTargetGroup);
            formData.append("typeInputBlacklist", typeInputBlacklist);
            formData.append("dataBlacklist", dataBlacklist);
            formData.append("idGroupBlacklist", idGroupBlacklist);
            formData.append("jsonSubGroup", jsonSubGroup);

            $.ajax({
                type: "POST",
                enctype : 'multipart/form-data',
                url: '/campaignManager/countMSISDNSubMainGroupByFile',
                headers: {
                    'X-CSRF-Token': csrfToken
                },
                data: formData,
                processData: false,
                contentType: false,
                timeout: 120000,
                async: true,
                success: function (response) {
                    console.log("response:" +response);
                    let jsonData = JSON.parse(response);
                    let dta = jsonData.data;
                    $("#msisdnSubGroup_" + obj).text(dta.count + " Thuê bao");
                    $("#ratioSubGroup_" + obj).text(dta.ratio + " %");
                },
                error: function (response) {
                    console.log("da co loi xay ra:" +response)
                }
            })
        }else if(typeTargetGroup == 4){

            submitCriteriaSetup();
            let jsonMainGroup = $("#jsonData").val();
            let dataTargetGroup = document.getElementById('data-Customer').files[0];
            let dataBlacklist = document.getElementById('blacklist-file').files[0];
            let idGroupBlacklist = $("#target-group-id").val();


            var formData = new FormData();
            formData.append("dataTargetGroup", dataTargetGroup);
            formData.append("jsonMainGroup", jsonMainGroup);
            formData.append("typeInputBlacklist", typeInputBlacklist);
            formData.append("dataBlacklist", dataBlacklist);
            formData.append("idGroupBlacklist", idGroupBlacklist);
            formData.append("jsonSubGroup", jsonSubGroup);

            $.ajax({
                type: "POST",
                enctype : 'multipart/form-data',
                url: '/campaignManager/countMSISDNSubMainGroupByFileJoinJSONCriteria',
                headers: {
                    'X-CSRF-Token': csrfToken
                },
                data: formData,
                processData: false,
                contentType: false,
                timeout: 120000,
                async: true,
                success: function (response) {
                    console.log("response:" +response);
                    let jsonData = JSON.parse(response);
                    let dta = jsonData.data;
                    $("#msisdnSubGroup_" + obj).text(dta.count + " Thuê bao");
                    $("#ratioSubGroup_" + obj).text(dta.ratio + " %");
                },
                error: function (response) {
                    console.log("da co loi xay ra:" +response)
                }
            })
        }else if(typeTargetGroup == 6){

            let dataTargetGroup = $("#input-target-group-id").val();
            let dataBlacklist = document.getElementById('blacklist-file').files[0];
            let idGroupBlacklist = $("#target-group-id").val();


            var formData = new FormData();
            formData.append("dataTargetGroup", dataTargetGroup);
            formData.append("typeInputBlacklist", typeInputBlacklist);
            formData.append("dataBlacklist", dataBlacklist);
            formData.append("idGroupBlacklist", idGroupBlacklist);
            formData.append("jsonSubGroup", jsonSubGroup);

            $.ajax({
                type: "POST",
                enctype : 'multipart/form-data',
                url: '/campaignManager/countMSISDNSubMainGroupByGroupId',
                headers: {
                    'X-CSRF-Token': csrfToken
                },
                data: formData,
                processData: false,
                contentType: false,
                timeout: 120000,
                async: true,
                success: function (response) {
                    console.log("response:" +response);
                    let jsonData = JSON.parse(response);
                    let dta = jsonData.data;
                    $("#msisdnSubGroup_" + obj).text(dta.count + " Thuê bao");
                    $("#ratioSubGroup_" + obj).text(dta.ratio + " %");
                },
                error: function (response) {
                    console.log("da co loi xay ra:" +response)
                }
            })
        }

        var today = new Date();
        var date = format_two_digits(today.getDate()) + '/' + format_two_digits((today.getMonth() + 1)) + '/' + today.getFullYear();
        var time = format_two_digits(today.getHours()) + ":" + format_two_digits(today.getMinutes()) + ":" + format_two_digits(today.getSeconds());
        var dateTime = date + ' ' + time;
        $("#labelGroupSize_" + obj).attr("title", "Cập nhật cuối: "+ dateTime);
        $("#labelRatio_" + obj).attr("title", "Cập nhật cuối: "+ dateTime);

    }
}

//step 2 - them nhom tieu chi con
function addSubTargetGroup(currentObj){
    mapDataSubGroup = new Map();
    if(arrayTreeSubTarget.size == 1){
        for (const [keySubTarget, value] of arrayTreeSubTarget) {
            $("#btn-delete-sub-"+keySubTarget).removeAttr("disabled");
        }
    }

    subTargetGroup++;
    let htmlField ="<tr id=\"subTargetGroup_"+subTargetGroup+"\">\n" +
        "                                <td class=\"sgroup_col\">\n" +
        "                                    <div class=\"form-group\">\n" +
        "                                        <input class=\"form-control isDisabled\" name='sub-target-group-name' maxlength='100' id=\"subTargetGroupName_"+subTargetGroup+"\" onkeyup=\"keyupCheckSubtarget("+subTargetGroup+")\" placeholder=\"Nhập tên nhóm\"\n" +
        "                                               type=\"text\">\n" +
        "                                    </div>\n" +
        "                                    <div class=\"btn-group btn_ss\">\n" +
        "                                        <button class=\"btn btn-rounded btn-primary isDisabled\" onclick=\"showSubTargetGroup('modalSubTargetGroup_"+subTargetGroup+"')\" type=\"button\">Thiết lập nhóm\n" +
        "                                        </button>\n" +
        "                                    </div>\n" +
        "                                </td>\n" +
        "                                <td>\n" +
        "                                    <div class=\"form-group\">\n" +
        "                                        <input class=\"form-control isDisabled\" onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" onkeypress='return blockCharacterDotByInputNumber(event)' name='sub-target-group-priority' id=\"subTargetGroupPriority_"+subTargetGroup+"\" onkeyup=\"keyupCheckSubtarget("+subTargetGroup+")\" placeholder=\"Nhập thứ tự ưu tiên\"\n" +
        "                                               type=\"number\">\n" +
        "                                    </div>\n" +
        "                                </td>\n" +
        "                                <td>\n" +
        "                                    <div class=\"form-group\" style=\"display:none;\" title=\"Đã thiết lập.\" id=\"checkSub_"+subTargetGroup+"\">\n" +
        "                                        <i class=\"ti-check\"></i>\n" +
        "                                    </div>\n" +
        "                                </td>\n" +
        "                                <td class=\"act_ksn\">\n" +
        "                                    <div class=\"btn-group\" style=\"margin-left: 20px\">\n" +
        "                                        <button class=\"btn btn_ksn btn_ksn_delete btn_cl_black isDisabled\" data-placement=\"top\"\n" +
        "                                                data-toggle=\"tooltip\" title=\"Nhân bản nhóm\" onclick=\"copyTargetGroup("+subTargetGroup+")\" type=\"button\">\n" +
        "                                            <i class=\"ti-files\"></i>\n" +
        "                                        </button>\n" +
        "                                        <button class=\"btn btn_ksn btn_ksn_delete btn_cl_black isDisabled\" data-placement=\"top\"\n" +
        "                                                data-toggle=\"tooltip\" title=\"Xóa nhóm\" id=\"btn-delete-sub-"+subTargetGroup+"\" type=\"button\" onclick=\"deleteSubTarget('subTargetGroup_"+subTargetGroup+"')\">\n" +
        "                                            <i class=\"ti-trash\"></i>\n" +
        "                                        </button>\n" +
        "                                        <button class=\"btn btn_ksn btn_ksn_delete btn_cl_black isDisabled\" data-placement=\"top\"\n" +
        "                                                data-toggle=\"tooltip\" title=\"Thêm nhóm\" type=\"button\" onclick=\"addSubTargetGroup('subTargetGroup_"+subTargetGroup+"')\">\n" +
        "                                            <i class=\"ti-plus\"></i>\n" +
        "                                        </button>\n" +
        "                                    </div>\n" +
        "                                </td>\n" +
        "                            </tr>";

    let subTargetGroupBefore = currentObj.split("_")[1];
    $("#subTargetGroup_" + subTargetGroupBefore).after(htmlField);

    let htmlFieldSubModal = "<!--Modal-->\n" +
        "    <div class=\"modal bs-example-modal-lg pop_mlg popup_sg\" data-backdrop=\"static\" data-keyboard=\"false\" id=\"modalSubTargetGroup_"+subTargetGroup+"\" role=\"dialog\" aria-labelledby=\"myLargeModalLabel\" aria-hidden=\"true\" style=\"display: none;\">\n" +
        "        <div class=\"modal-dialog modal-lg\">\n" +
        "            <div class=\"modal-content\">\n" +
        "                <div class=\"modal-header\">\n" +
        "                    <h4 class=\"modal-title\" id=\"labelSubGroupName_"+subTargetGroup+"\">Nhóm nhỏ</h4>\n" +
        "                    <h6 class=\"modal-title\" ><span class=\"red-clr\" id=\"labelGroupSize_"+subTargetGroup+"\" style=\"font-weight: bold\">Quy mô nhóm:</span> <span id=\"msisdnSubGroup_"+subTargetGroup+"\">0 Thuê bao</span></h6>\n" +
        "                    <h6 class=\"modal-title\" ><span class=\"red-clr\" id=\"labelRatio_"+subTargetGroup+"\" style=\"font-weight: bold\">Tỷ lệ so với nhóm đối tượng:</span><span id=\"ratioSubGroup_"+subTargetGroup+"\">0.00%</span></h6>" +
        "                </div>\n" +
        "                <div class=\"row modal-body p-t-30\">\n" +
        "                    <div class=\"col-lg-9 col-xlg-12 col-md-8 step3_GO_L L_card\">\n" +
        "\n" +
        "                        <div id=\"viewSubTargetGroup_"+subTargetGroup+"\"></div>\n" +
        "\n" +
        "                        <div class=\"btn_main_act btn_L m-t-20\">\n" +
        "                            <div class=\"btn-group\">\n" +
        "                                <button type=\"button\" class=\"btn btn-rounded btn-primary\" onclick=\"countMSISDNSubGroup("+subTargetGroup+")\"><i class=\"ti-reload\"></i> Cập nhật quy mô nhóm</button>\n" +
        "                            </div>\n" +
        "                        </div>\n" +
        "\n" +
        "                        <div class=\"btn_main_act btn_R m-t-20\">\n" +
        "                            <div class=\"btn-group\">\n" +
        "                                <button type=\"button\" class=\"btn btn-rounded btn-primary\" onclick='validateSubTarget("+subTargetGroup+")'>Lưu</button>\n" +
        "                            </div>\n" +
        "                            <div class=\"btn-group\">\n" +
        "                                <button type=\"button\" class=\"btn btn-rounded btn-danger\" onclick=\"closeModal("+subTargetGroup+")\">Hủy</button>\n" +
        "                            </div>\n" +
        "                        </div>\n" +
        "\n" +
        "                    </div>\n" +
        "                </div>\n" +
        "            </div>\n" +
        "            <!-- /.modal-content -->\n" +
        "        </div>\n" +
        "        <!-- /.modal-dialog -->\n" +
        "<input type=\"hidden\" id=\"jsonSubTarget_"+subTargetGroup+"\" name=\"json-sub-target-group\">" +
        "    </div>";
    $("#modalSubTargetGroup_" + subTargetGroupBefore).after(htmlFieldSubModal);

    $.ajax({
        type: 'GET',
        url: '/SubTargetGroupController/viewSubTargetGroup?id='+subTargetGroup,
        traditional: true,
        async: false,
        success: function (response) {
            $("#viewSubTargetGroup_"+subTargetGroup).append(response);
        },
        error: function (e) {
            console.log(e);
        },
    });

    criteriaIDSequence++;

    $.ajax({
        type: 'GET',
        url: '/SubTargetGroupController/GetRowLevel1Criteria?currentId='+criteriaIDSequence + '&parentId=0&subTreeId=' + subTargetGroup,
        traditional: true,
        async: false,
        success: function (response) {
            $("#subSetCriteria_"+subTargetGroup).after(response);
            $("#buttonDelete_"+criteriaIDSequence).attr("disabled", "disabled");
        },
        error: function (e) {
            console.log(e);
        },
    });

    let subCriteriaTree = new SubCriteriaTree("0");
    subCriteriaTree.insert("0", criteriaIDSequence);
    arrayTreeSubTarget.set(subTargetGroup, subCriteriaTree);

}

function changeCheckboxDuplicateMessage(){
    for (const [key, value] of arrayTreeSubTarget) {
        checkSubtarget(key);
    }
}

function closeModal(id){
    let groupName = $("#labelSubGroupName_" + id).text();
    Swal.fire({
        position: 'top',
        title: 'Thông báo',
        text: "Bạn muốn hủy thiết lập tiêu chí bổ sung cho nhóm " + groupName + " ?",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Có',
        cancelButtonText: 'Hủy',
    }).then((result) => {
        if (result.value) {
            $("#modalSubTargetGroup_" + id).modal('hide');
            setTreesubTargetCanceled.add(id);
            let json = $("#jsonSubTarget_" + id).val();
            if(json != null && json.length > 0){
                checkDataWhenSubCancleed = true;
                $("#checkSub_" + id).show()
            }else $("#checkSub_" + id).hide();
        }
    });
}

function deleteSubTarget(obj){
    mapDataSubGroup = new Map();
    $("#"+obj).remove();
    let key = parseInt(obj.split("_")[1]);
    arrayTreeSubTarget.delete(key);
    setTreesubTargetCanceled.delete(key);
    $("#jsonSubTarget_"+key).remove();
    if(arrayTreeSubTarget.size == 1){
        for (const [keySubTarget, value] of arrayTreeSubTarget) {
            $("#btn-delete-sub-"+keySubTarget).prop("disabled", true);
        }
    }
}

function processCriteriaSubGroup(subCriteriaTree, criteria, idDiv, subTreeId) {
    let criteriaNode = JSON.parse(criteria.selectedValue);
    let url;
    let urlView;
    let criteriaButtonID;
    let criteriaSelectID;
    if (criteria.levelCriteria == 1) {
        url = '/SubTargetGroupController/GetRowLevel1Criteria';
        criteriaButtonID = "buttonSubTargetId_" + criteriaNode.key;
        criteriaSelectID = "selectSubTargetCriteriaID_" + criteriaNode.key;

        $.ajax({
            type: 'GET',
            url: url + '?currentId=' + criteriaNode.key + '&parentId=' + criteriaNode.parentKey + '&subTreeId=' + subTreeId,
            traditional: true,
            async: false,
            success: function (response) {
                if (response) {
                    $("#subSetCriteria_" + subTreeId).parent().append(response);
                    editChonTieuChiSubTarget(subCriteriaTree, criteria.idBigdataCriteria, criteria.criteriaName, criteriaButtonID,
                        criteriaSelectID, criteriaNode.key, criteriaNode, subTreeId);
                }
            },
            error: function (e) {
                console.log(e);
            },
        });
    }
}

function editChonTieuChiSubTarget(tree, criteriaID, criteriaName, idElement, selectCriteriaID, currentNodeID, criteriaNode, subGroupId) {
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
                tree.find(currentNodeID).unit = "";
            } else tree.find(currentNodeID).unit = obj.unit;
            tree.find(currentNodeID).criteriaName = criteriaName;
            tree.find(currentNodeID).criteriaCode = obj.code;
            tree.find(currentNodeID).criteriaId = criteriaID;
            tree.find(currentNodeID).operator = criteriaNode.operator;
            tree.find(currentNodeID).value = criteriaNode.value;
            tree.find(currentNodeID).valueEnd = criteriaNode.valueEnd;
            tree.find(currentNodeID).operatorTime = criteriaNode.operatorTime;
            tree.find(currentNodeID).startTime = criteriaNode.startTime;
            tree.find(currentNodeID).endTime = criteriaNode.endTime;
            tree.find(currentNodeID).serviceCode = criteriaNode.serviceCode;
            console.log("operator", criteriaNode.operator)
            var htmlField = "";
            if (criteriaID == 1 || criteriaID == 2 || criteriaID == 6 || criteriaID == 7 || criteriaID == 65 || criteriaID == 66) {
                htmlField += "<div class=\"col-lg-3 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                    "                        <select id='operatorSub_" + currentNodeID + "' name='" + obj.code + "' onchange='changeOperator(this, "+currentNodeID+")' data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" class=\"form-control selectpicker\" >\n" +
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
                        "                        <input onpaste=\"return false;\" step=\"any\" ondrop=\"return false;\" autocomplete=\"off\" id='valueSub_" + currentNodeID + "' type='number' name='' class=\"form-control\" value='" + criteriaNode.value + "'  onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";

                    if (criteriaNode.operator == '><') {
                        htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_" + currentNodeID + "\" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                            "                        <input onpaste=\"return false;\" step=\"any\" ondrop=\"return false;\" autocomplete=\"off\" id='value_endSub_" + currentNodeID + "' type=\"number\" name='' class=\"form-control\" value='" + criteriaNode.valueEnd + "' onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                            "                    </div>";
                    } else {
                        htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_" + currentNodeID + "\" style=\"padding-left: 0px !important; display: none; padding-right: 0px !important;\">\n" +
                            "                        <input onpaste=\"return false;\" step=\"any\" ondrop=\"return false;\" autocomplete=\"off\" id='value_endSub_" + currentNodeID + "' type='number' name='' class=\"form-control\"  onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                            "                    </div>";
                    }
                }else {
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" id='valueSub_" + currentNodeID + "' type='number' name='' class=\"form-control\" value='" + criteriaNode.value + "'  onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";

                    if (criteriaNode.operator == '><') {
                        htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_" + currentNodeID + "\" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                            "                        <input onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" id='value_endSub_" + currentNodeID + "' type=\"number\" name='' class=\"form-control\" value='" + criteriaNode.valueEnd + "' onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                            "                    </div>";
                    } else {
                        htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_" + currentNodeID + "\" style=\"padding-left: 0px !important; display: none; padding-right: 0px !important;\">\n" +
                            "                        <input onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" id='value_endSub_" + currentNodeID + "' type='number' name='' class=\"form-control\"  onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                            "                    </div>";
                    }
                }

                htmlField += "<div class=\"col-lg-2 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                    "                        <input id='unitSub_" + currentNodeID + "'  name='' class=\"form-control\" disabled placeholder=\" " + obj.unit + " \"/>\n" +
                    "                    </div>";
                $("#" + selectCriteriaID).after(htmlField);
                $("#operatorSub_"+currentNodeID).selectpicker({
                    style: 'btn-operator'
                });
            } else if (criteriaID == 3 || criteriaID == 4) {
                if (criteriaID == 3) {
                    let value = criteriaNode.value;
                    htmlField += "<div class=\"col-lg-9 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n";
                    htmlField += "<div class='row' style='margin-left: 30px'>";

                    htmlField += "<div class='col-lg-2 custom-control custom-checkbox float-right' style='padding-top: 10px;'>";
                    htmlField += "<label class='control-label'>" +
                        "<input type='checkbox' class='form-check-input valueSub_" + currentNodeID + "' value='recall' " + (criteriaNode.value.indexOf('recall') != -1 ? 'checked' : '') + ">" + "Thu hồi số" +
                        "</label>";
                    htmlField += "</div>";

                    htmlField += "<div class='custom-control custom-checkbox float-right' style='padding-left: 50px; padding-right: 30px; padding-top: 10px;'>";
                    htmlField += "<label class='control-label'>" +
                        "<input type='checkbox' class='form-check-input valueSub_" + currentNodeID + "' value='lock2' " + (criteriaNode.value.indexOf('lock2') != -1  ? 'checked' : '') + ">" + "Khóa 2 chiều" +
                        "</label>";
                    htmlField += "</div>";

                    htmlField += "<div class='custom-control custom-checkbox float-right' style='padding-left: 50px; padding-right: 30px; padding-top: 10px;'>";
                    htmlField += "<label class='control-label'>" +
                        "<input type='checkbox' class='form-check-input valueSub_" + currentNodeID + "' value='lock1' " + (criteriaNode.value.indexOf('lock1') != -1 ? 'checked' : '') + ">" + "Khóa 1 chiều" +
                        "</label>";
                    htmlField += "</div>";

                    htmlField += "<div class='custom-control custom-checkbox float-right' style='padding-left: 50px; padding-right: 30px; padding-top: 10px;'>";
                    htmlField += "<label class='control-label'>" +
                        "<input type='checkbox' class='form-check-input valueSub_" + currentNodeID + "' value='active' " + (criteriaNode.value.indexOf('active') != -1  ? 'checked' : '') + " >" + "Hoạt động" +
                        "</label>";
                    htmlField += "</div>";

                    htmlField += "</div>";
                    htmlField += "</div>";
                    $("#" + selectCriteriaID).after(htmlField);
                } else if (criteriaID == 4) {
                    htmlField += "<div class=\"col-lg-3 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                        "                        <select id='valueSub_" + currentNodeID + "' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\">\n" +
                        "                            <option " + (criteriaNode.value == 'PREPAID' ? 'selected' : '') + " value=\"PREPAID\">Trả trước</option>\n" +
                        "                            <option " + (criteriaNode.value == 'POSTPAID' ? 'selected' : '') + " value=\"POSTPAID\">Trả sau</option>\n" +
                        "                        </select>\n" +
                        "                    </div>";
                    $("#" + selectCriteriaID).after(htmlField);
                    $("#valueSub_"+currentNodeID).selectpicker({
                        style: 'btn-operator'
                    });
                }
            } else if (criteriaID == 5) {
                htmlField += "<div class=\"col-lg-3 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                    "                        <select id='operatorSub_" + currentNodeID + "' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\">\n" +
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
                                    "                        <select id='valueSub_"+currentNodeID+"' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" >\n" ;
                                for(let i = 0; i < data.length; i++){
                                    htmlField +=  "                            <option "+ (criteriaNode.value == data[i] ? 'selected' : '') +" value=\""+data[i]+"\">"+data[i]+"</option>\n";
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
                    "                        <select id='operatorSub_" + currentNodeID + "' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" >\n" +
                    "                            <option " + (criteriaNode.operator == 'using' ? 'selected' : '') + " value=\"using\">Đang sử dụng</option>\n" +
                    "                            <option " + (criteriaNode.operator == 'notInUse' ? 'selected' : '') + " value=\"notInUse\">Đang không sử dụng</option>\n" +
                    "                        </select>\n" +
                    "                    </div>";

                htmlField += "<div class=\"col-lg-2 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
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
                            "                            <option " + (criteriaNode.criteriaId == 'all' ? 'selected' : '') + " value=\"all\">Tất cả</option>\n";
                        response.forEach(element => {
                            htmlField +=  " <option " + (criteriaNode.value == element.id ? 'selected' : '') + " value=\""+element.id+"\">"+element.packageName+"</option>\n";
                        });
                        htmlField +=   " </select>\n" +
                            " </div>";
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
            }
            else if(criteriaID == 57 || criteriaID == 58){
                htmlField += "<div class=\"col-lg-2 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                    "                        <select id='operatorSub_" + currentNodeID + "' name='" + obj.code + "' onchange='changeOperator(this, "+currentNodeID+")' data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" class=\"form-control selectpicker\" >\n" +
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
                        "                        <input onpaste=\"return false;\" step=\"any\" ondrop=\"return false;\" autocomplete=\"off\" id='valueSub_" + currentNodeID + "' type='number' name='' class=\"form-control\" value='" + criteriaNode.value + "'  onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";

                    if (criteriaNode.operator == '><') {
                        htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_" + currentNodeID + "\" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                            "                        <input onpaste=\"return false;\" step=\"any\" ondrop=\"return false;\" autocomplete=\"off\" id='value_endSub_" + currentNodeID + "' type=\"number\" name='' class=\"form-control\" value='" + criteriaNode.valueEnd + "' onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                            "                    </div>";
                    } else {
                        htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_" + currentNodeID + "\" style=\"padding-left: 0px !important; display: none; padding-right: 0px !important;\">\n" +
                            "                        <input onpaste=\"return false;\" step=\"any\" ondrop=\"return false;\" autocomplete=\"off\" id='value_endSub_" + currentNodeID + "' type='number' name='' class=\"form-control\"  onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                            "                    </div>";
                    }
                }else {
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" id='valueSub_" + currentNodeID + "' type='number' name='' class=\"form-control\" value='" + criteriaNode.value + "'  onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";

                    if (criteriaNode.operator == '><') {
                        htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_" + currentNodeID + "\" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                            "                        <input onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" id='value_endSub_" + currentNodeID + "' type=\"number\" name='' class=\"form-control\" value='" + criteriaNode.valueEnd + "' onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                            "                    </div>";
                    } else {
                        htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_" + currentNodeID + "\" style=\"padding-left: 0px !important; display: none; padding-right: 0px !important;\">\n" +
                            "                        <input onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" id='value_endSub_" + currentNodeID + "' type='number' name='' class=\"form-control\"  onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
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
                            "                        <select id='mainServiceSub_"+currentNodeID+"' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n";
                        response.forEach(element => {
                            htmlField +=  " <option " + (criteriaNode.serviceCode == element.id ? 'selected' : '') + " value=\""+element.id+"\">"+element.packageName+"</option>\n";

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
                    "                        <select id='operatorSub_"+currentNodeID+"' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" >\n" +
                    "                            <option " + (criteriaNode.operator == 'using' ? 'selected' : '') + " value=\"using\">Đang sử dụng</option>\n" +
                    "                            <option " + (criteriaNode.operator == 'notInUse' ? 'selected' : '') + " value=\"notInUse\">Đang không sử dụng</option>\n" +
                    "                        </select>\n" +
                    "                    </div>";

                htmlField += "<div class=\"col-lg-2 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
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
                            "                            <option " + (criteriaNode.serviceCode == 'all' ? 'selected' : '') + " value=\"all\">Tất cả</option>\n";
                        response.forEach(element => {
                            htmlField +=  " <option " + (criteriaNode.serviceCode == element.id ? 'selected' : '') + " value=\""+element.id+"\">"+element.packageName+"</option>\n";
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
                    "                        <select id='operatorSub_" + currentNodeID + "' name='" + obj.code + "' onchange='changeOperator(this, "+currentNodeID+")' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" >\n" +
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
                        "                        <input onpaste=\"return false;\" step=\"any\" ondrop=\"return false;\" autocomplete=\"off\" id='valueSub_" + currentNodeID + "' type='number' name='' class=\"form-control\" value='" + criteriaNode.value + "'  onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";

                    if (criteriaNode.operator == '><') {
                        htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_" + currentNodeID + "\" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                            "                        <input onpaste=\"return false;\" step=\"any\" ondrop=\"return false;\" autocomplete=\"off\" id='value_endSub_" + currentNodeID + "' type=\"number\" name='' class=\"form-control\" value='" + criteriaNode.valueEnd + "' onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                            "                    </div>";
                    } else {
                        htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_" + currentNodeID + "\" style=\"padding-left: 0px !important; display: none; padding-right: 0px !important;\">\n" +
                            "                        <input onpaste=\"return false;\" step=\"any\" ondrop=\"return false;\" autocomplete=\"off\" id='value_endSub_" + currentNodeID + "' type='number' name='' class=\"form-control\"  onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                            "                    </div>";
                    }
                }else {
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" id='valueSub_" + currentNodeID + "' type='number' name='' class=\"form-control\" value='" + criteriaNode.value + "'  onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";

                    if (criteriaNode.operator == '><') {
                        htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_" + currentNodeID + "\" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                            "                        <input onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" id='value_endSub_" + currentNodeID + "' type=\"number\" name='' class=\"form-control\" value='" + criteriaNode.valueEnd + "' onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                            "                    </div>";
                    } else {
                        htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_" + currentNodeID + "\" style=\"padding-left: 0px !important; display: none; padding-right: 0px !important;\">\n" +
                            "                        <input onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" id='value_endSub_" + currentNodeID + "' type='number' name='' class=\"form-control\"  onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
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
                            "                        <select id='mainServiceSub_"+currentNodeID+"' name='' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" tabindex=\"-98\">\n";
                        response.forEach(element => {
                            htmlField +=  " <option " + (criteriaNode.serviceCode == element.id ? 'selected' : '') + " value=\""+element.id+"\">"+element.packageName+"</option>\n";

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
                    "                        <select id='operatorSub_" + currentNodeID + "' name='' onchange='changeOperator(this, "+currentNodeID+")' class=\"form-control selectpicker\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" >\n" +
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
                        "                        <input onpaste=\"return false;\" step=\"any\" ondrop=\"return false;\" autocomplete=\"off\" id='valueSub_" + currentNodeID + "' type='number' name='' class=\"form-control\" value='" + criteriaNode.value + "'  onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";

                    if (criteriaNode.operator == '><') {
                        htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_" + currentNodeID + "\" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                            "                        <input onpaste=\"return false;\" step=\"any\" ondrop=\"return false;\" autocomplete=\"off\" id='value_endSub_" + currentNodeID + "' type=\"number\" name='' class=\"form-control\" value='" + criteriaNode.valueEnd + "' onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                            "                    </div>";
                    } else {
                        htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_" + currentNodeID + "\" style=\"padding-left: 0px !important; display: none; padding-right: 0px !important;\">\n" +
                            "                        <input onpaste=\"return false;\" step=\"any\" ondrop=\"return false;\" autocomplete=\"off\" id='value_endSub_" + currentNodeID + "' type='number' name='' class=\"form-control\"  onkeypress='return blockCharacterDotByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                            "                    </div>";
                    }
                }else {
                    htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" id='valueSub_" + currentNodeID + "' type='number' name='' class=\"form-control\" value='" + criteriaNode.value + "'  onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                        "                    </div>";

                    if (criteriaNode.operator == '><') {
                        htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_" + currentNodeID + "\" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                            "                        <input onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" id='value_endSub_" + currentNodeID + "' type=\"number\" name='' class=\"form-control\" value='" + criteriaNode.valueEnd + "' onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                            "                    </div>";
                    } else {
                        htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" id=\"div_value_end_" + currentNodeID + "\" style=\"padding-left: 0px !important; display: none; padding-right: 0px !important;\">\n" +
                            "                        <input onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" id='value_endSub_" + currentNodeID + "' type='number' name='' class=\"form-control\"  onkeypress='return blockCharacterByInputNumber(event)' placeholder=\"Nhập giá trị\"/>\n" +
                            "                    </div>";
                    }
                }

                htmlField += "<div class=\"col-lg-1 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                    "                        <input id='' name='' class=\"form-control\" disabled placeholder=\" " + obj.unit + " \"/>\n" +
                    "                    </div>";

                var setupDate = 'setupDateSub_' + selectCriteriaID;
                var idStartDate = 'setupStartDateSub_' + currentNodeID;
                var idEndDate = 'setupEndDateSub_' + currentNodeID;
                htmlField += "<div class=\"col-lg-2 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important; border-right: 1px solid #efefef !important;\">\n" +
                    "                        <select id='operatorTimeSub_" + currentNodeID + "' name='' class=\"form-control\" style=\"padding-left: 0px !important; padding-right: 0px !important;\" data-size=\"3\" data-dropup-auto=\"false\" data-live-search=\"true\" data-placeholder=\"Chọn giá trị\" onchange=\"changeTime(this, '" + setupDate + "')\">\n" +
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
                        "                        <input name='' onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" id='" + idStartDate + "' class=\"form-control\" type=\"text\" value='" + criteriaNode.startTime + "' placeholder='Từ ngày' id=\"example-date-input\">\n" +
                        "                    </div>";

                    htmlField += "<div class=\"col-lg-6 " + classNameCondition + " \" style=\"padding-left: 0px !important; padding-right: 0px !important;\">\n" +
                        "                        <input  name='' onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\" id='" + idEndDate + "' class=\"form-control\" type=\"text\" value='" + criteriaNode.endTime + "'  placeholder='Đến ngày' id=\"example-date-input\">\n" +
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

function validateForm(){
    $.blockUI({
        css: {
            border: 'none',
            padding: '15px',
            backgroundColor: '#000',
            '-webkit-border-radius': '10px',
            '-moz-border-radius': '10px',
            opacity: .5,
            color: '#fff'
        }});
    return true;
}

function showPopupSetupMessage(id){
    let value = $("#jsonSubMessageContent_" + id).val();
    if(value != null && value.length > 0){
        let objMessageContent = JSON.parse(value);
        $("#channel-marketing_" + id).val(objMessageContent.channelMarketing);
        $("#sending-account_" + id).val(objMessageContent.sendingAccount);
        $("#regis-command_" + id).val(objMessageContent.regisCommand);
        $("#confirm-command_"+id).val(objMessageContent.confirmCommand);
        $("#product-package_" + id).val(objMessageContent.productPackage);
        $("#message-content_" + id).val(objMessageContent.messageContent);
        countMT(id);
        $("#sending-account_" + id).selectpicker('refresh');
        $("#product-package_" + id).selectpicker('refresh');
    }
    $("#popup_ctn_mess_" + id).modal("show");
}

function genMultiMessageSubGroups(){
    let isSubtarget = $("#sub-target-group-radio-1").is(":checked");
    $("#content-message-subgroups").empty();
    for (const [key, value] of arrayTreeSubTarget) {
        let name = $("#subTargetGroupName_"+key).val();
        if(isSubtarget){
            let subPriority = $("#subTargetGroupPriority_" + key).val();
            let htmlField = "<div class=\"row p-t-20\">" +
                "        <div class=\"col-md-5\">" +
                "            <div class=\"form-group\">" +
                "                <label class=\"control-label\">Nhóm</label>" +
                "                <input class=\"form-control\" disabled placeholder=\"\" value='"+name+"' type=\"text\">" +
                "            </div>" +
                "        </div>" +
                "        <div class=\"col-md-2\">" +
                "            <div class=\"form-group\">" +
                "                <label class=\"control-label\">Thứ tự ưu tiên</label>" +
                "                <input class=\"form-control\" disabled placeholder=\"\" value='"+subPriority+"' type=\"text\">" +
                "            </div>" +
                "        </div>" +
                "        <div class=\"col-md-2\">" +
                "            <div class=\"form-group stg_mess\">" +
                "                <label class=\"control-label\">Thông điệp</label>" +
                "                <div class=\"\">" +
                "                    <a onclick=\"showPopupSetupMessage("+key+")\" id=\"subgroup-message-" + key + "\" class=\"subgroup-message\" data-toggle=\"modal\" href=\"#\">Cấu hình thông điệp</a>" +
                "<!--                    <i class=\"ti-check\"></i>-->" +
                "                </div>" +
                "            </div>" +
                "        </div>" +
                "       <div class=\"col-md-3\">" +
                "          <div class=\"form-group\" style=\"display:none;\" title='Đã cấu hình' id=\"checkMessageContent_"+key+"\">" +
                "                <i class=\"ti-check\"></i>" +
                "          </div>" +
                "        </div>" +
                "    </div>";



            htmlField += "<!--Modal-->" +
                "<div aria-hidden=\"true\" aria-labelledby=\"myLargeModalLabel\" data-backdrop=\"static\" data-keyboard=\"false\" id='popup_ctn_mess_"+key+"' class=\"modal fade bs-example-modal-lg\"" +
                "     role=\"dialog\" style=\"display: none;\" tabindex=\"-1\">" +
                "    <div class=\"modal-dialog modal-lg\">" +
                "        <div class=\"modal-content\">" +
                "            <div class=\"modal-header\">" +
                "                <h4 class=\"modal-title\" id=\"myLargeModalLabel\">Nội dung thông điệp</h4>" +
                "            </div>" +
                "            <div class=\"modal-body\">" +
                "                <div class=\"row p-t-20\">" +
                "                    <div class=\"col-md-6\">" +
                "                        <div class=\"form-group\">" +
                "                            <label class=\"control-label\">Kênh truyền thông<span class=\"red-clr\">*</span></label>" +
                "                            <select class=\"form-control custom-select\" id=\"channel-marketing_"+key+"\" data-placeholder=\"Chọn kênh truyền thông\"" +
                "                                    tabindex=\"1\">" +
                "                                <option value=\"1\">SMS</option>" +
                "                            </select>" +
                "                        </div>" +
                "                    </div>" +
                "                    <div class=\"col-md-6\">" +
                "                        <div class=\"form-group\">" +
                "                            <label class=\"control-label\">Tài khoản gửi tin<span class=\"red-clr\">*</span></label>" +
                "                            <select class=\"form-control custom-select sending-account-sub-group\" id=\"sending-account_"+key+"\" title=\"Chọn tài khoản gửi tin\"" +
                "                                   data-dropup-auto=\"false\" data-live-search=\"true\" data-size=\"6\" data-width=\"100%\">" +
                "                            </select>" +
                "                        </div>" +
                "                    </div>" +
                "                </div>" +
                "                <div class=\"row\">" +
                "                    <div class=\"col-md-6\">" +
                "                        <div class=\"form-group\">" +
                "                            <label class=\"control-label\">Gói cước<span class=\"red-clr\">*</span></label>" +
                "                           <select class=\"form-control package-sub-group custom-select\" id=\"product-package_"+key+"\" name=\"package\"  title=\"Chọn gói cước\"" +
                "                                   data-dropup-auto=\"false\" data-live-search=\"true\" data-size=\"6\" data-width=\"100%\">" +
                "                            </select>" +
                "                        </div>" +
                "                    </div>" +
                "                </div>" +
                "" +
                "                <div class=\"row\">" +
                "                    <div class=\"col-md-12\">" +
                "                        <div class=\"form-group m-b-20\">" +
                "                            <label class=\"control-label\">Nội dung <span class=\"red-clr\">*</span></label>" +
                "                            <textarea class=\"form-control fc_area\" id=\"message-content_"+key+"\" onpaste=\"countMTOnPaste(event)\" onkeyup=\"countMT("+key+")\" placeholder=\"Nhập nội dung bản tin\"" +
                "                                      rows=\"5\"></textarea>" +
                "                            <p class=\"note_area m-b-0\" id=\"mt-count_"+key+"\">0 ký tự - 1 tin nhắn (MT).</p>" +
                "                        </div>" +
                "                    </div>" +
                "                </div>" +
                "" +
                "                <div class=\"row m-b-20\">" +
                "                    <div class=\"col-md-6\">" +
                "                        <div class=\"btn-group\">" +
                "                            <button class=\"btn btn-rounded btn-primary\" type=\"button\"><i aria-hidden=\"true\"" +
                "                                                                                         class=\"ti-plus text m-r-10\"></i>Thêm nội" +
                "                                dung tham biến" +
                "                            </button>" +
                "                        </div>" +
                "                    </div>" +
                "                </div>" +
                "                <div class=\"btn_main_act btn_R\">" +
                "                    <div class=\"btn-group\">" +
                "                        <button class=\"btn btn-rounded btn-primary\" type=\"button\" onclick='validateContentMessage("+key+")'>Lưu</button>" +
                "                    </div>" +
                "                    <div class=\"btn-group\">" +
                "                        <button class=\"btn btn-rounded btn-danger\" data-dismiss=\"modal\" type=\"button\">Hủy</button>" +
                "                    </div>" +
                "                </div>" +
                "            </div>" +
                "        </div>" +
                "        <!-- /.modal-content -->" +
                "    </div>" +
                "    <!-- /.modal-dialog -->" +
                "</div>";
            htmlField += "<input type=\"hidden\" id=\"countMT_"+key+"\">";
            htmlField += "<input type=\"hidden\" id=\"unicode_"+key+"\">";
            htmlField += "<input type=\"hidden\" id=\"jsonSubMessageContent_"+key+"\" name=\"message-content\">";
            $("#content-message-subgroups").append(htmlField);
        } else {

        }
    }
}

function checkChangeInfoSubGroup(){
    let mapResult = new Map();
    for (const [key, value] of arrayTreeSubTarget) {
        if(mapDataSubGroup.get("subTargetGroupName_" + key) == $("#subTargetGroupName_"+ key).val() && mapDataSubGroup.get("subTargetGroupPriority_" + key) == $("#subTargetGroupPriority_"+ key).val() ){
            mapResult.set(key, $("#jsonSubMessageContent_" + key).val());
        }
    }

    return mapResult;
}

function dataAssignmentSubMessage(id, value){

    if(value != null && value.length > 0){
        $("#jsonSubMessageContent_" + id).val(value);
    }
}
