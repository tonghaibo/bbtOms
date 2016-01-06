package models.statistics;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 快递员店铺数据实体类
 * @author chenxi
 */
@Entity
@Table(name = "stat_daily_postman_shop")
public class StatDailyPostmanShop implements Serializable {

    private static final long serialVersionUID = 4927875382394220785L;

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "stat_date", columnDefinition = " varchar(11) '' ")
    private String statDate;

    @Column(name = "scan_pv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer scanPv;

    @Column(name = "scan_uv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer scanUv;

    @Column(name = "new_follow_cnt", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer newFollowCnt;

    @Column(name = "active_follow_cnt", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer activeFollowCnt;

    @Column(name = "total_follow_cnt", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer totalFollowCnt;

    @Column(name = "shop_list_pv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer shopListPv;

    @Column(name = "shop_list_uv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer shopListUv;

    @Column(name = "shop_detail_pv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer shopDetailPv;

    @Column(name = "shop_detail_uv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer shopDetailUv;

    @Column(name = "shop_buy_pv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer shopBuyPv;

    @Column(name = "shop_buy_uv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer shopBuyUv;

    @Column(name = "shop_confirm_pv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer shopConfirmPv;

    @Column(name = "shop_confirm_uv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer shopConfirmUv;

    @Column(name = "shop_pay_pv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer shopPayPv;

    @Column(name = "shop_pay_uv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer shopPayUv;

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

    public Integer getScanPv() {
        return scanPv;
    }

    public void setScanPv(Integer scanPv) {
        this.scanPv = scanPv;
    }

    public Integer getScanUv() {
        return scanUv;
    }

    public void setScanUv(Integer scanUv) {
        this.scanUv = scanUv;
    }

    public Integer getNewFollowCnt() {
        return newFollowCnt;
    }

    public void setNewFollowCnt(Integer newFollowCnt) {
        this.newFollowCnt = newFollowCnt;
    }

    public Integer getActiveFollowCnt() {
        return activeFollowCnt;
    }

    public void setActiveFollowCnt(Integer activeFollowCnt) {
        this.activeFollowCnt = activeFollowCnt;
    }

    public Integer getTotalFollowCnt() {
        return totalFollowCnt;
    }

    public void setTotalFollowCnt(Integer totalFollowCnt) {
        this.totalFollowCnt = totalFollowCnt;
    }

    public Integer getShopListPv() {
        return shopListPv;
    }

    public void setShopListPv(Integer shopListPv) {
        this.shopListPv = shopListPv;
    }

    public Integer getShopListUv() {
        return shopListUv;
    }

    public void setShopListUv(Integer shopListUv) {
        this.shopListUv = shopListUv;
    }

    public Integer getShopDetailPv() {
        return shopDetailPv;
    }

    public void setShopDetailPv(Integer shopDetailPv) {
        this.shopDetailPv = shopDetailPv;
    }

    public Integer getShopDetailUv() {
        return shopDetailUv;
    }

    public void setShopDetailUv(Integer shopDetailUv) {
        this.shopDetailUv = shopDetailUv;
    }

    public Integer getShopBuyPv() {
        return shopBuyPv;
    }

    public void setShopBuyPv(Integer shopBuyPv) {
        this.shopBuyPv = shopBuyPv;
    }

    public Integer getShopBuyUv() {
        return shopBuyUv;
    }

    public void setShopBuyUv(Integer shopBuyUv) {
        this.shopBuyUv = shopBuyUv;
    }

    public Integer getShopConfirmPv() {
        return shopConfirmPv;
    }

    public void setShopConfirmPv(Integer shopConfirmPv) {
        this.shopConfirmPv = shopConfirmPv;
    }

    public Integer getShopConfirmUv() {
        return shopConfirmUv;
    }

    public void setShopConfirmUv(Integer shopConfirmUv) {
        this.shopConfirmUv = shopConfirmUv;
    }

    public Integer getShopPayPv() {
        return shopPayPv;
    }

    public void setShopPayPv(Integer shopPayPv) {
        this.shopPayPv = shopPayPv;
    }

    public Integer getShopPayUv() {
        return shopPayUv;
    }

    public void setShopPayUv(Integer shopPayUv) {
        this.shopPayUv = shopPayUv;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
