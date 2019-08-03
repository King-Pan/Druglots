let errInfo = new Map();
errInfo.set("businessNumber", "请填写营业执照号码！");
errInfo.set("EndBusinessDate", "请填写营业执照有效截止日期！");
errInfo.set("DrugNumber", "请填写药品经营许可证号码！");
errInfo.set("EndDrugDate", "请填写药品经营许可证有效截止日期！");
errInfo.set("GSPNumber", "请填写GSP证号码！");
errInfo.set("EndGSPDate", "请填写GSP证有效截止日期！");
errInfo.set("PurchaseNumber", "请填写采购委托书号码！");
errInfo.set("EndPurchaseDate", "请填写采购委托书有效截止日期！");
errInfo.set("IDcardNumber", "请填写身份证号！");
errInfo.set("EndIDcardDate", "请填写身份证证有效截止日期！");
errInfo.set("DutyParagraph","请填写税号！");

/**
 * 表单提交事件
 * @returns {boolean}
 * @author yanziwei
 */
function myCheck() {
    let zwdata = $("#theForm").serializeObject();
    // 循环检查所有的input是否为空，并提示用户
    for ( var item in zwdata) {
	    	if (zwdata[item + ""] === "") {
	            alert(errInfo.get(item + ""));
	            return false;    // 阻止表单提交
	        }
        // 检查身份证号码填写是否为18位
        if (item === "IDcardNumber") {
            let card = document.getElementById("fileNumber5").value;
            if (card.length !== 18) {
                alert("您输入的身份证长度有误！");
                return false;
            }
        }
	}
    return true;    // 提交表单
}

$.prototype.serializeObject = function () {
    let form_data = this.serializeArray();
    let obj = {};
    $.each(form_data, function (index, item) {
        if (Object.keys(obj).indexOf(item.name) === -1) {
            obj[item.name] = item.value;
        } else {
            Array.isArray(obj[item.name])
                ? obj[item.name].push(item.value)
                : obj[item.name] = [obj[item.name], item.value];
        }
    });
    return obj;
};