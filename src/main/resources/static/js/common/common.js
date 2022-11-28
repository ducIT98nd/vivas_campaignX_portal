
function blockCharacterByInputNumber(event) {
    var k = event ? event.which : window.event.keyCode;
    if (k != 8 && k != 0 && (k <= 47 || k > 57)) return false;
}

function blockCharacterDotByInputNumber(event) {
    var k = event ? event.which : window.event.keyCode;
    if (k != 8 && k != 0 && (k < 46 || k > 57)) return false;
}

function showAlertMessageSuccess(message){
    Swal.fire({
        position: 'top',
        type: 'success',
        title: 'Thông báo',
        text: message,
        showConfirmButton: false,
    });
}

function showAlertMessageError(message){
    Swal.fire({
        position: 'top',
        type: 'error',
        title: 'Thông báo',
        text: message,
        showConfirmButton: false,
    });
}

function showAlertMessageInfo(message){
    Swal.fire({
        position: 'top',
        type: 'info',
        title: 'Thông báo',
        text: message,
        showConfirmButton: false,
    });
}

function showToastMessageError(message){
    Swal.fire({
        position: 'top',
        type: 'error',
        title: 'Thông báo',
        text: message,
        showConfirmButton: false,
        timer: 3000
    })
}

function showToastMessageSuccess(message){
    Swal.fire({
        position: 'top',
        type: 'success',
        title: 'Thông báo',
        text: message,
        showConfirmButton: false,
        timer: 3000
    })
}

function showToastMessageInfo(message){
    Swal.fire({
        position: 'top',
        type: 'info',
        title: 'Thông báo',
        text: message,
        showConfirmButton: false,
        timer: 3000
    })
}

function cancel(url, message) {
    Swal.fire({
        position: 'top',
        title: 'Thông báo',
        text: message,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'OK',
        cancelButtonText: 'HỦY'
    }).then((result) => {
        if (result.value) {
            window.location.href = url;
        }
    });
}

function logout() {
    Swal.fire({
        position: 'top',
        title: 'Thông báo',
        text: 'Tài khoản của bạn đã bị xóa khỏi hệ thống bởi Quản trị viên. Vui lòng liên hệ Quản trị viên để biết thêm thông tin.',
        icon: 'warning',
        showCancelButton: false,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'OK',
    }).then((result) => {
        if (result.value) {
            window.location.href = "../logout";
        }
    });
}

function logoutRenewpassword() {
    Swal.fire({
        position: 'top',
        title: 'Thông báo',
        text: 'Mật khẩu của bạn đã được thay đổi bởi Quản trị viên. Vui lòng liên hệ Quản trị viên để lấy thông tin mật khẩu và đăng nhập lại.',
        icon: 'warning',
        showCancelButton: false,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'OK',
    }).then((result) => {
        if (result.value) {
            window.location.href = "../logout";
        }
    });
}

function showTooltip(message, elementID){
    $("#" + elementID).parent().removeClass("has-danger");
    $("#" + elementID).attr("title", message);
    $("#" + elementID).addClass("tooltipError");
    $("#" + elementID).parent().addClass("has-danger");

    $('#' + elementID).tooltip({
        position: {
            my: "left top",
            at: "right+5 top-5",
            collision: "none"
        }
    });

    $( ".tooltipError" ).keyup(function() {
        let value = $(this).val();
        if(value != null && value.length > 0){
            $(this).removeAttr("title");
            $(this).parent().removeClass("has-danger");
        }
    });
}

function showToolTipOnBottom(message, elementID){
    $("#" + elementID).parent().removeClass("has-danger");
    $("#" + elementID).attr("title", message);
    $("#" + elementID).addClass("tooltipError");
    $("#" + elementID).parent().addClass("has-danger");

    $('#' + elementID).tooltip({
        position: {
            my: "top",
            at: "bottom",
            collision: "none"
        }
    });

    $( ".tooltipError" ).keyup(function() {
        let value = $(this).val();
        if(value != null && value.length > 0){
            $(this).removeAttr("title");
            $(this).parent().removeClass("has-danger");
        }
    });
}

