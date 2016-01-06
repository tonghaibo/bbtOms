package actor;


import java.sql.ResultSet;
import java.util.concurrent.TimeUnit;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import com.google.common.base.Strings;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import play.Logger;
import play.Logger.ALogger;
import play.libs.Akka;
import scala.concurrent.duration.Duration;
import services.ServiceFactory;
import utils.Constants;
import utils.GlobalDBControl;
import utils.JdbcOper;

import java.util.List;

/**
 * 释放未支付订单的库存
 * @author luobotao
 * @Date 2015年9月16日
 */
public class BbtshopsStat extends UntypedActor{
	private final ALogger logger = Logger.of(BbtshopsStat.class);
	
	public static ActorRef myActor = Akka.system().actorOf(Props.create(BbtshopsStat.class));
	@Override
	public void onReceive(Object message) throws Exception {
		if("ACT".equals(message)){
			Runnable runnable1 = new Runnable() {
			  public void run() {
				  syncBbtshopsStatDailyPostmanOrder();
			  }
			};
		    Thread thread1 = new Thread(runnable1);
		    thread1.start();
		    Runnable runnable2 = new Runnable() {
		    	public void run() {
		    		syncBbtshopsStatDailyPostmanOrderDetail();
		    	}
		    };
		    Thread thread2 = new Thread(runnable2);
		    thread2.start();
		    Runnable runnable3 = new Runnable() {
		    	public void run() {
		    		syncBbtshopsStatDailyPostmanShop();
		    	}
		    };
		    Thread thread3 = new Thread(runnable3);
		    thread3.start();
		}
	}

	/**
	 * 
	 * <p>Title: syncBbtshopsStat</p> 
	 * <p>Description: 同步棒棒糖商城的订单数据</p>
	 */
	private void syncBbtshopsStatDailyPostmanOrder() {
		//将这些商品的库存进行恢复
		String sql = "SELECT CONCAT('INSERT INTO `stat_daily_postman_order`(stat_date,order_cnt,paid_order_cnt,paid_order_amount,total_order_cnt,total_paid_order_amount,app_reg_user_cnt,create_time) values (''',datestr,''',',orderaddcnt,',',orderaddpaycnt,',',orderaddpaymoney*100,',',ordertotalcnt,',',ordertotalmoney*100,',',appcnt,',now());') AS concatstr FROM bbtshops_stat_orderday WHERE datestr = DATE_SUB(CURDATE(),INTERVAL 1 DAY)";// SQL语句// 
		logger.info("[syncBbtshopsStat]:"+sql);
		String rstsql = "";
			rstsql = Ebean.getServer(GlobalDBControl.getHgDB()).createSqlQuery(sql).findUnique().getString("concatstr");
			logger.info(rstsql+"======query insert stat_daily_postman_order11======");
		if(!Strings.isNullOrEmpty(rstsql)){
			logger.info(rstsql+"====== execute insert stat_daily_postman_order22======");
			Ebean.getServer(GlobalDBControl.getDB()).createSqlUpdate(rstsql).execute();
		}
	}
	/**
	 * 
	 * <p>Title: syncBbtshopsStat</p> 
	 * <p>Description: 同步棒棒糖商城的订单数据</p>
	 */
	private void syncBbtshopsStatDailyPostmanOrderDetail() {
		//将这些商品的库存进行恢复
		String sql = "SELECT CONCAT('INSERT INTO `stat_daily_postman_order_detail`(stat_date,goods_id,goods_name,sold_cnt,create_time) VALUES(''',datestr,''',',proid,',''',proname,''',',procnt,',now());') as concatstr FROM bbtshops_stat_orderProday WHERE datestr = DATE_SUB(CURDATE(),INTERVAL 1 DAY) ";// SQL语句// 
		logger.info("[syncBbtshopsStat]:"+sql);
		List<SqlRow> rstsqls = Ebean.getServer(GlobalDBControl.getHgDB()).createSqlQuery(sql).findList();
		logger.info(rstsqls.size()+"======query insert syncBbtshopsStatDailyPostmanOrderDetail 11======");
		if(rstsqls!=null&&rstsqls.size()>0){
			for (SqlRow rstsql : rstsqls) {
				logger.info(rstsql+"====== execute insert syncBbtshopsStatDailyPostmanOrderDetail 22======");
				logger.info(rstsql.getString("concatstr")+"====== execute insert syncBbtshopsStatDailyPostmanOrderDetail 22======");
				Ebean.getServer(GlobalDBControl.getDB()).createSqlUpdate(rstsql.getString("concatstr")).execute();
			}
		}
	}
	/**
	 * 
	 * <p>Title: syncBbtshopsStat</p> 
	 * <p>Description: 同步棒棒糖商城的订单数据</p>
	 */
	private void syncBbtshopsStatDailyPostmanShop() {
		//将这些商品的库存进行恢复
		String sql = "SELECT CONCAT('INSERT INTO stat_daily_postman_shop(stat_date,scan_pv,scan_uv,new_follow_cnt,active_follow_cnt,total_follow_cnt,shop_list_pv,shop_list_uv,shop_detail_pv,shop_detail_uv,shop_buy_pv,shop_buy_uv,shop_confirm_pv,shop_confirm_uv,shop_pay_pv,shop_pay_uv,create_time) VALUES(''',stat_date,''',',scan_pv,',',scan_uv,',',new_follow_cnt,',',active_follow_cnt,',',total_follow_cnt,',',shop_list_pv,',',shop_list_uv,',',shop_detail_pv,',',shop_detail_uv,',',shop_buy_pv,',',shop_buy_uv,',',shop_confirm_pv,',',shop_confirm_uv,',',shop_pay_pv,',',shop_pay_uv,',''',create_time,''');') AS concatstr FROM stat_daily_postman_shop WHERE stat_date = DATE_SUB(CURDATE(),INTERVAL 1 DAY)";// SQL语句// 
		logger.info("[syncBbtshopsStat]:"+sql);
		String rstsql = "";
		rstsql = Ebean.getServer(GlobalDBControl.getHgDB()).createSqlQuery(sql).findUnique().getString("concatstr");
		logger.info(rstsql+"======query insert syncBbtshopsStatDailyPostmanShop 11======");
		if(!Strings.isNullOrEmpty(rstsql)){
			logger.info(rstsql+"====== execute insert syncBbtshopsStatDailyPostmanShop 22======");
			Ebean.getServer(GlobalDBControl.getDB()).createSqlUpdate(rstsql).execute();
		}
	}
}
