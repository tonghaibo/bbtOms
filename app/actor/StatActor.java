package actor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.postcontent.Postcontent;
import models.statistics.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTime;
import org.joda.time.Seconds;

import play.Logger;
import play.libs.Akka;
import services.postcontent.PostcontentService;
import services.statistics.*;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

import com.aliyun.odps.Instance;
import com.aliyun.odps.Odps;
import com.aliyun.odps.OdpsException;
import com.aliyun.odps.account.Account;
import com.aliyun.odps.account.AliyunAccount;
import com.aliyun.odps.task.SQLTask;

/**
 * Created by Jiwei on 15/11/27.
 */
public class StatActor extends UntypedActor {
    private static final Logger.ALogger logger = Logger.of(StatActor.class);
    public static ActorRef myActor = Akka.system().actorOf(Props.create(StatActor.class));

    @Override
    public void onReceive(Object message) throws Exception {
        if ("ACT".equals(message)) {
            String statDate = StatActor.getYesterday();
            doAct(statDate);
        }
    }

    public static void doAct(String statDate) {

//        logger.info(String.format("statistics start"));

        //设置阿里云帐号
        Account account = new AliyunAccount("vs719bUTdtGT9Shm", "LFxKty9Gg3wDMWLUP60L8nyPfK5XN7");
        Odps odps = new Odps(account);
        String odpsUrl = "http://service.odps.aliyun.com/api";
        odps.setEndpoint(odpsUrl);
        odps.setDefaultProject("stat_ibbt_v1");

        // 用户数据
        doActForUser(odps, statDate);

        // 手机数据
        doActForX7(odps, statDate);

        // 任务数据
        doActForPostmanTask(odps, statDate);

        // 任务明细统计
        doActForTaskInfo(odps, statDate);

        // 配送路由统计
        doActForDeliveryRoute(odps, statDate);
    }

    public static void doActForUser(Odps odps, String statDate) {

        String date = statDate.replace("-","");
        String sql;   // sql命令

        // 用户数据
        StatDailyUser statDailyUser = StatsDailyUserService.getInstance().findByStatDate(statDate);
        if (statDailyUser == null) {
            statDailyUser = new StatDailyUser();
            statDailyUser.setUserCnt(0);
            statDailyUser.setTotalUserCnt(0);
        }
        statDailyUser.setStatDate(statDate);

        // 日活跃用户数
        sql = String.format("select count(distinct(user_id)) from hub_table_action " +
                "where to_char(time, 'yyyymmdd')='%s';", date);
        int activeUserCnt = StatActor.odpsStats(odps, sql);
        statDailyUser.setActiveUserCnt(activeUserCnt);

        // 日活跃用户启动次数
        sql = String.format("select count(distinct(user_id)) from hub_table_action " +
                "where to_char(time, 'yyyymmdd')='%s' and TOLOWER(action_code)='start';", date);
        int activeUserLaunchCnt = odpsStats(odps, sql);
        statDailyUser.setActiveUserLaunchCnt(activeUserLaunchCnt);

        statDailyUser.setCreateTime(getNowDate());
        StatsDailyUserService.getInstance().save(statDailyUser);
    }

