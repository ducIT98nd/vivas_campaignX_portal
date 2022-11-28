function changeFileBlacklist(){
    $("#error-blacklist-file").hide();
    removeTooltip('error-blacklist-file');
    removeTooltip('blacklist-file');
}
function validateStep3(){
    let check = true;
    var channel = $("#type-target-group").val();
    if(channel == 3 || channel == 4) {
        if(document.getElementById("data-Customer").files.length != 0){
            var fileDataTargetGroupSize = document.getElementById('data-Customer').files.item(0).size;
            console.log("fileDataWhiteListSize: "  +fileDataTargetGroupSize );

            var fileNameDataTargetGroup = document.getElementById('data-Customer').files.item(0).name;
            var fileExtensionDataTargetGroup = getFileExtension(fileNameDataTargetGroup);

            console.log("fileExtensionDataTargetGroup : " + fileExtensionDataTargetGroup);
            if(fileExtensionDataTargetGroup != "csv"){
                showTooltip("Định dạng file không hợp lệ.", 'data-Customer');
                check = false;
            }else {
                removeTooltip('data-Customer');
                if(fileDataTargetGroupSize > 300 * 1024 * 1024){
                    showTooltip("Dung lượng file không được vượt quá 300MB.", 'data-Customer');
                    check = false;
                }else {
                    removeTooltip('data-Customer');
                }
            }
        } else {
            showTooltip("File không được để trống.", 'data-Customer');
            check = false;
        }
    }
    let typeInputBlacklist = $("#type-input-blacklist").val();
    if(typeInputBlacklist == 3){
        if(document.getElementById("blacklist-file").files.length != 0){
            var fileDataTargetGroupSize = document.getElementById('blacklist-file').files.item(0).size;
            console.log("fileDataWhiteListSize: "  +fileDataTargetGroupSize );

            var fileNameDataTargetGroup = document.getElementById('blacklist-file').files.item(0).name;
            var fileExtensionDataTargetGroup = getFileExtension(fileNameDataTargetGroup);

            console.log("fileExtensionDataTargetGroup : " + fileExtensionDataTargetGroup);
            if(fileExtensionDataTargetGroup != "csv"){
                showTooltip("Định dạng file không hợp lệ.", 'blacklist-file');
                check = false;
            }else {
                removeTooltip('blacklist-file');
                if(fileDataTargetGroupSize > 300 * 1024 * 1024){
                    showTooltip("Dung lượng file không được vượt quá 300MB.", 'blacklist-file');
                    check = false;
                }else {
                    removeTooltip('blacklist-file');
                }
            }
        } else {
            showTooltip("File không được để trống.", 'blacklist-file');
            check = false;
        }
    }
    if(channel == 1 || channel == 4){
        if(submitCriteriaSetup() && check == true) check = true;
        else check = false;
    }
    let isSubgroups = false;
    isSubgroups = $("#sub-target-group-radio-1").is(":checked");
    if(isSubgroups) {
        let count = 0;
        let setPriority = new Set();
        let setName = new Set();
        let isDuplicateMsisdn = $("#is-duplicate-msisdn").is(":checked");
        for (const [key, value] of arrayTreeSubTarget) {
            let name = $("#subTargetGroupName_" + key).val();

            let json = null;
            if(!setTreesubTargetCanceled.has(key) || checkDataWhenSubCancleed == true){
                json = $("#jsonSubTarget_" + key).val();
            }else json = "";

            if(name == null || name.length <= 0){
                showTooltip("Tên nhóm nhỏ không được để trống.", "subTargetGroupName_" + key);
                check = false;
            }else {
                removeTooltip("subTargetGroupName_" + key);
                if(setName.has(name)){
                    showTooltip("Tên các nhóm nhỏ không được trùng lặp.", "subTargetGroupName_" + key);
                    check = false;
                }else {
                    setName.add(name);
                    removeTooltip("subTargetGroupName_" + key);
                }
            }

            if (isDuplicateMsisdn) {
                let subPriority = $("#subTargetGroupPriority_" + key).val();
                if(subPriority == null || subPriority.length <= 0){
                    showTooltip("Thứ tự ưu tiên không được để trống.", "subTargetGroupPriority_" + key);
                    check = false;
                }else {
                    if(setPriority.has(subPriority)){
                        showTooltip("Thứ tự ưu tiên trùng lặp giữa các nhóm nhỏ.", "subTargetGroupPriority_" + key);
                        check = false;
                    }else {
                        setPriority.add(subPriority);
                        removeTooltip("subTargetGroupPriority_" + key);
                    }
                    if ((name != null && name.length > 0) && (json != null && json.length > 0) && (subPriority != null && subPriority.length > 0)) {
                        count++;
                    }
                }
            } else {
                if ((name != null && name.length > 0) && (json != null && json.length > 0)) {
                    count++
                }
            }
        }
        if(setPriority.size == arrayTreeSubTarget.size){
            if (count != arrayTreeSubTarget.size || arrayTreeSubTarget.size <= 1 ) {
                showAlertMessageError("Nhóm nhỏ chưa được thiết lập đầy đủ thông tin. Hoặc cần thiết lập ít nhất 02 nhóm nhỏ.");
                check = false;
            }
        }else {
            if (count != arrayTreeSubTarget.size || arrayTreeSubTarget.size <= 1 ) {
                showAlertMessageError("Nhóm nhỏ chưa được thiết lập đầy đủ thông tin. Hoặc cần thiết lập ít nhất 02 nhóm nhỏ.");
                check = false;
            }
        }
    }

    return check;
}
