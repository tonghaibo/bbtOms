package controllers.statistics;

import com.avaje.ebean.PagedList;
import form.StatsDailyForm;
import models.statistics.StatDailyPostmanTask;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Content;
import services.ServiceFactory;
import services.cache.ICacheService;
import services.statistics.StatsDailyPostmanTaskService;

/**
 * 
 * <p>Title: StatsDailyPostmanTaskController.java</p>
 * <p>Description: 快递员任务数据统计</p>
 * <p>Company: higegou</p> 
 * @author  chenxi
 * date  2015年11月23日
 */
public class StatsDailyPostmanTaskController extends Controller {

	public static final Logger.ALogger logger = Logger.of(StatsDailyPostmanTaskController.class);
	
	ICacheService cache = ServiceFactory.getCacheService();
	private static final StatsDailyPostmanTaskService statsDailyPostmanTaskService = ServiceFactory.getStatsDailyPostmanTaskService();
	private final Form<StatsDailyForm> statsDailyForm = Form.form(StatsDailyForm.class);

	/**
	 * 快递员任务数据列表
	 */
	public Result statsDailyPostmanTaskList (){
		Form<StatsDailyForm> form = statsDailyForm.bindFromRequest();
		StatsDailyForm formPage = new StatsDailyForm();
        if (!form.hasErrors()) {
        	formPage = form.get();
        }
		formPage = StatsDailyPostmanOrderController.dealFormWithDatebetween(formPage);
		PagedList<StatDailyPostmanTask> statsDailyPostmanTaskPage = statsDailyPostmanTaskService.findStatsDailyPostmanTaskList(formPage);
        return ok((Content) views.html.statistics.StatsDailyPostmanTaskManage.render(statsDailyPostmanTaskPage,formPage));
    }
	
}
