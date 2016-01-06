package services.balance;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.postman.Balance;
import models.postman.BalanceWithdraw;
import models.postman.PostmanUser;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import play.Configuration;
import play.Logger;
import play.Logger.ALogger;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Expression;
import com.avaje.ebean.PagedList;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.avaje.ebean.annotation.Transactional;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import form.BalanceWithdrawForm;
import form.PostmanUserForm;
import services.ServiceFactory;
import utils.Constants;
import utils.Dates;
import utils.ExcelGenerateHelper;
import utils.GlobalDBControl;
import utils.JdbcOper;
import utils.Numbers;
import utils.StringUtil;
import vo.BalanceWithdrawVo;

public class BalanceService {
	private static BalanceService instance = null;
	private static ALogger logger = Logger.of(BalanceService.class);

	public static BalanceService getInstance() {
		if (instance == null) {
			syncInit();
		}
		return instance;
	}

	private BalanceService() {

	}

	private static synchronized void syncInit() {
		if (instance == null) {
			instance = new BalanceService();
		}
	}
	
	public Balance findBalanceById(Long id) {
		return Ebean.getServer(GlobalDBControl.getDB()).find(Balance.class).where().eq("id", id).findUnique();
	}

	@Transactional
	public Balance update(Balance balance) {
		Ebean.getServer(GlobalDBControl.getDB()).update(balance);
		return balance;
	}

	public BalanceWithdraw findBalanceWithdrawById(Long id) {
		return Ebean.getServer(GlobalDBControl.getDB()).find(BalanceWithdraw.class).where().eq("id", id).findUnique();
	}

	/**
	 * 
	 * <p>Title: updateBalanceWithdraw</p> 
	 * <p>Description: 用户提现操作，更新状态</p> 
	 * @param balanceWithdraw
	 * @return
	 */
	@Transactional
	public BalanceWithdraw updateBalanceWithdraw(BalanceWithdraw balanceWithdraw) {
		Ebean.getServer(GlobalDBControl.getDB()).update(balanceWithdraw);
		return balanceWithdraw;
	}
	
	/**
	 * 
	 * <p>Title: findBalanceByUid</p> 
	 * <p>Description: 查询用户钱包信息</p> 
	 * @param uid
	 * @return
	 */
	public Balance findBalanceByUid(Integer uid) {
		return Ebean.getServer(GlobalDBControl.getDB()).find(Balance.class).where().eq("uid", uid).findUnique();
	}

