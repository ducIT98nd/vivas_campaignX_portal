
let mapDataSubGroup = new Map();
$(document).ready(function () {

    /* init progress bar and step */
    const nextBtnStep1 = document.querySelector("#step1-next");
    const prevBtnStep2 = document.querySelector("#step2-prev");
    const nextBtnStep2 = document.querySelector("#step2-next");
    const prevBtnStep3 = document.querySelector("#step3-prev");
    const nextBtnStep3 = document.querySelector("#step3-next");
    const prevBtnStep4 = document.querySelector("#step4-prev-one-message");
    const prevBtnStep4MultiMessage = document.querySelector("#step4-prev-multi-message");
    const nextBtnStep4MultiMessage = document.querySelector("#step4-next-multi-message");
    const nextBtnStep4 = document.querySelector("#step4-next-one-message");
    const nextBtnStep5 = document.querySelector("#step5-next");
    const prevBtnStep5 = document.querySelector("#step5-prev");
    const prevBtnStep6 = document.querySelector("#step6-prev");
    const progressBar = document.querySelectorAll(".step-bar");
    const nodeList = document.querySelectorAll(".step-component");
    var stepComponent = Array.from(nodeList);
    let current = 0;

    nextBtnStep1.addEventListener("click", function (event) {
        event.preventDefault();
        if (validateStep1()){
            progressBar[current + 1].classList.add("active");
            stepComponent[current + 1].style.display = "";
            stepComponent[current].style.display = "none";
            current += 1;
            //hiển thị lên đầu trang
            document.documentElement.scrollTop = 0;
        }
    });

    nextBtnStep2.addEventListener("click", function (event) {
        event.preventDefault();
        if(validateStep2()) {
            progressBar[current + 1].classList.add("active");
            stepComponent[current + 1].style.display = "";
            stepComponent[current].style.display = "none";
            current += 1;
            //hiển thị lên đầu trang
            document.documentElement.scrollTop = 0;
        }
    });

    nextBtnStep3.addEventListener("click", function (event) {
        event.preventDefault();
        if(validateStep3()) {
            let arrayChange = [];
            if(mapDataSubGroup.size == 0 || !checkChangeData()){
                // đổi step 3 html nếu có phân nhỏ nhóm ở step 2
                let isSubgroups = $("#sub-target-group-radio-1").is(":checked");
                if (isSubgroups) {
                    stepComponent[current + 1] = document.querySelector("#step-component-3-message-subgroups");
                    let mapSubGroupNotChange = checkChangeInfoSubGroup();
                    genMultiMessageSubGroups();
                    loadListSendingAccountSubGroup();
                    loadListPackageSubGroup();
                    for (const [key, value] of mapSubGroupNotChange) {
                        if(value.length > 0){
                            dataAssignmentSubMessage(key, value);
                            if(mapDataSubGroup.get("package-group") == $("#package-group").val()) $("#checkMessageContent_" + key).show();
                        }
                    }
                }else {
                    stepComponent[current + 1] = document.querySelector("#step-component-3-message");
                    loadListSendingAccount();
                    loadListPackage();
                    $("#message-content_one").val("");
                }
            }else {
                let isSubgroups = $("#sub-target-group-radio-1").is(":checked");
                if (isSubgroups) {
                    stepComponent[current + 1] = document.querySelector("#step-component-3-message-subgroups");
                }else {
                    stepComponent[current + 1] = document.querySelector("#step-component-3-message");
                }
            }


            mapDataSubGroup.set("sub-target-group-radio", $('input[name="sub-target-group-radio"]:checked').val());
            let isDuplicateMsisdn = $("#is-duplicate-msisdn").is(":checked");
            mapDataSubGroup.set("is-duplicate-msisdn", isDuplicateMsisdn);
            mapDataSubGroup.set("package-group", $("#package-group").val());
            for (const [key, value] of arrayTreeSubTarget) {
                mapDataSubGroup.set("subTargetGroupName_" + key, $("#subTargetGroupName_"+ key).val());
                mapDataSubGroup.set("subTargetGroupPriority_" + key, $("#subTargetGroupPriority_"+ key).val());
            }

            progressBar[current + 1].classList.add("active");
            stepComponent[current + 1].style.display = "block";
            stepComponent[current].style.display = "none";
            current += 1;
            //hiển thị lên đầu trang
            document.documentElement.scrollTop = 0;
        }
    });

    nextBtnStep4.addEventListener("click", function (event) {
        event.preventDefault();
        let check;
        check = validateStep4NoSubgroups();
        if (check){
            progressBar[current + 1].classList.add("active");
            stepComponent[current + 1].style.display = "block";
            stepComponent[current].style.display = "none";
            current += 1;
            //hiển thị lên đầu trang
            document.documentElement.scrollTop = 0;
        }
    });

    nextBtnStep4MultiMessage.addEventListener("click", function (event) {
        event.preventDefault();
        let check;
        check = validateStep4Subgroups();
        if (check){
            progressBar[current + 1].classList.add("active");
            stepComponent[current + 1].style.display = "block";
            stepComponent[current].style.display = "none";
            current += 1;
            //hiển thị lên đầu trang
            document.documentElement.scrollTop = 0;
        } else {
            Swal.fire({
                position: 'top',
                type: 'error',
                title: 'Thông báo',
                text: "Chưa cấu hình thông điệp. Vui lòng thực hiện cấu hình thông điệp để tiếp tục.",
                showConfirmButton: false,
                timer: 3000
            })
        }
    });

    nextBtnStep5.addEventListener("click", function (event) {
        event.preventDefault();
        if (validateStep5()){
            progressBar[current + 1].classList.add("active");
            stepComponent[current + 1].style.display = "block";
            stepComponent[current].style.display = "none";
            current += 1;
            initDataOverview();
            //hiển thị lên đầu trang
            document.documentElement.scrollTop = 0;
        }
    });

    prevBtnStep2.addEventListener("click", function (event) {
        event.preventDefault();
        progressBar[current].classList.remove("active");
        stepComponent[current].style.display = "none";
        stepComponent[current - 1].style.display = "block";
        current -= 1;
        //hiển thị lên đầu trang
        document.documentElement.scrollTop = 0;
    });

    prevBtnStep3.addEventListener("click", function (event) {
        event.preventDefault();
        progressBar[current].classList.remove("active");
        stepComponent[current].style.display = "none";
        stepComponent[current - 1].style.display = "block";
        current -= 1;
        //hiển thị lên đầu trang
        document.documentElement.scrollTop = 0;
    });

    prevBtnStep4.addEventListener("click", function (event) {
        event.preventDefault();
        progressBar[current].classList.remove("active");
        stepComponent[current].style.display = "none";
        stepComponent[current - 1].style.display = "";
        current -= 1;
        //hiển thị lên đầu trang
        document.documentElement.scrollTop = 0;
    });

    prevBtnStep4MultiMessage.addEventListener("click", function (event) {
        event.preventDefault();
        progressBar[current].classList.remove("active");
        stepComponent[current].style.display = "none";
        stepComponent[current - 1].style.display = "";
        current -= 1;
        //hiển thị lên đầu trang
        document.documentElement.scrollTop = 0;
    });

    prevBtnStep5.addEventListener("click", function (event) {
        event.preventDefault();
        progressBar[current].classList.remove("active");
        stepComponent[current].style.display = "none";
        stepComponent[current - 1].style.display = "block";
        current -= 1;
        //hiển thị lên đầu trang
        document.documentElement.scrollTop = 0;
    });

    prevBtnStep6.addEventListener("click", function (event) {
        event.preventDefault();
        progressBar[current].classList.remove("active");
        stepComponent[current].style.display = "none";
        stepComponent[current - 1].style.display = "block";
        current -= 1;
        //hiển thị lên đầu trang
        document.documentElement.scrollTop = 0;
    });
    /* end init progress bar and step */

    /* event bam nut huy NOK */
    $(".cancel-create-campagin").on("click", function (event){
        event.preventDefault();
        let url = "/campaignManager"
        cancel(url, "Bạn muốn hủy thêm mới chiến dịch?")
    })
    /* end event bam nut huy */

    /* mục tiêu chiến dịch - taginput  */
    fetch(contextPath + "get-campaign-target-tag")
        .then((response) => {
            return response.json();
        }).then((response) => {
            $('#campaign-target').tagator({
                prefix: 'tagator_',           // CSS class prefix
                //height: 'auto',               // auto or element
                useDimmer: false,             // dims the screen when result list is visible
                autocomplete: response.data             // this is an array of autocomplete options
            });
            let tagAreaBaseHeight  = $("#campaign-description").height();
            $("#tagator_campaign-target").height(tagAreaBaseHeight);
            $("#tagator_campaign-target").addClass("form-control");
            $(".tagator_input").attr('maxlength','30'); // gioi han 1 tag 30 ky tu
            $(".tagator_input").bind('keypress', function (e) {
                let size = $("#campaign-target").val().split(",").length;
                if (size >= 10){
                    e.stopPropagation();
                    e.preventDefault();
                }
            });

            document.querySelector('.tagator_tags').addEventListener('DOMNodeInserted', function (event) {
                if( event.target.parentNode.classList.contains('tagator_tags')) {
                    let height = document.querySelector('.tagator_tags').offsetHeight;
                    if (height > tagAreaBaseHeight) {
                        $("#tagator_campaign-target").height(height + 6);
                    }
                };
            }, false );
        }).catch(function(error) {
            console.log("Error load list target tag " + error);
        });

    const today = new Date();

    /* step 1 Hiệu lực chiến dịch */
    $("#start-date").datepicker({format: 'dd/mm/yyyy', todayHighlight: true});
    $('#start-date').datepicker("setDate", today);
    $("#end-date").datepicker({format: 'dd/mm/yyyy', todayHighlight: true});
    $('#end-date').datepicker("setDate", new Date(today.getTime() + 86400000));

    /* step 1 tần suất gửi tin */
    $("#specific-date-value").datepicker({
        format: 'dd/mm/yyyy',
        todayHighlight: true,
        multidateSeparator: ", ",
        multidate: true
    });
    $('#specific-date-value').datepicker("setDate", today);

    /* step 1 Khung giờ gửi tin */
    const addTimeRange2 = document.getElementById("add-time-range-2");
    const timeRange2 = document.getElementById("time-range-2");
    const removeTimeRange2 = document.getElementById("remove-time-range-2");
    const timeRange1Start = document.getElementById("time-range-1-start");
    const timeRange1End = document.getElementById("time-range-1-end");
    const timeRange2Start = document.getElementById("time-range-2-start");
    const timeRange2End = document.getElementById("time-range-2-end");

    $("#time-range-1-start").qcTimepicker({
        // additional CSS classes
        classes: 'form-control',
        format: 'HH:mm',
        // step size in ms
        step: 1800,
    });
    $("#time-range-1-start-qcTimepicker").val("00:00").change();

    $("#time-range-1-end").qcTimepicker({
        // additional CSS classes
        classes: 'form-control',
        format: 'HH:mm',
        // step size in ms
        step: 1800,
    });
    $("#time-range-1-end-qcTimepicker").val("23:30").change();


    addTimeRange2.addEventListener("click", function (event) {
        event.preventDefault();
        $("#time-range-2-start").qcTimepicker({
            // additional CSS classes
            classes: 'form-control',
            format: 'HH:mm',
            // step size in ms
            step: 1800,
        });
        $("#time-range-2-start-qcTimepicker").val("00:00").change();

        $("#time-range-2-end").qcTimepicker({
            // additional CSS classes
            classes: 'form-control',
            format: 'HH:mm',
            // step size in ms
            step: 1800,
        });
        $("#time-range-2-end-qcTimepicker").val("23:30").change();
        timeRange2.style.display = "flex";
        addTimeRange2.style.display = "none";
    });

    removeTimeRange2.addEventListener("click", function (event) {
        event.preventDefault();
        timeRange2.style.display = "none";
        addTimeRange2.style.display = "block";
        $('time-range-2-start').qcTimepicker('destroy');
        $('time-range-2-end').qcTimepicker('destroy');
        timeRange2Start.value = "";
        timeRange2End.value = "";
        removeTooltip('alert-time-range-2');
    });

    var today1 = new Date();
    var date = today1.getDate() + '-' + (today1.getMonth() + 1) + '-' + today1.getFullYear();
    var time = today1.getHours() + ":" + today1.getMinutes() + ":" + today1.getSeconds();
    var dateTime = date + ' ' + time;

    $(".label-ratio").attr("title", "Cập nhật cuối: "+ dateTime);
    $(".label-groupSize").attr("title", "Cập nhật cuối: "+ dateTime);

})


