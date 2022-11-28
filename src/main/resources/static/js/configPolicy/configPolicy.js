$(document).ready(function () {
    document.getElementById("delete-time-sms-1").disabled = true;
    document.getElementById("delete-time-ussd-1").disabled = true;
    document.getElementById("delete-time-ivr-1").disabled = true;
})
var countSMS = 2;
var sumSMS = 1;
var countUSSD = 2;
var sumUSSD = 1;
var countIVR = 2;
var sumIVR = 1;

function addSMS() {
    var cols = "";
    cols += '<div class="row" id="SMS_' + (countSMS) + '" >\n' +
        '                                                            <div class="col-md-10" id="alert-time-sms-' + (countSMS) + '">\n' +
        '                                                                <div class="row time-limit-sms">\n' +
        '                                                                    <div class="col-md-6 form-group">\n' +
        '                                                                        <label class="control-label">Từ</label>\n' +
        '                                                                        <input class="form-control"\n' +
        '                                                                               id="start-time-limit-sms-' + (countSMS) + '"\n' +
        '                                                                               name="start-time-limit-sms"\n' +
        '                                                                               \n' +
        '                                                                               type="text">\n' +
        '                                                                    </div>\n' +
        '                                                                    <div class="col-md-6 form-group">\n' +
        '                                                                        <label class="control-label">Đến</label>\n' +
        '                                                                        <input class="form-control" type="text"\n' +
        '                                                                               id="end-time-limit-sms-' + (countSMS) + '"\n' +
        '                                                                               name="end-time-limit-sms"\n' +
        '                                                                               >\n' +
        '                                                                    </div>\n' +
        '                                                                </div>\n' +
        '                                                            </div>\n' +
        '                                                            <div class="col-md-2 act_crit poli_btn">\n' +
        '                                                                <button type="button"\n' +
        '                                                                        class="btn btn-rounded btn-danger size_i"\n' +
        '                                                                        data-toggle="tooltip" data-placement="top"\n' +
        '                                                                        title="Xóa tiêu chí" id="delete-time-sms-' + (countSMS) + '" onclick="deleteSMS('+countSMS+')">\n' +
        '                                                                    <i class="ti-close"></i>\n' +
        '                                                                </button>\n' +
        '                                                                <button type="button" class="btn btn_ksn btn-save"\n' +
        '                                                                        data-toggle="tooltip" data-placement="top"\n' +
        '                                                                        title="Tạo tiêu chí con" id="add-time-sms-' + (countSMS) + '" onclick="addSMS()">\n' +
        '                                                                    <i class="ti-plus"></i>\n' +
        '                                                                </button>\n' +
        '                                                            </div>\n' +
        '                                                        </div>'
    $("#addSMS").append(cols);

    $("#start-time-limit-sms-"+ countSMS).qcTimepicker({
        // additional CSS classes
        classes: 'form-control',
        format: 'HH:mm',
        // step size in ms
        step: 1800,
    });
    $("#start-time-limit-sms-"+countSMS+"-qcTimepicker").val("00:00").change();

    $("#end-time-limit-sms-"+ countSMS).qcTimepicker({
        // additional CSS classes
        classes: 'form-control',
        format: 'HH:mm',
        // step size in ms
        step: 1800,
    });
    $("#end-time-limit-sms-"+countSMS+"-qcTimepicker").val("23:30").change();
    sumSMS++;
    if (sumSMS == 2) {
        if(document.getElementById("delete-time-sms-1") != null) {
            document.getElementById("delete-time-sms-1").disabled = false;
        }
        if(document.getElementById("add-time-sms-1") != null) {
            document.getElementById("add-time-sms-1").disabled = true;
        }
        if(document.getElementById("delete-time-sms-2") != null) {
            document.getElementById("delete-time-sms-2").disabled = false;
        }
        if(document.getElementById("add-time-sms-2") != null) {
            document.getElementById("add-time-sms-2").disabled = true;
        }
    }
}

function deleteSMS(count) {
    $("#SMS_" + count).remove();
    countSMS = count;
    sumSMS--;
    if (sumSMS == 1) {
        if(document.getElementById("delete-time-sms-1") != null) {
            document.getElementById("delete-time-sms-1").disabled = true;
        }
        if(document.getElementById("add-time-sms-1") != null) {
            document.getElementById("add-time-sms-1").disabled = false;
        }
        if(document.getElementById("delete-time-sms-2") != null) {
            document.getElementById("delete-time-sms-2").disabled = true;
        }
        if(document.getElementById("add-time-sms-2") != null) {
            document.getElementById("add-time-sms-2").disabled = false;
        }
    }
}


