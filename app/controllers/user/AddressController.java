package controllers.user;

import java.util.Date;
import java.util.Map;

import models.user.UserAddress;
import models.wx.WxSign;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.baidu.BaiduService;
import services.user.UserAddressService;
import services.wx.WxService;
import utils.AjaxHelper;
import utils.Numbers;
import utils.StringUtil;

public class AddressController extends Controller{
	
	//地址初始化页面
	public Result address(){
		String flag=AjaxHelper.getHttpParam(request(), "flag");
		flag=StringUtils.isBlank(flag)?"1":flag;
		String sid=AjaxHelper.getHttpParam(request(), "sid");
		String rid=AjaxHelper.getHttpParam(request(), "rid");
		WxSign sign=WxService.getInstance().getwxWebSign(request());
		return ok(views.html.user.address.render(flag,sid,rid,sign));
	}
	//地址保存
	public Result saveaddress(){
		ObjectNode result=Json.newObject();
		result.put("status", "0");
		result.put("msg","地址保存失败，请重试");
		UserAddress adr=new UserAddress();
		String flag=AjaxHelper.getHttpParamOfFormUrlEncoded(request(),"flag");
		flag=StringUtils.isBlank(flag)?"1":flag;
		String privince=AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "provin");
		String city=AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "city");
		String country=AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "country");
		String address=AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "address");
		String sid=AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "sid");
		String rid=AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "rid");
		if(!StringUtils.isBlank(address) && !StringUtils.isBlank(country)){
			adr.setUid(Numbers.parseInt(session("bbtuid"), 0));
			adr.setAddress(privince+city+country+address);
			adr.setDate_new(new Date());
			adr.setTyp(Numbers.parseInt(flag, 1));
			//计算经纬度
			Map<String,Double> rlmap=BaiduService.instance.getlnglatbyaddress(adr.getAddress(), privince);
			if(rlmap!=null && !rlmap.isEmpty()){
				try{
					adr.setLat(rlmap.get("x"));
					adr.setLongs(rlmap.get("y"));
				}catch(Exception e){}
			}
			adr=UserAddressService.instance.save(adr);

		}
		UserAddress sender=null;
		UserAddress receiver=null;
		if(flag.equals("1")){
			//sender=adr;
			sid=adr.getId().intValue()+"";
//			if(!StringUtils.isBlank(rid))
//				receiver=UserAddressService.instance.getAddressbyid(Numbers.parseLong(rid, 0L));
		}else{
//			receiver=adr;
//			if(!StringUtils.isBlank(sid))
//				sender=UserAddressService.instance.getAddressbyid(Numbers.parseLong(sid, 0L));
			rid=adr.getId().intValue()+"";
		}
		//return ok(result);
		//return ok(views.html.user.preorder.render(sender,receiver));
		return redirect("/user/preorder?flag="+flag+"&sid="+sid+"&rid="+rid);
	}	
}