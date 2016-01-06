package utils;

import java.util.List;

import models.Postcompany;
import play.Play;

/**
 * @author luobotao
 * @Date 2015年10月21日
 */
public class Constants {
	public static final String USER = "adminUser";
	public static final String NL = "\r\n";
    public static final String COMMA = ",";
    public static final String ADMIN_SESSION_NAME = "_A_S";
	public static long defalutMeter =3000;//默认3公里

	/**
	 * 数据库名称
	 * 
	 * @return
	 */
	public static String getDB() {
		if (Play.application().configuration().getBoolean("production", false)) {
			return "product";
		} else {
			return "dev";
		}
	}

	public static final String cache_verifyCode = "bbt.verifyCode.";
	public static final String cache_postmanid = "bbt.postmanid.";//根据token找ID使用
	public static final String cache_tokenBypostmanid = "bbt.token.";//根据ID找token使用
	/*
	 * 微信支付ＪＳＡＰＩ回调地址
	 */
	public static final String WXCALLBACK = "http://h5dev.higegou.com/user/wxpayreturnjsapi";
	/*
	 * 微信AppID
	 */
	public static final String WXappID = "wx0cef7e835f598e36";// "wx99199cff15133f37";
	/*
	 * 微信密钥
	 * N
	 */
	public static final String WXappsecret = "416704762a6b40c20025ea169bb00e61";// "a017774f117bf0100a2f7939ef56c89a";

	/*
	 * 微信支付密钥
	 */
	public static final String WXappsecretpay = "neolixxinshiqiqinengwanwei123456";// "neolixxinshiqiqinengwanwei123456";
	/*
	 * 微信支付商户号
	 */
	public static final String WXMCID = "1228736802";// "1235413502";
	/******** 棒棒糖数据结束 ****************************/
	/********百度地图帐号*******************************/
	public static final String BAIDU_AK="MI9t5CBdVKEiDsHhPx1jXysB";
	public static final String BAIDU_SK="SuLiG2G53vsAEsySH7stEYXZDfGvQuP6";

	// 快递员状态
	public static enum PostmanStatus {
		NOCHECK(0, "未审核"),COMMON(1, "正常"),FAILED(2,"审核失败");

		private int status;
		private String message;

		private PostmanStatus(int status, String message) {
			this.message = message;
			this.status = status;
		}

		public int getStatus() {
			return status;
		}

		public String getMessage() {
			return message;
		}
		
		public static String stas2HTML(int value) {
			StringBuilder sb = new StringBuilder();
			PostmanStatus[] stas = PostmanStatus.values();
			sb.append(Htmls.generateOption(-1, "全部"));
			for (PostmanStatus s : stas) {
				if (value==s.status) {
					sb.append(Htmls.generateSelectedOption(s.status, s.message));
				} else {
					sb.append(Htmls.generateOption(s.status, s.message));
				}
			}
			return sb.toString();
		}
		
		public static String stas2Message(int status) {
			PostmanStatus[] stas = PostmanStatus.values();
			for (PostmanStatus s : stas) {
				if (status==s.status) {
					return s.getMessage();
				}
			}
			return "";
		}
	}
	
	//任务类型
	public static enum PostcontentTyps {
		DAILY(1, "日常任务"), 
		AWARD(2, "黄金任务"), 
		SIGNIN(3, "签到任务 "), 
		SYSINFO (4, "系统消息"),
		NEW (5, "新闻");
		
		private int status;
		private String message;
		
		private PostcontentTyps(int status, String message) {
			this.message = message;
			this.status = status;
		}
		
		public int getStatus() {
			return status;
		}
		
		public String getMessage() {
			return message;
		}
		
