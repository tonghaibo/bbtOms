package models.statistics;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 快递员订单数据实体类
 * @author chenxi
 */
@Entity
@Table(name = "stat_daily_postman_order")
public class StatDailyPostmanOrder implements Serializable {

    private static final long serialVersionUID = 4927875382394220785L;

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "stat_date", columnDefinition = " varchar(11) '' ")
    private String statDate;

    @Column(name = "order_cnt", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer orderCnt;

    @Column(name = "paid_order_cnt", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer paidOrderCnt;

    @Column(name = "paid_order_amount", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer paidOrderAmount;

    @Column(name = "total_order_cnt", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer totalOrderCnt;

    @Column(name = "total_paid_order_amount", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer totalPaidOrderAmount;

    @Column(name = "app_reg_user_cnt", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer appRegUserCnt;

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

    public Integer getOrderCnt() {
        return orderCnt;
    }

    public void setOrderCnt(Integer orderCnt) {
        this.orderCnt = orderCnt;
    }

    public Integer getPaidOrderCnt() {
        return paidOrderCnt;
    }

    public void setPaidOrderCnt(Integer paidOrderCnt) {
        this.paidOrderCnt = paidOrderCnt;
    }

    public Integer getPaidOrderAmount() {
        return paidOrderAmount;
    }

    public void setPaidOrderAmount(Integer paidOrderAmount) {
        this.paidOrderAmount = paidOrderAmount;
    }

    public Integer getTotalOrderCnt() {
        return totalOrderCnt;
    }

    public void setTotalOrderCnt(Integer totalOrderCnt) {
        this.totalOrderCnt = totalOrderCnt;
    }

    public Integer getTotalPaidOrderAmount() {
        return totalPaidOrderAmount;
    }

    public void setTotalPaidOrderAmount(Integer totalPaidOrderAmount) {
        this.totalPaidOrderAmount = totalPaidOrderAmount;
    }

    public Integer getAppRegUserCnt() {
        return appRegUserCnt;
    }

    public void setAppRegUserCnt(Integer appRegUserCnt) {
        this.appRegUserCnt = appRegUserCnt;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
