package services.statistics;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.PagedList;
import com.avaje.ebean.Query;
import com.google.common.base.Strings;
import form.StatsDailyForm;
import models.statistics.StatDailyPostmanOrderDetail;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import play.Logger.ALogger;
import services.ServiceFactory;
import utils.Dates;
import utils.GlobalDBControl;
import utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 快递员商品销售服务类
 * @author chenxi
 */
public class StatsDailyPostmanOrderDetailService {

	private static final StatsDailyPostmanOrderDetailService statsDailyPostmanOrderDetailService = ServiceFactory.getStatsDailyPostmanOrderDetailService();
	private static StatsDailyPostmanOrderDetailService instance = null;
	private static ALogger logger = Logger.of(StatsDailyPostmanOrderDetailService.class);

	public static StatsDailyPostmanOrderDetailService getInstance() {
		if (instance == null) {
			syncInit();
		}
		return instance;
	}

	private StatsDailyPostmanOrderDetailService() {

	}

	private static synchronized void syncInit() {
		if (instance == null) {
			instance = new StatsDailyPostmanOrderDetailService();
		}
	}

	public PagedList<StatDailyPostmanOrderDetail> findStatsDailyPostmanOrderDetailList(StatsDailyForm formPage) {
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
            // 区分是商品ID还是商品名称
            if (StringUtils.isNumeric(formPage.keyword) == true) {
                sqlList.add("goods_id=? ");
            }
            else {
                sqlList.add("instr(goods_name,?) ");
            }
            paramsList.add(formPage.keyword);
        }

		Query<StatDailyPostmanOrderDetail> query = Ebean.getServer(GlobalDBControl.getDB()).createQuery(StatDailyPostmanOrderDetail.class);
		query.where(StringUtils.join(sqlList, " AND "));

		int i = 1; // first param uses index 1 NOT 0!
		for (Object param : paramsList) {
		    query.setParameter(i, param);
		    i++;
		}

		PagedList<StatDailyPostmanOrderDetail> xyzList = query.orderBy("stat_date desc").findPagedList(formPage.page, formPage.size);
		
		return xyzList;
	}

	public StatDailyPostmanOrderDetail findStatsDailyPostmanOrderDetailById(Long id) {
		return Ebean.getServer(GlobalDBControl.getDB()).find(StatDailyPostmanOrderDetail.class).where().eq("id", id).findUnique();
	}

}
