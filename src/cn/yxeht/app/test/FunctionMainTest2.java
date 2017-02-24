package cn.yxeht.app.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.yxeht.app.bean.MerInfoBean;
import cn.yxeht.app.bean.YXETypeMatch;
import cn.yxeht.app.biz.bean.GoodDetail;
import cn.yxeht.app.biz.rule.BaseRule;
import cn.yxeht.app.biz.rule.DetailFetchRule;
import cn.yxeht.app.biz.rule.GoodListRule;
import cn.yxeht.app.constants.Constants;
import cn.yxeht.app.constants.MSDFetchRule;
import cn.yxeht.app.exception.JsoupDocException;
import cn.yxeht.app.utils.GsonUtils;
import cn.yxeht.app.utils.JsoupUtil;
import cn.yxeht.app.utils.TextUtil;

public class FunctionMainTest2 {

	public static void main(String[] args) {
		System.err.println("\n\n==============================逛丢=======================================");
		fetchGoodByRule(Constants.GUANG_DIU, "goodname");
		//TODO 买手党尚未完成
//		test(Constants.MAI_SHOU_DANG);
//		System.err.println("\n\n==============================什么值得买=======================================");
//		fetchGoodByRule(Constants.SHENME_ZHIDE_MAI, "itemName");
//		System.err.println("\n\n==============================55海淘=======================================");
//		fetchGoodByRule(Constants.WUWU_HAITAO, "index-deal-title");
//		System.err.println("\n\n==============================没得比=======================================");
//		fetchGoodByRule(Constants.MEI_DE_BI, "tit");
//		System.err.println("\n\n==============================北美省钱快报=======================================");
//		fetchGoodByRule(Constants.BEI_MEI_SHENG_QIAN_KUAI_BAO, "cnlist_realheight");
	}
	
	public static List<YXETypeMatch> YXE_TYPE_MATCHES = null;
	