    public static void doActForX7(Odps odps, String statDate) {

        String date = statDate.replace("-","");
        String sql;   // sql命令

        // 手机数据
        StatDailyX7 statDailyX7 = StatsDailyX7Service.getInstance().findByStatDate(statDate);
        if (statDailyX7 == null) {
            statDailyX7 = new StatDailyX7();
        }
        statDailyX7.setStatDate(statDate);

        // 日活跃
        sql = String.format("select count(distinct(device_id)) from hub_table_action " +
                "where to_char(time, 'yyyymmdd')='%s' and TOLOWER(device_name)='neolix+1';", date);
        int activeDeviceCnt = odpsStats(odps, sql);
        statDailyX7.setActiveDeviceCnt(activeDeviceCnt);

        // 日激活
        sql = String.format("select count(distinct(device_id)) from hub_table_action " +
                "where to_char(time, 'yyyymmdd')='%s' and TOLOWER(device_name)='neolix+1' " +
                "and TOLOWER(action_code)='reg' " +
                "and device_id not in " +
                "(select distinct(device_id) from hub_table_action " +
                "where to_char(time, 'yyyymmdd')<'%s' and TOLOWER(device_name)='neolix+1' " +
                "and TOLOWER(action_code)='reg');", date, date);
        int regDeviceCnt = odpsStats(odps, sql);
        statDailyX7.setRegDeviceCnt(regDeviceCnt);

        // 总激活
        sql = String.format("select count(distinct(device_id)) from hub_table_action " +
                "where TOLOWER(device_name)='neolix+1' and TOLOWER(action_code)='reg';");
        int totalRegDeviceCnt = odpsStats(odps, sql);
        statDailyX7.setTotalRegDeviceCnt(totalRegDeviceCnt);

        // 设备总数
        String dayBeforeYesterday = StatActor.getDayBeforeYesterday();
        StatDailyX7 oldStatDailyX7 = StatsDailyX7Service.getInstance().findByStatDate(dayBeforeYesterday);
        int totalDeviceCnt = (oldStatDailyX7!=null ? oldStatDailyX7.getTotalDeviceCnt() : 0);
        statDailyX7.setTotalDeviceCnt(totalDeviceCnt);

        statDailyX7.setCreateTime(getNowDate());
        StatsDailyX7Service.getInstance().save(statDailyX7);
    }

    public static void doActForPostmanTask(Odps odps, String statDate) {

        String date = statDate.replace("-","");
        String sql;   // sql命令

        // 任务数据
        StatDailyPostmanTask task = StatsDailyPostmanTaskService.getInstance().findByStatDate(statDate);
        if (task == null) {
            task = new StatDailyPostmanTask();
            task.setTaskCompleteCnt(0);
            task.setTaskCompleteUserCnt(0);
        }
        task.setStatDate(statDate);

        String taskPVSQL = "select count(user_id) from hub_table_action where to_char(time, 'yyyymmdd')=%s and TOLOWER(action_code)='%s' and TOLOWER(source)='%s';";
        String taskUVSQL = "select count(distinct(user_id)) from hub_table_action where to_char(time, 'yyyymmdd')=%s and TOLOWER(action_code)='%s' and TOLOWER(source)='%s';";

        sql = String.format(taskPVSQL, date, "tasklist", "app");
        int appTaskListPV = odpsStats(odps, sql);
        task.setAppTaskListPv(appTaskListPV);

        sql = String.format(taskUVSQL, date, "tasklist", "app");
        int appTaskListUV = odpsStats(odps, sql);
        task.setAppTaskListUv(appTaskListUV);

        sql = String.format(taskPVSQL, date, "taskinfo", "app");
        int appTaskInfoPV = odpsStats(odps, sql);
        task.setAppTaskDetailPv(appTaskInfoPV);

        sql = String.format(taskUVSQL, date, "taskinfo", "app");
        int appTaskInfoUV = odpsStats(odps, sql);
        task.setAppTaskDetailUv(appTaskInfoUV);

        sql = String.format(taskPVSQL, date, "tasklist", "widget");
        int widgetTaskListPV = odpsStats(odps, sql);
        task.setWidgetTaskListPv(widgetTaskListPV);

        sql = String.format(taskUVSQL, date, "tasklist", "widget");
        int widgetTaskListUV = odpsStats(odps, sql);
        task.setWidgetTaskListUv(widgetTaskListUV);

        sql = String.format(taskPVSQL, date, "taskinfo", "widget");
        int widgetTaskInfoPV = odpsStats(odps, sql);
        task.setWidgetTaskDetailPv(widgetTaskInfoPV);

        sql = String.format(taskUVSQL, date, "taskinfo", "widget");
        int widgetTaskInfoUV = odpsStats(odps, sql);
        task.setWidgetTaskDetailUv(widgetTaskInfoUV);

        task.setCreateTime(getNowDate());
        StatsDailyPostmanTaskService.getInstance().save(task);
    }

