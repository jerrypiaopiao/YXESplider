package cn.yxeht.app.biz.bean;

import java.io.Serializable;
import java.util.List;

public class GoodDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7309047860975077687L;
	
	public static final int ZDIRECT = 1;
	
	public static final int NO_ZDIRECT = 0;

	private String goodTitle;
	
	private String goodSrcLink;
	
	private String yxehtLink;
	
	private String goodContent;
	
	private List<String> mImgLinks;
	
	/**
	 * 外币价格
	 */
	private String foreignPrice;
	
	/**
	 * 人民币价格
	 */
	private String zhPrice;
	
	/**
	 * 是否直达中国
	 */
	private int zDirect;
	
	private String type;
	
	private String goodHost;

	public String getGoodTitle() {
		return goodTitle;
	}

	public void setGoodTitle(String goodTitle) {
		this.goodTitle = goodTitle;
	}

	public String getGoodSrcLink() {
		return goodSrcLink;
	}

	public void setGoodSrcLink(String goodSrcLink) {
		this.goodSrcLink = goodSrcLink;
	}

	public String getYxehtLink() {
		return yxehtLink;
	}

	public void setYxehtLink(String yxehtLink) {
		this.yxehtLink = yxehtLink;
	}

	public String getGoodContent() {
		return goodContent;
	}

	public void setGoodContent(String goodContent) {
		this.goodContent = goodContent;
	}

	public List<String> getmImgLinks() {
		return mImgLinks;
	}

	public void setmImgLinks(List<String> mImgLinks) {
		this.mImgLinks = mImgLinks;
	}

	public String getForeignPrice() {
		return foreignPrice;
	}

	public void setForeignPrice(String foreignPrice) {
		this.foreignPrice = foreignPrice;
	}

	public String getZhPrice() {
		return zhPrice;
	}

	public void setZhPrice(String zhPrice) {
		this.zhPrice = zhPrice;
	}

	public int getzDirect() {
		return zDirect;
	}

	public void setzDirect(int zDirect) {
		this.zDirect = zDirect;
	}
	
	@Override
	public String toString() {
		return "是否直邮:"+zDirect
				+" \n商品标题:"+goodTitle
				+" \n商品描述信息:"+goodContent
				+" \n源地址:"+goodSrcLink
				+" \n洋小二:"+yxehtLink
				+"\n共["+ (mImgLinks != null ? mImgLinks.size() : 0) +"]张图片"
				+"\n图片地址:"+(mImgLinks != null ? mImgLinks : "")
				+"\n类型:"+type
				+"\n";
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getGoodHost() {
		return goodHost;
	}

	public void setGoodHost(String goodHost) {
		this.goodHost = goodHost;
	}

}
