package com.wemall.core.tools;

import java.io.File;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

public class FileUtils {

	public static String fileUploadPage( MultipartFile file , HttpServletRequest request ,String max) throws Exception {
        //判断文件是否为空
        if(file !=null){
            	// 上传文件路径
        			Long min = new Date().getTime();
        			String filename = file.getOriginalFilename();
        			// 上传文件路径
        			String URL="/upada/"+ min + "/";
        			String path = request.getSession().getServletContext().getRealPath(URL);
        			File filepath = new File(path, filename);
        			// 上传文件名
        			if (!filepath.getParentFile().exists()) {
        				filepath.getParentFile().mkdirs();
        			}
        			// 判断路径是否存在，如果不存在就创建一个
        			// 将上传文件保存到一个目标文件当中
        			file.transferTo(new File(path + File.separator + filename));
            return  URL + filename;
        }else{
            System.out.println("文件上传异常");
            return "500";
        }
    }
}
