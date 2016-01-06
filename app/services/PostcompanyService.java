package services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;











import models.Postcompany;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.Logger.ALogger;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Expression;
import com.avaje.ebean.PagedList;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import services.ServiceFactory;
import utils.Constants;
import utils.GlobalDBControl;
import utils.JdbcOper;
import utils.Numbers;
import utils.StringUtil;

public class PostcompanyService {
	private static final PostcompanyService productService = ServiceFactory.getPostcompanyService();
	private static PostcompanyService instance = null;
	private static ALogger logger = Logger.of(PostcompanyService.class);

	public static PostcompanyService getInstance() {
		if (instance == null) {
			syncInit();
		}
		return instance;
	}

	private PostcompanyService() {

	}

	private static synchronized void syncInit() {
		if (instance == null) {
			instance = new PostcompanyService();
		}
	}

	/*public PagedList<Postcompany> findPostcompanyList(PostcompanyForm formPage) {
		List<String> sqlList = new ArrayList<>();
		List<Object> paramsList = new ArrayList<>();
		if (formPage.sta!=-1) {
			sqlList.add("sta = ?");
			paramsList.add(formPage.sta);
		}
		if (!Strings.isNullOrEmpty(formPage.nickname)) {
		    sqlList.add("nickname like ?");
		    paramsList.add("%"+formPage.nickname + "%");
		}

		Query<Postcompany> query = Ebean.getServer(GlobalDBControl.getDB()).createQuery(Postcompany.class);
		query.where(StringUtils.join(sqlList, " AND "));

		int i = 1; // first param uses index 1 NOT 0!
		for (Object param : paramsList) {
		    query.setParameter(i, param);
		    i++;
		}


		PagedList<Postcompany> xyzList = query.orderBy("id desc").findPagedList(formPage.page, formPage.size);
		
		return xyzList;
	}*/

	public Postcompany findPostcompanyById(Long id) {
		return Ebean.getServer(GlobalDBControl.getDB()).find(Postcompany.class).where().eq("id", id).findUnique();
	}


	public Postcompany update(Postcompany postcompany) {
		Ebean.getServer(GlobalDBControl.getDB()).update(postcompany);
		return postcompany;
	}

	/**
	 * 
	 * <p>Title: findAllPostcompany</p> 
	 * <p>Description: 查找出所有的快递公司</p> 
	 * @return
	 */
	public List<Postcompany> findAllPostcompany() {
		return Ebean.getServer(GlobalDBControl.getDB()).find(Postcompany.class).findList();
	}

}
