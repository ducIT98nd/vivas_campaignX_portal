function validateStep3NoSubgroups (){

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

function validateStep3Subgroups (){
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
        let mess = comboboxRequiredErrorMessage("Kênh truyền thông");
        showTooltip(mess, "channel-marketing_" + id);
        check = false;
    }else {
        removeTooltip("channel-marketing_" + id);
        check = true;
    }

    if(sendingAccount == null || sendingAccount.length <= 0){
        $("#sending-account_" + id).next().removeAttr("title");
        let mess = comboboxRequiredErrorMessage("Tài khoản gửi tin");
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
        $("#product-package_" + id).next().removeAttr("title");
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
    let unicode = $("#unicode_" + id).val();
    if(messageContent == null || messageContent.length <= 0){
        let mess = textRequiredErrorMessage("Nội dung");
        showToolTipOnBottom(mess, "message-content_" + id);
        check = false;
    }else {
        removeTooltip("message-content_" + id);
        if(unicode == 8){
            let mess = $("#message-content_" + id).val();
            let mtLength = mess.length;
            if(mtLength > 268){
                showTooltip("Không nhập quá số ký tự cho phép.", "message-content_" + id);
                check = false;
            }
        }
        else if(unicode == 0){
            let mess = $("#message-content_" + id).val();
            let mtLength = mess.length;
            if(mtLength > 612){
                showTooltip("Không nhập quá số ký tự cho phép.", "message-content_" + id);
                check = false;
            }
        }
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
            let sendingAccountNoSubGroup = $(".sending-account-sub-group");
            sendingAccountNoSubGroup.each(function(i, selectBox) {
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
