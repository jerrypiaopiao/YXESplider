package cn.yxeht.app.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.yxeht.app.AppConfig;
import cn.yxeht.app.bean.BizUserTags;
import cn.yxeht.app.bean.MerInfoBean;
import cn.yxeht.app.bean.YXETypeMatch;
import cn.yxeht.app.biz.bean.GoodDetail;
import cn.yxeht.app.biz.rule.BaseRule;
import cn.yxeht.app.biz.rule.DetailFetchRule;
import cn.yxeht.app.biz.rule.GoodListRule;
import cn.yxeht.app.constants.AmazonCfgInfo;
import cn.yxeht.app.constants.Constants;
import cn.yxeht.app.constants.MSDFetchRule;
import cn.yxeht.app.controller.YXEController;
import cn.yxeht.app.exception.JsoupDocException;
import cn.yxeht.app.quartz.AutoFetchJobV2;
import cn.yxeht.app.table.GoodInfo;
import cn.yxeht.app.table.Goods;
import cn.yxeht.app.table.Goodstype;
import cn.yxeht.app.table.Merchant;
import cn.yxeht.app.table.Picture;
import cn.yxeht.app.table.SpliderFilterWord;
import cn.yxeht.app.table.SpliderGoodType;
import cn.yxeht.app.table.SpliderInfo;
import cn.yxeht.app.utils.DateUtils;
import cn.yxeht.app.utils.JsoupUtil;
import cn.yxeht.app.utils.TextUtil;

public class SpliderService {

	private static final String TAG = SpliderService.class.getSimpleName();
	private static final Logger log = Logger.getLogger(SpliderService.class);

	public static List<YXETypeMatch> YXE_TYPE_MATCHES = null;

	/**
	 * @deprecated
	 * @param fullMerType
	 * @param merType
	 * @param fetchStyle
	 * @param merHost
	 * @param targetWebName
	 * @param merInfos
	 * @param dfr
	 */
	public synchronized static void fetchGoodByRule(String fullMerType, int merType, String fetchStyle, String merHost, String targetWebName, List<MerInfoBean> merInfos, DetailFetchRule dfr) {

		if (YXE_TYPE_MATCHES == null) {
			YXE_TYPE_MATCHES = new ArrayList<YXETypeMatch>();
			YXE_TYPE_MATCHES.addAll(Constants.TYPE_MATCH_RULE_LIST);
		}

		if (merInfos == null || merInfos.size() == 0) {
			return;
		}

		List<GoodListRule> ruleList = new ArrayList<GoodListRule>();

		for (int i = 0; i < merInfos.size(); i++) {
			MerInfoBean mib = merInfos.get(i);
			ruleList.add(createFetchRules(mib.getMerName(), mib.getMerHost(), mib.getMerType(), BaseRule.GET, mib.getSrcHost(), mib.getSrcLink(),
					fetchStyle/* fetchTagType //BaseRule.CLASS, classValue */, mib.getSrcLink()));
		}
		log.info(AppConfig.formatLog("fetchGoodByRule#target [" + targetWebName + "] has [" + ruleList.size() + "] rules."));
		fetchGoodByRules(ruleList, dfr, merHost);

	}

	/**
	 * 自动抓取
	 * @deprecated
	 * @param ruleList
	 * @param dfr
	 */
	public synchronized static void autoFetchGood(List<GoodListRule> ruleList, DetailFetchRule dfr) {

		log.info(AppConfig.formatLog("fetch good by rules [" + ruleList + "] and detailRule [" + dfr + "]"));

		if (YXE_TYPE_MATCHES == null) {
			YXE_TYPE_MATCHES = new ArrayList<YXETypeMatch>();
		} else {
			YXE_TYPE_MATCHES.clear();
		}

		YXE_TYPE_MATCHES.addAll(Constants.TYPE_MATCH_RULE_LIST);

		fetchGoodByRules(ruleList, dfr, null);
	}

	/**
	 * 抓取商品列表,将商品详情链接信息存入h_splider_info表中(规则自动生成),
	 * <br/>
	 * quartz ({@link AutoFetchJobV2}) 定时任务就是要抓取这些内容
	 * 
	 * @param refresh
	 *            是否需要更新目标网站信息
	 * @param path
	 *            当前项目绝对路径,用于加载指定目录下的配置文件
	 * @param targetWebs
	 *            指定抓取某个网站,未指定的情况下会把所有的目标网站信息都抓取下来
	 */
	public synchronized static void fetchGoodLinkByRules(boolean refresh, String path, String... targetWebs) {
		
		// 加载Constants.ON_FETCHING_RULE,需要抓取的商品列表的规则集合
		YXEConfLoad.loadFetchGoodListRule(refresh, path, targetWebs);
		// 抓取商品详情链接并存入h_splider_info文件
		fetchGoodLinkByRules(Constants.ON_FETCHING_RULE);
	}

	/**
	 * 根据GoodListRule商品列表抓取规则抓取商品列表信息,
	 * <br/>
	 * 将商品详情链接存入h_splider_info表中(指定规则)
	 * 
	 * @param ruleList
	 */
	public synchronized static void fetchGoodLinkByRules(List<GoodListRule> ruleList) {
		// 根据商品列表抓取规则集合抓取商品列表
		for (int i = 0; i < ruleList.size(); i++) {
			GoodListRule rule = ruleList.get(i);
			log.info(AppConfig.formatLog("start fetch good link by rules [" + rule.getRuleName() + "]"));
			List<String> goodList = fetchGoodLink(rule);// 抓取商品详情链接
			log.info(AppConfig.formatLog("fetch good link by rules [" + rule.getRuleName() + "], goods count is [" + goodList.size() + "]"));
			// 将抓取的商品详情链接存入到数据库中
			for (int j = 0; j < goodList.size(); j++) {
				String link = goodList.get(j);
//				List<SpliderInfo> tmpSinfos = SpliderInfo.me.find("select * from h_splider_info where h_rule_name=? and h_good_link=?", rule.getRuleName(), link);
				//链接去重问题,很无奈的选择
				List<SpliderInfo> tmpSinfos = SpliderInfo.me.find("select * from h_splider_info where h_good_link=?", link);
				if (tmpSinfos == null || tmpSinfos.size() == 0) {
					// 保存临时链接
					SpliderInfo sinfo = new SpliderInfo();
					sinfo.set("h_rule_name", rule.getRuleName());
					sinfo.set("h_mer_host", rule.getDefaultAmazonHost());
					sinfo.set("h_good_source", rule.getHost());
					sinfo.set("h_good_link", link);
					sinfo.set("h_catch_state", SpliderInfo.UN_FETCH);
					sinfo.set("h_src_type", rule.getTargetType());
					sinfo.set("h_src_free_str", TextUtil.isEmpty(rule.getFreeStr()) ? "" : rule.getFreeStr());
					sinfo.set("h_create_time", new Timestamp(System.currentTimeMillis()));//新增时默认系统当前时间
//					sinfo.set("h_update_time", new Timestamp(System.currentTimeMillis()));//新增时默认系统当前时间
					boolean isSaved = sinfo.save();
					log.info(AppConfig.formatLog("fetch good link by rules:save link [" + link + "] to database, save flag is [" + isSaved + "]!"));
				} else {
					log.info(AppConfig.formatLog("fetch good link by rules: link [" + link + "] has been saved to database, ingore this good link!"));
					continue;
				}
			}
		}
	}

