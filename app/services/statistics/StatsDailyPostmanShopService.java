package services.statistics;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.PagedList;
import com.avaje.ebean.Query;
import com.google.common.base.Strings;
import form.StatsDailyForm;
import models.statistics.StatDailyPostmanShop;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import play.Logger.ALogger;
import services.ServiceFactory;
import utils.GlobalDBControl;

import java.util.ArrayList;
import java.util.List;

/**
 * 快递员店铺服务类
 * @author chenxi
 */
public class StatsDailyPostmanShopService {

	private static final StatsDailyPostmanShopService statsDailyPostmanShopService = ServiceFactory.getStatsDailyPostmanShopService();
	private static StatsDailyPostmanShopService instance = null;
	private static ALogger logger = Logger.of(StatsDailyPostmanShopService.class);

	public static StatsDailyPostmanShopService getInstance() {
		if (instance == null) {
			syncInit();
		}
		return instance;
	}

	private StatsDailyPostmanShopService() {

	}

	private static synchronized void syncInit() {
		if (instance == null) {
			instance = new StatsDailyPostmanShopService();
		}
	}

	public PagedList<StatDailyPostmanShop> findStatsDailyPostmanShopList(StatsDailyForm formPage) {
		List<String> sqlList = new ArrayList<>();
		List<Object> paramsList = new ArrayList<>();
		//统计时间
		if (formPage.between != null) {
            sqlList.add("stat_date between date_format(?,'%Y-%m-%d') ");
            sqlList.add(" date_format(?,'%Y-%m-%d') ");
			paramsList.add(formPage.between.start);
			paramsList.add(formPage.between.end);
		}

		Query<StatDailyPostmanShop> query = Ebean.getServer(GlobalDBControl.getDB()).createQuery(StatDailyPostmanShop.class);
		query.where(StringUtils.join(sqlList, " AND "));

		int i = 1; // first param uses index 1 NOT 0!
		for (Object param : paramsList) {
		    query.setParameter(i, param);
		    i++;
		}

		PagedList<StatDailyPostmanShop> xyzList = query.orderBy("stat_date desc").findPagedList(formPage.page, formPage.size);
		
		return xyzList;
	}

	public StatDailyPostmanShop findStatsDailyPostmanShopById(Long id) {
		return Ebean.getServer(GlobalDBControl.getDB()).find(StatDailyPostmanShop.class).where().eq("id", id).findUnique();
	}

}
