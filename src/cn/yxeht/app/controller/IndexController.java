package cn.yxeht.app.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;

import cn.yxeht.app.AppConfig;
import cn.yxeht.app.biz.bean.GoodDetail;
import cn.yxeht.app.biz.rule.BaseRule;
import cn.yxeht.app.biz.rule.GoodListRule;
import cn.yxeht.app.constants.AmazonCfgInfo;
import cn.yxeht.app.constants.Constants;
import cn.yxeht.app.constants.TypeMatchRule;
import cn.yxeht.app.core.HaiTaoSpliderService;
import cn.yxeht.app.quartz.FetchAmazonJob;
//import cn.yxeht.app.quartz.FetchGDAmazonJob;
import cn.yxeht.app.quartz.QuartzManager;
import cn.yxeht.app.table.Goods;
import cn.yxeht.app.table.Goodstype;
import cn.yxeht.app.table.Merchant;
import cn.yxeht.app.test.FetchCommonTest;
import cn.yxeht.app.utils.TextUtil;

import com.jfinal.core.Controller;

public class IndexController extends Controller {

	private static final Logger log = Logger.getLogger(IndexController.class);

	public void index() {

		setAttr("biz_man", AppConfig.bizManName);
		setAttr("biz_man_tag", AppConfig.bizManTag);
		setAttr("gap_time", "1");
		AppConfig.initCache();

		setAttr("merchants", AppConfig.ALL_MERCHANT);
		setAttr("goods_types", AppConfig.ALL_GOODTYPE);

		setAttr("is_test", "on");
		
		String requestUrl = getRequest().getRequestURL().toString();
		String host = requestUrl.substring(0, requestUrl.lastIndexOf("/"));
//		AppConfig.HOST = host;
		
		render("/index.jsp");
	}

	public void startJob() {

		String[] isTestPara = getParaValues("is_test");
		String time = getPara("gap_time");

		boolean isTest = false;

		if (isTestPara == null || isTestPara.length == 0) {
			setAttr("is_test", "off");
			isTest = false;
		} else {
			setAttr("is_test", "on");
			isTest = true;
		}

		if (TextUtil.isEmpty(time)) {
			time = "10";
		}

		// FetchGDAmazonJob fetchJob = new FetchGDAmazonJob();
		// fetchJob.setIsTest(isTest);
//		String requestUrl = getRequest().getRequestURL().toString();
//		String host = requestUrl.substring(0, requestUrl.lastIndexOf("/"));
//		AppConfig.HOST = host;
		FetchAmazonJob fetchJob = new FetchAmazonJob();
		fetchJob.setIsTest(isTest);

		try {
			// QuartzManager.removeJob(FetchGDAmazonJob.TAG);
			// QuartzManager.addJob(FetchGDAmazonJob.TAG, fetchJob,
			// "0 */"+time+" * * * ?");
			QuartzManager.removeJob(FetchAmazonJob.TAG);
			QuartzManager.addJob(FetchAmazonJob.TAG, fetchJob, "0 */" + time
					+ " * * * ?");
			setAttr("start_job_info", "定时任务启动成功");
		} catch (SchedulerException e) {
			log.error("定时任务启动失败..., " + e.getLocalizedMessage(), e.getCause());
			e.printStackTrace();
			setAttr("start_job_info", "定时任务启动失败，请重试");
		} catch (ParseException e) {
			log.error("定时任务启动失败..., " + e.getLocalizedMessage(), e.getCause());
			setAttr("start_job_info", "定时任务启动失败，请重试");
		}

		setAttr("gap_time", time);
		setAttr("biz_man", AppConfig.bizManName);
		setAttr("biz_man_tag", AppConfig.bizManTag);

		AppConfig.initCache();

		setAttr("merchants", AppConfig.ALL_MERCHANT);
		setAttr("goods_types", AppConfig.ALL_GOODTYPE);

		render("/index.jsp");

	}

	public void stopJob() {
		String time = getPara("gap_time");
		try {
			// QuartzManager.removeJob(FetchGDAmazonJob.TAG);
			QuartzManager.removeJob(FetchAmazonJob.TAG);
			setAttr("start_job_info", "定时任务已关闭");
		} catch (SchedulerException e) {
			e.printStackTrace();
			setAttr("start_job_info", "定时任务关闭失败," + e.getLocalizedMessage());
		}

		setAttr("is_test", "on");

		setAttr("gap_time", time);

		setAttr("biz_man", AppConfig.bizManName);
		setAttr("biz_man_tag", AppConfig.bizManTag);

		AppConfig.initCache();

		setAttr("merchants", AppConfig.ALL_MERCHANT);
		setAttr("goods_types", AppConfig.ALL_GOODTYPE);

		render("/index.jsp");
	}

