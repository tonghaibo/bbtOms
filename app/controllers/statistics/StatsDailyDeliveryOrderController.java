package controllers.statistics;

import com.avaje.ebean.PagedList;
import form.DateBetween;
import form.StatsDailyForm;
import models.statistics.StatDailyDeliveryOrder;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Content;
import services.ServiceFactory;
import services.cache.ICacheService;
import services.statistics.StatsDailyDeliveryOrderService;
import utils.Dates;

import java.util.Date;

/**
 * 
 * <p>Title: StatsDailyDeliveryOrderController.java</p>
 * <p>Description: 配送订单数据统计</p>
 * <p>Company: higegou</p> 
 * @author  chenxi
 * date  2015年12月15日
 */
public class StatsDailyDeliveryOrderController extends Controller {

	public static final Logger.ALogger logger = Logger.of(StatsDailyDeliveryOrderController.class);
	
	ICacheService cache = ServiceFactory.getCacheService();
	private static final StatsDailyDeliveryOrderService statsDailyDeliveryOrderService = ServiceFactory.getStatsDailyDeliveryOrderService();
	private final Form<StatsDailyForm> statsDailyForm = Form.form(StatsDailyForm.class);

	/**
	 * 
	 * <p>Title: list</p> 
	 * <p>Description: 配送订单数据列表</p>
	 */
	public Result statsDailyDeliveryOrderList (){
		Form<StatsDailyForm> form = statsDailyForm.bindFromRequest();
		StatsDailyForm formPage = new StatsDailyForm();
        if (!form.hasErrors()) {
        	formPage = form.get();
        }
		formPage = StatsDailyDeliveryOrderController.dealFormWithDatebetween(formPage);
		PagedList<StatDailyDeliveryOrder> statsDailyDeliveryOrderPage = statsDailyDeliveryOrderService.findStatsDailyDeliveryOrderList(formPage);
        return ok((Content) views.html.statistics.StatsDailyDeliveryOrderManage.render(statsDailyDeliveryOrderPage,formPage));
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
