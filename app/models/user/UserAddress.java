package models.user;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_address")
public class UserAddress implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2483896093420151209L;
	/*
	 * 通讯录表
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	@Column(name="uid")
	private Integer uid;//用户编号
	@Column(name="longs")
	private Double longs;//经度
	@Column(name="lat")
	private Double lat;//纬度
	@Column(name="username")
	private String username;//姓名
	@Column(name="phone")
	private String phone;//手机
	@Column(name="address")
	private String address;//地址
	@Column(name="typ")
	private Integer typ ;//类型1发件人，2收件人
	@Column(name="date_new")
	private Date date_new;//创建时间
	@Column(name="date_upd")
	private Date date_upd ;//更新时间
	
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
	public Double getInteger() {
		return longs;
	}
	public void setInteger(Double l) {
		longs = l;
	}
	public Double getLat() {
		return lat;
	}
	public void setLat(Double la) {
		lat = la;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Integer getTyp() {
		return typ;
	}
	public void setTyp(Integer typ) {
		this.typ = typ;
	}
	public Double getLongs() {
		return longs;
	}
	public void setLongs(Double longs) {
		this.longs = longs;
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