	public static void fetchGoodByRule(int merType, String classValue){
		
		String yxeTypeMatchFilePath = "/D/workspace/YXESplider/type_match_rule";
		if(YXE_TYPE_MATCHES == null){
			YXE_TYPE_MATCHES = GsonUtils.jsonToList(TextUtil.readTxtFromFile(yxeTypeMatchFilePath), YXETypeMatch.class);
		}
		
		boolean isTestFun = true;//是否仅用于测试
		boolean showGD = true;//是否显示抓取结果信息
		int ruleMaxIndex = 0;//执行规则的最大数量
		int goodMaxIndex = 2;//每个商品抓取的最大数量
		
		String merBaseInfoPath = null;
		String merBaseInfoStr = null;
		String targetName = "";
		
		DetailFetchRule dfr = new DetailFetchRule();
		
		switch (merType) {
		case Constants.GUANG_DIU:
			targetName = "逛丢";
			merBaseInfoPath = "/D/workspace/YXESplider/guangdiu";
			dfr.setTitleCssStyle("a[class=dtitlelink]");
			dfr.setDescCssStyle("div[id=dabstract]#p,1");
			dfr.setTrueLinkCssStyle("a[class=dtitlegotobuy]#link,rel=canonical");
			dfr.setImgCssStyle("div[class=image-panel]#div[id=dabstract]");
			dfr.setTypeCssStyle("div[class=crumbs]#a,2");
			break;
		case Constants.MAI_SHOU_DANG:
			targetName = "买手党";
			break;
		case Constants.SHENME_ZHIDE_MAI:
			targetName = "什么值得买";
			merBaseInfoPath = "/D/workspace/YXESplider/smzdm";
			dfr.setTitleCssStyle("h1");
			dfr.setDescCssStyle("div[class=item-box item-preferential]#p,2");
			dfr.setTrueLinkCssStyle("div[class=buy]#link,rel=canonical");
			dfr.setImgCssStyle("img[itemprop=image]");
			dfr.setTypeCssStyle("div[class=crumbs]#a,2");
			break;
		case Constants.WUWU_HAITAO:
			targetName = "55海淘";
			merBaseInfoPath = "/D/workspace/YXESplider/55haitao";
			dfr.setTitleCssStyle("div[class=ht-deal-detail-title clearfix]#h1,h2");
			dfr.setDescCssStyle("div[class=ht-deal-detail-des-box]#p,1");
			dfr.setTrueLinkCssStyle("a[class=ht-deal-detail-buy-btn]#link,rel=canonical");
			dfr.setImgCssStyle("div[class=ht-deal-detail-buy]");
			dfr.setTypeCssStyle("p[class=ht-deal-detail-info-tag]#a,2");
			break;
		case Constants.MEI_DE_BI:
			targetName = "没得比";
			merBaseInfoPath = "/D/workspace/YXESplider/meidebi";
			dfr.setTitleCssStyle("h2[class=d-title]");
			dfr.setDescCssStyle("div[id=infoDesc]#p,1");
			dfr.setTrueLinkCssStyle("a[class=mdb-button mdb-button-orange mdb-button-large out-link]#link,rel=canonical");
			dfr.setImgCssStyle("div[class=d-output fr]");
			dfr.setTypeCssStyle("div[class=d-crumb gray9]#a,2");
			break;
		case Constants.BEI_MEI_SHENG_QIAN_KUAI_BAO:
			targetName = "北美省钱快报";
			merBaseInfoPath = "/D/workspace/YXESplider/bmsqkb";
			dfr.setTitleCssStyle("div[class=article_title]");
			dfr.setDescCssStyle("div[class=minfor  event_statistics]#p,1");
			dfr.setTrueLinkCssStyle("div[class=buy]#meta");
			dfr.setImgCssStyle("div[class=img_wrap]");
			dfr.setTypeCssStyle("div[class=mbx]#a,1");
			break;
		}
		
		if(!TextUtil.isEmpty(merBaseInfoPath)){
			merBaseInfoStr = TextUtil.readTxtFromFile(merBaseInfoPath);
			//加载商家信息,包含商家名称、商家默认域名、抓取目标域名、商品列表链接
			List<MerInfoBean> merInfos = GsonUtils.jsonToList(merBaseInfoStr, MerInfoBean.class);
			
			List<GoodListRule> ruleList = new ArrayList<GoodListRule>();
			
			for(int i = 0; i < merInfos.size(); i++){
				MerInfoBean mib = merInfos.get(i);
				ruleList.add(createFetchRules(mib.getMerName(), mib.getMerHost(), mib.getMerType(), BaseRule.GET, mib.getSrcHost(), mib.getSrcLink(), BaseRule.CLASS, classValue, mib.getSrcLink()));
			}
			System.out.println("====================商家["+targetName+"]共["+ruleList.size()+"]条规则===========================");
			for(int i = 0; i < ruleList.size(); i++){
				if(isTestFun){
					if(i > ruleMaxIndex){
						break;
					}
				}
				GoodListRule rule = ruleList.get(i);
				System.out.println("====================规则["+rule.getRuleName()+"]开始抓取数据===========================");
				List<String>  goodList = fetchGoodLink(rule);
				System.out.println("===========================规则["+rule.getRuleName()+"]中共["+goodList.size()+"]条商品数据===========================");
				for(int gi = 0; gi < goodList.size(); gi++){
					if(isTestFun){
						if(gi > goodMaxIndex){
							break;
						}
					}
					String link = goodList.get(gi);
					//http://guangdiu.com/detail.php?id=3577178
					if(!link.startsWith("http")){
						link = rule.getHost()+link;
					}
					System.out.println("商品详情链接:"+link);
					if(link.contains("guangdiu")){
						String[] gdType = rule.getRuleName().split("@");
						link = link + "#" + gdType[1];
					}
					GoodDetail gd = fetchGoodDetailmdb(BaseRule.GET, link, link, dfr);
					if(showGD){
						System.err.println(gd);
					}else{
						System.out.println("\n["+rule.getRuleName()+"]第["+gi+"]个商品【"+gd.getGoodTitle()+", "+gd.getGoodSrcLink()+"】");
					}
				}
			}
			
		}
		
	}

	public static GoodListRule createFetchRules(String ruleName, String merHost, int merIndex, int requestType,
			String sourceWebHost, String goodListUrl, int ruleType, String ruleTypeValue, String goodTypeCode) {
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
		rule.setType(ruleType);// 设置抓取规则标示,比如class、id、tag等
		rule.setClassName(ruleTypeValue);// "z-btn
											// z-btn-red");//"feed-link-btn-inner");//设置抓取规则标志值
		rule.setGoodTypeCode(goodTypeCode);// 设置商品类型code

		return rule;
	}

	/**
	 * 
	 * @param rule
	 * @return
	 */
	public static List<String> fetchGoodLink(GoodListRule rule) {
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
		}

