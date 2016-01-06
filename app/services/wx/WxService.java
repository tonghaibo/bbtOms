package services.wx;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;






import models.user.UserInfo;
import models.wx.WeiXinConstant;
import models.wx.WxOauthToken;
import models.wx.WxRequest;
import models.wx.WxUser;
import models.wx.WxuserInfo;
import models.wx.menu.MenuCreate;
import models.wx.menu.WeixinButton;
import models.wx.template.Template;
import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.Json;
import play.mvc.Http.Request;


import services.cache.CacheMem;
import services.user.UserService;
import utils.AjaxHelper;
import utils.BeanUtils;
import utils.Constants;
import utils.Numbers;
import utils.SignUtils;
import utils.StringUtil;
import utils.WSUtils;
import utils.wx.AesException;
import utils.wx.Sha1Util;
import utils.wx.TenpayHttpClient;
import utils.wx.WXBizMsgCrypt;
import models.wx.WxSign;

public class WxService {

	private static final Logger.ALogger logger = Logger.of(WxService.class);
	private static final String token = Play.application().configuration().getString("wx.token");
	private static CacheMem cache = CacheMem.instance;
	private static WXBizMsgCrypt wbmcBizMsgCrypt;
	
	// 消息预览URL
	private static String message_preview_url = "https://api.weixin.qq.com/cgi-bin/message/mass/preview?access_token=ACCESS_TOKEN";
	private static String menu_delete_url = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=ACCESS_TOKEN";
	// 支付完成后的回调处理页面
	private static String notify_url = Constants.WXCALLBACK;
	private static String app_id;
	private static String app_secret;
	private static String oms_url;
	private static String encodingAESKey;

	static {
		boolean IsProduct = Play.application().configuration().getBoolean("production", false);
		if (IsProduct) {
			app_id = "wx99199cff15133f37";
			app_secret = "a017774f117bf0100a2f7939ef56c89a";
//			app_id = "wx0cef7e835f598e36";
//			app_secret = "416704762a6b40c20025ea169bb00e61";
			oms_url = "http://oms.higegou.com";
			encodingAESKey = "Msmp1gf8mmY2ARbLFwLsrn5hgYgNgwFtwq8uAh8udkS";
		} else {
			app_id = "wx0cef7e835f598e36";
			app_secret = "416704762a6b40c20025ea169bb00e61";
			oms_url = "http://oms.higegou.com:9200";
			encodingAESKey = "Msmp1gf8mmY2ARbLFwLsrn5hgYgNgwFtwq8uAh8udkS";
		}
		try {
			wbmcBizMsgCrypt = new WXBizMsgCrypt(token, encodingAESKey, app_id);
		} catch (AesException e) {
			e.printStackTrace();
			logger.info("加解密出错");
		}
	}

	private static WxService instance = null;

	public static WxService getInstance() {
		if (instance == null) {
			syncInit();
		}
		return instance;
	}

	private WxService() {
	}

	private static synchronized void syncInit() {
		if (instance == null) {
			instance = new WxService();
		}
	}

	/**
	 * 获取TOKEN，一天最多获取200次，需要所有用户共享值
	 */
	public String getAccessToken() {
		String wxpay_access_token = cache.get("wx_access_token_new");
		if (StringUtils.isBlank(wxpay_access_token)) {
			wxpay_access_token = getTokenReal();
		}
		return wxpay_access_token;

	}

	private String getTokenReal() {
		String token = "";
		String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + app_id + "&secret="
				+ app_secret;
		logger.info(url);
		JsonNode result = WSUtils.getResponseAsJson(url);
		if (result != null) {
			token = result.get("access_token") == null ? "" : result.get("access_token").asText();
			cache.setWithOutTime("wx_access_token", token,5400);
		}
		return token;
	}
	
	/**
	 * 
	 * <p>Title: getJsapi_ticketReal</p> 
	 * <p>Description: </p> 
	 * @return
	 */
	private String getJsapi_ticketReal() {
		String ticket = "";
		String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+getAccessToken()+"&type=jsapi";
		logger.info(url);
		JsonNode result = WSUtils.getResponseAsJson(url);
		if (result != null) {
			ticket = result.get("ticket") == null ? "" : result.get("ticket").asText();
			cache.setWithOutTime("bbt_getwxSign_ticket_new", ticket,5400);
		}
		return ticket;
	}

