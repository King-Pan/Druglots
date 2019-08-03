 package com.wemall.manage.seller.action;
 
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.nutz.ioc.val.MapValue;
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
import com.wemall.foundation.domain.BuyerCreditLimit;
import com.wemall.foundation.domain.CreditLlineManagement;
import com.wemall.foundation.domain.InvitationCode;
import com.wemall.foundation.domain.Message;
import com.wemall.foundation.domain.ProcurementRelationship;
import com.wemall.foundation.domain.Store;
import com.wemall.foundation.domain.User;
import com.wemall.foundation.domain.query.CreditLlineManagementQueryObject;
import com.wemall.foundation.domain.query.ProcurementRelationshipQueryObject;
import com.wemall.foundation.domain.query.StoreQueryObject;
import com.wemall.foundation.service.AuthenticationService;
import com.wemall.foundation.service.BuyerCreditLimitService;
import com.wemall.foundation.service.CreditLlineManagementService;
import com.wemall.foundation.service.IAreaService;
import com.wemall.foundation.service.IMessageService;
import com.wemall.foundation.service.IStoreService;
import com.wemall.foundation.service.ISysConfigService;
import com.wemall.foundation.service.IUserConfigService;
import com.wemall.foundation.service.IUserService;
import com.wemall.foundation.service.ProcurementRelationshipService;
import com.wemall.foundation.service.impl.MessageServiceImpl;

/**
 *   我的账户   -》 采购关系
 * */
@Controller
public class ProcurementRelationshipAction {
	@Autowired
    private IAreaService areaService;
	
	 @Autowired
	 private ISysConfigService configService;
	 
	 @Autowired
	 private IUserConfigService userConfigService;
	 	 
	 @Autowired
	 private AuthenticationService authenticationService;
	  	 
	 @Autowired
	 private IUserService userService;
	 
	 @Autowired
	 private IStoreService storeService;
	 
	 @Autowired
	 private ProcurementRelationshipService procurementRelationshipService;
	 
	 @Autowired
	 private CreditLlineManagementService creditLlineManagementService;
	 
	 @Autowired
	 private BuyerCreditLimitService buyerCreditLimitService;
	 
	 private HttpClass hc;
	 
	 @Autowired
	 private IMessageService messageService;
	 