function addUSSD() {
    var cols = "";
    cols += '<div class="row" id="USSD_' + (countUSSD) + '" >\n' +
        '                                                            <div class="col-md-10" id="alert-time-ussd-' + (countUSSD ) + '">\n' +
        '                                                                <div class="row time-limit-ussd">\n' +
        '                                                                    <div class="col-md-6 form-group">\n' +
        '                                                                        <label class="control-label">Từ</label>\n' +
        '                                                                        <input class="form-control"\n' +
        '                                                                               id="start-time-limit-ussd-' + (countUSSD ) + '"\n' +
        '                                                                               name="start-time-limit-ussd"\n' +
        '                                                                               th:value="${startTimeLimitSms1}"\n' +
        '                                                                               type="text">\n' +
        '                                                                    </div>\n' +
        '                                                                    <div class="col-md-6 form-group">\n' +
        '                                                                        <label class="control-label">Đến</label>\n' +
        '                                                                        <input class="form-control" type="text"\n' +
        '                                                                               id="end-time-limit-ussd-' + (countUSSD) + '"\n' +
        '                                                                               name="end-time-limit-ussd"\n' +
        '                                                                               th:value="${endTimeLimitSms1}">\n' +
        '                                                                    </div>\n' +
        '                                                                </div>\n' +
        '                                                            </div>\n' +
        '                                                            <div class="col-md-2 act_crit poli_btn">\n' +
        '                                                                <button type="button"\n' +
        '                                                                        class="btn btn-rounded btn-danger size_i"\n' +
        '                                                                        data-toggle="tooltip" data-placement="top"\n' +
        '                                                                        title="Xóa tiêu chí" id="delete-time-ussd-' + (countUSSD) + '" onclick="deleteUSSD('+countUSSD+')">\n' +
        '                                                                    <i class="ti-close"></i>\n' +
        '                                                                </button>\n' +
        '                                                                <button type="button" class="btn btn_ksn btn-save"\n' +
        '                                                                        data-toggle="tooltip" data-placement="top"\n' +
        '                                                                        title="Tạo tiêu chí con" id="add-time-ussd-' + (countUSSD) + '" onclick="addUSSD()">\n' +
        '                                                                    <i class="ti-plus"></i>\n' +
        '                                                                </button>\n' +
        '                                                            </div>\n' +
        '                                                        </div>'
    $("#addUSSD").append(cols);
    $("#start-time-limit-ussd-"+countUSSD).qcTimepicker({
        // additional CSS classes
        classes: 'form-control',
        format: 'HH:mm',
        // step size in ms
        step: 1800,
    });
    $("#start-time-limit-ussd-"+countUSSD+"-qcTimepicker").val("00:00").change();

    $("#end-time-limit-ussd-"+countUSSD).qcTimepicker({
        // additional CSS classes
        classes: 'form-control',
        format: 'HH:mm',
        // step size in ms
        step: 1800,
    });

    $("#end-time-limit-ussd-"+countUSSD+"-qcTimepicker").val("23:30").change();
    sumUSSD++;
    if (sumUSSD == 2) {
        if(document.getElementById("delete-time-ussd-1") != null) {
            document.getElementById("delete-time-ussd-1").disabled = false;
        }
        if(document.getElementById("add-time-ussd-1") != null) {
            document.getElementById("add-time-ussd-1").disabled = true;
        }
        if(document.getElementById("delete-time-ussd-2") != null) {
            document.getElementById("delete-time-ussd-2").disabled = false;
        }
        if(document.getElementById("add-time-ussd-2") != null) {
            document.getElementById("add-time-ussd-2").disabled = true;
        }
    }

}

function deleteUSSD(count) {
    $("#USSD_"+count ).remove();
    sumUSSD--;
    countUSSD = count;
    if (sumUSSD == 1) {
        if(document.getElementById("delete-time-ussd-1") != null) {
            document.getElementById("delete-time-ussd-1").disabled = true;
        }
        if(document.getElementById("add-time-ussd-1") != null) {
            document.getElementById("add-time-ussd-1").disabled = false;
        }
        if(document.getElementById("delete-time-ussd-2") != null) {
            document.getElementById("delete-time-ussd-2").disabled = true;
        }
        if(document.getElementById("add-time-ussd-2") != null) {
            document.getElementById("add-time-ussd-2").disabled = false;
        }

    }
}


function addIVR() {
    var cols = "";
    cols += '<div class="row" id="IVR_' + (countIVR) + '" >\n' +
        '                                                            <div class="col-md-10" id="alert-time-ivr-' + (countIVR ) + '">\n' +
        '                                                                <div class="row time-limit-ivr">\n' +
        '                                                                    <div class="col-md-6 form-group">\n' +
        '                                                                        <label class="control-label">Từ</label>\n' +
        '                                                                        <input class="form-control"\n' +
        '                                                                               id="start-time-limit-ivr-' + (countIVR) + '"\n' +
        '                                                                               name="start-time-limit-ivr"\n' +
        '                                                                               th:value="${startTimeLimitSms1}"\n' +
        '                                                                               type="text">\n' +
        '                                                                    </div>\n' +
        '                                                                    <div class="col-md-6 form-group">\n' +
        '                                                                        <label class="control-label">Đến</label>\n' +
        '                                                                        <input class="form-control" type="text"\n' +
        '                                                                               id="end-time-limit-ivr-' + (countIVR) + '"\n' +
        '                                                                               name="end-time-limit-ivr"\n' +
        '                                                                               th:value="${endTimeLimitSms1}">\n' +
        '                                                                    </div>\n' +
        '                                                                </div>\n' +
        '                                                            </div>\n' +
        '                                                            <div class="col-md-2 act_crit poli_btn">\n' +
        '                                                                <button type="button"\n' +
        '                                                                        class="btn btn-rounded btn-danger size_i"\n' +
        '                                                                        title="Xóa tiêu chí" id="delete-time-ivr-' + (countIVR) + '" onclick="deleteIVR('+countIVR+')">\n' +
        '                                                                    <i class="ti-close"></i>\n' +
        '                                                                </button>\n' +
        '                                                                <button type="button" class="btn btn_ksn btn-save"\n' +
        '                                                                        title="Tạo tiêu chí con" id="add-time-ivr-' + (countIVR) + '" onclick="addIVR()">\n' +
        '                                                                    <i class="ti-plus"></i>\n' +
        '                                                                </button>\n' +
        '                                                            </div>\n' +
        '                                                        </div>'
    $("#addIVR").append(cols);
    sumIVR++;
    if (sumIVR == 2) {
        if(document.getElementById("add-time-ivr-1") != null) {
            document.getElementById("add-time-ivr-1").disabled = true;
        }
        if(document.getElementById("delete-time-ivr-1") != null) {
            document.getElementById("delete-time-ivr-1").disabled = false;
        }
        if(document.getElementById("add-time-ivr-2") != null) {
            document.getElementById("add-time-ivr-2").disabled = true;
        }
        if(document.getElementById("delete-time-ivr-2") != null) {
            document.getElementById("delete-time-ivr-2").disabled = false;
        }
    }
    $("#start-time-limit-ivr-"+countIVR).qcTimepicker({
        // additional CSS classes
        classes: 'form-control',
        format: 'HH:mm',
        // step size in ms
        step: 1800,
    });
    $("#start-time-limit-ivr-"+countIVR+"-qcTimepicker").val("00:00").change();

    $("#end-time-limit-ivr-"+countIVR).qcTimepicker({
        // additional CSS classes
        classes: 'form-control',
        format: 'HH:mm',
        // step size in ms
        step: 1800,
    });

    $("#end-time-limit-ivr-"+countIVR+"-qcTimepicker").val("23:30").change();
}

function deleteIVR(count) {
    $("#IVR_" + count).remove();
    sumIVR--;
    countIVR = count;
    if (sumIVR == 1) {
        if(document.getElementById("add-time-ivr-1") != null) {
            document.getElementById("add-time-ivr-1").disabled = false;
        }
        if(document.getElementById("delete-time-ivr-1") != null) {
            document.getElementById("delete-time-ivr-1").disabled = true;
        }
        if(document.getElementById("add-time-ivr-2") != null) {
            document.getElementById("add-time-ivr-2").disabled = false;
        }
        if(document.getElementById("delete-time-ivr-2") != null) {
            document.getElementById("delete-time-ivr-2").disabled = true;
        }
    }
}


