package controllers.wx;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import models.wx.menu.WeixinButton;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import services.wx.WxService;

/**
 * 
 * <p>Title: WxMenuController.java</p> 
 * <p>Description: </p> 
 * <p>Company: higegou</p> 
 * @author  ctt
 * date  2015年10月23日  下午3:44:10
 * @version
 */
public class WxMenuController extends Controller {
	private static final Logger.ALogger logger = Logger.of(WxMenuController.class);
	//private static final String token = Play.application().configuration().getString("wx.token");
	private static final WxService wxService = WxService.getInstance();
	/**
	 * 
	 * <p>Title: createMenu</p> 
	 * <p>Description: 创建菜单</p> 
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
    public Result createMenu() throws UnsupportedEncodingException {
    	List<WeixinButton> button = new ArrayList<WeixinButton>();
    	//第一列
    	WeixinButton wb = new WeixinButton();
    	String wxMallUrl = wxService.getWxOauthUrl("bbtsend");
    	wb = new WeixinButton();
    	wb.setName("发快递");
    	wb.setType("view");
    	wb.setUrl(wxMallUrl);
    	button.add(wb);
    	//第二列
    	List<WeixinButton> buttontwo=new ArrayList<WeixinButton>();
    	
    	String wxOrderUrl = wxService.getWxOauthUrl("bbtindex");

    	wb=new WeixinButton();
    	wb.setName("我的同城快递");
    	wb.setType("view");
    	wb.setUrl(wxService.getWxOauthUrl("bbtpostlist"));
    	buttontwo.add(wb);
    	wb=new WeixinButton();
    	wb.setName("发快递");
    	wb.setType("view");
    	wb.setUrl(wxOrderUrl);
    	buttontwo.add(wb);
    	wb = new WeixinButton();
    	wb.setName("同城快递");
    	wb.setType("view");
    	wb.setUrl(wxOrderUrl);
    	wb.setSub_button(buttontwo);
    	button.add(wb);
    	//第三列
    	List<WeixinButton> buttonThree = new ArrayList<WeixinButton>();
    	wb = new WeixinButton();
    	wb.setName("免费优惠券");
    	wb.setType("view");
    	wb.setUrl(wxService.getWxOauthUrl("bbtcoupon"));
    	buttonThree.add(wb);
    	
    	wb = new WeixinButton();
    	wb.setName("查快递");
    	wb.setType("view");
    	wb.setUrl(wxService.getWxOauthUrl("bbtsearch"));
    	buttonThree.add(wb);
    	wb = new WeixinButton();
    	wb.setName("我的快递");
    	wb.setType("view");
    	wb.setUrl(wxService.getWxOauthUrl("bbtlist"));
    	buttonThree.add(wb);
    	wb = new WeixinButton();
    	wb.setName("我的寄件人");
    	wb.setType("view");
    	//需变更素材链接
    	wb.setUrl(wxService.getWxOauthUrl("bbtsender"));
    	buttonThree.add(wb);
    	wb = new WeixinButton();
    	wb.setName("我的收件人");
    	wb.setSub_button(buttonThree);
    	wb.setUrl(wxService.getWxOauthUrl("bbtposter"));
    	button.add(wb);
    	String msg = wxService.createMenu(button);
    	logger.info("msg:"+msg);
        return ok(msg);
    }

    /**
	 * 删除所有的菜单
	 * @param accessToken
	 * @return
	 * @throws WexinReqException
	 */
	public Result deleteMenu() {
		wxService.deleteMenu();
		return ok("ok");
	}
}
