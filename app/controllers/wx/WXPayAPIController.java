package controllers.wx;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.wx.WXPayService;
import services.wx.WxService;
import utils.AjaxHelper;
import utils.Constants;
import utils.Numbers;
import utils.StringUtil;

public class WXPayAPIController extends Controller {
	private static final Logger.ALogger logger = Logger.of(WxMenuController.class);
	private static final WxService wxService = WxService.getInstance();
	/**
	 * {
    "retcode": "-2",
    "retmsg": "错误：获取prepayId失败"
}
	 * @return
	 */
	public Result getSignAndPrepayID() {
		response().setContentType("application/json;charset=utf-8");
		JsonNode reslut = WXPayService.getInstance().getSignAndPrepayID("234234234",  1,"127.0.0.1");
		return ok(Json.toJson(reslut));
	}
	
	//wxprepay.php
	public Result wxprepay() {
		response().setContentType("application/json;charset=utf-8");
		ObjectNode result = Json.newObject();

		Map<String, String> pramt = new HashMap<String, String>();
		String appversion = AjaxHelper.getHttpParam(request(), "appversion");
		if(StringUtils.isBlank(appversion)){
			appversion = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "appversion");
//			appversion = Form.form().bindFromRequest().get("appversion");
		}
		if(appversion!=null){
			pramt.put("appversion",appversion);
		}
		