$(document).ready(function () {

    //SMS
    const startTimeLimitSms1 = document.getElementById("start-time-limit-sms-1");
    const endTimeLimitSms1 = document.getElementById("end-time-limit-sms-1");
    const startTimeLimitSms2 = document.getElementById("start-time-limit-sms-2");
    const endTimeLimitSms2 = document.getElementById("end-time-limit-sms-2");
    const startTimeLimitSmsView = $("#start-time-limit-sms-view").val();
    const endTimeLimitSmsView = $("#end-time-limit-sms-view").val();
    $("#start-time-limit-sms-1").qcTimepicker({
        // additional CSS classes
        classes: 'form-control',
        format: 'HH:mm',
        // step size in ms
        step: 1800,
    });
    $("#start-time-limit-sms-1-qcTimepicker").val(startTimeLimitSms1.value).change();

    $("#end-time-limit-sms-1").qcTimepicker({
        // additional CSS classes
        classes: 'form-control',
        format: 'HH:mm',
        // step size in ms
        step: 1800,
    });
    $("#end-time-limit-sms-1-qcTimepicker").val(endTimeLimitSms1.value).change();

    if(startTimeLimitSmsView != "" && endTimeLimitSmsView != "") {
        sumSMS = 2;
        var cols = "";
        cols += '<div class="row" id="SMS_2" >\n' +
            '                                                            <div class="col-md-10" id="alert-time-sms-2">\n' +
            '                                                                <div class="row time-limit-sms">\n' +
            '                                                                    <div class="col-md-6 form-group">\n' +
            '                                                                        <label class="control-label">Từ</label>\n' +
            '                                                                        <input class="form-control"\n' +
            '                                                                               id="start-time-limit-sms-2"\n' +
            '                                                                               name="start-time-limit-sms"\n' +
            '                                                                               th:value="${startTimeLimitSms2}"\n' +
            '                                                                               type="text">\n' +
            '                                                                    </div>\n' +
            '                                                                    <div class="col-md-6 form-group">\n' +
            '                                                                        <label class="control-label">Đến</label>\n' +
            '                                                                        <input class="form-control" type="text"\n' +
            '                                                                               id="end-time-limit-sms-2"\n' +
            '                                                                               name="end-time-limit-sms"\n' +
            '                                                                               th:value="${endTimeLimitSms2}">\n' +
            '                                                                    </div>\n' +
            '                                                                </div>\n' +
            '                                                            </div>\n' +
            '                                                            <div class="col-md-2 act_crit poli_btn">\n' +
            '                                                                <button type="button"\n' +
            '                                                                        class="btn btn-rounded btn-danger size_i"\n' +
            '                                                                        title="Xóa tiêu chí" id="delete-time-sms-2" onclick="deleteSMS(2)">\n' +
            '                                                                    <i class="ti-close"></i>\n' +
            '                                                                </button>\n' +
            '                                                                <button type="button" class="btn btn_ksn btn-save"\n' +
            '                                                                        title="Tạo tiêu chí con" id="add-time-sms-2" onclick="addSMS()">\n' +
            '                                                                    <i class="ti-plus"></i>\n' +
            '                                                                </button>\n' +
            '                                                            </div>\n' +
            '                                                        </div>'
        $("#addSMS").append(cols);
        if (sumSMS == 2) {
            if(document.getElementById("delete-time-sms-1") != null) {
                document.getElementById("delete-time-sms-1").disabled = false;
            }
            if(document.getElementById("add-time-sms-1") != null) {
                document.getElementById("add-time-sms-1").disabled = true;
            }
            if(document.getElementById("delete-time-sms-2") != null) {
                document.getElementById("delete-time-sms-2").disabled = false;
            }
            if(document.getElementById("add-time-sms-2") != null) {
                document.getElementById("add-time-sms-2").disabled = true;
            }
        }
        $("#start-time-limit-sms-2").qcTimepicker({
            // additional CSS classes
            classes: 'form-control',
            format: 'HH:mm',
            // step size in ms
            step: 1800,
        });
        $("#start-time-limit-sms-2-qcTimepicker").val(startTimeLimitSmsView).change();

        $("#end-time-limit-sms-2").qcTimepicker({
            // additional CSS classes
            classes: 'form-control',
            format: 'HH:mm',
            // step size in ms
            step: 1800,
        });
        $("#end-time-limit-sms-2-qcTimepicker").val(endTimeLimitSmsView).change();
    }
    //USSD

    const startTimeLimitussd1 = document.getElementById("start-time-limit-ussd-1");
    const endTimeLimitussd1 = document.getElementById("end-time-limit-ussd-1");
    const startTimeLimitussd2 = document.getElementById("start-time-limit-ussd-2");
    const endTimeLimitussd2 = document.getElementById("end-time-limit-ussd-2");
    const startTimeLimitUSSDView = $("#start-time-limit-ussd-view").val();
    const endTimeLimitUSSDView = $("#end-time-limit-ussd-view").val();
    $("#start-time-limit-ussd-1").qcTimepicker({
        // additional CSS classes
        classes: 'form-control',
        format: 'HH:mm',
        // step size in ms
        step: 1800,
    });
    $("#start-time-limit-ussd-1-qcTimepicker").val(startTimeLimitussd1.value).change();

    $("#end-time-limit-ussd-1").qcTimepicker({
        // additional CSS classes
        classes: 'form-control',
        format: 'HH:mm',
        // step size in ms
        step: 1800,
    });
    $("#end-time-limit-ussd-1-qcTimepicker").val(endTimeLimitussd1.value).change();

    if(startTimeLimitUSSDView != "" && endTimeLimitUSSDView != "") {
        sumUSSD = 2;
        var cols = "";
        cols += '<div class="row" id="USSD_2" >\n' +
            '                                                            <div class="col-md-10" id="alert-time-ussd-2">\n' +
            '                                                                <div class="row time-limit-ussd">\n' +
            '                                                                    <div class="col-md-6 form-group">\n' +
            '                                                                        <label class="control-label">Từ</label>\n' +
            '                                                                        <input class="form-control"\n' +
            '                                                                               id="start-time-limit-ussd-2"\n' +
            '                                                                               name="start-time-limit-ussd"\n' +
            '                                                                               th:value="${startTimeLimitUssd2}"\n' +
            '                                                                               type="text">\n' +
            '                                                                    </div>\n' +
            '                                                                    <div class="col-md-6 form-group">\n' +
            '                                                                        <label class="control-label">Đến</label>\n' +
            '                                                                        <input class="form-control" type="text"\n' +
            '                                                                               id="end-time-limit-ussd-2"\n' +
            '                                                                               name="end-time-limit-ussd"\n' +
            '                                                                               th:value="${endTimeLimitUssd2}">\n' +
            '                                                                    </div>\n' +
            '                                                                </div>\n' +
            '                                                            </div>\n' +
            '                                                            <div class="col-md-2 act_crit poli_btn">\n' +
            '                                                                <button type="button"\n' +
            '                                                                        class="btn btn-rounded btn-danger size_i"\n' +
            '                                                                        title="Xóa tiêu chí" id="delete-time-ussd-2" onclick="deleteUSSD(2)">\n' +
            '                                                                    <i class="ti-close"></i>\n' +
            '                                                                </button>\n' +
            '                                                                <button type="button" class="btn btn_ksn btn-save"\n' +
            '                                                                        title="Tạo tiêu chí con" id="add-time-ussd-2" onclick="addUSSD()">\n' +
            '                                                                    <i class="ti-plus"></i>\n' +
            '                                                                </button>\n' +
            '                                                            </div>\n' +
            '                                                        </div>'
        $("#addUSSD").append(cols);
        if (sumUSSD == 2) {
            if(document.getElementById("delete-time-ussd-1") != null) {
                document.getElementById("delete-time-ussd-1").disabled = false;
            }
            if(document.getElementById("add-time-ussd-1") != null) {
                document.getElementById("add-time-ussd-1").disabled = true;
            }
            if(document.getElementById("delete-time-ussd-2") != null) {
                document.getElementById("delete-time-ussd-2").disabled = false;
            }
            if(document.getElementById("add-time-ussd-2") != null) {
                document.getElementById("add-time-ussd-2").disabled = true;
            }
        }
        $("#start-time-limit-ussd-2").qcTimepicker({
            // additional CSS classes
            classes: 'form-control',
            format: 'HH:mm',
            // step size in ms
            step: 1800,
        });
        $("#start-time-limit-ussd-2-qcTimepicker").val(startTimeLimitUSSDView).change();

        $("#end-time-limit-ussd-2").qcTimepicker({
            // additional CSS classes
            classes: 'form-control',
            format: 'HH:mm',
            // step size in ms
            step: 1800,
        });
        $("#end-time-limit-ussd-2-qcTimepicker").val(endTimeLimitUSSDView).change();
    }


    //IVR
    const addTimeivr2 = document.getElementById("add-time-ivr-2");
    const timeivr2 = document.getElementById("time-ivr-2");
    const removeTimeivr2 = document.getElementById("remove-time-ivr-2");
    const startTimeLimitivr1 = document.getElementById("start-time-limit-ivr-1");
    const endTimeLimitivr1 = document.getElementById("end-time-limit-ivr-1");
    const startTimeLimitivr2 = document.getElementById("start-time-limit-ivr-2");
    const endTimeLimitivr2 = document.getElementById("end-time-limit-ivr-2");
    const startTimeLimitIVRView = $("#start-time-limit-ivr-view").val();
    const endTimeLimitIVRView = $("#end-time-limit-ivr-view").val();
    $("#start-time-limit-ivr-1").qcTimepicker({
        // additional CSS classes
        classes: 'form-control',
        format: 'HH:mm',
        // step size in ms
        step: 1800,
    });
    $("#start-time-limit-ivr-1-qcTimepicker").val(startTimeLimitivr1.value).change();

    $("#end-time-limit-ivr-1").qcTimepicker({
        // additional CSS classes
        classes: 'form-control',
        format: 'HH:mm',
        // step size in ms
        step: 1800,
    });

    $("#end-time-limit-ivr-1-qcTimepicker").val(endTimeLimitivr1.value).change();

    if(startTimeLimitIVRView != "" && endTimeLimitIVRView != "") {
        sumIVR = 2;
        var cols = "";
        cols += '<div class="row" id="IVR_2" >\n' +
            '                                                            <div class="col-md-10" id="alert-time-ivr-2">\n' +
            '                                                                <div class="row time-limit-ivr">\n' +
            '                                                                    <div class="col-md-6 form-group">\n' +
            '                                                                        <label class="control-label">Từ</label>\n' +
            '                                                                        <input class="form-control"\n' +
            '                                                                               id="start-time-limit-ivr-2"\n' +
            '                                                                               name="start-time-limit-ivr"\n' +
            '                                                                               th:value="${startTimeLimitIvr2}"\n' +
            '                                                                               type="text">\n' +
            '                                                                    </div>\n' +
            '                                                                    <div class="col-md-6 form-group">\n' +
            '                                                                        <label class="control-label">Đến</label>\n' +
            '                                                                        <input class="form-control" type="text"\n' +
            '                                                                               id="end-time-limit-ivr-2"\n' +
            '                                                                               name="end-time-limit-ivr"\n' +
            '                                                                               th:value="${endTimeLimitIvr2}">\n' +
            '                                                                    </div>\n' +
            '                                                                </div>\n' +
            '                                                            </div>\n' +
            '                                                            <div class="col-md-2 act_crit poli_btn">\n' +
            '                                                                <button type="button"\n' +
            '                                                                        class="btn btn-rounded btn-danger size_i"\n' +
            '                                                                        title="Xóa tiêu chí" id="delete-time-ivr-2" onclick="deleteIVR(2)">\n' +
            '                                                                    <i class="ti-close"></i>\n' +
            '                                                                </button>\n' +
            '                                                                <button type="button" class="btn btn_ksn btn-save"\n' +
            '                                                                        title="Tạo tiêu chí con" id="add-time-ivr-2" onclick="addIVR()">\n' +
            '                                                                    <i class="ti-plus"></i>\n' +
            '                                                                </button>\n' +
            '                                                            </div>\n' +
            '                                                        </div>'
        $("#addIVR").append(cols);
        if (sumIVR == 2) {
            if(document.getElementById("delete-time-ivr-1") != null) {
                document.getElementById("delete-time-ivr-1").disabled = false;
            }
            if(document.getElementById("add-time-ivr-1") != null) {
                document.getElementById("add-time-ivr-1").disabled = true;
            }
            if(document.getElementById("delete-time-ivr-2") != null) {
                document.getElementById("delete-time-ivr-2").disabled = false;
            }
            if(document.getElementById("add-time-ivr-2") != null) {
                document.getElementById("add-time-ivr-2").disabled = true;
            }
        }
        $("#start-time-limit-ivr-2").qcTimepicker({
            // additional CSS classes
            classes: 'form-control',
            format: 'HH:mm',
            // step size in ms
            step: 1800,
        });
        $("#start-time-limit-ivr-2-qcTimepicker").val(startTimeLimitIVRView).change();

        $("#end-time-limit-ivr-2").qcTimepicker({
            // additional CSS classes
            classes: 'form-control',
            format: 'HH:mm',
            // step size in ms
            step: 1800,
        });
        $("#end-time-limit-ivr-2-qcTimepicker").val(endTimeLimitIVRView).change();
    }



    //Email
    // const addTimeemail2 = document.getElementById("add-time-email-2");
    // const timeemail2 = document.getElementById("time-email-2");
    // const removeTimeemail2 = document.getElementById("remove-time-email-2");
    // const startTimeLimitemail1 = document.getElementById("start-time-limit-email-1");
    // const endTimeLimitemail1 = document.getElementById("end-time-limit-email-1");
    // const startTimeLimitemail2 = document.getElementById("start-time-limit-email-2");
    // const endTimeLimitemail2 = document.getElementById("end-time-limit-email-2");
    // $("#start-time-limit-email-1").qcTimepicker({
    //     // additional CSS classes
    //     classes: 'form-control',
    //     format: 'HH:mm',
    //     // step size in ms
    //     step: 1800,
    // });
    // $("#start-time-limit-email-1-qcTimepicker").val(startTimeLimitemail1.value).change();
    //
    // $("#end-time-limit-email-1").qcTimepicker({
    //     // additional CSS classes
    //     classes: 'form-control',
    //     format: 'HH:mm',
    //     // step size in ms
    //     step: 1800,
    // });
    // $
    // $("#end-time-limit-email-1-qcTimepicker").val(endTimeLimitemail1.value).change();
    //
    // addTimeemail2.addEventListener("click", function (event) {
    //     event.preventDefault();
    //     $("#start-time-limit-email-2").qcTimepicker({
    //         // additional CSS classes
    //         classes: 'form-control',
    //         format: 'HH:mm',
    //         // step size in ms
    //         step: 1800,
    //     });
    //     $("#start-time-limit-email-2-qcTimepicker").val("00:00").change();
    //
    //     $("#end-time-limit-email-2").qcTimepicker({
    //         // additional CSS classes
    //         classes: 'form-control',
    //         format: 'HH:mm',
    //         // step size in ms
    //         step: 1800,
    //     });
    //     $("#end-time-limit-email-2-qcTimepicker").val("23:30").change();
    //     timeemail2.style.display = "flex";
    //     addTimeemail2.style.display = "none";
    // });
    //
    // removeTimeemail2.addEventListener("click", function (event) {
    //     event.preventDefault();
    //     timeemail2.style.display = "none";
    //     addTimeemail2.style.display = "block";
    //     $('start-time-limit-email-2').qcTimepicker('destroy');
    //     $('end-time-limit-email-2').qcTimepicker('destroy');
    //     startTimeLimitemail2.value = "";
    //     endTimeLimitemail2.value = "";
    //     removeTooltip('alert-time-email-2');
    // });
    // if(startTimeLimitemail2.value != "" && endTimeLimitemail2.value != "") {
    //     $("#time-email-2").show();
    //     $("#start-time-limit-email-2").qcTimepicker({
    //         // additional CSS classes
    //         classes: 'form-control',
    //         format: 'HH:mm',
    //         // step size in ms
    //         step: 1800,
    //     });
    //     $("#start-time-limit-email-2-qcTimepicker").val(startTimeLimitemail2.value).change();
    //
    //     $("#end-time-limit-email-2").qcTimepicker({
    //         // additional CSS classes
    //         classes: 'form-control',
    //         format: 'HH:mm',
    //         // step size in ms
    //         step: 1800,
    //     });
    //
    //     $("#end-time-limit-email-2-qcTimepicker").val(endTimeLimitemail2.value).change();
    //     timeemail2.style.display = "flex";
    //     addTimeemail2.style.display = "none";
    // }
    //
    // //Zalo
    // const addTimezalo2 = document.getElementById("add-time-zalo-2");
    // const timezalo2 = document.getElementById("time-zalo-2");
    // const removeTimezalo2 = document.getElementById("remove-time-zalo-2");
    // const startTimeLimitzalo1 = document.getElementById("start-time-limit-zalo-1");
    // const endTimeLimitzalo1 = document.getElementById("end-time-limit-zalo-1");
    // const startTimeLimitzalo2 = document.getElementById("start-time-limit-zalo-2");
    // const endTimeLimitzalo2 = document.getElementById("end-time-limit-zalo-2");
    // $("#start-time-limit-zalo-1").qcTimepicker({
    //     // additional CSS classes
    //     classes: 'form-control',
    //     format: 'HH:mm',
    //     // step size in ms
    //     step: 1800,
    // });
    // $("#start-time-limit-zalo-1-qcTimepicker").val(startTimeLimitzalo1.value).change();
    //
    // $("#end-time-limit-zalo-1").qcTimepicker({
    //     // additional CSS classes
    //     classes: 'form-control',
    //     format: 'HH:mm',
    //     // step size in ms
    //     step: 1800,
    // });
    //
    // $("#end-time-limit-zalo-1-qcTimepicker").val(endTimeLimitzalo1.value).change();
    //
    // addTimezalo2.addEventListener("click", function (event) {
    //     event.preventDefault();
    //     $("#start-time-limit-zalo-2").qcTimepicker({
    //         // additional CSS classes
    //         classes: 'form-control',
    //         format: 'HH:mm',
    //         // step size in ms
    //         step: 1800,
    //     });
    //     $("#start-time-limit-zalo-2-qcTimepicker").val("00:00").change();
    //
    //     $("#end-time-limit-zalo-2").qcTimepicker({
    //         // additional CSS classes
    //         classes: 'form-control',
    //         format: 'HH:mm',
    //         // step size in ms
    //         step: 1800,
    //     });
    //
    //     $("#end-time-limit-zalo-2-qcTimepicker").val("23:30").change();
    //     timezalo2.style.display = "flex";
    //     addTimezalo2.style.display = "none";
    // });
    //
    // removeTimezalo2.addEventListener("click", function (event) {
    //     event.preventDefault();
    //     timezalo2.style.display = "none";
    //     addTimezalo2.style.display = "block";
    //     $('start-time-limit-zalo-2').qcTimepicker('destroy');
    //     $('end-time-limit-zalo-2').qcTimepicker('destroy');
    //     startTimeLimitzalo2.value = "";
    //     endTimeLimitzalo2.value = "";
    //     removeTooltip('alert-time-zalo-2');
    // });
    // if(startTimeLimitzalo2.value != "" && endTimeLimitzalo2.value != "") {
    //     $("#time-zalo-2").show();
    //     $("#start-time-limit-zalo-2").qcTimepicker({
    //         // additional CSS classes
    //         classes: 'form-control',
    //         format: 'HH:mm',
    //         // step size in ms
    //         step: 1800,
    //     });
    //     $("#start-time-limit-zalo-2-qcTimepicker").val(startTimeLimitzalo2.value).change();
    //
    //     $("#end-time-limit-zalo-2").qcTimepicker({
    //         // additional CSS classes
    //         classes: 'form-control',
    //         format: 'HH:mm',
    //         // step size in ms
    //         step: 1800,
    //     });
    //
    //     $("#end-time-limit-zalo-2-qcTimepicker").val(endTimeLimitzalo2.value).change();
    //     timezalo2.style.display = "flex";
    //     addTimezalo2.style.display = "none";
    // }
    //
    // //Digilife
    // const addTimedigilife2 = document.getElementById("add-time-digilife-2");
    // const timedigilife2 = document.getElementById("time-digilife-2");
    // const removeTimedigilife2 = document.getElementById("remove-time-digilife-2");
    // const startTimeLimitdigilife1 = document.getElementById("start-time-limit-digilife-1");
    // const endTimeLimitdigilife1 = document.getElementById("end-time-limit-digilife-1");
    // const startTimeLimitdigilife2 = document.getElementById("start-time-limit-digilife-2");
    // const endTimeLimitdigilife2 = document.getElementById("end-time-limit-digilife-2");
    // $("#start-time-limit-digilife-1").qcTimepicker({
    //     // additional CSS classes
    //     classes: 'form-control',
    //     format: 'HH:mm',
    //     // step size in ms
    //     step: 1800,
    // });
    // $("#start-time-limit-digilife-1-qcTimepicker").val(startTimeLimitdigilife1.value).change();
    //
    // $("#end-time-limit-digilife-1").qcTimepicker({
    //     // additional CSS classes
    //     classes: 'form-control',
    //     format: 'HH:mm',
    //     // step size in ms
    //     step: 1800,
    // });
    //
    // $("#end-time-limit-digilife-1-qcTimepicker").val(endTimeLimitdigilife1.value).change();
    //
    // addTimedigilife2.addEventListener("click", function (event) {
    //     event.preventDefault();
    //     $("#start-time-limit-digilife-2").qcTimepicker({
    //         // additional CSS classes
    //         classes: 'form-control',
    //         format: 'HH:mm',
    //         // step size in ms
    //         step: 1800,
    //     });
    //     $("#start-time-limit-digilife-2-qcTimepicker").val("00:00").change();
    //
    //     $("#end-time-limit-digilife-2").qcTimepicker({
    //         // additional CSS classes
    //         classes: 'form-control',
    //         format: 'HH:mm',
    //         // step size in ms
    //         step: 1800,
    //     });
    //
    //     $("#end-time-limit-digilife-2-qcTimepicker").val("23:30").change();
    //     timedigilife2.style.display = "flex";
    //     addTimedigilife2.style.display = "none";
    // });
    //
    // removeTimedigilife2.addEventListener("click", function (event) {
    //     event.preventDefault();
    //     timedigilife2.style.display = "none";
    //     addTimedigilife2.style.display = "block";
    //     $('start-time-limit-digilife-2').qcTimepicker('destroy');
    //     $('end-time-limit-digilife-2').qcTimepicker('destroy');
    //     startTimeLimitdigilife2.value = "";
    //     endTimeLimitdigilife2.value = "";
    //     removeTooltip('alert-time-digilife-2');
    // });
    // console.log("startTimeLimitdigilife2.value ",startTimeLimitdigilife2.value )
    // if(startTimeLimitdigilife2.value != "" && endTimeLimitdigilife2.value != "") {
    //     $("#time-digilife-2").show();
    //     $("#start-time-limit-digilife-2").qcTimepicker({
    //         // additional CSS classes
    //         classes: 'form-control',
    //         format: 'HH:mm',
    //         // step size in ms
    //         step: 1800,
    //     });
    //     $("#start-time-limit-digilife-2-qcTimepicker").val(startTimeLimitdigilife2.value).change();
    //
    //     $("#end-time-limit-digilife-2").qcTimepicker({
    //         // additional CSS classes
    //         classes: 'form-control',
    //         format: 'HH:mm',
    //         // step size in ms
    //         step: 1800,
    //     });
    //
    //     $("#end-time-limit-digilife-2-qcTimepicker").val(endTimeLimitdigilife2.value).change();
    //     timedigilife2.style.display = "flex";
    //     addTimedigilife2.style.display = "none";
    // }
})

