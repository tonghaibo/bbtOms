package models.postman;

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
@Table(name = "postmanuser_temp")
public class PostmanUserTemp extends Model implements Serializable {

	private static final long serialVersionUID = -1619641047129597335L;
	@Id
	public Integer id;
	@Column(name="postmanid")
	public Integer postmanid;
	@Column(name="orderid")
	public Integer orderid;
	
	
	public Integer getOrderid() {
		return orderid;
	}

	public void setOrderid(Integer orderid) {
		this.orderid = orderid;
	}

	public Integer getPostmanid() {
		return postmanid;
	}

	public void setPostmanid(Integer postmanid) {
		this.postmanid = postmanid;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
