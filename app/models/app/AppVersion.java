package models.app;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.avaje.ebean.Model;

@Entity
@Table(name = "versioninfo")
public class AppVersion extends Model implements Serializable {

	private static final long serialVersionUID = -1619641047129597335L;
	@Id
	public Integer id;
	@Column(columnDefinition = " varchar(2) DEFAULT '' ")
	public String ostype;			//系统类型  1 ios 2 android
	@Column(columnDefinition = " varchar(16) DEFAULT '' ")
	public String latestver;		//最近版本号
	@Column(columnDefinition = " varchar(2) DEFAULT '' ")
	public String isforced;		//是否强制更新
	@Column(name="remind_time")
	public String remindTime;		//多长时间更新一次
	@Column(columnDefinition = " varchar(255) DEFAULT '' ")
	public String title;		//是否强制更新
	@Column(columnDefinition = "")
	public String message;		//提示文稿
	@Column(columnDefinition = " varchar(255) DEFAULT '' ")
	public String url;		//地址
	@Column(columnDefinition = " varchar(2) DEFAULT ''")
	public String sta;			//1有效 2无效
	@Column(name = "date_new")
	@Temporal(TemporalType.TIMESTAMP)
	public Date dateNew;
	@Column(name = "date_upd")
	@Temporal(TemporalType.TIMESTAMP)
	public Date dateUpd;
	@Column(columnDefinition = " varchar(255) DEFAULT '' ")
	public String marketcode;		//渠道号
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getOstype() {
		return ostype;
	}
	public void setOstype(String ostype) {
		this.ostype = ostype;
	}
	public String getLatestver() {
		return latestver;
	}
	public void setLatestver(String latestver) {
		this.latestver = latestver;
	}
	public String getIsforced() {
		return isforced;
	}
	public void setIsforced(String isforced) {
		this.isforced = isforced;
	}
	public String getRemindTime() {
		return remindTime;
	}
	public void setRemindTime(String remindTime) {
		this.remindTime = remindTime;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getSta() {
		return sta;
	}
	public void setSta(String sta) {
		this.sta = sta;
	}
	public Date getDateNew() {
		return dateNew;
	}
	public void setDateNew(Date dateNew) {
		this.dateNew = dateNew;
	}
	public Date getDateUpd() {
		return dateUpd;
	}
	public void setDateUpd(Date dateUpd) {
		this.dateUpd = dateUpd;
	}
	public String getMarketcode() {
		return marketcode;
	}
	public void setMarketcode(String marketcode) {
		this.marketcode = marketcode;
	}
	
}
