package com.yxjr.credit.security;

/**
 * YxjrSecurityConstants
 * 
 * @author XiaoChangYou
 * @version 修改时间：2016-5-6 下午3:04:54
 */
public final class YxjrSecurityConstants {

	// 加解密用获取对应加解密算法常量
	public final static String AES_MARK = "AES";
	public final static String MD5_MARK = "MD5";
	public final static String RSA_MARK = "RSA";
	public final static String SHA1_MARK = "SHA";
	public final static String DES_MARK = "DES";

	// 默认字符集
	public final static String CHARACTER_SET = "UTF-8";

	// 十六进制char数组
	public final static char[] DIGIT = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	// 生成随机数序列
	public final static char[] NUM_SEQUENCE = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
	// 默认生成随机数长度
	public final static int RANDOM_NUM_LENGTH = 6;

	// 报文加密方式
	public final static String ENCRYPT_TYPE_AES = "1";
	public final static String ENCRYPT_TYPE_RSA = "2";
	public final static String ENCRYPT_TYPE_MD = "3";
	public final static String ENCRYPT_TYPE_NONE = "4";

	// 签名算法
	public final static String SIGNATURE_ALGORITHM_MD5 = "MD5withRSA";
}
