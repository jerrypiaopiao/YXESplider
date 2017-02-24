package cn.yxeht.app.core;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import com.jfinal.kit.PropKit;

import cn.yxeht.app.AppConfig;
import cn.yxeht.app.bean.MerInfoBean;
import cn.yxeht.app.bean.YXETypeMatch;
import cn.yxeht.app.biz.rule.BaseRule;
import cn.yxeht.app.biz.rule.DetailFetchRule;
import cn.yxeht.app.biz.rule.GoodListRule;
import cn.yxeht.app.constants.Constants;
import cn.yxeht.app.controller.YXEController;
import cn.yxeht.app.utils.GsonUtils;
import cn.yxeht.app.utils.TextUtil;

public class YXEConfLoad {

	private static final String TAG = "LoadConf";

	private static final Logger log = Logger.getLogger(YXEConfLoad.class);

	/**
	 * 加载目标网站配置信息,对应目标list为{@link Constants#TARGET_WEBSITE_LIST},List"String"
	 */
	public static void loadSpliderTargetInfo() {
		log.info(AppConfig.formatLog("load splider target info, like '55haitaoSort'"));
		String targetWebsite = PropKit.get(Constants.FILE_CONF_TARGET_WEBSITE, "splider_file_conf_target_website");
		String[] targetWebsites = targetWebsite.split(",");

		for (String target : targetWebsites) {
			if (!Constants.TARGET_WEBSITE_LIST.contains(target)) {
				Constants.TARGET_WEBSITE_LIST.add(target);
			}
		}

	}
	
	/**
	 * 加载商品列表抓取规则
	 * @param refresh
	 * 						是否需要刷新缓存列表
	 * @param path
	 * 						项目路径,对应 getRequest().getSession().getServletContext().getRealPath("/"),
	 * @param targetWebs
	 * 						是否抓取指定网站,比如有参数[55haitaoShort_4]
	 */
	public static void loadFetchGoodListRule(boolean refresh,String path, String... targetWebs){
		log.info(AppConfig.formatLog("load splider good list fetch rule, like good list on 55haitao"));
		if(Constants.TARGET_WEBSITE_LIST == null || Constants.TARGET_WEBSITE_LIST.size() == 0){
			loadSpliderTargetInfo();
		}else if(refresh){
			Constants.TARGET_WEBSITE_LIST.clear();
			loadSpliderTargetInfo();
		}
		
		if(refresh){
			Constants.ALL_TARGET_FETCH_RULE.clear();
//			Constants.TMP_TARGET_FETCH_RULE.clear();
		}
		
		String targetWeb = null;
		if(targetWebs != null && targetWebs.length > 0){
			targetWeb = targetWebs[0];
		}
		
		Constants.ON_FETCHING_RULE.clear();
		for(String target : Constants.TARGET_WEBSITE_LIST){
			String[] merHostArr = null;
			String typeStr = null;
			String tpyeInt = null;
			
			if(!TextUtil.isEmpty(target) && target.contains("_")){
				merHostArr = target.split("_");
				typeStr = merHostArr[0];
				tpyeInt = merHostArr[1];
			}
			
			if(!TextUtil.isEmpty(targetWeb)){
				if(!targetWeb.equals(target) && !"--".equals(targetWeb)){
					continue;
				}
			}
			
			List<MerInfoBean> goodRules = YXEConfLoad.loadMerInfoByKey(path, typeStr);
			Constants.ALL_TARGET_FETCH_RULE.put(target, goodRules);
		}
		
		Iterator<String> keySets = Constants.ALL_TARGET_FETCH_RULE.keySet().iterator();
		int i = 0;
		while (keySets.hasNext()) {
			String key = keySets.next();
			String[] merTypeArr = null;
			String type = null;
			String tpyeInt = null;
			String fetchStyle = null;
			if (!TextUtil.isEmpty(key) && key.contains("_")) {
				merTypeArr = key.split("_");
				type = merTypeArr[0];
				tpyeInt = merTypeArr[1];
				fetchStyle = YXEConfLoad.getStyleByTargetWeb(Integer.valueOf(tpyeInt));
			}
			List<MerInfoBean> merInfos = Constants.ALL_TARGET_FETCH_RULE.get(key);
			String cc = merInfos == null ? "null" : String.valueOf(merInfos.size());
			log.info(AppConfig.formatLog("------------------->load splider good list fetch rule#merInfos[" + key + "]:" + cc + "<-------------------"));
			if (merInfos != null && merInfos.size() == 0) {
				// 这里复位key位置上的List<MerInfoBean> merInfos
				List<MerInfoBean> goodRules = YXEConfLoad.loadMerInfoByKey(YXEController.ROOT_FILE_PATH, type);
				if (Constants.ALL_TARGET_FETCH_RULE.get(key).size() == 0) {
					Constants.ALL_TARGET_FETCH_RULE.get(key).addAll(goodRules);
				}
				log.info(AppConfig.formatLog("------------------->load splider good list fetch rule#" + "reset merInfos on  key[" + key + "], count is [" + Constants.ALL_TARGET_FETCH_RULE.get(key).size() + "]<-------------------"));
			}
			log.info(AppConfig.formatLog("------------------->load splider good list fetch rule#" + "merInfos on  fetch rule key[" + key + "], count is [" + (merInfos == null ? "null" : merInfos.size()) + "]<-------------------"));
			if (merInfos != null && merInfos.size() > 0) {
				int c = merInfos.size();
				for (int r = 0; r < c; r++) {
					try {
						MerInfoBean mib = merInfos.get(r);
						GoodListRule listRule = SpliderService.createFetchRules(mib.getMerName(), mib.getMerHost(), mib.getMerType(), BaseRule.GET, mib.getSrcHost(), mib.getSrcLink(), fetchStyle, mib.getSrcLink());
						listRule.setFreeStr(key);
						//这一步应该放在SpliderService.createFetchRules中完成
						listRule.setTargetType(Integer.valueOf(tpyeInt));
						Constants.ON_FETCHING_RULE.add(listRule);
					} catch (Exception e) {
						log.info(e.getLocalizedMessage(), e.getCause());
						continue;
					}
				}
			}
			i++;
		}
		
	}

