function validateStep1 (){

    let check = true;

    /* tên chiến dịch */
    let name = $("#campaign-name").val().trim();;
    let duplicateName = checkDuplicateCampaignName(name);
    if(name.length == 0) {
        let messsage = textRequiredErrorMessage("Tên chiến dịch");
        showTooltip(messsage, 'campaign-name');
        check = false;
    } else if(name.length > 100) {
        let messsage = characterLimitedErrorMessage("Tên chiến dịch", 100);
        showTooltip(messsage, 'campaign-name');
        check = false;
    } else if (!duplicateName) {
        let messsage = existedErrorMessage("Tên chiến dịch");
        showTooltip(messsage, 'campaign-name');
        check = false;
    } else {
        removeTooltip('campaign-name');
    }

    /* tỷ lệ chấp thuận dự kiến */
    let expectedApprovalRate = $("#expected-approval-rate").val();
    if (expectedApprovalRate.length > 0){
        if((isPositiveInteger(expectedApprovalRate) && Number(expectedApprovalRate ) <= 100)) {
            removeTooltip('expected-approval-rate');
        } else {
            let messsage = characterTypeErrorMessage("số nguyên, giá trị từ 0 đến 100");
            showToolTipOnBottom(messsage, 'expected-approval-rate');
            check = false;
        }
    }

    /* mục tiêu chiến dịch - chuaxong */
    let campaignTarget = $("#campaign-target").val();
    let size = campaignTarget.split(",").length;
    if (campaignTarget.length == 0){
        let messsage = textRequiredErrorMessage("Mục tiêu chiến dịch");
        showTooltip(messsage, 'tagator_campaign-target');
        check = false;
    } else if (size > 10){
        let messsage = "Số nhãn tối đa: 10.";
        showTooltip(messsage, 'tagator_campaign-target');
        check = false;
    } else {
        removeTooltip('tagator_campaign-target');
    }

    /* mô tả */
    let description = $("#campaign-description").val();
    if (description.length > 1000){
        let messsage = characterLimitedErrorMessage("Mô tả", 1000);
        showToolTipOnBottom(messsage, 'campaign-description');
        check = false;
    } else {
        removeTooltip('campaign-description');
    }

    /* Hiệu lực chiến dịch*/

    let today = new Date();
    today.setHours(0,0,0,0);
    let dateString = $("#start-date").val();
    let dateParts = dateString.split("/");
    let startDate = new Date(+dateParts[2], dateParts[1] - 1, +dateParts[0]);
    dateString = $("#end-date").val();
    dateParts = dateString.split("/");
    let endDate = new Date(+dateParts[2], dateParts[1] - 1, +dateParts[0]);

    if (startDate < today){
        let messsage = "Ngày bắt đầu chiến dịch không được nhỏ hơn ngày hiện tại.";
        showTooltipElementID(messsage, 'div-start-date');
        check = false;
    } else {
        removeTooltipElementID('div-start-date');
    }

    if (startDate >= endDate) {
        let messsage = "Ngày kết thúc chiến dịch cần lớn hơn ngày bắt đầu ít nhất 01 ngày.";
        showToolTipOnBottom(messsage, 'div-end-date');
        check = false;
    } else if (endDate < today){
        let messsage = "Ngày kết thúc chiến dịch không được nhỏ hơn ngày hiện tại";
        showToolTipOnBottom(messsage, 'div-end-date');
        check = false;
    } else {
        removeTooltip('div-end-date');
    }


    /* khung giờ gửi tin */
    let timeRange1Start = $("#time-range-1-start").val();
    let timeRange1End = $("#time-range-1-end").val();
    let timeRange2Start = $("#time-range-2-start").val();
    let timeRange2End = $("#time-range-2-end").val();

    if (timeRange1Start >= timeRange1End) {
        let messsage = "Thời điểm kết thúc của khung giờ gửi tin cần lớn hơn thời điểm bắt đầu.";
        showTooltip(messsage, 'alert-time-range-1');
        check = false;
    } else {
        removeTooltip('alert-time-range-1');
    }

    /* Neu them khung gio thứ 2 */
    if($('#time-range-2:visible').length != 0) {
        if (timeRange2Start >= timeRange2End) {
            let messsage = "Thời điểm kết thúc của khung giờ gửi tin cần lớn hơn thời điểm bắt đầu.";
            showTooltip(messsage, 'alert-time-range-2');
            check = false;
        } else if ((timeRange1Start <= timeRange2Start && timeRange2Start <= timeRange1End) || (timeRange1Start <= timeRange2End && timeRange2End <= timeRange1End)
            || (timeRange1Start >= timeRange2Start && timeRange1End <= timeRange2End)) {
            let messsage = "Các khung giờ gửi tin không được đè lên nhau.";
            showTooltip(messsage, 'alert-time-range-2');
            check = false;
        } else {
            removeTooltip('alert-time-range-2');
        }
    }

    return check;
}

function checkDuplicateCampaignName (){

    let name = $("#campaign-name").val();
    var check = true;
    $.ajax({
        type: 'GET',
        url: '/event-campaign/check-duplicate-name?campaignName=' + name,
        traditional: true,
        dataType: 'json',
        async: false,
        success: function (response) {
            console.log(response);
            check = response.result;
        },
        error: function (e) {
            check = false;
            console.log(e);
        },
    });

    return check;
}