package controllers.balance;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;

import models.Postcompany;
import models.postcontent.Postcontent;
import models.postcontent.PostcontentImg;
import models.postman.Balance;
import models.postman.BalanceWithdraw;
import models.postman.PostmanUser;

import com.avaje.ebean.PagedList;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;

import form.BalanceWithdrawForm;
import form.DateBetween;
import form.PostmanUserForm;
import play.Configuration;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.twirl.api.Html;
import services.PostcompanyService;
import services.ServiceFactory;
import services.SmsService;
import services.balance.BalanceService;
import services.cache.ICacheService;
import services.postmanuser.PostmanUserService;
import utils.AjaxHelper;
import utils.Constants;
import utils.Dates;
import utils.Numbers;
import utils.OSSUtils;
import utils.StringUtil;
import vo.BalanceWithdrawVo;

/**
 * 
 * <p>Title: BalanceController.java</p> 
 * <p>Description: 体现管理</p> 
 * <p>Company: higegou</p> 
 * @author  ctt
 * date  2015年11月19日  下午12:18:47
 * @version
 */
public class BalanceOmsController extends Controller {
	public static final Logger.ALogger logger = Logger.of(BalanceOmsController.class);
	
	ICacheService cache = ServiceFactory.getCacheService();
	private static final BalanceService balanceService = ServiceFactory.getBalanceService(); 
	private static final PostmanUserService postmanUserService = ServiceFactory.getPostmanUserService();
	private static final SmsService smsService = ServiceFactory.getSmsService();
	private final Form<BalanceWithdrawForm> balanceWithdrawForm = Form.form(BalanceWithdrawForm.class);
	/**
	 * 
	 * <p>Title: list</p> 
	 * <p>Description: 任务信息列表</p> 
	 * @return
	 */
	public Result balanceWithdrawManage(){
		Form<BalanceWithdrawForm> form = balanceWithdrawForm.bindFromRequest();
		BalanceWithdrawForm formPage = new BalanceWithdrawForm();
        if (!form.hasErrors()) {
        	formPage = form.get();
        }
        formPage = dealFormPageWithKeyword(formPage);
        formPage = dealFormWithDatebetween(formPage);
        //根据现有条件获取提现总量
        int totals = balanceService.getTotalsWithForm(formPage);
        //根据现有条件获取订单集合,分页结果
        List<BalanceWithdrawVo> balanceWithdrawVoList = balanceService.queryBalanceWithdrawList(formPage);
        int totalPage = (int) (totals%formPage.size==0?totals/formPage.size:totals/formPage.size+1);
		return ok(views.html.balance.balanceWithdrawManage.render(totals,formPage.page,totalPage,balanceWithdrawVoList,Html.apply(Constants.BalanceWithdrawSta.stas2HTML(formPage.sta)),formPage));
	}
	
	/**
	 * 
	 * <p>Title: dealFormWithDatebetween</p> 
	 * <p>Description: 针对查询时间做的调整</p> 
	 * @param formPage
	 * @return
	 */
	private BalanceWithdrawForm dealFormWithDatebetween(
			BalanceWithdrawForm form) {
		if(form.between==null){
			DateBetween between = new DateBetween();
			String startDate = Dates.SevenBeforeformatEngLishDateTime(new Date());
			between.start=Dates.parseDate(startDate);
			between.end=new Date();
			form.between = between;
		}
		return form;
	}

	/**
	 * 
	 * <p>Title: dealFormPageWithKeyword</p> 
	 * <p>Description: 处理用户针对关键字的搜索</p> 
	 * @param formPage
	 * @return
	 */
	private BalanceWithdrawForm dealFormPageWithKeyword(BalanceWithdrawForm formPage) {
		if(!Strings.isNullOrEmpty(formPage.keyword)){
			//检查关键字是否为手机号
			if(StringUtil.checkPhone(formPage.keyword)){
				formPage.phone=formPage.keyword;
			}else{
				formPage.nickname=formPage.keyword;
				formPage.alipayAccount=formPage.keyword;
			}
		}
		return formPage;
	}
	
	/**
	 * 
	 * <p>Title: changePostcontentSta</p> 
	 * <p>Description:用户处理提现操作 </p> 
	 * @return
	 */
	public Result changeBalanceWithdrawSta(){
		String id = AjaxHelper.getHttpParam(request(), "id");//
		String sta = AjaxHelper.getHttpParam(request(), "sta");//
		//提现成功
		dealBalanceWithdrawWithChangeSta(id,sta);
		//提现成功无需操作
		return redirect("/balance/balanceWithdrawManage");
	}
	/**
	 * 
	 * <p>Title: changePostcontentSta</p> 
	 * <p>Description:用户通过提现申请操作 </p> 
	 * @return
	 */
	public Result piliangChangeBalanceWithdrawSta(){
		String bwdids = AjaxHelper.getHttpParam(request(), "bwdids");//
		String sta = AjaxHelper.getHttpParam(request(), "sta");//
		if(!Strings.isNullOrEmpty(bwdids)){
			String[] puid = bwdids.split(",");
			for (String id : puid) {
				dealBalanceWithdrawWithChangeSta(id,sta);
			}
		}
		return redirect("/balance/balanceWithdrawManage");
	}
	
