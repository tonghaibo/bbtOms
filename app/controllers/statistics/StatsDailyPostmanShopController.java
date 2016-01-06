package controllers.statistics;

import com.avaje.ebean.PagedList;
import form.StatsDailyForm;
import models.statistics.StatDailyPostmanShop;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Content;
import services.ServiceFactory;
import services.cache.ICacheService;
import services.statistics.StatsDailyPostmanShopService;

/**
 * 
 * <p>Title: StatsDailyPostmanShopController.java</p>
 * <p>Description: 快递员店铺数据统计</p>
 * <p>Company: higegou</p> 
 * @author  chenxi
 * date  2015年11月23日
 */
public class StatsDailyPostmanShopController extends Controller {

	public static final Logger.ALogger logger = Logger.of(StatsDailyPostmanShopController.class);
	
	ICacheService cache = ServiceFactory.getCacheService();
	private static final StatsDailyPostmanShopService statsDailyPostmanShopService = ServiceFactory.getStatsDailyPostmanShopService();
	private final Form<StatsDailyForm> statsDailyForm = Form.form(StatsDailyForm.class);

	/**
	 * 快递员店铺数据列表
	 */
	public Result statsDailyPostmanShopList (){
		Form<StatsDailyForm> form = statsDailyForm.bindFromRequest();
		StatsDailyForm formPage = new StatsDailyForm();
        if (!form.hasErrors()) {
        	formPage = form.get();
        }
		formPage = StatsDailyPostmanOrderController.dealFormWithDatebetween(formPage);
		PagedList<StatDailyPostmanShop> statsDailyPostmanShopPage = statsDailyPostmanShopService.findStatsDailyPostmanShopList(formPage);
        return ok((Content) views.html.statistics.StatsDailyPostmanShopManage.render(statsDailyPostmanShopPage,formPage));
    }
	
}
