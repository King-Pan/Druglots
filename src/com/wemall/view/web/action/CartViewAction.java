package com.wemall.view.web.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.HttpException;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.jdom.JDOMException;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.wemall.core.annotation.SecurityMapping;
import com.wemall.core.domain.virtual.SysMap;
import com.wemall.core.mv.JModelAndView;
import com.wemall.core.query.PageList;
import com.wemall.core.query.support.IPageList;
import com.wemall.core.security.support.SecurityUserHolder;
import com.wemall.core.tools.CommUtil;
import com.wemall.core.tools.HttpClass;
import com.wemall.core.tools.Md5Encrypt;
import com.wemall.core.tools.QRCodeEncoderHandler;
import com.wemall.core.tools.WebForm;
import com.wemall.core.tools.WxAdvancedUtil;
import com.wemall.core.tools.WxCommonUtil;
import com.wemall.core.tools.bean.WxOauth2Token;
import com.wemall.core.tools.bean.WxToken;
import com.wemall.foundation.domain.Address;
import com.wemall.foundation.domain.Area;
import com.wemall.foundation.domain.Authentication;
import com.wemall.foundation.domain.BuyerCreditLimit;
import com.wemall.foundation.domain.CouponInfo;
import com.wemall.foundation.domain.Goods;
import com.wemall.foundation.domain.GoodsCart;
import com.wemall.foundation.domain.GoodsSpecProperty;
import com.wemall.foundation.domain.GroupGoods;
import com.wemall.foundation.domain.IOU;
import com.wemall.foundation.domain.InvoiceRecord;
import com.wemall.foundation.domain.Message;
import com.wemall.foundation.domain.OrderForm;
import com.wemall.foundation.domain.OrderFormLog;
import com.wemall.foundation.domain.Payment;
import com.wemall.foundation.domain.PredepositLog;
import com.wemall.foundation.domain.Store;
import com.wemall.foundation.domain.StoreCart;
import com.wemall.foundation.domain.TemiIous;
import com.wemall.foundation.domain.TemiIousLog;
import com.wemall.foundation.domain.User;
import com.wemall.foundation.domain.query.AddressQueryObject;
import com.wemall.foundation.service.AuthenticationService;
import com.wemall.foundation.service.BuyerCreditLimitService;
import com.wemall.foundation.service.IAddressService;
import com.wemall.foundation.service.IAreaService;
import com.wemall.foundation.service.ICouponInfoService;
import com.wemall.foundation.service.IGoodsCartService;
import com.wemall.foundation.service.IGoodsService;
import com.wemall.foundation.service.IGoodsSpecPropertyService;
import com.wemall.foundation.service.IGroupGoodsService;
import com.wemall.foundation.service.IMessageService;
import com.wemall.foundation.service.IOUService;
import com.wemall.foundation.service.IOrderFormLogService;
import com.wemall.foundation.service.IOrderFormService;
import com.wemall.foundation.service.IPaymentService;
import com.wemall.foundation.service.IPredepositLogService;
import com.wemall.foundation.service.IStoreCartService;
import com.wemall.foundation.service.IStoreService;
import com.wemall.foundation.service.ISysConfigService;
import com.wemall.foundation.service.ITemplateService;
import com.wemall.foundation.service.IUserConfigService;
import com.wemall.foundation.service.IUserService;
import com.wemall.foundation.service.ProcurementRelationshipService;
import com.wemall.foundation.service.RecordCreditLogService;
import com.wemall.foundation.service.TemiIousLogService;
import com.wemall.foundation.service.TemiIousService;
import com.wemall.manage.admin.tools.MsgTools;
import com.wemall.manage.admin.tools.PaymentTools;
import com.wemall.manage.seller.tools.TransportTools;
import com.wemall.pay.alipay.config.AlipayConfig;
import com.wemall.pay.alipay.util.AlipaySubmit;
import com.wemall.pay.tools.PayTools;
import com.wemall.view.web.tools.GoodsViewTools;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 购物车控制器
 */
@Controller
public class CartViewAction {
	
    @Autowired
    private ISysConfigService configService;

    @Autowired
    private IUserConfigService userConfigService;

    @Autowired
    private IGoodsService goodsService;
    
    @Autowired
    private IOUService iouService;

    @Autowired
    private IGoodsSpecPropertyService goodsSpecPropertyService;

    @Autowired
    private IAddressService addressService;

    @Autowired
    private IAreaService areaService;

    @Autowired
    private IPaymentService paymentService;
    
    @Autowired
	private IMessageService messageService;

    
    @Autowired
    private TemiIousLogService temiIousLogService;

    @Autowired
    private IOrderFormService orderFormService;

    @Autowired
    private IGoodsCartService goodsCartService;

    @Autowired
    private IStoreService storeService;

    @Autowired
    private IOrderFormLogService orderFormLogService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ITemplateService templateService;

    @Autowired
    private IPredepositLogService predepositLogService;

    @Autowired
    private IGroupGoodsService groupGoodsService;

    @Autowired
    private ICouponInfoService couponInfoService;

    @Autowired
    private IStoreCartService storeCartService;

    @Autowired
    private MsgTools msgTools;

    @Autowired
    private PaymentTools paymentTools;

    @Autowired
    private PayTools payTools;

    @Autowired
    private TransportTools transportTools;
    
    @Autowired
    private ProcurementRelationshipService procurementRelationshipService;
    
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private GoodsViewTools goodsViewTools;
    
    @Autowired
    private TemiIousService temiIousService;
    
    @Autowired
    private BuyerCreditLimitService buyerCreditLimitService;
    
    @Autowired
    private RecordCreditLogService recordCreditLogService;

    private static Logger logger = LoggerFactory.getLogger(CartViewAction.class);

    private List<StoreCart> cart_calc(HttpServletRequest request){
        List<StoreCart> cart = new ArrayList<StoreCart>();
        List<StoreCart> user_cart = new ArrayList<StoreCart>();
        List<StoreCart> cookie_cart = new ArrayList<StoreCart>();
        User user = null;
        if (SecurityUserHolder.getCurrentUser() != null){
            user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        }
        String cart_session_id = "";
        Map params = new HashMap();
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            for (Cookie cookie : cookies){
                if (cookie.getName().equals("cart_session_id")){
                    cart_session_id = CommUtil.null2String(cookie.getValue());
                }
            }
        }
        if (user != null){
            if (!cart_session_id.equals("")){
                if (user.getStore() != null){
                    params.clear();
                    params.put("cart_session_id", cart_session_id);
                    params.put("user_id", user.getId());
                    params.put("sc_status", Integer.valueOf(0));
                    params.put("store_id", user.getStore().getId());
                    List<StoreCart> store_cookie_cart = this.storeCartService.query(
                                                            "select obj from StoreCart obj where (obj.cart_session_id=:cart_session_id or obj.user.id=:user_id) and obj.sc_status=:sc_status and obj.store.id=:store_id",
                                                            params, -1, -1);
                    for (StoreCart sc : store_cookie_cart){
                        // sc = (StoreCart)localIterator1.next();
                        for (GoodsCart gc : ((StoreCart) sc).getGcs()){
                            gc.getGsps().clear();
                            this.goodsCartService.delete(gc.getId());
                        }
                        this.storeCartService.delete(((StoreCart) sc).getId());
                    }
                }

                params.clear();
                params.put("cart_session_id", cart_session_id);
                params.put("sc_status", Integer.valueOf(0));
                cookie_cart = this.storeCartService.query(
                                  "select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status",
                                  params, -1, -1);

                params.clear();
                params.put("user_id", user.getId());
                params.put("sc_status", Integer.valueOf(0));
                user_cart = this.storeCartService.query(
                                "select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status", params,
                                -1, -1);
            }else{
                params.clear();
                params.put("user_id", user.getId());
                params.put("sc_status", Integer.valueOf(0));
                user_cart = this.storeCartService.query(
                                "select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status", params,
                                -1, -1);
            }

        }else if (!cart_session_id.equals("")){
            params.clear();
            params.put("cart_session_id", cart_session_id);
            params.put("sc_status", Integer.valueOf(0));
            cookie_cart = this.storeCartService.query(
                              "select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status",
                              params, -1, -1);
        }

        for (StoreCart sc : user_cart){
            boolean sc_add = true;
            for (StoreCart sc1 : cart){
                if (sc1.getStore().getId().equals(sc.getStore().getId())){
                    sc_add = false;
                }
            }
            if (sc_add){
                cart.add(sc);
            }
        }
        for (StoreCart sc : cookie_cart){
            boolean sc_add = true;
            for (StoreCart sc1 : cart){
                if (sc1.getStore().getId().equals(sc.getStore().getId())){
                    sc_add = false;
                    for (GoodsCart gc : sc.getGcs()){
                        gc.setSc(sc1);
                        this.goodsCartService.update(gc);
                    }
                    this.storeCartService.delete(sc.getId());
                }
            }
            if (sc_add){
                cart.add(sc);
            }
        }

