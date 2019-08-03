package com.wemall.manage.buyer.action;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wemall.core.annotation.SecurityMapping;
import com.wemall.core.domain.virtual.SysMap;
import com.wemall.core.mv.JModelAndView;
import com.wemall.core.query.support.IPageList;
import com.wemall.core.security.support.SecurityUserHolder;
import com.wemall.core.tools.CommUtil;
import com.wemall.core.tools.HttpClass;
import com.wemall.foundation.domain.Authentication;
import com.wemall.foundation.domain.Store;
import com.wemall.foundation.domain.TemiIousLog;
import com.wemall.foundation.domain.User;
import com.wemall.foundation.domain.query.AuthenticationQueryObject;
import com.wemall.foundation.domain.query.MessageQueryObject;
import com.wemall.foundation.domain.query.TemiIousLogQueryObject;
import com.wemall.foundation.service.AuthenticationService;
import com.wemall.foundation.service.IAreaService;
import com.wemall.foundation.service.IMessageService;
import com.wemall.foundation.service.IStoreClassService;
import com.wemall.foundation.service.IStoreGradeService;
import com.wemall.foundation.service.IStoreService;
import com.wemall.foundation.service.ISysConfigService;
import com.wemall.foundation.service.IUserConfigService;
import com.wemall.foundation.service.IUserService;
import com.wemall.foundation.service.TemiIousLogService;
import com.wemall.manage.admin.tools.StoreTools;
import com.wemall.view.web.tools.StoreViewTools;

/**
 * 特米白条
 * **/
@Controller
public class TheFirstCampAction {
	@Autowired
    private ISysConfigService configService;
	
	@Autowired
    private IUserConfigService userConfigService;
	
	@Autowired
    private IUserService userService;
	
	@Autowired
    private IAreaService areaService;
	
	@Autowired
	private IStoreClassService storeClassService;
	
	@Autowired
    private IStoreService storeService;
	
	@Autowired
    private StoreTools storeTools;
	 
	@Autowired
    private IMessageService messageService;
	
	@Autowired
    private AuthenticationService authenticationService;
	
	@Autowired
    private TemiIousLogService temiIousLogService;
	