function showTooltipElementID(message, elementID){
    $("#" + elementID).removeClass("has-danger");
    $("#" + elementID).attr("title", message);
    $("#" + elementID).addClass("tooltipError");
    $("#" + elementID).addClass("has-danger");

    $("#" + elementID).tooltip({
        position: {
            my: "left top",
            at: "right+5 top-5",
            collision: "none"
        }
    });
}

function showTooltipSelectpicker(message, element){
    if (typeof element == 'string'){
        $("#" + element).parent().removeClass("has-danger-selectpicker");
        $("#" + element).parent().attr("title", message);
        $("#" + element).parent().addClass("tooltipError");
        $("#" + element).parent().addClass("has-danger-selectpicker");

        $("#" + element).parent().tooltip({
            position: {
                my: "top",
                at: "bottom",
                collision: "none"
            }
        });

        $( "#" + element ).change(function() {
            $(this).parent().removeClass("has-danger-selectpicker");
            $(this).parent().removeAttr("title");
            $(this).next().removeAttr("title");
            $(this).removeAttr("title");
        });
    } else if (element instanceof Element) {
        $(element).parent().removeClass("has-danger-selectpicker");
        $(element).parent().attr("title", message);
        $(element).parent().addClass("tooltipError");
        $(element).parent().addClass("has-danger-selectpicker");

        $(element).parent().tooltip({
            position: {
                my: "left top",
                at: "right+5 top-5",
                collision: "none"
            }
        });
    }
}


function showTooltipElement(message, element){
    $(element).removeClass("has-danger-1");
    $(element).attr("title", message);
    $(element).addClass("tooltipError");
    $(element).addClass("has-danger-1");

    $('.tooltipError').tooltip({
        position: {
            my: "left top",
            at: "right+5 top-5",
            collision: "none"
        }
    });
}

function showTooltipOnParent(message, element){
    $(element).parent().removeClass("has-danger-1");
    $(element).parent().attr("title", message);
    $(element).parent().addClass("tooltipError");
    $(element).parent().addClass("has-danger-1");

    $(element).tooltip({
        position: {
            my: "left top",
            at: "right+5 top-5",
            collision: "none"
        }
    });

    $(element).click(function() {
        $(this).parent().removeClass("has-danger-1");
        $(this).parent().removeClass("tooltipError");
        $(this).parent().removeAttr("title");
    });
}

function showToolipParamCriteria(message, elementID){
    $("#" + elementID).parent().removeClass("has-danger-1");
    $("#" + elementID).attr("title", message);
    $("#" + elementID).addClass("tooltipError");
    $("#" + elementID).parent().addClass("has-danger-1");
    $('.tooltipError').tooltip({
        position: {
            my: "left top",
            at: "right+5 top-5",
            collision: "none"
        }
    });

    $( ".tooltipError" ).keyup(function() {
        let value = $(this).val();
        if(value != null && value.length > 0){
            $(this).removeAttr("title");
            $(this).parent().removeClass("has-danger-1");
        }
    });
}

function showToolipCriteria(message, elementID){
    $("#" + elementID).parent().removeClass("has-danger-1");
    $("#" + elementID).attr("title", message);
    $("#" + elementID).addClass("tooltipError");
    $("#" + elementID).parent().addClass("has-danger-1");
    $("#" + elementID).tooltip({
        position: {
            my: "left top",
            at: "right+5 top-5",
            collision: "none"
        }
    });
}

function showToolipIcheckBox(message, elementClass, divMessage){

    $("." + elementClass).each(function(i, obj) {
        $(this).parent().removeClass("has-danger-icheckbox");
        $(this).parent().addClass("has-danger-icheckbox");
        $(this).parent().tooltip({
            position: {
                my: "left top",
                at: "right+5 top-5",
                collision: "none"
            }
        });
    });

    $("#" + divMessage).attr("title", message);
    $("#" + divMessage).addClass("tooltipError");

}

