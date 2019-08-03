package com.wemall.manage.seller.action;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wemall.core.annotation.SecurityMapping;
import com.wemall.core.domain.virtual.SysMap;
import com.wemall.core.mv.JModelAndView;
import com.wemall.core.query.support.IPageList;
import com.wemall.core.security.support.SecurityUserHolder;
import com.wemall.core.tools.CommUtil;
import com.wemall.foundation.domain.*;
import com.wemall.foundation.domain.query.OrderFormQueryObject;
import com.wemall.foundation.domain.virtual.TransInfo;
import com.wemall.foundation.service.*;
import com.wemall.manage.admin.tools.MsgTools;
import com.wemall.manage.admin.tools.PaymentTools;
import com.wemall.pay.alipay.config.AlipayConfig;
import com.wemall.pay.alipay.util.AlipaySubmit;
import com.wemall.view.web.action.LoginViewAction;
import com.wemall.view.web.tools.StoreViewTools;
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
import java.util.*;

/**
 * 卖家订单控制器
 */
@Controller
public class OrderSellerAction {
    @Autowired
    private ISysConfigService configService;

    @Autowired
    private IUserConfigService userConfigService;

    @Autowired
    private IOrderFormService orderFormService;

    @Autowired
    private IOrderFormLogService orderFormLogService;

    @Autowired
    private IRefundLogService refundLogService;

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private IGoodsReturnService goodsReturnService;

    @Autowired
    private IGoodsReturnItemService goodsReturnItemService;

    @Autowired
    private IGoodsReturnLogService goodsReturnLogService;

    @Autowired
    private IGoodsCartService goodsCartService;

    @Autowired
    private IEvaluateService evaluateService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IIntegralLogService integralLogService;

    @Autowired
    private IGroupGoodsService groupGoodsService;

    @Autowired
    private ITemplateService templateService;

    @Autowired
    private IPaymentService paymentService;

    @Autowired
    private IExpressCompanyService expressCompayService;

    @Autowired
    private StoreViewTools storeViewTools;

    @Autowired
    private MsgTools msgTools;
    
    @Autowired
   	private IMessageService messageService;

    @Autowired
    private PaymentTools paymentTools;
    
    @Autowired
    private BuyerCreditLimitService buyerCreditLimitService;
   

