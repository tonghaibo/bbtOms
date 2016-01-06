package controllers.user;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import models.CountH5;
import models.user.UserInfo;
import models.wx.WxRequest;
import models.wx.WxSign;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonObject;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.ServiceFactory;
import services.baidu.BaiduService;
import services.cache.ICacheService;
import services.postmanuser.PostmanUserService;
import services.user.UserService;
import services.wx.WxService;
import utils.AjaxHelper;
import utils.Constants;
import utils.Numbers;
import utils.StringUtil;
import utils.wx.Sha1Util;

public class UserInfoController extends Controller{
	static ICacheService cache = ServiceFactory.getCacheService();
	WxService wxService=WxService.getInstance();
	UserService userService=UserService.instance;
	BaiduService baiduService=BaiduService.instance;
	PostmanUserService postmanService=PostmanUserService.getInstance();
	//客户端点做任意送页面
	public Result index(){
		//String unionid=AjaxHelper.getHttpParam(request(), "uid");
		
		
		if(!StringUtils.isBlank(session("bbtop"))){
			WxSign wxauth=WxService.getInstance().getwxWebSign(request());	
			return ok(views.html.user.index.render(wxauth,session("bbtop")));
		}

		String redirecturl =URLEncoder.encode(StringUtil
				.getDomainH5()+"/user/getwxpos");
		String wxauthURL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="
				+ Constants.WXappID
				+ "&redirect_uri="
				+ redirecturl
				+ "&response_type=code&scope=snsapi_base&state=123#wechat_redirect";
		return redirect(wxauthURL);
		}
	
	public Result getwxpos(){
		String code = AjaxHelper.getHttpParam(request(), "code");
		String state = AjaxHelper.getHttpParam(request(), "state");
		String flg=AjaxHelper.getHttpParam(request(), "flg");
		flg=StringUtils.isBlank(flg)?"":flg;
		String domain="http://m.ibbt.com";//"http://wx.neolix.cn";//;
		if("coupon".equals(flg))
			return redirect(domain+"/wechat/coupon/index?code="+code+"&state="+state);
		else if("mysender".equals(flg))
			return redirect(domain+"/wechat/receivers/list?code="+code+"&state="+state);
		else if("myposter".equals(flg))
			return redirect(domain+"/wechat/senders/list?code="+code+"&state="+state);
		else if("mybbt".equals(flg))
			return redirect(domain+"/wechat/order/list?code="+code+"&state="+state);
		else if("bbtsearch".equals(flg))
			return redirect(domain+"/wechat/order/search?code="+code+"&state="+state);
		else if("bbtindex".equals(flg))
			return redirect(domain+"/wechat/order/index?code="+code+"&state="+state);
		WxSign wxauth = null;
			//String orderurl = StringUtil.getDomainH5() + "/test/getpos?" + code+ "&state=" + state;
			JsonNode res = wxService.getwxtoken(code,state);
			if (res != null) {
				wxauth=new WxSign();
				wxauth.setAccess_token(res.get("access_token").textValue());
				wxauth.setOpenid(res.get("openid").textValue());
				Logger.info(wxauth.getOpenid());
				wxauth.setUnionid(res.get("unionid").textValue());
				wxauth.setCode(res.get("code").textValue());
				wxauth.setState(res.get("state").textValue());
				wxauth.setTimstr(res.get("timstr").textValue());
				wxauth.setNostr(res.get("nostr").textValue());
			}
			
			String wxauthstr ="";
			if(wxauth!=null){
				
				session("bbtop",wxauth.getOpenid());
				session("bbtun",wxauth.getUnionid());
				cache.setObject("wxauth"+wxauth.getOpenid(), wxauthstr, 7200);
				//更新用户oｐｎｅｉｄ，ｕｎｉｏｎｉｄ
				//userService.updateunionid(Numbers.parseInt(uid, 0),wxauth.getUnionid());
				UserInfo user=userService.getUserByopenid(wxauth.getOpenid());
				if(user==null || user.getOpenid()==null || StringUtils.isBlank(user.getOpenid())){
					//用户不存在自动注册
					user=new UserInfo();
					user.setDate_new(new Date());
					user.setOpenid(wxauth.getOpenid());
					user.setTyp(1);
					user.setNickName("个人");
					userService.addUser(user);
					user=userService.getUserByopenid(wxauth.getOpenid());
				}
				session("bbtuid",user.getUid().intValue()+"");
			}
			//WxSign sign=WxService.getInstance().getwxWebSign(request());
			//return ok(views.html.user.index.render(wxauth,Numbers.parseInt(session("bbtuid"), 0)));
			
			if(flg.equals("list"))
				return redirect("/user/orderlist");

			return redirect("/user/index");
	}
	

