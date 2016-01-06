package utils;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;

import play.Logger;
import utils.Base64;
import utils.wx.Sha1Util;
import utils.wx.SortMapUtil;

public class SignUtils {
    private static final Logger.ALogger LOGGER = Logger.of(SignUtils.class);
	private static final String SIGN_ALGORITHMS = "SHA1WithRSA";
	private static final String ALGORITHM = "RSA";
	private static final String DEFAULT_CHARSET = "UTF-8";
    /**
     * 
     * <p>Title: getSign</p> 
     * <p>Description: 获取微信加密后的字符串，字典排序后 sha1加密</p> 
     * @param params
     * @return
     */
	public static String getSha1Sign(String value) {
		String sign = Sha1Util.getSha1(value);
		return sign;
	}
	 
	public static String sign(String content, String privateKey) {
		try {
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
					Base64.decode(privateKey));
			KeyFactory keyf = KeyFactory.getInstance(ALGORITHM);
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);

			java.security.Signature signature = java.security.Signature
					.getInstance(SIGN_ALGORITHMS);

			signature.initSign(priKey);
			signature.update(content.getBytes(DEFAULT_CHARSET));

			byte[] signed = signature.sign();

			return Base64.encode(signed);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
