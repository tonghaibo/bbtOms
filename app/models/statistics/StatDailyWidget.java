package models.statistics;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Widget数据实体类
 *
 * @author chenxi
 */
@Entity
@Table(name = "stat_daily_widget")
public class StatDailyWidget implements Serializable {

    private static final long serialVersionUID = 4927875382394220785L;

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "stat_date", columnDefinition = " varchar(11) '' ")
    private String statDate;

    @Column(name = "pv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer pv;

    @Column(name = "uv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer uv;

    @Column(name = "click_pv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer clickPv;

    @Column(name = "click_uv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer clickUv;

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

    public Integer getPv() {
        return pv;
    }

    public void setPv(Integer pv) {
        this.pv = pv;
    }

    public Integer getUv() {
        return uv;
    }

    public void setUv(Integer uv) {
        this.uv = uv;
    }

    public Integer getClickPv() {
        return clickPv;
    }

    public void setClickPv(Integer clickPv) {
        this.clickPv = clickPv;
    }

    public Integer getClickUv() {
        return clickUv;
    }

    public void setClickUv(Integer clickUv) {
        this.clickUv = clickUv;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
