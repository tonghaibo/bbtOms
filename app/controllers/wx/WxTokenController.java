package controllers.wx;


import java.net.URLEncoder;
import java.util.Date;

import models.user.UserInfo;
import models.wx.WxUser;

import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import play.Logger;
import play.Play;
import play.libs.XPath;
import play.mvc.Controller;
import play.mvc.Result;
import services.ServiceFactory;
import services.user.UserService;
import services.wx.WxService;
import services.wx.WxUserService;
import utils.AjaxHelper;
import utils.StringUtil;

import com.google.common.base.Strings;

/**
 * 
 * <p>Title: WxTokenController.java</p> 
 * <p>Description: </p> 
 * <p>Company: higegou</p> 
 * @author  ctt
 * date  2015年10月23日  下午3:44:10
 * @version
 */
public class WxTokenController extends Controller {
	private static final Logger.ALogger logger = Logger.of(WxTokenController.class);
	private static final String token = Play.application().configuration().getString("wx.token");
	private static final WxService wxService = WxService.getInstance(); 
	private static final UserService userService = UserService.instance; 
	/**
	 * 
	 * <p>Title: reply</p> 
	 * <p>Description: 微信验证</p> 
	 * @return
	 */
	public Result reply() throws Exception{
		//微信加密签名
		String signature=AjaxHelper.getHttpParam(request(), "signature");
		//时间戳
		String timestamp=AjaxHelper.getHttpParam(request(), "timestamp");
		//随机数
		String nonce=AjaxHelper.getHttpParam(request(), "nonce");
		nonce=nonce==null?"":nonce;
		logger.info("[get] signature:"+signature);
		logger.info("[get] timestamp:"+timestamp);
		logger.info("[get] nonce:"+nonce);
		logger.info("[get] request:"+request());
		//随机字符串
		String echostr=AjaxHelper.getHttpParam(request(), "echostr");
		echostr=echostr==null?nonce:echostr;
		String encrypt_type=AjaxHelper.getHttpParam(request(), "encrypt_type");
		String msg_signature=AjaxHelper.getHttpParam(request(), "msg_signature");
		logger.info("[get] encrypt_type:"+encrypt_type);
		logger.info("[get] msg_signature:"+msg_signature);
		if(!Strings.isNullOrEmpty(signature)&&!Strings.isNullOrEmpty(timestamp)&&!Strings.isNullOrEmpty(nonce)){
			boolean vertiryWx = wxService.checkSignature(signature, timestamp, nonce);
			if(vertiryWx){
				logger.info("微信申请的服务器地址正确，接入成功success");
				return ok(echostr);
			}else{
				logger.info("微信申请的服务器地址无效，接入失败failed");
				return ok("failed");
			}
		}else {
			return ok("");
		}
	}
	
