package services.h5;

import java.util.List;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.PagedList;

import models.postcontent.PostReward;
import models.postcontent.Postcontent;
import models.postcontent.PostcontentUser;
import models.postman.BalanceWithdraw;
import models.postman.PostmanUser;
import models.postman.Reddot;
import play.Logger;
import utils.Constants;
import utils.Constants.PostcontentStas;
import utils.Constants.PostcontentTyps;

public class H5Service {
	private static final Logger.ALogger logger = Logger.of(H5Service.class);

	/**
	 * 根据快递员ID查询此快递员信息
	 * @param id
	 * @return
	 */
	public static PostmanUser getPostManUserById(Integer id){
		return Ebean.getServer(Constants.getDB()).find(PostmanUser.class,id);
	}

	/**
	 * 根据快递员token查询此快递员信息
	 * @param token
	 * @return
	 */
	public static PostmanUser getPostManUserByToken(String token){
		return Ebean.getServer(Constants.getDB()).find(PostmanUser.class).where().eq("token",token).findUnique();
	}
	/**
	 * 获取正在进行中的
	 * @return
	 *//*
	public static PostReward getPostReward(){
		return Ebean.getServer(Constants.getDB()).find(PostReward.class).where().eq("", paramObject);
	}*/
	/**
	 * 获取正在进行中的
	 * @return
	 */
	public static PostReward getPostRewardByRandomCode(int randomCode){
		List<PostReward> postRewardList = Ebean.getServer(Constants.getDB()).find(PostReward.class).where().eq("flg", 1).le("minNum", randomCode).ge("maxNum", randomCode).findList();
		if(postRewardList.isEmpty()){
			return null;
		}else{
			return postRewardList.get(0);
		}
	}
	/**
	 * 根据ID获取提现详情
	 * @param id
	 * @return
	 */
	public static BalanceWithdraw getBalanceIncomeById(int id) {
		return Ebean.getServer(Constants.getDB()).find(BalanceWithdraw.class,id);
	}
	/**
	 * 保存快递员信息
	 * @param postmanUser
	 * @return
	 */
	public static PostmanUser savePostuserman(PostmanUser postmanUser){
		 Ebean.getServer(Constants.getDB()).save(postmanUser);
		 return postmanUser;
	}
	/**
	 * 根据公司ID与工号去获取是否存在红点
	 * @param companyid
	 * @param staffid
	 * @return
	 */
	public static Reddot getReddotByUid(Integer postmanid) {
		List<Reddot> reddotList = Ebean.getServer(Constants.getDB()).find(Reddot.class).where().eq("uid", postmanid).findList();
		if(reddotList.isEmpty()){
			return null;
		}else{
			return reddotList.get(0);
		}
	}
	/**
	 * 根据用户ID与任务ID获取此关联信息
	 * @param pcid
	 * @param postmanid
	 * @return
	 */
	public static PostcontentUser getPostcontentUserByPcidAndUid(Integer pcid,Integer postmanid) {
		List<PostcontentUser> postcontentUserList = Ebean.getServer(Constants.getDB()).find(PostcontentUser.class).where().eq("uid", postmanid).eq("pcid", pcid).findList();
		if(postcontentUserList.isEmpty()){
			return null;
		}else{
			return postcontentUserList.get(0);
		}
	}
	/**
	 * 保存红点信息
	 * @param postmanUser
	 * @return
	 */
	public static Reddot saveReddot(Reddot reddot){
		 Ebean.getServer(Constants.getDB()).save(reddot);
		 return reddot;
	}
	
	/**
	 * 分页获取新闻
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public static PagedList<Postcontent> getPostcontent(int page,int pageSize){
		return Ebean.getServer(Constants.getDB()).find(Postcontent.class).where().eq("typ", PostcontentTyps.NEW.getStatus()).eq("sta", PostcontentStas.VALID.getStatus()).orderBy("id desc").findPagedList(page, pageSize);
	}

	/**
	 * 根据ID获取返利信息
	 * @param rewardid
	 * @return
	 */
	public static PostReward getPostRewardById(int rewardid) {
		List<PostReward> postRewardList = Ebean.getServer(Constants.getDB()).find(PostReward.class).where().eq("flg", 1).eq("id", rewardid).findList();
		if(postRewardList.isEmpty()){
			return null;
		}else{
			return postRewardList.get(0);
		}
	}
}