function validate() {

    let check = true;
    const limitMessagesPerDaySMS = $("#limitMessagesPerDaySMS").val();
    const limitMessagesPerDayUSSD = $("#limitMessagesPerDayUSSD").val();
    const limitMessagesPerDayIVR = $("#limitMessagesPerDayIVR").val();
    const limitMessagesPerDay = $("#limitMessagesPerDay").val();
    const limitMessagesPerDayZalo = $("#limitMessagesPerDayZalo").val();
    const limitMessagesPerDayDigiLife = $("#limitMessagesPerDayDigiLife").val();
    const limitMessagesPerDayEmail = $("#limitMessagesPerDayEmail").val();
    if (limitMessagesPerDaySMS != "") {
        if (!checkIsPositive(limitMessagesPerDaySMS)) {
            showTooltip("Chỉ nhập Số nguyên dương (>0).", "limitMessagesPerDaySMS")
            check = false;
        }
    }

    if (limitMessagesPerDayUSSD != "") {
        if (!checkIsPositive(limitMessagesPerDayUSSD)) {
            showTooltip("Chỉ nhập Số nguyên dương (>0).", "limitMessagesPerDayUSSD")
            check = false;
        }
    }

    if (limitMessagesPerDayIVR != "") {
        if (!checkIsPositive(limitMessagesPerDayIVR)) {
            showTooltip("Chỉ nhập Số nguyên dương (>0).", "limitMessagesPerDayIVR")
            check = false;
        }
    }

    if (limitMessagesPerDay != "") {
        if (!checkIsPositive(limitMessagesPerDay)) {
            showTooltip("Chỉ nhập Số nguyên dương (>0).", "limitMessagesPerDay")
            check = false;
        }
    }

    // if (limitMessagesPerDayZalo != "") {
    //     if (!checkIsPositive(limitMessagesPerDayZalo)) {
    //         showTooltip("Chỉ nhập Số nguyên dương (>0).", "limitMessagesPerDayZalo")
    //         check = false;
    //     }
    // }
    //
    // if (limitMessagesPerDayDigiLife != "") {
    //     if (!checkIsPositive(limitMessagesPerDayDigiLife)) {
    //         showTooltip("Chỉ nhập Số nguyên dương (>0).", "limitMessagesPerDayDigiLife")
    //         check = false;
    //     }
    // }
    //
    // if (limitMessagesPerDayEmail != "") {
    //     if (!checkIsPositive(limitMessagesPerDayEmail)) {
    //         showTooltip("Chỉ nhập Số nguyên dương (>0).", "limitMessagesPerDayEmail")
    //         check = false;
    //     }
    // }

    //sms
    let startTimeLimitSms1 = $("#start-time-limit-sms-1").val();
    let endTimeLimitSms1 = $("#end-time-limit-sms-1").val();
    let startTimeLimitSms2 = $("#start-time-limit-sms-2").val();
    let endTimeLimitSms2 = $("#end-time-limit-sms-2").val();
    //ussd
    const startTimeLimitUssd1 = $("#start-time-limit-ussd-1").val();
    const endTimeLimitUssd1 = $("#end-time-limit-ussd-1").val();
    const startTimeLimitUssd2 = $("#start-time-limit-ussd-2").val();
    const endTimeLimitUssd2 = $("#end-time-limit-ussd-2").val();
    //ivr
    const startTimeLimitIvr1 = $("#start-time-limit-ivr-1").val();
    const endTimeLimitIvr1 = $("#end-time-limit-ivr-1").val();
    const startTimeLimitIvr2 = $("#start-time-limit-ivr-2").val();
    const endTimeLimitIvr2 = $("#end-time-limit-ivr-2").val();
    //email
    const startTimeLimitEmail1 = $("#start-time-limit-email-1").val();
    const endTimeLimitEmail1 = $("#end-time-limit-email-1").val();
    const startTimeLimitEmail2 = $("#start-time-limit-email-2").val();
    const endTimeLimitEmail2 = $("#end-time-limit-email-2").val();
    //zalo
    const startTimeLimitZalo1 = $("#start-time-limit-zalo-1").val();
    const endTimeLimitZalo1 = $("#end-time-limit-zalo-1").val();
    const startTimeLimitZalo2 = $("#start-time-limit-zalo-2").val();
    const endTimeLimitZalo2 = $("#end-time-limit-zalo-2").val();
    //digi
    const startTimeLimitDigilife1 = $("#start-time-limit-digilife-1").val();
    const endTimeLimitDigilife1 = $("#end-time-limit-digilife-1").val();
    const startTimeLimitDigilife2 = $("#start-time-limit-digilife-2").val();
    const endTimeLimitDigilife2 = $("#end-time-limit-digilife-2").val();

    if (startTimeLimitSms1 >= endTimeLimitSms1) {
        let messsage = "Thời điểm kết thúc của khung giờ gửi tin cần lớn hơn thời điểm bắt đầu.";
        showTooltip(messsage, 'alert-time-sms-1');
        check = false;
    } else {
        removeTooltip('alert-time-sms-1');
    }

    /* Neu them khung gio thứ 2 */
    if (startTimeLimitSms2 != null && endTimeLimitSms2 != null) {
        if (startTimeLimitSms2 >= endTimeLimitSms2) {
            let messsage = "Thời điểm kết thúc của khung giờ gửi tin cần lớn hơn thời điểm bắt đầu.";
            showTooltip(messsage, 'alert-time-sms-2');
            check = false;

        } else if ((startTimeLimitSms1 <= startTimeLimitSms2 && startTimeLimitSms2 <= endTimeLimitSms1) || (startTimeLimitSms1 <= endTimeLimitSms2 && endTimeLimitSms2 <= endTimeLimitSms1)
            || (startTimeLimitSms1 >= startTimeLimitSms2 && endTimeLimitSms1 <= endTimeLimitSms2)) {
            let messsage = "Các khung giờ gửi tin không được đè lên nhau.";
            showTooltip(messsage, 'alert-time-sms-2');
            check = false;
        } else {
            removeTooltip('alert-time-sms-2');
        }
    }

    //ussd
    if (startTimeLimitUssd1 >= endTimeLimitUssd1) {
        let messsage = "Thời điểm kết thúc của khung giờ gửi tin cần lớn hơn thời điểm bắt đầu.";
        showTooltip(messsage, 'alert-time-ussd-1');
        check = false;
    } else {
        removeTooltip('alert-time-ussd-1');
    }

    /* Neu them khung gio thứ 2 */
    if (startTimeLimitUssd2 != null && endTimeLimitUssd2 != null) {
        if (startTimeLimitUssd2 >= endTimeLimitUssd2) {
            let messsage = "Thời điểm kết thúc của khung giờ gửi tin cần lớn hơn thời điểm bắt đầu.";
            showTooltip(messsage, 'alert-time-ussd-2');
            check = false;

        } else if ((startTimeLimitUssd1 <= startTimeLimitUssd2 && startTimeLimitUssd2 <= endTimeLimitUssd1) || (startTimeLimitUssd1 <= endTimeLimitUssd2 && endTimeLimitUssd2 <= endTimeLimitUssd1)
            || (startTimeLimitUssd1 >= startTimeLimitUssd2 && endTimeLimitUssd1 <= endTimeLimitUssd2)) {
            let messsage = "Các khung giờ gửi tin không được đè lên nhau.";
            showTooltip(messsage, 'alert-time-ussd-2');
            check = false;
        } else {
            removeTooltip('alert-time-ussd-2');
        }
    }

    //ivr
    if (startTimeLimitIvr1 >= endTimeLimitIvr1) {
        let messsage = "Thời điểm kết thúc của khung giờ gửi tin cần lớn hơn thời điểm bắt đầu.";
        showTooltip(messsage, 'alert-time-ivr-1');
        check = false;
    } else {
        removeTooltip('alert-time-ivr-1');
    }

    /* Neu them khung gio thứ 2 */
    if (startTimeLimitIvr2 != null && endTimeLimitIvr2 != null) {
        if (startTimeLimitIvr2 >= endTimeLimitIvr2) {
            let messsage = "Thời điểm kết thúc của khung giờ gửi tin cần lớn hơn thời điểm bắt đầu.";
            showTooltip(messsage, 'alert-time-ivr-2');
            check = false;
        } else if ((startTimeLimitIvr1 <= startTimeLimitIvr2 && startTimeLimitIvr2 <= endTimeLimitIvr1) || (startTimeLimitIvr1 <= endTimeLimitIvr2 && endTimeLimitIvr2 <= endTimeLimitIvr1)
            || (startTimeLimitIvr1 >= startTimeLimitIvr2 && endTimeLimitIvr1 <= endTimeLimitIvr2)) {
            let messsage = "Các khung giờ gửi tin không được đè lên nhau.";
            showTooltip(messsage, 'alert-time-ivr-2');
            check = false;
        } else {
            removeTooltip('alert-time-ivr-2');
        }
    }

    //email
    if (startTimeLimitEmail1 >= endTimeLimitEmail1) {
        let messsage = "Thời điểm kết thúc của khung giờ gửi tin cần lớn hơn thời điểm bắt đầu.";
        showTooltip(messsage, 'alert-time-email-1');
        check = false;
    } else {
        removeTooltip('alert-time-email-1');
    }

    /* Neu them khung gio thứ 2 */
    if ($('#time-email-2:visible').length != 0) {
        if (startTimeLimitEmail2 >= endTimeLimitEmail2) {
            let messsage = "Thời điểm kết thúc của khung giờ gửi tin cần lớn hơn thời điểm bắt đầu.";
            showTooltip(messsage, 'alert-time-email-2');
            check = false;
        } else if ((startTimeLimitEmail1 <= startTimeLimitEmail2 && startTimeLimitEmail2 <= endTimeLimitEmail1) || (startTimeLimitEmail1 <= endTimeLimitEmail2 && endTimeLimitEmail2 <= endTimeLimitEmail1)
            || (startTimeLimitEmail1 >= startTimeLimitEmail2 && endTimeLimitEmail1 <= endTimeLimitEmail2)) {
            let messsage = "Các khung giờ gửi tin không được đè lên nhau.";
            showTooltip(messsage, 'alert-time-email-2');
            check = false;
        } else {
            removeTooltip('alert-time-email-2');
        }
    }

    //zalo
    if (startTimeLimitZalo1 >= endTimeLimitZalo1) {
        let messsage = "Thời điểm kết thúc của khung giờ gửi tin cần lớn hơn thời điểm bắt đầu.";
        showTooltip(messsage, 'alert-time-zalo-1');
        check = false;
    } else {
        removeTooltip('alert-time-zalo-1');
    }

    /* Neu them khung gio thứ 2 */
    if ($('#time-zalo-2:visible').length != 0) {
        if (startTimeLimitZalo2 >= endTimeLimitZalo2) {
            let messsage = "Thời điểm kết thúc của khung giờ gửi tin cần lớn hơn thời điểm bắt đầu.";
            showTooltip(messsage, 'alert-time-zalo-2');
            check = false;
        } else if ((startTimeLimitZalo1 <= startTimeLimitZalo2 && startTimeLimitZalo2 <= endTimeLimitZalo1) || (startTimeLimitZalo1 <= endTimeLimitZalo2 && endTimeLimitZalo2 <= endTimeLimitZalo1)
            || (startTimeLimitZalo1 >= startTimeLimitZalo2 && endTimeLimitZalo1 <= endTimeLimitZalo2)) {
            let messsage = "Các khung giờ gửi tin không được đè lên nhau.";
            showTooltip(messsage, 'alert-time-zalo-2');
            check = false;
        } else {
            removeTooltip('alert-time-zalo-2');
        }
    }

    //digi
    if (startTimeLimitDigilife1 >= endTimeLimitDigilife1) {
        let messsage = "Thời điểm kết thúc của khung giờ gửi tin cần lớn hơn thời điểm bắt đầu.";
        showTooltip(messsage, 'alert-time-digilife-1');
        check = false;
    } else {
        removeTooltip('alert-time-digilife-1');
    }

    /* Neu them khung gio thứ 2 */
    if ($('#time-zalo-2:visible').length != 0) {
        if (startTimeLimitDigilife2 >= endTimeLimitDigilife2) {
            let messsage = "Thời điểm kết thúc của khung giờ gửi tin cần lớn hơn thời điểm bắt đầu.";
            showTooltip(messsage, 'alert-time-digilife-2');
            check = false;
        } else if ((startTimeLimitDigilife1 <= startTimeLimitDigilife2 && startTimeLimitDigilife2 <= endTimeLimitDigilife1) || (startTimeLimitDigilife1 <= endTimeLimitDigilife2 && endTimeLimitDigilife2 <= endTimeLimitDigilife1)
            || (startTimeLimitDigilife1 >= startTimeLimitDigilife2 && endTimeLimitDigilife1 <= endTimeLimitDigilife2)) {
            let messsage = "Các khung giờ gửi tin không được đè lên nhau.";
            showTooltip(messsage, 'alert-time-digilife-2');
            check = false;
        } else {
            removeTooltip('alert-time-digilife-2');
        }
    }

    if (check) {
        return true;
    } else return false;
}

function checkIsPositive(str) {
    const n = Math.floor(Number(str));
    return n !== Infinity && String(n) === str && n > 0;
}

function cancelPolicy(){
    event.preventDefault();
    let url = "/config-policy"
    cancel(url, "Bạn muốn hủy thiết lập chính sách chung?")
}

