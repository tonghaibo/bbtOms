package services.user;

import java.util.ArrayList;
import java.util.List;

import models.user.UserAddress;

import org.apache.commons.lang3.StringUtils;

import utils.GlobalDBControl;
import utils.Htmls;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.PagedList;
import com.avaje.ebean.Query;

import form.UserAddressForm;

public class UserAddressService{
	public static UserAddressService instance=new UserAddressService();
	private UserAddressService(){}
	
	//根据地址编号获取地址
	public UserAddress getAddressbyid(Long id){
		return Ebean.getServer(GlobalDBControl.getDB()).find(UserAddress.class)
				.where().eq("id", id).findUnique();
	}

	
	public PagedList<UserAddress> findUserAddressList(UserAddressForm formPage) {
		List<String> sqlList = new ArrayList<>();
		List<Object> paramsList = new ArrayList<>();
		Query<UserAddress> query = Ebean.getServer(GlobalDBControl.getDB()).createQuery(UserAddress.class);
		query.where(StringUtils.join(sqlList, " AND "));

		int i = 1; // first param uses index 1 NOT 0!
		for (Object param : paramsList) {
		    query.setParameter(i, param);
		    i++;
		}
		PagedList<UserAddress> xyzList = query.orderBy("id desc").findPagedList(formPage.page, formPage.size);
		return xyzList;
	}

	public UserAddress findUserAddressById(int id) {
		return Ebean.getServer(GlobalDBControl.getDB()).find(UserAddress.class).where().eq("id", id).findUnique();
	}

	//根据用户编号及类型获取最后一条地址信息
	public UserAddress getlastaddress(Integer userid,Integer typ){
		List<UserAddress> alist =Ebean.getServer(GlobalDBControl.getDB()).find(UserAddress.class)
		.where().eq("uid", userid).eq("typ", typ).orderBy(" id desc").findList();
		if(alist==null || alist.isEmpty())
			return null;
		return alist.get(0);
	}

	/**
	 * 
	 * <p>Title: save</p> 
	 * <p>Description: 保存版本信息</p> 
	 * @param appVersioUserAddressturn
	 */
	public UserAddress save(UserAddress userAddress) {
		Ebean.getServer(GlobalDBControl.getDB()).save(userAddress);
		return userAddress;
	}

	public UserAddress update(UserAddress userAddress) {
		Ebean.getServer(GlobalDBControl.getDB()).update(userAddress);
		return userAddress;
	}

	public void delUserAddress(int id) {
		UserAddress userAddress = findUserAddressById(id);
		Ebean.getServer(GlobalDBControl.getDB()).delete(userAddress);
	}

	public List<UserAddress> queryAllByUidAndTyp(Long uid, String typ) {
		return Ebean.getServer(GlobalDBControl.getDB()).find(UserAddress.class).where().eq("uid", uid).eq("typ", typ).findList();
	}

	public String userAddressList2Html(List<UserAddress> userAddressesList,
			String id) {
		StringBuilder sb = new StringBuilder();
		sb.append(Htmls.generateOption(-1, ""));
		for (UserAddress c : userAddressesList) {
			if (id != null && id.equals(c.getId()+"")) {
				sb.append(Htmls.generateSelectedOption(c.getId(), c.getUsername()));
			} else {
				sb.append(Htmls.generateOption(c.getId(), c.getUsername()));
			}
		}
		return sb.toString();
	}
}