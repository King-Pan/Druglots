package com.wemall.manage.admin.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wemall.core.annotation.SecurityMapping;
import com.wemall.core.mv.JModelAndView;
import com.wemall.core.query.PageList;
import com.wemall.core.query.support.IPageList;
import com.wemall.core.security.support.SecurityUserHolder;
import com.wemall.core.tools.CommUtil;
import com.wemall.core.tools.HttpClass;
import com.wemall.core.tools.WebForm;
import com.wemall.foundation.domain.*;
import com.wemall.foundation.domain.query.AuthenticationQueryObject;
import com.wemall.foundation.service.*;
import com.wemall.foundation.service.impl.StoreServiceImpl;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 管理员 首营资料管理控制器
 */
@Controller
public class AuthenticationManageAction {

	@Autowired
	private ITemplateService templateService;

	@Autowired
	private IMessageService messageService;

	@Autowired
	private IUserService userService;

	@Autowired
	private ISysConfigService configService;

	@Autowired
	private IUserConfigService userConfigService;

	@Autowired
	private AuthenticationService authenticationService;
	
	@Autowired
    private IStoreService storeService;

	@SecurityMapping(display = false, rsequence = 0, title = "首营资料管理", value = "/admin/authentication_list.htm*", rtype = "admin", rname = "首营资料管理", rcode = "admin_authentication_set", rgroup = "首营资料")
	@RequestMapping({ "/admin/authentication_list.htm" })
	public ModelAndView authentication_list(HttpServletRequest request, HttpServletResponse response,
			String currentPage) {
		ModelAndView mv = new JModelAndView("/admin/blue/authentication_list.html", this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		HttpClass hc = new HttpClass();
		try{
        	AuthenticationQueryObject qo = new AuthenticationQueryObject(currentPage, mv,"addTime", "desc");
            qo.setOrderBy("addTime");
            qo.setOrderType("desc");
            IPageList pList = this.authenticationService.list(qo);
            CommUtil.saveIPageList2ModelAndView(url +"/admin/blue/authentication_list.html","", "", pList, mv);
           }catch(Exception e){
        	e.printStackTrace();
        	mv = new JModelAndView("admin/blue/error.html", this.configService.getSysConfig(), 
        			this.userConfigService.getUserConfig(), 0, request, response);
        	mv.addObject("op_title", "首营资料列表获取失败！");
        	mv.addObject("url", CommUtil.getURL(request) + "/admin/welcome.htm");
        }
        return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "获取首营详细资料", value = "/admin/queryAuthentication.htm*", rtype = "admin", rname = "首营资料管理", rcode = "admin_authentication_set", rgroup = "首营资料")
	@RequestMapping({ "/admin/queryAuthentication.htm" })
	public ModelAndView queryAuthentication(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("admin/blue/queryAuthentication.html", this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		HttpClass hc = new HttpClass();

		try {
			// Authentication
			// authentication=authenticationService.getObjById(Long.valueOf(id));
			String Load = hc.load("http://127.0.0.1:8081/ssm_project/selectid", "id="+id);
			Authentication authentication = JSON.parseObject(Load, Authentication.class);
			mv.addObject("authentication", authentication);
		} catch (Exception e) {
			e.printStackTrace();
			mv = new JModelAndView("admin/blue/error.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "首营详细资料获取失败！");
			mv.addObject("url", CommUtil.getURL(request) + "/admin/welcome.htm");
		}
		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "首营资料审核提交", value = "/admin/authentication_save.htm*", rtype = "admin", rname = "首营资料管理", rcode = "admin_authentication_set", rgroup = "首营资料")
	@RequestMapping({ "/admin/authentication_save.htm" })
	public ModelAndView authentication_save(HttpServletRequest request, HttpServletResponse response, String id,
			Integer examine, String un) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html", this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		// 调用修改接口修改数据
		HttpClass hc = new HttpClass();
		try {
			String Load = hc.load("http://127.0.0.1:8081/ssm_project/selectid", "id="+id);
			Authentication authentication = JSON.parseObject(Load, Authentication.class);
			authentication.setExamine(examine);
			//authenticationService.update(authentication);
			
			try{
				String s = JSON.toJSONString(authentication);
				
				String fh = hc.load(
						"http://127.0.0.1:8081/ssm_project/Updateobj",
						"duixiang=" + s);
				} catch (Exception e) {
					e.printStackTrace();
					mv = new JModelAndView("admin/blue/error.html", this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 0, request, response);
					mv.addObject("op_title", "首营详细更新失败！");
					mv.addObject("url", CommUtil.getURL(request) + "/admin/welcome.htm");
				}
			
			//卖家
			User user1=this.userService.getObjById(Long.valueOf(authentication.getUserId()));
	        if (user1.getStore() != null) {
	        	//首营资料审核成功自动开通店铺
				if (examine == 2) {
					User  user=this.userService.getObjById(Long.valueOf(authentication.getUserId()));
					if (user.getStore().getId() != null) {
					Store store = this.storeService.getObjById(user.getStore().getId());
					String fh = hc.load(
							"http://127.0.0.1:8081/ssm_project/UpdateStore","store_status="+ 2 +"&"+"id="+store.getId());
					}
				  }
	        	// 向用户发送短信
				Integer ex = examine;
				String content = "";
				if (ex == 2) {
					content = "您的店铺" + user1.getStore().getStore_name()  + "，首营认证已通过审核！";
				}
				if (ex == 3) {
					content = "您的店铺" + user1.getStore().getStore_name()  + "，首营认证审核不通过，请重新提交资料！";
				}
						Message msg = new Message();
						msg.setAddTime(new Date());
						Whitelist whiteList = new Whitelist();
						content = content.replaceAll("\n", "iskyhop_br");
						msg.setContent(Jsoup.clean(content, Whitelist.basic()).replaceAll("iskyhop_br", "\n"));
						msg.setFromUser(SecurityUserHolder.getCurrentUser());
						msg.setToUser(user1);
						msg.setType(1);
						this.messageService.save(msg);
					
				}else {
					// 向用户发送短信
					Integer ex = examine;
					String content = "";
					if (ex == 2) {
						content = "您的首营认证已通过审核！";
					}
					if (ex == 3) {
						content = "您的首营认证审核不通过，请重新提交资料！";
					}
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
			
	        

			if (examine == 2) {
			mv.addObject("op_title", "首营资料审核成功！");
			mv.addObject("list_url", CommUtil.getURL(request) + "/admin/authentication_list.htm");
			}else {
				mv = new JModelAndView("admin/blue/error.html", this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request, response);
				mv.addObject("op_title", "首营资料审核失败！");
				mv.addObject("url", CommUtil.getURL(request) + "/admin/welcome.htm");
			}
			
		
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return mv;
	}

	@SecurityMapping(display = false, rsequence = 0, title = "首营资料删除", value = "/admin/authentication_del.htm*", rtype = "admin", rname = "首营资料管理", rcode = "admin_authentication_set", rgroup = "首营资料_删除")
	@RequestMapping({ "/admin/authentication_del.htm" })
	public ModelAndView authentication_del(HttpServletRequest request, HttpServletResponse response, String mulitId) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html", this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		try {
			String[] ids = mulitId.split(",");
			if (ids.length > 0) {
				for (String id : ids) {
					authenticationService.delete(Long.valueOf(id));
				}
			}
			mv.addObject("op_title", "首营资料删除成功！");
			mv.addObject("list_url", CommUtil.getURL(request) + "/admin/authentication_list.htm");
		} catch (Exception e) {
			e.printStackTrace();
			mv = new JModelAndView("admin/blue/error.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			mv.addObject("op_title", "首营资料删除失败！");
			mv.addObject("url", CommUtil.getURL(request) + "/admin/welcome.htm");
		}
		return mv;
	}

	/*
	 * private void send_site_msg(HttpServletRequest request, String mark,
	 * Authentication authentication) throws Exception {
	 * com.wemall.foundation.domain.Template template =
	 * this.templateService.getObjByProperty("mark", mark); if
	 * (template.isOpen()){ String path = request.getRealPath("/") + "/vm/";
	 * PrintWriter pwrite = new PrintWriter( new OutputStreamWriter(new
	 * FileOutputStream(path + "msg.vm", false), "UTF-8"));
	 * pwrite.print(template.getContent()); pwrite.flush(); pwrite.close();
	 * 
	 * Properties p = new Properties();
	 * p.setProperty("file.resource.loader.path", request .getRealPath("/") +
	 * "vm" + File.separator); p.setProperty("input.encoding", "UTF-8");
	 * p.setProperty("output.encoding", "UTF-8"); Velocity.init(p);
	 * org.apache.velocity.Template blank = Velocity.getTemplate("msg.vm",
	 * "UTF-8"); VelocityContext context = new VelocityContext();
	 * context.put("reason", authentication);
	 * context.put("user",authentication.getUserName()); context.put("config",
	 * this.configService.getSysConfig()); context.put("send_time",
	 * CommUtil.formatLongDate(new Date())); StringWriter writer = new
	 * StringWriter(); blank.merge(context, writer); String content =
	 * writer.toString(); User fromUser =
	 * this.userService.getObjByProperty("userName", "admin"); Message msg = new
	 * Message(); msg.setAddTime(new Date()); msg.setContent(content);
	 * msg.setFromUser(fromUser); msg.setTitle(template.getTitle());
	 * msg.setToUser(store.getUser()); msg.setType(0);
	 * this.messageService.save(msg); CommUtil.deleteFile(path + "msg.vm");
	 * writer.flush(); writer.close(); } }
	 */

}
