package com.wemall.view.web.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.wemall.core.tools.HttpClass;
import com.wemall.foundation.domain.Authentication;
import com.wemall.foundation.domain.User;
import com.wemall.foundation.service.AuthenticationService;
import com.wemall.foundation.service.IUserService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class TimerRunAction extends TimerTask{
	
	   @Autowired
	   private IUserService userService;

	   @Autowired
	   private AuthenticationService authenticationService;

	   private ServletContext context;
	   
	   private static boolean isRunning = false;
	   
	   
	   //本类
	   public TimerRunAction() {
	        super();
	    }

	    public TimerRunAction(ServletContext context) {
	        this.context = context;
	    }
	    int count = 1;
	        	public void run() { 
	        		 if (!isRunning) {
	        			 System.out.println("开始执行指定任务");
	            		HttpClass hc = new HttpClass();
	            	try {
	            		 SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				         Date m = new Date();
				         
				         LoginViewAction action=new LoginViewAction();
				         HttpServletRequest request = null;
						 HttpServletResponse response = null;
						 
	                    String Load = hc.load("http://127.0.0.1:8081/ssm_project/selectobject","id="+"");
	        	        JSONArray jsonArray = JSONArray.fromObject(Load);
		     		    for(int i = 0; i < jsonArray.size(); i++) {
		     		        //获取每一个JsonObject对象
		     		        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
		     		             Authentication authentication = (Authentication)JSONObject.toBean(jsonObject, Authentication.class);
		     		             String parseObject = hc.load("http://127.0.0.1:8081/ssm_project/selectUser","userId="+authentication.getUserId());
		     		             User user = JSON.parseObject(parseObject, User.class);
		     		            if (authentication.getNote() == 0) {
		     		            //买家
			                	if (user.getDifference() == 1) {
			    				int Businessdays = (int) ((df.parse(authentication.getEndBusinessDate()).getTime() - m.getTime()) / (1000*3600*24));
			    				int Drugdays = (int) ((df.parse(authentication.getEndDrugDate()).getTime() - m.getTime()) / (1000*3600*24));
			    				int GSPdays = (int) ((df.parse(authentication.getEndGSPDate()).getTime() - m.getTime()) / (1000*3600*24));
			    				int IDcarddays = (int) ((df.parse(authentication.getEndIDcardDate()).getTime() - m.getTime()) / (1000*3600*24));
			    				if( GSPdays<0 || IDcarddays<0 || Businessdays<0 || Drugdays<0){
		    						action.faxiaoxi(request, response,user.getMobile(),"SMS_160855437","","");
		    						String ue= hc.load("http://127.0.0.1:8081/ssm_project/UpdateNote", "note="+1+"&"+"userId="+authentication.getUserId());
		    					}else if( GSPdays<=30 || IDcarddays<=30 || Businessdays<=30 || Drugdays <= 30){
									action.faxiaoxi(request, response,user.getMobile(),"SMS_160860421","","");
									String ue= hc.load("http://127.0.0.1:8081/ssm_project/UpdateNote", "note="+1+"&"+"userId="+authentication.getUserId());
			    				}
			               }else {
			            	    //卖家
			            	    JSONObject jsStr = JSONObject.fromObject(parseObject);
		     		            String hehe = jsStr.getString("storeId");
		     		            String object = hc.load("http://127.0.0.1:8081/ssm_project/selectStore","storeId="+Long.valueOf(hehe));
		     		            JSONObject str = JSONObject.fromObject(object);
		     		            String storeName = str.getString("storeName");
			            	    int Businessdays = (int) ((df.parse(authentication.getEndBusinessDate()).getTime() - m.getTime()) / (1000*3600*24));
			    				int Drugdays = (int) ((df.parse(authentication.getEndDrugDate()).getTime() - m.getTime()) / (1000*3600*24));
			    				int GSPdays = (int) ((df.parse(authentication.getEndGSPDate()).getTime() - m.getTime()) / (1000*3600*24));
			    				int IDcarddays = (int) ((df.parse(authentication.getEndIDcardDate()).getTime() - m.getTime()) / (1000*3600*24));
			    				if( GSPdays<0 || IDcarddays<0 || Businessdays<0 || Drugdays<0){
			    					action.faxiaoxi(request, response,user.getMobile(),"SMS_160860459",storeName,"");
		    						String ue= hc.load("http://127.0.0.1:8081/ssm_project/UpdateNote", "note="+1+"&"+"userId="+authentication.getUserId());
		    					}else if( GSPdays<=30 || IDcarddays<=30 || Businessdays<=30 || Drugdays <= 30){
		    						action.faxiaoxi(request, response,user.getMobile(),"SMS_160855386",storeName,"");
									String ue= hc.load("http://127.0.0.1:8081/ssm_project/UpdateNote", "note="+1+"&"+"userId="+authentication.getUserId());
			    				}
			    			 }
			             }
		     		  }
	            }catch (Exception e) {
						// TODO: handle exception
					} 
	            	System.out.println("定时器运行了"+count+"次");
	                count++;
	                isRunning = false;
	                System.out.println("指定任务执行结束");
	            } else {
	            	System.out.println("上一次任务执行还未结束");
	            }
	}
}

	
