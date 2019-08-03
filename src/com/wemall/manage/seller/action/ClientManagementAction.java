package com.wemall.manage.seller.action;
 
import java.io.File;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.csvreader.CsvReader;
import com.wemall.core.annotation.SecurityMapping;
import com.wemall.core.domain.virtual.SysMap;
import com.wemall.core.mv.JModelAndView;
import com.wemall.core.query.support.IPageList;
import com.wemall.core.security.support.SecurityUserHolder;
import com.wemall.core.tools.CommUtil;
import com.wemall.foundation.domain.Authentication;
import com.wemall.foundation.domain.CustomerData;
import com.wemall.foundation.domain.RelatedAccount;
import com.wemall.foundation.domain.query.CustomerDataQueryObject;
import com.wemall.foundation.domain.query.UserQueryObject;
import com.wemall.foundation.service.AuthenticationService;
import com.wemall.foundation.service.CustomerDataService;
import com.wemall.foundation.service.ISysConfigService;
import com.wemall.foundation.service.IUserConfigService;
import com.wemall.foundation.service.IUserService;
import com.wemall.foundation.service.RelatedAccountService;

/**
 *  卖家中心->   客户管理
 * */
@Controller
public class ClientManagementAction {
	
	 @Autowired
	 private ISysConfigService configService;
	 
	 @Autowired
	 private IUserConfigService userConfigService;
	 
	 @Autowired
	 private CustomerDataService customerDataService;
	 
	 @Autowired
	 private RelatedAccountService relatedAccountService;
	  	 
	 @Autowired
	 private IUserService userService;
	 
	 @Autowired
	 private AuthenticationService authenticationService;
	 	 	
	@SecurityMapping(display = false, rsequence = 0, title = "客户管理", value = "/seller/client_management.htm*", rtype = "seller", rname = "客户管理", rcode = "client_management_seller", rgroup = "客户管理")
    @RequestMapping({"/seller/client_management.htm"})
    public ModelAndView client_management(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/client_management.html",
            this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        Long userid= SecurityUserHolder.getCurrentUser().getId();
        
       /* Authentication authen = this.authenticationService.getObjById(userid);
        System.out.println(authen.toString());*/
        
       /* Map params = new HashMap();
        params.put("userName", String.valueOf(userid));
        List<authentication> auth = this.authenticationService.query(
                         "select obj from authentication obj where obj.userName=:userName", params,
                         -1, -1);*/
       /* String userName="zhangsan";
        String url="http://192.168.1.145:8080/Load";
        String parm="userName="+userName;
        HttpClass httpc=new HttpClass();
        try {
        	String jsonstr=httpc.load(url, parm);
//        	jsonstr=jsonstr.replaceAll("upload\\ybm100\\","F:\\\\java\\\\apache-tomcat-8.5.15\\\\img\\\\");
        	System.out.println(jsonstr);
        	JSONObject json = JSONObject.fromObject(jsonstr);
        	authentication auth=(authentication)JSONObject.toBean(json, authentication.class);
        	mv.addObject("auth", auth);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
       /* authentication auth1=new authentication();
        List<Authentication> auth=null;
        auth1.setUserName("32768");
        auth1.setPurchaseOrders("upload\\ybm100\\6900011112.jpg");
        auth1.setBusinessNumber("32");
        auth.add(auth1);
        mv.addObject("auth", auth);*/
        return mv;
    }
	
	
	/**客户药房资料查询**/
	@SecurityMapping(display = false, rsequence = 0, title = "客户药房资料查询", value = "/seller/queryCustomerData.htm*", rtype = "seller", rname = "客户管理", rcode = "client_management_seller", rgroup = "客户管理")
    @RequestMapping({"/seller/queryCustomerData.htm"})
    public ModelAndView spare_goods(HttpServletRequest request, HttpServletResponse response,String currentPage){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/queryCustomerData.html",
            this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        
        String url = this.configService.getSysConfig().getAddress();
        Long userID=SecurityUserHolder.getCurrentUser().getId();
        
        Map params = new HashMap();
        params.put("id", userID);
        List userName = this.userService.query(
                "select userName from User obj where obj.id=:id", params,
                -1, -1);
        
        CustomerDataQueryObject qo = new CustomerDataQueryObject(currentPage, mv,"addTime", "desc");
        qo.addQuery("obj.userName",new SysMap("userName",userName.get(0)), "=");
        qo.setOrderBy("addTime");
        qo.setOrderType("desc");
        IPageList pList = this.customerDataService.list(qo);
        List<CustomerData> customerDatas= pList.getResult();
        Map map = new HashMap();
        if (customerDatas!=null) {
        	 for (CustomerData customerData : customerDatas) {
     			map.put("userName",customerData.getCustomerName());
     			List<Authentication> auths=this.authenticationService.query("select obj from Authentication obj where obj.userName=:userName", map, -1, -1);
     		customerData.setAuth(auths.get(0).getEnterpriseName());
        	 }
		}
        CommUtil.saveIPageList2ModelAndView(url +"/seller/queryCustomerData.htm","", "", pList, mv);
        return mv;
    }
	
	@SecurityMapping(display = false, rsequence = 0, title = "药房资料导入", value = "/seller/DataImport.htm*", rtype = "seller", rname = "客户管理", rcode = "client_management_seller", rgroup = "客户管理")
    @RequestMapping({"/seller/DataImport.htm"})
    public ModelAndView DataImport(HttpServletRequest request, HttpServletResponse response){
		 ModelAndView mv = new JModelAndView(
		            "user/default/usercenter/DataImport.html",
		            this.configService.getSysConfig(),
		            this.userConfigService.getUserConfig(), 0, request, response);
		        mv.addObject("userid",SecurityUserHolder.getCurrentUser().getId());
        return mv;
    }
	
