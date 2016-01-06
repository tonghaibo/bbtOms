package models.user;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import models.order.PostOrder;

@Entity
@Table(name = "user_balance_log")
public class UserBalanceLog implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2483896093420151208L;
	/*
	 * 余额变动表
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	@Column(name="uid")
	private Integer uid;//用户编号
	@Column(name="orderid")
	private String orderid;//订单编号
	@Column(name="changemoney")
	private Integer changemoney;//变动金额
	@Column(name="beforebalance")
	private Integer beforebalance;//变动前金额
	@Column(name="endbalance")
	private Integer endbalance;//变动后金额
	@Column(name="remark")
	private String remark;//备注
	@Column(name="typ")
	private Integer typ;//类型，1收入，2支出
	@Column(name="date_new")
	private Date date_new;//创建时间
	@Column(name="date_upd")
	private Date date_upd;//更新时间
	
	@Transient
	private PostOrder postOrder;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getUid() {
		return uid;
	}
	public void setUid(Integer uid) {
		this.uid = uid;
	}
	public Integer getChangemoney() {
		return changemoney;
	}
	public void setChangemoney(Integer changemoney) {
		this.changemoney = changemoney;
	}
	public Integer getBeforebalance() {
		return beforebalance;
	}
	public void setBeforebalance(Integer beforebalance) {
		this.beforebalance = beforebalance;
	}
	public Integer getEndbalance() {
		return endbalance;
	}
	public void setEndbalance(Integer endbalance) {
		this.endbalance = endbalance;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Date getDate_new() {
		return date_new;
	}
	public void setDate_new(Date date_new) {
		this.date_new = date_new;
	}
	public Date getDate_upd() {
		return date_upd;
	}
	public void setDate_upd(Date date_upd) {
		this.date_upd = date_upd;
	}
	public Integer getTyp() {
		return typ;
	}
	public void setTyp(Integer typ) {
		this.typ = typ;
	}
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	public PostOrder getPostOrder() {
		return postOrder;
	}
	public void setPostOrder(PostOrder postOrder) {
		this.postOrder = postOrder;
	}	
	
}
