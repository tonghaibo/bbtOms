package utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import play.Configuration;
import play.Logger;
import play.mvc.Http.Request;
import services.ServiceFactory;
import services.cache.ICacheService;
import utils.wx.MD5Util;

/**
 * String加密与解密
 * 
 * @author luobotao Date: 2015年4月14日 下午5:44:40
 */
public class StringUtil {
	private static final Logger.ALogger LOGGER = Logger.of(StringUtil.class);
	private static final SimpleDateFormat CHINESE_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy年MM月dd日HHmm");
	private static final SimpleDateFormat CHINESE_DATE_WithOutYear_FORMAT = new SimpleDateFormat("MM月dd日HH:mm");
	private static final SimpleDateFormat CHINESE_TIME_FORMAT = new SimpleDateFormat("HH:mm");
	private static final Pattern PHONE_PATTERN = java.util.regex.Pattern.compile("^((1))\\d{10}$");
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$");
	private static ICacheService cache = ServiceFactory.getCacheService();
	/**
	 * 生成随机数
	 * 
	 * @return
	 */
	public static String genRandomCode(int Length) {
		String key = "";
		for (int i = 0; i < Length; i++) {
			key += Math.round(Math.random() * 9); // 生成验证码随机数
		}
		return key;
	}

	/**
	 * 
	 * @param text
	 *            目标字符串
	 * @param length
	 *            截取长度
	 * @param encode
	 *            采用的编码方式
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String substring(String text, int length, String encode) {
		if (text == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		int currentLength = 0;
		for (char c : text.toCharArray()) {
			try {
				currentLength += String.valueOf(c).getBytes(encode).length;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (currentLength <= length) {
				sb.append(c);
			} else {
				break;
			}
		}
		return sb.toString();
	}

	/**
	 * 获取IP
	 * @param request
	 * @return
	 */
	public static String getIpAddr(Request request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.remoteAddress();
		}
		return ip;
	}

	/**
	 * 生成MD5串
	 * @param str
	 * @return
	 */
	public static String getMD5(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			String result = MD5(str, md);
			return result;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String MD5(String strSrc, MessageDigest md) {
		byte[] bt = strSrc.getBytes();
		md.update(bt);
		String strDes = bytes2Hex(md.digest()); // to HexString
		return strDes;
	}

	public static String bytes2Hex(byte[] bts) {
		StringBuffer des = new StringBuffer();
		String tmp = null;
		for (int i = 0; i < bts.length; i++) {
			tmp = (Integer.toHexString(bts[i] & 0xFF));
			if (tmp.length() == 1) {
				des.append("0");
			}
			des.append(tmp);
		}
		return des.toString();
	}

	public static String MD5Encode(String origin) {
		String resultString = null;

		try {
			resultString = new String(origin);
			MessageDigest md = MessageDigest.getInstance("MD5");
			resultString = byteArrayToString(md.digest(resultString.getBytes()));
		} catch (Exception ex) {

		}
		return resultString;
	}

	/**
	 * 转换字节数组为16进制字串
	 * 
	 * @param b
	 *            字节数组
	 * @return 16进制字串
	 */
	public static String byteArrayToString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			// resultSb.append(byteToHexString(b[i]));//若使用本函数转换则可得到加密结果的16进制表示，即数字字母混合的形式
			resultSb.append(byteToNumString(b[i]));// 使用本函数则返回加密结果的10进制数字字串，即全数字形式
		}
		return resultSb.toString();
	}

	private static String byteToNumString(byte b) {

		int _b = b;
		if (_b < 0) {
			_b = 256 + _b;
		}

		return String.valueOf(_b);
	}


	// 计算时间1分钟内，x秒前/1⼩小时内，x分钟前/24⼩小时内，x⼩小时前/48⼩小时内，昨天 hh:mm/超过48⼩小时，M⽉月d⽇日HH:MM
	public static String getfomatdate(Date date1, Date date2) {
		if (date1 == null)
			date1 = new Date();
		if (date2 == null)
			date2 = new Date();

		long temp = date2.getTime() - date1.getTime(); // 相差毫秒数
		long hours = temp / 1000 / 3600; // 相差小时数
		long temp2 = temp % (1000 * 3600);
		double mins = ((double) temp2 / 1000 / 60); // 相差分钟数
		long ss = temp / 1000;

		String out = "";

		if (hours > 48)
			out = CHINESE_DATE_WithOutYear_FORMAT.format(date1);
		else {
			if (hours > 24)
				out = "昨天" + CHINESE_TIME_FORMAT.format(date1);
			else {
				if (hours > 1)
					out = String.valueOf(hours).toString() + "小时前";
				else {
					if (mins > 1)
						out = (int) mins + "分钟前";
					else
						out = String.valueOf(ss) + "秒前";
				}
			}
		}
		return out;
	}

	// 格式化数量
	public static String formatnum(Long nums) {
		if (nums > 10000) {
			if (nums <= 100000)
				return (nums / 10000) + "万";
			else {
				if (nums >= 10000000)
					return (nums / 10000000) + "百万";
				else
					return (nums / 1000000) + "万";
			}
		}
		return nums.toString();
	}

	public static String filterString(String str) {
		String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	

	/**
	 * 验证MD5串是否正确
	 * @param deviceId
	 * @param md5str
	 * @return
	 */
	public static boolean checkMd5(String deviceId, String md5str) {
		try {
			/* 获取deviceid 0、8、2、9 字符串 */
			String secretStr = deviceId.substring(0, 1) + deviceId.substring(8, 9) + deviceId.substring(2, 3)
					+ deviceId.substring(9, 10);
			String str = getMD5(deviceId + secretStr);
			LOGGER.info(str);
			System.out.println(str);
			if (str.equals(md5str)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			return false;
		}

	}

	/**
	 * 验证是否电话
	 * 
	 * @param phone
	 * @return
	 */
	public static boolean checkPhone(String phone) {
		if (!PHONE_PATTERN.matcher(phone).matches()) {
			return false;
		} else {
			return true;
		}
	}
	/**
	 * 验证检验码是否正确
	 * @param phone
	 * @param verifyCode
	 * @return
	 */
	public static boolean checkVerifyCode(String phone,String verifyCode) {
		if("1111".equals(verifyCode)){//永远成功，测试专用
			return true;
		}
		String verifyCodeInCache = cache.get(Constants.cache_verifyCode+phone);
    	if(StringUtils.isBlank(verifyCodeInCache)||!verifyCodeInCache.equals(verifyCode)){
			return false;
    	}else{
    		return true;
    	}
	}

	/*
	 * 产生比例随机数,比例最大的放在最前面List<String> String 为 “key_couponId_限制数量_出现比例”
	 */
	public static String getRandomNum(List<String> numsmap, Integer numscale) {
		Integer rn = 0;
		if (numsmap == null || numsmap.size() == 0)
			return "";

		Random rm = new Random();
		Integer rdm = Math.abs(rm.nextInt(numscale));

		Integer tmpn = Integer.valueOf(numsmap.get(0).substring(numsmap.get(0).lastIndexOf("_")));
		Integer etmpn = Integer.valueOf(numsmap.get(0).substring(numsmap.get(0).lastIndexOf("_")));
		if (rdm >= 1 && rdm <= Integer.valueOf(numsmap.get(0).substring(numsmap.get(0).lastIndexOf("_"))))
			return numsmap.get(0);
		else {
			for (int n = 1; n < numsmap.size() - 1; n++) {
				etmpn = tmpn + Numbers.parseInt(numsmap.get(n).substring(numsmap.get(n).lastIndexOf("_")), 1);
				if (rdm >= tmpn && rdm <= etmpn)
					return numsmap.get(n);
				tmpn = etmpn;
			}
		}

		return numsmap.get(0);
	}

	/**
	 * 组装签名字符串,供OMS使用
	 * @param sortMap
	 * @return
	 */
	public static String makeSig(Map<String, String> sortMap) {
		StringBuilder sb = new StringBuilder();
			Object[] keys = sortMap.keySet().toArray();
			Arrays.sort(keys);
			for (int i = 0; i < keys.length; i++) {
				String mapkey = (String) keys[i];
				if (i == keys.length - 1) {// 拼接时，不包括最后一个&字符
					sb.append(mapkey).append("=").append(sortMap.get(mapkey));// QSTRING_EQUAL为=,QSTRING_SPLIT为&
				} else {
					sb.append(mapkey).append("=").append(sortMap.get(mapkey))
							.append("&");
				}
			}
			String data = sb.toString();// 参数拼好的字符串			
			
			System.out.println("加密参数为：" + data);
			return data;
	}

	/**
	 * 获取BUCKET_NAME
	 * @return
	 */
	public static String getBUCKET_NAME(){
		String BUCKET_NAME=Configuration.root().getString("oss.bucket.name.higouOMSDev", "test-higou-oms");
		boolean IsProduct = Configuration.root().getBoolean("production", false);
		if(IsProduct){
			BUCKET_NAME=Configuration.root().getString("oss.bucket.name.higouOMSProduct", "higou-oms");
		}
		return BUCKET_NAME;
	}
	/**
	 * 获取嗨购商城的H5 url
	 * @return
	 */
	public static String getHigegouH5Url(){
		String higeouH5=Configuration.root().getString("higegou.h5.dev", "http://h5dev.higegou.com");
		boolean IsProduct = Configuration.root().getBoolean("production", false);
		if(IsProduct){
			higeouH5=Configuration.root().getString("higegou.h5.product", "http://h5.higegou.com");
		}
		return higeouH5;
	}
	/**
	 * 获取嗨购商城的后台 url
	 * @return
	 */
	public static String getHigegouOMSUrl(){
		String higeouOMS=Configuration.root().getString("higegou.oms.dev", "http://oms.higegou.com:9200");
		boolean IsProduct = Configuration.root().getBoolean("production", false);
		if(IsProduct){
			higeouOMS=Configuration.root().getString("higegou.oms.product", "http://oms.higegou.com");
		}
		return higeouOMS;
	}
	
	/**
	 * 传入支付宝账户信息对其进行加*号
	 * @param str
	 * @return
	 */
	public static String getSecretAliacount(String str) {
        Matcher matcher = EMAIL_PATTERN.matcher(str);
        if (matcher.matches()) {
        	int index = str.indexOf("@");
        	if(index>3){
        		str = str.substring(0,index-3)+"***"+str.substring(index);
        	}
            return str;
        }
        matcher = PHONE_PATTERN.matcher(str);
        if (matcher.matches()) {
        		str = str.substring(0,3)+"****"+str.substring(7);
            return str;
        }
        int size = str.length();
    	if(size>5){
    		str = str.substring(0, str.length()-4)+"***"+str.substring(str.length()-1);
    	}
        return str;
    }
	
	public static void main(String[] args) {
		System.out.println(getSecretAliacount("18612asdf119582"));
	}
	
	 public static String doHttpGet(String urlString)
     {
		 HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(urlString);
			get.setHeader("Content-Type", "text/xml; charset=UTF-8");
			try {
				
				HttpResponse res = client.execute(get);
				String strResult = EntityUtils.toString(res.getEntity(),
						"UTF-8");

				Logger.info("接收到的报文:" + strResult);
				return strResult;
			}
			catch(Exception e){
				return "";
			}			
     }
		/*
		 * 获取域名
		 */
		public static String getDomainH5(){
			boolean IsProduct = Configuration.root().getBoolean("production", false);
			//String domains = "http://ht2.neolix.cn:9004";//Configuration.root().getString("domain.dev","http://ht2.neolix.cn:9004");
			String domains="http://h5dev.higegou.com";
			if(IsProduct){
				domains = "http://h5.higegou.com";
			}
			return domains;
		}
		// 组装微信支付签名
  		public static String getSign(Map<String, String> map) {

  			String str = makeSig(map);
  			str = str + "&key=" + Constants.WXappsecretpay;
  			try{
  			MessageDigest md5 = MessageDigest.getInstance("MD5");
  			str=MD5Util.MD5Encode(str, "UTF-8");
  			}
  			catch(Exception e){
  				str="";
  			}
  			return str.toUpperCase();
  		}
  	//组装分享
	  	public static String getShareSign(Map<String,String> map){
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
	  	/*生成微信地址签名*/
  		public static String getWXaddressSign(String token,String url,String timstr,String nostr){
  			Map<String, String> pramt = new HashMap<String, String>();
			pramt.put("accesstoken",token);
			pramt.put("appid",Constants.WXappID);
			pramt.put("url", url);
			pramt.put("timestamp", timstr);
			pramt.put("noncestr",nostr);
			return getShareSign(pramt);
  		}	  	
	  	public static String byteToHex(final byte[] hash) {
  	        Formatter formatter = new Formatter();
  	        for (byte b : hash)
  	        {
  	            formatter.format("%02x", b);
  	        }
  	        String result = formatter.toString();
  	        formatter.close();
  	        return result;
  	    }
	  	public static String BaiduMD5(String md5){
			try {
	            java.security.MessageDigest md = java.security.MessageDigest
	                            .getInstance("MD5");
	            byte[] array = md.digest(md5.getBytes());
	            StringBuffer sb = new StringBuffer();
	            for (int i = 0; i < array.length; ++i) {
	                    sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
	                                    .substring(1, 3));
	            }
	            return sb.toString();
		    } catch (java.security.NoSuchAlgorithmException e) {
		    }
		    return null;
		}
		// 对Map内所有value作utf8编码，拼接返回结果
	    public static String toQueryString(Map<?, ?> data) throws UnsupportedEncodingException
	    {
	            StringBuffer queryString = new StringBuffer();
	            for (Entry<?, ?> pair : data.entrySet())
	            {
	                    queryString.append(pair.getKey() + "=");
	                    queryString.append(URLEncoder.encode((String) pair.getValue(), "UTF-8") + "&");
	            }
	            if (queryString.length() > 0)
	            {
	                    queryString.deleteCharAt(queryString.length() - 1);
	            }
	            return queryString.toString();
	    }	
		/**
		 * 获取微信关注后的首页返回图片
		 * @return
		 */
		public static String getWeChatImg(){
			String weChatImg=Configuration.root().getString("wx.wechat.img.dev", "http://apitestpic.higegou.com/images/wechat/wechatindex.jpg@400w");
			boolean IsProduct = Configuration.root().getBoolean("production", false);
			if(IsProduct){
				weChatImg=Configuration.root().getString("wx.wechat.img.product", "http://apipic.higegou.com/images/wechat/wechatindex.jpg@400w");
			}
			return weChatImg;
		}
		/**
		 *  获取微信关注后的“好”图片
		 * @return
		 */
		public static String getWeChatImg(String imgName){
			String weChatImg="http://apipic.higegou.com/upload/h5/weixinMall/"+imgName+".jpg";
			return weChatImg;
		}
		/**
		 * 获取微信关注后的“特”图片
		 * @return
		 */
		public static String getWeChatTeImg(){
			String weChatImg=Configuration.root().getString("wx.wechat.imgTe.dev", "http://apitestpic.higegou.com/images/wechat/te.jpg");
			boolean IsProduct = Configuration.root().getBoolean("production", false);
			if(IsProduct){
				weChatImg=Configuration.root().getString("wx.wechat.imgTe.product", "http://apipic.higegou.com/images/wechat/te.jpg");
			}
			return weChatImg;
		}
		/**
		 *  获取微信关注后的“惠”图片
		 * @return
		 */
		public static String getWeChatHuiImg(){
			String weChatImg=Configuration.root().getString("wx.wechat.imgHui.dev", "http://apitestpic.higegou.com/images/wechat/hui.jpg");
			boolean IsProduct = Configuration.root().getBoolean("production", false);
			if(IsProduct){
				weChatImg=Configuration.root().getString("wx.wechat.imgHui.product", "http://apipic.higegou.com/images/wechat/hui.jpg");
			}
			return weChatImg;
		}	    
}