	/**
	 * 根据商品详情链接抓取商品信息,依据h_splider_*表中存储的信息进行商品详情信息抓取,并保存至数据库
	 * @param sinfo
	 * 						商品详情页对象
	 * @return
	 * 			0 表示成功, -1 表示失败
	 */
	public synchronized static int fetchGoodOnLink(SpliderInfo sinfo) {
		
		if (YXE_TYPE_MATCHES == null) {
			YXE_TYPE_MATCHES = new ArrayList<YXETypeMatch>();
			YXE_TYPE_MATCHES.addAll(Constants.TYPE_MATCH_RULE_LIST);
		}
		
		int flag = 0;
		
		if (sinfo == null) {
			log.info(AppConfig.formatLog("fetchGoodOnLink SpliderInfo#sinfo is null, skip!"));
			return -1;
		}

		DetailFetchRule dfr = null;
		String freeStr = sinfo.getStr("h_src_free_str");
		if (!TextUtil.isEmpty(freeStr)) {
			String key = freeStr;
			String[] merTypeArr = null;
			String type = null;
			String tpyeInt = null;
			if (!TextUtil.isEmpty(key) && key.contains("_")) {
				merTypeArr = key.split("_");
				type = merTypeArr[0];
				tpyeInt = merTypeArr[1];
				// 根据商家类型创建商品详情抓取规则
				dfr = YXEConfLoad.detailFetchRule(Integer.valueOf(tpyeInt));
			}
		}

		String goodTypeId = null;

		int goodSrcType = sinfo.getInt("h_src_type");
		switch (goodSrcType) {
		case Constants.GUANG_DIU:
			// goodTypeId = rule.getGoodTypeCode();
			// TODO 这个是针对光丢的商品类型的
			break;
		case Constants.MAI_SHOU_DANG:
		case Constants.SHENME_ZHIDE_MAI:
		case Constants.WUWU_HAITAO:
		case Constants.MEI_DE_BI:
		case Constants.BEI_MEI_SHENG_QIAN_KUAI_BAO:
			break;
		}

		String ruleName = sinfo.getStr("h_rule_name");
		String merHost = sinfo.getStr("h_mer_host");
		String goodHost = sinfo.getStr("h_good_source");
		String link = sinfo.getStr("h_good_link");

		if (!link.startsWith("http")) {
			link = goodHost + link;
		}
		log.info(AppConfig.formatLog("fetchGoodOnLink#goodDetailLink:[" + link + "]"));
		if (link.contains("guangdiu")) {
			String[] gdType = ruleName.split("@");
			link = link + "#" + gdType[1];
		}
		GoodDetail gd = fetchGoodDetail(BaseRule.GET, link, link, merHost, dfr, goodSrcType);
		switch (goodSrcType) {
		case Constants.GUANG_DIU:
			// goodTypeId = rule.getGoodTypeCode();
			// TODO 这里需要适配光丢
			break;
		case Constants.MAI_SHOU_DANG:
			if (gd != null) {
				goodTypeId = gd.getType();
			}
			break;
		case Constants.SHENME_ZHIDE_MAI:
			if (gd != null) {
				goodTypeId = gd.getType();
				String tmpYXELink = gd.getYxehtLink();
				boolean isGoSMZDM = true;
				if (!TextUtil.isEmpty(tmpYXELink)) {
					isGoSMZDM = tmpYXELink.contains("go.smzdm.com");
				} else {
					isGoSMZDM = false;
				}

				String requestUrl = AppConfig.HOST;
				String host = requestUrl.substring(0, requestUrl.lastIndexOf("/"));
				String realLink = HaiTaoSpliderService.fetchRealLink(host + "/old/parseDetail?detail_link=" + link);
				String[] tmp = LinkConvertor.convertToYxeLink(merHost, realLink, link);
				
				String yxeLink = null;
				if(tmp == null || tmp.length == 0){
					yxeLink = "";
				}else{
					yxeLink = tmp[0];
				}
				
				if (TextUtil.isEmpty(yxeLink)) {
					// TODO 这里是权宜之计,对于SMZDM上面不能识别的链接进行处理
					sinfo.set("h_catch_state", SpliderInfo.FETCH_FAILED);
					sinfo.set("h_catch_reson", "link["+link+"]:SMZDM yxeLink is null");
					sinfo.set("h_update_time", new Timestamp(System.currentTimeMillis()));
					boolean updateFlag = sinfo.update();
					log.info(AppConfig.formatLog("\n fetchGoodOnLink#task-yxelink-filter:[" + goodSrcType + "][yxeLink:" + yxeLink + "]为空,跳过商品[\n" + gd + "\n], updateFlag:"+updateFlag));
					return -1;
				}
				gd.setGoodHost(tmp[1]);
				gd.setYxehtLink(yxeLink);
			}
			break;
		case Constants.WUWU_HAITAO:
		case Constants.MEI_DE_BI:
		case Constants.BEI_MEI_SHENG_QIAN_KUAI_BAO:
			if (gd != null) {
				goodTypeId = gd.getType();
			}
			break;
		}
		
		log.info(AppConfig.formatLog("fetchGoodOnLink#goodTypeId:" + goodTypeId));
		if (gd == null || TextUtil.isEmpty(goodTypeId)) {
			sinfo.set("h_catch_state", SpliderInfo.FETCH_FAILED);
			sinfo.set("h_catch_reson", "link["+link+"]:goodTypeId[" + goodTypeId + "] or good[" + (gd == null) + "] is null");
			sinfo.set("h_update_time", new Timestamp(System.currentTimeMillis()));
			boolean updateFlag = sinfo.update();
			log.info(AppConfig.formatLog("fetchGoodOnLink#goodTypeId[" + goodTypeId + "] or good[" + (gd == null) + "] is null, ingore this good cause  1. updateFlag:"+updateFlag));
			return -1;
		}

		if (TextUtil.isEmpty(gd.getGoodTitle()) || TextUtil.isEmpty(gd.getGoodSrcLink()) || TextUtil.isEmpty(gd.getYxehtLink())) {
			sinfo.set("h_catch_state", SpliderInfo.FETCH_FAILED);
			sinfo.set("h_catch_reson", "link["+link+"]:goodTitle[" + gd.getGoodTitle() + "] or goodSrcLink[" + gd.getGoodSrcLink() + "] or YxeHtLink[" + gd.getYxehtLink() + "] is null");
			sinfo.set("h_update_time", new Timestamp(System.currentTimeMillis()));
			boolean updateFlag = sinfo.update();
			log.info(AppConfig.formatLog("fetchGoodOnLink#goodTitle[" + gd.getGoodTitle() + "] or goodSrcLink[" + gd.getGoodSrcLink() + "] or YxeHtLink[" + gd.getYxehtLink() + "] is null, ingore this good cause 2. updateFlag:"+updateFlag));
			return -1;
		}

		// TODO 数据不存数据库的问题就出在这里了,是merHost出了问题
		Merchant mer = Merchant.me.findFirst("select * from h_merchant where url like '%" + gd.getGoodHost() + "%' and whetherUse=1");
		log.info(AppConfig.formatLog("fetch good detail by rules#find merchant by host [" + gd.getGoodHost() + "] in database"));
		Goodstype type = AppConfig.GOODTYE_MAP.get(goodTypeId);
		Goods tmp = Goods.me.findFirst("select * from h_goods where hrefnew=?", gd.getYxehtLink());

		if (mer == null) {
			sinfo.set("h_catch_state", SpliderInfo.FETCH_FAILED);
			sinfo.set("h_catch_reson", "link["+link+"]:can not find merchant [" + gd.getGoodHost() + "] in database, ingore good [" + gd.getGoodTitle() + "]");
			sinfo.set("h_update_time", new Timestamp(System.currentTimeMillis()));
			boolean updateFlag = sinfo.update();
			log.info(AppConfig.formatLog("fetchGoodOnLink#can not find merchant [" + gd.getGoodHost() + "] in database, ingore good [" + gd.getGoodTitle() + "]. updateFlag:"+updateFlag));
//			log.info(AppConfig.formatLog("fetchGoodOnLink#can not find merchant [" + gd.getGoodHost() + "] in database, save good [" + gd.getGoodTitle() + "] to h_goods. updateFlag:"+updateFlag));
//			return -1;
		}

		if (type == null) {
			log.info(AppConfig.formatLog("fetchGoodOnLink#find goodType by typeId [" + goodTypeId + "]"));
			type = Goodstype.me.findById(goodTypeId);
		}

		if (type == null) {
			sinfo.set("h_catch_state", SpliderInfo.FETCH_FAILED);
			sinfo.set("h_catch_reson", "link["+link+"]:can not find goodType by typeId [" + goodTypeId + "] in database, ingore good [" + gd.getGoodTitle() + "]");
			sinfo.set("h_update_time", new Timestamp(System.currentTimeMillis()));
			boolean updateFlag = sinfo.update();
			log.info(AppConfig.formatLog("fetchGoodOnLink#can not find goodType by typeId [" + goodTypeId + "] in database, ingore good [" + gd.getGoodTitle() + "]. updateFlag:"+updateFlag));
			return -1;
		}

		String hostNoHttp = merHost;
		if (hostNoHttp.startsWith("http://")) {
			hostNoHttp = hostNoHttp.replaceFirst("http://", "");
		}
		BizUserTags bizUserTags = AmazonCfgInfo.ALL_AMAZON_HOST_TAG_MATCH.get(hostNoHttp);
		
		if (tmp == null) {
//			log.info(AppConfig.formatLog("fetchGoodOnLink#gd[" + gd + "],\n type[" + type + "],\n mer[" + mer + "],\n bizUserTags[" + bizUserTags + "]"));
			log.info(AppConfig.formatLog("fetchGoodOnLink#gd[" + gd + "],\n bizUserTags[" + bizUserTags + "]"));
			Goods goods = Goods.me.convert(gd, bizUserTags != null ? bizUserTags.getBizManName() : AppConfig.bizManName, bizUserTags != null ? bizUserTags.getBizManId() : String.valueOf(AppConfig.bizManId), goodTypeId, type.getStr("name"), null, null, gd.getFetch_link());
			// TODO 商品信息保存
			if (!AppConfig.IS_DEV) {
				boolean goodSave = goods.save();
				log.info(AppConfig.formatLog("fetchGoodOnLink#save gd[" + gd.getGoodTitle() + "] to database, saveFlag:[" + goodSave + "]"));
			}
			GoodInfo goodInfo = new GoodInfo();
			goodInfo.set("goodsid", goods.getInt("id"));
			goodInfo.set("descs", gd.getGoodContent());
			GoodInfo tmpGoodInfo = GoodInfo.me.findFirst("select * from h_goodsinfo where goodsid=?", goods.getInt("id"));
			// TODO 商品介绍内容保存
			if (!AppConfig.IS_DEV) {
				if (tmpGoodInfo == null) {
					boolean goodInfoSave = goodInfo.save();
					log.info(AppConfig.formatLog("fetchGoodOnLink#save goodInfo for [" + gd.getGoodTitle() + "] to database, saveFlag:[" + goodInfoSave + "]"));
				} else {
					tmpGoodInfo.set("goodsid", goods.getInt("id"));
					tmpGoodInfo.set("descs", gd.getGoodContent());
					boolean goodInfoUpdate = tmpGoodInfo.update();
					log.info(AppConfig.formatLog("fetchGoodOnLink#update goodInfo for [" + gd.getGoodTitle() + "] to database, updateFlag:[" + goodInfoUpdate + "]"));
				}
			}
			if (gd.getmImgLinks() != null && gd.getmImgLinks().size() > 0) {
				log.info(AppConfig.formatLog("fetchGoodOnLink#good[" + gd.getGoodTitle() + "]'s image count is [" + gd.getmImgLinks().size() + "]"));
				int j = 0;
				for (String src : gd.getmImgLinks()) {
					if (j < 3) {
						if (src.contains("!")) {
							src = src.substring(0, src.lastIndexOf("!"));
						}
						int picId = getImage(src);
						if (j == 0) {
							goods.set("mpic", picId);
							// TODO 更新图片信息
							if (!AppConfig.IS_DEV) {
								boolean updateFlag = goods.update();
								log.info(AppConfig.formatLog("fetchGoodOnLink#update good main image for [" + gd.getGoodTitle() + "] to database, updateFlag:[" + updateFlag + "]"));
							}
						}
					}
					j++;
				}
			}
		}
		
//		log.info(AppConfig.formatLog("fetch good detail by rules#[" + rule.getRuleName() + "]第[" + gi + "]个商品【" + gd.getGoodTitle() + ", " + gd.getGoodSrcLink() + "】"));
		
		sinfo.set("h_catch_state", SpliderInfo.FETCH_SUCCESS);
		sinfo.set("h_catch_reson", "fetch success");
		sinfo.set("h_update_time", new Timestamp(System.currentTimeMillis()));
		boolean updateFlag = sinfo.update();
		
		return flag;
		
	}

