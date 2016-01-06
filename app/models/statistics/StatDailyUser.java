package models.statistics;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户数据实体类
 *
 * @author chenxi
 */
@Entity
@Table(name = "stat_daily_user")
public class StatDailyUser implements Serializable {

    private static final long serialVersionUID = 4927875382394220785L;

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "stat_date", columnDefinition = " varchar(11) '' ")
    private String statDate;

    @Column(name = "user_cnt", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer userCnt;

    @Column(name = "total_user_cnt", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer totalUserCnt;

    @Column(name = "active_user_cnt", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer activeUserCnt;

    @Column(name = "active_user_launch_cnt", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer activeUserLaunchCnt;

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

    public Integer getUserCnt() {
        return userCnt;
    }

    public void setUserCnt(Integer userCnt) {
        this.userCnt = userCnt;
    }

    public Integer getTotalUserCnt() {
        return totalUserCnt;
    }

    public void setTotalUserCnt(Integer totalUserCnt) {
        this.totalUserCnt = totalUserCnt;
    }

    public Integer getActiveUserCnt() {
        return activeUserCnt;
    }

    public void setActiveUserCnt(Integer activeUserCnt) {
        this.activeUserCnt = activeUserCnt;
    }

    public Integer getActiveUserLaunchCnt() {
        return activeUserLaunchCnt;
    }

    public void setActiveUserLaunchCnt(Integer activeUserLaunchCnt) {
        this.activeUserLaunchCnt = activeUserLaunchCnt;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
