package controllers.h5;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import models.postman.BalanceIncome;
import models.postman.BalanceWithdraw;
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
import utils.BeanUtils;
import utils.Constants;
import utils.Constants.PostmanStatus;
import utils.Numbers;
import utils.WSUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

/**
 * H5任务相关
 * @author luobotao
 * @Date 2015年11月17日
 */
public class BalanceController extends Controller {
	private static final Logger.ALogger logger = Logger.of(BalanceController.class);
	ICacheService cache = ServiceFactory.getCacheService();
	/**
     * 收支明细
     * @return
     */
    public Result balanceList() {
    	int page = 0;
    	String token=request().cookie("token")==null?"P754d4a38cac835e21a1f0b788ca4d748":request().cookie("token").value();
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
    	JsonNode resultWithdraw = WSUtils.getResponseAsJson(Configuration.root().getString("bbt.api.url")+"/api/user_withdrawPage?token="+token+"&page="+page);
    	logger.info(resultWithdraw+"====resultWithdraw====="+page);
    	int pageTotalWithdraw = 0;
    	List<BalanceWithdraw> balanceWithdrawList = Lists.newArrayList();
    	if("1".equals(resultWithdraw.get("status").asText())){
    		pageTotalWithdraw = Numbers.parseInt(resultWithdraw.get("pageTotal").asText(), 0);
    		Iterator<JsonNode> balanceWithdrawJsonIt = resultWithdraw.get("balanceWithdrawList").elements();
    		while(balanceWithdrawJsonIt.hasNext()){
    			BalanceWithdraw balanceWithdraw =BeanUtils.castEntityFromJsonNode(balanceWithdrawJsonIt.next(), BalanceWithdraw.class);
    			balanceWithdrawList.add(balanceWithdraw);
    		}
    	}
    	JsonNode resultIncome = WSUtils.getResponseAsJson(Configuration.root().getString("bbt.api.url")+"/api/user_incomePage?token="+token+"&page="+page);
    	logger.info(resultIncome+"=====resultIncome===="+page);
    	int pageTotalIncome = 0;
    	List<BalanceIncome> balanceIncomeList = Lists.newArrayList();
    	if("1".equals(resultIncome.get("status").asText())){
    		pageTotalIncome = Numbers.parseInt(resultIncome.get("pageTotal").asText(), 0);
    		Iterator<JsonNode> balanceIncomeJsonIt = resultIncome.get("balanceIncomeList").elements();
    		while(balanceIncomeJsonIt.hasNext()){
    			BalanceIncome balanceIncome =BeanUtils.castEntityFromJsonNode(balanceIncomeJsonIt.next(), BalanceIncome.class);
    			balanceIncomeList.add(balanceIncome);
    		}
    	}
    	
    	Reddot reddot = H5Service.getReddotByUid(postmanid);
    	if(reddot==null){
    		reddot = new Reddot();
    		reddot.setDateNew(new Date());
    		reddot.setDateUpd(new Date());
    		reddot.setUpgrade("0");
    		reddot.setMyfav("0");
    		reddot.setUid(postmanid);
    	}
    	reddot.setWallet_incoming("0");
		reddot.setWallet_withdraw("0");
    	H5Service.saveReddot(reddot);
        return ok(views.html.h5.balance.balanceList.render(token,balanceWithdrawList,pageTotalWithdraw,page,balanceIncomeList,pageTotalIncome));
    }
    
    
    /**
     * 提现明细 json调用
     * @return
     */
    public Result balanceWithdrawListJson() {
    	ObjectNode result = Json.newObject();
    	int page =Numbers.parseInt(request().getQueryString("page"), 0)+1;
    	String token=request().getQueryString("token");
    	JsonNode resultWithdraw = WSUtils.getResponseAsJson(Configuration.root().getString("bbt.api.url")+"/api/user_withdrawPage?token="+token+"&page="+page);
    	logger.info(resultWithdraw+"====resultWithdraw====="+page);
    	if("1".equals(resultWithdraw.get("status").asText())){
    		Iterator<JsonNode> balanceWithdrawJsonIt = resultWithdraw.get("balanceWithdrawList").elements();
    		StringBuilder htmlstr=new StringBuilder();
    		while(balanceWithdrawJsonIt.hasNext()){
    			BalanceWithdraw balanceWithdraw =BeanUtils.castEntityFromJsonNode(balanceWithdrawJsonIt.next(), BalanceWithdraw.class);
    			htmlstr.append("<a href='/H5/scheduleInfo/"+balanceWithdraw.getId()+"'>");
    			htmlstr.append("<li data-id='"+balanceWithdraw.getId()+"'>");
    			htmlstr.append("<span class=\"fl\">提现金额："+utils.Numbers.intToStringWithDiv(balanceWithdraw.getAmount(), 100)+"元<br><b>"+utils.Dates.formatDateTime(balanceWithdraw.getDateNew())+"</b></span>");
    			htmlstr.append("<em class=\"fr\">");
    			String msg = "";
                  if("1".equals(balanceWithdraw.getSta())){
                	  msg = "处理中";
                  }
                  if("2".equals(balanceWithdraw.getSta())){
                	  msg = "提现成功";
                  }
                  if("3".equals(balanceWithdraw.getSta())){
                	  msg = "提现失败";
                  }
                  htmlstr.append(msg);
                  htmlstr.append("</em><i class=\"ico-next\"></i></li></a>");
    		}
    		result.put("htmlstr", htmlstr.toString());
    		result.put("page", page);
    		if(StringUtils.isBlank(htmlstr.toString())){
    			result.put("status", "0");
    		}else{
    			result.put("status", "1");
    		}
    		return ok(Json.toJson(result));
    	}
		result.put("status", "0");
		return ok(Json.toJson(result));
    }
    /**
     * 收支明细 json调用
     * @return
     */
    public Result balanceIncomeListJson() {
    	ObjectNode result = Json.newObject();
    	int page =Numbers.parseInt(request().getQueryString("page"), 0)+1;
    	String token=request().getQueryString("token");
    	JsonNode resultIncome = WSUtils.getResponseAsJson(Configuration.root().getString("bbt.api.url")+"/api/user_incomePage?token="+token+"&page="+page);
    	logger.info(resultIncome+"=====resultIncome===="+page);
    	if("1".equals(resultIncome.get("status").asText())){
    		Iterator<JsonNode> balanceIncomeJsonIt = resultIncome.get("balanceIncomeList").elements();
    		StringBuilder htmlstr=new StringBuilder();
			while (balanceIncomeJsonIt.hasNext()) {
				BalanceIncome balanceIncome = BeanUtils.castEntityFromJsonNode(balanceIncomeJsonIt.next(),
						BalanceIncome.class);
				htmlstr.append("<li data-id='" + balanceIncome.getId() + "'>");
				htmlstr.append("<span class=\"fl\">");
				htmlstr.append(balanceIncome.getTitle());
				htmlstr.append("<br><b>");
				htmlstr.append(utils.Dates.formatDateTime(balanceIncome.getDateNew()));
				htmlstr.append("</b></span><em ");
				String classStr = "class=\"fr color-blue\">+";
				if (balanceIncome.getAmount() > 0) {
					classStr = "class=\"fr color-blue\">+";
				} else {
					classStr = "class=\"fr color-red\">";
				}
				htmlstr.append(classStr);
				htmlstr.append(utils.Numbers.intToStringWithDiv(balanceIncome.getAmount(), 100) + "元 </em></li>");
			}
			result.put("htmlstr", htmlstr.toString());
			result.put("page", page);
    		if(StringUtils.isBlank(htmlstr.toString())){
    			result.put("status", "0");
    		}else{
    			result.put("status", "1");
    		}
    		return ok(Json.toJson(result));
    	}
    	result.put("status", "0");
		return ok(Json.toJson(result));
    }
    
    
    /**
     * 提现进度详情
     * @return
     */
    public Result scheduleInfo(int id) {
    	BalanceWithdraw balanceWithdraw = H5Service.getBalanceIncomeById(id);
    	PostmanUser postmanUser = H5Service.getPostManUserById(balanceWithdraw.getUid());
    	return ok(views.html.h5.balance.scheduleInfo.render(balanceWithdraw,postmanUser));
    }
   

}