	 /***
	  * 
	  * @param request
	  * @param response
	  * @return
	  * 注册初始化与邀请的商家展示建立采购关系
	  */
	 @RequestMapping("/buyer/initialPurchaseRelation.htm")
	 	 public ModelAndView initialPurchaseRelation(HttpServletRequest request,HttpServletResponse response){
		 ModelAndView mv = new JModelAndView("user/default/usercenter/buildPurchaseRelation.html",
	             this.configService.getSysConfig(),this.userConfigService.getUserConfig(), 0, request, response);
		 long userid=SecurityUserHolder.getCurrentUser().getId();
		 String invitionid=SecurityUserHolder.getCurrentUser().getInvitation();
		 //判断是否有业务员
		 if (invitionid=="druglots") {
			return mv;
		}else {
		 try {
			 //得到业务员信息表获得业务员所属店铺
			String load=hc.load("http://127.0.0.1:8081/ssm_project/invitationchaphoneqiu", "phone="+invitionid);
			InvitationCode invitationCode=JSON.parseObject(load, InvitationCode.class);
			//根据业务员表的店铺名称查询店铺信息
			Map params = new HashMap();
			params.put("userName", invitationCode.getSellername());
            List<User> user=this.userService.query("select obj from User obj where obj.userName =:userName", params, -1, -1);
          //判断买家是否已经  向该店铺添加过采购关系
       	    Map params2 = new HashMap();
            params2.clear();
       	    params2.put("buyerName", SecurityUserHolder.getCurrentUser().getUserName());
       	    params2.put("storeId", String.valueOf(user.get(0).getStore().getId()));
            List ProcurementRelationshipList = this.procurementRelationshipService.query(
                    "select obj from ProcurementRelationship obj where obj.buyerName=:buyerName and obj.storeId=:storeId ", params2,-1, -1);
            //如果采购关系已经建立不显示建立采购关系的信息
            if (ProcurementRelationshipList.size()>0 ){
            	return mv;
            }else {
				mv.addObject("invitationCode", invitationCode);
				Map paramMap = new HashMap();
				paramMap.put("id", user.get(0).getStore().getId());
				List<Store> storelist=this.storeService.query("select obj from Store obj where obj.id=:id", paramMap, -1, -1);
				mv.addObject("objs", storelist);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		 return mv;
	 }
	 /*   建立采购关系   */
     @SecurityMapping(display = false, rsequence = 0, title = "查询可建立采购关系的店铺", value = "/buyer/buildPurchaseRelation.htm*", rtype = "buyer", rname = "采购关系", rcode = "buildPurchaseRelation_buyer", rgroup = "采购关系")
     @RequestMapping({"/buyer/buildPurchaseRelation.htm"})
     public ModelAndView buildPurchaseRelation(HttpServletRequest request, HttpServletResponse response,String store_id){
         ModelAndView mv = new JModelAndView("user/default/usercenter/buildPurchaseRelation.html",
             this.configService.getSysConfig(),this.userConfigService.getUserConfig(), 0, request, response);
         try{
        	 Long userId= SecurityUserHolder.getCurrentUser().getId();
             String url = this.configService.getSysConfig().getAddress();
        	 Map<String, Comparable> params = new HashMap();
             params.put("id", userId);
             List userList = this.userService.query(
                     "select obj from User obj where obj.id=:id", params,-1, -1);
             User user= (User) userList.get(0);
             
             Store userStore1=user.getStore();
             if (userStore1!=null){
            	 String storeId=String.valueOf(userStore1.getId());
            	 if(store_id==storeId || store_id.equals(storeId)){
            		 mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                             this.userConfigService.getUserConfig(), 1, request, response);
                 	mv.addObject("op_title", "不能和自家店铺建立采购关系！");
                 	mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
                 	return mv;
            	 }
             }
        	 
             
             //判断买家是否通过首营认证
             params.clear();
             params.put("userName", SecurityUserHolder.getCurrentUser().getUserName());
             List authenticationList = this.authenticationService.query(
                     "select obj from Authentication obj where obj.userName=:userName", params,-1, -1);
             Authentication authentication=(Authentication) authenticationList.get(0);
             if (authenticationList.size()<1 || authenticationList==null || authentication.getExamine()!=2){
            	 mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                         this.userConfigService.getUserConfig(), 1, request, response);
             	mv.addObject("op_title", "请先通过首营认证！");
             	mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
             	return mv;
             }
             
             
             
        		//直接建立采购关系
                 if ("store_id"==store_id || store_id.equals("store_id")){
                	 StringBuffer sql= new StringBuffer();
                	 sql.append("select obj from Store obj where obj.store_status=:store_status ");
                	 
                	 params.clear();
                     //有业务员 邀请码
                   	if( user.getDifference()==1 && !user.getInvitation().equals("druglots") &&
                   		 !user.getInvitation().equals("") && user.getInvitation()!=null ){
                   		String Load= hc.load("http://127.0.0.1:8081/ssm_project/invitationchaphoneqiu", "phone="+user.getInvitation());
                   		 
                   		InvitationCode invitationCode=JSON.parseObject(Load, InvitationCode.class);
//                   		 invitationSellername="17620335477";
//                   		 //根据店主名字查询店铺信息
                   		 params.put("userName", invitationCode.getSellername());
                   		 mv.addObject("invitationCode", invitationCode);
                    }else {//没有业务邀请码
                   	 	params.put("userName", "wemall");
                   	 	mv.addObject("invitationCode", "druglots");
                    }
                   	
                	 List userStoreList = this.userService.query(
                             "select obj from User obj where obj.userName=:userName", params,-1, -1);
                     User userStore=(User) userStoreList.get(0);
                     
                     params.clear();
                	 params.put("store_status", 2);
                	 params.put("id", userStore.getStore().getId());
                	 sql.append(" and obj.id=:id"); 
                	 
                	//判断买家是否已经  向该店铺添加过采购关系
                	 Map params2 = new HashMap();
                	 params2.clear();
                	 params2.put("buyerName", SecurityUserHolder.getCurrentUser().getUserName());
                	 params2.put("storeId", String.valueOf(userStore.getStore().getId()));
                     List ProcurementRelationshipList = this.procurementRelationshipService.query(
                             "select obj from ProcurementRelationship obj where obj.buyerName=:buyerName and obj.storeId=:storeId ", params2,-1, -1);
                     if (ProcurementRelationshipList.size()>0 ){
                     	return mv;
                     }
                 	//判断买家是否已经  向该店铺添加过采购关系 结束
            		 
                	 List storeList = this.storeService.query(sql.toString(), params, -1, -1);	 
                	 mv.addObject("objs", storeList);
                 }else{//在购物车中跳转到采购关系的
                	//判断买家是否已经  向该店铺添加过采购关系
                     params.clear();
                     params.put("buyerName", SecurityUserHolder.getCurrentUser().getUserName());
                     params.put("storeId", store_id);
                     params.put("auditStatus", 2);
                     List ProcurementRelationshipList = this.procurementRelationshipService.query(
                             "select obj from ProcurementRelationship obj where obj.buyerName=:buyerName and obj.storeId=:storeId and obj.auditStatus<>:auditStatus ", params,-1, -1);
                     if (ProcurementRelationshipList.size()>0 ){
                     	return mv;
                     }
                     
                	 params.clear();
                	 params.put("store_id", Long.valueOf(store_id));
                	 params.put("store_status", 2);
                     List store_nameList= this.storeService.query("select obj from Store obj where obj.id =:store_id and store_status=:store_status", params, -1, -1);
                     mv.addObject("objs", store_nameList);
                     mv.addObject("store_id", store_id);
                     
                     InvitationCode invitationCode=new InvitationCode();
                     invitationCode.setSalesman("druglots");
                	 mv.addObject("invitationCode",invitationCode );
                     
                   /*//查所有业务员
                   String Load = hc.load("http://127.0.0.1:8081/ssm_project/invitationchasuoyou", "");
                   List<InvitationCode> list = JSON.parseArray(Load, InvitationCode.class);
                   for (int i = 0; i < list.size(); i++) {
                	   InvitationCode invitationCode=list.get(i);
                       if (invitationCode.getSellername()=="wemall" || invitationCode.getSellername().equals("wemall")){
                    	   mv.addObject("invitationCode",invitationCode);
                       }
                   }*/
                 }
         }catch (Exception e) {
        	e.printStackTrace();
         	mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                     this.userConfigService.getUserConfig(), 1, request, response);
         	mv.addObject("op_title", "请先首营认证！");
         	mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
         return mv;
	}
     
     /** 申请建立采购关系   ajax**/
     @RequestMapping({"/buyer/buildPurchaseRelation2.htm"})
     public void buildPurchaseRelation2(HttpServletRequest request, HttpServletResponse response,String store_id){
         response.setContentType("application/json;");
         response.setCharacterEncoding("utf-8");
         JSONObject json=null;
         Map map=new HashMap();
         String content=null;
         try{
             PrintWriter writer=response.getWriter();
        	 Long userId= SecurityUserHolder.getCurrentUser().getId();
             String url = this.configService.getSysConfig().getAddress();
             
        	 Map params = new HashMap();
             params.put("id", userId);
             List userList = this.userService.query(
                     "select obj from User obj where obj.id=:id", params,-1, -1);
             User user= (User) userList.get(0);
             
             Store userStore1=user.getStore();
             if (userStore1!=null){
            	 String storeId=String.valueOf(userStore1.getId());
            	 if(store_id==storeId || store_id.equals(storeId)){
            		 map.put("state", "error");
            		 map.put("content", "不能和自家店铺建立采购关系！");
            		 json=new JSONObject(map);
            		 writer.print(json);
            		 return ;
            	 }
             }
        	 
             //判断买家是否通过首营认证
             params.clear();
             
             params.put("userName", SecurityUserHolder.getCurrentUser().getUserName());
             List<Authentication> authenticationList = this.authenticationService.query(
                     "select obj from Authentication obj where obj.userName=:userName", params,-1, -1);
             if (authenticationList==null ||  authenticationList.size()==0) {
				
            	
                	 map.put("state", "error");
            		 map.put("content", "请先通过首营认证！");
            		 json=new JSONObject(map);
            		 writer.print(json);
            		 return ;
                 }else if ( authenticationList.get(0).getExamine()!=2) {
				
                	 map.put("state", "error");
            		 map.put("content", "请先通过首营认证！");
            		 json=new JSONObject(map);
            		 writer.print(json);
            		 return ;
				} 
         
             /**查询已经申请采购关系的状态**/
             params.clear();
             params.put("buyerId", String.valueOf(SecurityUserHolder.getCurrentUser().getId()));
             params.put("storeId", store_id);
             List ProcurementRelationshipList = this.procurementRelationshipService.query(
                     "select obj from ProcurementRelationship obj where obj.buyerId=:buyerId and obj.storeId=:storeId ", params,-1, -1);
             if (ProcurementRelationshipList.size()>0){
            	 ProcurementRelationship procurementRelationship=(ProcurementRelationship) ProcurementRelationshipList.get(0);
            	 if (procurementRelationship.getAuditStatus()==0){
            		 map.put("state", "error");
            		 map.put("content", "正在审核中，请勿重复提交！");
            		 json=new JSONObject(map);
            		 writer.print(json);
            		 return ;
            	 }
             }
             map.put("state", "success");
    		 json=new JSONObject(map);
    		 writer.print(json);
         }catch(Exception e){
        	 e.printStackTrace();
         }
     }
     /**
      * 提交采购关系申请判断是否通过首营认证
      * @param request
      * @param response
      */
     @RequestMapping("/buyer/shouying.htm")
     public void getshouying(HttpServletRequest request,HttpServletResponse response){
    	 Map params1 = new HashMap();
         params1.put("userName", SecurityUserHolder.getCurrentUser().getUserName());
         List authenticationList = this.authenticationService.query(
                 "select obj from Authentication obj where obj.userName=:userName", params1,-1, -1);
         if (authenticationList.size()<=0 || authenticationList==null){
         	response.setContentType("text/plain");
 	        response.setHeader("Cache-Control", "no-cache");
 	        response.setCharacterEncoding("UTF-8");
 	        try {
 	            PrintWriter writer = response.getWriter();
 	            writer.print("erro");
 	        } catch (IOException e){
 	            e.printStackTrace();
 	        }
         } 
     }
     
     /**采购关系提交**/
     @SecurityMapping(display = false, rsequence = 0, title = "采购关系提交", value = "/buyer/addPurchaseRelation.htm*", rtype = "buyer", rname = "采购关系", rcode = "addPurchaseRelation_buyer", rgroup = "采购关系")
     @RequestMapping({"/buyer/addPurchaseRelation.htm"})
     public ModelAndView addPurchaseRelation(HttpServletRequest request, HttpServletResponse response,String storeId,String invitationcodeid){
        ModelAndView mv = new JModelAndView("user/default/usercenter/success.html",
             this.configService.getSysConfig(),this.userConfigService.getUserConfig(), 0, request, response);
        
        Long userId= SecurityUserHolder.getCurrentUser().getId();
        String url = this.configService.getSysConfig().getAddress();
        Map contentMap=new HashMap(); 
        
        try{
        	Map params = new HashMap();
        	params.clear();
        	params.put("storeId", storeId);
        	params.put("buyerId", String.valueOf(userId));
        	List procurementRelationshipList=this.procurementRelationshipService.query(
        			"select obj from ProcurementRelationship obj where obj.storeId=:storeId and obj.buyerId=:buyerId ", params, -1, -1);
        	
        	//判断买家是否已经想卖家申请了采购关系
        	if (procurementRelationshipList.size()<=0){
        		//根据店铺id 查询店主名称， 查询店主id
                params.clear();
                params.put("store_id", Long.valueOf(storeId));
                params.put("store_status", 2);
                 List list = this.storeService.query("select obj from Store obj where obj.id =:store_id and obj.store_status=:store_status", params, -1, -1);
                 Store store = (Store) list.get(0);
                 String store_name=store.getStore_name();
                 String shopkeeperName=store.getUser().getUserName();
                 String shopkeeperId=store.getUser().getId().toString();
            	
            	ProcurementRelationship procurementRelationship=new ProcurementRelationship();
                procurementRelationship.setBuyerId(String.valueOf(userId));
                //根据买家id 查询买家名称
                params.clear();
                params.put("userId", Long.valueOf(userId));
                List userList= this.userService.query("select obj from User obj where obj.id =:userId", params, -1, -1);
                User user=(User) userList.get(0);
                procurementRelationship.setBuyerName(user.getUsername());
                
                procurementRelationship.setStoreId(storeId);
                procurementRelationship.setStoreName(store_name);
                procurementRelationship.setShopkeeperId(shopkeeperId);
                procurementRelationship.setShopkeeperName(shopkeeperName);
                
                if (invitationcodeid!="" && invitationcodeid!=null && !invitationcodeid.equals("请选择...")) {
                	procurementRelationship.setInvitationcodeid(invitationcodeid);
    			}
                //  添加买家的首营资料信息
                Authentication authn2= this.authenticationService.getObjByProperty("userName", SecurityUserHolder.getCurrentUser().getUserName());
                procurementRelationship.setBuyerbusinessLicense(authn2.getBusinessLicense());
                procurementRelationship.setBuyerDrugLicense(authn2.getDrugNumber());
                procurementRelationship.setBuyerGSPCertificate(authn2.getgSPNumber());
                procurementRelationship.setBuyerPurchaseOrders(authn2.getPurchaseNumber());
                
                //  添加卖家的首营资料信息
                Authentication auth= this.authenticationService.getObjByProperty("userName", store.getUser().getUsername());
                procurementRelationship.setStoreBusinessLicense(auth.getBusinessLicense());
                procurementRelationship.setStoreDrugLicense(auth.getDrugNumber());
                procurementRelationship.setStoreGSPCertificate(auth.getgSPNumber());
                procurementRelationship.setStorePurchaseOrders(auth.getPurchaseNumber());
                
                procurementRelationship.setAuditAddTime(new Date());
                procurementRelationship.setAuditStatus(0);
                procurementRelationship.setAddTime(new Date());
             	this.procurementRelationshipService.save(procurementRelationship);
             	//短信
             	String content=authn2.getEnterpriseName()+"向您的店铺申请建立采购关系。";
             	 User toUser = this.userService.getObjById(store.getUser().getId());
                 if (toUser != null){
                     Message msg = new Message();
                     msg.setAddTime(new Date());
                     Whitelist whiteList = new Whitelist();
                     content = content.replaceAll("\n", "iskyhop_br");
                     msg.setContent(Jsoup.clean(content, Whitelist.basic())
                                    .replaceAll("iskyhop_br", "\n"));
                     msg.setFromUser(SecurityUserHolder.getCurrentUser());
                     msg.setToUser(toUser);
                     msg.setType(1);
                     this.messageService.save(msg);
                 }
             	/**添加店铺赊销 与 买家赊销信息记录开始**/
             	BuyerCreditLimit buyerCreditLimit=new BuyerCreditLimit();
             	//判断买家是否已经设置过额度
             	params.clear();
            	params.put("buyerId", userId.intValue());//buyerId是buyerCreditLimit表的买家ID
            	params.put("storeId", Integer.parseInt(storeId));//sellerId是buyerCreditLimit表的买家ID
            	List buyerCreditLineList=this.buyerCreditLimitService.query(
            			"select buyerId from BuyerCreditLimit obj where obj.buyerId=:buyerId and obj.storeId=:storeId", params, -1, -1);
            	
            	if(buyerCreditLineList.size()<=0){

                    buyerCreditLimit.setBuyerId(userId.intValue());
                    buyerCreditLimit.setBuyerName(SecurityUserHolder.getCurrentUser().getUsername());
                	
                    buyerCreditLimit.setStoreId(store.getId().intValue());
                    buyerCreditLimit.setStoreName(store.getStore_name());
                    buyerCreditLimit.setSellerId(store.getUser().getId().intValue());
                    buyerCreditLimit.setSellerName(store.getUser().getUsername());
                	
                	buyerCreditLimit.setBuyerCombination("0");
                	buyerCreditLimit.setBuyerRemainingUndrawn("0");
                	buyerCreditLimit.setAddTime(new Date());
                	

                	buyerCreditLimit.setInterest(0.00);
                	this.buyerCreditLimitService.save(buyerCreditLimit);
            	}
             	/**添加店铺赊销 与买家赊销信息记录结束**/
        	}else{
        		/**已经提交了采购申请，审核不通过后再次提交采购申请   开始**/
            	ProcurementRelationship procurementRelationship =(ProcurementRelationship) procurementRelationshipList.get(0);
            	if (procurementRelationship.getAuditStatus()==2){
            		//根据店铺id 查询店主名称， 查询店主id
                    params.clear();
                    params.put("store_id", Long.valueOf(storeId));
                    params.put("store_status", 2);
                     List list = this.storeService.query("select obj from Store obj where obj.id =:store_id and obj.store_status=:store_status", params, -1, -1);
                     Store store = (Store) list.get(0);
                     String shopkeeperName=store.getUser().getUserName();
                     String shopkeeperId=store.getUser().getId().toString();
                     String store_name=store.getStore_name();
            		
            		procurementRelationship.setBuyerId(String.valueOf(userId));
                    //根据买家id 查询买家名称
                    params.clear();
                    params.put("userId", Long.valueOf(userId));
                    List userList= this.userService.query("select obj from User obj where obj.id =:userId", params, -1, -1);
                    User user=(User) userList.get(0);
                    procurementRelationship.setBuyerName(user.getUsername());
                    
                    procurementRelationship.setStoreId(storeId);
                    procurementRelationship.setShopkeeperName(shopkeeperName);
                    procurementRelationship.setStoreName(store_name);
                    procurementRelationship.setShopkeeperId(shopkeeperId);
                    if (invitationcodeid!="" && invitationcodeid!=null && !invitationcodeid.equals("请选择...")) {
                    	procurementRelationship.setInvitationcodeid(invitationcodeid);
        			}
                    //  添加买家的首营资料信息
                    Authentication authn2= this.authenticationService.getObjByProperty("userName", SecurityUserHolder.getCurrentUser().getUserName());
                    procurementRelationship.setBuyerbusinessLicense(authn2.getBusinessLicense());
                    procurementRelationship.setBuyerDrugLicense(authn2.getDrugNumber());
                    procurementRelationship.setBuyerGSPCertificate(authn2.getgSPNumber());
                    procurementRelationship.setBuyerPurchaseOrders(authn2.getPurchaseNumber());
                    
                    //  添加卖家的首营资料信息
                    Authentication auth= this.authenticationService.getObjByProperty("userName", store.getUser().getUsername());
                    procurementRelationship.setStoreBusinessLicense(auth.getBusinessLicense());
                    procurementRelationship.setStoreDrugLicense(auth.getDrugNumber());
                    procurementRelationship.setStoreGSPCertificate(auth.getgSPNumber());
                    procurementRelationship.setStorePurchaseOrders(auth.getPurchaseNumber());
                    
                    procurementRelationship.setAuditAddTime(new Date());
                    procurementRelationship.setAuditStatus(0);
                    procurementRelationship.setAddTime(new Date());
            		this.procurementRelationshipService.update(procurementRelationship);
            		//再次申请
            		String content=authn2.getEnterpriseName()+"向您的店铺申请建立采购关系。";
                	 User toUser = this.userService.getObjById(store.getUser().getId());
                    if (toUser != null){
                        Message msg = new Message();
                        msg.setAddTime(new Date());
                        Whitelist whiteList = new Whitelist();
                        content = content.replaceAll("\n", "iskyhop_br");
                        msg.setContent(Jsoup.clean(content, Whitelist.basic())
                                       .replaceAll("iskyhop_br", "\n"));
                        msg.setFromUser(SecurityUserHolder.getCurrentUser());
                        msg.setToUser(toUser);
                        msg.setType(1);
                        this.messageService.save(msg);
                    }
            	}
            	/**已经提交了采购申请，审核不通过后再次提交采购申请   结束**/
        	}
        	
            mv.addObject("op_title", "采购关系提交成功");
        	mv.addObject("url", CommUtil.getURL(request) + "/buyer/PurchaseRelation_list.htm?state=null");
        }catch(Exception e){
        	e.printStackTrace();
        	mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                     this.userConfigService.getUserConfig(), 1, request, response);
        	mv.addObject("op_title", "请先通过首营资料！");
         	mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
        }
        return mv; 
        
 	} 
     
