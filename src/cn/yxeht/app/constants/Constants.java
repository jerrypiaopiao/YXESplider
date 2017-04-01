package cn.yxeht.app.constants;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.yxeht.app.bean.MerInfoBean;
import cn.yxeht.app.bean.YXETypeMatch;
import cn.yxeht.app.biz.rule.GoodListRule;
import cn.yxeht.app.controller.YXEController;
import cn.yxeht.app.core.YXEConfLoad;

public class Constants {

	public static final String E_BAY_SRC_URL = "http://www.ebay.com/itm/";

	public static final int GUANG_DIU = 1;// 逛丢
	public static final int MAI_SHOU_DANG = 2;// 买手党
	public static final int SHENME_ZHIDE_MAI = 3;// 什么值得买
	public static final int WUWU_HAITAO = 4;// 55海淘
	public static final int MEI_DE_BI = 5;// 没得比
	public static final int BEI_MEI_SHENG_QIAN_KUAI_BAO = 6;// 北美省钱快报
	
	/**
	 * 原始链接+tag
	 */
	public static final int MER_UNION_TYPE_TAG = 1;//原始链接+tag
	/**
	 * 接入链接+原始链接+tag
	 */
	public static final int MER_UNION_TYPE_URL_SRC_TAG = 2;//接入链接+原始链接+tag
	/**
	 * 接入链接含tag+原始链接
	 */
	public static final int MER_UNION_TYPE_URLTAG_SRC = 3;//接入链接含tag+原始链接
	/**
	 * 接入链接
	 */
	public static final int MER_UNION_TYPE_URL = 5;//接入链接

	/**
	 * WEB-INF目录相对路径
	 */
	public static final String PATH_WEB_INF = "WEB-INF"+File.separator;
	/**
	 * conf目录相对路径
	 */
	public static final String PATH_YXE_CONF = "conf"+File.separator;
	
	/**
	 * 加载目标网站配置信息,在{@link YXEController#startFetchOnQuartz()}、{@link YXEController#loadSpliderTarget()}中实现了初始填充
	 */
	public static final List<String> TARGET_WEBSITE_LIST = new ArrayList<String>();
	/**
	 * 目标网站配置信息,比如55haitaoShort_4,bmsqkbShort_6
	 */
	public static final String FILE_CONF_TARGET_WEBSITE = "splider_file_conf_target_website";
	
	/**
	 * 加载分类信息匹配信息,在{@link YXEConfLoad#loadTypeMatchRule(String)}、{@link YXEController#startFetchOnQuartz()}、{@link YXEController#loadTypeMatch()}、{@link YXEController#startFetch()}中实现了初始填充
	 */
	public static final List<YXETypeMatch> TYPE_MATCH_RULE_LIST = new ArrayList<YXETypeMatch>();
	/**
	 * 分配匹配信息文件名，比如:type_match_rule
	 */
	public static final String KEY_FN_TYPE_MATCH_RULE = "splider_file_conf_type_match_rule";
	
	/**
	 * 目标网站配置信息,恒定变量,在{@link YXEController#startFetchOnQuartz()}中实现了初始填充
	 * <br/>
	 * 已无用
	 * @deprecated
	 */
	public static final Map<String, List<MerInfoBean>> ALL_TARGET_FETCH_RULE = new HashMap<String, List<MerInfoBean>>();
	
	/**
	 * 目标网站配置信息,临时变量,在{@link YXEController#startFetchOnQuartz()}中实现了初始填充
	 */
//	public static final Map<String, List<MerInfoBean>> TMP_TARGET_FETCH_RULE = new HashMap<String, List<MerInfoBean>>();
	
	/**
	 * 目标网站配置信息,已用变量,这个变量目前暂时没有用处
	 */
	@Deprecated
	public static final Map<String, List<MerInfoBean>> USED_TARGET_FETCH_RULE = new HashMap<String, List<MerInfoBean>>();
	
	/**
	 * 当前正在执行的抓取规则,即在抓取队列中的抓取目标网站信息,在{@link YXEConfLoad#autoFetch(String)}中进行填充以及使用操作
	 */
	public static final List<GoodListRule> ON_FETCHING_RULE = new ArrayList<GoodListRule>();
	
}
