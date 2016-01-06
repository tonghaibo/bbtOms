package models.statistics;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 快递员任务详细数据实体类
 * @author chenxi
 */
@Entity
@Table(name = "stat_daily_postman_task_detail")
public class StatDailyPostmanTaskDetail implements Serializable {

    private static final long serialVersionUID = 4927875382394220785L;

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "stat_date", columnDefinition = " varchar(11) '' ")
    private String statDate;

    @Column(name = "task_id", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer taskId;

    @Column(name = "task_name", columnDefinition = " varchar(200) DEFAULT 0 ")
    private String taskName;

    @Column(name = "task_view_pv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer taskViewPv;

    @Column(name = "task_view_uv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer taskViewUv;

    @Column(name = "task_complete_cnt", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer taskCompleteCnt;

    @Column(name = "task_complete_user_cnt", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer taskCompleteUserCnt;

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

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Integer getTaskViewPv() {
        return taskViewPv;
    }

    public void setTaskViewPv(Integer taskViewPv) {
        this.taskViewPv = taskViewPv;
    }

    public Integer getTaskViewUv() {
        return taskViewUv;
    }

    public void setTaskViewUv(Integer taskViewUv) {
        this.taskViewUv = taskViewUv;
    }

    public Integer getTaskCompleteCnt() {
        return taskCompleteCnt;
    }

    public void setTaskCompleteCnt(Integer taskCompleteCnt) {
        this.taskCompleteCnt = taskCompleteCnt;
    }

    public Integer getTaskCompleteUserCnt() {
        return taskCompleteUserCnt;
    }

    public void setTaskCompleteUserCnt(Integer taskCompleteUserCnt) {
        this.taskCompleteUserCnt = taskCompleteUserCnt;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