	/**
	 * @deprecated
	 * @param ruleList
	 * @param dfr
	 * @param merHost
	 */
	private synchronized static void fetchGoodByRules(List<GoodListRule> ruleList, DetailFetchRule dfr, String merHost) {

		boolean isTestFun = false;// 是否仅用于测试
		boolean showGD = false;// 是否显示抓取结果信息
		int ruleMaxIndex = 0;// 执行规则的最大数量
		int goodMaxIndex = 50;// 每个商品抓取的最大数量

		for (int i = 0; i < ruleList.size(); i++) {
			if (isTestFun) {
				if (i > ruleMaxIndex) {
					break;
				}
			}
			GoodListRule rule = ruleList.get(i);

			if (dfr == null && !TextUtil.isEmpty(rule.getFreeStr())) {
				String key = rule.getFreeStr();
				String[] merTypeArr = null;
				String type = null;
				String tpyeInt = null;
				if (!TextUtil.isEmpty(key) && key.contains("_")) {
					merTypeArr = key.split("_");
					type = merTypeArr[0];
					tpyeInt = merTypeArr[1];
					// 根据商家类型创建商品详情抓取规则
					dfr = YXEConfLoad.detailFetchRule(Integer.valueOf(tpyeInt));
				}
			}

			String goodTypeId = null;
			switch (rule.getSourceType()) {
			case Constants.GUANG_DIU:
				goodTypeId = rule.getGoodTypeCode();
				break;
			case Constants.MAI_SHOU_DANG:
				break;
			case Constants.SHENME_ZHIDE_MAI:
				break;
			case Constants.WUWU_HAITAO:
				break;
			case Constants.MEI_DE_BI:
				break;
			case Constants.BEI_MEI_SHENG_QIAN_KUAI_BAO:
				break;
			}

			if (!TextUtil.isEmpty(merHost)) {
				if (!merHost.equals(rule.getDefaultAmazonHost())) {
					continue;
				}
			}
			List<String> goodList = fetchGoodLink(rule);
			log.info(AppConfig.formatLog("fetch good detail by rules [" + rule.getRuleName() + "], goods count is [" + goodList.size() + "]"));
			int goodCount = rule.getRuleName().startsWith("meidebi") && goodList.size() > 20 ? 20 : goodList.size();
			if (goodCount == 0) {
				log.info(AppConfig.formatLog("fetch good detail by rules [" + rule.getRuleName() + "], goods count is [" + goodList.size() + "], execute next list fetch rule."));
				continue;
			}
			for (int gi = 0; gi < goodCount; gi++) {
				if (isTestFun) {
					if (gi > goodMaxIndex) {
						break;
					}
				}
				String link = goodList.get(gi);
				// http://guangdiu.com/detail.php?id=3577178
				if (!link.startsWith("http")) {
					link = rule.getHost() + link;
				}
				log.info(AppConfig.formatLog("fetch good detail by rules#goodDetailLink:[" + link + "]"));
				if (link.contains("guangdiu")) {
					String[] gdType = rule.getRuleName().split("@");
					link = link + "#" + gdType[1];
				}
				GoodDetail gd = fetchGoodDetail(BaseRule.GET, link, link, rule.getDefaultAmazonHost(), dfr, -1);
				switch (rule.getSourceType()) {
				case Constants.GUANG_DIU:
					goodTypeId = rule.getGoodTypeCode();
					break;
				case Constants.MAI_SHOU_DANG:
					if (gd != null) {
						goodTypeId = gd.getType();
					}
					break;
				case Constants.SHENME_ZHIDE_MAI:
					if (gd != null) {
						goodTypeId = gd.getType();
						String tmpYXELink = gd.getYxehtLink();
						boolean isGoSMZDM = true;
						if (!TextUtil.isEmpty(tmpYXELink)) {
							isGoSMZDM = tmpYXELink.contains("go.smzdm.com");
						} else {
							isGoSMZDM = false;
						}

						String requestUrl = AppConfig.HOST;
						String host = requestUrl.substring(0, requestUrl.lastIndexOf("/"));
						String realLink = HaiTaoSpliderService.fetchRealLink(host + "/old/parseDetail?detail_link=" + link);
						String[] tmp = LinkConvertor.convertToYxeLink(rule.getDefaultAmazonHost(), realLink, link);
						String yxeLink = tmp[0];
						if (TextUtil.isEmpty(yxeLink)) {
							// TODO 这里是权宜之计,对于SMZDM上面不能识别的链接进行处理
							log.info(AppConfig.formatLog("\n fetch good detail by rules#task-yxelink-filter:[" + rule.getSourceType() + "][yxeLink:" + yxeLink + "]为空,跳过商品[\n" + gd + "\n]"));
							continue;
						}
						gd.setGoodHost(tmp[1]);
						gd.setYxehtLink(yxeLink);
					}
					break;
				case Constants.WUWU_HAITAO:
					if (gd != null) {
						goodTypeId = gd.getType();
					}
					break;
				case Constants.MEI_DE_BI:
					if (gd != null) {
						goodTypeId = gd.getType();
					}
					break;
				case Constants.BEI_MEI_SHENG_QIAN_KUAI_BAO:
					if (gd != null) {
						goodTypeId = gd.getType();
					}
					break;
				}
				log.info(AppConfig.formatLog("fetch good detail by rules#goodTypeId:" + goodTypeId));
				if (gd == null || TextUtil.isEmpty(goodTypeId)) {
					log.info(AppConfig.formatLog("fetch good detail by rules#goodTypeId[" + goodTypeId + "] or good[" + (gd == null) + "] is null, ingore this good cause  1."));
					continue;
				}

				if (TextUtil.isEmpty(gd.getGoodTitle()) || TextUtil.isEmpty(gd.getGoodSrcLink()) || TextUtil.isEmpty(gd.getYxehtLink())) {
					log.info(AppConfig.formatLog("fetch good detail by rules#goodTitle[" + gd.getGoodTitle() + "] or goodSrcLink[" + gd.getGoodSrcLink() + "] or YxeHtLink[" + gd.getYxehtLink() + "] is null, ingore this good cause 2"));
					continue;
				}

				// TODO 数据不存数据库的问题就出在这里了,是merHost出了问题
				Merchant mer1 = Merchant.me.findFirst("select * from h_merchant where url like '%" + gd.getGoodHost() + "%' and whetherUse=1");
				log.info(AppConfig.formatLog("fetch good detail by rules#find merchant by host [" + gd.getGoodHost() + "] in database"));
				Goodstype type = AppConfig.GOODTYE_MAP.get(goodTypeId);
				Goods tmp = Goods.me.findFirst("select * from h_goods where hrefnew=?", gd.getYxehtLink());

//				if (mer == null) {
//					log.info(AppConfig.formatLog("fetch good detail by rules#can not find merchant [" + gd.getGoodHost() + "] in database, ingore good [" + gd.getGoodTitle() + "]"));
//					continue;
//				}

				if (type == null) {
					log.info(AppConfig.formatLog("fetch good detail by rules#find goodType by typeId [" + goodTypeId + "]"));
					type = Goodstype.me.findById(goodTypeId);
				}

				if (type == null) {
					log.info(AppConfig.formatLog("fetch good detail by rules#find goodType by typeId [" + goodTypeId + "] in database, ingore good [" + gd.getGoodTitle() + "]"));
					continue;
				}

				String hostNoHttp = rule.getDefaultAmazonHost();
				if (hostNoHttp.startsWith("http://")) {
					hostNoHttp = hostNoHttp.replaceFirst("http://", "");
				}
				BizUserTags bizUserTags = AmazonCfgInfo.ALL_AMAZON_HOST_TAG_MATCH.get(hostNoHttp);

				if (tmp == null) {
//					log.info(AppConfig.formatLog("fetch good detail by rules#gd[" + gd + "],\n type[" + type + "],\n mer[" + mer + "],\n bizUserTags[" + bizUserTags + "]"));
					log.info(AppConfig.formatLog("fetch good detail by rules#gd[" + gd + "], bizUserTags[" + bizUserTags + "]"));
					Goods goods = Goods.me.convert(gd, bizUserTags != null ? bizUserTags.getBizManName() : AppConfig.bizManName, bizUserTags != null ? bizUserTags.getBizManId() : String.valueOf(AppConfig.bizManId), goodTypeId, type.getStr("name"), null, null, gd.getFetch_link());
					// TODO 商品信息保存
					if (!AppConfig.IS_DEV) {
						boolean goodSave = goods.save();
						log.info(AppConfig.formatLog("fetch good detail by rules#save gd[" + gd.getGoodTitle() + "] to database, saveFlag:[" + goodSave + "]"));
					}
					GoodInfo goodInfo = new GoodInfo();
					goodInfo.set("goodsid", goods.getInt("id"));
					goodInfo.set("descs", gd.getGoodContent());
					GoodInfo tmpGoodInfo = GoodInfo.me.findFirst("select * from h_goodsinfo where goodsid=?", goods.getInt("id"));
					// TODO 商品介绍内容保存
					if (!AppConfig.IS_DEV) {
						if (tmpGoodInfo == null) {
							boolean goodInfoSave = goodInfo.save();
							log.info(AppConfig.formatLog("fetch good detail by rules#save goodInfo for [" + gd.getGoodTitle() + "] to database, saveFlag:[" + goodInfoSave + "]"));
						} else {
							tmpGoodInfo.set("goodsid", goods.getInt("id"));
							tmpGoodInfo.set("descs", gd.getGoodContent());
							boolean goodInfoUpdate = tmpGoodInfo.update();
							log.info(AppConfig.formatLog("fetch good detail by rules#update goodInfo for [" + gd.getGoodTitle() + "] to database, updateFlag:[" + goodInfoUpdate + "]"));
						}
					}
					if (gd.getmImgLinks() != null && gd.getmImgLinks().size() > 0) {
						log.info(AppConfig.formatLog("fetch good detail by rules#good[" + gd.getGoodTitle() + "]'s image count is [" + gd.getmImgLinks().size() + "]"));
						int j = 0;
						for (String src : gd.getmImgLinks()) {
							if (j < 3) {
								if (src.contains("!")) {
									src = src.substring(0, src.lastIndexOf("!"));
								}
								int picId = getImage(src);
								if (j == 0) {
									goods.set("mpic", picId);
									// TODO 更新图片信息
									if (!AppConfig.IS_DEV) {
										boolean updateFlag = goods.update();
										log.info(AppConfig.formatLog("fetch good detail by rules#update good main image for [" + gd.getGoodTitle() + "] to database, updateFlag:[" + updateFlag + "]"));
									}
								}
							}
							j++;
						}
					}
				}

				log.info(AppConfig.formatLog("fetch good detail by rules#[" + rule.getRuleName() + "]第[" + gi + "]个商品【" + gd.getGoodTitle() + ", " + gd.getGoodSrcLink() + "】"));
			}
		}

	}

