var subTargetGroup = 0;

$(document).ready(function () {

    let channel = $("#channel").val();
    if (channel == 6) {
        let targetGroupIdTemp = $("#targetGroupId").val();
        $("#input-target-group-id").val(targetGroupIdTemp);
    }

    let typeInputBlacklist = $("#type-input-blacklist").val();
    if (typeInputBlacklist == 3) {
        let targetGroupIdBlacklistTemp = $("#targetGroupIdBlacklist").val();
        $("#target-group-id").val(targetGroupIdBlacklistTemp);
    }

    $("#input-target-group-id").selectpicker('refresh');
    $("#target-group-id").selectpicker('refresh');


    let hasSub = $('input[name="sub-target-group-radio"]:checked').val();

    if (hasSub == 'no') {
        $(".isDisabled").attr("disabled", "disabled");

        $.ajax({
            type: 'GET',
            url: '/SubTargetGroupController/viewSubTargetGroup?id=0',
            traditional: true,
            async: false,
            success: function (response) {
                console.log("response " + response)
                $("#viewSubTargetGroup_0").append(response);
            },
            error: function (e) {
                console.log(e);
            },
        });

        criteriaIDSequence++;
        axios.get('/SubTargetGroupController/GetRowLevel1Criteria?currentId=' + criteriaIDSequence + '&parentId=0&subTreeId=0')
            .then(function (response) {
                $("#subSetCriteria_0").after(response.data);
                $("#buttonDelete_" + criteriaIDSequence).attr("disabled", "disabled");
            })
            .catch(function (error) {
                console.log(error);
            });
        let subCriteriaTree = new SubCriteriaTree("0");
        subCriteriaTree.insert("0", criteriaIDSequence);
        arrayTreeSubTarget.set(0, subCriteriaTree);
        if (arrayTreeSubTarget.size == 1) {
            for (const [keySubTarget, value] of arrayTreeSubTarget) {
                $("#btn-delete-sub-" + keySubTarget).prop("disabled", true);
            }
        }
    } else {
        $(".isDisabled").removeAttr("disabled");
        genModelSubGroup();
        if (arrayTreeSubTarget.size == 1) {
            for (const [keySubTarget, value] of arrayTreeSubTarget) {
                $("#btn-delete-sub-" + keySubTarget).prop("disabled", true);
            }
        }
    }

    let lstCheckbox = $("#checkbox-disable-sending-time-limit").is(":checked");
    if (lstCheckbox == true) {
        $('#div-policy-sending-time').show();
    } else {
        $('#div-policy-sending-time').hide();
    }

    let lstCheckbox2 = $("#checkbox-disable-message-limit").is(":checked");
    if (lstCheckbox2 == true) {
        $('#div-policy-message').show();
    } else {
        $('#div-policy-message').hide();
    }

    mapDataSubGroup.set("package-group", $("#package-group").val());
});

function getSubTargetGroupByCampaignId(campaignId) {
    let data = null;
    $.ajax({
        type: 'GET',
        url: '/SubTargetGroupController/getSubTargetGroupByCampaignId?frequencyCampaignId=' + campaignId,
        traditional: true,
        dataType: 'JSON',
        async: false,
        success: function (response) {
            if (response) {
                if (response.code == 1) {
                    data = response.data;
                    console.log(data);
                    return data;
                }
            }
        },
        error: function (e) {
            console.log(e);
        },
    })
    return data
}

