package cn.yxeht.app.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;

import com.jfinal.core.Controller;

import cn.yxeht.app.AppConfig;
import cn.yxeht.app.bean.MerInfoBean;
import cn.yxeht.app.biz.rule.DetailFetchRule;
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
import cn.yxeht.app.utils.DateUtils;
import cn.yxeht.app.utils.ListUtil;
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
			log.info(e.getLocalizedMessage(), e.getCause());
			setAttr("start_job_info", "定时任务启动失败，请重试[" + e.getCause().getLocalizedMessage() + "]");
		} catch (ParseException e) {
			e.printStackTrace();
			log.info(e.getLocalizedMessage(), e.getCause());
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
		
		if(TextUtil.isEmpty(WEB_ROOT_PATH)){
			WEB_ROOT_PATH = getRequest().getSession().getServletContext().getRealPath("/");
		}

		try {
			if(TextUtil.isEmpty(WEB_ROOT_PATH)){
				WEB_ROOT_PATH = getRequest().getSession().getServletContext().getRealPath("/");
			}
			
			IS_REFRESH = refresh;
			
//			AutoFetchJobV2 fetchJob = new AutoFetchJobV2(IS_REFRESH, WEB_ROOT_PATH);
			AutoFetchJobV2 fetchJob = new AutoFetchJobV2();
			if (isRunNow) {
				SpliderService.fetchGoodLinkByRules(refresh, getRequest().getSession().getServletContext().getRealPath("/"));
			}
			boolean is = false;
			if (is) {
				QuartzManager.removeJob(AutoFetchJob.TAG);
				// QuartzManager.addJob(AutoFetchJob.TAG, fetchJob, "0 */" +  time + " * * * ?");
				QuartzManager.addJob(AutoFetchJob.TAG, fetchJob, "0 0 7-23 * * ? *");
			}

			
			removeTargetWeb = null;
			FETCH_COUNT_RECORD.clear();
			FETCHED_WEB_SITE.clear();
			
			if(goodDetailFetchTask != null){
				try {
					goodDetailFetchTask.cancel();
				} catch (Exception e) {
					log.info(e.getLocalizedMessage(), e.getCause());
				}
				goodDetailFetchTask = null;
			}
			
			if(goodDetailFetchTimer != null){
				try {
					goodDetailFetchTimer.cancel();
				} catch (Exception e) {
					log.info(e.getLocalizedMessage(), e.getCause());
				}
				
				goodDetailFetchTimer = null;
				
			}
			goodDetailFetchTimer = new Timer();
			goodDetailFetchTask = new SpliderTask();
			goodDetailFetchTimer.schedule(goodDetailFetchTask, delay, oneHalfMunite);

			setAttr("start_job_info", "定时任务启动成功");
		} catch (SchedulerException e) {
			e.printStackTrace();
			log.info(e.getLocalizedMessage(), e.getCause());
			setAttr("start_job_info", "定时任务启动失败，请重试[" + e.getCause().getLocalizedMessage() + "]");
		} catch (ParseException e) {
			e.printStackTrace();
			log.info(e.getLocalizedMessage(), e.getCause());
			setAttr("start_job_info", "定时任务启动失败，请重试[" + e.getCause().getLocalizedMessage() + "]");
		}

		render("/hello.jsp");

	}
	
	/**
	 * 单条商品抓取测试
	 */
	public void singleFetch(){
		
		YXEConfLoad.loadFetchGoodListRule(true, getRequest().getSession().getServletContext().getRealPath("/"));
		
		String fetchUrl = getPara("fetch_url");
		
		SpliderInfo sinfo = SpliderInfo.me.findFirst("SELECT * FROM httest.h_splider_info where h_good_link=?", fetchUrl);
		
		 int flag = SpliderService.fetchGoodOnLink(sinfo);
		 
		 String message = flag == 0 ? "["+fetchUrl+"] fetch success, flag is ["+flag+"]" : "["+fetchUrl+"] fetch failed, flag is ["+flag+"]";
		
		 setAttr("fetch_message", message);
		 
		 List<SpliderInfo> spliderInfos = SpliderInfo.me.find("select * from httest.h_splider_info where h_catch_state=?", SpliderInfo.UN_FETCH);
			
		setAttr("splider_infos", spliderInfos);
		 
		 render("/single_fetch.jsp");
//		 render("/splider_infos.jsp");
		 
	}
	
	public void getAllSpliderInfo(){
		String fetchState = getPara("fetch_state");
		if(TextUtil.isEmpty(fetchState)){
			fetchState = String.valueOf(SpliderInfo.UN_FETCH);
		}
		List<SpliderInfo> spliderInfos = SpliderInfo.me.find("select * from httest.h_splider_info where h_catch_state=?", fetchState);
		
		setAttr("splider_infos", spliderInfos);
		
		render("/splider_infos.jsp");
		
	}

	static long oneSecond = 1000;
	static long oneMunite = 60 * oneSecond;
	static long delay = 10 * oneSecond;
	static long cacheTimeInMunite = 2 * oneMunite;
	static long oneHalfMunite = (long) (1.5 * oneMunite);
	static int i = 0;//详情抓取动作计数器
	static Random random = new Random();
	
	public static String WEB_ROOT_PATH;
	public static boolean IS_REFRESH;
	
	static Timer goodDetailFetchTimer = null;
	
	private SpliderTask goodDetailFetchTask;
	
	public class SpliderTask extends TimerTask{
		
		@Override
		public void run() {
			
			SpliderInfo sinfo = null;
			
			List<SpliderInfo> sinfos =  SpliderInfo.me.find("SELECT * FROM httest.h_splider_info where h_catch_state=0 and date(h_create_time)=? order by id asc", DateUtils.currentDate());
			
			if(sinfos != null && sinfos.size() > 0){
				sinfo = sinfos.get(0);
				int flag = SpliderService.fetchGoodOnLink(sinfo);
				log.info("------------->fetch good info ["+sinfo+"] on["+currentTargetWeb+"], save flag is "+flag);
			}else{
				SpliderService.fetchGoodLinkByRules(IS_REFRESH, WEB_ROOT_PATH);
				log.info("------------>refetch goodlist again at "+DateUtils.currentDate());
			}
			
//			long fetchTime = System.currentTimeMillis();
//			
//			
//			if(FETCH_COUNT_RECORD.isEmpty()){
//				//如果正在抓取的列表为空,则默认取目标网站的第一项来进行网络抓取操作
//				log.info("------------>first execute fetch, check the currentTargetWeb:["+currentTargetWeb+"]");
//				if(!TextUtil.isEmpty(currentTargetWeb)){
//					
//					for(String s : Constants.TARGET_WEBSITE_LIST){
//						if(!currentTargetWeb.equals(s)){
//							currentTargetWeb = s;
//							break;
//						}else{
//							continue;
//						}
//					}
//				}else{
//					currentTargetWeb = Constants.TARGET_WEBSITE_LIST.get(0);
//				}
//				log.info("------------>first execute fetch, use the first var["+currentTargetWeb+"] in TARGET_WEBSITE_LIST for fetch function");
//			}else{
//				Long lastFetchTime = FETCH_COUNT_RECORD.get(currentTargetWeb);
//				log.info("------------>duration after the last time on the ["+currentTargetWeb+"]  ===>"+(fetchTime - lastFetchTime));
//				if(lastFetchTime != null && (fetchTime - lastFetchTime >= oneMunite)){
//					//do nothing at here
//					log.info("------------>["+currentTargetWeb+"] more than "+oneMunite+", keep currentTargetWeb value as ["+currentTargetWeb+"]");
//				}else if(fetchTime - lastFetchTime < oneMunite){
//					String tmpStr = new String(currentTargetWeb);
//					String[] ref = Constants.TARGET_WEBSITE_LIST.toArray(new String[Constants.TARGET_WEBSITE_LIST.size()]);
//					String[] comp = FETCHED_WEB_SITE.toArray(new String[FETCHED_WEB_SITE.size()]);
//					String[] result = ListUtil.substract(ref, comp);
//					List<String> tmp = new ArrayList<String>(Arrays.asList(result));
//					if(removeTargetWeb != null && removeTargetWeb.length() > 0){
//						log.info("------------>has not fetch target web count is :"+tmp.size());
//						if (tmp.contains(removeTargetWeb) && tmp.size() > 0) {
//							for(int i = 0; i < tmp.size(); i++){
//								log.info("------------>the target web unfetched's name is["+i+"]:"+tmp.get(i));
//							}
//							log.info("--------------->target web that need remove is :["+removeTargetWeb+"]");
//							tmp.remove(removeTargetWeb);
//						}
//						removeTargetWeb = null;
//					}
//					
//					log.info("------------>(1)has not fetch target web count is :"+tmp.size());
//					for(int i = 0; i < tmp.size(); i++){
//						log.info("------------>(1)the target web unfetched's name is["+i+"]:"+tmp.get(i));
//					}
//					
//					if (tmp.size() > 0) {
//						log.info("there still have unfetch web site, keep going to exchange [currentTargetWeb]");
//						currentTargetWeb = tmp.get(0);
//					}else{
//						log.info("there has no unfetch web site, clear FETCHED_WEB_SITE and make [currentTargetWeb]'s value = "+ Constants.TARGET_WEBSITE_LIST.get(0));
//						FETCHED_WEB_SITE.clear();
////						currentTargetWeb = Constants.TARGET_WEBSITE_LIST.get(0);
//						for(String s : Constants.TARGET_WEBSITE_LIST){
//							if(!currentTargetWeb.equals(s)){
//								currentTargetWeb = s;
//								break;
//							}else{
//								continue;
//							}
//						}
//					}
//					System.err.println("--------hhhh------->["+tmpStr+"]抓取间隔小于"+oneMunite+", currentTargetWeb更改为["+currentTargetWeb+"]");
//					log.info("["+tmpStr+"] fetch duration time is less than ["+oneMunite+"], change currentTargetWeb from ["+tmpStr+"] to ["+currentTargetWeb+"]");
//				}
//			}
//			
//			log.info("SELECT * FROM httest.h_splider_info where h_catch_state=0 and h_src_free_str='"+currentTargetWeb+"' and date(h_create_time)='"+DateUtils.currentDate()+"' order by rand() limit 1");
//			
//			sinfo = SpliderInfo.me.findFirst("SELECT * FROM httest.h_splider_info where h_catch_state=0 and h_src_free_str=? and date(h_create_time)=? order by id asc", currentTargetWeb, DateUtils.currentDate());
//			
//			//记录抓取的地址
//			if(!FETCHED_WEB_SITE.contains(currentTargetWeb)){
//				log.info("there has no ["+currentTargetWeb+"] in fetch record, write ["+currentTargetWeb+"] into the FETCH_WEB_SITE");
//				FETCHED_WEB_SITE.add(currentTargetWeb);
//			}
//			
//			int flag = -1;
//			//如果以currentTargetWeb与当前日期为条件查询不到
//			if(sinfo == null){
//				log.info("["+currentTargetWeb+"] fetch over, remove ["+currentTargetWeb+"] from the FETCH_WEB_SITE and start fetch good list by ["+currentTargetWeb+"]");
//				Long lastFetchTime = FETCH_COUNT_RECORD.get(currentTargetWeb);
////				if(lastFetchTime != null && fetchTime - lastFetchTime > 3*oneMunite){
//					if(FETCHED_WEB_SITE.contains(currentTargetWeb)){
//						FETCHED_WEB_SITE.remove(currentTargetWeb);
//						removeTargetWeb = currentTargetWeb;
//					}
//					SpliderService.fetchGoodLinkByRules(IS_REFRESH, WEB_ROOT_PATH, removeTargetWeb);
//					for(String s : Constants.TARGET_WEBSITE_LIST){
//						if(!currentTargetWeb.equals(s)){
//							currentTargetWeb = s;
//							break;
//						}else{
//							continue;
//						}
//					}
////				}else{
////					log.info("["+currentTargetWeb+"] fetch over, but time limit is not over, do not remove ["+currentTargetWeb+"] from the FETCH_WEB_SITE and start fetch good list by ["+currentTargetWeb+"]");
////				}
//			    FETCH_COUNT_RECORD.put(currentTargetWeb, fetchTime);
//			}else{
//				flag = SpliderService.fetchGoodOnLink(sinfo);
//				log.info("fetch good info ["+sinfo+"] on["+currentTargetWeb+"], save flag is "+flag);
//				//更新当前目标网站本次抓取的时间
//				FETCH_COUNT_RECORD.put(currentTargetWeb, fetchTime);
//			}
		}
		
	}
	
	public static String currentTargetWeb;
	public static String removeTargetWeb;
	public static Map<String, Long> FETCH_COUNT_RECORD = new HashMap<String, Long>();
	public static List<String> FETCHED_WEB_SITE = new ArrayList<String>();

}
