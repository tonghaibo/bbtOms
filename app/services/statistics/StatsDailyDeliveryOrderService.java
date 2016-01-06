package services.statistics;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.PagedList;
import com.avaje.ebean.Query;
import form.StatsDailyForm;
import models.statistics.StatDailyDeliveryOrder;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import play.Logger.ALogger;
import services.ServiceFactory;
import utils.GlobalDBControl;

import java.util.ArrayList;
import java.util.List;

/**
 * 配送订单服务类
 * @author chenxi
 */
public class StatsDailyDeliveryOrderService {

	private static final StatsDailyDeliveryOrderService statsDailyDeliveryOrderService = ServiceFactory.getStatsDailyDeliveryOrderService();
	private static StatsDailyDeliveryOrderService instance = null;
	private static ALogger logger = Logger.of(StatsDailyDeliveryOrderService.class);

	public static StatsDailyDeliveryOrderService getInstance() {
		if (instance == null) {
			syncInit();
		}
		return instance;
	}

	private StatsDailyDeliveryOrderService() {

	}

	private static synchronized void syncInit() {
		if (instance == null) {
			instance = new StatsDailyDeliveryOrderService();
		}
	}

	public PagedList<StatDailyDeliveryOrder> findStatsDailyDeliveryOrderList(StatsDailyForm formPage) {
		List<String> sqlList = new ArrayList<>();
        List<Object> paramsList = new ArrayList<>();
		//统计时间
		if (formPage.between != null) {
            sqlList.add("stat_date between date_format(?,'%Y-%m-%d') ");
            sqlList.add(" date_format(?,'%Y-%m-%d') ");
			paramsList.add(formPage.between.start);
            paramsList.add(formPage.between.end);
		}
        // 关键词
        if (StringUtils.isNotEmpty(formPage.keyword) == true) {
            sqlList.add("instr(source,?) ");
            paramsList.add(formPage.keyword);
        }

		Query<StatDailyDeliveryOrder> query = Ebean.getServer(GlobalDBControl.getDB()).createQuery(StatDailyDeliveryOrder.class);
		query.where(StringUtils.join(sqlList, " AND "));

		int i = 1; // first param uses index 1 NOT 0!
		for (Object param : paramsList) {
		    query.setParameter(i, param);
		    i++;
		}

		PagedList<StatDailyDeliveryOrder> xyzList = query.orderBy("stat_date desc").findPagedList(formPage.page, formPage.size);
		
		return xyzList;
	}

	public StatDailyDeliveryOrder findStatsDailyDeliveryOrderById(Long id) {
		return Ebean.getServer(GlobalDBControl.getDB()).find(StatDailyDeliveryOrder.class).where().eq("id", id).findUnique();
	}

    public StatDailyDeliveryOrder findByStatDate (String statDate) {
        return Ebean.getServer(GlobalDBControl.getDB()).find(StatDailyDeliveryOrder.class).where().eq("stat_date", statDate).findUnique();
    }

    public void save(StatDailyDeliveryOrder StatDailyDeliveryOrder) {
        Ebean.getServer(GlobalDBControl.getDB()).save(StatDailyDeliveryOrder);
    }

}
