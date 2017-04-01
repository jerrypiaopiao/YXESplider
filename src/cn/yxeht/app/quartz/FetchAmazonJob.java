package cn.yxeht.app.quartz;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import cn.yxeht.app.AppConfig;
import cn.yxeht.app.bean.BizUserTags;
import cn.yxeht.app.biz.bean.GoodDetail;
import cn.yxeht.app.biz.rule.BaseRule;
import cn.yxeht.app.biz.rule.GoodListRule;
import cn.yxeht.app.constants.AmazonCfgInfo;
import cn.yxeht.app.constants.Constants;
import cn.yxeht.app.core.HaiTaoSpliderService;
import cn.yxeht.app.table.GoodInfo;
import cn.yxeht.app.table.Goods;
import cn.yxeht.app.table.Goodstype;
import cn.yxeht.app.table.Merchant;
import cn.yxeht.app.table.Picture;
import cn.yxeht.app.utils.DateUtils;
import cn.yxeht.app.utils.TextUtil;

@Deprecated
public class FetchAmazonJob implements Job {

	private static final Logger log = Logger.getLogger(FetchAmazonJob.class);
	
	public static final String TAG = "Amazon_Fetch_job";

	private boolean isTest;

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

		AppConfig.initCache();

		List<GoodListRule> rules = AmazonCfgInfo.GOOD_LIST_FETCH_RULES;

