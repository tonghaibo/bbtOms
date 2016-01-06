package models.postcontent;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 内容图片实体类
 * @author luobotao
 *
 */
@Entity
@Table(name = "postcontent_user")
public class PostcontentUser implements Serializable {

	private static final long serialVersionUID = 7173705035139729979L;

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name="pcid")
	private Integer pcid;
	@Column(name="uid")
	private Integer uid;//
	@Column(name="magid")
	private Integer magid;//杂志ID
	@Column(name="rewardid")
	private Integer rewardid;//奖励ID
	@Column(name="sta",columnDefinition=" varchar(2) default '0' ")
	private String sta;//
	@Column(name = "date_new")
	@Temporal(TemporalType.TIMESTAMP)
	public Date dateNew;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getPcid() {
		return pcid;
	}
	public void setPcid(Integer pcid) {
		this.pcid = pcid;
	}
	public Integer getUid() {
		return uid;
	}
	public void setUid(Integer uid) {
		this.uid = uid;
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
	public Integer getMagid() {
		return magid;
	}
	public void setMagid(Integer magid) {
		this.magid = magid;
	}
	public Integer getRewardid() {
		return rewardid;
	}
	public void setRewardid(Integer rewardid) {
		this.rewardid = rewardid;
	}
	
	
	
}
