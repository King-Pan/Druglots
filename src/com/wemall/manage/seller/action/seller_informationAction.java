package com.wemall.manage.seller.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wemall.core.annotation.SecurityMapping;
import com.wemall.core.domain.virtual.SysMap;
import com.wemall.core.mv.JModelAndView;
import com.wemall.core.query.PageList;
import com.wemall.core.query.support.IPageList;
import com.wemall.core.security.support.SecurityUserHolder;
import com.wemall.core.tools.CommUtil;
import com.wemall.core.tools.HttpClass;
import com.wemall.foundation.domain.Authentication;
import com.wemall.foundation.domain.InvoiceRecord;
import com.wemall.foundation.domain.query.RefundLogQueryObject;
import com.wemall.foundation.service.AuthenticationService;
import com.wemall.foundation.service.IRefundLogService;
import com.wemall.foundation.service.ISysConfigService;
import com.wemall.foundation.service.IUserConfigService;
/**
 * 卖家订单发票记录查询
 * @author Administrator
 *
 */
@Controller
public class seller_informationAction {
	 @Autowired
	    private ISysConfigService configService;

	    @Autowired
	    private IUserConfigService userConfigService;

	    @Autowired
	    private IRefundLogService refundLogService;
	    
	    @Autowired
	    
	    private AuthenticationService authenticationService;
	    
	    
	    @SecurityMapping(display = false, rsequence = 0, title = "卖家订单发票列表", value = "/seller/seller_information.htm*", rtype = "seller", rname = "卖家订单发票列表", rcode = "refund_seller", rgroup = "客户服务")
	    @RequestMapping({"/seller/seller_information.htm"})
	    public ModelAndView refund(HttpServletRequest request, HttpServletResponse response, String currentPage){
	        ModelAndView mv = new JModelAndView(
	            "user/default/usercenter/seller_in.html", this.configService
	            .getSysConfig(),
	            this.userConfigService.getUserConfig(), 0, request, response);
	        HttpClass hc = new HttpClass();
	        if (currentPage==null || currentPage=="") {
	        	currentPage="1";
			}
	        
			try {
		    String username = SecurityUserHolder.getCurrentUser().getStore().getStore_ower();
			String Load = hc.load("http://127.0.0.1:8081/ssm_project/selefenyemai", "currentPage="+currentPage+"&"+"username="+username);
		        PageList pList= JSON.parseObject(Load, PageList.class);
		        //从封装的分页获取Result的json数组
		        List<JSONObject> jsons=pList.getResult();
		        List<InvoiceRecord> list=  new ArrayList<>();
           	    Map map = new HashMap();
           	    //得到查询结果遍历得到买家名称，去首营认证表去获取企业名称
           	    for (JSONObject json : jsons) {
           	    	InvoiceRecord invoiceRecord1=JSON.parseObject(JSONObject.toJSONString(json),InvoiceRecord.class);
           		    map.clear();
					map.put("userName", invoiceRecord1.getBuyname());
					List<Authentication> auths=this.authenticationService.query("select obj from Authentication obj where obj.userName=:userName", map, -1, -1);
					invoiceRecord1.setAuth(auths.get(0).getEnterpriseName());
					list.add(invoiceRecord1);
           	       
           	    }
                pList.setResult(list);
		        CommUtil.saveIPageList2ModelAndView("seller_information.htm", "", "", pList, mv);  
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		

	        return mv;
	    }
	    }


