window.onload = function() {
function myChecked(){
    	let data = jQuery("#theForm").serializeObject();
    	alert(jQuery("#theForm"));
    	let gdetailes = "①基本内容："+data['classify']+data['goods_name']+data['gbname']+data['goods_inventory']+"②类目属性："+data['specification']+"采购价（商家可设置可见状态，添加选项按钮：①已登录已首营认证可见②已登录已首营认证已建立采购可见）"+data['approval_number']+data['manufacturer']+data['dosage_form']+data['a_charge']+data['medium_package']
    	+"③商品信息：upc编码";
    	console.log("gdetailes");
    }


jQuery.prototype.serializeObject = function () {
    let form_data = this.serializeArray();
    let obj = {};
    jQuery.each(form_data, function (index, item) {
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
}