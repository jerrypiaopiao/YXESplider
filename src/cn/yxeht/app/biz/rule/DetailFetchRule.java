package cn.yxeht.app.biz.rule;

import java.io.Serializable;

public class DetailFetchRule implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7592763385652531165L;
	
	private String titleCssStyle;//标题样式
	
	private String descCssStyle;//商品详情样式
	
	private String trueLinkCssStyle;//商品原始链接样式,比如亚马逊的源地址
	
	private String imgCssStyle;//图片链接样式
	
	private String typeCssStyle;//商品类型样式

	public String getTitleCssStyle() {
		return titleCssStyle;
	}

	public void setTitleCssStyle(String titleCssStyle) {
		this.titleCssStyle = titleCssStyle;
	}

	public String getDescCssStyle() {
		return descCssStyle;
	}

	public void setDescCssStyle(String descCssStyle) {
		this.descCssStyle = descCssStyle;
	}

	public String getTrueLinkCssStyle() {
		return trueLinkCssStyle;
	}

	public void setTrueLinkCssStyle(String trueLinkCssStyle) {
		this.trueLinkCssStyle = trueLinkCssStyle;
	}

	public String getImgCssStyle() {
		return imgCssStyle;
	}

	public void setImgCssStyle(String imgCssStyle) {
		this.imgCssStyle = imgCssStyle;
	}

	public String getTypeCssStyle() {
		return typeCssStyle;
	}

	public void setTypeCssStyle(String typeCssStyle) {
		this.typeCssStyle = typeCssStyle;
	}
	
	@Override
	public String toString() {
		return "{titleCssStyle:"+titleCssStyle+", descCssStyle:"+descCssStyle+", trueLinkCssStyle:"+trueLinkCssStyle+", imgCssStyle:"+imgCssStyle+", typeCssStyle:"+typeCssStyle+"}@"+DetailFetchRule.class.getSimpleName()+"@"+hashCode();
	}

}
