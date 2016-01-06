package services.wx;

import utils.GlobalDBControl;

import com.avaje.ebean.Ebean;

import models.wx.WxUser;

public class WxUserService{
	private static WxUserService instance=null;
	public static WxUserService getInstance(){
		if(instance==null)
			instance=new WxUserService();
		return instance;
	}
	
	//添加微信用户 
	public void saveWxUser(WxUser wu){
		Ebean.getServer(GlobalDBControl.getDB()).save(wu);
	}
	
	public void updateWxUser(WxUser wu){
		Ebean.getServer(GlobalDBControl.getDB()).update(wu);
	}
	//根据unionid查询用户
	public WxUser getWxUserByUnionid(String unionid){
		return Ebean.getServer(GlobalDBControl.getDB()).find(WxUser.class)
				.where().eq("unionid", unionid).findUnique();
	}
	
	//根据unionid查询用户
	public WxUser getWxUserByopenid(String openid){
		return Ebean.getServer(GlobalDBControl.getDB()).find(WxUser.class)
				.where().eq("openid", openid).findUnique();
	}
	
	public void updateWxUserWithUnsub(String fromUserName) {
		// 将wx_user指定用户的fromuid置为空
		WxUser wxUser = this.getWxUserByopenid(fromUserName);
		if (wxUser != null && wxUser.getId() != null) {
			wxUser.setFromuid("");
			this.updateWxUser(wxUser);
		}
	}	
	public WxUser saveOrUpdateWxUser(String openid,String fromid){
		WxUser wxUser = this.getWxUserByopenid(openid);
		if (wxUser != null && wxUser.getId() != null) {
			wxUser.setFromuid(fromid);
			this.updateWxUser(wxUser);
		}
		return wxUser;
	}
}