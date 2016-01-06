package models;

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

@Entity
@Table(name = "count_h5")
public class CountH5 implements Serializable {
	private static final long serialVersionUID = 2483896093444400505L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="createTime")
	private Date createtime;
	
	@Column
	private String ip;	//ip来源
	
	@Column
	private String url;//被访问URL
	@Column(name="shareType",columnDefinition = "varchar(2) DEFAULT ''")
	private String sharetype;//分享来源1微信，2微信短图，3微信长图
	@Column(columnDefinition = "varchar(2) DEFAULT '0'")
	private String iswx;//是否微信访问0其它，1微信
	@Column
	private String channel;//频道
	@Column(name="userId")
	private Long userid;//访问用户编号
	
	@Column
	private String openid;

	public Long getUserId() {
		return userid;
	}
	public void setUserId(Long userId) {
		this.userid = userId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getCreateTime() {
		return createtime;
	}
	public void setCreateTime(Date createTime) {
		this.createtime = createTime;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getShareType() {
		return sharetype;
	}
	public void setShareType(String shareType) {
		this.sharetype = shareType;
	}
	public String getIswx() {
		return iswx;
	}
	public void setIswx(String iswx) {
		this.iswx = iswx;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public String getSharetype() {
		return sharetype;
	}
	public void setSharetype(String sharetype) {
		this.sharetype = sharetype;
	}
	public Long getUserid() {
		return userid;
	}
	public void setUserid(Long userid) {
		this.userid = userid;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}

	
}
