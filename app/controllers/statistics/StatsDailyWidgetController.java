package controllers.statistics;

import com.avaje.ebean.PagedList;
import form.StatsDailyForm;
import models.statistics.StatDailyWidget;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Content;
import services.ServiceFactory;
import services.cache.ICacheService;
import services.statistics.StatsDailyWidgetService;

/**
 * 
 * <p>Title: StatsDailyWidgetController.java</p>
 * <p>Description: Widget数据统计</p>
 * <p>Company: higegou</p> 
 * @author  chenxi
 * date  2015年11月19日
 */
public class StatsDailyWidgetController extends Controller {

	public static final Logger.ALogger logger = Logger.of(StatsDailyWidgetController.class);
	
	ICacheService cache = ServiceFactory.getCacheService();
	private static final StatsDailyWidgetService statsDailyWidgetService = ServiceFactory.getStatsDailyWidgetService();
	private final Form<StatsDailyForm> statsDailyForm = Form.form(StatsDailyForm.class);

	/**
	 * Widget数据列表
	 */
	public Result statsDailyWidgetList (){
		Form<StatsDailyForm> form = statsDailyForm.bindFromRequest();
		StatsDailyForm formPage = new StatsDailyForm();
        if (!form.hasErrors()) {
        	formPage = form.get();
        }
		formPage = StatsDailyPostmanOrderController.dealFormWithDatebetween(formPage);
		PagedList<StatDailyWidget> statsDailyWidgetPage = statsDailyWidgetService.findStatsDailyWidgetList(formPage);
        return ok((Content) views.html.statistics.StatsDailyWidgetManage.render(statsDailyWidgetPage,formPage));
    }
	
}
