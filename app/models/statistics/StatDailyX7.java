package models.statistics;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 手机数据实体类
 * @author chenxi
 */
@Entity
@Table(name = "stat_daily_x7")
public class StatDailyX7 implements Serializable {

    private static final long serialVersionUID = 4927875382394220785L;

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "stat_date", columnDefinition = " varchar(11) '' ")
    private String statDate;

    @Column(name = "reg_device_cnt", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer regDeviceCnt;

    @Column(name = "active_device_cnt", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer activeDeviceCnt;

    @Column(name = "total_device_cnt", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer totalDeviceCnt;

    @Column(name = "total_reg_device_cnt", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer totalRegDeviceCnt;

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

    public Integer getRegDeviceCnt() {
        return regDeviceCnt;
    }

    public void setRegDeviceCnt(Integer regDeviceCnt) {
        this.regDeviceCnt = regDeviceCnt;
    }

    public Integer getActiveDeviceCnt() {
        return activeDeviceCnt;
    }

    public void setActiveDeviceCnt(Integer activeDeviceCnt) {
        this.activeDeviceCnt = activeDeviceCnt;
    }

    public Integer getTotalDeviceCnt() {
        return totalDeviceCnt;
    }

    public void setTotalDeviceCnt(Integer totalDeviceCnt) {
        this.totalDeviceCnt = totalDeviceCnt;
    }

    public Integer getTotalRegDeviceCnt() {
        return totalRegDeviceCnt;
    }

    public void setTotalRegDeviceCnt(Integer totalRegDeviceCnt) {
        this.totalRegDeviceCnt = totalRegDeviceCnt;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
