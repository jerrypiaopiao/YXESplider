package cn.yxeht.app.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 买手党抓取规则
 *
 */
public class MSDFetchRule {

	/*
	 * http://www.maishoudang.com/merchants/300 西班牙 55haitaozzzfr0b-21
	 * http://www.maishoudang.com/merchants/316   中亚 xx
	 * http://www.maishoudang.com/merchants/298  英亚 tag=666adang-21
	 * http://www.maishoudang.com/merchants/8   日亚  http://count.chanet.com.cn/click.cgi?a=527989&d=381499&u=suyeee&e=1007&url=
	 * http://www.maishoudang.com/merchants/301   意大利 tag=666ohyes-21
	 */
	
	public static final String HOST = "http://www.maishoudang.com/";
	
	public static final String CATEGARY_URL = "merchants/";
	
	public static final String FULL_CATEGARY_URL = HOST + CATEGARY_URL;
	
	/**
	 * 西班牙亚马逊
	 */
	public static final String CATEGARY_XIBANYA = "300";
	/**
	 * 中国亚马逊
	 */
	public static final String CATEGARY_CN = "316";
	/**
	 * 英国亚马逊
	 */
	public static final String CATEGARY_UK = "298";
	/**
	 * 日本亚马逊
	 */
	public static final String CATEGARY_JP = "8";
	/**
	 * 意大利亚马逊
	 */
	public static final String CATEGARY_ITALY = "301";
	
	/**
	 * ebay
	 */
	public static final String CATEGARY_EBAY = "19";
	
	public static final List<String> MSD_GOOD_CATEGARY_CODE = new ArrayList<String>();
	static{
		MSD_GOOD_CATEGARY_CODE.add(CATEGARY_XIBANYA);
//		MSD_GOOD_CATEGARY_CODE.add(CATEGARY_CN);
		MSD_GOOD_CATEGARY_CODE.add(CATEGARY_UK);
		MSD_GOOD_CATEGARY_CODE.add(CATEGARY_JP);
		MSD_GOOD_CATEGARY_CODE.add(CATEGARY_ITALY);
		MSD_GOOD_CATEGARY_CODE.add(CATEGARY_EBAY);
	}
	
	public static final Map<String, String> MSD_GOOD_CATEGARY_NAME = new HashMap<String, String>();
	static{
		MSD_GOOD_CATEGARY_NAME.put(CATEGARY_XIBANYA, "西班牙亚马逊");
//		MSD_GOOD_CATEGARY_NAME.put(CATEGARY_CN, "中国亚马逊");
		MSD_GOOD_CATEGARY_NAME.put(CATEGARY_UK, "英国亚马逊");
		MSD_GOOD_CATEGARY_NAME.put(CATEGARY_JP, "日本亚马逊");
		MSD_GOOD_CATEGARY_NAME.put(CATEGARY_ITALY, "意大利亚马逊");
		MSD_GOOD_CATEGARY_NAME.put(CATEGARY_EBAY, "ebay");
	}
	
	public static final Map<String, String> MSG_GOOD_SRC_HOSTS = new HashMap<String, String>();
	static{
		MSG_GOOD_SRC_HOSTS.put(CATEGARY_XIBANYA, "www.amazon.es");
//		MSG_GOOD_SRC_HOSTS.put(CATEGARY_CN, "www.amazon.cn");
		MSG_GOOD_SRC_HOSTS.put(CATEGARY_UK, "www.amazon.co.uk");
		MSG_GOOD_SRC_HOSTS.put(CATEGARY_JP, "www.amazon.co.jp");
		MSG_GOOD_SRC_HOSTS.put(CATEGARY_ITALY, "www.amazon.it");
		MSG_GOOD_SRC_HOSTS.put(CATEGARY_EBAY, "www.ebay.com");
	}
	
}
