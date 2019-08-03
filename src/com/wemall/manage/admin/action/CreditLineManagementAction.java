package com.wemall.manage.admin.action;

import com.wemall.core.annotation.SecurityMapping;
import com.wemall.core.domain.virtual.SysMap;
import com.wemall.core.mv.JModelAndView;
import com.wemall.core.query.support.IPageList;
import com.wemall.core.tools.CommUtil;
import com.wemall.core.tools.WebForm;
import com.wemall.foundation.domain.CreditLlineManagement;
import com.wemall.foundation.domain.Store;
import com.wemall.foundation.domain.User;
import com.wemall.foundation.domain.query.CreditLlineManagementQueryObject;
import com.wemall.foundation.domain.query.StoreQueryObject;
import com.wemall.foundation.service.CreditLlineManagementService;
import com.wemall.foundation.service.IStoreService;
import com.wemall.foundation.service.ISysConfigService;
import com.wemall.foundation.service.IUserConfigService;
import com.wemall.foundation.service.IUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商城后台   赊销额度管理
 */
@Controller
public class CreditLineManagementAction {
    @Autowired
    private ISysConfigService configService;

    @Autowired
    private IUserConfigService userConfigService;

    @Autowired
    private IStoreService storeService;
    
    @Autowired
    private IUserService userService;

    @Autowired
    private CreditLlineManagementService creditLlineManagementService;

    @SecurityMapping(display = false, rsequence = 0, title = "赊销设置", value = "/admin/queryCreditLimit.htm*", rtype = "admin", rname = "赊销设置", rcode = "admin_queryCreditLimit", rgroup = "设置")
    @RequestMapping({"/admin/queryCreditLimit.htm"})
    public ModelAndView queryCreditLimit(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("admin/blue/queryCreditLimit.html",
                                            this.configService.getSysConfig(), this.userConfigService
                                            .getUserConfig(), 0, request, response);
        try{
        	String url = this.configService.getSysConfig().getAddress();
            
        	CreditLlineManagementQueryObject qo = new CreditLlineManagementQueryObject("1", mv,"addTime", "desc");
            qo.setOrderBy("addTime");
            qo.setOrderType("desc");
            IPageList pList = this.creditLlineManagementService.list(qo);
            CommUtil.saveIPageList2ModelAndView(url +"/admin/blue/queryCreditLimit.html","", "", pList, mv);
        }catch (Exception  e){
        	e.printStackTrace();
        }
        return mv;
    }
    
    @SecurityMapping(display = false, rsequence = 0, title = "店铺额度设置编辑添加页面", value = "/admin/addUpdateCreditLimit.htm*", rtype = "admin", rname = "快递公司", rcode = "admin_express_company", rgroup = "设置")
    @RequestMapping({"/admin/addUpdateCreditLimit.htm"})
    public ModelAndView addUpdateCreditLimit(HttpServletRequest request, HttpServletResponse response, String id){
        ModelAndView mv = new JModelAndView("admin/blue/addCreditLimit.html",
        		this.configService.getSysConfig(),this.userConfigService.getUserConfig(), 0, request, response);

    	String url = this.configService.getSysConfig().getAddress();
        try{
        	if ((id != "id") && (!id.equals("id"))){//修改页面
            	CreditLlineManagement creditLlineManagement = this.creditLlineManagementService.getObjById(Long.valueOf(Long.parseLong(id)));
                mv.addObject("creditLlineManagement", creditLlineManagement);
                mv.addObject("edit", Boolean.valueOf(true));
            }else {//添加页面
            	StoreQueryObject qo = new StoreQueryObject("1", mv,"addTime", "desc");
                qo.addQuery("obj.store_status",new SysMap("store_status",2), "=");
                qo.setOrderBy("addTime");
                qo.setOrderType("desc");
                IPageList pList = this.storeService.list(qo);
                CommUtil.saveIPageList2ModelAndView(url +"/admin/blue/addCreditLimit.html","", "", pList, mv);
            }
        }catch (Exception e){
        	e.printStackTrace();
        }
        return mv;
    }
    
