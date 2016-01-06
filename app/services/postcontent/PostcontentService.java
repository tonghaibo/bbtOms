package services.postcontent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;









import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.Logger.ALogger;
import models.postcontent.Postcontent;
import models.postcontent.PostcontentDetail;
import models.postcontent.PostcontentImg;
import models.postcontent.PostcontentUser;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Expression;
import com.avaje.ebean.PagedList;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import form.PostcontentForm;
import services.ServiceFactory;
import utils.Constants;
import utils.GlobalDBControl;
import utils.JdbcOper;
import utils.Numbers;
import utils.StringUtil;

public class PostcontentService {
	private static final PostcontentService productService = ServiceFactory.getPostcontentService();
	private static PostcontentService instance = null;
	private static ALogger logger = Logger.of(PostcontentService.class);

	public static PostcontentService getInstance() {
		if (instance == null) {
			syncInit();
		}
		return instance;
	}

	private PostcontentService() {

	}

	private static synchronized void syncInit() {
		if (instance == null) {
			instance = new PostcontentService();
		}
	}

	public PagedList<Postcontent> findPostcontentList(PostcontentForm formPage) {
		List<String> sqlList = new ArrayList<>();
		List<Object> paramsList = new ArrayList<>();
		if (formPage.typ!=-1) {
			sqlList.add("typ = ?");
			paramsList.add(formPage.typ);
		}
		if (formPage.sta!=-1) {
			sqlList.add("sta = ?");
			paramsList.add(formPage.sta);
		}
		if (!Strings.isNullOrEmpty(formPage.title)) {
		    sqlList.add("title like ?");
		    paramsList.add("%"+formPage.title + "%");
		}
		Query<Postcontent> query = Ebean.getServer(GlobalDBControl.getDB()).createQuery(Postcontent.class);
		query.where(StringUtils.join(sqlList, " AND "));

		int i = 1; // first param uses index 1 NOT 0!
		for (Object param : paramsList) {
		    query.setParameter(i, param);
		    i++;
		}
		PagedList<Postcontent> xyzList = query.orderBy("nsort desc,date_new desc").findPagedList(formPage.page, formPage.size);
		return xyzList;
	}

	public Postcontent findPostcontentById(Long id) {
		return Ebean.getServer(GlobalDBControl.getDB()).find(Postcontent.class).where().eq("id", id).findUnique();
	}

	/**
	 * 
	 * <p>Title: savePostcontent</p> 
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
	public Postcontent save(Postcontent postcontent) {
		Ebean.getServer(GlobalDBControl.getDB()).save(postcontent);
		return postcontent;
	}

	public Postcontent update(Postcontent postcontent) {
		Ebean.getServer(GlobalDBControl.getDB()).update(postcontent);
		return postcontent;
	}

	/**
	 * 
	 * <p>Title: findPostcontentByPcid</p> 
	 * <p>Description: 根据pcid获取图片列表</p> 
	 * @param id
	 * @return
	 */
	public List<PostcontentImg> findPostcontentImgByPcid(Long id) {
		if(id==null){
			id=0L;
		}
		return Ebean.getServer(GlobalDBControl.getDB()).find(PostcontentImg.class).where().eq("pcid", id).findList();
	}
	/**
	 * 根据任务ID与用户ID获取关联信息
	 * @param pcid
	 * @param uid
	 * @return
	 */
	public PostcontentUser getPostcontentUserByPcidAndUid(Integer pcid,Integer uid) {
		List<PostcontentUser> postcontentUserList = Ebean.getServer(GlobalDBControl.getDB()).find(PostcontentUser.class).where().eq("pcid", pcid).eq("uid", uid).findList();
		if(postcontentUserList.isEmpty()){
			return null;
		}else{
			return postcontentUserList.get(0);
		}
	}

	public void savePostcontentImg(PostcontentImg postcontentImg) {
		Ebean.getServer(GlobalDBControl.getDB()).save(postcontentImg);
	}
	/**
	 * 保存任务与用户的关联信息
	 * @param postcontentUser
	 */
	public void savePostcontentUser(PostcontentUser postcontentUser) {
		Ebean.getServer(GlobalDBControl.getDB()).save(postcontentUser);
	}

	public PostcontentImg findPostcontentImgByImgid(Long imageid) {
		return Ebean.getServer(GlobalDBControl.getDB()).find(PostcontentImg.class).where().eq("id", imageid).findUnique();
	}
	
	public void deletePostcontentImgId(PostcontentImg postcontentImg) {
		Ebean.getServer(GlobalDBControl.getDB()).delete(postcontentImg);
	}

	/**
	 * 
	 * <p>Title: initMagazineWithPostcontent</p> 
	 * <p>Description: 新建签到任务，初始化杂志内容</p> 
	 * @param postcontent
	 */
	public void initMagazineWithPostcontent(Postcontent postcontent) {
		//call  `sp_magazine_auto`(p_pcid INT)
		String sql="CALL `sp_magazine_auto`(?)";
		// 调用存储过程
		JdbcOper oper = JdbcOper.getCalledbleDao(sql);
		try {
			// 数据库存储过程操作
			oper.cst.setInt(1, postcontent.getId());
			oper.rs = oper.cst.executeQuery();
		} catch (Exception e) {
			logger.info(e.toString());
		} finally {
			oper.close();
		}
	}

