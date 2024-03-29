package com.wemall.manage.seller.action;

import com.wemall.core.annotation.SecurityMapping;
import com.wemall.core.domain.virtual.SysMap;
import com.wemall.core.mv.JModelAndView;
import com.wemall.core.query.support.IPageList;
import com.wemall.core.security.support.SecurityUserHolder;
import com.wemall.core.tools.CommUtil;
import com.wemall.core.tools.WebForm;
import com.wemall.foundation.domain.Store;
import com.wemall.foundation.domain.Transport;
import com.wemall.foundation.domain.User;
import com.wemall.foundation.domain.query.TransportQueryObject;
import com.wemall.foundation.service.*;
import com.wemall.manage.seller.tools.TransportTools;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 卖家运费模板控制器
 */
@Controller
public class TransportSellerAction {
    @Autowired
    private ISysConfigService configService;

    @Autowired
    private IUserConfigService userConfigService;

    @Autowired
    private ITransportService transportService;

    @Autowired
    private ITransAreaService transAreaService;

    @Autowired
    private IUserService userService;

    @Autowired
    private TransportTools transportTools;

    @SecurityMapping(display = false, rsequence = 0, title = "卖家运费模板列表", value = "/seller/transport_list.htm*", rtype = "seller", rname = "物流工具", rcode = "transport_seller", rgroup = "交易管理")
    @RequestMapping({"/seller/transport_list.htm"})
    public ModelAndView transport_list(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/transport_list.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))){
            url = CommUtil.getURL(request);
        }
        String params = "";
        TransportQueryObject qo = new TransportQueryObject(currentPage, mv, orderBy, orderType);
        User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        qo.addQuery("obj.store.id", new SysMap("store_id", user.getStore().getId()), "=");
        IPageList pList = this.transportService.list(qo);
        CommUtil.saveIPageList2ModelAndView("", "", params, pList, mv);
        mv.addObject("transportTools", this.transportTools);
        System.out.println(pList);
        System.out.println(this.transportTools);

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "卖家运费模板添加", value = "/seller/transport_add.htm*", rtype = "seller", rname = "物流工具", rcode = "transport_seller", rgroup = "交易管理")
    @RequestMapping({"/seller/transport_add.htm"})
    public ModelAndView transport_add(HttpServletRequest request, HttpServletResponse response, String currentPage){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/transport_add.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        mv.addObject("currentPage", currentPage);

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "卖家运费模板编辑", value = "/seller/transport_edit.htm*", rtype = "seller", rname = "物流工具", rcode = "transport_seller", rgroup = "交易管理")
    @RequestMapping({"/seller/transport_edit.htm"})
    public ModelAndView transport_edit(HttpServletRequest request, HttpServletResponse response, String id, String currentPage){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/transport_add.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        if ((id != null) && (!id.equals(""))){
            Transport transport = this.transportService.getObjById(Long.valueOf(Long.parseLong(id)));
            mv.addObject("obj", transport);
            mv.addObject("currentPage", currentPage);
        }
        mv.addObject("transportTools", this.transportTools);

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "卖家运费模板复制", value = "/seller/transport_copy.htm*", rtype = "seller", rname = "物流工具", rcode = "transport_seller", rgroup = "交易管理")
    @RequestMapping({"/seller/transport_copy.htm"})
    public ModelAndView transport_copy(HttpServletRequest request, HttpServletResponse response, String id, String currentPage){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/transport_add.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        if ((id != null) && (!id.equals(""))){
            Transport transport = this.transportService.getObjById(
                                      Long.valueOf(Long.parseLong(id)));
            Transport obj = new Transport();
            obj.setStore(transport.getStore());
            obj.setTrans_ems(transport.isTrans_ems());
            obj.setTrans_ems_info(transport.getTrans_ems_info());
            obj.setTrans_express(transport.isTrans_express());
            obj.setTrans_express_info(transport.getTrans_express_info());
            obj.setTrans_mail(transport.isTrans_mail());
            obj.setTrans_mail_info(transport.getTrans_mail_info());
            obj.setTrans_name(transport.getTrans_name());
            mv.addObject("obj", obj);
            mv.addObject("currentPage", currentPage);
        }
        mv.addObject("transportTools", this.transportTools);

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "卖家运费模板保存", value = "/seller/transport_save.htm*", rtype = "seller", rname = "物流工具", rcode = "transport_seller", rgroup = "交易管理")
    @RequestMapping({"/seller/transport_save.htm"})
    public String transport_save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage, String trans_mail, String trans_express, String trans_ems, String mail_city_count, String express_city_count, String ems_city_count){
        WebForm wf = new WebForm();
        Transport transport = null;
        if (id.equals("")){
            transport = (Transport)wf.toPo(request, Transport.class);
            transport.setAddTime(new Date());
        }else{
            Transport obj = this.transportService.getObjById(Long.valueOf(Long.parseLong(id)));
            transport = (Transport)wf.toPo(request, obj);
        }
        if (CommUtil.null2Boolean(trans_mail)){
            List trans_mail_info = new ArrayList();
            Map map = new HashMap();
            map.put("city_id", "-1");
            map.put("city_name", "全国");
            map.put("trans_weight", Integer.valueOf(CommUtil.null2Int(request.getParameter("mail_trans_weight"))));
            map.put("trans_fee", Float.valueOf(CommUtil.null2Float(request.getParameter("mail_trans_fee"))));
            map.put("trans_add_weight", Integer.valueOf(CommUtil.null2Int(request.getParameter("mail_trans_add_weight"))));
            map.put("trans_add_fee", Float.valueOf(CommUtil.null2Float(request.getParameter("mail_trans_add_fee"))));
            trans_mail_info.add(map);
            for (int i = 1; i <= CommUtil.null2Int(mail_city_count); i++){
                int trans_weight = CommUtil.null2Int(request.getParameter("mail_trans_weight" + i));
                String city_ids = CommUtil.null2String(request.getParameter("mail_city_ids" + i));
                if ((!city_ids.equals("")) && (trans_weight > 0)){
                    float trans_fee = CommUtil.null2Float(request.getParameter("mail_trans_fee" + i));
                    int trans_add_weight = CommUtil.null2Int(request.getParameter("mail_trans_add_weight" + i));
                    float trans_add_fee = CommUtil.null2Float(request.getParameter("mail_trans_add_fee" + i));
                    String city_name = CommUtil.null2String(request.getParameter("mail_city_names" + i));
                    Map map1 = new HashMap();
                    map1.put("city_id", city_ids);
                    map1.put("city_name", city_name);
                    map1.put("trans_weight", Integer.valueOf(trans_weight));
                    map1.put("trans_fee", Float.valueOf(trans_fee));
                    map1.put("trans_add_weight", Integer.valueOf(trans_add_weight));
                    map1.put("trans_add_fee", Float.valueOf(trans_add_fee));
                    trans_mail_info.add(map1);
                }
            }
            transport.setTrans_mail_info(Json.toJson(trans_mail_info,
                                         JsonFormat.compact()));
        }
        if (CommUtil.null2Boolean(trans_express)){
            List trans_express_info = new ArrayList();
            Map map = new HashMap();
            map.put("city_id", "-1");
            map.put("city_name", "全国");
            map.put("trans_weight", Integer.valueOf(CommUtil.null2Int(request.getParameter("express_trans_weight"))));
            map.put("trans_fee", Float.valueOf(CommUtil.null2Float(request.getParameter("express_trans_fee"))));
            map.put("trans_add_weight", Integer.valueOf(CommUtil.null2Int(request.getParameter("express_trans_add_weight"))));
            map.put("trans_add_fee", Float.valueOf(CommUtil.null2Float(request.getParameter("express_trans_add_fee"))));
            trans_express_info.add(map);
            for (int i = 1; i <= CommUtil.null2Int(express_city_count); i++){
                int trans_weight = CommUtil.null2Int(request.getParameter("express_trans_weight" + i));
                String city_ids = CommUtil.null2String(request.getParameter("express_city_ids" + i));
                if ((!city_ids.equals("")) && (trans_weight > 0)){
                    float trans_fee = CommUtil.null2Float(request.getParameter("express_trans_fee" + i));
                    int trans_add_weight = CommUtil.null2Int(request.getParameter("express_trans_add_weight" + i));
                    float trans_add_fee = CommUtil.null2Float(request.getParameter("express_trans_add_fee" + i));
                    String city_name = CommUtil.null2String(request.getParameter("express_city_names" + i));
                    Map map1 = new HashMap();
                    map1.put("city_id", city_ids);
                    map1.put("city_name", city_name);
                    map1.put("trans_weight", Integer.valueOf(trans_weight));
                    map1.put("trans_fee", Float.valueOf(trans_fee));
                    map1.put("trans_add_weight", Integer.valueOf(trans_add_weight));
                    map1.put("trans_add_fee", Float.valueOf(trans_add_fee));
                    trans_express_info.add(map1);
                }
            }
            transport.setTrans_express_info(Json.toJson(trans_express_info,
                                            JsonFormat.compact()));
        }
        if (CommUtil.null2Boolean(trans_ems)){
            List trans_ems_info = new ArrayList();
            Map map = new HashMap();
            map.put("city_id", "-1");
            map.put("city_name", "全国");
            map.put("trans_weight", Integer.valueOf(CommUtil.null2Int(request.getParameter("ems_trans_weight"))));
            map.put("trans_fee", Float.valueOf(CommUtil.null2Float(request.getParameter("ems_trans_fee"))));
            map.put("trans_add_weight", Integer.valueOf(CommUtil.null2Int(request.getParameter("ems_trans_add_weight"))));
            map.put("trans_add_fee", Float.valueOf(CommUtil.null2Float(request.getParameter("ems_trans_add_fee"))));
            trans_ems_info.add(map);
            for (int i = 1; i <= CommUtil.null2Int(ems_city_count); i++){
                int trans_weight = CommUtil.null2Int(request.getParameter("ems_trans_weight" + i));
                String city_ids = CommUtil.null2String(request.getParameter("ems_city_ids" + i));
                if ((!city_ids.equals("")) && (trans_weight > 0)){
                    float trans_fee = CommUtil.null2Float(request.getParameter("ems_trans_fee" + i));
                    int trans_add_weight = CommUtil.null2Int(request.getParameter("ems_trans_add_weight" + i));
                    float trans_add_fee = CommUtil.null2Float(request.getParameter("ems_trans_add_fee" + i));
                    String city_name = CommUtil.null2String(request.getParameter("ems_city_names" + i));
                    Map map1 = new HashMap();
                    map1.put("city_id", city_ids);
                    map1.put("city_name", city_name);
                    map1.put("trans_weight", Integer.valueOf(trans_weight));
                    map1.put("trans_fee", Float.valueOf(trans_fee));
                    map1.put("trans_add_weight", Integer.valueOf(trans_add_weight));
                    map1.put("trans_add_fee", Float.valueOf(trans_add_fee));
                    trans_ems_info.add(map1);
                }
            }
            transport.setTrans_ems_info(Json.toJson(trans_ems_info,
                                                    JsonFormat.compact()));
        }
        transport.setAddTime(new Date());
        Store store = SecurityUserHolder.getCurrentUser().getStore();
        transport.setStore(store);
        if (id.equals(""))
            this.transportService.save(transport);
        else
            this.transportService.update(transport);

        return "redirect:transport_success.htm?currentPage=" + currentPage;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "卖家运费模板保存成功", value = "/seller/transport_success.htm*", rtype = "seller", rname = "物流工具", rcode = "transport_seller", rgroup = "交易管理")
    @RequestMapping({"/seller/transport_success.htm"})
    public ModelAndView transport_success(HttpServletRequest request, HttpServletResponse response, String currentPage){
        ModelAndView mv = new JModelAndView("user/default/usercenter/success.html", this.configService.getSysConfig(),
                                            this.userConfigService.getUserConfig(), 0, request, response);
        mv.addObject("op_title", "运费模板保存成功");
        mv.addObject("url", CommUtil.getURL(request) +
                     "/seller/transport_list.htm?currentPage=" + currentPage);

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "卖家运费模板删除", value = "/seller/transport_del.htm*", rtype = "seller", rname = "物流工具", rcode = "transport_seller", rgroup = "交易管理")
    @RequestMapping({"/seller/transport_del.htm"})
    public String transport_del(HttpServletRequest request, HttpServletResponse response, String mulitId, String currentPage){
        String[] ids = mulitId.split(",");
        for (String id : ids){
            if (!id.equals("")){
                Transport transport = this.transportService.getObjById(Long.valueOf(Long.parseLong(id)));
                this.transportService.delete(Long.valueOf(Long.parseLong(id)));
            }
        }

        return "redirect:transport_list.htm?currentPage=" + currentPage;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "卖家运费模板详细信息", value = "/seller/transport_info.htm*", rtype = "seller", rname = "物流工具", rcode = "transport_seller", rgroup = "交易管理")
    @RequestMapping({"/seller/transport_info.htm"})
    public ModelAndView transport_info(HttpServletRequest request, HttpServletResponse response, String type, String id){
        if ((type == null) || (type.equals(""))){
            type = CommUtil.null2String(request.getAttribute("type"));
        }
        if ((id == null) || (id.equals(""))){
            id = CommUtil.null2String(request.getAttribute("id"));
        }
        if (CommUtil.null2String(type).equals("")){
            type = "mail";
        }
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/transport_" + type + ".html",
            this.configService.getSysConfig(), this.userConfigService
            .getUserConfig(), 0, request, response);
        if ((id != null) && (!id.equals(""))){
            Transport transport = this.transportService.getObjById(
                                      Long.valueOf(Long.parseLong(id)));
            mv.addObject("obj", transport);
            mv.addObject("transportTools", this.transportTools);
        }

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "卖家运费模板区域编辑", value = "/seller/transport_area.htm*", rtype = "seller", rname = "物流工具", rcode = "transport_seller", rgroup = "交易管理")
    @RequestMapping({"/seller/transport_area.htm"})
    public ModelAndView transport_area(HttpServletRequest request, HttpServletResponse response, String id, String trans_city_type, String trans_index){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/transport_area.html", this.configService
            .getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        List objs = this.transAreaService
                    .query(
                        "select obj from TransArea obj where obj.parent.id is null order by obj.sequence asc",
                        null, -1, -1);
        mv.addObject("objs", objs);
        mv.addObject("trans_city_type", trans_city_type);
        mv.addObject("trans_index", trans_index);

        return mv;
    }
    
    
    
    @SecurityMapping(display = false, rsequence = 0, title = "省内外金额设置", value = "/seller/province.htm*", rtype = "seller", rname = "物流工具", rcode = "kkj", rgroup = "交易管理")
    @RequestMapping({"/seller/toprovince.htm"})
    public ModelAndView qwer(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/jine.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        if (user!=null) {
        	 mv.addObject("user",user);	
		}
        return mv;
    }
    
    
    @SecurityMapping(display = false, rsequence = 0, title = "省内外金额设置提交", value = "/seller/province.htm*", rtype = "seller", rname = "物流工具", rcode = "kkj", rgroup = "交易管理")
    @RequestMapping({"/seller/province.htm"})
    public ModelAndView province(HttpServletRequest request, HttpServletResponse response,String province,String outprovince){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/jine.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        //判断是否为空并且转int是否成功，不成功就跳转页面
        try {
			int pro = Integer.parseInt(province);
			int outpro = Integer.parseInt(outprovince);
		} catch (Exception e) {
			// TODO: handle exception
			mv = new JModelAndView("error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
   		    mv.addObject("current_url", "error.html");
            mv.addObject("op_title", "请填写合理的金额");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/toprovince.htm");
            return mv;
		}
        
        //查询到底数据库中是否有该值，有则修改，无则保存
        try {
        	 User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        	 if (user.getProvince()==null || user.getOutprovince()==null) {
        	  //保存
        	    user.setProvince(province);
          	    user.setOutprovince(outprovince);
          	    userService.update(user);
          	  mv = new JModelAndView("success.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
     		    mv.addObject("current_url", "success.html");
              mv.addObject("op_title", "保存成功");
              mv.addObject("url", CommUtil.getURL(request) + "/seller/toprovince.htm");
              return mv;
        	  }
        	   //修改
        	   user.setProvince(province);
      	       user.setOutprovince(outprovince);
        	   userService.update(user);
        	   mv = new JModelAndView("success.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
      		    mv.addObject("current_url", "success.html");
               mv.addObject("op_title", "修改成功");
               mv.addObject("url", CommUtil.getURL(request) + "/seller/toprovince.htm");
               return mv;
		} catch (Exception e) {
			mv = new JModelAndView("error.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
   		    mv.addObject("current_url", "error.html");
            mv.addObject("op_title", "系统繁忙，请稍后再试");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/toprovince.htm");
            return mv;
		}
    }
    
}






