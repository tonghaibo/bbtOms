package controllers.postcontent;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
import services.postcontent.PostcontentService;
import utils.AjaxHelper;
import utils.Constants;
import utils.Numbers;
import utils.OSSUtils;
import utils.StringUtil;

import com.avaje.ebean.PagedList;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;

import controllers.admin.AdminAuthenticated;
import form.PostcontentForm;

/**
 * 
 * <p>Title: PostcontentController.java</p> 
 * <p>Description: 任务管理</p> 
 * <p>Company: higegou</p> 
 * @author  ctt
 * date  2015年11月17日  上午11:27:45
 * @version
 */
public class PostcontentController extends Controller {
	public static final Logger.ALogger logger = Logger.of(PostcontentController.class);
	
	ICacheService cache = ServiceFactory.getCacheService();
	private static final PostcontentService postcontentService = ServiceFactory.getPostcontentService(); 
	private final Form<PostcontentForm> postcontentForm = Form.form(PostcontentForm.class);
	/**
	 * 
	 * <p>Title: list</p> 
	 * <p>Description: 任务信息列表</p> 
	 * @return
	 */
	@AdminAuthenticated
	public Result postcontentManage(){
		Form<PostcontentForm> form = postcontentForm.bindFromRequest();
		PostcontentForm formPage = new PostcontentForm();
		logger.info("log page1:"+form.toString());
        if (!form.hasErrors()) {
        	formPage = form.get();
        }
        logger.info("log page2:"+formPage.page);
		PagedList<Postcontent> postcontentPage = postcontentService.findPostcontentList(formPage);
		return ok(views.html.postcontent.postcontentManage.render(postcontentPage,Html.apply(Constants.PostcontentStas.stas2HTML(formPage.sta)),Html.apply(Constants.PostcontentTyps.typs2HTML(formPage.typ))));
	}
	
	/**
	 * 跳转至新增任务页面
	 * @return
	 */
	@AdminAuthenticated
	public Result newOrUpdatePostcontent(){
		Long id = Numbers.parseLong(Form.form().bindFromRequest().get("id"), Long.valueOf(0));
		String activeNum=Form.form().bindFromRequest().get("activeNum")==null?"1":Form.form().bindFromRequest().get("activeNum");
		String src=Form.form().bindFromRequest().get("src")==null?"1":Form.form().bindFromRequest().get("src");//panel来源
		Postcontent postcontent = new Postcontent();
		List<PostcontentImg> postcontentImgList = new ArrayList<PostcontentImg>();
		if(id!=0L){
			postcontent = postcontentService.findPostcontentById(id);
			postcontentImgList = postcontentService.findPostcontentImgByPcid(id);
			src = postcontent.getTyp()+"";
			if(postcontent.getTyp()==5){
				if(!"3".equals(activeNum)){
					activeNum = "2";
				}
				PostcontentDetail postcontentDetail = postcontentService.findPostcontentDetailByPcid(postcontent.getId());
				postcontent.setPostcontentDetail(postcontentDetail);
			}
		}
		return ok(views.html.postcontent.newOrUpdatePostcontent.render(id,postcontent,activeNum,src,postcontentImgList));
    }
	
	/**
	 * 保存任务信息
	 * @return
	 */
	@AdminAuthenticated
	public Result savePostcontent(){
		Long id = Numbers.parseLong(Form.form().bindFromRequest().get("id"), Long.valueOf(0));
		String typ = Form.form().bindFromRequest().get("typ");//
		String typname = Form.form().bindFromRequest().get("typname");//
		String title = Form.form().bindFromRequest().get("title");//
		String content = Form.form().bindFromRequest().get("content");//
		String amount = Form.form().bindFromRequest().get("amount");//
		int expectamount = Numbers.parseInt(Form.form().bindFromRequest().get("expectamount"), 0);//
		String linkurl = Form.form().bindFromRequest().get("linkurl");//
		String dateremark = Form.form().bindFromRequest().get("dateremark");//
		String start_tim = Form.form().bindFromRequest().get("start_tim");//
		String end_tim = Form.form().bindFromRequest().get("end_tim");//
		String nsort = Form.form().bindFromRequest().get("nsort");//
		Postcontent postcontent = new Postcontent();
		if(id!=0L){
			postcontent = postcontentService.findPostcontentById(id);
			typ = postcontent.getTyp()+"";
			postcontent = dealPostcontent(postcontent,typ,typname,title,content,amount,linkurl,dateremark,start_tim,end_tim,nsort,expectamount);
			postcontent.setDateUpd(new Date());
			postcontent = postcontentService.update(postcontent);
			//如果该任务已上线，则需要更新用户任务，调用指定存储过程
			if("1".equals(postcontent.getSta())){
				postcontentService.editInitPostcontent(postcontent);
			}
		}else{
			postcontent = dealPostcontent(postcontent,typ,typname,title,content,amount,linkurl,dateremark,start_tim,end_tim,nsort,expectamount);
			postcontent.setSta("0");
			//设置图标
			postcontent = postcontentService.save(postcontent);
		}
		//签到任务增加对杂志的初始化
		if(postcontent.getTyp()==3&&id==0L){
			logger.info("新建签到任务，初始化杂志信息");
			postcontentService.initMagazineWithPostcontent(postcontent);
		}
		//针对linkurl进行处理
		postcontent = dealLinkurl(postcontent, linkurl);
		return redirect("/postcontent/newOrUpdatePostcontent?id="+postcontent.getId()+"&activeNum="+3+"&src=1");
	}
	
