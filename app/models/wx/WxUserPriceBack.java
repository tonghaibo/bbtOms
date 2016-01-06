package models.wx;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
/*
 *微认鉴权表 
 */
@Entity
@Table(name = "wx_user_PriceBack")
public class WxUserPriceBack implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5139981429177632113L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(name="date_add")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateAdd;
	@Column(columnDefinition = "varchar(255) DEFAULT '' ")
	private Long uid;
	@Column(columnDefinition = "varchar(255) DEFAULT '' ")
	private Long postmanid;
	@Column(columnDefinition = "varchar(255) DEFAULT '' ")
	private double balance;
	@Column(columnDefinition = "varchar(255) DEFAULT '' ")
	private String sta;		
	@Column(columnDefinition = "varchar(255) DEFAULT '' ")
	private int typ;		
	@Column(columnDefinition = "varchar(255) DEFAULT '' ")
	private Long orderid;	
	@Column(name="orderCode")
	private String ordercode;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getDateAdd() {
		return dateAdd;
	}
	public Long getUid() {
		return uid;
	}
	public void setUid(Long uid) {
		this.uid = uid;
	}
	public Long getPostmanid() {
		return postmanid;
	}
	public void setPostmanid(Long postmanid) {
		this.postmanid = postmanid;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public String getSta() {
		return sta;
	}
	public void setSta(String sta) {
		this.sta = sta;
	}
	public String getOrderCode() {
		return ordercode;
	}
	public void setOrderCode(String orderCode) {
		this.ordercode = orderCode;
	}
	public void setDateAdd(Date dateAdd) {
		this.dateAdd = dateAdd;
	}
	public int getTyp() {
		return typ;
	}
	public void setTyp(int typ) {
		this.typ = typ;
	}
	public Long getOrderid() {
		return orderid;
	}
	public void setOrderid(Long orderid) {
		this.orderid = orderid;
	}
	
}