    @SecurityMapping(display = false, rsequence = 0, title = "卖家订单列表", value = "/seller/order.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({"/seller/order.htm"})
    public ModelAndView order(HttpServletRequest request, HttpServletResponse response, String currentPage, String order_status, String order_id, String beginTime, String endTime, String buyer_userName){
        ModelAndView mv = mv = new JModelAndView(
            "user/default/usercenter/seller_order.html",
            this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, mv,
                "addTime", "desc");
        ofqo.addQuery("obj.store.user.id",
                      new SysMap("user_id",
                                 SecurityUserHolder.getCurrentUser().getId()), "=");
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
            if (order_status.equals("order_evaluate")){
                ofqo.addQuery("obj.order_status",
                              new SysMap("order_status", Integer.valueOf(50)), "=");
            }
            if (order_status.equals("order_finish")){
                ofqo.addQuery("obj.order_status",
                              new SysMap("order_status", Integer.valueOf(60)), "=");
            }
            if (order_status.equals("order_cancel")){
                ofqo.addQuery("obj.order_status",
                              new SysMap("order_status", Integer.valueOf(0)), "=");
            }
        }
        if (!CommUtil.null2String(order_id).equals("")){
            ofqo.addQuery("obj.order_id",
                          new SysMap("order_id", "%" + order_id +
                                     "%"), "like");
        }
        if (!CommUtil.null2String(beginTime).equals("")){
            ofqo.addQuery("obj.addTime",
                          new SysMap("beginTime", CommUtil.formatDate(beginTime)),
                          ">=");
        }
        if (!CommUtil.null2String(endTime).equals("")){
            ofqo.addQuery("obj.addTime",
                          new SysMap("endTime", CommUtil.formatDate(endTime)), "<=");
        }
        if (!CommUtil.null2String(buyer_userName).equals("")){
            ofqo.addQuery("obj.user.userName",
                          new SysMap("userName",
                                     buyer_userName), "=");
        }
        IPageList pList = this.orderFormService.list(ofqo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        mv.addObject("storeViewTools", this.storeViewTools);
        mv.addObject("order_id", order_id);
        mv.addObject("order_status", order_status == null ? "all" :
                     order_status);
        mv.addObject("beginTime", beginTime);
        mv.addObject("endTime", endTime);
        mv.addObject("buyer_userName", buyer_userName);

        return mv;
    }

    
    //电商ID
  	private String EBusinessID="1465298";
  	//电商加密私钥
  	private String AppKey="2e3d9844-ece2-4791-8da5-7872c9735d53";
  	//请求url
  	private String ReqURL="http://api.kdniao.com/Ebusiness/EbusinessOrderHandle.aspx";
    @SecurityMapping(display = false, rsequence = 0, title = "卖家订单详情", value = "/seller/order_view.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({"/seller/order_view.htm"})
    public ModelAndView order_view(HttpServletRequest request, HttpServletResponse response, String id) throws Exception{
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/order_view.html",
            this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        OrderForm obj = this.orderFormService
                        .getObjById(CommUtil.null2Long(id));

		String shipCode = obj.getShipCode();//物流单号
		String company_mark = obj.getTransport();//快递编码
        if (obj.getStore().getId()
                .equals(SecurityUserHolder.getCurrentUser().getStore().getId())){
            mv.addObject("obj", obj);
            TransInfo transInfo = query_ship_getData(
                                      CommUtil.null2String(obj.getId()));
            mv.addObject("transInfo", transInfo);
        }else{
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request,
                                   response);
            mv.addObject("op_title", "您店铺中没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
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
    		if (obj.getStore().getId()
                    .equals(SecurityUserHolder.getCurrentUser().getStore().getId())){
    			//将获取的数组存进
    			obj.setExpress_logistics(String.valueOf(arr));
    		if (obj.getExpress_logistics() == null) {
    			this.orderFormService.save(obj);
			}else {
				this.orderFormService.update(obj);
			 }
    		mv.addObject("arr",arr);
    		
    	   /* String Success=jsStr.getString("Success");
    	    
                String State=jsStr.getString("State");
              //循环遍历
        		for(int i=0;i<arr.size();i++){
        			JSONObject a=arr.getJSONObject(i);
        			String  AcceptTime=a.getString("AcceptTime");
        			String  AcceptStation=a.getString("AcceptStation");
        		}*/
    		}

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "卖家取消订单", value = "/seller/order_cancel.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({"/seller/order_cancel.htm"})
    public ModelAndView order_cancel(HttpServletRequest request, HttpServletResponse response, String id, String currentPage){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/seller_order_cancel.html",
            this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        OrderForm obj = this.orderFormService
                        .getObjById(CommUtil.null2Long(id));

        if (obj.getStore().getId()
                .equals(SecurityUserHolder.getCurrentUser().getStore().getId())){
            mv.addObject("obj", obj);
            mv.addObject("currentPage", currentPage);
        }else{
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request,
                                   response);
            mv.addObject("op_title", "您没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
        }

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "卖家取消订单保存", value = "/seller/order_cancel_save.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({"/seller/order_cancel_save.htm"})
    public String order_cancel_save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String state_info, String other_state_info) throws Exception {
        OrderForm obj = this.orderFormService
                        .getObjById(CommUtil.null2Long(id));

        // 判断订单所属店铺是否是当前登录用户的店铺
        if (obj.getStore().getId()
                .equals(SecurityUserHolder.getCurrentUser().getStore().getId())){
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
                send_email(request, obj,
                           "email_tobuyer_order_cancel_notify");
            }
            if (this.configService.getSysConfig().isSmsEnbale()){
                send_sms(request, obj, obj.getUser().getMobile(),
                         "sms_tobuyer_order_cancel_notify");
            }
        }

        return "redirect:order.htm?currentPage=" + currentPage;
    }


    @RequestMapping({"/seller/order_cancel1.htm"})
    public ModelAndView order_cancel1(HttpServletRequest request, HttpServletResponse response, String id, String currentPage){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/seller_orderpay_cancle.html",
            this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        OrderForm obj = this.orderFormService
                        .getObjById(CommUtil.null2Long(id));

        if (obj.getStore().getId()
                .equals(SecurityUserHolder.getCurrentUser().getStore().getId())){
            mv.addObject("obj", obj);
            mv.addObject("currentPage", currentPage);
        }else{
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request,
                                   response);
            mv.addObject("op_title", "您没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
        }

        return mv;
    }
    
    
   @RequestMapping({"/seller/order_cancel_save1.htm"})
    public String order_cancel_save1(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String state_info, String other_state_info) throws Exception {
        OrderForm obj = this.orderFormService
                        .getObjById(CommUtil.null2Long(id));

        // 判断订单所属店铺是否是当前登录用户的店铺
        if (obj.getStore().getId()
                .equals(SecurityUserHolder.getCurrentUser().getStore().getId())){
        	//判断是否付款，付款进行退款操作
        	//赊销支付的状态码为20
            if (obj.getOrder_status()==20) {
            	Map params = new HashMap();
				params.put("buyerId",obj.getUser().getId().intValue());//buyerId是buyerCreditLimit表的买家ID
        		params.put("storeId",SecurityUserHolder.getCurrentUser().getStore().getId().intValue());
				List buyerCreditLineList=this.buyerCreditLimitService.query(
            			"select obj from BuyerCreditLimit obj where obj.buyerId=:buyerId and obj.storeId=:storeId", params, -1, -1);
            	if (buyerCreditLineList.size()>0){
            	BuyerCreditLimit buyerCreditLimit=(BuyerCreditLimit) buyerCreditLineList.get(0);
            	BigDecimal BuyerRemainingUndrawnBD=new BigDecimal(buyerCreditLimit.getBuyerRemainingUndrawn());
            	BuyerRemainingUndrawnBD=BuyerRemainingUndrawnBD.add(obj.getTotalPrice());
            	buyerCreditLimit.setBuyerRemainingUndrawn(BuyerRemainingUndrawnBD.toString());
                this.buyerCreditLimitService.update(buyerCreditLimit);
            	}
			}
        	//取消订单操作
            obj.setOrder_status(0);
            this.orderFormService.update(obj);
            List<GoodsCart> gcs = obj.getGcs();
            for (GoodsCart goodsCart : gcs) {
            	Goods goods = this.goodsService.getObjById(goodsCart.getGoods().getId());
                goods.setGoods_inventory(goods.getGoods_inventory()+goodsCart.getCount());
                this.goodsService.update(goods);
			}
            	//发送短信
				LoginViewAction loginViewAction=new LoginViewAction();
				String ordernum=obj.getOrder_id();
				String userName=obj.getUser().getMobile();
				loginViewAction.faxiaoxi(request, response, userName, "SMS_160855877", "", ordernum);
            //站内短信
            String content="您有一笔订单被取消：订单"+ obj.getOrder_id() +"已被商家取消！点击查看详情";
            Message msg1 = new Message();
            msg1.setAddTime(new Date());
            Whitelist whiteList = new Whitelist();
            content = content.replaceAll("\n", "iskyhop_br");
            msg1.setContent(Jsoup.clean(content, Whitelist.basic())
                           .replaceAll("iskyhop_br", "\n"));
            msg1.setFromUser(SecurityUserHolder.getCurrentUser());
            msg1.setToUser(obj.getUser());
            msg1.setType(1);
            this.messageService.save(msg1);
            
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
            if (this.configService.getSysConfig().isEmailEnable()){
                send_email(request, obj,
                           "email_tobuyer_order_cancel_notify");
            }
            if (this.configService.getSysConfig().isSmsEnbale()){
                send_sms(request, obj, obj.getUser().getMobile(),
                         "sms_tobuyer_order_cancel_notify");
            }
        }
        return "redirect:order.htm?currentPage=" + currentPage;
    }
    
    @SecurityMapping(display = false, rsequence = 0, title = "卖家调整订单费用", value = "/seller/order_fee.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({"/seller/order_fee.htm"})
    public ModelAndView order_fee(HttpServletRequest request, HttpServletResponse response, String id, String currentPage){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/seller_order_fee.html",
            this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        OrderForm obj = this.orderFormService
                        .getObjById(CommUtil.null2Long(id));

        if (obj.getStore().getId()
                .equals(SecurityUserHolder.getCurrentUser().getStore().getId())){
            mv.addObject("obj", obj);
            mv.addObject("currentPage", currentPage);
        }else{
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request,
                                   response);
            mv.addObject("op_title", "您没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
        }

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "卖家调整订单费用保存", value = "/seller/order_fee_save.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({"/seller/order_fee_save.htm"})
    public String order_fee_save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String goods_amount, String ship_price, String totalPrice) throws Exception {
        OrderForm obj = this.orderFormService
                        .getObjById(CommUtil.null2Long(id));

        // 判断订单所属店铺是否是当前登录用户的店铺
        if (obj.getStore().getId()
                .equals(SecurityUserHolder.getCurrentUser().getStore().getId())){
            obj.setGoods_amount(BigDecimal.valueOf(
                                    CommUtil.null2Double(goods_amount)));
            obj.setShip_price(BigDecimal.valueOf(
                                  CommUtil.null2Double(ship_price)));
            obj.setTotalPrice(BigDecimal.valueOf(
                                  CommUtil.null2Double(totalPrice)));
            this.orderFormService.update(obj);

            OrderFormLog ofl = new OrderFormLog();
            ofl.setAddTime(new Date());
            ofl.setLog_info("调整订单费用");
            ofl.setLog_user(SecurityUserHolder.getCurrentUser());
            ofl.setOf(obj);
            this.orderFormLogService.save(ofl);
            if (this.configService.getSysConfig().isEmailEnable()){
                send_email(request, obj,
                           "email_tobuyer_order_update_fee_notify");
            }
            if (this.configService.getSysConfig().isSmsEnbale()){
                send_sms(request, obj, obj.getUser().getMobile(),
                         "sms_tobuyer_order_fee_notify");
            }
        }

        return "redirect:order.htm?currentPage=" + currentPage;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "线下付款确认", value = "/seller/seller_order_outline.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({"/seller/seller_order_outline.htm"})
    public ModelAndView seller_order_outline(HttpServletRequest request, HttpServletResponse response, String id, String currentPage){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/seller_order_outline.html",
            this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        OrderForm obj = this.orderFormService
                        .getObjById(CommUtil.null2Long(id));

        if (obj.getStore().getId()
                .equals(SecurityUserHolder.getCurrentUser().getStore().getId())){
            mv.addObject("obj", obj);
            mv.addObject("currentPage", currentPage);
        }else{
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request,
                                   response);
            mv.addObject("op_title", "您没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
        }

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "线下付款确认保存", value = "/seller/seller_order_outline_save.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({"/seller/seller_order_outline_save.htm"})
    public String seller_order_outline_save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String state_info) throws Exception {
        OrderForm obj = this.orderFormService
                        .getObjById(CommUtil.null2Long(id));

        if (obj.getStore().getId()
                .equals(SecurityUserHolder.getCurrentUser().getStore().getId())){
            obj.setOrder_status(20);
            this.orderFormService.update(obj);

            for (GoodsCart gc : obj.getGcs()){
                Goods goods = gc.getGoods();
                if ((goods.getGroup() != null) && (goods.getGroup_buy() == 2)){
                    for (GroupGoods gg : goods.getGroup_goods_list()){
                        if (gg.getGroup().equals(goods.getGroup().getId())){
                            gg.setGg_count(gg.getGg_count() - gc.getCount());
                            gg.setGg_def_count(gg.getGg_def_count() +
                                               gc.getCount());
                            this.groupGoodsService.update(gg);
                        }
                    }
                }
                List gsps = new ArrayList();
                for (GoodsSpecProperty gsp : gc.getGsps()){
                    gsps.add(gsp.getId().toString());
                }
                String[] gsp_list = new String[gsps.size()];
                gsps.toArray(gsp_list);
                goods.setGoods_salenum(goods.getGoods_salenum() + gc.getCount());
                String inventory_type = goods.getInventory_type() == null ? "all" :
                                        goods.getInventory_type();
                Map temp;
                if (inventory_type.equals("all")){
                    goods.setGoods_inventory(goods.getGoods_inventory() -
                                             gc.getCount());
                }else{
                    List list = (List) Json.fromJson(ArrayList.class,
                                                     goods.getGoods_inventory_detail());
                    for (Iterator localIterator4 = list.iterator(); localIterator4.hasNext();){
                        temp = (Map) localIterator4.next();
                        String[] temp_ids =
                            CommUtil.null2String(temp.get("id")).split("_");
                        Arrays.sort(temp_ids);
                        Arrays.sort(gsp_list);
                        if (Arrays.equals(temp_ids, gsp_list)){
                            temp.put(
                                "count",
                                Integer.valueOf(CommUtil.null2Int(temp.get("count")) -
                                                gc.getCount()));
                        }
                    }
                    goods.setGoods_inventory_detail(Json.toJson(list,
                                                    JsonFormat.compact()));
                }
                for (GroupGoods gg : goods.getGroup_goods_list()){
                    if ((!gg.getGroup().getId().equals(goods.getGroup().getId())) ||
                            (gg.getGg_count() != 0)) continue;
                    goods.setGroup_buy(3);
                }

                this.goodsService.update(goods);
            }
            OrderFormLog ofl = new OrderFormLog();
            ofl.setAddTime(new Date());
            ofl.setLog_info("确认线下付款");
            ofl.setLog_user(SecurityUserHolder.getCurrentUser());
            ofl.setOf(obj);
            ofl.setState_info(state_info);
            this.orderFormLogService.save(ofl);
            if (this.configService.getSysConfig().isEmailEnable()){
                send_email(request, obj,
                           "email_tobuyer_order_outline_pay_ok_notify");
            }
            if (this.configService.getSysConfig().isSmsEnbale()){
                send_sms(request, obj, obj.getUser().getMobile(),
                         "sms_tobuyer_order_outline_pay_ok_notify");
            }
        }

        return "redirect:order.htm?currentPage=" + currentPage;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "卖家确认发货", value = "/seller/order_shipping.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({"/seller/order_shipping.htm"})
    public ModelAndView order_shipping(HttpServletRequest request, HttpServletResponse response, String id, String currentPage){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/seller_order_shipping.html",
            this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        OrderForm obj = this.orderFormService
                        .getObjById(CommUtil.null2Long(id));

        if (obj.getStore().getId()
                .equals(SecurityUserHolder.getCurrentUser().getStore().getId())){
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
            mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
        }

        return mv;
    }

    
    
  	
    @SecurityMapping(display = false, rsequence = 0, title = "卖家确认发货保存", value = "/seller/order_shipping_save.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({"/seller/order_shipping_save.htm"})
    public String order_shipping_save(HttpServletRequest request, HttpServletResponse response, String id,String currentPage, String shipCode, String state_info, String order_seller_intro,String company_mark,String company_num) throws Exception {
        OrderForm obj = this.orderFormService
                        .getObjById(CommUtil.null2Long(id));
        ExpressCompany ec = this.expressCompayService.getObjById(
                                CommUtil.null2Long(company_mark));//company_mark传过来的是ExpressCompany的id,只是这样命名而已

        if (obj.getStore().getId()
                .equals(SecurityUserHolder.getCurrentUser().getStore().getId())){
            obj.setOrder_status(30);
            obj.setShipCode(shipCode);//运单号
            obj.setTransport(company_num);//快递编码
            obj.setShipTime(new Date());
            obj.setEc(ec);
            obj.setOrder_seller_intro(order_seller_intro);
            this.orderFormService.update(obj);
            OrderFormLog ofl = new OrderFormLog();
            ofl.setAddTime(new Date());
            ofl.setLog_info("确认发货");
            ofl.setState_info(state_info);
            ofl.setLog_user(SecurityUserHolder.getCurrentUser());
            ofl.setOf(obj);
            this.orderFormLogService.save(ofl);
            if (this.configService.getSysConfig().isEmailEnable()){
                send_email(request, obj, "email_tobuyer_order_ship_notify");
            }
            if (this.configService.getSysConfig().isSmsEnbale()){
                send_sms(request, obj, obj.getUser().getMobile(),
                         "sms_tobuyer_order_ship_notify");
            }

            if (obj.getPayment().getMark().equals("alipay")){
                boolean synch = false;
                String safe_key = "";
                String partner = "";

                if (!CommUtil.null2String(obj.getPayment().getSafeKey())
                        .equals("")){
                    if (!CommUtil.null2String(obj.getPayment().getPartner())
                            .equals("")){
                        safe_key = obj.getPayment().getSafeKey();
                        partner = obj.getPayment().getPartner();
                        synch = true;
                    }
                }
                Map params = new HashMap();
                params.put("type", "admin");
                params.put("mark", "alipay");
                List payments = this.paymentService
                                .query("select obj from Payment obj where obj.type=:type and obj.mark=:mark",
                                       params, -1, -1);
                if ((payments.size() > 0) &&
                        (payments.get(0) != null)){
                    if (!CommUtil.null2String(
                                ((Payment) payments.get(0)).getSafeKey()).equals("")){
                        if (!CommUtil.null2String(
                                    ((Payment) payments.get(0)).getPartner()).equals("")){
                            safe_key = ((Payment) payments.get(0)).getSafeKey();
                            partner = ((Payment) payments.get(0)).getPartner();
                            synch = true;
                        }
                    }
                }
                label480:
                if (synch){
                    AlipayConfig config = new AlipayConfig();
                    config.setKey(safe_key);
                    config.setPartner(partner);
                    Map sParaTemp = new HashMap();
                    sParaTemp.put("service", "send_goods_confirm_by_platform");
                    sParaTemp.put("partner", config.getPartner());
                    sParaTemp.put("_input_charset", config.getInput_charset());
                    sParaTemp.put("trade_no", obj.getOut_order_id());
                    sParaTemp.put("logistics_name", ec.getCompany_name());
                    sParaTemp.put("invoice_no", shipCode);
                    sParaTemp.put("transport_type", ec.getCompany_type());

                    String str1 = AlipaySubmit.buildRequest(config, "web",
                                                            sParaTemp, "", "");
                }
            }
            String content="您有一笔订单已发货！订单"+obj.getOrder_id()+"商家已发货，请耐心等待收货。前往“我的账户”——“订单管理”查看详情。";
            Message msg = new Message();
            msg.setAddTime(new Date());
            Whitelist whiteList = new Whitelist();
            content = content.replaceAll("\n", "iskyhop_br");
            msg.setContent(Jsoup.clean(content, Whitelist.basic())
                           .replaceAll("iskyhop_br", "\n"));
            msg.setFromUser(obj.getStore().getUser());
            msg.setToUser(obj.getUser());
            msg.setType(1);
            this.messageService.save(msg);
        }

        return "redirect:ship_view.htm?id=" + id;
    }
    

    @SecurityMapping(display = false, rsequence = 0, title = "卖家修改物流", value = "/seller/order_shipping_code.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({"/seller/order_shipping_code.htm"})
    public ModelAndView order_shipping_code(HttpServletRequest request, HttpServletResponse response, String id, String currentPage){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/seller_order_shipping_code.html",
            this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        OrderForm obj = this.orderFormService
                        .getObjById(CommUtil.null2Long(id));

        if (obj.getStore().getId()
                .equals(SecurityUserHolder.getCurrentUser().getStore().getId())){
            mv.addObject("obj", obj);
            mv.addObject("currentPage", currentPage);
        }else{
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request,
                                   response);
            mv.addObject("op_title", "您没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
        }

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "卖家修改物流保存", value = "/seller/order_shipping_code_save.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({"/seller/order_shipping_code_save.htm"})
    public String order_shipping_code_save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String shipCode, String state_info){
        OrderForm obj = this.orderFormService
                        .getObjById(CommUtil.null2Long(id));

        if (obj.getStore().getId()
                .equals(SecurityUserHolder.getCurrentUser().getStore().getId())){
            obj.setShipCode(shipCode);
            this.orderFormService.update(obj);
            OrderFormLog ofl = new OrderFormLog();
            ofl.setAddTime(new Date());
            ofl.setLog_info("修改物流信息");
            ofl.setState_info(state_info);
            ofl.setLog_user(SecurityUserHolder.getCurrentUser());
            ofl.setOf(obj);
            this.orderFormLogService.save(ofl);
        }

        return "redirect:order.htm?currentPage=" + currentPage;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "卖家退款", value = "/seller/order_refund.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({"/seller/order_refund.htm"})
    public ModelAndView order_refund(HttpServletRequest request, HttpServletResponse response, String id, String currentPage){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/seller_order_refund.html",
            this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        OrderForm obj = this.orderFormService
                        .getObjById(CommUtil.null2Long(id));

        if (obj.getStore().getId()
                .equals(SecurityUserHolder.getCurrentUser().getStore().getId())){
            mv.addObject("obj", obj);
            mv.addObject("currentPage", currentPage);
            mv.addObject("paymentTools", this.paymentTools);
        }else{
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request,
                                   response);
            mv.addObject("op_title", "您没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
        }

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "卖家退款保存", value = "/seller/order_refund_save.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({"/seller/order_refund_save.htm"})
    public String order_refund_save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String refund, String refund_log, String refund_type){
        OrderForm obj = this.orderFormService
                        .getObjById(CommUtil.null2Long(id));

        if (obj.getStore().getId()
                .equals(SecurityUserHolder.getCurrentUser().getStore().getId())){
            obj.setRefund(BigDecimal.valueOf(CommUtil.add(obj.getRefund(),
                                             refund)));
            this.orderFormService.update(obj);

            String type = "赊销支付";
            if (type.equals(refund_type)){
            	//判断是否付款，付款进行退款操作
            	//赊销支付的状态码为20
                if (obj.getOrder_status()>=20) {
                	Map params = new HashMap();
    				params.put("buyerId",obj.getUser().getId().intValue());//buyerId是buyerCreditLimit表的买家ID
            		params.put("storeId",SecurityUserHolder.getCurrentUser().getStore().getId().intValue());
    				List buyerCreditLineList=this.buyerCreditLimitService.query(
                			"select obj from BuyerCreditLimit obj where obj.buyerId=:buyerId and obj.storeId=:storeId", params, -1, -1);
                	if (buyerCreditLineList.size()>0){
                	BuyerCreditLimit buyerCreditLimit=(BuyerCreditLimit) buyerCreditLineList.get(0);
                	BigDecimal BuyerRemainingUndrawnBD=new BigDecimal(buyerCreditLimit.getBuyerRemainingUndrawn());
                	BuyerRemainingUndrawnBD=BuyerRemainingUndrawnBD.add(obj.getTotalPrice());
                	buyerCreditLimit.setBuyerRemainingUndrawn(BuyerRemainingUndrawnBD.toString());
                    this.buyerCreditLimitService.update(buyerCreditLimit);
                    obj.setOrder_status(70);
                    this.orderFormService.update(obj);
                    
                  //站内短信
                    String content="您申请退款的订单"+ obj.getOrder_id() +"，商家已确认！退款在近期退还至您的余额。点击查看详情";
                    Message msg1 = new Message();
                    msg1.setAddTime(new Date());
                    Whitelist whiteList = new Whitelist();
                    content = content.replaceAll("\n", "iskyhop_br");
                    msg1.setContent(Jsoup.clean(content, Whitelist.basic())
                                   .replaceAll("iskyhop_br", "\n"));
                    msg1.setFromUser(SecurityUserHolder.getCurrentUser());
                    msg1.setToUser(obj.getUser());
                    msg1.setType(1);
                    this.messageService.save(msg1);
                	}
    			}
            }
            RefundLog log = new RefundLog();
            log.setAddTime(new Date());
            log.setRefund_id(CommUtil.formatTime("yyyyMMddHHmmss", new Date()) +
                             obj.getUser().getId().toString());
            log.setOf(obj);
            log.setRefund(BigDecimal.valueOf(CommUtil.null2Double(refund)));
            log.setRefund_log(refund_log);
            log.setRefund_type(refund_type);
            log.setRefund_user(SecurityUserHolder.getCurrentUser());
            this.refundLogService.save(log);
        }

        return "redirect:order.htm?currentPage=" + currentPage;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "卖家退货", value = "/seller/order_return.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({"/seller/order_return.htm"})
    public ModelAndView order_return(HttpServletRequest request, HttpServletResponse response, String id, String currentPage){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/seller_order_return.html",
            this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        OrderForm obj = this.orderFormService
                        .getObjById(CommUtil.null2Long(id));

        if (obj.getStore().getId()
                .equals(SecurityUserHolder.getCurrentUser().getStore().getId())){
            mv.addObject("obj", obj);
            mv.addObject("currentPage", currentPage);
        }else{
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request,
                                   response);
            mv.addObject("op_title", "您没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
        }

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "卖家退货保存", value = "/seller/order_return_save.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({"/seller/order_return_save.htm"})
    public String order_return_save(HttpServletRequest request, HttpServletResponse response, String id, String return_info, String currentPage){
        OrderForm obj = this.orderFormService
                        .getObjById(CommUtil.null2Long(id));

        if (obj.getStore().getId()
                .equals(SecurityUserHolder.getCurrentUser().getStore().getId())){
            Enumeration enum1 = request.getParameterNames();
            GoodsReturn gr = new GoodsReturn();
            gr.setAddTime(new Date());
            gr.setOf(obj);
            gr.setReturn_id(CommUtil.formatTime("yyyyMMddHHmmss", new Date()) +
                            obj.getUser().getId().toString());
            gr.setUser(SecurityUserHolder.getCurrentUser());
            gr.setReturn_info(return_info);
            this.goodsReturnService.save(gr);
            while (enum1.hasMoreElements()){
                String paramName = (String) enum1.nextElement();
                if (paramName.indexOf("refund_count_") >= 0){
                    GoodsCart gc = this.goodsCartService.getObjById(
                                       CommUtil.null2Long(paramName.substring(13)));
                    int count = CommUtil.null2Int(request
                                                  .getParameter(paramName));
                    if (count > 0){
                        gc.setCount(gc.getCount() - count);
                        this.goodsCartService.update(gc);
                        GoodsReturnItem item = new GoodsReturnItem();
                        item.setAddTime(new Date());
                        item.setCount(count);
                        item.setGoods(gc.getGoods());
                        item.setGr(gr);
                        for (GoodsSpecProperty gsp : gc.getGsps()){
                            item.getGsps().add(gsp);
                        }
                        item.setSpec_info(gc.getSpec_info());
                        this.goodsReturnItemService.save(item);

                        Goods goods = gc.getGoods();
                        if (goods.getInventory_type().equals("all")){
                            goods.setGoods_inventory(goods.getGoods_inventory() +
                                                     count);
                        }else{
                            Object gsps = new ArrayList();
                            for (GoodsSpecProperty gsp : gc.getGsps()){
                                ((List) gsps).add(gsp.getId().toString());
                            }
                            String[] gsp_list = new String[((List) gsps).size()];
                            ((List) gsps).toArray(gsp_list);
                            List<Map> list = (List) Json.fromJson(ArrayList.class,
                                                                  goods.getGoods_inventory_detail());
                            for (Map temp : list){
                                String[] temp_ids = CommUtil.null2String(
                                                        temp.get("id")).split("_");
                                Arrays.sort(temp_ids);
                                Arrays.sort(gsp_list);
                                if (Arrays.equals(temp_ids, gsp_list)){
                                    temp.put(
                                        "count",
                                        Integer.valueOf(CommUtil.null2Int(temp.get("count")) +
                                                        count));
                                }
                            }
                            goods.setGoods_inventory_detail(Json.toJson(list,
                                                            JsonFormat.compact()));
                        }
                        goods.setGoods_salenum(goods.getGoods_salenum() - count);
                        this.goodsService.update(goods);

                        if (obj.getPayment().getMark().equals("balance")){
                            BigDecimal balance = goods.getGoods_current_price();
                            User seller = this.userService
                                          .getObjById(
                                              SecurityUserHolder.getCurrentUser().getId());

                            if (this.configService.getSysConfig()
                                    .getBalance_fenrun() == 1){
                                Object params = new HashMap();
                                ((Map) params).put("type", "admin");
                                ((Map) params).put("mark", "balance");
                                List payments = this.paymentService
                                                .query("select obj from Payment obj where obj.type=:type and obj.mark=:mark",
                                                       (Map) params, -1, -1);
                                Payment shop_payment = new Payment();
                                if (payments.size() > 0){
                                    shop_payment = (Payment) payments.get(0);
                                }

                                double shop_availableBalance =
                                    CommUtil.null2Double(balance) *
                                    CommUtil.null2Double(shop_payment
                                                         .getBalance_divide_rate());
                                balance = BigDecimal.valueOf(
                                              CommUtil.null2Double(balance) -
                                              shop_availableBalance);
                            }
                            seller.setAvailableBalance(
                                BigDecimal.valueOf(CommUtil.subtract(
                                                       seller.getAvailableBalance(),
                                                       balance)));
                            this.userService.update(seller);
                            User buyer = obj.getUser();
                            buyer.setAvailableBalance(
                                BigDecimal.valueOf(CommUtil.add(
                                                       buyer.getAvailableBalance(),
                                                       balance)));
                            this.userService.update(buyer);
                        }
                    }
                }
            }
            GoodsReturnLog grl = new GoodsReturnLog();
            grl.setAddTime(new Date());
            grl.setGr(gr);
            grl.setOf(obj);
            grl.setReturn_user(SecurityUserHolder.getCurrentUser());
            this.goodsReturnLogService.save(grl);
        }

        return (String) (String) ("redirect:order.htm?currentPage=" + currentPage);
    }

    @SecurityMapping(display = false, rsequence = 0, title = "卖家评价", value = "/seller/order_evaluate.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({"/seller/order_evaluate.htm"})
    public ModelAndView order_evaluate(HttpServletRequest request, HttpServletResponse response, String id, String currentPage){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/seller_order_evaluate.html",
            this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        OrderForm obj = this.orderFormService
                        .getObjById(CommUtil.null2Long(id));

        if (obj.getStore().getId()
                .equals(SecurityUserHolder.getCurrentUser().getStore().getId())){
            mv.addObject("obj", obj);
            mv.addObject("currentPage", currentPage);
        }else{
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request,
                                   response);
            mv.addObject("op_title", "您没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
        }

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "卖家评价保存", value = "/seller/order_evaluate_save.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({"/seller/order_evaluate_save.htm"})
    public ModelAndView order_evaluate_save(HttpServletRequest request, HttpServletResponse response, String id, String evaluate_info, String evaluate_seller_val){
        OrderForm obj = this.orderFormService
                        .getObjById(CommUtil.null2Long(id));

        if (obj.getStore().getId()
                .equals(SecurityUserHolder.getCurrentUser().getStore().getId())){
            if (obj.getOrder_status() == 50){
                obj.setOrder_status(60);
                obj.setFinishTime(new Date());
                this.orderFormService.update(obj);
                Enumeration enum1 = request.getParameterNames();
                List maps = new ArrayList();
                while (enum1.hasMoreElements()){
                    String paramName = (String) enum1.nextElement();
                    if (paramName.indexOf("evaluate_seller_val") >= 0){
                        String value = request.getParameter(paramName);
                        Evaluate eva = this.evaluateService.getObjById(
                                           CommUtil.null2Long(paramName.substring(19)));
                        eva.setEvaluate_seller_val(CommUtil.null2Int(request
                                                   .getParameter(paramName)));
                        eva.setEvaluate_seller_user(
                            SecurityUserHolder.getCurrentUser());
                        eva.setEvaluate_seller_info(request
                                                    .getParameter("evaluate_info" +
                                                            eva.getId().toString()));
                        eva.setEvaluate_seller_time(new Date());
                        this.evaluateService.update(eva);
                        User user = obj.getUser();
                        user.setUser_credit(user.getUser_credit() +
                                            eva.getEvaluate_seller_val());

                        if (this.configService.getSysConfig().isIntegral()){
                            int integral = 0;

                            if (this.configService.getSysConfig()
                                    .getConsumptionRatio() > 0){
                                integral = CommUtil.null2Int(Double.valueOf(CommUtil.div(obj
                                                             .getTotalPrice(),
                                                             Integer.valueOf(this.configService
                                                                     .getSysConfig().getConsumptionRatio()))));
                            }
                            integral = integral > this.configService
                                       .getSysConfig().getEveryIndentLimit() ? this.configService
                                       .getSysConfig().getEveryIndentLimit() :
                                       integral;
                            user.setIntegral(user.getIntegral() + integral);
                            this.userService.update(user);
                            IntegralLog log = new IntegralLog();
                            log.setAddTime(new Date());
                            log.setContent("订单" + obj.getOrder_id() + "完成增加" +
                                           integral + "分");
                            log.setIntegral(integral);
                            log.setIntegral_user(user);
                            log.setType("login");
                            this.integralLogService.save(log);
                        }
                    }
                }
            }

            OrderFormLog ofl = new OrderFormLog();
            ofl.setAddTime(new Date());
            ofl.setLog_info("评价订单");
            ofl.setLog_user(SecurityUserHolder.getCurrentUser());
            ofl.setOf(obj);
            this.orderFormLogService.save(ofl);
        }
        ModelAndView mv = new JModelAndView("success.html",
                                            this.configService.getSysConfig(),
                                            this.userConfigService.getUserConfig(), 1, request, response);
        mv.addObject("op_title", "订单评价成功！");
        mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "打印订单", value = "/seller/order_print.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({"/seller/order_print.htm"})
    public ModelAndView order_print(HttpServletRequest request, HttpServletResponse response, String id){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/order_print.html",
            this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        if ((id != null) && (!id.equals(""))){
            OrderForm orderform = this.orderFormService.getObjById(
                                      CommUtil.null2Long(id));
            mv.addObject("obj", orderform);
        }

        return mv;
    }

    
    @SecurityMapping(display = false, rsequence = 0, title = "查询卖家物流详情", value = "/seller/ship_view.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({"/seller/ship_view.htm"})
    public ModelAndView order_ship_view(HttpServletRequest request, HttpServletResponse response, String id)throws Exception{
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/order_ship_view.html",
            this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        OrderForm obj = this.orderFormService
                        .getObjById(CommUtil.null2Long(id));
        String shipCode = obj.getShipCode();//物流单号
        String company_mark = obj.getTransport();//快递编码
        
        if (obj.getStore().getId()
                .equals(SecurityUserHolder.getCurrentUser().getStore().getId())){
            mv.addObject("obj", obj);
            TransInfo transInfo = query_ship_getData(
                                      CommUtil.null2String(obj.getId()));
            mv.addObject("transInfo", transInfo);
        }else{
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request,
                                   response);
            mv.addObject("op_title", "您店铺中没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
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
    		if (obj.getStore().getId()
                    .equals(SecurityUserHolder.getCurrentUser().getStore().getId())){
    			//将获取的数组存进
    			obj.setExpress_logistics(String.valueOf(arr));
    		if (obj.getExpress_logistics() == null) {
    			this.orderFormService.save(obj);
			}else {
				this.orderFormService.update(obj);
			 }
    		mv.addObject("arr",arr);
    		/*
    	    String Success=jsStr.getString("Success");
    	    
                String State=jsStr.getString("State");
              //循环遍历
        		for(int i=0;i<arr.size();i++){
        			JSONObject a=arr.getJSONObject(i);
        			String  AcceptTime=a.getString("AcceptTime");
        			String  AcceptStation=a.getString("AcceptStation");
        		}*/
    		}
        return mv;
    }
    
    

    private TransInfo query_ship_getData(String id){
        TransInfo info = new TransInfo();
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        try {
            URL url = new URL("http://api.kuaidi100.com/api?id=" +
                              this.configService.getSysConfig().getKuaidi_id() +
                              "&com=" + (obj.getEc() != null ? obj.getEc().getCompany_mark() : "") +
                              "&nu=" + obj.getShipCode() + "&show=0&muti=1&order=asc");
            URLConnection con = url.openConnection();
            con.setAllowUserInteraction(false);
            InputStream urlStream = url.openStream();
            String type = URLConnection.guessContentTypeFromStream(urlStream);
            String charSet = null;
            if (type == null)
                type = con.getContentType();
            if ((type == null) || (type.trim().length() == 0) || (type.trim().indexOf("text/html") < 0))
                return info;
            if (type.indexOf("charset=") > 0)
                charSet = type.substring(type.indexOf("charset=") + 8);

            /*byte[] b = new byte[10000];
            int numRead = urlStream.read(b);
            String content = new String(b, 0, numRead, charSet);
            while (numRead != -1){
              numRead = urlStream.read(b);
              if (numRead == -1)
                continue;
              String newContent = new String(b, 0, numRead, charSet);
              content = content + newContent;
            }*/

            String line;
            StringBuffer stringBuffer = new StringBuffer();
            Reader reader = new InputStreamReader(urlStream, charSet);
            //增加缓冲功能
            BufferedReader bufferedReader = new BufferedReader(reader);
            while ((line = bufferedReader.readLine()) != null){
                stringBuffer.append(line);
            }
            if (bufferedReader != null){
                bufferedReader.close();
            }
            String content = stringBuffer.toString();

            info = (TransInfo) Json.fromJson(TransInfo.class, content);
            urlStream.close();
        } catch (MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

        return info;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "卖家物流详情", value = "/seller/order_query_userinfor.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({"/seller/order_query_userinfor.htm"})
    public ModelAndView seller_query_userinfor(HttpServletRequest request, HttpServletResponse response, String id){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/seller_query_userinfor.html",
            this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        OrderForm obj = this.orderFormService
                        .getObjById(CommUtil.null2Long(id));
        mv.addObject("obj", obj);

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "买家退货申请详情", value = "/seller/seller_order_return_apply_view.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({"/seller/seller_order_return_apply_view.htm"})
    public ModelAndView seller_order_return_apply_view(HttpServletRequest request, HttpServletResponse response, String id, String currentPage){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/seller_order_return_apply_view.html",
            this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        OrderForm obj = this.orderFormService
                        .getObjById(CommUtil.null2Long(id));

        if (obj.getStore().getUser().getId()
                .equals(SecurityUserHolder.getCurrentUser().getId())){
            mv.addObject("obj", obj);
        }else{
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request,
                                   response);
            mv.addObject("op_title", "您没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
        }

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "卖家保存退货申请", value = "/seller/seller_order_return.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({"/seller/seller_order_return.htm"})
    public String seller_order_return(HttpServletRequest request, HttpServletResponse response, String id, String gr_id, String currentPage, String mark) throws Exception {
        OrderForm obj = this.orderFormService
                        .getObjById(CommUtil.null2Long(id));
        if (mark.equals("true")){
            if (obj.getStore()
                    .getId()
                    .equals(SecurityUserHolder.getCurrentUser().getStore()
                            .getId())){
                Enumeration enum1 = request.getParameterNames();
                GoodsReturn gr = this.goodsReturnService.getObjById(
                                     CommUtil.null2Long(gr_id));
                obj.setOrder_status(46);
                int auto_order_return = this.configService.getSysConfig()
                                        .getAuto_order_return();
                Calendar cal = Calendar.getInstance();
                cal.add(6, auto_order_return);
                obj.setReturn_shipTime(cal.getTime());
                if (this.configService.getSysConfig().isEmailEnable()){
                    send_email(request, obj,
                               "email_tobuyer_order_return_apply_ok_notify");
                }
                if (this.configService.getSysConfig().isSmsEnbale())
                    send_sms(request, obj, obj.getUser().getMobile(),
                             "sms_tobuyer_order_return_apply_ok_notify");
            }
        }else{
            obj.setOrder_status(48);
            if (this.configService.getSysConfig().isEmailEnable()){
                send_email(request, obj,
                           "email_tobuyer_order_return_apply_refuse_notify");
            }
            if (this.configService.getSysConfig().isSmsEnbale()){
                send_sms(request, obj, obj.getUser().getMobile(),
                         "sms_tobuyer_order_return_apply_refuse_notify");
            }
        }
        this.orderFormService.update(obj);

        return "redirect:order.htm?currentPage=" + currentPage;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "确认买家退货", value = "/seller/seller_order_return_confirm.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({"/seller/seller_order_return_confirm.htm"})
    public ModelAndView seller_order_return_confirm(HttpServletRequest request, HttpServletResponse response, String id){
        ModelAndView mv = new JModelAndView("error.html",
                                            this.configService.getSysConfig(),
                                            this.userConfigService.getUserConfig(), 1, request, response);
        OrderForm obj = this.orderFormService
                        .getObjById(CommUtil.null2Long(id));

        if (obj.getStore().getId()
                .equals(SecurityUserHolder.getCurrentUser().getStore().getId())){
            obj.setOrder_status(47);
            this.orderFormService.update(obj);
            mv = new JModelAndView("success.html",
                                   this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request,
                                   response);
            mv.addObject("op_title", "您已成功确认退货");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
        }else{
            mv.addObject("op_title", "您店铺中没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
        }

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "买家退商品流详情", value = "/seller/seller_order_return_ship_view.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({"/seller/seller_order_return_ship_view.htm"})
    public ModelAndView seller_order_return_ship_view(HttpServletRequest request, HttpServletResponse response, String id){
        ModelAndView mv = new JModelAndView("error.html",
                                            this.configService.getSysConfig(),
                                            this.userConfigService.getUserConfig(), 1, request, response);
        OrderForm obj = this.orderFormService
                        .getObjById(CommUtil.null2Long(id));

        if (obj.getStore().getId()
                .equals(SecurityUserHolder.getCurrentUser().getStore().getId())){
            if ((obj.getReturn_shipCode() != null) &&
                    (!obj.getReturn_shipCode().equals("")) &&
                    (obj.getReturn_ec() != null) &&
                    (!obj.getReturn_ec().equals(""))){
                mv = new JModelAndView(
                    "user/default/usercenter/seller_order_return_ship_view.html",
                    this.configService.getSysConfig(), this.userConfigService
                    .getUserConfig(), 0, request, response);
                TransInfo transInfo = query_return_ship(
                                          CommUtil.null2String(obj.getId()));
                mv.addObject("obj", obj);
                mv.addObject("transInfo", transInfo);
            }else{
                mv.addObject("op_title", "买家没有提交退商品流信息");
                mv.addObject("url", CommUtil.getURL(request) +
                             "/seller/order.htm");
            }
        }else{
            mv.addObject("op_title", "您店铺中没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
        }

        return mv;
    }

    private TransInfo query_return_ship(String id){
        TransInfo info = new TransInfo();
        OrderForm obj = this.orderFormService
                        .getObjById(CommUtil.null2Long(id));
        try {
            URL url = new URL("http://api.kuaidi100.com/api?id=" +
                              this.configService.getSysConfig().getKuaidi_id() +
                              "&com=" + (
                                  obj.getReturn_ec() != null ? obj.getReturn_ec()
                                  .getCompany_mark() : "") + "&nu=" +
                              obj.getReturn_shipCode() + "&show=0&muti=1&order=asc");
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

            info = (TransInfo) Json.fromJson(TransInfo.class, content);
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
        if ((template != null) && (template.isOpen())){
            String email = order.getUser().getEmail();
            String subject = template.getTitle();
            String path = request.getSession().getServletContext()
                          .getRealPath("") +
                          File.separator + "vm" + File.separator;
            if (!CommUtil.fileExist(path)){
                CommUtil.createFolder(path);
            }
            PrintWriter pwrite = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(path + "msg.vm", false), "UTF-8"));
            pwrite.print(template.getContent());
            pwrite.flush();
            pwrite.close();

            Properties p = new Properties();
            p.setProperty("file.resource.loader.path",
                          request.getRealPath("") + File.separator + "vm" +
                          File.separator);
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
        if ((template != null) && (template.isOpen())){
            String path = request.getSession().getServletContext()
                          .getRealPath("") +
                          File.separator + "vm" + File.separator;
            if (!CommUtil.fileExist(path)){
                CommUtil.createFolder(path);
            }
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