	public void sayHello() {

		String merchants = getPara("merchant_name");
		String goods_type = getPara("goods_type");

		String result = "";

		AppConfig.initCache();

		if (!TextUtil.isEmpty(merchants) && !TextUtil.isEmpty(goods_type)) {
			if (!merchants.endsWith("/")) {
				merchants = merchants + "/";
			}

			List<GoodListRule> list = new ArrayList<GoodListRule>();
			GoodListRule r1 = new GoodListRule();
			r1.setRuleName("美国亚马逊");
			r1.setRequestMoethod(BaseRule.GET);
			r1.setClassName("goodname");
			List<String> goodHosts = new ArrayList<>();
			goodHosts.add(merchants);
			if (merchants.startsWith("http://")) {
				goodHosts.add(merchants.replace("http://", "https://"));
			}
			r1.setGoodHost(goodHosts);
			r1.setHost("http://guangdiu.com/");
			r1.setGoodListUrl("cate.php?m=Amazon&k="
					+ TypeMatchRule.GUANGDIU_TYPE_MATCH_RULE.get(goods_type)
					+ "&c=us");
			r1.setType(BaseRule.CLASS);
			list.add(r1);

			for (GoodListRule gr : list) {
				synchronized (gr) {
					List<String> detailLinks = HaiTaoSpliderService
							.fetchGoodDetailLink(gr);

					result = "<p>" + result + "共[" + detailLinks.size()
							+ "]条商品信息</p>";
					int i = 0;
					for (String d : detailLinks) {
						GoodDetail goodDetail = HaiTaoSpliderService
								.fetchGDGoodDetail(gr.getDefaultAmazonHost(),
										gr.getHost() + d, BaseRule.GET);
						Merchant mer = null; 
//								AppConfig.MERCHANTID_NAME_MAP
//								.get(merchants.substring(0,
//										merchants.lastIndexOf("/")));
						Goodstype type = AppConfig.GOODTYE_MAP.get(goods_type);
						Goods tmp = Goods.me.findFirst(
								"select * from h_goods where hrefnew=?",
								goodDetail.getYxehtLink());
						if (tmp == null) {
							Goods goods = Goods.me.convert(goodDetail,
									AppConfig.bizManName,
									String.valueOf(AppConfig.bizManId),
									goods_type, type.getStr("name"),
									String.valueOf(mer.getInt("id")),
									mer.getStr("name"));
							goods.save();
							if (goodDetail.getmImgLinks() != null
									&& goodDetail.getmImgLinks().size() > 0) {
								for (String src : goodDetail.getmImgLinks()) {
									getImage(src);
								}
							}
						}
						result = "<p>" + result + goodDetail + "</p>";
						i++;
					}
				}
			}

		} else {
			result = "sorry, please choose merchants and good type.";
		}

		setAttr("biz_man", AppConfig.bizManName);
		setAttr("biz_man_tag", AppConfig.bizManTag);

		AppConfig.initCache();

		setAttr("merchants", AppConfig.ALL_MERCHANT);
		setAttr("goods_types", AppConfig.ALL_GOODTYPE);
		setAttr("parse_result", result);

		render("/hello.jsp");

	}

	public void parseDetail() {
		String detailLink = getPara("detail_link");
		log.info(AppConfig.formatLog("parseDetail:["+detailLink+"]"));
		GoodDetail goodDetail = HaiTaoSpliderService.fetchSMZDMGoodDetail(
				detailLink, "");
		setAttr("goodDetail", goodDetail);
		setAttr("detail_link", goodDetail.getGoodSrcLink());
		render("/decode.jsp");
	}

	public void save_smzdm_good() {
		String goodTitle = getPara("goodTitle");
		String goodContent = getPara("goodContent");
		String goodSrcLink = getPara("goodSrcLink");
		String yxehtLink = getPara("yxehtLink");
		String imgLinks = getPara("imgLinks");
		String goodType = getPara("goodType");
		
		String text = "goodTitle:"+goodTitle + ",<br>"
				+" goodContent:"+goodContent + ",<br>"
				+" goodSrcLink:"+goodSrcLink + ",<br>"
				+" yxehtLink:"+yxehtLink + ",<br>"
				+" imgLinks:"+imgLinks + ",<br>"
				+" goodType:"+goodType;
		
		//renderText(text);
		renderHtml(text);
		
	}

	public void jqueryTest() {
		getData();
		String id = getPara("id");
		String name = getPara("name");
	}
	
	public void fetchTest(){
		
		int fetchType = getParaToInt("fetchType");
		int fetchCount = getParaToInt("fetchCount");
		String merHost = getPara("mer");
		
		FetchCommonTest.startFetch(fetchCount, fetchType, merHost);
		renderText("start fetch");
	}
	
	private void getData() {
		try {
			StringBuilder json = new StringBuilder();
			BufferedReader reader = this.getRequest().getReader();
			String line = null;
			while ((line = reader.readLine()) != null) {
				json.append(line);
			}
			reader.close();
		} catch (Exception ex) {
			log.error(AppConfig.formatLog(ex.getLocalizedMessage()), ex.getCause());
		}
	}

	private void getImage(String src) {
		try {
			URL url = new URL(src);
			URLConnection uri = url.openConnection();
			InputStream is = uri.getInputStream();
			String imageName = System.currentTimeMillis() + ".jpg";
			OutputStream os = new FileOutputStream(new File("E://imgs",
					imageName));
			byte[] buf = new byte[1024];
			int l = 0;
			while ((l = is.read(buf)) != -1) {
				os.write(buf, 0, l);
			}

			os.close();
		} catch (MalformedURLException e) {
			log.error(AppConfig.formatLog(e.getLocalizedMessage()), e.getCause());
		} catch (FileNotFoundException e) {
			log.error(AppConfig.formatLog(e.getLocalizedMessage()), e.getCause());
		} catch (IOException e) {
			log.error(AppConfig.formatLog(e.getLocalizedMessage()), e.getCause());
		}
	}

	private void sendRequest(String urlString) {
		try {

			URL url = new URL(urlString); // 把字符串转换为URL请求地址
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();// 打开连接
			connection
					.setRequestProperty(
							"User-Agent",
							"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31");
			connection.connect();// 连接会话
			// 获取输入流
			BufferedReader br = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line;
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) {// 循环读取流
				sb.append(line);
			}

			br.close();// 关闭流
			connection.disconnect();// 断开连接
		} catch (Exception e) {
			log.error(AppConfig.formatLog(e.getLocalizedMessage()), e.getCause());
		}
	}
	
	public void testPage(){
		setAttr("j_name", "jerry");
		setAttr("j_type", "administrator");
		render("/hello.jsp");
	}

}