		String wdhjy = AjaxHelper.getHttpParam(request(), "wdhjy");
		if(StringUtils.isBlank(wdhjy)){
			wdhjy = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "wdhjy");
		}
		if(wdhjy!=null){
			pramt.put("wdhjy",wdhjy);
		}
		
		String osversion = AjaxHelper.getHttpParam(request(), "osversion");
		if(StringUtils.isBlank(osversion)){
			osversion = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "osversion");
		}
		if(osversion!=null){
			pramt.put("osversion",osversion);
		}
		
		String model = AjaxHelper.getHttpParam(request(), "model");
		if(StringUtils.isBlank(model)){
			model = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "model");
		}
		if(model!=null){
			pramt.put("model",model);
		}
		
		String usewallet = AjaxHelper.getHttpParam(request(), "usewallet");
		if(StringUtils.isBlank(usewallet)){
			usewallet = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "usewallet");
		}
		if(usewallet!=null){
			pramt.put("usewallet",usewallet);
		}
		
		
		String deviceType = AjaxHelper.getHttpParam(request(), "deviceType");
		if(StringUtils.isBlank(deviceType)){
			deviceType = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "deviceType");
		}
		if(deviceType!=null){
			pramt.put("deviceType",deviceType);
		}
		
		String devid = AjaxHelper.getHttpParam(request(), "devid");
		if(StringUtils.isBlank(devid)){
			devid = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "devid");
			//devid = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "devid");
		}
		if(devid!=null){
			pramt.put("devid",devid);
		}
		
		String idfa = AjaxHelper.getHttpParam(request(), "idfa");
		if(StringUtils.isBlank(idfa)){
			idfa = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "idfa");
		}
		if(idfa!=null){
			pramt.put("idfa",idfa);
		}
		
		String marketCode = AjaxHelper.getHttpParam(request(), "marketCode");
		if(StringUtils.isBlank(marketCode)){
			marketCode = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "marketCode");
		}
		if(marketCode!=null){
			pramt.put("marketCode",marketCode);
		}
		
		String uid = AjaxHelper.getHttpParam(request(), "uid");
		if(StringUtils.isBlank(uid)){
			uid = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "uid");
		}
		if(uid!=null){
			pramt.put("uid",uid);
		}
		String orderCode = AjaxHelper.getHttpParam(request(), "orderCode");
		if(StringUtils.isBlank(orderCode)){
			orderCode = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "orderCode")==null?"":AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "orderCode");
		}
		if(orderCode!=null){
			pramt.put("orderCode",orderCode);
		}
		String vstr = AjaxHelper.getHttpParam(request(), "vstr");
		if(StringUtils.isBlank(vstr)){
			vstr = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "vstr");
		}
		if(vstr!=null){
			pramt.put("vstr",vstr);
		}
		String amount = AjaxHelper.getHttpParam(request(), "amount");
		if(StringUtils.isBlank(amount)){
			amount = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "amount");
		}
		if(amount!=null){
			pramt.put("amount",amount);
		}
		
		String lwdjl=AjaxHelper.getHttpParamOfFormUrlEncoded(request(),"lwdjl")==null?"":AjaxHelper.getHttpParamOfFormUrlEncoded(request(),"lwdjl").trim();
		String sign=StringUtil.makeSig(pramt)+"HG2015";
		
		logger.info(sign);
		
	
		if(StringUtils.isBlank(uid)||StringUtils.isBlank(orderCode)||StringUtils.isBlank(amount)||StringUtils.isBlank(vstr)){
			result.put("status", 0);
			return ok(Json.toJson(result));
		}
		String ip=request().remoteAddress();
		BigDecimal price = new BigDecimal(amount).multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_CEILING);
		JsonNode reslut = WXPayService.getInstance().getSignAndPrepayID(orderCode,  price.longValue(),ip);
		if(reslut==null){
			result.put("status", 0);
			return ok(Json.toJson(result));
		}else{
			result.put("status", 1);
			result.put("info", reslut);
			return ok(Json.toJson(result));
		}
	}

	/**
	 * 微信支付的回调(异步) POST WAP
	 * @param id
	 * @return
	 */
	public Result wxpayWapNotify() {
		response().setContentType("application/json;charset=utf-8");
		String method = "10";
		String state = "20";
		ObjectNode result = Json.newObject();
		
		String orderCode = AjaxHelper.getHttpParam(request(), "out_trade_no");
		if(StringUtils.isBlank(orderCode)){
			orderCode = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "out_trade_no")==null?"":AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "out_trade_no");
		}
		String transaction_id = AjaxHelper.getHttpParam(request(), "transaction_id");
		if(StringUtils.isBlank(transaction_id)){
			transaction_id = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "transaction_id")==null?"":AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "transaction_id");
		}
		if(orderCode.length()>10){
    		orderCode = orderCode.substring(0, 10);
    	}
		logger.info("orderCode:"+orderCode);
		/////////待业务处理
		return null;
	}

	/**
	 * 微信支付的回调(异步) 供客户端调用
	 * @param id
	 * @return
	 */
	public Result wxcallback() {
		response().setContentType("application/json;charset=utf-8");
		String method = "10";
		String state = "10";
		ObjectNode result = Json.newObject();
		Map<String, String> pramt = new HashMap<String, String>();
	
		String appversion = AjaxHelper.getHttpParam(request(), "appversion");
		if(StringUtils.isBlank(appversion)){
			appversion = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "appversion");
		}
		if(appversion!=null){
			pramt.put("appversion",appversion);
		}
		
		String idfa = AjaxHelper.getHttpParam(request(), "idfa");
		if(StringUtils.isBlank(idfa)){
			idfa = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "idfa");
		}
		if(idfa!=null){
			pramt.put("idfa",idfa);
		}
		
		String marketCode = AjaxHelper.getHttpParam(request(), "marketCode");
		if(StringUtils.isBlank(marketCode)){
			marketCode = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "marketCode");
		}
		if(marketCode!=null){
			pramt.put("marketCode",marketCode);
		}
		
		String model = AjaxHelper.getHttpParam(request(), "model");
		if(StringUtils.isBlank(model)){
			model = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "model");
		}
		if(model!=null){
			pramt.put("model",model);
		}
		
		String wdhjy = AjaxHelper.getHttpParam(request(), "wdhjy");
		if(StringUtils.isBlank(wdhjy)){
			wdhjy = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "wdhjy");
		}
		if(wdhjy!=null){
			pramt.put("wdhjy",wdhjy);
		}
		
		String osversion = AjaxHelper.getHttpParam(request(), "osversion");
		if(StringUtils.isBlank(osversion)){
			osversion = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "osversion");
		}
		if(osversion!=null){
			pramt.put("osversion",osversion);
		}
		
		String usewallet = AjaxHelper.getHttpParam(request(), "usewallet");
		if(StringUtils.isBlank(usewallet)){
			usewallet = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "usewallet");
		}
		if(usewallet!=null){
			pramt.put("usewallet",usewallet);
		}
		
		
		String deviceType = AjaxHelper.getHttpParam(request(), "deviceType");
		if(StringUtils.isBlank(deviceType)){
			deviceType = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "deviceType");
		}
		if(deviceType!=null){
			pramt.put("deviceType",deviceType);
		}
		
		String devid = AjaxHelper.getHttpParam(request(), "devid");
		if(StringUtils.isBlank(devid)){
			devid = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "devid");
		}
		if(devid!=null){
			pramt.put("devid",devid);
		}
		
		String uid = AjaxHelper.getHttpParam(request(), "uid");
		if(StringUtils.isBlank(uid)){
			uid = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "uid");
		}
		if(uid!=null){
			pramt.put("uid",uid);
		}
		String orderCode = AjaxHelper.getHttpParam(request(), "orderCode");
		if(StringUtils.isBlank(orderCode)){
			orderCode = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "orderCode")==null?"":AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "orderCode");
		}
		if(orderCode!=null){
			pramt.put("orderCode",orderCode);
		}
		String vstr = AjaxHelper.getHttpParam(request(), "vstr");
		if(StringUtils.isBlank(vstr)){
			vstr = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "vstr");
		}
		if(vstr!=null){
			pramt.put("vstr",vstr);
		}
		String amount = AjaxHelper.getHttpParam(request(), "amount");
		if(StringUtils.isBlank(amount)){
			amount = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "amount");
		}
		if(amount!=null){
			pramt.put("amount",amount);
		}
		String errcode = AjaxHelper.getHttpParam(request(), "errcode");
		if(StringUtils.isBlank(errcode)){
			errcode = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "errcode")==null?"100":AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "errcode");
		}
		if(errcode!=null){
			pramt.put("errcode",errcode);
		}
		
		String lwdjl=AjaxHelper.getHttpParamOfFormUrlEncoded(request(),"lwdjl")==null?"":AjaxHelper.getHttpParamOfFormUrlEncoded(request(),"lwdjl").trim();
		String sign=StringUtil.makeSig(pramt)+"HG2015";
		
		logger.info(sign);

		if(orderCode.length()>10){
    		orderCode = orderCode.substring(0, 10);
    	}
		logger.info("uid:"+uid+"orderCode:"+orderCode+"vstr:"+vstr+"amount:"+amount+"errcode:"+errcode);
		///待处理
		return null;
	}
	
	//微信回调JSAPI回调
	public Result WXpayreturnJSAPI(){

		String bodystr=request().body().toString();
		logger.info("微信回调contentType:"+bodystr);
		
		String return_code="";
		String result_code="";
		String out_trade_no="";
		String transaction_id="";
		String mch_id="";
		String appid="";
		
		String method = "11";
		String state = "20";
		StringBuilder result=new StringBuilder();
		
		//解析body
	    if(bodystr.indexOf("<xml>")>=0){
	    	bodystr=bodystr.substring(bodystr.indexOf("<xml>"),bodystr.indexOf("</xml>"))+"</xml>";
	    	logger.info("bodystr:"+bodystr);
	    }

	    if(!StringUtils.isBlank(bodystr)){
	    	try{
				org.dom4j.Document xmltmp=org.dom4j.DocumentHelper.parseText(bodystr.replace("\n", ""));
				if(xmltmp!=null){
					org.dom4j.Element root =xmltmp.getRootElement();
					appid=root.elementText("appid");
					mch_id=root.elementText("mch_id");
					out_trade_no=root.elementText("out_trade_no");
					return_code=root.elementText("return_code");
					transaction_id=root.elementText("transaction_id");
				}
	    	}
	    	catch(Exception e){
	    		logger.info("转换ＸＭＬ失败："+e.toString());
	    	}
		}
		if(!StringUtils.isBlank(return_code) && return_code.equals("SUCCESS")&&!StringUtils.isBlank(out_trade_no) && !StringUtils.isBlank(appid) && !StringUtils.isBlank(transaction_id) && !StringUtils.isBlank(mch_id)){
			if(out_trade_no.length()>10){
				out_trade_no = out_trade_no.substring(0, 10);
	    	}
			if(appid.equals(Constants.WXappID) && mch_id.equals(Constants.WXMCID)){
				//待处理
				result.append("<xml><return_code><![CDATA[SUCCESS]]></return_code>");
				result.append("<return_msg><![CDATA[OK]]></return_msg></xml>");
				return ok(result.toString());
			}
		}
		result.append("<xml><return_code><![CDATA[FAIL]]></return_code>");
		result.append("<return_msg><![CDATA[OK]]></return_msg></xml>");
		return ok(result.toString());
	}
	
	
	/*
	 * 微信退款接口
	 */
	public Result WXpayBackMoney(){
		response().setContentType("application/json;charset=utf-8");
		String transactionid=AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "transactionid");
		String ordercode=AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "ordercode");
		ObjectNode re=Json.newObject();
		re.put("status", "0");
		re.put("msg", "退款失败");
		//ShoppingOrder order=shoppingOrderService.getShoppingOrderByOrderCode(ordercode);
		String nonce_str=RandomStringUtils.randomAlphanumeric(32);
		if(StringUtils.isBlank(transactionid) || StringUtils.isBlank(ordercode))
			return ok(re);
		
		Map<String,String> signmap=new HashMap<String,String>();
		signmap.put("appid",Constants.WXappID);
		signmap.put("mch_id",Constants.WXMCID);
		signmap.put("nonce_str", nonce_str);
		signmap.put("transaction_id", transactionid);
		signmap.put("out_trade_no",ordercode);
		signmap.put("out_refund_no", ordercode);
		//signmap.put("total_fee", String.valueOf(100*order.getTotalFee()));
		//signmap.put("refund_fee", String.valueOf(order.getFinalpay()));
		signmap.put("refund_fee_type", "CNY");
		signmap.put("op_user_id", Constants.WXMCID);
		signmap.put("sign", StringUtil.getSign(signmap));
		
		String postData = "<xml>";
		Set es = signmap.entrySet();
		Iterator it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			if (k != "appkey") {
				postData += "<" + k + ">" + v + "</"+k+">";
			}
		}
		postData += "</xml>";
		String wxurl="https://api.mch.weixin.qq.com/secapi/pay/refund";

		String resContent = "";
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(wxurl);
		post.setHeader("Content-Type", "text/xml; charset=UTF-8");
		try {
			post.setEntity(new StringEntity(postData,"UTF-8"));
			HttpResponse res = client.execute(post);
			String strResult = EntityUtils.toString(res.getEntity(), "UTF-8");

			Logger.info("weixin退款返回:" + strResult);
			if (strResult != null && strResult.length() > 0) {
				Document doc = null;
				doc = DocumentHelper.parseText(strResult);
				Element rootElt = doc.getRootElement();
				// returnstr=strResult;
				String return_code=rootElt.elementTextTrim("return_code");
				String return_msg=rootElt.elementTextTrim("return_code");
				if(!StringUtils.isBlank(return_code) && return_code.equals("")){
					//微信退款单号
					String refund_id=rootElt.elementText("refund_id");
					//微信退款渠道
					String refund_channel=rootElt.elementText("refund_channel");
					
					//记录退款日志表，待续............
					re.put("status", "1");
				}else{
					re.put("msg",StringUtils.isBlank(return_msg)?"调用退款失败":return_msg);
				}
			}
		}catch(Exception e){}
		
		return ok(re);
	}
	/**  
     * 初始化一个DocumentBuilder  
     *  
     * @return a DocumentBuilder  
     * @throws ParserConfigurationException  
     */  
    public static DocumentBuilder newDocumentBuilder()  
            throws ParserConfigurationException {  
        return newDocumentBuilderFactory().newDocumentBuilder();  
    }  
    /**  
     * 初始化一个DocumentBuilderFactory  
     *  
     * @return a DocumentBuilderFactory  
     */  
    public static DocumentBuilderFactory newDocumentBuilderFactory() {  
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
        dbf.setNamespaceAware(true);  
        return dbf;  
    }  
}
