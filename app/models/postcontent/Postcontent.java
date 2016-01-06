package models.postcontent;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * 内容实体类
 * @author luobotao
 *
 */
@Entity
@Table(name = "postcontent")
public class Postcontent implements Serializable {

	private static final long serialVersionUID = 5038986493405331896L;
	@Id
	@GeneratedValue
	private Integer id;
	
	@Column(name="typ",columnDefinition = " int(11)")
	private Integer typ;

	@Column(name="typname",columnDefinition = " varchar(32) '' ")
	private String typname;//
	
	@Column(name="typicon",columnDefinition = " varchar(256) '' ")
	private String typicon;//
	
	@Column(name="title",columnDefinition = " varchar(64) '' ")
	private String title;//
	
	@Column(name="subtitle",columnDefinition = " varchar(256) '' ")
	private String subtitle;//
	
	@Column(name="content",columnDefinition = " text  ")
	private String content;//
	
	@Column(name="expectamount",columnDefinition = " int(11)")
	private int expectamount;
	
	@Column(name="amount",columnDefinition = " varchar(256) DEFAULT ''")
	private String amount;
	
	@Column(name="tips",columnDefinition = " varchar(64) '' ")
	private String tips;//
	
	@Column(name="linkurl",columnDefinition = " varchar(256) '' ")
	private String linkurl;//
	
	@Column(name="dateremark",columnDefinition = " varchar(64) '' ")
	private String dateremark;//
	
	@Column(name = "start_tim")
	private String start_tim;
	@Column(name = "end_tim")
	private String end_tim;
	
	@Column(name="nsort",columnDefinition = " int(11) DEFAULT 0 ")
	private Integer nsort;

	@Column(name="sta",columnDefinition = " varchar(2) '0' ")
	private String sta;//
	
	@Column(name = "date_new")
	@Temporal(TemporalType.TIMESTAMP)
	public Date dateNew;
	
	@Column(name = "date_upd")
	@Temporal(TemporalType.TIMESTAMP)
	public Date dateUpd;
	
	@Transient
	public PostcontentDetail postcontentDetail;
	
	@Transient
	public List<PostcontentImg> postcontentImgList;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getTyp() {
		return typ;
	}

	public void setTyp(Integer typ) {
		this.typ = typ;
	}

	public String getTypname() {
		return typname;
	}

	public void setTypname(String typname) {
		this.typname = typname;
	}

	public String getTypicon() {
		return typicon;
	}

	public void setTypicon(String typicon) {
		this.typicon = typicon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getTips() {
		return tips;
	}

	public void setTips(String tips) {
		this.tips = tips;
	}

	public String getLinkurl() {
		return linkurl;
	}

	public void setLinkurl(String linkurl) {
		this.linkurl = linkurl;
	}

	public String getDateremark() {
		return dateremark;
	}

	public void setDateremark(String dateremark) {
		this.dateremark = dateremark;
	}


	public String getStart_tim() {
		return start_tim;
	}

	public void setStart_tim(String start_tim) {
		this.start_tim = start_tim;
	}

	public String getEnd_tim() {
		return end_tim;
	}

	public void setEnd_tim(String end_tim) {
		this.end_tim = end_tim;
	}

	public Integer getNsort() {
		return nsort;
	}

	public void setNsort(Integer nsort) {
		this.nsort = nsort;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public PostcontentDetail getPostcontentDetail() {
		return postcontentDetail;
	}

	public void setPostcontentDetail(PostcontentDetail postcontentDetail) {
		this.postcontentDetail = postcontentDetail;
	}

	public int getExpectamount() {
		return expectamount;
	}

	public void setExpectamount(int expectamount) {
		this.expectamount = expectamount;
	}

	public List<PostcontentImg> getPostcontentImgList() {
		return postcontentImgList;
	}

	public void setPostcontentImgList(List<PostcontentImg> postcontentImgList) {
		this.postcontentImgList = postcontentImgList;
	}
	
}