	/**
	 * 创建商品列表抓取规则
	 * @param ruleName
	 *            规则名称
	 * @param merHost
	 *            商家名称,比如55海淘
	 * @param merIndex
	 *            商家对应的索引值,比如55海淘对应4,格式为:55haitaoShort_4
	 * @param requestType
	 *            请求方式,get 或 post
	 * @param sourceWebHost
	 *            被抓网站的域名,比如55海淘为http://www.55haitao.com/
	 * @param goodListUrl
	 *            商品列表页面地址
	 * @param fetchStyle
	 *            需要根据此设置在下载的网页中寻找指定元素
	 * @param goodTypeCode
	 *            商品类型,比如日用的id为6
	 * @return
	 * 
	 */
	public synchronized static GoodListRule createFetchRules(String ruleName, String merHost, int merIndex, int requestType, String sourceWebHost, String goodListUrl, String fetchStyle, String goodTypeCode) {
		String goodHost = merHost;
		GoodListRule rule = new GoodListRule();
		/*
		 * try { rule.setRuleName(URLDecoder.decode(ruleName)); } catch
		 * (Exception e) {
		 */
		rule.setRuleName(ruleName);
		// }
		rule.setDefaultAmazonHost(goodHost);// 商家域名
		rule.setSourceType(merIndex);// 商家对应的索引值
		rule.setRequestMoethod(requestType);// 设置请求方式
		// TODO 这个setGoodHost需要去掉,待定
		List<String> goodHosts = new ArrayList<>();
		rule.setGoodHost(goodHosts);
		rule.setHost(sourceWebHost);// 设置源站host
		rule.setGoodListUrl(goodListUrl);// 设置商品列表页url
		int ruleType = BaseRule.CLASS;
		String ruleTypeValue = null;
		String nextCssStyle = "";
		if (!TextUtil.isEmpty(fetchStyle)) {
			String[] fetchSyleArr = fetchStyle.split("#");
			int length = fetchSyleArr.length;
			String[] rootFetchType = null;
			switch (length) {
			case 1:
				rootFetchType = fetchSyleArr[0].split(",");
				ruleType = Integer.valueOf(rootFetchType[0]);
				ruleTypeValue = rootFetchType[1];
				break;
			case 2:
				rootFetchType = fetchSyleArr[0].split(",");
				ruleType = Integer.valueOf(rootFetchType[0]);
				ruleTypeValue = rootFetchType[1];
				nextCssStyle = fetchSyleArr[1];
				break;
			case 3:
				// TODO do nothing...
				break;
			}
		}
		rule.setType(ruleType);// 设置抓取规则标示,比如class、id、tag等
		rule.setClassName(ruleTypeValue);// 设置抓取规则标志值
		rule.setNextCss(nextCssStyle);//
		rule.setGoodTypeCode(goodTypeCode);// 设置商品类型code

		return rule;
	}

