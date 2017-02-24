package cn.yxeht.app.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 什么值得买抓取链接
 * @author Administrator
 *
 */
public class SMZDMFetchRule {

	/**
	 * 美亚--
	 * 英亚--
	 * 日亚--
	 * 德亚--
	 * 6pm--
	 * ebay--
	 * 新百伦outlet--
	 * finishline--
	 * ashford--
	 * drugstore--
	 * Ralph Lauren(商城有，发现频道没有)
	 * 尼曼(商城有，发现频道没有)
	 * REI--
	 * chemiswarehouse
	 * 中国亚马逊 --
	 * ORIGINS
	 * STP--
	 * backcountry
	 * 新蛋美国--
	 * The  Hut
	 */
	public static final String HOST = "http://haitao.smzdm.com/";//"http://faxian.smzdm.com/";
	
//	public static final String CATEGARY_URL = "mall/";
	
	/**
	 * 美国亚马逊
	 */
	public static final String CATEGARY_A_USA = "xuan/s41f0t0p1/";//"美国亚马逊";//
	
	/**
	 * 中国亚马逊,对应中国海外购
	 */
	public static final String CATEGARY_A_CN = "mall/amazon_b/faxian/#tabs";//"%E4%BA%9A%E9%A9%AC%E9%80%8A%E4%B8%AD%E5%9B%BD";//"亚马逊中国";//
	
	/**
	 * 日本亚马逊
	 */
	public static final String CATEGARY_A_JP = "xuan/s271f0t0p1/";//"%E6%97%A5%E6%9C%AC%E4%BA%9A%E9%A9%AC%E9%80%8A";//"日本亚马逊";//
	
	/**
	 * 德国亚马逊
	 */
	public static final String CATEGARY_A_DE = "xuan/s155f0t0p1/";//"%E5%BE%B7%E5%9B%BD%E4%BA%9A%E9%A9%AC%E9%80%8A";//"德国亚马逊";//
	
	/**
	 * 英国亚马逊
	 */
	public static final String CATEGARY_A_UK = "xuan/s279f0t0p1/";//"%E8%8B%B1%E5%9B%BD%E4%BA%9A%E9%A9%AC%E9%80%8A";//"英国亚马逊";//
	
	/**
	 * ebay
	 */
	public static final String CATEGARY_EBAY = "xuan/s73f0t0p1/";//"ebay";
	
	/**
	 * 6pm
	 */
	public static final String CATEGARY_6PM = "xuan/s49f0t0p1/";//"6pm";
	
	/**
	 * Finish Line
	 */
	public static final String CATEGARY_FINISH_LINE = "xuan/s75f0t0p1/";//"Finish%20Line";//"Finish Line";
	
	/**
	 * Ashford
	 */
	public static final String CATEGARY_ASHFORD = "xuan/s51f0t0p1/";//"Ashford";
	
	/**
	 * Drugstore
	 */
//	public static final String CATEGARY_DRUGSTORE = "Drugstore";
	
	/**
	 * rei
	 */
//	public static final String CATEGARY_REI = "rei";
	
	/**
	 * 新百伦折扣店
	 */
	public static final String CATEGARY_NB_OUTLET = "xuan/s95f0t0p1/";//"Joe's%20NB%20Outlet";//"Joe's NB Outlet";//"Joe's%20NB%20Outlet";
	
	/**
	 * STP
	 */
	public static final String CATEGARY_STP = "xuan/s751f0t0p1/";//"STP";
	
	/**
	 * 意大利亚马逊
	 */
	public static final String CATERARY_ITALY = "mall/amazon_it/haitao/#tabs";//"amazon_it/faxian/#tabs";
	
	/**
	 * 西班牙亚马逊
	 */
	public static final String CATERARY_XIBANYA = "mall/amazon_es/haitao/#tabs";//"amazon_es/faxian/#tabs";
	
	/**
	 * 美国新蛋
	 */
	public static final String CATEGARY_USA_NEW_EGG = "mall/newegg/";//"newegg/faxian/#tabs";//"美国新蛋";//
	
	public static final String CATEGARY_THE_HUT = "xuan/s2703f0t0p1/";//"thehut/faxian/#tabs";
	
	public static final String CATEGARY_LOOK_FANTASTIC = "xuan/s973f0t0p1/";//"lookfantastic/faxian/#tabs";
	
