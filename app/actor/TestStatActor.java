package actor;

import com.aliyun.odps.Instance;
import com.aliyun.odps.Odps;
import com.aliyun.odps.OdpsException;
import com.aliyun.odps.account.Account;
import com.aliyun.odps.account.AliyunAccount;
import com.aliyun.odps.task.SQLTask;
import com.avaje.ebean.config.JsonConfig;
import models.statistics.StatDailyX7;
import org.apache.commons.lang3.StringUtils;
import services.statistics.StatsDailyX7Service;
import utils.StringUtil;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Jiwei on 15/11/27.
 */
public class TestStatActor {

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
