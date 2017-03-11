package cn.yxeht.app.core;

import java.net.URLDecoder;
import java.util.HashMap;

import org.apache.log4j.Logger;

import cn.yxeht.app.AppConfig;
import cn.yxeht.app.bean.BizUserTags;
import cn.yxeht.app.constants.AmazonCfgInfo;
import cn.yxeht.app.constants.Constants;
import cn.yxeht.app.table.Merchant;
import cn.yxeht.app.utils.TextUtil;

public class LinkConvertor {

	private static final String TAG = LinkConvertor.class.getSimpleName();
	private static final String demo_tag = "yxe_tag";
	
	private static final Logger log = Logger.getLogger(LinkConvertor.class);

	public static String[] convertToYxeLink(String srcGoodHost, String srcLink, String goodUrl) {
		
		log.info(AppConfig.formatLog("convertToYxeLink#srcGoodHost:"+srcGoodHost+", srcLink:"+srcLink+", goodUrl:"+goodUrl));
		
		String yxeLink = srcLink;

		String[] linkArr = new String[2];
		
		if (TextUtil.isEmpty(srcLink)) {
			linkArr[0] = yxeLink;
			linkArr[1] = "unknown_merchant";
			return linkArr;
		}
		
		// 去掉链接开头的http://或https://
		// 参考原始链接:http://www.amazon.com/gp/product/B008UPSGPS/ref=oh_aui_detailpage_o00_s00?ie=UTF8&psc=1&tag=gdiu-20
		String[] srcLinkArr = TextUtil.replaceFirstHttpInUrl(srcLink);
		String http = srcLinkArr[0];// 替换掉的字符串,比如是http://或https://
		srcLink = srcLinkArr[1];// 比如:www.amazon.com/gp/product/B008UPSGPS/ref=oh_aui_detailpage_o00_s00?ie=UTF8&psc=1&tag=gdiu-20
		
		try {
			srcLink = URLDecoder.decode(srcLink, "utf-8");
			if(srcLink.contains("http://")){
				srcLink = srcLink.substring(srcLink.lastIndexOf("http://"));
			}else if(srcLink.contains("https://")){
				srcLink = srcLink.substring(srcLink.lastIndexOf("https://"));
			}
			if(srcLink.contains("&")){
				srcLink = srcLink.substring(0, srcLink.lastIndexOf("&"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		log.info(AppConfig.formatLog("convertToYxeLink#replace http head#http:"+http+", srcLink:"+srcLink));
		
		if (srcLink.contains("http://")) {
			String[] tmpArr = srcLink.split("http://");
			srcLink = tmpArr[1];
			log.info("convertToYxeLink#srcLink has union(http://), find true good link is ["+srcLink+"]");
		} else if (srcLink.contains("https://")) {
			String[] tmpArr = srcLink.split("https://");
			srcLink = tmpArr[1];
			log.info("convertToYxeLink#srcLink has union(https://), find true good link is ["+srcLink+"]");
		}

		// 截取商家域名
		String merHost = "";
		if (!TextUtil.isEmpty(srcLink)) {
			merHost = srcLink.split("/")[0];
			srcGoodHost = merHost;
			log.info("convertToYxeLink#find merchant host in srcLink["+srcLink+"], srcGoodHost is ["+srcGoodHost+"]");
		}

		// TODO 这里需要根据srcGoodHost获取商家信息,主要用来得到联盟设置情况
		Merchant mer = Merchant.me.findFirst("select * from h_merchant where url like '%" + srcGoodHost + "%' and whetherUse=1");
			log.info("convertToYxeLink#find merchant["+srcGoodHost+"] obj in database, merchant obj is ["+mer+"]");
		
		if (mer == null) {
			String[] arr = TextUtil.replaceLastHttpInUrl(srcLink);
			if(arr.length == 2){
				merHost = arr[1].split("/")[0];
				srcGoodHost = merHost;
			}
			mer = Merchant.me.findFirst("select * from h_merchant where url like '%" + srcGoodHost + "%' and whetherUse=1");
			log.info("convertToYxeLink#find merchant["+srcGoodHost+"] obj in database again, merchant obj is ["+mer+"]");
		}

		log.info("convertToYxeLink#srcGoodHost:" + srcGoodHost + ", srcLink:" + srcLink + ", goodUrl:" + goodUrl);

		if (mer == null) {
			srcLink = srcLink.startsWith("http") ? srcLink : (http + srcLink);
			log.info("convertToYxeLink#find merchant["+srcGoodHost+"] is not in database, return srcLink["+srcLink+"] to user");
			linkArr[0] = srcLink;
			linkArr[1] = srcGoodHost;
			return linkArr;
		}

		int merLinkType = mer.getInt("typeM");
		String commissionUrl = mer.getStr("commissionUrl");

		if (TextUtil.isEmpty(commissionUrl)) {
			srcLink = srcLink.startsWith("http") ? srcLink : (http + srcLink);
			log.info("convertToYxeLink#there has no merchant["+srcGoodHost+"] in database, return srcLink["+srcLink+"] to user");
			linkArr[0] = srcLink;
			linkArr[1] = srcGoodHost;
			return linkArr;
		}
		
		if(srcLink.startsWith("www.amazon") && srcLink.contains("?")){
			srcLink = srcLink.substring(0, srcLink.lastIndexOf("?"));
		}

		String[] commUrlArr = TextUtil.replaceFirstHttpInUrl(commissionUrl);
		if (commUrlArr.length == 2) {
			commissionUrl = commUrlArr[1];
		}

		switch (merLinkType) {
		case Constants.MER_UNION_TYPE_TAG:
			// 示例:https://www.amazon.de/?tag=55haistaocn-21

			// String tag =
			// commissionUrl.substring(commissionUrl.lastIndexOf("?"),
			// commissionUrl.length());
			BizUserTags bizUserTags = AmazonCfgInfo.ALL_AMAZON_HOST_TAG_MATCH.get(srcGoodHost);
			String tag = bizUserTags == null ? AppConfig.bizManTag : bizUserTags.getBizManTag();

			String tmpSrcLink = null;

			if (TextUtil.isEmpty(tmpSrcLink)) {
				// TODO 这里需要对临时链接进行处理
				// return null;
			}

			tmpSrcLink = srcLink;

			if (!srcLink.startsWith(srcGoodHost)) {
				srcGoodHost = srcLink.substring(0, srcLink.indexOf("/"));
			}

			if (srcGoodHost.endsWith("/")) {
				srcGoodHost = srcGoodHost.substring(0, srcGoodHost.lastIndexOf("/"));
			}

			/// 这里要对亚马逊的tag做处理{&&
			if (tmpSrcLink.startsWith("dp")) {
				yxeLink = srcGoodHost + tmpSrcLink.substring(0, tmpSrcLink.indexOf("?"));
			} else if (tmpSrcLink.contains("ref=") && !tmpSrcLink.contains("b/")) {
				tmpSrcLink = tmpSrcLink.substring(0, tmpSrcLink.indexOf("/ref="));
				yxeLink = srcGoodHost + "/dp/"
						+ tmpSrcLink.substring(tmpSrcLink.lastIndexOf("/") + 1, tmpSrcLink.length());
			} else if (tmpSrcLink.startsWith("gp") && !tmpSrcLink.contains("ref=")) {
				tmpSrcLink = tmpSrcLink.substring(0, tmpSrcLink.indexOf("?"));
				yxeLink = srcGoodHost + tmpSrcLink.substring(tmpSrcLink.lastIndexOf("/") + 1, tmpSrcLink.length());
			} else if (tmpSrcLink.startsWith("b/")) {
				yxeLink = srcLink.substring(0, srcLink.lastIndexOf("&"));
			} else {
				if (srcLink.contains("?")) {
					yxeLink = srcLink.substring(0, srcLink.lastIndexOf("?"));
				} else {
					yxeLink = srcLink;
				}
			}
			///&&}

			if (yxeLink.endsWith("/")) {
				yxeLink = yxeLink.substring(0, yxeLink.length() - 1);
			}

			yxeLink = yxeLink + "?tag=" + tag;

			break;
		case Constants.MER_UNION_TYPE_URL:
			return null;
		case Constants.MER_UNION_TYPE_URL_SRC_TAG:
			// 示例:https://www.linkhaitao.com/index.php?mod=lhdeal&track=1a79oYhYaFE3_a_b3LdL_bo1OzE4XPBj3OpURqOE6ouY5ClKVlWoLmkKYBp&new=http%3A%2F%2Fwww.gnc.com&tag=mutou
			// commissionUrl =
			String[] tmpCommUrlArr = commissionUrl.split("http");
			if(tmpCommUrlArr.length == 2){
				yxeLink = tmpCommUrlArr[0] + (srcLink.startsWith("http") ? srcLink : (http + srcLink)) + tmpCommUrlArr[1].substring(tmpCommUrlArr[1].lastIndexOf("&"));
			}else{
				yxeLink = tmpCommUrlArr[0] + (srcLink.startsWith("http") ? srcLink : (http + srcLink));
			}
			
			break;
		case Constants.MER_UNION_TYPE_URLTAG_SRC:
			// 示例:http://www.jdoqocy.com/click-7786390-10870161?sid=mutou&url=http%3A%2F%2Fwww.ashford.com%2Fus%2Fhome
			yxeLink = commissionUrl.substring(0, commissionUrl.lastIndexOf("http")) + (srcLink.startsWith("http") ? srcLink : (http + srcLink));
			break;
		}
		
		if (!yxeLink.startsWith("http")) {
			yxeLink = http + yxeLink;
		}
		
		log.info("merLinkType["+merLinkType+"], srcLink["+srcLink+"], yxeLink["+yxeLink+"], srcGoodHost["+srcGoodHost+"]");
		
		linkArr[0] = yxeLink;
		linkArr[1] = srcGoodHost;
		return linkArr;

	}

}
