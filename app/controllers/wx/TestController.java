package controllers.wx;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.user.UserAddress;
import models.wx.WxSign;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;

import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import services.baidu.BaiduService;
import services.wx.WxService;
import utils.AjaxHelper;
import utils.Constants;
import utils.StringUtil;

public class TestController extends Controller {
	private final WxService wxService=WxService.getInstance();
	private BaiduService bdservice=BaiduService.instance;
	public Result getpos(){
		String code = AjaxHelper.getHttpParam(request(), "code");
		String state = AjaxHelper.getHttpParam(request(), "state");

		WxSign addrSign = null;
			String orderurl = StringUtil.getDomainH5() + "/test/getpos?" + code+ "&state=" + state;
			JsonNode res = wxService.getwxtoken(code,state);
			if (res != null) {
				addrSign=new WxSign();
				addrSign.setAccess_token(res.get("access_token").textValue());
				addrSign.setOpenid(res.get("openid").textValue());
				addrSign.setUnionid(res.get("unionid").textValue());
				addrSign.setCode(res.get("code").textValue());
				addrSign.setState(res.get("state").textValue());
				addrSign.setTimstr(res.get("timstr").textValue());
				addrSign.setNostr(res.get("nostr").textValue());
			}
			
			String addrSignstr ="";
			if(addrSign!=null){
				addrSignstr=StringUtil.getWXaddressSign(
					addrSign.getAccess_token(), orderurl, addrSign.getTimstr(),
					addrSign.getNostr());
				addrSign.setSign(addrSignstr);
			}
			if(addrSign==null){
				addrSign=new WxSign();
				addrSign.setAppId(Constants.WXappID);
				addrSign.setNostr("");
				addrSign.setTimstr("");
				addrSign.setOpenid("");
				addrSign.setUnionid("");
				addrSign.setSign("");
			}
			return ok(views.html.address.getwxpos.render(addrSign));
		}
	
	public Result pos(){
		Logger.info(request().remoteAddress());
		//经伟度调用
//		List<Double> longlat=BaiduService.instance.changelonglat(114.21892734521D, 29.575429778924D);
//		if(longlat!=null && !longlat.isEmpty())
//			Logger.info(longlat.get(0)+","+longlat.get(1));
		
		//按名称查询经伟度
		//UserAddress ad=BaiduService.instance.getlonglatByPlace("浙江 台州 临海市 大田", "天津");
		
		//根据地址查经纬度
		//Map<String,Double> map=BaiduService.instance.getlnglatbyaddress("广开南马路今晚报大厦", "天津");

		//查两点间距离
//		Map<String,Double> begin=new HashMap<String,Double>();
//		Map<String,String> end=new HashMap<String,String>();
//		begin.put("x", "40.056878");
//		begin.put("y", "116.30815");
//		
//		end.put("x", "39.915285");
//		end.put("y", "116.403857");
//		Long dis=BaiduService.instance.getdistanceByPoints(begin, end, "北京");
		
		//查多点间距离
		Map<String,String> bg=new HashMap<String,String>();
		bg.put("xy1", "40.056878,116.30815");
//		bg.put("xy2", "40.056878,116.30815");
		Map<String,String> ed=new HashMap<String,String>();
		ed.put("lngcat1", "39.915285,116.403857");
		ed.put("lngcat2", "48.056878,116.403857");
		Map<String,Long> dis=BaiduService.instance.getdisPointmore(bg, ed, "北京");
		
		return ok("123");
		//return ok(views.html.address.pos.render());
	}
}
