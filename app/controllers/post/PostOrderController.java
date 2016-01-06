package controllers.post;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import models.admin.AdminUser;
import models.order.PostOrder;
import models.user.UserAddress;
import play.Configuration;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.Security.Authenticated;
import play.twirl.api.Html;
import services.ServiceFactory;
import services.baidu.BaiduService;
import services.cache.ICacheService;
import services.order.PostOrderService;
import services.user.UserAddressService;
import utils.AjaxHelper;
import utils.Constants;
import utils.Dates;
import utils.Numbers;
import utils.OSSUtils;
import utils.StringUtil;

import com.avaje.ebean.PagedList;
import com.google.common.base.Strings;

import controllers.admin.AdminAuthenticated;
import controllers.admin.BaseAdminController;
import form.DateBetween;
import form.PostOrderForm;


/**
 * 
 * <p>Title: PostOrderController.java</p> 
 * <p>Description: 地址管理</p> 
 * <p>Company: higegou</p> 
 * @author  ctt
 * date  2015年12月22日  下午4:47:13
 * @version
 */
public class PostOrderController extends BaseAdminController {
	public static final Logger.ALogger logger = Logger.of(PostOrderController.class);
	private BaiduService bdservice=BaiduService.instance;
	ICacheService cache = ServiceFactory.getCacheService();
	private static final PostOrderService postOrderService = PostOrderService.instance; 
	private static final UserAddressService userAddressService = UserAddressService.instance; 
	
