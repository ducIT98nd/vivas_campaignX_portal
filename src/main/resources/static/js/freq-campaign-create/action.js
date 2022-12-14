$(document).ready(function () {
    $('#sending-frequency').on('change', function() {
        if (this.value == 'once'){
            $("#div-end-date").css("display", "none");
            $("#end-date").prop("disabled", true);
            $(".freq_week").css("display", "none");
            $(".freq_month").css("display", "none");
            $("#custom-date").css("display", "none");
        } else if (this.value == 'daily') {
            $("#div-end-date").css("display", "flex");
            $("#end-date").removeAttr('disabled');
            $(".freq_week").css("display", "none");
            $(".freq_month").css("display", "none");
            $("#custom-date").css("display", "none");
        } else if (this.value == 'weekly') {
            $("#div-end-date").css("display", "flex");
            $("#end-date").removeAttr('disabled');
            $(".freq_week").css("display", "block");
            $(".freq_month").css("display", "none");
            $("#custom-date").css("display", "none");
        } else if (this.value == 'monthly') {
            $("#div-end-date").css("display", "flex");
            $("#end-date").removeAttr('disabled');
            $(".freq_week").css("display", "none");
            $(".freq_month").css("display", "block");
            $("#custom-date").css("display", "none");
        } else if (this.value == 'custom') {
            $("#div-end-date").css("display", "flex");
            $("#end-date").removeAttr('disabled');
            $(".freq_week").css("display", "none");
            $(".freq_month").css("display", "none");
            $("#custom-date").css("display", "block");
        }
    });

    $('#custom-date-freq-type').on('change', function() {

        if (this.value == 'monthly'){
            $(".freq_month").css("display", "block");
            $(".freq_week").css("display", "none");
        } else if (this.value == 'weekly'){
            $(".freq_week").css("display", "block");
            $(".freq_month").css("display", "none");
        } else {
            $(".freq_week").css("display", "none");
            $(".freq_month").css("display", "none");
        }
    });

    /* step 3 dem ky tu va mt noi dung tin */
    $("#message-content").on("keyup", function (){
        var smsMessageContent = $(this).val();
        var posVNChar = checkVNCharacter(smsMessageContent);
        let MTInfo = "[length] k?? t??? - [count] tin nh???n (MT)."
        let mtLength = smsMessageContent.length;
        if (posVNChar != -1) {
            $("#is-unicode").val("8");
        } else {
            $("#is-unicode").val("0");
        }
        var isUnicode = $("#is-unicode").val();
        MTInfo = MTInfo.replace("[length]", mtLength);
        if (isUnicode == 0) {
            $("#message-content").attr('maxlength','612');
            if (mtLength == 0 || smsMessageContent == "") {
                MTInfo = MTInfo.replace("[count]", 0);
            } else if (mtLength >= 1 && mtLength <= 160) {
                MTInfo = MTInfo.replace("[count]", 1);
            } else if (mtLength >= 161 && mtLength <= 306) {
                MTInfo = MTInfo.replace("[count]", 2);
            } else if (mtLength >= 307 && mtLength <= 459) {
                MTInfo = MTInfo.replace("[count]", 3);
            } else if (mtLength >= 460 && mtLength <= 612) {
                MTInfo = MTInfo.replace("[count]", 4);
            }
        } else if (isUnicode == 8) {
            $("#smsMessageContent").attr('maxlength','268');
            if (mtLength == 0 || smsMessageContent == "") {
                MTInfo = MTInfo.replace("[count]", 0);
            } else if (mtLength >= 1 && mtLength <= 70) {
                MTInfo = MTInfo.replace("[count]", 1);
            } else if (mtLength >= 71 && mtLength <= 134) {
                MTInfo = MTInfo.replace("[count]", 2);
            } else if (mtLength >= 135 && mtLength <= 201) {
                MTInfo = MTInfo.replace("[count]", 3);
            } else if (mtLength >= 202 && mtLength <= 268) {
                MTInfo = MTInfo.replace("[count]", 4);
            } else {
                let str = smsMessageContent.substring(0,268);
                $("#smsMessageContent").val(str)
                $("#MTInfo").val(4);
                $("#soMTSMS").text("4 MT");
            }
        }
        $("#mt-count").text(MTInfo);
    })
    
})

