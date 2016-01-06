package controllers.postmanuser;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;
import models.Postcompany;
import models.postcontent.Postcontent;
import models.postcontent.PostcontentImg;
import models.postman.PostmanUser;

import com.avaje.ebean.PagedList;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;

import form.PostmanUserForm;
import models.postman.PostmanUserLocationLog;
import play.Configuration;
import play.Logger;
import play.api.libs.json.JsValue;
import play.api.libs.json.jackson.JacksonJson;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.twirl.api.Html;
import services.PostcompanyService;
import services.ServiceFactory;
import services.SmsService;
import services.cache.ICacheService;
import services.postmanuser.PostmanUserService;
import utils.*;

/**
 * 
 * <p>Title: PostmanuserController.java</p> 
 * <p>Description: 用户信息管理</p> 
 * <p>Company: higegou</p> 
 * @author  ctt
 * date  2015年11月18日  下午7:45:20
 * @version
 */
public class PostmanUserController extends Controller {
	public static final Logger.ALogger logger = Logger.of(PostmanUserController.class);
	
	ICacheService cache = ServiceFactory.getCacheService();
	private static final PostmanUserService postmanUserService = ServiceFactory.getPostmanUserService(); 
	private static final PostcompanyService postcompanyService = ServiceFactory.getPostcompanyService(); 
	private static final SmsService smsService = ServiceFactory.getSmsService();
	
	private final Form<PostmanUserForm> postmanUserForm = Form.form(PostmanUserForm.class);
	/**
	 * 
	 * <p>Title: list</p> 
	 * <p>Description: 任务信息列表</p> 
	 * @return
	 */
	public Result postmanUserManage(){
		Form<PostmanUserForm> form = postmanUserForm.bindFromRequest();
		PostmanUserForm formPage = new PostmanUserForm();
        if (!form.hasErrors()) {
        	formPage = form.get();
        	formPage = dealFormPageWithKeyword(formPage);
        }
        List<Postcompany> postcompanys = postcompanyService.findAllPostcompany();
		PagedList<PostmanUser> postmanUserPage = postmanUserService.findPostmanUserList(formPage);
		return ok(views.html.postmanuser.postmanUserManage.render(postmanUserPage,Html.apply(Constants.PostmanStatus.stas2HTML(formPage.sta)),Html.apply(Constants.companyList2Html(postcompanys, formPage.companyid))));
	}
	
	/**
	 * 
	 * <p>Title: dealFormPageWithKeyword</p> 
	 * <p>Description: 处理用户针对关键字的搜索</p> 
	 * @param formPage
	 * @return
	 */
	private PostmanUserForm dealFormPageWithKeyword(PostmanUserForm formPage) {
		if(!Strings.isNullOrEmpty(formPage.keyword)){
			//检查关键字是否为手机号
			if(StringUtil.checkPhone(formPage.keyword)){
				formPage.phone=formPage.keyword;
			}else{
				formPage.nickname=formPage.keyword;
			}
		}
		return formPage;
	}

	/**
	 * 跳转至编辑用户信息页面
	 * @return
	 */
	public Result newOrUpdatePostmanUser(){
		int id = Numbers.parseInt(Form.form().bindFromRequest().get("id"), 0);
		PostmanUser postmanUser = new PostmanUser();
		if(id!=0L){
			postmanUser = postmanUserService.findPostmanUserById(id);
		}
		List<Postcompany> postcompanys = postcompanyService.findAllPostcompany();
		return ok(views.html.postmanuser.newOrUpdatePostmanUser.render(id,postmanUser,Html.apply(Constants.companyList2Html(postcompanys, postmanUser.companyid))));
    }
	
	/**
	 * 保存用户信息
	 * @return
	 */
	public Result savePostmanUser(){
		int id = Numbers.parseInt(Form.form().bindFromRequest().get("id"), 0);
		Form<PostmanUserForm> form = postmanUserForm.bindFromRequest();
		PostmanUserForm formPage = new PostmanUserForm();
        if (!form.hasErrors()) {
        	formPage = form.get();
        }
		if(id!=0L){
			PostmanUser postmanUser = postmanUserService.findPostmanUserById(id);
			postmanUser.setNickname(formPage.nickname);
			postmanUser.setPhone(formPage.phone);
			postmanUser.setCardidno(formPage.cardidno);
			postmanUser.setCompanyid(formPage.companyid);
			postmanUser.setSubstation(formPage.substation);
			postmanUser.setStaffid(formPage.staffid);
			postmanUserService.update(postmanUser);
		}
		return redirect("/postmanuser/postmanUserManage");
	}
	
