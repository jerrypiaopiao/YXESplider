package cn.yxeht.app.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.TextUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.yxeht.app.bean.MerInfoBean;
import cn.yxeht.app.biz.bean.GoodDetail;
import cn.yxeht.app.biz.rule.BaseRule;
import cn.yxeht.app.biz.rule.GoodListRule;
import cn.yxeht.app.constants.Constants;
import cn.yxeht.app.constants.MSDFetchRule;
import cn.yxeht.app.rule.Rule;
import cn.yxeht.app.utils.GsonUtils;
import cn.yxeht.app.utils.TextUtil;

public class FunctionMainTest {

	public static void main(String[] args) {
//		test(Constants.GUANG_DIU);
//		test(Constants.MAI_SHOU_DANG);
//		test(Constants.SHENME_ZHIDE_MAI);
		test(Constants.WUWU_HAITAO);
//		test(Constants.MEI_DE_BI);
//		test(Constants.BEI_MEI_SHENG_QIAN_KUAI_BAO);
	}
	
	public static void test(int merType){
		String merBaseInfoPath = null;
		String merBaseInfoStr = null;
		String targetName = "";
		switch (merType) {
		case Constants.GUANG_DIU:
			targetName = "逛丢";
			break;
		case Constants.MAI_SHOU_DANG:
			targetName = "买手党";
			break;
		case Constants.SHENME_ZHIDE_MAI:
			targetName = "什么值得买";
			merBaseInfoPath = "/D/workspace/YXESplider/smzdm";
			break;
		case Constants.WUWU_HAITAO:
			targetName = "55海淘";
			merBaseInfoPath = "/D/workspace/YXESplider/55haitao";
			break;
		case Constants.MEI_DE_BI:
			targetName = "没得比";
			merBaseInfoPath = "/D/workspace/YXESplider/meidebi";
			break;
		case Constants.BEI_MEI_SHENG_QIAN_KUAI_BAO:
			targetName = "北美省钱快报";
			merBaseInfoPath = "/D/workspace/YXESplider/bmsqkb";
			break;
		}
		
		if(!TextUtil.isEmpty(merBaseInfoPath)){
			merBaseInfoStr = TextUtil.readTxtFromFile(merBaseInfoPath);
			List<MerInfoBean> merInfos = GsonUtils.jsonToList(merBaseInfoStr, MerInfoBean.class);
			
			List<GoodListRule> ruleList = new ArrayList<GoodListRule>();
			
			for(int i = 0; i < merInfos.size(); i++){
				MerInfoBean mib = merInfos.get(i);
				ruleList.add(createFetchRules(mib.getMerName(), mib.getMerHost(), mib.getMerType(), BaseRule.GET, mib.getSrcHost(), mib.getSrcLink(), BaseRule.CLASS, "index-deal-title", mib.getSrcLink()));
			}
			System.out.println("====================商家["+targetName+"]共["+ruleList.size()+"]条规则===========================");
			for(int i = 0; i < ruleList.size(); i++){
				GoodListRule rule = ruleList.get(i);
				System.out.println("===================="+rule.getRuleName()+"===========================");
				System.out.println("规则["+rule.getRuleName()+"]开始抓取数据");
				List<String>  goodList = fetchGoodLink(rule);
				System.out.println("规则["+rule.getRuleName()+"]中共["+goodList.size()+"]条商品数据");
				for(int gi = 0; gi < goodList.size(); gi++){
					String link = goodList.get(gi);
					GoodDetail gd = fetchGoodDetail(BaseRule.GET, link, "");
//					System.err.println(gd);
					System.out.println("["+rule.getRuleName()+"]第["+gi+"]个商品【"+gd.getGoodTitle()+", "+gd.getGoodSrcLink()+"】");
				}
				System.out.println("===============================================");
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

			// 通过Jsoup抓取网站信息
			Connection conn = Jsoup.connect(url).userAgent(
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31");
			// 设置查询参数

			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					conn.data(params[i], values[i]);
				}
			}

			// 设置请求类型
			Document doc = null;
			switch (requestType) {
			case Rule.GET:
				doc = conn.timeout(100000).get();
				break;
			case Rule.POST:
				doc = conn.timeout(100000).post();
				break;
			}

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

		} catch (IOException e) {
			e.printStackTrace();
		}

		return goodDetailLinks;

	}

