package models.postcontent;

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
@Table(name = "post_reward")
public class PostReward extends Model implements Serializable {

	private static final long serialVersionUID = -1619641047129597335L;
	@Id
	public Integer id;
	@Column(columnDefinition = " int(11) DEFAULT 0 ")
	public Integer typ;			//1 首单额外奖励， 2 全局奖励 3购买分成奖励 4 小店关注奖励
	@Column(columnDefinition = " varchar(32) DEFAULT '' ")
	public String btm;			//开始时间
	@Column(columnDefinition = " varchar(16) DEFAULT '' ")
	public String etm;			//结束时间
	@Column(columnDefinition = " int(11) DEFAULT 0 ")
	public Integer commision;	//奖励金额
	@Column(columnDefinition = " int(11) DEFAULT 0 ")
	public Integer commisiontyp;//奖励比例 100%
	@Column(columnDefinition = " int(11) DEFAULT 0 ")
	public double randomval;//随机比例
	@Column(columnDefinition = "")
	public String congratulation;//恭喜文案
	@Column(columnDefinition = "")
	public String remarktxt;	//说明文案
	@Column(columnDefinition = "")
	public String ruletxt;		//规则文案
	@Column()
	public Integer minNum;	//最小数
	@Column()
	public Integer maxNum;		//最大数
	@Column(columnDefinition = " int(11) DEFAULT 0 ")
	public Integer flg;			//1有效 2无效
	@Column(name = "date_new")
	@Temporal(TemporalType.TIMESTAMP)
	public Date dateNew;
	@Column(name = "date_upd")
	@Temporal(TemporalType.TIMESTAMP)
	public Date dateUpd;
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
	public String getBtm() {
		return btm;
	}
	public void setBtm(String btm) {
		this.btm = btm;
	}
	public String getEtm() {
		return etm;
	}
	public void setEtm(String etm) {
		this.etm = etm;
	}
	public Integer getCommision() {
		return commision;
	}
	public void setCommision(Integer commision) {
		this.commision = commision;
	}
	public Integer getCommisiontyp() {
		return commisiontyp;
	}
	public void setCommisiontyp(Integer commisiontyp) {
		this.commisiontyp = commisiontyp;
	}
	public String getCongratulation() {
		return congratulation;
	}
	public void setCongratulation(String congratulation) {
		this.congratulation = congratulation;
	}
	public String getRemarktxt() {
		return remarktxt;
	}
	public void setRemarktxt(String remarktxt) {
		this.remarktxt = remarktxt;
	}
	public String getRuletxt() {
		return ruletxt;
	}
	public void setRuletxt(String ruletxt) {
		this.ruletxt = ruletxt;
	}
	public Integer getFlg() {
		return flg;
	}
	public void setFlg(Integer flg) {
		this.flg = flg;
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
	public double getRandomval() {
		return randomval;
	}
	public void setRandomval(double randomval) {
		this.randomval = randomval;
	}
	
	public Integer getMinNum() {
		return minNum;
	}
	public void setMinNum(Integer minNum) {
		this.minNum = minNum;
	}
	public Integer getMaxNum() {
		return maxNum;
	}
	public void setMaxNum(Integer maxNum) {
		this.maxNum = maxNum;
	}
	
}
