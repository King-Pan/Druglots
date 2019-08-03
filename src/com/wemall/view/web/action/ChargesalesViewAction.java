package com.wemall.view.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.wemall.core.annotation.SecurityMapping;
import com.wemall.core.domain.virtual.SysMap;
import com.wemall.core.mv.JModelAndView;
import com.wemall.core.query.PageList;
import com.wemall.core.query.support.IPageList;
import com.wemall.core.security.support.SecurityUserHolder;
import com.wemall.core.tools.CommUtil;
import com.wemall.core.tools.HttpClass;
import com.wemall.foundation.domain.Goods;
import com.wemall.foundation.domain.Store;
import com.wemall.foundation.domain.StoreCreditStatus;
import com.wemall.foundation.domain.query.GoodsQueryObject;
import com.wemall.foundation.domain.query.StoreCreditStatusQueryObject;
import com.wemall.foundation.domain.query.StoreQueryObject;
import com.wemall.foundation.service.IGoodsService;
import com.wemall.foundation.service.IStoreService;
import com.wemall.foundation.service.ISysConfigService;
import com.wemall.foundation.service.IUserConfigService;
import com.wemall.foundation.service.StoreCreditStatusService;

@Controller
public class ChargesalesViewAction {
	
	@Autowired
	private ISysConfigService configService;

	@Autowired
	private IUserConfigService userConfigService;
	
	@Autowired
	private StoreCreditStatusService storeCreditStatusService;
	
	@Autowired
	private IGoodsService googsService;
	
	@Autowired
	private IStoreService storeService;
	
	
	@SuppressWarnings("null")
	@SecurityMapping(display = false, rsequence = 0, title = "赊销商城", value = "/charge_sales.htm*", rtype = "buyer", rname = "赊销商城", rcode = "charge_sales", rgroup = "赊销商城")
    @RequestMapping({"/charge_sales.htm"})
    public ModelAndView brand(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new JModelAndView("charge_sales.html", this.configService.getSysConfig(),
                                            this.userConfigService.getUserConfig(), 1, request, response);

        
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
        
        String url = this.configService.getSysConfig().getAddress();
        try{
        	//查询开通赊销的店铺
        	List pList = this.storeCreditStatusService.query(
        			"select obj from StoreCreditStatus obj where obj.state=1",null, -1, -1);
        	
        	List list=new ArrayList<>();
        	@SuppressWarnings("unchecked")
			List<Goods> goodslist=new ArrayList(); 
        	if(pList.size()>0){//开通赊销的店铺
        		for(int i=0;i<pList.size();i++){
        			StoreCreditStatus storeCreditStatus=(StoreCreditStatus) pList.get(i);
        			Long storeid=Long.valueOf(storeCreditStatus.getStoreId());
        			
        			//查询已经开通了赊销的店铺
        			Map map = new HashMap();
        			map.put("storeid", storeid);
        			map.put("store_status", 2);
        			List pListstoreQuery = this.storeService.query(
                			"select obj from Store obj where obj.id=:storeid and store_status=:store_status",map, -1, -1);
        			for(int a=0;a<pListstoreQuery.size();a++){
        				list.add(a,pListstoreQuery.get(a));
        				
        				//查询开通赊销店铺的商品
        				map.clear();
                        map.put("storeid", storeid);
                        map.put("goods_status", 0);
                    	List pListg = this.googsService.query(
                    			"select obj from Goods obj where obj.goods_store.id=:storeid and obj.goods_status=:goods_status ",map, -1, -1);
                    	
                    	for(int j=0;j<pListg.size();j++){
                    		Goods goods=(Goods) pListg.get(j);
                        	goodslist.add(goods);
                    	}
        			}
            	}
        	}
        	PageList pListstore =new PageList();
        	pListstore.setResult(list);
        	pListstore.setRowCount(list.size());
        	pListstore.setPages(list.size()/12);
        	pListstore.setCurrentPage(0);
        	pListstore.setPageSize(12);
			
        	CommUtil.saveIPageList2ModelAndView(url +"/charge_sales.html","", "", pListstore, mv);
            mv.addObject("goodslist", goodslist);
        }catch (Exception e){
        	e.printStackTrace();
        	mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
        	mv.addObject("op_title", "暂时没有开启赊销的店铺！");
        	mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
        }
       String userId= null;
       try{
    	   userId=String.valueOf(SecurityUserHolder.getCurrentUser().getId());
       }catch(Exception e){
    	   e.printStackTrace();
       }
       mv.addObject("userId", userId);
        
        return mv;

	}
}