	/**
	 * 韩国乐天
	 */
	public static final String CATEGARY_LOTTE = "xuan/s2677f0t0p1/";//"lotte/faxian/#tabs";
	
	/**
	 * 德国 W家
	 */
	public static final String CATRGARY_WINDELN = "mall/windeln/haitao/#tabs";//"windeln/faxian/#tabs";
	
	/**
	 * 乐天国际
	 */
	public static final String CATEGARY_RAKUTEN_GLOBAL = "xuan/s1111f0t0p1/";//"rakuten_global/faxian/#tabs";
	
	/**
	 * wiggle中国
	 */
	public static final String CATEGARY_WIGGLE = "mall/wiggle/haitao/#tabs";//"wiggle/faxian/#tabs";
	
	/**
	 * JIMMY JAZZ
	 */
	public static final String CATEGARY_JIMMY_JAZZ = "mall/jimmyjazz/haitao/#tabs";//"jimmyjazz/faxian/#tabs";
	
	public static final String CATEGARY_RALPH_LAUREN = "mall/ralphlauren/";//"ralphlauren/faxian/#tabs";
	
	public static final List<String> SMZDM_GOOD_CATEGARY_CODE = new ArrayList<String>();
	static{
		SMZDM_GOOD_CATEGARY_CODE.add(CATEGARY_A_USA);
		SMZDM_GOOD_CATEGARY_CODE.add(CATEGARY_A_CN);
		SMZDM_GOOD_CATEGARY_CODE.add(CATEGARY_A_JP);
		SMZDM_GOOD_CATEGARY_CODE.add(CATEGARY_A_DE);
		SMZDM_GOOD_CATEGARY_CODE.add(CATEGARY_A_UK);
		SMZDM_GOOD_CATEGARY_CODE.add(CATERARY_ITALY);
		SMZDM_GOOD_CATEGARY_CODE.add(CATERARY_XIBANYA);
		SMZDM_GOOD_CATEGARY_CODE.add(CATEGARY_EBAY);
		SMZDM_GOOD_CATEGARY_CODE.add(CATEGARY_6PM);
		SMZDM_GOOD_CATEGARY_CODE.add(CATEGARY_FINISH_LINE);
		SMZDM_GOOD_CATEGARY_CODE.add(CATEGARY_ASHFORD);
//		SMZDM_GOOD_CATEGARY_CODE.add(CATEGARY_DRUGSTORE);
//		SMZDM_GOOD_CATEGARY_CODE.add(CATEGARY_REI);
		SMZDM_GOOD_CATEGARY_CODE.add(CATEGARY_NB_OUTLET);
		SMZDM_GOOD_CATEGARY_CODE.add(CATEGARY_STP);
		//以下为新增信息
		SMZDM_GOOD_CATEGARY_CODE.add(CATEGARY_USA_NEW_EGG);
		SMZDM_GOOD_CATEGARY_CODE.add(CATEGARY_THE_HUT);
		SMZDM_GOOD_CATEGARY_CODE.add(CATEGARY_LOOK_FANTASTIC);
		SMZDM_GOOD_CATEGARY_CODE.add(CATEGARY_LOTTE);
		SMZDM_GOOD_CATEGARY_CODE.add(CATRGARY_WINDELN);
		SMZDM_GOOD_CATEGARY_CODE.add(CATEGARY_RAKUTEN_GLOBAL);
		SMZDM_GOOD_CATEGARY_CODE.add(CATEGARY_WIGGLE);
		SMZDM_GOOD_CATEGARY_CODE.add(CATEGARY_JIMMY_JAZZ);
		SMZDM_GOOD_CATEGARY_CODE.add(CATEGARY_RALPH_LAUREN);
	}
	
