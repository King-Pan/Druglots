package com.wemall.manage.seller.action;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.wemall.core.mv.JModelAndView;
import com.wemall.core.tools.CommUtil;
import com.wemall.core.tools.HttpClass;
import com.wemall.foundation.domain.Authentication;
import com.wemall.foundation.service.ISysConfigService;
import com.wemall.foundation.service.IUserConfigService;
@Controller
public class WhiteCertificatioAction {
	
	 @Autowired
	 private ISysConfigService configService;
	 @Autowired
	 private IUserConfigService userConfigService;
	@Test
	public void cheshi() {
		
	}
	
	
	
	/**
    * 用户认证接口，调用了外部接口存储数据
    * @param request
    * @param response
    * @param url
    * @return
    */
	@RequestMapping("/user/login2.htm")
	public ModelAndView  rengzheng(@RequestParam("Features")int Features,String PublicAccount,@RequestParam("DutyParagraph")String DutyParagraph,@RequestParam("EndPurchaseDate")String EndPurchaseDate,@RequestParam("EndGSPDate")String EndGSPDate,@RequestParam("EndDrugDate")String EndDrugDate,@RequestParam("EndBusinessDate")String EndBusinessDate,@RequestParam("EndIDcardDate")String EndIDcardDate,@RequestParam("IDcardNumber")String IDcardNumber,@RequestParam("PurchaseNumber")String PurchaseNumber,@RequestParam("GSPNumber")String GSPNumber,@RequestParam("DrugNumber")String DrugNumber,@RequestParam("businessNumber")String businessNumber,@RequestParam("userName")String userName,HttpServletRequest request, HttpServletResponse response,@RequestParam("file1") MultipartFile file1 ,@RequestParam("file2") MultipartFile file2 ,@RequestParam("file3") MultipartFile file3 ,@RequestParam("file4") MultipartFile file4 , @RequestParam("file5") MultipartFile file5 ,@RequestParam("file6")  MultipartFile file6) {		
		Authentication authentication = new Authentication();
		 HttpClass hc = new	HttpClass();
		 //判断字段是否为空 
		
		 if (DutyParagraph == null || DutyParagraph.length() <= 0 || EndPurchaseDate == null || EndPurchaseDate.length() <= 0 || EndGSPDate == null || EndGSPDate.length() <= 0 || EndDrugDate == null || EndDrugDate.length() <= 0 || EndBusinessDate == null || EndBusinessDate.length() <= 0 || EndIDcardDate == null || EndIDcardDate.length() <= 0 || IDcardNumber == null || IDcardNumber.length() <= 0 || PurchaseNumber == null || PurchaseNumber.length() <= 0 || GSPNumber == null || GSPNumber.length() <= 0 || DrugNumber == null || DrugNumber.length() <= 0 || businessNumber == null || businessNumber.length() <= 0 || userName == null || userName.length() <= 0 ) {
			 return Jump("error.html", "认证信息不完整", request, response);
		    }
		 //判断如果是卖家，单独判断对公账号是否为空,
		 if (Features ==2) {
			
        	if (PublicAccount == null || PublicAccount.length() <= 0 ) {
        		return Jump("error.html", "认证信息不完整", request, response);
			}
			}
		 //开始认证
		 try {
			 //查询用户，判断是否认证过
			 String Load = hc.load("http://127.0.0.1:8081/ssm_project/select", "userName="+userName);
			 //如果有就是修改数据
			 if (Load!=null&&Load != "" ) {
				 Authentication auth = JSON.parseObject(Load, Authentication.class);
				 
				 //调用保存方法保存图片，六张图片保存,如果改了的就存，没改就原样
				 if (StringUtils.isNotBlank(file1.getOriginalFilename())) {
					 String s1 =saveFile(request, file1);//营业执照
					 auth.setBusinessLicense(s1);//营业执照
				 }
				 if (StringUtils.isNotBlank(file2.getOriginalFilename())) {
					 String s2 =saveFile(request, file2);//药品经营许可证
					  auth.setDrugLicense(s2);//药品经营许可证
				 }
				 if (StringUtils.isNotBlank(file3.getOriginalFilename())) {
					 String s3 =saveFile(request, file3);//gsp证书
					 auth.setgSPCertificate(s3);//gsp证书
				 }
				 if (StringUtils.isNotBlank(file4.getOriginalFilename())) {
					 String s4 =saveFile(request, file4);//采购委托书
					 auth.setPurchaseOrders(s4);//采购委托书
				 }
				 if (StringUtils.isNotBlank(file5.getOriginalFilename())) {
					 String s5 =saveFile(request, file5);//身份证复印件正反面
					 auth.setiDcard(s5);//身份证复印件正反面
				 }
				 if (StringUtils.isNotBlank(file6.getOriginalFilename())) {
					  String s6 =saveFile(request, file6);//手持身份证
					  auth.setHandIDcard(s6);//手持身份证
				 }
	               
	              //修改对象里面的值
	                auth.setBusinessNumber(businessNumber);//营业执照号码
	                auth.setEndBusinessDate(EndBusinessDate);//营业执照有效截止日期
	                auth.setDrugNumber(DrugNumber);//药品经营许可证号码
	                auth.setEndDrugDate(EndDrugDate);//药品经营许可证有效截止日期
	                auth.setgSPNumber(GSPNumber);//GSP证号码
	                auth.setEndGSPDate(EndGSPDate);//GSP证有效截止日期
	                auth.setPurchaseNumber(PurchaseNumber);//采购委托书号码
	                auth.setEndPurchaseDate(EndPurchaseDate);//采购委托书有效截止日期
	                auth.setiDcardNumber(IDcardNumber);//身份证号码
	                auth.setEndIDcardDate(EndIDcardDate);//身份证有效截止日期
	                auth.setDutyParagraph(DutyParagraph);//税号
	                auth.setPublicAccount(PublicAccount);//对公账号
	                auth.setTemiiOpeningStatus(0);//特米白条
	                if (auth.getFeatures()==2) {
						Features = 2;
					}
	                authentication.setFeatures(Features);//1是买家2是卖家
	                
	               
	                auth.setNewtemiiOpeningDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//当前时间
	                
	                //对象转json字符串
	                String s = JSON.toJSONString(auth);
	                //调用修改接口修改数据
	               String fh= hc.load("http://127.0.0.1:8081/ssm_project/Updateobj1", "dx="+s);
	               if (fh != null) {
	            	   return Jump("success.html", "白条申请提交成功,请等待审核", request, response);
				    }else {
				    	 return Jump("error.html", "白条申请提交失败", request, response);
					}
				 
			 }
			
           //判断file不能为空
			 if (StringUtils.isBlank(file1.getOriginalFilename())||StringUtils.isBlank(file2.getOriginalFilename())||StringUtils.isBlank(file3.getOriginalFilename())||StringUtils.isBlank(file4.getOriginalFilename())||StringUtils.isBlank(file5.getOriginalFilename())||StringUtils.isBlank(file6.getOriginalFilename())) {
				 return Jump("error.html", "认证信息不完整", request, response);
			  }
			 
               //调用保存方法保存图片，六张图片保存
               String s1 =saveFile(request, file1);//营业执照
               String s2 =saveFile(request, file2);//药品经营许可证
               String s3 =saveFile(request, file3);//gsp证书
               String s4 =saveFile(request, file4);//采购委托书
               String s5 =saveFile(request, file5);//身份证复印件正反面
               String s6 =saveFile(request, file6);//手持身份证

               //存入对象
               authentication.setUserName(userName);//用户id，需要页面传递
               authentication.setExamine(1);//认证状态
               authentication.setBusinessNumber(businessNumber);//营业执照号码
               authentication.setEndBusinessDate(EndBusinessDate);//营业执照有效截止日期
               authentication.setDrugNumber(DrugNumber);//药品经营许可证号码
               authentication.setEndDrugDate(EndDrugDate);//药品经营许可证有效截止日期
               authentication.setgSPNumber(GSPNumber);//GSP证号码
               authentication.setEndGSPDate(EndGSPDate);//GSP证有效截止日期
               authentication.setPurchaseNumber(PurchaseNumber);//采购委托书号码
               authentication.setEndPurchaseDate(EndPurchaseDate);//采购委托书有效截止日期
               authentication.setiDcardNumber(IDcardNumber);//身份证号码
               authentication.setEndIDcardDate(EndIDcardDate);//身份证有效截止日期
               authentication.setDutyParagraph(DutyParagraph);//税号
               authentication.setPublicAccount(PublicAccount);//对公账号
               authentication.setFeatures(Features);//1是买家2是卖家
           

               
               //将六张图片存入对象
               authentication.setBusinessLicense(s1);//营业执照
               authentication.setDrugLicense(s2);//药品经营许可证
               authentication.setgSPCertificate(s3);//gsp证书
               authentication.setPurchaseOrders(s4);//采购委托书
               authentication.setiDcard(s5);//身份证复印件正反面
               authentication.setHandIDcard(s6);//手持身份证
               authentication.setNewAuthenticationDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//当前时间
               
               //转json字符串
               String s = JSON.toJSONString(authentication);
               String filesUpload = hc.load("http://127.0.0.1:8081/ssm_project/filesUpload", "duixiang="+s);
              //成功跳转成功页面,买家
               if (authentication.getFeatures()==1) {
               if (filesUpload.equals("cg")) {
                 return Jump("success.html", "用户认证资料提交成功，请等待审核", request, response);
			   }else {
	              return Jump("error.html", "认证信息提交失败", request, response);
			   }
               }else {
               	return null;
               }
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//失败跳转失败页面
				
                return Jump("error.html", "认证信息提交错误", request, response);
			     
			}
		
		
		
		
	}
	
	
	/**
    * 用户认证接口，调用了外部接口存储数据,该接口为店铺开通调用，因为其不能跳转页面
    * @param request
    * @param response
    * @param url
    * @return
    */
	
