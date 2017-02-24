package cn.yxeht.app.biz.rule;

public abstract class BaseRule {

	public final static int CLASS = 0;
	public final static int ID = 1;
	public final static int SELECTION = 2;

	public final static int GET = 0;
	public final static int POST = 1;
	
	private String ruleName;
	
	private String className;
	
	private String tagId;
	
	private String selectionName;
	
	/**
	 * 参数集合
	 */
	private String[] params;
	/**
	 * 参数对应的值
	 */
	private String[] values;
	
	private int type;
	
	private int sourceType;
	
	private String nextCss;
	
	/**
	 * GET / POST 请求的类型，默认GET
	 */
	private int requestMoethod = GET;

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

	public String getSelectionName() {
		return selectionName;
	}

	public void setSelectionName(String selectionName) {
		this.selectionName = selectionName;
	}
	
	public String[] getParams() {
		return params;
	}

	public void setParams(String[] params) {
		this.params = params;
	}

	public String[] getValues() {
		return values;
	}

	public void setValues(String[] values) {
		this.values = values;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getRequestMoethod() {
		return requestMoethod;
	}

	public void setRequestMoethod(int requestMoethod) {
		this.requestMoethod = requestMoethod;
	}

	public int getSourceType() {
		return sourceType;
	}

	public void setSourceType(int sourceType) {
		this.sourceType = sourceType;
	}
	
	public abstract String getFullGoodListLink();

	public String getNextCss() {
		return nextCss;
	}

	public void setNextCss(String nextCss) {
		this.nextCss = nextCss;
	}

}
