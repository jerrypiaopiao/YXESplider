package cn.yxeht.app.bean;

import java.io.Serializable;

public class MerInfoBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6977337039418584437L;

	private String merName;//商家名称
	private String merHost;//商家域名
	private String srcHost;//蜘蛛目标网站的默认域名
	private String srcLink;//蜘蛛目标地址,比如55海涛的美亚
	private int merType;//商家对应的索引值,方便查找
	
	public String getMerName() {
		return merName;
	}
	public void setMerName(String merName) {
		this.merName = merName;
	}
	public String getMerHost() {
		return merHost;
	}
	public void setMerHost(String merHost) {
		this.merHost = merHost;
	}
	public String getSrcHost() {
		return srcHost;
	}
	public void setSrcHost(String srcHost) {
		this.srcHost = srcHost;
	}
	public String getSrcLink() {
		return srcLink;
	}
	public void setSrcLink(String srcLink) {
		this.srcLink = srcLink;
	}
	
	public int getMerType() {
		return merType;
	}
	public void setMerType(int merType) {
		this.merType = merType;
	}
	@Override
	public String toString() {
		return "{merName: "+merName+" , merHost:"+merHost+", srcLink:"+srcLink+"}@"+super.toString();
	}
	
}
