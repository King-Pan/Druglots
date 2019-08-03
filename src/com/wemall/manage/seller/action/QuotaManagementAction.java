package com.wemall.manage.seller.action;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.Buffer;
import java.text.DateFormat;
import java.text.DecimalFormat;
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
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.wemall.core.annotation.SecurityMapping;
import com.wemall.core.domain.virtual.SysMap;
import com.wemall.core.mv.JModelAndView;
import com.wemall.core.query.support.IPageList;
import com.wemall.core.security.support.SecurityUserHolder;
import com.wemall.core.tools.CommUtil;
import com.wemall.core.tools.ExportExcelSeedBack;
import com.wemall.core.tools.FileUtils;
import com.wemall.core.tools.HttpClass;
import com.wemall.foundation.domain.Authentication;
import com.wemall.foundation.domain.BuyerCreditLimit;
import com.wemall.foundation.domain.CreditLlineManagement;
import com.wemall.foundation.domain.Message;
import com.wemall.foundation.domain.OrderForm;
import com.wemall.foundation.domain.ProcurementRelationship;
import com.wemall.foundation.domain.Store;
import com.wemall.foundation.domain.User;
import com.wemall.foundation.domain.query.BuyerCreditLimitQueryObject;
import com.wemall.foundation.domain.query.OrderFormQueryObject;
import com.wemall.foundation.domain.query.ProcurementRelationshipQueryObject;
import com.wemall.foundation.service.AuthenticationService;
import com.wemall.foundation.service.BuyerCreditLimitService;
import com.wemall.foundation.service.CreditLlineManagementService;
import com.wemall.foundation.service.IMessageService;
import com.wemall.foundation.service.IOrderFormService;
import com.wemall.foundation.service.ISysConfigService;
import com.wemall.foundation.service.IUserConfigService;
import com.wemall.foundation.service.IUserService;
import com.wemall.foundation.service.ProcurementRelationshipService;
import com.wemall.manage.seller.tools.CodeUtil;

/**
 * 卖家中心-> 额度管理
 */
@Controller
public class QuotaManagementAction {

	@Autowired
	private ISysConfigService configService;

	@Autowired
	private IUserConfigService userConfigService;

	@Autowired
	private ProcurementRelationshipService procurementRelationshipService;

	@Autowired
	private CreditLlineManagementService creditLlineManagementService;

	@Autowired
	private BuyerCreditLimitService buyerCreditLimitService;

	@Autowired
	private IUserService userService;

	@Autowired
	private AuthenticationService authenticationservice;

	@Autowired
	private IMessageService messageService;

	@Autowired
	private IOrderFormService orderFormService;

	@SecurityMapping(display = false, rsequence = 0, title = "赊销账期及额度", value = "/seller/quotaManagement.htm*", rtype = "seller", rname = "额度管理", rcode = "quotaManagement_seller", rgroup = "额度管理")
	@RequestMapping({ "/seller/quotaManagement.htm" })
	public ModelAndView quotaManagement(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/quotaManagement.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);

