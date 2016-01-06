package services;

import services.admin.AdminUserService;
import services.app.AppVersionService;
import services.balance.BalanceService;
import services.cache.CacheMem;
import services.cache.ICacheService;
import services.cache.JedisHelper;
import services.postcontent.PostRewardService;
import services.postcontent.PostcontentService;
import services.postmanuser.PostmanUserService;
import services.statistics.*;

public class ServiceFactory {

	/**
	 * 获取Cache操作对象；
	 * 若redis异常，则用Play自带的Cache系统，否则用redis
	 */
	public static ICacheService getCacheService()
	{
		try {
			return JedisHelper.getInstance();
		} catch (Exception e) {
			System.err.print("redis连接失败，错误信息为："+e.toString());
			return CacheMem.instance;
		}
	}

	public static PostcontentService getPostcontentService() {
		return PostcontentService.getInstance();
	}
	
	public static PostmanUserService getPostmanUserService() {
		return PostmanUserService.getInstance();
	}
	public static PostcompanyService getPostcompanyService() {
		return PostcompanyService.getInstance();
	}
	public static BalanceService getBalanceService() {
		return BalanceService.getInstance();
	}
    public static StatsDailyUserService getStatsDailyUserService() {
        return StatsDailyUserService.getInstance();
    }
    public static StatsDailyWidgetService getStatsDailyWidgetService() {
        return StatsDailyWidgetService.getInstance();
    }
    public static StatsDailyX7Service getStatsDailyX7Service() {
        return StatsDailyX7Service.getInstance();
    }
    public static StatsDailyPostmanShopService getStatsDailyPostmanShopService() {
        return StatsDailyPostmanShopService.getInstance();
    }
    public static StatsDailyPostmanOrderService getStatsDailyPostmanOrderService() {
        return StatsDailyPostmanOrderService.getInstance();
    }
    public static StatsDailyPostmanOrderDetailService getStatsDailyPostmanOrderDetailService() {
        return StatsDailyPostmanOrderDetailService.getInstance();
    }
    public static StatsDailyPostmanTaskService getStatsDailyPostmanTaskService() {
        return StatsDailyPostmanTaskService.getInstance();
    }
    public static StatsDailyPostmanTaskDetailService getStatsDailyPostmanTaskDetailService() {
        return StatsDailyPostmanTaskDetailService.getInstance();
    }
    public static StatsDailyDeliveryRouteService getStatsDailyDeliveryRouteService() {
        return StatsDailyDeliveryRouteService.getInstance();
    }
    public static StatsDailyDeliveryOrderService getStatsDailyDeliveryOrderService() {
        return StatsDailyDeliveryOrderService.getInstance();
    }
	public static SmsService getSmsService() {
		return SmsService.getInstance();
	}
	
	public static PostRewardService getPostRewardService() {
		return PostRewardService.getInstance();
	}
	public static AppVersionService getAppVersionService() {
		return AppVersionService.getInstance();
	}
	
	public static AdminUserService getAdminUserService() {
		return AdminUserService.getInstance();
	}
	public static OperateLogService getOperateLogService() {
		return OperateLogService.getInstance();
	}

}
