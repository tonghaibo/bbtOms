package controllers.statistics;

import com.avaje.ebean.PagedList;
import form.StatsDailyForm;
import models.statistics.StatDailyPostmanTaskDetail;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Content;
import services.ServiceFactory;
import services.cache.ICacheService;
import services.statistics.StatsDailyPostmanTaskDetailService;

/**
 * 
 * <p>Title: StatsDailyPostmanTaskController.java</p>
 * <p>Description: 快递员任务详细数据统计</p>
 * <p>Company: higegou</p> 
 * @author  chenxi
 * date  2015年11月23日
 */
public class StatsDailyPostmanTaskDetailController extends Controller {

	public static final Logger.ALogger logger = Logger.of(StatsDailyPostmanTaskDetailController.class);
	
	ICacheService cache = ServiceFactory.getCacheService();
	private static final StatsDailyPostmanTaskDetailService statsDailyPostmanTaskDetailService = ServiceFactory.getStatsDailyPostmanTaskDetailService();
	private final Form<StatsDailyForm> statsDailyForm = Form.form(StatsDailyForm.class);

	/**
	 * 快递员任务详细数据列表
	 */
	public Result statsDailyPostmanTaskDetailList (){
		Form<StatsDailyForm> form = statsDailyForm.bindFromRequest();
		StatsDailyForm formPage = new StatsDailyForm();
        if (!form.hasErrors()) {
        	formPage = form.get();
        }
		formPage = StatsDailyPostmanOrderController.dealFormWithDatebetween(formPage);
		PagedList<StatDailyPostmanTaskDetail> statsDailyPostmanTaskDetailPage = statsDailyPostmanTaskDetailService.findStatsDailyPostmanTaskDetailList(formPage);
        return ok((Content) views.html.statistics.StatsDailyPostmanTaskDetailManage.render(statsDailyPostmanTaskDetailPage,formPage));
    }
	
}