	//白条认证
	@SecurityMapping(display = false, rsequence = 0, title = "白条认证", value = "/buyer/IousCertification.htm*", rtype = "buyer", rname = "首营认证", rcode = "user_center", rgroup = "首营认证")
    @RequestMapping({"/buyer/IousCertification.htm"})
    public ModelAndView IousCertification(HttpServletRequest request, HttpServletResponse response){
		ModelAndView mv= new JModelAndView(
	            "user/default/usercenter/IousCertification.html", this.configService
	            .getSysConfig(),
	            this.userConfigService.getUserConfig(), 0, request, response);
        Store store = this.storeService.getObjByProperty("user.id",
                      SecurityUserHolder.getCurrentUser().getId());
//        int store_status = store == null ? 0 : store.getStore_status();
        String userName=SecurityUserHolder.getCurrentUser().getUserName();
        String url = this.configService.getSysConfig().getAddress();
        
        try{
        	/*AuthenticationQueryObject qo=new AuthenticationQueryObject();
            qo.addQuery("obj.userName",new SysMap("userName",username), "=");
            IPageList pList = this.authenticationService.list(qo);
            if (pList.getRowCount()>0) {
            	CommUtil.saveIPageList2ModelAndView(url +"/IousCertification.html","", "", pList, mv);
            }*/
            Map map = new HashMap();
			map.put("userName", userName);
            List authenticationList=this.authenticationService.query(
            		"select obj from Authentication obj where obj.userName=:userName", map, -1, -1);
            mv.addObject("authenticationList", authenticationList);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date m = new Date();
            for (int i = 0; i < authenticationList.size(); i++) {
            	Authentication authentication=(Authentication) authenticationList.get(i);
				int Businessdays = (int) ((df.parse(authentication.getEndBusinessDate()).getTime() - m.getTime()) / (1000*3600*24));
				if( Businessdays<=30 ){//营业执照有效期比较
					if( Businessdays<0 ){//营业执照有效期比较    已过期
						mv.addObject("EndBusinessDatetitle", "营业执照已过期！");
					}else {
						mv.addObject("EndBusinessDatetitle", "营业执照还有"+Businessdays+"天过期！");
					}
				}
				int Drugdays = (int) ((df.parse(authentication.getEndDrugDate()).getTime() - m.getTime()) / (1000*3600*24));
				if( Drugdays <= 30 ){//药品经营许可证有效期比较
					if( Drugdays<0 ){//药品经营许可证有效期比较    已过期
						mv.addObject("EndDrugDatetitle", "药品经营许可证已过期！");
					}else {
						mv.addObject("EndDrugDatetitle", "药品经营许可证有效期还有"+Drugdays+"天过期！");
					}
				}
				int GSPdays = (int) ((df.parse(authentication.getEndGSPDate()).getTime() - m.getTime()) / (1000*3600*24));
				if( GSPdays<=30){//gsp证书证书有效期比较
					if( GSPdays<0 ){//gsp证书证书有效期    已过期
						mv.addObject("endGSPDatetitle", "gsp证书已过期！");
					}else {
						mv.addObject("endGSPDatetitle", "gsp证书有效期还有"+GSPdays+"天过期！");
					}
				}
				int IDcarddays = (int) ((df.parse(authentication.getEndIDcardDate()).getTime() - m.getTime()) / (1000*3600*24));
				if( IDcarddays<=30 ){//身份证有效期比较
					if( IDcarddays<0 ){//身份证有效期    已过期
						mv.addObject("endIDcardDatetitle", "身份证已过期！");
					}else {
						mv.addObject("endIDcardDatetitle", "身份证有效期还有"+IDcarddays+"天过期！");
					}
				}
			}
        }catch (Exception e){
        	e.printStackTrace();
        	mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
        	mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
        }
        
        
       
        
        
        /*********************************************************
        //判断商家的店铺状态
        Map params = new HashMap();
        params.put("store_ower", "wemall");
        List grade_id_list = this.storeService.query(
                "select store_status from Store obj where obj.store_ower=:store_ower", params,
                -1, -1);
        String grade_id="";grade_id_list.get(0).toString();
        
        if (this.configService.getSysConfig().isStore_allow()){
            if ((grade_id == null) || (grade_id.equals(""))){
            	mv = new JModelAndView(
    		            "user/default/usercenter/IousCertification.html", this.configService
    		            .getSysConfig(),
    		            this.userConfigService.getUserConfig(), 0, request, response);
    		        mv.addObject("user", this.userService.getObjById(
    		                         SecurityUserHolder.getCurrentUser().getId()));
    		        List areas = this.areaService.query(
    		                         "select obj from Area obj where obj.parent.id is null", null,
    		                         -1, -1);
    		        mv.addObject("areas", areas);
            }else{
            	
                if (store_status == 0){
                    mv = new JModelAndView("user/default/usercenter/IousCertification.html",
                                           this.configService.getSysConfig(),
                                           this.userConfigService.getUserConfig(), 1, request,
                                           response);
                    List areas = this.areaService
                                 .query("select obj from Area obj where obj.parent.id is null",
                                        null, -1, -1);
                    List scs = this.storeClassService
                               .query("select obj from StoreClass obj where obj.parent.id is null",
                                      null, -1, -1);
                    String store_create_session = CommUtil.randomString(32);
                    request.getSession(false).setAttribute(
                        "store_create_session", store_create_session);
                    mv.addObject("store_create_session", store_create_session);
                    mv.addObject("scs", scs);
                    mv.addObject("areas", areas);
                    mv.addObject("grade_id", grade_id);
                }
                if (store_status == 1){
                    mv = new JModelAndView("error.html",
                                           this.configService.getSysConfig(),
                                           this.userConfigService.getUserConfig(), 1, request,
                                           response);
                    mv.addObject("op_title", "您的店铺正在审核中");
                    mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
                }
                if (store_status == 2){
                    mv = new JModelAndView("error.html",
                                           this.configService.getSysConfig(),
                                           this.userConfigService.getUserConfig(), 1, request,
                                           response);
                    mv.addObject("op_title", "您已经开通店铺");
                    mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
                }
                if (store_status == 3){
                    mv = new JModelAndView("error.html",
                                           this.configService.getSysConfig(),
                                           this.userConfigService.getUserConfig(), 1, request,
                                           response);
                    mv.addObject("op_title", "您的店铺已经被关闭");
                    mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
                }
            }
        }else{
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request,
                                   response);
            mv.addObject("op_title", "系统暂时关闭了申请店铺");
            mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
           
        }
        //查询认证对象
        HttpClass hc = new	HttpClass();
        try {
			String Load = hc.load("http://127.0.0.1:8081/ssm_project/select", "userName="+userName);
			if (Load!=null && Load!="") {
				Authentication auth = JSON.parseObject(Load, Authentication.class);
				mv.addObject("auth",auth);
			}
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
        return mv;
    }
	 
	@SecurityMapping(display = false, rsequence = 0, title = "首营认证", value = "/buyer/TheFirstCamp.htm*", rtype = "buyer", rname = "首营认证", rcode = "user_center", rgroup = "首营认证")
    @RequestMapping({"/buyer/TheFirstCamp.htm"})
    public ModelAndView account(HttpServletRequest request, HttpServletResponse response/*, @RequestParam("userName")String userName*/){
		String btnstatus="0";
		ModelAndView mv = null;
		mv = new JModelAndView(
	            "user/default/usercenter/TheFirstCamp.html", this.configService
	            .getSysConfig(),
	            this.userConfigService.getUserConfig(), 0, request, response);
		  mv.addObject("user", this.userService.getObjById(
                  SecurityUserHolder.getCurrentUser().getId()));
		  List area = this.areaService.query(
                  "select obj from Area obj where obj.parent.id is null", null,
                  -1, -1);
		  mv.addObject("areas", area);
        Store store = this.storeService.getObjByProperty("user.id",
                      SecurityUserHolder.getCurrentUser().getId());
        int store_status = store == null ? 0 : store.getStore_status();
        
        String userName = SecurityUserHolder.getCurrentUser().getUserName();
        //判断商家的店铺状态
        Map params = new HashMap();
        params.put("store_ower", "wemall");
        List grade_id_list = this.storeService.query(
                "select store_status from Store obj where obj.store_ower=:store_ower", params,
                -1, -1);
        String grade_id="";grade_id_list.get(0).toString();
        
        if (this.configService.getSysConfig().isStore_allow()){
            if ((grade_id == null) || (grade_id.equals(""))){
                /*mv = new JModelAndView("user/default/usercenter/TheFirstCamp.html",
                                       this.configService.getSysConfig(),
                                       this.userConfigService.getUserConfig(), 1, request, response);
                List sgs = this.storeGradeService
                           .query("select obj from StoreGrade obj order by obj.sequence asc",
                                  null, -1, -1);
                mv.addObject("sgs", sgs);
                mv.addObject("storeTools", this.storeTools);*/
            	mv = new JModelAndView(
    		            "user/default/usercenter/TheFirstCamp.html", this.configService
    		            .getSysConfig(),
    		            this.userConfigService.getUserConfig(), 0, request, response);
    		        mv.addObject("user", this.userService.getObjById(
    		                         SecurityUserHolder.getCurrentUser().getId()));
    		        List areas = this.areaService.query(
    		                         "select obj from Area obj where obj.parent.id is null", null,
    		                         -1, -1);
    		        mv.addObject("areas", areas);
            }else{	
                if (store_status == 0){
                    mv = new JModelAndView("user/default/usercenter/TheFirstCamp.html",
                                           this.configService.getSysConfig(),
                                           this.userConfigService.getUserConfig(), 1, request,
                                           response);
                    List areas = this.areaService
                                 .query("select obj from Area obj where obj.parent.id is null",
                                        null, -1, -1);
                    List scs = this.storeClassService
                               .query("select obj from StoreClass obj where obj.parent.id is null",
                                      null, -1, -1);
                    String store_create_session = CommUtil.randomString(32);
                    request.getSession(false).setAttribute(
                        "store_create_session", store_create_session);
                    mv.addObject("store_create_session", store_create_session);
                    mv.addObject("scs", scs);
                    mv.addObject("areas", areas);
                    mv.addObject("grade_id", grade_id);
                }
                if (store_status == 1){
                    mv = new JModelAndView("error.html",
                                           this.configService.getSysConfig(),
                                           this.userConfigService.getUserConfig(), 1, request,
                                           response);
                    mv.addObject("op_title", "您的店铺正在审核中");
                    mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
                }
                if (store_status == 2){
                    mv = new JModelAndView("error.html",
                                           this.configService.getSysConfig(),
                                           this.userConfigService.getUserConfig(), 1, request,
                                           response);
                    mv.addObject("op_title", "您已经开通店铺");
                    mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
                }
                if (store_status == 3){
                    mv = new JModelAndView("error.html",
                                           this.configService.getSysConfig(),
                                           this.userConfigService.getUserConfig(), 1, request,
                                           response);
                    mv.addObject("op_title", "您的店铺因违规被关闭");
                    mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
                }
                if (store_status == 6){
                    mv = new JModelAndView("error.html",
                                           this.configService.getSysConfig(),
                                           this.userConfigService.getUserConfig(), 1, request,
                                           response);
                    mv.addObject("op_title", "资质过期，您的店铺已关闭");
                    mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
                }
            }
        }else{
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request,
                                   response);
            mv.addObject("op_title", "系统暂时关闭了申请店铺");
            mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
        } 
        