	/**
	 * 加载商家信息,List"MerInfoBean"
	 * 
	 * @param filePath
	 * @param key
	 * @return
	 */
	public static List<MerInfoBean> loadMerInfoByKey(String filePath, String key) {

		String fullPath = filePath + Constants.PATH_WEB_INF + Constants.PATH_YXE_CONF + key;
		String json = TextUtil.readTxtFromFile(fullPath);
		List<MerInfoBean> list = GsonUtils.jsonToList(json, MerInfoBean.class);

		log.info(AppConfig.formatLog("load merchant info by [" + key + "] from [" + fullPath + "]"));

		return list;

	}

	/**
	 * 加载分类信息匹配信息,对应{@link Constants#TYPE_MATCH_RULE_LIST},List"YXETypeMatch"
	 * 
	 * @param filePath
	 */
	public static void loadTypeMatchRule(String filePath) {

		String typeMatchRuleFN = PropKit.get(Constants.KEY_FN_TYPE_MATCH_RULE, "type_match_rule");

		String fullPath = filePath + Constants.PATH_WEB_INF + Constants.PATH_YXE_CONF + typeMatchRuleFN;

		String typeMatchRuleJson = TextUtil.readTxtFromFile(fullPath);
		List<YXETypeMatch> tmpList = GsonUtils.jsonToList(typeMatchRuleJson, YXETypeMatch.class);
		Constants.TYPE_MATCH_RULE_LIST.clear();
		Constants.TYPE_MATCH_RULE_LIST.addAll(tmpList);

		log.info(AppConfig.formatLog("load type match rule from file [" + fullPath + "], the count is [" + tmpList.size() + "]"));

	}

	public static String getStyleByTargetWeb(int merType) {
		log.info(AppConfig.formatLog("create good detailLink list fetch rule in CSS selection by merType[" + merType + "]"));
		String style = null;
		switch (merType) {
		case Constants.GUANG_DIU:
			style = "0,goodname";
			break;
		case Constants.MAI_SHOU_DANG:
			break;
		case Constants.SHENME_ZHIDE_MAI:
			// style = "0,itemName";
			// style = "2,ul[id=feed-main-list]#0,[class=feed-block-title]";
			style = "0,feed-block-title";
			break;
		case Constants.WUWU_HAITAO:
			// style = "2,div[id=deals_new]#0,[class=index-deal-title]";
			style = "2,ul[id=deal_list]#0,[class=index-deal-title]";
			break;
		case Constants.MEI_DE_BI:
			// style = "0,tit";
			style = "2,ul[class=clearfix list_grid]#0,[class=tit]";
			break;
		case Constants.BEI_MEI_SHENG_QIAN_KUAI_BAO:
			style = "0,cnlist_realheight";
			break;
		}

		return style;

	}

