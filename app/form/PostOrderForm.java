package form;

import java.io.File;
import java.util.Date;


public class PostOrderForm {
	
	public Integer page = 0;
	public Integer size = 10;
	public Integer uid;//寄件人编号
	public Double userlong;//寄件人经度
	public Double userlat;//寄件人伟度
	public String username;//寄件人姓名
	public String phone;//寄件人手机 
	public String address;//寄件地址
	public Double receivelong;//收件人经度
	public Double receivelat;//收件人伟度
	public String receivename;//收件人姓名
	public String receivephone;//收件人手机
	public String receiveaddress;//收件人地址
	public String subjectname;//物品名称
	public Integer subjecttyp;//特别类型
	public String subjectremark;//物品备注
	public Long distance;//距离 
	public Integer weight;//重量
	public String paytyp;//付款方式
	public int totalfee;//总价
	public Integer award;//奖励
	public Integer status=-1;//状态
	public Date gettime;//取件时间
	public Date realgettime;//实际取件时间
	public Date overtime;//完成时间
	public Date date_new;//创建时间
	public Date date_upd;//更新时间
	public String gettyp;//取件类型 1立即取件 2其他时间
	public String ordertyp="-1";//订单类型 1客户端下单 2渠道下单
	public int goodsfee;
	public int frieght;
	public String province;
	public String provincereceive;
	public String ordercode;
	public DateBetween between;
	public String keyword;
}