	/**
	 * 
	 * <p>Title: doWithWx</p> 
	 * <p>Description: 处理微信消息</p> 
	 * @return
	 * @throws Exception 
	 * @throws DocumentException 
	 */
	public Result doWithWx() throws Exception {
		logger.info("body:"+request().body());
		response().setContentType("application/json;charset=utf-8");
		
		Document dom = request().body().asXml();
    	if(dom == null) {
    	    return badRequest("Expecting Xml data");
    	} 
		String toUserName = XPath.selectText("//ToUserName", dom);
		String fromUserName = XPath.selectText("//FromUserName", dom);
		String createTime = XPath.selectText("//CreateTime", dom);
		String msgType = XPath.selectText("//MsgType", dom);
		String event = XPath.selectText("//Event", dom);
		String eventKey = XPath.selectText("//EventKey", dom);
		logger.info("toUserName:"+toUserName);
		logger.info("fromUserName:"+fromUserName);
		logger.info("createTime:"+createTime);
		logger.info("msgType:"+msgType);
		logger.info("event:"+event);
		logger.info("eventKey:"+eventKey);
		String signature=AjaxHelper.getHttpParam(request(), "signature");
		String timestamp=AjaxHelper.getHttpParam(request(), "timestamp");
		String nonce=AjaxHelper.getHttpParam(request(), "nonce");
		logger.info("[post] signature:"+signature);
		logger.info("[post] timestamp:"+timestamp);
		logger.info("[post] nonce:"+nonce);
		/*String encrypt_type=AjaxHelper.getHttpParam(request(), "encrypt_type");
		String msg_signature=AjaxHelper.getHttpParam(request(), "msg_signature");
		logger.info("[post] encrypt_type:"+encrypt_type);
		logger.info("[post] msg_signature:"+msg_signature);
		String str = dealWithCrypt(dom);    	//获取解密后的信息
		boolean vertiryWx = dealWithSign();
		if(!vertiryWx){
			logger.info("微信信息验证失败！！！");
			return ok("");
		}*/
		String  test ="success";
		if("text".equals(msgType)){
			String content = XPath.selectText("//Content", dom);		//文本消息内容
			content = new String(content.getBytes("ISO-8859-1"), "utf-8");
			String msgId = XPath.selectText("//MsgId", dom);			//消息id,64位整型
			logger.info("from user1	:"+fromUserName+", content:"+new String(content.getBytes("ISO-8859-1"), "utf-8"));
			//针对不同的消息进行相应的回复，关键字处理
			test = wxService.dealWithContent(fromUserName, toUserName, content);
			System.out.println("---------------replay info:"+test);
		}else if("event".equals(msgType)){
			//用户取消关注 
			if("unsubscribe".equals(event)){
		    	 Logger.info("higouH5 ==> unsubscribe");
		    	 //取消当前微信用户的fromuid，置为空
		    	 WxUserService.getInstance().updateWxUserWithUnsub(fromUserName);
		     }else if ("subscribe".equals(event)){
		    	 logger.info("higouH5 ==> subscribe");
		    	 //判断用户是否扫码带参数的二维码，ticket记录二维码的图片
		    	 if(!Strings.isNullOrEmpty(eventKey)){
		    		 String ticket = XPath.selectText("//Ticket", dom);
		    		 logger.info("ticket:"+ticket);
		    	 }
		    	 String fromUid = "";
		    	 String concatStr = "";
		    	 if(!Strings.isNullOrEmpty(eventKey)){
		    		 //记录来源的fromuid
		    		 String fromUidStr = eventKey.replace("qrscene_", "");
		    		 String[] fromUidArgs = fromUidStr.split("_");
		    		 logger.info("fromUidArgs length:"+fromUidArgs.length);
		    		 fromUid = fromUidArgs[0];
		    		 if(fromUidArgs.length>1&&!"".equals(fromUidArgs[1])){
		    			 concatStr = fromUidArgs[1];
		    		 }
		    	 }
		    	 WxUser wxuser = WxUserService.getInstance().saveOrUpdateWxUser(fromUserName,fromUid);
		    	 UserInfo user=new UserInfo();
		    	 user=UserService.instance.getUserByopenid(fromUid);
		    	 if(user==null){
		    		 user.setOpenid(fromUid);
		    		 user.setNickName(fromUserName);
		    		 user.setDate_new(new Date());
		    		 UserService.instance.addUser(user);
		    	 }
		    	 //if(!Strings.isNullOrEmpty(eventKey)){
	    		 logger.info("eventKey:"+eventKey+", -----------concatStr:"+concatStr);
	    		 if(Strings.isNullOrEmpty(concatStr)){
	    			 test = wxService.getCommonPushText(fromUid, fromUserName,toUserName);
	    		 }else if("bbtadv".equals(concatStr)){
	    			// test = wxService.getCommonPushBbtadvText(fromUid, fromUserName,toUserName);
	    		 }
		    	 //}
		     }else if("VIEW".equals(event)){ 
		    	 //根据fromUserName(openid)跳转到指定快递员商城或自家商城
		    	 logger.info(eventKey);
		     }else if("CLICK".equals(event)){
		    	 if("tiaoxikefu@higegou".equals(eventKey)){
		    		 test ="<xml><ToUserName><![CDATA["+fromUserName+"]]></ToUserName><FromUserName><![CDATA["+toUserName+"]]></FromUserName><CreateTime>12345678</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[想调戏我可直接发私信给我哦，或者请拨打 409616909]]></Content></xml>";
		    	 }
		     }else if("SCAN".equals(event)){
		    	 logger.info("higouH5 ==> scan subscribe");
		    	 String fromUid = "";
		    	 String concatStr = "";
		    	 if(!Strings.isNullOrEmpty(eventKey)){
		    		 //记录来源的fromuid
		    		 logger.info(eventKey);
		    		 String fromUidStr = eventKey.replace("qrscene_", "");
		    		 String[] fromUidArgs = fromUidStr.split("_");
		    		 fromUid = fromUidArgs[0];
		    		 if(fromUidArgs.length>1&&!"".equals(fromUidArgs[1])){
		    			 concatStr = fromUidArgs[1];
		    		 }
		    		 if(Strings.isNullOrEmpty(concatStr)){
		    			 test = wxService.getCommonPushText(fromUid, fromUserName,toUserName);
		    		 }else if("bbtadv".equals(concatStr)){
		    			// test = wxService.getCommonPushBbtadvText(fromUid, fromUserName,toUserName);
		    		 }
		    	 }
		     }
		}
		
		return ok(test);
	}
	
	/**
	 * 
	 * <p>Title: dealWithSign</p> 
	 * <p>Description: 简单的sign校验</p> 
	 * @return
	 */
	private static boolean dealWithSign() {
		String signature=AjaxHelper.getHttpParam(request(), "signature");
		String timestamp=AjaxHelper.getHttpParam(request(), "timestamp");
		String nonce=AjaxHelper.getHttpParam(request(), "nonce");
		boolean flag = wxService.checkSignature(signature, timestamp, nonce);
		return flag;
	}

	/**
	 * 
	 * <p>Title: dealWithCrypt</p> 
	 * <p>Description: 加解密验证信息是否来自微信</p> 
	 * @param dom
	 * @return
	 */
	private static String dealWithCrypt(Document dom) {
		String signature=AjaxHelper.getHttpParam(request(), "signature");
		String timestamp=AjaxHelper.getHttpParam(request(), "timestamp");
		String nonce=AjaxHelper.getHttpParam(request(), "nonce");
		String encrypt_type=AjaxHelper.getHttpParam(request(), "encrypt_type");
		String msg_signature=AjaxHelper.getHttpParam(request(), "msg_signature");
		DOMImplementationLS domImplLS = (DOMImplementationLS) dom 
                .getImplementation(); 
        LSSerializer serializer = domImplLS.createLSSerializer(); 
        String postData = serializer.writeToString(dom); 
		String str = wxService.checkSignatureWithCrypt(signature, timestamp, nonce,encrypt_type,msg_signature,postData);
		return str;
	}

	/**
	 * 
	 * <p>Title: getWxToken</p> 
	 * <p>Description: 获取到微信token</p> 
	 * @return
	 */
    public Result getAccessToken() {
        return ok(wxService.getAccessToken());
    }

}
