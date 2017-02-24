package cn.yxeht.app.biz.rule;

import java.util.List;

import org.apache.http.util.TextUtils;

import cn.yxeht.app.constants.Constants;
import cn.yxeht.app.constants.SMZDMFetchRule;

public class GoodListRule extends BaseRule {

	/**
	 * 数据源域名
	 */
	private String host;
	
	/**
	 * 商品列表url
	 */
	private String goodListUrl;
	
	/**
	 * 商城Host
	 */
	private List<String> goodHost;
	
	/**
	 * 所有的商品详情链接
	 */
	private List<String> goodDetailLinkList;
	
	private String defaultAmazonHost;
	
	private String goodTypeCode;
	
	/**
	 * 自由变量
	 */
	private String freeStr;
	
	/**
	 * 目标网站类型,比如55海淘对应 4
	 */
	private int targetType;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getGoodListUrl() {
		return goodListUrl;
	}

	public void setGoodListUrl(String goodListUrl) {
		this.goodListUrl = goodListUrl;
	}

	public List<String> getGoodHost() {
		return goodHost;
	}

	public void setGoodHost(List<String> goodHost) {
		this.goodHost = goodHost;
	}

	public List<String> getGoodDetailLinkList() {
		return goodDetailLinkList;
	}

	public void setGoodDetailLinkList(List<String> goodDetailLinkList) {
		this.goodDetailLinkList = goodDetailLinkList;
	}

	@Override
	public String getFullGoodListLink() {
		/*we don't need this convert
		 * if(getSourceType() == Constants.SHENME_ZHIDE_MAI){
			if(!TextUtils.isEmpty(getDefaultAmazonHost()) && SMZDMFetchRule.URL_START_WWW.contains(getDefaultAmazonHost())){
				if(host.contains("faxian")){
					host = host.replaceFirst("faxian", "www");
				}else if(host.contains("haitao")){
					host = host.replaceFirst("haitao", "www");
				}
			}
		}*/
		
//		if(goodListUrl.startsWith(host)){
			return goodListUrl;
//		}else{
//			return host + goodListUrl;
//		}
		
	}

	public String getDefaultAmazonHost() {
		return defaultAmazonHost;
	}

	public void setDefaultAmazonHost(String defaultAmazonHost) {
		this.defaultAmazonHost = defaultAmazonHost;
	}

	public String getGoodTypeCode() {
		return goodTypeCode;
	}

	public void setGoodTypeCode(String goodTypeCode) {
		this.goodTypeCode = goodTypeCode;
	}

	public String getFreeStr() {
		return freeStr;
	}

	public void setFreeStr(String freeStr) {
		this.freeStr = freeStr;
	}

	public int getTargetType() {
		return targetType;
	}

	public void setTargetType(int targetType) {
		this.targetType = targetType;
	}
	
}