	/**
	 * 
	 * <p>Title: initPostcontentUser</p> 
	 * <p>Description: 初始化任务，赋予所有的快递员</p> 
	 * @param postcontent
	 */
	public void initPostcontentUser(Postcontent postcontent) {
		String sql="CALL `sp_postmancontent_init`(?)";
		// 调用存储过程
		JdbcOper oper = JdbcOper.getCalledbleDao(sql);
		try {
			// 数据库存储过程操作
			oper.cst.setInt(1, postcontent.getId());
			oper.rs = oper.cst.executeQuery();
		} catch (Exception e) {
			logger.info(e.toString());
		} finally {
			oper.close();
		}
	}

	/**
	 * 
	 * <p>Title: delInitPostcontentUser</p> 
	 * <p>Description: 任务下线，解绑</p> 
	 * @param postcontent
	 */
	public void delInitPostcontentUser(Postcontent postcontent) {
		String sql="CALL `sp_postmancontent_delinit`(?)";
		// 调用存储过程
		JdbcOper oper = JdbcOper.getCalledbleDao(sql);
		try {
			// 数据库存储过程操作
			oper.cst.setInt(1, postcontent.getId());
			oper.rs = oper.cst.executeQuery();
		} catch (Exception e) {
			logger.info(e.toString());
		} finally {
			oper.close();
		}
	}

	/**
	 * 
	 * <p>Title: editInitPostcontent</p> 
	 * <p>Description: 任务信息变更，更新</p> 
	 * @param postcontent
	 */
	public void editInitPostcontent(Postcontent postcontent) {
		String sql="CALL `sp_postmancontent_editinit`(?)";
		// 调用存储过程
		JdbcOper oper = JdbcOper.getCalledbleDao(sql);
		try {
			// 数据库存储过程操作
			oper.cst.setInt(1, postcontent.getId());
			oper.rs = oper.cst.executeQuery();
		} catch (Exception e) {
			logger.info(e.toString());
		} finally {
			oper.close();
		}
	}

	/**
	 * 根据任务ID与用户ID更新任务状态
	 * @param pcid
	 * @param postmanid
	 */
	public void updatePostcontentStaByPcidAndUid(int pcid, int postmanid) {
		Ebean.getServer(GlobalDBControl.getDB())
				.createSqlUpdate("update postcontent_user set sta =1 where pcid = :pcid and uid=:uid")
				.setParameter("pcid", pcid).setParameter("uid", postmanid).execute();

	}
	
	/**
	 * 根据新闻ID获取新闻详情
	 * @param pcid
	 * @return
	 */
	public PostcontentDetail findPostcontentDetailByPcid(int pcid) {
		List<PostcontentDetail> postcontentDetailList = Ebean.getServer(GlobalDBControl.getDB()).find(PostcontentDetail.class).where().eq("pcid", pcid).findList();
		if(postcontentDetailList.isEmpty()){
			return null;
		}else{
			return postcontentDetailList.get(0);
		}
		
	}

	/**
	 * 
	 * <p>Title: saveOrUpdatePostcontentDetail</p> 
	 * <p>Description:  保存或更新新闻内容详情</p> 
	 * @param postcontent
	 * @param newscontentDetail
	 * @return
	 */
	public PostcontentDetail saveOrUpdatePostcontentDetail(
			Postcontent postcontent, String newscontentDetail) {
		PostcontentDetail postcontentDetail = findPostcontentDetailByPcid(postcontent.getId());
		newscontentDetail = dealNewscontentDetail(newscontentDetail);
		if(postcontentDetail==null){
			postcontentDetail = new PostcontentDetail();
			postcontentDetail.setPcid(postcontent.getId());
			postcontentDetail.setPccontent(newscontentDetail);
			postcontentDetail.setDateNew(new Date());
			Ebean.getServer(GlobalDBControl.getDB()).save(postcontentDetail);
		}else{
			postcontentDetail.setPccontent(newscontentDetail);
			Ebean.getServer(GlobalDBControl.getDB()).update(postcontentDetail);
		}
		return postcontentDetail;
	}

	/**
	 * 
	 * <p>Title: dealNewscontentDetail</p> 
	 * <p>Description: 处理新闻内容特殊字符</p> 
	 * @param newscontentDetail
	 * @return
	 */
	private String dealNewscontentDetail(String newscontentDetail) {
		logger.info("=========news============>>>>"+newscontentDetail);
		String replaceStr = "style=&quot;white-space: nowrap;&quot;";
		newscontentDetail = newscontentDetail.replace(replaceStr,"");
		replaceStr = "style=\"white-space: nowrap;\"";
		newscontentDetail = newscontentDetail.replace(replaceStr,"");
		logger.info("==========news o===========>>>>"+newscontentDetail);
		return newscontentDetail;
	}





	
}
