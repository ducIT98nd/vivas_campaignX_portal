
var subTargetGroup = 0;
$(document).ready(function(){
    $('#formid').on('keyup keypress', function(e) {
        var keyCode = e.keyCode || e.which;
        if (keyCode === 13) {
            e.preventDefault();
            return false;
        }
    });
    $("#is-duplicate-msisdn").change(function() {
        if(this.checked) {
            changeCheckboxDuplicateMessage();
        }
    });

    $.ajax({
        type: 'GET',
        url: '/SubTargetGroupController/viewSubTargetGroup?id=0',
        traditional: true,
        async: false,
        success: function (response) {
            $("#viewSubTargetGroup_0").append(response);
        },
        error: function (e) {
            console.log(e);
        },
    });

    criteriaIDSequence++;
    $.ajax({
        type: 'GET',
        url: '/SubTargetGroupController/GetRowLevel1Criteria?currentId='+criteriaIDSequence + '&parentId=0&subTreeId=0',
        traditional: true,
        async: false,
        success: function (response) {
            $("#subSetCriteria_0").after(response);
            $("#buttonDelete_"+criteriaIDSequence).attr("disabled", "disabled");
        },
        error: function (e) {
            console.log(e);
        },
    });
    let subCriteriaTree = new SubCriteriaTree("0");
    subCriteriaTree.insert("0", criteriaIDSequence);
    arrayTreeSubTarget.set(0, subCriteriaTree);

    if(arrayTreeSubTarget.size == 1){
        for (const [keySubTarget, value] of arrayTreeSubTarget) {
            $("#btn-delete-sub-"+keySubTarget).prop("disabled", true);
        }
    }

});










