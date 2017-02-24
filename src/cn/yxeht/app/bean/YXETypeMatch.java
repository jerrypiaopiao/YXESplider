package cn.yxeht.app.bean;

import java.io.Serializable;
import java.util.List;

public class YXETypeMatch implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2119359296042159370L;
	
	private String yxeIndex;
	
	private List<String> targetTypes;

	public String getYxeIndex() {
		return yxeIndex;
	}

	public void setYxeIndex(String yxeIndex) {
		this.yxeIndex = yxeIndex;
	}

	public List<String> getTargetTypes() {
		return targetTypes;
	}

	public void setTargetTypes(List<String> targetTypes) {
		this.targetTypes = targetTypes;
	}
	
}