	public static final Map<String, String> SMZDM_GOOD_SRC_HOSTS = new HashMap<String, String>();
	static{
		SMZDM_GOOD_SRC_HOSTS.put(CATEGARY_A_USA, "www.amazon.com");//-
		SMZDM_GOOD_SRC_HOSTS.put(CATEGARY_A_CN, "www.amazon.cn");//未成功
		SMZDM_GOOD_SRC_HOSTS.put(CATEGARY_A_JP, "www.amazon.co.jp");//-
		SMZDM_GOOD_SRC_HOSTS.put(CATEGARY_A_DE, "www.amazon.de");//-
		SMZDM_GOOD_SRC_HOSTS.put(CATEGARY_A_UK, "www.amazon.co.uk");//-
		SMZDM_GOOD_SRC_HOSTS.put(CATERARY_ITALY, "www.amazon.it");//未成功
		SMZDM_GOOD_SRC_HOSTS.put(CATERARY_XIBANYA, "www.amazon.es");//未成功
		SMZDM_GOOD_SRC_HOSTS.put(CATEGARY_EBAY, "www.ebay.com");//-
		SMZDM_GOOD_SRC_HOSTS.put(CATEGARY_6PM, "www.6pm.com");//-
		SMZDM_GOOD_SRC_HOSTS.put(CATEGARY_FINISH_LINE, "www.finishline.com");//-
		SMZDM_GOOD_SRC_HOSTS.put(CATEGARY_ASHFORD, "www.ashford.com");//联盟接法与6pm一样， SMZDM抓取有异常//-
//		SMZDM_GOOD_SRC_HOSTS.put(CATEGARY_DRUGSTORE, "www.drugstore.com");//联盟接法与finishline
//		SMZDM_GOOD_SRC_HOSTS.put(CATEGARY_REI, "www.rei.com");
		SMZDM_GOOD_SRC_HOSTS.put(CATEGARY_NB_OUTLET, "www.joesnewbalanceoutlet.com");//联盟接法与finish line一样,SMZDM抓取有异常//未完成，url转换为空
		SMZDM_GOOD_SRC_HOSTS.put(CATEGARY_STP, "www.sierratradingpost.com");//-
		//以下为新增内容
		SMZDM_GOOD_SRC_HOSTS.put(CATEGARY_USA_NEW_EGG, "www.newegg.com");//-
		SMZDM_GOOD_SRC_HOSTS.put(CATEGARY_THE_HUT, "www.thehut.com");//-
		SMZDM_GOOD_SRC_HOSTS.put(CATEGARY_LOOK_FANTASTIC, "www.lookfantastic.com");//-
		SMZDM_GOOD_SRC_HOSTS.put(CATEGARY_LOTTE, "global.lotte.com");//-
		SMZDM_GOOD_SRC_HOSTS.put(CATRGARY_WINDELN, "www.windeln.de");//未完成
		SMZDM_GOOD_SRC_HOSTS.put(CATEGARY_RAKUTEN_GLOBAL, "global.rakuten.com");//-
		SMZDM_GOOD_SRC_HOSTS.put(CATEGARY_WIGGLE, "www.wiggle.cn");//未完成
		SMZDM_GOOD_SRC_HOSTS.put(CATEGARY_JIMMY_JAZZ, "www.jimmyjazz.com");//未完成
		SMZDM_GOOD_SRC_HOSTS.put(CATEGARY_RALPH_LAUREN, "www.ralphlauren.com");//-
	}
	
	public static final List<String> URL_START_WWW = new ArrayList<String>();
	static{
		URL_START_WWW.add("www.amazon.cn");
		URL_START_WWW.add("www.amazon.es");
		URL_START_WWW.add("www.amazon.it");
		URL_START_WWW.add("www.newegg.com");
//		URL_START_WWW.add("www.thehut.com"); //--
//		URL_START_WWW.add("www.lookfantastic.com");
//		URL_START_WWW.add("global.lotte.com"); //--
		URL_START_WWW.add("www.windeln.de");
//		URL_START_WWW.add("global.rakuten.com");//--
		URL_START_WWW.add("www.wiggle.cn");
		URL_START_WWW.add("www.jimmyjazz.com");
		URL_START_WWW.add("www.ralphlauren.com");
	}
	
	
	public static final List<String> WORD_FILTER = new ArrayList<String>();
	static{
		WORD_FILTER.add("值友");
		WORD_FILTER.add("点击购买>>");
		WORD_FILTER.add("点击购买");
		WORD_FILTER.add("产品介绍：");
		WORD_FILTER.add("产品介绍");
		WORD_FILTER.add("比友");
		WORD_FILTER.add("最新版直邮攻略");
		WORD_FILTER.add("商品详情：");
		WORD_FILTER.add("商品详情");
		WORD_FILTER.add("点此去官网");
		WORD_FILTER.add("官网地址");
		WORD_FILTER.add("直达链接");
		WORD_FILTER.add("什么值得买");
		WORD_FILTER.add("官网链接在此");
		WORD_FILTER.add("推荐的价格不对？");
//		WORD_FILTER.add("推荐价格");
//		WORD_FILTER.add("");
	}
	
}