/* step 5 init */
function initDataOverview() {
    // chi tiet
    let campaignName = $("#campaign-name").val();
    $("#overview-campaign-name").text(campaignName);

    let expectedApprovalRate = $("#expected-approval-rate").val();
    if(expectedApprovalRate == null || expectedApprovalRate.length <= 0) $("#overview-expected-approval-rate").text("N/A");
    else $("#overview-expected-approval-rate").text(expectedApprovalRate + " %");

    let campaignTarget = $("#campaign-target").val();
    if(campaignTarget == null || campaignTarget.length <= 0) $("#overview-campaign-target").text("N/A");
    else $("#overview-campaign-target").text(campaignTarget);

    let description = $("#campaign-description").val();
    if(description == null || description.length <= 0) $("#overview-description").text("N/A");
    else $("#overview-description").text(description);

    let startDate = $("#start-date").val();
    let endDate = $("#end-date").val();
    let frequency = $("#sending-frequency").val();
    if(frequency == 'once') {
        $("#overview-campaign-date").text(startDate);
    }else {
        $("#overview-campaign-date").text("T??? " + startDate + " ?????n " + endDate);
    }
    let styleDisplay = $('#time-range-2').css('display');
    let startTimeRange1 = $("#time-range-1-start").val();
    let endTimeRange1 = $("#time-range-1-end").val();
    let timeRange = "T??? " + startTimeRange1 + " ?????n " + endTimeRange1;
    if(styleDisplay != "none") {
        let timeRange2Start = $("#time-range-2-start").val();
        let timeRange2End = $("#time-range-2-end").val();
        timeRange += "; t??? " + timeRange2Start + " ?????n " + timeRange2End;
    }
    $("#overview-sending-time").text(timeRange);

    $("#overview-product").text($( "#package-group option:selected" ).text());

    let valueFrequency = "";
    if(frequency == 'once') valueFrequency += " M???t l???n";
    else if(frequency == 'daily') valueFrequency += " H???ng ng??y";
    else if(frequency == 'weekly') {
        valueFrequency += "H???ng tu???n v??o";
        $('input[name="freq_daily"]:checked').each(function() {
            if(this.value == '0') valueFrequency += " th??? hai, " ;
            else if(this.value == '1') valueFrequency += " th??? ba, " ;
            else if(this.value == '2') valueFrequency += " th??? t??, " ;
            else if(this.value == '3') valueFrequency += " th??? n??m, " ;
            else if(this.value == '4') valueFrequency += " th??? s??u, " ;
            else if(this.value == '5') valueFrequency += " th??? b???y, " ;
            else if(this.value == '6') valueFrequency += " ch??? nh???t, " ;
        });
        valueFrequency = valueFrequency.trim();
        valueFrequency = valueFrequency.substring(0, valueFrequency.length - 1);
    }
    else if(frequency == 'monthly') {
        let monthlyByDay = $("#monthly-by-day-checkbox").is(":checked");
        if(monthlyByDay){
            valueFrequency += "H???ng th??ng v??o ng??y " + $("#day-in-month").val();
        }else {
            let monthlyByWeekday = $("#monthly-by-weekday").val();
            valueFrequency += "V??o";
            if(monthlyByWeekday == 0) valueFrequency += " th??? hai " ;
            else if(monthlyByWeekday == 1) valueFrequency += " th??? ba " ;
            else if(monthlyByWeekday == 2) valueFrequency += " th??? t?? " ;
            else if(monthlyByWeekday == 3) valueFrequency += " th??? n??m " ;
            else if(monthlyByWeekday == 4) valueFrequency += " th??? s??u " ;
            else if(monthlyByWeekday == 5) valueFrequency += " th??? b???y " ;
            else if(monthlyByWeekday == 6) valueFrequency += " ch??? nh???t " ;
            let monthlyByWeekdayOrdinal = $("#monthly-by-weekday-ordinal").val();
            if(monthlyByWeekdayOrdinal == 0) valueFrequency += " ?????u ti??n " ;
            else if(monthlyByWeekdayOrdinal == 1) valueFrequency += " th??? 2 " ;
            else if(monthlyByWeekdayOrdinal == 2) valueFrequency += " th??? 3 " ;
            else if(monthlyByWeekdayOrdinal == 3) valueFrequency += " th??? 4 " ;
            else if(monthlyByWeekdayOrdinal == 4) valueFrequency += " cu???i c??ng " ;
            valueFrequency += "c???a th??ng";
        }
    }
    else if(frequency == 'custom') {
        let specificDate = $("#specific-date-checkbox").is(":checked");
        if(specificDate){
            valueFrequency += "V??o c??c ng??y: " + $("#specific-date-value").val();
        }else {
            valueFrequency += "C??? m???i " + $("#periodic-number").val();
            let customDateFreqType = $("#custom-date-freq-type").val();
            if(customDateFreqType == 'daily') valueFrequency += " ng??y/l???n";
            else if(customDateFreqType == 'weekly') {
                valueFrequency += " tu???n/l???n, v??o ";
                $('input[name="freq_daily"]:checked').each(function() {
                    if(this.value == '0') valueFrequency += " th??? hai, " ;
                    else if(this.value == '1') valueFrequency += " th??? ba, " ;
                    else if(this.value == '2') valueFrequency += " th??? t??, " ;
                    else if(this.value == '3') valueFrequency += " th??? n??m, " ;
                    else if(this.value == '4') valueFrequency += " th??? s??u, " ;
                    else if(this.value == '5') valueFrequency += " th??? b???y, " ;
                    else if(this.value == '6') valueFrequency += " ch??? nh???t, " ;
                });
                valueFrequency = valueFrequency.trim();
                valueFrequency = valueFrequency.substring(0, valueFrequency.length - 1);
                valueFrequency += " c???a tu???n";
            }
            else if(customDateFreqType == 'monthly') {
                valueFrequency += " th??ng/l???n, v??o ";
                let checked = $("#monthly-by-day-checkbox").is(":checked");
                if(checked) valueFrequency += "ng??y " + $("#day-in-month").val() + " c???a th??ng";
                else {
                    let monthlyByWeekday = $("#monthly-by-weekday").val();
                    if(monthlyByWeekday == 0) valueFrequency += " th??? hai " ;
                    else if(monthlyByWeekday == 1) valueFrequency += " th??? ba " ;
                    else if(monthlyByWeekday == 2) valueFrequency += " th??? t?? " ;
                    else if(monthlyByWeekday == 3) valueFrequency += " th??? n??m " ;
                    else if(monthlyByWeekday == 4) valueFrequency += " th??? s??u " ;
                    else if(monthlyByWeekday == 5) valueFrequency += " th??? b???y " ;
                    else if(monthlyByWeekday == 6) valueFrequency += " ch??? nh???t " ;
                    let monthlyByWeekdayOrdinal = $("#monthly-by-weekday-ordinal").val();
                    if(monthlyByWeekdayOrdinal == 0) valueFrequency += " ?????u ti??n " ;
                    else if(monthlyByWeekdayOrdinal == 1) valueFrequency += " th??? 2 " ;
                    else if(monthlyByWeekdayOrdinal == 2) valueFrequency += " th??? 3 " ;
                    else if(monthlyByWeekdayOrdinal == 3) valueFrequency += " th??? 4 " ;
                    else if(monthlyByWeekdayOrdinal == 4) valueFrequency += " cu???i c??ng " ;
                    valueFrequency += "c???a th??ng";
                }
            }
        }
    }
    $("#overview-frequency").text(valueFrequency.trim());

    let product = $("#package-group").val();
    $("#overview-package-group").text(product);

    //nhom doi tuong
    let typeTargetGroup = $("#type-target-group").val();
    if(typeTargetGroup == 1) $("#overview-type-target-group").text("D??ng ti??u ch?? l???c thu?? bao")
    else if(typeTargetGroup == 2) $("#overview-type-target-group").text("K???t h???p Danh s??ch thu?? bao v?? Ti??u ch??")
    else if(typeTargetGroup == 3) $("#overview-type-target-group").text("D??ng Danh s??ch thu?? bao")
    else if(typeTargetGroup == 4) $("#overview-type-target-group").text("D??ng T???p giao c???a Danh s??ch thu?? bao v?? Ti??u ch??")
    else if(typeTargetGroup == 5) $("#overview-type-target-group").text("D??ng T???p giao c???a c??c Danh s??ch thu?? bao")
    else if(typeTargetGroup == 6) $("#overview-type-target-group").text("D??ng Nh??m ?????i t?????ng c?? s???n")

    $("#overview-criteria").empty();
    if(typeTargetGroup == 1) {
        $("#div-overview-criteria").show();
        $("#div-overview-file-target-group").hide();
        $("#div-overview-target-group").hide();
        let html = generateOverviewCriteriaDiv();
        $("#overview-criteria").empty();
        $("#overview-criteria").append(html);
    }
    else if(typeTargetGroup == 2) {
        var filename = document.getElementById('data-Customer').files[0].name;
        $("#link-download-data").text(filename);
        $("#div-overview-file-target-group").show();
        $("#div-overview-criteria").show();
        $("#div-overview-target-group").hide();
        let html = generateOverviewCriteriaDiv();
        $("#overview-criteria").empty();
        $("#overview-criteria").append(html);
    }
    else if(typeTargetGroup == 3) {
        var filename = document.getElementById('data-Customer').files[0].name;
        $("#link-download-data").text(filename);
        $("#div-overview-file-target-group").show();
        $("#div-overview-criteria").hide();
        $("#div-overview-target-group").hide();
    }
    else if(typeTargetGroup == 4) {
        var filename = document.getElementById('data-Customer').files[0].name;
        $("#link-download-data").text(filename);
        $("#div-overview-file-target-group").show();
        $("#div-overview-criteria").show();
        $("#div-overview-target-group").hide();
        let html = generateOverviewCriteriaDiv();
        $("#overview-criteria").empty();
        $("#overview-criteria").append(html);
    }
    else if(typeTargetGroup == 5) {
        var filename = document.getElementById('data-Customer').files[0].name;
        $("#link-download-data").text(filename);
        $("#div-overview-file-target-group").show();
        $("#div-overview-criteria").hide();
        $("#div-overview-target-group").hide();
    }
    else if(typeTargetGroup == 6) {
        $("#div-overview-file-target-group").hide();
        $("#div-overview-criteria").hide();
        $("#div-overview-target-group").show();
        let text = $("#input-target-group-id option:selected").text();
        $("#overview-target-group").text(text);

    }
    //Quy mo nhom

    let groupSize = $("#groupSize").text();
    let ratio = $("#ratio").text();

    $("#div-overview-group-size").text('Quy m?? nh??m: ' + groupSize + ' thu?? bao');
    $("#div-overview-ratio").text('T??? l??? so v???i to??n m???ng: ' + ratio);

    //blacklist
    let typeInputBlacklist = $("#type-input-blacklist").val();
    if(typeInputBlacklist == 2) {
        $("#div-overview-blacklist-file-target-group").hide();
        $("#div-overview-blacklist-target-group").show();
        $("#div-overview-blacklist-na").hide();
        $("#overview-blacklist-target-group").text($( "#target-group-id option:selected" ).text());
    }else if(typeInputBlacklist == 3){
        var filenameBL = document.getElementById('blacklist-file').files[0].name;
        $("#link-download-blacklist").text(filenameBL);
        $("#div-overview-blacklist-file-target-group").show();
        $("#div-overview-blacklist-target-group").hide();
        $("#div-overview-blacklist-na").hide();
    }else {
        $("#div-overview-blacklist-file-target-group").hide();
        $("#div-overview-blacklist-target-group").hide();
        $("#div-overview-blacklist-na").show();
    }
    //phan nho nhom
    let isSubgroups = $("#sub-target-group-radio-1").is(":checked");
    if(isSubgroups){
        $("#div-overview-sub-group").show();
        let isDuplicateMsisdn = $("#is-duplicate-msisdn").is(":checked");
        if(isDuplicateMsisdn) $("#overview-is-duplicate-msisdn").text("C??");
        else $("#overview-is-duplicate-msisdn").text("Kh??ng");
        $("#overview-sub-target-group").empty();
        for (const [key, value] of arrayTreeSubTarget) {
            let htmlSubTarget = generateOverviewSubTargetGroupDiv(key, value);
            $("#overview-sub-target-group").append(htmlSubTarget);
        }
    }else {
        $("#div-overview-sub-group").hide();
    }


    //thong diep
    let html = "";
    if (isSubgroups) {
        let i = 0;
        for (const [key, value] of arrayTreeSubTarget) {
            if (i%2 == 0){
                html += "<div class=\"row\">";
            }
            let jsonObj = JSON.parse($("#jsonSubMessageContent_" + key).val());
            let subgroupName = $("#subTargetGroupName_"+key).val();

            let channel = $("#channel-marketing_"+key+" option[value='"+jsonObj.channelMarketing+"']").text();
            let sendingAccount = $("#sending-account_"+key+" option[value='"+jsonObj.sendingAccount+"']").text();
            let package = $("#product-package_"+key+" option[value='"+jsonObj.productPackage+"']").text();
            let messageContent = jsonObj.messageContent;
            html += generateOverviewMessageDiv(channel, sendingAccount, package, messageContent, subgroupName);
            if (i%2 == 1){
                html += "</div>";
            }
            i++;
        }
    } else {
        let channel = $("#channel-marketing_one option:selected").text();
        let sendingAccount = $("#sending-account_one option:selected").text();
        let package = $("#product-package_one option:selected").text();
        let messageContent = $("#message-content_one").val();
        html += "<div class=\"row\">";
        html += generateOverviewMessageDiv(channel, sendingAccount, package, messageContent, "");
        html += "</div>";
    }
    $("#overview-message").empty();
    $("#overview-message").append(html);

    //chinh sach
    let message = "Khung gi??? g???i tin t???ng k??nh truy???n th??ng ngo??i nh?? m???ng: ";
    let isDisableSendingTimeLimitChecked = $("#checkbox-disable-sending-time-limit").is(":checked");
    if (isDisableSendingTimeLimitChecked == true){
        let input = $(".checkbox-policy-1");
        let listCheckedChannel = "";
        let value = input.each(function() {
            if ($(this).is(":checked")){
                listCheckedChannel += this.value + ", ";
            };
        });
        $("#overview-disable-sending-time-limit").text(message + listCheckedChannel.substring(0, listCheckedChannel.length - 2));
    } else {
        $("#overview-disable-sending-time-limit").text("");
    }

    message = "Gi???i h???n th??ng ??i???p t???ng k??nh truy???n th??ng ngo??i nh?? m???ng: ";
    let isDisableMessageLimitChecked = $("#checkbox-disable-message-limit").is(":checked");
    if (isDisableMessageLimitChecked == true){
        let input = $(".checkbox-policy-2");
        let listCheckedChannel = "";
        let value = input.each(function() {
            if ($(this).is(":checked")){
                listCheckedChannel += this.value + ", ";
            };
        });
        $("#overview-disable-message-limit").text(message + listCheckedChannel.substring(0, listCheckedChannel.length - 2));
    } else {
        $("#overview-disable-message-limit").text("");
    }

    if(isDisableSendingTimeLimitChecked == false && isDisableMessageLimitChecked == false){
        $("#overview-disable-sending-na").show();
    }else {
        $("#overview-disable-sending-na").hide();
    }
}

