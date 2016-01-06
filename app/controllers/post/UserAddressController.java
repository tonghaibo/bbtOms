package controllers.post;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import models.admin.AdminUser;
import models.user.UserAddress;
import play.Configuration;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.twirl.api.Html;
import services.ServiceFactory;
import services.cache.ICacheService;
import services.user.UserAddressService;
import utils.AjaxHelper;
import utils.Constants;
import utils.Numbers;
import utils.OSSUtils;
import utils.StringUtil;

import com.avaje.ebean.PagedList;
import com.google.common.base.Strings;

import controllers.admin.AdminAuthenticated;
import controllers.admin.BaseAdminController;
import form.UserAddressForm;


/**
 * 
 * <p>Title: UserAddressController.java</p> 
 * <p>Description: 地址管理</p> 
 * <p>Company: higegou</p> 
 * @author  ctt
 * date  2015年12月22日  下午4:47:13
 * @version
 */
public class UserAddressController extends BaseAdminController {
	public static final Logger.ALogger logger = Logger.of(UserAddressController.class);
	
	ICacheService cache = ServiceFactory.getCacheService();
	private static final UserAddressService userAddressService = UserAddressService.instance; 
	
	private final Form<UserAddressForm> userAddressForm = Form.form(UserAddressForm.class);
	/**
	 * 
	 * <p>Title: list</p> 
	 * <p>Description: App升级信息列表</p> 
	 * @return
	 */
	public Result userAddressManage(){
		Form<UserAddressForm> form = userAddressForm.bindFromRequest();
		UserAddressForm formPage = new UserAddressForm();
		logger.info("log page1:"+form.toString());
        if (!form.hasErrors()) {
        	formPage = form.get();
        }
		PagedList<UserAddress> userAddressPage = userAddressService.findUserAddressList(formPage);
		return ok(views.html.post.userAddressManage.render(userAddressPage));
	}
	
	/**
	 * 跳转至新增App升级添加页面
	 * @return
	 */
	public Result newOrUpdateUserAddress(){
		int id = Numbers.parseInt(Form.form().bindFromRequest().get("id"), 0);
		
		UserAddress userAddress = new UserAddress();
		int typ = -1;
		if(id!=0L){
			userAddress = userAddressService.findUserAddressById(id);
			typ = userAddress.getTyp();
		}
		return ok(views.html.post.newOrUpdateUserAddress.render(id,userAddress,Html.apply(Constants.UserAddressTyps.typs2HTML(typ))));
    }
	
	/**
	 * 保存APP升级信息
	 * @return
	 */
	@AdminAuthenticated
	public Result saveUserAddress(){
		int id = Numbers.parseInt(Form.form().bindFromRequest().get("id"), 0);
		Form<UserAddressForm> form = userAddressForm.bindFromRequest();
		UserAddressForm formPage = new UserAddressForm();
		logger.info("log page1:"+form.toString());
        if (!form.hasErrors()) {
        	formPage = form.get();
        }
		UserAddress userAddress = new UserAddress();
		if(id!=0L){
			userAddress = userAddressService.findUserAddressById(id);
			userAddress = dealUserAddress(userAddress,formPage);
			userAddress.setDate_upd(new Date());
			userAddress = userAddressService.update(userAddress);
		}else{
			userAddress = dealUserAddress(userAddress,formPage);
			userAddress.setDate_new(new Date());
			userAddress.setDate_upd(new Date());
			//设置图标
			userAddress = userAddressService.save(userAddress);
		}
		return redirect("/post/userAddressManage");
	}
	
	/**
	 * 
	 * <p>Title: delUserAddress</p> 
	 * <p>Description: 删除相应的用户信息</p> 
	 * @return
	 */
	public Result delUserAddress(){
		int id = Numbers.parseInt(Form.form().bindFromRequest().get("id"), 0);
		if(id!=0L){
			userAddressService.delUserAddress(id);
		}
		return redirect("/post/userAddressManage");
	}
	
	private UserAddress dealUserAddress(UserAddress userAddress, UserAddressForm form) {
		AdminUser adminUser =getCurrentAdminUser();
		userAddress.setTyp(form.typ);
		userAddress.setUsername(form.username);
		userAddress.setPhone(form.phone);
		userAddress.setAddress(form.province+form.address);
		userAddress.setLongs(form.longs);
		userAddress.setLat(form.lat);
		userAddress.setUid(adminUser.getId().intValue());
		return userAddress;
	}
	
	/**
	 * 
	 * <p>Title: getUserAddress</p> 
	 * <p>Description: 获取指定的联系人信息</p> 
	 * @param userAddressId
	 * @return
	 */
	public Result getUserAddress(Integer userAddressId){
		UserAddress userAddress = userAddressService.findUserAddressById(userAddressId);
		return ok(Json.toJson(userAddress));
	}
}