	/**
	 * 
	 * <p>
	 * Title: checkSignature
	 * </p>
	 * <p>
	 * Description: 校验签名
	 * </p>
	 * 
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @return
	 */
	public boolean checkSignature(String signature, String timestamp, String nonce) {
		String[] paramArr = new String[] { token, timestamp, nonce };
		Arrays.sort(paramArr);
		String content = paramArr[0].concat(paramArr[1]).concat(paramArr[2]);
		String mySign = SignUtils.getSha1Sign(content);
		logger.info("mySign：" + mySign);
		if (!Strings.isNullOrEmpty(signature) && !Strings.isNullOrEmpty(mySign)) {
			if (mySign.equals(signature)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * <p>Title: getWyAccessToken</p> 
	 * <p>Description: 获取网页授权access_token</p> 
	 * @param code
	 */
	public WxOauthToken getWyAccessToken(String code) {
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+app_id+"&secret="+app_secret+"&code="+code+"&grant_type=authorization_code";
//		String url = "http://api.higegou.com/getWyAccessToken/"+code;
		JsonNode result = WSUtils.getResponseAsJson(url);
		logger.info("url:"+url);
		logger.info("result:"+result);
		WxOauthToken wxOauthToken = new WxOauthToken();
		if(result!=null){
			Object error = result.get(WeiXinConstant.RETURN_ERROR_INFO_CODE);
			if (error != null && Integer.parseInt(error.toString()) != 0) {
				logger.info("getWyAccessToken error info:"+result.toString());
				String msg = result.get(WeiXinConstant.RETURN_ERROR_INFO_MSG).asText();
				logger.info("getWyAccessToken errormsg:" + msg);
			}
			wxOauthToken = BeanUtils.castEntityFromJsonNode(result, WxOauthToken.class);
			logger.info("wyaccesstoken openid:"+result.get("openid"));
		}
		return wxOauthToken;
	}

	/**
	 * 创建菜单 button 是 一级菜单数组，个数应为1~3个 sub_button 否 二级菜单数组，个数应为1~5个 type 是
	 * 菜单的响应动作类型 name 是 菜单标题，不超过16个字节，子菜单不超过40个字节 key click等点击类型必须
	 * 菜单KEY值，用于消息接口推送，不超过128字节 url view类型必须 网页链接，用户点击菜单可打开链接，不超过256字节
	 * 
	 * @param accessToken
	 * @param button
	 *            的json字符串
	 * @throws WexinReqException
	 */
	public String createMenu(List<WeixinButton> button) {
		String accessToken = getAccessToken();
		MenuCreate m = new MenuCreate();
		m.setAccess_token(accessToken);
		m.setButton(button);
		String createMenuUrl = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + accessToken;
		JsonNode json = Json.toJson(m);
		JsonNode result = WSUtils.postByJSON(createMenuUrl, json);
		Object error = result.get(WeiXinConstant.RETURN_ERROR_INFO_CODE).asInt();
		logger.info("createMenu errorcode:" + error);
		String msg = result.get(WeiXinConstant.RETURN_ERROR_INFO_MSG).asText();
		logger.info("createMenu errormsg:" + msg);
		logger.info("createMenu errorcode:" + error + ",errormsg:" + msg);
		return msg;
	}
	
	/**
	 * 
	 * <p>Title: deleteMenu</p> 
	 * <p>Description: 删除菜单</p>
	 */
	public void deleteMenu() {
		String accesstoken = getAccessToken();
		logger.info("accessToken:" + accesstoken);
		String requestUrl = menu_delete_url.replace("ACCESS_TOKEN", accesstoken);
		JsonNode result = WSUtils.getResponseAsJson(requestUrl);
		int code = result.get(WeiXinConstant.RETURN_ERROR_INFO_CODE).asInt();		
		String msg = result.get(WeiXinConstant.RETURN_ERROR_INFO_MSG).asText();
		logger.info("deleteMenu result code:"+code+", msg:"+msg);
	}

	/**
	 * 根据user_openid 获取关注用户的基本信息
	 * 
	 * @param shelf_id
	 * @return
	 * @throws Exception
	 * @throws WexinReqException
	 */
	public WxuserInfo getWxuser(String user_openid) {
		String accesstoken = getAccessToken();
		logger.info("accessToken:" + accesstoken);
		if (!Strings.isNullOrEmpty(accesstoken) && !Strings.isNullOrEmpty(user_openid)) {
			String url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=" + accesstoken + "&openid="
					+ user_openid + "&lang=zh_CN";
			JsonNode result = WSUtils.getResponseAsJson(url);
			logger.info("getWxuser result" + result);
			if (result != null) {
				Object error = result.get(WeiXinConstant.RETURN_ERROR_INFO_CODE);
				if (error != null && Integer.parseInt(error.toString()) != 0) {
					logger.info("getWxuser error info:"+result.toString());
				}else{
					logger.info("wx user name "+result.get("nickname").asText());
				}
				WxuserInfo wxuserInfo = BeanUtils.castEntityFromJsonNode(result, WxuserInfo.class);
				return wxuserInfo;
			}
		}
		return null;
	}
	

	// 组装微信签名返回参数数据
	public WxSign getwxstr(Request request) {
		String url = "http://"+request.host() + request.uri();
		String nostr = RandomStringUtils.randomAlphanumeric(16);
		String timstr = Sha1Util.getTimeStamp();
		String ticket = cache.get("bbt_getwxSign_ticket_new");
		if (StringUtils.isBlank(ticket)) {
			ticket = getJsapi_ticketReal();
		}
		// 设置签名参数
		SortedMap<String, String> signParams = new TreeMap<String, String>();
		signParams.put("timestamp", timstr);
		signParams.put("noncestr", nostr);
		signParams.put("jsapi_ticket", ticket);
		signParams.put("url", url);
		String sign = getShareSign(signParams);
		logger.info("加密后的sign为："+sign);
		WxSign wxsign = new WxSign();
		wxsign.setNostr(nostr);
		wxsign.setTimstr(timstr);
		wxsign.setAppId(app_id);
		wxsign.setSign(sign);
		wxsign.setShareurl(url);
		return wxsign;
	}

	// 组装分享
	public String getShareSign(Map<String, String> map) {
		String str = makeSig(map);

		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(str.getBytes("UTF-8"));
			return byteToHex(crypt.digest());
		} catch (Exception e) {
			str = "";
		}
		return str;
	}

	private String byteToHex(final byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}

	// 组装签名字符串
	public String makeSig(Map<String, String> sortMap) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		Object[] keys = sortMap.keySet().toArray();
		Arrays.sort(keys);
		for (int i = 0; i < keys.length; i++) {
			String mapkey = (String) keys[i];
			if (i == keys.length - 1) {// 拼接时，不包括最后一个&字符
				sb.append(mapkey).append("=").append(sortMap.get(mapkey));// QSTRING_EQUAL为=,QSTRING_SPLIT为&
			} else {
				sb.append(mapkey).append("=").append(sortMap.get(mapkey)).append("&");
			}
		}
		String data = sb.toString();// 参数拼好的字符串

		System.out.println("加密参数为：" + data);
		return data;
	}

	// 获取配置文件中的uid
	public String getConfigUid() {
		String uid = "";
    	boolean IsProduct = Play.application().configuration().getBoolean("production", false);
		if(IsProduct){
			uid = Play.application().configuration().getString("wx.default.uid.product");
		}else{
			uid = Play.application().configuration().getString("wx.default.uid.dev");
		}
		return uid;
	}
	
	/**
	 * 
	 * <p>Title: checkSignatureWithCrypt</p> 
	 * <p>Description: </p> 
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @param encrypt_type
	 * @param msg_signature
	 * @param string 
	 * @return
	 */
	public String checkSignatureWithCrypt(String signature, String timestamp,
			String nonce, String encrypt_type, String msg_signature, String postData) {
		String str = "";
		logger.info("post data:"+postData);
		logger.info("post signature:"+signature);
		logger.info("post timestamp:"+timestamp);
		logger.info("post nonce:"+nonce);
		try {
			str = wbmcBizMsgCrypt.decryptMsg(signature, timestamp, nonce, postData);
		} catch (AesException e) {
			e.printStackTrace();
			logger.info("解密出错");
		}
		logger.info("decrypt msg info:"+str);
		return str;
	}

	/**
	 * 获取二维码ticket
	 * @param scene_id
	 * @return
	 */
	public String getQrCodeTicket(String scene_str){
		String ticket="";
		String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token="+getTokenReal();
		ObjectNode json = Json.newObject();
		json.put("action_name", "QR_LIMIT_STR_SCENE");//二维码类型，QR_SCENE为临时,QR_LIMIT_SCENE为永久,QR_LIMIT_STR_SCENE为永久的字符串参数值
		ObjectNode action_info = Json.newObject();//二维码详细信息
		ObjectNode scene = Json.newObject();//二维码详细信息
//		action_info.put("scene_id", scene_id);//场景值ID，临时二维码时为32位非0整型，永久二维码时最大值为100000（目前参数只支持1--100000）
		scene.put("scene_str", scene_str);//场景值ID（字符串形式的ID），字符串类型，长度限制为1到64，仅永久二维码支持此字段
		action_info.set("scene", scene);
		json.set("action_info", action_info);
		JsonNode result = WSUtils.postByJSON(url, json);
		if(result!=null){
			ticket = result.get("ticket")==null?"":result.get("ticket").asText();
			String urlResult = result.get("url")==null?"":result.get("url").asText();
		}
		return ticket;
	}

	/**
	 * 
	 * <p>Title: getWxOauthUrl</p> 
	 * <p>Description: 微信授权指定的url调整</p> 
	 * @param string
	 * @return
	 */
	public String getWxOauthUrl(String type) {
		String url = "";
		String href = StringUtil.getDomainH5();
		try{
			if("bbtsend".equals(type)){
				//发快递
			 	//String urlorderlistEncode = URLEncoder.encode(href+"/wechat/order/index","utf-8");
			 	String urlTodayEncode = URLEncoder.encode(href+"/user/getwxpos?flg=bbtindex","utf-8");
			 	url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+app_id+"&redirect_uri="+urlTodayEncode+"&response_type=code&scope=snsapi_base&state=123#wechat_redirect";
			}else if("bbtsearch".equals(type)){
				//查快递
				//String urlmallEncode = URLEncoder.encode(href+"/wechat/order/search","utf-8");
				String urlTodayEncode = URLEncoder.encode(href+"/user/getwxpos?flg=bbtsearch","utf-8");
				url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+app_id+"&redirect_uri="+urlTodayEncode+"&response_type=code&scope=snsapi_base&state=123#wechat_redirect";
			}else if("bbtlist".equals(type)){
				//我的快递
				//String urlTodayEncode = URLEncoder.encode(href+"/wechat/order/list","utf-8");
				String urlTodayEncode = URLEncoder.encode(href+"/user/getwxpos?flg=mybbt","utf-8");
				url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+app_id+"&redirect_uri="+urlTodayEncode+"&response_type=code&scope=snsapi_base&state=123#wechat_redirect";				
			}else if(("bbtsender").equals(type)){
				//我的寄件人
				//String urlTodayEncode = URLEncoder.encode(href+"/wechat/senders/list","utf-8");
				String urlTodayEncode = URLEncoder.encode(href+"/user/getwxpos?flg=myposter","utf-8");
				url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+app_id+"&redirect_uri="+urlTodayEncode+"&response_type=code&scope=snsapi_base&state=123#wechat_redirect";								
			}else if(("bbtposter").equals(type)){
				//我的收件人
				//String urlTodayEncode = URLEncoder.encode(href+"/wechat/receivers/list","utf-8");
				String urlTodayEncode = URLEncoder.encode(href+"/user/getwxpos?flg=mysender","utf-8");
				url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+app_id+"&redirect_uri="+urlTodayEncode+"&response_type=code&scope=snsapi_base&state=123#wechat_redirect";								
			}
			else if(("bbtcoupon").equals(type)){
				//免费优惠券
				//String urlTodayEncode = URLEncoder.encode(href+"/wechat/coupon/index","utf-8");
				String urlTodayEncode = URLEncoder.encode(href+"/user/getwxpos?flg=coupon","utf-8");
				url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+app_id+"&redirect_uri="+urlTodayEncode+"&response_type=code&scope=snsapi_base&state=123#wechat_redirect";								
			}else if(("bbtindex").equals(type)){
				//微信发棒棒糖快递
				String urlTodayEncode = URLEncoder.encode(href+"/user/getwxpos","utf-8");
				url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+app_id+"&redirect_uri="+urlTodayEncode+"&response_type=code&scope=snsapi_base&state=123#wechat_redirect";				
			}else if("bbtpostlist".equals(type)){
				String urlTodayEncode = URLEncoder.encode(href+"/user/getwxpos?flg=list","utf-8");
				url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+app_id+"&redirect_uri="+urlTodayEncode+"&response_type=code&scope=snsapi_base&state=123#wechat_redirect";				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return url;
	}
	
	/**
	 * 
	 * <p>Title: sendTemplate</p> 
	 * <p>Description: 发送模板消息</p> 
	 * @param wxuser
	 * @return
	 */
	public String sendTemplate(Template template) {
		String accessToken = getAccessToken();
		String sendTemplateUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + accessToken;
		JsonNode json = Json.toJson(template);
		JsonNode result = WSUtils.postByJSON(sendTemplateUrl, json);
		int error = result.get(WeiXinConstant.RETURN_ERROR_INFO_CODE).asInt();
		logger.info("sendTemplate errorcode:" + error);
		if(error==0){
			logger.info("推送用户："+template.getTouser()+"模板消息成功");
		}else{
			logger.info("推送用户："+template.getTouser()+"模板消息失败");
			
		}
		String msg = result.get(WeiXinConstant.RETURN_ERROR_INFO_MSG).asText();
		logger.info("sendTemplate errormsg:" + msg);
		logger.info("sendTemplate errorcode:" + error + ",errormsg:" + msg);
		return msg;
	}
	

	/*
	 * 封装微信支付totalfee以分为单位 
	 */
		public static WxRequest getPrepayId(String ordercode,Integer totalfee,String openid,String goods_tag){
			String wxorderurl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
			Map<String, String> prams = new HashMap<String, String>();
			WxRequest rq = new WxRequest();
			String sign = "";
			String prepayid="";
			String nostr = RandomStringUtils.randomAlphanumeric(32);
			prams.put("appid", Constants.WXappID);
			prams.put("attach", "嗨个购-" + ordercode);
			rq.setAttach("嗨个购-" + ordercode);
			prams.put("body", "嗨个购-" + ordercode);
			rq.setBody("嗨个购-" + ordercode);
			prams.put("mch_id", Constants.WXMCID);

			prams.put("nonce_str", nostr);
			rq.setNonce_str(nostr);
			rq.setTim_Str(Sha1Util.getTimeStamp());
			
			prams.put("notify_url", notify_url);
			rq.setNotify_url(notify_url);

			prams.put("openid", openid);
			rq.setOpenid(openid);

			prams.put("out_trade_no", ordercode);
			rq.setOut_trade_no(ordercode);
			prams.put("spbill_create_ip", "123.56.105.53");
			rq.setSpbill_create_ip("123.56.105.53");
			
			//tmpt="1";
			
			prams.put("total_fee", totalfee.intValue()+"");
			rq.setTotal_fee(totalfee);
			prams.put("trade_type", "JSAPI");
			
			if(goods_tag!=null && !StringUtils.isBlank(goods_tag)){
				prams.put("goods_tag", goods_tag);
				rq.setGoods_tag(goods_tag);
			}
			
			sign = StringUtil.getSign(prams);
			prams.put("sign", sign);
			rq.setSign(sign);

			String postData = "<xml>";
			Set es = prams.entrySet();
			Iterator it = es.iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String k = (String) entry.getKey();
				String v = (String) entry.getValue();
				if (k != "appkey") {
					postData += "<" + k + ">" + v + "</" + k + ">";
				}
			}
			postData += "</xml>";
			postData = getXmlInfo(rq);
			// postData=new String(postData.getBytes(), "ISO8859-1");
			String resContento = "";
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(wxorderurl);
			post.setHeader("Content-Type", "text/xml; charset=UTF-8");
			try {
				post.setEntity(new StringEntity(postData, "UTF-8"));
				HttpResponse res = client.execute(post);
				String strResult = EntityUtils.toString(res.getEntity(),
						"UTF-8");

				Logger.info("统一订购接收到的报文:" + strResult);

				if (strResult != null && strResult.length() > 0) {
					Document doc = null;
					doc = DocumentHelper.parseText(strResult);
					Element rootElt = doc.getRootElement();
					// returnstr=strResult;
					String return_code = rootElt
							.elementTextTrim("return_code");
					String return_msg = rootElt
							.elementTextTrim("return_code");

					logger.info("win xin pay return:"
							+ rootElt.elementTextTrim("return_code"));
					logger.info("win xin pay return:"
							+ rootElt.elementText("return_msg"));
					if (return_code != null
							&& return_code.equals("SUCCESS")) {
						logger.info("win xin pay return:"
								+ rootElt.elementText("err_code"));
						logger.info("win xin pay return:"
								+ rootElt.elementText("err_code_des"));

						if (rootElt.element("result_code") != null
								&& rootElt.elementText("result_code")
										.equals("SUCCESS")) {
							if (rootElt.element("prepay_id") != null)
								prepayid = (rootElt
										.elementText("prepay_id"));
						}
					}
				}
			} catch (Exception e) {

				logger.error("win xin pay error:" + e.toString());
			}
			if(!StringUtils.isBlank(prepayid))
				rq.setPrepayid(prepayid);
			else
				return null;
			return rq;
		}
		// 组装统一订购接口XML字符串
		private static String getXmlInfo(WxRequest obj) {
			StringBuilder sb = new StringBuilder();
			sb.append("<xml>");
			sb.append("<appid>" + Constants.WXappID + "</appid>");
			if (obj.getAttach() != null && !obj.getAttach().equals(""))
				sb.append("<attach>" + obj.getAttach() + "</attach>");
			else
				sb.append("<attach></attach>");

			if (obj.getBody() != null && !obj.getBody().equals(""))
				sb.append("<body>" + obj.getBody() + "</body>");
			sb.append("<mch_id>" + Constants.WXMCID + "</mch_id>");
			if (obj.getNonce_str() != null && !obj.getNonce_str().equals(""))
				sb.append("<nonce_str>" + obj.getNonce_str() + "</nonce_str>");
			if (obj.getNotify_url() != null && !obj.getNotify_url().equals(""))
				sb.append("<notify_url>" + obj.getNotify_url() + "</notify_url>");
			if (obj.getOpenid() != null && !obj.getOpenid().equals(""))
				sb.append("<openid>" + obj.getOpenid() + "</openid>");
			if (obj.getOut_trade_no() != null && !obj.getOut_trade_no().equals(""))
				sb.append("<out_trade_no>" + obj.getOut_trade_no()
						+ "</out_trade_no>");
			if (obj.getProduct_id() != null && !obj.getProduct_id().equals(""))
				sb.append("<product_id>" + obj.getProduct_id() + "</product_id>");
			if (obj.getSign() != null && !obj.getSign().equals(""))
				sb.append("<sign>" + obj.getSign() + "</sign>");
			if (obj.getSpbill_create_ip() != null
					&& !obj.getSpbill_create_ip().equals(""))
				sb.append("<spbill_create_ip>" + obj.getSpbill_create_ip()
						+ "</spbill_create_ip>");

			if (obj.getTotal_fee().intValue() > 0)
				sb.append("<total_fee>" + obj.getTotal_fee().intValue()
						+ "</total_fee>");
			if (obj.getTrade_type() != null && !obj.getTrade_type().equals(""))
				sb.append("<trade_type>" + obj.getTrade_type() + "</trade_type>");
			sb.append("</xml>");

			String sbt = sb.toString();
			Logger.debug(sbt);

			return sbt;
		}	
		// 组装微信签名返回参数数据
		public static WxSign getwxWebSign(Request request) {
			String nostr = StringUtils.isBlank(cache.get("bbt_getwxSign_nostr")) ? RandomStringUtils
					.randomAlphanumeric(16) : cache.get("bbt_getwxSign_nostr");
			String timstr = StringUtils.isBlank(cache.get("bbt_getwxSign_timstr")) ? Sha1Util
					.getTimeStamp() : cache.get("bbt_getwxSign_timstr");
			String ticket = StringUtils.isBlank(cache.get("bbt_getwxSign_ticket")) ? ""
					: cache.get("bbt_getwxSign_ticket");// session("ticket");
			String access_token = StringUtils.isBlank(cache
					.get("bbt_getwxSign_access_token")) ? "" : cache
					.get("bbt_getwxSign_access_token");

			String resContent = "";
			String sign = "";
			boolean IsProduct = Configuration.root()
					.getBoolean("production", false);

			TenpayHttpClient httpClient = new TenpayHttpClient();
			//access_token="";
			if (StringUtils.isBlank(access_token)) {
				try {
					// 清缓存
					cache.clear("bbt_getwxSign_nostr");
					cache.clear("bbt_getwxSign_timstr");
					cache.clear("bbt_getwxSign_ticket");
					nostr = RandomStringUtils.randomAlphanumeric(16);
					timstr = Sha1Util.getTimeStamp();

					String gettokenurl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="
							+ Constants.WXappID
							+ "&secret="
							+ Constants.WXappsecret;
					httpClient.setReqContent(gettokenurl);
					if (httpClient.callHttpPost(gettokenurl, "")) {
						resContent = httpClient.getResContent();
						Logger.info("微信请求access_token返回：" + resContent);
						JsonNode json = Json.parse(resContent);
						try {
							access_token = json.get("access_token").asText();
							cache.setWithOutTime("bbt_getwxSign_nostr", nostr, 7200);
							cache.setWithOutTime("bbt_getwxSign_timstr", timstr, 7200);
							cache.setWithOutTime("bbt_getwxSign_access_token",
									access_token, 7200);
						} catch (Exception ee) {
						}
						if (!StringUtils.isBlank(access_token)) {
							try {
								// 获取 jsp_tackit
								String getTickaturl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="
										+ access_token + "&type=jsapi";
								httpClient = new TenpayHttpClient();
								httpClient.setReqContent(getTickaturl);
								if (httpClient.callHttpPost(getTickaturl, "")) {
									resContent = httpClient.getResContent();
									Logger.info("微信请求jsapi_ticket返回：" + resContent);
									JsonNode jsont = Json.parse(resContent);
									if (jsont.get("errcode").asText().equals("0") || jsont.get("errcode").asInt()==0) {
										ticket = jsont.get("ticket").asText();
										cache.setWithOutTime("bbt_getwxSign_ticket",
												ticket, 7150);
									}
								}
							} catch (Exception ee) {
							}
						}
					}
				} catch (Exception e) {
				}
			}
			String qry = "";
			String vn = "";
			String vl = "";
			Map<String, String[]> maps = request.queryString();
			if (maps != null && maps.keySet() != null) {
				Iterator<String> keyIt = maps.keySet().iterator();
				while (keyIt.hasNext()) {
					vn = keyIt.next();
					vl = maps.get(vn)[0];
					qry = qry + "&" + vn + "=" + vl;
				}
			}

			if (!StringUtils.isBlank(qry))
				qry = "?" + qry.substring(1);
			String dport = IsProduct == true ? "" : "";//":9004";
			String urls = "http://" +  request.host() + dport +  request.path()
					+ qry;
			Logger.info("解析urls:" + urls);
			Map<String, String> pramt = new HashMap<String, String>();
			if (!StringUtils.isBlank(ticket)) {
				pramt.put("timestamp", timstr);
				Logger.info("nostr："+nostr);
				Logger.info("timstr:"+timstr);
				Logger.info("url:"+urls);
				Logger.info("ticket:"+ticket);
				
				pramt.put("noncestr", nostr);
				pramt.put("jsapi_ticket", ticket);
				pramt.put("url", urls);
				sign = StringUtil.getShareSign(pramt);
				Logger.info("sign:"+sign);
			}
			WxSign wxsign = new WxSign();
			
			wxsign.setNostr(nostr);
			wxsign.setTimstr(timstr);
			wxsign.setAppId(Constants.WXappID);
			wxsign.setSharecontent("棒棒糖生活");
			wxsign.setSharetitle("棒棒生活");
			wxsign.setShareimg("");
			wxsign.setShareurl(StringUtil.getDomainH5()+"/user/index");
			wxsign.setSign(sign);
			return wxsign;
		}
		/*
		 * 微信基础鉴权
		 */
		public static JsonNode getwxtoken(String code,String state) {
			if (StringUtils.isBlank(state))
				state = "";

			if (code == null || code.equals("") || StringUtils.isBlank(state)) {
				// 用户取消或景鉴权失败
				return null;
			}

			String access_token = "";
			String openid = "";
			String unionid = "";
			String refresh_token = "";
			// 先进行基础授权，如果是第一次没有用户妮称等信息则进行用户级授权
			// 微信第二步认证
			String wxurl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
					+ Constants.WXappID
					+ "&secret="
					+ Constants.WXappsecret
					+ "&code=" + code + "&grant_type=authorization_code";
			try {
				// 发送请求，返回json
				TenpayHttpClient httpClient = new TenpayHttpClient();
				httpClient.setReqContent(wxurl);
				String resContent = "";
				if (httpClient.callHttpPost(wxurl, "")) {
					resContent = httpClient.getResContent();
					Logger.info("weixin第二步返回" + resContent);
					JsonNode json = Json.parse(resContent);
					try {
						if (!StringUtils.isBlank(json.get("openid").asText())) {
							// 更新application值.
							openid = json.get("openid").asText();
						}
						if (!StringUtils.isBlank(json.get("unionid").asText())) {
							unionid = json.get("unionid").asText();
						}
						if (!StringUtils.isBlank(json.get("access_token").asText()))
							access_token = json.get("access_token").asText();
						if (!StringUtils
								.isBlank(json.get("refresh_token").asText()))
							refresh_token = json.get("refresh_token").asText();
					} catch (Exception e) {
					}
					access_token = access_token == null ? "" : access_token;
				}
			} catch (Exception e) {
			}
			if (!StringUtils.isBlank(access_token)) {
				// 微信鉴权延时
				String wxrefuressurl = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid="
						+ Constants.WXappID
						+ "&grant_type=refresh_token&refresh_token="
						+ refresh_token;
				try {
					HttpGet getr = new HttpGet(wxrefuressurl);
					HttpClient clientcr = new DefaultHttpClient();
					String resContentir = "";
					HttpResponse rescr = clientcr.execute(getr);
					if (rescr.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						resContentir = EntityUtils.toString(rescr.getEntity(),
								"UTF-8");
						Logger.info("调用getRefreshAccessToken 接口返回报文内容:"
								+ resContentir);
						JsonNode jsons = Json.parse(resContentir);
						if (!StringUtils
								.isBlank(jsons.get("access_token").asText())) {
							access_token = jsons.get("access_token").asText();
						}
					}
				} catch (Exception e) {
				}

				ObjectNode result = Json.newObject();
				result.put("access_token", access_token);
				result.put("code", code);
				result.put("state", state);
				result.put("openid", openid);
				result.put("unionid", unionid);
				result.put("timstr", RandomStringUtils.randomAlphanumeric(16));
				result.put("nostr", Sha1Util.getTimeStamp());
				result.put("refresh_token", refresh_token);


				result.put("uid", "0");
				
				if (!StringUtils.isBlank(openid)) {
					cache.setWithOutTime("wx_access_token_voke" + openid,
							access_token, 5400);
					cache.setWithOutTime("wxaddress" + openid,
							openid + "," + unionid + "," + access_token + ","
									+ result.get("timstr").textValue() + ","
									+ result.get("nostr").textValue() + "," + code
									+ "," + state, 5400);
				}
				return Json.toJson(result);
			}
			return null;
		}
		/**
		 * 
		 * <p>Title: dealWithContent</p> 
		 * <p>Description: 处理微信发送的信息，关键字</p> 
		 * @param fromUserName
		 * @param toUserName
		 * @param content
		 * @return
		 */
		public String dealWithContent(String fromUserName, String toUserName,
				String content) {
			WxUser wxuser = WxUserService.getInstance().getWxUserByopenid(fromUserName);
			StringBuffer sb = new StringBuffer();
			String fromUid = wxuser.getFromuid();
			String redirectUrl = StringUtil.getDomainH5();
			String wxMallUrl = "";
			String wxOrderUrl = "";
			String wxTodayUrl = "";
			if(Strings.isNullOrEmpty(fromUid)){
				fromUid = getConfigUid();
				wxOrderUrl = getWxOauthUrl("order");
				wxMallUrl = redirectUrl+"/H5/shoplist";
				String defaultPidToday=Configuration.root().getString("wx.default.defaultPidToday", "4155");
				wxTodayUrl = redirectUrl+"/H5/product?pid="+defaultPidToday;
			}else{
				wxMallUrl = getWxOauthUrl("mall");
				wxOrderUrl = getWxOauthUrl("order");
				wxTodayUrl = getWxOauthUrl("today");
			}
			sb.append("<xml><ToUserName><![CDATA[").append(fromUserName).append("]]></ToUserName><FromUserName><![CDATA[").append(toUserName).append("]]></FromUserName><CreateTime>12345678</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[");
			if(content.indexOf("小嗨")>-1){
				sb.append("哇塞，发私信给我是不是想勾搭我呀？想多多了解我、问我问题，直接点击：").append(
					"\n<a href=\"").append(wxOrderUrl).append("\" >看看我都买了啥？东西到哪了？</a>\n").append(
					"<a href=\"").append(wxTodayUrl).append("\" >今日该抢什么</a>\n").append(
					"<a href=\"").append(wxMallUrl).append("\" >还有哪些好东西</a>\n").append(
					"或者点击\n<a href=\"").append("http://mp.weixin.qq.com/s?__biz=MzA4NTQ4ODY3Nw==&mid=400666606&idx=1&sn=11a409b9f3742871bb1a72c9d483d77f#rd").append("\" >我这么有魅力，不想了解我？</a>");
			}else if (content.indexOf("您好")>-1||content.indexOf("你好")>-1||content.indexOf("嗨")>-1||content.indexOf("Hi")>-1||content.indexOf("我")>-1||content.indexOf("？")>-1||content.indexOf("?")>-1){
				sb.append("哇塞，发私信给我是不是想勾搭我呀？想多多了解我、问我问题，直接点击：").append(
					"\n<a href=\"").append(wxOrderUrl).append("\" >看看我都买了啥？东西到哪了？</a>\n").append(
					"<a href=\"").append(wxTodayUrl).append("\" >今日该抢什么</a>\n").append(
					"<a href=\"").append(wxMallUrl).append("\" >还有哪些好东西</a>\n").append(
					"或者点击\n<a href=\"").append("http://mp.weixin.qq.com/s?__biz=MzA4NTQ4ODY3Nw==&mid=400666606&idx=1&sn=11a409b9f3742871bb1a72c9d483d77f#rd").append("\" >我这么有魅力，不想了解我？</a>");
			}else if (content.indexOf("订单")>-1||content.indexOf("查询")>-1){
				sb.append("MUA~感谢您对我们的支持哦！您可直接点击：\n<a href=\"").append(wxOrderUrl).append("\" >看看我都买了啥？东西到哪了？</a>");
			}else if (content.indexOf("客服")>-1||content.indexOf("售后")>-1){
				sb.append("哎呀~是不是想调戏我啊？\n可直接发私信给我哦，或者请拨打电话 4009616909");
			}else if (content.indexOf("特价")>-1||content.indexOf("优惠")>-1||content.indexOf("秒杀")>-1){
				sb.append("嘿嘿，你的小心思我还不懂吗~直接点击\n<a href=\"").append(wxTodayUrl).append("\">今日该抢什么？</a>\n买对不买贵，一般人我不告诉他，哈哈。");
			}else if (content.indexOf("化妆品")>-1||content.indexOf("零食")>-1||content.indexOf("生鲜")>-1){
				sb.append("发现喜欢的了吗？ 我来帮你一起找哦~请点击：\n<a href=\"").append(wxMallUrl).append("\" >微商城</a>\n发现更多海淘好物~");
			}else {
				sb.append("亲爱的，对不起~您的意思我还不太懂，您可回复【小嗨】了解更多哦~");
			}
			sb.append("]]></Content></xml>");
			String test = sb.toString();//回复的内容
			System.out.println("replay info:"+test);
			return test;
		}

		/**
		 * 
		 * <p>Title: getCommonPushText</p> 
		 * <p>Description: 用户关注后推送消息</p> 
		 * @param wxuser
		 * @param fromUserName
		 * @param toUserName
		 * @return
		 */
		public String getCommonPushText(String fromUid, String fromUserName,
				String toUserName) {

			UserInfo user=UserService.instance.getUserByopenid(fromUserName);
	    	/*long cid = 0l;
	    	if(!StringUtils.isBlank(fromUid)){
	    		cid = productService.getPadChannelCidWithUserid(Numbers.parseLong(fromUid, 0l));
	    	}*/
	    	//String pidFirst=Configuration.root().getString("wx.default.pidFirst", "4940");
	    	//String eidFirst = productService.findPadChannelProEidByUseridAndPid(strUid,pidFirst);
	    	//String urlFirst =StringUtil.getAPIDomain()+"/sheSaid/show?uid="+strUid+"&pid="+pidFirst+"&daiyanid="+eidFirst+"&wx=0";
	    	//需变更关注后回复内容，若指定商品详情，参考
	    	//String kdyShopUrl = StringUtil.getDomainH5()+"/H5/prolist?cid="+cid; //快递员商城url
	    	String kdyShopUrl = StringUtil.getDomainH5()+"/H5/prolist?uid="+fromUid; //快递员商城url
	    	String titleStr1 = "您有一张优惠券未领取";
	    	String contentStr1 = "我是快递员"+user.getNickName()+"，欢迎光临我的微信小店";
	    	String contentStr2 = "看看都有什么特价商品吧";
	    	String titleImg = StringUtil.getWeChatImg("headerimg_1208");
	    	String titleurl = StringUtil.getDomainH5()+"/bbt/preadv?uid="+fromUid;	//查找用户所关注的快递员商城的首页频道id，即
	    	String contentUrl1 = "http://mp.weixin.qq.com/s?__biz=MzA4NTQ4ODY3Nw==&mid=401547190&idx=1&sn=42d4127fc534e0f1181efb7e3ef80bd9#rd";
	    	String contentUrl2 = kdyShopUrl;
	    	
	        String test = "<xml>" +
	                "<ToUserName><![CDATA["+fromUserName+"]]></ToUserName>" +
	                "<FromUserName><![CDATA["+toUserName+"]]></FromUserName>" +
	                "<CreateTime>12345678</CreateTime>" +
	                "<MsgType><![CDATA[news]]></MsgType>" +
	                "<ArticleCount>3</ArticleCount>" +
	                "<Articles>" +
	                "<item>" +
	                "<Title><![CDATA["+titleStr1+"]]></Title>" +
	                "<Description><![CDATA[]]></Description>" +
	                "<PicUrl><![CDATA["+titleImg+"]]></PicUrl>" +
	                "<Url><![CDATA["+titleurl+"]]></Url>" +
	                "</item>" +
	                "<item>" +
	                "<Title><![CDATA["+contentStr1+"]]></Title>" +
	                "<Description><![CDATA[]]></Description>" +
	                "<PicUrl><![CDATA["+StringUtil.getWeChatImg("te")+"]]></PicUrl>" +
	                "<Url><![CDATA["+contentUrl1+"]]></Url>" +
	                "</item>" +
	                "<item>" +
	                "<Title><![CDATA["+contentStr2+"]]></Title>" +
	                "<Description><![CDATA[]]></Description>" +
	                "<PicUrl><![CDATA["+StringUtil.getWeChatImg("hui")+"]]></PicUrl>" +
	                "<Url><![CDATA["+contentUrl2+"]]></Url>" +
	                "</item>" +
	                "</Articles>" +
	                "</xml>";
	        logger.info(test);
	        return test;
		}	
		
		public static WxSign getcacheWxsign(String sessionstr) {
			String adt = cache.get("wxauth" + sessionstr);
			WxSign addrSign = new WxSign();
			if (!StringUtils.isBlank(adt)) {
				
				String[] tmp = adt.split(",");
				addrSign.setOpenid(tmp[0]);
				addrSign.setUnionid(tmp[1]);
				addrSign.setAccess_token(tmp[2]);
				addrSign.setTimstr(tmp[3]);
				addrSign.setNostr(tmp[4]);
				addrSign.setCode(tmp[5]);
				addrSign.setState(tmp[6]);
				addrSign.setAppId(Constants.WXappID);
				return addrSign;
			}
			addrSign.setAppId(Constants.WXappID);
			return addrSign;
		}
}
