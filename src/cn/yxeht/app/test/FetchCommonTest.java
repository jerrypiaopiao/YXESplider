package cn.yxeht.app.test;

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

public class FetchCommonTest {

	private static final Logger log = Logger.getLogger(FetchCommonTest.class);
	
	public static void startFetch(int  fetchCount, int fetchType, String merHost) {

		AppConfig.initCache();

		List<GoodListRule> rules = AmazonCfgInfo.GOOD_LIST_FETCH_RULES;
		System.out.println("rules:----------->"+rules.size());
		for (GoodListRule rule : rules) {
//			System.out.println("isEquals:"+merHost.equals(rule.getDefaultAmazonHost()));
			//System.out.println("rule.defaultHost:"+rule.getDefaultAmazonHost());
			synchronized (rule) {
				
				if(!TextUtil.isEmpty(merHost) && !merHost.equals(rule.getDefaultAmazonHost())){
//					System.out.println("------1------->merHost:"+merHost+", rule.getDefaultAmazonHost():"+rule.getDefaultAmazonHost());
					continue;
				}
				
				if(rule.getSourceType() != fetchType){
//					System.out.println("rule.defaultHost:"+rule.getDefaultAmazonHost());
//					System.out.println("------------->rule.getSourceType():"+rule.getSourceType());
					continue;
				}
				
				System.out.println("------2------->merHost:"+merHost+", rule.getDefaultAmazonHost():"+rule.getDefaultAmazonHost());
				
				log.info(AppConfig.formatLog("start fetch[" + rule.getRuleName()+", host:"+rule.getDefaultAmazonHost()+"]"));
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

				int count = 0;
				System.out.println("detailLinks:"+detailLinks.size());
				for (String d : detailLinks) {
					if(count > fetchCount){
						break;
					}
					count++;
					GoodDetail goodDetail = null;

					switch (rule.getSourceType()) {
					case Constants.GUANG_DIU:
						System.out.println("-------------------------------------------------------->1"+d);
						goodDetail = HaiTaoSpliderService.fetchGDGoodDetail(rule.getDefaultAmazonHost(), rule.getHost() + d, BaseRule.GET);
						break;
					case Constants.MAI_SHOU_DANG:
						System.out.println("-------------------------------------------------------->2"+d);
						if (d.startsWith("/")) {
							d = d.replaceFirst("/", "");
						}
						goodDetail = HaiTaoSpliderService.fetchMSDGoodDetail(rule.getHost() + d, rule.getGoodTypeCode());
						if (goodDetail != null) {
							goodTypeId = goodDetail.getType();
						}
						break;
					case Constants.SHENME_ZHIDE_MAI:
						System.out.println("-------------------------------------------------------->3（"+d+"）");
						goodDetail = HaiTaoSpliderService.fetchSMZDMGoodDetail(d, "");
						if (goodDetail != null) {
							goodTypeId = goodDetail.getType();
						}
						String requestUrl = AppConfig.HOST;
						String host = requestUrl.substring(0, requestUrl.lastIndexOf("/"));
						String realLink = HaiTaoSpliderService.fetchRealLink(host + "/parseDetail?detail_link=" + d);
						System.out.println("-------------------------------------------------------->3");
						System.out.println("-----------xxxxxxx----------->"+realLink);
						String yxeLink = null;//AmazonLinkConvertor.convertToYxeLink(rule.getDefaultAmazonHost(), realLink);
						System.out.println("-----------xxxxxxx-----------yxeLink>"+yxeLink);
						if(TextUtil.isEmpty(yxeLink)){
							//TODO 这里是权宜之计,对于SMZDM上面不能识别的链接进行处理
							continue;
						}
						goodDetail.setYxehtLink(yxeLink);
						break;
					}

					log.info(AppConfig.formatLog("\n goodDetail:" + goodDetail));
					System.out.println("\n goodDetail:" + goodDetail);
					log.info(AppConfig.formatLog("\n rule.getDefaultAmazonHost():" + rule.getDefaultAmazonHost()));

					if (goodDetail == null) {
						continue;
					}

					Merchant mer = null;//AppConfig.MERCHANTID_NAME_MAP.get(rule.getDefaultAmazonHost());
					Goodstype type = AppConfig.GOODTYE_MAP.get(goodTypeId);
					Goods tmp = Goods.me.findFirst("select * from h_goods where hrefnew=?", goodDetail.getYxehtLink());

					String hostNoHttp = rule.getDefaultAmazonHost();
					if (hostNoHttp.startsWith("http://")) {
						hostNoHttp = hostNoHttp.replaceFirst("http://", "");
					}
					BizUserTags bizUserTags = AmazonCfgInfo.ALL_AMAZON_HOST_TAG_MATCH.get(hostNoHttp);
					
					log.info("编辑信息:" + bizUserTags);
					log.info("分类信息:" + type);
					log.info("商户信息:" + mer);
					Goods goods1 = Goods.me.convert(goodDetail, bizUserTags != null ? bizUserTags.getBizManName() : AppConfig.bizManName, bizUserTags != null ? bizUserTags.getBizManId() : String.valueOf(AppConfig.bizManId), goodTypeId,
							type.getStr("name"), String.valueOf(mer.getInt("id")), mer.getStr("name"));
					log.info(AppConfig.formatLog("goodInfo:"+goods1.toJson()));
					if (tmp == null) {
						Goods goods = Goods.me.convert(goodDetail, bizUserTags != null ? bizUserTags.getBizManName() : AppConfig.bizManName, bizUserTags != null ? bizUserTags.getBizManId() : String.valueOf(AppConfig.bizManId), goodTypeId,
								type.getStr("name"), String.valueOf(mer.getInt("id")), mer.getStr("name"));
//						log.info(AppConfig.formatLog("goodInfo:"+goods.toJson()));
						// TODO 商品信息保存
						if (!AppConfig.IS_DEV) {
							log.info(AppConfig.formatLog("save good info:[" + goodDetail.getGoodTitle() + "]"));
							boolean goodSave = goods.save();
							log.info(AppConfig.formatLog("goods save flag:[" + goodSave + "]"));
						}
						GoodInfo goodInfo = new GoodInfo();
						goodInfo.set("goodsid", goods.getInt("id"));
						goodInfo.set("descs", goodDetail.getGoodContent());
						// TODO 商品介绍内容保存
						if (!AppConfig.IS_DEV) {
							log.info(AppConfig.formatLog("save good detail info:[" + goodDetail.getGoodTitle() + "]"));
							boolean goodInfoSave = goodInfo.save();
							log.info(AppConfig.formatLog("good info save flag:[" + goodInfoSave + "]"));
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

	private static int getImage(String src) {

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
			picture.set("path", AppConfig.IMAGE_URL + currentPath + "/" + imageName);
			picture.set("url", "");
			picture.set("md5", "");
			picture.set("sha1", "");
			picture.set("status", "1");
			picture.set("create_time", System.currentTimeMillis() / 1000);
			// TODO 图片信息保存
			if (!AppConfig.IS_DEV) {
				picture.save();
				pictureId = picture.getLong("id").intValue();
			}
		} catch (MalformedURLException e) {
			log.error("图片保存失败", e.getCause());
		} catch (FileNotFoundException e) {
			log.error("图片保存失败", e.getCause());
		} catch (IOException e) {
			log.error("图片保存失败", e.getCause());
		}

		return pictureId;

	}

}
