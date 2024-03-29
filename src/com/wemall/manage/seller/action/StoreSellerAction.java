package com.wemall.manage.seller.action;

import com.alibaba.fastjson.JSON;
import com.wemall.core.annotation.SecurityMapping;
import com.wemall.core.mv.JModelAndView;
import com.wemall.core.security.support.SecurityUserHolder;
import com.wemall.core.tools.CommUtil;
import com.wemall.core.tools.HttpClass;
import com.wemall.core.tools.QRCodeEncoderHandler;
import com.wemall.core.tools.WebForm;
import com.wemall.foundation.domain.*;
import com.wemall.foundation.service.*;
import com.wemall.view.web.tools.AreaViewTools;
import com.wemall.view.web.tools.StoreViewTools;

import org.apache.poi.hssf.record.HCenterRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 卖家店铺控制器
 */
@Controller
public class StoreSellerAction {
    @Autowired
    private ISysConfigService configService;

    @Autowired
    private IUserConfigService userConfigService;

    @Autowired
    private IStoreGradeService storeGradeService;

    @Autowired
    private IAreaService areaService;

    @Autowired
    private IStoreClassService storeClassService;

    @Autowired
    private IStoreService storeService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private IAccessoryService accessoryService;

    @Autowired
    private IStoreGradeLogService storeGradeLogService;

    @Autowired
    private IStoreSlideService storeSlideService;

    @Autowired
    private StoreViewTools storeTools;

    @Autowired
    private AreaViewTools areaViewTools;
    @Autowired
    private IAddressService addressService;
    
    @Autowired
    private StoreCreditStatusService storeCreditStatusService;
    
    @Autowired
    private AuthenticationService authenticationService;

