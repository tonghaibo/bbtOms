package assets;

import java.util.Date;

import play.Configuration;
import play.Play;
import play.api.http.HttpErrorHandler;
import controllers.AssetsBuilder;


public class CdnAssets{
    
	private static String getCDN_BASE_URL(){
    	if(Play.application().configuration().getBoolean("production", false)){
        	return Configuration.root().getString("cdn.url.product", "http://192.168.0.109/");
        }else{
        	return Configuration.root().getString("cdn.url.dev", "http://192.168.0.109/");
        }
    }
    private static final String CDN_ADMIN_ADMINLTE_URL;
    private static final String CDN_ADMIN_ADMINLTE_PLUGINS_URL;
    private static final String CDN_ADMIN_BOOTSTRAP_URL;
    private static final String CDN_ADMIN_EDITOR_URL;
    static {
        CDN_ADMIN_ADMINLTE_URL = getCDN_BASE_URL() + "/adminLTE/";
        CDN_ADMIN_BOOTSTRAP_URL = getCDN_BASE_URL() + "/bootstrap/";
        CDN_ADMIN_ADMINLTE_PLUGINS_URL = CDN_ADMIN_ADMINLTE_URL + "plugins/";
    	CDN_ADMIN_EDITOR_URL = getCDN_BASE_URL() + "/umeditor/";
    }

    //表态文件地址
	public static final String CDN_API_PUBLIC_URL;
    static {
    	CDN_API_PUBLIC_URL = getCDN_BASE_URL()+"/";// + "/public/";
    }
    public static String urlForAPIPublic(String file) {
        StringBuilder sb = new StringBuilder(CDN_API_PUBLIC_URL);
        sb.append(file);
        return sb.toString();
    }
    /*后台系统静态资源AdminLTE根路径*/
    public static String urlForAdminLTE(String file) {
        StringBuilder sb = new StringBuilder(CDN_ADMIN_ADMINLTE_URL);
        sb.append(file);
        return sb.toString();
    }
    /*后台系统静态资源Bootstrap根路径*/
    public static String urlForAdminBootstrap(String file) {
    	StringBuilder sb = new StringBuilder(CDN_ADMIN_BOOTSTRAP_URL);
    	sb.append(file);
    	return sb.toString();
    }
    /*后台系统静态资源Plugins根路径*/
    public static String urlForAdminLTEPlugins(String file) {
    	StringBuilder sb = new StringBuilder(CDN_ADMIN_ADMINLTE_PLUGINS_URL);
    	sb.append(file);
    	return sb.toString();
    }
    /*后台系统静态资源Plugins根路径*/
    public static String urlForEditor(String file) {
    	StringBuilder sb = new StringBuilder(CDN_ADMIN_EDITOR_URL);
    	sb.append(file);
    	return sb.toString();
    }
    /*
     * 格式式显示页面时间
     */
    public static String formatePosttim(Date tim){
    	Date date=new Date();
    	if(date.getDay()>tim.getDay())
    		return "明天"+tim.getHours()+"时";
    	return tim.getHours()+"时";
    }
    //返回订单状态
    public static String formatordersta(Integer stat){
    	//0未支付，1：待接单，2：待揽收，3：待配送，4：已完成（已送达），5：已完成（有问题）
    	switch(stat){
    	case 0:
    		return "未支付";
    	case 1:
    		return "待接单";
    	case 2:
    		return "快递员已接单-待取货";
    	case 3:
    		return "快递员已收件-待配送";
    	case 4:
    		return "已送达";
    	case 5:
    		return "已完成";
    	}
    	return "";
    }
}