		public static String typs2HTML(int value) {
			StringBuilder sb = new StringBuilder();
			PostcontentTyps[] typs = PostcontentTyps.values();
			sb.append(Htmls.generateOption(-1, "全部"));
			for (PostcontentTyps s : typs) {
				if (value==s.status) {
					sb.append(Htmls.generateSelectedOption(s.status, s.message));
				} else {
					sb.append(Htmls.generateOption(s.status, s.message));
				}
			}
			return sb.toString();
		}
		
		public static String typs2Message(int status) {
			PostcontentTyps[] typs = PostcontentTyps.values();
			for (PostcontentTyps s : typs) {
				if (status==s.status) {
					return s.getMessage();
				}
			}
			return "";
		}
	}
	
	//任务状态
	public static enum PostcontentStas {
		INVALID(0, "无效"),
		VALID(1, "有效");
		
		private int status;
		private String message;
		
		private PostcontentStas(int status, String message) {
			this.message = message;
			this.status = status;
		}
		
		public int getStatus() {
			return status;
		}
		
		public String getMessage() {
			return message;
		}
		
		public static String stas2HTML(int value) {
			StringBuilder sb = new StringBuilder();
			PostcontentStas[] stas = PostcontentStas.values();
			sb.append(Htmls.generateOption(-1, "全部"));
			for (PostcontentStas s : stas) {
				if (value==s.status) {
					sb.append(Htmls.generateSelectedOption(s.status, s.message));
				} else {
					sb.append(Htmls.generateOption(s.status, s.message));
				}
			}
			return sb.toString();
		}
		
		public static String stas2Message(int status) {
			PostcontentStas[] stas = PostcontentStas.values();
			for (PostcontentStas s : stas) {
				if (status==s.status) {
					return s.getMessage();
				}
			}
			return "";
		}
	}
	//提现审批状态
	public static enum BalanceWithdrawSta {
		APPLY(1, "申请提现"),
		SUCCESS(2, "提现成功"),
 		FAILED(3, "提现失败");
		
		private int status;
		private String message;
		
		private BalanceWithdrawSta(int status, String message) {
			this.message = message;
			this.status = status;
		}
		
		public int getStatus() {
			return status;
		}
		
		public String getMessage() {
			return message;
		}
		
		public static String stas2HTML(int value) {
			StringBuilder sb = new StringBuilder();
			BalanceWithdrawSta[] stas = BalanceWithdrawSta.values();
			sb.append(Htmls.generateOption(-1, "全部"));
			for (BalanceWithdrawSta s : stas) {
				if (value==s.status) {
					sb.append(Htmls.generateSelectedOption(s.status, s.message));
				} else {
					sb.append(Htmls.generateOption(s.status, s.message));
				}
			}
			return sb.toString();
		}
		
		public static String stas2Message(int status) {
			BalanceWithdrawSta[] stas = BalanceWithdrawSta.values();
			for (BalanceWithdrawSta s : stas) {
				if (status==s.status) {
					return s.getMessage();
				}
			}
			return "";
		}
	}
	
    //翻排任务类型
  	public static enum PostRewardTyps {
  		FIRSTREWARD(1, "首单额外奖励"), 
  		GLOBALREWARD(2, "全局奖励"), 
  		BUYREWARD(3, "购买分成奖励"), 
  		STOREREWARD(4, "小店关注奖励");
  		
  		private int status;
  		private String message;
  		
  		private PostRewardTyps(int status, String message) {
  			this.message = message;
  			this.status = status;
  		}
  		
  		public int getStatus() {
  			return status;
  		}
  		
  		public String getMessage() {
  			return message;
  		}
  		
  		public static String typs2HTML(int value) {
  			StringBuilder sb = new StringBuilder();
  			PostRewardTyps[] typs = PostRewardTyps.values();
  			sb.append(Htmls.generateOption(-1, "全部"));
  			for (PostRewardTyps s : typs) {
  				if (value==s.status) {
  					sb.append(Htmls.generateSelectedOption(s.status, s.message));
  				} else {
  					sb.append(Htmls.generateOption(s.status, s.message));
  				}
  			}
  			return sb.toString();
  		}
  		
