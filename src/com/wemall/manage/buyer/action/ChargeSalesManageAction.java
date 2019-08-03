package com.wemall.manage.buyer.action;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.ResponseWrapper;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.druid.sql.visitor.functions.Now;
import com.alibaba.fastjson.JSON;
import com.wemall.core.annotation.SecurityMapping;
import com.wemall.core.domain.virtual.SysMap;
import com.wemall.core.mv.JModelAndView;
import com.wemall.core.query.PageList;
import com.wemall.core.query.support.IPageList;
import com.wemall.core.security.support.SecurityUserHolder;
import com.wemall.core.tools.CommUtil;
import com.wemall.core.tools.ExportExcelSeedBack;
import com.wemall.core.tools.HttpClass;
import com.wemall.foundation.domain.Authentication;
import com.wemall.foundation.domain.BuyerCreditLimit;
import com.wemall.foundation.domain.CreditLlineManagement;
import com.wemall.foundation.domain.Message;
import com.wemall.foundation.domain.OrderForm;
import com.wemall.foundation.domain.ProcurementRelationship;
import com.wemall.foundation.domain.StoreCreditStatus;
//import com.wemall.foundation.domain.Refund;
import com.wemall.foundation.domain.Store;
import com.wemall.foundation.domain.User;
import com.wemall.foundation.domain.query.BuyerCreditLimitQueryObject;
import com.wemall.foundation.domain.query.OrderFormQueryObject;
import com.wemall.foundation.service.AuthenticationService;
import com.wemall.foundation.service.BuyerCreditLimitService;
import com.wemall.foundation.service.IMessageService;
import com.wemall.foundation.service.IOrderFormService;
import com.wemall.foundation.service.IStoreService;
import com.wemall.foundation.service.ISysConfigService;
import com.wemall.foundation.service.IUserConfigService;
import com.wemall.foundation.service.IUserService;
import com.wemall.foundation.service.ProcurementRelationshipService;
import com.wemall.foundation.service.StoreCreditStatusService;

/**
 * 买家赊销管理控制器
 * 
 * @author yanziwei
 *
 */
@Controller
public class ChargeSalesManageAction {

	@Autowired
	private AuthenticationService authenticationservice;

	@Autowired
	private ISysConfigService configService;

	@Autowired
	private IUserConfigService userConfigService;

	@Autowired
	private BuyerCreditLimitService buyerCreditLimitService;

	@Autowired
	private ProcurementRelationshipService procurementRelationshipService;

	@Autowired
	private IUserService userService;

	@Autowired
	private StoreCreditStatusService storeCreditStatusService;

	@Autowired
	private IStoreService storeService;

	@Autowired
	private IOrderFormService orderFormService;

	@Autowired
	private IMessageService messageService;

	@SecurityMapping(display = false, rsequence = 0, title = "买家赊销管理", value = "/buyer/charge_sales_manage.htm*", rtype = "buyer", rname = "赊销管理", rcode = "wemall_charge_sales", rgroup = "赊销管理")
	@RequestMapping({ "/buyer/charge_sales_manage.htm" })
	public ModelAndView coupon(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/charge_sales_manage.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		/*
		 * String storeId=request.getParameter("storeId"); String
		 * userName=request.getParameter("userName");
		 */

		/*
		 * HttpClass hc = new HttpClass(); String lwload=hc.toString();
		 */
		try {
			Long buyerId = SecurityUserHolder.getCurrentUser().getId();

			Map params = new HashMap();
			params.put("buyerId", buyerId.intValue());
			List buyerCreditLimitList = this.buyerCreditLimitService.query(
					"select obj from BuyerCreditLimit obj where  obj.buyerId=:buyerId  and obj.zstatus> 1", params, -1,
					-1);

			/*
			 * for(int i = 0; i < jsonArray.size(); i++) { //获取每一个JsonObject对象 JSONObject
			 * jsonObject = (JSONObject) jsonArray.get(i); BuyerCreditLimit buyerCreditLimit
			 * = (BuyerCreditLimit)JSONObject.toBean(jsonObject, BuyerCreditLimit.class);
			 * storeNameList.add(buyerCreditLimit); }
			 */
			mv.addObject("zwlist", buyerCreditLimitList);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "买家赊销历史记录", value = "/buyer/charge_sales_history.htm*", rtype = "buyer", rname = "赊销历史记录", rcode = "wemall_charge_sales", rgroup = "赊销历史记录")
	@RequestMapping({ "/buyer/charge_sales_history.htm" })
	public ModelAndView chargeHistory(HttpServletRequest request, HttpServletResponse response, int storeId) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/charge_sales_history.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);

