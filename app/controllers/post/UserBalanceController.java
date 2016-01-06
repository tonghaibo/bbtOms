package controllers.post;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import models.admin.AdminUser;
import models.order.PostOrder;
import models.user.UserBalance;
import models.user.UserBalanceLog;
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
import services.admin.AdminUserService;
import services.cache.ICacheService;
import services.order.PostOrderService;
import services.user.UserBalanceService;
import utils.AjaxHelper;
import utils.Constants;
import utils.Numbers;
import utils.OSSUtils;
import utils.StringUtil;

import com.avaje.ebean.PagedList;
import com.google.common.base.Strings;
import com.sun.security.sasl.ServerFactoryImpl;

import controllers.admin.AdminAuthenticated;
import controllers.admin.BaseAdminController;
import form.UserBalanceForm;


/**
 * 
 * <p>Title: UserBalanceController.java</p> 
 * <p>Description: 用户余额管理</p> 
 * <p>Company: higegou</p> 
 * @author  ctt
 * date  2015年12月22日  下午4:47:13
 * @version
 */
public class UserBalanceController extends BaseAdminController {
	public static final Logger.ALogger logger = Logger.of(UserBalanceController.class);
	
	ICacheService cache = ServiceFactory.getCacheService();
	private static final UserBalanceService userBalanceService = UserBalanceService.instance; 
	private static final PostOrderService postOrderService = PostOrderService.instance; 
	private static final AdminUserService adminUserService = ServiceFactory.getAdminUserService(); 
	
	private final Form<UserBalanceForm> userBalanceForm = Form.form(UserBalanceForm.class);
	/**
	 * 
	 * <p>Title: list</p> 
	 * <p>Description: App升级信息列表</p> 
	 * @return
	 */
	public Result userBalanceLogManage(){
		Form<UserBalanceForm> form = userBalanceForm.bindFromRequest();
		UserBalanceForm formPage = new UserBalanceForm();
		logger.info("log page1:"+form.toString());
        if (!form.hasErrors()) {
        	formPage = form.get();
        }
		PagedList<UserBalanceLog> userBalanceLogPage = userBalanceService.findUserBalanceLogList(formPage);
		/*List<UserBalanceLog> userBalanceLogs = userBalanceLogPage.getList();
		for (UserBalanceLog userBalanceLog : userBalanceLogs) {
			int orderid = userBalanceLog.getOrderid();
			if(orderid!=0){
				PostOrder postOrder = postOrderService.findPostOrderById(orderid);
				userBalanceLog.setPostOrder(postOrder);
			}
		}*/
		return ok(views.html.post.userBalanceLogManage.render(userBalanceLogPage));
	}
	
	/**
	 * 
	 * <p>Title: newOrUpdateUserBalance</p> 
	 * <p>Description: 调整到增加商户余额页面</p> 
	 * @return
	 */
	@AdminAuthenticated
	public Result newOrUpdateUserBalance(){
		AdminUser user = getCurrentAdminUser();
		if("admin".equals(user.getUsername())){
			List<AdminUser> adminUsers = adminUserService.findAllAdminUser();
			Html realnames = Html.apply(adminUserService.realnameList2Html(adminUsers, "-1"));
			return ok(views.html.post.newOrUpdateUserBalance.render(realnames));
		}else{
			return ok(views.html.unauthorized.render());
		}
	}
	
	/**
	 * 
	 * <p>Title: saveUserBalance</p> 
	 * <p>Description: 商户账户充值</p> 
	 * @return
	 */
	public Result saveUserBalance(){
		String adminId = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "adminId");
		int balance = Numbers.parseInt(AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "balance"), 0);
		//保存用户充值信息，余额+balance
		AdminUser adminUser = adminUserService.getAdminUser(Numbers.parseLong(adminId, Long.valueOf(0)));
		int uid = adminUser.getUid().intValue();
		UserBalance ub = userBalanceService.getBalanceByUid(uid);
		int endbalance = balance;
		int beforebalance = 0;
		if(ub==null){
			ub = new UserBalance();
			ub.setUid(uid);
			ub.setBalance(balance);
			ub.setDate_new(new Date());
			ub.setDate_upd(new Date());
			userBalanceService.addbalance(ub);
		}else{
			beforebalance = ub.getBalance();
			endbalance = balance+beforebalance;
			userBalanceService.updateBalance(uid, endbalance);
		}
		//保存用户充值记录
		UserBalanceLog blg = new UserBalanceLog();
		blg.setUid(uid);
		blg.setOrderid("");
		blg.setChangemoney(balance);
		blg.setBeforebalance(beforebalance);
		blg.setEndbalance(endbalance);
		blg.setTyp(1);
		blg.setRemark("商户充值");
		blg.setDate_new(new Date());
		blg.setDate_upd(new Date());
		userBalanceService.addbalancelog(blg);
		return ok(views.html.post.pagePaySuccess.render(adminUser.getRealname(),balance,endbalance));
	}
}
