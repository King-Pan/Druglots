package com.wemall.manage.buyer.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wemall.core.annotation.SecurityMapping;
import com.wemall.core.domain.virtual.SysMap;
import com.wemall.core.mv.JModelAndView;
import com.wemall.core.query.support.IPageList;
import com.wemall.core.security.support.SecurityUserHolder;
import com.wemall.core.tools.CommUtil;
import com.wemall.core.tools.HttpClass;
import com.wemall.foundation.domain.*;
import com.wemall.foundation.domain.query.OrderFormQueryObject;
import com.wemall.foundation.domain.virtual.TransInfo;
import com.wemall.foundation.service.*;
import com.wemall.manage.admin.tools.MsgTools;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.*;

/**
 * 买家订单控制器
 */
@Controller
public class OrderBuyerAction {
    @Autowired
    private ISysConfigService configService;
    
    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private IUserConfigService userConfigService;

    @Autowired
    private IOrderFormService orderFormService;

    @Autowired
    private IOrderFormLogService orderFormLogService;

    @Autowired
    private IEvaluateService evaluateService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IStoreService storeService;

    @Autowired
    private ITemplateService templateService;

    @Autowired
    private IStorePointService storePointService;

    @Autowired
    private IPredepositLogService predepositLogService;

    @Autowired
    private IPaymentService paymentService;

    @Autowired
    private IGoodsCartService goodsCartService;

    @Autowired
    private IGoodsReturnItemService goodsReturnItemService;

    @Autowired
    private IGoodsReturnService goodsReturnService;

    @Autowired
    private IExpressCompanyService expressCompayService;

    @Autowired
    private MsgTools msgTools;
    
    @Autowired
   	private IMessageService messageService;
    
    
    

