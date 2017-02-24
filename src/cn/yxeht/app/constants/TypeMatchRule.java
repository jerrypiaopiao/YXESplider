package cn.yxeht.app.constants;

import java.util.HashMap;
import java.util.Iterator;

import cn.yxeht.app.AppConfig;
import cn.yxeht.app.utils.TextUtil;

public class TypeMatchRule {

	public static final HashMap<String, String> GUANGDIU_TYPE_MATCH_RULE = new HashMap<String, String>();
	static {
		// <option value="1">电子数码</option>
		// <option value="2">钟表饰品</option>
		// <option value="3">服装鞋包</option>
		// <option value="4">个护化妆</option>
		// <option value="5">运动户外</option>
		// <option value="6">日用百货</option>
		// <option value="7">食品保健</option>
		// <option value="8">母婴用品</option>
		// <option value="393">汽车用品</option>
		// <option value="394">商城活动</option>
		// <option value="395">商城活动</option>
		GUANGDIU_TYPE_MATCH_RULE.put("1", "digital");// ,electrical");
		GUANGDIU_TYPE_MATCH_RULE.put("2", "makeup");
		GUANGDIU_TYPE_MATCH_RULE.put("3", "clothes");
		GUANGDIU_TYPE_MATCH_RULE.put("4", "makeup");
		GUANGDIU_TYPE_MATCH_RULE.put("5", "sport");
		GUANGDIU_TYPE_MATCH_RULE.put("6", "daily");
		GUANGDIU_TYPE_MATCH_RULE.put("7", "food");
		GUANGDIU_TYPE_MATCH_RULE.put("8", "baby");
		if (!AppConfig.IS_YXEHT) {
			GUANGDIU_TYPE_MATCH_RULE.put("393", "automobile");
			GUANGDIU_TYPE_MATCH_RULE.put("394", "sale");
			GUANGDIU_TYPE_MATCH_RULE.put("395", "zdirect");
		}
		// else{
		// <option value="1">电子数码</option>
		// <option value="2">钟表饰品</option>
		// <option value="3">服装鞋包</option>
		// <option value="4">个护化妆</option>
		// <option value="5">运动户外</option>
		// <option value="6">日用百货</option>
		// <option value="7">食品保健</option>
		// <option value="8">母婴用品</option>
		// <option value="366">我来试试</option>
		// <option value="392">test</option>
		// GUANGDIU_TYPE_MATCH_RULE.put("1", "digital");//,electrical");
		// GUANGDIU_TYPE_MATCH_RULE.put("2", "makeup");
		// GUANGDIU_TYPE_MATCH_RULE.put("3", "clothes");
		// GUANGDIU_TYPE_MATCH_RULE.put("4", "makeup");
		// GUANGDIU_TYPE_MATCH_RULE.put("5", "sport");
		// GUANGDIU_TYPE_MATCH_RULE.put("6", "daily");
		// GUANGDIU_TYPE_MATCH_RULE.put("7", "food");
		// GUANGDIU_TYPE_MATCH_RULE.put("8", "baby");
		// GUANGDIU_TYPE_MATCH_RULE.put("393", "automobile");
		// GUANGDIU_TYPE_MATCH_RULE.put("394", "sale");
		// GUANGDIU_TYPE_MATCH_RULE.put("395", "zdirect");
		// }

	}

	public static final String[] GUANGDIU_TYPE_STRING = { "zdirect", "digital",
			"electrical", "makeup", "clothes", "sport", "daily", "food",
			"baby", "automobile", "sale" };

	public static String getGoodTypeIdByNameGuangDiu(String name) {

		if (TextUtil.isEmpty(name)) {
			return "6";
		}

		String id = "6";

		Iterator<String> keyIter = GUANGDIU_TYPE_MATCH_RULE.keySet().iterator();

		while (keyIter.hasNext()) {
			String idStr = keyIter.next();
			String value = GUANGDIU_TYPE_MATCH_RULE.get(idStr);
			if (!TextUtil.isEmpty(value)) {
				if (value.equals(name) || value.contains(name)) {
					id = idStr;
					break;
				}
			}
		}

		return id;
	}

	public static final HashMap<String, String> MAISHOUDANG_TYPE_MACTH_RULE;

	static {
		MAISHOUDANG_TYPE_MACTH_RULE = initMSDTypeMatchRule();
	}