  		public static String typs2Message(int status) {
  			PostRewardTyps[] typs = PostRewardTyps.values();
  			for (PostRewardTyps s : typs) {
  				if (status==s.status) {
  					return s.getMessage();
  				}
  			}
  			return "";
  		}
  	}
  	
	//翻牌任务状态
  	public static enum PostRewardFlgs {
  		VALID(1, "有效"),
  		INVALID(2, "无效");
  		
  		private int status;
  		private String message;
  		
  		private PostRewardFlgs(int status, String message) {
  			this.message = message;
  			this.status = status;
  		}
  		
  		public int getStatus() {
  			return status;
  		}
  		
  		public String getMessage() {
  			return message;
  		}
  		
  		public static String flgs2HTML(int value) {
  			StringBuilder sb = new StringBuilder();
  			PostRewardFlgs[] stas = PostRewardFlgs.values();
  			sb.append(Htmls.generateOption(-1, "全部"));
  			for (PostRewardFlgs s : stas) {
  				if (value==s.status) {
  					sb.append(Htmls.generateSelectedOption(s.status, s.message));
  				} else {
  					sb.append(Htmls.generateOption(s.status, s.message));
  				}
  			}
  			return sb.toString();
  		}
  		
  		public static String flgs2Message(int status) {
  			PostRewardFlgs[] stas = PostRewardFlgs.values();
  			for (PostRewardFlgs s : stas) {
  				if (status==s.status) {
  					return s.getMessage();
  				}
  			}
  			return "";
  		}
  	}
  	//翻牌任务奖励类型
  	public static enum PostRewardCommisiontyps {
  		VALID(1, "金额"),
  		INVALID(2, "比例");
  		
  		private int status;
  		private String message;
  		
  		private PostRewardCommisiontyps(int status, String message) {
  			this.message = message;
  			this.status = status;
  		}
  		
  		public int getStatus() {
  			return status;
  		}
  		
  		public String getMessage() {
  			return message;
  		}
  		
  		public static String typs2HTML(int value) {
  			StringBuilder sb = new StringBuilder();
  			PostRewardCommisiontyps[] stas = PostRewardCommisiontyps.values();
  			sb.append(Htmls.generateOption(-1, "全部"));
  			for (PostRewardCommisiontyps s : stas) {
  				if (value==s.status) {
  					sb.append(Htmls.generateSelectedOption(s.status, s.message));
  				} else {
  					sb.append(Htmls.generateOption(s.status, s.message));
  				}
  			}
  			return sb.toString();
  		}
  		
  		public static String typs2Message(int status) {
  			PostRewardCommisiontyps[] stas = PostRewardCommisiontyps.values();
  			for (PostRewardCommisiontyps s : stas) {
  				if (status==s.status) {
  					return s.getMessage();
  				}
  			}
  			return "";
  		}
  	}
  	
  	//平台类型 0 ios 1 android
  	public static enum AppVersionOstypes {
  		IOS(0, "iOS"), 
  		ANDROID(1, "ANDROID");
  		
  		private int typ;
  		private String message;
  		
  		private AppVersionOstypes(int typ, String message) {
  			this.message = message;
  			this.typ = typ;
  		}
  		
  		public int getTyp() {
  			return typ;
  		}
  		
  		public String getMessage() {
  			return message;
  		}
  		
  		public static String typs2HTML(int value) {
  			StringBuilder sb = new StringBuilder();
  			AppVersionOstypes[] typs = AppVersionOstypes.values();
  			sb.append(Htmls.generateOption(-1, "全部"));
  			for (AppVersionOstypes s : typs) {
  				if (value==s.typ) {
  					sb.append(Htmls.generateSelectedOption(s.typ, s.message));
  				} else {
  					sb.append(Htmls.generateOption(s.typ, s.message));
  				}
  			}
  			return sb.toString();
  		}
  		