		for (GoodListRule rule : rules) {

			synchronized (rule) {
				
//			if(rule.getSourceType() == Constants.GUANG_DIU){
//				continue;
//			}
//			
			if(rule.getSourceType() == Constants. MAI_SHOU_DANG){
				continue;
			}
			
			if(rule.getSourceType() == Constants. SHENME_ZHIDE_MAI){
				continue;
			}
			
//			if(!rule.getGoodTypeCode().equals(MSDFetchRule.CATEGARY_EBAY)){
//				continue;
//			}
				
//				if(!TextUtil.isEmpty(merHost) && !rule.getDefaultAmazonHost().equals(merHost)){
//					continue;
//				}
//				
//				if(rule.getSourceType() != fetchType){
//					continue;
//				}
				
				List<String> detailLinks = HaiTaoSpliderService.fetchGoodDetailLink(rule);
				String goodTypeId = null;
				switch (rule.getSourceType()) {
				case Constants.GUANG_DIU:
					log.info(AppConfig.formatLog("GuangDiu has [" + rule.getFullGoodListLink() + "]:[" + detailLinks.size() + "] goods info."));
					goodTypeId = rule.getGoodTypeCode();
					break;
				case Constants.MAI_SHOU_DANG:
					log.info(AppConfig.formatLog("MaiShouDang has [" + rule.getFullGoodListLink() + "]:[" + detailLinks.size() + "] goods info."));
					break;
				case Constants.SHENME_ZHIDE_MAI:
					log.info(AppConfig.formatLog("SMZDM has[" + rule.getFullGoodListLink() + "]:[" + detailLinks.size() + "] good info."));
					break;
				}

				for (String d : detailLinks) {
					GoodDetail goodDetail = null;

					switch (rule.getSourceType()) {
					case Constants.GUANG_DIU:
						goodDetail = HaiTaoSpliderService.fetchGDGoodDetail(rule.getDefaultAmazonHost(), rule.getHost() + d, BaseRule.GET);
						break;
					case Constants.MAI_SHOU_DANG:
						if (d.startsWith("/")) {
							d = d.replaceFirst("/", "");
						}
						goodDetail = HaiTaoSpliderService.fetchMSDGoodDetail(rule.getHost() + d, rule.getDefaultAmazonHost());//rule.getGoodTypeCode());
						if (goodDetail != null) {
							goodTypeId = goodDetail.getType();
						}
						break;
					case Constants.SHENME_ZHIDE_MAI:
						goodDetail = HaiTaoSpliderService.fetchSMZDMGoodDetail(d, "");
						goodTypeId = goodDetail.getType();
						String tmpYXELink = goodDetail.getYxehtLink();
						boolean isGoSMZDM = true;
						if(!TextUtil.isEmpty(tmpYXELink)){
							isGoSMZDM = tmpYXELink.contains("go.smzdm.com");
						}else{
							isGoSMZDM = false;
						}
						
//						String yxeLink = null;
						
						/*if(isGoSMZDM){
							String requestUrl = AppConfig.HOST;
							String host = requestUrl.substring(0, requestUrl.lastIndexOf("/"));
							String realLink = HaiTaoSpliderService.fetchRealLink(host + "/parseDetail?detail_link=" + tmpYXELink);
							yxeLink = AmazonLinkConvertor.convertToYxeLink(rule.getDefaultAmazonHost(), realLink);
						}else{
							yxeLink =  AmazonLinkConvertor.convertToYxeLink(rule.getDefaultAmazonHost(), tmpYXELink);//tmpYXELink;
						}*/
						String requestUrl = AppConfig.HOST;
						String host = requestUrl.substring(0, requestUrl.lastIndexOf("/"));
						String realLink = HaiTaoSpliderService.fetchRealLink(host + "/parseDetail?detail_link=" + d);
						log.info("-----------xxxxxxx----------->"+realLink);
						String yxeLink = null;//AmazonLinkConvertor.convertToYxeLink(rule.getDefaultAmazonHost(), realLink);
						log.info("-----------yyyyyyy----------->"+yxeLink);
						if(TextUtil.isEmpty(yxeLink)){
							//TODO 这里是权宜之计,对于SMZDM上面不能识别的链接进行处理
							log.info(AppConfig.formatLog("\n task-yxelink-filter:["+rule.getSourceType()+"][yxeLink:"+yxeLink+"]为空,跳过商品[\n"+goodDetail+"\n]"));
							continue;
						}
						goodDetail.setYxehtLink(yxeLink);
						break;
					}

					if (goodDetail == null || TextUtil.isEmpty(goodTypeId)) {
						log.info(AppConfig.formatLog("\n All-filter:["+rule.getSourceType()+"][goodDetail:" + goodDetail+"], [goodTypeId:"+goodTypeId+"]"));
						continue;
					}

					Merchant mer = null;//AppConfig.MERCHANTID_NAME_MAP.get(rule.getDefaultAmazonHost());
					Goodstype type = AppConfig.GOODTYE_MAP.get(goodTypeId);
					Goods tmp = Goods.me.findFirst("select * from h_goods where hrefnew=?", goodDetail.getYxehtLink());

					if(mer == null){
						log.info(AppConfig.formatLog("\n All-filter:["+rule.getSourceType()+"][mer:" + rule.getDefaultAmazonHost()+"]为空，跳过商品[\n"+goodDetail+"\n]"));
						continue;
					}
					
					String hostNoHttp = rule.getDefaultAmazonHost();
					if (hostNoHttp.startsWith("http://")) {
						hostNoHttp = hostNoHttp.replaceFirst("http://", "");
					}
					BizUserTags bizUserTags = AmazonCfgInfo.ALL_AMAZON_HOST_TAG_MATCH.get(hostNoHttp);

//					log.info(AppConfig.formatLog("editor info:" + bizUserTags));
//					log.info(AppConfig.formatLog("good type info:" + type));
//					log.info(AppConfig.formatLog("merchant info:" + mer));

					if (tmp == null) {
						Goods goods = Goods.me.convert(goodDetail, bizUserTags != null ? bizUserTags.getBizManName() : AppConfig.bizManName, bizUserTags != null ? bizUserTags.getBizManId() : String.valueOf(AppConfig.bizManId), goodTypeId,
								type.getStr("name"), String.valueOf(mer.getInt("id")), mer.getStr("name"), "");
						// TODO 商品信息保存
						if (!AppConfig.IS_DEV) {
//							log.info(AppConfig.formatLog("save good info:[" + goodDetail.getGoodTitle() + "]"));
							boolean goodSave = goods.save();
							log.info(AppConfig.formatLog("goods save flag:[" + goodSave + "]"));
						}
						GoodInfo goodInfo = new GoodInfo();
						goodInfo.set("goodsid", goods.getInt("id"));
						goodInfo.set("descs", goodDetail.getGoodContent());
						GoodInfo tmpGoodInfo = GoodInfo.me.findFirst("select * from h_goodsinfo where goodsid=?", goods.getInt("id"));
						// TODO 商品介绍内容保存
						if (!AppConfig.IS_DEV) {
//							log.info(AppConfig.formatLog("save good detail info:[" + goodDetail.getGoodTitle() + "]"));
							if(tmpGoodInfo == null){
								boolean goodInfoSave = goodInfo.save();
								log.info(AppConfig.formatLog("good info save flag:[" + goodInfoSave + "]"));
							}else{
								tmpGoodInfo.set("goodsid", goods.getInt("id"));
								tmpGoodInfo.set("descs", goodDetail.getGoodContent());
								boolean goodInfoUpdate = tmpGoodInfo.update();
								log.info(AppConfig.formatLog("good info update flag:[" + goodInfoUpdate + "]"));
							}
						}
						if (goodDetail.getmImgLinks() != null && goodDetail.getmImgLinks().size() > 0) {
//							log.info("当前商品共:[" + goodDetail.getmImgLinks().size() + "]张图片");
							int i = 0;
							for (String src : goodDetail.getmImgLinks()) {
								if (i < 3) {
									if (src.contains("!")) {
										src = src.substring(0, src.lastIndexOf("!"));
									}
									int picId = getImage(src);
									if (i == 0) {
										goods.set("mpic", picId);
										// TODO 更新图片信息
										if (!AppConfig.IS_DEV) {
											goods.update();
										}
									}
								}
								i++;
							}
						}
					}
				}

			}
		}
	}

	private int getImage(String src) {

		int pictureId = 0;

		try {
			URL url = new URL(src);
			URLConnection uri = url.openConnection();
			InputStream is = uri.getInputStream();
			String imageName = System.currentTimeMillis() + ".jpg";

			String path = AppConfig.IMAGE_SAVE_PATH;

			String currentPath = DateUtils.currentDate();

			if (path.endsWith("\\")) {
				path = path + currentPath;
			} else {
				path = path + File.separator + currentPath;
			}

			File todayDir = new File(path);
			if (!todayDir.exists()) {
				todayDir.mkdirs();
			}

			OutputStream os = new FileOutputStream(new File(path, imageName));
			byte[] buf = new byte[1024];
			int l = 0;
			while ((l = is.read(buf)) != -1) {
				os.write(buf, 0, l);
			}

			os.close();

			Picture picture = new Picture();
			/*
			 * `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id自增',
			 * `path` varchar(255) NOT NULL DEFAULT '' COMMENT '路径', `url`
			 * varchar(255) NOT NULL DEFAULT '' COMMENT '图片链接', `md5` char(32)
			 * NOT NULL DEFAULT '' COMMENT '文件md5', `sha1` char(40) NOT NULL
			 * DEFAULT '' COMMENT '文件 sha1编码', `status` tinyint(2) NOT NULL
			 * DEFAULT '0' COMMENT '状态', `create_time` int(10) unsigned NOT NULL
			 * DEFAULT '0' COMMENT '创建时间',
			 */
			picture.set("path", AppConfig.IMAGE_URL + currentPath + "/" + imageName);
			picture.set("url", "");
			picture.set("md5", "");
			picture.set("sha1", "");
			picture.set("status", "1");
			picture.set("create_time", System.currentTimeMillis() / 1000);
			//TODO 图片信息保存
			if(!AppConfig.IS_DEV){
				picture.save();
				pictureId = picture.getLong("id").intValue();
			}
		} catch (MalformedURLException e) {
			log.error(AppConfig.formatLog("pic save failed"), e.getCause());
		} catch (FileNotFoundException e) {
			log.error(AppConfig.formatLog("pic save failed"), e.getCause());
		} catch (IOException e) {
			log.error(AppConfig.formatLog("pic save failed"), e.getCause());
		}

		return pictureId;

	}

	public void setIsTest(boolean isTest) {
		this.isTest = isTest;
	}
	
}
