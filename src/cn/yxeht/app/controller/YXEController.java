package cn.yxeht.app.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.eclipse.jetty.util.log.Log;
import org.quartz.SchedulerException;

import com.jfinal.core.Controller;

import cn.yxeht.app.AppConfig;
import cn.yxeht.app.bean.MerInfoBean;
import cn.yxeht.app.bean.YXETypeMatch;
import cn.yxeht.app.biz.rule.BaseRule;
import cn.yxeht.app.biz.rule.DetailFetchRule;
import cn.yxeht.app.biz.rule.GoodListRule;
import cn.yxeht.app.constants.Constants;
import cn.yxeht.app.core.SpliderService;
import cn.yxeht.app.core.YXEConfLoad;
import cn.yxeht.app.quartz.AutoFetchJob;
import cn.yxeht.app.quartz.AutoFetchJobV2;
import cn.yxeht.app.quartz.QuartzManager;
import cn.yxeht.app.table.Goodstype;
import cn.yxeht.app.table.SpliderFilterWord;
import cn.yxeht.app.table.SpliderGoodType;
import cn.yxeht.app.table.SpliderInfo;
import cn.yxeht.app.utils.TextUtil;

public class YXEController extends Controller {

	private static final Logger log = Logger.getLogger(YXEController.class);

	public static String ROOT_FILE_PATH = "";

	public void index() {
		ROOT_FILE_PATH = getRequest().getSession().getServletContext().getRealPath("/");
		setAttr("biz_man", AppConfig.bizManName);
		setAttr("biz_man_tag", AppConfig.bizManTag);
		setAttr("gap_time", "1");
		AppConfig.initCache();

		setAttr("merchants", AppConfig.ALL_MERCHANT);
		setAttr("goods_types", AppConfig.ALL_GOODTYPE);

		setAttr("is_test", "on");

		String realPath = getRequest().getSession().getServletContext().getRealPath("/");
		setAttr("realPath", realPath);

		if (Constants.ALL_TARGET_FETCH_RULE == null || Constants.ALL_TARGET_FETCH_RULE.size() == 0) {
			YXEConfLoad.loadSpliderTargetInfo();
		}

		setAttr("target_web_info", Constants.TARGET_WEBSITE_LIST);

		render("/index_yxe.jsp");
	}

	/**
	 * 加载分配匹配信息
	 */
	public void loadTypeMatch() {
		// TODO 需要有一个重新加载的过程
		Constants.TYPE_MATCH_RULE_LIST.clear();
		YXEConfLoad.loadTypeMatchRule(getRequest().getSession().getServletContext().getRealPath("/"));
		List<SpliderGoodType> types = SpliderGoodType.me.find("select * from h_splider_type order by h_yxe_type_id asc");
		// setAttr("type_match_rule", Constants.TYPE_MATCH_RULE_LIST);
		setAttr("type_match_rule", types);
		renderJson();
	}

	public void gotoAddTypeMatch() {

		int opt = getParaToInt("opt");
		int yxe_t = getParaToInt("yxe_t");
		int target_web_id = getParaToInt("target_web_id");

		List<Goodstype> goodsTypes = Goodstype.me.find("select * from h_goodstype where father=?", 0);
		
		SpliderGoodType sgt = null;
		
		Constants.TARGET_WEBSITE_LIST.clear();
		YXEConfLoad.loadSpliderTargetInfo();
		setAttr("target_infos", Constants.TARGET_WEBSITE_LIST);
		
		switch (opt) {
		case SpliderGoodType.MODIFY:
			setAttr("goods_types", goodsTypes);
			setAttr("change_type", SpliderGoodType.MODIFY);
			if (yxe_t > -1) {
				sgt = SpliderGoodType.me.findFirst("select * from h_splider_type where h_yxe_type_id=? and h_src_id=?", yxe_t, target_web_id);
				setAttr("good_type", sgt);
			}
			break;
		case SpliderGoodType.ADD:
		default:
			setAttr("change_type", SpliderGoodType.ADD);
			setAttr("goods_types", goodsTypes);
			break;
		}

		render("/type_match_mgr.jsp");

	}

