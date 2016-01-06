package models.statistics;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 快递员任务数据实体类
 * @author chenxi
 */
@Entity
@Table(name = "stat_daily_postman_task")
public class StatDailyPostmanTask implements Serializable {

    private static final long serialVersionUID = 4927875382394220785L;

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "stat_date", columnDefinition = " varchar(11) '' ")
    private String statDate;

    @Column(name = "app_task_list_pv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer appTaskListPv;

    @Column(name = "app_task_list_uv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer appTaskListUv;

    @Column(name = "app_task_detail_pv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer appTaskDetailPv;

    @Column(name = "app_task_detail_uv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer appTaskDetailUv;

    @Column(name = "widget_task_list_pv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer widgetTaskListPv;

    @Column(name = "widget_task_list_uv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer widgetTaskListkUv;

    @Column(name = "widget_task_detail_pv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer widgetTaskDetailPv;

    @Column(name = "widget_task_detail_uv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer widgetTaskDetailUv;

    @Column(name = "task_complete_user_cnt", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer taskCompleteUserCnt;

    @Column(name = "task_complete_cnt", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer taskCompleteCnt;

    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    public Date createTime;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatDate() {
        return statDate;
    }

    public void setStatDate(String statDate) {
        this.statDate = statDate;
    }

    public Integer getAppTaskListPv() {
        return appTaskListPv;
    }

    public void setAppTaskListPv(Integer appTaskListPv) {
        this.appTaskListPv = appTaskListPv;
    }

    public Integer getAppTaskListUv() {
        return appTaskListUv;
    }

    public void setAppTaskListUv(Integer appTaskListUv) {
        this.appTaskListUv = appTaskListUv;
    }

    public Integer getAppTaskDetailPv() {
        return appTaskDetailPv;
    }

    public void setAppTaskDetailPv(Integer appTaskDetailPv) {
        this.appTaskDetailPv = appTaskDetailPv;
    }

    public Integer getAppTaskDetailUv() {
        return appTaskDetailUv;
    }

    public void setAppTaskDetailUv(Integer appTaskDetailUv) {
        this.appTaskDetailUv = appTaskDetailUv;
    }

    public Integer getWidgetTaskListPv() {
        return widgetTaskListPv;
    }

    public void setWidgetTaskListPv(Integer widgetTaskListPv) {
        this.widgetTaskListPv = widgetTaskListPv;
    }

    public Integer getWidgetTaskListUv() {
        return widgetTaskListkUv;
    }

    public void setWidgetTaskListUv(Integer widgetTaskListkUv) {
        this.widgetTaskListkUv = widgetTaskListkUv;
    }

    public Integer getWidgetTaskDetailPv() {
        return widgetTaskDetailPv;
    }

    public void setWidgetTaskDetailPv(Integer widgetTaskDetailPv) {
        this.widgetTaskDetailPv = widgetTaskDetailPv;
    }

    public Integer getWidgetTaskDetailUv() {
        return widgetTaskDetailUv;
    }

    public void setWidgetTaskDetailUv(Integer widgetTaskDetailUv) {
        this.widgetTaskDetailUv = widgetTaskDetailUv;
    }

    public Integer getTaskCompleteUserCnt() {
        return taskCompleteUserCnt;
    }

    public void setTaskCompleteUserCnt(Integer taskCompleteUserCnt) {
        this.taskCompleteUserCnt = taskCompleteUserCnt;
    }

    public Integer getTaskCompleteCnt() {
        return taskCompleteCnt;
    }

    public void setTaskCompleteCnt(Integer taskCompleteCnt) {
        this.taskCompleteCnt = taskCompleteCnt;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
