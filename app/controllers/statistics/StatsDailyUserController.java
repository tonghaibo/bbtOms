package controllers.statistics;

import com.avaje.ebean.PagedList;
import form.StatsDailyForm;
import models.statistics.StatDailyUser;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Content;
import services.ServiceFactory;
import services.cache.ICacheService;
import services.statistics.StatsDailyUserService;

/**
 * 
 * <p>Title: StatsDailyUserController.java</p>
 * <p>Description: 用户数据统计</p>
 * <p>Company: higegou</p> 
 * @author  chenxi
 * date  2015年11月19日
 */
public class StatsDailyUserController extends Controller {

	public static final Logger.ALogger logger = Logger.of(StatsDailyUserController.class);
	
	ICacheService cache = ServiceFactory.getCacheService();
	private static final StatsDailyUserService statsDailyUserService = ServiceFactory.getStatsDailyUserService();
	private final Form<StatsDailyForm> statsDailyForm = Form.form(StatsDailyForm.class);

	/**
	 * 用户数据列表
	 */
	public Result statsDailyUserList (){
		Form<StatsDailyForm> form = statsDailyForm.bindFromRequest();
		StatsDailyForm formPage = new StatsDailyForm();
        if (!form.hasErrors()) {
        	formPage = form.get();
        }
		formPage = StatsDailyPostmanOrderController.dealFormWithDatebetween(formPage);
		PagedList<StatDailyUser> statsDailyUserPage = statsDailyUserService.findStatsDailyUserList(formPage);
        return ok((Content) views.html.statistics.StatsDailyUserManage.render(statsDailyUserPage,formPage));
    }
	
}