	public void changeTypeMatch() {
		int opt = getParaToInt("change_type");
		int yxe_type_id = getParaToInt("yxe_type_id");
		String yxe_type_name = getPara("good_type_name");
		int target_web_id = getParaToInt("target_web_id");
		String target_web_name = getPara("target_web_name");
		String src_type_str = getPara("src_type_str");
		SpliderGoodType sgt = null;
		switch (opt) {
		case SpliderGoodType.MODIFY:
			if (!TextUtil.isEmpty(src_type_str)) {
				sgt = SpliderGoodType.me.findFirst("select * from h_splider_type where h_yxe_type_id=? and h_src_id=?", yxe_type_id, target_web_id);
				boolean isNull = sgt == null;
				if(isNull){
					sgt = new SpliderGoodType();
				}
				sgt.set("h_yxe_type_id", yxe_type_id);
				sgt.set("h_yxe_type_name", yxe_type_name);
				sgt.set("h_src_type_str", src_type_str);
				sgt.set("h_src_id", target_web_id);
				sgt.set("h_src_name", target_web_name);
				if(isNull){
					sgt.save();
				}else{
					sgt.update();
				}
			}
			break;
		case SpliderGoodType.ADD:
		default:
			sgt = new SpliderGoodType();
			sgt.set("h_yxe_type_id", yxe_type_id);
			sgt.set("h_yxe_type_name", yxe_type_name);
			sgt.set("h_src_type_str", src_type_str);
			sgt.set("h_src_id", target_web_id);
			sgt.set("h_src_name", target_web_name);
			sgt.save();
			break;
		}
		render("/success.jsp");
	}

	public void loadFilterWord() {

		List<SpliderFilterWord> sfws = SpliderFilterWord.me.find("select * from h_splider_filter_word order by id desc");
		setAttr("splider_filter_words", sfws);

		render("/load_filter_word.jsp");

	}

	public void gotoChangeFilterWord() {

		Constants.TARGET_WEBSITE_LIST.clear();
		YXEConfLoad.loadSpliderTargetInfo();
		setAttr("target_infos", Constants.TARGET_WEBSITE_LIST);

		int opt = getParaToInt("opt");
		String id_str = getPara("id");

		SpliderFilterWord sfw = null;
		switch (opt) {
		case SpliderFilterWord.MODIFY:
			sfw = SpliderFilterWord.me.findById(id_str);
			setAttr("filter_word", sfw);
			setAttr("change_type", SpliderFilterWord.MODIFY);
			break;
		case SpliderFilterWord.ADD:
		default:
			setAttr("change_type", SpliderFilterWord.ADD);
			break;
		}

		render("/change_filter_word.jsp");

	}

	public void changeFilterWord() {

		int opt = getParaToInt("change_type");
		String id_str = getPara("id");
		String h_src_id_str = getPara("h_src_id");
		String h_filter_words_str = getPara("h_filter_words");
		SpliderFilterWord sfw = null;
		switch (opt) {
		case SpliderFilterWord.MODIFY:
			sfw = SpliderFilterWord.me.findById(id_str);
			sfw.set("h_src_id", h_src_id_str);
			sfw.set("h_filter_words", h_filter_words_str);
			sfw.update();
			break;
		case SpliderFilterWord.ADD:
		default:
			sfw = new SpliderFilterWord();
			sfw.set("h_src_id", h_src_id_str);
			sfw.set("h_filter_words", h_filter_words_str);
			sfw.save();
			break;
		}

		render("/success.jsp");

	}

	/**
	 * 加载目标网站配置信息
	 */
	public void loadSpliderTarget() {
		Constants.TARGET_WEBSITE_LIST.clear();
		YXEConfLoad.loadSpliderTargetInfo();
		setAttr("target_infos", Constants.TARGET_WEBSITE_LIST);
		renderJson();
	}

	/**
	 * 根据指定目标获取商家详情信息
	 */
	public void loadMerInfoByTarget() {
		String merType = getRequest().getParameter("mer_type");
		String[] merTypeArr = null;
		String type = null;
		if (!TextUtil.isEmpty(merType) && merType.contains("_")) {
			merTypeArr = merType.split("_");
			type = merTypeArr[0];
		} else {
			type = merType;
		}

		List<MerInfoBean> merInfoList = YXEConfLoad.loadMerInfoByKey(getRequest().getSession().getServletContext().getRealPath("/"), type);
		setAttr("mer_info_list", merInfoList);
		renderJson();
	}