		Long buyerId = SecurityUserHolder.getCurrentUser().getId();
		try {
			Map params = new HashMap();
			params.put("storeId", storeId);
			params.put("buyerId", buyerId.intValue());
			List buyerCreditLimitList = this.buyerCreditLimitService.query(
					"select obj from BuyerCreditLimit obj where obj.storeId=:storeId and obj.buyerId=:buyerId", params,
					-1, -1);

			BuyerCreditLimit buyerCreditLimit = (BuyerCreditLimit) buyerCreditLimitList.get(0);

			mv.addObject("buyerCreditLimit", buyerCreditLimit);

			params.clear();
			params.put("storeId", Long.valueOf(storeId));
			params.put("buyerId", Long.valueOf(buyerId));
			params.put("pay_msg", "赊销支付");
			List orderFormList = this.orderFormService.query(
					"select obj from OrderForm obj where obj.store.id=:storeId and obj.user.id=:buyerId and  obj.pay_msg=:pay_msg  and obj.order_status=20",
					params, -1, -1);
			mv.addObject("orderFormList", orderFormList);
		} catch (Exception e) {
			mv = new JModelAndView("error.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			mv.addObject("op_title", "买家赊销历史记录数据获取错误，请联系统管理员！");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}

		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "建立赊销信息", value = "/buyer/build_charge_sales_info.htm*", rtype = "buyer", rname = "填写赊销信息", rcode = "wemall_charge_sales", rgroup = "填写赊销信息")
	@RequestMapping({ "/buyer/build_charge_sales_info.htm" })
	public ModelAndView buildChargeInfo(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/build_charge_sales_info.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		// 验证是否登陆
		try {
			Long id = SecurityUserHolder.getCurrentUser().getId();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			mv = new JModelAndView("error.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			mv.addObject("op_title", "请登录！");
			mv.addObject("url", CommUtil.getURL(request) + "/user/login.htm");
			return mv;
		}

		try {
			Long buyerid = SecurityUserHolder.getCurrentUser().getId();
			int buyerId = buyerid.intValue();

			Map params = new HashMap();
			params.put("buyerId", String.valueOf(buyerId));
			List procurementRelationshipList = this.procurementRelationshipService.query(
					"select obj from ProcurementRelationship obj where obj.buyerId=:buyerId and obj.auditStatus=1",
					params, -1, -1);
			if (procurementRelationshipList.size() > 0) {
				HttpClass hc = new HttpClass();
				try {
					/* 查询不包含建立赊销关系的采购列表 */
					String proShip = hc.load("http://127.0.0.1:8081/ssm_project/checkRepeat", "buyerId=" + buyerId);
					JSONArray jsonArray = JSONArray.fromObject(proShip);
					List<ProcurementRelationship> proList = new LinkedList<>();
					for (int i = 0; i < jsonArray.size(); i++) {
						// 获取每一个JsonObject对象
						JSONObject jsonObject = (JSONObject) jsonArray.get(i);
						ProcurementRelationship procurementRelationship = (ProcurementRelationship) JSONObject
								.toBean(jsonObject, ProcurementRelationship.class);
						proList.add(procurementRelationship);
					}
					mv.addObject("proship", proList);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}

				return mv;

			} else {
				mv = new JModelAndView("error.html", this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
				mv.addObject("op_title", "未建立任何采购关系！");
				mv.addObject("url", CommUtil.getURL(request) + "/buyer/charge_sales_manage.htm");
			}

			// List BuyerCreditLimitList=this.buyerCreditLimitService.query(
			// "select obj from BuyerCreditLimit obj where obj.buyerId=:buyerId and
			// obj.zstatus=2", params, -1, -1);
			//
			// if(BuyerCreditLimitList.size()>0){
			// mv = new JModelAndView("error.html", this.configService.getSysConfig(),
			// this.userConfigService.getUserConfig(), 1, request, response);
			// mv.addObject("op_title", "正在审核,不能再次提交！");
			// mv.addObject("url", CommUtil.getURL(request) +
			// "/buyer/charge_sales_manage.htm");
			// }

		} catch (Exception e) {
			e.printStackTrace();
		}

		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "填写赊销信息", value = "/buyer/charge_sales_info.htm*", rtype = "buyer", rname = "填写赊销信息", rcode = "wemall_charge_sales", rgroup = "填写赊销信息")
	@RequestMapping({ "/buyer/charge_sales_info.htm" })
	public ModelAndView chargeInfo(HttpServletRequest request, HttpServletResponse response, String zwstoreId) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/charge_sales_info.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		// 验证是否登陆
		try {
			Long id = SecurityUserHolder.getCurrentUser().getId();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			mv = new JModelAndView("error.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			mv.addObject("op_title", "请登录！");
			mv.addObject("url", CommUtil.getURL(request) + "/user/login.htm");
			return mv;
		}
		Long userId = SecurityUserHolder.getCurrentUser().getId();
		String userStoreid = "";
		Map userparams = new HashMap();
		userparams.put("userId", userId);
		List userList = this.userService.query("select obj from User obj where obj.id=:userId", userparams, -1, -1);
		User user = (User) userList.get(0);
		if ((user.getUserRole() != null) && (!user.getUserRole().equals(""))
				&& !user.getUserRole().equals("BUYER_SELLER")) {
			userStoreid = userId.toString();
		} else {
			userStoreid = String.valueOf(SecurityUserHolder.getCurrentUser().getStore().getId());
		}
		if (zwstoreId == userStoreid || zwstoreId.equals(userStoreid)) {
			mv = new JModelAndView("error.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			mv.addObject("op_title", "不能和自己店铺建立赊销！");
			mv.addObject("url", CommUtil.getURL(request) + "/store_" + zwstoreId + ".htm");
			return mv;
		}

		String yzwsan = null;
		try {
			yzwsan = SecurityUserHolder.getCurrentUser().getUserName();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			yzwsan = null;
		}

		if (yzwsan != null && yzwsan != "") {

			try {
				Long buyerid = SecurityUserHolder.getCurrentUser().getId();
				int buyerId = buyerid.intValue();

				int storeid = Integer.valueOf(zwstoreId);

				Map params = new HashMap();
				params.put("storeId", String.valueOf(storeid));
				params.put("buyerId", String.valueOf(buyerId));
				List procurementRelationshipList = this.procurementRelationshipService.query(
						"select obj from ProcurementRelationship obj where obj.storeId=:storeId and obj.buyerId=:buyerId",
						params, -1, -1);
				if (procurementRelationshipList.size() < 1) {
					mv = new JModelAndView("error.html", this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "请先建立采购关系！");
					mv.addObject("url",
							CommUtil.getURL(request) + "/buyer/buildPurchaseRelation.htm?store_id=" + storeid);
				} else if (((ProcurementRelationship) procurementRelationshipList.get(0)).getAuditStatus() == 0) {
					mv = new JModelAndView("error.html", this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "采购关系审核中，暂不可提交！");
					mv.addObject("url", CommUtil.getURL(request) + "/store_" + storeid + ".htm");
				}

				else {
					// 判断赊销是否开启
					// Map creditparam = new HashMap();
					// creditparam.put("storeId",zwstoreId);
					// List creditList=this.storeCreditStatusService.query(
					// "select obj from StoreCreditStatus obj where obj.storeId=:storeId",
					// creditparam, -1, -1);
					// StoreCreditStatus storestatu=(StoreCreditStatus) creditList.get(0);
					// if (Integer.valueOf(storestatu.getState())==1){
					params.clear();
					params.put("storeId", storeid);
					params.put("buyerId", buyerId);
					Map storeparam = new HashMap();

					// 将申请赊销店铺信息回显
					storeparam.put("storeId", Long.valueOf(Long.parseLong(zwstoreId)));
					List StoreList = this.storeService.query("select obj from Store obj where obj.id=:storeId",
							storeparam, -1, -1);
					Store stores = (Store) StoreList.get(0);
					mv.addObject("applyname", stores.getStore_name());
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					mv.addObject("dates", df.format(new Date()));
					mv.addObject("zwstoreId", zwstoreId);

					// 从赊销商城点击判断是否正在审核重复提交
					List BuyerCreditLimitList = this.buyerCreditLimitService.query(
							"select obj from BuyerCreditLimit obj where obj.storeId=:storeId and obj.buyerId=:buyerId and obj.zstatus=2",
							params, -1, -1);

					if (BuyerCreditLimitList.size() > 0) {
						mv = new JModelAndView("error.html", this.configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1, request, response);
						mv.addObject("op_title", "正在审核,不能再次提交！");
						mv.addObject("url", CommUtil.getURL(request) + "/store_" + storeid + ".htm");
					}

					// 从赊销商城点击判断是否审核完成重复提交
					List statusList = this.buyerCreditLimitService.query(
							"select obj from BuyerCreditLimit obj where obj.storeId=:storeId and obj.buyerId=:buyerId and obj.zstatus=3",
							params, -1, -1);

					if (statusList.size() > 0) {
						mv = new JModelAndView("error.html", this.configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1, request, response);
						mv.addObject("op_title", "赊销已通过,不能再次提交！");
						mv.addObject("url", CommUtil.getURL(request) + "/store_" + storeid + ".htm");
					}
					// } else {
					// mv = new JModelAndView("error.html", this.configService.getSysConfig(),
					// this.userConfigService.getUserConfig(), 1, request, response);
					// mv.addObject("op_title", "店铺未开启赊销功能，敬请期待!");
					// mv.addObject("url", CommUtil.getURL(request) + "/store_" + storeid + ".htm");
					// }
					//

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			mv = new JModelAndView("error.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			mv.addObject("op_title", "请登录！");
			mv.addObject("url", CommUtil.getURL(request) + "/user/login.htm");
		}
		return mv;
	}

	/**
	 * 建立赊销信息
	 * 
	 * @param request
	 * @param response
	 * @param storeName
	 * @param apTime
	 * @param buyerCombination
	 * @return
	 */
	@RequestMapping({ "/subcharge.htm" })
	public ModelAndView subcharge(HttpServletRequest request, HttpServletResponse response, int storeId,
			String storeName, String startTime, String num, String buyerCombination) {
		ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		BuyerCreditLimit buyerCreditLimit = new BuyerCreditLimit();
		HttpClass hc = new HttpClass();
		Long buyerid = SecurityUserHolder.getCurrentUser().getId();
		int buyerId = buyerid.intValue();
		String userName = SecurityUserHolder.getCurrentUser().getUserName();
		buyerCreditLimit.setBuyerName(userName);
		buyerCreditLimit.setAddTime(new Date());
		buyerCreditLimit.setStoreId(storeId);

		try {
			// 从我的账户——>赊销管理判断是否重复提交
			Map params = new HashMap();
			params.put("storeId", storeId);
			params.put("buyerId", buyerId);
			List<BuyerCreditLimit> BuyerCreditLimitList = this.buyerCreditLimitService.query(
					"select obj from BuyerCreditLimit obj where obj.storeId=:storeId and obj.buyerId=:buyerId and ( obj.zstatus!=0 and  obj.zstatus!=2) and obj.limitState!=1",
					params, -1, -1);
			System.out.println(BuyerCreditLimitList.size() > 0);
			if (BuyerCreditLimitList.size() > 0 ) {
				mv = new JModelAndView("error.html", this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
				mv.addObject("op_title", "正在审核,不能再次提交！");
				mv.addObject("url", CommUtil.getURL(request) + "/buyer/buyer_my_charge_sales.htm");
				return mv;
			}
			params.clear();
			String strIds = String.valueOf(storeId);
			params.put("storeId", storeId);
			params.put("buyerId", buyerId);
			List<BuyerCreditLimit> BuyerCreditLimitList2 = this.buyerCreditLimitService.query(
					"select obj from BuyerCreditLimit obj where obj.storeId=:storeId and obj.buyerId=:buyerId", params,
					-1, -1);

			if (BuyerCreditLimitList2.size() > 0 || BuyerCreditLimitList2 == null) {

				BuyerCreditLimit BuyerCreditLimit = BuyerCreditLimitList2.get(0);
				BuyerCreditLimit.setZstatus(2);
				BuyerCreditLimit.setLimitState(1);
				BuyerCreditLimit.setBuyerCombination(buyerCombination);
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String substring = sdf.format(date).substring(8, 10);
				BuyerCreditLimit.setRepaymentDate(Integer.valueOf(substring));
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				BuyerCreditLimit.setLimitState(0);
				BuyerCreditLimit.setAccountDate(0);
				String arr[] = startTime.split(" - ");
				if (arr.length > 0) {
					for (int j = 0; j < arr.length; j++) {
						buyerCreditLimit.setStartTime(sdf.parse(arr[0]));
						buyerCreditLimit.setMonthClean(sdf.parse(arr[1]));
					}
				}
				buyerCreditLimit.setApTime(0);
				this.buyerCreditLimitService.update(BuyerCreditLimit);

			} else {

				params.clear();
				strIds = String.valueOf(storeId);
				params.put("storeId", Long.valueOf(Long.parseLong(strIds)));
				List storeinfo = this.storeService.query("select obj from Store obj where obj.id=:storeId", params, -1,
						-1);
				Store stores = (Store) storeinfo.get(0);
				buyerCreditLimit.setStoreName(stores.getStore_name());
				String jsonUser = hc.load("http://127.0.0.1:8081/ssm_project/selSeller", "storeId=" + storeId);
				// 将获取到的json转为User对象
				JSONObject jsonObject = JSONObject.fromObject(jsonUser);
				User user = (User) JSONObject.toBean(jsonObject, User.class);
				// 存入buyerCreditLimit对象
				int id = user.getId().intValue();
				buyerCreditLimit.setSellerId(id);
				buyerCreditLimit.setSellerName(user.getUserName());
				buyerCreditLimit.setBuyerId(buyerId);
				buyerCreditLimit.setBuyerName(userName);
				//总额度 
				buyerCreditLimit.setBuyerCombination(buyerCombination);
				//剩余额度
				buyerCreditLimit.setBuyerRemainingUndrawn(buyerCombination);
				buyerCreditLimit.setZstatus(5);
				buyerCreditLimit.setLimitState(0);
				buyerCreditLimit.setAccountDate(0);
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String substring = sdf.format(date).substring(8, 10);

				buyerCreditLimit.setRepaymentDate(Integer.valueOf(substring));
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				String arr[] = startTime.split(" - ");
				if (arr.length > 1) {
					for (int j = 0; j < arr.length; j++) {
						buyerCreditLimit.setStartTime(sdf.parse(arr[0]));
						buyerCreditLimit.setMonthClean(sdf.parse(arr[1]));
					}
				}
				String zwjs = hc.load("http://127.0.0.1:8081/ssm_project/insBuyerCharge",
						"obje=" + JSON.toJSONString(buyerCreditLimit));
			}

		} catch (Exception e) {
			mv = new JModelAndView("success.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			mv.addObject("op_title", "提交失败，网络异常！");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/buyer_my_charge_sales.htm");
			return mv;
		}

		mv = new JModelAndView("success.html", this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("op_title", "提交成功，待审核！");
		mv.addObject("url", CommUtil.getURL(request) + "/buyer/buyer_my_charge_sales.htm");
		// 赊销申请的站内短信
		Map map = new HashMap();
		map.put("userName", buyerCreditLimit.getBuyerName());
		List<Authentication> auths = authenticationservice
				.query("select obj from Authentication obj where obj.userName=:userName", map, -1, -1);
		long idstore = (long) storeId;
		Store stroStore = this.storeService.getObjById(idstore);
		String content = auths.get(0).getEnterpriseName() + "向您的店铺申请开通赊销。";
		Message msg = new Message();
		msg.setAddTime(new Date());
		Whitelist whiteList = new Whitelist();
		content = content.replaceAll("\n", "iskyhop_br");
		msg.setContent(Jsoup.clean(content, Whitelist.basic()).replaceAll("iskyhop_br", "\n"));
		msg.setFromUser(SecurityUserHolder.getCurrentUser());
		msg.setToUser(stroStore.getUser());
		msg.setType(1);
		this.messageService.save(msg);
		return mv;
	}

	// 计算相差天数
	public static int getDistanceTime(Date startTime, Date endTime) {
		int days = 0;
		long time1 = startTime.getTime();
		long time2 = endTime.getTime();

		long diff;
		if (time1 < time2) {
			diff = time2 - time1;
		} else {
			diff = time1 - time2;
		}
		days = (int) (diff / (24 * 60 * 60 * 1000));
		return days;
	}

	@RequestMapping({ "/zwupdzstatus.htm" })
	public void updstatus(HttpServletRequest request, HttpServletResponse response, int storeId) {
		response.setContentType("application/json;charset=utf-8");
		try {
			HttpClass hc = new HttpClass();
			Long buyerId = SecurityUserHolder.getCurrentUser().getId();
			Map params = new HashMap();
			params.put("storeId", storeId);
			params.put("buyerId", buyerId.intValue());
			List<BuyerCreditLimit> buyerCreditLimit = buyerCreditLimitService.query(
					"select obj from BuyerCreditLimit obj where obj.storeId=:storeId and obj.buyerId=:buyerId  ",
					params, -1, -1);

			if (buyerCreditLimit != null || buyerCreditLimit.size() > 0) {
				BuyerCreditLimit buyerCreditLimi = buyerCreditLimit.get(0);
				buyerCreditLimi.setZstatus(2);
				buyerCreditLimitService.update(buyerCreditLimi);
				// 赊销申请的站内短信
				Map map = new HashMap();
				map.put("userName", buyerCreditLimi.getBuyerName());
				List<Authentication> auths = authenticationservice
						.query("select obj from Authentication obj where obj.userName=:userName", map, -1, -1);
				long idstore = (long) storeId;
				Store stroStore = this.storeService.getObjById(idstore);
				String content = auths.get(0).getEnterpriseName() + "向您的店铺申请开通赊销。";
				Message msg = new Message();
				msg.setAddTime(new Date());
				Whitelist whiteList = new Whitelist();
				content = content.replaceAll("\n", "iskyhop_br");
				msg.setContent(Jsoup.clean(content, Whitelist.basic()).replaceAll("iskyhop_br", "\n"));
				msg.setFromUser(SecurityUserHolder.getCurrentUser());
				msg.setToUser(stroStore.getUser());
				msg.setType(1);
				this.messageService.save(msg);

			}

			// String zwjs = hc.load("http://127.0.0.1:8081/ssm_project/qwer",
			// "zstatus="+2+"&"+"buyerId="+buyerId+"&"+"storeId="+storeId);
			response.getWriter().print(1);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	String storeName1 = "";
	String time1 = "";

	@SecurityMapping(display = false, rsequence = 0, title = "买家-我的赊销", value = "/buyer/buyer_my_charge_sales.htm*", rtype = "buyer", rname = "赊销管理", rcode = "buyer_my_charge_sales", rgroup = "赊销管理")
	@RequestMapping({ "/buyer/buyer_my_charge_sales.htm" })
	public ModelAndView buyerMychangeSales(HttpServletRequest request,
			HttpServletResponse response, String time, String storeName,String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/buyer_my_charge_sales.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		try {
			String url = this.configService.getSysConfig().getAddress();
			Long buyerId = SecurityUserHolder.getCurrentUser().getId();
			// 查询赊销状态通过的集合
			BuyerCreditLimitQueryObject qo = new BuyerCreditLimitQueryObject(
					currentPage, mv, "addTime", "desc");
			qo.addQuery("obj.buyerId",
					new SysMap("buyerId", buyerId.intValue()), "=");
			qo.addQuery("obj.zstatus", new SysMap("zstatus", 1), ">");

			// 授额期限
			time1 = time;
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (time != null && !time.equals("")) {
				String arr[] = time.split(" - ");
				if (arr.length > 1) {
					for (int j = 0; j < arr.length; j++) {

						Date startTime1 = format.parse(new StringBuffer(arr[0])
								.append(" 00:00:00").toString());
						Date monthClean = format.parse(new StringBuffer(arr[1])
								.append(" 00:00:00").toString());
						qo.addQuery("obj.startTime", new SysMap("startTime",
								startTime1), ">=");
						qo.addQuery("obj.monthClean", new SysMap("monthClean",
								monthClean), "<=");
					}
				}

			}

			// 客户名称
			storeName1 = storeName;
			if (storeName != null && !storeName.equals("")) {
				qo.addQuery("obj.storeName", new SysMap("storeName", "%"
						+ storeName + "%"), "like");
			}

			qo.setOrderBy("addTime");
			qo.setOrderType("desc");
			IPageList pList = this.buyerCreditLimitService.list(qo);
			CommUtil.saveIPageList2ModelAndView(url + "", "", "", pList, mv);
			List<BuyerCreditLimit> BuyerCreditLimits = pList.getResult();
			double total = 0;
			double shengyu = 0;
			double yiyong = 0;
			double totalday=0;
			if(pList.getRowCount() != 0) {
			for (BuyerCreditLimit buyerCreditLimit : BuyerCreditLimits) {
				OrderFormQueryObject qos = new OrderFormQueryObject(
						"1", mv, "addTime", "desc");
				qos.addQuery("obj.user.id", new SysMap("userId", buyerId), "=");
				 IPageList list = this.orderFormService.list(qos);
				 if(list.getRowCount()!=0) {
					 List<OrderForm> OrderForms = list.getResult();
						for (OrderForm orderForm : OrderForms) {
							// 获取当前日期
							Date date = new Date();
							SimpleDateFormat formats = new SimpleDateFormat(
									"yyyy-MM-dd");
							String formatDate = formats.format(orderForm
									.getPayTime());
							String nowdates = formats.format(date);
							StringBuffer dates = new StringBuffer(formatDate);
							StringBuffer datess = new StringBuffer(formatDate);
							mv.addObject("dates", dates);
							// 拼接出最后还款日
							StringBuffer replace = dates.replace(8, 10, String
									.valueOf(buyerCreditLimit.getRepaymentDate()));
							StringBuffer account = dates.replace(8, 10, String
									.valueOf(buyerCreditLimit.getAccountDate()));
							// 如果还款日的月份与当前日期的月份相等
							if (new StringBuffer(orderForm.getPayTime().toString())
									.subSequence(5, 7).equals(
											replace.subSequence(5, 7))) {
								int month = Integer.parseInt(dates
										.subSequence(5, 7).toString());
								// 最后还款日月份+1
								replace = dates.replace(
										8,
										10,
										String.valueOf(buyerCreditLimit
												.getRepaymentDate())).replace(5, 7,
										String.valueOf(month + 1));
								account = datess.replace(
										8,
										10,
										String.valueOf(buyerCreditLimit
												.getAccountDate())).replace(5, 7,
										String.valueOf(month + 1));
							}
							// 拼接结果的最后还款日
							mv.addObject("repaymentDate", replace);
							mv.addObject("accountDate", account);
							if (orderForm.getActual_date() != null) {
								long date1 = formats.parse(
										orderForm.getActual_date().toString())
										.getTime()
										- formats.parse(replace.toString())
												.getTime();
								if (date1 < 0) {
									orderForm.setDays(0);
								} else {
									orderForm
											.setDays(date1 / (1000 * 60 * 60 * 24));
								}
							} else {
								long date2 = formats.parse(nowdates.toString())
										.getTime()
										- formats.parse(replace.toString())
												.getTime();
								if (date2 > 0) {
									orderForm
											.setDays(date2 / (1000 * 60 * 60 * 24));
								} else {
									orderForm.setDays(0);
								}
							}
							// 总罚息
							double totalPrice = orderForm.getTotalPrice().doubleValue();
							totalday = totalday + orderForm.getDays() * totalPrice * buyerCreditLimit.getInterest();
						}
				 }
				
				
				total = Double.parseDouble(buyerCreditLimit.getBuyerCombination());
				shengyu = Double.parseDouble(buyerCreditLimit
						.getBuyerRemainingUndrawn());
				yiyong = total - shengyu;
			}
		}
			CommUtil.saveIPageList2ModelAndView(url + "", "", "", pList, mv);
			mv.addObject("total", total);
			mv.addObject("totalday", totalday+yiyong);
			mv.addObject("yiyong", yiyong);
			mv.addObject("shengyu", shengyu);
			mv.addObject("storeName",storeName);
			
			return mv;
		} catch (Exception e) {

			e.printStackTrace();
		}

		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "买家-导出我的赊销列表", value = "/buyer/buyer_my_charge_sales.htm*", rtype = "buyer", rname = "赊销管理", rcode = "buyer_my_charge_sales", rgroup = "赊销管理")
	@RequestMapping({ "/buyer/buyer_sales_excel.htm" })
	public void buyerSalesExcel(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/buyer_my_charge_sales.htm.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		try {
			String url = this.configService.getSysConfig().getAddress();
			Long buyerId = SecurityUserHolder.getCurrentUser().getId();
			// 查询赊销状态通过的集合
			BuyerCreditLimitQueryObject qo = new BuyerCreditLimitQueryObject("1", mv, "addTime", "desc");
			qo.addQuery("obj.buyerId", new SysMap("buyerId", buyerId.intValue()), "=");
			qo.addQuery("obj.zstatus", new SysMap("zstatus", 1), ">");

			// 授额期限
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (time1 != null && !time1.equals("")) {
				String arr[] = time1.split(" - ");
				if (arr.length > 1) {
					for (int j = 0; j < arr.length; j++) {

						Date startTime1 = format.parse(new StringBuffer(arr[0]).append(" 00:00:00").toString());
						Date monthClean = format.parse(new StringBuffer(arr[1]).append(" 00:00:00").toString());
						qo.addQuery("obj.startTime", new SysMap("startTime", startTime1), ">=");
						qo.addQuery("obj.monthClean", new SysMap("monthClean", monthClean), "<=");
					}
				}

			}

			// 客户名称
			if (storeName1 != null && !storeName1.equals("")) {
				qo.addQuery("obj.storeName", new SysMap("storeName", "%" + storeName1 + "%"), "like");
			}

			qo.setOrderBy("addTime");
			qo.setOrderType("desc");
			IPageList pList = this.buyerCreditLimitService.list(qo);
			List<BuyerCreditLimit> limits = pList.getResult();
			// 查询的数据不为空就对数据进行导出
			if (pList != null && pList.getPageSize() > 0) {
				// 导出文件的标题
				String title = "mjsxlb" + ".xls";
				// 设置表格标题行
				String[] headers = new String[] { "序号","客户名称", "授额期限", "总额度(元)", "已用额度(元)", "剩余额度(元)", "出账日", "最后还款日", "逾期计息%" };
				List<Object[]> dataList = new ArrayList<>();
				Map map = new HashMap();
				for (BuyerCreditLimit limit : limits) {
					// 客户名称模糊查
					
					map.put("userId", String.valueOf(limit.getSellerId()));
					List<Authentication> auths = this.authenticationservice.query(
							"select obj from Authentication obj where obj.userId=:userId",
							map, -1, -1);
					if (auths != null && auths.size() > 0) {
						limit.setAuthString(auths.get(0).getEnterpriseName());
					}
					Object[] objs = new Object[9];
					objs[0] = limit.getId();// 序号
					objs[1] = limit.getAuthString();// 客户名称
					SimpleDateFormat formats = new SimpleDateFormat("yyyy-MM-dd");
					String formatDate1 = formats.format(limit.getStartTime());
					String times = formats.format(limit.getMonthClean());
					StringBuffer buffer = new StringBuffer(formatDate1 + "至" + times);
					objs[2] = buffer;// 授额期限
					objs[3] = limit.getBuyerCombination();// 总额度
					objs[4] = Integer.parseInt(limit.getBuyerCombination())
							- Integer.parseInt(limit.getBuyerRemainingUndrawn());// 已用额度
					objs[5] = limit.getBuyerRemainingUndrawn();// 剩余额度
					objs[6] = "每月"+limit.getAccountDate()+"日";// 出账日
					objs[7] = "每月"+limit.getRepaymentDate()+"日";// 最后还款日
					objs[8] = limit.getInterest()+"%";// 逾期计息%
					// 数据添加到excel表格
					dataList.add(objs);
				}
				// 使用流将数据导出
				OutputStream out = null;
				try {
					// 防止中文乱码
					String headStr = "attachment; filename=\"" + new String(title.getBytes("utf-8"), "utf-8") + "\"";
					response.setContentType("octets/stream");
					response.setContentType("APPLICATION/OCTET-STREAM");
					response.setHeader("Content-Disposition", headStr);
					out = response.getOutputStream();
					ExportExcelSeedBack ex = new ExportExcelSeedBack(title, headers, dataList);// 没有标题
					ex.export(out);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "买家-我的赊销详情", value = "/buyer/buyer_my_charge_sales_detail.htm*", rtype = "buyer", rname = "赊销管理", rcode = "buyer_my_charge_sales_detail", rgroup = "赊销管理")
	@RequestMapping({ "/buyer/buyer_my_charge_sales_detail.htm" })
	public ModelAndView buyerMychangeSalesDetail(HttpServletRequest request, HttpServletResponse response,
			Integer storeId, String number, String actual, Integer payBack,String currentPage) throws ParseException {
		ModelAndView mv = new JModelAndView("user/default/usercenter/buyer_my_charge_sales_detail.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		Long buyerId = SecurityUserHolder.getCurrentUser().getId();

		Map params = new HashMap();
		params.put("storeId", storeId);
		params.put("buyerId", buyerId.intValue());
		List<BuyerCreditLimit> buyerCreditLimits = buyerCreditLimitService.query(
				"select obj from BuyerCreditLimit obj where obj.storeId=:storeId and obj.buyerId=:buyerId  ", params,
				-1, -1);
		BuyerCreditLimit buyerCreditLimit = buyerCreditLimits.get(0);
		mv.addObject("store", buyerCreditLimit);
		// 获取当前日期
		Date date = new Date();
		SimpleDateFormat formats = new SimpleDateFormat("yyyy-MM-dd");
		String formatDate = formats.format(date);
		StringBuffer dates = new StringBuffer(formatDate);
		StringBuffer nowdates = new StringBuffer(formatDate);
		mv.addObject("dates", dates);
		// 拼接出最后还款日
		StringBuffer replace = dates.replace(8, 10, String.valueOf(buyerCreditLimit.getRepaymentDate()));
		// 如果还款日的月份与当前日期的月份相等
		if (new StringBuffer(buyerCreditLimit.getStartTime().toString()).subSequence(5, 7)
				.equals(replace.subSequence(5, 7))) {
			int month = Integer.parseInt(dates.subSequence(5, 7).toString());
			// 最后还款日月份+1
			replace = dates.replace(8, 10, String.valueOf(buyerCreditLimit.getRepaymentDate())).replace(5, 7,
					String.valueOf(month + 1));
		}
		// 拼接结果的最后还款日
		mv.addObject("repaymentDate", replace);

		OrderFormQueryObject qo = new OrderFormQueryObject(currentPage, mv, "addTime", "desc");
		qo.addQuery("obj.user.id", new SysMap("id", Long.valueOf(buyerId)), "=");
		qo.addQuery("obj.store.id", new SysMap("storeId", Long.valueOf(storeId)), "=");
		qo.addQuery("obj.bill_state", new SysMap("billState", 1), "=");

		// 还款编号
		if (number != null && !number.equals("")) {
			qo.addQuery("obj.number", new SysMap("number", number), "=");
		}

		// 实际还款日期
		if (actual != null && !actual.equals("")) {
			String arr[] = actual.split(" - ");
			if (arr.length > 1) {
				for (int j = 0; j < arr.length; j++) {

					Date startTime1 = formats.parse(new StringBuffer(arr[0]).append(" 00:00:00").toString());
					Date monthClean = formats.parse(new StringBuffer(arr[1]).append(" 00:00:00").toString());
					qo.addQuery("obj.actual_date", new SysMap("actual", startTime1), ">=");
					qo.addQuery("obj.actual_date", new SysMap("actual", monthClean), "<=");
				}
			}

		}

		// 还款状态
		if (payBack != null && payBack != 0) {
			qo.addQuery("obj.pay_status", new SysMap("payBack", payBack), "=");
		}

		IPageList pList = this.orderFormService.list(qo);
		List<OrderForm> orderForms = pList.getResult();
		double totalday = 0;
		if (orderForms != null) {
			for (OrderForm orderForm : orderForms) {
				// 判断是否有实际还款日 如果有 则用实际还款日减去最后还款日 若果没有 判断当前日期是否大于最后还款日 当前日期减去 最后还款日等于逾期时长
				if (orderForm.getActual_date() != null) {
					long date1 = formats.parse(orderForm.getActual_date().toString()).getTime()
							- formats.parse(replace.toString()).getTime();
					if (date1 < 0) {
						orderForm.setDays(0);
					} else {
						orderForm.setDays(date1 / (1000 * 60 * 60 * 24));
					}
				} else {
					long date2 = formats.parse(nowdates.toString()).getTime()
							- formats.parse(replace.toString()).getTime();
					if (date2 > 0) {
						orderForm.setDays(date2 / (1000 * 60 * 60 * 24));
					} else {
						orderForm.setDays(0);
					}
				}
				// 总罚息
				double totalPrice = orderForm.getTotalPrice().doubleValue();
				totalday = totalday + orderForm.getDays() * totalPrice * buyerCreditLimit.getInterest();
			}
		}
		mv.addObject("totalday", totalday);
		CommUtil.saveIPageList2ModelAndView(url + "/user/default/usercenter/buyer_my_charge_sales_detail.html", "", "",
				pList, mv);
		try {
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "买家-申请赊销", value = "/buyer/buyer_charge_sales_amount_granted.htm*", rtype = "buyer", rname = "赊销管理", rcode = "buyer_charge_sales_amount_granted", rgroup = "赊销管理")
	@RequestMapping({ "/buyer/buyer_charge_sales_amount_granted.htm" })
	public ModelAndView buyerChangeSalesAmountGranted(HttpServletRequest request, HttpServletResponse response,
			String flag, String id) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/buyer_charge_sales_amount_granted.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		try {
			Long buyerid = SecurityUserHolder.getCurrentUser().getId();
			int buyerId = buyerid.intValue();

			if (flag.equals("update")) {
				BuyerCreditLimit buyerCreditLimit = this.buyerCreditLimitService.getObjById(Long.valueOf(id));
				// 得到查询结果得到买家名称，去首营认证表去获取企业名称，
				Map map = new HashMap();
				map.put("userName", buyerCreditLimit.getBuyerName());
				List<Authentication> auths = authenticationservice
						.query("select obj from Authentication obj where obj.userName=:userName", map, -1, -1);
				buyerCreditLimit.setAuthString(auths.get(0).getEnterpriseName());
				mv.addObject("buyerCreditLimit", buyerCreditLimit);

				SimpleDateFormat formats = new SimpleDateFormat("yyyy-MM-dd");
				String formatDate1 = formats.format(buyerCreditLimit.getStartTime());
				String times = formats.format(buyerCreditLimit.getMonthClean());
				StringBuffer buffer = new StringBuffer(formatDate1 + " - " + times);
				mv.addObject("time", buffer);
				mv.addObject("flag", "update");
			} else if (flag.equals("save")) {
				mv.addObject("flag", "save");
				Map params = new HashMap();
				params.put("buyerId", String.valueOf(buyerId));
				List procurementRelationshipList = this.procurementRelationshipService.query(
						"select obj from ProcurementRelationship obj where obj.buyerId=:buyerId and obj.auditStatus=1",
						params, -1, -1);
				if (procurementRelationshipList.size() > 0) {
					HttpClass hc = new HttpClass();
					try {
						/* 查询不包含建立赊销关系的采购列表 */
						String proShip = hc.load("http://127.0.0.1:8081/ssm_project/checkRepeat", "buyerId=" + buyerId);
						JSONArray jsonArray = JSONArray.fromObject(proShip);
						List<ProcurementRelationship> proList = new LinkedList<>();
						for (int i = 0; i < jsonArray.size(); i++) {
							// 获取每一个JsonObject对象
							JSONObject jsonObject = (JSONObject) jsonArray.get(i);
							ProcurementRelationship procurementRelationship = (ProcurementRelationship) JSONObject
									.toBean(jsonObject, ProcurementRelationship.class);
							proList.add(procurementRelationship);
						}
						mv.addObject("proship", proList);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					return mv;
				} else {
					mv = new JModelAndView("error.html", this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "未建立任何采购关系！");
					mv.addObject("url", CommUtil.getURL(request) + "/buyer/buyer_my_charge_sales.htm");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "买家-赊销审核", value = "/buyer/buyer_charge_sales_check.htm*", rtype = "buyer", rname = "赊销管理", rcode = "buyer_charge_sales_check", rgroup = "赊销管理")
	@RequestMapping({ "/buyer/buyer_charge_sales_check.htm" })
	public ModelAndView buyerChangeSalesCheck(HttpServletRequest request, HttpServletResponse response, String storeId,
			String enterpriseName, String startTime, Integer zstatus, String buyerCombination, String sellerId,String currentPage) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/buyer_charge_sales_check.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		try {
			HttpClass hc = new HttpClass();
			Long buyerId = SecurityUserHolder.getCurrentUser().getId();

			BuyerCreditLimitQueryObject qo = new BuyerCreditLimitQueryObject(currentPage, mv, "addTime", "desc");
			qo.addQuery("obj.buyerId", new SysMap("sellerId", buyerId.intValue()), "=");
			qo.addQuery("obj.buyerCombination", new SysMap("buyerCombination",  "0"), "!=");

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (startTime != null && !startTime.equals("")) {
				String arr[] = startTime.split(" - ");
				if (arr.length > 1) {
					for (int j = 0; j < arr.length; j++) {

						Date startTime1 = formatter.parse(new StringBuffer(arr[0]).append     (" 00:00:00").toString());
						Date monthClean = formatter.parse(new StringBuffer(arr[1]).append(" 00:00:00").toString());
						qo.addQuery("obj.startTime", new SysMap("startTime", startTime1), ">=");
						qo.addQuery("obj.monthClean", new SysMap("monthClean", monthClean), "<=");
					}
				}
			}

			if (sellerId != null && !sellerId.equals("")) {
				qo.addQuery("obj.sellerId", new SysMap("sellerId", Integer.parseInt(sellerId)), "=");
			}
			if (zstatus != null && !zstatus.equals("")) {
				qo.addQuery("obj.zstatus", new SysMap("zstatus", zstatus), "=");
			}
			if (buyerCombination != null && !buyerCombination.equals("")) {
				qo.addQuery("obj.buyerCombination", new SysMap("buyerCombination", buyerCombination), "=");
			}
			qo.setOrderBy("addTime");
			qo.setOrderType("desc");
			IPageList pList = this.buyerCreditLimitService.list(qo);
			List<BuyerCreditLimit> buyerCreditLimits = pList.getResult();
			if (buyerCreditLimits != null && buyerCreditLimits.size() > 0) {
				for (BuyerCreditLimit buyerCreditLimit1 : buyerCreditLimits) {
					Map map = new HashMap();
					map.put("userName", buyerCreditLimit1.getBuyerName());
					// Authentication
					// auth=cl.load("http://127.0.0.1:8081/ssm_project/select","userName"+buyerCreditLimit.getBuyerName());
					if (enterpriseName != null && !enterpriseName.equals("")) {
						map.put("enterpriseName", "%" + enterpriseName + "%");
					} else {
						map.put("enterpriseName", "%%");
					}
					List<Authentication> auths = this.authenticationservice.query(
							"select obj from Authentication obj where obj.userName=:userName and obj.enterpriseName like :enterpriseName",
							map, -1, -1);
					if (auths.size() > 0) {
						buyerCreditLimit1.setAuthString(auths.get(0).getEnterpriseName());
					}
				}
			}
			CommUtil.saveIPageList2ModelAndView(url + "/user/default/usercenter/buyer_charge_sales_check.html", "", "",
					pList, mv);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "买家-赊销订单", value = "/buyer/buyer_charge_sales_order.htm*", rtype = "buyer", rname = "赊销管理", rcode = "buyer_charge_sales_order", rgroup = "赊销管理")
	@RequestMapping({ "/buyer/buyer_charge_sales_order.htm" })
	public ModelAndView buyerChangeSalesOrder(HttpServletRequest request, HttpServletResponse response,
			String storeName, String order_id, String payTime, Integer order_status,String currentPage) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/buyer_charge_sales_order.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		try {
			String url = this.configService.getSysConfig().getAddress();
			Long userId = SecurityUserHolder.getCurrentUser().getId();
			// 查询当前登录人的赊销信息
			Map map = new HashMap();
			map.put("buyerId", userId.intValue());
			List buyerCreditLimitList = this.buyerCreditLimitService.query(
					"select obj from BuyerCreditLimit obj where obj.buyerId=:buyerId and (obj.zstatus=3 or obj.zstatus=5 or obj.zstatus=7)",
					map, -1, -1);
			if (buyerCreditLimitList.size() > 0) {
				BuyerCreditLimit buyerCreditLimit = (BuyerCreditLimit) buyerCreditLimitList.get(0);
				// 订单列表
				map.clear();
				map.put("userId", Long.parseLong(String.valueOf(buyerCreditLimit.getBuyerId())));
				List<OrderForm> query = orderFormService
						.query("select obj from OrderForm obj where obj.user.id=:userId", map, -1, -1);
				for (OrderForm order : query) {
					Calendar cal = Calendar.getInstance();
					int day = cal.get(Calendar.DATE);// 当天
					Date date1 = buyerCreditLimit.getMonthClean();// 赊销授额结束时间
					int accountDate = buyerCreditLimit.getAccountDate();// 出账日
					int repaymentDate = buyerCreditLimit.getRepaymentDate();// 还款日

					Date date2 = new Date();
					long endMillisecond = date1.getTime();// 赊销结束时间
					long currentTime = date2.getTime();// 当前时间
					Calendar now = Calendar.getInstance();
					int currentDay = now.get(Calendar.HOUR_OF_DAY);// 当前几号
					// 未出账
					if (endMillisecond >= currentTime) {
						order.setStatus(1);

					}

					// 已出账
					if (day >= accountDate && day <= repaymentDate) {
						order.setStatus(2);
					}

					// 已还款不需要修改状态
					if (order.getPay_status() != 2) {
						// 已逾期
						if (currentDay > repaymentDate) {
							order.setPay_status(3);
						}
					}

					this.orderFormService.update(order);
				}
			}
			OrderFormQueryObject qo = new OrderFormQueryObject(currentPage, mv, "addTime", "desc");
			qo.addQuery("obj.user.id", new SysMap("id", userId), "=");

			OrderFormQueryObject qo1 = new OrderFormQueryObject("1", mv, "addTime", "desc");
			qo1.addQuery("obj.user.id", new SysMap("id", userId), "=");
			qo1.addQuery("obj.status", new SysMap("status", 2), "=");
			IPageList pList1 = this.orderFormService.list(qo1);
			if (pList1.getRowCount() != 0) {
				mv.addObject("yichuzhang", pList1.getRowCount());
			} else {
				mv.addObject("yichuzhang", 0);
			}
			OrderFormQueryObject qo2 = new OrderFormQueryObject("1", mv, "addTime", "desc");
			qo2.addQuery("obj.user.id", new SysMap("id", userId), "=");
			qo2.addQuery("obj.status", new SysMap("status", 1), "=");
			IPageList pList2 = this.orderFormService.list(qo2);
			if (pList2.getRowCount() != 0) {
				mv.addObject("weichuzhang", pList2.getRowCount());
			} else {
				mv.addObject("weichuzhang", 0);
			}
			OrderFormQueryObject qo4 = new OrderFormQueryObject("1", mv, "addTime", "desc");
			qo4.addQuery("obj.pay_status", new SysMap("status", 1), "=");
			qo4.addQuery("obj.user.id", new SysMap("id", userId), "=");
			IPageList pList4 = this.orderFormService.list(qo4);
			if (pList4.getRowCount() != 0) {
				mv.addObject("daihuankuan", pList4.getRowCount());
			} else {
				mv.addObject("daihuankuan", 0);
			}
			OrderFormQueryObject qo3 = new OrderFormQueryObject("1", mv, "addTime", "desc");
			qo3.addQuery("obj.pay_status", new SysMap("status", 3), "=");
			qo3.addQuery("obj.user.id", new SysMap("id", userId), "=");
			IPageList pList3 = this.orderFormService.list(qo3);
			if (pList3.getRowCount() != 0) {
				mv.addObject("yiyuqi", pList3.getRowCount());
			} else {
				mv.addObject("yiquqi", 0);
			}
			// 已还款
			OrderFormQueryObject qo5 = new OrderFormQueryObject("1", mv, "addTime", "desc");
			qo5.addQuery("obj.pay_status", new SysMap("pay_status", 2), "=");
			qo5.addQuery("obj.user.id", new SysMap("id", userId), "=");
			IPageList pList5 = this.orderFormService.list(qo5);
			if (pList5.getRowCount() != 0) {
				mv.addObject("yihuankuan", pList5.getRowCount());
			} else {
				mv.addObject("yihuankuan", 0);
			}
			// 交易日期
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (payTime != null && !payTime.equals("")) {
				String arr[] = payTime.split(" - ");
				if (arr.length > 1) {
					for (int j = 0; j < arr.length; j++) {
						Date pay1 = format.parse(new StringBuffer(arr[0]).append(" 00:00:00").toString());
						Date pay2 = format.parse(new StringBuffer(arr[1]).append(" 00:00:00").toString());
						qo.addQuery("obj.payTime", new SysMap("pay1", pay1), ">=");
						qo.addQuery("obj.payTime", new SysMap("pay2", pay2), "<=");
					}
				}

			}

			// 店铺名称
			if (storeName != null && !storeName.equals("")) {
				qo.addQuery("obj.store.store_name", new SysMap("storeName", "%" + storeName + "%"), "like");
			}

			// 订单编号
			if (order_id != null && !order_id.equals("")) {
				qo.addQuery("obj.order_id", new SysMap("order_id", "%" + order_id + "%"), "like");
			}

			// 订单状态
			if (order_status != null && !order_status.equals("")) {
				if (order_status == 10 || order_status == 20 || order_status == 30 || order_status == 40) {
					qo.addQuery("obj.order_status", new SysMap("order_status", order_status), "=");
				}
			}

			IPageList pList = this.orderFormService.list(qo);
			CommUtil.saveIPageList6ModelAndView(url + "", "", "", pList, mv);
			return mv;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "买家-赊销订单-五个列表", value = "/buyer/buyer_charge_sales_order.htm*", rtype = "buyer", rname = "赊销管理", rcode = "buyer_charge_sales_order", rgroup = "赊销管理")
	@RequestMapping({ "/buyer/charge_off_order.htm" })
	public ModelAndView changeOffOrder(HttpServletRequest request, HttpServletResponse response, String storeName,
			String order_id, String payTime, Integer order_status, Integer statusFive, Integer repaymentDate,
			String actual_date, Integer accountDate,String currentPage) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/buyer_charge_sales_order.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		try {
			// statusFive 0 已出账列表 1 未出账列表 2 待还款列表 3 已还款列表 4 已逾期列表
			String url = this.configService.getSysConfig().getAddress();
			Long userId = SecurityUserHolder.getCurrentUser().getId();
			// 未出账列表
			Map params = new HashMap();
			params.put("buyerId", Integer.parseInt(userId.toString()));

			StringBuffer stringBuffer = new StringBuffer(
					"select obj from BuyerCreditLimit obj where obj.buyerId=:buyerId and (obj.zstatus=3 or obj.zstatus=5 or obj.zstatus=6)");

			// 出账日
			if (accountDate != null && !accountDate.equals("")) {
				// sql拼接
				stringBuffer.append(" and obj.accountDate=:accountDate ");
				params.put("accountDate", accountDate);
			}

			// 最后还款日
			if (repaymentDate != null && !repaymentDate.equals("")) {
				stringBuffer.append(" and obj.repaymentDate=:repaymentDate ");
				params.put("repaymentDate", repaymentDate);
			}
			List<BuyerCreditLimit> buyerCreditLimit = buyerCreditLimitService.query(stringBuffer.toString(), params, -1,
					-1);

			if (buyerCreditLimit.size() > 0) {
				// 取第一条数据
				BuyerCreditLimit buyerCreditLimi = buyerCreditLimit.get(0);
				Double interest = buyerCreditLimi.getInterest();
				Calendar now = Calendar.getInstance();
				int currentDay = now.get(Calendar.DATE);// 当前几号
				int repaymentDate2 = buyerCreditLimi.getRepaymentDate();
				int overdueTime = currentDay - repaymentDate2;
				mv.addObject("overdueTime", overdueTime);// 逾期时长
				// mv.addObject("accountDate", buyerCreditLimi.getAccountDate());// 出账日
				// mv.addObject("repaymentDate", buyerCreditLimi.getRepaymentDate());// 最后还款日
				mv.addObject("interest", buyerCreditLimi.getInterest());// 计息
				OrderFormQueryObject qo = new OrderFormQueryObject(currentPage, mv, "addTime", "desc");
				qo.addQuery("obj.user.id", new SysMap("id", Long.valueOf(buyerCreditLimi.getBuyerId())), "=");

				// 已出账
				if (statusFive == 0) {
					qo.addQuery("obj.status", new SysMap("status", 2), "=");
				}
				OrderFormQueryObject qo1 = new OrderFormQueryObject("1", mv, "addTime", "desc");
				qo1.addQuery("obj.status", new SysMap("status", 2), "=");
				qo1.addQuery("obj.user.id", new SysMap("id", Long.valueOf(buyerCreditLimi.getBuyerId())), "=");
				IPageList pList1 = this.orderFormService.list(qo1);
				if (pList1.getRowCount() != 0) {
					mv.addObject("yichuzhang", pList1.getRowCount());
				} else {
					mv.addObject("yichuzhang", 0);
				}

				// 未出账
				if (statusFive == 1) {
					qo.addQuery("obj.status", new SysMap("status", 1), "=");
				}
				OrderFormQueryObject qo2 = new OrderFormQueryObject("1", mv, "addTime", "desc");
				qo2.addQuery("obj.status", new SysMap("status", 1), "=");
				qo2.addQuery("obj.user.id", new SysMap("id", Long.valueOf(buyerCreditLimi.getBuyerId())), "=");
				IPageList pList2 = this.orderFormService.list(qo2);
				if (pList2.getRowCount() != 0) {
					mv.addObject("weichuzhang", pList2.getRowCount());
				} else {
					mv.addObject("weichuzhang", 0);
				}

				// 待还款
				if (statusFive == 2) {
					qo.addQuery("obj.pay_status", new SysMap("pay_status", 1), "=");
				}
				OrderFormQueryObject qo3 = new OrderFormQueryObject("1", mv, "addTime", "desc");
				qo3.addQuery("obj.pay_status", new SysMap("pay_status", 1), "=");
				qo3.addQuery("obj.user.id", new SysMap("id", Long.valueOf(buyerCreditLimi.getBuyerId())), "=");
				IPageList pList4 = this.orderFormService.list(qo3);
				if (pList4.getRowCount() != 0) {
					mv.addObject("daihuankuan", pList4.getRowCount());
				} else {
					mv.addObject("daihuankuan", 0);
				}

				// 已还款
				if (statusFive == 3) {
					qo.addQuery("obj.pay_status", new SysMap("pay_status", 2), "=");
				}
				OrderFormQueryObject qo4 = new OrderFormQueryObject("1", mv, "addTime", "desc");
				qo4.addQuery("obj.pay_status", new SysMap("pay_status", 2), "=");
				qo4.addQuery("obj.user.id", new SysMap("id", Long.valueOf(buyerCreditLimi.getBuyerId())), "=");
				IPageList pList5 = this.orderFormService.list(qo4);
				if (pList5.getRowCount() != 0) {
					mv.addObject("yihuankuan", pList5.getRowCount());
				} else {
					mv.addObject("yihunakuan", 0);
				}

				// 已逾期
				if (statusFive == 4) {
					qo.addQuery("obj.pay_status", new SysMap("pay_status", 3), "=");
				}
				OrderFormQueryObject qo5 = new OrderFormQueryObject("1", mv, "addTime", "desc");
				qo5.addQuery("obj.pay_status", new SysMap("status", 3), "=");
				qo5.addQuery("obj.user.id", new SysMap("id", Long.valueOf(buyerCreditLimi.getBuyerId())), "=");
				IPageList pList3 = this.orderFormService.list(qo5);
				if (pList3.getRowCount() != 0) {
					mv.addObject("yiyuqi", pList3.getRowCount());
				} else {
					mv.addObject("yiquqi", 0);
				}

				// 交易日期
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				if (payTime != null && !payTime.equals("")) {
					String arr[] = payTime.split(" - ");
					if (arr.length > 1) {
						for (int j = 0; j < arr.length; j++) {
							Date pay1 = format.parse(new StringBuffer(arr[0]).append(" 00:00:00").toString());
							Date pay2 = format.parse(new StringBuffer(arr[1]).append(" 00:00:00").toString());
							qo.addQuery("obj.payTime", new SysMap("pay1", pay1), ">=");
							qo.addQuery("obj.payTime", new SysMap("pay2", pay2), "<=");
						}
					}

				}
				// 实际回款日期
				if (actual_date != null && !actual_date.equals("")) {
					String arr[] = actual_date.split(" - ");
					if (arr.length > 1) {
						for (int j = 0; j < arr.length; j++) {
							Date finish1 = format.parse(new StringBuffer(arr[0]).append(" 00:00:00").toString());
							Date finish2 = format.parse(new StringBuffer(arr[1]).append(" 00:00:00").toString());
							qo.addQuery("obj.actual_date", new SysMap("finish1", finish1), ">=");
							qo.addQuery("obj.actual_date", new SysMap("finish2", finish2), "<=");
						}
					}

				}
				// 店铺名称
				if (storeName != null && !storeName.equals("")) {
					qo.addQuery("obj.store.store_name", new SysMap("storeName", "%" + storeName + "%"), "like");
				}

				// 订单编号
				if (order_id != null && !order_id.equals("")) {
					qo.addQuery("obj.order_id", new SysMap("order_id", "%" + order_id + "%"), "like");
				}
				IPageList pList = this.orderFormService.list(qo);
				if (statusFive == 4 || statusFive == 3) {
					List<OrderForm> OrderForms = pList.getResult();
					if (OrderForms != null) {
					for (OrderForm orderForm : OrderForms) {
						// 获取当前日期
						Date date = new Date();
						SimpleDateFormat formats = new SimpleDateFormat("yyyy-MM-dd");
						String formatDate = formats.format(orderForm.getPayTime());
						String nowdates = formats.format(date);
						StringBuffer dates = new StringBuffer(formatDate);
						StringBuffer datess = new StringBuffer(formatDate);
						mv.addObject("dates", dates);
						// 拼接出最后还款日
						StringBuffer replace = dates.replace(8, 10, String.valueOf(buyerCreditLimi.getRepaymentDate()));
						StringBuffer account = dates.replace(8, 10, String.valueOf(buyerCreditLimi.getAccountDate()));
						// 如果还款日的月份与当前日期的月份相等
						if (new StringBuffer(orderForm.getPayTime().toString()).subSequence(5, 7)
								.equals(replace.subSequence(5, 7))) {
							int month = Integer.parseInt(dates.subSequence(5, 7).toString());
							// 最后还款日月份+1
							replace = dates.replace(8, 10, String.valueOf(buyerCreditLimi.getRepaymentDate()))
									.replace(5, 7, String.valueOf(month + 1));
							account = datess.replace(8, 10, String.valueOf(buyerCreditLimi.getAccountDate())).replace(5,
									7, String.valueOf(month + 1));
						}
						// 拼接结果的最后还款日
						mv.addObject("repaymentDate", replace);
						mv.addObject("accountDate", account);
						if (orderForm.getActual_date() != null) {
							long date1 = formats.parse(orderForm.getActual_date().toString()).getTime()
									- formats.parse(replace.toString()).getTime();
							if (date1 < 0) {
								orderForm.setDays(0);
							} else {
								orderForm.setDays(date1 / (1000 * 60 * 60 * 24));
							}
						} else {
							long date2 = formats.parse(nowdates.toString()).getTime()
									- formats.parse(replace.toString()).getTime();
							if (date2 > 0) {
								orderForm.setDays(date2 / (1000 * 60 * 60 * 24));
							} else {
								orderForm.setDays(0);
							}
						}
					}
				}

				}
				CommUtil.saveIPageList2ModelAndView(url + "", "", "", pList, mv);
				return mv;

			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "买家-赊销钱包", value = "/buyer/buyer_charge_sales_wallet.htm*", rtype = "buyer", rname = "赊销管理", rcode = "buyer_charge_sales_wallet", rgroup = "赊销管理")
	@RequestMapping({ "/buyer/buyer_charge_sales_wallet.htm" })
	public ModelAndView buyerChangeSalesWallet(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/buyer_charge_sales_wallet.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		try {
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "买家-添加赊销钱包", value = "/buyer/buyer_charge_sales_wallet_add.htm*", rtype = "buyer", rname = "赊销管理", rcode = "buyer_charge_sales_wallet_add", rgroup = "赊销管理")
	@RequestMapping({ "/buyer/buyer_charge_sales_wallet_add.htm" })
	public ModelAndView buyerChangeSalesWalletAdd(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/buyer_charge_sales_wallet_add.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		try {
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return mv;
	}
}
