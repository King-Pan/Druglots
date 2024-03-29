package com.wemall.manage.admin.action;

import com.alibaba.fastjson.JSON;
import com.easyjf.beans.BeanUtils;
import com.easyjf.beans.BeanWrapper;
import com.wemall.core.annotation.SecurityMapping;
import com.wemall.core.domain.virtual.SysMap;
import com.wemall.core.mv.JModelAndView;
import com.wemall.core.query.support.IPageList;
import com.wemall.core.security.support.SecurityUserHolder;
import com.wemall.core.tools.CommUtil;
import com.wemall.core.tools.HttpClass;
import com.wemall.core.tools.WebForm;
import com.wemall.core.tools.database.DatabaseTools;
import com.wemall.foundation.domain.*;
import com.wemall.foundation.domain.query.StoreGradeLogQueryObject;
import com.wemall.foundation.domain.query.StoreQueryObject;
import com.wemall.foundation.service.*;
import com.wemall.manage.admin.tools.AreaManageTools;
import com.wemall.manage.admin.tools.StoreTools;
import com.wemall.view.web.action.LoginViewAction;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 店铺管理控制器
 */
@Controller
public class StoreManageAction {
    @Autowired
    private ISysConfigService configService;

    @Autowired
    private IUserConfigService userConfigService;

    @Autowired
    private IStoreService storeService;

    @Autowired
    private IStoreGradeService storeGradeService;

    @Autowired
    private IStoreClassService storeClassService;

    @Autowired
    private IAreaService areaService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private IConsultService consultService;

    @Autowired
    private AreaManageTools areaManageTools;

    @Autowired
    private StoreTools storeTools;

    @Autowired
    private DatabaseTools databaseTools;

    @Autowired
    private ITemplateService templateService;

    @Autowired
    private IMessageService messageService;

    @Autowired
    private IStoreGradeLogService storeGradeLogService;

    @Autowired
    private IEvaluateService evaluateService;

    @Autowired
    private IGoodsCartService goodsCartService;

    @Autowired
    private IOrderFormService orderFormService;

    @Autowired
    private IOrderFormLogService orderFormLogService;

    @Autowired
    private IAccessoryService accessoryService;

    @Autowired
    private IAlbumService albumService;
    
    @Autowired
    private StoreCreditStatusService storeCreditStatusService;
    
    @Autowired
    private AuthenticationService authenticationService;
    

    private HttpClass httpc; 

    @SecurityMapping(display = false, rsequence = 0, title = "店铺列表", value = "/admin/store_list.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
    @RequestMapping({"/admin/store_list.htm"})
    public ModelAndView store_list(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType){
        ModelAndView mv = new JModelAndView("admin/blue/store_list.html",
                                            this.configService.getSysConfig(), this.userConfigService
                                            .getUserConfig(), 0, request, response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))){
            url = CommUtil.getURL(request);
        }
        String params = "";
        StoreQueryObject qo = new StoreQueryObject(currentPage, mv, orderBy,
                orderType);
        WebForm wf = new WebForm();
        wf.toQueryPo(request, qo, Store.class, mv);
        IPageList pList = this.storeService.list(qo);
        CommUtil.saveIPageList2ModelAndView(url + "/admin/store_list.htm", "",
                                            params, pList, mv);
        List grades = this.storeGradeService.query(
                          "select obj from StoreGrade obj order by obj.sequence asc",
                          null, -1, -1);
        mv.addObject("grades", grades);
        