	private Postcontent dealPostcontent(Postcontent postcontent, String typ,
			String typname, String title, String content, String amount,
			String linkurl, String dateremark, String start_tim,
			String end_tim, String nsort,int expectamount) {
		String typeicon = "";
		postcontent.setTyp(Numbers.parseInt(typ, 0));
		switch(typ){
			case "1":
				typeicon = Configuration.root().getString("postcontent_daily_the_task","/upload/homeImg/daily_the_task.png");
				break;
			case "2":
				typeicon = Configuration.root().getString("postcontent_reward_the_task","/upload/homeImg/reward_the_task.png");
				break;
			case "3":
				typeicon = Configuration.root().getString("postcontent_turn_the_task","/upload/homeImg/sign_the_task.png");
				break;
			/*case "4":
				typeicon = Configuration.root().getString("postcontent_sys_the_task","/upload/homeImg/sys_the_task.png");
				break;
			case "5":
				typeicon = Configuration.root().getString("postcontent_news_the_task","/upload/homeImg/news_the_task.png");
				break;
			default:
				typeicon = Configuration.root().getString("postcontent_daily_the_task","/upload/homeImg/daily_the_task.png");
				break;*/
		}
		postcontent.setTypicon(typeicon);
		postcontent.setTypname(typname);
		postcontent.setTitle(title);
		postcontent.setContent(content);
		postcontent.setAmount(amount);
		postcontent.setLinkurl(linkurl);
		postcontent.setDateremark(dateremark);
		postcontent.setStart_tim(start_tim);
		postcontent.setEnd_tim(end_tim);
		postcontent.setNsort(Integer.parseInt(nsort));
		postcontent.setExpectamount(expectamount);
		return postcontent;
	}

	/**
	 * 
	 * <p>Title: dealLinkurl</p> 
	 * <p>Description: 处理linkurl</p> 
	 * @param postcontent 
	 * @param linkurl
	 * @return
	 */
	private Postcontent dealLinkurl(Postcontent postcontent, String linkurl) {
		if(!Strings.isNullOrEmpty(linkurl)&&!linkurl.contains("taskid")){
			if(linkurl.contains("?")){
				if(linkurl.contains("&")){
					linkurl = linkurl+"&taskid="+postcontent.getId();
				}else{
					int length = linkurl.indexOf("?");
					logger.info(length+"<-->"+linkurl.getBytes().length+"");
					if(length != linkurl.getBytes().length-1){
						linkurl = linkurl+"&taskid="+postcontent.getId();
					}else{
						linkurl = linkurl+"taskid="+postcontent.getId();
					}
				}
			}else{
				linkurl = linkurl+"?taskid="+postcontent.getId();
			}
			postcontent.setLinkurl(linkurl.trim());
			logger.info("linkurl:"+linkurl);
			postcontentService.update(postcontent);
		}
		return postcontent;
	}

