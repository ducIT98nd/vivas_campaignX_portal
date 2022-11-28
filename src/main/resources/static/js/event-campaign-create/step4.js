function validateStep4NoSubgroups (){

    let check = true;

    /* Kênh truyền thông */
    let channel = $("#channel-marketing_one").val();
    if(channel.length == 0) {
        let messsage = comboboxRequiredErrorMessage("kênh truyền thông");
        showTooltip(messsage, 'channel');
        check = false;
    } else {
        removeTooltip('channel');
    }

    /* tài khoản gửi tin*/
    let sendingAccount = $("#sending-account_one").val();
    if(sendingAccount.length == 0) {
        let messsage = comboboxRequiredErrorMessage("Tài khoản gửi tin");
        showTooltipSelectpicker(messsage, 'sending-account_one');
        check = false;
    } else {
        removeTooltipSelectPicker('sending-account_one');
    }

    /* gói cước */
    let productPackage = $("#product-package_one").val();
    if(productPackage.length == 0) {
        let messsage = comboboxRequiredErrorMessage("Gói cước");
        showTooltipSelectpicker(messsage, 'product-package_one');
        check = false;
    } else {
        removeTooltipSelectPicker('product-package_one');
    }

    /* nội dung tin */
    let messageConent = $("#message-content_one").val().trim();
    if(messageConent.length == 0) {
        let messsage = textRequiredErrorMessage("Nội dung tin");
        showTooltip(messsage, 'message-content');
        check = false;
    } else if (messageConent.length > 612) {
        let messsage = characterLimitedErrorMessage("Cú pháp xác nhận đăng ký SMS", 612);
        showTooltip(messsage, 'message-content');
        check = false;
    } else {
        removeTooltip('message-content');
    }

    return check;
}

function validateStep4Subgroups (){
    let check = true;
    for (const [key, value] of arrayTreeSubTarget) {
        let channelMarketing = $("#channel-marketing_" + key).val();
        let sendingAccount = $("#sending-account_" + key).val();
        let productPackage = $("#product-package_" + key).val();
        let messageContent = $("#message-content_" + key).val();
        let jsonContent = $("#jsonSubMessageContent_" + key).val();
        if(channelMarketing != null && channelMarketing.length > 0 && sendingAccount != null && sendingAccount.length > 0 &&
            productPackage != null && productPackage.length > 0 && messageContent != null && messageContent.length > 0 &&
            jsonContent != null && jsonContent.length > 0){

        }else {
            check = false;
        }
    }
    return check;
}

function validateContentMessage(id){
    let channelMarketing = $("#channel-marketing_" + id).val();
    let sendingAccount = $("#sending-account_" + id).val();
    let productPackage = $("#product-package_" + id).val();
    let messageContent = $("#message-content_" + id).val().trim();
    let check = true;
    if(channelMarketing == null || channelMarketing.length <= 0){
        let mess = textRequiredErrorMessage("Kênh truyền thông");
        showTooltip(mess, "channel-marketing_" + id);
        check = false;
    }else {
        removeTooltip("channel-marketing_" + id);
        check = true;
    }

    if(sendingAccount == null || sendingAccount.length <= 0){
        let mess = textRequiredErrorMessage("Tài khoản gửi tin");
        showTooltipSelectpicker(mess, "sending-account_" + id);

        /* showTooltip(mess, "sending-account_" + id);*/
        check = false;
    } else {
        removeTooltipSelectPicker("sending-account_" + id);
        if(check){
            check = true;
        }

    }

    if(productPackage == null || productPackage.length <= 0){
        let messsage = comboboxRequiredErrorMessage("Gói cước");
        showTooltipSelectpicker(messsage, 'product-package_'  + id);

        /*showTooltip(mess, "product-package_" + id);*/
        check = false;
    }else {
        removeTooltipSelectPicker("product-package_" + id);
        if(check){
            check = true;
        }
    }

    if(messageContent == null || messageContent.length <= 0){
        let mess = textRequiredErrorMessage("Nội dung");
        showToolTipOnBottom(mess, "message-content_" + id);
        check = false;
    }else {
        removeTooltip("message-content_" + id);
        if(check){
            check = true;
        }
    }

    if(check){
        saveMessageContent(id);
        $("#checkMessageContent_"+id).show();
        $("#popup_ctn_mess_"+id).modal("hide");
        if(id != "one"){
            let groupName = $("#subTargetGroupName_" + id).val();
            Swal.fire({
                position: 'top',
                type: 'success',
                title: 'Thông báo',
                text: "Cấu hình thông điệp cho nhóm "+groupName+" thành công",
                showConfirmButton: false,
                timer: 3000
            })
        }
    }
}

function loadListPackageSubGroup (){
    let packageGroup = $("#package-group").val();
    $.ajax({
        url: '/package-datas/find-active?packageGroup=' + packageGroup,
        type: 'get',
        dataType: 'json',
        async: false,
        success: function (response) {
            let packageSubGroup = $(".package-sub-group");
            packageSubGroup.each(function(i, selectBox) {
                $(selectBox).empty();
                response.forEach(element => {
                    $(selectBox).append('<option value="' + element.id + '">' + element.packageName + '</option>');
                });
                $(selectBox).selectpicker('refresh');
            });

        },
        error: function (e) {
            console.log(e);
        }
    });
}

function loadListPackage (){
    let packageGroup = $("#package-group").val();
    $.ajax({
        url: '/package-datas/find-active?packageGroup=' + packageGroup,
        type: 'get',
        dataType: 'json',
        async: false,
        success: function (response) {
            let package = $("#product-package_one");
            package.empty();
            response.forEach(element => {
                package.append('<option value="' + element.id + '">' + element.packageName + '</option>');
            });
            package.selectpicker('refresh');

        },
        error: function (e) {
            console.log(e);
        }
    });
}

async function loadListSendingAccountSubGroup (){
    await axios.get('/sending-accounts/get-list-active')
        .then(function (response) {
            let sendingAccountSubGroup = $(".sending-account-sub-group");
            sendingAccountSubGroup.each(function(i, selectBox) {
                $(selectBox).empty();
                response.data.forEach(element => {
                    $(selectBox).append('<option value="' + element.id + '">' + element.senderAccount + '</option>');
                });
                $(selectBox).selectpicker('refresh');
            });


        })
        .catch(function (error) {
            console.log(error);
        });
}

async function loadListSendingAccount (){
    await axios.get('/sending-accounts/get-list-active')
        .then(function (response) {
            let sendingAccountNoSubGroup = $("#sending-account_one");
            sendingAccountNoSubGroup.empty();
            response.data.forEach(element => {
                sendingAccountNoSubGroup.append('<option value="' + element.id + '">' + element.senderAccount + '</option>');
            });
            sendingAccountNoSubGroup.selectpicker('refresh');
        })
        .catch(function (error) {
            console.log(error);
        });
}

/* step 4 dem ky tu va mt noi dung tin */
$("#message-content").on("keyup", function (){
    var smsMessageContent = $(this).val();
    var posVNChar = checkVNCharacter(smsMessageContent);
    let MTInfo = "[length] ký tự - [count] tin nhắn (MT)."
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