function generateOverviewMessageDiv(channel, sendingAccount, package, messageContent, subgroupName){

    let html = "<div class=\"col-md-6 p-r-10\">";
    if (subgroupName != null && subgroupName.length != 0){
        html += "<label><b>" + subgroupName + "</b></label>"
    }
    html += "    <div class=\"ov_col_mes\">" +
        "        <div class=\"row\">" +
        "            <div class=\"col-md-6\">" +
        "                <label>K??nh truy???n th??ng</label>" +
        "                <p>" + channel + "</p>" +
        "            </div>" +
        "            <div class=\"col-md-6\">" +
        "                <label>T??i kho???n g???i tin</label>" +
        "                <p>"+ sendingAccount + "</p>" +
        "            </div>" +
        "        </div>" +
        "        <div class=\"row\">" +
        "            <div class=\"col-md-6\">" +
        "                <label>G??i c?????c</label>" +
        "                <p>" + package + "</p>" +
        "            </div>" +
        "        </div>" +
        "        <div class=\"row\">" +
        "            <div class=\"col-md-12\">" +
        "                <label>N???i dung</label>" +
        "                <p>" + messageContent + "</p>" +
        "            </div>" +
        "        </div>" +
        "    </div>" +
        "</div>";
    return html;
}

function generateOverviewCriteriaDiv(){
    let html = "";
    let conditionLv1 = $("#conditionLevel1").val();
    if(conditionLv1 == "AND"){
        html += "<p style='margin-bottom: 0px'>Th???a m??n t???t c??? c??c ??i???u ki???n sau ????y</p>";
    }else {
        html += "<p style='margin-bottom: 0px' >Th???a m??n m???t trong s??? c??c ??i???u ki???n sau ????y</p>";
    }

    let temp = 0;
    for( let currentNode of criteriaTree.preOrderTraversal()){
        if(currentNode.key != 0 && currentNode.level == 1){
            html += "<p style='margin-bottom: 0px; font-weight: lighter'>+";
            html += detailCriteria(currentNode);
            html += "</p>";
            let lv2  = currentNode.children;
            if(lv2 != null && lv2.length > 0){
                let conditionLv2 = null;
                for( let currentNode20 of lv2){
                    conditionLv2 = $("#condition_level2_of_"+currentNode20.parent.key).val();
                }
                if(conditionLv2 == "AND"){
                    html += "<p style='margin-bottom: 0px; margin-left: 2em;'>Th???a m??n t???t c??? c??c ??i???u ki???n sau ????y</p>"
                }else {
                    html += "<p style='margin-bottom: 0px; margin-left: 2em;' >Th???a m??n m???t trong s??? c??c ??i???u ki???n sau ????y</p>"
                }
                for( let currentNode21 of lv2){
                    if(currentNode21.key != 0 && currentNode21.level == 2){
                        html += "<p style='margin-bottom: 0px; margin-left: 2em; font-weight: lighter'>+";
                        html += detailCriteria(currentNode21);
                        html += "</p>";
                    }
                    let lv3 = currentNode21.children;
                    if(lv3 != null && lv3.length > 0) {
                        let conditionLv3 = null;
                        for (let currentNode30 of lv3) {
                            conditionLv3 = $("#condition_level3_of_"+currentNode30.parent.key).val();
                        }
                        if (conditionLv3 == "AND") {
                            html += "<p style='margin-bottom: 0px; margin-left: 4em;'>Th???a m??n t???t c??? c??c ??i???u ki???n sau ????y</p>"
                        } else {
                            html += "<p style='margin-bottom: 0px; margin-left: 4em;' >Th???a m??n m???t trong s??? c??c ??i???u ki???n sau ????y</p>"
                        }
                        for (let currentNode31 of lv3) {
                            if (currentNode31.key != 0 && currentNode31.level == 3) {
                                html += "<p style='margin-bottom: 0px; margin-left: 4em; font-weight: lighter'>+";
                                html += detailCriteria(currentNode31);
                                html += "</p>";
                            }
                        }
                    }
                }

            }

        }
    }
    return html;
}