	/**
	 * 
	 * @param rule
	 * @return
	 */
	public synchronized static List<String> fetchGoodLink(GoodListRule rule) {
		List<String> goodDetailLinks = new ArrayList<String>();

		try {
			String url = rule.getFullGoodListLink();
			String[] params = rule.getParams();
			String[] values = rule.getValues();
			String resultTagName = rule.getClassName();
			int type = rule.getType();
			int requestType = rule.getRequestMoethod();

			StringBuilder paramsSB = new StringBuilder();
			StringBuilder valuesSB = new StringBuilder();
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					paramsSB.append(params[i]);
					paramsSB.append(",");
					valuesSB.append(values[i]);
					valuesSB.append(",");
				}
			}

			// 设置请求类型
			Document doc = JsoupUtil.createJsoupDocument(requestType, url, paramsSB.toString(), valuesSB.toString());

			// 处理返回数据
			Elements results = new Elements();
			switch (type) {
			case BaseRule.CLASS:
				results = doc.getElementsByClass(resultTagName);
				if (Constants.SHENME_ZHIDE_MAI == rule.getSourceType()) {
					if (results.size() == 0) {
						results = doc.getElementsByClass("picBox");
					}
				}
				break;
			case BaseRule.ID:
				Element element = doc.getElementById(resultTagName);
				results.add(element);
				break;
			case BaseRule.SELECTION:
				// css query
				results = doc.select(resultTagName);
			default:
				break;
			}