	@SecurityMapping(display = false, rsequence = 0, title = "买家订单列表", value = "/buyer/order.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({"/buyer/order.htm"})
    public ModelAndView order(HttpServletRequest request, HttpServletResponse response, String currentPage, String order_id, String beginTime, String endTime, String order_status){
        ModelAndView mv = new JModelAndView("user/default/usercenter/buyer_order.html", this.configService.getSysConfig(),
                                            this.userConfigService.getUserConfig(), 0, request, response);

        
        String wemall_view_type = CommUtil.null2String(request.getSession().getAttribute("wemall_view_type"));
        if((wemall_view_type != null) && (!wemall_view_type.equals("")) && (wemall_view_type.equals("wap"))){
            mv = new JModelAndView("wap/buyer_order.html",
                                   this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }
        OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, mv, "addTime", "desc");
        ofqo.addQuery("obj.user.id", new SysMap("user_id", SecurityUserHolder.getCurrentUser().getId()), "=");
        if (!CommUtil.null2String(order_id).equals("")){
            ofqo.addQuery("obj.order_id", new SysMap("order_id", "%" + order_id + "%"), "like");
            mv.addObject("order_id", order_id);
        }
        if (!CommUtil.null2String(beginTime).equals("")){ 
            ofqo.addQuery("obj.addTime", new SysMap("beginTime", CommUtil.formatDate(beginTime)), ">=");
            mv.addObject("beginTime", beginTime);
        }
        if (!CommUtil.null2String(beginTime).equals("")){
            ofqo.addQuery("obj.addTime",
                          new SysMap("endTime", CommUtil.formatDate(endTime)), "<=");
            mv.addObject("endTime", endTime);
        }
        if (!CommUtil.null2String(order_status).equals("")){
            if (order_status.equals("order_submit")){
                ofqo.addQuery("obj.order_status",
                              new SysMap("order_status", Integer.valueOf(10)), "=");
            }
            if (order_status.equals("order_pay")){
                ofqo.addQuery("obj.order_status",
                              new SysMap("order_status", Integer.valueOf(20)), "=");
            }
            if (order_status.equals("order_shipping")){
                ofqo.addQuery("obj.order_status",
                              new SysMap("order_status", Integer.valueOf(30)), "=");
            }
            if (order_status.equals("order_receive")){
                ofqo.addQuery("obj.order_status",
                              new SysMap("order_status", Integer.valueOf(40)), "=");
            }
            if (order_status.equals("order_finish")){
                ofqo.addQuery("obj.order_status",
                              new SysMap("order_status", Integer.valueOf(50)), "=");
            }
            if (order_status.equals("order_cancel")){
                ofqo.addQuery("obj.order_status",
                              new SysMap("order_status", Integer.valueOf(0)), "=");
            }
        }
        
        mv.addObject("order_status", order_status);
        IPageList pList = this.orderFormService.list(ofqo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "买家订单列表", value = "/buyer/ajaxorder.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({"/buyer/ajaxorder.htm"})
    public void ajaxorder(HttpServletRequest request, HttpServletResponse response, String currentPage, String order_id, String beginTime, String endTime, String order_status){
        Map<String, Object> map = new HashMap<String, Object>();

        OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, map, "addTime", "desc");

        ofqo.addQuery("obj.user.id", new SysMap("user_id", SecurityUserHolder.getCurrentUser().getId()), "=");
        if (!CommUtil.null2String(order_id).equals("")){
            ofqo.addQuery("obj.order_id", new SysMap("order_id", "%" + order_id + "%"), "like");
            map.put("order_id", order_id);
        }
        if (!CommUtil.null2String(beginTime).equals("")){
            ofqo.addQuery("obj.addTime", new SysMap("beginTime", CommUtil.formatDate(beginTime)), ">=");
            map.put("beginTime", beginTime);
        }
        if (!CommUtil.null2String(beginTime).equals("")){
            ofqo.addQuery("obj.addTime",
                          new SysMap("endTime", CommUtil.formatDate(endTime)), "<=");
            map.put("endTime", endTime);
        }
        if (!CommUtil.null2String(order_status).equals("")){
            if (order_status.equals("order_submit")){
                ofqo.addQuery("obj.order_status",
                              new SysMap("order_status", Integer.valueOf(10)), "=");// 待付款
            }
            if (order_status.equals("order_pay")){
                ofqo.addQuery("obj.order_status",
                              new SysMap("order_status", Integer.valueOf(20)), "=");// 待发货
            }
            if (order_status.equals("order_shipping")){
                ofqo.addQuery("obj.order_status",
                              new SysMap("order_status", Integer.valueOf(30)), "=");// 已发货
            }
            if (order_status.equals("order_receive")){
                ofqo.addQuery("obj.order_status",
                              new SysMap("order_status", Integer.valueOf(40)), "=");// 已收货
            }
            if (order_status.equals("order_finish")){
                ofqo.addQuery("obj.order_status",
                              new SysMap("order_status", Integer.valueOf(60)), "=");// 已结束
            }
            if (order_status.equals("order_cancel")){
                ofqo.addQuery("obj.order_status",
                              new SysMap("order_status", Integer.valueOf(0)), "=");// 已取消
            }
        }
        map.put("order_status", order_status);

        IPageList pList = this.orderFormService.list(ofqo);

        CommUtil.saveWebPaths(map, this.configService.getSysConfig(), request);
        map.put("show", "orders");
        CommUtil.saveIPageList2Map("", "", "", pList, map);

        String ret = Json.toJson(map, JsonFormat.compact());
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(ret);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 取消订单
     * @param request
     * @param response
     * @param id
     * @param currentPage
     * @return
     */
    @SecurityMapping(display = false, rsequence = 0, title = "订单取消", value = "/buyer/order_cancel.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({"/buyer/order_cancel.htm"})
    public ModelAndView order_cancel(HttpServletRequest request, HttpServletResponse response, String id, String currentPage){
        ModelAndView mv = new JModelAndView("user/default/usercenter/buyer_order_cancel.html",
                                            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);

        String wemall_view_type = CommUtil.null2String(request.getSession().getAttribute("wemall_view_type"));

        if((wemall_view_type != null) && (!wemall_view_type.equals("")) && (wemall_view_type.equals("wap"))){
            mv = new JModelAndView("wap/buyer_order_cancel.html",
                                   this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));

        if (obj.getUser().getId().equals(SecurityUserHolder.getCurrentUser().getId())){
            mv.addObject("obj", obj);
            mv.addObject("currentPage", currentPage);
        }else{
            mv = new JModelAndView("error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "您没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
        }

        return mv;
    }

    /**
     * 订单取消确认
     * @param request
     * @param response
     * @param id
     * @param currentPage
     * @param state_info
     * @param other_state_info
     * @return
     * @throws Exception
     */
    @SecurityMapping(display = false, rsequence = 0, title = "订单取消确认", value = "/buyer/order_cancel_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({"/buyer/order_cancel_save.htm"})
    public String order_cancel_save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String state_info, String other_state_info) throws Exception {
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));

        if (obj.getUser().getId().equals(SecurityUserHolder.getCurrentUser().getId())){
            obj.setOrder_status(0);
            this.orderFormService.update(obj);
            OrderFormLog ofl = new OrderFormLog();
            ofl.setAddTime(new Date());
            ofl.setLog_info("取消订单");
            ofl.setLog_user(SecurityUserHolder.getCurrentUser());
            ofl.setOf(obj);
            if (state_info.equals("other"))
                ofl.setState_info(other_state_info);
           else{
                ofl.setState_info(state_info);
            }
            this.orderFormLogService.save(ofl);
            List<GoodsCart> gcs = obj.getGcs();
            for (GoodsCart goodsCart : gcs) {
            	Goods goods = this.goodsService.getObjById(goodsCart.getGoods().getId());
                goods.setGoods_inventory(goods.getGoods_inventory()+goodsCart.getCount());
                this.goodsService.update(goods);
			}
            if (this.configService.getSysConfig().isEmailEnable()){
                send_email(request, obj, "email_toseller_order_cancel_notify");
            }
            if (this.configService.getSysConfig().isSmsEnbale()){
                send_sms(request, obj, obj.getStore().getUser().getMobile(), "sms_toseller_order_cancel_notify");
            }
        }

        return "redirect:order.htm?currentPage=" + currentPage;
    }

    String id1="";
    @SecurityMapping(display = false, rsequence = 0, title = "收货确认", value = "/buyer/order_cofirm.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({"/buyer/order_cofirm.htm"})
    public ModelAndView order_cofirm(HttpServletRequest request, HttpServletResponse response, String id, String currentPage){
        ModelAndView mv = new JModelAndView("user/default/usercenter/buyer_order_cofirm.html",
                                            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
        String wemall_view_type = CommUtil.null2String(request.getSession().getAttribute("wemall_view_type"));
        if((wemall_view_type != null) && (!wemall_view_type.equals("")) && (wemall_view_type.equals("wap"))){
            mv = new JModelAndView("wap/buyer_order_cofirm.html",
                                   this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
        }
        id1=id;
        double totalprice=0;
        int count = 0;
        String[] ids = id.split(",");
		for (String orderId : ids) {
			Map params = new HashMap();
			params.put("id", Long.parseLong(orderId));
			List<OrderForm> list = this.orderFormService.query(
					"select obj from OrderForm obj where obj.id=:id", params, -1, -1);
			 OrderForm orderForm = list.get(0);
				if(orderForm.getOrder_status() == 30) {
					totalprice=totalprice+orderForm.getTotalPrice().doubleValue();
					count++;
				}
        if (orderForm.getUser().getId().equals(SecurityUserHolder.getCurrentUser().getId())){
            mv.addObject("obj", orderForm);
            mv.addObject("currentPage", currentPage);
        }else{
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "您没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
          }
        if(ids.length>1) {
        	//总金额
			mv.addObject("totalprice",totalprice);
			//总条数
			mv.addObject("count",count);
		}
        //全部id
        mv.addObject("id",id1);
	 }
		
		
		
        return mv;
    }

  //收货验证码是否正确
    @RequestMapping("/checkSave.htm")
    public void checkSave(HttpServletRequest request, HttpServletResponse response,String phonecode){
    	int  code=1;
    	if (request.getSession().getAttribute(SecurityUserHolder.getCurrentUser().getMobile())==null) {
    		code=2;
    	}else if(request.getSession().getAttribute(SecurityUserHolder.getCurrentUser().getMobile()).equals(phonecode)){
      	  code=1;
    	}else {
          code=3;
		}
    	try {
			response.setContentType("text/html; charset=UTF-8"); //转码
      	    PrintWriter out = response.getWriter();
      	    out.println(code);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    /**
     * 买家确认收货
     * @param request
     * @param response
     * @param id
     * @param currentPage
     * @return
     * @throws Exception
     */
    @SecurityMapping(display = false, rsequence = 0, title = "收货确认保存", value = "/buyer/order_cofirm_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({"/buyer/order_cofirm_save.htm"})
    public String order_cofirm_save(HttpServletRequest request, HttpServletResponse response, String currentPage) throws Exception {
        	String[] ids = id1.split(",");
    		for (String orderId : ids) {
    			Map params = new HashMap();
    			params.put("id", Long.parseLong(orderId));
    			List<OrderForm> list = this.orderFormService.query(
    					"select obj from OrderForm obj where obj.id=:id", params, -1, -1);
    		OrderForm obj = list.get(0);
            obj.setOrder_status(40);
            boolean ret = this.orderFormService.update(obj);
            String content="买家已收货：订单"+ obj.getOrder_id() +"买家已经确认收货！前往“商家中心”——“我的 订单”查看详情。";
            Message msg1 = new Message();
            msg1.setAddTime(new Date());
            Whitelist whiteList = new Whitelist();
            content = content.replaceAll("\n", "iskyhop_br");
            msg1.setContent(Jsoup.clean(content, Whitelist.basic())
                           .replaceAll("iskyhop_br", "\n"));
            msg1.setFromUser(SecurityUserHolder.getCurrentUser());
            msg1.setToUser(obj.getStore().getUser());
            msg1.setType(1);
            this.messageService.save(msg1);
            if (ret){
                OrderFormLog ofl = new OrderFormLog();
                ofl.setAddTime(new Date());
                ofl.setLog_info("确认收货");
                ofl.setLog_user(SecurityUserHolder.getCurrentUser());
                ofl.setOf(obj);
                this.orderFormLogService.save(ofl);
                if (this.configService.getSysConfig().isEmailEnable()){
                    send_email(request, obj, "email_toseller_order_receive_ok_notify");
                }
                if (this.configService.getSysConfig().isSmsEnbale()){
                    send_sms(request, obj, obj.getStore().getUser().getMobile(), "sms_toseller_order_receive_ok_notify");
                }
                if (obj.getPayment() !=null && !obj.getPayment().equals("")) {
                if (obj.getPayment().getMark().equals("balance")){
                    User seller = this.userService.getObjById(obj.getStore().getUser().getId());
                    if (this.configService.getSysConfig().getBalance_fenrun() == 1){
                        params.clear();
                        params.put("type", "admin");
                        params.put("mark", "balance");
                        List payments = this.paymentService.query("select obj from Payment obj where obj.type=:type and obj.mark=:mark", params, -1, -1);
                        Payment shop_payment = new Payment();
                        if (payments.size() > 0){
                            shop_payment = (Payment)payments.get(0);
                        }

                        double shop_availableBalance = CommUtil.null2Double(obj.getTotalPrice()) * CommUtil.null2Double(shop_payment.getBalance_divide_rate());
                        User admin = this.userService.getObjByProperty("userName", "admin");
                        admin.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(admin.getAvailableBalance(), Double.valueOf(shop_availableBalance))));
                        this.userService.update(admin);
                        PredepositLog log = new PredepositLog();
                        log.setAddTime(new Date());
                        log.setPd_log_user(seller);
                        log.setPd_op_type("分润");
                        log.setPd_log_amount(BigDecimal.valueOf(shop_availableBalance));
                        log.setPd_log_info("订单" + obj.getOrder_id() + "确认收货平台分润获得预存款");
                        log.setPd_type("可用预存款");
                        this.predepositLogService.save(log);

                        double seller_availableBalance = CommUtil.null2Double(obj.getTotalPrice()) - shop_availableBalance;
                        seller.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(seller.getAvailableBalance(), Double.valueOf(seller_availableBalance))));
                        this.userService.update(seller);
                        PredepositLog log1 = new PredepositLog();
                        log1.setAddTime(new Date());
                        log1.setPd_log_user(seller);
                        log1.setPd_op_type("增加");
                        log1.setPd_log_amount(BigDecimal.valueOf(seller_availableBalance));
                        log1.setPd_log_info("订单" + obj.getOrder_id() + "确认收货增加预存款");
                        log1.setPd_type("可用预存款");
                        this.predepositLogService.save(log1);

                        User buyer = obj.getUser();
                        buyer.setFreezeBlance(BigDecimal.valueOf(CommUtil.subtract(buyer.getFreezeBlance(), obj.getTotalPrice())));
                        this.userService.update(buyer);
                    }else{
                        seller.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(seller.getAvailableBalance(), obj.getTotalPrice())));
                        this.userService.update(seller);
                        PredepositLog log = new PredepositLog();
                        log.setAddTime(new Date());
                        log.setPd_log_user(seller);
                        log.setPd_op_type("增加");
                        log.setPd_log_amount(obj.getTotalPrice());
                        log.setPd_log_info("订单" + obj.getOrder_id() + "确认收货增加预存款");
                        log.setPd_type("可用预存款");
                        this.predepositLogService.save(log);

                        User buyer = obj.getUser();
                        buyer.setFreezeBlance(BigDecimal.valueOf(CommUtil.subtract(buyer.getFreezeBlance(), obj.getTotalPrice())));
                        this.userService.update(buyer);
                    }
                }
              }
            }
        }
       
        String url = "redirect:order.htm?currentPage=" + currentPage;

        return url;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "买家评价", value = "/buyer/order_evaluate.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({"/buyer/order_evaluate.htm"})
    public ModelAndView order_evaluate(HttpServletRequest request, HttpServletResponse response, String id){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/buyer_order_evaluate.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));

        if (obj.getUser().getId().equals(SecurityUserHolder.getCurrentUser().getId())){
            mv.addObject("obj", obj);
            if (obj.getOrder_status() >= 50){
                mv = new JModelAndView("success.html", this.configService.getSysConfig(),
                                       this.userConfigService.getUserConfig(), 1, request, response);
                mv.addObject("op_title", "订单已经评价！");
                mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
            }
        }else{
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "您没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
        }

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "买家评价保存", value = "/buyer/order_evaluate_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({"/buyer/order_evaluate_save.htm"})
    public ModelAndView order_evaluate_save(HttpServletRequest request, HttpServletResponse response, String id) throws Exception {
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));

        if (obj.getUser().getId().equals(SecurityUserHolder.getCurrentUser().getId())){
            if (obj.getOrder_status() == 40){
                obj.setOrder_status(50);
                this.orderFormService.update(obj);
                OrderFormLog ofl = new OrderFormLog();
                ofl.setAddTime(new Date());
                ofl.setLog_info("评价订单");
                ofl.setLog_user(SecurityUserHolder.getCurrentUser());
                ofl.setOf(obj);
                this.orderFormLogService.save(ofl);
                for (GoodsCart gc : obj.getGcs()){
                    Evaluate eva = new Evaluate();
                    eva.setAddTime(new Date());
                    eva.setEvaluate_goods(gc.getGoods());
                    eva.setEvaluate_info(request.getParameter("evaluate_info_" + gc.getId()));
                    eva.setEvaluate_buyer_val(CommUtil.null2Int(request.getParameter("evaluate_buyer_val" + gc.getId())));
                    eva.setDescription_evaluate(BigDecimal.valueOf(
                                                    CommUtil.null2Double(request.getParameter("description_evaluate" + gc.getId()))));
                    eva.setService_evaluate(BigDecimal.valueOf(CommUtil.null2Double(request.getParameter("service_evaluate" + gc.getId()))));
                    eva.setShip_evaluate(BigDecimal.valueOf(CommUtil.null2Double(request.getParameter("ship_evaluate" + gc.getId()))));
                    eva.setEvaluate_type("goods");
                    eva.setEvaluate_user(SecurityUserHolder.getCurrentUser());
                    eva.setOf(obj);
                    eva.setGoods_spec(gc.getSpec_info());
                    this.evaluateService.save(eva);
                    Map params = new HashMap();
                    params.put("store_id", obj.getStore().getId());
                    List<Evaluate> evas = this.evaluateService.query("select obj from Evaluate obj where obj.of.store.id=:store_id", params, -1, -1);
                    double store_evaluate1 = 0.0D;
                    double store_evaluate1_total = 0.0D;
                    double description_evaluate = 0.0D;
                    double description_evaluate_total = 0.0D;
                    double service_evaluate = 0.0D;
                    double service_evaluate_total = 0.0D;
                    double ship_evaluate = 0.0D;
                    double ship_evaluate_total = 0.0D;
                    DecimalFormat df = new DecimalFormat("0.0");
                    for (Evaluate eva1 : evas){
                        store_evaluate1_total = store_evaluate1_total + eva1.getEvaluate_buyer_val();

                        description_evaluate_total = description_evaluate_total + CommUtil.null2Double(eva1.getDescription_evaluate());

                        service_evaluate_total = service_evaluate_total + CommUtil.null2Double(eva1.getService_evaluate());

                        ship_evaluate_total = ship_evaluate_total + CommUtil.null2Double(eva1.getShip_evaluate());
                    }
                    store_evaluate1 = CommUtil.null2Double(df.format(store_evaluate1_total / evas.size()));
                    description_evaluate = CommUtil.null2Double(df.format(description_evaluate_total / evas.size()));
                    service_evaluate = CommUtil.null2Double(df.format(service_evaluate_total / evas.size()));
                    ship_evaluate = CommUtil.null2Double(df.format(ship_evaluate_total / evas.size()));
                    Store store = obj.getStore();
                    store.setStore_credit(store.getStore_credit() + eva.getEvaluate_buyer_val());
                    this.storeService.update(store);
                    params.clear();
                    params.put("store_id", store.getId());
                    List sps = this.storePointService.query("select obj from StorePoint obj where obj.store.id=:store_id", params, -1, -1);
                    StorePoint point = null;
                    if (sps.size() > 0)
                        point = (StorePoint)sps.get(0);
                   else{
                        point = new StorePoint();
                    }
                    point.setAddTime(new Date());
                    point.setStore(store);
                    point.setDescription_evaluate(BigDecimal.valueOf(description_evaluate));
                    point.setService_evaluate(BigDecimal.valueOf(service_evaluate));
                    point.setShip_evaluate(BigDecimal.valueOf(ship_evaluate));
                    point.setStore_evaluate1(BigDecimal.valueOf(store_evaluate1));
                    if (sps.size() > 0)
                        this.storePointService.update(point);
                   else{
                        this.storePointService.save(point);
                    }

                    User user = obj.getUser();
                    user.setIntegral(user.getIntegral() + this.configService.getSysConfig().getIndentComment());
                    this.userService.update(user);
                }
            }
            if (this.configService.getSysConfig().isEmailEnable()){
                send_email(request, obj, "email_toseller_evaluate_ok_notify");
            }
        }
        ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(),
                                            this.userConfigService.getUserConfig(), 1, request, response);
        mv.addObject("op_title", "订单评价成功！");
        mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "删除订单信息", value = "/buyer/order_delete.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({"/buyer/order_delete.htm"})
    public String order_delete(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) throws Exception {
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));