function genModelSubGroup() {

    $("#model-Sub-Group").empty();
    arrayTreeSubTarget.clear();


    let campaignId = $("#campaignId").val();
    let data = getSubTargetGroupByCampaignId(campaignId);

    mapDataSubGroup.set("sub-target-group-radio", $('input[name="sub-target-group-radio"]:checked').val());
    let isDuplicateMsisdn = $("#is-duplicate-msisdn").is(":checked");
    mapDataSubGroup.set("is-duplicate-msisdn", isDuplicateMsisdn);

    console.log("data: " + data);
    for (let i = 0; i < data.length; i++) {
        let idSubTargetGroup = data[i].id;
        let numberMSISDN = data[i].quantityMsisdn;
        let ratio = data[i].ratio;
        let html = getHTMLSubTargetGroup(data[i].dataJson);
        $("#model-Sub-Group").append(html);

        $("#msisdnSubGroup_" + subTargetGroup).text(numberMSISDN == null ? 0 + " thuê bao" : numberMSISDN + " thuê bao");
        $("#ratioSubGroup_" + subTargetGroup).text(ratio == null ? 0 + " %" : ratio + " %");

        let subCriteriaTree = new SubCriteriaTree("0");

        $.ajax({
            type: 'GET',
            url: '/SubTargetGroupController/getCriteriaMappingBySubGroupId?subGroupId=' + idSubTargetGroup,
            traditional: true,
            dataType: 'JSON',
            async: false,
            success: function (response) {
                if (response) {
                    if (response.code == 1) {
                        let dataSelectedValue = response.data;
                        let arrayData = [];

                        $.ajax({
                            type: 'GET',
                            url: '/SubTargetGroupController/viewSubTargetGroup?id=' + subTargetGroup,
                            traditional: true,
                            async: false,
                            success: function (response) {
                                if (response) {
                                    $("#viewSubTargetGroup_" + subTargetGroup).append(response);
                                }
                            },
                            error: function (e) {
                                console.log(e);
                            },
                        });

                        for (let j = 0; j < dataSelectedValue.length; j++) {
                            let criteriaNode = JSON.parse(dataSelectedValue[j].selectedValue);
                            arrayData.push(criteriaNode);
                            subCriteriaTree.insert("0", criteriaNode.key);
                            if (criteriaIDSequence < criteriaNode.key) criteriaIDSequence = criteriaNode.key;
                            processCriteriaSubGroup(subCriteriaTree, dataSelectedValue[j], 'viewSubTargetGroup_' + subTargetGroup, subTargetGroup);
                        }
                        criteriaIDSequence++;
                        mapSubTargetWithCriteria.set(subTargetGroup, arrayData);
                    }
                }
            },
            error: function (e) {
                console.log(e);
            },
        })
        arrayTreeSubTarget.set(subTargetGroup, subCriteriaTree);

        mapDataSubGroup.set("subTargetGroupName_" + subTargetGroup, $("#subTargetGroupName_" + subTargetGroup).val());
        mapDataSubGroup.set("subTargetGroupPriority_" + subTargetGroup, $("#subTargetGroupPriority_" + subTargetGroup).val());

        subTargetGroup++;
    }
    console.log("mapDataSubGroup: ");
    console.log(mapDataSubGroup);
}

//step 2 - gen nhom tieu chi con
function getHTMLSubTargetGroup(jsonOldData) {
    let htmlFieldSubModal = "<!--Modal-->\n" +
        "    <div class=\"modal fade bs-example-modal-lg pop_mlg popup_sg\" data-backdrop=\"static\" data-keyboard=\"false\" id=\"modalSubTargetGroup_" + subTargetGroup + "\" tabindex=\"-1\" role=\"dialog\" aria-labelledby=\"myLargeModalLabel\" aria-hidden=\"true\" style=\"display: none;\">\n" +
        "        <div class=\"modal-dialog modal-lg\">\n" +
        "            <div class=\"modal-content\">\n" +
        "                <div class=\"modal-header\">\n" +
        "                    <h4 class=\"modal-title\" id=\"labelSubGroupName_" + subTargetGroup + "\">Nhóm nhỏ</h4>\n" +
        "                    <h6 class=\"modal-title\" ><span class=\"red-clr\" style=\"font-weight: bold\">Quy mô nhóm:</span><span id=\"msisdnSubGroup_" + subTargetGroup + "\"> 0 Thuê bao </span></h6>\n" +
        "                    <h6 class=\"modal-title\" ><span class=\"red-clr\" style=\"font-weight: bold\">Tỷ lệ so với nhóm đối tượng:</span><span id=\"ratioSubGroup_" + subTargetGroup + "\"> 0%</span> </h6>" +
        "                </div>\n" +
        "                <div class=\"row modal-body p-t-30\">\n" +
        "                    <div class=\"col-lg-9 col-xlg-12 col-md-8 step3_GO_L L_card\">\n" +
        "\n" +
        "                        <div id=\"viewSubTargetGroup_" + subTargetGroup + "\"></div>\n" +
        "\n" +
        "                        <div class=\"btn_main_act btn_L m-t-20\">\n" +
        "                            <div class=\"btn-group\">\n" +
        "                                <button type=\"button\" class=\"btn btn-rounded btn-primary\" onclick=\"countMSISDNSubGroup(" + subTargetGroup + ")\"><i class=\"ti-reload\"></i> Cập nhật quy mô nhóm</button>\n" +
        "                            </div>\n" +
        "                        </div>\n" +
        "\n" +
        "                        <div class=\"btn_main_act btn_R m-t-20\">\n" +
        "                            <div class=\"btn-group\">\n" +
        "                                <button type=\"button\" class=\"btn btn-rounded btn-primary\" onclick='validateSubTarget(" + subTargetGroup + ")'>Lưu</button>\n" +
        "                            </div>\n" +
        "                            <div class=\"btn-group\">\n" +
        "                                <button type=\"button\" class=\"btn btn-rounded btn-danger\" onclick=\"closeModal(" + subTargetGroup + ")\">Hủy</button>\n" +
        "                            </div>\n" +
        "                        </div>\n" +
        "\n" +
        "                    </div>\n" +
        "                </div>\n" +
        "            </div>\n" +
        "            <!-- /.modal-content -->\n" +
        "        </div>\n" +
        "        <!-- /.modal-dialog -->\n" +
        "       <input type=\"hidden\" id=\"jsonSubTarget_" + subTargetGroup + "\" value='" + jsonOldData + "' name=\"json-sub-target-group\">\n" +
        "    </div>";

    return htmlFieldSubModal;
}