  		public static String typs2Message(int typ) {
  			AppVersionOstypes[] typs = AppVersionOstypes.values();
  			for (AppVersionOstypes s : typs) {
  				if (typ==s.typ) {
  					return s.getMessage();
  				}
  			}
  			return "";
  		}
  	}
  	/**
     * 生成html中需要的来源select
     * @param companyList
     * @param id
     * @return
     */
    public static String companyList2Html(List<Postcompany> postcompanyList,Integer id){
		StringBuilder sb = new StringBuilder();
		sb.append(Htmls.generateOption(-1, "全部"));
		for (Postcompany c : postcompanyList) {
			if (id != null && id.equals(c.getId())) {
				sb.append(Htmls.generateSelectedOption(c.getId(), c.getCompanyname()));
			} else {
				sb.append(Htmls.generateOption(c.getId(), c.getCompanyname()));
			}
		}
		return sb.toString();
    }
    
  //管理员状态
  	public static enum AdminStatus {
  		STOP(0, "停用"),COMMON(1, "正常");
  		
  		private int status;
  		private String message;
  		
  		private AdminStatus(int status, String message) {
  			this.message = message;
  			this.status = status;
  		}

  		public int getStatus() {
  			return status;
  		}

  		public String getMessage() {
  			return message;
  		}

  		public static String status2HTML(int value) {
  			StringBuilder sb = new StringBuilder();
  			AdminStatus[] adminStatus = AdminStatus.values();
  			sb.append(Htmls.generateOption(0, "全部"));
  			for (AdminStatus s : adminStatus) {
  				if (value==s.status) {
  					sb.append(Htmls.generateSelectedOption(s.status, s.message));
  				} else {
  					sb.append(Htmls.generateOption(s.status, s.message));
  				}
  			}
  			return sb.toString();
  		}
  		
  		public static String status2Message(int status) {
  			AdminStatus[] adminStatus = AdminStatus.values();
  			for (AdminStatus s : adminStatus) {
  				if (status==s.status) {
  					return s.getMessage();
  				}
  			}
  			return "";
  		}
  	}
  	
  //管理员类型
  	public static enum AdminType {
  		COMMON("1", "正常"),UNION("2","联营"),OUT("3","商户");
  		
  		private String type;
  		private String message;
  		
  		private AdminType(String type, String message) {
  			this.message = message;
  			this.type = type;
  		}
  		
  		public String getType() {
  			return type;
  		}
  		
  		public String getMessage() {
  			return message;
  		}
  		
  		public static String type2HTML(String value) {
  			StringBuilder sb = new StringBuilder();
  			AdminType[] adminTypes = AdminType.values();
  			sb.append(Htmls.generateOption(0, "全部"));
  			for (AdminType s : adminTypes) {
  				if (value!=null && value.equals(s.getType())) {
  					sb.append(Htmls.generateSelectedOptionString(s.type, s.message));
  				} else {
  					sb.append(Htmls.generateOptionString(s.type, s.message));
  				}
  			}
  			return sb.toString();
  		}
  		
  		public static String type2Message(String value) {
  			AdminType[] adminTypes = AdminType.values();
  			for (AdminType s : adminTypes) {
  				if (value!=null && value.equals(s.getType())) {
  					return s.getMessage();
  				}
  			}
  			return "";
  		}
  	}
  	
  	//地址类型
  	public static enum UserAddressTyps {
  		SENDER(1, "寄件人"), 
  		RECEIVER(2, "收件人"); 
  		
  		private int status;
  		private String message;
  		
  		private UserAddressTyps(int status, String message) {
  			this.message = message;
  			this.status = status;
  		}
  		
  		public int getStatus() {
  			return status;
  		}
  		
  		public String getMessage() {
  			return message;
  		}
  		
