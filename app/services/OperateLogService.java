package services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.annotation.Transactional;

import play.Logger;
import play.Logger.ALogger;
import form.OperateLogQueryForm;
import models.admin.OperateLog;
import models.admin.OperateLog.LogType;
import utils.Dates;
import utils.GlobalDBControl;

/**
 * 日志操作
 * 
 * @author luobotao
 * @Date 2015年9月11日
 */
@Named
@Singleton
public class OperateLogService {

	private static OperateLogService instance = null;
	private static ALogger logger = Logger.of(OperateLogService.class);

	public static OperateLogService getInstance() {
		if (instance == null) {
			syncInit();
		}
		return instance;
	}

	private OperateLogService() {

	}

	private static synchronized void syncInit() {
		if (instance == null) {
			instance = new OperateLogService();
		}
	}
	@Transactional
	public void saveAdminUserLog(Long adminUserId, String adminUserName, String ip,String remark) {
		Ebean.getServer(GlobalDBControl.getDB()).save(createAdminUserLog(adminUserId, adminUserName, ip,remark));
	}
	
	public OperateLog createAdminUserLog(Long adminUserId, String adminUserName, String ip,String remark ) {
		return create(adminUserId, adminUserName, LogType.ADMINUSER.getMessage(), adminUserName+remark, ip,LogType.ADMINUSER);
	}
	@Transactional
	public void saveLoginLog(Long id, String username, String remoteAddress) {
		Ebean.getServer(GlobalDBControl.getDB()).save(createLoginLog(id,username,remoteAddress));
	}

	private OperateLog createLoginLog(Long adminUserId, String adminUserName, String ip) {
		return create(adminUserId, adminUserName, LogType.LOGIN.getMessage(), adminUserName + "登录", ip, LogType.LOGIN);
	}

	public OperateLog create(Long adminid, String adminname, String typ, String remark, String ip, LogType opType) {
		OperateLog operateLog = new OperateLog();
		operateLog.setAdminid(adminid);
		operateLog.setAdminname(adminname);
		operateLog.setDateAdd(new Date());
		operateLog.setIp(ip);
		operateLog.setTyp(typ);
		operateLog.setRemark(remark);
		operateLog.setOpType(opType);
		return operateLog;
	}

	public void saveLogoutLog(Long id, String username, String remoteAddress) {
		Ebean.getServer(GlobalDBControl.getDB()).save(createLogoutLog(id, username, remoteAddress));
	}
	
	private OperateLog createLogoutLog(Long adminUserId, String adminUserName, String ip) {
		return create(adminUserId, adminUserName, LogType.LOGOUT.getMessage(), adminUserName + "登出", ip,LogType.LOGOUT);
	}
}