	public void startFetch() {

		if (Constants.TYPE_MATCH_RULE_LIST == null || Constants.TYPE_MATCH_RULE_LIST.size() == 0) {
			YXEConfLoad.loadTypeMatchRule(getRequest().getSession().getServletContext().getRealPath("/"));
		}

		// 获取商家类型,比如55haitao
		String merType = getPara("mer_type");

		if (TextUtil.isEmpty(merType)) {
			// TODO 类型获取失败,这里需要跳转一个错误页面
			setAttr("response_msg", "{merType:" + merType + "-failed}");
			renderJson();
			return;
		}

		String[] merHostArr = null;
		String targetWebName = null;
		String typeStr = null;
		String tpyeInt = null;
		if (!TextUtil.isEmpty(merType) && merType.contains("_")) {
			merHostArr = merType.split("_");
			targetWebName = merHostArr[0];
			typeStr = merHostArr[0];
			tpyeInt = merHostArr[1];
		} else {
			// TODO 类型获取失败,这里需要跳转一个错误页面
			setAttr("response_msg", "{merType:" + merType + "-failed}");
			renderJson();
			return;
		}

		// 根据商家类型创建商品列表抓取规则
		List<MerInfoBean> merInfoList = YXEConfLoad.loadMerInfoByKey(getRequest().getSession().getServletContext().getRealPath("/"), typeStr);
		// 根据商家类型创建商品详情抓取规则
		DetailFetchRule dfr = YXEConfLoad.detailFetchRule(Integer.valueOf(tpyeInt));

		// 获取商家原始域名
		String merHost = getPara("mer_host");

		String fetchStyle = YXEConfLoad.getStyleByTargetWeb(Integer.valueOf(tpyeInt));
		SpliderService.fetchGoodByRule(merType, Integer.valueOf(tpyeInt), fetchStyle, merHost, targetWebName, merInfoList, dfr);
		setAttr("response_msg", "{merType:" + merType + ", target:" + targetWebName + "}");
		renderJson();

	}

	public void startFetchOnQuartz() {

		if (TextUtil.isEmpty(ROOT_FILE_PATH)) {
			ROOT_FILE_PATH = getRequest().getSession().getServletContext().getRealPath("/");
		}

		String time = getPara("gap_time");

		String[] is_run_now = getParaValues("is_run_now");
		boolean isRunNow = false;

		System.out.println("--------------->" + is_run_now);
		if (is_run_now != null && is_run_now.length > 0) {
			String isOn = is_run_now[0];
			isRunNow = "on".equals(isOn);
		}

		String targetWeb = getPara("target_web");
		if (!TextUtil.isEmpty(targetWeb) || !"--".equals(targetWeb)) {
			log.info("------------>appointed target, clear target fetch rule set");
			Constants.ALL_TARGET_FETCH_RULE.clear();
			// Constants.TMP_TARGET_FETCH_RULE.clear();
		}

		time = TextUtil.isEmpty(time) ? "45" : time;

		// 加载蜘蛛目标网站以及索要抓取的商家列表,比如55haitaoShort_4,medebiShort_5等
		if (Constants.ALL_TARGET_FETCH_RULE == null || Constants.ALL_TARGET_FETCH_RULE.size() == 0) {
			YXEConfLoad.loadSpliderTargetInfo();
		}

		for (String target : Constants.TARGET_WEBSITE_LIST) {
			String[] merHostArr = null;
			String typeStr = null;
			String tpyeInt = null;

			if (!TextUtil.isEmpty(target) && target.contains("_")) {
				merHostArr = target.split("_");
				typeStr = merHostArr[0];
				tpyeInt = merHostArr[1];
			}

			if (!TextUtil.isEmpty(targetWeb)) {
				log.info("------------>targetWeb:" + targetWeb);
				if (!targetWeb.equals(target) && !"--".equals(targetWeb)) {
					continue;
				}
			}

			List<MerInfoBean> goodRules = YXEConfLoad.loadMerInfoByKey(getRequest().getSession().getServletContext().getRealPath("/"), typeStr);
			Constants.ALL_TARGET_FETCH_RULE.put(target, goodRules);
			// Constants.TMP_TARGET_FETCH_RULE.put(target, goodRules);
		}

		// 分类信息匹配表加载
		if (Constants.TYPE_MATCH_RULE_LIST == null || Constants.TYPE_MATCH_RULE_LIST.size() > 0) {
			YXEConfLoad.loadTypeMatchRule(getRequest().getSession().getServletContext().getRealPath("/"));
		}

		// ///开始抓取
		// SpliderService.autoFetchGood(Constants.ON_FETCHING_RULE, null);
		try {
			AutoFetchJob fetchJob = new AutoFetchJob();
			fetchJob.setFilePath(getRequest().getSession().getServletContext().getRealPath("/"));
			if (isRunNow) {
				YXEConfLoad.autoFetch("");
			}
			boolean is = true;
			if (is) {
				QuartzManager.removeJob(AutoFetchJob.TAG);
				QuartzManager.addJob(AutoFetchJob.TAG, fetchJob, "0 */" + time + " * * * ?");
				// QuartzManager.addJob(AutoFetchJob.TAG, fetchJob, "0/"+time+"
				// * * * * ?");
			}
			// }

			setAttr("start_job_info", "定时任务启动成功");
		} catch (SchedulerException e) {
			e.printStackTrace();
			setAttr("start_job_info", "定时任务启动失败，请重试[" + e.getCause().getLocalizedMessage() + "]");
		} catch (ParseException e) {
			e.printStackTrace();
			setAttr("start_job_info", "定时任务启动失败，请重试[" + e.getCause().getLocalizedMessage() + "]");
		}
		render("/hello.jsp");
	}

