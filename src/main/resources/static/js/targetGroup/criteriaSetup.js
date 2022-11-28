/*cấu trúc dữ liệu cây để lưu danh sách tiêu chí*/
var criteriaTree =  new CriteriaTree("0");
/*Biến tự tăng để đánh id của tiêu chí*/
var criteriaIDSequence = 0;

$(document).ready(function () {
    criteriaIDSequence++;
    $.ajax({
        type: 'GET',
        url: '/TargetGroupController/GetRowLevel1Criteria?currentId='+criteriaIDSequence + '&parentId=0',
        traditional: true,
        async: false,
        success: function (response) {
            $("#SetCriteria").after(response);
            $("#buttonDelete_"+criteriaIDSequence).attr("disabled", "disabled");
        },
        error: function (e) {
            console.log(e);
        },
    });

    criteriaTree.insert("0", criteriaIDSequence);
});