		Long userId = SecurityUserHolder.getCurrentUser().getId();
		String url = this.configService.getSysConfig().getAddress();
		try {
			Map params = new HashMap();
			params.put("sellerId", userId.intValue());
			List creditLlineManagementList = this.creditLlineManagementService
					.query("select obj from CreditLlineManagement obj where obj.sellerId=:sellerId", params, -1, -1);
			CreditLlineManagement creditLlineManagement = (CreditLlineManagement) creditLlineManagementList.get(0);
			mv.addObject("creditLlineManagement", creditLlineManagement);
			/*
			 * CreditLlineManagementQueryObject qo = new
			 * CreditLlineManagementQueryObject("1", mv,"addTime", "desc");
			 * qo.addQuery("obj.sellerId",new SysMap("sellerId",userId), "=");
			 * qo.setOrderBy("addTime"); qo.setOrderType("desc"); IPageList pList =
			 * this.creditLlineManagementService.list(qo);
			 * CommUtil.saveIPageList2ModelAndView(url
			 * +"/user/default/usercenter/quotaManagement.html","", "", pList, mv);
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "查询客户赊销额度", value = "/seller/is_charge_sales.htm*", rtype = "seller", rname = "赊销管理", rcode = "is_charge_sales", rgroup = "赊销管理")
	@RequestMapping({ "/seller/is_charge_sales.htm" })
	public ModelAndView is_charge_sales(HttpServletRequest request, HttpServletResponse response, String enterpriseName,
			String startTime) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/is_charge_sales.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		try {
			HttpClass hc = new HttpClass();
			String userName = SecurityUserHolder.getCurrentUser().getUserName();
			Long userId = SecurityUserHolder.getCurrentUser().getId();
			String load = hc.load("http://127.0.0.1:8081/ssm_project/selStatus", "userName=" + userName);
			String url = this.configService.getSysConfig().getAddress();
			mv.addObject("storstatus", load);
			System.out.println(load.indexOf("1"));
			if (load != null && load.indexOf("1") != -1) {
				mv = new JModelAndView("user/default/usercenter/my_charge_sales.html",
						this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
						response);

				Store store = SecurityUserHolder.getCurrentUser().getStore();
				int storeId = store.getId().intValue();

				BuyerCreditLimitQueryObject qo = new BuyerCreditLimitQueryObject("1", mv, "addTime", "desc");
				qo.addQuery("obj.storeId", new SysMap("storeId", storeId), "=");
				qo.addQuery("obj.zstatus", new SysMap("zstatus", 3), "=");

				// 授额期限
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				if (startTime != null && !startTime.equals("")) {
					String arr[] = startTime.split(" - ");
					if (arr.length > 1) {
						for (int j = 0; j < arr.length; j++) {

							Date startTime1 = format.parse(new StringBuffer(arr[0]).append(" 00:00:00").toString());
							Date monthClean = format.parse(new StringBuffer(arr[1]).append(" 00:00:00").toString());
							qo.addQuery("obj.startTime", new SysMap("startTime", startTime1), ">=");
							qo.addQuery("obj.monthClean", new SysMap("monthClean", monthClean), "<=");
						}
					}
				}

				qo.setOrderBy("addTime");
				qo.setOrderType("desc");
				IPageList pList = this.buyerCreditLimitService.list(qo);
				List<BuyerCreditLimit> buyerCreditLimits = pList.getResult();
				Map map = new HashMap();
				double i = 0;
				double j = 0;
				if (buyerCreditLimits != null && buyerCreditLimits.size() > 0) {
					for (BuyerCreditLimit buyerCreditLimit : buyerCreditLimits) {
						map.clear();
						map.put("storeId", (long) storeId);
						map.put("userId", Long.parseLong(String.valueOf(buyerCreditLimit.getBuyerId())));
						map.put("order_status", 20);
						List<OrderForm> query = orderFormService.query(
								"select obj from OrderForm obj where obj.store.id=:storeId and obj.user.id=:userId and obj.order_status=:order_status",
								map, -1, -1);
						buyerCreditLimit.setOrderCount(query.size());
						map.clear();
						map.put("userName", buyerCreditLimit.getBuyerName());
						// 客户名称
						if (enterpriseName != null && !enterpriseName.equals("")) {
							map.put("enterpriseName", "%" + enterpriseName + "%");
						} else {
							map.put("enterpriseName", "%%");
						}
						List<Authentication> auths = this.authenticationservice.query(
								"select obj from Authentication obj where obj.userName=:userName and obj.enterpriseName like :enterpriseName",
								map, -1, -1);
						if (auths.size() > 0) {
							buyerCreditLimit.setAuthString(auths.get(0).getEnterpriseName());
							// i=i+Integer.parseInt(buyerCreditLimit.getBuyerRemainingUndrawn());
							j = j + Double.parseDouble(buyerCreditLimit.getBuyerCombination());
							// 已用的额度 = 总额度- 剩余额度
							i = i + Double.parseDouble(buyerCreditLimit.getBuyerCombination())
									- Double.parseDouble(buyerCreditLimit.getBuyerRemainingUndrawn());
						}
					}
					mv.addObject("total", j);
					mv.addObject("shenyu", i);
				}

				// 如果查询条件包含时间或状态不为通过，即只显示赊销数据
				if (startTime != null && !startTime.equals("")) {
					CommUtil.saveIPageList2ModelAndView(url + "/user/default/usercenter/addUpdate_customerCredit.html",
							"", "", pList, mv);
					return mv;
				}

				ProcurementRelationshipQueryObject qo1 = new ProcurementRelationshipQueryObject("1", mv, "addTime",
						"desc");
				qo1.addQuery("obj.shopkeeperId", new SysMap("shopkeeperId", String.valueOf(userId)), "=");

				qo1.setOrderBy("auditAddTime");
				qo1.setOrderType("desc");
				IPageList pList1 = this.procurementRelationshipService.list(qo1);
				List<ProcurementRelationship> procurementRelationships = pList1.getResult();
				double m = 0;
				double n = 0;
				if (procurementRelationships != null && procurementRelationships.size() > 0) {
					for (ProcurementRelationship procurementRelationship : procurementRelationships) {
						map.clear();
						map.put("storeId", (long) storeId);
						map.put("userId", Long.parseLong(procurementRelationship.getBuyerId()));
						map.put("order_status", 20);
						List<OrderForm> query = orderFormService.query(
								"select obj from OrderForm obj where obj.store.id=:storeId and obj.user.id=:userId and obj.order_status=:order_status",
								map, -1, -1);
						procurementRelationship.setOrderCount(query.size());
						map.clear();
						map.put("userName", procurementRelationship.getBuyerName());
						// 客户名称
						if (enterpriseName != null && !enterpriseName.equals("")) {
							map.put("enterpriseName", "%" + enterpriseName + "%");
						} else {
							map.put("enterpriseName", "%%");
						}
						List<Authentication> auths = this.authenticationservice.query(
								"select obj from Authentication obj where obj.userName=:userName and obj.enterpriseName like :enterpriseName",
								map, -1, -1);
						if (auths.size() > 0) {
							procurementRelationship.setAuth(auths.get(0).getEnterpriseName());
						}

						if (buyerCreditLimits != null && buyerCreditLimits.size() > 0) {
							for (BuyerCreditLimit buyerCreditLimit : buyerCreditLimits) {
								if (buyerCreditLimit.getBuyerId() == Integer
										.parseInt(procurementRelationship.getBuyerId())) {
									procurementRelationship.setBuyamout(buyerCreditLimit.getBuyerCombination());
									procurementRelationship.setAccountDate(buyerCreditLimit.getAccountDate());
									procurementRelationship.setRepaymentDate(buyerCreditLimit.getRepaymentDate());
									procurementRelationship.setInterest(buyerCreditLimit.getInterest());
									procurementRelationship.setStartTime(buyerCreditLimit.getStartTime());
									procurementRelationship.setMonthClean(buyerCreditLimit.getMonthClean());
									procurementRelationship.setLimitState(buyerCreditLimit.getLimitState());
									procurementRelationship.setSxid(buyerCreditLimit.getId());
									// 已用的额度 = 原总额 - 剩余额(待回款总额)
									m = m + Double.parseDouble(buyerCreditLimit.getBuyerCombination())
											- Double.parseDouble(buyerCreditLimit.getBuyerRemainingUndrawn());
									n = n + Double.parseDouble(buyerCreditLimit.getBuyerCombination());
								}
								// 查询采购
								ProcurementRelationship relationship = this.procurementRelationshipService
										.getObjById(procurementRelationship.getId());

								if (relationship.getMonthClean() != null) {
									if (relationship.getStoreName() == buyerCreditLimit.getStoreName()) {
										Date date1 = relationship.getMonthClean();// 赊销结束时间
										Date date2 = new Date();
										Calendar now = Calendar.getInstance();
										Integer repaymentDate = relationship.getRepaymentDate();// 每月还款日
										int currentDay = now.get(Calendar.HOUR_OF_DAY);// 当前几号
										long endMillisecond = date1.getTime();// 赊销结束时间
										long currentTime = date2.getTime();// 当前时间
										if (endMillisecond >= currentTime) {
											buyerCreditLimit.setLimitState(1);// 进行中

										}
										if (endMillisecond < currentTime) {
											buyerCreditLimit.setLimitState(2);// 已到期
										}

										map.clear();
										map.put("storeId", (long) storeId);
										map.put("userId",
												Long.parseLong(String.valueOf(buyerCreditLimit.getBuyerId())));
										map.put("pay_status", 3);
										List<OrderForm> query1 = orderFormService.query(
												"select obj from OrderForm obj where obj.store.id=:storeId and obj.user.id=:userId and obj.pay_status=:pay_status",
												map, -1, -1);
										if (query1.size() > 0) {
											buyerCreditLimit.setLimitState(3);// 已冻结
										}

										buyerCreditLimitService.update(buyerCreditLimit);
									}
								}
							}
						}
						mv.addObject("total", n);
						mv.addObject("shenyu", m);
					}
				}
				OrderFormQueryObject orderqo = new OrderFormQueryObject("1", mv, "addTime", "desc");
				orderqo.addQuery("obj.store.id", new SysMap("storeId", Long.valueOf(storeId)), "=");
				orderqo.addQuery("obj.bill_state", new SysMap("billState", 1), "=");
				IPageList orderList = this.orderFormService.list(orderqo);
				List<OrderForm> orderForms = orderList.getResult();
				double totalprice = 0;
				if (orderForms != null && orderForms.size() > 0) {
					for (OrderForm orderForm : orderForms) {
						totalprice = totalprice + orderForm.getTotalPrice().doubleValue();
					}
				}
				mv.addObject("totalprice", m - totalprice);
				IPageList pList2 = this.procurementRelationshipService.list(qo1);
				CommUtil.saveIPageList2ModelAndView(url + "/user/default/usercenter/addUpdate_customerCredit.html", "",
						"", pList2, mv);
				return mv;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}

	String enterpriseName1 = "";
	String time = "";
	Integer state = 0;

	@SecurityMapping(display = false, rsequence = 0, title = "商家中心-赊销管理-我的赊销", value = "/seller/my_charge_sales.htm*", rtype = "buyer", rname = "赊销管理", rcode = "my_charge_sales", rgroup = "赊销管理")
	@RequestMapping({ "/seller/my_charge_sales.htm" })
	public ModelAndView myChangeSales(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String enterpriseName, String startTime, Integer limitState) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/my_charge_sales.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		try {
			HttpClass hc = new HttpClass();
			String userName = SecurityUserHolder.getCurrentUser().getUserName();
			Long userId = SecurityUserHolder.getCurrentUser().getId();
			String load = hc.load("http://127.0.0.1:8081/ssm_project/selStatus", "userName=" + userName);
			String url = this.configService.getSysConfig().getAddress();
			mv.addObject("storstatus", load);
			System.out.println(load.indexOf("1"));
			if (load != null && load.indexOf("1") != -1) {
				mv = new JModelAndView("user/default/usercenter/my_charge_sales.html",
						this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
						response);

				Store store = SecurityUserHolder.getCurrentUser().getStore();
				int storeId = store.getId().intValue();

				BuyerCreditLimitQueryObject qo = new BuyerCreditLimitQueryObject(currentPage, mv, "addTime", "desc");
				qo.addQuery("obj.storeId", new SysMap("storeId", storeId), "=");
				qo.addQuery("obj.zstatus", new SysMap("zstatus", 3), "=");

				// 授额期限
				time = startTime;
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				if (startTime != null && !startTime.equals("")) {
					String arr[] = startTime.split(" - ");
					if (arr.length > 1) {
						for (int j = 0; j < arr.length; j++) {

							Date startTime1 = format.parse(new StringBuffer(arr[0]).append(" 00:00:00").toString());
							Date monthClean = format.parse(new StringBuffer(arr[1]).append(" 00:00:00").toString());
							qo.addQuery("obj.startTime", new SysMap("startTime", startTime1), ">=");
							qo.addQuery("obj.monthClean", new SysMap("monthClean", monthClean), "<=");
						}
					}

				}
				// 根据授额状态查询
				state = limitState;
				if (limitState != null && !limitState.equals("")) {
					if (limitState == 2 || limitState == 3 || limitState == 1) {
						qo.addQuery("obj.limitState", new SysMap("limitState", limitState), "=");
					}
				}

				qo.setOrderBy("addTime");
				qo.setOrderType("desc");
				IPageList pList = this.buyerCreditLimitService.list(qo);
				List<BuyerCreditLimit> buyerCreditLimits = pList.getResult();
				Map map = new HashMap();
				double i = 0;
				double j = 0;
				if (buyerCreditLimits != null && buyerCreditLimits.size() > 0) {
					for (BuyerCreditLimit buyerCreditLimit : buyerCreditLimits) {
						map.clear();
						map.put("storeId", (long) storeId);
						map.put("userId", Long.parseLong(String.valueOf(buyerCreditLimit.getBuyerId())));
						map.put("order_status", 20);
						List<OrderForm> query = orderFormService.query(
								"select obj from OrderForm obj where obj.store.id=:storeId and obj.user.id=:userId and obj.order_status=:order_status",
								map, -1, -1);
						buyerCreditLimit.setOrderCount(query.size());
						map.clear();
						map.put("userName", buyerCreditLimit.getBuyerName());
						// 客户名称
						if (enterpriseName != null && !enterpriseName.equals("")) {
							map.put("enterpriseName", "%" + enterpriseName + "%");
						} else {
							map.put("enterpriseName", "%%");
						}
						List<Authentication> auths = this.authenticationservice.query(
								"select obj from Authentication obj where obj.userName=:userName and obj.enterpriseName like :enterpriseName",
								map, -1, -1);
						if (auths.size() > 0) {
							buyerCreditLimit.setAuthString(auths.get(0).getEnterpriseName());
							// i=i+Integer.parseInt(buyerCreditLimit.getBuyerRemainingUndrawn());
							j = j + Double.parseDouble(buyerCreditLimit.getBuyerCombination());
							// 已用的额度 = 总额度- 剩余额度
							i = i + Double.parseDouble(buyerCreditLimit.getBuyerCombination())
									- Double.parseDouble(buyerCreditLimit.getBuyerRemainingUndrawn());
						}
					}
					mv.addObject("total", j);
					mv.addObject("shenyu", i);
				}

				// 如果查询条件包含时间或状态不为通过，即只显示赊销数据
				if (startTime != null && !startTime.equals("")) {
					CommUtil.saveIPageList2ModelAndView(url + "/user/default/usercenter/addUpdate_customerCredit.html",
							"", "", pList, mv);
					return mv;
				} else if (limitState != null && !limitState.equals("")) {
					if (limitState == 2 || limitState == 3 || limitState == 1) {
						CommUtil.saveIPageList2ModelAndView(
								url + "/user/default/usercenter/addUpdate_customerCredit.html", "", "", pList, mv);
						return mv;
					}
				}

				ProcurementRelationshipQueryObject qo1 = new ProcurementRelationshipQueryObject(currentPage, mv,
						"auditAddTime", "desc");
				qo1.addQuery("obj.shopkeeperId", new SysMap("shopkeeperId", String.valueOf(userId)), "=");
				map.clear();
				map.put("shopkeeperId", String.valueOf(userId));
				List<ProcurementRelationship> procurementRelationships = this.procurementRelationshipService.query(
						"select obj from ProcurementRelationship obj where obj.shopkeeperId=:shopkeeperId ", map, -1,
						-1);
				System.out.println(procurementRelationships.size());
				double m = 0;
				double n = 0;
				if (procurementRelationships != null || procurementRelationships.size() > 0) {
					for (ProcurementRelationship procurementRelationship : procurementRelationships) {
						map.clear();
						map.put("storeId", (long) storeId);
						map.put("userId", Long.parseLong(procurementRelationship.getBuyerId()));
						map.put("order_status", 20);
						List<OrderForm> query = orderFormService.query(
								"select obj from OrderForm obj where obj.store.id=:storeId and obj.user.id=:userId and obj.order_status=:order_status",
								map, -1, -1);
						procurementRelationship.setOrderCount(query.size());
						map.clear();
						map.put("userName", procurementRelationship.getBuyerName());
						// 客户名称
						enterpriseName1 = enterpriseName;
						if (!CommUtil.null2String(enterpriseName1).equals("")) {
							map.put("enterpriseName", "%" + enterpriseName + "%");
						} else {
							map.put("enterpriseName", "%%");
						}
						List<Authentication> auths = this.authenticationservice.query(
								"select obj from Authentication obj where obj.userName=:userName and obj.enterpriseName like :enterpriseName",
								map, -1, -1);
						if (auths.size() > 0) {
							procurementRelationship.setAuth(auths.get(0).getEnterpriseName());
						}
						if (buyerCreditLimits != null && buyerCreditLimits.size() > 0) {
							for (BuyerCreditLimit buyerCreditLimit : buyerCreditLimits) {
								if (buyerCreditLimit.getBuyerId() == Integer
										.parseInt(procurementRelationship.getBuyerId())) {
									procurementRelationship.setBuyamout(buyerCreditLimit.getBuyerCombination());
									procurementRelationship.setAccountDate(buyerCreditLimit.getAccountDate());
									procurementRelationship.setRepaymentDate(buyerCreditLimit.getRepaymentDate());
									procurementRelationship.setInterest(buyerCreditLimit.getInterest());
									procurementRelationship.setStartTime(buyerCreditLimit.getStartTime());
									procurementRelationship.setMonthClean(buyerCreditLimit.getMonthClean());
									procurementRelationship.setLimitState(buyerCreditLimit.getLimitState());
									procurementRelationship.setSxid(buyerCreditLimit.getId());
									// 已用的额度 = 原总额 - 剩余额
									m = m + Double.parseDouble(buyerCreditLimit.getBuyerCombination())
											- Double.parseDouble(buyerCreditLimit.getBuyerRemainingUndrawn());
									n = n + Double.parseDouble(buyerCreditLimit.getBuyerCombination());
									if (limitState != null && !limitState.equals("")) {
										if (limitState == 4) {
											procurementRelationship.setAuth(null);
										}
									}
								}
								// 查询采购
								ProcurementRelationship relationship = this.procurementRelationshipService
										.getObjById(procurementRelationship.getId());

								if (relationship.getMonthClean() != null) {
									if (relationship.getStoreName() == buyerCreditLimit.getStoreName()) {
										Date date1 = relationship.getMonthClean();// 赊销结束时间
										Date time2 = relationship.getStartTime();// 赊销开始时间
										Date date2 = new Date();
										long endMillisecond = date1.getTime();// 赊销结束时间
										long starTime = time2.getTime();
										long currentTime = date2.getTime();// 当前时间
										if (endMillisecond >= currentTime) {
											buyerCreditLimit.setLimitState(1);// 进行中

										}
										if (endMillisecond < currentTime) {
											buyerCreditLimit.setLimitState(2);// 已到期
										}
										map.clear();
										map.put("storeId", (long) storeId);
										map.put("userId",
												Long.parseLong(String.valueOf(buyerCreditLimit.getBuyerId())));
										map.put("pay_status", 3);
										List<OrderForm> query1 = orderFormService.query(
												"select obj from OrderForm obj where obj.store.id=:storeId and obj.user.id=:userId and obj.pay_status=:pay_status",
												map, -1, -1);
										if (query1.size() > 0) {
											buyerCreditLimit.setLimitState(3);// 已冻结
										}

										// if (starTime > currentTime) {
										// buyerCreditLimit.setLimitState(4);// 待授额
										// }

										buyerCreditLimitService.update(buyerCreditLimit);
									}
								}
							}
						}
						mv.addObject("total", n);
						mv.addObject("shenyu", m);
					}
				}
				OrderFormQueryObject orderqo = new OrderFormQueryObject("1", mv, "addTime", "desc");
				orderqo.addQuery("obj.store.id", new SysMap("storeId", Long.valueOf(storeId)), "=");
				orderqo.addQuery("obj.bill_state", new SysMap("billState", 1), "=");
				IPageList orderList = this.orderFormService.list(orderqo);
				List<OrderForm> orderForms = orderList.getResult();
				double totalprice = 0;
				if (orderForms != null && orderForms.size() > 0) {
					for (OrderForm orderForm : orderForms) {
						totalprice = totalprice + orderForm.getTotalPrice().doubleValue();
					}
				}
				mv.addObject("totalprice", m - totalprice);
				IPageList pList2 = this.procurementRelationshipService.list(qo1);
				CommUtil.saveIPageList2ModelAndView(url + "/user/default/usercenter/addUpdate_customerCredit.html", "",
						"", pList2, mv);
				return mv;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "商家中心-赊销管理-导出我的赊销列表", value = "/seller/my_charge_sales.htm*", rtype = "buyer", rname = "赊销管理", rcode = "my_charge_sales", rgroup = "赊销管理")
	@RequestMapping({ "/seller/export.htm" })
	public void thirdTradExcel(HttpServletResponse response, HttpServletRequest request) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/my_charge_sales.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		try {
			HttpClass hc = new HttpClass();
			String userName = SecurityUserHolder.getCurrentUser().getUserName();
			Long userId = SecurityUserHolder.getCurrentUser().getId();
			String load = hc.load("http://127.0.0.1:8081/ssm_project/selStatus", "userName=" + userName);
			mv.addObject("storstatus", load);
			if (load != null && load.indexOf("1") != -1) {
				mv = new JModelAndView("user/default/usercenter/my_charge_sales.html",
						this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
						response);

				Store store = SecurityUserHolder.getCurrentUser().getStore();
				int storeId = store.getId().intValue();
				String url = this.configService.getSysConfig().getAddress();
				// 查询赊销状态通过的集合
				BuyerCreditLimitQueryObject qo = new BuyerCreditLimitQueryObject("1", mv, "addTime", "desc");
				qo.addQuery("obj.storeId", new SysMap("storeId", storeId), "=");
				qo.addQuery("obj.zstatus", new SysMap("zstatus", 3), "=");

				// 授额期限
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				if (time != null && !time.equals("")) {
					String arr[] = time.split(" - ");
					if (arr.length > 1) {
						for (int j = 0; j < arr.length; j++) {

							Date startTime1 = format.parse(new StringBuffer(arr[0]).append(" 00:00:00").toString());
							Date monthClean = format.parse(new StringBuffer(arr[1]).append(" 00:00:00").toString());
							qo.addQuery("obj.startTime", new SysMap("startTime", startTime1), ">=");
							qo.addQuery("obj.monthClean", new SysMap("monthClean", monthClean), "<=");
						}
					}

				}
				// 根据授额状态查询
				if (state != null && !state.equals("")) {
					if (state == 2 || state == 3 || state == 1) {
						qo.addQuery("obj.limitState", new SysMap("limitState", state), "=");
					}
				}
				qo.setOrderBy("addTime");
				qo.setOrderType("desc");
				IPageList pList = this.buyerCreditLimitService.list(qo);

				// 查询与当前商家建立采购关系的买家
				ProcurementRelationshipQueryObject qo1 = new ProcurementRelationshipQueryObject("1", mv, "addTime",
						"desc");
				qo1.addQuery("obj.shopkeeperId", new SysMap("shopkeeperId", String.valueOf(userId)), "=");
				qo1.setOrderBy("auditAddTime");
				qo1.setOrderType("desc");
				IPageList pList1 = this.procurementRelationshipService.list(qo1);

				// 赊销集合
				List<BuyerCreditLimit> buyerCreditLimits = pList.getResult();
				// 采购集合
				List<ProcurementRelationship> procurementRelationships = pList1.getResult();

				// 得到查询结果遍历得到买家名称，去首营认证表去获取企业名称，
				if (procurementRelationships != null || procurementRelationships.size() > 0) {
					for (ProcurementRelationship procurementRelationship : procurementRelationships) {
						Map map = new HashMap();
						map.clear();
						map.put("userName", procurementRelationship.getBuyerName());
						// 客户名称
						if (enterpriseName1 != null && !enterpriseName1.equals("")) {
							map.put("enterpriseName", "%" + enterpriseName1 + "%");
						} else {
							map.put("enterpriseName", "%%");
						}
						List<Authentication> auths = this.authenticationservice.query(
								"select obj from Authentication obj where obj.userName=:userName and obj.enterpriseName like :enterpriseName",
								map, -1, -1);
						if (auths.size() > 0) {
							procurementRelationship.setAuth(auths.get(0).getEnterpriseName());
						}
						if (buyerCreditLimits != null || buyerCreditLimits.size() > 0) {
							for (BuyerCreditLimit buyerCreditLimit : buyerCreditLimits) {
								if (buyerCreditLimit.getBuyerId() == Integer
										.parseInt(procurementRelationship.getBuyerId())) {
									procurementRelationship.setBuyamout(buyerCreditLimit.getBuyerCombination());
									procurementRelationship.setAccountDate(buyerCreditLimit.getAccountDate());
									procurementRelationship.setRepaymentDate(buyerCreditLimit.getRepaymentDate());
									procurementRelationship.setInterest(buyerCreditLimit.getInterest());
									procurementRelationship.setStartTime(buyerCreditLimit.getStartTime());
									procurementRelationship.setMonthClean(buyerCreditLimit.getMonthClean());
									procurementRelationship.setLimitState(buyerCreditLimit.getLimitState());
									if (state != null && !state.equals("")) {
										if (state == 4) {
											procurementRelationship.setAuth(null);
										}
									}
								}
							}
						}
					}
					pList1 = this.procurementRelationshipService.list(qo1);

					// 查询的数据不为空就对数据进行导出
					if (pList1 != null && pList1.getPageSize() > 0) {
						// 导出文件的标题
						String title = "cglb" + ".xls";
						// 设置表格标题行
						String[] headers = new String[] { "序号", "客户名称", "授额期限", "总额度", "出账日", "最后还款日", "逾期计息%",
								"授额状态" };
						List<Object[]> dataList = new ArrayList<>();
						for (ProcurementRelationship procurementRelationship : procurementRelationships) {
							Object[] objs = new Object[8];
							objs[0] = procurementRelationship.getId();// 序号
							objs[1] = procurementRelationship.getAuth();// 客户名称
							if (procurementRelationship.getStartTime() != null
									&& !(procurementRelationship.getStartTime().equals(""))) {
								SimpleDateFormat formats = new SimpleDateFormat("yyyy-MM-dd");
								String formatDate1 = formats.format(procurementRelationship.getStartTime());
								String times = formats.format(procurementRelationship.getMonthClean());
								StringBuffer buffer = new StringBuffer(formatDate1 + "至" + times);
								objs[2] = buffer;// 授额期限
							}
							if (objs[2] == null) {
								objs[2] = "--";
							}
							objs[3] = procurementRelationship.getBuyamout();// 总额度
							if (objs[3] == null) {
								objs[3] = "--";
							}
							objs[4] = procurementRelationship.getAccountDate();// 出账日
							if (objs[4].equals(0)) {
								objs[4] = "--";
							}
							objs[5] = procurementRelationship.getRepaymentDate();// 最后还款日
							if (objs[5].equals(0)) {
								objs[5] = "--";
							}
							objs[6] = procurementRelationship.getInterest();// 逾期计息%
							if (objs[6].equals(0.0)) {
								objs[6] = "--";
							}

							if (procurementRelationship.getLimitState() == 1) {
								objs[7] = "进行中";
							} else if (procurementRelationship.getLimitState() == 2) {
								objs[7] = "已到期";
							} else if (procurementRelationship.getLimitState() == 3) {
								objs[7] = "已冻结";
							} else {
								objs[7] = "未开通";
							}
							// 数据添加到excel表格
							if (procurementRelationship.getAuth() != null && !procurementRelationship.equals("")) {
								dataList.add(objs);
							}
						}
						// 使用流将数据导出
						OutputStream out = null;
						try {
							// 防止中文乱码
							String headStr = "attachment; filename=\"" + new String(title.getBytes("utf-8"), "utf-8")
									+ "\"";
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
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "商家中心-赊销管理-查看赊销详情", value = "/seller/my_charge_sales_detail.htm*", rtype = "seller", rname = "赊销管理", rcode = "my_charge_sales_detail", rgroup = "赊销管理")
	@RequestMapping({ "/seller/my_charge_sales_detail.htm" })
	public ModelAndView myChangeSalesDetail(HttpServletRequest request, HttpServletResponse response,
			String currentPage, Integer storeId, String number, String actual, Integer payStatus, Integer buyerId) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/my_charge_sales_detail.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		try {
			String url = this.configService.getSysConfig().getAddress();
			Map params = new HashMap();
			params.put("storeId", storeId);
			params.put("buyerId", buyerId);
			List<BuyerCreditLimit> buyerCreditLimits = buyerCreditLimitService.query(
					"select obj from BuyerCreditLimit obj where obj.storeId=:storeId and obj.buyerId=:buyerId", params,
					-1, -1);
			BuyerCreditLimit buyerCreditLimit = buyerCreditLimits.get(0);
			mv.addObject("limit", buyerCreditLimit);
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
			qo.addQuery("obj.store.id", new SysMap("storeId", Long.valueOf(storeId)), "=");
			qo.addQuery("obj.bill_state", new SysMap("billState", 1), "=");
			qo.addQuery("obj.user.id", new SysMap("userId", Long.valueOf(buyerId)), "=");

			// 回款编号
			if (number != null && !number.equals("")) {
				qo.addQuery("obj.number", new SysMap("number", number), "=");
			}

			// 实际还款日
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

			// 回款状态
			if (payStatus != null && payStatus != 0) {
				qo.addQuery("obj.pay_status", new SysMap("payStatus", payStatus), "=");
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
			CommUtil.saveIPageList2ModelAndView(url + "/user/default/usercenter/my_charge_sales_detail.html", "", "",
					pList, mv);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return mv;
	}

	String enterpriseName2 = "";
	String payTime1 = "";
	String finishTime1 = "";
	String orderNo = "";
	Integer bill_state1 = 0;
	String actual_date1 = "";
	Integer settlement_status1 = 0;

	@SecurityMapping(display = false, rsequence = 0, title = "商家中心-赊销管理-赊销订单-对账订单及结算订单", value = "/seller/charge_sales_order.htm*", rtype = "seller", rname = "赊销管理", rcode = "charge_sales_order", rgroup = "赊销管理")
	@RequestMapping({ "/seller/charge_sales_order.htm" })
	public ModelAndView changeSalesOrder(HttpServletRequest request, HttpServletResponse response,
			String enterpriseName, String order_id, String payTime, String finishTime, Integer bill_state,
			Integer twoStatus, Integer settlement_status, Integer repaymentDate, String actual_date,
			String currentPage) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/charge_sales_order.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		try {
			// 当前登录人店铺
			Store store = SecurityUserHolder.getCurrentUser().getStore();
			String url = this.configService.getSysConfig().getAddress();

			OrderFormQueryObject qo = new OrderFormQueryObject(currentPage, mv, "addTime", "desc");
			qo.addQuery("obj.store.id", new SysMap("id", store.getId()), "=");
			// 查询确认收货后的对账订单列表 order_status 40 已收货
			if (twoStatus == 1) {
				qo.addQuery("obj.order_status", new SysMap("order_status", 40), "=");
				// 对账状态查询
				bill_state1 = bill_state;
				if (bill_state != null && !bill_state.equals("")) {
					if (bill_state != 2) {
						qo.addQuery("obj.bill_state", new SysMap("bill_state", bill_state), "=");
					}
				}
			}

			// 查询（已对账）结算订单列表
			if (twoStatus == 2) {
				qo.addQuery("obj.bill_state", new SysMap("bill_state", 1), "=");
				qo.addQuery("obj.order_status", new SysMap("order_status", 40), "=");
				// 结算状态查询
				settlement_status1 = settlement_status;
				if (settlement_status != null && !settlement_status.equals("")) {
					if (settlement_status != 0) {
						qo.addQuery("obj.settlement_status", new SysMap("settlement_status", settlement_status), "=");
					}
				}
			}

			// 交易日期
			payTime1 = payTime;
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (payTime1 != null && !payTime1.equals("")) {
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

			// 完成日期
			finishTime1 = finishTime;
			if (finishTime != null && !finishTime.equals("")) {
				String arr[] = finishTime.split(" - ");
				if (arr.length > 1) {
					for (int j = 0; j < arr.length; j++) {
						Date finish1 = format.parse(new StringBuffer(arr[0]).append(" 00:00:00").toString());
						Date finish2 = format.parse(new StringBuffer(arr[1]).append(" 00:00:00").toString());
						qo.addQuery("obj.finishTime", new SysMap("finish1", finish1), ">=");
						qo.addQuery("obj.finishTime", new SysMap("finish2", finish2), "<=");
					}
				}

			}

			// 实际回款日期
			actual_date1 = actual_date;
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

			// 订单号
			orderNo = order_id;
			if (orderNo != null && !orderNo.equals("")) {
				qo.addQuery("obj.order_id", new SysMap("order_id", "%" + orderNo + "%"), "like");
			}

			IPageList pList = this.orderFormService.list(qo);
			List<OrderForm> orders = pList.getResult();

			Map map = new HashMap();
			if (orders != null && orders.size() > 0) {
				for (OrderForm orderForm : orders) {

					// 获取当前日期
					Date date = new Date();
					SimpleDateFormat formats = new SimpleDateFormat("yyyy-MM-dd");
					String formatDate = formats.format(orderForm.getPayTime());
					StringBuffer dates = new StringBuffer(formatDate);
					String formatDate1 = formats.format(date);
					StringBuffer nowdates = new StringBuffer(formatDate1);
					mv.addObject("dates", dates);
					// 根据赊销buyerId与订单表userId关联查询赊销数据
					map.clear();
					map.put("buyerId", Integer.parseInt(String.valueOf(orderForm.getUser().getId())));
					StringBuffer stringBuffer = new StringBuffer(
							"select obj from BuyerCreditLimit obj where obj.buyerId=:buyerId and (obj.zstatus=3 or obj.zstatus=5 or obj.zstatus=6)");
					// 最后还款日
					if (repaymentDate != null && !repaymentDate.equals("")) {
						stringBuffer.append(" and obj.repaymentDate=:repaymentDate ");
						map.put("repaymentDate", repaymentDate);
					}
					List<BuyerCreditLimit> buyerCreditLimit = this.buyerCreditLimitService
							.query(stringBuffer.toString(), map, -1, -1);

					if (!buyerCreditLimit.isEmpty()) {
						BuyerCreditLimit limit = buyerCreditLimit.get(0);
						orderForm.setInterest(limit.getInterest());
						// 拼接出最后还款日
						StringBuffer replace = dates.replace(8, 10, String.valueOf(limit.getRepaymentDate()));
						// 如果还款日的月份与当前日期的月份相等
						int start = Integer.parseInt(
								new StringBuffer(orderForm.getPayTime().toString()).subSequence(5, 7).toString());
						int month = Integer.parseInt(dates.subSequence(5, 7).toString());
						replace = dates.replace(8, 10, String.valueOf(limit.getRepaymentDate())).replace(5, 7,
								String.valueOf(month + 1));
						// 拼接结果的最后还款日
						orderForm.setRepaymentDate(replace.toString());
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
						map.clear();
						// 客户名称模糊查
						enterpriseName2 = enterpriseName;
						if (enterpriseName != null && !enterpriseName.equals("")) {
							map.put("enterpriseName", "%" + enterpriseName + "%");
						} else {
							map.put("enterpriseName", "%%");
						}
						map.put("userId", orderForm.getUser().getId().toString());
						List<Authentication> auths = this.authenticationservice.query(
								"select obj from Authentication obj where obj.userId=:userId and obj.enterpriseName like :enterpriseName",
								map, -1, -1);
						if (auths != null && auths.size() > 0) {
							orderForm.setAuthString(auths.get(0).getEnterpriseName());
						}
					}
				}
			}
			CommUtil.saveIPageList2ModelAndView(url + "", "", "", pList, mv);
			return mv;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "商家中心-赊销管理-赊销订单-导出结算列表", value = "/seller/my_charge_sales.htm*", rtype = "buyer", rname = "赊销管理", rcode = "my_charge_sales", rgroup = "赊销管理")
	@RequestMapping({ "/seller/exportCloseAccount.htm" })
	public void exportCloseAccount(HttpServletResponse response, HttpServletRequest request) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/charge_sales_order.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		try {
			// 当前登录人店铺
			Store store = SecurityUserHolder.getCurrentUser().getStore();
			String url = this.configService.getSysConfig().getAddress();
			OrderFormQueryObject qo = new OrderFormQueryObject("1", mv, "addTime", "desc");
			qo.addQuery("obj.store.id", new SysMap("id", store.getId()), "=");
			// 查询（已对账）结算订单列表
			qo.addQuery("obj.bill_state", new SysMap("bill_state", 1), "=");
			qo.addQuery("obj.order_status", new SysMap("order_status", 40), "=");

			// 结算状态查询
			if (settlement_status1 != null && !settlement_status1.equals("")) {
				if (settlement_status1 != 0) {
					qo.addQuery("obj.settlement_status", new SysMap("settlement_status", settlement_status1), "=");
				}
			}

			// 交易日期
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (payTime1 != null && !payTime1.equals("")) {
				String arr[] = payTime1.split(" - ");
				if (arr.length > 1) {
					for (int j = 0; j < arr.length; j++) {
						Date pay1 = format.parse(new StringBuffer(arr[0]).append(" 00:00:00").toString());
						Date pay2 = format.parse(new StringBuffer(arr[1]).append(" 00:00:00").toString());
						qo.addQuery("obj.payTime", new SysMap("pay1", pay1), ">=");
						qo.addQuery("obj.payTime", new SysMap("pay2", pay2), "<=");
					}
				}

			}

			// 完成日期
			if (finishTime1 != null && !finishTime1.equals("")) {
				String arr[] = finishTime1.split(" - ");
				if (arr.length > 1) {
					for (int j = 0; j < arr.length; j++) {
						Date finish1 = format.parse(new StringBuffer(arr[0]).append(" 00:00:00").toString());
						Date finish2 = format.parse(new StringBuffer(arr[1]).append(" 00:00:00").toString());
						qo.addQuery("obj.finishTime", new SysMap("finish1", finish1), ">=");
						qo.addQuery("obj.finishTime", new SysMap("finish2", finish2), "<=");
					}
				}

			}

			// 实际回款日期
			if (actual_date1 != null && !actual_date1.equals("")) {
				String arr[] = actual_date1.split(" - ");
				if (arr.length > 1) {
					for (int j = 0; j < arr.length; j++) {
						Date finish1 = format.parse(new StringBuffer(arr[0]).append(" 00:00:00").toString());
						Date finish2 = format.parse(new StringBuffer(arr[1]).append(" 00:00:00").toString());
						qo.addQuery("obj.actual_date", new SysMap("finish1", finish1), ">=");
						qo.addQuery("obj.actual_date", new SysMap("finish2", finish2), "<=");
					}
				}

			}

			// 订单号
			if (orderNo != null && !orderNo.equals("")) {
				qo.addQuery("obj.order_id", new SysMap("order_id", "%" + orderNo + "%"), "like");
			}

			IPageList pList = this.orderFormService.list(qo);
			List<OrderForm> orders = pList.getResult();

			Map map = new HashMap();
			if (orders != null && orders.size() > 0) {
				for (OrderForm orderForm : orders) {

					// 获取当前日期
					Date date = new Date();
					SimpleDateFormat formats = new SimpleDateFormat("yyyy-MM-dd");
					String formatDate = formats.format(orderForm.getPayTime());
					StringBuffer dates = new StringBuffer(formatDate);
					String formatDate1 = formats.format(date);
					StringBuffer nowdates = new StringBuffer(formatDate1);
					mv.addObject("dates", dates);
					// 根据赊销buyerId与订单表userId关联查询赊销数据
					map.clear();
					map.put("buyerId", Integer.parseInt(String.valueOf(orderForm.getUser().getId())));
					StringBuffer stringBuffer = new StringBuffer(
							"select obj from BuyerCreditLimit obj where obj.buyerId=:buyerId and (obj.zstatus=3 or obj.zstatus=5 or obj.zstatus=6)");

					List<BuyerCreditLimit> buyerCreditLimit = this.buyerCreditLimitService
							.query(stringBuffer.toString(), map, -1, -1);
					BuyerCreditLimit limit = buyerCreditLimit.get(0);
					orderForm.setInterest(limit.getInterest());
					// 拼接出最后还款日
					StringBuffer replace = dates.replace(8, 10, String.valueOf(limit.getRepaymentDate()));
					// 如果还款日的月份与当前日期的月份相等
					int start = Integer
							.parseInt(new StringBuffer(orderForm.getPayTime().toString()).subSequence(5, 7).toString());
					int month = Integer.parseInt(dates.subSequence(5, 7).toString());
					replace = dates.replace(8, 10, String.valueOf(limit.getRepaymentDate())).replace(5, 7,
							String.valueOf(month + 1));
					// 拼接结果的最后还款日
					orderForm.setRepaymentDate(replace.toString());
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
					map.clear();
					// 客户名称模糊查
					if (enterpriseName2 != null && !enterpriseName2.equals("")) {
						map.put("enterpriseName", "%" + enterpriseName2 + "%");
					} else {
						map.put("enterpriseName", "%%");
					}
					map.put("userId", orderForm.getUser().getId().toString());
					List<Authentication> auths = this.authenticationservice.query(
							"select obj from Authentication obj where obj.userId=:userId and obj.enterpriseName like :enterpriseName",
							map, -1, -1);
					if (auths != null && auths.size() > 0) {
						orderForm.setAuthString(auths.get(0).getEnterpriseName());
					}
				}
			}

			// 查询的数据不为空就对数据进行导出
			if (pList != null && pList.getPageSize() > 0) {
				// 导出文件的标题
				String title = "jslb" + ".xls";
				// 设置表格标题行
				String[] headers = new String[] { "序号", "客户名称", "订单编号", "应结金额(元)", "交易日期", "预计回款日期", "实际回款日期",
						"逾期时长(天)", "逾期计息(%)", "总罚息(元)", "支付方式", "银行流水单号", "，每月回款编号", "状态" };
				List<Object[]> dataList = new ArrayList<>();
				for (OrderForm orderForm : orders) {
					if (orderForm != null) {
						Object[] objs = new Object[14];
						objs[0] = orderForm.getId();// 序号
						objs[1] = orderForm.getAuthString();// 客户名称
						objs[2] = orderForm.getOrder_id();// 订单编号
						objs[3] = orderForm.getTotalPrice();// 应结金额(元)

						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						String formatDate1 = sdf.format(orderForm.getPayTime());
						if (orderForm.getActual_date() != null) {
							String formatDate3 = sdf.format(orderForm.getActual_date());
							objs[6] = formatDate3;// 实际回款日期
						} else {
							objs[6] = "--";
						}

						objs[4] = formatDate1;// 交易日期
						objs[5] = orderForm.getRepaymentDate();// 预计回款日期

						objs[7] = orderForm.getDays();// 逾期时长(天)

						objs[8] = orderForm.getInterest();// 逾期计息%
						if (objs[8].equals(0.0)) {
							objs[8] = "--";
						}
						// 总罚息(元)
						BigDecimal price = orderForm.getTotalPrice();
						objs[9] = new DecimalFormat("0.00")
								.format(price.doubleValue() * orderForm.getInterest() * orderForm.getDays());

						// 支付方式
						if (orderForm.getPay_msg() != null) {
							objs[10] = orderForm.getPay_msg();
						} else {
							objs[10] = "--";
						}
						
						// 银行流水单号
						if (orderForm.getSettlement_status() == 1 || orderForm.getSettlement_status() == 2) {
							objs[11] = "--";
						} else {
							objs[11] = "银行流水单号";
						}

						objs[12] = orderForm.getNumber();// 每月回款编号

						if (orderForm.getSettlement_status() == 1) {
							objs[13] = "未结算";
						} else if (orderForm.getSettlement_status() == 2) {
							objs[13] = "结算中";
						} else {
							objs[13] = "已结算";
						}
						// 数据添加到excel表格
						if (orderForm.getAuthString() != null && !orderForm.equals("")) {
							dataList.add(objs);
						}
					}
					// 使用流将数据导出
					try {
						// 防止中文乱码
						String headStr = "attachment; filename=\"" + new String(title.getBytes("utf-8"), "utf-8")
								+ "\"";
						response.setContentType("octets/stream");
						response.setContentType("APPLICATION/OCTET-STREAM");
						response.setHeader("Content-Disposition", headStr);
						OutputStream out = response.getOutputStream();
						ExportExcelSeedBack ex = new ExportExcelSeedBack(title, headers, dataList);// 没有标题
						ex.export(out);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "商家中心-赊销管理-导出对账订单", value = "/seller/charge_sales_order.htm*", rtype = "buyer", rname = "赊销管理", rcode = "charge_sales_order", rgroup = "赊销管理")
	@RequestMapping({ "/seller/orderExport.htm" })
	public void orderTradExcel(HttpServletResponse response, HttpServletRequest request) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/charge_sales_order.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		try {
			Store store = SecurityUserHolder.getCurrentUser().getStore();
			OrderFormQueryObject qo = new OrderFormQueryObject("1", mv, "addTime", "desc");
			qo.addQuery("obj.store.id", new SysMap("id", store.getId()), "=");
			qo.addQuery("obj.order_status", new SysMap("order_status", 40), "=");
			// 交易日期
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (payTime1 != null && !payTime1.equals("")) {
				String arr[] = payTime1.split(" - ");
				if (arr.length > 1) {
					for (int j = 0; j < arr.length; j++) {
						Date time1 = format.parse(new StringBuffer(arr[0]).append(" 00:00:00").toString());
						Date time12 = format.parse(new StringBuffer(arr[1]).append(" 00:00:00").toString());
						qo.addQuery("obj.payTime", new SysMap("time1", time1), ">=");
						qo.addQuery("obj.payTime", new SysMap("time12", time12), "<=");
					}
				}

			}

			// 完成日期
			if (finishTime1 != null && !finishTime1.equals("")) {
				String arr[] = finishTime1.split(" - ");
				if (arr.length > 1) {
					for (int j = 0; j < arr.length; j++) {
						Date stime1 = format.parse(new StringBuffer(arr[0]).append(" 00:00:00").toString());
						Date stime2 = format.parse(new StringBuffer(arr[1]).append(" 00:00:00").toString());
						qo.addQuery("obj.finishTime", new SysMap("stime1", stime1), ">=");
						qo.addQuery("obj.finishTime", new SysMap("stime2", stime2), "<=");
					}
				}

			}

			// 订单号
			if (orderNo != null && !orderNo.equals("")) {
				qo.addQuery("obj.order_id", new SysMap("order_id", "%" + orderNo + "%"), "like");
			}

			// 对账状态查询
			if (bill_state1 != null && !bill_state1.equals("")) {
				if (bill_state1 != 2) {
					qo.addQuery("obj.bill_state", new SysMap("bill_state", bill_state1), "=");
				}
			}

			IPageList pList = this.orderFormService.list(qo);
			List<OrderForm> orders = pList.getResult();
			Map map = new HashMap();
			if (orders != null && orders.size() > 0) {
				for (OrderForm orderForm : orders) {
					// 客户名称模糊查
					if (enterpriseName2 != null && !enterpriseName2.equals("")) {
						map.put("enterpriseName", "%" + enterpriseName2 + "%");
					} else {
						map.put("enterpriseName", "%%");
					}
					map.put("userId", orderForm.getUser().getId().toString());
					List<Authentication> auths = this.authenticationservice.query(
							"select obj from Authentication obj where obj.userId=:userId and obj.enterpriseName like :enterpriseName",
							map, -1, -1);
					if (auths != null && auths.size() > 0) {
						orderForm.setAuthString(auths.get(0).getEnterpriseName());
					}
				}
			}

			// 查询的数据不为空就对数据进行导出
			if (pList != null && pList.getPageSize() > 0) {
				// 导出文件的标题
				String title = "orderList" + ".xls";
				// 设置表格标题行
				String[] headers = new String[] { "序号", "客户名称", "订单编号", "交易日期", "完成日期", "应结金额(元)", "配送费(元)", "对账状态" };
				List<Object[]> dataList = new ArrayList<>();
				if (orders != null && orders.size() > 0) {
					for (OrderForm orderForm : orders) {
						Object[] objs = new Object[8];
						objs[0] = orderForm.getId();// 序号
						objs[1] = orderForm.getAuthString();// 客户名称
						objs[2] = orderForm.getOrder_id();// 订单编号
						objs[3] = orderForm.getPayTime();// 交易日期
						objs[4] = orderForm.getFinishTime();// 完成日期
						objs[5] = orderForm.getTotalPrice();// 应结金额(元)
						objs[6] = orderForm.getShip_price();// 配送费(元)
						if (orderForm.getBill_state() == 1) {
							objs[7] = "已对账";
						} else {
							objs[7] = "未对账";
						}
						// 数据添加到excel表格
						if (orderForm.getAuthString() != null && !orderForm.getAuthString().equals("")) {
							dataList.add(objs);
						}
					}
					// 使用流将数据导出
					OutputStream out = null;
					try {
						// 防止中文乱码
						String headStr = "attachment; filename=\"" + new String(title.getBytes("utf-8"), "utf-8")
								+ "\"";
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
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "商家中心-赊销管理-赊销订单-对账", value = "/seller/charge_sales_order.htm*", rtype = "seller", rname = "赊销管理", rcode = "charge_sales_order", rgroup = "赊销管理")
	@RequestMapping({ "/seller/bill_sales.htm" })
	public ModelAndView billSales(HttpServletRequest request, HttpServletResponse response, String orderIds) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/charge_sales_order.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		try {
			String[] ids = orderIds.split(",");
			for (String id : ids) {
				Map params = new HashMap();
				params.put("id", Long.parseLong(id));
				List<OrderForm> list = this.orderFormService.query(
						"select obj from OrderForm obj where obj.id=:id order by obj.number desc", params, -1, -1);
				OrderForm order = list.get(0);
				String number;
				if (order.getBill_state() == 0) {
					params.clear();
					List<OrderForm> list1 = this.orderFormService
							.query("select obj from OrderForm obj  order by obj.number desc", params, -1, -1);
					if (list1.get(0).getNumber() == null || list1.get(0).getNumber().equals("")) {
						number = CodeUtil.generateCode("RE", null);
					} else {
						number = CodeUtil.generateCode("RE", list1.get(0).getNumber());
					}
					order.setNumber(number);
					this.orderFormService.update(order);
				}
				OrderForm orderForm = this.orderFormService.getObjById(Long.valueOf(Long.parseLong(id)));
				orderForm.setBill_state(1);
				this.orderFormService.update(orderForm);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "商家中心-赊销管理-赊销钱包", value = "/seller/charge_sales_wallet.htm*", rtype = "seller", rname = "赊销管理", rcode = "charge_sales_wallet", rgroup = "赊销管理")
	@RequestMapping({ "/seller/charge_sales_wallet.htm" })
	public ModelAndView changeSalesWallet(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/charge_sales_wallet.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		try {
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "商家中心-赊销管理-新增银行账户", value = "/buyer/charge_sales_wallet_add.htm*", rtype = "buyer", rname = "赊销管理", rcode = "charge_sales_wallet_add", rgroup = "赊销管理")
	@RequestMapping({ "/seller/charge_sales_wallet_add.htm" })
	public ModelAndView buyerChangeSalesWalletAdd(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/charge_sales_wallet_add.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		try {
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "赊销审核", value = "/seller/charge_sales_check.htm*", rtype = "seller", rname = "赊销审核", rcode = "charge_sales_check", rgroup = "赊销审核")
	@RequestMapping({ "/seller/charge_sales_check.htm" })
	public ModelAndView charge_sales_check(HttpServletRequest request, HttpServletResponse response, Long Id,
			String newstatus, String flag, String enterpriseName, String currentPage, String buyerId, String startTime,
			String zstatus, String refReason) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/charge_sales_check.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (flag != null && flag.equals("updateZstatus")) {
			BuyerCreditLimit buyerCreditLimit = buyerCreditLimitService.getObjById(Id);
			buyerCreditLimit.setZstatus(Integer.parseInt(newstatus));
			if (refReason != null && !flag.equals("")) {
				buyerCreditLimit.setRefReason(refReason);
			}
			buyerCreditLimitService.update(buyerCreditLimit);
		}
		HttpClass hc = new HttpClass();
		Long sellerId = SecurityUserHolder.getCurrentUser().getId();
		try {

			String userName = SecurityUserHolder.getCurrentUser().getUserName();
			String load = hc.load("http://127.0.0.1:8081/ssm_project/selStatus", "userName=" + userName);
			mv.addObject("storstatus", load);

			BuyerCreditLimitQueryObject qo = new BuyerCreditLimitQueryObject(currentPage, mv, "addTime", "desc");
			qo.addQuery("obj.sellerId", new SysMap("sellerId", sellerId.intValue()), "=");
			qo.addQuery("obj.buyerCombination", new SysMap("buyerCombination", "0"), "!=");

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (startTime != null && !startTime.equals("")) {
				String arr[] = startTime.split(" - ");
				if (arr.length > 1) {
					for (int j = 0; j < arr.length; j++) {

						Date startTime1 = formatter.parse(new StringBuffer(arr[0]).append(" 00:00:00").toString());
						Date monthClean = formatter.parse(new StringBuffer(arr[1]).append(" 00:00:00").toString());
						qo.addQuery("obj.startTime", new SysMap("startTime", startTime1), ">=");
						qo.addQuery("obj.monthClean", new SysMap("monthClean", monthClean), "<=");
					}
				}
			}
			if (buyerId != null && !buyerId.equals("")) {
				qo.addQuery("obj.buyerId", new SysMap("buyerId", Integer.parseInt(buyerId)), "=");
			}
			if (zstatus != null && !zstatus.equals("")) {
				qo.addQuery("obj.zstatus", new SysMap("zstatus", Integer.parseInt(zstatus)), "=");
			}
			qo.setOrderBy("addTime");
			qo.setOrderType("desc");
			IPageList pList = this.buyerCreditLimitService.list(qo);
			List<BuyerCreditLimit> buyerCreditLimits = pList.getResult();
			// 得到查询结果遍历得到买家名称，去首营认证表去获取企业名称，
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
			CommUtil.saveIPageList2ModelAndView(url + "/user/default/usercenter/charge_sales_check.html", "", "", pList,
					mv);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mv;
	}

	@RequestMapping({ "/updpass.htm" })
	public ModelAndView updzstatus(HttpServletRequest request, HttpServletResponse response, int storeId,
			String buyerName, int id) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/addUpdate_customerCredit.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		Map param = new HashMap();

		HttpClass hc = new HttpClass();
		Long sellerId = SecurityUserHolder.getCurrentUser().getId();

		// 授额页面可能会用到的买家姓名 addUpdate_customerCredit.html
		Map map = new HashMap();
		map.put("userName", buyerName);
		List<Authentication> auths = authenticationservice
				.query("select obj from Authentication obj where obj.userName=:userName", map, -1, -1);
		String authname = auths.get(0).getEnterpriseName();
		mv.addObject("buyerName", authname);
		mv.addObject("buyerId", id);
		mv.addObject("type", "update");
		map.clear();
		map.put("sellerId", sellerId.intValue());
		map.put("buyerId", id);
		List<BuyerCreditLimit> BuyerCreditLimitlist = buyerCreditLimitService.query(
				"select obj from BuyerCreditLimit obj where obj.sellerId=:sellerId and  obj.buyerId=:buyerId", map, -1,
				-1);
		System.out.println(BuyerCreditLimitlist.size());
		BuyerCreditLimit buyerCreditLimit = BuyerCreditLimitlist.get(0);

		mv.addObject("buyerCreditLimit", buyerCreditLimit);
		return mv;
	}

	@RequestMapping({ "/updnotpass.htm" })
	public ModelAndView updzstatustwo(HttpServletRequest request, HttpServletResponse response, int storeId,
			int buyerId, String refReason) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/charge_sales_check.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);

		// HttpClass hc = new HttpClass();
		// Long sellerId = SecurityUserHolder.getCurrentUser().getId();
		/*
		 * try { String zwjs = hc.load("http://127.0.0.1:8081/ssm_project/qwertwo",
		 * "zstatus=" + 4 + "&" + "refReason=" + refReason + "&" + "sellerId=" +
		 * sellerId + "&" + "storeId=" + storeId); } catch (Exception e) {
		 * e.printStackTrace(); }
		 */

		Map map = new HashMap();
		map.put("storeId", storeId);
		map.put("buyerId", buyerId);

		List<BuyerCreditLimit> buyerCreditLimit = buyerCreditLimitService.query(
				"select obj from BuyerCreditLimit obj where obj.buyerId=:buyerId  and  obj.storeId=:storeId ", map, -1,
				-1);
		if (buyerCreditLimit.size() <= 0 || buyerCreditLimit == null) {

			mv.addObject("zwlist", buyerCreditLimit);
		} else {
			map.clear();
			map.put("storeId", storeId);
			BuyerCreditLimit creditLimit = buyerCreditLimit.get(0);
			creditLimit.setZstatus(4);
			creditLimit.setRefReason(refReason);
			buyerCreditLimitService.equals(creditLimit);
			List<BuyerCreditLimit> buyerCredi = buyerCreditLimitService.query(
					"select obj from BuyerCreditLimit obj where  obj.storeId=:storeId   or obj.zstatus=4 or obj.zstatus=2 or obj.zstatus=1",
					map, -1, -1);
			mv.addObject("zwlist", buyerCredi);
			// 站内短信
			User user1 = this.userService.getObjById((long) creditLimit.getBuyerId());
			String content = SecurityUserHolder.getCurrentUser().getStore().getStore_name() + "已拒绝您的赊销申请，请重新申请！";
			Message msg1 = new Message();
			msg1.setAddTime(new Date());
			Whitelist whiteList = new Whitelist();
			content = content.replaceAll("\n", "iskyhop_br");
			msg1.setContent(Jsoup.clean(content, Whitelist.basic()).replaceAll("iskyhop_br", "\n"));
			msg1.setFromUser(SecurityUserHolder.getCurrentUser());
			msg1.setToUser(user1);
			msg1.setType(1);
			this.messageService.save(msg1);
		}
		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "赊销", value = "ller/is_charge_sale.htm*", rtype = "seller", rname = "赊销", rcode = "is_charge_sale", rgroup = "赊销")
	@RequestMapping({ "/seller/is_charge_sale.htm" })
	public void shezhi(HttpServletResponse response, String state, String storeCredits) {
		response.setContentType("application/json;charset=utf-8");
		try {
			HttpClass hc = new HttpClass();
			String userName = SecurityUserHolder.getCurrentUser().getUserName();

			String load = hc.load("http://127.0.0.1:8081/ssm_project/updStatus",
					"userName=" + userName + "&state=" + state);
			response.getWriter().print(load);
			String content = "";
			if (load != "" || load == null) {
				content = "您的店铺" + SecurityUserHolder.getCurrentUser().getStore().getStore_name()
						+ "已开启赊销支付方式！买家可以在您的店铺进行赊销采购。";
			}
			Message msg = new Message();
			msg.setAddTime(new Date());
			Whitelist whiteList = new Whitelist();
			content = content.replaceAll("\n", "iskyhop_br");
			msg.setContent(Jsoup.clean(content, Whitelist.basic()).replaceAll("iskyhop_br", "\n"));
			msg.setFromUser(SecurityUserHolder.getCurrentUser());
			msg.setToUser(SecurityUserHolder.getCurrentUser());
			msg.setType(1);
			this.messageService.save(msg);
			// 保存额度
			CreditLlineManagement ment = new CreditLlineManagement();
			Store store = SecurityUserHolder.getCurrentUser().getStore();
			ment.setAddTime(new Date());
			ment.setDeleteStatus(true);
			ment.setStoreId(store.getId().intValue());
			ment.setStoreName(store.getStore_name());
			ment.setSellerId(store.getUser().getId().intValue());
			ment.setSellerName(store.getUser().getUsername());
			ment.setStoreCredits(storeCredits);
			creditLlineManagementService.save(ment);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SecurityMapping(display = false, rsequence = 0, title = "查询客户额度", value = "/seller/query_customerCredit.htm*", rtype = "seller", rname = "额度管理", rcode = "query_customerCredit_seller", rgroup = "额度管理")
	@RequestMapping({ "/seller/query_customerCredit.htm" })
	public ModelAndView query_customerCredit(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/query_customerCredit.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);

		Store store = SecurityUserHolder.getCurrentUser().getStore();
		int storeId = store.getId().intValue();
		String url = this.configService.getSysConfig().getAddress();
		try {
			BuyerCreditLimitQueryObject qo = new BuyerCreditLimitQueryObject("1", mv, "addTime", "desc");
			qo.addQuery("obj.storeId", new SysMap("storeId", storeId), "=");
			qo.addQuery("obj.zstatus", new SysMap("zstatus", 3), "=");
			qo.setOrderBy("addTime");
			qo.setOrderType("desc");
			IPageList pList = this.buyerCreditLimitService.list(qo);
			List<BuyerCreditLimit> buyerCreditLimits = pList.getResult();
			Map map = new HashMap();
			// 得到查询结果遍历得到买家名称，去首营认证表去获取企业名称，
			for (BuyerCreditLimit buyerCreditLimit : buyerCreditLimits) {
				map.put("userName", buyerCreditLimit.getBuyerName());
				// Authentication
				// auth=cl.load("http://127.0.0.1:8081/ssm_project/select","userName"+buyerCreditLimit.getBuyerName());
				List<Authentication> auths = authenticationservice
						.query("select obj from Authentication obj where obj.userName=:userName", map, -1, -1);
				buyerCreditLimit.setAuthString(auths.get(0).getEnterpriseName());
				/*
				 * SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd"); String
				 * parse=sdf.format(buyerCreditLimit.getMonthClean());
				 * System.out.println(parse); Date end = sdf.parse(parse);
				 * System.out.println(end); buyerCreditLimit.setMonthClean(end); String
				 * parse1=sdf.format(buyerCreditLimit.getStartTime()); Date start =
				 * sdf.parse(parse1); buyerCreditLimit.setStartTime(start);
				 */
			}
			CommUtil.saveIPageList2ModelAndView(url + "/user/default/usercenter/addUpdate_customerCredit.html", "", "",
					pList, mv);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "添加与编辑 _客户额度页面", value = "/seller/addUpdate_customerCredit.htm*", rtype = "seller", rname = "额度管理", rcode = "addUpdate_customerCredit_seller", rgroup = "额度管理")
	@RequestMapping({ "/seller/addUpdate_customerCredit.htm" })
	public ModelAndView addUpdate_customerCredit(HttpServletRequest request, HttpServletResponse response, String type,
			String id) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/addUpdate_customerCredit.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);

		Store store = SecurityUserHolder.getCurrentUser().getStore();
		Long userId = SecurityUserHolder.getCurrentUser().getId();
		String url = this.configService.getSysConfig().getAddress();
		try {
			Map params = new HashMap();
			if (type.equals("save")) {// 添加页面
				// 查询已经成功建立 采购关系 的买家
				mv.addObject("type", "save");
				params.clear();
				// 查询出建立采购关系对象 procurementRelationshipList
				params.put("shopkeeperId", String.valueOf(userId));
				List<ProcurementRelationship> procurementRelationshipList = this.procurementRelationshipService.query(
						"select obj from ProcurementRelationship obj where obj.auditStatus=1 and obj.shopkeeperId=:shopkeeperId",
						params, -1, -1);

				// 拿到建立采购关系的买家id集合BuyerIdlist
				List<String> BuyerIdlist = new LinkedList<>();
				for (ProcurementRelationship ProcurementRelationship : procurementRelationshipList) {
					BuyerIdlist.add(ProcurementRelationship.getBuyerId());
				}

				List<String> list = new LinkedList<>();// 用来装成功建立了采购关系却没申请赊销的买家id集合
				// 判断买家id集合是空还是有数据
				if (BuyerIdlist != null || BuyerIdlist.size() > 0) {
					for (String buyerId : BuyerIdlist) {
						params.clear();
						params.put("sellerId", userId.intValue());
						int byid = Integer.parseInt(buyerId);
						params.put("buyerId", byid);
						List<BuyerCreditLimit> BuyerCreditLimit = buyerCreditLimitService.query(
								"select obj from BuyerCreditLimit obj where  obj.sellerId=:sellerId and obj.buyerId=:buyerId and obj.zstatus!=0",
								params, -1, -1);
						System.out.println(BuyerCreditLimit.size());
						if (BuyerCreditLimit == null || BuyerCreditLimit.size() <= 0) {
							list.add(buyerId);
						}
					}
				}

				// 给卖家主动受额对象集合
				List<Authentication> authenticationst = new LinkedList<Authentication>();
				// 通过list集合去才认证过通过的认证对象
				if (list.size() > 0 || list != null) {
					for (String AuserId : list) {
						params.clear();
						String byid = AuserId;
						params.put("userId", byid);
						List<Authentication> authentications = authenticationservice.query(
								"select obj from Authentication obj where obj.examine=2 and obj.userId=:userId", params,
								-1, -1);
						if (authentications != null && authentications.size() > 0) {
							System.out.println(authentications.size());
							System.out.println(authentications.get(0));
							authenticationst.add(authentications.get(0));
						}

					}

				}
				System.out.println(authenticationst);
				mv.addObject("authenticationst", authenticationst);
				return mv;

			} else {// 客户额度编辑页面
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
				mv.addObject("type", "update");
			}
		} catch (Exception e) {
			e.printStackTrace();
			mv = new JModelAndView("error.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			mv.addObject("op_title", "店铺额度操作失败！");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/my_charge_sales.htm");
		}
		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "提交_客户额度", value = "/seller/save_customerCredit.htm*", rtype = "seller", rname = "额度管理", rcode = "save_customerCredit_seller", rgroup = "额度管理")
	@RequestMapping({ "/seller/save_customerCredit.htm" })
	public ModelAndView save_customerCredit(HttpServletRequest request, HttpServletResponse response, String buyerId,
			String buyerCredits, String type, String startTime, String repaymentDate, String interest,
			String accountDate, @RequestParam(value = "fileUrl") MultipartFile fileUrl) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/success.html", this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		Long userId = SecurityUserHolder.getCurrentUser().getId();
		String url = this.configService.getSysConfig().getAddress();
		try {
			Map params = new HashMap();
			Store store = SecurityUserHolder.getCurrentUser().getStore();
			if (type.equals("save")) {
				// 判断买家是否已经设置过额度
				BuyerCreditLimit buyerCreditLimit = new BuyerCreditLimit();
				params.put("buyerId", Integer.parseInt(buyerId));// buyerId是buyerCreditLimit表的买家ID
				params.put("sellerId", userId.intValue());
				List buyerCreditLineList = this.buyerCreditLimitService.query(
						"select buyerId from BuyerCreditLimit obj where obj.buyerId=:buyerId and obj.sellerId=:sellerId and obj.zstatus=3 ",
						params, -1, -1);

				if (buyerCreditLineList.size() > 0) {
					mv = new JModelAndView("error.html", this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "买家已经设置过赊销额度，不需要重复添加！");
					mv.addObject("url", CommUtil.getURL(request) + "/seller/my_charge_sales.htm");
					return mv;
				}

				// CreditLlineManagementQueryObject qo = new
				// CreditLlineManagementQueryObject("1", mv,"addTime", "desc");
				// qo.addQuery("obj.sellerId",new SysMap("sellerId",new
				// Long(userId).intValue()), "=");
				// IPageList pList = this.creditLlineManagementService.list(qo);

				buyerCreditLimit.setStoreId(store.getId().intValue());
				buyerCreditLimit.setStoreName(store.getStore_name());
				buyerCreditLimit.setSellerId(userId.intValue());
				buyerCreditLimit.setSellerName(SecurityUserHolder.getCurrentUser().getUsername());
				buyerCreditLimit.setBuyerId(Integer.parseInt(buyerId));

				User user = this.userService.getObjById(Long.valueOf(buyerId));
				buyerCreditLimit.setBuyerName(user.getUsername());

				buyerCreditLimit.setBuyerCombination(buyerCredits);
				buyerCreditLimit.setBuyerRemainingUndrawn(buyerCredits);
				Date addtime = new Date();
				buyerCreditLimit.setAddTime(addtime);

				DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");

				Calendar rightNow = Calendar.getInstance();
				rightNow.setTime(addtime);
				// rightNow.add(Calendar.YEAR,apTime);
				Date end = rightNow.getTime();
				String R = format1.format(end);
				// 计息修改
				buyerCreditLimit.setRepaymentDate(Integer.valueOf(repaymentDate));
				buyerCreditLimit.setAccountDate(Integer.valueOf(accountDate));

				String arr[] = startTime.split(" - ");
				if (arr.length > 1) {
					for (int j = 0; j < arr.length; j++) {
						buyerCreditLimit.setStartTime(format1.parse(arr[0]));
						buyerCreditLimit.setMonthClean(format1.parse(arr[1]));
					}
				}

				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				// cal.add(Calendar.YEAR, apTime);
				buyerCreditLimit.setApTime(0);
				if (interest != null && !interest.equals("")) {
					buyerCreditLimit.setInterest(Double.valueOf(interest.toString()));
				} else {
					buyerCreditLimit.setInterest(Double.valueOf(0));
				}
				buyerCreditLimit.setZstatus(3);
				buyerCreditLimit.setLimitState(0);
				// 上传合同
				String file = FileUtils.fileUploadPage(fileUrl, request, "contract");
				buyerCreditLimit.setFileUrl(file);
				this.buyerCreditLimitService.save(buyerCreditLimit);
				// 审核情况通知买家（商家主动发起）
				User user1 = this.userService.getObjById((long) buyerCreditLimit.getBuyerId());
				if (SecurityUserHolder.getCurrentUser().getStore() != null) {
					String content = "恭喜您，" + SecurityUserHolder.getCurrentUser().getStore().getStore_name()
							+ "已向您授信赊销额度！您可以在该店铺进行赊销采购啦。";
					Message msg1 = new Message();
					msg1.setAddTime(new Date());
					Whitelist whiteList = new Whitelist();
					content = content.replaceAll("\n", "iskyhop_br");
					msg1.setContent(Jsoup.clean(content, Whitelist.basic()).replaceAll("iskyhop_br", "\n"));
					msg1.setFromUser(SecurityUserHolder.getCurrentUser());
					msg1.setToUser(user1);
					msg1.setType(1);
					this.messageService.save(msg1);
				}
			} else {
				params.clear();
				params.put("buyerId", Integer.parseInt(buyerId));// buyerId是buyerCreditLimit表的买家ID
				params.put("sellerId", userId.intValue());
				List buyerCreditLineList = this.buyerCreditLimitService.query(
						"select obj from BuyerCreditLimit obj where obj.buyerId=:buyerId and obj.sellerId=:sellerId",
						params, -1, -1);

				BuyerCreditLimit buyerCreditLimit = (BuyerCreditLimit) buyerCreditLineList.get(0);
				if (Integer.parseInt(buyerCreditLimit.getBuyerRemainingUndrawn()) == 0
						|| buyerCreditLimit.getBuyerRemainingUndrawn() == null
						|| buyerCreditLimit.getBuyerRemainingUndrawn().equals("")) {
					buyerCreditLimit.setBuyerRemainingUndrawn(buyerCredits);
					buyerCreditLimit.setBuyerCombination(buyerCredits);

				} else {
					// 已用的额度 = 原总额 - 剩余额
					Integer sun = Integer.parseInt(buyerCreditLimit.getBuyerCombination())
							- Integer.parseInt(buyerCreditLimit.getBuyerRemainingUndrawn());
					// 新余额 = 新总额 - 已用额度
					Integer max = Integer.parseInt(buyerCredits) - sun;
					buyerCreditLimit.setBuyerRemainingUndrawn(max.toString());
					buyerCreditLimit.setBuyerCombination(buyerCredits);
				}

				// int result1 = Integer.parseInt(buyerCreditLimit.getBuyerRemainingUndrawn()) +
				// Integer.parseInt(buyerCredits);
				buyerCreditLimit.setAddTime(new Date());
				buyerCreditLimit.setStoreName(store.getStore_name());
				DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
				Calendar rightNow = Calendar.getInstance();
				rightNow.setTime(new Date());
				Date end = rightNow.getTime();
				String R = format1.format(end);
				// 计息修改
				buyerCreditLimit.setRepaymentDate(Integer.valueOf(repaymentDate));
				buyerCreditLimit.setAccountDate(Integer.valueOf(accountDate));

				String arr[] = startTime.split(" - ");
				if (arr.length > 1) {
					for (int j = 0; j < arr.length; j++) {
						buyerCreditLimit.setStartTime(format1.parse(arr[0]));
						buyerCreditLimit.setMonthClean(format1.parse(arr[1]));
					}
				}

				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				buyerCreditLimit.setApTime(0);

				// 上传合同
				String file = FileUtils.fileUploadPage(fileUrl, request, "contract");
				buyerCreditLimit.setFileUrl(file);
				this.buyerCreditLimitService.update(buyerCreditLimit);

				// 计息修改

				buyerCreditLimit.setRepaymentDate(Integer.valueOf(repaymentDate));
				if (interest != null && !interest.equals("")) {
					buyerCreditLimit.setInterest(Double.valueOf(interest.toString()));
				} else {
					buyerCreditLimit.setInterest(Double.valueOf(0));
				}
				buyerCreditLimit.setZstatus(3);
				this.buyerCreditLimitService.update(buyerCreditLimit);
				// 审核情况通知买家（买家主动发起）
				User user1 = this.userService.getObjById(((long) buyerCreditLimit.getBuyerId()));
				if (SecurityUserHolder.getCurrentUser().getStore() != null) {
					String content = "恭喜您，" + SecurityUserHolder.getCurrentUser().getStore().getStore_name()
							+ "已通过您的赊销申请！您可以在该店铺进行赊销采购啦";
					Message msg = new Message();
					msg.setAddTime(new Date());
					Whitelist whiteList = new Whitelist();
					content = content.replaceAll("\n", "iskyhop_br");
					msg.setContent(Jsoup.clean(content, Whitelist.basic()).replaceAll("iskyhop_br", "\n"));
					msg.setFromUser(SecurityUserHolder.getCurrentUser());
					msg.setToUser(user1);
					msg.setType(1);
					this.messageService.save(msg);
				}
			}
			mv.addObject("op_title", "客户额度保存成功！");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/my_charge_sales.htm");

		} catch (Exception e) {
			e.printStackTrace();
			mv = new JModelAndView("error.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			mv.addObject("op_title", "客户额度保存失败！");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/my_charge_sales.htm");
		}
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