  		public static String typs2HTML(int value) {
  			StringBuilder sb = new StringBuilder();
  			UserAddressTyps[] typs = UserAddressTyps.values();
  			sb.append(Htmls.generateOption(-1, ""));
  			for (UserAddressTyps s : typs) {
  				if (value==s.status) {
  					sb.append(Htmls.generateSelectedOption(s.status, s.message));
  				} else {
  					sb.append(Htmls.generateOption(s.status, s.message));
  				}
  			}
  			return sb.toString();
  		}
  		
  		public static String typs2Message(int status) {
  			UserAddressTyps[] typs = UserAddressTyps.values();
  			for (UserAddressTyps s : typs) {
  				if (status==s.status) {
  					return s.getMessage();
  				}
  			}
  			return "";
  		}
  	}
  	
  	//同城派送状态1：待接单，2：待揽收，3：待配送，4：已完成（已送达），5：已完成（有问题）
 	public static enum CityWideDeliverStas {
 		WaitToCatch(1, "待接单"), WaitToGet(2, "待揽收"), WaitToSend(3, "待配送"), Success(
 				4, "已完成"), SuccessWithProblom(5, "已完成");
 		private int status;
 		private String message;

 		private CityWideDeliverStas(int status, String message) {
 			this.message = message;
 			this.status = status;
 		}

 		public int getStatus() {
 			return status;
 		}

 		public String getMessage() {
 			return message;
 		}
 		
 		public static String stas2HTML(int value) {
  			StringBuilder sb = new StringBuilder();
  			CityWideDeliverStas[] stas = CityWideDeliverStas.values();
  			sb.append(Htmls.generateOption(-1, ""));
  			for (CityWideDeliverStas s : stas) {
  				if (value==s.status) {
  					sb.append(Htmls.generateSelectedOption(s.status, s.message));
  				} else {
  					sb.append(Htmls.generateOption(s.status, s.message));
  				}
  			}
  			return sb.toString();
  		}
  		
  		public static String stas2Message(int status) {
  			CityWideDeliverStas[] stas = CityWideDeliverStas.values();
  			for (CityWideDeliverStas s : stas) {
  				if (status==s.status) {
  					return s.getMessage();
  				}
  			}
  			return "";
  		}
 	}
 	
  	//订单类型
  	public static enum OrderTyps {
  		CLIENT(1, "客户端"), 
  		SHOP(2, "商铺"); 
  		
  		private int status;
  		private String message;
  		
  		private OrderTyps(int status, String message) {
  			this.message = message;
  			this.status = status;
  		}
  		
  		public int getStatus() {
  			return status;
  		}
  		
  		public String getMessage() {
  			return message;
  		}
  		
  		public static String typs2HTML(int value) {
  			StringBuilder sb = new StringBuilder();
  			OrderTyps[] typs = OrderTyps.values();
  			sb.append(Htmls.generateOption(-1, ""));
  			for (OrderTyps s : typs) {
  				if (value==s.status) {
  					sb.append(Htmls.generateSelectedOption(s.status, s.message));
  				} else {
  					sb.append(Htmls.generateOption(s.status, s.message));
  				}
  			}
  			return sb.toString();
  		}
  		
  		public static String typs2Message(int status) {
  			OrderTyps[] typs = OrderTyps.values();
  			for (OrderTyps s : typs) {
  				if (status==s.status) {
  					return s.getMessage();
  				}
  			}
  			return "";
  		}
  	}
  	
  //支付方式 （0：现金，1：刷卡，2：微信，3：支付宝，4：其它）
  	public static enum PayModeStas {
  		Cash("0", "现金支付"), POS("1", "刷卡"), Weixin("2", "微信"),Alipay("3","支付宝"),Other("4","其他");
  		
  		private String status;
  		private String message;
  		
  		private PayModeStas(String status, String message) {
  			this.message = message;
  			this.status = status;
  		}
  		
  		public String getStatus() {
  			return status;
  		}
  		
  		public String getMessage() {
  			return message;
  		}
  	}
}
