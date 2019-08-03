package com.wemall.manage.buyer.action;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.http.HttpResponse;
import com.wemall.core.annotation.SecurityMapping;
import com.wemall.core.mv.JModelAndView;
import com.wemall.core.query.PageList;
import com.wemall.core.security.support.SecurityUserHolder;
import com.wemall.core.tools.CommUtil;
import com.wemall.core.tools.HttpClass;
import com.wemall.foundation.domain.Authentication;
import com.wemall.foundation.domain.Invoice;
import com.wemall.foundation.service.ICouponInfoService;
import com.wemall.foundation.service.ISysConfigService;
import com.wemall.foundation.service.IUserConfigService;

/**
 * 买家发票管理控制器
 */
@Controller
public class InvoiceBuyerAction {
	 @Autowired
	    private ISysConfigService configService;

	    @Autowired
	    private IUserConfigService userConfigService;

	    @Autowired
	    private ICouponInfoService couponInfoService;

	    /**
	     * 发票首页管理
	     * @param request
	     * @param response
	     * @param reply
	     * @param currentPage
	     * @return
	     */
	    @SecurityMapping(display = false, rsequence = 0, title = "买家发票index展示", value = "/buyer/invoice.htm*", rtype = "buyer", rname = "发票管理", rcode = "wemall_invoice", rgroup = "发票管理")
	    @RequestMapping({"/buyer/invoice.htm"})
	    public ModelAndView coupon(HttpServletRequest request, HttpServletResponse response, String reply, String currentPage){
	        ModelAndView mv = new JModelAndView(
	            "user/default/usercenter/invoice_index.html", this.configService
	            .getSysConfig(),
	            this.userConfigService.getUserConfig(), 0, request, response);
	        HttpClass hc = new HttpClass();
	        if (currentPage==null || currentPage=="") {
	        	currentPage="1";
			}
	        String username = SecurityUserHolder.getCurrentUser().getUserName();
			try {
			String Load = hc.load("http://127.0.0.1:8081/ssm_project/selectpage", "currentPage="+currentPage+"&"+"username="+username);
		        PageList pList= JSON.parseObject(Load, PageList .class);
		        if (pList.getResult().size()<=0) {
		        	 mv.addObject("pd","w");
				}
		        CommUtil.saveIPageList2ModelAndView("invoice.htm", "", "", pList, mv);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		

	        return mv;
	    }
	    
	    /**
	     * 发票删除
	     * @param request
	     * @param response
	     * @param reply
	     * @param currentPage
	     * @return
	     */
	    @SecurityMapping(display = false, rsequence = 0, title = "发票删除", value = "/buyer/invoice_del.htm*", rtype = "buyer", rname = "发票管理", rcode = "wemall_invoice", rgroup = "发票管理")
	    @RequestMapping({"/buyer/invoice_del.htm"})
	    public ModelAndView del(HttpServletRequest request, HttpServletResponse response, String reply, String currentPage,String id){
	        ModelAndView mv = new JModelAndView(
	            "user/default/usercenter/invoice_index.html", this.configService
	            .getSysConfig(),
	            this.userConfigService.getUserConfig(), 0, request, response);
	        HttpClass hc = new HttpClass();
	        if (currentPage==null || currentPage=="") {
	        	currentPage="1";
			}
	        String username = SecurityUserHolder.getCurrentUser().getUserName();
			try {
			hc.load("http://127.0.0.1:8081/ssm_project/delinvoice", "id="+id);
			String Load = hc.load("http://127.0.0.1:8081/ssm_project/selectpage", "currentPage="+currentPage+"&"+"username="+username);
		        PageList pList= JSON.parseObject(Load, PageList .class);
		        if (pList.getResult().size()<=0) {
		        	 mv.addObject("pd","w");
				}
		        CommUtil.saveIPageList2ModelAndView("invoice.htm", "", "", pList, mv);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		

	        return mv;
	    }
	    
	    /**
	     * 发票回显
	     * @param request
	     * @param response
	     * @param reply
	     * @param currentPage
	     * @return
	     */
	    @SecurityMapping(display = false, rsequence = 0, title = "发票修改回显", value = "/buyer/invoice_display.htm*", rtype = "buyer", rname = "发票管理", rcode = "wemall_invoice", rgroup = "发票管理")
	    @RequestMapping({"/buyer/invoice_display.htm"})
	    public ModelAndView display(HttpServletRequest request, HttpServletResponse response,String id){
	    	ModelAndView mv = null;
	    	   HttpClass hc = new HttpClass();
	    	try {
				String Load = hc.load("http://127.0.0.1:8081/ssm_project/huiinvoice", "id="+id);
				if (Load!=null && Load!="") {
					Invoice auth = JSON.parseObject(Load, Invoice.class);
					if (auth.getMark()==1) {
						mv =new JModelAndView(
					            "user/default/usercenter/invoice_update.html", this.configService
					            .getSysConfig(),
					            this.userConfigService.getUserConfig(), 0, request, response);
					}else {
						mv =new JModelAndView(
					            "user/default/usercenter/invoice_update2.html", this.configService
					            .getSysConfig(),
					            this.userConfigService.getUserConfig(), 0, request, response);
					}
					mv.addObject("auth", auth);
					return mv;
					
				}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			return mv;
	    	
	    }
	    
	    /**
	     * 发票修改
	     * @param request
	     * @param response
	     * @param reply
	     * @param currentPage
	     * @return
	     */
	    @SecurityMapping(display = false, rsequence = 0, title = "修改", value = "/buyer/invoice_update.htm*", rtype = "buyer", rname = "发票管理", rcode = "wemall_invoice", rgroup = "发票管理")
	    @RequestMapping({"/buyer/invoice_update.htm"})
	    public ModelAndView update(HttpServletRequest request, HttpServletResponse response, String reply, String currentPage,Invoice invoice){
	        ModelAndView mv = new JModelAndView(
	            "user/default/usercenter/invoice_index.html", this.configService
	            .getSysConfig(),
	            
	            this.userConfigService.getUserConfig(), 0, request, response);
	        HttpClass hc = new HttpClass();
	        if (currentPage==null || currentPage=="") {
	        	currentPage="1";
			} 
			try {
				 hc.load("http://127.0.0.1:8081/ssm_project/Upinvoice", "duixiang="+JSON.toJSONString(invoice));
			String Load = hc.load("http://127.0.0.1:8081/ssm_project/selectpage", "currentPage="+currentPage+"&"+"username="+invoice.getName());
		        PageList pList= JSON.parseObject(Load, PageList .class);
		        if (pList.getResult().size()<=0) {
		        	 mv.addObject("pd","w");
				}
		        CommUtil.saveIPageList2ModelAndView("invoice.htm", "", "", pList, mv);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		

	        return mv;
	    }
	    
	    
	    
	    
	    /**
	     * 跳转发票新增专票
	     * @param request
	     * @param response
	     * @param reply
	     * @param currentPage
	     * @return
	     */
	    @SecurityMapping(display = false, rsequence = 0, title = "修改", value = "/buyer/invoice_insert.htm*", rtype = "buyer", rname = "发票管理", rcode = "wemall_invoice", rgroup = "发票管理")
	    @RequestMapping({"/buyer/invoice_inserttiao.htm"})
	    public ModelAndView tiaoinsert(HttpServletRequest request, HttpServletResponse response, String reply, String currentPage){
	    	ModelAndView mv = new JModelAndView(
		            "user/default/usercenter/invoice_add.html", this.configService
		            .getSysConfig(),
		            this.userConfigService.getUserConfig(), 0, request, response);
		        return mv;
	    }
	    
	    
	    /**
	     * 跳转发票新增普票
	     * @param request
	     * @param response
	     * @param reply
	     * @param currentPage
	     * @return
	     */
	    @SecurityMapping(display = false, rsequence = 0, title = "修改", value = "/buyer/invoice_insert.htm*", rtype = "buyer", rname = "发票管理", rcode = "wemall_invoice", rgroup = "发票管理")
	    @RequestMapping({"/buyer/invoice_inserttiao2.htm"})
	    public ModelAndView tiaoinsert2(HttpServletRequest request, HttpServletResponse response, String reply, String currentPage){
	    	ModelAndView mv = new JModelAndView(
		            "user/default/usercenter/invoice_add2.html", this.configService
		            .getSysConfig(),
		            this.userConfigService.getUserConfig(), 0, request, response);
		        return mv;
	    }
	    
	    /***
	     * 判断税号是否重复
	     * 
	     */
	    @RequestMapping("/buyer/invoice_insert_checknum.htm")
	    public void checknum(HttpServletRequest request,HttpServletResponse response,String invoice){
              HttpClass hc=new HttpClass();
		     //判断税号是否重复
		try {
			String  fan = hc.load("http://127.0.0.1:8081/ssm_project/chongfu", "shuihao="+invoice);
			if (fan.equals("sb")) {
				response.setContentType("text/plain");
		        response.setHeader("Cache-Control", "no-cache");
		        response.setCharacterEncoding("UTF-8");
		        PrintWriter writer = response.getWriter();
		        int result=1;
	            writer.print(result);
			   }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    }
	    
	    /**
	     * 专票新增
	     * @param request
	     * @param response
	     * @param reply
	     * @param currentPage
	     * @return
	     */
	    @SecurityMapping(display = false, rsequence = 0, title = "修改", value = "/buyer/invoice_insert.htm*", rtype = "buyer", rname = "发票管理", rcode = "wemall_invoice", rgroup = "发票管理")
	    @RequestMapping({"/buyer/invoice_insert.htm"})
	    public ModelAndView insert(HttpServletRequest request, HttpServletResponse response, String reply, String currentPage,Invoice invoice){
	        ModelAndView mv = new JModelAndView(
	            "user/default/usercenter/invoice_index.html", this.configService
	            .getSysConfig(),
	            this.userConfigService.getUserConfig(), 0, request, response);
	        HttpClass hc = new HttpClass();
	        if (currentPage==null || currentPage=="") {
	        	currentPage="1";
			}
			try {
				 String Load = hc.load("http://127.0.0.1:8081/ssm_project/selectpage", "currentPage="+currentPage+"&"+"username="+invoice.getName());
			        PageList pList= JSON.parseObject(Load, PageList .class);
				if (pList.getResult().size()<=0) {
					hc.load("http://127.0.0.1:8081/ssm_project/insinvoice", "duixiang="+JSON.toJSONString(invoice));
				}
				String Lad = hc.load("http://127.0.0.1:8081/ssm_project/selectpage", "currentPage="+currentPage+"&"+"username="+invoice.getName());
		        PageList List= JSON.parseObject(Lad, PageList .class);
		        CommUtil.saveIPageList2ModelAndView("invoice.htm", "", "", List, mv);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return mv;
	    }
	    
	    
	    /**
	     * 专票新增2
	     * @param request
	     * @param response
	     * @param reply
	     * @param currentPage
	     * @return
	     */
	    @SecurityMapping(display = false, rsequence = 0, title = "修改", value = "/buyer/invoice_insert.htm*", rtype = "buyer", rname = "发票管理", rcode = "wemall_invoice", rgroup = "发票管理")
	    @RequestMapping({"/buyer/invoice_insertt.htm"})
	    public void insertt(HttpServletRequest request, HttpServletResponse response, String reply, String currentPage,Invoice invoice){
	        HttpClass hc = new HttpClass();
	        if (currentPage==null || currentPage=="") {
	        	currentPage="1";
			}
			try {
				 String Load = hc.load("http://127.0.0.1:8081/ssm_project/selectpage", "currentPage="+currentPage+"&"+"username="+invoice.getName());
			        PageList pList= JSON.parseObject(Load, PageList .class);
				if (pList.getResult().size()<=0) {
					
					     //判断税号是否重复
					String fan=  hc.load("http://127.0.0.1:8081/ssm_project/chongfu", "shuihao="+invoice.getTaxnumber());
					
					if (!fan.equals("sb")) {
						hc.load("http://127.0.0.1:8081/ssm_project/insinvoice", "duixiang="+JSON.toJSONString(invoice));
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    
	    /**
	     * 普票新增
	     * @param request
	     * @param response
	     * @param reply
	     * @param currentPage
	     * @return
	     */
	    @SecurityMapping(display = false, rsequence = 0, title = "修改", value = "/buyer/invoice_insert.htm*", rtype = "buyer", rname = "发票管理", rcode = "wemall_invoice", rgroup = "发票管理")
	    @RequestMapping({"/buyer/invoice_insert2.htm"})
	    public ModelAndView insert2(HttpServletRequest request, HttpServletResponse response, String reply, String currentPage,Invoice invoice){
	        ModelAndView mv = new JModelAndView(
	            "user/default/usercenter/invoice_index.html", this.configService
	            .getSysConfig(),
	            this.userConfigService.getUserConfig(), 0, request, response);
	        HttpClass hc = new HttpClass();
	        if (currentPage==null || currentPage=="") {
	        	currentPage="1";
			}
	        
			try {
				 hc.load("http://127.0.0.1:8081/ssm_project/insinvoice2", "duixiang="+JSON.toJSONString(invoice));
			String Load = hc.load("http://127.0.0.1:8081/ssm_project/selectpage", "currentPage="+currentPage+"&"+"username="+invoice.getName());
		        PageList pList= JSON.parseObject(Load, PageList .class);
		        CommUtil.saveIPageList2ModelAndView("invoice.htm", "", "", pList, mv);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		

	        return mv;
	    }
	    
	    
	    /**
	     * 买家发票记录查询
	     * @param request
	     * @param response
	     * @param reply
	     * @param currentPage
	     * @return
	     */
	    @SecurityMapping(display = false, rsequence = 0, title = "发票信息记录", value = "/buyer/information.htm*", rtype = "buyer", rname = "发票信息管理", rcode = "wemall_invoice_record", rgroup = "发票信息管理")
	    @RequestMapping({"/buyer/information.htm"})
	    public ModelAndView sele(HttpServletRequest request, HttpServletResponse response,String currentPage){
	        ModelAndView mv = new JModelAndView(
	            "user/default/usercenter/informationbuyer.html", this.configService
	            .getSysConfig(),
	            this.userConfigService.getUserConfig(), 0, request, response);
	        HttpClass hc = new HttpClass();
	        if (currentPage==null || currentPage=="") {
	        	currentPage="1";
			}
	        
			try {
		  String username = SecurityUserHolder.getCurrentUser().getUserName();
			String Load = hc.load("http://127.0.0.1:8081/ssm_project/selefenye", "currentPage="+currentPage+"&"+"username="+username);
		        PageList pList= JSON.parseObject(Load, PageList .class);
		        if (pList.getResult().size()<=0) {
		        	 mv.addObject("pd","w");
				}
		        CommUtil.saveIPageList2ModelAndView("information.htm", "", "", pList, mv);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		

	        return mv;
	    }
	    
	    /**
	     * 买家发票详情查询
	     * @param request
	     * @param response
	     * @param reply
	     * @param currentPage
	     * @return
	     */
	    @SecurityMapping(display = false, rsequence = 0, title = "发票信息记录", value = "/buyer/information.htm*", rtype = "buyer", rname = "发票信息管理", rcode = "wemall_invoice_record", rgroup = "发票信息管理")
	    @RequestMapping({"/buyer/informationxq.htm"})
	    public ModelAndView xiangqin(HttpServletRequest request, HttpServletResponse response,String id){
	        ModelAndView mv = new JModelAndView(
	            "user/default/usercenter/invoice_info.html", this.configService
	            .getSysConfig(),
	            this.userConfigService.getUserConfig(), 0, request, response);
	        HttpClass hc = new HttpClass();
	        
	        
			try {
			String Load = hc.load("http://127.0.0.1:8081/ssm_project/huiinvoice", "id="+id);
		     Invoice auth =  JSON.parseObject(Load, Invoice.class);
		     mv.addObject("auth",auth);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return mv;
	    }
	    
	    
	}