        return (List<StoreCart>) cart;
    }

    @RequestMapping({ "/cart_menu_detail.htm" })
    public ModelAndView cart_menu_detail(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("cart_menu_detail.html", this.configService.getSysConfig(),
                                            this.userConfigService.getUserConfig(), 1, request, response);
        List<StoreCart> cart = cart_calc(request);
        List<GoodsCart> list = new ArrayList<GoodsCart>();
        if (cart != null){
            for (StoreCart sc : cart){
                if (sc != null)
                    list.addAll(sc.getGcs());
            }
        }
        float total_price = 0.0F;
        for (GoodsCart gc : list){
        	
        	
           Goods goods = this.goodsService.getObjById(gc.getGoods().getId());
            if (CommUtil.null2String(gc.getCart_type()).equals("combin"))
                total_price = CommUtil.null2Float(goods.getCombin_price());
           else{
        	   if (gc.getGoods().getGoods_status()==0) {
                total_price = CommUtil.null2Float(Double.valueOf(CommUtil.mul(Integer.valueOf(gc.getCount()), gc.getPrice()))) + total_price;
        	   }
            }
        }
        mv.addObject("total_price", Float.valueOf(total_price));
        mv.addObject("cart", list);

        return mv;
    }

    /**
     * 添加到购物车
     * @param request
     * @param response
     * @param id
     * @param count
     * @param price
     * @param gsp
     * @param buy_type
     */
    @RequestMapping({ "/add_goods_cart.htm" })
    public void add_goods_cart(HttpServletRequest request, HttpServletResponse response, String id, String count,
                               String price, String gsp, String buy_type){
        String cart_session_id = "";
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            for (Cookie cookie : cookies){
                if (cookie.getName().equals("cart_session_id")){
                    cart_session_id = CommUtil.null2String(cookie.getValue());
                }
            }
        }

        if (cart_session_id.equals("")){
            cart_session_id = UUID.randomUUID().toString();
            Cookie cookie = new Cookie("cart_session_id", cart_session_id);
            cookie.setDomain(CommUtil.generic_domain(request));
            response.addCookie(cookie);
        }

        List<StoreCart> cart = new ArrayList<StoreCart>();// 购物车对象
        List<StoreCart> user_cart = new ArrayList<StoreCart>();// 买家登录状态下的购物车
        List<StoreCart> cookie_cart = new ArrayList<StoreCart>();// 买家未登录状态下的购物车
        User user = null;
        if (SecurityUserHolder.getCurrentUser() != null){
            user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        }
        Map params = new HashMap();
        StoreCart sc;
        if (user != null){// 买家已登录
            if (!cart_session_id.equals("")){
                if (user.getStore() != null){
                    params.clear();
                    params.put("cart_session_id", cart_session_id);
                    params.put("user_id", user.getId());
                    params.put("sc_status", Integer.valueOf(0));
                    params.put("store_id", user.getStore().getId());
                    List store_cookie_cart = this.storeCartService.query(
                                                 "select obj from StoreCart obj where (obj.cart_session_id=:cart_session_id or obj.user.id=:user_id) and obj.sc_status=:sc_status and obj.store.id=:store_id",
                                                 params, -1, -1);
                    for (Iterator localIterator1 = store_cookie_cart.iterator(); localIterator1.hasNext();){
                        sc = (StoreCart) localIterator1.next();
                        for (GoodsCart gc : sc.getGcs()){
                            gc.getGsps().clear();
                            this.goodsCartService.delete(gc.getId());
                        }
                        this.storeCartService.delete(sc.getId());
                    }
                }

                params.clear();
                params.put("cart_session_id", cart_session_id);
                params.put("sc_status", Integer.valueOf(0));
                cookie_cart = this.storeCartService.query(
                                  "select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status",
                                  params, -1, -1);

                params.clear();
                params.put("user_id", user.getId());
                params.put("sc_status", Integer.valueOf(0));
                user_cart = this.storeCartService.query(
                                "select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status", params,
                                -1, -1);
            }else{
                params.clear();
                params.put("user_id", user.getId());
                params.put("sc_status", Integer.valueOf(0));
                user_cart = this.storeCartService.query(
                                "select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status", params,
                                -1, -1);
            }

        }else if (!cart_session_id.equals("")){// 买家未登录
            params.clear();
            params.put("cart_session_id", cart_session_id);
            params.put("sc_status", Integer.valueOf(0));
            cookie_cart = this.storeCartService.query(
                              "select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status",
                              params, -1, -1);
        }

        // 遍历买家登录状态下的购物车，并将购物车内容添加到cart对象里
        for (StoreCart sc12 : user_cart){
            boolean sc_add = true;
            for (StoreCart sc1 : cart){
                if (sc1.getStore().getId().equals(sc12.getStore().getId())){
                    sc_add = false;
                }
            }
            if (sc_add){
                cart.add(sc12);
            }
        }

        // 遍历买家未登录状态下的购物车，并将购物车内容添加到cart对象里
        for (StoreCart sc11 : cookie_cart){
            boolean sc_add = true;
            for (StoreCart sc1 : cart){
                if (sc11.getStore().getId().equals(sc1.getStore().getId())){
                    sc_add = false;
                    for (GoodsCart gc : sc1.getGcs()){
                        gc.setSc(sc1);
                        this.goodsCartService.update(gc);
                    }
                    this.storeCartService.delete(sc1.getId());
                }
            }
            if (sc_add){
                cart.add(sc11);
            }
        }

        String[] gsp_ids = gsp.split(",");// 买家选择的商品规格
        Arrays.sort(gsp_ids);
        boolean add = true;// 是否加入购物车的标志位
        double total_price = 0.0D;
        int total_count = 0;
        String[] gsp_ids1;// 已有购物车内的商品规格
        // 遍历购物车明细，判断用户已有购物车内是否包含当前所选规格的商品。如果包含，则
        for (StoreCart sc1 : cart){
            for (GoodsCart gc : sc1.getGcs()){
                if ((gsp_ids != null) && (gsp_ids.length > 0) && (gc.getGsps() != null) && (gc.getGsps().size() > 0)){
                    gsp_ids1 = new String[gc.getGsps().size()];
                    for (int i = 0; i < gc.getGsps().size(); i++){
                        gsp_ids1[i] = (gc.getGsps().get(i) != null ? ((GoodsSpecProperty) gc.getGsps().get(i)).getId().toString() : "");
                    }
                    Arrays.sort(gsp_ids1);
                    if ((!gc.getGoods().getId().toString().equals(id)) || (!Arrays.equals(gsp_ids, gsp_ids1))){
                        continue;
                    }
                    add = false;
                }else if (gc.getGoods().getId().toString().equals(id)){
                    add = false;
                }
            }
        }

        Object obj;
        if (add){// 买家当前所选规格的商品可以添加到购物车
            Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));

            // 判断是更新购物车还是新增购物车，一个卖家一条购物车记录
            String type = "save";// 更新购物车内商品或新增加商品到购物车的标志位
            StoreCart sc33 = new StoreCart();
            // 遍历购物车，检查当前买家所选规格商品的店铺是否存在。如果存在，则更新购物车记录，否则新增购物车记录
            for (StoreCart sc1 : cart){
                if (sc1.getStore().getId().equals(goods.getGoods_store().getId())){
                    sc33 = sc1;
                    type = "update";
                    break;
                }
            }
            sc33.setStore(goods.getGoods_store());
            if (((String) type).equals("save")){
                sc33.setAddTime(new Date());
                this.storeCartService.save(sc33);
            }else{
                this.storeCartService.update(sc33);
            }

            // 处理购物车明细
            obj = new GoodsCart();
            ((GoodsCart) obj).setAddTime(new Date());
            if (CommUtil.null2String(buy_type).equals("")){
                ((GoodsCart) obj).setCount(CommUtil.null2Int(count));
                ((GoodsCart) obj).setPrice(BigDecimal.valueOf(CommUtil.null2Double(price)));
            }
            if (CommUtil.null2String(buy_type).equals("combin")){
                ((GoodsCart) obj).setCount(1);
                ((GoodsCart) obj).setCart_type("combin");
                ((GoodsCart) obj).setPrice(goods.getCombin_price());
            }
            ((GoodsCart) obj).setGoods(goods);
            // 解析商品规格
            String spec_info = "";
            GoodsSpecProperty spec_property;
            for (String gsp_id : gsp_ids){
                spec_property = this.goodsSpecPropertyService.getObjById(CommUtil.null2Long(gsp_id));
                ((GoodsCart) obj).getGsps().add(spec_property);
                if (spec_property != null){
                    spec_info = spec_property.getSpec().getName() + ":" + spec_property.getValue() + " " + spec_info;
                }
            }
            ((GoodsCart) obj).setSc(sc33);
            ((GoodsCart) obj).setSpec_info(spec_info);
            this.goodsCartService.save((GoodsCart) obj);// 保存购物车明细
            sc33.getGcs().add((GoodsCart) obj);// 将购物车明细添加到购物车内

            double cart_total_price = 0.0D;

            for (GoodsCart gc1 : sc33.getGcs()){
                if (CommUtil.null2String(gc1.getCart_type()).equals("")){
                    cart_total_price = cart_total_price + CommUtil.null2Double(gc1.getPrice()) * gc1.getCount();
                }
                if (!CommUtil.null2String(gc1.getCart_type()).equals("combin"))
                    continue;
                cart_total_price = cart_total_price
                                   + CommUtil.null2Double(gc1.getGoods().getCombin_price()) * gc1.getCount();
            }

            sc33.setTotal_price(BigDecimal.valueOf(CommUtil.formatMoney(Double.valueOf(cart_total_price))));
            if (user == null)
                sc33.setCart_session_id(cart_session_id);
           else{
                sc33.setUser(user);
            }

            // 再次更新购物车
            if (((String) type).equals("save")){
                sc33.setAddTime(new Date());
                this.storeCartService.save(sc33);
            }else{
                this.storeCartService.update(sc33);
            }
            boolean cart_add = true;
            for (StoreCart sc1 : cart){
                if (sc1.getStore().getId().equals(sc33.getStore().getId())){
                    cart_add = false;
                }
            }
            if (cart_add){
                cart.add(sc33);
            }
        }

        // 计算购物车内商品总价
        for (Object type = cart.iterator(); ((Iterator) type).hasNext();){
            StoreCart sc1 = (StoreCart) ((Iterator) type).next();

            total_count += sc1.getGcs().size();
            for (obj = sc1.getGcs().iterator(); ((Iterator) obj).hasNext();){
                GoodsCart gc1 = (GoodsCart) ((Iterator) obj).next();

                total_price = total_price + CommUtil.mul(gc1.getPrice(), Integer.valueOf(gc1.getCount()));
            }
        }

        Map map = new HashMap();
        map.put("count", Integer.valueOf(total_count));
        map.put("total_price", Double.valueOf(total_price));
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
     * 从购物车删除商品
     * @param request
     * @param response
     * @param id
     * @param store_id
     */
    @RequestMapping({ "/remove_goods_cart.htm" })
    public void remove_goods_cart(HttpServletRequest request, HttpServletResponse response, String id,
                                  String store_id){
        GoodsCart gc = this.goodsCartService.getObjById(CommUtil.null2Long(id));
        StoreCart the_sc = gc.getSc();
        gc.getGsps().clear();

        this.goodsCartService.delete(CommUtil.null2Long(id));
        if (the_sc.getGcs().size() == 0){
            this.storeCartService.delete(the_sc.getId());
        }
        List<StoreCart> cart = cart_calc(request);
        double total_price = 0.0D;
        double sc_total_price = 0.0D;
        double count = 0.0D;
        for (StoreCart sc2 : cart){
            for (GoodsCart gc1 : sc2.getGcs()){
                total_price = CommUtil.null2Double(gc1.getPrice()) * gc1.getCount() + total_price;
                count += 1.0D;
                if ((store_id == null) || (store_id.equals(""))
                        || (!sc2.getStore().getId().toString().equals(store_id)))
                    continue;
                sc_total_price = sc_total_price + CommUtil.null2Double(gc1.getPrice()) * gc1.getCount();
                sc2.setTotal_price(BigDecimal.valueOf(sc_total_price));
            }

            this.storeCartService.update(sc2);
        }
        request.getSession(false).setAttribute("cart", cart);
        Map map = new HashMap();
        map.put("count", Double.valueOf(count));
        map.put("total_price", Double.valueOf(total_price));
        map.put("sc_total_price", Double.valueOf(sc_total_price));
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(Json.toJson(map, JsonFormat.compact()));
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @RequestMapping({ "/goods_count_adjust.htm" })
    public void goods_count_adjust(HttpServletRequest request, HttpServletResponse response, String cart_id,
                                   String store_id, String count){
        List<StoreCart> cart = cart_calc(request);

        double goods_total_price = 0.0D;
        String error = "100";
        Goods goods = null;
        String cart_type = "";
        GoodsCart gc;
        for (StoreCart sc : cart)
            for (Iterator localIterator2 = sc.getGcs().iterator(); localIterator2.hasNext();){
                gc = (GoodsCart) localIterator2.next();
                if (gc.getId().toString().equals(cart_id)){
                    goods = gc.getGoods();
                    cart_type = CommUtil.null2String(gc.getCart_type());
                }
            }
        Object sc;
        if (cart_type.equals("")){
            if (goods.getGroup_buy() == 2){
                GroupGoods gg = new GroupGoods();
                for (GroupGoods gg1 : goods.getGroup_goods_list()){
                    if (gg1.getGg_goods().equals(goods.getId())){
                        gg = gg1;
                    }
                }
                if (gg.getGg_count() >= CommUtil.null2Int(count))
                    for (StoreCart sc1 : cart){ // sc = (StoreCart)gc.next();
                        for (int i = 0; i < ((StoreCart) sc1).getGcs().size(); i++){
                            GoodsCart art = (GoodsCart) ((StoreCart) sc1).getGcs().get(i);
                            GoodsCart gc1 = art;
                            if (art.getId().toString().equals(cart_id)){
                                ((StoreCart) sc1).setTotal_price(
                                    BigDecimal.valueOf(CommUtil.add(((StoreCart) sc1).getTotal_price(),
                                                                    Double.valueOf((CommUtil.null2Int(count) - art.getCount())
                                                                            * CommUtil.null2Double(art.getPrice())))));
                                art.setCount(CommUtil.null2Int(count));
                                gc1 = art;
                                ((StoreCart) sc1).getGcs().remove(art);
                                ((StoreCart) sc1).getGcs().add(gc1);
                                goods_total_price = CommUtil.null2Double(gc1.getPrice()) * gc1.getCount();
                                this.storeCartService.update((StoreCart) sc1);
                            }
                        }
                    }
               else{
                    error = "300";
                }
            }else if (goods.getGoods_inventory() >= CommUtil.null2Int(count)){
                for (StoreCart scart : cart){
                    for (int i = 0; i < scart.getGcs().size(); i++){
                        GoodsCart gcart = (GoodsCart) scart.getGcs().get(i);
                        GoodsCart gc1 = gcart;
                        if (gcart.getId().toString().equals(cart_id)){
                            scart.setTotal_price(BigDecimal.valueOf(CommUtil.add(scart.getTotal_price(),
                                                                    Double.valueOf((CommUtil.null2Int(count) - gcart.getCount())
                                                                            * Double.parseDouble(gcart.getPrice().toString())))));
                            gcart.setCount(CommUtil.null2Int(count));
                            gc1 = gcart;
                            scart.getGcs().remove(gcart);
                            scart.getGcs().add(gc1);
                            goods_total_price = Double.parseDouble(gc1.getPrice().toString()) * gc1.getCount();
                            this.storeCartService.update(scart);
                        }
                    }
                }
            }else{
                error = "200";
            }
        }

        if (cart_type.equals("combin")){
            if (goods.getGoods_inventory() >= CommUtil.null2Int(count))
                for (StoreCart sscart : cart){
                    for (int i = 0; i < sscart.getGcs().size(); i++){
                        gc = (GoodsCart) sscart.getGcs().get(i);
                        GoodsCart gc1 = (GoodsCart) gc;
                        if (((GoodsCart) gc).getId().toString().equals(cart_id)){
                            sscart.setTotal_price(BigDecimal.valueOf(CommUtil.add(sscart.getTotal_price(),
                                                  Float.valueOf((CommUtil.null2Int(count) - ((GoodsCart) gc).getCount())
                                                                * CommUtil.null2Float(((GoodsCart) gc).getGoods().getCombin_price())))));
                            ((GoodsCart) gc).setCount(CommUtil.null2Int(count));
                            gc1 = (GoodsCart) gc;
                            sscart.getGcs().remove(gc);
                            sscart.getGcs().add(gc1);
                            goods_total_price = Double.parseDouble(gc1.getPrice().toString()) * gc1.getCount();
                            this.storeCartService.update(sscart);
                        }
                    }
                }
           else{
                error = "200";
            }
        }
        DecimalFormat df = new DecimalFormat("0.00");
        Object map = new HashMap();
        ((Map) map).put("count", count);
        for (StoreCart ssscart : cart){
            if (ssscart.getStore().getId().equals(CommUtil.null2Long(store_id))){
                ((Map) map).put("sc_total_price", Float.valueOf(CommUtil.null2Float(ssscart.getTotal_price())));
            }
        }
        ((Map) map).put("goods_total_price", Double.valueOf(df.format(goods_total_price)));
        ((Map) map).put("error", error);
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();

            writer.print(Json.toJson(map, JsonFormat.compact()));
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 查看购物车
     * @param request
     * @param response
     * @return
     */
    @SecurityMapping(display = false, rsequence = 0, title = "查看购物车", value = "/goods_cart1.htm*", rtype = "buyer", rname = "购物流程1", rcode = "goods_cart", rgroup = "在线购物")
    @RequestMapping({ "/goods_cart1.htm" })
    public ModelAndView goods_cart1(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("goods_cart1.html", this.configService.getSysConfig(),
                                            this.userConfigService.getUserConfig(), 1, request, response);
        String wemall_view_type = CommUtil.null2String(request.getSession().getAttribute("wemall_view_type"));
        if((wemall_view_type != null) && (!wemall_view_type.equals("")) && (wemall_view_type.equals("wap"))){
            mv = new JModelAndView("wap/goods_cart1.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
        }
        List<StoreCart> cart = cart_calc(request);
        if (cart != null){
            Store store = SecurityUserHolder.getCurrentUser().getStore() != null
                          ? SecurityUserHolder.getCurrentUser().getStore() : null;
            if (store != null){
                for (StoreCart sc : cart){
                    if (sc.getStore().getId().equals(store.getId())){
                        for (GoodsCart gc : sc.getGcs()){
                            gc.getGsps().clear();
                            this.goodsCartService.delete(gc.getId());
                        }
                        sc.getGcs().clear();
                        this.storeCartService.delete(sc.getId());
                    }
                }
            }
            request.getSession(false).setAttribute("cart", cart);
            mv.addObject("cart", cart);
            mv.addObject("goodsViewTools", this.goodsViewTools);
        }else{
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
            if((wemall_view_type != null) && (!wemall_view_type.equals("")) && (wemall_view_type.equals("wap"))){
                mv = new JModelAndView("wap/error.html", this.configService.getSysConfig(),
                                       this.userConfigService.getUserConfig(), 1, request, response);
            }
            mv.addObject("op_title", "购物车信息为空");
            mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
        }

        if (this.configService.getSysConfig().isZtc_status()){
            List ztc_goods = null;
            Map ztc_map = new HashMap();
            ztc_map.put("ztc_status", Integer.valueOf(3));
            ztc_map.put("now_date", new Date());
            ztc_map.put("ztc_gold", Integer.valueOf(0));
            List goods = this.goodsService.query(
                             "select obj from Goods obj where obj.ztc_status =:ztc_status and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold order by obj.ztc_dredge_price desc", ztc_map, -1, -1);

            ztc_goods = randomZtcGoods(goods);
            mv.addObject("ztc_goods", ztc_goods);
        }
        
        return mv;
    }

    private List<Goods> randomZtcGoods(List<Goods> goods){
        Random random = new Random();
        int random_num = 0;
        int num = 0;
        if (goods.size() - 8 > 0){
            num = goods.size() - 8;
            random_num = random.nextInt(num);
        }
        Map ztc_map = new HashMap();
        ztc_map.put("ztc_status", Integer.valueOf(3));
        ztc_map.put("now_date", new Date());
        ztc_map.put("ztc_gold", Integer.valueOf(0));
        List ztc_goods = this.goodsService.query(
                             "select obj from Goods obj where obj.ztc_status =:ztc_status and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold order by obj.ztc_dredge_price desc",
                             ztc_map, random_num, 8);
        Collections.shuffle(ztc_goods);

        return ztc_goods;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "确认购物车填写地址", value = "/goods_cart2.htm*", rtype = "buyer", rname = "购物流程2", rcode = "goods_cart", rgroup = "在线购物")
    @RequestMapping({ "/goods_cart2.htm" })
    public ModelAndView goods_cart2(HttpServletRequest request, HttpServletResponse response, String store_id){
        ModelAndView mv = new JModelAndView("goods_cart2.html", this.configService.getSysConfig(),
                                            this.userConfigService.getUserConfig(), 1, request, response);
        Long userId= SecurityUserHolder.getCurrentUser().getId();
        String wemall_view_type = CommUtil.null2String(request.getSession().getAttribute("wemall_view_type"));
        if((wemall_view_type != null) && (!wemall_view_type.equals("")) && (wemall_view_type.equals("wap"))){
            mv = new JModelAndView("wap/goods_cart2.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
        }
        
        //查所有的发票
        HttpClass hc = new HttpClass();
        String username = SecurityUserHolder.getCurrentUser().getUserName();
		try {
			String	Load = hc.load("http://127.0.0.1:8081/ssm_project/selectall", "username="+username);
			 PageList pList= JSON.parseObject(Load, PageList .class);
			 mv.addObject("objs", pList.getResult().get(0));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        //判断买家是否通过首营资料
        Map params = new HashMap();
        params.put("buyerId", SecurityUserHolder.getCurrentUser().getUserName());
        List examine = this.authenticationService.query(
                         "select examine from Authentication obj where obj.userName =:buyerId", params, -1, -1);

        System.out.println(examine);
        int examineListsize=examine.size();
        if(examineListsize==0){
        	mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
        	mv.addObject("op_title", "您的首营资料未提交，请首营资料通过后再提交订单！");
        	mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
        	return mv;
        }
        String examine2=examine.get(0).toString();
        
        if(!examine2.equals("2")){
        	mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
        	mv.addObject("op_title", "您的首营资质已过期，请重新提交资质！");
        	mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
        	return mv;
        }
        
        //  查询 判断采购关系是否通过；
        params.clear();
        params.put("buyerId", String.valueOf(userId));
        params.put("storeId", store_id);
        List auditStatusList = this.procurementRelationshipService.query(
                         "select auditStatus from ProcurementRelationship obj where obj.buyerId =:buyerId and obj.storeId=:storeId ", params, -1, -1);

        int auditStatusListsize=auditStatusList.size();
        if(auditStatusListsize==0){
        	mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
        	mv.addObject("op_title", "您的采购关系未成功，请采购关系通过后再提交订单！");
        	mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
        	return mv;
        }
        
        String auditStatus=auditStatusList.get(0).toString();
        if (!auditStatus.equals("1")){
        	mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request,
                    response);
        	mv.addObject("op_title", "您未与店家的建立采购关系! 请先建立采购关系在提交订单！");
//        	mv.addObject("url", CommUtil.getURL(request) +"/goods_cart1.htm");
        	mv.addObject("url", CommUtil.getURL(request) +"/buyer/buildPurchaseRelation.htm?store_id="+store_id);
        	return mv;
        }
        // 查询 判断采购关系是否通过结束
        
        
        
        List<StoreCart> cart = cart_calc(request);
        StoreCart sc = null;
        if (cart != null){
            for (StoreCart sc1 : cart){
                if (sc1.getStore().getId().equals(CommUtil.null2Long(store_id))){
                    sc = sc1;
                    break;
                }
            }
        }
        if (sc != null){
        	//dz代表省内1还是省外2 jg代表价格 pd代表让不让过0过1不过
        	int dz = 1;
        	int jg = 0;
        	int pd = 0;
        	//开始判断
        	try {
        		User user = SecurityUserHolder.getCurrentUser();
        		User user2 = sc.getStore().getUser();
        		//取用户的地址
				Area area = user.getArea();
				//取商家的地址
				Area area2 = user2.getArea();
				//取出总价
				 int total = sc.getTotal_price().intValue();
				
				
        		//比较地址不等
				if (area.getId()!=area2.getId()) {
					dz=2;
					String outprovince = user2.getOutprovince();
					int out = 0;
					if (outprovince!=null) {
						out = Integer.parseInt(outprovince);  
						if (total<out) {
							pd=1;
						}
					jg=Math.abs(out-total);
					}
					if (outprovince==null) {
						jg=Math.abs(out-total);
					}
				}
				//相等
				if (area.getId()==area2.getId()) {
					dz=1;
					String province = user2.getProvince();
					if (province!=null) {
						if (total<Integer.parseInt(province)) {
							pd=1;
						}
						jg=Math.abs(Integer.parseInt(province)-total);
					}
					if (province==null) {
						jg=Math.abs(0-total);
					}
				}
				mv.addObject("dz",dz);
				mv.addObject("jg", jg);
				mv.addObject("pd", pd);
        		
			} catch (Exception e) {
				 dz = 1;
	        	 jg = 0;
	        	 pd = 0;
	        	mv.addObject("dz",dz);
				mv.addObject("jg", jg);
				mv.addObject("pd", pd);
			}
        	
        	
        	
        	params.clear();
            params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
            List addrs = this.addressService.query(
                             "select obj from Address obj where obj.user.id=:user_id order by obj.addTime desc", params, -1, -1);
            mv.addObject("addrs", addrs);
            if ((store_id == null) || (store_id.equals(""))){
                store_id = sc.getStore().getId().toString();
            }
            String cart_session = CommUtil.randomString(32);
            request.getSession(false).setAttribute("cart_session", cart_session);
            params.clear();
            params.put("coupon_order_amount", sc.getTotal_price());
            params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
            params.put("coupon_begin_time", new Date());
            params.put("coupon_end_time", new Date());
            params.put("status", Integer.valueOf(0));
            List couponinfos = this.couponInfoService.query(
                                   "select obj from CouponInfo obj where obj.coupon.coupon_order_amount<=:coupon_order_amount and obj.status=:status and obj.user.id=:user_id and obj.coupon.coupon_begin_time<=:coupon_begin_time and obj.coupon.coupon_end_time>=:coupon_end_time",
                                   params, -1, -1);
            mv.addObject("couponinfos", couponinfos);
            mv.addObject("sc", sc);
            mv.addObject("cart_session", cart_session);
            mv.addObject("store_id", store_id);
            mv.addObject("transportTools", this.transportTools);
            mv.addObject("goodsViewTools", this.goodsViewTools);

            boolean goods_delivery = false;
            List<GoodsCart> goodCarts = sc.getGcs();
            for (GoodsCart gc : goodCarts){
                if (gc.getGoods().getGoods_choice_type() == 0){
                    goods_delivery = true;
                    break;
                }
            }
            mv.addObject("goods_delivery", Boolean.valueOf(goods_delivery));
        }else{
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
            if((wemall_view_type != null) && (!wemall_view_type.equals("")) && (wemall_view_type.equals("wap"))){
                mv = new JModelAndView("wap/error.html", this.configService.getSysConfig(),
                                       this.userConfigService.getUserConfig(), 1, request, response);
            }
            mv.addObject("op_title", "购物车信息为空");
            mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
        }

        return mv;
    }

    /**
     * 生成订单
     * @param request
     * @param response
     * @param cart_session
     * @param store_id
     * @param addr_id
     * @param coupon_id
     * @return
     * @throws Exception
     */
    @SecurityMapping(display = false, rsequence = 0, title = "完成订单提交进入支付", value = "/goods_cart3.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
    @RequestMapping({ "/goods_cart3.htm" })
    public ModelAndView goods_cart3(final HttpServletRequest request, HttpServletResponse response, String cart_session,
                                    String goods_count,String goods_id,String store_id, String addr_id, String coupon_id,String invoiceid,String invoice) throws Exception {
        ModelAndView mv = new JModelAndView("goods_cart3.html", this.configService.getSysConfig(),
                                            this.userConfigService.getUserConfig(), 1, request, response);
        String wemall_view_type = CommUtil.null2String(request.getSession().getAttribute("wemall_view_type"));
        if((wemall_view_type != null) && (!wemall_view_type.equals("")) && (wemall_view_type.equals("wap"))){
            mv = new JModelAndView("wap/goods_cart3.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
        }
        try {
        	 //按照-来切割字符串
            String[] inv = invoice.split("-");
            //发票id
            invoiceid=inv[1];
            //普票还是专票
            invoice=inv[0];
		} catch (Exception e) {
			String login_role = (String)request.getSession(false).getAttribute("login_role");
	         wemall_view_type = CommUtil.null2String(request.getSession(false).getAttribute("wemall_view_type"));

	        if (login_role == null)
	            login_role = "user";
	        if (login_role.equals("admin")){
	            mv = new JModelAndView("admin/blue/login_error.html", this.configService.getSysConfig(),
	                                   this.userConfigService.getUserConfig(), 0, request, response);
	        }else{
	            if ((wemall_view_type != null) && (!wemall_view_type.equals("")) && (wemall_view_type.equals("wap"))){
	                mv = new JModelAndView("wap/error.html", this.configService.getSysConfig(),
	                                       this.userConfigService.getUserConfig(), 1, request, response);
	                mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
	            }
	            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
	                                   this.userConfigService.getUserConfig(), 1, request, response);
	            mv.addObject("url", CommUtil.getURL(request) + "/buyer/invoice.htm");
	        }
	        mv.addObject("op_title", "暂未填写发票信息");

	        return mv;
		}
       
        
        
        if (invoiceid==null || invoiceid=="") {
       	 mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
                   mv.addObject("op_title", "没有选择发票信息");
                    mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
                    return mv;
		}
        String cart_session1 = (String) request.getSession(false).getAttribute("cart_session");
        List<StoreCart> cart = cart_calc(request);
        if (cart != null){
            if (CommUtil.null2String(cart_session1).equals(cart_session)){
                request.getSession(false).removeAttribute("cart_session");
                WebForm wf = new WebForm();
                final OrderForm of = (OrderForm) wf.toPo(request, OrderForm.class);
                of.setAddTime(new Date());
                of.setOrder_id(SecurityUserHolder.getCurrentUser().getId()
                               + CommUtil.formatTime("yyyyMMddHHmmss", new Date()));
                Address addr = this.addressService.getObjById(CommUtil.null2Long(addr_id));
                of.setAddr(addr);
                of.setOrder_status(10);//待付款
                of.setBill_state(0);//未对账
                of.setStatus(0);
                of.setPay_status(0);
                of.setSettlement_status(1);//未结算
                of.setUser(SecurityUserHolder.getCurrentUser());
                of.setStore(this.storeService.getObjById(CommUtil.null2Long(store_id)));
                of.setTotalPrice(BigDecimal.valueOf(CommUtil.add(of.getGoods_amount(), of.getShip_price())));
                
                if (!CommUtil.null2String(coupon_id).equals("")){
                    CouponInfo ci = this.couponInfoService.getObjById(CommUtil.null2Long(coupon_id));
                    ci.setStatus(1);
                    this.couponInfoService.update(ci);
                    of.setCi(ci);
                    of.setTotalPrice(BigDecimal.valueOf(CommUtil.subtract(of.getTotalPrice(), ci.getCoupon().getCoupon_amount())));
                }
                of.setOrder_type("web");
                this.orderFormService.save(of);
                GoodsCart gc;
                for (StoreCart sc : cart){
                    if (sc.getStore().getId().toString().equals(store_id)){
                        for (Iterator localIterator2 = sc.getGcs().iterator(); localIterator2.hasNext();){
                            gc = (GoodsCart) localIterator2.next();
                            gc.setOf(of);
                            this.goodsCartService.update(gc);
                        }
                        sc.setCart_session_id(null);
                        sc.setUser(SecurityUserHolder.getCurrentUser());
                        sc.setSc_status(1);
                        this.storeCartService.update(sc);
                        break;
                    }
                }
                Cookie[] cookies = request.getCookies();
                if (cookies != null){
                    for (int i = 0; i < cookies.length; i++){
                        Cookie cookie = cookies[i];
                        if (cookie.getName().equals("cart_session_id")){
                            cookie.setDomain(CommUtil.generic_domain(request));
                            cookie.setValue("");
                            cookie.setMaxAge(0);
                            response.addCookie(cookie);
                        }
                    }
                }
                OrderFormLog ofl = new OrderFormLog();
                ofl.setAddTime(new Date());
                ofl.setOf(of);
                ofl.setLog_info("提交订单");
                ofl.setLog_user(SecurityUserHolder.getCurrentUser());
                this.orderFormLogService.save(ofl);
                
                final Timer timer = new Timer();
    	        timer.schedule(new TimerTask() {
    	            public void run() {
    	            	OrderForm order = orderFormService
    	                        .getObjById(CommUtil.null2Long(of.getId()));
    	            	
    	            	
    	            	//取消订单操作
    	            	order.setOrder_status(0);
    	                orderFormService.update(order);
//    	                List<GoodsCart> gcs = order.getGcs();
//    	                for (GoodsCart goodsCart : gcs) {
//    	                	Goods goods = goodsService.getObjById(goodsCart.getGoods().getId());
//    	                    goods.setGoods_inventory(goods.getGoods_inventory()+goodsCart.getCount());
//    	    			}
    	                Map params = new HashMap();
                        params.put("Id", order.getId());
                        
    	                List<GoodsCart> query = goodsCartService.query("select obj from GoodsCart obj where obj.of.id=:Id ", params, -1, -1);
    	                for (GoodsCart goodsCart : query) {
    	                	Goods goods = goodsService.getObjById(goodsCart.getGoods().getId());
    	                    goods.setGoods_inventory(goods.getGoods_inventory()+goodsCart.getCount());
    	                    goodsService.update(goods);
    	    			}
    	                
    	                
    	                OrderFormLog ofl = new OrderFormLog();
    	                ofl.setAddTime(new Date());
    	                ofl.setLog_info("取消订单");
    	                ofl.setLog_user(SecurityUserHolder.getCurrentUser());
    	                ofl.setOf(order);
    	                
	                    ofl.setState_info("订单时间到期，自动取消!");
    	                
    	                orderFormLogService.save(ofl);
    	                timer.cancel();
    	            }
    	        }, 24*60*60*1000, 24*60*60*1000);
                
                //更新库存
                if(goods_count.contains(",")&&goods_id.contains(",")){
                	String[]  strs=goods_count.split(",");
                	String[]  idstrs=goods_id.split(",");
                	for(int i=0;i<strs.length;i++){
                		
                			Goods goods = this.goodsService.getObjById(CommUtil.null2Long(idstrs[i]));
                            goods.setGoods_inventory(goods.getGoods_inventory()-Integer.parseInt(strs[i]));
                            this.goodsService.update(goods);
                		
                	}
                } else{
                	Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
                    goods.setGoods_inventory(goods.getGoods_inventory()-Integer.parseInt(goods_count));
                    this.goodsService.update(goods);
                }
                
                mv.addObject("of", of);
                mv.addObject("paymentTools", this.paymentTools);
                InvoiceRecord invoiceRecord = new InvoiceRecord();
                //开始存发票记录1是普票 2是专票
                invoiceRecord.setSubmitdate(of.getAddTime());
                invoiceRecord.setBuyname(of.getUser().getUserName());
                invoiceRecord.setSellername(of.getStore().getStore_ower());
                invoiceRecord.setStorename(of.getStore().getStore_name());
                invoiceRecord.setStoreid(of.getOrder_id());
                invoiceRecord.setInvamount(of.getTotalPrice().toString());
                invoiceRecord.setInvoiceid(Integer.parseInt(invoiceid));
                if (Integer.parseInt(invoice)==1) {
                	invoiceRecord.setInvoicetype("普票");
				}
                //专票
                if (Integer.parseInt(invoice)==2) {
                	invoiceRecord.setInvoicetype("专票");
				}
               
                
                HttpClass hc = new HttpClass();
                
                String duixiang= JSON.toJSONString(invoiceRecord);
               System.out.println(duixiang);
               hc.load("http://127.0.0.1:8081/ssm_project/saveRecord", "duixiang="+duixiang);

                
                if (this.configService.getSysConfig().isEmailEnable()){
                    send_email(request, of, of.getUser().getEmail(), "email_tobuyer_order_submit_ok_notify");
                }
                if (this.configService.getSysConfig().isSmsEnbale()){
                    send_sms(request, of, of.getUser().getMobile(), "sms_tobuyer_order_submit_ok_notify");
                }
            }else{
                mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                                       this.userConfigService.getUserConfig(), 1, request, response);
                if((wemall_view_type != null) && (!wemall_view_type.equals("")) && (wemall_view_type.equals("wap"))){
                    mv = new JModelAndView("wap/error.html", this.configService.getSysConfig(),
                                           this.userConfigService.getUserConfig(), 1, request, response);
                }
                mv.addObject("op_title", "订单已经失效");
                mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
            }
        }else{
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
            if((wemall_view_type != null) && (!wemall_view_type.equals("")) && (wemall_view_type.equals("wap"))){
                mv = new JModelAndView("wap/error.html", this.configService.getSysConfig(),
                                       this.userConfigService.getUserConfig(), 1, request, response);
            }
            mv.addObject("op_title", "订单信息错误");
            mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
        }

        return mv;
    }
    

    /**
     * 买家选择付款方式
     * @param request
     * @param response
     * @param id
     * @return
     */
    @SecurityMapping(display = false, rsequence = 0, title = "订单支付详情", value = "/order_pay_view.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
    @RequestMapping({ "/order_pay_view.htm" })
    public ModelAndView order_pay_view(HttpServletRequest request, HttpServletResponse response, String id,String password){
        ModelAndView mv = new JModelAndView("order_pay.html", this.configService.getSysConfig(),
                                            this.userConfigService.getUserConfig(), 1, request, response);

        String wemall_view_type = CommUtil.null2String(request.getSession().getAttribute("wemall_view_type"));
        if((wemall_view_type != null) && (!wemall_view_type.equals("")) && (wemall_view_type.equals("wap"))){
            mv = new JModelAndView("wap/order_pay.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
        }
        OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if (of.getOrder_status() == 10){
            mv.addObject("of", of);
            mv.addObject("paymentTools", this.paymentTools);
            mv.addObject("url", CommUtil.getURL(request));
        }else if (of.getOrder_status() < 10){
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "该订单已经取消！");
            mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
        }else{
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
            if((wemall_view_type != null) && (!wemall_view_type.equals("")) && (wemall_view_type.equals("wap"))){
                mv = new JModelAndView("wap/error.html", this.configService.getSysConfig(),
                                       this.userConfigService.getUserConfig(), 1, request, response);
            }
            mv.addObject("op_title", "该订单已经付款！");
            mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
        }

        return mv;
    }
    
    
    
    
    
    /**
     * 点击发消息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({"/panduan.htm"})
    public String fa(HttpServletRequest request, HttpServletResponse response, final String userName){
    	Map<String, String> map = new HashMap<String, String>();
    	map.put("code", "12");
    	map.put("msg", "密码错误");
    	map.put("state", "333");
    	String jsonString = JSON.toJSONString(map);
    	System.out.println("w3q2422222222222222222");
       return jsonString;
    	

    }

    /**
     * 跳转第三方支付页面，进行支付
     * @param request
     * @param response
     * @param payType
     * @param order_id
     * @return
     */
    @SecurityMapping(display = false, rsequence = 0, title = "订单支付", value = "/order_pay.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
    @RequestMapping({ "/order_pay.htm" })
    public ModelAndView order_pay(HttpServletRequest request, HttpServletResponse response, String payType, String order_id,String password){
        ModelAndView mv = null;
        OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
        String wemall_view_type = CommUtil.null2String(request.getSession().getAttribute("wemall_view_type"));
      
        Long userId=SecurityUserHolder.getCurrentUser().getId();
       /* 判断密码并操作
        User user = this.userService.getObjById(userId);
        //没有密码就发短信给他
        if (user.getPassword()==null) {
			//发短信
        	
        	
		}else {
			//判断密码
			String psjm = MD5Util.MD5Encode(password, user.getUserName());
			
			
		}*/
        
        
        
        /*  判断密码结束*/
        if (of.getOrder_status() == 10){
            if (CommUtil.null2String(payType).equals("")){
                mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                                       this.userConfigService.getUserConfig(), 1, request, response);
                if((wemall_view_type != null) && (!wemall_view_type.equals("")) && (wemall_view_type.equals("wap"))){
                    mv = new JModelAndView("wap/error.html", this.configService.getSysConfig(),
                                           this.userConfigService.getUserConfig(), 1, request, response);
                }
                mv.addObject("op_title", "支付方式错误！");
                mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
            }else{
                List payments = new ArrayList();
                List list_temiiOpeningStatus= new ArrayList();
                Map params = new HashMap();
                //判断是否平台支付
                if (this.configService.getSysConfig().getConfig_payment_type() == 1){
                	params.clear();
                    params.put("mark", payType);
                    params.put("type", "admin");
                    payments = this.paymentService.query(
                                   "select obj from Payment obj where obj.mark=:mark and obj.type=:type", params, -1, -1);
                    
                }else{
                    params.put("mark", payType);
                    params.put("store_id", of.getStore().getId());
                    payments = this.paymentService.query(
                                   "select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id", params, -1, -1);
                }
                of.setPayment((Payment) payments.get(0));
                this.orderFormService.update(of);
                if (payType.equals("balance")){// 余额支付
                    mv = new JModelAndView("balance_pay.html", this.configService.getSysConfig(),
                                           this.userConfigService.getUserConfig(), 1, request, response);
                }else if (payType.equals("shexiao")){// 赊销支付
                    
//                  mv.addObject("store_id",
//                  		this.orderFormService.getObjById(CommUtil.null2Long(order_id)).getStore().getId());
                  
//					Long userId= SecurityUserHolder.getCurrentUser().getId();
					
              	mv = new JModelAndView("success.html", this.configService.getSysConfig(),
                          this.userConfigService.getUserConfig(), 1, request, response);
					of.setPay_msg("赊销支付");
					of.setPay_status(1);//待还款
//修改BuyerCreditLimit买家 赊销额度信息的剩余额度
					
//					BuyerCreditLimitQueryObject bqo=new BuyerCreditLimitQueryObject(); 
//					bqo.addQuery("obj.buyerId",new SysMap("buyerId",userId.intValue()), "=");
//					bqo.setOrderBy("addTime");
//					bqo.setOrderType("desc");
//					IPageList pList = this.buyerCreditLimitService.list(bqo);
//					BuyerCreditLimit buyerCreditLimit=(BuyerCreditLimit) pList.getResult().get(0);
					
					params.clear();
					params.put("buyerId",  userId.intValue());//buyerId是buyerCreditLimit表的买家ID
	        		params.put("storeId", of.getStore().getId().intValue());
					List buyerCreditLineList=this.buyerCreditLimitService.query(
	            			"select obj from BuyerCreditLimit obj where obj.buyerId=:buyerId and obj.storeId=:storeId", params, -1, -1);
	            	if (buyerCreditLineList.size()>0){
	            	BuyerCreditLimit buyerCreditLimit=(BuyerCreditLimit) buyerCreditLineList.get(0);
					
					//买家剩余额度
					BigDecimal BuyerRemainingUndrawnBD=new BigDecimal(buyerCreditLimit.getBuyerRemainingUndrawn());
					
					BigDecimal buyerRemainingUndrawn=BuyerRemainingUndrawnBD.subtract(of.getTotalPrice());
					if(BuyerRemainingUndrawnBD.compareTo(of.getTotalPrice())==-1){
					mv = new JModelAndView("error.html", this.configService.getSysConfig(),
					        this.userConfigService.getUserConfig(), 1, request, response);
					mv.addObject("op_title", "赊销余额不足，请先提升赊销额度！");
					mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
					return mv;
					}
	            	
					buyerRemainingUndrawn=buyerRemainingUndrawn.setScale(2, BigDecimal.ROUND_HALF_UP);
					buyerCreditLimit.setBuyerRemainingUndrawn(buyerRemainingUndrawn.toString());
					this.buyerCreditLimitService.update(buyerCreditLimit);
					
	            	}
					Map shexiaoparams = new HashMap();
					shexiaoparams.put("mark", "shexiao");
					shexiaoparams.put("store_id", of.getStore().getId());
					payments = this.paymentService.query(
					                "select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id", shexiaoparams, -1, -1);
					if (payments.size() > 0){
					of.setPayment((Payment) payments.get(0));
					}
					of.setPayTime(new Date());
					of.setOrder_status(20);
					of.setPay_status(1);
					this.orderFormService.update(of);
					/* if (this.configService.getSysConfig().isSmsEnbale()){
					send_sms(request, of, of.getStore().getUser().getMobile(), "sms_toseller_shexiao_pay_ok_notify");
					}
					if (this.configService.getSysConfig().isEmailEnable()){
					send_email(request, of, of.getStore().getUser().getEmail(), "email_toseller_shexiao_pay_ok_notify");
					}*/
					
					
					
					//   添加赊销记录log
					User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
					PredepositLog log = new PredepositLog();
					log.setAddTime(new Date());
					log.setPd_log_user(user);
					log.setPd_op_type("消费");
					log.setPd_log_amount(BigDecimal.valueOf(-CommUtil.null2Double(of.getTotalPrice())));
					log.setPd_log_info("订单" + of.getOrder_id() + "购物减少可用额度");
					log.setPd_type("可用赊销额度");
					this.predepositLogService.save(log);
					
					OrderFormLog ofl = new OrderFormLog();
					ofl.setAddTime(new Date());
					ofl.setLog_info("提交赊销支付申请");
					ofl.setLog_user(SecurityUserHolder.getCurrentUser());
					ofl.setOf(of);
					this.orderFormLogService.save(ofl);
					mv.addObject("op_title", "赊销支付提交成功，等待卖家发货！");
					//站内短信
					String content= "订单"+ofl.getOf().getOrder_id()+"付款成功！"+ ofl.getOf().getStore().getStore_name()+"已收到您的订单。请耐心等待商家发货。";
					 User toUser = this.userService.getObjById(ofl.getLog_user().getId());
	                 if (toUser != null){
	                     Message msg = new Message();
	                     msg.setAddTime(new Date());
	                     Whitelist whiteList = new Whitelist();
	                     content = content.replaceAll("\n", "iskyhop_br");
	                     msg.setContent(Jsoup.clean(content, Whitelist.basic())
	                                    .replaceAll("iskyhop_br", "\n"));
	                     msg.setFromUser(of.getStore().getUser());
	                     msg.setToUser(SecurityUserHolder.getCurrentUser());
	                     msg.setType(1);
	                     this.messageService.save(msg);
	                     //店家
	                     Map map = new HashMap();
	                 	map.put("userName",toUser.getUsername());
	                 	List<Authentication> auths = authenticationService
	             				.query("select obj from Authentication obj where obj.userName=:userName", map, -1, -1);
	                 	
	                     String content1=auths.get(0).getEnterpriseName()+"在您的店铺下单并付款成功！请及时发货。前往“商家中心”——“我的 订单”查看详情。";
	                     Message msg1 = new Message();
	                     msg1.setAddTime(new Date());
	                     Whitelist whiteList1 = new Whitelist();
	                     content1 = content1.replaceAll("\n", "iskyhop_br");
	                     msg1.setContent(Jsoup.clean(content1, Whitelist.basic())
	                                    .replaceAll("iskyhop_br", "\n"));
	                     msg1.setFromUser(SecurityUserHolder.getCurrentUser());
	                     msg1.setToUser(of.getStore().getUser());
	                     msg1.setType(1);
	                     this.messageService.save(msg1);
	                 }
					mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
                  
              }else if (payType.equals("outline")){// 特米白条支付
                	params.clear();
                	params.put("id", userId);
                	List userList = this.userService.query(
                            "select userName from User obj where obj.id=:id", params, -1, -1);
                	
                	params.clear();
                	params.put("userName", userList.get(0).toString());
                	params.put("temiiOpeningStatus", 1);
                	list_temiiOpeningStatus = this.iouService.query(
                             "select temiiOpeningStatus from IOU obj where obj.userName=:userName and obj.temiiOpeningStatus=:temiiOpeningStatus", params, -1, -1);
                	
                	int temiiOpeningStatuslength=list_temiiOpeningStatus.size();
                	params.clear();
                	if (temiiOpeningStatuslength<0){
                		//已开通白条    特米白条接口调用
                		/*mv = new JModelAndView("payment_IousToPay.html", this.configService.getSysConfig(),
                                this.userConfigService.getUserConfig(), 1, request, response);*/
                		mv = new JModelAndView("success.html", this.configService.getSysConfig(),
                                this.userConfigService.getUserConfig(), 1, request, response);
                		String pay_session = CommUtil.randomString(32);
                		request.getSession(false).setAttribute("pay_session", pay_session);
                		mv.addObject("op_title", "特米白条支付成功！");
                		mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm?order_status=order_submit");
                	}else {
                		//未开通白条
                		mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                                this.userConfigService.getUserConfig(), 1, request, response);
                    	mv.addObject("op_title", "未开通白条支付，请先开通白条支付！");
                    	mv.addObject("url", CommUtil.getURL(request) + "/payment_OpeningStatus.html");
                    	return mv;
                	}
                }else if (payType.equals("outline")){// 线下支付
                    mv = new JModelAndView("outline_pay.html", this.configService.getSysConfig(),
                            this.userConfigService.getUserConfig(), 1, request, response);
                    String pay_session = CommUtil.randomString(32);
                    request.getSession(false).setAttribute("pay_session", pay_session);
                    mv.addObject("paymentTools", this.paymentTools);
                    mv.addObject("store_id",
                    		this.orderFormService.getObjById(CommUtil.null2Long(order_id)).getStore().getId());
                    mv.addObject("pay_session", pay_session);
                }else if (payType.equals("payafter")){// 货到付款
                    mv = new JModelAndView("payafter_pay.html", this.configService.getSysConfig(),
                                           this.userConfigService.getUserConfig(), 1, request, response);
                    String pay_session = CommUtil.randomString(32);
                    request.getSession(false).setAttribute("pay_session", pay_session);
                    mv.addObject("paymentTools", this.paymentTools);
                    mv.addObject("store_id",
                                 this.orderFormService.getObjById(CommUtil.null2Long(order_id)).getStore().getId());
                    mv.addObject("pay_session", pay_session);
                }else{// 在线支付
                    mv = new JModelAndView("line_pay.html", this.configService.getSysConfig(),
                                           this.userConfigService.getUserConfig(), 1, request, response);
                    mv.addObject("payType", payType);
                    mv.addObject("url", CommUtil.getURL(request));
                    mv.addObject("payTools", this.payTools);
                    mv.addObject("type", "goods");
                    mv.addObject("payment_id", of.getPayment().getId());
                }
                mv.addObject("order_id", order_id);
            }
        }else{
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
            if((wemall_view_type != null) && (!wemall_view_type.equals("")) && (wemall_view_type.equals("wap"))){
                mv = new JModelAndView("wap/error.html", this.configService.getSysConfig(),
                                       this.userConfigService.getUserConfig(), 1, request, response);
            }
            mv.addObject("op_title", "该订单不能进行付款！");
            mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
        }

        return mv;
    }
    /**
     * wap支付提交
     */
    @SecurityMapping(display = false, rsequence = 0, title = "wap订单支付", value = "/wxwap_submit.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
    @RequestMapping({ "/pay_submit.htm" })
    public String paymentSubmit(HttpServletRequest request,
                                HttpServletResponse response, String payType, String order_id){
        OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(order_id));

        if (of != null && of.getOrder_status() == 10){
            List payments = new ArrayList();
            Map params = new HashMap();
            // 1为平台支付:
            if (this.configService.getSysConfig().getConfig_payment_type() == 1){
                params.put("mark", payType);
                params.put("type", "admin");
                payments = this.paymentService.query("select obj from Payment obj where obj.mark=:mark and obj.type=:type", params, -1, -1);
            }else{// 店铺支付
                params.put("store_id", of.getStore().getId());
                params.put("mark", payType);
                payments = this.paymentService.query("select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id", params, -1, -1);
            }
            // 支付方式已经配置:wap支持支付宝wap支付以及微信公众号支付
            if (payments.size() > 0){
                of.setPayment((Payment) payments.get(0));
                this.orderFormService.update(of);
                // 微信公众号支付
                if (payType.equals("weixin_wap")){
                    String APPID = of.getPayment().getWeixin_appId();
                    String siteURL = CommUtil.getURL(request);
                    String out_trade_no = of.getId().toString();

                    return "redirect:https://open.weixin.qq.com/connect/oauth2/authorize?appid="
                           + APPID
                           + "&redirect_uri="
                           + siteURL
                           + "/wechat/oauthCode.htm?sn="
                           + out_trade_no
                           + "&response_type=code&scope=snsapi_base&state=123#wechat_redirect";
                }else if (payType.equals("alipay_wap")){// 支付宝手机网页支付
                    String siteURL = CommUtil.getURL(request);
                    AlipayConfig config = new AlipayConfig();

                    config.setSeller_email(of.getPayment().getSeller_email());
                    config.setKey(of.getPayment().getSafeKey());
                    config.setPartner(of.getPayment().getPartner());
                    config.setSign_type("RSA");
                    // 把请求参数打包成数组
                    Map<String, String> sParaTemp = new HashMap<String, String>();
                    // 调用的接口名，无需修改
                    sParaTemp.put("service", "alipay.wap.create.direct.pay.by.user");
                    // 合作身份者ID，签约账号，以2088开头由16位纯数字组成的字符串，查看地址：https://b.alipay.com/order/pidAndKey.htm
                    sParaTemp.put("partner", of.getPayment().getPartner());
                    // 收款支付宝账号，以2088开头由16位纯数字组成的字符串，一般情况下收款账号就是签约账号
                    sParaTemp.put("seller_id", of.getPayment().getPartner());
                    sParaTemp.put("_input_charset", config.getInput_charset());
                    // 支付类型 ，无需修改
                    sParaTemp.put("payment_type", "1");

                    sParaTemp.put("notify_url", siteURL + "/alipay/alipay_notify.htm");
                    sParaTemp.put("return_url", siteURL + "/alipay/alipay_return.htm");
                    sParaTemp.put("out_trade_no", of.getId().toString());
                    sParaTemp.put("subject", "订单号为" + of.getOrder_id());
                    // 价格测试改为1分钱
                    //sParaTemp.put("total_fee", "0.01");
                    sParaTemp.put("total_fee", of.getTotalPrice().toPlainString());
                    sParaTemp.put("show_url", "/index.htm");
                    sParaTemp.put("body", "支付宝wap支付");
                    // 其他业务参数根据在线开发文档，添加参数.文档地址:https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.2Z6TSk&treeId=60&articleId=103693&docType=1

                    String sHtmlText = AlipaySubmit.buildRequestWap(config, sParaTemp, "get", "确定");

                    try {
                        response.setCharacterEncoding("UTF-8");
                        response.setContentType("text/html");
                        response.getWriter().print(sHtmlText);
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }else{
                    // 支付方式错误
                    return "redirect:" + CommUtil.getURL(request) + "/index.htm?payMethodError";
                }
            }else{
                // 支付方式未配置
                return "redirect:" + CommUtil.getURL(request) + "/index.htm?noPayMethod";
            }
        }else{
            // 该订单状态不正确，不能进行付款！
            return "redirect:" + CommUtil.getURL(request) + "/index.htm?orderError";
        }

        return null;
    }

    /**
     * 微信CODE回调JSP并进行微信授权接口认证获取用户openid
     */
    @RequestMapping({"/wechat/oauthCode.htm"})
    public ModelAndView oauthCode(HttpServletRequest request, HttpServletResponse response){
        logger.info("支付收到微信code回调请求");
        ModelAndView mv = new JModelAndView("wap/wxpay.html", this.configService.getSysConfig(),
                                            this.userConfigService.getUserConfig(), 1, request, response);
        // 用户同意授权后，能获取到code
        String code = request.getParameter("code");
        String sn = request.getParameter("sn");
        HttpSession session = request.getSession();
        String scode = (String)session.getAttribute("wxcode");

        if(code != null && code.equalsIgnoreCase(scode)){
        }else{
            session.setAttribute("wxcode", code);
        }
        String openId = null;
        // 用户同意授权
        if (null != code && !"".equals(code) && !"authdeny".equals(code)){
            OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(sn));

            // 获取网页授权access_token
            WxOauth2Token wxOauth2Token = WxAdvancedUtil.getOauth2AccessToken(of.getPayment().getWeixin_appId(), of.getPayment().getWeixin_appSecret(), code);
            // 用户标识
            if(null != wxOauth2Token){
                openId = wxOauth2Token.getOpenId();
            }
            logger.info("微信code回调请求:openId={},sn={}", openId, sn);

            String prodName = "网上购物";
            String amount = of.getTotalPrice().toString();

            mv.addObject("openId", openId);
            mv.addObject("sn", sn);
            mv.addObject("amount", amount);
            mv.addObject("siteName", this.configService.getSysConfig().getWebsiteName());
            mv.addObject("productName", prodName);

        }else{
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "用户未授权！");
            mv.addObject("url", CommUtil.getURL(request) + "/index.htm?authdeny");
        }

        return mv;
    }

    /**
     * 生成微信订单数据以及微信支付需要的签名等信息，传输到前端，发起调用JSAPI支付
     *
     * @param
     * @return
     * @throws Exception
     */
    @RequestMapping({"/wechat/wxpay.htm"})
    public void wxpay(HttpServletRequest request, HttpServletResponse response, String openId, String sn, String productName, String totalPrice, String clientUrl) throws Exception {
        String APPID = null;
        String APP_SECRET = null;
        String MCH_ID = null;
        String API_KEY = null;
        String siteURL = CommUtil.getURL(request);
        String UNI_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

        logger.info("微信确认支付获取openId={},sn={}" + openId, sn);

        OrderForm of = null;
        String amount = null;
        try {
            of = this.orderFormService.getObjById(CommUtil.null2Long(sn));
        } catch (Exception e){
            logger.error("微信确认支付查询paymentLog异常=" + e.getMessage());
            e.printStackTrace();
        }
        if(of == null){
            amount = "";
            logger.info("微信确认支付查询orderForm=null");
        }else{
            APPID = of.getPayment().getWeixin_appId();
            APP_SECRET = of.getPayment().getWeixin_appSecret();
            MCH_ID = of.getPayment().getWeixin_partnerId();
            API_KEY = of.getPayment().getWeixin_paySignKey();

            /** 将元转换为分 */
            amount = of.getTotalPrice().multiply(new BigDecimal(100)).setScale(0).toString();
            logger.info("微信确认支付元转分成功amount={}", amount);
        }

        SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
        parameters.put("appid", APPID);
        parameters.put("mch_id", MCH_ID);
        parameters.put("nonce_str", WxCommonUtil.createNoncestr());
        parameters.put("body", productName);// 商品名称

        /** 订单号 */
        parameters.put("out_trade_no", sn);
        /** 订单金额以分为单位，只能为整数 */
        //parameters.put("total_fee", "1");//测试用的金额1分钱
        parameters.put("total_fee", amount);
        /** 客户端本地ip */
        parameters.put("spbill_create_ip", request.getRemoteAddr());
        /** 支付回调地址 */
        parameters.put("notify_url", siteURL + "/wechat/paynotify.htm");
        /** 支付方式为JSAPI支付 */
        parameters.put("trade_type", "JSAPI");
        /** 用户微信的openid，当trade_type为JSAPI的时候，该属性字段必须设置 */
        parameters.put("openid", openId);

        /** 使用MD5进行签名，编码必须为UTF-8 */
        String sign = WxCommonUtil.createSignMD5("UTF-8", parameters, API_KEY);

        /**将签名结果加入到map中，用于生成xml格式的字符串*/
        parameters.put("sign", sign);

        /** 生成xml结构的数据，用于统一下单请求的xml请求数据 */
        String requestXML = WxCommonUtil.getRequestXml(parameters);
        logger.info("请求统一支付requestXML：" + requestXML);

        try {
            /** 1、使用POST请求统一下单接口，获取预支付单号prepay_id */
            String result = WxCommonUtil.httpsRequestString(UNI_URL, "POST", requestXML);
            logger.info("请求统一支付result:" + result);
            //解析微信返回的信息，以Map形式存储便于取值
            Map<String, String> map = WxCommonUtil.doXMLParse(result);
            logger.info("预支付单号prepay_id为:" + map.get("prepay_id"));
            //全局map，该map存放前端ajax请求的返回值信息，包括wx.config中的配置参数值，也包括wx.chooseWXPay中的配置参数值
            SortedMap<Object, Object> params = new TreeMap<Object, Object>();
            params.put("appId", APPID);
            params.put("timeStamp", new Date().getTime() + ""); //时间戳
            params.put("nonceStr", WxCommonUtil.createNoncestr()); //随机字符串
            params.put("package", "prepay_id=" + map.get("prepay_id")); //格式必须为 prepay_id=***
            params.put("signType", "MD5"); //签名的方式必须是MD5
            /**
             * 获取预支付prepay_id后，需要再次签名，此次签名是用于前端js中的wx.chooseWXPay中的paySign。
             * 参与签名的参数有5个，分别是：appId、timeStamp、nonceStr、package、signType 注意参数名称的大小写
             */
            String paySign = WxCommonUtil.createSignMD5("UTF-8", params, API_KEY);
            //预支付单号
            params.put("packageValue", "prepay_id=" + map.get("prepay_id"));
            params.put("paySign", paySign); //支付签名
            //付款成功后同步请求的URL，请求我们自定义的支付成功的页面，展示给用户
            params.put("sendUrl", siteURL + "/wechat/paysuccess.htm?totalPrice=" + totalPrice);

            //获取用户的微信客户端版本号，用于前端支付之前进行版本判断，微信版本低于5.0无法使用微信支付
            String userAgent = request.getHeader("user-agent");
            char agent = userAgent.charAt(userAgent.indexOf("MicroMessenger") + 15);
            params.put("agent", new String(new char[] { agent }));

            /**2、获取access_token作为参数传递,由于access_token有有效期限制，和调用次数限制，可以缓存到session或者数据库中.有效期设置为小于7200秒*/
            WxToken wxtoken = WxCommonUtil.getToken(APPID, APP_SECRET);
            String token = wxtoken.getAccessToken();
            logger.info("获取的token值为:" + token);

            /**3、获取凭证ticket发起GET请求 */
            String requestUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?type=jsapi&access_token=" + token;
            logger.info("接口调用凭证ticket的requestUrl：" + requestUrl);

            String ticktresult = WxCommonUtil.httpsRequestString(requestUrl, "GET", null);
            JSONObject jsonresult = JSONObject.fromObject(ticktresult);
            //JSONObject jsonObject = WxCommonUtil.httpsRequest(requestUrl, "GET", null);
            //使用JSSDK支付，需要另一个凭证，也就是jsapi_ticket。这个是JSSDK中使用到的。
            String jsapi_ticket = jsonresult.getString("ticket");
            logger.info("jsapi_ticket：" + jsapi_ticket);
            // 获取到ticket凭证之后，需要进行一次签名
            String config_nonceStr = WxCommonUtil.createNoncestr();// 获取随机字符串
            long config_timestamp = new Date().getTime();// 时间戳
            // 加入签名的参数有4个，分别是： noncestr、jsapi_ticket、timestamp、url，注意字母全部为小写
            SortedMap<Object, Object> configMap = new TreeMap<Object, Object>();
            configMap.put("noncestr", config_nonceStr);
            configMap.put("jsapi_ticket", jsapi_ticket);
            configMap.put("timestamp", config_timestamp + "");
            configMap.put("url", clientUrl);
            //该签名是用于前端js中wx.config配置中的signature值。
            String config_sign = WxCommonUtil.createSignSHA1("UTF-8", configMap);
            // 将config_nonceStr、jsapi_ticket 、config_timestamp、config_sign一同传递到前端
            // 这几个参数名称和上面获取预支付prepay_id使用的参数名称是不一样的，不要混淆了。
            // 这几个参数是提供给前端js代码在调用wx.config中进行配置的参数，wx.config里面的signature值就是这个config_sign的值，以此类推
            params.put("config_nonceStr", config_nonceStr);
            params.put("config_timestamp", config_timestamp + "");
            params.put("config_sign", config_sign);
            // 将map转换为json字符串，传递给前端ajax回调
            String json = JSONArray.fromObject(params).toString();
            logger.info("用于wx.config配置的json：" + json);

            response.setContentType("text/plain");
            response.setHeader("Cache-Control", "no-cache");
            response.setCharacterEncoding("UTF-8");

            try {
                PrintWriter writer = response.getWriter();
                writer.print(json);
            } catch (IOException e){
                e.printStackTrace();
            }
        } catch (JDOMException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 微信扫码支付
     * @param request
     * @param response
     * @param order_id
     * @return
     */
    @RequestMapping({"/wechat/"})
    public void wxcodepay(HttpServletRequest request, HttpServletResponse response, String order_id){
        String UNI_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
        OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
        String returnhtml = null;
        if (of.getOrder_status() == 10){
            List payments = new ArrayList();
            Map params = new HashMap();
            //判断是否平台支付
            if (this.configService.getSysConfig().getConfig_payment_type() == 1){// 平台统一支付
                params.put("mark", "wxcodepay");
                params.put("type", "admin");
                payments = this.paymentService.query(
                               "select obj from Payment obj where obj.mark=:mark and obj.type=:type", params, -1, -1);
            }else{// 店铺支付
                params.put("mark", "wxcodepay");
                params.put("store_id", of.getStore().getId());
                payments = this.paymentService.query(
                               "select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id", params, -1, -1);
            }
            Payment payment = (Payment) payments.get(0);
            of.setPayment(payment);
            this.orderFormService.update(of);

            String codeUrl = "";//微信返回的二维码地址信息

            SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
            parameters.put("appid", payment.getWeixin_appId());// 公众账号id
            parameters.put("mch_id", payment.getWeixin_partnerId());// 商户号
            parameters.put("nonce_str", WxCommonUtil.createNoncestr());// 随机字符串
            parameters.put("body", "在线购物");// 商品描述
            parameters.put("out_trade_no", order_id);// 商户订单号
            parameters.put("total_fee", of.getTotalPrice().multiply(new BigDecimal(100)).setScale(0).toString());// 总金额
            parameters.put("spbill_create_ip", WxCommonUtil.localIp());// 终端IP.Native支付填调用微信支付API的机器IP。
            // 支付成功后回调的action，与JSAPI相同
            parameters.put("notify_url", CommUtil.getURL(request) + "/wechat/paynotify.htm"); //支付成功后回调的action，与JSAPI相同
            parameters.put("trade_type", "NATIVE");// 交易类型
            parameters.put("product_id", order_id);// 商品ID。商品号要唯一,trade_type=NATIVE，此参数必传。此id为二维码中包含的商品ID，商户自行定义
            String sign = WxCommonUtil.createSignMD5("UTF-8", parameters, payment.getWeixin_paySignKey());
            parameters.put("sign", sign);// 签名
            String requestXML = WxCommonUtil.getRequestXml(parameters);
            logger.info("requestXML" + requestXML);
            String result = WxCommonUtil.httpsRequestString(UNI_URL, "POST", requestXML);//WxCommonUtil.httpsRequest(WxConstants.UNIFIED_ORDER_URL, "POST", requestXML);
            // System.out.println(" 微信支付二维码生成" + result);
            Map<String, String> map = new HashMap<String, String>();
            try {
                map = WxCommonUtil.doXMLParse(result);
                logger.info("------------------code_url=" + map.get("code_url") + ";      result_code=" + map.get("code_url") + "------------------------------");
            } catch (Exception e){
                logger.error("doXMLParse()--error", e);
            }
            String returnCode = map.get("return_code");
            String resultCode = map.get("result_code");

            if (returnCode.equalsIgnoreCase("SUCCESS")
                    && resultCode.equalsIgnoreCase("SUCCESS")){
                codeUrl = map.get("code_url");
                // 拿到codeUrl，生成二维码图片
                byte[] imgs = QRCodeEncoderHandler.createQRCode(codeUrl);

                String urls = request.getSession().getServletContext().getRealPath("/") + this.configService.getSysConfig().getUploadFilePath()
                              + java.io.File.separator + "weixin_qr" + java.io.File.separator + "wxpay"
                              + java.io.File.separator;
                // 图片的实际路径
                String imgfile = urls + order_id + ".png";

                QRCodeEncoderHandler.saveImage(imgs, imgfile, "png");

                // 图片的网路路径
                String imgUrl = CommUtil.getURL(request) + "/"
                                + this.configService.getSysConfig().getUploadFilePath()
                                + "/weixin_qr/wxpay/" + order_id + ".png";

                logger.info("图片的网路路径imgurl={}", imgUrl);

                returnhtml = "<img src='" + imgUrl + "' style='width:200px;height:200px;'/>";
            }else{
                returnhtml = "支付状态不正确";
            }
        }
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(returnhtml);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @SecurityMapping(display = false, rsequence = 0, title = "订单线下支付", value = "/order_pay_outline.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
    @RequestMapping({ "/order_pay_outline.htm" })
    public ModelAndView order_pay_outline(HttpServletRequest request, HttpServletResponse response, String payType,
                                          String order_id, String pay_msg, String pay_session) throws Exception {
        ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(),
                                            this.userConfigService.getUserConfig(), 1, request, response);
        String pay_session1 = CommUtil.null2String(request.getSession(false).getAttribute("pay_session"));
        if (pay_session1.equals(pay_session)){
            OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
            of.setPay_msg(pay_msg);
            Map params = new HashMap();
            params.put("mark", "outline");
            params.put("store_id", of.getStore().getId());
            List payments = this.paymentService.query(
                                "select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id", params, -1, -1);
            if (payments.size() > 0){
                of.setPayment((Payment) payments.get(0));
                of.setPayTime(new Date());
            }
            of.setOrder_status(15);
            this.orderFormService.update(of);
            if (this.configService.getSysConfig().isSmsEnbale()){
                send_sms(request, of, of.getStore().getUser().getMobile(), "sms_toseller_outline_pay_ok_notify");
            }
            if (this.configService.getSysConfig().isEmailEnable()){
                send_email(request, of, of.getStore().getUser().getEmail(), "email_toseller_outline_pay_ok_notify");
            }

            OrderFormLog ofl = new OrderFormLog();
            ofl.setAddTime(new Date());
            ofl.setLog_info("提交线下支付申请");
            ofl.setLog_user(SecurityUserHolder.getCurrentUser());
            ofl.setOf(of);
            this.orderFormLogService.save(ofl);
            request.getSession(false).removeAttribute("pay_session");
            mv.addObject("op_title", "线下支付提交成功，等待卖家审核！");
            mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
        }else{
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "订单已经支付，禁止重复支付！");
            mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
        }

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "订单货到付款", value = "/order_pay_payafter.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
    @RequestMapping({ "/order_pay_payafter.htm" })
    public ModelAndView order_pay_payafter(HttpServletRequest request, HttpServletResponse response, String payType,
                                           String order_id, String pay_msg, String pay_session) throws Exception {
        ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(),
                                            this.userConfigService.getUserConfig(), 1, request, response);
        String pay_session1 = CommUtil.null2String(request.getSession(false).getAttribute("pay_session"));
        if (pay_session1.equals(pay_session)){
            OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
            of.setPay_msg(pay_msg);
            Map params = new HashMap();
            params.put("mark", "payafter");
            params.put("store_id", of.getStore().getId());
            List payments = this.paymentService.query(
                                "select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id", params, -1, -1);
            if (payments.size() > 0){
                of.setPayment((Payment) payments.get(0));
                of.setPayTime(new Date());
            }
            of.setOrder_status(16);
            this.orderFormService.update(of);
            if (this.configService.getSysConfig().isSmsEnbale()){
                send_sms(request, of, of.getStore().getUser().getMobile(), "sms_toseller_payafter_pay_ok_notify");
            }
            if (this.configService.getSysConfig().isEmailEnable()){
                send_email(request, of, of.getStore().getUser().getEmail(), "email_toseller_payafter_pay_ok_notify");
            }

            OrderFormLog ofl = new OrderFormLog();
            ofl.setAddTime(new Date());
            ofl.setLog_info("提交货到付款申请");
            ofl.setLog_user(SecurityUserHolder.getCurrentUser());
            ofl.setOf(of);
            this.orderFormLogService.save(ofl);
            request.getSession(false).removeAttribute("pay_session");
            mv.addObject("op_title", "货到付款提交成功，等待卖家发货！");
            mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
        }else{
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "订单已经支付，禁止重复支付！");
            mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
        }

        return mv;
    }
    
//    /** 订单赊销支付 **/
//    @SecurityMapping(display = false, rsequence = 0, title = "订单赊销支付", value = "/order_pay_shexiao.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
//    @RequestMapping({ "/order_pay_shexiao.htm" })
//    public ModelAndView order_pay_shexiao(HttpServletRequest request, HttpServletResponse response, String payType,
//    		   String pay_session,  String order_id, String pay_msg  ) throws Exception {
//        ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(),
//                                            this.userConfigService.getUserConfig(), 1, request, response);
//        Long userId= SecurityUserHolder.getCurrentUser().getId();
//        
//        String pay_session1  = Md5Encrypt.md5(userId.toString()).toLowerCase();
//       
//        if (pay_session1.equals(pay_session)){
//            OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
//            of.setPay_msg(pay_msg);
//            Map params = new HashMap();
//            params.put("mark", "shexiao");
//            params.put("store_id", of.getStore().getId());
//            List payments = this.paymentService.query(
//                                "select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id", params, -1, -1);
//            if (payments.size() > 0){
//                of.setPayment((Payment) payments.get(0));
//                of.setPayTime(new Date());
//            }
//            of.setOrder_status(16);
//            this.orderFormService.update(of);
//           /* if (this.configService.getSysConfig().isSmsEnbale()){
//                send_sms(request, of, of.getStore().getUser().getMobile(), "sms_toseller_shexiao_pay_ok_notify");
//            }
//            if (this.configService.getSysConfig().isEmailEnable()){
//                send_email(request, of, of.getStore().getUser().getEmail(), "email_toseller_shexiao_pay_ok_notify");
//            }*/
//            
//          //修改BuyerCreditLimit买家 赊销额度信息的剩余额度
//    		
//    		BuyerCreditLimitQueryObject bqo=new BuyerCreditLimitQueryObject(); 
//    		bqo.addQuery("obj.buyerId",new SysMap("buyerId",userId.intValue()), "=");
//    		bqo.setOrderBy("addTime");
//    		bqo.setOrderType("desc");
//            IPageList pList = this.buyerCreditLimitService.list(bqo);
//            BuyerCreditLimit buyerCreditLimit=(BuyerCreditLimit) pList.getResult().get(0);
//    		
//            //买家剩余额度
//            BigDecimal BuyerRemainingUndrawnBD=new BigDecimal(buyerCreditLimit.getBuyerRemainingUndrawn());
//            
//            BigDecimal buyerRemainingUndrawn=BuyerRemainingUndrawnBD.subtract(of.getTotalPrice());
//        	if(BuyerRemainingUndrawnBD.compareTo(of.getTotalPrice())==-1){
//        		mv = new JModelAndView("error.html", this.configService.getSysConfig(),
//                        this.userConfigService.getUserConfig(), 1, request, response);
//            	mv.addObject("op_title", "赊销余额不足，请先提升赊销额度！");
//            	mv.addObject("url", CommUtil.getURL(request) + "/goods_cart3.htm");
//            	return mv;
//        	}
//        	buyerRemainingUndrawn=buyerRemainingUndrawn.setScale(2, BigDecimal.ROUND_HALF_UP);
//        	buyerCreditLimit.setBuyerRemainingUndrawn(buyerRemainingUndrawn.toString());
//        	this.buyerCreditLimitService.update(buyerCreditLimit);
//
//        	//   添加赊销记录log
////        	RecordCreditLog recordCreditLog=new RecordCreditLog();
////        	recordCreditLog.setUserId(String.valueOf(userId));
////        	params.clear();
////        	params.put("id", userId);
////        	List userList = this.userService.query(
////                    "select userName from User obj where obj.id=:id", params, -1, -1);
////        	recordCreditLog.setUserName("123");
////        	recordCreditLog.setConsumptionQuota("123");
////        	recordCreditLog.setOrdernumber(of.getOrder_id());
////        	recordCreditLog.setConsumptionType("消费");
////        	recordCreditLog.setUsertype("0");
////        	recordCreditLog.setAddTime(new Date());
////        	recordCreditLog.setUserId("0");
////        	recordCreditLog.setId(Long.parseLong("0"));
////    		this.recordCreditLogService.save(recordCreditLog);
//        	User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
//        	PredepositLog log = new PredepositLog();
//            log.setAddTime(new Date());
//            log.setPd_log_user(user);
//            log.setPd_op_type("消费");
//            log.setPd_log_amount(BigDecimal.valueOf(-CommUtil.null2Double(of.getTotalPrice())));
//            log.setPd_log_info("订单" + of.getOrder_id() + "购物减少可用额度");
//            log.setPd_type("可用赊销额度");
//            this.predepositLogService.save(log);
//    		
//            OrderFormLog ofl = new OrderFormLog();
//            ofl.setAddTime(new Date());
//            ofl.setLog_info("提交赊销支付申请");
//            ofl.setLog_user(SecurityUserHolder.getCurrentUser());
//            ofl.setOf(of);
//            this.orderFormLogService.save(ofl);
//            
//            
//            request.getSession(false).removeAttribute("pay_session");
//            mv.addObject("op_title", "赊销支付提交成功，等待卖家发货！");
//            mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
//        }else{
//            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
//                                   this.userConfigService.getUserConfig(), 1, request, response);
//            mv.addObject("op_title", "订单已经支付，禁止重复支付！");
//            mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
//        }
//
//        return mv;
//    }

    @SecurityMapping(display = false, rsequence = 0, title = "订单预付款支付", value = "/order_pay_balance.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
    @RequestMapping({ "/order_pay_balance.htm" })
    public ModelAndView order_pay_balance(HttpServletRequest request, HttpServletResponse response, String payType,
                                          String order_id, String pay_msg) throws Exception {
        ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(),
                                            this.userConfigService.getUserConfig(), 1, request, response);
        OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
        User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());

        if (CommUtil.null2Double(user.getAvailableBalance()) > CommUtil.null2Double(of.getTotalPrice())){
            of.setPay_msg(pay_msg);
            of.setOrder_status(20);
            Map params = new HashMap();
            params.put("mark", "balance");
            params.put("store_id", of.getStore().getId());
            List payments = this.paymentService.query(
                                "select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id", params, -1, -1);
            if (payments.size() > 0){
                of.setPayment((Payment) payments.get(0));
                of.setPayTime(new Date());
            }
            boolean ret = this.orderFormService.update(of);
            if (this.configService.getSysConfig().isEmailEnable()){
                send_email(request, of, of.getStore().getUser().getEmail(), "email_toseller_balance_pay_ok_notify");
                send_email(request, of, of.getStore().getUser().getEmail(), "email_tobuyer_balance_pay_ok_notify");
            }
            if (this.configService.getSysConfig().isSmsEnbale()){
                send_sms(request, of, of.getStore().getUser().getMobile(), "sms_toseller_balance_pay_ok_notify");
                send_sms(request, of, of.getUser().getMobile(), "sms_tobuyer_balance_pay_ok_notify");
            }
            if (ret){
                user.setAvailableBalance(
                    BigDecimal.valueOf(CommUtil.subtract(user.getAvailableBalance(), of.getTotalPrice())));
                user.setFreezeBlance(BigDecimal.valueOf(CommUtil.add(user.getFreezeBlance(), of.getTotalPrice())));
                this.userService.update(user);
                PredepositLog log = new PredepositLog();
                log.setAddTime(new Date());
                log.setPd_log_user(user);
                log.setPd_op_type("消费");
                log.setPd_log_amount(BigDecimal.valueOf(-CommUtil.null2Double(of.getTotalPrice())));
                log.setPd_log_info("订单" + of.getOrder_id() + "购物减少可用预存款");
                log.setPd_type("可用预存款");
                this.predepositLogService.save(log);

                for (GoodsCart gc : of.getGcs()){
                    Goods goods = gc.getGoods();
                    if ((goods.getGroup() != null) && (goods.getGroup_buy() == 2)){
                        for (GroupGoods gg : goods.getGroup_goods_list()){
                            if (gg.getGroup().getId().equals(goods.getGroup().getId())){
                                gg.setGg_count(gg.getGg_count() - gc.getCount());
                                gg.setGg_def_count(gg.getGg_def_count() + gc.getCount());
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
                    Map temp;
                    if (goods.getInventory_type().equals("all")){
                        goods.setGoods_inventory(goods.getGoods_inventory() - gc.getCount());
                    }else{
                        List list = (List) Json.fromJson(ArrayList.class, goods.getGoods_inventory_detail());
                        for (Iterator localIterator4 = list.iterator(); localIterator4.hasNext();){
                            temp = (Map) localIterator4.next();
                            String[] temp_ids = CommUtil.null2String(temp.get("id")).split("_");
                            Arrays.sort(temp_ids);
                            Arrays.sort(gsp_list);
                            if (Arrays.equals(temp_ids, gsp_list)){
                                temp.put("count",
                                         Integer.valueOf(CommUtil.null2Int(temp.get("count")) - gc.getCount()));
                            }
                        }
                        goods.setGoods_inventory_detail(Json.toJson(list, JsonFormat.compact()));
                    }
                    for (GroupGoods gg : goods.getGroup_goods_list()){
                        if ((!gg.getGroup().getId().equals(goods.getGroup().getId())) || (gg.getGg_count() != 0))
                            continue;
                        goods.setGroup_buy(3);
                    }

                    this.goodsService.update(goods);
                }

            }

            OrderFormLog ofl = new OrderFormLog();
            ofl.setAddTime(new Date());
            ofl.setLog_info("预付款支付");
            ofl.setLog_user(SecurityUserHolder.getCurrentUser());
            ofl.setOf(of);
            this.orderFormLogService.save(ofl);
            mv.addObject("op_title", "预付款支付成功！");
            mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
        }else{
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "可用余额不足，支付失败！");
            mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
        }

        return mv;
    }

    
    @SecurityMapping(display = false, rsequence = 0, title = "订单支付结果", value = "/order_finish.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
    @RequestMapping({ "/order_finish.htm" })
    public ModelAndView order_finish(HttpServletRequest request, HttpServletResponse response, String order_id){
        ModelAndView mv = new JModelAndView("order_finish.html", this.configService.getSysConfig(),
                                            this.userConfigService.getUserConfig(), 1, request, response);
        String wemall_view_type = CommUtil.null2String(request.getSession().getAttribute("wemall_view_type"));
        if((wemall_view_type != null) && (!wemall_view_type.equals("")) && (wemall_view_type.equals("wap"))){
            mv = new JModelAndView("wap/order_finish.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
        }
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
        mv.addObject("obj", obj);

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "地址新增", value = "/cart_address.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
    @RequestMapping({ "/cart_address.htm" })
    public ModelAndView cart_address(HttpServletRequest request, HttpServletResponse response, String id, String store_id){
        ModelAndView mv = new JModelAndView("cart_address.html", this.configService.getSysConfig(),
                                            this.userConfigService.getUserConfig(), 1, request, response);
        String wemall_view_type = CommUtil.null2String(request.getSession().getAttribute("wemall_view_type"));
        if((wemall_view_type != null) && (!wemall_view_type.equals("")) && (wemall_view_type.equals("wap"))){
            mv = new JModelAndView("wap/cart_address.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
        }
        List areas = this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1);
        mv.addObject("areas", areas);
        mv.addObject("store_id", store_id);

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "购物车中收货地址保存", value = "/cart_address_save.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
    @RequestMapping({ "/cart_address_save.htm" })
    public String cart_address_save(HttpServletRequest request, HttpServletResponse response, String id, String area_id, String store_id){
        WebForm wf = new WebForm();
        Address address = null;
        if (id.equals("")){
            address = (Address) wf.toPo(request, Address.class);
            address.setAddTime(new Date());
        }else{
            Address obj = this.addressService.getObjById(Long.valueOf(Long.parseLong(id)));
            address = (Address) wf.toPo(request, obj);
        }
        address.setUser(SecurityUserHolder.getCurrentUser());
        Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
        address.setArea(area);
        if (id.equals(""))
            this.addressService.save(address);
        else
            this.addressService.update(address);

        return "redirect:goods_cart2.htm?store_id=" + store_id;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "地址切换", value = "/order_address.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
    @RequestMapping({ "/order_address.htm" })
    public void order_address(HttpServletRequest request, HttpServletResponse response, String addr_id,
                              String store_id){
        List<StoreCart> cart = (List) request.getSession(false).getAttribute("cart");
        StoreCart sc = null;
        if (cart != null){
            for (StoreCart sc1 : cart){
                if (sc1.getStore().getId().equals(CommUtil.null2Long(store_id))){
                    sc = sc1;
                    break;
                }
            }
        }
        Address addr = this.addressService.getObjById(CommUtil.null2Long(addr_id));
        List sms = this.transportTools.query_cart_trans(sc, CommUtil.null2String(addr.getArea().getId()));

        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(Json.toJson(sms, JsonFormat.compact()));
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @SecurityMapping(display = false, rsequence = 0, title = "收货地址列表", value = "/address.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({"/address.htm"})
    public ModelAndView address(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType, String store_id){
        ModelAndView mv = new JModelAndView("address.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        String wemall_view_type = CommUtil.null2String(request.getSession().getAttribute("wemall_view_type"));
        if((wemall_view_type != null) && (!wemall_view_type.equals("")) && (wemall_view_type.equals("wap"))){
            mv = new JModelAndView("wap/address.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        }
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))){
            url = CommUtil.getURL(request);
        }
        String params = "";
        AddressQueryObject qo = new AddressQueryObject(currentPage, mv, orderBy, orderType);
        qo.addQuery("obj.user.id", new SysMap("user_id", SecurityUserHolder.getCurrentUser().getId()), "=");
        IPageList pList = this.addressService.list(qo);
        CommUtil.saveIPageList2ModelAndView(url + "/address.htm", "", params, pList, mv);
        List areas = this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1);
        mv.addObject("areas", areas);
        mv.addObject("store_id", store_id);

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "修改收货地址", value = "/address_edit.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({"/address_edit.htm"})
    public ModelAndView address_edit(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String store_id){
        ModelAndView mv = new JModelAndView("cart_address.html", this.configService.getSysConfig(),
                                            this.userConfigService.getUserConfig(), 1, request, response);
        String wemall_view_type = CommUtil.null2String(request.getSession().getAttribute("wemall_view_type"));
        if((wemall_view_type != null) && (!wemall_view_type.equals("")) && (wemall_view_type.equals("wap"))){
            mv = new JModelAndView("wap/cart_address.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
        }
        List areas = this.areaService.query("select obj from Area obj where obj.parent.id is null", null, -1, -1);
        Address obj = this.addressService.getObjById(CommUtil.null2Long(id));
        mv.addObject("obj", obj);
        mv.addObject("areas", areas);
        mv.addObject("store_id", store_id);
        mv.addObject("currentPage", currentPage);

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "收货地址删除", value = "/address_del.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({"/address_del.htm"})
    public String address_del(HttpServletRequest request, HttpServletResponse response, String mulitId, String currentPage, String store_id){
        String[] ids = mulitId.split(",");
        for (String id : ids){
            if (!id.equals("")){
                Address address = this.addressService.getObjById(Long.valueOf(Long.parseLong(id)));
                this.addressService.delete(Long.valueOf(Long.parseLong(id)));
            }
        }

        return "redirect:goods_cart2.htm?store_id=" + store_id;
    }


    private void send_email(HttpServletRequest request, OrderForm order, String email, String mark) throws Exception {
        com.wemall.foundation.domain.Template template = this.templateService.getObjByProperty("mark", mark);
        if ((template != null) && (template.isOpen())){
            String subject = template.getTitle();
            String path = request.getSession().getServletContext().getRealPath("") + File.separator + "vm"
                          + File.separator;
            if (!CommUtil.fileExist(path)){
                CommUtil.createFolder(path);
            }
            PrintWriter pwrite = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(path + "msg.vm", false), "UTF-8"));
            pwrite.print(template.getContent());
            pwrite.flush();
            pwrite.close();

            Properties p = new Properties();
            p.setProperty("file.resource.loader.path", request.getRealPath("/") + "vm" + File.separator);
            p.setProperty("input.encoding", "UTF-8");
            p.setProperty("output.encoding", "UTF-8");
            Velocity.init(p);
            org.apache.velocity.Template blank = Velocity.getTemplate("msg.vm", "UTF-8");
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
            String path = request.getSession().getServletContext().getRealPath("") + File.separator + "vm"
                          + File.separator;
            if (!CommUtil.fileExist(path)){
                CommUtil.createFolder(path);
            }
            PrintWriter pwrite = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(path + "msg.vm", false), "UTF-8"));
            pwrite.print(template.getContent());
            pwrite.flush();
            pwrite.close();

            Properties p = new Properties();
            p.setProperty("file.resource.loader.path", request.getRealPath("/") + "vm" + File.separator);
            p.setProperty("input.encoding", "UTF-8");
            p.setProperty("output.encoding", "UTF-8");
            Velocity.init(p);
            org.apache.velocity.Template blank = Velocity.getTemplate("msg.vm", "UTF-8");
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
    
    /*特米白条支付成功*/
    @RequestMapping({"/temii_success.htm"})
    public ModelAndView temii_OpeningStatus(HttpServletRequest request, HttpServletResponse response, String certificate, String license, String GSPCertificate, String letterOfInstruction,String CopyOfIdCard)
    throws HttpException, IOException {
    	 ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        
          String userName =  CommUtil.null2String(request.getSession(false).getAttribute("user"));
          mv.addObject("current_url", "success.html");
          mv.addObject("op_title", "特米白条支付成功！");

          mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
         
           IOU iou =new IOU();
           iou.setUserName(userName);
           iou.setTemiiOpeningStatus(1);
           iou.setCertificate(certificate);
           iou.setLicense(license);
           iou.setGSPCertificate(GSPCertificate);
           iou.setLetterOfInstruction(letterOfInstruction);
           iou.setCopyOfIdCard(CopyOfIdCard);
           
           SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:dd");
           iou.setAddtime(sdf.format(new Date()));
           this.iouService.save(iou);

        return  mv;
    }
    
    /** 订单赊销支付 **/
    @SecurityMapping(display = false, rsequence = 0, title = "订单赊销支付", value = "/order_pay_temii.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
    @RequestMapping({ "/order_pay_temii.htm" })
    public ModelAndView order_pay_temii(HttpServletRequest request, HttpServletResponse response, String payType,
                                           String order_id, String pay_msg, String pay_session) throws Exception {
        ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(),
                                            this.userConfigService.getUserConfig(), 1, request, response);
        String pay_session1 = CommUtil.null2String(request.getSession(false).getAttribute("pay_session"));
        Long userId= SecurityUserHolder.getCurrentUser().getId();
        if (pay_session1.equals(pay_session)){
            OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
            of.setPay_msg(pay_msg);
            Map params = new HashMap();
            params.put("mark", "temii");
            params.put("store_id", of.getStore().getId());
            List payments = this.paymentService.query(
                                "select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id", params, -1, -1);
            if (payments.size() > 0){
                of.setPayment((Payment) payments.get(0));
                of.setPayTime(new Date());
            }
            of.setOrder_status(16);
            this.orderFormService.update(of);
           /* if (this.configService.getSysConfig().isSmsEnbale()){
                send_sms(request, of, of.getStore().getUser().getMobile(), "sms_toseller_shexiao_pay_ok_notify");
            }
            if (this.configService.getSysConfig().isEmailEnable()){
                send_email(request, of, of.getStore().getUser().getEmail(), "email_toseller_shexiao_pay_ok_notify");
            }*/
            
            /**特米白条接口调用开始**/
            BigDecimal jine=of.getTotalPrice();
            /**特米白条接口调用结束**/
            params.clear();
        	params.put("id", userId);
        	List userList = this.userService.query(
                    "select userName from User obj where obj.id=:id", params, -1, -1);
        	TemiIous temiIous=this.temiIousService.getObjByProperty("userName", userList.get(0).toString());
            BigDecimal Balance=new BigDecimal(temiIous.getBalance());
            temiIous.setBalance(Balance.subtract(of.getTotalPrice()).toString());
            this.temiIousService.update(temiIous);
            
            //添加 特米白条   日志记录  log
            TemiIousLog temiIousLog=new TemiIousLog();
            temiIousLog.setUserName(userList.get(0).toString());
            temiIousLog.setConsumptionType("消费");
            temiIousLog.setSumOfConsumption(of.getTotalPrice().toString());
            temiIousLog.setAddTime(new Date());
            this.temiIousLogService.save(temiIousLog);
    		
            OrderFormLog ofl = new OrderFormLog();
            ofl.setAddTime(new Date());
            ofl.setLog_info("提交特米白条支付申请");
            ofl.setLog_user(SecurityUserHolder.getCurrentUser());
            ofl.setOf(of);
            this.orderFormLogService.save(ofl);
            
            request.getSession(false).removeAttribute("pay_session");
            mv.addObject("op_title", "特米白条支付提交成功，等待卖家发货！");
            mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
        }else{
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "订单已经支付，禁止重复支付！");
            mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
        }

        return mv;
    }
    /**
     * 初始化密码
     * 
     */
    @RequestMapping("/insertPwd.htm")
    public void insertPwd(HttpServletRequest request,HttpServletResponse response,String payPwd,String verificationCode,String userName){
    	System.out.println("进入初始化密码方法");
    	String  flag="true";
		if(!request.getSession().getAttribute("code").equals(verificationCode)){
			flag="codeerro";
		}else {
			HttpClass hc = new	HttpClass();
	    	try {
				/*hc.load("http://127.0.0.1:8081/ssm_project/insertPwd", "payPwd="+Md5Encrypt.md5(payPwd).toLowerCase()+"&"+"userName="+userName);*/
	    		User user = SecurityUserHolder.getCurrentUser();
	    		user.setPayPwd(Md5Encrypt.md5(payPwd).toLowerCase());
	    		userService.update(user);
			
	    	} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		response.setContentType("text/plain");
	        response.setHeader("Cache-Control", "no-cache");
	        response.setCharacterEncoding("UTF-8");
	        try {
				response.getWriter().print(flag);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }
    
    
    /**
     * 支付页面判断密码是否为空
     * 为空获取验证码设置密码
     * 不为空确认密码后提交跳转支付页面
     */
    @RequestMapping("/checkPayPwd.htm")
    public void getpwd(HttpServletResponse response,HttpServletRequest request,String userName,String payPwd,String code){
    	System.out.println("进入方法====");
    	String result="true";
    	if(payPwd!=null&&payPwd!=""){
    		if (request.getSession().getAttribute("code")==null) {
    			result="codeerro";
			}else if (!request.getSession().getAttribute("code").equals(code)) {
				result="codeerro";		
			}else {
				 Map params = new HashMap();
		 	        params.put("userName", userName);
		 	        params.put("payPwd", Md5Encrypt.md5(payPwd).toLowerCase());
		 	        List users = this.userService
		 	                     .query("select obj from User obj where obj.userName=:userName and obj.payPwd=:payPwd",
		 	                            params, -1, -1);
				if(users.size()==0){
					result="pwderro";
				}
			}
    	}else {
			result="false";
		}
    		response.setContentType("text/plain");
 	        response.setHeader("Cache-Control", "no-cache");
 	        response.setCharacterEncoding("UTF-8");
    	try {
    		//返回状态
			response.getWriter().print(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    /**
     * 查询买家剩余额度
     */
    @RequestMapping("/buyerlimit.htm")
    public void getbuyerlimit(HttpServletRequest request,HttpServletResponse response,String order_id){
    	
    	//查询首营是否过期
    	String result="";
    	Long userId=SecurityUserHolder.getCurrentUser().getId();
    	 Map params = new HashMap();
  		params.put("userId",  userId.toString());//buyerId是 买家ID
  	    List<Authentication>  authentications	= authenticationService.query(
  	     			"select obj from Authentication obj where   obj.userId=:userId", params, -1, -1);
  	    if (authentications.size()>0&&authentications!=null) {
  	     if (authentications.get(0).getExamine()==3) {
  	    	result="authentifalse3";
  	    	 
		}  else if(authentications.get(0).getExamine()==1) {
			result="authentifalse1";
		}else {
    	 System.out.println("查询赊销支付额度=========");
    	params.clear();
    	 OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
    	 
    	params.clear();
		params.put("buyerId",  userId.intValue());//buyerId是buyerCreditLimit表的买家ID
		params.put("storeId", of.getStore().getId().intValue());
		List buyerCreditLineList=this.buyerCreditLimitService.query(
    			"select obj from BuyerCreditLimit obj where obj.buyerId=:buyerId and obj.storeId=:storeId", params, -1, -1);
		if (buyerCreditLineList.size()>0){
	    	BuyerCreditLimit buyerCreditLimit=(BuyerCreditLimit) buyerCreditLineList.get(0);
			
			//买家剩余额度
			BigDecimal BuyerRemainingUndrawnBD=new BigDecimal(buyerCreditLimit.getBuyerRemainingUndrawn());
			
			BigDecimal buyerRemainingUndrawn=BuyerRemainingUndrawnBD.subtract(of.getTotalPrice());
			if (buyerCreditLimit.getZstatus()==2) {
				//赊销正在审核
				result="limittrue";
			}else if(BuyerRemainingUndrawnBD.compareTo(of.getTotalPrice())==-1){
				//赊销余额不足
			     result="limitfalse";
			     }
			}else{
	    		//买家还没有开启赊销
	    		result="creditfalse";
	    	}
		}
  	    }
    	    response.setContentType("text/plain");
	        response.setHeader("Cache-Control", "no-cache");
	        response.setCharacterEncoding("UTF-8");
	   try {
		//返回状态
		response.getWriter().print(result);
     	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
    }
    
    
}