	private Date Date(Object setInterest) {
		// TODO Auto-generated method stub
		return null;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "店主提现", value = "/seller/withdraDeposit.htm*", rtype = "seller", rname = "额度管理", rcode = "withdraDeposit_seller", rgroup = "额度管理")
	@RequestMapping({ "/seller/withdraDeposit.htm" })
	public ModelAndView withdraDeposit(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/withdraDeposit.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);

		Long userId = SecurityUserHolder.getCurrentUser().getId();
		String url = this.configService.getSysConfig().getAddress();
		try {
			Map params = new HashMap();
			params.put("sellerId", userId.intValue());
			List creditLlineManagementList = this.creditLlineManagementService
					.query("select obj from CreditLlineManagement obj where obj.sellerId=:sellerId", params, -1, -1);
			CreditLlineManagement creditLlineManagement = (CreditLlineManagement) creditLlineManagementList.get(0);
			mv.addObject("creditLlineManagement", creditLlineManagement);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "店主提现_提交", value = "/seller/save_withdraDeposit.htm*", rtype = "seller", rname = "额度管理", rcode = "save_withdraDeposit_seller", rgroup = "额度管理")
	@RequestMapping({ "/seller/save_withdraDeposit.htm" })
	public ModelAndView save_withdraDeposit(HttpServletRequest request, HttpServletResponse response,
			String withdrawalAmount) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/success.html", this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		Long userId = SecurityUserHolder.getCurrentUser().getId();
		String url = this.configService.getSysConfig().getAddress();
		try {
			/** 赊销提现，银行接口调用开始 **/
			/** 赊销提现，银行接口调用结束 **/

			Map params = new HashMap();
			params.put("sellerId", userId.intValue());
			List creditLlineManagementList = this.creditLlineManagementService
					.query("select obj from CreditLlineManagement obj where obj.sellerId=:sellerId", params, -1, -1);
			CreditLlineManagement creditLlineManagement = (CreditLlineManagement) creditLlineManagementList.get(0);

			BigDecimal mayWithdrawalAmount_bd = new BigDecimal(creditLlineManagement.getMayWithdrawalAmount());// 可提现额度
			BigDecimal yetWithdrawalAmount_bd = new BigDecimal(creditLlineManagement.getYetWithdrawalAmount());// 已提现完成额度
			BigDecimal withdrawalAmount_bd = new BigDecimal(withdrawalAmount);// 提现额度

			int compare = mayWithdrawalAmount_bd.compareTo(withdrawalAmount_bd);
			if (compare == 1) {
				withdrawalAmount = (yetWithdrawalAmount_bd.add(withdrawalAmount_bd)).toString();
				creditLlineManagement.setYetWithdrawalAmount(withdrawalAmount);

				String mayWithdrawalAmount = (mayWithdrawalAmount_bd.subtract(withdrawalAmount_bd)).toString();
				creditLlineManagement.setMayWithdrawalAmount(mayWithdrawalAmount);
				this.creditLlineManagementService.update(creditLlineManagement);

				mv.addObject("op_title", "店主提现成功！");
				mv.addObject("url", CommUtil.getURL(request) + "/seller/quotaManagement.htm");
			} else {
				mv.addObject("op_title", "提现额度不能大于可提现额度！");
				mv.addObject("url", CommUtil.getURL(request) + "/seller/withdraDeposit.htm");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}

}
