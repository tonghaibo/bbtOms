package utils;

import play.Play;

/**
 * @author luobotao
 * @Date 2015年10月21日
 */
public class GlobalDBControl {

	public static String getDB() {
		if (Play.application().configuration().getBoolean("production", false)) {
			return "product";
		} else {
			return "dev";
		}
	}
	public static String getHgDB() {
		return "hgproduct";
	}
}
