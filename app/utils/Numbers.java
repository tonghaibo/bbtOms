package utils;

import java.text.DecimalFormat;

import play.Logger;


public class Numbers {
	
	public static Integer parseInt(String str, Integer defaultValue){
		try{
			str = str.replace(".0", "");
			return Integer.parseInt(str);
		}catch(Exception e){
			return defaultValue;
		}
	}
	
	public static Long parseLong(String str, Long defaultValue){
		try{
			str = str.replace(".0", "");
			return Long.parseLong(str);
		}catch(Exception e){
			return defaultValue;
		}
	}
	public static Double parseDouble(String str, Double defaultValue){
		try{
			return Double.parseDouble(str);
		}catch(Exception e){
			return defaultValue;
		}
	}
	/**
	 * double类型如果小数点后为零显示整数否则保留
	 * @param num
	 * @return
	 */
	public static String doubleTrans(double num) {
		if (num % 1.0 == 0) {
			return String.valueOf((long) num);
		}
		return String.valueOf(num);
	}

	/**
	 * 将src除以div后返回
	 * @param src
	 * @param div
	 * @return
	 */
	public static String intToStringWithDiv(Integer src,Integer div){
		double srcD = Double.valueOf(src);
		double divD = Double.valueOf(div);
		Double result =srcD/divD;
		return doubleTrans(result);
	}
	
	
	public static void main(String[] args) {
		String a = intToStringWithDiv(18764, 100);
		System.out.println(a);
	}
}
