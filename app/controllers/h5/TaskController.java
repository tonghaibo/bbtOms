package controllers.h5;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import models.magazine.MagazineUser;
import models.postcontent.PostReward;
import models.postcontent.Postcontent;
import models.postcontent.PostcontentDetail;
import models.postcontent.PostcontentImg;
import models.postcontent.PostcontentUser;
import models.postman.PostmanUser;
import models.postman.Reddot;

import org.apache.commons.lang3.StringUtils;

import play.Configuration;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.ServiceFactory;
import services.cache.ICacheService;
import services.h5.H5Service;
import services.wx.WxService;
import services.magazine.MagazineService;
import services.postcontent.PostcontentService;
import utils.Constants;
import utils.Constants.PostmanStatus;
import utils.AjaxHelper;
import utils.ErrorCode;
import utils.FileUtils;
import utils.Numbers;
import utils.OSSUtils;
import utils.StringUtil;
import utils.WSUtils;

import com.avaje.ebean.PagedList;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


/**
 * H5任务相关
 * @author luobotao
 * @Date 2015年11月17日
 */
public class TaskController extends Controller {
	private static final ICacheService cache = ServiceFactory.getCacheService();
	private static final PostcontentService postcontentService = ServiceFactory.getPostcontentService(); 
	private static final Logger.ALogger logger = Logger.of(TaskController.class);

    /**
     * 首单任务
     * @return
     */
    public Result firstOrder() {
    	ObjectNode result = Json.newObject();
    	String token=request().cookie("token")==null?"":request().cookie("token").value();
    	if(StringUtils.isBlank(token)){
    		return ok(views.html.h5.task.tokenError.render());
		}
    	int postmanid = 0;
    	PostmanUser postmanuser = new PostmanUser();
    	if(StringUtils.isBlank(cache.get(token))){
    		postmanuser=H5Service.getPostManUserByToken(token);
    		if(postmanuser==null){//用户信息不存在
        		return ok(views.html.h5.task.tokenError.render());
        	}else{
        		postmanid = postmanuser.getId();
        		cache.set(token, token);//写入token
        		cache.set(Constants.cache_postmanid+token, String.valueOf(postmanuser.getId()));//根据token找ID
        		cache.set(Constants.cache_tokenBypostmanid+postmanuser.getId(), token);//根据ID找token
        	}
    	}else{
    		postmanid = Numbers.parseInt(cache.get(Constants.cache_postmanid+token), 0);
    		postmanuser=H5Service.getPostManUserById(postmanid);
    	}
    	if(postmanuser==null){//用户信息不存在
    		return ok(views.html.h5.task.tokenError.render());
    	}
    	if(PostmanStatus.COMMON.getStatus()!=Numbers.parseInt(postmanuser.getSta(), 0)){
    		return ok(views.html.h5.userStaError.render());
    	}
    	String taskid=request().getQueryString("taskid")==null?"":request().getQueryString("taskid");
    	Postcontent postcontent = PostcontentService.getInstance().findPostcontentById(Numbers.parseLong(taskid, 0L));
    	if(postcontent!=null && Constants.PostcontentStas.VALID.getStatus()!=Numbers.parseInt(postcontent.getSta(), 0)){
    		return ok(views.html.h5.contentStaError.render());
    	}
        return ok(views.html.h5.task.firstOrder.render(postmanuser.getShopurl()));
    }
    /**
     * 新闻任务
     * @return
     */
    public Result news() {
    	String taskid=request().getQueryString("taskid")==null?"":request().getQueryString("taskid");
    	Postcontent postcontent = PostcontentService.getInstance().findPostcontentById(Numbers.parseLong(taskid, 0L));
    	PostcontentDetail contentDetail = postcontentService.findPostcontentDetailByPcid(Numbers.parseInt(taskid, 0));
        return ok(views.html.h5.news.render(postcontent,contentDetail));
    }

