function changeChannel() {
    var channel = $("#channel").val();
    if (channel == 3) {
        $("#listMsisdn").show();
        $("#criteria").hide();
    }

    if (channel == 1) {
        $("#listMsisdn").hide();
        $("#criteria").show();
    }

    if(channel == 4) {
        $("#listMsisdn").show();
        $("#criteria").show();
    }
}

function getFileExtension(filename) {
    var ext = /^.+\.([^.]+)$/.exec(filename);
    return ext == null ? "" : ext[1];
}
$(document).ready(function(){
    callHoverEvent();
});



function validate() {
    var name = $("#nameTargetGroup").val();
    var check = true;
    if (name.length == null || name.length <= 0) {
        showTooltip("Tên nhóm đối tượng không được để trống", 'nameTargetGroup');
        check = false;
    }else {
        removeTooltip("nameTargetGroup");
        var check1 = checkTargetGroupName(name);
        console.log("check", check1);
        if(checkTargetGroupName(name)){
            showTooltip('Tên nhóm đối tượng đã tồn tại', 'nameTargetGroup');
            check = false;
        }else {
            removeTooltip("nameTargetGroup");
        }
    }




    var channel = $("#channel").val();
    if(channel == 3 || channel == 4) {
        if(document.getElementById("dataTargetGroup").files.length > 0){
            var fileDataTargetGroupSize = document.getElementById('dataTargetGroup').files.item(0).size;
            console.log("fileDataWhiteListSize: "  +fileDataTargetGroupSize );

            var fileNameDataTargetGroup = document.getElementById('dataTargetGroup').files.item(0).name;
            var fileExtensionDataTargetGroup = getFileExtension(fileNameDataTargetGroup);

            console.log("fileExtensionDataTargetGroup : " + fileExtensionDataTargetGroup);
            if(fileExtensionDataTargetGroup != "csv"){
                showTooltip("Định dạng file không hợp lệ.", 'dataTargetGroup');
                check = false;
            }else {
                removeTooltip("dataTargetGroup");
                if(fileDataTargetGroupSize > 60 * 1024 * 1024){
                    showTooltip("Dung lượng file không được vượt quá 60MB.", 'dataTargetGroup');
                    check = false;
                }else {
                    removeTooltip("dataTargetGroup");
                }
            }
        } else if(document.getElementById("dataTargetGroup").files.length <= 0){
            showTooltip("Danh sách thuê bao không được để trống.", 'dataTargetGroup');
            check = false;
        } else {
            removeTooltip("dataTargetGroup");
        }
    }

    // if(submitCriteriaSetup() == false) {
    //     check = false
    // } else check = true;

    if (check == false) {
        return false
    } else{
        return true;
    }

}


function checkTargetGroupName(name) {
    var value = true;
    $.ajax({
        type: 'GET',
        url: '/TargetGroupController/checkTargetGroupName?name=' + name,
        traditional: true,
        async: false,
        dataType: 'JSON',
        success: function (response) {
            console.log("response ", response)
            value = response.data;

        },
        error: function (e) {
            console.log(e);
        },
    });
    if (value == false) return false;
    else return true;

}

function createNewTargetGroup() {
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

    var channel = $("#channel").val();
    let check = false;
    let checksetup = false;
    try{
        check = validate();
        if (channel == 1 || channel == 4) {
            checksetup = submitCriteriaSetup();
        }
        if (channel == 1 || channel == 4) {
            if (check && checksetup) {
                return  true;
            } else {
                $.unblockUI();
                return false;
            }
        } else if (channel == 3) {
            if (check) {
                return true;
            } else {
                $.unblockUI();
                return false;
            }
        }
    } catch(e){
        return false
    }
}
var today = new Date();
var date = format_two_digits(today.getDate()) + '/' + format_two_digits((today.getMonth() + 1)) + '/' + today.getFullYear();
var time = format_two_digits(today.getHours()) + ":" + format_two_digits(today.getMinutes()) + ":" + format_two_digits(today.getSeconds());
var dateTime = date + ' ' + time;
var dateHover = dateTime;

function queryCountMsisdn() {

    Swal.fire({
        position: 'top',
        type: 'info',
        title: 'Thông báo',
        text: "Hệ thống đang cập nhật số lượng thuê bao của nhóm đối tượng. Vui lòng chờ trong ít phút",
        showConfirmButton: false,
        timer: 5000
    })

    var csrfToken = $("#csrf-input").val();
    var channel = $("#channel").val();
    if(channel == 1) {

        submitCriteriaSetup();
        var data = $("#jsonData").val();

        var formData = new FormData();
        formData.append("dataTargetGroup", data);

        $.ajax({
            type: "POST",
            enctype : 'multipart/form-data',
            url: '/TargetGroupController/countMSISDNFromTargetGroupByJSONCriteria',
            headers: {
                'X-CSRF-Token': csrfToken
            },
            data: formData,
            processData: false,
            contentType: false,
            timeout: 120000,
            async: true,
            success: function (response) {
                let jsonData = JSON.parse(response);
                let dta = jsonData.data;
                $("#groupSize").text(dta.count);
                $("#groupSizeSave").val(dta.count);

                $("#wholeNetwork").text(dta.ratio +'%');
                $("#wholeNetworkSave").val(dta.ratio);
            },
            error: function (response) {
                console.log("error:" +response.data);
            }

        })
    }
    else if(channel == 3) {

        let data = document.getElementById('dataTargetGroup').files[0];

        var formData = new FormData();
        formData.append("dataTargetGroup", data);
        $.ajax({
            type: "POST",
            enctype : 'multipart/form-data',
            url: '/TargetGroupController/countMSISDNFromTargetGroupByFile',
            headers: {
                'X-CSRF-Token': csrfToken
            },
            data: formData,
            processData: false,
            contentType: false,
            timeout: 120000,
            async: true,
            success: function (response) {
                let jsonData = JSON.parse(response);
                let dta = jsonData.data;
                $("#groupSize").text(dta.count);
                $("#groupSizeSave").val(dta.count);

                $("#wholeNetwork").text(dta.ratio +'%');
                $("#wholeNetworkSave").val(dta.ratio);
            },
            error: function (response) {
            }

        })

    }

    else if (channel == 4) {
        submitCriteriaSetup();
        var dataJSON = $("#jsonData").val();
        let data = document.getElementById('dataTargetGroup').files[0];
        var formData = new FormData();

        formData.append("dataTargetGroup", data);
        formData.append("jsonData", dataJSON);

        $.ajax({
            type: "POST",
            enctype : 'multipart/form-data',
            url: '/TargetGroupController/countMSISDNFromTargetGroupByFileJOINJSONCriteria',
            headers: {
                'X-CSRF-Token': csrfToken
            },
            data: formData,
            processData: false,
            contentType: false,
            timeout: 120000,
            async: true,
            success: function (response) {
                let jsonData = JSON.parse(response);
                let dta = jsonData.data;
                $("#groupSize").text(dta.count);
                $("#groupSizeSave").val(dta.count);

                $("#wholeNetwork").text(dta.ratio +'%');
                $("#wholeNetworkSave").val(dta.ratio);
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
    dateHover = dateTime;
}

function callHoverEvent() {
    $("#groupSizeHover").attr("title", "Cập nhật cuối: "+ dateHover);
    $("#wholeNetworkHover").attr("title","Cập nhật cuối: "+ dateHover);
}