     @SecurityMapping(display = false, rsequence = 0, title = "买卖家  查询采购关系", value = "/buyer/PurchaseRelation_list.htm*", rtype = "buyer", rname = "采购关系", rcode = "PurchaseRelation_list_buyer", rgroup = "采购关系")
     @RequestMapping({"/buyer/PurchaseRelation_list.htm"})
     public ModelAndView PurchaseRelation_list(HttpServletRequest request, HttpServletResponse response,String state){
         ModelAndView mv = new JModelAndView("user/default/usercenter/PurchaseRelation_list.html",
             this.configService.getSysConfig(),this.userConfigService.getUserConfig(), 0, request, response);
         
         Long userId= SecurityUserHolder.getCurrentUser().getId();
         String url = this.configService.getSysConfig().getAddress();
         
         try{
        	 Map params = new HashMap();
             params.put("id", userId);
             
             List userList = this.userService.query(
                     "select obj from User obj where obj.id=:id", params,-1, -1);
             User user=(User) userList.get(0);
             
        	 ProcurementRelationshipQueryObject qo = new ProcurementRelationshipQueryObject("1", mv,"addTime", "desc");
        	 if(state=="audit" || state.equals("audit")){//从商家中心查询采购关系，查询卖家的采购关系
        		 qo.addQuery("obj.shopkeeperId",new SysMap("shopkeeperId",String.valueOf(userId)), "=");
             }else{//从我的账户查询采购关系   ;查询买家的采购关系
            	 qo.addQuery("obj.buyerId",new SysMap("buyerId",String.valueOf(userId)), "=");
             }
             qo.setOrderBy("auditAddTime");
             qo.setOrderType("desc");
             IPageList pList = this.procurementRelationshipService.list(qo);
             if(state=="audit" || state.equals("audit")){//从商家中心查询采购关系，
            	 mv = new JModelAndView("user/default/usercenter/audit_Management.html",
                         this.configService.getSysConfig(),this.userConfigService.getUserConfig(), 0, request, response);
            	//根据建立采购关系的买家名字去首营中查询买家企业名
            	 List<ProcurementRelationship> procurementRelationships= pList.getResult();
            	 Map map = new HashMap();
            	 for (ProcurementRelationship procurementRelationship : procurementRelationships) {
            		map.clear();
					map.put("userName", procurementRelationship.getBuyerName());
					List<Authentication> auths=this.authenticationService.query("select obj from Authentication obj where obj.userName=:userName", map, -1, -1);
				    procurementRelationship.setAuth(auths.get(0).getEnterpriseName());
            	 }
            	 CommUtil.saveIPageList2ModelAndView(url +"/user/default/usercenter/audit_Management.html","", "", pList, mv);
             }else{//从我的账户查询采购关系
            	 CommUtil.saveIPageList2ModelAndView(url +"/user/default/usercenter/PurchaseRelation_list.html","", "", pList, mv);
             }
         }catch(Exception e){
        	 e.printStackTrace();
        	 mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                     this.userConfigService.getUserConfig(), 1, request, response);
         	 mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
         }
         return mv;
 	}
     
     @SecurityMapping(display = false, rsequence = 0, title = "买卖解除买卖关系", value = "/buyer/PurchaseRelation_shan.htm*", rtype = "buyer", rname = "采购关系", rcode = "PurchaseRelation_shan_buyer", rgroup = "采购关系")
     @RequestMapping({"/buyer/PurchaseRelation_shan.htm"})
     public ModelAndView PurchaseRelation_shan(HttpServletRequest request, HttpServletResponse response ,String id){
         ModelAndView mv = new JModelAndView("user/default/usercenter/PurchaseRelation_list.html",
             this.configService.getSysConfig(),this.userConfigService.getUserConfig(), 0, request, response);
         
         Long userId= SecurityUserHolder.getCurrentUser().getId();
         String url = this.configService.getSysConfig().getAddress();
         
         try{
        	 this.procurementRelationshipService.delete((long) Integer.parseInt(id));
        	 
        	 Map params = new HashMap();
             params.put("id", userId);
             
             List userRole = this.userService.query(
                     "select userRole from User obj where obj.id=:id", params,-1, -1);
        	 
        	 ProcurementRelationshipQueryObject qo = new ProcurementRelationshipQueryObject("1", mv,"addTime", "desc");
        	 //查询买家的采购关系
        	 if(userRole.get(0).toString()=="BUYER" || userRole.get(0).toString().equals("BUYER")){
        		 qo.addQuery("obj.buyerId",new SysMap("buyerId",String.valueOf(userId)), "=");
        	 }
        	 //查询卖家的采购关系
        	 if(userRole.get(0).toString()=="BUYER_SELLER" || userRole.get(0).toString().equals("BUYER_SELLER")){
        		 qo.addQuery("obj.shopkeeperId",new SysMap("shopkeeperId",String.valueOf(userId)), "=");
        	 }
             qo.setOrderBy("auditAddTime");
             qo.setOrderType("desc");
             IPageList pList = this.procurementRelationshipService.list(qo);
             CommUtil.saveIPageList2ModelAndView(url +"/user/default/usercenter/PurchaseRelation_list.html","", "", pList, mv);
             mv.addObject("userRole", userRole.get(0).toString());
         }catch(Exception e){
        	 e.printStackTrace();
        	 mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                     this.userConfigService.getUserConfig(), 1, request, response);
         	 mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
         }
         return mv;
 	}
     
     @SecurityMapping(display = false, rsequence = 0, title = "买家采购关系复核提交,卖家审核提交", value = "/buyer/PurchaseRelation_review.htm*", rtype = "buyer", rname = "采购关系", rcode = "PurchaseRelation_review_buyer", rgroup = "采购关系")
     @RequestMapping({"/buyer/PurchaseRelation_review.htm"})
     public ModelAndView PurchaseRelation_review(HttpServletRequest request, HttpServletResponse response,String shopkeeperId,Long id,int auditStatus,String zhcause){
    	 ModelAndView mv = new JModelAndView("user/default/usercenter/PurchaseRelation_list.html",
                 this.configService.getSysConfig(),this.userConfigService.getUserConfig(), 0, request, response);
    	 
    	 Long userId= SecurityUserHolder.getCurrentUser().getId();
         String url = this.configService.getSysConfig().getAddress();
         try{
        	 Map params = new HashMap();
             params.put("id", userId);
             List userRole = this.userService.query(
                     "select userRole from User obj where obj.id=:id", params,-1, -1);
             
        	 ProcurementRelationship pr = this.procurementRelationshipService.getObjById(id);
        	 pr.setAuditStatus(auditStatus);
        	 pr.setAuditAddTime(new Date());
        	 
        	 Authentication authn2= this.authenticationService.getObjByProperty("userName", shopkeeperId);
        	 pr.setStoreBusinessLicense(authn2.getBusinessLicense());
        	 pr.setStoreDrugLicense(authn2.getDrugNumber());
        	 pr.setStoreGSPCertificate(authn2.getgSPNumber());
        	 pr.setStorePurchaseOrders(authn2.getPurchaseNumber());
        	 pr.setZhcause(zhcause);
        	 
        	 this.procurementRelationshipService.update(pr);
        	 
        	 if (auditStatus==2){
        		 String storeId=pr.getStoreId();
            	 String buyerId=pr.getBuyerId();
            	 String Load = hc.load("http://127.0.0.1:8081/ssm_project/updateProcurementRelationship", "zhcause="+zhcause+"&buyerId="+buyerId+"&storeId="+storeId);
    		     String content=SecurityUserHolder.getCurrentUser().getStore().getStore_name()+"已拒绝您的采购关系申请！去看看其他商家吧。";
            	 if (Load=="sb" || Load.equals("sb")){//返回失败，修改原因失败
    		    	 System.out.println("修改原因失败");
    		     }
            	 //短信
            	 User toUser = this.userService.getObjById(Long.parseLong(pr.getBuyerId()));
                 if (toUser != null){
                     Message msg = new Message();
                     msg.setAddTime(new Date());
                     Whitelist whiteList = new Whitelist();
                     content = content.replaceAll("\n", "iskyhop_br");
                     msg.setContent(Jsoup.clean(content, Whitelist.basic())
                                    .replaceAll("iskyhop_br", "\n"));
                     msg.setFromUser(SecurityUserHolder.getCurrentUser());
                     msg.setToUser(toUser);
                     msg.setType(1);
                     this.messageService.save(msg);
                 }
        	 }
        	 
        	 if (auditStatus==1){
        	 String content=SecurityUserHolder.getCurrentUser().getStore().getStore_name()+"已通过您的采购关系申请！现在可以去采购啦。";
        	 User toUser = this.userService.getObjById(Long.parseLong(pr.getBuyerId()));
             if (toUser != null){
                 Message msg = new Message();
                 msg.setAddTime(new Date());
                 Whitelist whiteList = new Whitelist();
                 content = content.replaceAll("\n", "iskyhop_br");
                 msg.setContent(Jsoup.clean(content, Whitelist.basic())
                                .replaceAll("iskyhop_br", "\n"));
                 msg.setFromUser(SecurityUserHolder.getCurrentUser());
                 msg.setToUser(toUser);
                 msg.setType(1);
                 this.messageService.save(msg);
        	 }
        }
        	 
        	 //查询采购关系记录
        	 ProcurementRelationshipQueryObject qo = new ProcurementRelationshipQueryObject("1", mv,"addTime", "desc");
        	 //查询买家的采购关系
        	 if(userRole.get(0).toString()=="BUYER" || userRole.get(0).toString().equals("BUYER")){
        		 qo.addQuery("obj.buyerId",new SysMap("buyerId",String.valueOf(userId)), "=");
        	 }
        	 //查询卖家的采购关系
        	 if(userRole.get(0).toString()=="BUYER_SELLER" || userRole.get(0).toString().equals("BUYER_SELLER")){
        		 qo.addQuery("obj.shopkeeperId",new SysMap("shopkeeperId",String.valueOf(userId)), "=");
        	 }
             qo.setOrderBy("auditAddTime");
             qo.setOrderType("desc");
             IPageList pList = this.procurementRelationshipService.list(qo);
             mv=new ModelAndView("redirect:/buyer/PurchaseRelation_list.htm?state=audit");
             CommUtil.saveIPageList2ModelAndView(url +"/user/default/usercenter/audit_Management.html","", "", pList, mv);
             mv.addObject("userRole", userRole.get(0).toString());
         }catch(Exception e){
        	 e.printStackTrace();
        	 mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                     this.userConfigService.getUserConfig(), 1, request, response);
         	 mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
         }
         return mv;
 	}
     
     /**查询特定的可建立采购关系的店铺**/
     @SecurityMapping(display = false, rsequence = 0, title = "查询特定的可建立采购关系的店铺", value = "/buyer/queryProcurementRelationship.htm*", rtype = "buyer", rname = "采购关系", rcode = "buildPurchaseRelation_buyer", rgroup = "采购关系")
     @RequestMapping({"/buyer/queryProcurementRelationship.htm"})
     public ModelAndView queryProcurementRelationship(HttpServletRequest request, HttpServletResponse response,String store_id){
    	 ModelAndView mv = new JModelAndView("user/default/usercenter/PurchaseRelation_list.html",
                 this.configService.getSysConfig(),this.userConfigService.getUserConfig(), 0, request, response);
    	 
    	 Long userId= SecurityUserHolder.getCurrentUser().getId();
         String url = this.configService.getSysConfig().getAddress();
         try{
        	 ProcurementRelationship procurementRelationship =new ProcurementRelationship();
        	 procurementRelationship=this.procurementRelationshipService.getObjByProperty("storeId", store_id);
        	 mv.addObject("procurementRelationship", procurementRelationship);
         }catch(Exception e){
        	 e.printStackTrace();
        	 mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                     this.userConfigService.getUserConfig(), 1, request, response);
         	 mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
         }
         return mv;
     }
     
     /**采购关系审核中  首营资料详情查看**/
     @SecurityMapping(display = false, rsequence = 0, title = "首营资料详情查看", value = "/seller/queryProcuAuthentication.htm*", rtype = "seller", rname = "采购关系审核", rcode = "buildPurchaseRelation_buyer", rgroup = "采购关系审核")
     @RequestMapping({"/seller/queryProcuAuthentication.htm"})
     public ModelAndView queryProcuAuthentication(HttpServletRequest request, HttpServletResponse response,String buyerId){
    	 ModelAndView mv = new JModelAndView("user/default/usercenter/queryProcuAuthentication.html",
                 this.configService.getSysConfig(),this.userConfigService.getUserConfig(), 0, request, response);
    	 Long userId= SecurityUserHolder.getCurrentUser().getId();
         String url = this.configService.getSysConfig().getAddress();
         try{
        	 User user =this.userService.getObjById(Long.valueOf(buyerId));
        	 Map map = new HashMap();
 			 map.put("userName", user.getUsername());
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
         }catch(Exception e){
        	 e.printStackTrace();
        	 mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                     this.userConfigService.getUserConfig(), 1, request, response);
         	 mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
         }
         return mv;
     }
     
     
     /**我的账户-采购关系列表  首营资料详情查看**/
     @SecurityMapping(display = false, rsequence = 0, title = "首营资料详情查看", value = "/seller/queryProcuAuthentication2.htm*", rtype = "seller", rname = "采购关系列表", rcode = "buildPurchaseRelation_buyer", rgroup = "采购关系列表")
     @RequestMapping({"/seller/queryProcuAuthentication2.htm"})
     public ModelAndView queryProcuAuthentication2(HttpServletRequest request, HttpServletResponse response,String userId){
    	 ModelAndView mv = new JModelAndView("user/default/usercenter/queryProcuAuthentication2.html",
                 this.configService.getSysConfig(),this.userConfigService.getUserConfig(), 0, request, response);
    	 
         String url = this.configService.getSysConfig().getAddress();
         try{
        	 Map map = new HashMap();
 			 map.put("userId", userId);
             List authenticationList=this.authenticationService.query(
             		"select obj from Authentication obj where obj.userId=:userId", map, -1, -1);
             SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
             Date m = new Date();
             for (int i = 0; i < authenticationList.size(); i++) {
             	Authentication authentication=(Authentication) authenticationList.get(i);
             	mv.addObject("auth", authentication);
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
         }catch(Exception e){
        	 e.printStackTrace();
        	 mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                     this.userConfigService.getUserConfig(), 1, request, response);
         	 mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
         }
         return mv;
     }
     

}