	public String  rengzheng2(@RequestParam("Features")int Features,String PublicAccount,@RequestParam("DutyParagraph")String DutyParagraph,@RequestParam("EndPurchaseDate")String EndPurchaseDate,@RequestParam("EndGSPDate")String EndGSPDate,@RequestParam("EndDrugDate")String EndDrugDate,@RequestParam("EndBusinessDate")String EndBusinessDate,@RequestParam("EndIDcardDate")String EndIDcardDate,@RequestParam("IDcardNumber")String IDcardNumber,@RequestParam("PurchaseNumber")String PurchaseNumber,@RequestParam("GSPNumber")String GSPNumber,@RequestParam("DrugNumber")String DrugNumber,@RequestParam("businessNumber")String businessNumber,@RequestParam("userName")String userName,HttpServletRequest request, HttpServletResponse response,@RequestParam("file1") MultipartFile file1 ,@RequestParam("file2") MultipartFile file2 ,@RequestParam("file3") MultipartFile file3 ,@RequestParam("file4") MultipartFile file4 , @RequestParam("file5") MultipartFile file5 ,@RequestParam("file6")  MultipartFile file6) {
		 ModelAndView mv = null;
		Authentication authentication = new Authentication();
		 HttpClass hc = new	HttpClass();
		 //判断字段是否为空 
		 if (PublicAccount == null || PublicAccount.length() <= 0 || DutyParagraph == null || DutyParagraph.length() <= 0 || EndPurchaseDate == null || EndPurchaseDate.length() <= 0 || EndGSPDate == null || EndGSPDate.length() <= 0 || EndDrugDate == null || EndDrugDate.length() <= 0 || EndBusinessDate == null || EndBusinessDate.length() <= 0 || EndIDcardDate == null || EndIDcardDate.length() <= 0 || IDcardNumber == null || IDcardNumber.length() <= 0 || PurchaseNumber == null || PurchaseNumber.length() <= 0 || GSPNumber == null || GSPNumber.length() <= 0 || DrugNumber == null || DrugNumber.length() <= 0 || businessNumber == null || businessNumber.length() <= 0 || userName == null || userName.length() <= 0 ) {
			 return "zd";
		    }
		
		 //开始认证
		 try {
			 
			 //查询用户，判断是否认证过
			 String Load = hc.load("http://127.0.0.1:8081/ssm_project/select", "userName="+userName);
			 //如果有就是修改数据
			 if (Load!=null && Load != "" ) {
				 Authentication auth = JSON.parseObject(Load, Authentication.class);
				 
				 //调用保存方法保存图片，六张图片保存,如果改了的就存，没改就原样
				 if (StringUtils.isNotBlank(file1.getOriginalFilename())) {
					 String s1 =saveFile(request, file1);//营业执照
					 auth.setBusinessLicense(s1);//营业执照
				 }
				 if (StringUtils.isNotBlank(file2.getOriginalFilename())) {
					 String s2 =saveFile(request, file2);//药品经营许可证
					  auth.setDrugLicense(s2);//药品经营许可证
				 }
				 if (StringUtils.isNotBlank(file3.getOriginalFilename())) {
					 String s3 =saveFile(request, file3);//gsp证书
					 auth.setgSPCertificate(s3);//gsp证书
				 }
				 if (StringUtils.isNotBlank(file4.getOriginalFilename())) {
					 String s4 =saveFile(request, file4);//采购委托书
					 auth.setPurchaseOrders(s4);//采购委托书
				 }
				 if (StringUtils.isNotBlank(file5.getOriginalFilename())) {
					 String s5 =saveFile(request, file5);//身份证复印件正反面
					 auth.setiDcard(s5);//身份证复印件正反面
				 }
				 if (StringUtils.isNotBlank(file6.getOriginalFilename())) {
					  String s6 =saveFile(request, file6);//手持身份证
					  auth.setHandIDcard(s6);//手持身份证
				 }
	               
	              //修改对象里面的值
	                auth.setBusinessNumber(businessNumber);//营业执照号码
	                auth.setEndBusinessDate(EndBusinessDate);//营业执照有效截止日期
	                auth.setDrugNumber(DrugNumber);//药品经营许可证号码
	                auth.setEndDrugDate(EndDrugDate);//药品经营许可证有效截止日期
	                auth.setgSPNumber(GSPNumber);//GSP证号码
	                auth.setEndGSPDate(EndGSPDate);//GSP证有效截止日期
	                auth.setPurchaseNumber(PurchaseNumber);//采购委托书号码
	                auth.setEndPurchaseDate(EndPurchaseDate);//采购委托书有效截止日期
	                auth.setiDcardNumber(IDcardNumber);//身份证号码
	                auth.setEndIDcardDate(EndIDcardDate);//身份证有效截止日期
	                auth.setDutyParagraph(DutyParagraph);//税号
	                auth.setPublicAccount(PublicAccount);//对公账号
	                auth.setTemiiOpeningStatus(2);//特米白条
	                if (auth.getFeatures()==2) {
						Features = 2;
					}
	                authentication.setFeatures(Features);//1是买家2是卖家
	                
	               
	                auth.setNewtemiiOpeningDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//当前时间
	                
	                //对象转json字符串
	                String s = JSON.toJSONString(auth);
	                //调用修改接口修改数据
	               String fh= hc.load("http://127.0.0.1:8081/ssm_project/Updateobj1", "dx="+s);
	               if (fh == null) {
	            	  return "xg";		    }
	               
	               return "cg";
				 
			 }
			
           //判断file不能为空
			 if (StringUtils.isBlank(file1.getOriginalFilename())||StringUtils.isBlank(file2.getOriginalFilename())||StringUtils.isBlank(file3.getOriginalFilename())||StringUtils.isBlank(file4.getOriginalFilename())||StringUtils.isBlank(file5.getOriginalFilename())||StringUtils.isBlank(file6.getOriginalFilename())) {
				 return "tp";
			  }
			 
               //调用保存方法保存图片，六张图片保存
               String s1 =saveFile(request, file1);//营业执照
               String s2 =saveFile(request, file2);//药品经营许可证
               String s3 =saveFile(request, file3);//gsp证书
               String s4 =saveFile(request, file4);//采购委托书
               String s5 =saveFile(request, file5);//身份证复印件正反面
               String s6 =saveFile(request, file6);//手持身份证

               //存入对象
               authentication.setUserName(userName);//用户id，需要页面传递
               authentication.setExamine(1);//认证状态
               authentication.setBusinessNumber(businessNumber);//营业执照号码
               authentication.setEndBusinessDate(EndBusinessDate);//营业执照有效截止日期
               authentication.setDrugNumber(DrugNumber);//药品经营许可证号码
               authentication.setEndDrugDate(EndDrugDate);//药品经营许可证有效截止日期
               authentication.setgSPNumber(GSPNumber);//GSP证号码
               authentication.setEndGSPDate(EndGSPDate);//GSP证有效截止日期
               authentication.setPurchaseNumber(PurchaseNumber);//采购委托书号码
               authentication.setEndPurchaseDate(EndPurchaseDate);//采购委托书有效截止日期
               authentication.setiDcardNumber(IDcardNumber);//身份证号码
               authentication.setEndIDcardDate(EndIDcardDate);//身份证有效截止日期
               authentication.setDutyParagraph(DutyParagraph);//税号
               authentication.setPublicAccount(PublicAccount);//对公账号
               authentication.setFeatures(Features);//1是买家2是卖家
               

               
               //将六张图片存入对象
               authentication.setBusinessLicense(s1);//营业执照
               authentication.setDrugLicense(s2);//药品经营许可证
               authentication.setgSPCertificate(s3);//gsp证书
               authentication.setPurchaseOrders(s4);//采购委托书
               authentication.setiDcard(s5);//身份证复印件正反面
               authentication.setHandIDcard(s6);//手持身份证
               authentication.setNewAuthenticationDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//当前时间
               
               //转json字符串
               String s = JSON.toJSONString(authentication);
               String filesUpload = hc.load("http://127.0.0.1:8081/ssm_project/filesUpload", "duixiang="+s);
              //成功跳转成功页面,买家
               if (authentication.getFeatures()==1) {
               if (!filesUpload.equals("cg")) {
               	 return "tp";
			   }
             }
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//失败跳转失败页面
				return "cw";
			     
			}
		 return "cg";
		
		
		
		
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
	
	
	
	
	/***
    * 保存文件，私有调用
    * @param file
    * @return
    */
   private String saveFile(HttpServletRequest request, MultipartFile file) {
   	// 判断文件是否为空
       if (!file.isEmpty()) {
           try {
           	String sdf= new SimpleDateFormat("yyyy-MM-dd").format(new Date());
               
               String separator = File.separator;
               String picPath2 = request.getSession().getServletContext().getRealPath("")+File.separator+"upload"+ File.separator;
               String directory = picPath2 + "wyInFile" + separator+sdf + "/";
               String newPicName = "";
               if (file.getSize() != 0) {
               String originalFileNameLeft = file.getOriginalFilename();
                   // 新的图片名称
                   newPicName = UUID.randomUUID()
                           + originalFileNameLeft
                                   .substring(originalFileNameLeft
                                           .lastIndexOf("."));
                   // 新图片，写入磁盘
                   File f = new File(directory, newPicName);
                   if (!f.exists()) {
                       // 先创建文件所在的目录
                       f.getParentFile().mkdirs();
                   }
                   file.transferTo(f);
               }
               return newPicName;
           } catch (Exception e) {
               e.printStackTrace();
           }
       }
       return "";

   }
}
