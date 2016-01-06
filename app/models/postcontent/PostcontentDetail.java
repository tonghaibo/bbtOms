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
 * 
 * <p>Title: PostcontentDetail.java</p> 
 * <p>Description: 新闻内容类</p> 
 * <p>Company: higegou</p> 
 * @author  ctt
 * date  2015年11月17日  下午8:10:08
 * @version
 */
@Entity
@Table(name = "postcontent_detail")
public class PostcontentDetail implements Serializable {

	private static final long serialVersionUID = 5038986493405331896L;
	@Id
	@GeneratedValue
	private Integer id;
	
	@Column(name="pcid",columnDefinition = " int(11)")
	private int pcid;

	@Column(name="pccontent",columnDefinition = " varchar(32) '' ")
	private String pccontent;//
	
	@Column(name = "date_new")
	@Temporal(TemporalType.TIMESTAMP)
	public Date dateNew;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getPcid() {
		return pcid;
	}

	public void setPcid(int pcid) {
		this.pcid = pcid;
	}

	public String getPccontent() {
		return pccontent;
	}

	public void setPccontent(String pccontent) {
		this.pccontent = pccontent;
	}

	public Date getDateNew() {
		return dateNew;
	}
	public void setDateNew(Date dateNew) {
		this.dateNew = dateNew;
	}

	
}
