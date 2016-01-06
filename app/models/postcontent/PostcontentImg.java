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
import javax.persistence.Transient;

import models.magazine.Magazinelist;

/**
 * 内容图片实体类
 * @author luobotao
 *
 */
@Entity
@Table(name = "postcontent_img")
public class PostcontentImg implements Serializable {

	private static final long serialVersionUID = 5038986493405331896L;
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(name="pcid",columnDefinition = " int(11)")
	private Long pcid;

	
	@Column(name="imgurl",columnDefinition = " varchar(256) '0' ")
	private String imgurl;//

	
	@Column(name = "date_new")
	@Temporal(TemporalType.TIMESTAMP)
	public Date dateNew;
	
	@Column(name="magid",columnDefinition = " int(11)")
	private Long magid;

	@Transient
	private Magazinelist magazinelist;
	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Long getPcid() {
		return pcid;
	}


	public void setPcid(Long pcid) {
		this.pcid = pcid;
	}


	public String getImgurl() {
		return imgurl;
	}


	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}


	public Date getDateNew() {
		return dateNew;
	}


	public void setDateNew(Date dateNew) {
		this.dateNew = dateNew;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	public Long getMagid() {
		return magid;
	}


	public void setMagid(Long magid) {
		this.magid = magid;
	}


	public Magazinelist getMagazinelist() {
		return magazinelist;
	}


	public void setMagazinelist(Magazinelist magazinelist) {
		this.magazinelist = magazinelist;
	}

	
	
}