        try {
		    HttpClass hc = new HttpClass();
		    Long buyerId= SecurityUserHolder.getCurrentUser().getId();
		    String zwjs = hc.load("http://127.0.0.1:8081/ssm_project/selPro", "buyerId="+buyerId);

		    JSONArray jsonArray = JSONArray.fromObject(zwjs);
		    List<String> storeNameList = new LinkedList<>();
		    for(int i = 0; i < jsonArray.size(); i++) {
		        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
		        storeNameList.add(jsonObject.getString("storeName"));
		    }
		    mv.addObject("zwjson", storeNameList);
		} catch (Exception e) {
		    e.printStackTrace();
		}
        
        
        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "店铺添加1", value = "/admin/store_add.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
    @RequestMapping({"/admin/store_add.htm"})
    public ModelAndView store_add(HttpServletRequest request, HttpServletResponse response, String currentPage){
        ModelAndView mv = new JModelAndView("admin/blue/store_add.html",
                                            this.configService.getSysConfig(), this.userConfigService
                                            .getUserConfig(), 0, request, response);
        mv.addObject("currentPage", currentPage);

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "店铺添加2", value = "/admin/store_new.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
    @RequestMapping({"/admin/store_new.htm"})
    public ModelAndView store_new(HttpServletRequest request, HttpServletResponse response, String currentPage, String userName, String list_url, String add_url){
        ModelAndView mv = new JModelAndView("admin/blue/store_new.html",
                                            this.configService.getSysConfig(), this.userConfigService
                                            .getUserConfig(), 0, request, response);
        User user = this.userService.getObjByProperty("userName", userName);
        Store store = null;
        if (user != null)
            store = this.storeService.getObjByProperty("user.id", user.getId());
        if (user == null){
            mv = new JModelAndView("admin/blue/tip.html", this.configService
                                   .getSysConfig(), this.userConfigService.getUserConfig(), 0,
                                   request, response);
            mv.addObject("op_tip", "不存在该用户");
            mv.addObject("list_url", list_url);
        }else if (store == null){
            List scs = this.storeClassService
                       .query(
                           "select obj from StoreClass obj where obj.parent.id is null order by obj.sequence asc",
                           null, -1, -1);
            List areas = this.areaService.query(
                             "select obj from Area obj where obj.parent.id is null",
                             null, -1, -1);
            List grades = this.storeGradeService
                          .query(
                              "select obj from StoreGrade obj order by obj.sequence asc",
                              null, -1, -1);
            mv.addObject("grades", grades);
            mv.addObject("areas", areas);
            mv.addObject("scs", scs);
            mv.addObject("currentPage", currentPage);
            mv.addObject("user", user);
        }else{
            mv = new JModelAndView("admin/blue/tip.html", this.configService
                                   .getSysConfig(),
                                   this.userConfigService.getUserConfig(), 0, request,
                                   response);
            mv.addObject("op_tip", "该用户已经开通店铺");
            mv.addObject("list_url", add_url);
        }

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "店铺编辑", value = "/admin/store_edit.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
    @RequestMapping({"/admin/store_edit.htm"})
    public ModelAndView store_edit(HttpServletRequest request, HttpServletResponse response, String id,String userName, String currentPage){
        ModelAndView mv = new JModelAndView("admin/blue/store_edit.html",
                                            this.configService.getSysConfig(), this.userConfigService
                                            .getUserConfig(), 0, request, response);
        if ((id != null) && (!id.equals(""))){
            Store store = this.storeService.getObjById(Long.valueOf(id));
            List scs = this.storeClassService
                       .query(
                           "select obj from StoreClass obj where obj.parent.id is null order by obj.sequence asc",
                           null, -1, -1);
            List areas = this.areaService.query(
                             "select obj from Area obj where obj.parent.id is null",
                             null, -1, -1);

            String url="http://127.0.0.1:8081/ssm_project/select";
            String parm="userName="+userName;
            try {
//            	String jsonstr=httpc.load(url, parm);
//            	if (jsonstr.equals("wu")) {
//					throw new Exception();
//				}
//            	JSONObject json = JSONObject.fromObject(jsonstr);
//            	Authentication authentication=(Authentication)JSONObject.toBean(json, Authentication.class);
//            	mv.addObject("auth", authentication);
            	
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
			} catch (Exception e) {
				e.printStackTrace();
			}
            mv.addObject("areas", areas);
            mv.addObject("scs", scs);
            mv.addObject("obj", store);
            mv.addObject("currentPage", currentPage);
            mv.addObject("edit", Boolean.valueOf(true));
            if (store.getArea() != null){
                mv.addObject("area_info", this.areaManageTools
                             .generic_area_info(store.getArea()));
            }
        }

        return mv;
    }
    
    @RequestMapping({"/fileDownload1"})
    public void download(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //设置响应头和客户端保存文件名
        response.setCharacterEncoding("utf-8");
        response.setContentType("multipart/form-data");
        response.setHeader("Content-Disposition", "attachment;fileName=abc.sh");
        // jdk1.7新特性，不用关闭流！！
        try (InputStream inputStream = new FileInputStream("src/test.sh");      //打开本地文件流
             OutputStream os = response.getOutputStream()) {        //激活下载操作
            //循环写入输出流
            byte[] b = new byte[2048];
            int length;
            while ((length = inputStream.read(b)) > 0) {
                os.write(b, 0, length);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @SecurityMapping(display = false, rsequence = 0, title = "店铺保存", value = "/admin/store_save.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
    @RequestMapping({"/admin/store_save.htm"})
    public ModelAndView store_save(HttpServletRequest request, HttpServletResponse response, String id, String area_id, String sc_id, String grade_id, String user_id, String store_status, String currentPage, String cmd, String list_url, String add_url, String name,String store_starttime)
    throws Exception {
        WebForm wf = new WebForm();
        Store store = null;
        if (id.equals("")){
            store = (Store)wf.toPo(request, Store.class);
            store.setAddTime(new Date());
        }else{
            Store obj = this.storeService.getObjById(Long.valueOf(Long.parseLong(id)));
            store = (Store)wf.toPo(request, obj);
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(new Date());
        if((store_starttime==null)||("".equals(store_starttime))){
        	store.setStore_starttime(date);
        }else{
        	store.setStore_starttime(store_starttime);
        }
        Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
        store.setArea(area);
/*       StoreClass sc = this.storeClassService.getObjById(Long.valueOf(Long.parseLong(sc_id)));
        store.setSc(sc);*/
        store.setTemplate("default");
        if ((grade_id != null) && (!grade_id.equals(""))){
            store.setGrade(this.storeGradeService.getObjById(
                               Long.valueOf(Long.parseLong(grade_id))));
        }

        if ((store_status != null) && (!store_status.equals(""))) {
        	
        	//向用户发送短信
        	int sst = Integer.parseInt(store_status);
        	String storeTelephpone=store.getStore_telephone();
        	LoginViewAction action=new LoginViewAction();
            String content = "";
            if (sst==-1) {
            	content="您的店铺"+store.getStore_name()+"，开店申请已被拒绝！请重新提交。";
            	action.faxiaoxi(request, response, storeTelephpone, "SMS_160860417",store.getStore_name(),"");
    		}
            if (sst==2) {
            	content="您的店铺"+store.getStore_name()+"开店申请已通过审核！";
            	action.faxiaoxi(request, response, storeTelephpone, "SMS_160855380",store.getStore_name(),"");
    		}
            if (sst==3) {
            	content="尊敬的"+name+",您的店铺违规关闭,请联系管理员";
    		}
            if (name!=null && content !="") {
            	User toUser = this.userService.getObjByProperty("userName",name);
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
            //发消息结束
        	
           store.setStore_status(CommUtil.null2Int(store_status));
           HttpClass ca = new HttpClass();
           Authentication authentication = new Authentication();
           if("2".equals(store_status)){  
               authentication.setUserName(name);
               authentication.setExamine(2);
               String s = JSON.toJSONString(authentication);
               ca.load("http://127.0.0.1:8081/ssm_project/Audit1", "duixiang="+s);
           }if("1".equals(store_status)){
        	   authentication.setUserName(name);
               authentication.setExamine(1);
               String s = JSON.toJSONString(authentication);
               ca.load("http://127.0.0.1:8081/ssm_project/Audit1", "duixiang="+s);
           }if("3".equals(store_status)||"-1".equals(store_status)){
        	   authentication.setUserName(name);
               authentication.setExamine(3);
               String s = JSON.toJSONString(authentication);
               ca.load("http://127.0.0.1:8081/ssm_project/Audit1", "duixiang="+s);
           }
           authentication.setUserName(name);
           authentication.setExamine(Integer.parseInt(store_status));
           String s = JSON.toJSONString(authentication);
           ca.load("http://127.0.0.1:8081/ssm_project/Audit", "duixiang="+s);
           
        }else{
            store.setStore_status(2);
            
        }
/*        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = df.parse(new Date());*/

            
        if (store.isStore_recommend())
            store.setStore_recommend_time(new Date());
        else
            store.setStore_recommend_time(null);
        	store.setAddTime(new Date());
        if (id.equals(""))
            this.storeService.save(store);
        else{
            this.storeService.update(store);

            if(store_status=="2" || store_status.equals("2")){
            	/**店铺赊销开启状态  数据初始化  开始**/
                //判断赊销开启的状态表中是否已经存在数据
                Store storeStoreCreditStatus =this.storeService.getObjById(Long.parseLong(id));
                StoreCreditStatus storeCreditStatus=this.storeCreditStatusService.
                		getObjByProperty("storeId", String.valueOf(storeStoreCreditStatus.getId()));
                if (storeCreditStatus==null) {
                	StoreCreditStatus scs=new StoreCreditStatus();
                    scs.setStoreId(String.valueOf(storeStoreCreditStatus.getId()));
                    scs.setUserName(storeStoreCreditStatus.getUser().getUsername());
                    scs.setState("0");
                    scs.setAddTime(new Date());
                    this.storeCreditStatusService.save(scs);
                }
                /**店铺赊销开启状态  数据初始化  结束**/
                
                // 店铺审核通过，首营认证也审核通过
                Authentication  authentication=this.authenticationService.getObjByProperty("userName", storeStoreCreditStatus.getUser().getUsername());
                if (authentication.getExamine()!=2) {
                	authentication.setExamine(2);
                	this.authenticationService.update(authentication);
                }
            }
        }
        
        
        if ((user_id != null) && (!user_id.equals(""))){
            User user = this.userService.getObjById(Long.valueOf(Long.parseLong(user_id)));
            user.setStore(store);
            user.setUserRole("BUYER_SELLER");
            Map params = new HashMap();
            params.put("type", "SELLER");
            List roles = this.roleService.query(
                             "select obj from Role obj where obj.type=:type", params,
                             -1, -1);
            user.getRoles().addAll(roles);
            
            this.userService.update(user);
        }

        if ((!id.equals("")) && (store.getStore_status() == 3)){
            send_site_msg(request, "msg_toseller_store_closed_notify",
                          store);
        }
        ModelAndView mv = new JModelAndView("admin/blue/success.html",
                                            this.configService.getSysConfig(), this.userConfigService
                                            .getUserConfig(), 0, request, response);
        mv.addObject("list_url", list_url);
        mv.addObject("op_title", "保存店铺成功");
        if (add_url != null){
            mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
        }

        return mv;
    }

    private void send_site_msg(HttpServletRequest request, String mark, Store store) throws Exception {
        com.wemall.foundation.domain.Template template = this.templateService.getObjByProperty("mark", mark);
        if (template.isOpen()){
            String path = request.getRealPath("/") + "/vm/";
            PrintWriter pwrite = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(path + "msg.vm", false), "UTF-8"));
            pwrite.print(template.getContent());
            pwrite.flush();
            pwrite.close();

            Properties p = new Properties();
            p.setProperty("file.resource.loader.path", request
                          .getRealPath("/") +
                          "vm" + File.separator);
            p.setProperty("input.encoding", "UTF-8");
            p.setProperty("output.encoding", "UTF-8");
            Velocity.init(p);
            org.apache.velocity.Template blank = Velocity.getTemplate("msg.vm",
                                                 "UTF-8");
            VelocityContext context = new VelocityContext();
            context.put("reason", store.getViolation_reseaon());
            context.put("user", store.getUser());
            context.put("config", this.configService.getSysConfig());
            context.put("send_time", CommUtil.formatLongDate(new Date()));
            StringWriter writer = new StringWriter();
            blank.merge(context, writer);
            String content = writer.toString();
            User fromUser = this.userService.getObjByProperty("userName",
                            "admin");
            Message msg = new Message();
            msg.setAddTime(new Date());
            msg.setContent(content);
            msg.setFromUser(fromUser);
            msg.setTitle(template.getTitle());
            msg.setToUser(store.getUser());
            msg.setType(0);
            this.messageService.save(msg);
            CommUtil.deleteFile(path + "msg.vm");
            writer.flush();
            writer.close();
        }
    }

    @SecurityMapping(display = false, rsequence = 0, title = "店铺删除", value = "/admin/store_del.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
    @RequestMapping({"/admin/store_del.htm"})
    public String store_del(HttpServletRequest request, String mulitId) throws Exception {
        String[] ids = mulitId.split(",");
        for (String id : ids){
            if (!id.equals("")){
                Store store = this.storeService.getObjById(Long.valueOf(Long.parseLong(id)));
                if (store.getUser() != null)
                    store.getUser().setStore(null);
                    store.getUser().setUserRole("BUYER");
                List<GoodsCart> goodCarts;
                for (Goods goods : store.getGoods_list()){
                    Map map = new HashMap();
                    map.put("gid", goods.getId());
                    goodCarts = this.goodsCartService
                                .query(
                                    "select obj from GoodsCart obj where obj.goods.id = :gid",
                                    map, -1, -1);
                    Long ofid = null;
                    Map map2;
                    List goodCarts2;
                    for (GoodsCart gc : goodCarts){
                        gc.getGsps().clear();
                        this.goodsCartService.delete(gc.getId());
                        if (gc.getOf() != null){
                            map2 = new HashMap();
                            ofid = gc.getOf().getId();
                            map2.put("ofid", ofid);
                            goodCarts2 = this.goodsCartService
                                         .query(
                                             "select obj from GoodsCart obj where obj.of.id = :ofid",
                                             map2, -1, -1);
                            if (goodCarts2.size() == 0){
                                this.orderFormService.delete(ofid);
                            }
                        }
                    }

                    List<Evaluate> evaluates = goods.getEvaluates();
                    for (Evaluate e : evaluates){
                        this.evaluateService.delete(e.getId());
                    }
                    goods.getGoods_ugcs().clear();
                    Accessory acc = goods.getGoods_main_photo();
                    if (acc != null){
                        acc.setAlbum(null);
                        Album album = acc.getCover_album();
                        if (album != null){
                            album.setAlbum_cover(null);
                            this.albumService.update(album);
                        }
                        this.accessoryService.update(acc);
                    }
                    for (Accessory acc1 : goods.getGoods_photos()){
                        if (acc1 != null){
                            acc1.setAlbum(null);
                            Album album = acc1.getCover_album();
                            if (album != null){
                                album.setAlbum_cover(null);
                                this.albumService.update(album);
                            }
                            acc1.setCover_album(null);
                            this.accessoryService.update(acc1);
                        }
                    }
                    goods.getCombin_goods().clear();
                    this.goodsService.delete(goods.getId());
                }
                for (OrderForm of : store.getOfs()){
                    for (GoodsCart gc : of.getGcs()){
                        gc.getGsps().clear();
                        this.goodsCartService.delete(gc.getId());
                    }
                    this.orderFormService.delete(of.getId());
                }
                this.storeService.delete(CommUtil.null2Long(id));
                send_site_msg(request,
                              "msg_toseller_goods_delete_by_admin_notify", store);
            }
        }

        return "redirect:store_list.htm";
    }

    @SecurityMapping(display = false, rsequence = 0, title = "店铺AJAX更新", value = "/admin/store_ajax.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
    @RequestMapping({"/admin/store_ajax.htm"})
    public void store_ajax(HttpServletRequest request, HttpServletResponse response, String id, String fieldName, String value) throws ClassNotFoundException {
        Store obj = this.storeService.getObjById(Long.valueOf(Long.parseLong(id)));
        Field[] fields = Store.class.getDeclaredFields();
        BeanWrapper wrapper = new BeanWrapper(obj);
        Object val = null;
        for (Field field : fields){
            if (field.getName().equals(fieldName)){
                Class clz = Class.forName("java.lang.String");
                if (field.getType().getName().equals("int")){
                    clz = Class.forName("java.lang.Integer");
                }
                if (field.getType().getName().equals("boolean")){
                    clz = Class.forName("java.lang.Boolean");
                }
                if (!value.equals(""))
                    val = BeanUtils.convertType(value, clz);
               else{
                    val = Boolean.valueOf(
                              !CommUtil.null2Boolean(wrapper
                                                     .getPropertyValue(fieldName)));
                }
                wrapper.setPropertyValue(fieldName, val);
            }
        }
        if (fieldName.equals("store_recommend")){
            if (obj.isStore_recommend())
                obj.setStore_recommend_time(new Date());
           else{
                obj.setStore_recommend_time(null);
            }
        }
        this.storeService.update(obj);
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(val.toString());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @SecurityMapping(display = false, rsequence = 0, title = "卖家信用", value = "/admin/store_base.htm*", rtype = "admin", rname = "基本设置", rcode = "admin_store_base", rgroup = "店铺")
    @RequestMapping({"/admin/store_base.htm"})
    public ModelAndView store_base(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("admin/blue/store_base_set.html",
                                            this.configService.getSysConfig(), this.userConfigService
                                            .getUserConfig(), 0, request, response);

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "卖家信用保存", value = "/admin/store_set_save.htm*", rtype = "admin", rname = "基本设置", rcode = "admin_store_base", rgroup = "店铺")
    @RequestMapping({"/admin/store_set_save.htm"})
    public ModelAndView store_set_save(HttpServletRequest request, HttpServletResponse response, String id, String list_url, String store_allow){
        ModelAndView mv = new JModelAndView("admin/blue/success.html",
                                            this.configService.getSysConfig(), this.userConfigService
                                            .getUserConfig(), 0, request, response);
        SysConfig sc = this.configService.getSysConfig();
        sc.setStore_allow(CommUtil.null2Boolean(store_allow));
        Map map = new HashMap();
        for (int i = 0; i <= 29; i++){
            map.put("creditrule" + i, Integer.valueOf(CommUtil.null2Int(request
                    .getParameter("creditrule" + i))));
        }
        String creditrule = Json.toJson(map, JsonFormat.compact());
        sc.setCreditrule(creditrule);
        if (id.equals(""))
            this.configService.save(sc);
        else
            this.configService.update(sc);
        mv.addObject("list_url", list_url);
        mv.addObject("op_title", "保存店铺设置成功");

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "店铺模板", value = "/admin/store_template.htm*", rtype = "admin", rname = "店铺模板", rcode = "admin_store_template", rgroup = "店铺")
    @RequestMapping({"/admin/store_template.htm"})
    public ModelAndView store_template(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("admin/blue/store_template.html",
                                            this.configService.getSysConfig(), this.userConfigService
                                            .getUserConfig(), 0, request, response);
        mv.addObject("path", request.getRealPath("/"));
        mv.addObject("separator", File.separator);

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "店铺模板增加", value = "/admin/store_template_add.htm*", rtype = "admin", rname = "店铺模板", rcode = "admin_store_template", rgroup = "店铺")
    @RequestMapping({"/admin/store_template_add.htm"})
    public ModelAndView store_template_add(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView(
            "admin/blue/store_template_add.html", this.configService
            .getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "店铺模板保存", value = "/admin/store_template_save.htm*", rtype = "admin", rname = "店铺模板", rcode = "admin_store_template", rgroup = "店铺")
    @RequestMapping({"/admin/store_template_save.htm"})
    public ModelAndView store_template_save(HttpServletRequest request, HttpServletResponse response, String id, String list_url, String templates){
        ModelAndView mv = new JModelAndView("admin/blue/success.html",
                                            this.configService.getSysConfig(), this.userConfigService
                                            .getUserConfig(), 0, request, response);
        SysConfig sc = this.configService.getSysConfig();
        sc.setTemplates(templates);
        if (id.equals(""))
            this.configService.save(sc);
        else
            this.configService.update(sc);
        mv.addObject("list_url", list_url);
        mv.addObject("op_title", "店铺模板设置成功");

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "店铺升级列表", value = "/admin/store_gradelog_list.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
    @RequestMapping({"/admin/store_gradelog_list.htm"})
    public ModelAndView store_gradelog_list(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType, String store_name, String grade_id, String store_grade_status){
        ModelAndView mv = new JModelAndView(
            "admin/blue/store_gradelog_list.html", this.configService
            .getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))){
            url = CommUtil.getURL(request);
        }
        String params = "";
        StoreGradeLogQueryObject qo = new StoreGradeLogQueryObject(currentPage,
                mv, orderBy, orderType);
        if (!CommUtil.null2String(store_name).equals("")){
            qo.addQuery("obj.store.store_name",
                        new SysMap("store_name", "%" +
                                   store_name + "%"), "like");
            mv.addObject("store_name", store_name);
        }
        if (CommUtil.null2Long(grade_id).longValue() != -1L){
            qo.addQuery("obj.store.update_grade.id",
                        new SysMap("grade_id",
                                   CommUtil.null2Long(grade_id)), "=");
            mv.addObject("grade_id", grade_id);
        }
        if (!CommUtil.null2String(store_grade_status).equals("")){
            qo.addQuery("obj.store_grade_status",
                        new SysMap("store_grade_status",
                                   Integer.valueOf(CommUtil.null2Int(store_grade_status))), "=");
            mv.addObject("store_grade_status", store_grade_status);
        }
        IPageList pList = this.storeGradeLogService.list(qo);
        CommUtil.saveIPageList2ModelAndView(url + "/admin/store_list.htm", "",
                                            params, pList, mv);
        List grades = this.storeGradeService.query(
                          "select obj from StoreGrade obj order by obj.sequence asc",
                          null, -1, -1);
        mv.addObject("grades", grades);

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "店铺升级编辑", value = "/admin/store_gradelog_edit.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
    @RequestMapping({"/admin/store_gradelog_edit.htm"})
    public ModelAndView store_gradelog_edit(HttpServletRequest request, HttpServletResponse response, String currentPage, String id){
        ModelAndView mv = new JModelAndView(
            "admin/blue/store_gradelog_edit.html", this.configService
            .getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        StoreGradeLog obj = this.storeGradeLogService.getObjById(
                                CommUtil.null2Long(id));
        mv.addObject("obj", obj);
        mv.addObject("currentPage", currentPage);

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "店铺升级保存", value = "/admin/store_gradelog_save.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
    @RequestMapping({"/admin/store_gradelog_save.htm"})
    public ModelAndView store_gradelog_save(HttpServletRequest request, HttpServletResponse response, String currentPage, String id, String cmd, String list_url) throws Exception {
        WebForm wf = new WebForm();
        StoreGradeLog obj = this.storeGradeLogService.getObjById(
                                CommUtil.null2Long(id));
        StoreGradeLog log = (StoreGradeLog)wf.toPo(request, obj);
        log.setLog_edit(true);
        log.setAddTime(new Date());
        boolean ret = this.storeGradeLogService.update(log);
        if (ret){
            Store store = log.getStore();
            if (log.getStore_grade_status() == 1){
                store.setGrade(store.getUpdate_grade());
            }
            store.setUpdate_grade(null);
            this.storeService.update(store);
        }

        if (log.getStore_grade_status() == 1){
            send_site_msg(request,
                          "msg_toseller_store_update_allow_notify", log.getStore());
        }
        if (log.getStore_grade_status() == -1){
            send_site_msg(request,
                          "msg_toseller_store_update_refuse_notify", log.getStore());
        }
        send_site_msg(request, "msg_toseller_store_update_allow_notify",
                      log.getStore());
        ModelAndView mv = new JModelAndView("admin/blue/success.html",
                                            this.configService.getSysConfig(), this.userConfigService
                                            .getUserConfig(), 0, request, response);
        mv.addObject("list_url", list_url);
        mv.addObject("op_title", "保存店铺成功");

        return mv;
    }

    @SecurityMapping(display = false, rsequence = 0, title = "店铺升级日志查看", value = "/admin/store_gradelog_view.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
    @RequestMapping({"/admin/store_gradelog_view.htm"})
    public ModelAndView store_gradelog_view(HttpServletRequest request, HttpServletResponse response, String currentPage, String id){
        ModelAndView mv = new JModelAndView(
            "admin/blue/store_gradelog_view.html", this.configService
            .getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        StoreGradeLog obj = this.storeGradeLogService.getObjById(
                                CommUtil.null2Long(id));
        mv.addObject("obj", obj);
        mv.addObject("currentPage", currentPage);

        return mv;
    }
}




