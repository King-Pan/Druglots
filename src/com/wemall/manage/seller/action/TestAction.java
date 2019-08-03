package com.wemall.manage.seller.action;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wemall.core.mv.JModelAndView;
import com.wemall.core.security.support.SecurityUserHolder;
import com.wemall.core.tools.CommUtil;
import com.wemall.core.tools.HttpClass;
import com.wemall.core.tools.WebForm;
import com.wemall.foundation.domain.Address;
import com.wemall.foundation.domain.Area;
import com.wemall.foundation.domain.Authentication;
import com.wemall.foundation.domain.User;
import com.wemall.foundation.service.AuthenticationService;
import com.wemall.foundation.service.IAddressService;
import com.wemall.foundation.service.IAreaService;
import com.wemall.foundation.service.ISysConfigService;
import com.wemall.foundation.service.IUserConfigService;
import com.wemall.foundation.service.IUserService;
import com.wemall.view.web.action.TimerRunAction;

@Controller
public class TestAction {

	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IAreaService areaService;
    @Autowired
    private IAddressService addressService;
    @Autowired
 	private IUserService userService;
    @Autowired
	   private AuthenticationService authenticationService;

	@Test
	public void cheshi() {

	}

	@RequestMapping("/user/check.htm")
	public void check(@RequestParam("enterpriseName") String enterpriseName,
			@RequestParam("businesRegsNumber") String businesRegsNumber,
			@RequestParam("DutyParagraph") String DutyParagraph,
			@RequestParam("IDcardNumber") String IDcardNumber,
			@RequestParam("GSPNumber") String GSPNumber,
			@RequestParam("DrugNumber") String DrugNumber,
			@RequestParam("businessNumber") String businessNumber,
			@RequestParam("userName") String userName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		HttpClass hc = new HttpClass();

		Authentication au = new Authentication();
		au.setUserName(userName);// 用户名
		au.setBusinesRegsNumber(businesRegsNumber);// 营业执照注册号
		au.setEnterpriseName(enterpriseName);// 企业名称
		au.setBusinessNumber(businessNumber);// 营业执照号码
		au.setDrugNumber(DrugNumber);// 药品经营许可证号码
		au.setgSPNumber(GSPNumber);// GSP证号码
		au.setiDcardNumber(IDcardNumber);// 身份证号码
		au.setDutyParagraph(DutyParagraph);// 税号
		String fan = hc.load("http://127.0.0.1:8081/ssm_project/shuihao",
				"duixiang=" + JSON.toJSONString(au));
		String zfc = "";
		if (!"cg".equals(fan)) {
			Authentication aut = JSON.parseObject(fan, Authentication.class);
			// 创建一个字符串用来拼接
			System.out.println(aut);

			if (aut.getBusinesRegsNumber().equals(businesRegsNumber)) {
				zfc += "营业执照注册号  ";
			}
			if (aut.getEnterpriseName().equals(enterpriseName)) {
				zfc += "企业名称  ";
			}
			if (aut.getBusinessNumber().equals(businessNumber)) {
				zfc += "营业执照号码  ";
			}
			if (aut.getDrugNumber().equals(DrugNumber)) {
				zfc += "药品经营许可证号码 ";
			}
			if (aut.getgSPNumber().equals(GSPNumber)) {
				zfc += "GSP证号码  ";
			}
			if (aut.getiDcardNumber().equals(IDcardNumber)) {
				zfc += "身份证号码  ";
			}
			if (aut.getDutyParagraph().equals(DutyParagraph)) {
				zfc += "税号  ";
			}
			zfc += "被占用,请重新填写";
		}
		System.out.println(zfc);
		response.getWriter().write(zfc);

	}

