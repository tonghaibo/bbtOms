package controllers.statistics;

import com.avaje.ebean.PagedList;
import form.BalanceWithdrawForm;
import form.DateBetween;
import form.StatsDailyForm;
import models.statistics.StatDailyPostmanOrder;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Content;
import services.ServiceFactory;
import services.cache.ICacheService;
import services.statistics.StatsDailyPostmanOrderService;
import utils.Dates;

import java.util.Date;

/**
 * <p>Title: StatsDailyPostmanOrderController.java</p>
 * <p>Description: 快递员订单数据统计</p>
 * <p>Company: higegou</p> 
 * @author  chenxi
 * date  2015年11月23日
 */
public class StatsDailyPostmanOrderController extends Controller {

	public static final Logger.ALogger logger = Logger.of(StatsDailyPostmanOrderController.class);
	
	ICacheService cache = ServiceFactory.getCacheService();
	private static final StatsDailyPostmanOrderService statsDailyPostmanOrderService = ServiceFactory.getStatsDailyPostmanOrderService();
	private final Form<StatsDailyForm> statsDailyForm = Form.form(StatsDailyForm.class);

	/**
	 * 
	 * <p>Title: list</p> 
	 * <p>Description: 快递员订单数据列表</p>
	 */
	public Result statsDailyPostmanOrderList (){
		Form<StatsDailyForm> form = statsDailyForm.bindFromRequest();
		StatsDailyForm formPage = new StatsDailyForm();
        if (!form.hasErrors()) {
        	formPage = form.get();
        }
		formPage = dealFormWithDatebetween(formPage);
		PagedList<StatDailyPostmanOrder> statsDailyPostmanOrderPage = statsDailyPostmanOrderService.findStatsDailyPostmanOrderList(formPage);
        return ok((Content) views.html.statistics.StatsDailyPostmanOrderManage.render(statsDailyPostmanOrderPage,formPage));
    }

	/**
	 *
	 * <p>Title: dealFormWithDatebetween</p>
	 * <p>Description: 针对查询时间做的调整</p>
	 * @param form
	 * @return
	 */
	public static  StatsDailyForm dealFormWithDatebetween(
			StatsDailyForm form) {
		if(form.between==null){
			DateBetween between = new DateBetween();
			String startDate = Dates.SevenBeforeformatEngLishDateTime(new Date());
			between.start=Dates.parseDate(startDate);
			between.end=new Date();
			form.between = between;
		}
		return form;
	}
	
}