		return goodDetailLinks;

	}

	public static GoodDetail fetchGoodDetailmdb(int requestType, String url, String filePath, DetailFetchRule fetchRule) {
		System.out.println("fetchGoodDetailmdb#filePath:" + filePath);
		
		String gdType = null;
		if(filePath.contains("#")){
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
			if(getTitle){
				String titleCss = fetchRule.getTitleCssStyle();
				String[] titleCssArr = titleCss.split("#");
				Elements titles = doc.select(titleCssArr[0]);
				if(titles != null&& titles.size() > 0){
					String titleStr = "";
					
//					System.out.println("标题["+titleCssArr[0]+"]有["+titles.size()+"]个元素\n["+titles.select("[class=article_title]")+"]");
					
					//TODO 这里需要处理，有可能titles节点的子节点数量多于一个
					Element title = titles.first();
					Elements children =	null;
					
					String childCss = null;
					String[] childCssArr = null;
					if(titleCssArr.length > 1){
						childCss = titleCssArr[1];
						childCssArr = childCss.split(",");
					}
					
					if(!TextUtil.isEmpty(childCss) && childCssArr != null && childCssArr.length > 1){
						for(int i = 0; i < childCssArr.length; i++){
							String css = childCssArr[i];
							children = title.select(css);
//							System.out.println("标题css["+css+"]["+i+"]");
							if(children != null && children.size() > 0){
								titleStr = children.text().trim();
							}else{
								continue;
							}
						}
					}else{
						children = title.children();
						String tmpTitle = title.text();
						if(!TextUtil.isEmpty(tmpTitle)){
							titleStr = tmpTitle;
						}
					}
					
//					System.err.println("【输出】标题:"+titleStr);
					
					goodDetail.setGoodTitle(titleStr);
				}
			}
			
			/*
			 * 获取商品内容
			 */
			if(getDesc){
				String descCss = fetchRule.getDescCssStyle();
				String[] descCssArr = descCss.split("#");
				String childCss = null;
				String[] childCssArr = null;
				if(descCssArr.length > 1){
					childCss = descCssArr[1];
					childCssArr = childCss.split(",");
				}
				
				Elements mainContent = doc.select(descCssArr[0]);
				StringBuilder sb = new StringBuilder();
				if (mainContent != null && mainContent.size() > 0) {
					String filterTag = "p";
					int depth = 1;
					if(childCssArr != null && childCssArr.length > 1){
						filterTag = childCssArr[0];
						depth = Integer.valueOf(childCssArr[1]);
					}
					Elements subContents = mainContent.select(filterTag);
					for (int i = 0; i < subContents.size(); i++) {
						Element el = subContents.get(i);
						if(i > depth){
							break;
						}
						String pInnerText = el.text();
						if(!TextUtil.isEmpty(pInnerText)){
							//TODO 这里要去掉商品介绍里的超链接，另外还需要去掉里面与网站相关的关键字，比如“值友推荐”
							Elements ss = el.getElementsByAttributeValue("style", "position:absolute;left:-9999px;top:-9999px");
							ss.remove();
							el.select("a").remove();
							String tmpContent = el.html();
							sb.append(tmpContent);
							sb.append("<br>");
						}else{
							break;
						}
					}

					String mainC = sb.toString();
//					System.err.println("【输出】商品描述:"+mainC);
					goodDetail.setGoodContent(mainC);

				}
			}
			
			/*
			 * 获取商品原链接并制造现有链接
			 */
			if(getLink){
				String linkCss = fetchRule.getTrueLinkCssStyle();
				String[] linkCssArr = linkCss.split("#");
				String childCss = null;
				String[] childCssArr = null;
				if(linkCssArr.length > 1){
					childCss = linkCssArr[1];
					childCssArr = childCss.split(",");
				}
				
				Elements links = doc.select(linkCssArr[0]);
				
				if(links != null && links.size() > 0){
					Elements tmpLinks = links.select("a");
//					System.out.println("超链接["+linkCssArr[0]+"]有["+tmpLinks.size()+"]个元素");
					//TODO 这里可能会有问题,因为取的是第一个标签中的内容
					String srcLink = tmpLinks.first().attr("href");
//					System.err.println("【输出】商品原链接:"+srcLink);
					// TODO 这里需要获取商品的真实链接
					String cssQuery = "";
					if(childCssArr != null){
						if(childCssArr.length == 1){
							cssQuery = childCssArr[0];
						}else if(childCssArr.length > 1){
							cssQuery = childCssArr[0]+"["+childCssArr[1]+"]";
						}
					}
					String urlTmp;
					if (filePath.contains("guangdiu")) {
						urlTmp = srcLink;
					}else{
						urlTmp = getGoodSrcLinkByJsoup(srcLink, BaseRule.GET, cssQuery);
					}
					if(TextUtil.isEmpty(urlTmp) || !urlTmp.startsWith("http")){
						urlTmp = srcLink;
					}
//					System.err.println("【输出】商品原链接(1):"+urlTmp);
					goodDetail.setGoodSrcLink(urlTmp);
					// 洋小二链接生成
					// TODO 这里需要把原始链接转换为洋小二链接
					String yxeHtLink = urlTmp;
					goodDetail.setYxehtLink(yxeHtLink);
				}
			}
			
			/*
			 * 获取图片
			 */
			if(getImage){
				String imgCss = fetchRule.getImgCssStyle();
				String[] imgCssArr = imgCss.split("#");
				Elements imgPanel = null;
				for(String s : imgCssArr){
					 imgPanel = doc.select(s);
					 if(imgPanel != null && imgPanel.size() > 0){
						 break;
					 }
				}
				
				//适配逛丢
				if(!TextUtil.isEmpty(gdType)){
					if(imgPanel == null || imgPanel.select("img").size() == 0){
						imgPanel = doc.select("a[class=dimage]");
					}
				}
				
				List<String> imgLinks = new ArrayList<String>();
				if (imgPanel != null && imgPanel.size() > 0) {
//					System.out.println("图片节点["+fetchRule.getImgCssStyle()+"]有"+imgPanel.size()+"个元素");
					Elements imgTags = imgPanel.select("img");
					for (Element img : imgTags) {
						String imgSrc = img.attr("src");
						if (!TextUtil.isEmpty(imgSrc)) {
							//TODO 55海淘
							if (imgSrc.contains("@")) {
								imgSrc = imgSrc.substring(0, imgSrc.lastIndexOf("@"));
							}
							//TODO 没得比
							if (imgSrc.contains("-")) {
								imgSrc = imgSrc.substring(0, imgSrc.lastIndexOf("-"));
							}
							//TODO 逛丢
							if (imgSrc.contains("?")) {
								imgSrc = imgSrc.substring(0, imgSrc.lastIndexOf("?"));
							}
							
						}
						if (imgSrc.startsWith("//")) {
							imgSrc = imgSrc.replaceFirst("//", "http://");
						}
//						System.err.println("【输出】商品图片地址:"+imgSrc);
						imgLinks.add(imgSrc);
					}
					goodDetail.setmImgLinks(imgLinks);
				}
			}

			/*
			 * 类型 
			 */
			if(getType){
				if (filePath.contains("guangdiu")) {
					String typeId = null;
					for(YXETypeMatch ytm : YXE_TYPE_MATCHES){
						if(ytm.getTargetTypes().contains(gdType.trim())){
							typeId = ytm.getYxeIndex();
							break;
						}
					}
					goodDetail.setType(typeId);
				}else{
					String typeCss = fetchRule.getTypeCssStyle();
					String[] typeCssArr = typeCss.split("#");
					String childCss = null;
					String[] childCssArr = null;
					if(typeCssArr.length > 1){
						childCss = typeCssArr[1];
						childCssArr = childCss.split(",");
					}
					
					Elements types = doc.select(typeCssArr[0]);
					
					if (types != null && types.size() > 0) {
//						System.out.println("类型节点"+typeCssArr[0]+"有"+types.size()+"个元素");
						
						String aTag = "a";
						int depth = 2;
						
						if(childCssArr != null){
							int length = childCssArr.length;
							if(length == 1){
								
							}else if(length > 1){
								aTag = childCssArr[0];
								depth = Integer.valueOf(childCssArr[1]);
							}
						}
						
						// TODO 获取类型暂时只设置默认为6
						Elements type = types.select(aTag);
						Elements realTypes =type;
//						System.out.println("realTypes:"+realTypes.text());
						Element realType = realTypes.get(depth);
						// 获取类型
						if (realType != null && !TextUtil.isEmpty(realType.text().trim())) {
							String typeStr = realType.text().trim();
							String typeId = "6";
							for(YXETypeMatch ytm : YXE_TYPE_MATCHES){
								if(ytm.getTargetTypes().contains(typeStr.trim())){
									typeId = ytm.getYxeIndex();
									break;
								}
							}
							goodDetail.setType(typeId);
						} else {
//							System.err.println("【输出】未知商品类型字符串:");
							goodDetail.setType("6");
						}
					}
				}
				
//				System.err.println("【输出】商品类型字符串:"+goodDetail.getType());
				
				if (TextUtil.isEmpty(goodDetail.getType())) {
					goodDetail.setType("6");
				}
			}
			
			goodDetail.setzDirect(GoodDetail.NO_ZDIRECT);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JsoupDocException e) {
			e.printStackTrace();
		}
		return goodDetail;
	}

	public static String getGoodSrcLinkByJsoup(String url, int requestType, String cssQuery) {
		String goodSrcLink = null;
		System.out.println("getGoodSrcLinkByJsoup#url(直达链接):["+url+"]");
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
				if(!TextUtil.isEmpty(link)){
					goodSrcLink = link.substring(link.indexOf("url=")+"url=".length());
					break;
				}
				//55海淘,没得比
				if ("canonical".equals(el.attr("rel"))) {
					goodSrcLink = el.attr("href");
					break;
				} else {
					continue;
				}
			}
			
			//55海淘
			if(TextUtil.isEmpty(goodSrcLink) || !goodSrcLink.startsWith("http")){
				elements = doc.getElementsByClass("btn-go-look");
				for(Element el : elements){
					if("a".equals(el.tagName())){
						goodSrcLink = el.attr("href");
						break;
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JsoupDocException e) {
			e.printStackTrace();
		}

		return goodSrcLink;
	}

}