	public static HashMap<String, String> initMSDTypeMatchRule() {

		HashMap<String, String> maps = new HashMap<String, String>();

		String elec = "笔记本 电脑整机 平板电脑 电脑组件 外设产品 网络产品 网络设备 办公用品 办公文仪 电子书刊 书刊杂志 软件游戏 影像制品 手机通讯 相机影像 影音娱乐 大家电 生活电器 厨房电器 个护电器 手机配件 数码配件 新奇数码家电 其它家电";
		String[] elecs = elec.split(" ");
		for (String e : elecs) {
			maps.put(e, "1");
		}

		String clock = "男表 女表 眼镜 首饰";
		String[] clocks = clock.split(" ");
		for (String c : clocks) {
			maps.put(c, "2");
		}

		String clothes = "童装 女装 男装 男鞋 女鞋 童鞋 精品女包 精品男包 功能箱包 家居内饰 装饰配饰";
		String[] clothess = clothes.split(" ");
		for (String c : clothess) {
			maps.put(c, "3");
		}

		String makeup = "洗护用品 口腔护理 面部护理 成人用品 魅力彩妆 身体护理 女性护理 香水";
		String[] makeups = makeup.split(" ");
		for (String m : makeups) {
			maps.put(m, "4");
		}

		String sport = "户外鞋服 户外休闲 户外工具 运动装备";
		String[] sports = sport.split(" ");
		for (String s : sports) {
			maps.put(s, "5");
		}

		String daily = "厨具家具 灯具工具 清洁用品 烹饪锅具 居家装饰 收纳保鲜 水具酒具 餐具 茶具咖啡具 床上用品 保温保冷杯";
		String[] dailies = daily.split(" ");
		for (String d : dailies) {
			maps.put(d, "6");
		}

		String foodMedic = "保健品 食品饮品 日常药品 进口食品 休闲食品 宠物食品 粮油调味 中外名酒 饮料冲调 营养健康 生鲜食品";
		String[] foodMedics = foodMedic.split(" ");
		for (String f : foodMedics) {
			maps.put(f, "7");
		}

		String baby = "宝宝食品 营养辅食 尿裤湿巾 喂养用品 童车童床 服饰寝居 妈妈专区 玩模乐器 乐器相关";
		String[] babies = baby.split(" ");
		for (String b : babies) {
			maps.put(b, "8");
		}

		if (!AppConfig.IS_YXEHT) {
			String automobile = "汽车用品 车载用品 安全自驾";
			String[] automobiles = automobile.split(" ");
			for (String a : automobiles) {
				maps.put(a, "393");
			}

			String sale = "旅游产品 资讯/优惠券 买手党活动 其他产品";
			String[] sales = sale.split(" ");
			for (String s : sales) {
				maps.put(s, "394");
			}
		}
		return maps;

	}

	public static final HashMap<String, String> SMZDM_TYPE_MACTH_RULE;

	static {
		SMZDM_TYPE_MACTH_RULE = initSMZDMTypeMatchRule();
	}

	public static HashMap<String, String> initSMZDMTypeMatchRule() {
		HashMap<String, String> maps = new HashMap<String, String>();

		String elec = "电脑数码 家用电器 图书音像 玩模乐器";
		String[] elecs = elec.split(" ");
		for (String e : elecs) {
			maps.put(e, "1");
		}

		String clock = "礼品钟表";
		String[] clocks = clock.split(" ");
		for (String c : clocks) {
			maps.put(c, "2");
		}

		String clothes = "服饰鞋包";
		String[] clothess = clothes.split(" ");
		for (String c : clothess) {
			maps.put(c, "3");
		}

		String makeup = "个护化妆";
		String[] makeups = makeup.split(" ");
		for (String m : makeups) {
			maps.put(m, "4");
		}

		String sport = "运动户外";
		String[] sports = sport.split(" ");
		for (String s : sports) {
			maps.put(s, "5");
		}

		String daily = "日用百货 办公设备 家居家装";
		String[] dailies = daily.split(" ");
		for (String d : dailies) {
			maps.put(d, "6");
		}

		String foodMedic = "食品保健";
		String[] foodMedics = foodMedic.split(" ");
		for (String f : foodMedics) {
			maps.put(f, "7");
		}

		String baby = "母婴用品";
		String[] babies = baby.split(" ");
		for (String b : babies) {
			maps.put(b, "8");
		}

		if (!AppConfig.IS_YXEHT) {
			String automobile = "汽车用品";
			String[] automobiles = automobile.split(" ");
			for (String a : automobiles) {
				maps.put(a, "393");
			}

			String sale = "其他分类";
			String[] sales = sale.split(" ");
			for (String s : sales) {
				maps.put(s, "394");
			}
		}
		return maps;
	}

}