	private final Form<PostOrderForm> postOrderForm = Form.form(PostOrderForm.class);
	/**
	 * 
	 * <p>Title: list</p> 
	 * <p>Description: App升级信息列表</p> 
	 * @return
	 */
	public Result postOrderManage(){
		Form<PostOrderForm> form = postOrderForm.bindFromRequest();
		PostOrderForm formPage = new PostOrderForm();
		logger.info("log page1:"+form.toString());
        if (!form.hasErrors()) {
        	formPage = form.get();
        }
        String start ="";
		String end ="";
		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		if(formPage.between==null){
			start = DATE_FORMAT.format(new Date());
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.DAY_OF_MONTH, 1);
			end = DATE_FORMAT.format(cal.getTime());
			formPage.between = new DateBetween();
			formPage.between.start=new Date();
			formPage.between.end=cal.getTime();
		}
		PagedList<PostOrder> postOrderPage = postOrderService.findPostOrderList(formPage);
		Html ordertyps = Html.apply(Constants.OrderTyps.typs2HTML(Numbers.parseInt(formPage.ordertyp, -1)));
		Html statuses = Html.apply(Constants.CityWideDeliverStas.stas2HTML(formPage.status));
		return ok(views.html.post.postOrderManage.render(postOrderPage,ordertyps,statuses,formPage));
	}
	
	/**
	 * 跳转至新增App升级添加页面
	 * @return
	 */
	@AdminAuthenticated
	public Result newOrUpdatePostOrder(){
		int id = Numbers.parseInt(Form.form().bindFromRequest().get("id"), 0);
		//获取该用户下所有的寄件人和收件人id集合
		PostOrder postOrder = new PostOrder();
		AdminUser user = getCurrentAdminUser();
		//获取该用户下的收件人集合
		List<UserAddress> userAddressesList = userAddressService.queryAllByUidAndTyp(user.getId(),"1");
		List<UserAddress> receiveUserAddressesList = userAddressService.queryAllByUidAndTyp(user.getId(),"2");
		//展示收件人集合为html
		Html userAddressesHtml = Html.apply(userAddressService.userAddressList2Html(userAddressesList, "-1"));
		Html receiveUserAddressesHtml = Html.apply(userAddressService.userAddressList2Html(receiveUserAddressesList, "-1"));
		if(id!=0L){
			postOrder = postOrderService.findPostOrderById(id);
		}
		return ok(views.html.post.newOrUpdatePostOrder.render(id,postOrder,userAddressesHtml,receiveUserAddressesHtml));
    }
	
	/**
	 * 保存同城订单信息
	 * @return
	 */
	@AdminAuthenticated
	public Result savePostOrder(){
		int id = Numbers.parseInt(Form.form().bindFromRequest().get("id"), 0);
		Form<PostOrderForm> form = postOrderForm.bindFromRequest();
		PostOrderForm formPage = new PostOrderForm();
		logger.info("log page1:"+form.toString());
        if (!form.hasErrors()) {
        	formPage = form.get();
        	//根据寄件地址和收件地址查询相应的经纬度存入form
        	formPage = dealFormPage(formPage);
        }
		PostOrder postOrder = new PostOrder();
		AdminUser adminUser =getCurrentAdminUser();
		formPage.uid=adminUser.getId().intValue();
		if(id!=0L){
			postOrder = postOrderService.findPostOrderById(id);
			postOrder = dealPostOrder(postOrder,formPage);
			postOrder.setDate_upd(new Date());
			postOrder = postOrderService.update(postOrder);
		}else{
			postOrder = dealPostOrder(postOrder,formPage);
			postOrder.setStatus(1);//设置状态为待接单
			postOrder.setOrdertyp("2");//设置订单类型为渠道取件
			postOrder.setDate_new(new Date());
			postOrder.setDate_upd(new Date());
			String ordercode= "spxd"+Dates.formatDate(new Date(), "yyyyMMddhhmmss");
			ordercode += Dates.formatDate(new Date(), "hhmmss");
			postOrder.setOrdercode(ordercode);
			//保存
			postOrder = postOrderService.save(postOrder);
		}
		return redirect("/post/postOrderManage");
	}
	
	/**
	 * 
	 * <p>Title: dealFormPage</p> 
	 * <p>Description: 根据收发地址获取经纬度</p> 
	 * @param formPage
	 * @return
	 */
	private PostOrderForm dealFormPage(PostOrderForm formPage) {
		Map<String,Double> senderAddress = bdservice.getlnglatbyaddress(formPage.address, "北京");
		formPage.userlong=senderAddress.get("y");
		formPage.userlat=senderAddress.get("x");
		Map<String,Double> receiveAddress = bdservice.getlnglatbyaddress(formPage.receiveaddress, "北京");
		formPage.receivelong=receiveAddress.get("y");
		formPage.receivelat=receiveAddress.get("x");
		Long distance = bdservice.getdistanceByPoints(senderAddress, receiveAddress,"北京");
		formPage.distance=distance;
		return formPage;
	}

	/**
	 * 
	 * <p>Title: delPostOrder</p> 
	 * <p>Description: 删除相应的用户信息</p> 
	 * @return
	 */
	public Result delPostOrder(){
		int id = Numbers.parseInt(Form.form().bindFromRequest().get("id"), 0);
		if(id!=0L){
			postOrderService.delPostOrder(id);
		}
		return redirect("/post/postOrderManage");
	}
	/**
	 * 
	 * <p>Title: cancelPostOrder</p> 
	 * <p>Description: 取消相应的订单</p> 
	 * @return
	 */
	public Result cancelPostOrder(){
		int id = Numbers.parseInt(Form.form().bindFromRequest().get("id"), 0);
		if(id!=0L){
			PostOrder postOrder = postOrderService.findPostOrderById(id);
			postOrder.setStatus(5);
			postOrderService.saveorder(postOrder);
		}
		return redirect("/post/postOrderManage");
	}
	
	
	private PostOrder dealPostOrder(PostOrder postOrder, PostOrderForm form) {		
		postOrder.setUid(form.uid.intValue());
		postOrder.setUserlong(form.userlong);
		postOrder.setUserlat(form.userlat);
		postOrder.setUsername(form.username);
		postOrder.setPhone(form.phone);
		postOrder.setAddress(form.province+form.address);
		postOrder.setReceivelong(form.receivelong);
		postOrder.setReceivelat(form.receivelat);
		postOrder.setReceivename(form.receivename);
		postOrder.setReceivephone(form.receivephone);
		postOrder.setReceiveaddress(form.provincereceive+form.receiveaddress);
		postOrder.setSubjectname(form.subjectname);
		postOrder.setSubjecttyp(form.subjecttyp);
		postOrder.setSubjectremark(form.subjectremark);
		postOrder.setDistance(form.distance);
		postOrder.setWeight(form.weight);
		postOrder.setPaytyp(form.paytyp);
		postOrder.setTotalfee(form.totalfee);
		postOrder.setAward(form.award);
		postOrder.setStatus(form.status);
		postOrder.setGettime(form.gettime);
		postOrder.setRealgettime(form.realgettime);
		postOrder.setOvertime(form.overtime);
		postOrder.setGettyp(form.gettyp);
		return postOrder;
	}
	
}
