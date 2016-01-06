package models.magazine;

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
 * 杂志与用户关联实体类
 * @author luobotao
 *
 */
@Entity
@Table(name = "magazine_user")
public class MagazineUser implements Serializable {

	private static final long serialVersionUID = -193048856744162371L;

	@Id
	@GeneratedValue
	private Integer id;
	
	@Column(name="magid")
	private Integer magid;
	@Column(name="uid")
	private Integer uid;//
	@Column(name = "date_new")
	@Temporal(TemporalType.TIMESTAMP)
	public Date dateNew;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getMagid() {
		return magid;
	}
	public void setMagid(Integer magid) {
		this.magid = magid;
	}
	public Integer getUid() {
		return uid;
	}
	public void setUid(Integer uid) {
		this.uid = uid;
	}
	public Date getDateNew() {
		return dateNew;
	}
	public void setDateNew(Date dateNew) {
		this.dateNew = dateNew;
	}
	
	
}