        //查询认证对象
        HttpClass hc = new	HttpClass();
        try {
			String Load = hc.load("http://127.0.0.1:8081/ssm_project/select", "userName="+userName);
			if (Load!=null && Load!="") {
				Authentication auth = JSON.parseObject(Load, Authentication.class);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String nowTime=sdf.format(new Date());
				String endBusinessDate=auth.getEndBusinessDate();
				String day1=getDay(endBusinessDate);
				String endDrugDate=auth.getEndDrugDate();
				String day2=getDay(endDrugDate);
				String endGSPDate=auth.getEndGSPDate();
				String day3=getDay(endGSPDate);
				String endIDcardDate=auth.getEndIDcardDate();
				String day4=getDay(endIDcardDate);
				String endPurchaseDate=auth.getEndPurchaseDate();
				String day5=getDay(endPurchaseDate);
				int e=auth.getExamine();
				if(e==3){
					btnstatus="1";
				}
				
				if(nowTime.compareTo(endBusinessDate)>0||nowTime.compareTo(endPurchaseDate)>0||
					nowTime.compareTo(endIDcardDate)>0||nowTime.compareTo(endGSPDate)>0||
					nowTime.compareTo(endDrugDate)>0||nowTime.compareTo(day1)>0||
					nowTime.compareTo(day2)>0||nowTime.compareTo(day3)>0||nowTime.compareTo(day4)>0
					||nowTime.compareTo(day5)>0){
					
					btnstatus="1";
					if(nowTime.compareTo(endBusinessDate)>0||nowTime.compareTo(endPurchaseDate)>0||
							nowTime.compareTo(endIDcardDate)>0||nowTime.compareTo(endGSPDate)>0||
							nowTime.compareTo(endDrugDate)>0){
					try {
						String examine="3";
						String ue= hc.load("http://127.0.0.1:8081/ssm_project/UpdateExamine", "examine="+examine+"&"+"userName="+userName);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				  }
				}
				/**时间 开始**/
				Map map = new HashMap();
				map.put("userName", userName);
	            List authenticationList=this.authenticationService.query(
	            		"select obj from Authentication obj where obj.userName=:userName", map, -1, -1);
	           
	            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	            Date m = new Date();
	            for (int i = 0; i < authenticationList.size(); i++) {
	            	Authentication authentication=(Authentication) authenticationList.get(i);
		            mv.addObject("auth", authentication);
					int Businessdays = (int) ((df.parse(authentication.getEndBusinessDate()).getTime() - m.getTime()) / (1000*3600*24));
					if( Businessdays<=30 ){//营业执照有效期比较
						if( Businessdays<0 ){//营业执照有效期比较    已过期
							mv.addObject("EndBusinessDatetitle", "营业执照已过期！");
							if (store!=null) {
								store.setStore_status(6);
								this.storeService.update(store);
							}
						}else {
							mv.addObject("EndBusinessDatetitle", "营业执照还有"+Businessdays+"天过期！");
						}
					}
					int Drugdays = (int) ((df.parse(authentication.getEndDrugDate()).getTime() - m.getTime()) / (1000*3600*24));
					if( Drugdays <= 30 ){//药品经营许可证有效期比较
						if( Drugdays<0 ){//药品经营许可证有效期比较    已过期
							mv.addObject("EndDrugDatetitle", "药品经营许可证已过期！");
							if (store!=null) {
								store.setStore_status(6);
								this.storeService.update(store);
							}
						}else {
							mv.addObject("EndDrugDatetitle", "药品经营许可证有效期还有"+Drugdays+"天过期！");
						}
					}
					int GSPdays = (int) ((df.parse(authentication.getEndGSPDate()).getTime() - m.getTime()) / (1000*3600*24));
					if( GSPdays<=30){//gsp证书证书有效期比较
						if( GSPdays<0 ){//gsp证书证书有效期    已过期
							mv.addObject("endGSPDatetitle", "gsp证书已过期！");
							if (store!=null) {
								store.setStore_status(6);
								this.storeService.update(store);
							}
						}else {
							mv.addObject("endGSPDatetitle", "gsp证书有效期还有"+GSPdays+"天过期！");
						}
					}
					int IDcarddays = (int) ((df.parse(authentication.getEndIDcardDate()).getTime() - m.getTime()) / (1000*3600*24));
					if( IDcarddays<=30 ){//身份证有效期比较
						if( IDcarddays<0 ){//身份证有效期    已过期
							mv.addObject("endIDcardDatetitle", "身份证已过期！");
							if (store!=null) {
								store.setStore_status(6);
								this.storeService.update(store);
							}
							
						}else {
							mv.addObject("endIDcardDatetitle", "身份证有效期还有"+IDcarddays+"天过期！");
						}
					}
				}
				/**时间**/
				
				mv.addObject("auth",auth);
				mv.addObject("btnstatus", btnstatus);
			}else{
			     btnstatus="1";
				 mv.addObject("btnstatus", btnstatus);
			}
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mv;
       
    }
	
	   public  String getDay(String day) {     
		   Calendar c = Calendar.getInstance(); 
		   Date date = null;     
		    try {
				date = new SimpleDateFormat("yy-MM-dd").parse(day);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		   c.setTime(date);   
		   int day1 = c.get(Calendar.DATE);     
		   c.set(Calendar.DATE, day1 - 30);        
		   String dayAfter = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());  
		  
		   return dayAfter;   
	   }


	
	 @SecurityMapping(display = false, rsequence = 0, title = "首营认证提交", value = "/buyer/TheFirstCampSave.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	    @RequestMapping({"/buyer/TheFirstCampSave.htm"})
	    public ModelAndView TheFirstCampSave(HttpServletRequest request, HttpServletResponse response,MultipartHttpServletRequest file,String fileNumber1
	    		,String fileNumber2,String fileNumber3,String fileNumber4,String fileNumber5,String fileEndTime1,String fileEndTime2
	    		,String fileEndTime3,String fileEndTime4,String fileEndTime5){
	    	
	        ModelAndView mv = new JModelAndView("user/default/usercenter/IousReimbursement.html", this.configService.getSysConfig(),
	                                            this.userConfigService.getUserConfig(), 0, request, response);
	      /*  
	        User user = this.userService.getObjById(CommUtil.null2Long(user_id));
	        String path = this.storeTools.createUserFolder(request, this.configService.getSysConfig(), user.getStore());
	        String url = this.storeTools.createUserFolderURL(this.configService.getSysConfig(), user.getStore());*/
	        
	        System.out.println(fileNumber1+"---"+fileNumber2);
	        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;	
	        // 获得文件：   
	        MultipartFile files =  multipartRequest.getFile("file1"); 
	        /*
	        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
	        CommonsMultipartFile file = (CommonsMultipartFile) multipartRequest.getFile("imgFile");
	        double fileSize = Double.valueOf(file.getSize()).doubleValue();
	        fileSize /= 1048576.0D;
	        double csize = CommUtil.fileSize(new File(path));
	        double remainSpace = 0.0D;
	        if (user.getStore().getGrade().getSpaceSize() != 0.0F)
	            remainSpace = (user.getStore().getGrade().getSpaceSize() * 1024.0F - csize) * 1024.0D;
	       else{
	            remainSpace = 10000000.0D;
	        }
	        Map json_map = new HashMap();
*/
	        return mv;
	    }
	
	
	@SecurityMapping(display = false, rsequence = 0, title = "特米白条消费记录", value = "/buyer/TemiIous.htm*", rtype = "buyer", rname = "用户中心", rcode = "TemiIousLog", rgroup = "特米白条")
    @RequestMapping({"/buyer/TemiIous.htm"})
    public ModelAndView IousBorrowing(HttpServletRequest request, HttpServletResponse response, String currentPage,String consumptionType){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/TemiIous.html", this.configService
            .getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        
        String userName=SecurityUserHolder.getCurrentUser().getUserName();
        String url = this.configService.getSysConfig().getAddress();
       
        try{
        	if(currentPage==null){
        		currentPage="1";
        	}
        	TemiIousLogQueryObject qo=new TemiIousLogQueryObject(currentPage, mv,"addTime", "desc");
        	qo.addQuery("obj.userName",new SysMap("userName",userName), "=");
        	if(consumptionType!=null ){
            	consumptionType= new String(consumptionType.getBytes("ISO-8859-1"),"utf-8");
            	//  借款操作 页面
        		if(consumptionType=="借款" || consumptionType.equals("借款")){
            		qo.addQuery("obj.consumptionType",new SysMap("consumptionType",consumptionType), "=");
            	}
        		//还款操作 页面
            	if(consumptionType=="还款" || consumptionType.equals("还款")){
            		qo.addQuery("obj.consumptionType",new SysMap("consumptionType",consumptionType), "=");
            	}
        	}
        	qo.setOrderBy("addTime");
            qo.setOrderType("desc");
            IPageList pList = this.temiIousLogService.list(qo);
            if(consumptionType!=null ){
            	//  借款操作 页面
        		if(consumptionType=="借款" || consumptionType.equals("借款")){
            		mv = new JModelAndView(
            	            "user/default/usercenter/TemiIous_borrow.html", this.configService
            	            .getSysConfig(),
            	            this.userConfigService.getUserConfig(), 0, request, response);
            		CommUtil.saveIPageList2ModelAndView(url +"/user/default/usercenter/TemiIous_borrow.html","", "", pList, mv);
            	}
        		//还款操作 页面
            	if(consumptionType=="还款" || consumptionType.equals("还款")){
            		mv = new JModelAndView(
            	            "user/default/usercenter/TemiIous_refund.html", this.configService
            	            .getSysConfig(),
            	            this.userConfigService.getUserConfig(), 0, request, response);
            		CommUtil.saveIPageList2ModelAndView(url +"/user/default/usercenter/TemiIous_refund.html","", "", pList, mv);
            	}
        	}else {
        		//  全部白条消费记录
        		CommUtil.saveIPageList2ModelAndView(url +"/user/default/usercenter/TemiIous.html","", "", pList, mv);
        	}
        }catch(Exception e){
       	 	e.printStackTrace();
       	 	mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
        	mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
        }
        return mv; 
    }
	
	 private void cal_msg_info1(ModelAndView mv){
	        Map params = new HashMap();
	        params.put("status", Integer.valueOf(0));
	        params.put("status1", Integer.valueOf(3));
	        params.put("type", Integer.valueOf(1));
	        params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
	        List user_msgs = this.messageService
	                         .query(
	                             "select obj from Message obj where (obj.status=:status or obj.status=:status1) and obj.type=:type and obj.toUser.id=:user_id and obj.parent.id is null order by obj.addTime desc",
	                             params, -1, -1);
	        params.clear();
	        params.put("status", Integer.valueOf(0));
	        params.put("type", Integer.valueOf(0));
	        params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
	        List sys_msgs = this.messageService
	                        .query(
	                            "select obj from Message obj where obj.status=:status and obj.type=:type and obj.toUser.id=:user_id and obj.parent.id is null order by obj.addTime desc",
	                            params, -1, -1);
	        params.clear();
	        params.put("reply_status", Integer.valueOf(1));
	        params.put("from_user_id", SecurityUserHolder.getCurrentUser().getId());
	        List replys = this.messageService
	                      .query(
	                          "select obj from Message obj where obj.reply_status=:reply_status and obj.fromUser.id=:from_user_id",
	                          params, -1, -1);
	        mv.addObject("user_msgs", user_msgs);
	        mv.addObject("sys_msgs", sys_msgs);
	        mv.addObject("reply_msgs", replys);
	    }
	
	 /**个人资料-特米白条查询**/
	    @SecurityMapping(display = false, rsequence = 0, title = "特米白条", value = "/buyer/queryIOU.htm*", rtype = "buyer", rname = "特米白条", rcode = "user_center", rgroup = "特米白条")
	    @RequestMapping({"/buyer/queryIOU.htm"})
	    public ModelAndView queryIOU(HttpServletRequest request, HttpServletResponse response, String type, String from_user_id, String currentPage){
	        ModelAndView mv = new JModelAndView(
	            "user/default/usercenter/queryIOU.html", this.configService
	            .getSysConfig(),
	            this.userConfigService.getUserConfig(), 0, request, response);
	        MessageQueryObject qo = new MessageQueryObject();
	        if ((from_user_id == null) || (from_user_id.equals(""))){
	            if ((type == null) || (type.equals(""))){
	                type = "1";
	            }
	            qo.addQuery("obj.type",
	                        new SysMap("type", Integer.valueOf(CommUtil.null2Int(type))), "=");
	            qo.addQuery("obj.toUser.id",
	                        new SysMap("user_id",
	                                   SecurityUserHolder.getCurrentUser().getId()), "=");
	        }else{
	            qo.addQuery("obj.fromUser.id",
	                        new SysMap("user_id",
	                                   SecurityUserHolder.getCurrentUser().getId()), "=");
	            type = "2";
	        }
	        qo.addQuery("obj.parent.id is null", null);
	        qo.setOrderBy("addTime");
	        qo.setOrderType("desc");
	        qo.setCurrentPage(Integer.valueOf(CommUtil.null2Int(currentPage)));
	        IPageList pList = this.messageService.list(qo);
	        String url = this.configService.getSysConfig().getAddress();
	        if ((url == null) || (url.equals(""))){
	            url = CommUtil.getURL(request);
	        }
	        CommUtil.saveIPageList2ModelAndView(url + "/buyer/queryIOU.htm", "", "",
	                                            pList, mv);
	        cal_msg_info1(mv);
	        mv.addObject("type", type);
	        mv.addObject("from_user_id", from_user_id);

	        return mv;
	    }
	    
	    @SecurityMapping(display = false, rsequence = 0, title = "特米白条还款页面", value = "/buyer/IousReimbursement.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	    @RequestMapping({"/buyer/IousReimbursement.htm"})
	    public ModelAndView account_password(HttpServletRequest request, HttpServletResponse response){
	        ModelAndView mv = new JModelAndView("user/default/usercenter/IousReimbursement.html", this.configService.getSysConfig(),
	                                            this.userConfigService.getUserConfig(), 0, request, response);
	        String wemall_view_type = CommUtil.null2String(request.getSession().getAttribute("wemall_view_type"));
	        
	      /*  Map params = new HashMap();
	        params.put("store_ower", "wemall");
	        List grade_id_list = this.storeService.query(
	                "select store_status from IOU obj where obj.store_ower=:store_ower", params,
	                -1, -1);*/
	        
	        
	        if((wemall_view_type != null) && (!wemall_view_type.equals("")) && (wemall_view_type.equals("wap"))){
	            mv = new JModelAndView("wap/IousReimbursement.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
	        }

	        return mv;
	    }
	    
	    @SecurityMapping(display = false, rsequence = 0, title = "赊销支付消费记录", value = "/buyer/PayOnCredit.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	    @RequestMapping({"/buyer/PayOnCredit.htm"})
	    public ModelAndView IousBorrowing1(HttpServletRequest request, HttpServletResponse response, String type, String from_user_id, String currentPage){
	        ModelAndView mv = new JModelAndView(
	            "user/default/usercenter/PayOnCredit.html", this.configService
	            .getSysConfig(),
	            this.userConfigService.getUserConfig(), 0, request, response);
	        MessageQueryObject qo = new MessageQueryObject();
	        if ((from_user_id == null) || (from_user_id.equals(""))){
	            if ((type == null) || (type.equals(""))){
	                type = "1";
	            }
	            qo.addQuery("obj.type",
	                        new SysMap("type", Integer.valueOf(CommUtil.null2Int(type))), "=");
	            qo.addQuery("obj.toUser.id",
	                        new SysMap("user_id",
	                                   SecurityUserHolder.getCurrentUser().getId()), "=");
	        }else{
	            qo.addQuery("obj.fromUser.id",
	                        new SysMap("user_id",
	                                   SecurityUserHolder.getCurrentUser().getId()), "=");
	            type = "2";
	        }
	        qo.addQuery("obj.parent.id is null", null);
	        qo.setOrderBy("addTime");
	        qo.setOrderType("desc");
	        qo.setCurrentPage(Integer.valueOf(CommUtil.null2Int(currentPage)));
	        IPageList pList = this.messageService.list(qo);
	        String url = this.configService.getSysConfig().getAddress();
	        if ((url == null) || (url.equals(""))){
	            url = CommUtil.getURL(request);
	        }
	        CommUtil.saveIPageList2ModelAndView(url + "/buyer/PayOnCredit.htm", "", "",
	                                            pList, mv);
	        cal_msg_info1(mv);
	        mv.addObject("type", type);
	        mv.addObject("from_user_id", from_user_id); 
	        mv.addObject("op", "PayOnCredit"); 

	        return mv;
	    }
		
		 private void cal_msg_info(ModelAndView mv){
		        Map params = new HashMap();
		        params.put("status", Integer.valueOf(0));
		        params.put("status1", Integer.valueOf(3));
		        params.put("type", Integer.valueOf(1));
		        params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		        List user_msgs = this.messageService
		                         .query(
		                             "select obj from Message obj where (obj.status=:status or obj.status=:status1) and obj.type=:type and obj.toUser.id=:user_id and obj.parent.id is null order by obj.addTime desc",
		                             params, -1, -1);
		        params.clear();
		        params.put("status", Integer.valueOf(0));
		        params.put("type", Integer.valueOf(0));
		        params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		        List sys_msgs = this.messageService
		                        .query(
		                            "select obj from Message obj where obj.status=:status and obj.type=:type and obj.toUser.id=:user_id and obj.parent.id is null order by obj.addTime desc",
		                            params, -1, -1);
		        params.clear();
		        params.put("reply_status", Integer.valueOf(1));
		        params.put("from_user_id", SecurityUserHolder.getCurrentUser().getId());
		        List replys = this.messageService
		                      .query(
		                          "select obj from Message obj where obj.reply_status=:reply_status and obj.fromUser.id=:from_user_id",
		                          params, -1, -1);
		        mv.addObject("user_msgs", user_msgs);
		        mv.addObject("sys_msgs", sys_msgs);
		        mv.addObject("reply_msgs", replys);
		    }
		
		 /**个人资料-赊销支付查询**/
	    @SecurityMapping(display = false, rsequence = 0, title = "赊销支付", value = "/buyer/queryIOU.htm*", rtype = "buyer", rname = "特米白条", rcode = "user_center", rgroup = "特米白条")
		    @RequestMapping({"/buyer/queryIOU.htm"})
		    public ModelAndView queryIOU1(HttpServletRequest request, HttpServletResponse response, String type, String from_user_id, String currentPage){
		        ModelAndView mv = new JModelAndView(
		            "user/default/usercenter/queryIOU.html", this.configService
		            .getSysConfig(),
		            this.userConfigService.getUserConfig(), 0, request, response);
		        MessageQueryObject qo = new MessageQueryObject();
		        if ((from_user_id == null) || (from_user_id.equals(""))){
		            if ((type == null) || (type.equals(""))){
		                type = "1";
		            }
		            qo.addQuery("obj.type",
		                        new SysMap("type", Integer.valueOf(CommUtil.null2Int(type))), "=");
		            qo.addQuery("obj.toUser.id",
		                        new SysMap("user_id",
		                                   SecurityUserHolder.getCurrentUser().getId()), "=");
		        }else{
		            qo.addQuery("obj.fromUser.id",
		                        new SysMap("user_id",
		                                   SecurityUserHolder.getCurrentUser().getId()), "=");
		            type = "2";
		        }
		        qo.addQuery("obj.parent.id is null", null);
		        qo.setOrderBy("addTime");
		        qo.setOrderType("desc");
		        qo.setCurrentPage(Integer.valueOf(CommUtil.null2Int(currentPage)));
		        IPageList pList = this.messageService.list(qo);
		        String url = this.configService.getSysConfig().getAddress();
		        if ((url == null) || (url.equals(""))){
		            url = CommUtil.getURL(request);
		        }
		        CommUtil.saveIPageList2ModelAndView(url + "/buyer/queryIOU.htm", "", "",
		                                            pList, mv);
		        cal_msg_info1(mv);
		        mv.addObject("type", type);
		        mv.addObject("from_user_id", from_user_id);

		        return mv;
		    }
		    
		    @SecurityMapping(display = false, rsequence = 0, title = "赊销支付还款", value = "/buyer/IousReimbursementPayOn.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
		    @RequestMapping({"/buyer/IousReimbursementPayOn.htm"})
		    public ModelAndView account_password1(HttpServletRequest request, HttpServletResponse response){
		        ModelAndView mv = new JModelAndView("user/default/usercenter/IousReimbursementPayOn.html", this.configService.getSysConfig(),
		                                            this.userConfigService.getUserConfig(), 0, request, response);
		        String wemall_view_type = CommUtil.null2String(request.getSession().getAttribute("wemall_view_type"));
		        
		        Map params = new HashMap();
		        params.put("store_ower", "wemall");
		     /*   List grade_id_list = this.storeService.query(
		                "select store_status from IOU obj where obj.store_ower=:store_ower", params,
		                -1, -1);
		        */
		        
		        if((wemall_view_type != null) && (!wemall_view_type.equals("")) && (wemall_view_type.equals("wap"))){
		            mv = new JModelAndView("wap/IousReimbursement.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
		        }

		        return mv;
		    }
	    
	   
	

}
