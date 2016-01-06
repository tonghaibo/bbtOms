package controllers.statistics;

import actor.StatActor;
import com.aliyun.odps.Instance;
import com.aliyun.odps.Odps;
import com.aliyun.odps.OdpsException;
import com.aliyun.odps.account.Account;
import com.aliyun.odps.account.AliyunAccount;
import com.aliyun.odps.task.SQLTask;
import models.statistics.StatDailyPostmanTask;
import models.statistics.StatDailyPostmanTaskDetail;
import models.statistics.StatDailyUser;
import models.statistics.StatDailyX7;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import play.mvc.Controller;
import play.mvc.Result;
import services.statistics.StatsDailyPostmanTaskService;
import services.statistics.StatsDailyUserService;
import services.statistics.StatsDailyX7Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Jiwei on 15/12/3.
 */
public class StatTempRunController extends Controller {

    public Result daily (String statDate){

        StatActor.doAct(statDate);
        return ok("ok");
    }

}
