package services.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.order.PostOrder;
import models.postman.PostmanUser;

import models.postman.PostmanUserTemp;
import org.apache.commons.lang3.StringUtils;

import play.Logger;
import services.baidu.BaiduService;
import services.postmanuser.PostmanUserService;
import utils.Constants;
import utils.GlobalDBControl;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.PagedList;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.google.common.base.Strings;

import form.PostOrderForm;

public class PostOrderService{
	private final Logger.ALogger logger = Logger.of(PostOrderService.class);
	public static PostOrderService instance=new PostOrderService();
	
	private PostOrderService(){}
	
	//添加订单
	public void saveorder(PostOrder order){
		Ebean.getServer(GlobalDBControl.getDB()).save(order);
	}
	
	//修改订单状态
	public void updateStatus(Integer orderid,Integer status){
		String sql="update post_order set status="+status+" where id="+orderid;
		Ebean.getServer(GlobalDBControl.getDB()).createSqlUpdate(sql).execute();
	}
	
	//获取用户订单列表
	public List<PostOrder> getorderlist(Integer uid){
//		String sql="select id,uid,ordercode,userlong,userlat,username,phone,receivelong,"
//				+ "receivelat,receivename,receivephone,receiveaddress,subjectname,"
//				+ "subjecttyp,subjectremark,goodsfee,freight,distance,weight,paytyp"
//				+ ",totalfee,award,status,gettyp,gettime,realgettime,overtime, date_new"
//				+ ",date_upd,islooked,ordertyp,remark,reasonid from post_order where uid="+uid+" and status>-1";
//		RawSql sqlr=RawSqlBuilder.parse(sql).create();
//		return Ebean.getServer(GlobalDBControl.getDB()).find(PostOrder.class)
//				.setRawSql(sqlr).findList();
		return Ebean.getServer(GlobalDBControl.getDB()).find(PostOrder.class)
				.where().eq("uid", uid).orderBy("id desc").findList();
	}

	//按各类条件查询订单列表
	public PagedList<PostOrder> findPostOrderList(PostOrderForm formPage) {
		List<String> sqlList = new ArrayList<>();
		List<Object> paramsList = new ArrayList<>();
		if (!"-1".equals(formPage.ordertyp)) {
			sqlList.add("ordertyp = ?");
			paramsList.add(formPage.ordertyp);
		}
		if (formPage.status!=-1) {
			sqlList.add("status = ?");
			paramsList.add(formPage.status);
		}
		if (!Strings.isNullOrEmpty(formPage.ordercode)) {
		    sqlList.add("ordercode = ?");
		    paramsList.add(formPage.ordercode);
		}
		if (!Strings.isNullOrEmpty(formPage.keyword)) {
			sqlList.add(" (username = ? or receivename = ? or phone=? or receivephone=?) ");
			paramsList.add(formPage.keyword);
			paramsList.add(formPage.keyword);
			paramsList.add(formPage.keyword);
			paramsList.add(formPage.keyword);
		}
		if (formPage.between!=null) {
			sqlList.add("date_new between ? and ?");
			paramsList.add(formPage.between.start);
			paramsList.add(formPage.between.end);
		}
		
		Query<PostOrder> query = Ebean.getServer(GlobalDBControl.getDB()).createQuery(PostOrder.class);
		query.where(StringUtils.join(sqlList, " AND "));

		int i = 1; // first param uses index 1 NOT 0!
		for (Object param : paramsList) {
		    query.setParameter(i, param);
		    i++;
		}
		PagedList<PostOrder> xyzList = query.orderBy("id desc").findPagedList(formPage.page, formPage.size);
		return xyzList;
	}

	public PostOrder findPostOrderById(Integer id) {
		return Ebean.getServer(GlobalDBControl.getDB()).find(PostOrder.class).where().eq("id", id).findUnique();
	}

	//根据状态查询订单列表
	public List<PostOrder> getlistBystatus(Integer status){
		return Ebean.getServer(GlobalDBControl.getDB()).find(PostOrder.class)
				.where().eq("status", status).findList();
	}
		
	/*
	 * 计算收单距离并做push推送
	 * count  push数量
	 * distance　小于该距离内ｐｕｓｈ
	 */
	public void sendpostpush(Integer orderid){
		PostOrder order=PostOrderService.instance.findPostOrderById(orderid);
		if(order!=null && order.getStatus()==1){
			List<PostmanUser> pulist=PostmanUserService.getInstance().getPostManlist( order.getUserlat()==null?0.0:order.getUserlat(),order.getUserlong()==null?0.0:order.getUserlong(), Constants.defalutMeter,orderid);
			if(pulist!=null && !pulist.isEmpty()){
				pushPostOrderToPostmanList(pulist,order);
			}
		}
	}

	/**
	 * 进行推送
	 * @param pulist
	 * @param order
     */
	public void pushPostOrderToPostmanList(List<PostmanUser> pulist, PostOrder order) {
		new Thread(new Runpushtimer(pulist,order)).start();//
		try {
			Thread.sleep(3000);//休息三秒
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * <p>Title: save</p> 
	 * <p>Description: 保存版本信息</p> 
	 * @param postOrder
	 */
	public PostOrder save(PostOrder postOrder) {
		Ebean.getServer(GlobalDBControl.getDB()).save(postOrder);
		return postOrder;
	}

	public PostOrder update(PostOrder postOrder) {
		Ebean.getServer(GlobalDBControl.getDB()).update(postOrder);
		return postOrder;
	}

	public void delPostOrder(Integer id) {
		PostOrder postOrder = findPostOrderById(id);
		Ebean.getServer(GlobalDBControl.getDB()).delete(postOrder);
	}
	//向同城快递员推送订单
	public class Runpushtimer implements Runnable {
		private List<PostmanUser> puserlist;
		private PostOrder order;

		public Runpushtimer(List<PostmanUser> puserlist, PostOrder order) {
			this.order = order;
			this.puserlist = puserlist;
		}
		public void run() {
			for (PostmanUser postmanUser : puserlist) {
				PostmanUserTemp po=new PostmanUserTemp();
				po.setOrderid(order.getId());
				po.setPostmanid(postmanUser.getId());
				PostmanUserService.getInstance().savetemp(po);
			}
		}
	}
}
