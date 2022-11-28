function validateStep5(){

    let check = true;

    if ($('#checkbox-disable-sending-time-limit').is(':checked')){
        if ($('#checkbox-sending-time-limit-zalo').is(':checked') == false &&
            $('#checkbox-sending-time-limit-email').is(':checked') == false &&
            $('#checkbox-sending-time-limit-digilife').is(':checked') == false) {
            let messsage = comboboxRequiredErrorMessage("nhà mạng");
            showTooltipElementID(messsage, 'div-policy-sending-time');
            check = false;
        }else {
            let checked = $(".checkbox-policy-1");
            let obj = new Object();
            let jsonString = "";
            let value = checked.each(function() {
                let fieldName = this.value;
                obj[fieldName] = $(this).is(":checked");
                jsonString = JSON.stringify(obj);
            });
            $("#disable-policy-sending-time-limit").val(jsonString);
        }
    } else {
        removeTooltipElementID('div-policy-sending-time');
        $("#disable-policy-sending-time-limit").val("");
    }

    if ($('#checkbox-disable-message-limit').is(':checked')){
        if ($('#checkbox-message-limit-zalo').is(':checked') == false &&
            $('#checkbox-message-limit-email').is(':checked') == false &&
            $('#checkbox-message-limit-digilife').is(':checked') == false) {
            let messsage = comboboxRequiredErrorMessage("nhà mạng");
            showTooltipElementID(messsage, 'div-policy-message');
            check = false;
        } else {
            let checked = $(".checkbox-policy-2");
            let obj = new Object();
            let jsonString = "";
            let value = checked.each(function() {
                let fieldName = this.value;
                obj[fieldName] = $(this).is(":checked");
                jsonString = JSON.stringify(obj);
            });
            $("#disable-policy-message-limit").val(jsonString);
        }
    } else {
        removeTooltipElementID('div-policy-message');
        $("#disable-policy-message-limit").val("");
    }

    /* Chu kỳ giữa các sự kiện */
    let eventCycle = $("#event-cycle").val();

    if (eventCycle != null && eventCycle.length != 0 && !isPositiveInteger(eventCycle)){
        let messsage = "Chu kỳ giữa các sự kiện nhập số nguyên dương";
        showTooltipElementID(messsage, 'event-cycle');
        check = false;
    } else {
        removeTooltipElementID('event-cycle');
    }


    /* Giới hạn sự kiện */
    let eventLimitPerDay = $("#event-limit-per-day").val();

    if (eventLimitPerDay != null && eventLimitPerDay.length != 0 && !isPositiveInteger(eventLimitPerDay)){
        let messsage = "Giới hạn sự kiện nhập số nguyên dương";
        showTooltipElementID(messsage, 'event-limit-per-day');
        check = false;
    } else {
        removeTooltipElementID('event-limit-per-day');
    }

    return check;
}

$(document).ready(function () {
    $('#checkbox-disable-sending-time-limit').on("ifToggled",function() {
        let lstCheckbox = $(".checkbox-policy-1");
        if(this.checked) {
            lstCheckbox.each(function( index ) {
                 $( this ).iCheck('check');
            });
            $('#div-policy-sending-time').show();
        } else {
            lstCheckbox.each(function( index ) {
                $( this ).iCheck('uncheck');
            });
            $('#div-policy-sending-time').hide();
        }
    });
    $('#checkbox-disable-message-limit').on("ifToggled",function() {
        let lstCheckbox = $(".checkbox-policy-2");
        if(this.checked) {
            lstCheckbox.each(function( index ) {
                $( this ).iCheck('check');
            });
            $('#div-policy-message').show();
        } else {
            lstCheckbox.each(function( index ) {
                $( this ).iCheck('uncheck');
            });
            $('#div-policy-message').hide();
        }
    });
});