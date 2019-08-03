package com.wemall.manage.seller.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.wemall.core.annotation.SecurityMapping;
import com.wemall.core.mv.JModelAndView;
import com.wemall.core.query.PageList;
import com.wemall.core.security.support.SecurityUserHolder;
import com.wemall.core.tools.CommUtil;
import com.wemall.core.tools.HttpClass;
import com.wemall.foundation.domain.Authentication;
import com.wemall.foundation.domain.InvitationCode;
import com.wemall.foundation.domain.UserFuzhi;
import com.wemall.foundation.service.IRefundLogService;
import com.wemall.foundation.service.ISysConfigService;
import com.wemall.foundation.service.IUserConfigService;

/**
 * 卖家业务员邀请码设置
 */
@Controller
public class InformationSellerAction {
	    @Autowired
	    private ISysConfigService configService;

	    @Autowired
	    private IUserConfigService userConfigService;

	    @Autowired
	    private IRefundLogService refundLogService;
	    
	    
		/***
	     * 页面跳转，私有调用
	     * @param file
	     * @return
	     */
		private ModelAndView Jump(String lu,String msg,HttpServletRequest request, HttpServletResponse response) {
			 ModelAndView mv = new JModelAndView(lu, this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
			 mv.addObject("current_url", lu);
	         mv.addObject("op_title", msg);
	         mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
	         return mv;
		}
		
	    
	    @SecurityMapping(display = false, rsequence = 0, title = "卖家业务员激活码管理", value = "/seller/seller_InvitationCode.html*", rtype = "seller", rname = "卖家订单发票列表", rcode = "refund_seller", rgroup = "客户服务")
	    @RequestMapping({"/seller/seller_invitationCode.htm"})
	    public ModelAndView cha(HttpServletRequest request, HttpServletResponse response, String salesman ,String cumulative,String phone,String kssj,String jssj,String currentPage){
	        ModelAndView mv = new JModelAndView(
	            "user/default/usercenter/seller_InvitationCode.html", this.configService
 	            .getSysConfig(),
	            this.userConfigService.getUserConfig(), 0, request, response);
	        HttpClass hc = new HttpClass();
	        //判断非空，如果空就把他变为空字符串
	        if (currentPage==null || currentPage=="") {
	        	currentPage="1";
			}
			if(salesman==null || salesman==""){
				salesman="";
			}
			if(cumulative==null || cumulative==""){
				cumulative="-1";
			}
			if(phone==null || phone==""){
				phone="";
			}
			if(kssj==null || kssj==""){
				kssj="";
			}
			if(jssj==null || jssj==""){
				jssj="";
			}
		try {
		    String username = SecurityUserHolder.getCurrentUser().getUserName();
			String Load = hc.load("http://127.0.0.1:8081/ssm_project/invitationfenyecha", "currentPage="+currentPage+"&"+"sellername="+username+"&"+"salesman="+salesman+"&"+"cumulative="+cumulative+"&"+"phone="+phone+"&"+"kssj="+kssj+"&"+"jssj="+jssj);
		        PageList pList= JSON.parseObject(Load, PageList .class);
		        CommUtil.saveIPageList2ModelAndView("seller_invitationCode.htm", "", "", pList, mv);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		

	        return mv;
	    }
	    
	    /*新增跳转页面*/
	    @SecurityMapping(display = false, rsequence = 0, title = "业务员新增跳转页面", value = "/seller/seller_InvitationCodezeng.html*", rtype = "seller", rname = "卖家订单发票列表", rcode = "refund_seller", rgroup = "客户服务")
	    @RequestMapping({"/seller/seller_InvitationCodezeng.htm"})
	    public ModelAndView zeng(HttpServletRequest request, HttpServletResponse response){
	        ModelAndView mv = new JModelAndView(
	            "user/default/usercenter/seller_InvitationCodezeng.html", this.configService
	            .getSysConfig(),
	            this.userConfigService.getUserConfig(), 0, request, response);
	             return mv;
	    }
	    
	    /*新增*/
	    @SecurityMapping(display = false, rsequence = 0, title = "业务员新增", value = "/seller/seller_invitationCodezeng2.htm*", rtype = "seller", rname = "卖家订单发票列表", rcode = "refund_seller", rgroup = "客户服务")
	    @RequestMapping({"/seller/seller_invitationCodezeng2.htm"})
	    public ModelAndView zeng2(HttpServletRequest request, HttpServletResponse response, InvitationCode invitationCode,String currentPage,String username){
	      System.out.println(invitationCode.getSalesman());
	    	ModelAndView mv = new JModelAndView(
	            "user/default/usercenter/seller_InvitationCode.html", this.configService
	            .getSysConfig(),
	            this.userConfigService.getUserConfig(), 0, request, response);
	        HttpClass hc = new HttpClass();
	        //判断非空，如果空就把他变为空字符串
	        if (currentPage==null || currentPage=="") {
	        	currentPage="1";
			}
	        invitationCode.setAddtime(new Date());
	        invitationCode.setSellername(username);
	        
		try {
		String fanhui= hc.load("http://127.0.0.1:8081/ssm_project/invitationchaphone", "phone="+invitationCode.getPhone());
			 if (fanhui.equals("sb")) {
				 return Jump("error.html", "该号码已被别人占用", request, response);
			 }
			 
		    hc.load("http://127.0.0.1:8081/ssm_project/invitationcun", "duixiang="+JSON.toJSONString(invitationCode));
			String Load = hc.load("http://127.0.0.1:8081/ssm_project/invitationfenyecha", "currentPage="+currentPage+"&"+"sellername="+username);
		        PageList pList= JSON.parseObject(Load, PageList .class);
		        CommUtil.saveIPageList2ModelAndView("seller_invitationCode.htm", "", "", pList, mv);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return mv;
	    }
	    
	    
	    
	    /*删除*/
	    @SecurityMapping(display = false, rsequence = 0, title = "业务员删除", value = "/seller/seller_invitationCodeshan.html*", rtype = "seller", rname = "卖家订单发票列表", rcode = "refund_seller", rgroup = "客户服务")
	    @RequestMapping({"/seller/seller_invitationCodeshan.htm"})
	    public ModelAndView shan(HttpServletRequest request, HttpServletResponse response, String id,String currentPage){
	        ModelAndView mv = new JModelAndView(
	            "user/default/usercenter/seller_InvitationCode.html", this.configService
	            .getSysConfig(),
	            this.userConfigService.getUserConfig(), 0, request, response);
	        HttpClass hc = new HttpClass();
	        //判断非空，如果空就把他变为空字符串
	        if (currentPage==null || currentPage=="") {
	        	currentPage="1";
			}
	        String username = SecurityUserHolder.getCurrentUser().getUserName();
		try {
		    hc.load("http://127.0.0.1:8081/ssm_project/invitationshan", "id="+id);
			String Load = hc.load("http://127.0.0.1:8081/ssm_project/invitationfenyecha", "currentPage="+currentPage+"&"+"sellername="+username);
		        PageList pList= JSON.parseObject(Load, PageList .class);
		        CommUtil.saveIPageList2ModelAndView("seller_invitationCode.htm", "", "", pList, mv);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		

	        return mv;
	    }
	    
	    
	    /*修改回显*/
	    @SecurityMapping(display = false, rsequence = 0, title = "业务员回显", value = "/seller/seller_invitationCodehuixian.html*", rtype = "seller", rname = "卖家订单发票列表", rcode = "refund_seller", rgroup = "客户服务")
	    @RequestMapping({"/seller/seller_invitationCodehuixian.htm"})
	    public ModelAndView hui(HttpServletRequest request, HttpServletResponse response, String id){
	        ModelAndView mv = new JModelAndView(
	            "user/default/usercenter/seller_InvitationCodehuixian.html", this.configService
	            .getSysConfig(),
	            this.userConfigService.getUserConfig(), 0, request, response);
	        HttpClass hc = new HttpClass();
		try {
		  String load =  hc.load("http://127.0.0.1:8081/ssm_project/invitationhuixian", "id="+id);
		  InvitationCode invitationCode = JSON.parseObject(load, InvitationCode.class);
		  mv.addObject("auth",invitationCode);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		

	        return mv;
	    }
	    
	    
	    
	    /*修改*/
	    @SecurityMapping(display = false, rsequence = 0, title = "业务员修改", value = "/seller/seller_invitationCodeshan.html*", rtype = "seller", rname = "卖家订单发票列表", rcode = "refund_seller", rgroup = "客户服务")
	    @RequestMapping({"/seller/seller_invitationCodexiugai.htm"})
	    public ModelAndView xiugai(HttpServletRequest request, HttpServletResponse response,InvitationCode invitationCode ,String currentPage){
	        ModelAndView mv = new JModelAndView(
	            "user/default/usercenter/seller_InvitationCode.html", this.configService
	            .getSysConfig(),
	            this.userConfigService.getUserConfig(), 0, request, response);
	        HttpClass hc = new HttpClass();
	        //判断非空，如果空就把他变为空字符串
	        if (currentPage==null || currentPage=="") {
	        	currentPage="1";
			}
	        String username = SecurityUserHolder.getCurrentUser().getUserName();
	        invitationCode.setAddtime(new Date());
	        invitationCode.setSellername(username);
		try {
		    hc.load("http://127.0.0.1:8081/ssm_project/invitationxiugai",  "duixiang="+JSON.toJSONString(invitationCode));
			String Load = hc.load("http://127.0.0.1:8081/ssm_project/invitationfenyecha", "currentPage="+currentPage+"&"+"sellername="+username);
		        PageList pList= JSON.parseObject(Load, PageList .class);
		        CommUtil.saveIPageList2ModelAndView("seller_invitationCode.htm", "", "", pList, mv);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		

	        return mv;
	    }
	    
	    
	    /*查看用户*/
	    @SecurityMapping(display = false, rsequence = 0, title = "业务员回显", value = "/seller/seller_invitationCodehuixian.html*", rtype = "seller", rname = "卖家订单发票列表", rcode = "refund_seller", rgroup = "客户服务")
	    @RequestMapping({"/seller/seller_InvitationCodechayonhu.htm"})
	    public ModelAndView chayonhu(HttpServletRequest request, HttpServletResponse response, String phone,String currentPage){
	        ModelAndView mv = new JModelAndView(
	            "user/default/usercenter/seller_InvitationCodechayonhu.html", this.configService
	            .getSysConfig(),
	            this.userConfigService.getUserConfig(), 0, request, response);
	        
	        //判断非空，如果空就把他变为空字符串
	        if (currentPage==null || currentPage=="") {
	        	currentPage="1";
			}
	        HttpClass hc = new HttpClass();
	        
	        
	        try {
				String Load = hc.load("http://127.0.0.1:8081/ssm_project/invitationchatonhu", "phone="+phone+"&"+"currentPage="+currentPage);
			        PageList pList= JSON.parseObject(Load, PageList .class);
			        CommUtil.saveIPageList2ModelAndView("seller_InvitationCodechayonhu.htm", "", "", pList, mv);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        return mv;
	    }
	    
	    
	    
	    


}
