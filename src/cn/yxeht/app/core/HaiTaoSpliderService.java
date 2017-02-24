package cn.yxeht.app.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import cn.yxeht.app.AppConfig;
import cn.yxeht.app.biz.bean.GoodDetail;
import cn.yxeht.app.biz.rule.BaseRule;
import cn.yxeht.app.biz.rule.GoodListRule;
import cn.yxeht.app.constants.Constants;
import cn.yxeht.app.constants.MSDFetchRule;
import cn.yxeht.app.constants.SMZDMFetchRule;
import cn.yxeht.app.constants.TypeMatchRule;
import cn.yxeht.app.rule.Rule;
import cn.yxeht.app.utils.TextUtil;

public class HaiTaoSpliderService {

	private static final Logger log = Logger.getLogger(HaiTaoSpliderService.class);
	
	public static List<String> fetchGoodDetailLink(GoodListRule goodListRule) {
		List<String> goodDetailLinks = new ArrayList<String>();

		try {
			String url = goodListRule.getFullGoodListLink();
			String[] params = goodListRule.getParams();
			String[] values = goodListRule.getValues();
			String resultTagName = goodListRule.getClassName();
			int type = goodListRule.getType();
			int requestType = goodListRule.getRequestMoethod();

			// 通过Jsoup抓取网站信息
			Connection conn = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31");
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
				if(Constants.SHENME_ZHIDE_MAI == goodListRule.getSourceType()){
					if(results.size() == 0){
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
				if(!"a".equals(el.tagName())){
					Elements links = el.getElementsByTag("a");
					if(links != null && links.size() > 0){
						el = links.first();
					}
				}
				
				if(goodListRule.getGoodTypeCode().equals(MSDFetchRule.CATEGARY_EBAY)){
					if("查看全文".equals(el.text())){
						String linkHref = el.attr("href");
						goodDetailLinks.add(linkHref);
					}
				}else{
					String linkHref = el.attr("href");
					goodDetailLinks.add(linkHref);
				}
			}

		} catch (IOException e) {
			log.error(AppConfig.formatLog(e.getLocalizedMessage()), e.getCause());
		}

		return goodDetailLinks;

	}

	/**
	 * guangdiu抓取
	 * @param goodHost
	 * @param url
	 * @param requestType
	 */
	public static GoodDetail fetchGDGoodDetail(String goodHost, String url, int requestType) {
		
		System.err.println("逛丢:"+url);
		
		GoodDetail goodDetail = null;
		try {
			Connection conn = Jsoup.connect(url);

			Document doc = null;
			switch (requestType) {
			case Rule.GET:
				doc = conn.timeout(100000).get();
				break;
			case Rule.POST:
				doc = conn.timeout(100000).post();
				break;
			}
			
			goodDetail = new GoodDetail();
			
			//获取是否直邮中国的标签
			Elements zdirectsquare = doc.getElementsByClass("zdirectsquare");
			//开始获取商品直邮信息
			if(zdirectsquare != null && zdirectsquare.size() > 0){
				goodDetail.setzDirect(GoodDetail.ZDIRECT);
			}else{
				goodDetail.setzDirect(GoodDetail.NO_ZDIRECT);
			}
			
			//获取商品标题与链接
			Elements titleAndLink = doc.getElementsByClass("dtitlelink");
			//开始获取商品标题与链接信息
			if(titleAndLink != null && titleAndLink.size() > 0){
				goodDetail.setGoodTitle(titleAndLink.get(0).text());
				String srcLink = titleAndLink.get(0).attr("href");
				goodDetail.setGoodSrcLink(srcLink);
				if(!TextUtil.isEmpty(srcLink)){
					//goodDetail.setYxehtLink(AmazonLinkConvertor.convertToYxeLink(goodHost, srcLink));
				}
			}else{
				//TODO do nothing here...
				log.info(AppConfig.formatLog("GuangDiu-title_link-filter:标题/商品链接获取失败"));
				return null;
			}
			
			//开始获取商品描述信息
			Element goodDesc = doc.getElementById("dabstract");
			if(goodDesc == null){
				//TODO 逛丢的权宜之计
				log.info(AppConfig.formatLog("GuangDiu-goodDesc-filter:"+goodDesc));
				return null;
			}
			log.info(AppConfig.formatLog("fetch GuangDiu good:"+goodDesc));
			goodDetail.setGoodContent(goodDesc.text());
			
			Elements imgs = goodDesc.getElementsByTag("img");
			List<String> imgLinks = new ArrayList<String>();
			if(imgs == null || imgs.size() == 0){
				Elements tmpImgs = doc.getElementsByClass("dimage");
				if(tmpImgs != null && tmpImgs.size() == 1){
					Elements images = tmpImgs.get(0).getElementsByTag("img");
					for(Element img : images){
						imgLinks.add(img.attr("src"));
					}
				}
			}else{
				for(Element img : imgs){
					imgLinks.add(img.attr("src"));
				}
			}
			
			goodDetail.setmImgLinks(imgLinks);
			
		} catch (IOException e) {
			log.error(AppConfig.formatLog(e.getLocalizedMessage()), e.getCause());
			return null;
		}
		
		return goodDetail;

	}
	
	public static GoodDetail fetchMSDGoodDetail(String url, String goodHost){
		log.info(AppConfig.formatLog("Fetch \"MaiShouDang\" by link ["+url+"]"));
		GoodDetail goodDetail = null;
		
		HashMap<String, String> typeMatchRule = TypeMatchRule.MAISHOUDANG_TYPE_MACTH_RULE;//initMSDTypeMatchRule();
		
		try {
			
			goodDetail = new GoodDetail();
			
			Connection conn = Jsoup.connect(url);

			Document doc = null;
			int requestType = Rule.GET;
			switch (requestType) {
			case Rule.GET:
				doc = conn.timeout(100000).get();
				break;
			case Rule.POST:
				doc = conn.timeout(100000).post();
				break;
			}
			
			Elements allContent = doc.getElementsByClass("ty-article");
			
			if(allContent != null && allContent.size() > 0){
				
				Element content = allContent.get(0);
				
				//获取标题
				Elements titles = content.getElementsByTag("h1");
				if(titles != null && titles.size() > 0){
					Element title = titles.first();
					goodDetail.setGoodTitle(title.text());
				}
				
				Elements types = content.getElementsByClass("info-li");
				//获取类型
				if(types != null && types.size() > 0){
					Element type = types.first();
					
					Elements subTypes = type.getElementsByTag("a");
					
					if(subTypes != null && subTypes.size() >= 2){
						Element subType = subTypes.get(1);
						String typeStr = subType.text();
						if("买手党活动".equals(typeStr) || "资讯/优惠券".equals(typeStr) || (!TextUtil.isEmpty(typeStr) && typeStr.startsWith("资讯"))){
							log.info(AppConfig.formatLog("MaiShouDang-type-filter:["+typeStr+"]by url["+url+"]"));
							return null;
						}
						goodDetail.setType(typeMatchRule.get(typeStr));
					}
					
					if(TextUtil.isEmpty(goodDetail.getType())){
						goodDetail.setType("6");
					}
					
				}
				
				//获取商品原链接并制造现有链接
				Elements links = content.getElementsByClass("tb-i-buy");
				if(links != null && links.size() > 0){
					Element link = links.first();
					String tag = link.tagName();
					if("a".equals(tag)){
						String srcLink = link.attr("href");
						goodDetail.setGoodSrcLink(srcLink);
						//洋小二链接生成
						String yxeHtLink = null;
						try {
							//yxeHtLink = AmazonLinkConvertor.convertToYxeLink(goodHost, srcLink);
						} catch (Exception e) {
							System.err.println("create yxeLink error:"+e.getLocalizedMessage());
						}
						if(TextUtil.isEmpty(yxeHtLink)){
							log.info(AppConfig.formatLog("MaiShouDang-link-filter:[unknown-goodlink:"+srcLink+"]by url["+url+"]"));
							return null;
						}
						goodDetail.setYxehtLink(yxeHtLink);
					}
				}
				
				//获取商品内容与图片链接
				Elements mainContent = content.getElementsByClass("ty-text");
				if(mainContent != null && mainContent.size() > 0){
					Element subContent = mainContent.first();
					Elements pTags = subContent.getElementsByTag("p");
					
					//获取商品内容
					StringBuilder sb = new StringBuilder();
					
					for(Element p : pTags){
						String str = p.text();
						if(!TextUtil.isEmpty(str)){
							sb.append(str);
							sb.append("<br>");
						}
					}
					
					goodDetail.setGoodContent(sb.toString());
					
					Elements imgTags = subContent.getElementsByTag("img");
					List<String> imgLinks = new ArrayList<String>();
					for(Element img : imgTags){
						imgLinks.add(img.attr("src"));
					}
					
					goodDetail.setmImgLinks(imgLinks);
					
				}
					
			}else{
				log.info(AppConfig.formatLog("MaiShouDang-content-filter:[ty-article:解析失败]by url["+url+"]"));
				return null;
			}
			
			goodDetail.setzDirect(GoodDetail.NO_ZDIRECT);
			
		} catch (IOException e) {
			log.error(AppConfig.formatLog("MaiShouDang--exception-filter:"+e.getLocalizedMessage()), e.getCause());
			return null;
		}
		
		return goodDetail;
		
	}
	
	public static GoodDetail fetchSMZDMGoodDetail(String url, String hostType){
		log.info(AppConfig.formatLog("Fetch \"SMZDM\" by link ["+url+"]"));
		GoodDetail goodDetail = null;
		
		HashMap<String, String> typeMatchRule = TypeMatchRule.SMZDM_TYPE_MACTH_RULE;
		
		try {
			
			goodDetail = new GoodDetail();
			
			Connection conn = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31");

			Document doc = null;
			int requestType = Rule.GET;
			switch (requestType) {
			case Rule.GET:
				doc = conn.timeout(100000).get();
				break;
			case Rule.POST:
				doc = conn.timeout(100000).post();
				break;
			}
			
			Elements allContent = doc.getElementsByClass("article-details");
			
			Elements type = doc.getElementsByClass("crumbs");
			
			if(type != null && type.size() > 0){
				//TODO 获取类型暂时只设置默认为6
				Elements types = type.get(0).getElementsByClass("crumbsCate");
				//获取类型
				if(types != null && types.size() > 0){
					if(types.size() >= 2){
						String typeStr = types.get(1).text().trim();
						goodDetail.setType(typeMatchRule.get(typeStr));
					}else{
						goodDetail.setType("6");
					}
				}
			}
			
			if(TextUtil.isEmpty(goodDetail.getType())){
				goodDetail.setType("6");
			}
			
			if(allContent != null && allContent.size() > 0){
				
				Element content = allContent.get(0);
				
				//获取标题
				Elements titles = content.getElementsByClass("article_title");
				if(titles != null && titles.size() > 0){
					Element title = titles.first();
					goodDetail.setGoodTitle(title.text());
				}
				
				//获取商品原链接并制造现有链接
				Elements linksPanel = content.getElementsByClass("buy");
				if(linksPanel != null && linksPanel.size() > 0){
					Element linkPanel = linksPanel.first();
					Elements links = linkPanel.getElementsByTag("a");
					if (links != null && links.size() > 0) {
						Element link = links.first();
						String tag = link.tagName();
						if ("a".equals(tag)) {
							String srcLink = link.attr("href");
							goodDetail.setGoodSrcLink(srcLink);
							//洋小二链接生成
							String yxeHtLink = srcLink;
							goodDetail.setYxehtLink(yxeHtLink);
						}
					}
				}
				
				//获取商品内容
				Elements mainContent = content.getElementsByClass("item-preferential");
				StringBuilder sb = new StringBuilder();
				StringBuilder tmpSB = new StringBuilder();
				if(mainContent != null && mainContent.size() > 0){
					Element subContent = mainContent.first();
					
					Elements baoliao = subContent.getElementsByClass("baoliao-block");
					
					for(Element bb : baoliao){
						Elements pTags = bb.getElementsByTag("p");
						if(pTags.size() > 0){
							for(Element p : pTags){
								String c = p.text();
								sb.append(c);
								sb.append("<br>");
								tmpSB.append(c);
							}
						}
					}
					
					if(TextUtil.isEmpty(tmpSB.toString())){
						for(Element bb : baoliao){
							String  tmpContent = bb.text();
							sb.append(tmpContent);
							sb.append("<br>");
						}
					}
					
					String mainC = sb.toString();
					if(mainC.contains("爆料原文：")){
						mainC = mainC.substring(mainC.indexOf("爆料原文：") + "爆料原文：".length());
					}
					
					for(String word : SMZDMFetchRule.WORD_FILTER){
						if(mainC.contains(word)){
							mainC = mainC.replaceAll(word, "");
						}
					}
					
					if(mainC.contains("尺码参考")){
						mainC = mainC.substring(0, mainC.indexOf("尺码参考"));
					}
					
					goodDetail.setGoodContent(mainC);
					
				}
				
				//获取图片地址
				int imgCount = 0;
				Elements imgPanel = content.getElementsByClass("bigImgContent");
				List<String> imgLinks = new ArrayList<String>();
				if(imgPanel != null && imgPanel.size() > 0){
					Element imgEl = imgPanel.first();
					Elements imgTags = imgEl.getElementsByTag("img");
					for(Element img : imgTags){
						String imgSrc = img.attr("src");
						if(!TextUtil.isEmpty(imgSrc) && imgSrc.contains("_d")){
							imgSrc = imgSrc.substring(0, imgSrc.lastIndexOf("_d"));
						}
						imgLinks.add(imgSrc);
					}
					goodDetail.setmImgLinks(imgLinks);
				}
				
				if(imgCount == 0){
					Elements imgPanel_2 = content.getElementsByClass("pic-Box");
					if(imgPanel_2 != null && imgPanel_2.size() > 0){
						Element imgEl = imgPanel_2.first();
						Elements imgTags = imgEl.getElementsByTag("img");
						for(Element img : imgTags){
							String imgSrc = img.attr("src");
							if(!TextUtil.isEmpty(imgSrc) && imgSrc.contains("_d")){
								imgSrc = imgSrc.substring(0, imgSrc.lastIndexOf("_d"));
							}
							imgLinks.add(imgSrc);
						}
						goodDetail.setmImgLinks(imgLinks);
					}
				}
				
			}
			
			goodDetail.setzDirect(GoodDetail.NO_ZDIRECT);
			
		} catch (IOException e) {
			log.error(AppConfig.formatLog(e.getLocalizedMessage()), e.getCause());
		}
		return goodDetail;
	}
	
	public static String fetchRealLink(String decodeUrl){
		
		String decUrl = null;
		
		try {
			WebClient webClient = new WebClient(BrowserVersion.CHROME);
            //设置webClient的相关参数
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setCssEnabled(false);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            //模拟浏览器打开一个目标网址
            HtmlPage rootPage = webClient.getPage(decodeUrl);
            //主要是这个线程的等待 因为js加载也是需要时间的
            log.info(AppConfig.formatLog("parse \"SMZDM\" good detail page--------->decodeUrl:"+decodeUrl));
            Thread.sleep(3000);
            String html = rootPage.asXml();
			
			Document doc = Jsoup.parse(html);
			Element el = doc.getElementById("truelink");
			if(el != null){
				decUrl = el.text();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			log.error(AppConfig.formatLog(e.getLocalizedMessage()), e.getCause());
			decUrl = null;
		}catch (InterruptedException e){
			e.printStackTrace();
			log.error(AppConfig.formatLog(e.getLocalizedMessage()), e.getCause());
			decUrl = null;
		}
		
		return decUrl;
		
	}

}