	/**
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(display = false, rsequence = 0, title = "药房资料上传提交", value = "/seller/PharmacyDataupLoad.htm*", rtype = "seller", rname = "客户管理", rcode = "client_management_seller", rgroup = "客户管理")
    @RequestMapping({"/seller/PharmacyDataupLoad.htm"})
    public ModelAndView PharmacyDataupLoad(HttpServletRequest request, HttpServletResponse response){
		 ModelAndView mv = new JModelAndView(
		            "user/default/usercenter/success.html",
		            this.configService.getSysConfig(),
		            this.userConfigService.getUserConfig(), 0, request, response);
		 
	    String path = request.getSession().getServletContext().getRealPath("") +File.separator + "csv";
	    try {
	    Map map = CommUtil.saveFileToServer(request, "yaofangziliao", path,"yaofangziliao.cvs", null);
	    if (!map.get("fileName").equals("")){
	        String csvFilePath = path + File.separator + "yaofangziliao.cvs";
	        CsvReader reader = new CsvReader(csvFilePath, '\t',
	                Charset.forName("UTF-8"));
	        reader.readHeaders();// 跳过模板版本号，version 1.00 
	        
	        int userName=0;
	        int customerName=1;
	        int pharmacyName=2;
	        int pharmacyAddress=3;
	        int business=4;
	        int DrugDistributionLicense=5;
	        int GSPCertificate=6;
	        int PurchaseOrder=7;
	        int IdCard=8;
	        reader.readRecord();// 跳过英文标题
	        reader.readRecord();// 跳过中文标题
	        
	        String line=null;
	        while (reader.readRecord()){
	        	line=reader.get(userName);
	        	String item[] = line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分
	        	CustomerData customerData = new CustomerData();
	        	customerData.setUserName(item[userName]);
	        	customerData.setCustomerName(item[customerName]);
	        	customerData.setPharmacyName(item[pharmacyName]);
	        	customerData.setPharmacyAddress(item[pharmacyAddress]);
	        	customerData.setBusiness(item[business]);
	        	customerData.setDrugDistributionLicense(item[DrugDistributionLicense]);
	        	customerData.setGSPCertificate(item[GSPCertificate]);
	        	customerData.setPurchaseOrder(item[PurchaseOrder]);
	        	customerData.setIdCard(item[IdCard]);
	        	
	        	SimpleDateFormat sdf=new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
	        	customerData.setAddTime(sdf.parse(sdf.format(new Date())));
	        	this.customerDataService.save(customerData);
	        	
	        	mv.addObject("url", CommUtil.getURL(request) +"/seller/index.htm");
	        	mv.addObject("op_title", "药房资料信息上传成功");
	        }
	    }
        } catch (Exception e){
            e.printStackTrace();
        }
         
        return mv;//"redirect:queryCustomerData.htm";
    }
	
	/** 查询客户经理     **/
	@SecurityMapping(display = false, rsequence = 0, title = "关联客户经理", value = "/seller/relatedAccount.htm*", rtype = "seller", rname = "客户管理", rcode = "client_management_seller", rgroup = "客户管理")
    @RequestMapping({"/seller/relatedAccount.htm"})
    public ModelAndView relatedAccount(HttpServletRequest request, HttpServletResponse response,String seek){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/relatedAccount.html",
            this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        String url = this.configService.getSysConfig().getAddress();
        String userid=String.valueOf(SecurityUserHolder.getCurrentUser().getId());
        
    //  检索所有的客户
        if(seek=="1" || seek.equals("1")){
        	UserQueryObject qo = new UserQueryObject("", mv,"addTime", "desc");
            qo.addQuery("obj.userRole",new SysMap("userRole","BUYER"), "=");
            qo.setOrderBy("addTime");
            qo.setOrderType("desc");
            IPageList pList = this.userService.list(qo);
            CommUtil.saveIPageList2ModelAndView(url +"/seller/relatedAccount.htm","", "", pList, mv);
        }
        else{//搜索   单条的  客户经理 
        	Map params = new HashMap();
            params.put("userName", seek);
            params.put("userRole", "BUYER");
            List userNameList = this.userService.query(
                    "select userName from User obj where obj.userName=:userName and obj.userRole=:userRole", params,
                    -1, -1);
            String userName=userNameList.get(0).toString();
            mv.addObject("userName", userName);
        }
        mv.addObject("userid", userid);
        return mv;
    }
	
	/** 关联客户经理 提交 **/
	@SecurityMapping(display = false, rsequence = 0, title = "关联客户经理提交", value = "/seller/SaveRelatedAccount.htm*", rtype = "seller", rname = "客户管理", rcode = "client_management_seller", rgroup = "客户管理")
    @RequestMapping({"/seller/SaveRelatedAccount.htm"})
    public ModelAndView SaveRelatedAccount(HttpServletRequest request, HttpServletResponse response,String customerMmanager){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/success.html",
            this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        String userid=String.valueOf(SecurityUserHolder.getCurrentUser().getId());
        try{
        	 RelatedAccount rela=new RelatedAccount();
             rela.setUserID(userid);
             rela.setCustomerManager(customerMmanager);
             
             SimpleDateFormat sdf=new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
             rela.setAddTime(sdf.parse(sdf.format(new Date())));
             this.relatedAccountService.save(rela);
             mv.addObject("url", CommUtil.getURL(request) +"/seller/index.htm");
	         mv.addObject("op_title", "关联客户经理成功！");
        }catch(Exception e){
        	e.printStackTrace();
        	mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
        	mv.addObject("op_title", "关联客户经理失败！");
        	mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
        }
        return mv;
    }

}
