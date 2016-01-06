package services.baidu;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.user.UserAddress;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import play.Logger;
import play.libs.Json;

import com.aliyun.oss.common.comm.ServiceClient.Request;
import com.fasterxml.jackson.databind.JsonNode;

import utils.AjaxHelper;
import utils.Constants;
import utils.StringUtil;
import utils.wx.Sha1Util;
import utils.wx.TenpayHttpClient;

public class BaiduService{
	public static BaiduService instance=new BaiduService();
	private BaiduService(){}
	
	//根据微信经伟度转换百度纬度，经度（x纬度，y经度）
	public Map<String,Double> changelonglat(Double longs,Double lat){
		Map<String,Double> longlat=new HashMap<String,Double>();
		String badulonglaturl="http://api.map.baidu.com/geoconv/v1/";
		//badulonglaturl="http://api.map.baidu.com/geocoder/v2/";
		String coords=longs+","+lat;
		String ak=Constants.BAIDU_AK;//"pSyCHkn8sbkvaAjtkjBFX7Qo";
		String sk=Constants.BAIDU_SK;
		String sn="";
		String from="1";
		String to="5"; 
		Map paramsMap = new LinkedHashMap<String, String>();
        paramsMap.put("coords", coords);
        paramsMap.put("ak", ak);
        paramsMap.put("from", from);
        paramsMap.put("to", to);
//		paramsMap.put("address", "百度大厦");
//		paramsMap.put("output", "json");
//		paramsMap.put("ak", "pSyCHkn8sbkvaAjtkjBFX7Qo");
		
        String tmps="";
        try
        {
        	tmps=StringUtil.toQueryString(paramsMap);
            String wholestr="/geoconv/v1/?"+tmps+sk;
           // wholestr="/geocoder/v2/?"+tmps+"crIuGcKCTd4knsrbw87blCwddunLCAxL";
            String temps=URLEncoder.encode(wholestr,"UTF-8");
        	sn=StringUtil.BaiduMD5(temps);
        }catch(Exception ee){}
        
		//badulonglaturl=badulonglaturl+"?coords="+coords+"&ak="+ak+"&sn="+sn+"&from="+from+"&to="+to;
        badulonglaturl=badulonglaturl+"?"+tmps+"&sn="+sn;
        Logger.info("sn:"+sn);
        Logger.info("baiduurl:"+badulonglaturl);       
		String resContent=StringUtil.doHttpGet(badulonglaturl);
			Logger.info("百度返回："+resContent);
			JsonNode json = Json.parse(resContent);
			String status=json.get("status").asInt()+"";
			if(status.equals("0")){
				JsonNode jsont=json.get("result");
				Logger.info(jsont.findValue("x").asDouble()+"");
				longlat.put("x", jsont.findValue("x").asDouble());
				longlat.put("y",jsont.findValue("y").asDouble());
			}
		
		if(longlat.isEmpty())
			return null;
		return longlat;
	}
	
	//根据地点查询经伟度（模糊查询选第一个结果）
	public UserAddress getlonglatByPlace(String pname,String region){
		UserAddress ad=null;
		String scope="1";
		String page_size="1";
		String page_num="0";
		//String region="北京";
		String bdsearchurl="http://api.map.baidu.com/place/v2/search";
		Map map=new LinkedHashMap<String,String>();
		map.put("q", pname);
		map.put("scope", scope);
		map.put("page_size", page_size);
		map.put("page_num", page_num);
		map.put("region", region);
		map.put("ak", Constants.BAIDU_AK);
		map.put("output", "json");
		try{
			String tmpq=StringUtil.toQueryString(map);
			String temps=URLEncoder.encode("/place/v2/search?"+tmpq+Constants.BAIDU_SK,"UTF-8");
        	String sn=StringUtil.BaiduMD5(temps);
        	
			bdsearchurl=bdsearchurl+"?"+tmpq+"&sn="+sn;
			String rescontent=StringUtil.doHttpGet(bdsearchurl);
			JsonNode json = Json.parse(rescontent);
			String status=json.get("status").asInt()+"";
			if(status.equals("0")){
				ad=new UserAddress();
				JsonNode jsont=json.get("results");
				if(jsont!=null){
					JsonNode jsonl=jsont.findPath("location");
					if(jsonl!=null){
						ad.setLat(jsonl.get("lat").asDouble());
						ad.setLongs(jsonl.get("lng").asDouble());
					}
					ad.setAddress(jsont.findValue("address").asText());					
				}
			}
		}catch(Exception e){}
		return ad;
	}

