package controllers.statistics;

import com.avaje.ebean.PagedList;
import form.StatsDailyForm;
import models.statistics.StatDailyX7;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Content;
import services.ServiceFactory;
import services.cache.ICacheService;
import services.statistics.StatsDailyX7Service;

/**
 * 
 * <p>Title: StatsDailyX7Controller.java</p>
 * <p>Description: 手机数据统计</p>
 * <p>Company: higegou</p> 
 * @author  chenxi
 * date  2015年11月23日
 */
public class StatsDailyX7Controller extends Controller {

	public static final Logger.ALogger logger = Logger.of(StatsDailyX7Controller.class);
	
	ICacheService cache = ServiceFactory.getCacheService();
	private static final StatsDailyX7Service statsDailyX7Service = ServiceFactory.getStatsDailyX7Service();
	private final Form<StatsDailyForm> statsDailyForm = Form.form(StatsDailyForm.class);

	/**
	 * X7数据列表
	 */
	public Result statsDailyX7List (){
		Form<StatsDailyForm> form = statsDailyForm.bindFromRequest();
		StatsDailyForm formPage = new StatsDailyForm();
        if (!form.hasErrors()) {
        	formPage = form.get();
        }
		formPage = StatsDailyPostmanOrderController.dealFormWithDatebetween(formPage);
		PagedList<StatDailyX7> statsDailyX7Page = statsDailyX7Service.findStatsDailyX7List(formPage);
        return ok((Content) views.html.statistics.StatsDailyX7Manage.render(statsDailyX7Page,formPage));
    }
	
}
