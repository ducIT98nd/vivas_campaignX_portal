function tagInput(dataUrl, inputDiv){

    $.ajax({
        url: dataUrl,
        type: 'get',
        dataType: 'json',
        async: false,
        success: function(response) {
            if (response.code == 0){
                inputDiv.tagator({
                    autocomplete: response.data,
                    prefix:                'tagator_',
                    height:                'auto',
                    showAllOptionsOnFocus: false
                });
            }
        }
    });
}
