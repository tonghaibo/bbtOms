package services.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.codec.digest.DigestUtils;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.annotation.Transactional;

import form.admin.AdminLoginForm;
import form.admin.AdminUserQueryForm;
import form.admin.CreateOrUpdateAdminUserForm;
import models.admin.AdminUser;
import models.admin.OperateLog;
import play.Logger;
import play.Logger.ALogger;
import play.cache.Cache;
import utils.BeanUtils;
import utils.Constants;
import utils.GlobalDBControl;
import utils.Htmls;

/**
 * @author luobotao
 * @Date 2015年10月19日
 */
@Named
@Singleton
public class AdminUserService {

    private static AdminUserService instance = null;
	private static ALogger logger = Logger.of(AdminUserService.class);

	public static AdminUserService getInstance() {
		if (instance == null) {
			syncInit();
		}
		return instance;
	}

	private AdminUserService() {

	}

	private static synchronized void syncInit() {
		if (instance == null) {
			instance = new AdminUserService();
		}
	}

    public AdminUser getAdminUser(Long id) {
    	return Ebean.getServer(GlobalDBControl.getDB()).find(AdminUser.class).where().eq("id", id).findUnique();
    }

    public AdminUser login(AdminLoginForm form) {
    	String username = form.username;
    	String pass = DigestUtils.md5Hex(form.password);
    	Logger.info(username);
    	Logger.info(pass);
    	List<AdminUser> adminUserList = Ebean.getServer(GlobalDBControl.getDB()).find(AdminUser.class).where().eq("username", form.username).eq("passwd", DigestUtils.md5Hex(form.password)).findList();
		if(adminUserList.isEmpty()||adminUserList.size()<1){
			return null;
		}else{
			return adminUserList.get(0);
		}
    }
    
    @Transactional
    public AdminUser saveAdminUser(AdminUser adminUser) {
    	Ebean.getServer(GlobalDBControl.getDB()).save(adminUser);
    	return adminUser;
    }
    @Transactional
    public AdminUser updateAdminUser(AdminUser adminUser) {
    	Ebean.getServer(GlobalDBControl.getDB()).update(adminUser);
    	return adminUser;
    }

    @Transactional
    public AdminUser createAdminUser(OperateLog operateLog,CreateOrUpdateAdminUserForm form) {
        AdminUser adminUser = new AdminUser();
        if(form.id!=null && form.id.longValue()==-1){
        	form.id = null;
        }
        BeanUtils.copyProperties(form, adminUser);
        if(adminUser.getId()==null || (adminUser.getId()!=null && adminUser.getId().longValue()==-1)){
        	adminUser.setDate_add(new Date());
        	adminUser.setDate_login(new Date());
        	adminUser = saveAdminUser(adminUser);
        }else{
        	adminUser.setDate_login(new Date());
        	adminUser = updateAdminUser(adminUser);
        }
        return adminUser;
    }

    public List<AdminUser> listAdminUsers(final AdminUserQueryForm form) {
    	return Ebean.getServer(GlobalDBControl.getDB()).find(AdminUser.class).findList();
    }
    
    public void delete(Long id) {
    	AdminUser adminUser = getAdminUser(id);
    	if(adminUser!=null){
    		Ebean.getServer(GlobalDBControl.getDB()).delete(adminUser);
    	}
    }

    public String allAdminUser2HTML(Long id) {
        StringBuilder sb = new StringBuilder();
        List<AdminUser> adminUsers = null;
        try {
            adminUsers = Cache.getOrElse(Constants.USER, new Callable<List<AdminUser>>() {

                @Override
                public List<AdminUser> call() throws Exception {
                    List<AdminUser> adminUsers = new ArrayList<AdminUser>();
                    adminUsers = Ebean.getServer(GlobalDBControl.getDB()).find(AdminUser.class).findList();
                    Cache.set(Constants.USER, adminUsers);
                    return adminUsers;
                }
            }, 1);
        } catch (Exception e) {
            logger.error("allAdminUser2HTML error.", e);
        }
        sb.append(Htmls.generateOption(-1, "默认全部"));

       /* if (adminUsers!=null&&adminUsers.size()>0) {
            for (AdminUser adminUser : adminUsers) {
            }
        }*/

        return sb.toString();
    }

	/**
	 * 根据用户名查找此管理员
	 * @param phone
	 * @return
	 */
	public AdminUser findByUsername(String username) {
		List<AdminUser> adminUsers = Ebean.getServer(GlobalDBControl.getDB()).find(AdminUser.class).where().eq("username", username).findList();
		if(adminUsers.isEmpty()){
			return new AdminUser();
		}else{
			return adminUsers.get(0);
		}
	}

	public int updateAdminUserStatus(Long adminId, Integer active) {
		AdminUser adminUser = getAdminUser(adminId);
		if(adminUser!=null){
			adminUser.setActive(active);
			saveAdminUser(adminUser);
			return 1;
		}
		return 0;
	}

	public List<AdminUser> findAllAdminUser() {
		return Ebean.getServer(GlobalDBControl.getDB()).find(AdminUser.class).findList();
	}

	public String realnameList2Html(List<AdminUser> adminUsers, String id) {
		StringBuilder sb = new StringBuilder();
		sb.append(Htmls.generateOption(-1, "请选择"));
		for (AdminUser c : adminUsers) {
			if (id != null && id.equals(c.getId())) {
				sb.append(Htmls.generateSelectedOption(c.getId(), c.getRealname()));
			} else {
				sb.append(Htmls.generateOption(c.getId(), c.getRealname()));
			}
		}
		return sb.toString();
	}

}
