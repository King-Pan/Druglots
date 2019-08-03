package com.wemall.view.web.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.HttpException;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.hp.hpl.sparta.xpath.ThisNodeTest;
import com.wemall.core.mv.JModelAndView;
import com.wemall.core.security.support.SecurityUserHolder;
import com.wemall.core.tools.CommUtil;
import com.wemall.core.tools.HttpClass;
import com.wemall.core.tools.Md5Encrypt;
import com.wemall.foundation.domain.Album;
import com.wemall.foundation.domain.Authentication;
import com.wemall.foundation.domain.IOU;
import com.wemall.foundation.domain.IntegralLog;
import com.wemall.foundation.domain.Message;
import com.wemall.foundation.domain.Store;
import com.wemall.foundation.domain.User;
import com.wemall.foundation.service.AuthenticationService;
import com.wemall.foundation.service.IAlbumService;
import com.wemall.foundation.service.IAreaService;
import com.wemall.foundation.service.IGoodsService;
import com.wemall.foundation.service.IIntegralLogService;
import com.wemall.foundation.service.IMessageService;
import com.wemall.foundation.service.IOUService;
import com.wemall.foundation.service.IRoleService;
import com.wemall.foundation.service.IStoreClassService;
import com.wemall.foundation.service.IStoreGradeService;
import com.wemall.foundation.service.IStoreService;
import com.wemall.foundation.service.ISysConfigService;
import com.wemall.foundation.service.IUserConfigService;
import com.wemall.foundation.service.IUserService;
import com.wemall.uc.api.UCClient;
import com.wemall.uc.api.UCTools;
import com.wemall.view.web.tools.ImageViewTools;
import com.wemall.view.web.tools.StoreViewTools;

/**
 * 买家登录控制器
 */
@Controller
public class LoginViewAction {
	

	 //产品名称:云通信短信API产品,开发者无需替换
   static final String product = "Dysmsapi";
   //产品域名,开发者无需替换
   static final String domain = "dysmsapi.aliyuncs.com";

   // TODO 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
   static final String accessKeyId = "LTAIsU4u7y975Kq7";
   static final String accessKeySecret = "8zajvrMGXtXmhU05iE7ZXblf9c8EcT";
   
   @Autowired
   private ISysConfigService configService;
   
	@Autowired
    private IAreaService areaService;
	
	@Autowired
	private IStoreClassService storeClassService;
   

   @Autowired
   private IUserConfigService userConfigService;

   @Autowired
   private IStoreGradeService storeGradeService;


   @Autowired
   private IStoreService storeService;

   @Autowired
   private IUserService userService;

   @Autowired
   private IRoleService roleService;

   @Autowired
   private StoreViewTools storeTools;

   @Autowired
   private AuthenticationService authenticationService;


   @Autowired
   private IOUService iouService;
   

   @Autowired
   private IIntegralLogService integralLogService;

   @Autowired
   private IAlbumService albumService;

   @Autowired
   private ImageViewTools imageViewTools;

   @Autowired
   private UCTools ucTools;
   
	@Autowired
    private IMessageService messageService;