			String nextCss = rule.getNextCss();
			if (!TextUtil.isEmpty(nextCss)) {
				String[] nextStyleArr = nextCss.split(",");
				Elements nextEls = results.select(nextStyleArr[1]);
				results.clear();
				results.addAll(nextEls);
			}

			for (Element el : results) {
				if (!"a".equals(el.tagName())) {
					Elements links = el.getElementsByTag("a");
					if (links != null && links.size() > 0) {
						el = links.first();
					}
				}

				if (rule.getGoodTypeCode().equals(MSDFetchRule.CATEGARY_EBAY)) {
					if ("查看全文".equals(el.text())) {
						String linkHref = el.attr("href");
						if (!goodDetailLinks.contains(linkHref)) {
							goodDetailLinks.add(linkHref);
						}
					}
				} else {
					String linkHref = el.attr("href");
					if (!goodDetailLinks.contains(linkHref)) {
						goodDetailLinks.add(linkHref);
					}
				}
			}

		} catch (IOException | JsoupDocException e) {
			e.printStackTrace();
			log.info(e.getLocalizedMessage(), e.getCause());
		}

		return goodDetailLinks;

	}

	public synchronized static GoodDetail fetchGoodDetail(int requestType, String url, String filePath, String merHost, DetailFetchRule fetchRule, int targetWebId) {

		log.info("fetchGoodDetail#filePath:" + filePath);

		String gdType = null;
		if (filePath.contains("#")) {
			String[] urlArr = url.split("#");
			filePath = urlArr[0];
			gdType = urlArr[1];
		}

		GoodDetail goodDetail = null;

		boolean getTitle = true;
		boolean getDesc = true;
		boolean getLink = true;
		boolean getImage = true;
		boolean getType = true;

		try {

			goodDetail = new GoodDetail();
			Document doc = JsoupUtil.createJsoupDocument(requestType, filePath);

			/*
			 * 标题
			 */
			if (getTitle) {
				String titleCss = fetchRule.getTitleCssStyle();
				String[] titleCssArr = titleCss.split("#");
				Elements titles = doc.select(titleCssArr[0]);
				if (titles != null && titles.size() > 0) {
					String titleStr = "";

					// TODO 这里需要处理，有可能titles节点的子节点数量多于一个
					Element title = titles.first();
					Elements children = null;

					String childCss = null;
					String[] childCssArr = null;
					if (titleCssArr.length > 1) {
						childCss = titleCssArr[1];
						childCssArr = childCss.split(",");
					}

					if (!TextUtil.isEmpty(childCss) && childCssArr != null && childCssArr.length >= 1) {
						for (int i = 0; i < childCssArr.length; i++) {
							String css = childCssArr[i];
							children = title.select(css);
							if (children != null && children.size() > 0) {
								titleStr = children.text().trim();
							} else {
								continue;
							}
						}
					} else {
						children = title.children();
						String tmpTitle = title.text();
						if (!TextUtil.isEmpty(tmpTitle)) {
							titleStr = tmpTitle;
						}
					}

					log.info("[output]title:" + titleStr);
					SpliderFilterWord sfw = SpliderFilterWord.me.findFirst("select * from h_splider_filter_word where h_src_id=?", targetWebId);
					if(sfw != null){
						String filterWord = sfw.getStr("h_filter_words");
						if(!TextUtil.isEmpty(filterWord)){
							String[] arr = filterWord.split(",");
							for(String str : arr){
								titleStr = titleStr.contains(str) ? titleStr.replaceAll(str, "") : titleStr; 
							}
						}
					}
					goodDetail.setGoodTitle(titleStr);
				}
			}

			/*
			 * 获取商品内容
			 */
			if (getDesc) {
				String descCss = fetchRule.getDescCssStyle();
				String[] descCssArr = descCss.split("#");
				String childCss = null;
				String[] childCssArr = null;
				if (descCssArr.length > 1) {
					childCss = descCssArr[1];
					childCssArr = childCss.split(",");
				}

				Elements mainContent = doc.select(descCssArr[0]);
				StringBuilder sb = new StringBuilder();
				if (mainContent != null && mainContent.size() > 0) {
					String filterTag = "p";
					int depth = 1;
					if (childCssArr != null && childCssArr.length > 1) {
						filterTag = childCssArr[0];
						depth = Integer.valueOf(childCssArr[1]);
					}
					Elements subContents = mainContent.select(filterTag);
					for (int i = 0; i < subContents.size(); i++) {
						Element el = subContents.get(i);
						if (i > depth) {
							break;
						}
						String pInnerText = el.text();
						if (!TextUtil.isEmpty(pInnerText)) {
							// TODO 这里要去掉商品介绍里的超链接，另外还需要去掉里面与网站相关的关键字，比如“值友推荐”
							Elements ss = el.getElementsByAttributeValue("style", "position:absolute;left:-9999px;top:-9999px");
							ss.remove();
							el.select("a").remove();
							String tmpContent = el.html();
							sb.append(tmpContent);
							sb.append("<br>");
						} else {
							break;
						}
					}

					String mainC = sb.toString();
					log.info("[output]good desc:" + mainC);
					SpliderFilterWord sfw = SpliderFilterWord.me.findFirst("select * from h_splider_filter_word where h_src_id=?", targetWebId);
					if(sfw != null){
						String filterWord = sfw.getStr("h_filter_words");
						if(!TextUtil.isEmpty(filterWord)){
							String[] arr = filterWord.split(",");
							for(String str : arr){
								mainC = mainC.contains(str) ? mainC.replaceAll(str, "") : mainC;
							}
						}
					}
					goodDetail.setGoodContent(mainC);

				}
			}

			/*
			 * 获取商品原链接并制造现有链接
			 */
			if (getLink) {
				String linkCss = fetchRule.getTrueLinkCssStyle();
				String[] linkCssArr = linkCss.split("#");
				String childCss = null;
				String[] childCssArr = null;
				if (linkCssArr.length > 1) {
					childCss = linkCssArr[1];
					childCssArr = childCss.split(",");
				}

				Elements links = doc.select(linkCssArr[0]);

				if (links != null && links.size() > 0) {
					Elements tmpLinks = links.select("a");
					// TODO 这里可能会有问题,因为取的是第一个标签中的内容
					String srcLink = tmpLinks.first().attr("href");
					log.info("[output]good srcLink:" + srcLink);
					// TODO 这里需要获取商品的真实链接
					String cssQuery = "";
					if (childCssArr != null) {
						if (childCssArr.length == 1) {
							cssQuery = childCssArr[0];
						} else if (childCssArr.length > 1) {
							cssQuery = childCssArr[0] + "[" + childCssArr[1] + "]";
						}
					}
					String urlTmp;
					log.info("【输出】srcLink:" + srcLink);
					if (filePath.contains("guangdiu") || filePath.contains("go.smzdm") || filePath.contains("www.smzdm")) {
						urlTmp = srcLink;
					} else {
						urlTmp = getGoodSrcLinkByJsoup(srcLink, BaseRule.GET, cssQuery);
					}

					if (TextUtil.isEmpty(urlTmp)) {
						return null;//TODO 这里有一个获取商品原链为空的情况出现
					}

					if (!urlTmp.startsWith("http")) {
						urlTmp = srcLink;
					}
					try {
						urlTmp = URLDecoder.decode(urlTmp, "utf-8");
					} catch (Exception e) {
						log.info(e.getLocalizedMessage(), e.getCause());
						urlTmp = srcLink;
					}

					log.info("[output]good urlTmp_srcLink:" + urlTmp);
					goodDetail.setGoodSrcLink(urlTmp);
					// 洋小二链接生成
					// TODO 这里需要把原始链接转换为洋小二链接
					String yxeHtLink = null;// urlTmp;
					if (filePath.contains("go.smzdm") || filePath.contains("www.smzdm")) {
						yxeHtLink = filePath;
					} else {
						String[] tmp = LinkConvertor.convertToYxeLink(merHost, urlTmp, filePath);
						if (tmp == null || tmp.length == 0) {
							return null;
						}
						yxeHtLink = tmp[0];
						goodDetail.setGoodHost(tmp[1]);
					}

					log.info("[output]good yxeHtLink:" + yxeHtLink);

					if (TextUtil.isEmpty(yxeHtLink)) {
						return null;
					}

					goodDetail.setYxehtLink(yxeHtLink);
				}
			}

			/*
			 * 获取图片
			 */
			if (getImage) {
				String imgCss = fetchRule.getImgCssStyle();
				String[] imgCssArr = imgCss.split("#");
				Elements imgPanel = null;
				for (String s : imgCssArr) {
					imgPanel = doc.select(s);
					if (imgPanel != null && imgPanel.size() > 0) {
						break;
					}
				}

				// 适配逛丢
				if (!TextUtil.isEmpty(gdType)) {
					if (imgPanel == null || imgPanel.select("img").size() == 0) {
						imgPanel = doc.select("a[class=dimage]");
					}
				}

				List<String> imgLinks = new ArrayList<String>();
				if (imgPanel != null && imgPanel.size() > 0) {
					Elements imgTags = imgPanel.select("img");
					for (Element img : imgTags) {
						String imgSrc = img.attr("src");
						if (!TextUtil.isEmpty(imgSrc)) {
							// TODO 55海淘
							if (imgSrc.contains("@")) {
								imgSrc = imgSrc.substring(0, imgSrc.lastIndexOf("@"));
							}
							// TODO 没得比
							if (imgSrc.contains("-") && (imgSrc.lastIndexOf("-") > imgSrc.lastIndexOf(".")) && !goodDetail.getGoodSrcLink().contains("smzdm.com")) {
								imgSrc = imgSrc.substring(0, imgSrc.lastIndexOf("-"));
							}
							// TODO 逛丢
							if (imgSrc.contains("?")) {
								imgSrc = imgSrc.substring(0, imgSrc.lastIndexOf("?"));
							}

						}
						if (imgSrc.startsWith("//")) {
							imgSrc = imgSrc.replaceFirst("//", "http://");
						}
						// System.err.println("【输出】商品图片地址:"+imgSrc);
						imgLinks.add(imgSrc);
					}
					goodDetail.setmImgLinks(imgLinks);
				}
			}

			/*
			 * 类型
			 */
			if (getType) {
				if (filePath.contains("guangdiu")) {
					String typeId = null;
					for (YXETypeMatch ytm : YXE_TYPE_MATCHES) {
						if (ytm.getTargetTypes().contains(gdType.trim())) {
							typeId = ytm.getYxeIndex();
							break;
						}
					}
					goodDetail.setType(typeId);
				} else {
					String typeCss = fetchRule.getTypeCssStyle();
					String[] typeCssArr = typeCss.split("#");
					String childCss = null;
					String[] childCssArr = null;
					if (typeCssArr.length > 1) {
						childCss = typeCssArr[1];
						childCssArr = childCss.split(",");
					}

					Elements types = doc.select(typeCssArr[0]);

					if (types != null && types.size() > 0) {
						String aTag = "a";
						int depth = 1;

						if (childCssArr != null) {
							int length = childCssArr.length;
							if (length == 1) {

							} else if (length > 1) {
								aTag = childCssArr[0];
								depth = Integer.valueOf(childCssArr[1]);
							}
						}

						// TODO 获取类型暂时只设置默认为6
						Elements type = types.select(aTag);
						Elements realTypes = type;
						Element realType = realTypes.size() - 1 < depth ? realTypes.last() : realTypes.get(depth);
						// 获取类型
						if (realType != null && !TextUtil.isEmpty(realType.text().trim())) {
							String typeStr = realType.text().trim();
							//TODO 类型不对应问题修改
							SpliderGoodType sgt = null;
							log.info(AppConfig.formatLog("fetchGoodDetail#targetWebId:"+targetWebId));
							try {
								sgt = SpliderGoodType.me.findFirst("select * from h_splider_type where h_src_type_str like '%"+typeStr+"%' and h_src_id=?", targetWebId);
							} catch (Exception e1) {
								log.info(e1.getLocalizedMessage(), e1.getCause());
							}
							System.err.println("aTag:" + aTag + ", realTypes.size():" + realTypes.size() + ", realTypes:" + realTypes.text() + ", typeStr:" + typeStr);
							String typeId = "6";
							if(sgt != null){
								String tmpType = null;
								try {
									tmpType = String.valueOf(sgt.getInt("h_yxe_type_id"));
								} catch (Exception e) {
									tmpType = "6";
								}
								typeId = tmpType;
							}else{
								typeId = "6";
							}
//							for (YXETypeMatch ytm : YXE_TYPE_MATCHES) {
//								if (ytm.getTargetTypes().contains(typeStr.trim())) {
//									typeId = ytm.getYxeIndex();
//									break;
//								}
//							}
							goodDetail.setType(typeId);
						} else {
							// System.err.println("【输出】未知商品类型字符串:");
							goodDetail.setType("6");
						}
					}
				}

				log.info("[output]good type:" + goodDetail.getType());
				if (TextUtil.isEmpty(goodDetail.getType())) {
					goodDetail.setType("6");
				}
			}

			goodDetail.setzDirect(GoodDetail.NO_ZDIRECT);
			goodDetail.setFetch_link(filePath);
		} catch (IOException e) {
			log.info(e.getLocalizedMessage(), e.getCause());
			goodDetail.setType("6");
		} catch (JsoupDocException e) {
			log.info(e.getLocalizedMessage(), e.getCause());
			goodDetail.setType("6");
		}
		return goodDetail;
	}

	public synchronized static String getGoodSrcLinkByJsoup(String url, int requestType, String cssQuery) {
		String goodSrcLink = null;
		try {
			Document doc = JsoupUtil.createJsoupDocument(requestType, url);

			/*
			 * 55海淘:link标签,link[rel="canonical"]
			 * 没得比:link标签,link[rel="canonical"]
			 * 北美省钱快报:meta标签,取其中的content属性值并去掉之前的"url="字符串
			 */
			Elements elements = doc.select(cssQuery);
			for (int i = 0; i < elements.size(); i++) {
				Element el = elements.get(i);
				String link = el.attr("content");
				if (!TextUtil.isEmpty(link)) {
					goodSrcLink = link.substring(link.indexOf("url=") + "url=".length());
					break;
				}
				// 55海淘,没得比
				if ("canonical".equals(el.attr("rel"))) {
					goodSrcLink = el.attr("href");
					break;
				} else {
					continue;
				}
			}

			// 55海淘
			if (TextUtil.isEmpty(goodSrcLink) || !goodSrcLink.startsWith("http")) {
				elements = doc.getElementsByClass("btn-go-look");
				for (Element el : elements) {
					if ("a".equals(el.tagName())) {
						goodSrcLink = el.attr("href");
						break;
					}
				}
			}
			
			/*
			 * 这里处理了目标网站商品详情页面返回最终地址的情况,
			 * 比如通过jsoup访问https://www.55haitao.com/g/1696-d-302780?acm=2.5.24.0后直接进入了商品原始链接https://www.amazon.com/dp/B01GTKKYZ0?tag=55haitao-20
			 */
			if(TextUtil.isEmpty(goodSrcLink)){
				goodSrcLink = doc.baseUri();
			}			

		} catch (HttpStatusException e) {
			log.info("HttpStatusException:" + e.getLocalizedMessage(), e.getCause());
			if(TextUtil.isEmpty(goodSrcLink)){
				goodSrcLink = e.getUrl();
			}
			return goodSrcLink;
		} catch (IOException e) {
			e.printStackTrace();
			log.info(e.getLocalizedMessage(), e.getCause());
			return null;
		} catch (JsoupDocException e) {
			e.printStackTrace();
			log.info(e.getLocalizedMessage(), e.getCause());
			return null;
		}

		return goodSrcLink;
	}

	private synchronized static int getImage(String src) {

		int pictureId = 0;

		try {
			URL url = new URL(src);
			URLConnection uri = url.openConnection();
			InputStream is = uri.getInputStream();
			String imageName = System.currentTimeMillis() + ".jpg";

			String path = AppConfig.IMAGE_SAVE_PATH;

			String currentPath = DateUtils.currentDate();

			if (path.endsWith("\\")) {
				path = path + currentPath;
			} else {
				path = path + File.separator + currentPath;
			}

			File todayDir = new File(path);
			if (!todayDir.exists()) {
				todayDir.mkdirs();
			}

			OutputStream os = new FileOutputStream(new File(path, imageName));
			byte[] buf = new byte[1024];
			int l = 0;
			while ((l = is.read(buf)) != -1) {
				os.write(buf, 0, l);
			}

			os.close();

			Picture picture = new Picture();
			/*
			 * `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id自增',
			 * `path` varchar(255) NOT NULL DEFAULT '' COMMENT '路径', `url`
			 * varchar(255) NOT NULL DEFAULT '' COMMENT '图片链接', `md5` char(32)
			 * NOT NULL DEFAULT '' COMMENT '文件md5', `sha1` char(40) NOT NULL
			 * DEFAULT '' COMMENT '文件 sha1编码', `status` tinyint(2) NOT NULL
			 * DEFAULT '0' COMMENT '状态', `create_time` int(10) unsigned NOT NULL
			 * DEFAULT '0' COMMENT '创建时间',
			 */
			picture.set("path", AppConfig.IMAGE_URL + currentPath + "/" + imageName);
			picture.set("url", "");
			picture.set("md5", "");
			picture.set("sha1", "");
			picture.set("status", "1");
			picture.set("create_time", System.currentTimeMillis() / 1000);
			// TODO 图片信息保存
			if (!AppConfig.IS_DEV) {
				picture.save();
				pictureId = picture.getLong("id").intValue();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			log.info(e.getLocalizedMessage(), e.getCause());
			// log.error(AppConfig.formatLog("pic save failed"), e.getCause());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			log.info(e.getLocalizedMessage(), e.getCause());
			// log.error(AppConfig.formatLog("pic save failed"), e.getCause());
		} catch (IOException e) {
			e.printStackTrace();
			log.info(e.getLocalizedMessage(), e.getCause());
			// log.error(AppConfig.formatLog("pic save failed"), e.getCause());
		}

		return pictureId;

	}

}
