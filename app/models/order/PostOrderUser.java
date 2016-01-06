package models.order;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "post_order_user")
public class PostOrderUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2483896093420151211L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	@Column(name="orderid")
	private Integer orderid;//订单编号
	@Column(name="postmanid")
	private Integer postmanid;//快递员编号
	@Column(name="status")
	private Integer status;//同城派送状态0待配送，1配送中，2已完成，3异常
	@Column(name="date_new")
	private Date date_new;//创建时间
	@Column(name="date_upd")
	private Date date_upd;//更新时间
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getPostmanid() {
		return postmanid;
	}
	public void setPostmanid(Integer postmanid) {
		this.postmanid = postmanid;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getOrderid() {
		return orderid;
	}
	public void setOrderid(Integer orderid) {
		this.orderid = orderid;
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
	
}
