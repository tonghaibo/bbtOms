package models.statistics;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 配送订单数据实体类
 *
 * @author chenxi
 */
@Entity
@Table(name = "stat_daily_delivery_order")
public class StatDailyDeliveryOrder implements Serializable {

    private static final long serialVersionUID = 4927875382394220785L;

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "stat_date", columnDefinition = " varchar(11) '' ")
    private String statDate;

    @Column(name = "source", columnDefinition = " varchar(50) '' ")
    private String source;

    @Column(name = "new_order_cnt", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer newOrderCnt;

    @Column(name = "new_complete_cnt", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer newCompleteCnt;

    @Column(name = "new_suspend_cnt", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer newSuspendCnt;

    @Column(name = "new_reject_cnt", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer newRejectCnt;

    @Column(name = "complete_cnt", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer completeCnt;

    @Column(name = "suspend_cnt", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer suspendCnt;

    @Column(name = "reject_cnt", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer rejectCnt;

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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Integer getNewOrderCnt() {
        return newOrderCnt;
    }

    public void setNewOrderCnt(Integer newOrderCnt) {
        this.newOrderCnt = newOrderCnt;
    }

    public Integer getNewCompleteCnt() {
        return newCompleteCnt;
    }

    public void setNewCompleteCnt(Integer newCompleteCnt) {
        this.newCompleteCnt = newCompleteCnt;
    }

    public Integer getNewSuspendCnt() {
        return newSuspendCnt;
    }

    public void setNewSuspendCnt(Integer newSuspendCnt) {
        this.newSuspendCnt = newSuspendCnt;
    }

    public Integer getNewRejectCnt() {
        return newRejectCnt;
    }

    public void setNewRejectCnt(Integer newRejectCnt) {
        this.newRejectCnt = newRejectCnt;
    }

    public Integer getCompleteCnt() {
        return completeCnt;
    }

    public void setCompleteCnt(Integer completeCnt) {
        this.completeCnt = completeCnt;
    }

    public Integer getSuspendCnt() {
        return suspendCnt;
    }

    public void setSuspendCnt(Integer suspendCnt) {
        this.suspendCnt = suspendCnt;
    }

    public Integer getRejectCnt() {
        return rejectCnt;
    }

    public void setRejectCnt(Integer rejectCnt) {
        this.rejectCnt = rejectCnt;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