	public void startFetchV2() {

		// 是否需要刷新缓存
		String[] refreshArr = getParaValues("is_refresh");
		boolean refresh = false;
		if (refreshArr != null && refreshArr.length > 0) {
			String isOn = refreshArr[0];
			refresh = "on".equals(isOn);
		}

		// 是否需要立即运行
		String[] is_run_now = getParaValues("is_run_now");
		boolean isRunNow = false;

		if (is_run_now != null && is_run_now.length > 0) {
			String isOn = is_run_now[0];
			isRunNow = "on".equals(isOn);
		}

		// 是否指定抓取某个网站
		String targetWeb = getPara("target_web");
		if (!TextUtil.isEmpty(targetWeb) || !"--".equals(targetWeb)) {
			log.info("------------>appointed target, clear target fetch rule set");
			Constants.ALL_TARGET_FETCH_RULE.clear();
		}

		try {
			AutoFetchJobV2 fetchJob = new AutoFetchJobV2(refresh, getRequest().getSession().getServletContext().getRealPath("/"));
			if (isRunNow) {
				SpliderService.fetchGoodLinkByRules(refresh, getRequest().getSession().getServletContext().getRealPath("/"));
			}
			boolean is = true;
			if (is) {
				QuartzManager.removeJob(AutoFetchJob.TAG);
				// QuartzManager.addJob(AutoFetchJob.TAG, fetchJob, "0 */" +
				// time + " * * * ?");
				QuartzManager.addJob(AutoFetchJob.TAG, fetchJob, "0 0 7-23 * * ?");
			}

			
			if(timer != null){
				timer = null;
			}
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					int rand = random.nextInt(5);
					cacheTimeInMunite = rand * oneMunite;
					System.out.println("i:" + i + ", cacheTime:" + cacheTimeInMunite + ", date:" + new Date());
					SpliderInfo sinfo = SpliderInfo.me.findFirst("SELECT * FROM httest.h_splider_info where h_catch_state=0 order by rand() limit 1");
					 int flag = SpliderService.fetchGoodOnLink(sinfo);
					 log.info("auto fetch good sinfo["+sinfo+"] in "+rand + "munite, save flag is "+flag);
					i++;
				}
			}, delay, cacheTimeInMunite);

			setAttr("start_job_info", "定时任务启动成功");
		} catch (SchedulerException e) {
			e.printStackTrace();
			setAttr("start_job_info", "定时任务启动失败，请重试[" + e.getCause().getLocalizedMessage() + "]");
		} catch (ParseException e) {
			e.printStackTrace();
			setAttr("start_job_info", "定时任务启动失败，请重试[" + e.getCause().getLocalizedMessage() + "]");
		}

		render("/hello.jsp");

	}

	static long oneSecond = 1000;
	static long oneMunite = 60 * oneSecond;
	static long delay = 10 * oneSecond;
	static long cacheTimeInMunite = 2 * oneMunite;
	static int i = 0;
	static Random random = new Random();
	static Timer timer = null;

}