	/**
	 * 
	 * <p>Title: dealBalanceWithdrawWithChangeSta</p> 
	 * <p>Description: 更新提现申请状态</p> 
	 * @param id
	 * @param sta
	 */
	private void dealBalanceWithdrawWithChangeSta(String id, String sta) {
		//提现成功
		BalanceWithdraw balanceWithdraw = balanceService.findBalanceWithdrawById(Numbers.parseLong(id, Long.valueOf(0)));
		balanceWithdraw.setSta(sta);
		balanceWithdraw.setDateUpd(new Date());
		balanceWithdraw.setRemark("提现金额："+balanceWithdraw.getAmount()/100+"元, 提现成功时间："+Dates.formatDateTime(new Date()));
		balanceWithdraw.setOper("systemuser");
		balanceWithdraw.setOperRemark("付款成功");
		balanceWithdraw = balanceService.updateBalanceWithdraw(balanceWithdraw);
		//发送审批成功短信给用户
		PostmanUser postmanUser = postmanUserService.findPostmanUserById(balanceWithdraw.getUid());
		//提现成功：【棒棒糖】您提现的1500元款项将在1-5个工作日汇入您的支付宝账户，如有任何问题请联系400-961-6090
		DecimalFormat df = new DecimalFormat("#.00");
		String smsargs = Configuration.root().getString("balancewithdraw.success.smsargs")+df.format(balanceWithdraw.getAmount()/100.0);
		String tpl_id = Configuration.root().getString("balancewithdraw.success.tpl_id");
		smsService.saveSmsInfo(smsargs,postmanUser.getPhone(), tpl_id, "1");
	}

	/**
	 * 
	 * <p>Title: backBalanceWithdraw</p> 
	 * <p>Description:驳回提现申请 </p> 
	 * @return
	 */
	public Result backBalanceWithdraw(){
		String id = AjaxHelper.getHttpParam(request(), "backId");//
		String reason = AjaxHelper.getHttpParam(request(), "reason");//
		if(!Strings.isNullOrEmpty(id)&&!Strings.isNullOrEmpty(reason)){
			if("其他".equals(reason)){
				reason = AjaxHelper.getHttpParam(request(), "reasoncontent");
			}
			BalanceWithdraw balanceWithdraw = balanceService.findBalanceWithdrawById(Numbers.parseLong(id, Long.valueOf(0)));
			balanceWithdraw.setSta("3");
			balanceWithdraw.setDateUpd(new Date());
			balanceWithdraw.setOperRemark(reason);
			balanceWithdraw.setRemark("提现金额："+balanceWithdraw.getAmount()/100+"元, 提现驳回时间："+Dates.formatDateTime(new Date()));
			balanceWithdraw.setOper("systemuser");
			balanceWithdraw = balanceService.updateBalanceWithdraw(balanceWithdraw);
			//发送审核失败给用户
			PostmanUser postmanUser = postmanUserService.findPostmanUserById(balanceWithdraw.getUid());
			//提现失败：【棒棒糖】您的提现申请被驳回（由于xxx）
			String smsargs = Configuration.root().getString("balancewithdraw.failed.smsargs")+balanceWithdraw.getOperRemark();
			String tpl_id = Configuration.root().getString("balancewithdraw.failed.tpl_id");
			smsService.saveSmsInfo(smsargs,postmanUser.getPhone(), tpl_id, "1");
			//更改balance表
			Balance balance = balanceService.findBalanceByUid(balanceWithdraw.getUid());
			if(balance!=null){
				balance.setCanuse(balance.getCanuse()+balanceWithdraw.getAmount());
				balance.setBalance(balance.getBalance()+balanceWithdraw.getAmount());
				balance.setWithdraw(balance.getWithdraw()-balanceWithdraw.getAmount());
				balance.setDateUpd(new Date());
				balanceService.update(balance);
			}
		}
		return redirect("/balance/balanceWithdrawManage");
	}
	
	/**
	 * 
	 * <p>Title: exportBalanceWithdraw</p> 
	 * <p>Description: 导出提现结果</p> 
	 * @return
	 */
	public Result exportBalanceWithdraw(){
		Form<BalanceWithdrawForm> form = balanceWithdrawForm.bindFromRequest();
		BalanceWithdrawForm formPage = new BalanceWithdrawForm();
        if (!form.hasErrors()) {
        	formPage = form.get();
        }
        formPage = dealFormPageWithKeyword(formPage);
        formPage = dealFormWithDatebetween(formPage);
		formPage.page=0;
		formPage.size=999999999;
		// 根据现有条件获取提现申请集合,分页结果
		List<BalanceWithdrawVo> balanceWithdrawVoList = balanceService.queryBalanceWithdrawList(formPage);
		File downFile = balanceService.exportFile(balanceWithdrawVoList);
		String fileNameChine = "快递员提现记录"+formPage.between.toSimpleString()+".xls";
		try {
			if (request().getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
				fileNameChine = "=?UTF-8?B?" + (new String(Base64.encodeBase64(fileNameChine.getBytes("UTF-8")))) + "?=";    
	        } else{
	        	fileNameChine = java.net.URLEncoder.encode(fileNameChine, "UTF-8");
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
		response().setHeader("Content-disposition","attachment;filename=" + fileNameChine);
		response().setContentType("application/msexcel");
		return ok(downFile);
	}
}