function generateOverviewSubTargetGroupDiv(key, tree){
    let html = "";
    html += "<tr>\n" +
        "  <td>"+$("#subTargetGroupName_"+key).val()+"</td>\n" +
        "  <td>\n" +
        "  <p style='margin-bottom: 0px'>Th???a m??n t???t c??? c??c ??i???u ki???n sau ????y</p>";
    for( let currentNode of tree.preOrderTraversal()){
        if(currentNode.key != 0 && currentNode.level == 1) {
            html += detailCriteriaSub(currentNode);
            html += "<br>";
        }
    }
    html +=    "  </td>\n";
    if($("#subTargetGroupPriority_"+key).val() == null || $("#subTargetGroupPriority_"+key).val().length <= 0) html +=  "  <td>N/A</td>\n";
    else html += "  <td>"+$("#subTargetGroupPriority_"+key).val()+"</td>\n";
    html += "  <td>"+$("#msisdnSubGroup_"+key).text()+"</td>\n" +
        "  <td>"+$("#ratioSubGroup_"+key).text()+"</td>\n" +
        " </tr>";
    return html;
}

function detailCriteria(currentNode){
    let htmlField = "";
    let criteriaID = currentNode.criteriaId;
    if (criteriaID == 1 || criteriaID == 2 || criteriaID == 6 || criteriaID == 7 || criteriaID == 65 || criteriaID == 66) {
        htmlField += currentNode.criteriaName;

        if($("#operator_"+currentNode.key).val() == "=") htmlField += " b???ng ";
        else if($("#operator_"+currentNode.key).val() == "!=") htmlField += " kh??ng b???ng ";
        else if($("#operator_"+currentNode.key).val() == "<") htmlField += " nh??? h??n ";
        else if($("#operator_"+currentNode.key).val() == ">") htmlField += " l???n h??n ";
        else if($("#operator_"+currentNode.key).val() == "<=") htmlField += " nh??? h??n ho???c b???ng ";
        else if($("#operator_"+currentNode.key).val() == ">=") htmlField += " l???n h??n ho???c b???ng ";
        else if($("#operator_"+currentNode.key).val() == "><") htmlField += " trong kho???ng ";

        if($("#operator_"+currentNode.key).val() == "><") {
            htmlField += "t??? ";
            htmlField += $("#value_"+currentNode.key).val();
            htmlField += " ";
            htmlField += currentNode.unit;
            htmlField += " ?????n ";
            htmlField += $("#value_end_"+currentNode.key).val();
            htmlField += " ";
            htmlField += currentNode.unit;
        }else {
            htmlField += $("#value_"+currentNode.key).val();
            htmlField += " ";
            htmlField += currentNode.unit;
        }

    } else if (criteriaID == 3 || criteriaID == 4) {
        if (criteriaID == 3) {
            htmlField += currentNode.criteriaName;
            $(".value_"+currentNode.key+":checkbox:checked").each(function () {
                let valueTmp = $(this).val();
                if(valueTmp == 'recall') htmlField += " Thu h???i s???;";
                else if(valueTmp == 'lock2') htmlField += " Kh??a 2 chi???u;";
                else if(valueTmp == 'lock1') htmlField += " Kh??a 1 chi???u;";
                else if(valueTmp == 'active') htmlField += " Ho???t ?????ng;";
            });
            htmlField = htmlField.substring(0, htmlField.length - 1);
        } else if (criteriaID == 4) {
            htmlField += currentNode.criteriaName;
            if($("#value_"+currentNode.key).val() == 'PREPAID') htmlField += " tr??? tr?????c ";
            else if($("#value_"+currentNode.key).val() == 'POSTPAID') htmlField += " tr??? sau ";
        }
    } else if (criteriaID == 5) {
        htmlField += currentNode.criteriaName;
        if($("#operator_"+currentNode.key).val() == 'is') htmlField += " l?? ";
        else if($("#operator_"+currentNode.key).val() == 'isnot') htmlField += " kh??ng l?? ";
        htmlField += $( "#value_"+currentNode.key+" option:selected" ).text();
    } else if (criteriaID == 56) {
        //htmlField += currentNode.criteriaName;
        htmlField += "G??i DATA ";
        if($("#operator_"+currentNode.key).val() == 'using') htmlField += " ??ang s??? d???ng: ";
        else if($("#operator_"+currentNode.key).val() == 'notInUse') htmlField += " ??ang kh??ng s??? d???ng: ";
        htmlField += $("#value_"+currentNode.key+" option:selected" ).text();

    } else if (criteriaID == 60) {
        htmlField += "G??i KMCB ";
        //htmlField += currentNode.criteriaName;
        if($("#operator_"+currentNode.key).val() == 'using') htmlField += " ??ang s??? d???ng: ";
        else if($("#operator_"+currentNode.key).val() == 'notInUse') htmlField += " ??ang kh??ng s??? d???ng: ";
        htmlField += $("#value_"+currentNode.key+" option:selected" ).text();
    } else if(criteriaID == 58 || criteriaID == 57){
        htmlField += currentNode.criteriaName;
        if($("#operator_"+currentNode.key).val() == "=") htmlField += " b???ng ";
        else if($("#operator_"+currentNode.key).val() == "!=") htmlField += " kh??ng b???ng ";
        else if($("#operator_"+currentNode.key).val() == "<") htmlField += " nh??? h??n ";
        else if($("#operator_"+currentNode.key).val() == ">") htmlField += " l???n h??n ";
        else if($("#operator_"+currentNode.key).val() == "<=") htmlField += " nh??? h??n ho???c b???ng ";
        else if($("#operator_"+currentNode.key).val() == ">=") htmlField += " l???n h??n ho???c b???ng ";
        else if($("#operator_"+currentNode.key).val() == "><") htmlField += " trong kho???ng ";

        if($("#operator_"+currentNode.key).val() == "><") {
            htmlField += "t??? ";
            htmlField += $("#value_"+currentNode.key).val();
            htmlField += " ";
            htmlField += currentNode.unit;
            htmlField += " ?????n ";
            htmlField += $("#value_end_"+currentNode.key).val();
            htmlField += " ";
            htmlField += currentNode.unit;
            htmlField += " g??i ";
            htmlField += $( "#mainService_"+currentNode.key+" option:selected" ).text();
        }else {
            htmlField += $("#value_"+currentNode.key).val();
            htmlField += " ";
            htmlField += currentNode.unit;
            htmlField += " g??i ";
            htmlField += $( "#mainService_"+currentNode.key+" option:selected" ).text();
        }
    } else if(criteriaID == 62 || criteriaID == 61){
        htmlField += currentNode.criteriaName;
        if($("#operator_"+currentNode.key).val() == "=") htmlField += " b???ng ";
        else if($("#operator_"+currentNode.key).val() == "!=") htmlField += " kh??ng b???ng ";
        else if($("#operator_"+currentNode.key).val() == "<") htmlField += " nh??? h??n ";
        else if($("#operator_"+currentNode.key).val() == ">") htmlField += " l???n h??n ";
        else if($("#operator_"+currentNode.key).val() == "<=") htmlField += " nh??? h??n ho???c b???ng ";
        else if($("#operator_"+currentNode.key).val() == ">=") htmlField += " l???n h??n ho???c b???ng ";
        else if($("#operator_"+currentNode.key).val() == "><") htmlField += " trong kho???ng ";

        if($("#operator_"+currentNode.key).val() == "><") {
            htmlField += "t??? ";
            htmlField += $("#value_"+currentNode.key).val();
            htmlField += " ";
            htmlField += currentNode.unit;
            htmlField += " ?????n ";
            htmlField += $("#value_end_"+currentNode.key).val();
            htmlField += " ";
            htmlField += currentNode.unit;
            htmlField += " g??i ";
            htmlField += $( "#mainService_"+currentNode.key+" option:selected" ).text();
        }else {
            htmlField += $("#value_"+currentNode.key).val();
            htmlField += " ";
            htmlField += currentNode.unit;
            htmlField += " g??i ";
            htmlField += $( "#mainService_"+currentNode.key+" option:selected" ).text();
        }
    }else {
        htmlField += currentNode.criteriaName;
        if($("#operator_"+currentNode.key).val() == "=") htmlField += " b???ng ";
        else if($("#operator_"+currentNode.key).val() == "!=") htmlField += " kh??ng b???ng ";
        else if($("#operator_"+currentNode.key).val() == "<") htmlField += " nh??? h??n ";
        else if($("#operator_"+currentNode.key).val() == ">") htmlField += " l???n h??n ";
        else if($("#operator_"+currentNode.key).val() == "<=") htmlField += " nh??? h??n ho???c b???ng ";
        else if($("#operator_"+currentNode.key).val() == ">=") htmlField += " l???n h??n ho???c b???ng ";
        else if($("#operator_"+currentNode.key).val() == "><") htmlField += " trong kho???ng ";

        if($("#operator_"+currentNode.key).val() == "><") {
            htmlField += "t??? ";
            htmlField += $("#value_"+currentNode.key).val();
            htmlField += " ";
            htmlField += currentNode.unit;
            htmlField += " ?????n ";
            htmlField += $("#value_end_"+currentNode.key).val();
            htmlField += " ";
            htmlField += currentNode.unit;
        }else {
            htmlField += " ";
            htmlField += $("#value_"+currentNode.key).val();
            htmlField += " ";
            htmlField += currentNode.unit;
        }

        if($("#operatorTime_"+currentNode.key).val() == "1") htmlField += " Trong 60 ng??y g???n nh???t";
        else if($("#operatorTime_"+currentNode.key).val() == "2") htmlField += " Trong 30 ng??y g???n nh???t";
        else if($("#operatorTime_"+currentNode.key).val() == "3") htmlField += " Trong 7 ng??y g???n nh???t";
        else if($("#operatorTime_"+currentNode.key).val() == "4") htmlField += " Trong th??ng hi???n t???i";
        else if($("#operatorTime_"+currentNode.key).val() == "5") htmlField += " Trong th??ng tr?????c";
        else if($("#operatorTime_"+currentNode.key).val() == "6") htmlField += " Trong kho???ng th???i gian";

        if($("#operatorTime_"+currentNode.key).val() == "6") htmlField += " t??? ng??y " + $("#setupStartDate_"+currentNode.key).val() + " ?????n ng??y " + $("#setupEndDate_"+currentNode.key).val();
    }
    return htmlField;
}

