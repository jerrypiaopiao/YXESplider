package cn.yxeht.app.constants;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import cn.yxeht.app.AppConfig;
import cn.yxeht.app.bean.BizUserTags;
import cn.yxeht.app.biz.rule.BaseRule;
import cn.yxeht.app.biz.rule.GoodListRule;

public class AmazonCfgInfo {

	private static final Logger log = Logger.getLogger(AmazonCfgInfo.class);
	
	public static final List<String> ALL_MERCHANT_HOST_LIST = new ArrayList<String>();
	
	static{
		log.info(AppConfig.formatLog("create business link set"));
		//西班牙
		ALL_MERCHANT_HOST_LIST.add("https://www.amazon.es");
		ALL_MERCHANT_HOST_LIST.add("http://www.amazon.es");
		//中国
		ALL_MERCHANT_HOST_LIST.add("https://www.amazon.cn");
		ALL_MERCHANT_HOST_LIST.add("http://www.amazon.cn");
		//英国
		ALL_MERCHANT_HOST_LIST.add("https://www.amazon.co.uk");
		ALL_MERCHANT_HOST_LIST.add("http://www.amazon.co.uk");
		//日本
		ALL_MERCHANT_HOST_LIST.add("https://www.amazon.co.jp");
		ALL_MERCHANT_HOST_LIST.add("http://www.amazon.co.jp");
		//意大利
		ALL_MERCHANT_HOST_LIST.add("https://www.amazon.it");
		ALL_MERCHANT_HOST_LIST.add("http://www.amazon.it");
		//美国
		ALL_MERCHANT_HOST_LIST.add("http://www.amazon.com");
		ALL_MERCHANT_HOST_LIST.add("https://www.amazon.com");
		//ebay
		ALL_MERCHANT_HOST_LIST.add("http://rover.ebay.com");
		ALL_MERCHANT_HOST_LIST.add("https://rover.ebay.com");
		//6pm
		ALL_MERCHANT_HOST_LIST.add("http://www.6pm.com");
		ALL_MERCHANT_HOST_LIST.add("https://www.6pm.com");
		
	}
	
	public static final HashMap<String, BizUserTags> ALL_AMAZON_HOST_TAG_MATCH = new HashMap<String, BizUserTags>();
	
	/*
	 * tisantakis
	 * mutou
	 */
	//TODO 买手党抓取任务未完成
	//TODO 逛丢美亚使用tag匹配方式未完成
	
	/*
	 * 英亚tag=666adang-21
	 * 法亚tag=adanglovefr-21
	 * 意亚tag=rabyguo0e-21
	 * 西亚tag=xuanzhi0b-21
	 * 中亚tag=xiao2-23
	 * 日本亚马逊：在链接前面加上这段 
	 * http://count.chanet.com.cn/click.cgi?a=527989&d=381499&u=suyeee&e=1007&url=
	 * 【范例：http://count.chanet.com.cn/click.cgi?a=527989&d=381499&u=suyeee&e=1007&url=https://www.amazon.co.jp/gp/product/B00D2LQ1L6】
	 */
	static{
//		log.info(AppConfig.formatLog("the eidtor info init"));
		//西班牙,这里这样配置可能会让后台的统计数据除问题
//		ALL_AMAZON_HOST_TAG_MATCH.put("www.amazon.es", new BizUserTags("5", "mutou", "xuanzhi0b-21"));//"666adang-21"));
		//英国
//		ALL_AMAZON_HOST_TAG_MATCH.put("www.amazon.co.uk", new BizUserTags("5", "mutou", "666adang-21"));
		//日本
//		ALL_AMAZON_HOST_TAG_MATCH.put("www.amazon.co.jp", new BizUserTags("5", "mutou", "xxxxxx"));
		//意大利
//		ALL_AMAZON_HOST_TAG_MATCH.put("www.amazon.it", new BizUserTags("289", "tisantakis", "rabyguo0e-21"));
		//美国
//		ALL_AMAZON_HOST_TAG_MATCH.put("www.amazon.com", new BizUserTags("5", "mutou", "yx2x2-20"));
		//中国
//		ALL_AMAZON_HOST_TAG_MATCH.put("www.amazon.cn", new BizUserTags("5", "mutou", "xiao2-23"));
	}
	
	public static final List<GoodListRule> GOOD_LIST_FETCH_RULES = new ArrayList<GoodListRule>();
	static{
		log.info(AppConfig.formatLog("init fetch rule, the current fetch rule["+GOOD_LIST_FETCH_RULES.size()+"]"));
		if(GOOD_LIST_FETCH_RULES.size() > 0){
			log.info(AppConfig.formatLog("clear fetch rule"));
			GOOD_LIST_FETCH_RULES.clear();
		}
		GOOD_LIST_FETCH_RULES.addAll(createGuangDiuFetchRules());
		GOOD_LIST_FETCH_RULES.addAll(createMSDFetchRules());
		GOOD_LIST_FETCH_RULES.addAll(createSMZDMRules());
	}