	//获取3公里内快递员数量
	public Result getpostmancount(){
		ObjectNode result=Json.newObject();
		Integer count=0;
		Double lat=Numbers.parseDouble(AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "lat"), 0D);
		Double lng=Numbers.parseDouble(AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "lng"), 0D);
//		if(lat.doubleValue()>0 && lng.doubleValue()>0){
//			Map<String,Double> latlng=new HashMap<String,Double>();
//			latlng.put("x", lat);
//			latlng.put("y", lng);
//			count = postmanService.postmancount(3000L, latlng);
//		}
		count=postmanService.getPostMancount(lat, lng, 0.03);
		result.put("count", count);
		return ok(result);
	}
	//修改用户经纬度
	public Result editlatlng(){
		Integer uid=0;//Numbers.parseInt(AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "uid"),0);
		if(!StringUtils.isBlank(session("bbtop"))){
			UserInfo user=UserService.instance.getUserByopenid(session("bbtop"));
			if(user!=null)
				uid=user.getUid();
		}
		Double lat=Numbers.parseDouble(AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "lat"), 0D);
		Double lng=Numbers.parseDouble(AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "lng"), 0D);
		if(uid>0 && lat>0 && lng>0){
			//转换成百度经纬度
			Map<String,Double> llmap=BaiduService.instance.changelonglat(lng, lat);
			lat=llmap.get("x");
			lng=llmap.get("y");
			userService.updatelatlng(uid, lat.doubleValue()+"", lng.doubleValue()+"");
		}
		ObjectNode re=Json.newObject();
		re.put("status", "1");
		return ok(re);
	}
	
	// 访问统计添加页面
	public Result countjs() {
		response().setContentType("application/json;charset=utf-8");
		ObjectNode result = Json.newObject();
		String ip = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "ips");
		String url = AjaxHelper
				.getHttpParamOfFormUrlEncoded(request(), "curl");
		String sharetype = AjaxHelper.getHttpParamOfFormUrlEncoded(request(),
				"sharetype");
		String iswx = AjaxHelper.getHttpParamOfFormUrlEncoded(request(),
				"iswx");
		String channel = AjaxHelper.getHttpParamOfFormUrlEncoded(request(),
				"channel");
		String uid = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "uid");
		String openid=AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "openid");

		if (!StringUtils.isBlank(url) && !StringUtils.isBlank(channel)) {
			CountH5 cnt = new CountH5();
			cnt.setChannel(channel);
			cnt.setIp(StringUtils.isBlank(ip) ? request().remoteAddress() : ip);
			cnt.setIswx(iswx);
			cnt.setShareType(sharetype);
			cnt.setUrl(url);
			cnt.setOpenid(openid);
			cnt.setCreateTime(new Date());
			cnt.setUserId(Numbers.parseLong(uid, 0L));
			UserService.instance.saveCount(cnt);
		}
		result.put("status", "1");
		return ok(Json.toJson(result));
	}
	public static WxSign getcacheWxsign() {
		String adt = cache.get("wxauth" + session("op"));
		WxSign wxauth = new WxSign();
		if (!StringUtils.isBlank(adt)) {			
			String[] tmp = adt.split(",");
			wxauth.setOpenid(tmp[0]);
			wxauth.setUnionid(tmp[1]);
			wxauth.setAccess_token(tmp[2]);
			wxauth.setTimstr(tmp[3]);
			wxauth.setNostr(tmp[4]);
			wxauth.setCode(tmp[5]);
			wxauth.setState(tmp[6]);
			wxauth.setAppId(Constants.WXappID);
			return wxauth;
		}else{
			wxauth.setAccess_token("");
			wxauth.setAppId(Constants.WXappID);
			wxauth.setOpenid("");
			wxauth.setUnionid("");
		}
		return wxauth;
	}
}