    public static void doActForTaskInfo(Odps odps, String statDate) {

        String date = statDate.replace("-","");
        String sql;   // sql命令

        // 任务明细统计
        // 任务浏览PV
        sql ="select taskid,count(user_id) from("
                + " select KEYVALUE(split_part(regexp_replace(regexp_replace(url, '\\r', ''), '\\n', ''),'?',2),'&','=', 'taskid') taskid,device_id,user_id"
                + " from hub_table_action"
                + " where to_char(time, 'yyyymmdd')='%s'  and  TOLOWER(action_code)='taskinfo' ) temp"
                + " group by taskid;";
        sql = String.format(sql, date);
        List<List<String>> result = odpsStatsList(odps, sql);
        for(List<String> row :result){
            if (row.size() != 2) {
                continue;
            }
            long taskId =NumberUtils.toLong((row.get(0) != null ? row.get(0).replace("\"", "") : "0"));
            Postcontent taskInfo = PostcontentService.getInstance().findPostcontentById(taskId);
            String taskName = (taskInfo!=null ? taskInfo.getTitle() : "无");
            int pv = NumberUtils.toInt(row.get(1));
            StatDailyPostmanTaskDetail detail = StatsDailyPostmanTaskService.getInstance().findByStatDateAndTaskId(statDate, taskId);
            if(detail == null){
                detail = new StatDailyPostmanTaskDetail();
                detail.setTaskViewUv(0);
                detail.setTaskCompleteCnt(0);
                detail.setTaskCompleteUserCnt(0);
            }
            detail.setTaskViewPv(pv);
            detail.setTaskId((int) taskId);
            detail.setTaskName(taskName);
            detail.setStatDate(statDate);
            detail.setCreateTime(getNowDate());
            StatsDailyPostmanTaskService.getInstance().saveDetail(detail);
        }

        // 任务浏览UV
        sql ="select taskid,count(distinct(user_id)) from("
                + " select KEYVALUE(split_part(regexp_replace(regexp_replace(url, '\\r', ''), '\\n', ''),'?',2),'&','=', 'taskid') taskid,device_id,user_id"
                + " from hub_table_action"
                + " where to_char(time, 'yyyymmdd')='%s'  and  TOLOWER(action_code)='taskinfo' ) temp"
                + " group by taskid;";
        sql = String.format(sql, date);
        result = odpsStatsList(odps, sql);
        for(List<String> row :result){
            if (row.size() != 2) {
                continue;
            }
            int taskId =NumberUtils.toInt((row.get(0)!=null ? row.get(0).replace("\"", "") : "0"));
            Postcontent taskInfo = PostcontentService.getInstance().findPostcontentById((long)taskId);
            String taskName = (taskInfo!=null ? taskInfo.getTitle() : "无");
            int uv = NumberUtils.toInt(row.get(1));
            StatDailyPostmanTaskDetail detail = StatsDailyPostmanTaskService.getInstance().findByStatDateAndTaskId(statDate, taskId);
            if(detail == null){
                detail = new StatDailyPostmanTaskDetail();
                detail.setTaskViewPv(0);
                detail.setTaskCompleteCnt(0);
                detail.setTaskCompleteUserCnt(0);
            }
            detail.setTaskViewUv(uv);
            detail.setTaskId(taskId);
            detail.setTaskName(taskName);
            detail.setStatDate(statDate);
            detail.setCreateTime(getNowDate());
            StatsDailyPostmanTaskService.getInstance().saveDetail(detail);
        }
    }

