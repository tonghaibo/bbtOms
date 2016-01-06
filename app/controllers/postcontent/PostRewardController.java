package controllers.postcontent;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.avaje.ebean.PagedList;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import com.sun.jndi.toolkit.ctx.Continuation;

import form.PostRewardForm;
import form.PostcontentForm;
import models.postcontent.PostReward;
import models.postcontent.Postcontent;
import models.postcontent.PostcontentDetail;
import models.postcontent.PostcontentImg;
import play.Configuration;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.twirl.api.Html;
import services.ServiceFactory;
import services.cache.ICacheService;
import services.postcontent.PostRewardService;
import services.postcontent.PostcontentService;
import utils.AjaxHelper;
import utils.Constants;
import utils.Numbers;
import utils.OSSUtils;
import utils.StringUtil;

/**
 * 
 * <p>Title: PostRewardController.java</p> 
 * <p>Description: 翻牌任务设置</p> 
 * <p>Company: higegou</p> 
 * @author  ctt
 * date  2015年11月21日  下午12:43:07
 * @version
 */
public class PostRewardController extends Controller {
	public static final Logger.ALogger logger = Logger.of(PostRewardController.class);
	
	ICacheService cache = ServiceFactory.getCacheService();
	private static final PostcontentService postcontentService = ServiceFactory.getPostcontentService(); 
	private static final PostRewardService postRewardService = ServiceFactory.getPostRewardService(); 
	
	private final Form<PostRewardForm> postRewardForm = Form.form(PostRewardForm.class);
	/**
	 * 
	 * <p>Title: list</p> 
	 * <p>Description: 任务信息列表</p> 
	 * @return
	 */
	public Result postRewardManage(){
		Form<PostRewardForm> form = postRewardForm.bindFromRequest();
		PostRewardForm formPage = new PostRewardForm();
		logger.info("log page1:"+form.toString());
        if (!form.hasErrors()) {
        	formPage = form.get();
        }
		PagedList<PostReward> postRewardPage = postRewardService.findPostRewardList(formPage);
		return ok(views.html.postcontent.postRewardManage.render(postRewardPage,Html.apply(Constants.PostcontentTyps.typs2HTML(formPage.typ))));
	}
	
	/**
	 * 跳转至新增任务页面
	 * @return
	 */
	public Result newOrUpdatePostReward(){
		Long id = Numbers.parseLong(Form.form().bindFromRequest().get("id"), Long.valueOf(0));
		PostReward postReward = new PostReward();
		if(id!=0L){
			postReward = postRewardService.findPostRewardById(id);
			if(postReward.getCommisiontyp()==2){
				postReward.setCommision(postReward.getCommision()-100);
			}
		}
		return ok(views.html.postcontent.newOrUpdatePostReward.render(id,postReward));
    }
	
	/**
	 * 保存任务信息
	 * @return
	 */
	public Result savePostReward(){
		Long id = Numbers.parseLong(Form.form().bindFromRequest().get("id"), Long.valueOf(0));
		String typ = Form.form().bindFromRequest().get("typ");//
		String commision = Form.form().bindFromRequest().get("commision");//
		String commisiontyp = Form.form().bindFromRequest().get("commisiontyp");//
		String randomval = Form.form().bindFromRequest().get("randomval");//
		String congratulation = Form.form().bindFromRequest().get("congratulation");//
		String remarktxt = Form.form().bindFromRequest().get("remarktxt");//
		String ruletxt = Form.form().bindFromRequest().get("ruletxt");//
		String flg = Form.form().bindFromRequest().get("flg");//
		PostReward postReward = new PostReward();
		if(id!=0L){
			postReward = postRewardService.findPostRewardById(id);
			typ = postReward.getTyp()+"";
			flg = String.valueOf(postReward.getFlg());
			postReward = dealPostReward(postReward,typ,commision,commisiontyp,randomval,congratulation,remarktxt,ruletxt,flg);
			postReward.setDateUpd(new Date());
			postReward = postRewardService.update(postReward);
		}else{
			postReward = dealPostReward(postReward,typ,commision,commisiontyp,randomval,congratulation,remarktxt,ruletxt,flg);
			postReward.setFlg(2);
			//设置图标
			postReward = postRewardService.save(postReward);
		}
		return redirect("/postcontent/postRewardManage");
	}
	/**
	 * 
	 * <p>Title: changePostRewardSta</p> 
	 * <p>Description:更改任务状态 </p> 
	 * @return
	 */
	public Result changePostRewardFlg(){
		String id = AjaxHelper.getHttpParam(request(), "id");//
		String flg = AjaxHelper.getHttpParam(request(), "flg");//
		PostReward postReward = postRewardService.findPostRewardById(Numbers.parseLong(id, Long.valueOf(0)));
		postReward.setFlg(Numbers.parseInt(flg, 0));
		postReward.setDateUpd(new Date());
		postReward = postRewardService.update(postReward);
		return redirect("/postcontent/postRewardManage");
	}
	
	private PostReward dealPostReward(PostReward postReward, String typ,
			String commision, String commisiontyp, String randomval,
			String congratulation, String remarktxt, String ruletxt, String flg) {
		postReward.setTyp(Numbers.parseInt(typ, 0));
		if("2".equals(commisiontyp)){
			postReward.setCommision(Numbers.parseInt(commision, 0)+100);
		}else{
			postReward.setCommision(Numbers.parseInt(commision, 0));
		}
		postReward.setCommisiontyp(Numbers.parseInt(commisiontyp, 0));
		postReward.setRandomval(Numbers.parseDouble(randomval, 0.0));
		postReward.setCongratulation(congratulation);
		postReward.setRemarktxt(remarktxt);
		postReward.setRuletxt(ruletxt);
		postReward.setFlg(Numbers.parseInt(flg, 0));
		return postReward;
	}
	
}