	/**
	 * 保存任务信息
	 * @return
	 */
	@AdminAuthenticated
	public Result savenewsPostcontent(){
		String newsidStr = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "newsid");
		long id = Numbers.parseLong(newsidStr, Long.valueOf(0));
		String newstitle = Form.form().bindFromRequest().get("newstitle");//
		String newscontent = Form.form().bindFromRequest().get("newscontent");//
		String newscontentDetail = Form.form().bindFromRequest().get("newscontentDetail");//
		String newsstart_tim = Form.form().bindFromRequest().get("newsstart_tim");//
		String newsend_tim = Form.form().bindFromRequest().get("newsend_tim");//
		String newsnsort = Form.form().bindFromRequest().get("newsnsort");//
		Postcontent postcontent = new Postcontent();
		if(id!=0l){
			postcontent = postcontentService.findPostcontentById(id);
			postcontent = dealPostcontent(postcontent,"5","新闻资讯",newstitle,newscontent,"0","","",newsstart_tim,newsend_tim,newsnsort,0);
			postcontent.setDateUpd(new Date());
			postcontent = postcontentService.update(postcontent);
		}else{
			postcontent.setSta("0");
			postcontent = dealPostcontent(postcontent,"5","新闻资讯",newstitle,newscontent,"0","","",newsstart_tim,newsend_tim,newsnsort,0);
			//保存新闻主体内容
			postcontent = postcontentService.save(postcontent);
		}
		PostcontentDetail postcontentDetail = postcontentService.saveOrUpdatePostcontentDetail(postcontent,newscontentDetail);
		//针对linkurl进行处理
		postcontent = dealNewsLinkurl(postcontent);
		return redirect("/postcontent/newOrUpdatePostcontent?id="+postcontent.getId()+"&activeNum="+3+"&src=5");
	}
	
	/**
	 * 
	 * <p>Title: dealNewsLinkurl</p> 
	 * <p>Description: 针对新闻链接的更改</p> 
	 * @param postcontent
	 * @return
	 */
	private Postcontent dealNewsLinkurl(Postcontent postcontent) {
		String preLink = Configuration.root().getString("postcontent.new.link","http://182.92.237.254:9301/H5/news");
		String linkurl = preLink+"?taskid="+postcontent.getId();
		postcontent.setLinkurl(linkurl);
		postcontentService.update(postcontent);
		return postcontent;
	}

	/**
	 * 
	 * <p>Title: changePostcontentSta</p> 
	 * <p>Description:更改任务状态 </p> 
	 * @return
	 */
	@AdminAuthenticated
	public Result changePostcontentSta(){
		String id = AjaxHelper.getHttpParam(request(), "id");//
		String sta = AjaxHelper.getHttpParam(request(), "sta");//
		Postcontent postcontent = postcontentService.findPostcontentById(Numbers.parseLong(id, Long.valueOf(0)));
		postcontent.setSta(sta);
		postcontent = postcontentService.update(postcontent);
		//激活任务，将任务下发到每一个快递员
		if("1".equals(postcontent.getSta())){
			//获取所有快递员用户信息//校验快递员是否已拥有此任务，若无则派发
			postcontentService.initPostcontentUser(postcontent);
		}else if("0".equals(postcontent.getSta())){
			postcontentService.delInitPostcontentUser(postcontent);
		}
		
		return redirect("/postcontent/postcontentManage");
	}
	
	/**
	 * 
	 * <p>Title: uploadPostcontentImg</p> 
	 * <p>Description: 上传图片</p> 
	 * @return
	 */
	@AdminAuthenticated
	public Result uploadPostcontentImg(){
		Long pcid = Numbers.parseLong(Form.form().bindFromRequest().get("pcidImages"), Long.valueOf(0));
		Postcontent postcontent = postcontentService.findPostcontentById(pcid);
		MultipartFormData body = request().body().asMultipartFormData();
        List<FilePart> imageParts = body.getFiles();
        if (postcontent!=null && imageParts != null) {
        	String path=Configuration.root().getString("oss.upload.postcontent.image", "upload/bbt/");//上传路径
        	String BUCKET_NAME=StringUtil.getBUCKET_NAME();
    		for(FilePart imagePart:imageParts){
    			String fileName = imagePart.getFilename();
    			File file = imagePart.getFile();//获取到该文件
    			int p = fileName.lastIndexOf('.');
    			String type = fileName.substring(p, fileName.length()).toLowerCase();
    			
    			if (".jpg".equals(type)||".gif".equals(type)||".png".equals(type)||".jpeg".equals(type)||".bmp".equals(type)) {
    				// 检查文件后缀格式
    				String fileNameLast = UUID.randomUUID().toString().replaceAll("-", "")+type;//最终的文件名称
    				logger.info(path);
    				logger.info(fileNameLast);
    				logger.info(type);
    				logger.info(BUCKET_NAME);
    				String endfilestr = OSSUtils.uploadFile(file,path,fileNameLast, type,BUCKET_NAME);		
    				logger.info("========="+endfilestr);//=/upload/postcontent/images/fbbbeeb1-2a96-4634-a77b-4566dc68dff0.jpg
    				PostcontentImg postcontentImg=new PostcontentImg();
    				postcontentImg.setDateNew(new Date());
    				postcontentImg.setImgurl(endfilestr);
    				postcontentImg.setPcid(pcid);
    				postcontentService.savePostcontentImg(postcontentImg);
    			}
    		}
        }
        
        return ok(Json.toJson(""));
	}
	
	/**
	 * 删除任务信息图片
	 * @param detailId
	 * @return
	 */
	@AdminAuthenticated
	public Result deletePostcontentImg(Long imageid){
		PostcontentImg postcontentImg = postcontentService.findPostcontentImgByImgid(imageid);
		postcontentService.deletePostcontentImgId(postcontentImg);
		ObjectNode result=Json.newObject();
		result.put("status", 1);
		return ok(Json.toJson(result));
	}
	
}
