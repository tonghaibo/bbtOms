package models.statistics;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 快递员商品销售数据实体类
 * @author chenxi
 */
@Entity
@Table(name = "stat_daily_postman_order_detail")
public class StatDailyPostmanOrderDetail implements Serializable {

    private static final long serialVersionUID = 4927875382394220785L;

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "stat_date", columnDefinition = " varchar(11) '' ")
    private String statDate;

    @Column(name = "goods_id", columnDefinition = " int(11) DEFAULT 0 ")
    private Long googsId;

    @Column(name="goods_name",columnDefinition = " varchar(200) '' ")
    private String goodsName;

    @Column(name = "sold_cnt", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer soldCnt;

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

    public Long getGoogsId() {
        return googsId;
    }

    public void setGoogsId(Long googsId) {
        this.googsId = googsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Integer getSoldCnt() {
        return soldCnt;
    }

    public void setSoldCnt(Integer soldCnt) {
        this.soldCnt = soldCnt;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
