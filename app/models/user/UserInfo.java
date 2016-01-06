package models.user;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
/*
 * 用户表
 */
@Entity
@Table(name = "user_info")
public class UserInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2483896093420151207L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer uid;
	@Column(name="openid")
	private String openid;//微信ＵＮＩＯＮＩＤ
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	@Column(name="nickname")
	private String NickName;//妮称
	@Column(name="date_new")
	private Date date_new;//创建时间
	@Column(name="date_upd")
	private Date date_upd;//更新时间
	@Column(name="typ")
	private Integer typ;//0商户，1个人
	@Column(name="lat")//纬度
	private Double lat;
	@Column(name="lng")
	private Double lng;//经度
	
	public Integer getUid() {
		return uid;
	}
	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public String getNickName() {
		return NickName;
	}
	public void setNickName(String nickName) {
		NickName = nickName;
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
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public Double getLng() {
		return lng;
	}
	public void setLng(Double lng) {
		this.lng = lng;
	}	
}