    /**
     * 用户登录跳转页面
     * @param request
     * @param response
     * @param url
     * @return
     */
    @RequestMapping({"/user/login.htm"})
    public ModelAndView login(HttpServletRequest request, HttpServletResponse response, String url){
        ModelAndView mv = new JModelAndView("login.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);

        String wemall_view_type = CommUtil.null2String(request.getSession(false).getAttribute("wemall_view_type"));

        if ((wemall_view_type != null) && (!wemall_view_type.equals("")) && (wemall_view_type.equals("wap"))){
            mv = new JModelAndView("/wap/login.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
        }

        request.getSession(false).removeAttribute("verify_code");
        boolean domain_error = CommUtil.null2Boolean(request.getSession(false).getAttribute("domain_error"));
        if ((url != null) && (!url.equals(""))){
            request.getSession(false).setAttribute("refererUrl", url);
        }
        if (domain_error){
            mv = new JModelAndView("error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
            if ((wemall_view_type != null) && (!wemall_view_type.equals("")) && (wemall_view_type.equals("wap"))){
                mv = new JModelAndView("wap/error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
            }
        }else{
            mv.addObject("imageViewTools", this.imageViewTools);
        }
        mv.addObject("uc_logout_js", request.getSession(false).getAttribute("uc_logout_js"));

        return mv;
    }

    /**
     * 注册跳转页面
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({"/register.htm"})
    public ModelAndView register(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("register.html", this.configService.getSysConfig(),
                                            this.userConfigService.getUserConfig(), 1, request, response);

        String wemall_view_type = CommUtil.null2String(request.getSession(false).getAttribute("wemall_view_type"));

        if ((wemall_view_type != null) && (!wemall_view_type.equals("")) && (wemall_view_type.equals("wap"))){
            mv = new JModelAndView("wap/register.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
        }
        request.getSession(false).removeAttribute("verify_code");

        return mv;
    }

    /**
     * 注册保存
     * @param request
     * @param response
     * @param userName
     * @param password
     * @param email
     * @param code
     * @return
     * @throws HttpException
     * @throws IOException
     */
    @RequestMapping({"/register_finish.htm"})
    public String register_finish(HttpServletRequest request, HttpServletResponse response, String userName, String password, String email, String code,String phone,String phonecode,String difference )
    throws HttpException, IOException {
    	boolean reg = true;
       /* // 判断验证码
        if ((code != null) && (!code.equals(""))){
            code = CommUtil.filterHTML(code);
        }
        if (this.configService.getSysConfig().isSecurityCodeRegister()){
            if (!request.getSession(false).getAttribute("verify_code").equals(code)){
                reg = false;
            }
        }*/

        // 用户名不能重复
        Map params = new HashMap();
        params.put("userName", userName);
        List users = this.userService.query("select obj from User obj where obj.userName=:userName", params, -1, -1);
        if ((users != null) && (users.size() > 0)){
            reg = false;
        }
        if (phone==null || phone=="") {
        	reg = false;
		}
        String s= (String) request.getSession().getAttribute(userName);
        if (s==null || !s.equals(phonecode)) {
        	reg = false;
		}
     //判断数据库中用户是否已经存在
     /*  User user1=(User)users.get(0);
       if (s.equals(user1.getUserName())&&user1.getUserName()!=null) {
       mv =new JModelAndView("error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
       mv.addObject("op_title", "用户名已存在");
       mv.addObject("url", CommUtil.getURL(request) + "/register.htm");
		}
    */
	

        if (reg){
            User user = new User();
            user.setUserName(userName);
            user.setUserRole("BUYER");
            user.setAddTime(new Date());
            user.setEmail(email);
            user.setPassword(Md5Encrypt.md5(password).toLowerCase());
            user.setMobile(userName);
            //将商业公司与终端放到user中
            if (difference!=null) {
            	 user.setDifference(Integer.parseInt(difference));
			}else{
				user.setDifference(0);
			}
            //结束
            params.clear();
            params.put("type", "BUYER");
            List roles = this.roleService.query("select obj from Role obj where obj.type=:type", params, -1, -1);
            
            /**/
            IOU iou = new IOU();
            iou.setUserName(userName);
            iou.setTemiiOpeningStatus(0);
            
            /**/
            user.getRoles().addAll(roles);

            // 如果系统开启积分功能，则给会员新增积分
            if (this.configService.getSysConfig().isIntegral()){
                user.setIntegral(this.configService.getSysConfig().getMemberRegister());
                this.userService.save(user);
                IntegralLog log = new IntegralLog();
                log.setAddTime(new Date());
                log.setContent("用户注册增加" + this.configService.getSysConfig().getMemberRegister() + "分");
                log.setIntegral(this.configService.getSysConfig().getMemberRegister());
                log.setIntegral_user(user);
                log.setType("reg");
                this.integralLogService.save(log);
            }else{
                this.userService.save(user);
            }
            
            /*不写就保存保存邀请码*/
            HttpClass hc = new HttpClass();
          /* try {
            	 初始化赊销数据开始
				hc.load("http://127.0.0.1:8081/ssm_project/insStatus", "userName="+userName);
				初始化赊销数据结束
			} catch (Exception e) {
				
			}*/
            try {
				hc.load("http://127.0.0.1:8081/ssm_project/invitationcunuse", "phone="+phone+"&"+"name="+userName);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            // 设置相册
            Album album = new Album();
            album.setAddTime(new Date());
            album.setAlbum_default(true);
            album.setAlbum_name("默认相册");
            album.setAlbum_sequence(-10000);
            album.setUser(user);
            this.albumService.save(album);

            request.getSession(false).removeAttribute("verify_code");

            // UC会员同步
            if (this.configService.getSysConfig().isUc_bbs()){
                UCClient client = new UCClient();
                String ret = client.uc_user_register(userName, password, email);
                int uid = Integer.parseInt(ret);
                if (uid <= 0){
                    if (uid == -1)
                        System.out.print("用户名不合法");
                   else if (uid == -2)
                        System.out.print("包含要允许注册的词语");
                   else if (uid == -3)
                        System.out.print("用户名已经存在");
                   else if (uid == -4)
                        System.out.print("Email 格式有误");
                   else if (uid == -5)
                        System.out.print("Email 不允许注册");
                   else if (uid == -6)
                        System.out.print("该 Email 已经被注册");
                    else
                        System.out.print("未定义");
                }else{
                    this.ucTools.active_user(userName, user.getPassword(), email);
                }
            }
          return "redirect:wemall_login.htm?username=" +
                   CommUtil.encode(userName) + "&password=" + password + "&encode=true";
        }
        return "redirect:register.htm";
    }

    /**
     * 登录操作成功之后跳转判断
     * @param request
     * @param response
     * @return
     * @throws ParseException 
     */
    @RequestMapping({"/user_login_success.htm"})
    public ModelAndView user_login_success(HttpServletRequest request, HttpServletResponse response) throws ParseException{
    	String btnstatus="0";
        ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        String url = CommUtil.getURL(request) + "/index.htm";
        String wemall_view_type = CommUtil.null2String(request.getSession(false).getAttribute("wemall_view_type"));
        //跳转到微信端
        if ((wemall_view_type != null) && (!wemall_view_type.equals("")) && (wemall_view_type.equals("wap"))){
            String store_id = CommUtil.null2String(request.getSession(false).getAttribute("store_id"));
            mv = new JModelAndView("wap/success.html",
                                   this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
            url = CommUtil.getURL(request) + "/wap/index.htm?store_id=" + store_id;
        }
        HttpSession session = request.getSession(false);
        if ((session.getAttribute("refererUrl") != null) &&
                (!session.getAttribute("refererUrl").equals(""))){
            url = (String)session.getAttribute("refererUrl");
            session.removeAttribute("refererUrl");
        }
        if (this.configService.getSysConfig().isUc_bbs()){
            String uc_login_js = CommUtil.null2String(request.getSession(false).getAttribute("uc_login_js"));
            mv.addObject("uc_login_js", uc_login_js);
        }
        //第三方登录：QQ、新浪等
        String bind = CommUtil.null2String(request.getSession(false).getAttribute("bind"));
        User user = SecurityUserHolder.getCurrentUser();
        if (!bind.equals("")){
            mv = new JModelAndView(bind + "_login_bind.html",
                                   this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("user", user);
            request.getSession(false).removeAttribute("bind");
        }
        mv.addObject("op_title", "登录成功");
        mv.addObject("url", url);
        //终端药房
        //判断买家是否通过首营资料
        Map params = new HashMap();
        params.put("buyerId", SecurityUserHolder.getCurrentUser().getUserName());
        List list = this.authenticationService.query(
                         "select obj from Authentication obj where obj.userName =:buyerId", params, -1, -1);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date m = new Date();
        //买家
        if (user.getDifference()==1) {
        	//首营认证没有
        	if (list.size()==0) {
            	mv = new JModelAndView("success.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
       		    mv.addObject("current_url", "success.html");
                mv.addObject("op_title", "必须认证，才可采购");
                mv.addObject("url", CommUtil.getURL(request) + "/buyer/TheFirstCamp.htm");
        	}else {
                //判断首营资质是否过期
                for (int i = 0; i < list.size(); i++) {
                	Authentication authentication=(Authentication) list.get(i);
    				int Businessdays = (int) ((df.parse(authentication.getEndBusinessDate()).getTime() - m.getTime()) / (1000*3600*24));
    				int Drugdays = (int) ((df.parse(authentication.getEndDrugDate()).getTime() - m.getTime()) / (1000*3600*24));
    				int GSPdays = (int) ((df.parse(authentication.getEndGSPDate()).getTime() - m.getTime()) / (1000*3600*24));
    				int IDcarddays = (int) ((df.parse(authentication.getEndIDcardDate()).getTime() - m.getTime()) / (1000*3600*24));
    				if(GSPdays<0 || IDcarddays<0 || Businessdays<0 || Drugdays<0){
    				//已过期站内短信
					String content1 = "您的首营认证已过期！无法继续采购！请重新提交。";
					Message msg1 = new Message();
					msg1.setAddTime(new Date());
					Whitelist whiteList1 = new Whitelist();
					content1 = content1.replaceAll("\n", "iskyhop_br");
					msg1.setContent(Jsoup.clean(content1, Whitelist.basic()).replaceAll("iskyhop_br", "\n"));
					msg1.setFromUser(SecurityUserHolder.getCurrentUser());
					msg1.setToUser(SecurityUserHolder.getCurrentUser());
					msg1.setType(1);
					this.messageService.save(msg1);
					mv = new JModelAndView("error.html",
                            this.configService.getSysConfig(),
                            this.userConfigService.getUserConfig(), 1, request,
                            response);
		             mv.addObject("op_title", "您的资质已过期，请更新资质");
		             mv.addObject("url", CommUtil.getURL(request) + "/buyer/TheFirstCamp.htm");
		             
    				}else if(GSPdays<=30 || IDcarddays<=30 || Businessdays<=30 || Drugdays <= 30) {
					//即将过期站内短信
					String content1 = "您的首营认证即将过期！过期将无法采购，请尽快重新提交。";
					Message msg1 = new Message();
					msg1.setAddTime(new Date());
					Whitelist whiteList1 = new Whitelist();
					content1 = content1.replaceAll("\n", "iskyhop_br");
					msg1.setContent(Jsoup.clean(content1, Whitelist.basic()).replaceAll("iskyhop_br", "\n"));
					msg1.setFromUser(SecurityUserHolder.getCurrentUser());
					msg1.setToUser(SecurityUserHolder.getCurrentUser());
					msg1.setType(1);
					this.messageService.save(msg1);
    				}
                }
			}
        	 return mv;
		}
    

        //卖家
        if (user.getDifference()==2) {
        	Map map = new HashMap();
			map.put("userName", SecurityUserHolder.getCurrentUser().getUserName());
            List authenticationList=this.authenticationService.query(
            		"select obj from Authentication obj where obj.userName=:userName", map, -1, -1);
            User  user1=this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
			Store store = user1.getStore();
        	if (store != null) {
            //判断首营资质是否过期
            for (int i = 0; i < authenticationList.size(); i++) {
            	Authentication authentication=(Authentication) authenticationList.get(i);
				int Businessdays = (int) ((df.parse(authentication.getEndBusinessDate()).getTime() - m.getTime()) / (1000*3600*24));
				int Drugdays = (int) ((df.parse(authentication.getEndDrugDate()).getTime() - m.getTime()) / (1000*3600*24));
				int GSPdays = (int) ((df.parse(authentication.getEndGSPDate()).getTime() - m.getTime()) / (1000*3600*24));
				int IDcarddays = (int) ((df.parse(authentication.getEndIDcardDate()).getTime() - m.getTime()) / (1000*3600*24));
				if(GSPdays<0 || IDcarddays<0 || Businessdays<0 || Drugdays<0){
					String content1 = "您的店铺" + store.getStore_name()  + "，首营认证已过期！店铺已关闭！请重新提交。";
					Message msg1 = new Message();
					msg1.setAddTime(new Date());
					Whitelist whiteList1 = new Whitelist();
					content1 = content1.replaceAll("\n", "iskyhop_br");
					msg1.setContent(Jsoup.clean(content1, Whitelist.basic()).replaceAll("iskyhop_br", "\n"));
					msg1.setFromUser(SecurityUserHolder.getCurrentUser());
					msg1.setToUser(user1);
					msg1.setType(1);
					this.messageService.save(msg1);
					if (store!=null) {
						store.setStore_status(6);
						this.storeService.update(store);
					}
				 }else if( GSPdays<=30 || IDcarddays<=30 || Businessdays<=30 || Drugdays <= 30){
					//即将过期发短信提示
					String content = "您的店铺" + store.getStore_name()  + "，首营认证即将过期！过期将被关店，请尽快重新提交。";
					Message msg = new Message();
					msg.setAddTime(new Date());
					Whitelist whiteList = new Whitelist();
					content = content.replaceAll("\n", "iskyhop_br");
					msg.setContent(Jsoup.clean(content, Whitelist.basic()).replaceAll("iskyhop_br", "\n"));
					msg.setFromUser(SecurityUserHolder.getCurrentUser());
					msg.setToUser(user1);
					msg.setType(1);
				    this.messageService.save(msg);
					
					}
				}
				
            }
          //店铺认证没有
        	//user.getStore()==null
        	if (store==null) {
        	 mv = new JModelAndView("success.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
    		    mv.addObject("current_url", "success.html");
             mv.addObject("op_title", "必须认证，才可成功开店");
             mv.addObject("url", CommUtil.getURL(request) + "/seller/store_create_second.htm?grade_id='1' & userName="+user.getUserName());
        	}else {
				if (store != null) {
        		if (store.getStore_status() == -1){
                    mv = new JModelAndView("error.html",
                                           this.configService.getSysConfig(),
                                           this.userConfigService.getUserConfig(), 1, request,
                                           response);
                    mv.addObject("op_title", "您的店铺审核被拒绝");
                    mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
                }
					
	                if (store.getStore_status() == 3){
	                    mv = new JModelAndView("error.html",
	                                           this.configService.getSysConfig(),
	                                           this.userConfigService.getUserConfig(), 1, request,
	                                           response);
	                    mv.addObject("op_title", "您的店铺因违规被关闭");
	                    mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
	                }
	                if (store.getStore_status() == 6){
	                    mv = new JModelAndView("error.html",
	                                           this.configService.getSysConfig(),
	                                           this.userConfigService.getUserConfig(), 1, request,
	                                           response);
	                    mv.addObject("op_title", "您的资质已过期，请更新资质");
	                    mv.addObject("url", CommUtil.getURL(request) + "/buyer/TheFirstCamp.htm");
	                }
	              }
        	}
        }
        HttpClass hc = new	HttpClass();
        try {
			String Load = hc.load("http://127.0.0.1:8081/ssm_project/select", "userName="+user.getUserName());
			if (Load!=null && Load!="") {
				Authentication auth = JSON.parseObject(Load, Authentication.class);
				int examine=auth.getExamine();
				if(examine==1){
					mv.addObject("current_url", "success.html");
					mv.addObject("op_title", "您的首营资料已提交，待审核");
					mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
					return mv;
				}
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
    /**
     * 登录模态窗口
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({"/user_dialog_login.htm"})
    public ModelAndView user_dialog_login(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("user_dialog_login.html", this.configService.getSysConfig(),
                                            this.userConfigService.getUserConfig(), 1, request, response);

        return mv;
    }


    /** wap登录业务逻辑begin */

    @RequestMapping({ "/user/wap/login.htm" })
    public ModelAndView waplogin(HttpServletRequest request, HttpServletResponse response, String url){
        ModelAndView mv = new JModelAndView("wap/login.html", this.configService.getSysConfig(),
                                            this.userConfigService.getUserConfig(), 1, request, response);
        request.getSession(false).removeAttribute("verify_code");

        boolean domain_error = CommUtil.null2Boolean(request.getSession(false).getAttribute("domain_error"));
        if ((url != null) && (!url.equals(""))){
            request.getSession(false).setAttribute("refererUrl", url);
        }
        if (domain_error)
            mv = new JModelAndView("wap/error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
       else{
            mv.addObject("imageViewTools", this.imageViewTools);
        }
        mv.addObject("uc_logout_js", request.getSession(false).getAttribute("uc_logout_js"));

        /*String wemall_view_type = CommUtil.null2String(request.getSession(false).getAttribute("wemall_view_type"));

        if ((wemall_view_type != null) && (!wemall_view_type.equals("")) && (wemall_view_type.equals("wap"))){
        	//String store_id = CommUtil.null2String(request.getSession(false).getAttribute("store_id"));
        	mv = new JModelAndView("wap/success.html", this.configService.getSysConfig(),
        			this.userConfigService.getUserConfig(), 1, request, response);
        	mv.addObject("op_title", "成功！");
        	mv.addObject("url", CommUtil.getURL(request) + "/wap/index.htm");
        }*/

        return mv;
    }
    
    /**
     * 特米白条激活
     * @param request
     * @param response
     * @param userName
     * @param password
     * @param email
     * @param code
     * @return
     * @throws HttpException
     * @throws IOException
     */
    @RequestMapping({"/temii_OpeningStatus.htm"})
    public ModelAndView temii_OpeningStatus(HttpServletRequest request, HttpServletResponse response, String certificate, String license, String GSPCertificate, String letterOfInstruction,String CopyOfIdCard)
    throws HttpException, IOException {
    	 ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        
         String userName = "zhangsan";
          mv.addObject("current_url", "success.html");
          mv.addObject("op_title", "特米白条激活成功！");
          mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
          
          Map params = new HashMap();
          List iouid= new ArrayList();
          params.put("userName", "qzh");
      	  params.put("temiiOpeningStatus", 1);
      	  iouid = this.iouService.query(
                   "select max(id) from IOU obj where obj.userName=:userName and obj.temiiOpeningStatus=:temiiOpeningStatus", params, -1, -1);
      	  long id=(Long) iouid.get(0);
          
          IOU iou =new IOU();
          iou.setId(id+1);
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
    
    /**
     * 点击发消息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({"/faxiaoxi.htm"})
    public void fa(HttpServletRequest request, HttpServletResponse response, final String userName,String templatecode,String storename,String ordernum){
    	System.out.println(userName);
    	if (userName!=null) {
    		try {
        		Random random = new Random();
        		String result="";
        		for (int i=0;i<6;i++)
        		{
        			result+=random.nextInt(10);
        		}
        		result=result.replaceFirst("0","1");
	    			SendSmsResponse res = sendSms(userName,result,templatecode,storename,ordernum);
	    			System.out.println(result);
    			 final HttpSession session = request.getSession();
    			  session.setAttribute(userName, result);
    			 final Timer timer=new Timer();
    	            timer.schedule(new TimerTask() {
    	                @Override
    	                public void run() {
    	                    session.removeAttribute(userName);
    	                    timer.cancel();
    	                }
    	            },5*60*1000);
    	
    		} catch (ClientException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
		}
    	

    }
    
    //发送短信方法templatecode为短信的模板，phone为发送的手机号码，result为验证码，storename为店铺名，ordernum为订单号
    public static SendSmsResponse sendSms(String phone,String result,String templatecode,String storename,String ordernum) throws ClientException {

        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);

        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号
        request.setPhoneNumbers(phone);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName("今朝药链");
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(templatecode);
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        //审核店铺的通知短信
        if (templatecode.equals("SMS_160860417")||templatecode.equals("SMS_160855380") || templatecode.equals("SMS_160860459") || templatecode.equals("SMS_160855386")) {
        	
			 request.setTemplateParam("{\"dianpumingcheng\":\""+storename+"\"}");
			 
		}else if (templatecode.equals("SMS_159230537")) {
			//修改手机号验证码短信
			request.setTemplateParam("{'code':"+result+"}");
			
		}else if (templatecode.equals("SMS_160855877")) {
			 request.setTemplateParam("{'dingdanhao':"+ordernum+"}");
		}else{
	        //验证码短信
	        request.setTemplateParam("{'yanzhengma':"+result+"}");
		}
        //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");

        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        request.setOutId("yourOutId");

        //hint 此处可能会抛出异常，注意catch
        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
        System.out.println(sendSmsResponse.getCode());
        return sendSmsResponse;
    }
    
    
    public int randomInt(int from, int to) {
        Random r = new Random();
        return from + r.nextInt(to - from);
    }
    
    @RequestMapping({ "/xiaoxi.htm" })
	public void faxiaoxi(HttpServletRequest request, HttpServletResponse response, final String userName,String templatecode,String storename,String ordernum) {
		System.out.println(userName);
		if (userName != null) {
			try {
				Random random = new Random();
				String result = "";
				for (int i = 0; i < 6; i++) {
					char c = (char) (randomInt(0, 10) + '0');
					result += String.valueOf(c);
				}
				result=result.replaceFirst("0","1");
				System.out.println(result+"======");
				SendSmsResponse res = sendSms(userName, result,templatecode,storename,ordernum);
				final HttpSession session = request.getSession();
				session.setAttribute("code", result);
				final Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						session.removeAttribute("code");
						timer.cancel();
					}
				}, 5 * 60 * 1000);
			} catch (Exception e) {//Client
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
	/**
	 * 验证用户是否存在，存在不可发送验证码
	 * @param request
	 * @param response
	 * @param userName
	 * @param id
	 */
	 @RequestMapping({"/checkUser.htm"})
	    public void getuser(HttpServletRequest request, HttpServletResponse response, String userName, String id){
	        boolean ret = true;
	        Map params = new HashMap();
	        params.put("userName", userName);
	        params.put("id", CommUtil.null2Long(id));
	        System.out.println(userName+"=====");
	        List users = this.userService
	                     .query("select obj from User obj where obj.userName=:userName and obj.id!=:id",
	                            params, -1, -1);
	        if ((users != null) && (users.size() > 0)){
	            ret = false;
	        }
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
}