	public static DetailFetchRule detailFetchRule(int merType) {
		DetailFetchRule dfr = new DetailFetchRule();
		log.info(AppConfig.formatLog("create good detail info fetch rule by target type [" + merType + "]"));
		switch (merType) {
		case Constants.GUANG_DIU:
			// targetName = "逛丢";
			dfr.setTitleCssStyle("a[class=dtitlelink]");
			dfr.setDescCssStyle("div[id=dabstract]#p,1");
			dfr.setTrueLinkCssStyle("a[class=dtitlegotobuy]#link,rel=canonical");
			dfr.setImgCssStyle("div[class=image-panel]#div[id=dabstract]");
			dfr.setTypeCssStyle("div[class=crumbs]#a,2");
			break;
		case Constants.MAI_SHOU_DANG:
			// targetName = "买手党";
			break;
		case Constants.SHENME_ZHIDE_MAI:
			// targetName = "什么值得买";
			dfr.setTitleCssStyle("div[class=article-right]#h1");// h1[class=article_title]
			dfr.setDescCssStyle("div[class=item-box item-preferential]#p,2");
			dfr.setTrueLinkCssStyle("div[class=buy]#link");
			dfr.setImgCssStyle("img[itemprop=image]");
			dfr.setTypeCssStyle("div[class=crumbs]#a,2");
			break;
		case Constants.WUWU_HAITAO:
			// targetName = "55海淘";
			dfr.setTitleCssStyle("div[class=ht-deal-detail-title clearfix]#h1,h2");
			dfr.setDescCssStyle("div[class=ht-deal-detail-des-box]#p,1");
			dfr.setTrueLinkCssStyle("a[class=ht-deal-detail-buy-btn]#a,class=btn-go-look");// link,rel=canonical");
			dfr.setImgCssStyle("div[class=ht-deal-detail-buy]");
			dfr.setTypeCssStyle("p[class=ht-deal-detail-info-tag]#a,2");
			break;
		case Constants.MEI_DE_BI:
			// targetName = "没得比";
			dfr.setTitleCssStyle("h2[class=d-title]");
			dfr.setDescCssStyle("div[id=infoDesc]#p,1");
			dfr.setTrueLinkCssStyle("a[class=mdb-button mdb-button-orange mdb-button-large out-link]#link,rel=canonical");
			dfr.setImgCssStyle("div[class=d-output fr]");
			dfr.setTypeCssStyle("div[class=d-crumb gray9]#a,2");
			break;
		case Constants.BEI_MEI_SHENG_QIAN_KUAI_BAO:
			// targetName = "北美省钱快报";
			dfr.setTitleCssStyle("div[class=article_title]");
			dfr.setDescCssStyle("div[class=minfor  event_statistics]#p,1");
			dfr.setTrueLinkCssStyle("div[class=buy]#meta");
			dfr.setImgCssStyle("div[class=img_wrap]");
			dfr.setTypeCssStyle("div[class=mbx]#a,1");
			break;
		}

		return dfr;

	}

	public synchronized static void autoFetch(String filePath) {
		/// 根据当前情况创建{&&
		log.info(AppConfig.formatLog("------------------->start auto fetch(ROOT_FILE_PATH:" + YXEController.ROOT_FILE_PATH + ")<-------------------"));
		// 清除正在运行的队列
		Constants.ON_FETCHING_RULE.clear();
//		Iterator<String> keySets = Constants.TMP_TARGET_FETCH_RULE.keySet().iterator();
		Iterator<String> keySets = Constants.ALL_TARGET_FETCH_RULE.keySet().iterator();
		int i = 0;
		while (keySets.hasNext()) {
			String key = keySets.next();
			String[] merTypeArr = null;
			String type = null;
			String tpyeInt = null;
			String fetchStyle = null;
			if (!TextUtil.isEmpty(key) && key.contains("_")) {
				merTypeArr = key.split("_");
				type = merTypeArr[0];
				tpyeInt = merTypeArr[1];
				fetchStyle = YXEConfLoad.getStyleByTargetWeb(Integer.valueOf(tpyeInt));
			}
//			List<MerInfoBean> merInfos = Constants.TMP_TARGET_FETCH_RULE.get(key);
			List<MerInfoBean> merInfos = Constants.ALL_TARGET_FETCH_RULE.get(key);
			String cc = merInfos == null ? "null" : String.valueOf(merInfos.size());
			log.info(AppConfig.formatLog("------------------->start auto fetch#merInfos[" + key + "]:" + cc + "<-------------------"));
			if (merInfos != null && merInfos.size() == 0) {
				// 这里复位key位置上的List<MerInfoBean> merInfos
				List<MerInfoBean> goodRules = YXEConfLoad.loadMerInfoByKey(YXEController.ROOT_FILE_PATH, type);
				if (Constants.ALL_TARGET_FETCH_RULE.get(key).size() == 0) {
					Constants.ALL_TARGET_FETCH_RULE.get(key).addAll(goodRules);
				}
				log.info(AppConfig.formatLog("------------------->start auto fetch#" + "reset merInfos on  key[" + key + "], count is [" + Constants.ALL_TARGET_FETCH_RULE.get(key).size() + "]<-------------------"));
			}
			log.info(AppConfig.formatLog("------------------->start auto fetch#" + "merInfos on  fetch rule key[" + key + "], count is [" + (merInfos == null ? "null" : merInfos.size()) + "]<-------------------"));
			if (merInfos != null && merInfos.size() > 0) {
				int c = merInfos.size();
				for (int r = 0; r < c; r++) {
					try {
						MerInfoBean mib = merInfos.get(r);
						// merInfos.remove(mib);
						GoodListRule listRule = SpliderService.createFetchRules(mib.getMerName(), mib.getMerHost(), mib.getMerType(), BaseRule.GET, mib.getSrcHost(), mib.getSrcLink(), fetchStyle, mib.getSrcLink());
						listRule.setFreeStr(key);
						Constants.ON_FETCHING_RULE.add(listRule);
					} catch (Exception e) {
						log.info(e.getLocalizedMessage(), e.getCause());
						continue;
					}
				}
			}
			i++;
		}

		log.info(AppConfig.formatLog("------------------->start auto fetch#" + "merInfos on  fetch rule count is [" + Constants.ON_FETCHING_RULE.size() + "]<-------------------"));
		SpliderService.autoFetchGood(Constants.ON_FETCHING_RULE, null);
		/// &&}
	}

}