	/**
	 * 
	 * <p>Title: changePostmanUserSta</p> 
	 * <p>Description: 审批用户资质</p> 
	 * @return
	 */
	public Result changePostmanUserSta(){
		String id = AjaxHelper.getHttpParam(request(), "id");//
		String sta = AjaxHelper.getHttpParam(request(), "sta");//
		dealPostmanUserWithChangeSta(id,sta);
		return redirect("/postmanuser/postmanUserManage");
	}
	/**
	 * 
	 * <p>Title: piliangChangePostmanUserSta</p> 
	 * <p>Description: 批量审批用户资质</p> 
	 * @return
	 */
	public Result piliangChangePostmanUserSta(){
		String puids = AjaxHelper.getHttpParam(request(), "puids");//
		String sta = AjaxHelper.getHttpParam(request(), "sta");//
		if(!Strings.isNullOrEmpty(puids)){
			String[] puid = puids.split(",");
			for (String id : puid) {
				dealPostmanUserWithChangeSta(id,sta);
			}
		}
		return redirect("/postmanuser/postmanUserManage");
	}
	
	/**
	 * 
	 * <p>Title: dealPostmanUserWithChangeSta</p> 
	 * <p>Description: 公共方法，用于处理用户资质审核操作</p> 
	 * @param id
	 * @param sta
	 */
	public void dealPostmanUserWithChangeSta(String id, String sta){
		PostmanUser postmanUser = postmanUserService.findPostmanUserById(Numbers.parseInt(id, 0));
		if(postmanUser!=null&&!"1".equals(postmanUser.getSta())){
			postmanUser.setSta(sta);
			postmanUser.setDateUpd(new Date());
			postmanUser = postmanUserService.update(postmanUser);
			if("1".equals(sta)){
				//用户审核通过，调用存储过程初始化数据
				postmanUserService.initPostmanUser(postmanUser);
				String smsargs = Configuration.root().getString("postmanuser.success.smsargs");
				String tpl_id = Configuration.root().getString("postmanuser.success.tpl_id");
				smsService.saveSmsInfo(smsargs,postmanUser.getPhone(), tpl_id, "2");
			}else if("2".equals(sta)){
				String smsargs = Configuration.root().getString("postmanuser.failed.smsargs");
				String tpl_id = Configuration.root().getString("postmanuser.failed.tpl_id");
				smsService.saveSmsInfo(smsargs,postmanUser.getPhone(), tpl_id, "1");
			}
		}
	}

	/**
	 * 用户位置信息
	 *
	 * @return
	 */
	public Result userLocation(int postmanid) {
		return ok(views.html.postmanuser.userLocation.render(postmanid));
	}
	public Result userLocationData(int postmanid) {
		List<PostmanUserLocationLog> postmanUserLocationLogList = postmanUserService.findPostmanUserLocationLogByUid(postmanid);
		List<Object[]> result = Lists.newArrayList();
		List<Object[]> resultTemp = Lists.newArrayList();
		for(PostmanUserLocationLog postmanUserLocationLog:postmanUserLocationLogList){
			Object[] temp2 = new Object[2];
			temp2[0] = postmanUserLocationLog.getLontitude();
			temp2[1] = postmanUserLocationLog.getLatitude();
			if(resultTemp.contains(temp2)){
				continue;
			}else{
				resultTemp.add(temp2);
				Object[] temp = new Object[3];
				temp[0] = postmanUserLocationLog.getLontitude();
				temp[1] = postmanUserLocationLog.getLatitude();
				temp[2] = Dates.formatDateTime(postmanUserLocationLog.getDateNew()) ;
				result.add(temp);
			}


		}
		return ok(Json.toJson(result));
	}
}
