
// convert VN string
var ORIGINAL_CHAR = [
 		"Á", "À", "Ả", "Ạ", "Ã",
 		"á", "à", "ả", "ạ", "ã",
 		"Ă", "Ắ", "Ằ", "Ẳ", "Ặ", "Ẵ",
 		"ă", "ắ", "ằ", "ẳ", "ặ", "ẵ",
 		"Â", "Ấ", "Ầ", "Ẩ", "Ậ", "Ẫ",
 		"â", "ấ", "ầ", "ẩ", "ậ", "ẫ",
 		"É", "È", "Ẻ", "Ẹ", "Ẽ",
 		"é", "è", "ẻ", "ẹ", "ẽ",
 		"Ê", "Ế", "Ề", "Ể", "Ệ", "Ễ",
 		"ê", "ế", "ề", "ể", "ệ", "ễ",
 		"Ó", "Ò", "Ỏ", "Ọ", "Õ",
 		"ó", "ò", "ỏ", "ọ", "õ",
 		"Ô", "Ố", "Ồ", "Ổ", "Ộ", "Ỗ",
 		"ô", "ố", "ồ", "ổ", "ộ", "ỗ",
 		"Ơ", "Ớ", "Ờ", "Ở", "Ợ", "Ỡ",
 		"ơ", "ớ", "ờ", "ở", "ợ", "ỡ",
 		"Í", "Ì", "Ỉ", "Ị", "Ĩ",
 		"í", "ì", "ỉ", "ị", "ĩ",
 		"Ý", "Ỳ", "Ỷ", "Ỵ", "Ỹ",
 		"ý", "ỳ", "ỷ", "ỵ", "ỹ",
 		"Ú", "Ù", "Ủ", "Ụ", "Ũ",
 		"ú", "ù", "ủ", "ụ", "ũ",
 		"Ư", "Ứ", "Ừ", "Ử", "Ự", "Ữ",
 		"ư", "ứ", "ừ", "ử", "ự", "ữ",
 		"đ","Đ"
 ];
 	
 var REPLACE_CHAR = [
 		"A", "A", "A", "A", "A", "A",
 		"a", "a", "a", "a", "a", "a",
 		"A", "A", "A", "A", "A", "A",
 		"a", "a", "a", "a", "a", "a",
 		"A", "A", "A", "A", "A", "A",
 		"a", "a", "a", "a", "a", "a",
 		"E", "E", "E", "E", "E", "E",
 		"e", "e", "e", "e", "e", "e",
 		"E", "E", "E", "E", "E", "E",
 		"e", "e", "e", "e", "e", "e",
 		"O", "O", "O", "O", "O", "O",
 		"o", "o", "o", "o", "o", "o",
 		"O", "O", "O", "O", "O", "O",
 		"o", "o", "o", "o", "o", "o",
 		"O", "O", "O", "O", "O", "O",
 		"o", "o", "o", "o", "o", "o",
 		"I", "I", "I", "I", "I", "I",
 		"i", "i", "i", "i", "i", "i",
 		"Y", "Y", "Y", "Y", "Y", "Y",
 		"y", "y", "y", "y", "y", "y",
 		"U", "U", "U", "U", "U", "U",
 		"u", "u", "u", "u", "u", "u",
 		"U", "U", "U", "U", "U", "U",
 		"u", "u", "u", "u", "u", "u",
 		"d", "d","D","D"
 ];
 	
 function vnConvert(msg){
 	
 	if(msg == null || msg == ''){
 		return msg;
 	}
 	for (var i = 0; i < ORIGINAL_CHAR.length; i++) {
 		msg = msg.replace(ORIGINAL_CHAR[i],
 				REPLACE_CHAR[i]);
 	}
 	return msg;
 }
 
 function checkVNCharacter(msg){
	 var result = -1;
	 if(msg == null || msg == ''){
	 		return result;
	 }
	 for (var i = 0; i < ORIGINAL_CHAR.length; i++) {
	 		result = msg.search(ORIGINAL_CHAR[i]);
	 		if(result > -1) break;
	 }
	 return result;
 }