	/**
	 * 
	 * <p>Title: getTotalsWithForm</p> 
	 * <p>Description: 根据条件查找出所有满足条件的提现信息数量</p> 
	 * @param formPage
	 * @return
	 */
	public int getTotalsWithForm(BalanceWithdrawForm form) {
		StringBuffer sb = new StringBuffer("SELECT count(bw.id) ")
			.append("FROM balance_withdraw bw, postmanuser p WHERE bw.`uid`=p.`id` ");
		//提现状态
		if (-1!=form.sta) {
			sb.append("and ").append("bw.sta=").append(form.sta).append(" ");
		}
		if(!Strings.isNullOrEmpty(form.nickname)){
			sb.append("and (").append("p.nickname like '%").append(form.nickname).append("%' ");
			if(!Strings.isNullOrEmpty(form.alipayAccount)){
				sb.append("or ").append("p.`alipay_account` = '").append(form.alipayAccount).append("' ");
			}
			sb.append(") ");
		}
		if(!Strings.isNullOrEmpty(form.phone)){
			sb.append("and ").append("p.phone = ").append(form.nickname).append(" ");
		}
		//下单时间
		if (form.between != null) {
		    sb.append("and ").append("bw.date_new between '").append(Dates.formatEngLishDateTime(form.between.start)).append("' and '").append(Dates.formatEngLishDateTime(form.between.end)).append("' ");
		}
		String sql = sb.toString();
		logger.info("sql:==============>>>"+sql); 
		int totals = 0;
        JdbcOper oper = JdbcOper.getPrepareStateDao(sql);
		try {
			// 数据库sql操作
			ResultSet rs = oper.pst.executeQuery();// 执行语句，得到结果集
			while(rs.next()){
				totals = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			oper.close();
		}
		return totals;
	}

	/**
	 * 
	 * <p>Title: queryBalanceWithdrawList</p> 
	 * <p>Description: 根据条件查找出所有满足条件的提现信息</p> 
	 * @param formPage
	 * @return
	 */
	public List<BalanceWithdrawVo> queryBalanceWithdrawList(
			BalanceWithdrawForm form) {
		StringBuffer sb = new StringBuffer("SELECT bw.`id`,p.`nickname`,p.`phone`,p.`alipay_account`,bw.`amount`,bw.`date_new`,bw.`sta`,p.`id`,p.`cardidno`,p.`companyname`,p.`companyid`,p.`substation`,p.`staffid`,bw.`oper_remark`,bw.`oper`,bw.`date_upd` ")
				.append("FROM balance_withdraw bw, postmanuser p WHERE bw.`uid`=p.`id` ");
		//提现状态
		if (-1!=form.sta) {
			sb.append("and ").append("bw.sta=").append(form.sta).append(" ");
        }
		if(!Strings.isNullOrEmpty(form.nickname)){
			sb.append("and (").append("p.nickname like '%").append(form.nickname).append("%' ");
			if(!Strings.isNullOrEmpty(form.alipayAccount)){
				sb.append("or ").append("p.`alipay_account` = '").append(form.alipayAccount).append("' ");
			}
			sb.append(") ");
		}
		if(!Strings.isNullOrEmpty(form.phone)){
			sb.append("and ").append("p.phone = ").append(form.nickname).append(" ");
		}
        //下单时间
        if (form.between != null) {
            sb.append("and ").append("bw.date_new between '").append(Dates.formatEngLishDateTime(form.between.start)).append("' and '").append(Dates.formatEngLishDateTime(form.between.end)).append("' ");
        }
        sb.append(" order by bw.id desc ");
        sb.append("limit ").append(form.page*form.size).append(",").append(form.size).append(" ");
        String sql = sb.toString();
        logger.info(sql); 
        List<BalanceWithdrawVo> result = new ArrayList<BalanceWithdrawVo>();
        JdbcOper oper = JdbcOper.getPrepareStateDao(sql);
		try {
			// 数据库sql操作
			ResultSet rs = oper.pst.executeQuery();// 执行语句，得到结果集
			while(rs.next()){
				BalanceWithdrawVo balanceWithdrawVo = new BalanceWithdrawVo();
				balanceWithdrawVo.balanceWithdrawId=rs.getInt(1);
				balanceWithdrawVo.nickname=rs.getString(2);
				balanceWithdrawVo.phone=rs.getString(3);
				balanceWithdrawVo.alipayAccount=rs.getString(4);
				balanceWithdrawVo.amount=String.valueOf(Numbers.parseDouble((rs.getInt(5)/100.0)+"", 0.0));
				String dateNew = rs.getString(6);
				if(!Strings.isNullOrEmpty(dateNew)){
					dateNew = dateNew.substring(0, 19);
				}
				balanceWithdrawVo.dateNew=dateNew;
				balanceWithdrawVo.sta=rs.getString(7);
				balanceWithdrawVo.postmanuserId=rs.getInt(8);
				balanceWithdrawVo.cardidno=rs.getString(9);
				balanceWithdrawVo.companyname=rs.getString(10);
				balanceWithdrawVo.companyid=rs.getInt(11);
				balanceWithdrawVo.substation=rs.getString(12);
				balanceWithdrawVo.staffid=rs.getString(13);
				balanceWithdrawVo.operRemark=rs.getString(14);
				balanceWithdrawVo.oper=rs.getString(15);
				balanceWithdrawVo.dateUpd=rs.getString(16);
				result.add(balanceWithdrawVo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			oper.close();
		}
		return result;
	}

	/**
	 * 
	 * <p>Title: exportFile</p> 
	 * <p>Description: 导出快递员提现记录</p> 
	 * @param balanceWithdrawVoList
	 * @return
	 */
	public File exportFile(List<BalanceWithdrawVo> balanceWithdrawVoList) {
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet("sheet1");
		sheet.setColumnWidth(0, 5000);
		sheet.setColumnWidth(1, 5000);
		sheet.setColumnWidth(2, 5000);
		sheet.setColumnWidth(3, 5000);
		sheet.setColumnWidth(4, 5000);
		sheet.setColumnWidth(5, 5000);
		sheet.setColumnWidth(6, 8000);
		sheet.setColumnWidth(7, 5000);
		sheet.setColumnWidth(8, 5000);
		sheet.setColumnWidth(9, 5000);
		ExcelGenerateHelper helper = ExcelGenerateHelper.getInstance(wb);

		// 获取行索引
		int rowIndex = 0;
		int colIndex = 0;

		// 表头
		String[] titles = { "快递员姓名", "手机号", "支付宝账号", "提现金额", "申请时间", "描述","状态", "审核时间", "审核人"};

		// 生成表头
		helper.setRowIndex(rowIndex++);
		helper.generateHeader(sheet, titles, 0, StringUtils.EMPTY,StringUtils.EMPTY);
		for(BalanceWithdrawVo balanceWithdrawVo:balanceWithdrawVoList){
			colIndex = 0;
			Row row = sheet.createRow(rowIndex++);
			helper.createStringCell(row, colIndex++, balanceWithdrawVo.nickname);//快递员姓名
			helper.createStringCell(row, colIndex++, balanceWithdrawVo.phone);//手机号
			helper.createStringCell(row, colIndex++, balanceWithdrawVo.alipayAccount);//支付宝账号
			helper.createStringCell(row, colIndex++, balanceWithdrawVo.amount);//提现金额
			helper.createStringCell(row, colIndex++, balanceWithdrawVo.dateNew);//申请时间
			helper.createStringCell(row, colIndex++, balanceWithdrawVo.operRemark);//描述
			helper.createStringCell(row, colIndex++, Constants.BalanceWithdrawSta.stas2Message(Numbers.parseInt(balanceWithdrawVo.sta, 0)));//状态
			helper.createStringCell(row, colIndex++, balanceWithdrawVo.dateUpd);//审核时间
			helper.createStringCell(row, colIndex++, balanceWithdrawVo.oper);//审核人
		}
		//在application.conf中配置的路径
		String path = Configuration.root().getString("balancewithdraw.export.path");
		File file = new File(path);
		file.mkdir();//判断文件夹是否存在，不存在就创建
		
		FileOutputStream out = null;
		String fileName = path + "BalanceWithdraw" + System.currentTimeMillis() + ".xls";
		try {
			out = new FileOutputStream(fileName);
			wb.write(out);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new File(fileName);
	}



}
