package controllers.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

import form.admin.AdminLoginForm;
import form.admin.AdminUserQueryForm;
import form.admin.CreateOrUpdateAdminUserForm;
import models.admin.AdminUser;
import models.admin.OperateLog;
import play.Logger;
import play.Logger.ALogger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.twirl.api.Html;
import services.OperateLogService;
import services.ServiceFactory;
import services.admin.AdminUserService;
import utils.AjaxHelper;
import utils.Constants;
import utils.Errors;
import utils.Numbers;

/**
 * @author luobotao
 *
 */
@Named
@Singleton
public class AdminController extends BaseAdminController {

    private final ALogger logger = Logger.of(AdminController.class);

    private final Form<AdminLoginForm> adminLoginForm = Form.form(AdminLoginForm.class);
    private final Form<CreateOrUpdateAdminUserForm> createOrUpdateAdminUserForm = Form.form(CreateOrUpdateAdminUserForm.class);
    private final String error = "error";
    private final String errorSave = "errorSave";
    private static final AdminUserService adminUserService = ServiceFactory.getAdminUserService(); 
    private static final OperateLogService operateLogService = ServiceFactory.getOperateLogService(); 

	public Result index() {
		
		String currentId = AdminSession.get();
		if(!StringUtils.isBlank(currentId)){
			AdminUser adminUserCurrent = adminUserService.getAdminUser(Long.valueOf(currentId));
			if(adminUserCurrent!=null){
				return redirect(controllers.admin.routes.AdminController.main());
			}
		}
		
		Form<AdminLoginForm> form = adminLoginForm.bindFromRequest(request());
		if (form==null || form.hasErrors() || form.get()==null) {
			flash(error, Errors.wrapedWithDiv(form.errors()));
			return redirect(controllers.admin.routes.AdminController.login());
		} else {
			AdminLoginForm alf = form.get();
			logger.info("username:"+alf.username);
			logger.info("password:"+alf.password);
 			if(StringUtils.isBlank(form.get().username)||StringUtils.isBlank(form.get().password)){
				flash(error, "请重新登录。");
				return redirect(controllers.admin.routes.AdminController.login());
			}
			AdminUser adminUser = adminUserService.login(form.get());
			if (adminUser == null) {
				flash(error, "用户名,密码不匹配");
				return redirect(controllers.admin.routes.AdminController.login());
			}
			if (adminUser.getActive() == 0) {
				flash(error, "此用户已停用，请联系管理员");
				return redirect(controllers.admin.routes.AdminController.login());
			}
			adminUser.setLastip(request().remoteAddress());
			adminUser.setLogin_count(adminUser.getLogin_count() + 1);
			adminUser.setDate_login(new Date());
			adminUser = adminUserService.updateAdminUser(adminUser);
			AdminSession.put(String.valueOf(adminUser.getId()));
			ServiceFactory.getCacheService().setObject(Constants.USER+adminUser.getId(), adminUser, 0);
			operateLogService.saveLoginLog(adminUser.getId(),adminUser.getUsername(), request().remoteAddress());
			return redirect(controllers.admin.routes.AdminController.main());
		}
	}
	
	@AdminAuthenticated()
	public Result main() {
		AdminUser adminUser =getCurrentAdminUser();
		return ok(views.html.index.render(getCurrentAdminUser()));
	}
	
    public Result login() {
        return ok(views.html.login.render(flash(error)));
    }
    
    /**
     * 管理员管理
     * @return
     */
    @AdminAuthenticated()
    public Result users() {
    	AdminUser adminUser =getCurrentAdminUser();
    	List<AdminUser> adminUsers = adminUserService.listAdminUsers(new AdminUserQueryForm());
        return ok(views.html.admin.users.render(adminUser,adminUsers));
    }
    /**
     * 根据管理员ID获取该管理员
     * @return
     */
    @AdminAuthenticated()
    public Result getAdminUser(long id) {
    	AdminUser current = adminUserService.getAdminUser(id);
    	if(current==null)
    		current = new AdminUser();
    	return ok(views.html.admin.addOrEditAdminUser.render(current,flash(errorSave),Html.apply(Constants.AdminType.type2HTML(current.getAdminType()))));
    }
    
    @AdminAuthenticated()
    public Result saveUser() {
    	Form<CreateOrUpdateAdminUserForm> form = createOrUpdateAdminUserForm
                .bindFromRequest(request());
        if (form.hasErrors()) {
            flash(errorSave, Errors.wrapedWithDiv(form.errors()));
            return redirect(controllers.admin.routes.AdminController.getAdminUser(Numbers.parseLong(form.data().get("id"), -1L)));
        } else {
            AdminUser adminUser = null;
            String reason = null;
            try {
            	OperateLog operateLog =operateLogService.createAdminUserLog(getCurrentAdminUser().getId(),getCurrentAdminUser().getUsername(), request().remoteAddress(), "");
            	adminUser = adminUserService.createAdminUser(operateLog,form.get());
            } catch (Throwable t) {
                reason = Errors.trace(t);
            }
            if (adminUser == null) {
                flash(errorSave, "<span>创建用户失败,原因如下</span><div>" + reason + "</div>");
                return redirect(controllers.admin.routes.AdminController.getAdminUser(form.get().id==null?-1L:form.get().id));
            }
            flash(errorSave, "<span>创建管理员" + adminUser.getUsername() + "成功!</span>");
        }
    	return redirect(routes.AdminController.users());
    }
    
    /**
     * 修改管理员状态
     * @return
     */
    @AdminAuthenticated()
    public Result updateAdminStatus() {
    	String adminIdForStatus = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "adminIdForStatus");
    	String adminStatus = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "adminStatus");
    	logger.info(adminIdForStatus);
    	logger.info(adminStatus);
    	int rows = adminUserService.updateAdminUserStatus(Numbers.parseLong(adminIdForStatus, 0L),Numbers.parseInt(adminStatus, 0));
    	return ok(Json.toJson(rows));
    }
    
    /**
     * 用户列表(弹窗)批量
     * @return
     */
    @AdminAuthenticated()
    public Result adminUserListBatchForJson() {
    	String roleid = AjaxHelper.getHttpParam(request(), "roleid");
    	AdminUserQueryForm form = new AdminUserQueryForm();
    	if(!StringUtils.isBlank(roleid)){
    		form.roleid = Numbers.parseInt(roleid, 0);
    	}
    	List<AdminUser> adminUsers = adminUserService.listAdminUsers(form);
        return ok(views.html.admin.adminUsersBatch.render(adminUsers));
    }
    
    
    
    
    @AdminAuthenticated
    public Result logout() {
    	operateLogService.saveLogoutLog(getCurrentAdminUser().getId(),getCurrentAdminUser().getUsername(), request().remoteAddress());
        AdminSession.remove();
        return redirect(controllers.admin.routes.AdminController.login());
    }

}