        if ((obj.getUser().getId().equals(SecurityUserHolder.getCurrentUser().getId())) &&
                (obj.getOrder_status() == 0)){
            for (GoodsCart gc : obj.getGcs()){
                gc.getGsps().clear();
                this.goodsCartService.delete(gc.getId());
            }
            this.orderFormService.delete(obj.getId());
        }

        return "redirect:order.htm?currentPage=" + currentPage;
    }

    
   
    //电商ID
  	private String EBusinessID="1465298";
  	//电商加密私钥
  	private String AppKey="2e3d9844-ece2-4791-8da5-7872c9735d53";
  	//请求url
  	private String ReqURL="http://api.kdniao.com/Ebusiness/EbusinessOrderHandle.aspx";
    @SecurityMapping(display = false, rsequence = 0, title = "买家订单详情", value = "/buyer/order_view.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({"/buyer/order_view.htm"})
    public ModelAndView order_view(HttpServletRequest request, HttpServletResponse response, String id) throws Exception{
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/order_view.html",
            this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        	OrderForm obj = this.orderFormService
                        .getObjById(CommUtil.null2Long(id));
        	String shipCode = obj.getShipCode();//物流单号
        	String company_mark = obj.getTransport();//快递编码
        if (obj.getUser().getId()
                .equals(SecurityUserHolder.getCurrentUser().getId())){
            mv.addObject("obj", obj);
            TransInfo transInfo = query_ship_getData(
                                      CommUtil.null2String(obj.getId()));
            mv.addObject("transInfo", transInfo);
        }else{
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request,
                                   response);
            mv.addObject("op_title", "您没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
        }
        //查发票信息
        HttpClass hc = new	HttpClass();
        try {
			String Load = hc.load("http://127.0.0.1:8081/ssm_project/stoinvoice", "storeid="+obj.getOrder_id());
			String Load2 = hc.load("http://127.0.0.1:8081/ssm_project/storecord", "storeid="+obj.getOrder_id());
			Invoice invoice = JSON.parseObject(Load, Invoice.class);
			InvoiceRecord invoiceRecord = JSON.parseObject(Load2, InvoiceRecord.class);
			mv.addObject("invoiceRecord", invoiceRecord);
			mv.addObject("invoice", invoice);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        
        /**
        * Json方式 查询订单物流轨迹
   	 * @throws Exception 
        */
          // company_mark快递公司编码          shipCode物流单号
   		String requestData= "{'OrderCode':'','ShipperCode':'" + company_mark + "','LogisticCode':'" + shipCode + "'}";
   		Map<String, String> params = new HashMap<String, String>();
   		//请求内容需进行URL(utf-8)编码。请求内容JSON格式，须和DataType一致。
   		params.put("RequestData", urlEncoder(requestData, "UTF-8"));
   		//商户ID
   		params.put("EBusinessID", EBusinessID);
   		//请求指令类型：1002
   		params.put("RequestType", "1002");
   		//数据内容签名
   		String dataSign=encrypt(requestData, AppKey, "UTF-8");
   		params.put("DataSign", urlEncoder(dataSign, "UTF-8"));
   		//返回数据类型
   		params.put("DataType", "2");

   		//接口返回给我的是json字符串   这里转成string类型
   		String result=sendPost(ReqURL, params);
   		
   	   //string转成JSONObject 
   	    JSONObject jsStr = JSONObject.parseObject(result);
   	    //mv.addObject("jsStr",jsStr);
   	    
   	    //取第二级节点 是一个JSONArray
   		JSONArray  arr = jsStr.getJSONArray("Traces");
   		mv.addObject("arr",arr);
        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "买家物流详情", value = "/buyer/ship_view.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({"/buyer/ship_view.htm"})
    public ModelAndView order_ship_view(HttpServletRequest request, HttpServletResponse response, String id){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/order_ship_view.html",
            this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        OrderForm obj = this.orderFormService
                        .getObjById(CommUtil.null2Long(id));
        if ((obj != null) && (!obj.equals(""))){
            if (obj.getUser().getId()
                    .equals(SecurityUserHolder.getCurrentUser().getId())){
                mv.addObject("obj", obj);
                TransInfo transInfo = query_ship_getData(
                                          CommUtil.null2String(obj.getId()));
                mv.addObject("transInfo", transInfo);
            }else{
                mv = new JModelAndView("error.html",
                                       this.configService.getSysConfig(),
                                       this.userConfigService.getUserConfig(), 1, request,
                                       response);
                mv.addObject("op_title", "您查询的物流不存在！");
                mv.addObject("url", CommUtil.getURL(request) +
                             "/buyer/order.htm");
            }
        }else{
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request,
                                   response);
            mv.addObject("op_title", "您查询的物流不存在！");
            mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
        }

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "物流跟踪查询", value = "/buyer/query_ship.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({"/buyer/query_ship.htm"})
    public ModelAndView query_ship(HttpServletRequest request, HttpServletResponse response, String id) throws Exception{
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/query_ship_data.html",
            this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        TransInfo info = query_ship_getData(id);
        mv.addObject("transInfo", info);
     
        	OrderForm obj = this.orderFormService
                    .getObjById(CommUtil.null2Long(id));
    		String shipCode = obj.getShipCode();//物流单号
    		String company_mark = obj.getTransport();//快递编码
		
        /**
         * Json方式 查询订单物流轨迹
    	 * @throws Exception 
         */
           // company_mark快递公司编码          shipCode物流单号
    		String requestData= "{'OrderCode':'','ShipperCode':'" + company_mark + "','LogisticCode':'" + shipCode + "'}";
    		Map<String, String> params = new HashMap<String, String>();
    		//请求内容需进行URL(utf-8)编码。请求内容JSON格式，须和DataType一致。
    		params.put("RequestData", urlEncoder(requestData, "UTF-8"));
    		//商户ID
    		params.put("EBusinessID", EBusinessID);
    		//请求指令类型：1002
    		params.put("RequestType", "1002");
    		//数据内容签名
    		String dataSign=encrypt(requestData, AppKey, "UTF-8");
    		params.put("DataSign", urlEncoder(dataSign, "UTF-8"));
    		//返回数据类型
    		params.put("DataType", "2");

    		//接口返回给我的是json字符串   这里转成string类型
    		String result=sendPost(ReqURL, params);
    	   //string转成JSONObject 
    	    JSONObject jsStr = JSONObject.parseObject(result);
    	    //mv.addObject("jsStr",jsStr);
    	    
    	    //取第二级节点 是一个JSONArray
    		JSONArray  arr = jsStr.getJSONArray("Traces");
    		mv.addObject("arr",arr);
        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "虚拟商品信息", value = "/buyer/order_seller_intro.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({"/buyer/order_seller_intro.htm"})
    public ModelAndView order_seller_intro(HttpServletRequest request, HttpServletResponse response, String id){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/order_seller_intro.html",
            this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        OrderForm obj = this.orderFormService
                        .getObjById(CommUtil.null2Long(id));

        if (obj.getUser().getId()
                .equals(SecurityUserHolder.getCurrentUser().getId())){
            mv.addObject("obj", obj);
        }

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "买家退货申请", value = "/buyer/order_return_apply.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({"/buyer/order_return_apply.htm"})
    public ModelAndView order_return_apply(HttpServletRequest request, HttpServletResponse response, String id, String view){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/order_return_apply.html",
            this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        OrderForm obj = this.orderFormService
                        .getObjById(CommUtil.null2Long(id));

        if (obj.getUser().getId()
                .equals(SecurityUserHolder.getCurrentUser().getId())){
            mv.addObject("obj", obj);
            if ((view != null) && (!view.equals("")))
                mv.addObject("view", Boolean.valueOf(true));
        }else{
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request,
                                   response);
            mv.addObject("op_title", "您没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
        }

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "买家退货申请保存", value = "/buyer/order_return_apply_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({"/buyer/order_return_apply_save.htm"})
    public String order_return_apply_save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String return_content) throws Exception {
        OrderForm obj = this.orderFormService
                        .getObjById(CommUtil.null2Long(id));

        if (obj.getUser().getId()
                .equals(SecurityUserHolder.getCurrentUser().getId())){
            obj.setOrder_status(45);
            obj.setReturn_content(return_content);
            this.orderFormService.update(obj);
            if (this.configService.getSysConfig().isEmailEnable()){
//                send_email(request, obj,
//                           "email_toseller_order_return_apply_notify");
            }
            if (this.configService.getSysConfig().isSmsEnbale()){
                send_sms(request, obj, obj.getUser().getMobile(),
                         "sms_toseller_order_return_apply_notify");
            }
        }

        return "redirect:order.htm?currentPage=" + currentPage;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "买家退商品流信息", value = "/buyer/order_return_ship.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({"/buyer/order_return_ship.htm"})
    public ModelAndView order_return_ship(HttpServletRequest request, HttpServletResponse response, String id, String currentPage){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/order_return_ship.html",
            this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        OrderForm obj = this.orderFormService
                        .getObjById(CommUtil.null2Long(id));

        if (obj.getUser().getId()
                .equals(SecurityUserHolder.getCurrentUser().getId())){
            mv.addObject("obj", obj);
            mv.addObject("currentPage", currentPage);

            Map map = new HashMap();
            map.put("oid", CommUtil.null2Long(id));
            List<GoodsCart> goodsCarts = this.goodsCartService.query(
                                             "select obj from GoodsCart obj where obj.of.id = :oid",
                                             map, -1, -1);
            List deliveryGoods = new ArrayList();
            boolean physicalGoods = false;
            for (GoodsCart gc : goodsCarts){
                if (gc.getGoods().getGoods_choice_type() == 1)
                    deliveryGoods.add(gc);
               else{
                    physicalGoods = true;
                }
            }
            Map params = new HashMap();
            params.put("status", Integer.valueOf(0));
            List expressCompanys = this.expressCompayService
                                   .query("select obj from ExpressCompany obj where obj.company_status=:status order by company_sequence asc",
                                          params, -1, -1);
            mv.addObject("expressCompanys", expressCompanys);
            mv.addObject("physicalGoods", Boolean.valueOf(physicalGoods));
            mv.addObject("deliveryGoods", deliveryGoods);
        }else{
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request,
                                   response);
            mv.addObject("op_title", "您没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
        }

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "买家退商品流信息保存", value = "/buyer/order_return_ship_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({"/buyer/order_return_ship_save.htm"})
    public String order_return_ship_save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String ec_id, String return_shipCode){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/order_return_apply_view.html",
            this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        OrderForm obj = this.orderFormService
                        .getObjById(CommUtil.null2Long(id));
        ExpressCompany ec = this.expressCompayService.getObjById(
                                CommUtil.null2Long(ec_id));
        obj.setReturn_ec(ec);
        obj.setReturn_shipCode(return_shipCode);
        this.orderFormService.update(obj);

        return "redirect:order.htm?currentPage=" + currentPage;
    }

    private TransInfo query_ship_getData(String id){
        TransInfo info = new TransInfo();
        OrderForm obj = this.orderFormService
                        .getObjById(CommUtil.null2Long(id));
        try {
            String query_url = "http://api.kuaidi100.com/api?id=" +
                               this.configService.getSysConfig().getKuaidi_id() +
                               "&com=" + (
                                   obj.getEc() != null ? obj.getEc().getCompany_mark() : "") +
                               "&nu=" + obj.getShipCode() + "&show=0&muti=1&order=asc";
            URL url = new URL(query_url);
            URLConnection con = url.openConnection();
            con.setAllowUserInteraction(false);
            InputStream urlStream = url.openStream();
            String type = URLConnection.guessContentTypeFromStream(urlStream);
            String charSet = null;
            if (type == null)
                type = con.getContentType();
            if ((type == null) || (type.trim().length() == 0) ||
                    (type.trim().indexOf("text/html") < 0))
                return info;
            if (type.indexOf("charset=") > 0)
                charSet = type.substring(type.indexOf("charset=") + 8);
            byte[] b = new byte[10000];
            int numRead = urlStream.read(b);
            String content = new String(b, 0, numRead, charSet);
            while (numRead != -1){
                numRead = urlStream.read(b);
                if (numRead == -1)
                    continue;
                String newContent = new String(b, 0, numRead, charSet);
                content = content + newContent;
            }

            info = (TransInfo)Json.fromJson(TransInfo.class, content);
            urlStream.close();
        } catch (MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

        return info;
    }

    private void send_email(HttpServletRequest request, OrderForm order, String mark) throws Exception {
        com.wemall.foundation.domain.Template template = this.templateService.getObjByProperty("mark", mark);
        if (template.isOpen()){
            String email = order.getStore().getUser().getEmail();
            String subject = template.getTitle();
            String path = request.getSession().getServletContext()
                          .getRealPath("/") +
                          "/vm/";
            PrintWriter pwrite = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(path + "msg.vm", false), "UTF-8"));
            pwrite.print(template.getContent());
            pwrite.flush();
            pwrite.close();

            Properties p = new Properties();
            p.setProperty("file.resource.loader.path",
                          request.getRealPath("/") + "vm" + File.separator);
            p.setProperty("input.encoding", "UTF-8");
            p.setProperty("output.encoding", "UTF-8");
            Velocity.init(p);
            org.apache.velocity.Template blank = Velocity.getTemplate("msg.vm",
                                                 "UTF-8");
            VelocityContext context = new VelocityContext();
            context.put("buyer", order.getUser());
            context.put("seller", order.getStore().getUser());
            context.put("config", this.configService.getSysConfig());
            context.put("send_time", CommUtil.formatLongDate(new Date()));
            context.put("webPath", CommUtil.getURL(request));
            context.put("order", order);
            StringWriter writer = new StringWriter();
            blank.merge(context, writer);

            String content = writer.toString();
            this.msgTools.sendEmail(email, subject, content);
        }
    }

    private void send_sms(HttpServletRequest request, OrderForm order, String mobile, String mark) throws Exception {
        com.wemall.foundation.domain.Template template = this.templateService.getObjByProperty("mark", mark);
        if (template.isOpen()){
            String path = request.getSession().getServletContext()
                          .getRealPath("/") +
                          "/vm/";
            PrintWriter pwrite = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(path + "msg.vm", false), "UTF-8"));
            pwrite.print(template.getContent());
            pwrite.flush();
            pwrite.close();

            Properties p = new Properties();
            p.setProperty("file.resource.loader.path",
                          request.getRealPath("/") + "vm" + File.separator);
            p.setProperty("input.encoding", "UTF-8");
            p.setProperty("output.encoding", "UTF-8");
            Velocity.init(p);
            org.apache.velocity.Template blank = Velocity.getTemplate("msg.vm",
                                                 "UTF-8");
            VelocityContext context = new VelocityContext();
            context.put("buyer", order.getUser());
            context.put("seller", order.getStore().getUser());
            context.put("config", this.configService.getSysConfig());
            context.put("send_time", CommUtil.formatLongDate(new Date()));
            context.put("webPath", CommUtil.getURL(request));
            context.put("order", order);
            StringWriter writer = new StringWriter();
            blank.merge(context, writer);

            String content = writer.toString();
            this.msgTools.sendSMS(mobile, content);
        }
    }


    /**
     * wap服务中心
     * @param request
     * @param response
     * @return
     */
    @SecurityMapping(display = false, rsequence = 0, title = "服务中心", value = "/buyer/service_center.htm*", rtype = "buyer", rname = "服务中心", rcode = "user_center", rgroup = "服务中心")
    @RequestMapping({ "/buyer/service_center.htm" })
    public ModelAndView service_center(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("wap/service_center.html",
                                            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);

        return mv;
    }
    
    /**
     * MD5加密
     * @param str 内容       
     * @param charset 编码方式
	 * @throws Exception 
     */
	@SuppressWarnings("unused")
	private String MD5(String str, String charset) throws Exception {
	    MessageDigest md = MessageDigest.getInstance("MD5");
	    md.update(str.getBytes(charset));
	    byte[] result = md.digest();
	    StringBuffer sb = new StringBuffer(32);
	    for (int i = 0; i < result.length; i++) {
	        int val = result[i] & 0xff;
	        if (val <= 0xf) {
	            sb.append("0");
	        }
	        sb.append(Integer.toHexString(val));
	    }
	    return sb.toString().toLowerCase();
	}
	
	/**
     * base64编码
     * @param str 内容       
     * @param charset 编码方式
	 * @throws UnsupportedEncodingException 
     */
	private String base64(String str, String charset) throws UnsupportedEncodingException{
		String encoded = base64Encode(str.getBytes(charset));
		return encoded;    
	}	
	
	@SuppressWarnings("unused")
	private String urlEncoder(String str, String charset) throws UnsupportedEncodingException{
		String result = URLEncoder.encode(str, charset);
		return result;
	}
	
	/**
     * 电商Sign签名生成
     * @param content 内容   
     * @param keyValue Appkey  
     * @param charset 编码方式
	 * @throws UnsupportedEncodingException ,Exception
	 * @return DataSign签名
     */
	@SuppressWarnings("unused")
	private String encrypt (String content, String keyValue, String charset) throws UnsupportedEncodingException, Exception
	{
		if (keyValue != null)
		{
			return base64(MD5(content + keyValue, charset), charset);
		}
		return base64(MD5(content, charset), charset);
	}
	
	 /**
     * 向指定 URL 发送POST方法的请求     
     * @param url 发送请求的 URL    
     * @param params 请求的参数集合     
     * @return 远程资源的响应结果
     */
	@SuppressWarnings("unused")
	private String sendPost(String url, Map<String, String> params) {
        OutputStreamWriter out = null;
        BufferedReader in = null;        
        StringBuilder result = new StringBuilder(); 
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn =(HttpURLConnection) realUrl.openConnection();
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // POST方法
            conn.setRequestMethod("POST");
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.connect();
            // 获取URLConnection对象对应的输出流
            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            // 发送请求参数            
            if (params != null) {
		          StringBuilder param = new StringBuilder(); 
		          for (Map.Entry<String, String> entry : params.entrySet()) {
		        	  if(param.length()>0){
		        		  param.append("&");
		        	  }	        	  
		        	  param.append(entry.getKey());
		        	  param.append("=");
		        	  param.append(entry.getValue());		        	  
		        	  //System.out.println(entry.getKey()+":"+entry.getValue());
		          }
		          //System.out.println("param:"+param.toString());
		          out.write(param.toString());
            }
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {            
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result.toString();
    }
	
	
    private static char[] base64EncodeChars = new char[] { 
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 
        'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 
        'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 
        'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 
        'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 
        'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 
        'w', 'x', 'y', 'z', '0', '1', '2', '3', 
        '4', '5', '6', '7', '8', '9', '+', '/' }; 
	
    public static  String base64Encode(byte[] data) { 
        StringBuffer sb = new StringBuffer(); 
        int len = data.length; 
        int i = 0; 
        int b1, b2, b3; 
        while (i < len) { 
            b1 = data[i++] & 0xff; 
            if (i == len) 
            { 
                sb.append(base64EncodeChars[b1 >>> 2]); 
                sb.append(base64EncodeChars[(b1 & 0x3) << 4]); 
                sb.append("=="); 
                break; 
            } 
            b2 = data[i++] & 0xff; 
            if (i == len) 
            { 
                sb.append(base64EncodeChars[b1 >>> 2]); 
                sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]); 
                sb.append(base64EncodeChars[(b2 & 0x0f) << 2]); 
                sb.append("="); 
                break; 
            } 
            b3 = data[i++] & 0xff; 
            sb.append(base64EncodeChars[b1 >>> 2]); 
            sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]); 
            sb.append(base64EncodeChars[((b2 & 0x0f) << 2) | ((b3 & 0xc0) >>> 6)]); 
            sb.append(base64EncodeChars[b3 & 0x3f]); 
        } 
        return sb.toString(); 
    }

}




