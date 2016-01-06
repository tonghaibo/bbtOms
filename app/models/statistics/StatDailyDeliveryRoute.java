package models.statistics;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 配送路径数据实体类
 *
 * @author chenxi
 */
@Entity
@Table(name = "stat_daily_delivery_route")
public class StatDailyDeliveryRoute implements Serializable {

    private static final long serialVersionUID = 4927875382394220785L;

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "stat_date", columnDefinition = " varchar(11) '' ")
    private String statDate;

    @Column(name = "app_scan_pv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer appScanPv;

    @Column(name = "app_scan_uv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer appScanUv;

    @Column(name = "app_search_pv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer appSearchPv;

    @Column(name = "app_search_uv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer appSearchUv;

    @Column(name = "app_order_list_pv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer appOrderListPv;

    @Column(name = "app_order_list_uv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer appOrderListUv;

    @Column(name = "app_order_info_pv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer appOrderInfoPv;

    @Column(name = "app_order_info_uv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer appOrderInfoUv;

    @Column(name = "widget_order_list_pv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer widgetOrderListPv;

    @Column(name = "widget_order_list_uv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer widgetOrderListUv;

    @Column(name = "widget_order_info_pv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer widgetOrderInfoPv;

    @Column(name = "widget_order_info_uv", columnDefinition = " int(11) DEFAULT 0 ")
    private Integer widgetOrderInfoUv;

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

    public Integer getAppScanPv() {
        return appScanPv;
    }

    public void setAppScanPv(Integer appScanPv) {
        this.appScanPv = appScanPv;
    }

    public Integer getAppScanUv() {
        return appScanUv;
    }

    public void setAppScanUv(Integer appScanUv) {
        this.appScanUv = appScanUv;
    }

    public Integer getAppSearchPv() {
        return appSearchPv;
    }

    public void setAppSearchPv(Integer appSearchPv) {
        this.appSearchPv = appSearchPv;
    }

    public Integer getAppSearchUv() {
        return appSearchUv;
    }

    public void setAppSearchUv(Integer appSearchUv) {
        this.appSearchUv = appSearchUv;
    }

    public Integer getAppOrderListPv() {
        return appOrderListPv;
    }

    public void setAppOrderListPv(Integer appOrderListPv) {
        this.appOrderListPv = appOrderListPv;
    }

    public Integer getAppOrderListUv() {
        return appOrderListUv;
    }

    public void setAppOrderListUv(Integer appOrderListUv) {
        this.appOrderListUv = appOrderListUv;
    }

    public Integer getAppOrderInfoPv() {
        return appOrderInfoPv;
    }

    public void setAppOrderInfoPv(Integer appOrderInfoPv) {
        this.appOrderInfoPv = appOrderInfoPv;
    }

    public Integer getAppOrderInfoUv() {
        return appOrderInfoUv;
    }

    public void setAppOrderInfoUv(Integer appOrderInfoUv) {
        this.appOrderInfoUv = appOrderInfoUv;
    }

    public Integer getWidgetOrderListPv() {
        return widgetOrderListPv;
    }

    public void setWidgetOrderListPv(Integer widgetOrderListPv) {
        this.widgetOrderListPv = widgetOrderListPv;
    }

    public Integer getWidgetOrderListUv() {
        return widgetOrderListUv;
    }

    public void setWidgetOrderListUv(Integer widgetOrderListUv) {
        this.widgetOrderListUv = widgetOrderListUv;
    }

    public Integer getWidgetOrderInfoPv() {
        return widgetOrderInfoPv;
    }

    public void setWidgetOrderInfoPv(Integer widgetOrderInfoPv) {
        this.widgetOrderInfoPv = widgetOrderInfoPv;
    }

    public Integer getWidgetOrderInfoUv() {
        return widgetOrderInfoUv;
    }

    public void setWidgetOrderInfoUv(Integer widgetOrderInfoUv) {
        this.widgetOrderInfoUv = widgetOrderInfoUv;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