    @SecurityMapping(display = false, rsequence = 0, title = "申请店铺第一步", value = "/seller/store_create_first.htm*", rtype = "buyer", rname = "申请店铺", rcode = "create_store", rgroup = "申请店铺")
    @RequestMapping({"/seller/store_create_first.htm"})
    public ModelAndView store_create_first(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = null;
        int store_status = 0;
        Store store = this.storeService.getObjByProperty("user.id",
                      SecurityUserHolder.getCurrentUser().getId());
        if (store != null){
            store_status = store.getStore_status();
        }
        if (this.configService.getSysConfig().isStore_allow()){
            if (store_status == 0){
                mv = new JModelAndView("store_create_first.html", this.configService.getSysConfig(),
                                       this.userConfigService.getUserConfig(), 1, request, response);
                List sgs = this.storeGradeService.query("select obj from StoreGrade obj where obj.sequence='0' ", null, -1, -1);
                mv.addObject("sgs", sgs);
                mv.addObject("storeTools", this.storeTools);
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
        }else{
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request,
                                   response);
            mv.addObject("op_title", "系统暂时关闭了申请店铺");
            mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
        }

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "申请店铺第二步", value = "/seller/store_create_second.htm*", rtype = "buyer", rname = "申请店铺", rcode = "create_store", rgroup = "申请店铺")
    @RequestMapping({"/seller/store_create_second.htm"})
    public ModelAndView store_create_second(HttpServletRequest request, HttpServletResponse response, String grade_id, String userName){
    	ModelAndView mv = null;
        Store store = this.storeService.getObjByProperty("user.id",
                      SecurityUserHolder.getCurrentUser().getId());
        int store_status = store == null ? 0 : store.getStore_status();
        if (this.configService.getSysConfig().isStore_allow()){
            if ((grade_id == null) || (grade_id.equals(""))){
                mv = new JModelAndView("store_create_first.html",
                                       this.configService.getSysConfig(),
                                       this.userConfigService.getUserConfig(), 1, request, response);
                List sgs = this.storeGradeService
                           .query("select obj from StoreGrade obj order by obj.sequence asc",
                                  null, -1, -1);
                mv.addObject("sgs", sgs);
                mv.addObject("storeTools", this.storeTools);
            }else{
                if (store_status == 0){
                    mv = new JModelAndView("store_create_second.html",
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
        HttpClass hc = new	HttpClass();
        try {
			String Load = hc.load("http://127.0.0.1:8081/ssm_project/select", "userName="+userName);
			 if (Load!=null&&Load != "" ) {
				 com.wemall.foundation.domain.Authentication auth = JSON.parseObject(Load, com.wemall.foundation.domain.Authentication.class);
				 mv.addObject("auth",auth);
			 }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "申请店铺完成", value = "/seller/store_create_finish.htm*", rtype = "buyer", rname = "申请店铺", rcode = "create_store", rgroup = "申请店铺")
    @RequestMapping({"/seller/store_create_finish.htm"})
    public ModelAndView store_create_finish(@RequestParam("store_telphone") String store_telephone,@RequestParam("businesRegsNumber") String businesRegsNumber,@RequestParam("Features")int Features,String PublicAccount,@RequestParam("DutyParagraph")String DutyParagraph,@RequestParam("EndPurchaseDate")String EndPurchaseDate,@RequestParam("EndGSPDate")String EndGSPDate,@RequestParam("EndDrugDate")String EndDrugDate,@RequestParam("EndBusinessDate")String EndBusinessDate,@RequestParam("EndIDcardDate")String EndIDcardDate,@RequestParam("IDcardNumber")String IDcardNumber,@RequestParam("PurchaseNumber")String PurchaseNumber,@RequestParam("GSPNumber")String GSPNumber,@RequestParam("DrugNumber")String DrugNumber,@RequestParam("businessNumber")String businessNumber,@RequestParam("userName")String userName,HttpServletRequest request, HttpServletResponse response,@RequestParam("file1") MultipartFile file1 ,@RequestParam("file2") MultipartFile file2 ,@RequestParam("file3") MultipartFile file3 ,@RequestParam("file4") MultipartFile file4 , @RequestParam("file5") MultipartFile file5 ,@RequestParam("file6")  MultipartFile file6, String sc_id, String grade_id, String area_id,String store_address,String store_name,String store_ower, String store_create_session){
    	ModelAndView mv = new JModelAndView("success.html",
                                            this.configService.getSysConfig(),
                                            this.userConfigService.getUserConfig(), 1, request, response);
    	String store_create_session1 = CommUtil.null2String(request.getSession(
                                           false).getAttribute("store_create_session"));
        if ((!store_create_session1.equals("")) &&
                (store_create_session1.equals(store_create_session))){
            Store user_store = this.storeService.getObjByProperty("user.id",
                               SecurityUserHolder.getCurrentUser().getId());
            int store_status = user_store == null ? 1 : user_store.getStore_status();
            if (store_status == 1){
            	 //认证信息保存
                TestAction action = new TestAction();
           		Area  area1 = this.areaService.getObjById(CommUtil.null2Long(area_id));
        		WebForm w = new WebForm();
        		User u = SecurityUserHolder.getCurrentUser();
        	    User us = (User)w.toPo(request, u);
        		us.setArea(area1);
        		us.setWW(store_name);
        		us.setTrueName(store_ower);
        		us.setAddress(area1.getParent().getParent().getAreaName()+area1.getParent().getAreaName()+area1.getAreaName());
        		this.userService.update(us);
        		Area area = this.areaService.getObjById(Long.valueOf(Long.parseLong(area_id)));
        		String 	l = action.rengzheng2(u.getId().toString(),store_ower,businesRegsNumber,area,area_id,store_address,store_name,Features, PublicAccount, DutyParagraph, EndPurchaseDate, EndGSPDate, EndDrugDate, EndBusinessDate, EndIDcardDate, IDcardNumber, PurchaseNumber, GSPNumber, DrugNumber, businessNumber, userName, request, response, file1, file2, file3, file4, file5, file6);
				    if (l.equals("zd")) {
						mv = new JModelAndView("error.html",
	                            this.configService.getSysConfig(),
	                            this.userConfigService.getUserConfig(), 1, request,
	                            response);
	                     mv.addObject("op_title", "您的信息填写不完整");
	                     mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
	                     return mv;
					}else if (l.equals("xg")) {
						mv = new JModelAndView("error.html",
	                            this.configService.getSysConfig(),
	                            this.userConfigService.getUserConfig(), 1, request,
	                            response);
	                    mv.addObject("op_title", "修改出错");
	                     mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
	                     return mv;
					}else if (l.equals("tp")) {
						mv = new JModelAndView("error.html",
	                            this.configService.getSysConfig(),
	                            this.userConfigService.getUserConfig(), 1, request,
	                            response);
	                    mv.addObject("op_title", "图片为空或不符合要求");
	                     mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
	                     return mv;
					}else if (l.equals("cw")) {
						mv = new JModelAndView("error.html",
	                            this.configService.getSysConfig(),
	                            this.userConfigService.getUserConfig(), 1, request,
	                            response);
	                    mv.addObject("op_title", "申请失败");
	                     mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
	                     return mv;
					}
                WebForm wf = new WebForm();
                Store store = (Store)wf.toPo(request, Store.class);
/*                StoreClass sc = this.storeClassService.getObjById(Long.valueOf(Long.parseLong(sc_id)));
                store.setSc(sc);*/
                grade_id="1";
                StoreGrade grade = this.storeGradeService.getObjById(Long.valueOf(Long.parseLong(grade_id)));
                store.setGrade(grade);
				store.setStore_telephone(store_telephone);
                Area area2= this.areaService.getObjById(Long.valueOf(Long.parseLong(area_id)));
                store.setArea(area2);
                store.setTemplate("default");
                store.setAddTime(new Date());
                store.setStore_second_domain("shop" + SecurityUserHolder.getCurrentUser().getId().toString());
                store.setStore_status(1);
                store.setStore_ower_card(IDcardNumber);
                this.storeService.save(store);

                String path = request.getSession().getServletContext().getRealPath("/") +
                              File.separator + "upload" + File.separator + "store" + File.separator + store.getId();
                CommUtil.createFolder(path);

                String store_url = CommUtil.getURL(request) + "/store_" + store.getId() + ".htm";
                QRCodeEncoderHandler handler = new QRCodeEncoderHandler();
                handler.encoderQRCode(store_url, path + "/code.png");
                User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
                user.setStore(store);
                SecurityUserHolder.getCurrentUser().setStore(store);
               /* if (store.getGrade().isAudit())
                    store.setStore_status(1);
               else{
                    store.setStore_status(2);
                }*/
                if (user.getUserRole().equals("BUYER")){
                    user.setUserRole("BUYER_SELLER");
                }
                if (user.getUserRole().equals("ADMIN")){
                    user.setUserRole("ADMIN_BUYER_SELLER");
                }

                Map params = new HashMap();
                params.put("type", "SELLER");
                List roles = this.roleService.query(
                                 "select obj from Role obj where obj.type=:type",
                                 params, -1, -1);
                user.getRoles().addAll(roles);
                this.userService.update(user);

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                    SecurityContextHolder.getContext().getAuthentication().getPrincipal(),
                    SecurityContextHolder.getContext().getAuthentication().getCredentials(),
                    user.get_common_Authorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                mv.addObject("op_title", "待审核");
                try {
                     //成功开店，初始化收货地址数据
					//成功提交，新增默认地址
					Address address = new Address();
					address.setTrueName(user.getTrueName());
					address.setAddTime(new Date());
					address.setTelephone(user.getUserName());
					address.setUser(user);
					address.setMobile(user.getUsername());
					address.setArea_info(store_address);
					address.setArea(area2);
					addressService.save(address);
				} catch (Exception e) {
					// TODO: handle exception
				}
                
                
                
                
            }else{
                mv = new JModelAndView("error.html",
                                       this.configService.getSysConfig(),
                                       this.userConfigService.getUserConfig(), 1, request,
                                       response);
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
                                           this.userConfigService.getUserConfig(), 1, request, response);
                    mv.addObject("op_title", "您已经开通店铺");
                    mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
                }
                if (store_status == 6){
                    mv = new JModelAndView("error.html",
                                           this.configService.getSysConfig(),
                                           this.userConfigService.getUserConfig(), 1, request, response);
                    mv.addObject("op_title", "资质过期，您的店铺已经被关闭");
                    mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
                }
            }
            mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
           // request.getSession(false).removeAttribute("store_create_session");
        }else{
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "表单已经失效");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
        }

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "店铺设置", value = "/seller/store_set.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "店铺设置")
    @RequestMapping({"/seller/store_set.htm"})
    public ModelAndView store_set(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/store_set.html",
            this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        Store store = this.storeService.getObjByProperty("user.id",
                      SecurityUserHolder.getCurrentUser().getId());
        mv.addObject("store", store);
        mv.addObject("areaViewTools", this.areaViewTools);
        List areas = this.areaService.query(
                         "select obj from Area obj where obj.parent.id is null", null,
                         -1, -1);
        mv.addObject("areas", areas);

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "店铺设置保存", value = "/seller/store_set_save.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "店铺设置")
    @RequestMapping({"/seller/store_set_save.htm"})
    public ModelAndView store_set_save(HttpServletRequest request, HttpServletResponse response, String area_id, String store_second_domain){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/success.html",
            this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        Store store = this.storeService.getObjByProperty("user.id",
                      SecurityUserHolder.getCurrentUser().getId());
        WebForm wf = new WebForm();
        wf.toPo(request, store);

        String uploadFilePath = this.configService.getSysConfig()
                                .getUploadFilePath();
        String saveFilePathName = request.getSession().getServletContext()
                                  .getRealPath("/") +
                                  uploadFilePath + "/store_logo";
        Map map = new HashMap();
        try {
            String fileName = store.getStore_logo() == null ? "" : store
                              .getStore_logo().getName();
            map = CommUtil.saveFileToServer(request, "logo", saveFilePathName,
                                            fileName, null);
            if (fileName.equals("")){
                if (map.get("fileName") != ""){
                    Accessory store_logo = new Accessory();
                    store_logo
                    .setName(CommUtil.null2String(map.get("fileName")));
                    store_logo.setExt(CommUtil.null2String(map.get("mime")));
                    store_logo
                    .setSize(CommUtil.null2Float(map.get("fileSize")));
                    store_logo.setPath(uploadFilePath + "/store_logo");
                    store_logo.setWidth(CommUtil.null2Int(map.get("width")));
                    store_logo.setHeight(CommUtil.null2Int(map.get("height")));
                    store_logo.setAddTime(new Date());
                    this.accessoryService.save(store_logo);
                    store.setStore_logo(store_logo);
                }
            }else if (map.get("fileName") != ""){
                Accessory store_logo = store.getStore_logo();
                store_logo
                .setName(CommUtil.null2String(map.get("fileName")));
                store_logo.setExt(CommUtil.null2String(map.get("mime")));
                store_logo
                .setSize(CommUtil.null2Float(map.get("fileSize")));
                store_logo.setPath(uploadFilePath + "/store_logo");
                store_logo.setWidth(CommUtil.null2Int(map.get("width")));
                store_logo.setHeight(CommUtil.null2Int(map.get("height")));
                this.accessoryService.update(store_logo);
            }
        } catch (IOException e){
            e.printStackTrace();
        }

        saveFilePathName = request.getSession().getServletContext()
                           .getRealPath("/") +
                           this.configService.getSysConfig().getUploadFilePath() +
                           "/store_banner";
        try {
            String fileName = store.getStore_banner() == null ? "" : store
                              .getStore_banner().getName();
            map = CommUtil.saveFileToServer(request, "banner",
                                            saveFilePathName, fileName, null);
            if (fileName.equals("")){
                if (map.get("fileName") != ""){
                    Accessory store_banner = new Accessory();
                    store_banner.setName(CommUtil.null2String(map
                                         .get("fileName")));
                    store_banner.setExt(CommUtil.null2String(map.get("mime")));
                    store_banner.setSize(CommUtil.null2Float(map
                                         .get("fileSize")));
                    store_banner.setPath(uploadFilePath + "/store_banner");
                    store_banner.setWidth(CommUtil.null2Int(map.get("width")));
                    store_banner
                    .setHeight(CommUtil.null2Int(map.get("height")));
                    store_banner.setAddTime(new Date());
                    this.accessoryService.save(store_banner);
                    store.setStore_banner(store_banner);
                }
            }else if (map.get("fileName") != ""){
                Accessory store_banner = store.getStore_banner();
                store_banner.setName(CommUtil.null2String(map
                                     .get("fileName")));
                store_banner.setExt(CommUtil.null2String(map.get("mime")));
                store_banner.setSize(CommUtil.null2Float(map
                                     .get("fileSize")));
                store_banner.setPath(uploadFilePath + "/store_banner");
                store_banner.setWidth(CommUtil.null2Int(map.get("width")));
                store_banner
                .setHeight(CommUtil.null2Int(map.get("height")));
                this.accessoryService.update(store_banner);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
        store.setArea(area);
        if (this.configService.getSysConfig().isSecond_domain_open()){
            if ((this.configService.getSysConfig().getDomain_allow_count() > store
                    .getDomain_modify_count()) &&
                    (!CommUtil.null2String(store_second_domain).equals(""))){
                if (!store_second_domain.equals(store
                                                .getStore_second_domain())){
                    store.setStore_second_domain(store_second_domain);
                    store.setDomain_modify_count(store.getDomain_modify_count() + 1);
                }
            }
        }
        this.storeService.update(store);
        mv.addObject("op_title", "店铺设置成功");
        mv.addObject("url", CommUtil.getURL(request) + "/seller/store_set.htm");

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "店铺地图", value = "/seller/store_map.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "店铺设置")
    @RequestMapping({"/seller/store_map.htm"})
    public ModelAndView store_map(HttpServletRequest request, HttpServletResponse response, String map_type){
        Store store = this.storeService.getObjByProperty("user.id",
                      SecurityUserHolder.getCurrentUser().getId());
        if (CommUtil.null2String(map_type).equals("")){
            if ((store.getMap_type() != null) && (!store.getMap_type().equals("")))
                map_type = store.getMap_type();
           else{
                map_type = "baidu";
            }
        }

        ModelAndView mv = new JModelAndView("user/default/usercenter/store_" +
                                            map_type + "_map.html", this.configService.getSysConfig(),
                                            this.userConfigService.getUserConfig(), 0, request, response);
        mv.addObject("store", store);

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "店铺地图保存", value = "/seller/store_map_save.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "店铺设置")
    @RequestMapping({"/seller/store_map_save.htm"})
    public ModelAndView store_map_save(HttpServletRequest request, HttpServletResponse response, String store_lat, String store_lng){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/success.html",
            this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        Store store = this.storeService.getObjByProperty("user.id",
                      SecurityUserHolder.getCurrentUser().getId());
        store.setStore_lat(BigDecimal.valueOf(CommUtil.null2Double(store_lat)));
        store.setStore_lng(BigDecimal.valueOf(CommUtil.null2Double(store_lng)));
        this.storeService.update(store);
        mv.addObject("op_title", "店铺设置成功");
        mv.addObject("url", CommUtil.getURL(request) + "/seller/store_map.htm");

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "主题设置", value = "/seller/store_theme.htm*", rtype = "seller", rname = "主题设置", rcode = "store_theme_seller", rgroup = "店铺设置")
    @RequestMapping({"/seller/store_theme.htm"})
    public ModelAndView store_theme(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/store_theme.html",
            this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        Store store = this.storeService.getObjByProperty("user.id",
                      SecurityUserHolder.getCurrentUser().getId());
        mv.addObject("store", store);

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "主题设置", value = "/seller/store_theme_save.htm*", rtype = "seller", rname = "主题设置", rcode = "store_theme_seller", rgroup = "店铺设置")
    @RequestMapping({"/seller/store_theme_save.htm"})
    public String store_theme_set(HttpServletRequest request, HttpServletResponse response, String theme){
        Store store = this.storeService.getObjByProperty("user.id",
                      SecurityUserHolder.getCurrentUser().getId());
        if (store.getGrade().getTemplates().indexOf(theme) >= 0){
            store.setTemplate(theme);
            this.storeService.update(store);
        }

        return "redirect:store_theme.htm";
    }

    @SecurityMapping(display = false, rsequence = 0, title = "店铺认证", value = "/seller/store_approve.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "店铺设置")
    @RequestMapping({"/seller/store_approve.htm"})
    public ModelAndView store_approve(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/store_approve.html",
            this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        Store store = this.storeService.getObjByProperty("user.id",
                      SecurityUserHolder.getCurrentUser().getId());
        mv.addObject("store", store);

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "店铺认证保存", value = "/seller/store_approve_save.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "店铺设置")
    @RequestMapping({"/seller/store_approve_save.htm"})
    public String store_approve_save(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/store_approve.html",
            this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        Store store = this.storeService.getObjByProperty("user.id",
                      SecurityUserHolder.getCurrentUser().getId());

        String uploadFilePath = this.configService.getSysConfig()
                                .getUploadFilePath();
        String saveFilePathName = request.getSession().getServletContext()
                                  .getRealPath("/") +
                                  uploadFilePath;
        Map map = new HashMap();
        try {
            String fileName = store.getCard() == null ? "" : store.getCard()
                              .getName();
            map = CommUtil.saveFileToServer(request, "card_img",
                                            saveFilePathName + File.separator + "card", fileName, null);
            if (fileName.equals("")){
                if (map.get("fileName") != ""){
                    Accessory card = new Accessory();
                    card.setName(CommUtil.null2String(map.get("fileName")));
                    card.setExt(CommUtil.null2String(map.get("mime")));
                    card.setSize(CommUtil.null2Float(map.get("fileSize")));
                    card.setPath(uploadFilePath + "/card");
                    card.setWidth(CommUtil.null2Int(map.get("width")));
                    card.setHeight(CommUtil.null2Int(map.get("height")));
                    card.setAddTime(new Date());
                    this.accessoryService.save(card);
                    store.setCard(card);
                }
            }else if (map.get("fileName") != ""){
                Accessory card = store.getCard();
                card.setName(CommUtil.null2String(map.get("fileName")));
                card.setExt(CommUtil.null2String(map.get("mime")));
                card.setSize(CommUtil.null2Float(map.get("fileSize")));
                card.setPath(uploadFilePath + "/card");
                card.setWidth(CommUtil.null2Int(map.get("width")));
                card.setHeight(CommUtil.null2Int(map.get("height")));
                this.accessoryService.update(card);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        try {
            String fileName = store.getStore_license() == null ? "" : store
                              .getStore_license().getName();
            map = CommUtil.saveFileToServer(request, "license_img",
                                            saveFilePathName + File.separator + "license", fileName,
                                            null);
            if (fileName.equals("")){
                if (map.get("fileName") != ""){
                    Accessory store_license = new Accessory();
                    store_license.setName(CommUtil.null2String(map
                                          .get("fileName")));
                    store_license.setExt(CommUtil.null2String(map.get("mime")));
                    store_license.setSize(CommUtil.null2Float(map
                                          .get("fileSize")));
                    store_license.setPath(uploadFilePath + "/license");
                    store_license.setWidth(CommUtil.null2Int(map.get("width")));
                    store_license
                    .setHeight(CommUtil.null2Int(map.get("height")));
                    store_license.setAddTime(new Date());
                    this.accessoryService.save(store_license);
                    store.setStore_license(store_license);
                }
            }else if (map.get("fileName") != ""){
                Accessory store_license = store.getStore_license();
                store_license.setName(CommUtil.null2String(map
                                      .get("fileName")));
                store_license.setExt(CommUtil.null2String(map.get("mime")));
                store_license.setSize(CommUtil.null2Float(map
                                      .get("fileSize")));
                store_license.setPath(uploadFilePath + "/license");
                store_license.setWidth(CommUtil.null2Int(map.get("width")));
                store_license
                .setHeight(CommUtil.null2Int(map.get("height")));
                this.accessoryService.update(store_license);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        this.storeService.update(store);

        return "redirect:store_approve.htm";
    }

    @SecurityMapping(display = false, rsequence = 0, title = "店铺升级", value = "/seller/store_grade.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "店铺设置")
    @RequestMapping({"/seller/store_grade.htm"})
    public ModelAndView store_grade(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/store_grade.html",
            this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        Store store = this.storeService.getObjByProperty("user.id",
                      SecurityUserHolder.getCurrentUser().getId());
        if (store.getUpdate_grade() == null){
            List sgs = this.storeGradeService.query(
                           "select obj from StoreGrade obj order by obj.sequence asc",
                           null, -1, -1);
            mv.addObject("sgs", sgs);
            mv.addObject("store", store);
        }else{
            mv = new JModelAndView("user/default/usercenter/error.html",
                                   this.configService.getSysConfig(),
                                   this.userConfigService.getUserConfig(), 0, request,
                                   response);
            mv.addObject("op_title", "您的店铺升级正在审核中");
            mv.addObject("url", CommUtil.getURL(request) +
                         "/seller/store_set.htm");
        }

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "店铺升级申请完成", value = "/seller/store_grade_finish.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "店铺设置")
    @RequestMapping({"/seller/store_grade_finish.htm"})
    public ModelAndView store_grade_finish(HttpServletRequest request, HttpServletResponse response, String grade_id){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/success.html",
            this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        Store store = this.storeService.getObjByProperty("user.id",
                      SecurityUserHolder.getCurrentUser().getId());

        store.setUpdate_grade(this.storeGradeService.getObjById(
                                  CommUtil.null2Long(grade_id)));
        this.storeService.update(store);
        StoreGradeLog grade_log = new StoreGradeLog();
        grade_log.setAddTime(new Date());
        grade_log.setStore(store);
        this.storeGradeLogService.save(grade_log);
        mv.addObject("op_title", "申请提交成功");
        mv.addObject("url", CommUtil.getURL(request) + "/seller/store_set.htm");

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "店铺幻灯", value = "/seller/store_slide.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "店铺设置")
    @RequestMapping({"/seller/store_slide.htm"})
    public ModelAndView store_slide(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/store_slide.html",
            this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        Store store = this.storeService.getObjByProperty("user.id",
                      SecurityUserHolder.getCurrentUser().getId());
        mv.addObject("store", store);

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "店铺幻灯保存", value = "/seller/store_slide_save.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "店铺设置")
    @RequestMapping({"/seller/store_slide_save.htm"})
    public ModelAndView store_slide_save(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/store_slide.html",
            this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        Store store = this.storeService.getObjByProperty("user.id",
                      SecurityUserHolder.getCurrentUser().getId());
        String uploadFilePath = this.configService.getSysConfig()
                                .getUploadFilePath();
        String saveFilePathName = request.getSession().getServletContext()
                                  .getRealPath("/") +
                                  uploadFilePath + File.separator + "store_slide";
        for (int i = 1; i <= 5; i++){
            Map map = new HashMap();
            String fileName = "";
            StoreSlide slide = null;
            //如果没有店铺，报错
             if (store==null) {
            	 mv.addObject("op_title", "您还没有开启店铺");
                 mv.addObject("url", CommUtil.getURL(request) + "/seller/store_set.htm");
                 return mv;
			}
            if (store.getSlides().size() >= i){
                if (store.getSlides().get(i - 1).getAcc()==null) {
                	fileName="";
				}else {
					fileName = ((StoreSlide)store.getSlides().get(i - 1)).getAcc().getName();
				}
                slide = (StoreSlide)store.getSlides().get(i - 1);
            }
            try {
                map = CommUtil.saveFileToServer(request, "acc" + i,
                                                saveFilePathName,fileName, null);
                Accessory acc = null;
                if (fileName.equals("")){
                    if (map.get("fileName") != ""){
                        acc = new Accessory();
                        acc.setName(CommUtil.null2String(map.get("fileName")));
                        acc.setExt(CommUtil.null2String(map.get("mime")));
                        acc.setSize(CommUtil.null2Float(map.get("fileSize")));
                        acc.setPath(uploadFilePath + "/store_slide");
                        acc.setWidth(CommUtil.null2Int(map.get("width")));
                        acc.setHeight(CommUtil.null2Int(map.get("height")));
                        acc.setAddTime(new Date());
                        boolean flag= this.accessoryService.save(acc);
                    }
                }else if (map.get("fileName") != ""){
                    acc = slide.getAcc();
                    acc.setName(CommUtil.null2String(map.get("fileName")));
                    acc.setExt(CommUtil.null2String(map.get("mime")));
                    acc.setSize(CommUtil.null2Float(map.get("fileSize")));
                    acc.setPath(uploadFilePath + "/store_slide");
                    acc.setWidth(CommUtil.null2Int(map.get("width")));
                    acc.setHeight(CommUtil.null2Int(map.get("height")));
                    acc.setAddTime(new Date());
                    this.accessoryService.update(acc);
                }

               // if (acc != null){
                if (store.getSlides().size()==0) {
                	slide = new StoreSlide();
                    slide.setAcc(acc);
                    slide.setAddTime(new Date());
                    slide.setStore(store);
				}else if (store.getSlides().get(i - 1).getAcc()==null){
                    	StoreSlide storeSlide=store.getSlides().get(i-1);
                    	storeSlide.setAcc(acc);
                    	this.storeSlideService.update(storeSlide);
                    }
                    slide.setUrl(request.getParameter("acc_url" + i));
                    if (slide == null)
                        this.storeSlideService.save(slide);
                    else
                        this.storeSlideService.update(slide);
               // }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        mv.addObject("store", store);

        return mv;
    }
    
    /**
     * 
     * 删除幻灯片方法
     */
    @RequestMapping("/deleteStoreSlide.htm")
    public ModelAndView deleteStoreSlide(HttpServletRequest request,HttpServletResponse response,Long  id){
    	 ModelAndView mv = new JModelAndView(
    	            "user/default/usercenter/store_slide.html",
    	            this.configService.getSysConfig(),
    	            this.userConfigService.getUserConfig(), 0, request, response);
    	 User user=SecurityUserHolder.getCurrentUser();
    	 StoreSlide storeSlide=this.storeSlideService.getObjById(id);
    	 storeSlide.setAcc(null);
    	 boolean result=this.storeSlideService.update(storeSlide);
    	 return mv;
    }
    
    /***
     * 页面跳转，私有调用
     * @param file
     * @return
     */
	private ModelAndView Jump(String lu,String msg,HttpServletRequest request, HttpServletResponse response) {
		 ModelAndView mv = new JModelAndView(lu, this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
		 mv.addObject("current_url", lu);
         mv.addObject("op_title", msg);
         mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
         return mv;
	}
}