	public static List<GoodListRule> createMSDFetchRules(){
		List<GoodListRule> rules = new ArrayList<GoodListRule>();
		log.info(AppConfig.formatLog("create \"MaiShouDang\" fetch rules"));
		for(String code : MSDFetchRule.MSD_GOOD_CATEGARY_CODE){
//			log.info("创建\"买手党\"抓取规则......["+code+"]["+MSDFetchRule.MSD_GOOD_CATEGARY_NAME.get(code)+"]");
			String goodHost = MSDFetchRule.MSG_GOOD_SRC_HOSTS.get(code);
			GoodListRule rule = new GoodListRule();
			rule.setRuleName(MSDFetchRule.MSD_GOOD_CATEGARY_NAME.get(code));
			rule.setRequestMoethod(BaseRule.GET);
			rule.setType(BaseRule.CLASS);
			rule.setClassName("tb-i-read");
//			rule.setGoodHost(goodHosts);
			rule.setHost(MSDFetchRule.HOST);
			rule.setDefaultAmazonHost(goodHost);
			rule.setGoodListUrl(MSDFetchRule.CATEGARY_URL+code);
			rule.setSourceType(Constants.MAI_SHOU_DANG);
			rule.setGoodTypeCode(code);
			rules.add(rule);
		}
		
		return rules;
	}
	
	public static List<GoodListRule> createGuangDiuFetchRules(){
		List<GoodListRule> rules = new ArrayList<GoodListRule>();
		
		log.info(AppConfig.formatLog("create \"GuangDiu\" fetch rules"));
		for(String typeName : TypeMatchRule.GUANGDIU_TYPE_STRING){
//			log.info("创建\"逛丢\"抓取规则......["+typeName+"]");
			String goodTypeId = TypeMatchRule.getGoodTypeIdByNameGuangDiu(typeName);
			
			GoodListRule rule = new GoodListRule();
			rule.setRuleName("美国亚马逊");
			rule.setRequestMoethod(BaseRule.GET);
			rule.setClassName("goodname");
			System.err.println("逛丢:["+typeName+"]");
			List<String> goodHosts = new ArrayList<>();
			rule.setDefaultAmazonHost("www.amazon.com");
			rule.setGoodHost(goodHosts);
			rule.setHost("http://guangdiu.com/");
			rule.setGoodListUrl("cate.php?m=Amazon&k="+TypeMatchRule.GUANGDIU_TYPE_MATCH_RULE.get(goodTypeId)+"&c=us");
			rule.setType(BaseRule.CLASS);
			rule.setSourceType(Constants.GUANG_DIU);
			rule.setGoodTypeCode(goodTypeId);
			rules.add(rule);
		}
		
		return rules;
	}
	
	public static List<GoodListRule> createSMZDMRules(){
			List<GoodListRule> rules = new ArrayList<GoodListRule>();
			log.info(AppConfig.formatLog("create \"SMZDM\" fetch rules"));
			for(String codeStr : SMZDMFetchRule.SMZDM_GOOD_CATEGARY_CODE){
				String goodHost = SMZDMFetchRule.SMZDM_GOOD_SRC_HOSTS.get(codeStr);//获取商家默认域名
//				String code = URLEncoder.encode(codeStr);
				GoodListRule rule = new GoodListRule();
				try {
					rule.setRuleName(URLDecoder.decode(codeStr));
				} catch (Exception e) {
					rule.setRuleName(codeStr);
				}
				rule.setRequestMoethod(BaseRule.GET);
//				rule.setClassName("picBox");
				rule.setType(BaseRule.CLASS);
				rule.setClassName("feed-block-title");//"z-btn z-btn-red");//"feed-link-btn-inner");
				List<String> goodHosts = new ArrayList<>();
				rule.setDefaultAmazonHost(goodHost);
				rule.setGoodHost(goodHosts);
				String smzdm = SMZDMFetchRule.HOST;
				rule.setHost(smzdm);
//				rule.setGoodListUrl(SMZDMFetchRule.CATEGARY_URL+codeStr);
				rule.setGoodListUrl(codeStr);
				
				rule.setSourceType(Constants.SHENME_ZHIDE_MAI);
				rule.setGoodTypeCode(codeStr);
				rules.add(rule);
			}
			
			return rules;
	}
	
}