    @SecurityMapping(display = false, rsequence = 0, title = "店铺额度设置_保存", value = "/admin/CreditLimit_save.htm*", rtype = "admin", rname = "赊销设置", rcode = "admin_addCreditLimit_save", rgroup = "设置")
    @RequestMapping({"/admin/CreditLimit_save.htm"})
    public ModelAndView CreditLimit_save(HttpServletRequest request, HttpServletResponse response, String storeId,String storeCredits,String type){
    	ModelAndView mv = new JModelAndView("admin/blue/success.html",
                this.configService.getSysConfig(), this.userConfigService
                .getUserConfig(), 0, request, response);
    	
        try{
        	 CreditLlineManagement creditLlineManagement=new CreditLlineManagement();
        	 if (type.equals("save")){//新添加
	        	//判断店铺是否已经添加过
//        		 storeId:为店铺id
	            Map params = new HashMap();
	            params.put("storeId", Integer.parseInt(storeId));
	            List creditLlineManagementList = this.creditLlineManagementService.query(
	                    "select storeId from CreditLlineManagement obj where obj.storeId=:storeId", params,
	                    -1, -1);
		        if(creditLlineManagementList.size()>0){
		        	mv = new JModelAndView("admin/blue/error.html", this.configService.getSysConfig(),
		                       this.userConfigService.getUserConfig(), 1, request, response);
		           	mv.addObject("op_title", "店铺已设置赊销额度！");
		           	mv.addObject("url", CommUtil.getURL(request) + "/admin/queryCreditLimit.htm");
		           	return mv;
		        }
		        Store store=this.storeService.getObjByProperty("id",Long.valueOf(storeId));
		        
		       
		        creditLlineManagement.setStoreId(Integer.parseInt(storeId));
		        creditLlineManagement.setStoreName(store.getStore_name());
		        
		        params.clear();
		        params.put("userName", store.getStore_ower());
		        List sellerIdList = this.userService.query(
		                "select id from User obj where obj.userName=:userName", params,-1, -1);
		        
//		        creditLlineManagement.setSellerId(Integer.parseInt(sellerIdList.get(0).toString()));
		        creditLlineManagement.setSellerName(store.getStore_ower());
		        creditLlineManagement.setMayWithdrawalAmount("0");
		        creditLlineManagement.setYetWithdrawalAmount("0");
		        
		        creditLlineManagement.setStoreCredits(storeCredits);
		        creditLlineManagement.setStoreSurplus(storeCredits);
		        creditLlineManagement.setAddTime(new Date());
	            
	            this.creditLlineManagementService.save(creditLlineManagement);
	        }else{//修改
//	        	storeId:为creditLlineManagement的自增ID，不是店铺id
	        	WebForm wf = new WebForm();
	        	CreditLlineManagement obj = this.creditLlineManagementService.getObjById(
	                                     Long.valueOf(Long.parseLong(storeId)));
	        	Long storeSurplus=(Long.valueOf(storeCredits)-Long.valueOf(obj.getStoreCredits()))+Long.valueOf(obj.getStoreSurplus());
	        	creditLlineManagement = (CreditLlineManagement)wf.toPo(request, obj);
	        	creditLlineManagement.setStoreSurplus(String.valueOf(storeSurplus));
	            this.creditLlineManagementService.update(creditLlineManagement);
	        }
        	mv.addObject("op_title", "店铺额度保存成功！");
         	mv.addObject("list_url", CommUtil.getURL(request) +"/admin/queryCreditLimit.htm");
        }catch (Exception e){
        	e.printStackTrace();
        	mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
        	mv.addObject("op_title", "店铺额度保存失败！");
        	mv.addObject("url", CommUtil.getURL(request) + "/admin/queryCreditLimit.htm");
        }
        return mv;
    }

    
   
}