    /**
     * 延迟
     * @return
     */
    public Result timeDelay(int wait) {
logger.info(wait+"============");
		try {
			Thread.currentThread().sleep(100*wait);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return ok(Json.toJson("1"));
    }
    /**
     * 新闻列表
     * @return
     */
    public Result newsList() {
    	int page = 0;
    	int pageSize=5;
    	PagedList<Postcontent> postcontentList = H5Service.getPostcontent(page,pageSize);//获取有效的新闻
    	for(Postcontent postcontent : postcontentList.getList()){
    		List<PostcontentImg> postcontentImgList = PostcontentService.getInstance().findPostcontentImgByPcid(Long.valueOf(postcontent.getId()));
        	for(PostcontentImg postcontentImg:postcontentImgList){
        		postcontentImg.setMagazinelist(MagazineService.getMagazinelistById(postcontentImg.getMagid()));
        		postcontentImg.setImgurl(Configuration.root().getString("oss.image.url")+postcontentImg.getImgurl());
        	}
        	postcontent.postcontentImgList = postcontentImgList;
    	}
    	
    	return ok(views.html.h5.newsList.render(postcontentList.getTotalPageCount(),(postcontentList.getPageIndex()+1),postcontentList.getList()));
    }
    /**
     * 新闻列表Json
     * @return
     */
    public Result newsListForJson() {
    	int page = Numbers.parseInt(request().getQueryString("page"), 0);
    	int pageSize=5;
    	ObjectNode result = Json.newObject();
    	StringBuilder sb = new StringBuilder();
    	PagedList<Postcontent> postcontentList = H5Service.getPostcontent(page,pageSize);//获取有效的新闻
    	for(Postcontent postcontent : postcontentList.getList()){
    		sb.append("<a href=\"/H5/news?taskid="+postcontent.getId()+"\">");
    		sb.append("<li><h2>");
    		sb.append(postcontent.getTitle());
    		sb.append("</h2>");
    		sb.append("<div class=\"news-pic\">");
    		List<PostcontentImg> postcontentImgList = PostcontentService.getInstance().findPostcontentImgByPcid(Long.valueOf(postcontent.getId()));
    		for(PostcontentImg postcontentImg:postcontentImgList){
    			postcontentImg.setImgurl(Configuration.root().getString("oss.image.url")+postcontentImg.getImgurl());
    			sb.append("<img src=\""+postcontentImg.getImgurl()+"\" />");
    		}
    		sb.append("<p>"+postcontent.getContent()+"</p>");
    		sb.append("</div>");
    		sb.append("<div class=\"news-Time\">"+utils.Dates.formatDateTime(postcontent.getDateNew())+"</div>");
    		sb.append("</li></a>");
    	}
    	result.put("status", "1");
    	result.put("htmlstr", sb.toString());
    	result.put("pageCur", page+1);
    	return ok(Json.toJson(result));
    }
    /**
     * 销售任务
     * @return
     */
    public Result sale() {
    	ObjectNode result = Json.newObject();
    	String token=request().cookie("token")==null?"":request().cookie("token").value();
    	if(StringUtils.isBlank(token)){
    		return ok(views.html.h5.task.tokenError.render());
		}
    	int postmanid = 0;
    	PostmanUser postmanuser = new PostmanUser();
    	if(StringUtils.isBlank(cache.get(token))){
    		postmanuser=H5Service.getPostManUserByToken(token);
    		if(postmanuser==null){//用户信息不存在
        		return ok(views.html.h5.task.tokenError.render());
        	}else{
        		postmanid = postmanuser.getId();
        		cache.set(token, token);//写入token
        		cache.set(Constants.cache_postmanid+token, String.valueOf(postmanuser.getId()));//根据token找ID
        		cache.set(Constants.cache_tokenBypostmanid+postmanuser.getId(), token);//根据ID找token
        	}
    	}else{
    		postmanid = Numbers.parseInt(cache.get(Constants.cache_postmanid+token), 0);
    		postmanuser=H5Service.getPostManUserById(postmanid);
    	}
    	if(postmanuser==null){//用户信息不存在
    		return ok(views.html.h5.task.tokenError.render());
    	}
    	if(PostmanStatus.COMMON.getStatus()!=Numbers.parseInt(postmanuser.getSta(), 0)){
    		return ok(views.html.h5.userStaError.render());
    	}
    	String taskid=request().getQueryString("taskid")==null?"":request().getQueryString("taskid");
    	Postcontent postcontent = PostcontentService.getInstance().findPostcontentById(Numbers.parseLong(taskid, 0L));
    	if(postcontent!=null && Constants.PostcontentStas.VALID.getStatus()!=Numbers.parseInt(postcontent.getSta(), 0)){
    		return ok(views.html.h5.contentStaError.render());
    	}
    	return ok(views.html.h5.task.sale.render(postmanuser.getShopurl()));
    }
    /**
     * weixin关注任务
     * @return
     */
    public Result weixin() {
    	ObjectNode result = Json.newObject();
    	String token=request().cookie("token")==null?"":request().cookie("token").value();
    	if(StringUtils.isBlank(token)){
    		return ok(views.html.h5.task.tokenError.render());
		}
    	int postmanid = 0;
    	PostmanUser postmanuser = new PostmanUser();
    	if(StringUtils.isBlank(cache.get(token))){
    		postmanuser=H5Service.getPostManUserByToken(token);
    		if(postmanuser==null){//用户信息不存在
        		return ok(views.html.h5.task.tokenError.render());
        	}else{
        		postmanid = postmanuser.getId();
        		cache.set(token, token);//写入token
        		cache.set(Constants.cache_postmanid+token, String.valueOf(postmanuser.getId()));//根据token找ID
        		cache.set(Constants.cache_tokenBypostmanid+postmanuser.getId(), token);//根据ID找token
        	}
    	}else{
    		postmanid = Numbers.parseInt(cache.get(Constants.cache_postmanid+token), 0);
    		postmanuser=H5Service.getPostManUserById(postmanid);
    	}
    	if(postmanuser==null){//用户信息不存在
    		return ok(views.html.h5.task.tokenError.render());
    	}
    	if(PostmanStatus.COMMON.getStatus()!=Numbers.parseInt(postmanuser.getSta(), 0)){
    		return ok(views.html.h5.userStaError.render());
    	}
    	String taskid=request().getQueryString("taskid")==null?"":request().getQueryString("taskid");
    	Postcontent postcontent = PostcontentService.getInstance().findPostcontentById(Numbers.parseLong(taskid, 0L));
    	if(postcontent!=null && Constants.PostcontentStas.VALID.getStatus()!=Numbers.parseInt(postcontent.getSta(), 0)){
    		return ok(views.html.h5.contentStaError.render());
    	}
    	//String url=StringUtil.getHigegouH5Url()+"/mall/mallOpened/"+postmanid;
    	//return ok(views.html.h5.task.weixin.render(url));
        return ok(views.html.h5.task.weixin.render(postmanuser.getShopurl()));
    }
    /**
     * 开店任务
     * @return
     */
	public Result expressMall() {
		ObjectNode result = Json.newObject();
		String token = request().cookie("token") == null ? "" : request().cookie("token").value();
		if(StringUtils.isBlank(token)){
    		return ok(views.html.h5.task.tokenError.render());
		}
    	int postmanid = 0;
    	PostmanUser postmanuser = new PostmanUser();
    	if(StringUtils.isBlank(cache.get(token))){
    		postmanuser=H5Service.getPostManUserByToken(token);
    		if(postmanuser==null){//用户信息不存在
        		return ok(views.html.h5.task.tokenError.render());
        	}else{
        		postmanid = postmanuser.getId();
        		cache.set(token, token);//写入token
        		cache.set(Constants.cache_postmanid+token, String.valueOf(postmanuser.getId()));//根据token找ID
        		cache.set(Constants.cache_tokenBypostmanid+postmanuser.getId(), token);//根据ID找token
        	}
    	}else{
    		postmanid = Numbers.parseInt(cache.get(Constants.cache_postmanid+token), 0);
    		postmanuser=H5Service.getPostManUserById(postmanid);
    	}
		if (postmanuser == null) {// 用户信息不存在
			return ok(views.html.h5.task.tokenError.render());
		}
		if(PostmanStatus.COMMON.getStatus()!=Numbers.parseInt(postmanuser.getSta(), 0)){
    		return ok(views.html.h5.userStaError.render());
    	}
		String taskid=request().getQueryString("taskid")==null?"":request().getQueryString("taskid");
    	Postcontent postcontent = PostcontentService.getInstance().findPostcontentById(Numbers.parseLong(taskid, 0L));
    	if(postcontent!=null && Constants.PostcontentStas.VALID.getStatus()!=Numbers.parseInt(postcontent.getSta(), 0)){
    		return ok(views.html.h5.contentStaError.render());
    	}
		int pcid=Numbers.parseInt(taskid, 0);//开店任务的ID
		if(!StringUtils.isBlank(postmanuser.getShopurl())){
			postcontentService.updatePostcontentStaByPcidAndUid(pcid,postmanid);
			postcontentService.updatePostcontentStaByPcidAndUid(pcid,postmanid);//更新任务状态
			H5Service.savePostuserman(postmanuser);
			return redirect(postmanuser.getShopurl());
		}
		ObjectNode req = Json.newObject();
		Long timestamp = System.currentTimeMillis();// 时间戳
		// 设置签名参数
		SortedMap<String, String> signParams = new TreeMap<String, String>();
		signParams.put("phone", postmanuser.getPhone());
		signParams.put("postman_id", String.valueOf(postmanid));
		signParams.put("timestamp", String.valueOf(timestamp));
		String signMine = StringUtil.makeSig(signParams);
		req.put("postman_id", postmanid);// 快递员ID
		req.put("phone", postmanuser.getPhone());// 手机
		req.put("nickname", postmanuser.getNickname());// 昵称
		req.put("timestamp", timestamp);// 时间戳
		req.put("sign", signMine);// 加密
		Logger.info(StringUtil.getHigegouOMSUrl()+"/bbt/expressMall");
		JsonNode response = WSUtils.postByJSON(StringUtil.getHigegouOMSUrl()+"/bbt/expressMall", Json.toJson(req));
		if(response!=null){
			String responseResult = response.get("result")==null?"":response.get("result").asText();
			if("ok".equals(responseResult)){
				postmanuser.setShopurl(response.get("mall_url").asText());
				postcontentService.updatePostcontentStaByPcidAndUid(pcid,postmanid);//更新任务状态
				H5Service.savePostuserman(postmanuser);
				String url=StringUtil.getHigegouH5Url()+"/mall/mallOpened/"+postmanid;
				return redirect(url);
			}
		}
		return ok(Json.toJson(req));
	}
	 /**
     * 翻牌任务
     * @return
     */
    public Result fanpai() {
    	ObjectNode result = Json.newObject();
    	String token=request().cookie("token")==null?"Pf0d05622715776611c92c3ae7bee0d10":request().cookie("token").value();
    	if(StringUtils.isBlank(token)){
    		return ok(views.html.h5.task.tokenError.render());
		}
    	int postmanid = 0;
    	PostmanUser postmanuser = new PostmanUser();
    	if(StringUtils.isBlank(cache.get(token))){
    		postmanuser=H5Service.getPostManUserByToken(token);
    		if(postmanuser==null){//用户信息不存在
        		return ok(views.html.h5.task.tokenError.render());
        	}else{
        		postmanid = postmanuser.getId();
        		cache.set(token, token);//写入token
        		cache.set(Constants.cache_postmanid+token, String.valueOf(postmanuser.getId()));//根据token找ID
        		cache.set(Constants.cache_tokenBypostmanid+postmanuser.getId(), token);//根据ID找token
        	}
    	}else{
    		postmanid = Numbers.parseInt(cache.get(Constants.cache_postmanid+token), 0);
    		postmanuser=H5Service.getPostManUserById(postmanid);
    	}
    	if(postmanuser==null){//用户信息不存在
    		return ok(views.html.h5.task.tokenError.render());
    	}
    	if(PostmanStatus.COMMON.getStatus()!=Numbers.parseInt(postmanuser.getSta(), 0)){
    		return ok(views.html.h5.userStaError.render());
    	}
    	String taskid=request().getQueryString("taskid")==null?"":request().getQueryString("taskid");
    	Postcontent postcontent = PostcontentService.getInstance().findPostcontentById(Numbers.parseLong(taskid, 0L));
    	if(postcontent!=null && Constants.PostcontentStas.VALID.getStatus()!=Numbers.parseInt(postcontent.getSta(), 0)){
    		return ok(views.html.h5.contentStaError.render());
    	}
    	PostcontentUser postcontentUser = H5Service.getPostcontentUserByPcidAndUid(Numbers.parseInt(taskid, 0),postmanid);
    	List<PostcontentImg> postcontentImgList = PostcontentService.getInstance().findPostcontentImgByPcid(Numbers.parseLong(taskid, 0L));
    	for(PostcontentImg postcontentImg:postcontentImgList){
    		postcontentImg.setMagazinelist(MagazineService.getMagazinelistById(postcontentImg.getMagid()));
    		postcontentImg.setImgurl(Configuration.root().getString("oss.image.url")+postcontentImg.getImgurl());
    	}
//    	PostReward postReward = H5Service.getPostReward();
    	return ok(views.html.h5.task.fanpai.render(token,postcontent,postcontentImgList,postcontentUser));
    }
    /**
     * 收藏翻牌任务
     * @return
     */
    public Result fanpaiStore() {
    	ObjectNode result = Json.newObject();
    	String token=request().getQueryString("token")==null?"P5b8bb433712c50ff999ea34a8203b6eb":request().getQueryString("token");
    	Integer postcontentImgId=Numbers.parseInt(request().getQueryString("postcontentImgId"), 0);
    	if(StringUtils.isBlank(token)){
    		result.put("status", ErrorCode.getErrorCode("global.postmanNotExit"));
    		result.put("msg", ErrorCode.getErrorMsg("global.postmanNotExit"));
    		return ok(Json.toJson(result));
		}
    	int postmanid = 0;
    	PostmanUser postmanuser = new PostmanUser();
    	if(StringUtils.isBlank(cache.get(token))){
    		postmanuser=H5Service.getPostManUserByToken(token);
    		if(postmanuser==null){//用户信息不存在
    			result.put("status", ErrorCode.getErrorCode("global.postmanNotExit"));
        		result.put("msg", ErrorCode.getErrorMsg("global.postmanNotExit"));
        		return ok(Json.toJson(result));
        	}else{
        		postmanid = postmanuser.getId();
        		cache.set(token, token);//写入token
        		cache.set(Constants.cache_postmanid+token, String.valueOf(postmanuser.getId()));//根据token找ID
        		cache.set(Constants.cache_tokenBypostmanid+postmanuser.getId(), token);//根据ID找token
        	}
    	}else{
    		postmanid = Numbers.parseInt(cache.get(Constants.cache_postmanid+token), 0);
    		postmanuser=H5Service.getPostManUserById(postmanid);
    	}
    	if(postmanuser==null){//用户信息不存在
    		result.put("status", ErrorCode.getErrorCode("global.postmanNotExit"));
    		result.put("msg", ErrorCode.getErrorMsg("global.postmanNotExit"));
    		return ok(Json.toJson(result));
    	}
    	if(PostmanStatus.COMMON.getStatus()!=Numbers.parseInt(postmanuser.getSta(), 0)){
    		return ok(views.html.h5.userStaError.render());
    	}
    	MagazineUser magazineUser =  MagazineService.getMagaZineUserByMagidAndUid(postcontentImgId,postmanid);
    	if(magazineUser==null){
    		magazineUser = new MagazineUser();
    		magazineUser.setDateNew(new Date());
    		magazineUser.setMagid(postcontentImgId);
    		magazineUser.setUid(postmanid);
    		MagazineService.saveMagazineUser(magazineUser);
    	}
    	Reddot reddot = H5Service.getReddotByUid(postmanid);
    	if(reddot==null){
    		reddot = new Reddot();
    		reddot.setDateNew(new Date());
    		reddot.setDateUpd(new Date());
    		reddot.setUpgrade("0");
    		reddot.setUid(postmanid);
    		reddot.setWallet_incoming("0");
    		reddot.setWallet_withdraw("0");
    	}
    	reddot.setMyfav("1");
    	H5Service.saveReddot(reddot);
    	result.put("status", "1");
		result.put("msg", "收藏成功");
		return ok(Json.toJson(result));
    }
    /**
     * 领取翻牌任务
     * @return
     */
    public Result receiveStore() {
    	ObjectNode result = Json.newObject();
    	String token=request().getQueryString("token");
    	Integer postcontentId=Numbers.parseInt(request().getQueryString("postcontentId"), 0);
    	Integer idContent=Numbers.parseInt(request().getQueryString("idContent"), 0);//翻过的杂志ID
    	Logger.info(idContent+"========================================");
    	if(StringUtils.isBlank(token)){
    		result.put("status", ErrorCode.getErrorCode("global.postmanNotExit"));
    		result.put("msg", ErrorCode.getErrorMsg("global.postmanNotExit"));
    		return ok(Json.toJson(result));
		}
    	int postmanid = 0;
    	PostmanUser postmanuser = new PostmanUser();
    	if(StringUtils.isBlank(cache.get(token))){
    		postmanuser=H5Service.getPostManUserByToken(token);
    		if(postmanuser==null){//用户信息不存在
    			result.put("status", ErrorCode.getErrorCode("global.postmanNotExit"));
        		result.put("msg", ErrorCode.getErrorMsg("global.postmanNotExit"));
        		return ok(Json.toJson(result));
        	}else{
        		postmanid = postmanuser.getId();
        		cache.set(token, token);//写入token
        		cache.set(Constants.cache_postmanid+token, String.valueOf(postmanuser.getId()));//根据token找ID
        		cache.set(Constants.cache_tokenBypostmanid+postmanuser.getId(), token);//根据ID找token
        	}
    	}else{
    		postmanid = Numbers.parseInt(cache.get(Constants.cache_postmanid+token), 0);
    		postmanuser=H5Service.getPostManUserById(postmanid);
    	}
    	if(postmanuser==null){//用户信息不存在
    		result.put("status", ErrorCode.getErrorCode("global.postmanNotExit"));
    		result.put("msg", ErrorCode.getErrorMsg("global.postmanNotExit"));
    		return ok(Json.toJson(result));
    	}
    	if(PostmanStatus.COMMON.getStatus()!=Numbers.parseInt(postmanuser.getSta(), 0)){
    		return ok(views.html.h5.userStaError.render());
    	}
    	PostReward postReward = null ;
    	PostcontentUser postcontentUser =  PostcontentService.getInstance().getPostcontentUserByPcidAndUid(postcontentId,postmanid);
    	if(postcontentUser==null){
    		postcontentUser = new PostcontentUser();
    		postcontentUser.setDateNew(new Date());
    		postcontentUser.setPcid(postcontentId);
    		postcontentUser.setUid(postmanid);
    	}else{
    		int rewardid = postcontentUser.getRewardid();
    		postReward = H5Service.getPostRewardById(rewardid);
    	}
    	if(postReward==null){
    		int randomCode = Numbers.parseInt(StringUtil.genRandomCode(2), 21);
    		postReward = H5Service.getPostRewardByRandomCode(randomCode);
    	}
    	postcontentUser.setRewardid(postReward==null?1:postReward.getId());
    	postcontentUser.setMagid(idContent);
    	postcontentUser.setSta("1");//已领取
    	PostcontentService.getInstance().savePostcontentUser(postcontentUser);
    	result.put("status", "1");
    	result.put("msg", "领取成功");
    	
    	
    	StringBuilder sb = new StringBuilder("<h2><b>");
    	sb.append(postReward.getCongratulation());
    	sb.append("</b><br>");
    	sb.append(postReward.getRemarktxt());
    	sb.append("</h2>");
    	sb.append("<div><p>");
    	sb.append(postReward.getRuletxt());
    	sb.append("</div>");
    	
    	result.put("postReward",sb.toString());
    	return ok(Json.toJson(result));
    }
    /**
     * 获取翻过的杂志ID
     * @return
     */
    public Result getMagaIdOld(){
    	ObjectNode result = Json.newObject();
    	String token=request().getQueryString("token");
    	Integer postcontentId=Numbers.parseInt(request().getQueryString("postcontentId"), 0);
    	if(StringUtils.isBlank(token)){
    		result.put("status", ErrorCode.getErrorCode("global.postmanNotExit"));
    		result.put("msg", ErrorCode.getErrorMsg("global.postmanNotExit"));
    		return ok(Json.toJson(result));
		}
    	int postmanid = 0;
    	PostmanUser postmanuser = new PostmanUser();
    	if(StringUtils.isBlank(cache.get(token))){
    		postmanuser=H5Service.getPostManUserByToken(token);
    		if(postmanuser==null){//用户信息不存在
    			result.put("status", ErrorCode.getErrorCode("global.postmanNotExit"));
        		result.put("msg", ErrorCode.getErrorMsg("global.postmanNotExit"));
        		return ok(Json.toJson(result));
        	}else{
        		postmanid = postmanuser.getId();
        		cache.set(token, token);//写入token
        		cache.set(Constants.cache_postmanid+token, String.valueOf(postmanuser.getId()));//根据token找ID
        		cache.set(Constants.cache_tokenBypostmanid+postmanuser.getId(), token);//根据ID找token
        	}
    	}else{
    		postmanid = Numbers.parseInt(cache.get(Constants.cache_postmanid+token), 0);
    		postmanuser=H5Service.getPostManUserById(postmanid);
    	}
    	if(postmanuser==null){//用户信息不存在
    		result.put("status", ErrorCode.getErrorCode("global.postmanNotExit"));
    		result.put("msg", ErrorCode.getErrorMsg("global.postmanNotExit"));
    		return ok(Json.toJson(result));
    	}
    	if(PostmanStatus.COMMON.getStatus()!=Numbers.parseInt(postmanuser.getSta(), 0)){
    		return ok(views.html.h5.userStaError.render());
    	}
    	PostcontentUser postcontentUser =  PostcontentService.getInstance().getPostcontentUserByPcidAndUid(postcontentId,postmanid);
    	
    	if(postcontentUser!=null){
    		MagazineUser magazineUser =  MagazineService.getMagaZineUserByMagidAndUid(postcontentUser.getMagid(),postmanid);
        	if(magazineUser==null){
        		result.put("storeflag", "0");
        	}else{
        		result.put("storeflag", "1");
        	}
    		result.put("status", "1");
    		result.put("magid", postcontentUser.getMagid());
    		return ok(Json.toJson(result));
    	}else{
    		result.put("status", "0");
    		result.put("msg", "出错了");
    		return ok(Json.toJson(result));
    	}
    }
    
    /*
  	 * 推广特惠商品
  	 */
  	public Result pre(){
  		String token=request().cookie("token")==null?"":request().cookie("token").value();
    	if(StringUtils.isBlank(token)){
    		return ok(views.html.h5.task.tokenError.render());
		}
    	int postmanid = 0;
    	PostmanUser postmanuser = new PostmanUser();
    	if(StringUtils.isBlank(cache.get(token))){
    		postmanuser=H5Service.getPostManUserByToken(token);
    		if(postmanuser==null){//用户信息不存在
        		return ok(views.html.h5.task.tokenError.render());
        	}else{
        		postmanid = postmanuser.getId();
        		cache.set(token, token);//写入token
        		cache.set(Constants.cache_postmanid+token, String.valueOf(postmanuser.getId()));//根据token找ID
        		cache.set(Constants.cache_tokenBypostmanid+postmanuser.getId(), token);//根据ID找token
        	}
    	}else{
    		postmanid = Numbers.parseInt(cache.get(Constants.cache_postmanid+token), 0);
    		postmanuser=H5Service.getPostManUserById(postmanid);
    	}
    	if(postmanuser==null){//用户信息不存在
    		return ok(views.html.h5.task.tokenError.render());
    	}
    	if(PostmanStatus.COMMON.getStatus()!=Numbers.parseInt(postmanuser.getSta(), 0)){
    		return ok(views.html.h5.userStaError.render());
    	}
    	return ok(views.html.h5.task.pre.render(postmanid));
  	}
  	/*
  	 * 生成并显示二维码
  	 */
  	public Result showcode(int postmanid){
  		PostmanUser postmanuser=H5Service.getPostManUserById(postmanid);
    	if(postmanuser==null){//用户信息不存在
    		return ok(views.html.h5.task.tokenError.render());
    	}
    	if(PostmanStatus.COMMON.getStatus()!=Numbers.parseInt(postmanuser.getSta(), 0)){
    		return ok(views.html.h5.userStaError.render());
    	}
  		String shopurl = postmanuser.getShopurl();//是否开店完成
    	if(StringUtils.isBlank(shopurl)){
    		ObjectNode req = Json.newObject();
    		Long timestamp = System.currentTimeMillis();// 时间戳
    		// 设置签名参数
    		SortedMap<String, String> signParams = new TreeMap<String, String>();
    		signParams.put("phone", postmanuser.getPhone());
    		signParams.put("postman_id", String.valueOf(postmanid));
    		signParams.put("timestamp", String.valueOf(timestamp));
    		String signMine = StringUtil.makeSig(signParams);
    		req.put("postman_id", postmanid);// 快递员ID
    		req.put("phone", postmanuser.getPhone());// 手机
    		req.put("nickname", postmanuser.getNickname());// 昵称
    		req.put("timestamp", timestamp);// 时间戳
    		req.put("sign", signMine);// 加密
    		Logger.info(StringUtil.getHigegouOMSUrl()+"/bbt/expressMall");
    		JsonNode response = WSUtils.postByJSON(StringUtil.getHigegouOMSUrl()+"/bbt/expressMall", Json.toJson(req));
    		if(response!=null){
    			String responseResult = response.get("result")==null?"":response.get("result").asText();
    			if("ok".equals(responseResult)){
    				postmanuser.setShopurl(response.get("mall_url").asText());
    				H5Service.savePostuserman(postmanuser);
    				shopurl = postmanuser.getShopurl();
    			}
    		}
    	}
    	String ticket = postmanuser.getSpreadticket();
    	if(StringUtils.isBlank(ticket)){
    		String higouUid = shopurl.replace("http://h5.higegou.com/mall/mallProducts/", "").replace("http://h5dev.higegou.com/mall/mallProducts/", "");
        	logger.info(higouUid);
    		ticket = WxService.getInstance().getQrCodeTicket(higouUid+"_bbtadv");
			String url = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket="+ticket;
			String type=".jpg";
			String BUCKET_NAME=Configuration.root().getString("oss.bucket.name.higouAPIDev", "higou-api");
			boolean IsProduct = Configuration.root().getBoolean("production", false);
			if(IsProduct){
				BUCKET_NAME=Configuration.root().getString("oss.bucket.name.higouAPIProduct", "higou-api");
			}
			String fileName = UUID.randomUUID().toString().replaceAll("-", "")+type;//最终的文件名称
			File localFile = FileUtils.getFileFromUrl(url, fileName);
			String endfilestr = OSSUtils.uploadFile(localFile,"upload/h5/wx/",fileName, type,BUCKET_NAME);
			postmanuser.setSpreadticket(endfilestr);
    		H5Service.savePostuserman(postmanuser);
			ticket = OSSUtils.getOSSUrl()+endfilestr;
		}else{
			ticket = OSSUtils.getOSSUrl()+ticket;
		}
  		return ok(views.html.h5.task.showcode.render(ticket));
  	}
}