    public static void doActForDeliveryRoute(Odps odps, String statDate) {

        String date = statDate.replace("-","");
        String sql;   // sql命令

        // 配送路由统计
        StatDailyDeliveryRoute statDailyDeliveryRoute = StatsDailyDeliveryRouteService.getInstance().findByStatDate(statDate);
        if (statDailyDeliveryRoute == null) {
            statDailyDeliveryRoute = new StatDailyDeliveryRoute();
        }
        statDailyDeliveryRoute.setStatDate(statDate);

        String deliveryPVSQL = "select count(user_id) from hub_table_action " +
                "where to_char(time, 'yyyymmdd')='%s' and TOLOWER(action_code)='%s' and TOLOWER(source)='%s';";
        String deliveryUVSQL = "select count(distinct(user_id)) from hub_table_action " +
                "where to_char(time, 'yyyymmdd')='%s' and TOLOWER(action_code)='%s' and TOLOWER(source)='%s';";

        // App扫描按钮PV
        sql = String.format(deliveryPVSQL, date, "deliveryscan", "app");
        int appScanPv = odpsStats(odps, sql);
        statDailyDeliveryRoute.setAppScanPv(appScanPv);

        // App扫描按钮UV
        sql = String.format(deliveryUVSQL, date, "deliveryscan", "app");
        int appScanUv = odpsStats(odps, sql);
        statDailyDeliveryRoute.setAppScanUv(appScanUv);

        // App搜索按钮PV
        sql = String.format(deliveryPVSQL, date, "deliverysearch", "app");
        int appSearchPv = odpsStats(odps, sql);
        statDailyDeliveryRoute.setAppSearchPv(appSearchPv);

        // App搜索按钮UV
        sql = String.format(deliveryUVSQL, date, "deliverysearch", "app");
        int appSearchUv = odpsStats(odps, sql);
        statDailyDeliveryRoute.setAppSearchUv(appSearchUv);

        // App订单列表PV
        sql = String.format(deliveryPVSQL, date, "deliverylist", "app");
        int appOrderListPv = odpsStats(odps, sql);
        statDailyDeliveryRoute.setAppOrderListPv(appOrderListPv);

        // App订单列表UV
        sql = String.format(deliveryUVSQL, date, "deliverylist", "app");
        int appOrderListUv = odpsStats(odps, sql);
        statDailyDeliveryRoute.setAppOrderListUv(appOrderListUv);

        // App订单详情PV
        sql = String.format(deliveryPVSQL, date, "deliveryinfo", "app");
        int appOrderInfoPv = odpsStats(odps, sql);
        statDailyDeliveryRoute.setAppOrderInfoPv(appOrderInfoPv);

        // App订单详情UV
        sql = String.format(deliveryUVSQL, date, "deliveryinfo", "app");
        int appOrderInfoUv = odpsStats(odps, sql);
        statDailyDeliveryRoute.setAppOrderInfoUv(appOrderInfoUv);

        // Widget订单列表PV
        sql = String.format(deliveryPVSQL, date, "deliverylist", "widget");
        int widgetOrderListPv = odpsStats(odps, sql);
        statDailyDeliveryRoute.setWidgetOrderListPv(widgetOrderListPv);

        // Widget订单列表UV
        sql = String.format(deliveryUVSQL, date, "deliverylist", "widget");
        int widgetOrderListUv = odpsStats(odps, sql);
        statDailyDeliveryRoute.setWidgetOrderListUv(widgetOrderListUv);

        // Widget订单详情PV
        sql = String.format(deliveryPVSQL, date, "deliveryinfo", "widget");
        int widgetOrderInfoPv = odpsStats(odps, sql);
        statDailyDeliveryRoute.setWidgetOrderInfoPv(widgetOrderInfoPv);

        // Widget订单详情UV
        sql = String.format(deliveryUVSQL, date, "deliveryinfo", "widget");
        int widgetOrderInfoUv = odpsStats(odps, sql);
        statDailyDeliveryRoute.setWidgetOrderInfoUv(widgetOrderInfoUv);

        statDailyDeliveryRoute.setCreateTime(getNowDate());
        StatsDailyDeliveryRouteService.getInstance().save(statDailyDeliveryRoute);
    }

