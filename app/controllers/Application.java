package controllers;

import java.io.File;
import java.util.UUID;

import com.fasterxml.jackson.databind.node.ObjectNode;

import controllers.admin.AdminAuthenticated;
import play.*;
import play.libs.Json;
import play.mvc.*;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import utils.FileUtils;
import utils.OSSUtils;
import utils.StringUtil;
import views.html.*;

public class Application extends BaseController {

    public Result index() {
        return redirect("/admin");
    }
    @AdminAuthenticated()
    public Result welcome() {
    	return ok(views.html.welcome.render(getCurrentAdminUser()));
    }

    /**
	 * editor上传文件
	 */
	public Result imgUpload(){
		MultipartFormData imageBody = request().body().asMultipartFormData();
		FilePart contentFile = imageBody.getFile("upfile");
		String path=Configuration.root().getString("oss.upload.editor.image", "upload/editor/images/");//上传路径
		String BUCKET_NAME=Configuration.root().getString("oss.bucket.name.higouOMSDev", "higou-oms");
		boolean IsProduct = Configuration.root().getBoolean("production", false);
		if(IsProduct){
			BUCKET_NAME=Configuration.root().getString("oss.bucket.name.higouOMSProduct", "higou-oms");
		}
		if (contentFile != null && contentFile.getFile() != null) {
			String fileName = contentFile.getFilename();
			File file = contentFile.getFile();//获取到该文件
			int p = fileName.lastIndexOf('.');
			String type = fileName.substring(p, fileName.length()).toLowerCase();
			// 检查文件后缀格式
			String fileNameLast = UUID.randomUUID().toString().replaceAll("-", "")+type;//最终的文件名称
			String endfilestr = OSSUtils.uploadFile(file,path,fileNameLast, type,BUCKET_NAME);		
			String domains=Configuration.root().getString("oss.image.url");
			ObjectNode result = Json.newObject();
			result.put("name", fileNameLast);
			result.put("originalName", fileName);
			result.put("size", ""+FileUtils.getSize(file));
			result.put("state", "SUCCESS");
			result.put("type", FileUtils.getStrFileExt(fileName));
			result.put("url", domains+endfilestr);
			response().setContentType("text/html;charset=utf-8");
			return ok(Json.toJson(result).toString());
		}else{
			return ok();
		}
	}
}
