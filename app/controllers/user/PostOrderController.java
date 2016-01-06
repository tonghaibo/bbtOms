package controllers.user;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.PushInfo;
import models.order.PostOrder;
import models.order.PostOrderUser;
import models.postman.PostmanUser;
import models.postman.PostmanUserTemp;
import models.user.UserAddress;
import models.wx.WxRequest;
import models.wx.WxSign;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.ServiceFactory;
import services.baidu.BaiduService;
import services.cache.ICacheService;
import services.order.PostOrderService;
import services.postmanuser.PostmanUserService;
import services.user.UserAddressService;
import services.user.UserService;
import services.wx.WxService;
import utils.AjaxHelper;
import utils.Constants;
import utils.Numbers;
import utils.StringUtil;
import utils.wx.Sha1Util;

public class PostOrderController extends Controller{
	private static final SimpleDateFormat CHINESE_D_TIME_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	ICacheService cache = ServiceFactory.getCacheService();
	WxService wxService=WxService.getInstance();
	UserService userService=UserService.instance;

	//初始化订单页面
	public Result preorder(){
		String sid=AjaxHelper.getHttpParam(request(), "sid");
		String rid=AjaxHelper.getHttpParam(request(), "rid");
		
		UserAddress sender=null;
		UserAddress receiver=null;
		if(!StringUtils.isBlank(sid)){
			sender=UserAddressService.instance.getAddressbyid(Numbers.parseLong(sid, 0L));
		}else{
			//取最后一条发件地址
			sender=UserAddressService.instance.getlastaddress(Numbers.parseInt(session("bbtuid"), 0), 1);
		}
		if(!StringUtils.isBlank(rid)){
			receiver=UserAddressService.instance.getAddressbyid(Numbers.parseLong(rid, 0L));
		}else{
			//取最后一条发件地址
			receiver=UserAddressService.instance.getlastaddress(Numbers.parseInt(session("uid"), 0), 2);
		}
		WxSign sign=WxService.getInstance().getwxWebSign(request());
		return ok(views.html.user.preorder.render(sender,receiver,sign,session("bbtop")==null?"":session("bbtop")));
	}
	//保存订单
	public Result saveorder(){
		ObjectNode result=Json.newObject();
		result.put("status", "0");
		result.put("orderid", 0);
		result.put("msg","订单生成失败，请重试");
		String sid=AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "sid");
		String rid=AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "rid");
		Integer weight=Numbers.parseInt(AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "count"), 0);
		String taketim=AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "taketim");
		

		String sendername=AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "sendername");
		String sendphone=AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "senderphone");
		String receivername=AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "receivername");
		String receiverphone=AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "receiverphone");
		String subjectname=AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "subjectname");
		String remark=AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "remark");
		
		UserAddress sender=UserAddressService.instance.getAddressbyid(Numbers.parseLong(sid, 0L));
		UserAddress recv=UserAddressService.instance.getAddressbyid(Numbers.parseLong(rid, 0L));
		if(weight<1)
			weight=5;
		
		if(StringUtils.isBlank(sid) || StringUtils.isBlank(rid) ||sender==null || recv==null){
			return null;
		}
		PostOrder order=new PostOrder();
		if(StringUtils.isBlank(taketim))
			taketim=(new Date()).getHours()+"";
		Date date=new Date();//取时间 
		  Calendar calendar=new GregorianCalendar(); 
		  calendar.setTime(date); 
		if(taketim.indexOf("t")>=0){			  
			  calendar.add(calendar.DATE,1);
			  date=calendar.getTime(); 
			  taketim=taketim.replace("t", "");
			  if(taketim.length()<2)
				  taketim="0"+taketim;
			  taketim=calendar.getWeekYear()+"-"+date.getMonth()+"-"+date.getDay()+" "+taketim+":00:00";
			  order.setGettyp("1");
				try{
					order.setGettime(CHINESE_D_TIME_FORMAT.parse(taketim));
				}catch(Exception e){
					order.setGettime(new Date());
				}
		}else{
			order.setGettyp("2");
			 if(taketim.length()<2)
				  taketim="0"+taketim;
			 order.setGettime(new Date());
		}
		order.setUid(Numbers.parseInt(session("bbtuid"), 0));
		order.setAddress(sender.getAddress());
		order.setUserlat(sender.getLat());
		order.setUserlong(sender.getLongs());
		order.setReceiveaddress(recv.getAddress());
		order.setReceivelat(recv.getLat());
		order.setReceivelong(recv.getLongs());
		order.setWeight(weight*1000);
		order.setUsername(sendername);
		order.setPhone(sendphone);
		order.setReceivename(receivername);
		order.setReceivephone(receiverphone);
		order.setSubjectname(subjectname);
		order.setSubjectremark(remark);
		order.setDate_upd(new Date());
		order.setStatus(0);
		
		//计算两点距离
		Map<String,Double> begin=new HashMap<String,Double>();
		begin.put("x", sender.getLat());
		begin.put("y", sender.getLongs());
		Map<String,Double> end=new HashMap<String,Double>();
		end.put("x", recv.getLat());
		end.put("y",recv.getLongs());
		Long distance=BaiduService.instance.getdistanceByPoints(begin, end,"河北");
		order.setDistance(distance);
		order.setTotalfee(1);
		order.setDate_new(new Date());
		order.setDate_upd(order.getDate_new());
		order.setOrdertyp("3");
		order=PostOrderService.instance.save(order);

		session("bbtorderid",order.getId().intValue()+"");
		//return redirect("/user/");
		//return ok(views.html.user.nextorder.render(order));
		if(order!=null){
			result.put("status", "1");
			result.put("orderid", order.getId());
		}else{
			result.put("orderid", 0);
		}
		return ok(result);
	}

	//保存订单第二步
	public Result nextorder(){
		WxSign sign=UserInfoController.getcacheWxsign();
		@SuppressWarnings("static-access")
		WxSign signshare=WxService.getInstance().getwxWebSign(request());
		return ok(views.html.user.nextorder.render(sign,signshare,session("bbtop")==null?"":session("bbtop")));
	}
	//计算两点距离
	public Result getdistance(){
		//计算两点距离
		String sid=AjaxHelper.getHttpParam(request(), "sid");
		String rid=AjaxHelper.getHttpParam(request(), "rid");
		UserAddress sender=UserAddressService.instance.getAddressbyid(Numbers.parseLong(sid, 0L));
		UserAddress recv=UserAddressService.instance.getAddressbyid(Numbers.parseLong(rid, 0L));

		Map<String,Double> begin=new HashMap<String,Double>();
		begin.put("x", sender.getLat());
		begin.put("y", sender.getLongs());
		Map<String,Double> end=new HashMap<String,Double>();
		end.put("x", recv.getLat());
		end.put("y",recv.getLongs());
		Long distance=BaiduService.instance.getdistanceByPoints(begin, end,"河北");
		ObjectNode re=Json.newObject();
		re.put("d", Double.valueOf(distance.longValue())/1000);
		return ok(re);
	}

	//订单列表
	public Result orderlist(){
		
		List<PostOrder> orderlist=PostOrderService.instance.getorderlist(Numbers.parseInt(session("bbtuid"), 0));
		if(orderlist!=null){
			for(PostOrder order:orderlist){
				if(order.getStatus()>3){
					PostmanUser pu=PostmanUserService.getInstance().getpostmanbyorder(order.getId());
					if(pu!=null)
						order.setPostman(pu);					
				}
			}
		}
		WxSign sign=WxService.getInstance().getwxWebSign(request());
		return ok(views.html.user.orderlist.render(orderlist,sign,session("bbtop")==null?"":session("bbtop")));
	}
	//订单详情
	public Result orderdetail(){
		Integer orderid=Numbers.parseInt(AjaxHelper.getHttpParam(request(), "orderid"), 0);
		PostOrder order=PostOrderService.instance.findPostOrderById(orderid);
		PostmanUser postman=PostmanUserService.getInstance().getpostmanbyorder(orderid);
		WxSign sign=WxService.getInstance().getwxWebSign(request());
		return ok(views.html.user.orderdetail.render(order,postman,sign,session("bbtop")==null?"":session("bbtop")));
	}
	
	//微信支付成功完成订单状态页面
	public Result ordersuccess(){
		Integer orderid=Numbers.parseInt(session("bbtorderid"), 0);//Numbers.parseInt(AjaxHelper.getHttpParam(request(), "orderid"), 0);
		String timstr = AjaxHelper.getHttpParam(request(), "timstr");
		String nostr = AjaxHelper.getHttpParam(request(), "nostr");
		String sign = AjaxHelper.getHttpParam(request(), "sign");
		String prepayid = AjaxHelper.getHttpParam(request(), "payid");
		if(orderid==null || StringUtils.isBlank(sign) || StringUtils.isBlank(prepayid))
			return null;
		
		//修改订单状态
		PostOrderService.instance.updateStatus(orderid, Constants.CityWideDeliverStas.WaitToCatch.getStatus());
		/**********支付完成完成以下步骤*****************/
		//向待推送快递员列表里加入快递员数据
		PostOrder order=PostOrderService.instance.findPostOrderById(orderid);
		List<PostmanUser> pulist=PostmanUserService.getInstance().getPostManlist(order.getReceivelat(), order.getReceivelong(), Constants.defalutMeter,orderid);
		PostOrderService.instance.pushPostOrderToPostmanList(pulist,order);
		/**********支付完成完成以上步骤*****************/
		return redirect("/user/orderdetail?orderid="+orderid);
	}
	//取消订单
	public Result cancelorder(){
		ObjectNode result=Json.newObject();
		Integer orderid=Numbers.parseInt(AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "orderid"), 0);
		if(orderid!=null && orderid.intValue()>0){
			PostOrderService.instance.updateStatus(orderid, Constants.CityWideDeliverStas.SuccessWithProblom.getStatus());
		}
		result.put("status", "1");
		result.put("msg", "取消成功");
		return ok(result);
	}
	//微信支付
	public Result orderwxpay(){
		String orderid=AjaxHelper.getHttpParam(request(), "orderid");
		Integer totalfee=0;//Numbers.parseDouble(AjaxHelper.getHttpParamOfFormUrlEncoded(request(),"totalfee"), 0D);
		PostOrder order=PostOrderService.instance.findPostOrderById(Numbers.parseInt(orderid, 0));
		if(order==null)
			return null;
		totalfee=order.getTotalfee();
		String openid=session("bbtop");
		String goods_tag="";		
		WxRequest rq=WxService.getInstance().getPrepayId(orderid, totalfee, openid, goods_tag);
		String prepayid="";
		String nostr="";
		String sign="";
		if(rq!=null){
			prepayid=rq.getPrepayid();
			nostr=rq.getNonce_str();
			sign=rq.getSign();
		}
		if (StringUtils.isBlank(prepayid)) {
			// return ok(views.html.sheSaid.pageError.render());
			return redirect("/user/orderdetail?orderid=" + orderid);
		} else {

			return redirect("/user/wxpayh5?nostr=" + nostr + "&sign=" + sign
					+ "&orderid=" + orderid + "&payid=" + prepayid);
		}
	}
	
	//微信支付页面
	public Result wxpayh5(){
		String nostr = AjaxHelper.getHttpParam(request(), "nostr");
		String sign = AjaxHelper.getHttpParam(request(), "sign");
		String orderid = AjaxHelper.getHttpParam(request(), "orderid");
		String payid = AjaxHelper.getHttpParam(request(), "payid");
		Map<String, String> pramt = new HashMap<String, String>();
		nostr = RandomStringUtils.randomAlphanumeric(32);
		String timstr = Sha1Util.getTimeStamp();
		pramt.put("appId", Constants.WXappID);
		pramt.put("timeStamp", timstr);
		pramt.put("nonceStr", nostr);
		pramt.put("package", "prepay_id=" + payid);
		pramt.put("signType", "MD5");
		sign = StringUtil.getSign(pramt);
		
		WxSign sharesign=WxService.getInstance().getwxWebSign(request());
		return ok(views.html.user.wxpayh5.render(Constants.WXappID, timstr,
				nostr, payid, sign, orderid,sharesign));
	}
	
	//取快递员电话
	public Result getpostmanphone(){
		Integer orderid=Numbers.parseInt(AjaxHelper.getHttpParam(request(), "orderid"),0);
		PostmanUser post=PostmanUserService.getInstance().getpostmanbyorder(orderid);
		ObjectNode re=Json.newObject();
		re.put("phone", "");
		if(post!=null)
			re.put("phone", post.getPhone());
		
		return ok(re);
	}
}