	public static GoodDetail fetchGoodDetail(int requestType, String url, String hostType1) {
		System.out.println("\n\nurl:" + url);
		GoodDetail goodDetail = null;

		try {

			goodDetail = new GoodDetail();

			Connection conn = Jsoup.connect(url).userAgent(
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31");

			Document doc = null;
			switch (requestType) {
			case Rule.GET:
				doc = conn.timeout(100000).get();
				break;
			case Rule.POST:
				doc = conn.timeout(100000).post();
				break;
			}

			Elements type = doc.getElementsByClass("ht-deal-detail-info-tag");

			/*
			 * 类型 55海淘-已完成
			 * 
			 */
			if (type != null && type.size() > 0) {
				// TODO 获取类型暂时只设置默认为6
				Elements types = type.get(0).getElementsByClass("ht-deal-detail-info-tag");
				Elements realTypes = types.select("a");
				Element realType = realTypes.last();
				// 获取类型
				if (realType != null && !TextUtil.isEmpty(realType.text().trim())) {
					String typeStr = realType.text().trim();
					goodDetail.setType(typeStr);// TODO 这里需要将类型字符串映射为类型id
				} else {
					goodDetail.setType("6");
				}

			}

			if (TextUtil.isEmpty(goodDetail.getType())) {
				goodDetail.setType("6");
			}

			/*
			 * 获取标题 55海淘-已完成
			 * 
			 */
			Elements titles = doc.getElementsByAttributeValueContaining("class", "ht-deal-detail-title clearfix");
			if (titles != null && titles.size() == 1) {
				Element el1 = titles.first();
				String title = "-";
				for (Element el : el1.children()) {
					String filterTagValue = "h1";
					String currentTagName = el.tagName().toLowerCase();
					if (!filterTagValue.equals(currentTagName)) {
						continue;
					}
					title = el.text();
				}
				goodDetail.setGoodTitle(title);
			}

			/*
			 * 获取商品原链接并制造现有链接
			 * 
			 * TODO 55海淘-未完成链接转换
			 * 
			 */
			Elements linksPanel = doc.getElementsByClass("ht-deal-detail-buy");
			if (linksPanel != null && linksPanel.size() > 0) {
				Elements links = linksPanel.select("a");// getElementsByTag("a");
				if (links != null && links.size() > 0) {

					for (Element link : links) {
						if ("ht-deal-detail-buy-btn".equals(link.attr("class"))) {
							String srcLink = link.attr("href");
//							System.out.println("link:"+link);
//							System.out.println("srcLink:" + srcLink);
							// TODO 这里需要获取商品的真实链接
							String urlTmp = null;
							if(link.text().contains("拿返利")){
								urlTmp = getGoodSrcLinkByJsoup(srcLink, BaseRule.GET);
							}else{
								urlTmp = getGoodSrcLinkByJsoup(srcLink, BaseRule.GET);
							}
							
							
							goodDetail.setGoodSrcLink(urlTmp);
							// 洋小二链接生成
							// TODO 这里需要把原始链接转换为洋小二链接
							String yxeHtLink = urlTmp;
							goodDetail.setYxehtLink(yxeHtLink);
						}else{
							continue;
						}
					}
				}
			}

			// 获取商品内容
			Elements mainContent = doc.getElementsByClass("ht-deal-detail-des-box");
			StringBuilder sb = new StringBuilder();
			if (mainContent != null && mainContent.size() > 0) {
				Element subContent = mainContent.first();

				Elements descTags = subContent.getElementsByTag("p");
				for (int i = 0; i < descTags.size(); i++) {
					Element el = descTags.get(i);
					if (i > 1) {
						break;
					}
					Elements ss = el.getElementsByAttributeValue("style", "position:absolute;left:-9999px;top:-9999px");
					ss.remove();
					el.select("a").remove();
					String tmpContent = el.html();
					sb.append(tmpContent);
					sb.append("<br>");
				}

				String mainC = sb.toString();

				goodDetail.setGoodContent(mainC);

			}

			/*
			 * 获取图片地址 55海淘-已完成
			 */
			Elements imgPanel = doc.getElementsByClass("ht-deal-detail-buy");
			List<String> imgLinks = new ArrayList<String>();
			if (imgPanel != null && imgPanel.size() > 0) {
				Element imgEl = imgPanel.first();
				Elements imgTags = imgEl.getElementsByTag("img");
				for (Element img : imgTags) {
					String imgSrc = img.attr("src");
					if (!TextUtil.isEmpty(imgSrc)) {
						if (imgSrc.contains("@")) {
							imgSrc = imgSrc.substring(0, imgSrc.lastIndexOf("@"));
						}
					}
					if (imgSrc.startsWith("//")) {
						imgSrc = imgSrc.replaceFirst("//", "http://");
					}
					imgLinks.add(imgSrc);
				}
				goodDetail.setmImgLinks(imgLinks);
			}

			goodDetail.setzDirect(GoodDetail.NO_ZDIRECT);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return goodDetail;
	}

	public static String getGoodSrcLinkByJsoup(String url, int requestType) {
		String goodSrcLink = null;

		try {
			Connection conn = Jsoup.connect(url).userAgent(
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31");

			Document doc = null;
			switch (requestType) {
			case Rule.GET:
				doc = conn.timeout(100000).get();
				break;
			case Rule.POST:
				doc = conn.timeout(100000).post();
				break;
			}

			Elements elements = doc.getElementsByTag("link");
			for (int i = 0; i < elements.size(); i++) {
				Element el = elements.get(i);
				if ("canonical".equals(el.attr("rel"))) {
					goodSrcLink = el.attr("href");
					break;
				} else {
					// System.out.println("el["+i+"]:"+el);
				}
			}
			
			if(TextUtil.isEmpty(goodSrcLink)){
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
		}

		return goodSrcLink;
	}

	public static String getGoodSrcLink(String urlStr) {
		String goodLink = null;

		try {
			String htmlCode = HtmlCodeDownload.getHtmlCodeByUrl(urlStr);

			Document doc = Jsoup.parse(htmlCode);

			Elements elements = doc.getElementsByTag("link");
			for (int i = 0; i < elements.size(); i++) {
				Element el = elements.get(i);
				if ("canonical".equals(el.attr("rel"))) {
					// System.err.println("期望得到的元素:\n"+el);
					goodLink = el.attr("href");
					break;
				} else {
					// System.out.println("el["+i+"]:"+el);
				}
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// System.out.println("result:"+goodLink);
		return goodLink;
	}
	
	public void justfun(){
		List<String> goodList = null;
		/*
		 * 北美省钱快报-美亚 http://cn.dealmoon.com/Online-Stores/Amazon-com
		 */
		GoodListRule rule = createFetchRules("美亚", "www.amazon.com", Constants.BEI_MEI_SHENG_QIAN_KUAI_BAO,
				BaseRule.GET, "http://cn.dealmoon.com/", "Online-Stores/Amazon-com", BaseRule.CLASS,
				"cnlist_realheight", "Online-Stores/Amazon-com");
				// goodList = fetchGoodLink(rule);

		// 55海淘
		rule = createFetchRules("美亚", "www.amazon.com", Constants.WUWU_HAITAO, BaseRule.GET, "http://www.55haitao.com/",
				"http://www.55haitao.com/store/Amazon.html", BaseRule.CLASS, "index-deal-title", "http://www.55haitao.com/store/Amazon.html");
//		goodList = fetchGoodLink(rule);

		rule = createFetchRules("英亚", "www.amazon.co.uk", Constants.WUWU_HAITAO, BaseRule.GET,
				"http://www.55haitao.com/", "store/amazon.co.uk.html", BaseRule.CLASS, "index-deal-title",
				"store/amazon.co.uk.html");
		// goodList = fetchGoodLink(rule);
		// http://www.55haitao.com/store/Backcountry.html
		rule = createFetchRules("Backcountry户外运动", "www.backcountry.com", Constants.WUWU_HAITAO, BaseRule.GET,
				"http://www.55haitao.com/", "store/Backcountry.html", BaseRule.CLASS, "index-deal-title",
				"store/Backcountry.html");
				// goodList = fetchGoodLink(rule);

		// 没得比
		rule = createFetchRules("英亚", "www.amazon.com", Constants.MEI_DE_BI, BaseRule.GET,
				"http://www.meidebi.com/company/", "61/dp/", BaseRule.CLASS, "tit", "61/dp/");
				// goodList = fetchGoodLink(rule);

		// 什么之得买,http://www.smzdm.com/mall/amazon/haitao/#tabs
		rule = createFetchRules("美亚", "www.amazon.com", Constants.SHENME_ZHIDE_MAI, BaseRule.GET,
				"http://www.smzdm.com/", "mall/amazon/haitao/#tabs", BaseRule.CLASS, "itemName",
				"mall/amazon/haitao/#tabs");
				// goodList = fetchGoodLink(rule);

		// 什么值得买, http://www.smzdm.com/mall/6pm/haitao/#tabs
		rule = createFetchRules("6pm", "www.amazon.com", Constants.SHENME_ZHIDE_MAI, BaseRule.GET,
				"http://www.smzdm.com/", "mall/6pm/haitao/#tabs", BaseRule.CLASS, "itemName", "mall/6pm/haitao/#tabs");
				// goodList = fetchGoodLink(rule);

		// 买手党, http://www.maishoudang.com/merchants/298
		rule = createFetchRules("6pm", "www.amazon.co.uk", Constants.MAI_SHOU_DANG, BaseRule.GET,
				"http://www.maishoudang.com/", "merchants/298", BaseRule.CLASS, "tb-i-read", "merchants/298");
				// goodList = fetchGoodLink(rule);

		// 逛丢,只抓了美亚,依托商品分类进行
		/*
		 * { "zdirect", "digital", "electrical", "makeup", "clothes", "sport",
		 * "daily", "food", "baby", "automobile", "sale" };
		 * cate.php?m=Amazon&k=zdirect&c=us 
		 * cate.php?m=Amazon&k=digital&c=us
		 * cate.php?m=Amazon&k=electrical&c=us 
		 * cate.php?m=Amazon&k=makeup&c=us
		 * cate.php?m=Amazon&k=clothes&c=us 
		 * cate.php?m=Amazon&k=sport&c=us
		 * cate.php?m=Amazon&k=daily&c=us 
		 * cate.php?m=Amazon&k=food&c=us
		 * cate.php?m=Amazon&k=baby&c=us 
		 * cate.php?m=Amazon&k=automobile&c=us
		 * cate.php?m=Amazon&k=sale&c=us
		 */
		String[] guangdiu = {"http://guangdiu.com/cate.php?m=Amazon&k=zdirect&c=us",
												"http://guangdiu.com/cate.php?m=Amazon&k=digital&c=us",
												"http://guangdiu.com/cate.php?m=Amazon&k=electrical&c=us",
												"http://guangdiu.com/cate.php?m=Amazon&k=makeup&c=us",
												"http://guangdiu.com/cate.php?m=Amazon&k=clothes&c=us",
												"http://guangdiu.com/cate.php?m=Amazon&k=sport&c=us",
												"http://guangdiu.com/cate.php?m=Amazon&k=daily&c=us",
												"http://guangdiu.com/cate.php?m=Amazon&k=food&c=us",
												"http://guangdiu.com/cate.php?m=Amazon&k=baby&c=us",
												"http://guangdiu.com/cate.php?m=Amazon&k=automobile&c=us",
												"http://guangdiu.com/cate.php?m=Amazon&k=sale&c=us"};
		
		rule = createFetchRules("美亚", "www.amazon.com", Constants.GUANG_DIU, BaseRule.GET, "http://guangdiu.com/",
				"cate.php?m=Amazon&k=zdirect&c=us", BaseRule.CLASS, "goodname", "cate.php?m=Amazon&k=zdirect&c=us");
		// goodList = fetchGoodLink(rule);

//		System.out.println("当前页面商品数量：" + goodList.size() + "\n");
		// for(int i = 0; i < goodList.size(); i++){
		// System.out.println("goodList["+i+"]:"+goodList.get(i));
		// }

//		int i = 0;
//		for (String link : goodList) {
//			if (i > 0) {
//				break;
//			}
//			GoodDetail gd = fetchGoodDetail(BaseRule.GET, link, "");
//			System.err.println("---------------->[" + i + "]" + gd);
//			i++;
//		}

		// getGoodSrcLink("http://www.55haitao.com/g/3234-d-294898?acm=2.5.24.0");
	}	

}
