package controllers.app;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.avaje.ebean.PagedList; 
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import com.sun.jndi.toolkit.ctx.Continuation;

import form.AppVersionForm;
import form.PostcontentForm;
import models.app.AppVersion;
import models.postcontent.Postcontent;
import models.postcontent.PostcontentDetail;
import models.postcontent.PostcontentImg;
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
import services.app.AppVersionService;
import services.postcontent.PostcontentService;
import utils.AjaxHelper;
import utils.Constants;
import utils.Numbers;
import utils.OSSUtils;
import utils.StringUtil;

/**
 * 
 * <p>Title: AppVersionController.java</p> 
 * <p>Description:App升级管理</p> 
 * <p>Company: higegou</p>
 * ostype   iOS设为0  Android设为1
	isforced 0为普通    1为强制
	提示次数 1为一次    0为多次  
	sta      3激活      非3失效 
 * @author  ctt
 * date  2015年11月21日  下午12:43:07
 * @version
 */
public class AppVersionController extends Controller {
	public static final Logger.ALogger logger = Logger.of(AppVersionController.class);
	
	ICacheService cache = ServiceFactory.getCacheService();
	private static final AppVersionService appVersionService = ServiceFactory.getAppVersionService(); 
	
	private final Form<AppVersionForm> appVersionForm = Form.form(AppVersionForm.class);
	/**
	 * 
	 * <p>Title: list</p> 
	 * <p>Description: App升级信息列表</p> 
	 * @return
	 */
	public Result appVersionManage(){
		Form<AppVersionForm> form = appVersionForm.bindFromRequest();
		AppVersionForm formPage = new AppVersionForm();
		logger.info("log page1:"+form.toString());
        if (!form.hasErrors()) {
        	formPage = form.get();
        }
		PagedList<AppVersion> appVersionPage = appVersionService.findAppVersionList(formPage);
		return ok(views.html.app.appVersionManage.render(appVersionPage));
	}
	
	/**
	 * 跳转至新增App升级添加页面
	 * @return
	 */
	public Result newOrUpdateAppVersion(){
		Long id = Numbers.parseLong(Form.form().bindFromRequest().get("id"), Long.valueOf(0));
		AppVersion appVersion = new AppVersion();
		if(id!=0L){
			appVersion = appVersionService.findAppVersionById(id);
		}
		return ok(views.html.app.newOrUpdateAppVersion.render(id,appVersion,Html.apply(Constants.AppVersionOstypes.typs2HTML(Numbers.parseInt(appVersion.getOstype(), -1)))));
    }
	
	/**
	 * 保存APP升级信息
	 * @return
	 */
	public Result saveAppVersion(){
		Long id = Numbers.parseLong(Form.form().bindFromRequest().get("id"), Long.valueOf(0));
		Form<AppVersionForm> form = appVersionForm.bindFromRequest();
		AppVersionForm formPage = new AppVersionForm();
		logger.info("log page1:"+form.toString());
        if (!form.hasErrors()) {
        	formPage = form.get();
        }
		AppVersion appVersion = new AppVersion();
		if(!Strings.isNullOrEmpty(formPage.ostype)&&String.valueOf(Constants.AppVersionOstypes.ANDROID.getTyp()).equals(formPage.ostype)){
			MultipartFormData body = request().body().asMultipartFormData();
	        List<FilePart> apkParts = body.getFiles();
	        if(apkParts!=null){
	        	String apkUrl = uploadApkFile(apkParts);
	        	if(!Strings.isNullOrEmpty(apkUrl)){
	        		formPage.url=apkUrl;
	        	}
	        }
		}
		if(id!=0L){
			appVersion = appVersionService.findAppVersionById(id);
			appVersion = dealAppVersion(appVersion,formPage);
			appVersion.setDateUpd(new Date());
			appVersion = appVersionService.update(appVersion);
		}else{
			appVersion = dealAppVersion(appVersion,formPage);
			appVersion.setSta("1");//默认失效	1.无效 3有效
			appVersion.setDateNew(new Date());
			appVersion.setDateUpd(new Date());
			//设置图标
			appVersion = appVersionService.save(appVersion);
		}
		return redirect("/app/appVersionManage");
	}
	
	/**
	 * 
	 * <p>Title: uploadApkFile</p> 
	 * <p>Description: 上次apk文件到服务器</p> 
	 * @param apkParts
	 */
	private String uploadApkFile(List<FilePart> apkParts) {
		String url = "";
		String path=Configuration.root().getString("oss.upload.app.apkbackup", "upload/apkbackup/");//上传路径
		String BUCKET_NAME=StringUtil.getBUCKET_NAME();
		for(FilePart imagePart:apkParts){
			String fileName = imagePart.getFilename();
			File file = imagePart.getFile();//获取到该文件
			int p = fileName.lastIndexOf('.');
			String type = fileName.substring(p, fileName.length()).toLowerCase();
			if (".apk".equals(type)) {
				// 检查文件后缀格式
				String fileNameLast = UUID.randomUUID().toString().replaceAll("-", "")+type;//最终的文件名称
				logger.info(path);
				logger.info(fileNameLast);
				logger.info(type);
				logger.info(BUCKET_NAME);
				String endfilestr = OSSUtils.uploadFile(file,path,fileNameLast, type,BUCKET_NAME);		
				logger.info("========="+endfilestr);//=/upload/postcontent/images/fbbbeeb1-2a96-4634-a77b-4566dc68dff0.jpg
				url = Configuration.root().getString("oss.image.url")+endfilestr;
			}
		}
		return url;
	}

	/**
	 * 
	 * <p>Title: changeAppVersionSta</p> 
	 * <p>Description: 更改当前app升级信息状态</p> 
	 * @return
	 */
	public Result changeAppVersionSta(){
		String id = AjaxHelper.getHttpParam(request(), "id");//
		String sta = AjaxHelper.getHttpParam(request(), "sta");//
		AppVersion appVersion = appVersionService.findAppVersionById(Numbers.parseLong(id, Long.valueOf(0)));
		if(appVersion!=null){
			appVersion.setSta(sta);
			appVersion.setDateUpd(new Date());
			appVersion = appVersionService.update(appVersion);
			if("3".equals(sta)){
				appVersionService.updateReddotWithReddot(appVersion);
			}
		}
		return redirect("/app/appVersionManage");
	}
	
	private AppVersion dealAppVersion(AppVersion appVersion, AppVersionForm form) {
		appVersion.setOstype(form.ostype);
		appVersion.setLatestver(form.latestver);
		appVersion.setIsforced(form.isforced);
		appVersion.setRemindTime(form.remindTime);
		appVersion.setRemindTime(form.remindTime);
		appVersion.setTitle(form.title);
		appVersion.setMessage(form.message);
		appVersion.setUrl(form.url);
		appVersion.setMarketcode(form.marketcode);
		return appVersion;
	}
	
}