async function addSubTargetGroupEdit(beforeId) {

    if (arrayTreeSubTarget.size == 1) {
        for (const [keySubTarget, value] of arrayTreeSubTarget) {
            $("#btn-delete-sub-" + keySubTarget).removeAttr("disabled");
        }
    }

    mapDataSubGroup = new Map();
    subTargetGroup++;
    let htmlField = "<tr id=\"subTargetGroup_" + subTargetGroup + "\">\n" +
        "                                <td class=\"sgroup_col\">\n" +
        "                                    <div class=\"form-group\">\n" +
        "                                        <input class=\"form-control isDisabled\" name='sub-target-group-name' maxlength='100' id=\"subTargetGroupName_" + subTargetGroup + "\" onkeyup=\"keyupCheckSubtarget(" + subTargetGroup + ")\" placeholder=\"Nhập tên nhóm\"\n" +
        "                                               type=\"text\">\n" +
        "                                    </div>\n" +
        "                                    <div class=\"btn-group btn_ss\">\n" +
        "                                        <button class=\"btn btn-rounded btn-primary isDisabled\" onclick=\"showSubTargetGroup('modalSubTargetGroup_'+subTargetGroup)\" type=\"button\">Thiết lập nhóm\n" +
        "                                        </button>\n" +
        "                                    </div>\n" +
        "                                </td>\n" +
        "                                <td>\n" +
        "                                    <div class=\"form-group\">\n" +
        "                                        <input class=\"form-control isDisabled\" onpaste=\"return false;\" ondrop=\"return false;\" autocomplete=\"off\"  onkeypress='return blockCharacterByInputNumber(event)' name='sub-target-group-priority' id=\"subTargetGroupPriority_" + subTargetGroup + "\" onkeyup=\"keyupCheckSubtarget(" + subTargetGroup + ")\" placeholder=\"Nhập thứ tự ưu tiên\"\n" +
        "                                               type=\"number\">\n" +
        "                                    </div>\n" +
        "                                </td>\n" +
        "                                <td>\n" +
        "                                    <div class=\"form-group\" style=\"display:none;\" title=\"Đã thiết lập.\" id=\"checkSub_" + subTargetGroup + "\">\n" +
        "                                        <i class=\"ti-check\"></i>\n" +
        "                                    </div>\n" +
        "                                </td>\n" +
        "                                <td class=\"act_ksn\" >\n" +
        "                                    <div class=\"btn-group\" style=\"margin-left: 20px\">\n" +
        "                                        <button class=\"btn btn_ksn btn_ksn_delete btn_cl_black isDisabled\" data-placement=\"top\"\n" +
        "                                                data-toggle=\"tooltip\" title=\"Nhân bản nhóm\" onclick=\"copyTargetGroup(" + subTargetGroup + ")\" type=\"button\">\n" +
        "                                            <i class=\"ti-files\"></i>\n" +
        "                                        </button>\n" +
        "                                        <button class=\"btn btn_ksn btn_ksn_delete btn_cl_black isDisabled\" data-placement=\"top\"\n" +
        "                                                data-toggle=\"tooltip\" title=\"Xóa nhóm\" id=\"btn-delete-sub-" + subTargetGroup + "\" type=\"button\" onclick=\"deleteSubTarget('subTargetGroup_" + subTargetGroup + "')\">\n" +
        "                                            <i class=\"ti-trash\"></i>\n" +
        "                                        </button>\n" +
        "                                        <button class=\"btn btn_ksn btn_ksn_delete btn_cl_black isDisabled\" data-placement=\"top\"\n" +
        "                                                data-toggle=\"tooltip\" title=\"Thêm nhóm\" type=\"button\" onclick=\"addSubTargetGroupEdit(" + subTargetGroup + ")\">\n" +
        "                                            <i class=\"ti-plus\"></i>\n" +
        "                                        </button>\n" +
        "                                    </div>\n" +
        "                                </td>\n" +
        "                            </tr>";
    $("#subTargetGroup_" + beforeId).after(htmlField);

    let htmlFieldSubModal = "<!--Modal-->\n" +
        "    <div class=\"modal bs-example-modal-lg pop_mlg popup_sg\" data-backdrop=\"static\" data-keyboard=\"false\" id=\"modalSubTargetGroup_" + subTargetGroup + "\" role=\"dialog\" aria-labelledby=\"myLargeModalLabel\" aria-hidden=\"true\" style=\"display: none;\">\n" +
        "        <div class=\"modal-dialog modal-lg\">\n" +
        "            <div class=\"modal-content\">\n" +
        "                <div class=\"modal-header\">\n" +
        "                    <h4 class=\"modal-title\" id=\"labelSubGroupName_" + subTargetGroup + "\">Nhóm nhỏ</h4>\n" +
        "                    <h6 class=\"modal-title\" ><span class=\"red-clr\" id=\"labelGroupSize_" + subTargetGroup + "\" style=\"font-weight: bold\">Quy mô nhóm:</span> <span id=\"msisdnSubGroup_" + subTargetGroup + "\">0 Thuê bao</span></h6>\n" +
        "                    <h6 class=\"modal-title\" ><span class=\"red-clr\" id=\"labelRatio_" + subTargetGroup + "\" style=\"font-weight: bold\">Tỷ lệ so với nhóm đối tượng:</span><span id=\"ratioSubGroup_" + subTargetGroup + "\">0.00%</span></h6>" +
        "                </div>\n" +
        "                <div class=\"row modal-body p-t-30\">\n" +
        "                    <div class=\"col-lg-9 col-xlg-12 col-md-8 step3_GO_L L_card\">\n" +
        "\n" +
        "                        <div id=\"viewSubTargetGroup_" + subTargetGroup + "\"></div>\n" +
        "\n" +
        "                        <div class=\"btn_main_act btn_L m-t-20\">\n" +
        "                            <div class=\"btn-group\">\n" +
        "                                <button type=\"button\" class=\"btn btn-rounded btn-primary\" onclick=\"countMSISDNSubGroup(" + subTargetGroup + ")\"><i class=\"ti-reload\"></i> Cập nhật quy mô nhóm</button>\n" +
        "                            </div>\n" +
        "                        </div>\n" +
        "\n" +
        "                        <div class=\"btn_main_act btn_R m-t-20\">\n" +
        "                            <div class=\"btn-group\">\n" +
        "                                <button type=\"button\" class=\"btn btn-rounded btn-primary\" onclick='validateSubTarget(" + subTargetGroup + ")'>Lưu</button>\n" +
        "                            </div>\n" +
        "                            <div class=\"btn-group\">\n" +
        "                                <button type=\"button\" class=\"btn btn-rounded btn-danger\" onclick=\"closeModal(" + subTargetGroup + ")\">Hủy</button>\n" +
        "                            </div>\n" +
        "                        </div>\n" +
        "\n" +
        "                    </div>\n" +
        "                </div>\n" +
        "            </div>\n" +
        "            <!-- /.modal-content -->\n" +
        "        </div>\n" +
        "        <!-- /.modal-dialog -->\n" +
        "       <input type=\"hidden\" id=\"jsonSubTarget_" + subTargetGroup + "\" name=\"json-sub-target-group\">\n" +
        "    </div>";
    $("#modalSubTargetGroup_" + beforeId).after(htmlFieldSubModal);

    await axios.get('/SubTargetGroupController/viewSubTargetGroup?id=' + subTargetGroup)
        .then(function (response) {
            console.log(response);
            $("#viewSubTargetGroup_" + subTargetGroup).append(response.data);
        })
        .catch(function (error) {
            console.log(error);
        });

    criteriaIDSequence++;
    await axios.get('/SubTargetGroupController/GetRowLevel1Criteria?currentId=' + criteriaIDSequence + '&parentId=0&subTreeId=' + subTargetGroup)
        .then(function (response) {
            $("#subSetCriteria_" + subTargetGroup).after(response.data);
            $("#buttonDelete_" + criteriaIDSequence).attr("disabled", "disabled");
        })
        .catch(function (error) {
            console.log(error);
        });
    let subCriteriaTree = new SubCriteriaTree("0");
    subCriteriaTree.insert("0", criteriaIDSequence);
    arrayTreeSubTarget.set(subTargetGroup, subCriteriaTree);

}

//=================







