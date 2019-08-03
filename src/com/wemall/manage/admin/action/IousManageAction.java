package com.wemall.manage.admin.action;

import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.wemall.core.annotation.SecurityMapping;
import com.wemall.core.mv.JModelAndView;
import com.wemall.core.query.support.IPageList;
import com.wemall.core.security.support.SecurityUserHolder;
import com.wemall.core.tools.CommUtil;
import com.wemall.foundation.domain.Authentication;
import com.wemall.foundation.domain.Message;
import com.wemall.foundation.domain.User;
import com.wemall.foundation.domain.query.AuthenticationQueryObject;
import com.wemall.foundation.service.AuthenticationService;
import com.wemall.foundation.service.IMessageService;
import com.wemall.foundation.service.ISysConfigService;
import com.wemall.foundation.service.IUserConfigService;
import com.wemall.foundation.service.IUserService;

/**
 * 
 * @author Administrator
 * 管理员
 * 白条认证资料管理控制器
 *
 */
@Controller
public class IousManageAction {
	@Autowired
    private IUserService userService;
    @Autowired
    private IMessageService messageService;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private AuthenticationService authenticationService;
	@SecurityMapping(display = false, rsequence = 0, title = "白条资料管理", value = "/admin/ious_list.htm*", rtype = "admin", rname = "白条资料管理", rcode = "admin_ious_set", rgroup = "白条资料")
	@RequestMapping({"/admin/ious_list.htm"})
	public ModelAndView Ious_list(HttpServletRequest request,HttpServletResponse response,String currentPage){
		ModelAndView mv=new JModelAndView("admin/blue/ious_list.html",this.configService.getSysConfig(), this.userConfigService
                .getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		try{
        	AuthenticationQueryObject qo = new AuthenticationQueryObject(currentPage, mv,"addTime", "desc");
            qo.setOrderBy("addTime");
            qo.setOrderType("desc");
            IPageList pList = this.authenticationService.list(qo);
            CommUtil.saveIPageList2ModelAndView(url +"/admin/blue/ious_list.html","", "", pList, mv);
        }catch(Exception e){
        	e.printStackTrace();
        	mv = new JModelAndView("admin/blue/error.html", this.configService.getSysConfig(), 
        			this.userConfigService.getUserConfig(), 0, request, response);
            
        	mv.addObject("op_title", "白条资料列表获取失败！");
        	mv.addObject("url", CommUtil.getURL(request) + "/admin/welcome.htm");
        }
        return mv;
		
		
	}
	@SecurityMapping(display=false,rsequence=0,title="获取白条详细资料",value="/admin/queryIous.htm*",rtype="admin",rname="白条资料管理",rcode="admin_authentication_set",rgroup="白条资料")
	@RequestMapping({"/admin/queryIous.htm"})
	public ModelAndView queryIous(HttpServletRequest request,HttpServletResponse response,String id){
		ModelAndView mv=new JModelAndView("admin/blue/queryIous.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request,response);
		String url=this.configService.getSysConfig().getAddress();
		try{
			Authentication authentication=authenticationService.getObjById(Long.valueOf(id));
			mv.addObject("authentication",authentication);
		}catch(Exception e){
			e.printStackTrace();
			mv=new JModelAndView("admin/blue/error.html",this.configService.getSysConfig(),this.userConfigService.getUserConfig(),0,request,response);
			mv.addObject("op_title", "白条详细资料获取失败!");
			mv.addObject("url", CommUtil.getURL(request)+"/admin/welcome.htm");
		}
		
		return mv;
	}
	
	 @SecurityMapping(display = false, rsequence = 0, title = "白条资料审核提交", value = "/admin/ious_save.htm*", rtype = "admin", rname = "白条资料管理", rcode = "admin_ious_set", rgroup = "白条资料")
	    @RequestMapping({"/admin/ious_save.htm"})
	    public ModelAndView authentication_save(HttpServletRequest request, HttpServletResponse response,String id,String temiiOpeningStatus, String un){
	        ModelAndView mv = new JModelAndView("admin/blue/success.html",
	                                            this.configService.getSysConfig(), this.userConfigService
	                                            .getUserConfig(), 0, request, response);
	        String url = this.configService.getSysConfig().getAddress();
	        
	        try{
	        	Authentication authentication=authenticationService.getObjById(Long.valueOf(id));
	        	authentication.setTemiiOpeningStatus(Integer.valueOf(temiiOpeningStatus));
	        	authenticationService.update(authentication);
	        	Integer to = Integer.valueOf(temiiOpeningStatus);
	            String content = "";
	            if (to==2) {
	            	content="尊敬的"+un+",您的白条认证资料已经审核成功";
	    		}
	            if (to==3) {
	            	content="尊敬的"+un+",您的白条认证资料审核失败,请联系管理员";
	    		}
	            if (to!=null && content !="") {
	            	User toUser = this.userService.getObjByProperty("userName",un);
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
	        	
	        	mv.addObject("op_title", "白条资料审核成功！");
	        	mv.addObject("list_url", CommUtil.getURL(request) +"/admin/ious_list.htm");
	        }catch(Exception e){
	        	e.printStackTrace();
	        	mv = new JModelAndView("admin/blue/error.html", this.configService.getSysConfig(), 
	        			this.userConfigService.getUserConfig(), 0, request, response);
	        	mv.addObject("op_title", "白条资料审核失败！");
	        	mv.addObject("url", CommUtil.getURL(request) + "/admin/welcome.htm");
	        }
	        return mv;
	    }
	    @SecurityMapping(display = false, rsequence = 0, title = "白条资料删除", value = "/admin/ious_del.htm*", rtype = "admin", rname = "白条资料管理", rcode = "admin_ious_set", rgroup = "白条资料_删除")
	    @RequestMapping({"/admin/ious_del.htm"})
	    public ModelAndView authentication_del(HttpServletRequest request, HttpServletResponse response,String mulitId){
	        ModelAndView mv = new JModelAndView("admin/blue/success.html",
	                                            this.configService.getSysConfig(), this.userConfigService
	                                            .getUserConfig(), 0, request, response);
	        String url = this.configService.getSysConfig().getAddress();
	        try {
	        	String[] ids = mulitId.split(",");
	        	if (ids.length>0){
	        		for (String id : ids){
	        			authenticationService.delete(Long.valueOf(id));
	        		}
	        	}
	        	mv.addObject("op_title", "白条资料删除成功！");
	        	mv.addObject("list_url", CommUtil.getURL(request) +"/admin/ious_list.htm");
	        }catch (Exception e){
	        	e.printStackTrace();
	        	mv = new JModelAndView("admin/blue/error.html", this.configService.getSysConfig(), 
	        			this.userConfigService.getUserConfig(), 0, request, response);
	        	mv.addObject("op_title", "白条资料删除失败！");
	        	mv.addObject("url", CommUtil.getURL(request) + "/admin/welcome.htm");
	        }
	        return mv;
	    }
}
