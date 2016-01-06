package services.postcontent;

import models.postcontent.PostReward;
import play.Logger;
import play.Logger.ALogger;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.PagedList;
import com.avaje.ebean.Query;

import form.PostRewardForm;
import services.ServiceFactory;
import utils.GlobalDBControl;

public class PostRewardService {
	private static final PostRewardService productService = ServiceFactory.getPostRewardService();
	private static PostRewardService instance = null;
	private static ALogger logger = Logger.of(PostRewardService.class);

	public static PostRewardService getInstance() {
		if (instance == null) {
			syncInit();
		}
		return instance;
	}

	private PostRewardService() {

	}

	private static synchronized void syncInit() {
		if (instance == null) {
			instance = new PostRewardService();
		}
	}

	public PagedList<PostReward> findPostRewardList(PostRewardForm formPage) {
		/*List<String> sqlList = new ArrayList<>();
		List<Object> paramsList = new ArrayList<>();*/
		Query<PostReward> query = Ebean.getServer(GlobalDBControl.getDB()).createQuery(PostReward.class);
/*		query.where(StringUtils.join(sqlList, " AND "));

		int i = 1; // first param uses index 1 NOT 0!
		for (Object param : paramsList) {
		    query.setParameter(i, param);
		    i++;
		}
*/		PagedList<PostReward> xyzList = query.orderBy("btm desc,etm desc").findPagedList(formPage.page, formPage.size);
		return xyzList;
	}

	public PostReward findPostRewardById(Long id) {
		return Ebean.getServer(GlobalDBControl.getDB()).find(PostReward.class).where().eq("id", id).findUnique();
	}

	/**
	 * 
	 * <p>Title: savePostReward</p> 
	 * <p>Description: 保存任务信息</p> 
	 * @param typ
	 * @param typname
	 * @param title
	 * @param content
	 * @param amount
	 * @param linkurl
	 * @param dateremark
	 * @param start_tim
	 * @param end_tim
	 * @param nsort
	 * @return
	 */
	public PostReward save(PostReward postReward) {
		Ebean.getServer(GlobalDBControl.getDB()).save(postReward);
		return postReward;
	}

	public PostReward update(PostReward postReward) {
		Ebean.getServer(GlobalDBControl.getDB()).update(postReward);
		return postReward;
	}




	
}