    public static List<List<String>> odpsStatsList (Odps odps, String sql) {
        System.out.println(sql);
        List<List<String>> resultList = new ArrayList<List<String>>();

        try {
            Instance instance = SQLTask.run(odps, sql);
            instance.waitForSuccess();


            Map<String, String> results = instance.getTaskResults();
            Map<String, Instance.TaskStatus> taskStatus = instance.getTaskStatus();

            for (Map.Entry<String, Instance.TaskStatus> status : taskStatus.entrySet()) {
                String result = results.get(status.getKey());
                System.out.println(result);
                // parse result
                String[] records = StringUtils.split(result, "\n");
                for(int i=0; i< records.length;i++){
                    if(i ==0){
                        continue;
                    }
                    String r = records[i];
                    String[] rows = r.split(",");
                    List<String> row = new ArrayList<String>();
                    for(String tmp :rows){
                        row.add(tmp);
                    }
                    resultList.add(row);
                }
            }
        } catch (OdpsException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    public static int odpsStats (Odps odps, String sql) {
        System.out.println(sql);

        try {
            Instance instance = SQLTask.run(odps, sql);
            instance.waitForSuccess();

            Map<String, String> results = instance.getTaskResults();
            Map<String, Instance.TaskStatus> taskStatus = instance.getTaskStatus();

            for (Map.Entry<String, Instance.TaskStatus> status : taskStatus.entrySet()) {
                String result = results.get(status.getKey());
                System.out.println(result);
                // parse result
                String[] records = StringUtils.split(result, "\n");
                System.out.println(records);
                return Integer.parseInt(records[1]);
            }
        } catch (OdpsException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getYesterday(){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String date = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
        return date;
    }

    public static String getDayBeforeYesterday(){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -2);
        String date = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
        return date;
    }

    public static Date getNowDate(){
        return new Date();
    }

    public static int nextExecutionInSeconds(int hour, int minute){
        return Seconds.secondsBetween(
                new DateTime(),
                nextExecution(hour, minute)
        ).getSeconds();
    }

    public static DateTime nextExecution(int hour, int minute){
        DateTime next = new DateTime()
                .withHourOfDay(hour)
                .withMinuteOfHour(minute)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);

        return (next.isBeforeNow())
                ? next.plusHours(24)
                : next;
    }

    public static void main(String[] args) {

        //设置阿里云帐号
        Account account = new AliyunAccount("vs719bUTdtGT9Shm", "LFxKty9Gg3wDMWLUP60L8nyPfK5XN7");
        Odps odps = new Odps(account);
        String odpsUrl = "http://service.odps.aliyun.com/api";
        odps.setEndpoint(odpsUrl);
        odps.setDefaultProject("stat_ibbt_v1");

        List<List<String>> resultList = new ArrayList<List<String>>();

//        String sql = "select * from hub_table_action;";
        String sql = "select taskid,regexp_replace(regexp_replace(url, '\\r', ''), '\\n', ''),count(user_id) from(select trim(KEYVALUE(split_part(regexp_replace(regexp_replace(url, '\\r', ''), '\\n', ''),'?',2),'&','=', 'taskid')) taskid,url,device_id,user_id from hub_table_action where to_char(time, 'yyyymmdd')='20151216' and  TOLOWER(action_code)='taskinfo') temp group by taskid,regexp_replace(regexp_replace(url, '\\r', ''), '\\n', '');";

        try {
            Instance instance = SQLTask.run(odps, sql);
            instance.waitForSuccess();

            Map<String, String> results = instance.getTaskResults();
            Map<String, Instance.TaskStatus> taskStatus = instance.getTaskStatus();

            for (Map.Entry<String, Instance.TaskStatus> status : taskStatus.entrySet()) {
                String result = results.get(status.getKey());
                System.out.println(result);
                // parse result
                String[] records = StringUtils.split(result, "\n");
                for(int i=0; i< records.length;i++){
                    if(i ==0){
                        continue;
                    }
                    String r = records[i];
                    String[] rows = r.split(",");
                    System.out.println(rows.length);
                    List<String> row = new ArrayList<String>();
                    for(String tmp :rows){
                        row.add(tmp);
                    }
                    resultList.add(row);
                }
                System.out.println(resultList);
            }
        } catch (OdpsException e) {
            e.printStackTrace();
        }
    }
}
