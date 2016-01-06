package controllers.statistics;

import com.avaje.ebean.PagedList;
import form.StatsDailyForm;
import models.statistics.StatDailyPostmanOrder;
import models.statistics.StatDailyPostmanOrderDetail;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Content;
import services.ServiceFactory;
import services.cache.ICacheService;
import services.statistics.StatsDailyPostmanOrderDetailService;

/**
 * 
 * <p>Title: StatsDailyPostmanOrderController.java</p>
 * <p>Description: 快递员商品销售数据统计</p>
 * <p>Company: higegou</p> 
 * @author  chenxi
 * date  2015年11月23日
 */
public class StatsDailyPostmanOrderDetailController extends Controller {

	public static final Logger.ALogger logger = Logger.of(StatsDailyPostmanOrderDetailController.class);
	
	ICacheService cache = ServiceFactory.getCacheService();
	private static final StatsDailyPostmanOrderDetailService statsDailyPostmanOrderDetailService = ServiceFactory.getStatsDailyPostmanOrderDetailService();
	private final Form<StatsDailyForm> statsDailyForm = Form.form(StatsDailyForm.class);

	/**
	 * 快递员商品销售数据列表
	 */
	public Result statsDailyPostmanOrderDetailList (){
		Form<StatsDailyForm> form = statsDailyForm.bindFromRequest();
		StatsDailyForm formPage = new StatsDailyForm();
        if (!form.hasErrors()) {
        	formPage = form.get();
        }
		formPage = StatsDailyPostmanOrderController.dealFormWithDatebetween(formPage);
		PagedList<StatDailyPostmanOrderDetail> statsDailyPostmanOrderDetailPage = statsDailyPostmanOrderDetailService.findStatsDailyPostmanOrderDetailList(formPage);
        return ok((Content) views.html.statistics.StatsDailyPostmanOrderDetailManage.render(statsDailyPostmanOrderDetailPage,formPage));
    }
	
}
