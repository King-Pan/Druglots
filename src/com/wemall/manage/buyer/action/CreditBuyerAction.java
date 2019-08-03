package com.wemall.manage.buyer.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wemall.core.annotation.SecurityMapping;
import com.wemall.core.domain.virtual.SysMap;
import com.wemall.core.mv.JModelAndView;
import com.wemall.core.query.PageList;
import com.wemall.core.query.support.IPageList;
import com.wemall.core.security.support.SecurityUserHolder;
import com.wemall.core.tools.CommUtil;
import com.wemall.core.tools.HttpClass;
import com.wemall.foundation.domain.Authentication;
import com.wemall.foundation.domain.BuyerCreditLimit;
import com.wemall.foundation.domain.OrderForm;
import com.wemall.foundation.domain.RecordCreditLog;
import com.wemall.foundation.domain.Store;
import com.wemall.foundation.domain.query.CouponInfoQueryObject;
import com.wemall.foundation.domain.query.PredepositLogQueryObject;
import com.wemall.foundation.service.BuyerCreditLimitService;
import com.wemall.foundation.service.ICouponInfoService;
import com.wemall.foundation.service.IOrderFormService;
import com.wemall.foundation.service.IPredepositLogService;
import com.wemall.foundation.service.ISysConfigService;
import com.wemall.foundation.service.IUserConfigService;
import com.wemall.foundation.service.IUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 买家赊销记录控制器
 */
@Controller
public class CreditBuyerAction {
    @Autowired
    private ISysConfigService configService;

    @Autowired
    private IUserConfigService userConfigService;

    @Autowired
    private ICouponInfoService couponInfoService;
    
    @Autowired
    private IPredepositLogService predepositLogService;

    @Autowired
    private IUserService userService;
    
    @Autowired
	private BuyerCreditLimitService buyerCreditLimitService;
    
    @Autowired
 	 private IOrderFormService orderFormService;

    @SecurityMapping(display = false, rsequence = 0, title = "买家赊销记录列表", value = "/buyer/credit.htm*", rtype = "buyer", rname = "用户中心", rcode = "wemall_recordcreditlog", rgroup = "用户中心")
    @RequestMapping({"/buyer/credit.htm"})
    public ModelAndView coupon(HttpServletRequest request, HttpServletResponse response, String reply, String currentPage){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/buyer_credit.html", this.configService
            .getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        Long userId= SecurityUserHolder.getCurrentUser().getId();
        try{
        	Map params = new HashMap();
//            params.put("buyerId", buyerId.intValue());
//            List buyerCreditLimitList=this.buyerCreditLimitService.query(
//            		"select obj from BuyerCreditLimit obj where obj.storeId=:storeId and obj.buyerId=:buyerId", params, -1, -1);
//            
//            BuyerCreditLimit buyerCreditLimit=(BuyerCreditLimit) buyerCreditLimitList.get(0);
//
//            mv.addObject("buyerCreditLimit", buyerCreditLimit);
//            
//            params.clear();
            params.put("userId", Long.valueOf(userId));
            List orderFormList=this.orderFormService.query(
            		"select obj from OrderForm obj where obj.user.id=:userId and obj.order_status=20", params, -1, -1);
            mv.addObject("orderFormList", orderFormList);
        }catch(Exception e){
        	mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
        	mv.addObject("op_title", "买家赊销历史记录数据获取错误，请联系统管理员！");
        	mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
        }
        
        return mv;
    }
}




