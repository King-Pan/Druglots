package com.wemall.manage.buyer.action;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wemall.core.annotation.SecurityMapping;
import com.wemall.core.security.support.SecurityUserHolder;
import com.wemall.core.tools.WebForm;
import com.wemall.foundation.domain.Album;
import com.wemall.foundation.service.IAlbumService;

@Controller
public class AuthenticationAction {
	
	@Autowired
    private IAlbumService albumService;
	
	 	@SecurityMapping(display = false, rsequence = 0, title = "商家入驻信息认证", value = "/seller/Authentication_message.htm*", rtype = "Authentication", rname = "信息认证", rcode = "Authentication_message", rgroup = "信息认证")
	    @RequestMapping({"/seller/Authentication_message.htm"})
	    public String Authentication_message(HttpServletRequest request, HttpServletResponse response, String id, String currentPage){
	        WebForm wf = new WebForm();
	        Album album = null;
	        if (id.equals("")){
	            album = (Album)wf.toPo(request, Album.class);
	            album.setAddTime(new Date());
	        }else{
	            Album obj = this.albumService.getObjById(Long.valueOf(Long.parseLong(id)));
	            album = (Album)wf.toPo(request, obj);
	        }
	        album.setUser(SecurityUserHolder.getCurrentUser());
	        boolean ret = true;
	        if (id.equals(""))
	            ret = this.albumService.save(album);
	        else
	            ret = this.albumService.update(album);
	        
	        System.out.println("ret");

	        return "redirect:album.htm?currentPage=" + currentPage;
	    }

}