	//根据地址查询纬度，经度信息
	public Map<String,Double> getlnglatbyaddress(String address,String city){
		Map<String,Double> lnglat=new HashMap<String,Double>();
		String bdurl="http://api.map.baidu.com/geocoder/v2/";
		Map<String,String> qry=new LinkedHashMap<String, String>();
		qry.put("ak", Constants.BAIDU_AK);
		qry.put("output", "json");
		qry.put("callback", "renderOption");
		qry.put("address", address);
		qry.put("city", city);
		
		try{
			String tmp=StringUtil.toQueryString(qry);
			String sn=StringUtil.BaiduMD5(URLEncoder.encode("/geocoder/v2/?"+tmp+Constants.BAIDU_SK));
			bdurl=bdurl+"?"+tmp+"&sn="+sn;
			String resContent=StringUtil.doHttpGet(bdurl);
			Logger.info("百度返回："+resContent);
			resContent=resContent.replace("renderOption&&renderOption(","");
			resContent=resContent.substring(0,resContent.length()-1);
			JsonNode json = Json.parse(resContent);
			String status=json.get("status").asInt()+"";
			if(status.equals("0")){
				JsonNode jsont=json.get("result");
				if(jsont!=null){
					JsonNode jsonl=jsont.findPath("location");
					if(jsonl!=null){
						lnglat.put("x",jsonl.get("lat").asDouble());
						lnglat.put("y",jsonl.get("lng").asDouble());
					}				
				}
			}
		}catch(Exception e){}
		
		return lnglat;
	}
	
	//计算两点间距离（百度经纬度查询)
	public Long getdistanceByPoints(Map<String,Double> begin,Map<String,Double> end,String region){
		String origin=begin.get("x")+","+begin.get("y");
		String destination=end.get("x")+","+end.get("y");
		String timestamp=Sha1Util.getTimeStamp();
		String bdurl="http://api.map.baidu.com/direction/v1";
		Map<String,String> map=new LinkedHashMap<String,String>();
		map.put("ak", Constants.BAIDU_AK);
		map.put("origin",origin);
		map.put("destination", destination);
		map.put("region", region);
		map.put("output", "json");
		map.put("timestamp", timestamp);
		Long distinct=0L;
		try{
			String tmp=StringUtil.toQueryString(map);
			String sn=StringUtil.BaiduMD5(URLEncoder.encode("/direction/v1?"+tmp+Constants.BAIDU_SK));
			bdurl=bdurl+"?"+tmp+"&sn="+sn;
			String resContent=StringUtil.doHttpGet(bdurl);
			JsonNode jsn=Json.parse(resContent);
			String status=jsn.get("status").asText();
			if(status.equals("0")){
				JsonNode jst=jsn.get("result").get("routes");
				if(jst!=null){
					distinct=jst.findValue("distance").asLong();
					Logger.info("distance:"+distinct);
				}
			}
		//	Logger.info("百度查询两点间距离返回："+resContent);
		}catch(Exception e){}
		return distinct;
	}
	
	//计算一点到多个终点的距离，lat,lng以“,”分隔
	public Map<String,Long> getdisPointmore(Map<String,String> beginpoint,Map<String,String> points,String region){
		Map<String,Long> dislist=new HashMap<String,Long>();
		Set keyset=points.keySet();
		Iterator it=keyset.iterator();
		String orign="";
		String endpoints="";
		while(it.hasNext()){
			String tmp=points.get(it.next());
			if(!StringUtils.isBlank(tmp)){
				endpoints=endpoints+tmp+"|";
			}
		}
		keyset=beginpoint.keySet();
		it=keyset.iterator();
		while(it.hasNext()){
			String tmp=beginpoint.get(it.next());
			if(!StringUtils.isBlank(tmp)){
				orign=orign+tmp+"|";
			}
		}
		if(!StringUtils.isBlank(orign))
			orign=orign.substring(0,orign.length()-1);
		if(!StringUtils.isBlank(endpoints))
			endpoints=endpoints.substring(0,endpoints.length()-1);
		
		String bdurl="http://api.map.baidu.com/direction/v1/routematrix";
		Map<String,String> map=new LinkedHashMap<String,String>();
		map.put("ak", Constants.BAIDU_AK);
		map.put("origins", orign);
		map.put("destinations",endpoints);
		map.put("output", "json");
		map.put("timestamp", Sha1Util.getTimeStamp());
		
		try{
			String tmp=StringUtil.toQueryString(map);
			String sn=StringUtil.BaiduMD5(URLEncoder.encode("/direction/v1/routematrix?"+tmp+Constants.BAIDU_SK));
			bdurl=bdurl+"?"+tmp+"&sn="+sn;
			String resContent=StringUtil.doHttpGet(bdurl);
			JsonNode jsn=Json.parse(resContent);
			String status=jsn.get("status").asText();
			if(status.equals("0")){
				JsonNode jsr=jsn.get("result");
				if(jsr!=null){
					List<JsonNode>jslist=jsr.findValues("distance");
					if(jslist!=null && !jslist.isEmpty()){
						int i=0;
						for(JsonNode js:jslist){
							i++;	
							dislist.put("lngcat"+i, js.get("value").asLong());
						}	
					}
				}
			}
		}catch(Exception e){}
		if(dislist.isEmpty())
			return null;
		return dislist;
	}
	
}