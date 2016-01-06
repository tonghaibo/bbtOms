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
@Table(name = "wx_user")
public class WxUser implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5139981429177632113L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(name="date_new")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createtime;
	@Column(columnDefinition = "varchar(255) DEFAULT '' ")
	private String unionid;
	@Column(columnDefinition = "varchar(255) DEFAULT '' ")
	private String nickname;
	@Column(columnDefinition = "varchar(255) DEFAULT '' ")
	private String headicon;
	@Column(columnDefinition = "varchar(255) DEFAULT '' ")
	private String fromuid;		//记录用户的来源棒棒糖快递员对应的userid
	@Column(columnDefinition = "varchar(255) DEFAULT '' ")
	private String openid;		//记录微信用户的openid ->fromuserid
	@Column(columnDefinition = "varchar(255) DEFAULT '' ")
	private String userid;		//记录用户在user表里的对应id
	@Column(columnDefinition = "varchar(10) DEFAULT '' ")
	private String tmpflg;
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
	public String getUnionid() {
		return unionid;
	}
	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getHeadicon() {
		return headicon;
	}
	public void setHeadicon(String headicon) {
		this.headicon = headicon;
	}
	public String getFromuid() {
		return fromuid;
	}
	public void setFromuid(String fromuid) {
		this.fromuid = fromuid;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getTmpflg() {
		return tmpflg;
	}
	public void setTmpflg(String tmpflg) {
		this.tmpflg = tmpflg;
	}
	
	
}
