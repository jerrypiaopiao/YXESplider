package cn.yxeht.app.bean;

public class BizUserTags {

	private String bizManId;
	
	private String bizManName;
	
	private String bizManTag;
	
	public BizUserTags(String bizManId, String bizManName,String bizManTag) {
		this.bizManId = bizManId;
		this.bizManName = bizManName;
		this.bizManTag = bizManTag;
	}

	public String getBizManId() {
		return bizManId;
	}

	public void setBizManId(String bizManId) {
		this.bizManId = bizManId;
	}

	public String getBizManName() {
		return bizManName;
	}

	public void setBizManName(String bizManName) {
		this.bizManName = bizManName;
	}

	public String getBizManTag() {
		return bizManTag;
	}

	public void setBizManTag(String bizManTag) {
		this.bizManTag = bizManTag;
	}
	
	@Override
	public String toString() {
		return super.toString()+"> bizManId:"+bizManId+", bizManName:"+bizManName+", bizManTag:"+bizManTag;
	}
	
}