function removeTooltipElementID(elementID){
    $("#" + elementID).removeClass("has-danger");
    $("#" + elementID).removeAttr("title");
}

function removeTooltipSelectPicker(element){
    if (typeof element == 'string'){
        $("#" + element).parent().removeClass("has-danger-selectpicker");
        $("#" + element).parent().removeAttr("title");
    } else if (element instanceof Element) {
        $(element).parent().removeClass("has-danger-selectpicker");
        $(element).parent().removeAttr("title");
    }
}

function removeTooltipElement(elementID){
    $(elementID).removeClass("has-danger-1");
    $(elementID).removeAttr("title");
}

function removeTooltipOnParent(element){
    $(element).parent().removeClass("has-danger-1");
    $(element).parent().removeAttr("title");
}

function removeTooltip(elementID){
    $("#" + elementID).parent().removeClass("has-danger");
    $("#" + elementID).removeAttr("title");
}

function removeTooltipParamCriteria(elementID){
    $("#" + elementID).parent().removeClass("has-danger-1");
    $("#" + elementID).removeAttr("title");
}


function textRequiredErrorMessage(fieldName){
    return fieldName + " không được để trống.";
}

function comboboxRequiredErrorMessage(fieldName){
    let message = "Vui lòng chọn [fieldName].";
    message = message.replace("[fieldName]", fieldName);
    return message;
}

function existedErrorMessage(fieldName){
    let message = "[fieldName] đã tồn tại.";
    message = message.replace("[fieldName]", fieldName);
    return message;
}

function characterLimitedErrorMessage(fieldName, maximumChar){
    let message = "[fieldName] nhập tối đa [maximumChar] ký tự.";
    message = message.replace("[fieldName]", fieldName);
    message = message.replace("[maximumChar]", maximumChar);
    return message;
}

function characterTypeErrorMessage(charType){
    let message = "Chỉ nhập [charType].";
    message = message.replace("[charType]", charType);
    return message;
}

function isPositiveInteger(str) {
    if (typeof str !== 'string') {
        return false;
    }

    const num = Number(str);

    if (Number.isInteger(num) && num >= 0) {
        return true;
    }

    return false;
}

function convertCalendarToDate (dateString){
    let dateParts = dateString.split("/");
    return  new Date(+dateParts[2], dateParts[1] - 1, +dateParts[0]);
}

function removeTooltipICheckbox(elementClass, divMessage){

    $("." + elementClass).each(function(i, obj) {
        $(this).parent().removeClass("has-danger-icheckbox");
    });

    $("#" + divMessage).removeClass("tooltipError");
    $("#" + divMessage).removeAttr("title");
}

function keyPressPositiveNumber (event){
    return (event.charCode == 8 || event.charCode == 0 || event.charCode == 13) ? null : event.charCode >= 48 && event.charCode <= 57;
}

function keyPressMonthDay (event){
    return (event.charCode == 8 || event.charCode == 0 || event.charCode == 13) ? null : event.charCode >= 48 && event.charCode <= 57;
}

function pastePositiveNumber (event){
    // Stop data actually being pasted into div
    var pattern = new RegExp('^[1-9]\\d*$');
    // Get pasted data via clipboard API
    let clipboardData = event.clipboardData || window.clipboardData;
    let pastedData = clipboardData.getData('Text');
    let abc = pattern.test(pastedData);
    return abc;
}

function format_two_digits(n) {
    return n < 10 ? '0' + n : n;
}

$(document).ready(function(){
    var today = new Date();
    var date = format_two_digits(today.getDate()) + '/' + format_two_digits((today.getMonth() + 1)) + '/' + today.getFullYear();
    var time = format_two_digits(today.getHours()) + ":" + format_two_digits(today.getMinutes()) + ":" + format_two_digits(today.getSeconds());
    var dateTime = date + ' ' + time;
    $(".titleDate").attr("title", "Cập nhật cuối: "+ dateTime);
});