	/**
	 * 用户认证接口，调用了外部接口存储数据
	 * 
	 * @param request
	 * @param response
	 * @param url
	 * @return
	 */
	@RequestMapping("/user/login1.htm")
	public ModelAndView rengzheng(
			@RequestParam("enterpriseName") String enterpriseName,
			@RequestParam("trueName") String trueName,
			@RequestParam("businesRegsNumber") String businesRegsNumber,
			@RequestParam("areaId") String areaId,
			@RequestParam("detailedStreet") String detailedStreet,
			@RequestParam("Features") int Features, String PublicAccount,
			@RequestParam("DutyParagraph") String DutyParagraph,
			@RequestParam("EndPurchaseDate") String EndPurchaseDate,
			@RequestParam("EndGSPDate") String EndGSPDate,
			@RequestParam("EndDrugDate") String EndDrugDate,
			@RequestParam("EndBusinessDate") String EndBusinessDate,
			@RequestParam("EndIDcardDate") String EndIDcardDate,
			@RequestParam("IDcardNumber") String IDcardNumber,
			@RequestParam("PurchaseNumber") String PurchaseNumber,
			@RequestParam("GSPNumber") String GSPNumber,
			@RequestParam("DrugNumber") String DrugNumber,
			@RequestParam("businessNumber") String businessNumber,
			@RequestParam("userName") String userName,
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam("file1") MultipartFile file1,
			@RequestParam("file2") MultipartFile file2,
			@RequestParam("file3") MultipartFile file3,
			@RequestParam("file4") MultipartFile file4,
			@RequestParam("file5") MultipartFile file5,
			@RequestParam("file6") MultipartFile file6) {
		Authentication authentication = new Authentication();
		WebForm wf = new WebForm();
		User u = SecurityUserHolder.getCurrentUser();
	    User user = (User)wf.toPo(request, u);
		Area  area1 = this.areaService.getObjById(CommUtil.null2Long(areaId));
		user.setArea(area1);
		user.setWW(enterpriseName);
		
		Area area = this.areaService.getObjById(Long.valueOf(Long
				.parseLong(areaId)));
		String a1 = area.getAreaName();
		Area areaParaent = area.getParent();
		String a2 = areaParaent.getAreaName();
		Area areaParaent1 = areaParaent.getParent();
		String a3 = areaParaent1.getAreaName();
		String diqu = a3 + a2 + a1 + "";
		System.out.println(diqu);
		
		user.setAddress(diqu);
		this.userService.update(user);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String nowTime = sdf.format(new Date());

		HttpClass hc = new HttpClass();
		// 判断字段是否为空
		if (DutyParagraph == null || DutyParagraph.length() <= 0
				|| EndPurchaseDate == null || EndPurchaseDate.length() <= 0
				|| EndGSPDate == null || EndGSPDate.length() <= 0
				|| EndDrugDate == null || EndDrugDate.length() <= 0
				|| EndBusinessDate == null || EndBusinessDate.length() <= 0
				|| EndIDcardDate == null || EndIDcardDate.length() <= 0
				|| IDcardNumber == null || IDcardNumber.length() <= 0
				|| PurchaseNumber == null || PurchaseNumber.length() <= 0
				|| GSPNumber == null || GSPNumber.length() <= 0
				|| DrugNumber == null || DrugNumber.length() <= 0
				|| businessNumber == null || businessNumber.length() <= 0
				|| userName == null || userName.length() <= 0) {
			return Jump("error.html", "认证信息不完整", request, response);
		}
		if (nowTime.compareTo(EndBusinessDate) > 0
				|| nowTime.compareTo(EndPurchaseDate) > 0
				|| nowTime.compareTo(EndIDcardDate) > 0
				|| nowTime.compareTo(EndGSPDate) > 0
				|| nowTime.compareTo(EndDrugDate) > 0) {
			/*
			 * String examine="3"; try { String ue=
			 * hc.load("http://127.0.0.1:8081/ssm_project/UpdateExamine",
			 * "examine="+examine+"&"+"userName="+userName); } catch (Exception
			 * e1) { // TODO Auto-generated catch block e1.printStackTrace(); }
			 */
			return Jump("error.html", "认证信息过期,请提交有效认证信息", request, response);

		}
		// 判断如果是卖家，单独判断对公账号是否为空,
		if (Features == 2) {
			if (PublicAccount == null || PublicAccount.length() <= 0) {
				return Jump("error.html", "认证信息不完整", request, response);
			}
		}
		// 开始认证
		try {

			// 查询用户，判断是否认证过
			String Load = hc.load("http://127.0.0.1:8081/ssm_project/select",
					"userName=" + userName);

			// 创建对象接受数据
			Authentication au = new Authentication();
			au.setUserName(userName);// 用户名
			au.setTrueName(trueName);// 真实姓名
			au.setBusinesRegsNumber(businesRegsNumber);// 营业执照注册号
			au.setEnterpriseName(enterpriseName);// 企业名称
			au.setBusinessNumber(businessNumber);// 营业执照号码
			au.setDrugNumber(DrugNumber);// 药品经营许可证号码
			au.setgSPNumber(GSPNumber);// GSP证号码
			au.setiDcardNumber(IDcardNumber);// 身份证号码
			au.setDutyParagraph(DutyParagraph);// 税号
			
			// 开始判断税号是否重复
			/*
			 * try { String fan =
			 * hc.load("http://127.0.0.1:8081/ssm_project/shuihao", "duixiang="
			 * + JSON.toJSONString(au)); if (!"cg".equals(fan)) { Authentication
			 * aut = JSON.parseObject(fan, Authentication.class); // 创建一个字符串用来拼接
			 * System.out.println(aut); String zfc = "您填写的"; if
			 * (aut.getBusinesRegsNumber().equals(businesRegsNumber)) { zfc +=
			 * "营业执照注册号  "; } if
			 * (aut.getEnterpriseName().equals(enterpriseName)) { zfc +=
			 * "企业名称  "; } if (aut.getBusinessNumber().equals(businessNumber)) {
			 * zfc += "营业执照号码  "; } if (aut.getDrugNumber().equals(DrugNumber))
			 * { zfc += "药品经营许可证号码 "; } if
			 * (aut.getgSPNumber().equals(GSPNumber)) { zfc += "GSP证号码  "; } if
			 * (aut.getiDcardNumber().equals(IDcardNumber)) { zfc += "身份证号码  ";
			 * } if (aut.getDutyParagraph().equals(DutyParagraph)) { zfc +=
			 * "税号  "; } zfc += "被占用,请重新填写"; return Jump("error.html", zfc,
			 * request, response); } } catch (Exception e1) { // TODO
			 * Auto-generated catch block e1.printStackTrace(); }
			 */
			// 如果有就是修改数据
			if (Load != null && Load != "") {
				Authentication auth = JSON.parseObject(Load,
						Authentication.class);
				
               // 调用保存方法保存图片，六张图片保存,如果改了的就存，没改就原样
				if (file1.getSize() != 0) {
					String s1 = saveFile(request, file1);// 营业执照
					auth.setBusinessLicense(s1);// 营业执照
				}
				if (file2.getSize() != 0) {
					String s2 = saveFile(request, file2);// 药品经营许可证
					auth.setDrugLicense(s2);// 药品经营许可证
				}
				if (file3.getSize() != 0) {
					String s3 = saveFile(request, file3);// gsp证书
					auth.setgSPCertificate(s3);// gsp证书
				}
				if (file4.getSize() != 0) {
					String s4 = saveFile(request, file4);// 采购委托书
					auth.setPurchaseOrders(s4);// 采购委托书
				}
				if (file5.getSize() != 0) {
					String s5 = saveFile(request, file5);// 身份证复印件正反面
					auth.setiDcard(s5);// 身份证复印件正反面
				}
				if (file6.getSize() != 0) {
					String s6 = saveFile(request, file6);// 手持身份证
					auth.setHandIDcard(s6);// 手持身份证
				}

				// 修改对象里面的值
				auth.setAreaId(diqu);// 所在地区
				auth.setAddress(areaId);//所在地区id
				auth.setBusinesRegsNumber(businesRegsNumber);// 营业执照注册号
				auth.setDetailedStreet(detailedStreet);// 详细地址
				auth.setEnterpriseName(enterpriseName);// 企业名称
				auth.setBusinessNumber(businessNumber);// 营业执照号码
				auth.setEndBusinessDate(EndBusinessDate);// 营业执照有效截止日期
				auth.setDrugNumber(DrugNumber);// 药品经营许可证号码
				auth.setEndDrugDate(EndDrugDate);// 药品经营许可证有效截止日期
				auth.setgSPNumber(GSPNumber);// GSP证号码
				auth.setEndGSPDate(EndGSPDate);// GSP证有效截止日期
				auth.setPurchaseNumber(PurchaseNumber);// 采购委托书号码
				auth.setEndPurchaseDate(EndPurchaseDate);// 采购委托书有效截止日期
				auth.setiDcardNumber(IDcardNumber);// 身份证号码
				auth.setEndIDcardDate(EndIDcardDate);// 身份证有效截止日期
				auth.setDutyParagraph(DutyParagraph);// 税号
				auth.setPublicAccount(PublicAccount);// 对公账号
				auth.setExamine(1);// 认证状态
				auth.setTrueName(trueName);//真实姓名
				auth.setNote(0);
				System.out.println(auth.getExamine());
				if (auth.getFeatures() == 2) {
					Features = 2;
				}
				authentication.setFeatures(Features);// 1是买家2是卖家

				auth.setNewAuthenticationDate(new SimpleDateFormat("yyyy-MM-dd")
						.format(new Date()));// 当前时间

				// 对象转json字符串
				String s = JSON.toJSONString(auth);
				// 调用修改接口修改数据
				String fh = hc.load(
						"http://127.0.0.1:8081/ssm_project/Updateobj",
						"duixiang=" + s);
				if (fh != null) {
					return Jump("success.html", "待审核", request, response);
				} else {
					return Jump("error.html", "认证信息修改失败", request, response);
				}

			}

			// 判断file不能为空
			if (StringUtils.isBlank(file1.getOriginalFilename())
					|| StringUtils.isBlank(file2.getOriginalFilename())
					|| StringUtils.isBlank(file3.getOriginalFilename())
					|| StringUtils.isBlank(file4.getOriginalFilename())
					|| StringUtils.isBlank(file5.getOriginalFilename())
					|| StringUtils.isBlank(file6.getOriginalFilename())) {
				return Jump("error.html", "认证信息不完整", request, response);
			}

			// 调用保存方法保存图片，六张图片保存
			String s1 = saveFile(request, file1);// 营业执照
			String s2 = saveFile(request, file2);// 药品经营许可证
			String s3 = saveFile(request, file3);// gsp证书
			String s4 = saveFile(request, file4);// 采购委托书
			String s5 = saveFile(request, file5);// 身份证复印件正反面
			String s6 = saveFile(request, file6);// 手持身份证

			// 存入对象
			authentication.setUserId(SecurityUserHolder.getCurrentUser().getId().toString());//用户id
			authentication.setAreaId(diqu);// 所在地区
			authentication.setAddress(areaId);//所在地区id
			authentication.setBusinesRegsNumber(businesRegsNumber);// 营业执照注册号
			authentication.setEnterpriseName(enterpriseName);// 企业名称
			authentication.setDetailedStreet(detailedStreet);// 详细地址
			authentication.setUserName(userName);// 用户id，需要页面传递
			authentication.setTrueName(trueName);//用户的真实姓名
			authentication.setExamine(1);// 认证状态
			authentication.setBusinessNumber(businessNumber);// 营业执照号码
			authentication.setEndBusinessDate(EndBusinessDate);// 营业执照有效截止日期
			authentication.setDrugNumber(DrugNumber);// 药品经营许可证号码
			authentication.setEndDrugDate(EndDrugDate);// 药品经营许可证有效截止日期
			authentication.setgSPNumber(GSPNumber);// GSP证号码
			authentication.setEndGSPDate(EndGSPDate);// GSP证有效截止日期
			authentication.setPurchaseNumber(PurchaseNumber);// 采购委托书号码
			authentication.setEndPurchaseDate(EndPurchaseDate);// 采购委托书有效截止日期
			authentication.setiDcardNumber(IDcardNumber);// 身份证号码
			authentication.setEndIDcardDate(EndIDcardDate);// 身份证有效截止日期
			authentication.setDutyParagraph(DutyParagraph);// 税号
			authentication.setPublicAccount(PublicAccount);// 对公账号
			authentication.setFeatures(Features);// 1是买家2是卖家
			authentication.setNote(0);

			// 将六张图片存入对象
			authentication.setBusinessLicense(s1);// 营业执照
			authentication.setDrugLicense(s2);// 药品经营许可证
			authentication.setgSPCertificate(s3);// gsp证书
			authentication.setPurchaseOrders(s4);// 采购委托书
			authentication.setiDcard(s5);// 身份证复印件正反面
			authentication.setHandIDcard(s6);// 手持身份证
			authentication.setNewAuthenticationDate(new SimpleDateFormat(
					"yyyy-MM-dd").format(new Date()));// 当前时间

			// 转json字符串
			String s = JSON.toJSONString(authentication);
			String filesUpload = hc.load(
					"http://127.0.0.1:8081/ssm_project/filesUpload",
					"duixiang=" + s);
			// 成功跳转成功页面,买家
			if (authentication.getFeatures() == 1) {
				if (filesUpload.equals("cg")) {
					try {
						//获取用户对象
						User user1 = SecurityUserHolder.getCurrentUser();
						user1.setTrueName(trueName);
						this.userService.update(user1);
						//成功提交，新增默认地址
						Address address = new Address();
						address.setTrueName(authentication.getTrueName());//往address添加用户真实姓名
						address.setAddTime(new Date());
						address.setTelephone(authentication.getUserName());
						address.setUser(user1);
						address.setMobile(user1.getMobile());
						address.setArea_info(detailedStreet);
						address.setArea(area1);
						addressService.save(address);
						/*//提交时触发首营过期定时器方法
						TimerRunAction run1 = new TimerRunAction();
						run1.test(request, response,authentication.getUserId());*/
					} catch (Exception e) {
						// TODO: handle exception
					}
					return Jump("success.html", "待审核", request, response);
					
				} 
				else {
					return Jump("error.html", "认证信息提交失败", request, response);
				}
			}else {
				return null;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// 失败跳转失败页面

			return Jump("error.html", "认证信息提交错误", request, response);

		}

	}

	/**
	 * 用户认证接口，调用了外部接口存储数据,该接口为店铺开通调用，因为其不能跳转页面
	 * 
	 * @param request
	 * @param response
	 * @param url
	 * @return
	 * @throws Exception
	 */

	public String rengzheng2(@RequestParam("userId") String userId,
			@RequestParam("trueName") String trueName,
			@RequestParam("businesRegsNumber") String businesRegsNumber,
			@RequestParam("area") Area area,@RequestParam("area_id") String area_id, 
			@RequestParam("store_address") String store_address,
			@RequestParam("store_name") String store_name,
			@RequestParam("Features") int Features, String PublicAccount,
			@RequestParam("DutyParagraph") String DutyParagraph,
			@RequestParam("EndPurchaseDate") String EndPurchaseDate,
			@RequestParam("EndGSPDate") String EndGSPDate,
			@RequestParam("EndDrugDate") String EndDrugDate,
			@RequestParam("EndBusinessDate") String EndBusinessDate,
			@RequestParam("EndIDcardDate") String EndIDcardDate,
			@RequestParam("IDcardNumber") String IDcardNumber,
			@RequestParam("PurchaseNumber") String PurchaseNumber,
			@RequestParam("GSPNumber") String GSPNumber,
			@RequestParam("DrugNumber") String DrugNumber,
			@RequestParam("businessNumber") String businessNumber,
			@RequestParam("userName") String userName,
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam("file1") MultipartFile file1,
			@RequestParam("file2") MultipartFile file2,
			@RequestParam("file3") MultipartFile file3,
			@RequestParam("file4") MultipartFile file4,
			@RequestParam("file5") MultipartFile file5,
			@RequestParam("file6") MultipartFile file6) {

		Authentication authentication = new Authentication();
		HttpClass hc = new HttpClass();
		String a1 = area.getAreaName();
		Area areaParaent = area.getParent();
		String a2 = areaParaent.getAreaName();
		Area areaParaent1 = areaParaent.getParent();
		String a3 = areaParaent1.getAreaName();
		String diqu = a3 + a2 + a1 + "";
		System.out.println(diqu);

		// 判断字段是否为空
		if (businesRegsNumber == null || PublicAccount == null
				|| PublicAccount.length() <= 0 || DutyParagraph == null
				|| DutyParagraph.length() <= 0 || EndPurchaseDate == null
				|| EndPurchaseDate.length() <= 0 || EndGSPDate == null
				|| EndGSPDate.length() <= 0 || EndDrugDate == null
				|| EndDrugDate.length() <= 0 || EndBusinessDate == null
				|| EndBusinessDate.length() <= 0 || EndIDcardDate == null
				|| EndIDcardDate.length() <= 0 || IDcardNumber == null
				|| IDcardNumber.length() <= 0 || PurchaseNumber == null
				|| PurchaseNumber.length() <= 0 || GSPNumber == null
				|| GSPNumber.length() <= 0 || DrugNumber == null
				|| DrugNumber.length() <= 0 || businessNumber == null
				|| businessNumber.length() <= 0 || userName == null
				|| userName.length() <= 0) {
			return "zd";
		}

		// 开始认证
		try {
			System.out.println(userName);
			// 查询用户，判断是否认证过
			String Load = hc.load("http://127.0.0.1:8081/ssm_project/select",
					"userName=" + userName);
			// 如果有就是修改数据
			if (Load != null && Load != "") {
				Authentication auth = JSON.parseObject(Load,
						Authentication.class);

				// 调用保存方法保存图片，六张图片保存,如果改了的就存，没改就原样
				if (StringUtils.isNotBlank(file1.getOriginalFilename())) {
					String s1 = saveFile(request, file1);// 营业执照
					auth.setBusinessLicense(s1);// 营业执照
				}
				if (StringUtils.isNotBlank(file2.getOriginalFilename())) {
					String s2 = saveFile(request, file2);// 药品经营许可证
					auth.setDrugLicense(s2);// 药品经营许可证
				}
				if (StringUtils.isNotBlank(file3.getOriginalFilename())) {
					String s3 = saveFile(request, file3);// gsp证书
					auth.setgSPCertificate(s3);// gsp证书
				}
				if (StringUtils.isNotBlank(file4.getOriginalFilename())) {
					String s4 = saveFile(request, file4);// 采购委托书
					auth.setPurchaseOrders(s4);// 采购委托书
				}
				if (StringUtils.isNotBlank(file5.getOriginalFilename())) {
					String s5 = saveFile(request, file5);// 身份证复印件正反面
					auth.setiDcard(s5);// 身份证复印件正反面
				}
				if (StringUtils.isNotBlank(file6.getOriginalFilename())) {
					String s6 = saveFile(request, file6);// 手持身份证
					auth.setHandIDcard(s6);// 手持身份证
				}

				// 修改对象里面的值
				auth.setAreaId(diqu);// 所在地区
				auth.setAddress(area_id);//所在地区id
				auth.setBusinesRegsNumber(businesRegsNumber);// 营业执照注册号
				auth.setEnterpriseName(store_name);// 店铺名称
				auth.setDetailedStreet(store_address);// 详细地址
				auth.setBusinessNumber(businessNumber);// 营业执照号码
				auth.setEndBusinessDate(EndBusinessDate);// 营业执照有效截止日期
				auth.setDrugNumber(DrugNumber);// 药品经营许可证号码
				auth.setEndDrugDate(EndDrugDate);// 药品经营许可证有效截止日期
				auth.setgSPNumber(GSPNumber);// GSP证号码
				auth.setEndGSPDate(EndGSPDate);// GSP证有效截止日期
				auth.setPurchaseNumber(PurchaseNumber);// 采购委托书号码
				auth.setEndPurchaseDate(EndPurchaseDate);// 采购委托书有效截止日期
				auth.setiDcardNumber(IDcardNumber);// 身份证号码
				auth.setEndIDcardDate(EndIDcardDate);// 身份证有效截止日期
				auth.setDutyParagraph(DutyParagraph);// 税号
				auth.setPublicAccount(PublicAccount);// 对公账号
				if (auth.getFeatures() == 2) {
					Features = 2;
				}
				authentication.setFeatures(Features);// 1是买家2是卖家
				auth.setNewAuthenticationDate(new SimpleDateFormat("yyyy-MM-dd")
						.format(new Date()));// 当前时间

				// 对象转json字符串
				String s = JSON.toJSONString(auth);
				// 调用修改接口修改数据
				String fh = hc.load(
						"http://127.0.0.1:8081/ssm_project/Updateobj",
						"duixiang=" + s);
				if (fh == null) {
					return "xg";
				}

				return "cg";

			}

			// 判断file不能为空
			if (StringUtils.isBlank(file1.getOriginalFilename())
					|| StringUtils.isBlank(file2.getOriginalFilename())
					|| StringUtils.isBlank(file3.getOriginalFilename())
					|| StringUtils.isBlank(file4.getOriginalFilename())
					|| StringUtils.isBlank(file5.getOriginalFilename())
					|| StringUtils.isBlank(file6.getOriginalFilename())) {
				return "tp";
			}

			// 调用保存方法保存图片，六张图片保存
			String s1 = saveFile(request, file1);// 营业执照
			String s2 = saveFile(request, file2);// 药品经营许可证
			String s3 = saveFile(request, file3);// gsp证书
			String s4 = saveFile(request, file4);// 采购委托书
			String s5 = saveFile(request, file5);// 身份证复印件正反面
			String s6 = saveFile(request, file6);// 手持身份证

			// 存入对象
			authentication.setUserId(userId);
			authentication.setTrueName(trueName);
			authentication.setUserName(userName);// 用户id，需要页面传递
			authentication.setAreaId(diqu);// 所在地区
			authentication.setAddress(area_id);//所在地区id
			authentication.setBusinesRegsNumber(businesRegsNumber);// 营业执照注册号
			authentication.setEnterpriseName(store_name);// 企业名称
			authentication.setDetailedStreet(store_address);// 详细地址
			authentication.setExamine(1);// 认证状态
			authentication.setBusinessNumber(businessNumber);// 营业执照号码
			authentication.setEndBusinessDate(EndBusinessDate);// 营业执照有效截止日期
			authentication.setDrugNumber(DrugNumber);// 药品经营许可证号码
			authentication.setEndDrugDate(EndDrugDate);// 药品经营许可证有效截止日期
			authentication.setgSPNumber(GSPNumber);// GSP证号码
			authentication.setEndGSPDate(EndGSPDate);// GSP证有效截止日期
			authentication.setPurchaseNumber(PurchaseNumber);// 采购委托书号码
			authentication.setEndPurchaseDate(EndPurchaseDate);// 采购委托书有效截止日期
			authentication.setiDcardNumber(IDcardNumber);// 身份证号码
			authentication.setEndIDcardDate(EndIDcardDate);// 身份证有效截止日期
			authentication.setDutyParagraph(DutyParagraph);// 税号
			authentication.setPublicAccount(PublicAccount);// 对公账号
			authentication.setFeatures(Features);// 1是买家2是卖家
			authentication.setNote(0);

			// 将六张图片存入对象
			authentication.setBusinessLicense(s1);// 营业执照
			authentication.setDrugLicense(s2);// 药品经营许可证
			authentication.setgSPCertificate(s3);// gsp证书
			authentication.setPurchaseOrders(s4);// 采购委托书
			authentication.setiDcard(s5);// 身份证复印件正反面
			authentication.setHandIDcard(s6);// 手持身份证
			authentication.setNewAuthenticationDate(new SimpleDateFormat(
					"yyyy-MM-dd").format(new Date()));// 当前时间

			// 转json字符串
			String s = JSON.toJSONString(authentication);
			String filesUpload = hc.load(
					"http://127.0.0.1:8081/ssm_project/filesUpload",
					"duixiang=" + s);
			// 成功跳转成功页面,买家
			if (authentication.getFeatures() == 1) {
				if (!filesUpload.equals("cg")) {
					return "tp";
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// 失败跳转失败页面
			return "cw";
		}
		return "cg";

	}

	/***
	 * 页面跳转，私有调用
	 * 
	 * @param file
	 * @return
	 */
	private ModelAndView Jump(String lu, String msg,
			HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(lu,
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("current_url", lu);
		mv.addObject("op_title", msg);
		mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		return mv;
	}

	/***
	 * 保存文件，私有调用
	 * 
	 * @param file
	 * @return
	 */
	private String saveFile(HttpServletRequest request, MultipartFile file) {
		// 判断文件是否为空
		if (!file.isEmpty()) {
			try {
				String sdf = new SimpleDateFormat("yyyy-MM-dd")
						.format(new Date());

				String separator = File.separator;
				String picPath2 = request.getSession().getServletContext()
						.getRealPath("")
						+ File.separator + "upload" + File.separator;
				String directory = picPath2 + "wyInFile" + separator + sdf
						+ "/";
				String newPicName = "";
				if (file.getSize() != 0) {
					String originalFileNameLeft = file.getOriginalFilename();
					// 新的图片名称
					newPicName = UUID.randomUUID()
							+ originalFileNameLeft
									.substring(originalFileNameLeft
											.lastIndexOf("."));
					// 新图片，写入磁盘
					File f = new File(directory, newPicName);
					if (!f.exists()) {
						// 先创建文件所在的目录
						f.getParentFile().mkdirs();
					}
					file.transferTo(f);
				}
				return newPicName;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "";

	}

}
