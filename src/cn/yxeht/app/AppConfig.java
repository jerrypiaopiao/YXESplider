package cn.yxeht.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import cn.yxeht.app.bean.BizUserTags;
import cn.yxeht.app.constants.AmazonCfgInfo;
import cn.yxeht.app.controller.IndexController;
import cn.yxeht.app.controller.YXEController;
import cn.yxeht.app.core.YXEConfLoad;
import cn.yxeht.app.table.GoodInfo;
import cn.yxeht.app.table.Goods;
import cn.yxeht.app.table.Goodstype;
import cn.yxeht.app.table.Merchant;
import cn.yxeht.app.table.Picture;
import cn.yxeht.app.table.SpliderFilterWord;
import cn.yxeht.app.table.SpliderGoodType;
import cn.yxeht.app.table.SpliderInfo;
import cn.yxeht.app.table.UserTags;
import cn.yxeht.app.table.Users;
import cn.yxeht.app.utils.TextUtil;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.render.ViewType;

public class AppConfig extends JFinalConfig {

	private static final Logger log = Logger.getLogger(AppConfig.class);
	
	public static final String LOG_START_TAG = ">>>>>>>>>>>>>";
	public static final String LOG_END_TAG = "<<<<<<<<<<<<<";
	
	public static int bizManId;
	public static String bizManName;
	public static String bizManTag;	
	public static String bizManTagNames;
	public static List<Merchant> ALL_MERCHANT;
	public static List<String> ALL_MERCHANT_HOST;
//	public static Map<String, Merchant> MERCHANTID_NAME_MAP;
	public static List<Goodstype> ALL_GOODTYPE;
	public static Map<String, Goodstype> GOODTYE_MAP;
	public static String IMAGE_SAVE_PATH;
	public static String IMAGE_URL;
	public static String USER_TAGS;
	public static final boolean IS_DEV  = false;
	public static final boolean IS_YXEHT = false;
	
	public static String smzdm = "";
	
	public static String HOST = "";
	
	@Override
	public void configConstant(Constants me) {
		if(IS_YXEHT){
			PropKit.use("system_config_yxeht_info.properties");
		}else{
			PropKit.use("system_config_info.properties");
		}
		me.setDevMode(PropKit.getBoolean("devMode", false));
		me.setEncoding("utf-8");
		me.setViewType(ViewType.JSP);
		
		if(IS_YXEHT){
			bizManId = PropKit.getInt("biz_man_uid", 5);
			bizManName = PropKit.get("biz_man_name", "zimuge");
			bizManTag = PropKit.get("biz_man_tag", "55htaob6-20");
			bizManTagNames = PropKit.get("biz_man_tag_names", "zimuge");
		}else{
			bizManId = PropKit.getInt("biz_man_uid", 5);
			bizManName = PropKit.get("biz_man_name", "mutou");
			bizManTag = PropKit.get("biz_man_tag", "yx2x2-20");
			bizManTagNames = PropKit.get("biz_man_tag_names", "aaaaac");
		}
		
		//加载目标网站配置信息
		YXEConfLoad.loadSpliderTargetInfo();
		
	}

	@Override
	public void configRoute(Routes me) {
		// TODO Auto-generated method stub
		me.add("/old", IndexController.class, "/o_splider");
		me.add("/", YXEController.class);
	}

	@Override
	public void configPlugin(Plugins me) {

		//配置c3p0数据库连接池插件
		C3p0Plugin cp = new C3p0Plugin(PropKit.get("jdbcUrl"), PropKit.get("username"), PropKit.get("password"));
		me.add(cp);
		
		//配置ActiveRecord插件
		ActiveRecordPlugin arp = new ActiveRecordPlugin(cp);
		me.add(arp);
		
		//表映射关系
//		h_goods 商品表
//		h_goodstype 商品类型表
//		h_merchant 商家表
//		h_users 用户表
//		h_usertags 用户标签表
		arp.addMapping("h_goods", Goods.class);
		arp.addMapping("h_goodstype", Goodstype.class);
		arp.addMapping("h_merchant", Merchant.class);
		arp.addMapping("h_users", Users.class);
		arp.addMapping("h_userstags", UserTags.class);
		arp.addMapping("h_picture", Picture.class);
		arp.addMapping("h_goodsinfo", GoodInfo.class);
		//蜘蛛配置相关
		arp.addMapping("h_splider_info", SpliderInfo.class);
		arp.addMapping("h_splider_type", SpliderGoodType.class);
		arp.addMapping("h_splider_filter_word", SpliderFilterWord.class);
		
	}

	@Override
	public void configInterceptor(Interceptors me) {
		
	}

	@Override
	public void configHandler(Handlers me) {
		me.add(new ContextPathHandler("basePath"));
	}
	
	@Override
	public void afterJFinalStart() {
		super.afterJFinalStart();
		initCache();
		IMAGE_SAVE_PATH = PropKit.get("image_save_path");
		IMAGE_URL = PropKit.get("image_url_prefix");
		HOST = PropKit.get("YXE_HOST");
	}
	
	public static void initCache(){

		//从数据库中加载商品分类信息
		if(AppConfig.ALL_GOODTYPE == null){
			AppConfig.ALL_GOODTYPE = Goodstype.me.find("select * from h_goodstype where father=?", 0);
			
			if(GOODTYE_MAP == null || GOODTYE_MAP.size() == 0){
				GOODTYE_MAP = new HashMap<String, Goodstype>();
			}else{
				GOODTYE_MAP.clear();
			}
			
			for(Goodstype type : ALL_GOODTYPE){
				GOODTYE_MAP.put(String.valueOf(type.getInt("id")), type);
			}
			log.info("typeShow:"+GOODTYE_MAP);
		}
		
		//根据特定用户查找这个用户每个商户的tags，主要针对亚马逊商家
		List<UserTags> specialTagsByUser = UserTags.me.find("select * from h_userstags where username=?", bizManTagNames);
		if(AmazonCfgInfo.ALL_AMAZON_HOST_TAG_MATCH != null && AmazonCfgInfo.ALL_AMAZON_HOST_TAG_MATCH.size() > 0){
			AmazonCfgInfo.ALL_AMAZON_HOST_TAG_MATCH.clear();
		}
		
		//商家联盟信息初始化
		if(AmazonCfgInfo.ALL_AMAZON_HOST_TAG_MATCH.size() == 0){
			for(UserTags tag : specialTagsByUser){
				int manId = tag.getInt("uid");
				Users user = Users.me.findFirst("select * from h_users where uid=?", manId);
				String url = tag.getStr("url");
				String[] replaceUrl = TextUtil.replaceFirstHttpInUrl(url);
				if(replaceUrl != null && replaceUrl.length > 1){
					url = replaceUrl[1];
				}
				String trueTag = tag.getStr("truetags");
				BizUserTags bizUserTag = new BizUserTags(String.valueOf(manId), user.getStr("username"), trueTag);
				AmazonCfgInfo.ALL_AMAZON_HOST_TAG_MATCH.put(url, bizUserTag);
				
			}
		}
		
	}
	
	public static String formatLog(String log){
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append(LOG_START_TAG);
		sb.append("\n");
		sb.append("is in dev mode:"+IS_DEV+", is yxeht:"+IS_YXEHT);
		sb.append("\n");
		sb.append(TextUtil.isEmpty(log) ? "Null Log" : log);
		sb.append("\n");
		sb.append(LOG_END_TAG);
		sb.append("\n");
		return sb.toString();
	}

}