function detailCriteriaSub(currentNode){
    let htmlField = "";
    let criteriaID = currentNode.criteriaId;
    if (criteriaID == 1 || criteriaID == 2 || criteriaID == 6 || criteriaID == 7 || criteriaID == 65 || criteriaID == 66) {
        htmlField += currentNode.criteriaName;

        if($("#operatorSub_"+currentNode.key).val() == "=") htmlField += " b???ng ";
        else if($("#operatorSub_"+currentNode.key).val() == "!=") htmlField += " kh??ng b???ng ";
        else if($("#operatorSub_"+currentNode.key).val() == "<") htmlField += " nh??? h??n ";
        else if($("#operatorSub_"+currentNode.key).val() == ">") htmlField += " l???n h??n ";
        else if($("#operatorSub_"+currentNode.key).val() == "<=") htmlField += " nh??? h??n ho???c b???ng ";
        else if($("#operatorSub_"+currentNode.key).val() == ">=") htmlField += " l???n h??n ho???c b???ng ";
        else if($("#operatorSub_"+currentNode.key).val() == "><") htmlField += " trong kho???ng ";

        if($("#operatorSub_"+currentNode.key).val() == "><") {
            htmlField += "t??? ";
            htmlField += $("#valueSub_"+currentNode.key).val();
            htmlField += " ";
            htmlField += currentNode.unit;
            htmlField += " ?????n ";
            htmlField += $("#value_endSub_"+currentNode.key).val();
            htmlField += " ";
            htmlField += currentNode.unit;
        }else {
            htmlField += $("#valueSub_"+currentNode.key).val();
            htmlField += " ";
            htmlField += currentNode.unit;
        }

    } else if (criteriaID == 3 || criteriaID == 4) {
        if (criteriaID == 3) {
            htmlField += currentNode.criteriaName;
            $(".valueSub_"+currentNode.key+":checkbox:checked").each(function () {
                let valueTmp = $(this).val();
                if(valueTmp == 'recall') htmlField += " Thu h???i s???;";
                else if(valueTmp == 'lock2') htmlField += " Kh??a 2 chi???u;";
                else if(valueTmp == 'lock1') htmlField += " Kh??a 1 chi???u;";
                else if(valueTmp == 'active') htmlField += " Ho???t ?????ng;";
            });
            htmlField = htmlField.substring(0, htmlField.length - 1);
        } else if (criteriaID == 4) {
            htmlField += currentNode.criteriaName;
            if($("#valueSub_"+currentNode.key).val() == 'PREPAID') htmlField += " tr??? tr?????c ";
            else if($("#valueSub_"+currentNode.key).val() == 'POSTPAID') htmlField += " tr??? sau ";
        }
    } else if (criteriaID == 5) {
        htmlField += currentNode.criteriaName;
        if($("#operatorSub_"+currentNode.key).val() == 'is') htmlField += " l?? ";
        else if($("#operatorSub_"+currentNode.key).val() == 'isnot') htmlField += " kh??ng l?? ";
        htmlField += $( "#valueSub_"+currentNode.key+" option:selected" ).text();
    } else if (criteriaID == 56) {
        //htmlField += currentNode.criteriaName;
        htmlField += "G??i DATA ";
        if($("#operatorSub_"+currentNode.key).val() == 'using') htmlField += " ??ang s??? d???ng: ";
        else if($("#operatorSub_"+currentNode.key).val() == 'notInUse') htmlField += " ??ang kh??ng s??? d???ng: ";
        htmlField += $("#valueSub_"+currentNode.key+" option:selected" ).text();

    } else if (criteriaID == 60) {
        htmlField += "G??i KMCB ";
        //htmlField += currentNode.criteriaName;
        if($("#operatorSub_"+currentNode.key).val() == 'using') htmlField += " ??ang s??? d???ng: ";
        else if($("#operatorSub_"+currentNode.key).val() == 'notInUse') htmlField += " ??ang kh??ng s??? d???ng: ";
        htmlField += $("#valueSub_"+currentNode.key+" option:selected" ).text();
    } else if(criteriaID == 58 || criteriaID == 57){
        htmlField += currentNode.criteriaName;
        if($("#operatorSub_"+currentNode.key).val() == "=") htmlField += " b???ng ";
        else if($("#operatorSub_"+currentNode.key).val() == "!=") htmlField += " kh??ng b???ng ";
        else if($("#operatorSub_"+currentNode.key).val() == "<") htmlField += " nh??? h??n ";
        else if($("#operatorSub_"+currentNode.key).val() == ">") htmlField += " l???n h??n ";
        else if($("#operatorSub_"+currentNode.key).val() == "<=") htmlField += " nh??? h??n ho???c b???ng ";
        else if($("#operatorSub_"+currentNode.key).val() == ">=") htmlField += " l???n h??n ho???c b???ng ";
        else if($("#operatorSub_"+currentNode.key).val() == "><") htmlField += " trong kho???ng ";

        if($("#operatorSub_"+currentNode.key).val() == "><") {
            htmlField += "t??? ";
            htmlField += $("#valueSub_"+currentNode.key).val();
            htmlField += " ";
            htmlField += currentNode.unit;
            htmlField += " ?????n ";
            htmlField += $("#value_endSub_"+currentNode.key).val();
            htmlField += " ";
            htmlField += currentNode.unit;
            htmlField += " g??i ";
            htmlField += $( "#mainServiceSub_"+currentNode.key+" option:selected" ).text();
        }else {
            htmlField += $("#valueSub_"+currentNode.key).val();
            htmlField += " ";
            htmlField += currentNode.unit;
            htmlField += " g??i ";
            htmlField += $( "#mainServiceSub_"+currentNode.key+" option:selected" ).text();
        }
    } else if(criteriaID == 62 || criteriaID == 61){
        htmlField += currentNode.criteriaName;
        if($("#operatorSub_"+currentNode.key).val() == "=") htmlField += " b???ng ";
        else if($("#operatorSub_"+currentNode.key).val() == "!=") htmlField += " kh??ng b???ng ";
        else if($("#operatorSub_"+currentNode.key).val() == "<") htmlField += " nh??? h??n ";
        else if($("#operatorSub_"+currentNode.key).val() == ">") htmlField += " l???n h??n ";
        else if($("#operatorSub_"+currentNode.key).val() == "<=") htmlField += " nh??? h??n ho???c b???ng ";
        else if($("#operatorSub_"+currentNode.key).val() == ">=") htmlField += " l???n h??n ho???c b???ng ";
        else if($("#operatorSub_"+currentNode.key).val() == "><") htmlField += " trong kho???ng ";

        if($("#operatorSub_"+currentNode.key).val() == "><") {
            htmlField += "t??? ";
            htmlField += $("#valueSub_"+currentNode.key).val();
            htmlField += " ";
            htmlField += currentNode.unit;
            htmlField += " ?????n ";
            htmlField += $("#value_endSub_"+currentNode.key).val();
            htmlField += " ";
            htmlField += currentNode.unit;
            htmlField += " g??i ";
            htmlField += $( "#mainServiceSub_"+currentNode.key+" option:selected" ).text();
        }else {
            htmlField += $("#valueSub_"+currentNode.key).val();
            htmlField += " ";
            htmlField += currentNode.unit;
            htmlField += " g??i ";
            htmlField += $( "#mainServiceSub_"+currentNode.key+" option:selected" ).text();
        }
    }else {
        htmlField += currentNode.criteriaName;
        if($("#operatorSub_"+currentNode.key).val() == "=") htmlField += " b???ng ";
        else if($("#operatorSub_"+currentNode.key).val() == "!=") htmlField += " kh??ng b???ng ";
        else if($("#operatorSub_"+currentNode.key).val() == "<") htmlField += " nh??? h??n ";
        else if($("#operatorSub_"+currentNode.key).val() == ">") htmlField += " l???n h??n ";
        else if($("#operatorSub_"+currentNode.key).val() == "<=") htmlField += " nh??? h??n ho???c b???ng ";
        else if($("#operatorSub_"+currentNode.key).val() == ">=") htmlField += " l???n h??n ho???c b???ng ";
        else if($("#operatorSub_"+currentNode.key).val() == "><") htmlField += " trong kho???ng ";

        if($("#operatorSub_"+currentNode.key).val() == "><") {
            htmlField += "t??? ";
            htmlField += $("#valueSub_"+currentNode.key).val();
            htmlField += " ";
            htmlField += currentNode.unit;
            htmlField += " ?????n ";
            htmlField += $("#value_endSub_"+currentNode.key).val();
            htmlField += " ";
            htmlField += currentNode.unit;
        }else {
            htmlField += " ";
            htmlField += $("#valueSub_"+currentNode.key).val();
            htmlField += " ";
            htmlField += currentNode.unit;
        }

        if($("#operatorTimeSub_"+currentNode.key).val() == "1") htmlField += " Trong 60 ng??y g???n nh???t";
        else if($("#operatorTimeSub_"+currentNode.key).val() == "2") htmlField += " Trong 30 ng??y g???n nh???t";
        else if($("#operatorTimeSub_"+currentNode.key).val() == "3") htmlField += " Trong 7 ng??y g???n nh???t";
        else if($("#operatorTimeSub_"+currentNode.key).val() == "4") htmlField += " Trong th??ng hi???n t???i";
        else if($("#operatorTimeSub_"+currentNode.key).val() == "5") htmlField += " Trong th??ng tr?????c";
        else if($("#operatorTimeSub_"+currentNode.key).val() == "6") htmlField += " Trong kho???ng th???i gian";

        if($("#operatorTimeSub_"+currentNode.key).val() == "6") htmlField += " t??? ng??y " + $("#setupStartDateSub_"+currentNode.key).val() + " ?????n ng??y " + $("#setupEndDateSub_"+currentNode.key).val();
    }
    return htmlField;
}

