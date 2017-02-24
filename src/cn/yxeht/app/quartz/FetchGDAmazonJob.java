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
import java.util.ArrayList;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import cn.yxeht.app.AppConfig;
import cn.yxeht.app.biz.bean.GoodDetail;
import cn.yxeht.app.biz.rule.BaseRule;
import cn.yxeht.app.biz.rule.GoodListRule;
import cn.yxeht.app.constants.TypeMatchRule;
import cn.yxeht.app.core.HaiTaoSpliderService;
import cn.yxeht.app.table.GoodInfo;
import cn.yxeht.app.table.Goods;
import cn.yxeht.app.table.Goodstype;
import cn.yxeht.app.table.Merchant;
import cn.yxeht.app.table.Picture;
import cn.yxeht.app.utils.DateUtils;

public class FetchGDAmazonJob implements Job {

	public static final String TAG = "GuangDiu_Fetch_job";
	
	private boolean isTest;
	
	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		AppConfig.initCache();
		
		int limit = 0;
		
		for(String typeName : TypeMatchRule.GUANGDIU_TYPE_STRING){
			String goodTypeId = TypeMatchRule.getGoodTypeIdByNameGuangDiu(typeName);
			
			List<GoodListRule> list = new ArrayList<GoodListRule>();
			GoodListRule r1 = new GoodListRule();
			r1.setRuleName("美国亚马逊");
			r1.setRequestMoethod(BaseRule.GET);
			r1.setClassName("goodname");
			List<String> goodHosts = new ArrayList<>();
			goodHosts.add("http://www.amazon.com");
			goodHosts.add("https://www.amazon.com");
			r1.setGoodHost(goodHosts);
			r1.setHost("http://guangdiu.com/");
			r1.setGoodListUrl("cate.php?m=Amazon&k="+TypeMatchRule.GUANGDIU_TYPE_MATCH_RULE.get(goodTypeId)+"&c=us");
			r1.setType(BaseRule.CLASS);
			list.add(r1);
			
			for (GoodListRule gr : list) {
				synchronized (gr) {
					List<String> detailLinks = HaiTaoSpliderService.fetchGoodDetailLink(gr);
					
					int count = 0;
					for (String d : detailLinks) {
						if(count > 1 && isTest){
							break;
						}
						count++;
						GoodDetail goodDetail = HaiTaoSpliderService.fetchGDGoodDetail(gr.getDefaultAmazonHost(),
								gr.getHost() + d, BaseRule.GET);
						Merchant mer = null;//AppConfig.MERCHANTID_NAME_MAP.get("http://www.amazon.com");
						Goodstype type =AppConfig.GOODTYE_MAP.get(goodTypeId);
						Goods tmp = Goods.me.findFirst("select * from h_goods where hrefnew=?", goodDetail.getYxehtLink());
						if(tmp == null){
							Goods goods = Goods.me.convert(goodDetail, AppConfig.bizManName, String.valueOf(AppConfig.bizManId), goodTypeId, type.getStr("name"), String.valueOf(mer.getInt("id")), mer.getStr("name"));
							goods.save();
							GoodInfo goodInfo = new GoodInfo();
							goodInfo.set("goodsid", goods.getInt("id"));
							goodInfo.set("descs", goodDetail.getGoodContent());
							goodInfo.save();
							if(goodDetail.getmImgLinks() != null && goodDetail.getmImgLinks().size() > 0){
								int i = 0;
								for(String src : goodDetail.getmImgLinks()){
									if(i < 3){
										int picId = getImage(src);
										if(i == 0){
											goods.set("mpic", picId);
											goods.update();
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
		
	}
	
	private int getImage(String src){
		
		int pictureId = 0;
		
		try {
			URL url = new URL(src);
			URLConnection uri=url.openConnection();
			InputStream is=uri.getInputStream();
			String imageName = System.currentTimeMillis()+".jpg";
			
			String path = AppConfig.IMAGE_SAVE_PATH;
			
			String currentPath = DateUtils.currentDate();
			
			if(path.endsWith("\\")){
				path = path + currentPath;
			}else{
				path = path + File.separator + currentPath;
			}
			
			File todayDir = new File(path);
			if(!todayDir.exists()){
				todayDir.mkdirs();
			}
			
			OutputStream os = new FileOutputStream(new File(path, imageName));
			byte[] buf = new byte[1024];
			int l=0;
			while((l=is.read(buf))!=-1){
				os.write(buf, 0, l); 
			}
			
			os.close();
			
			Picture picture = new Picture();
			/*
			 *   `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id自增',
  `path` varchar(255) NOT NULL DEFAULT '' COMMENT '路径',
  `url` varchar(255) NOT NULL DEFAULT '' COMMENT '图片链接',
  `md5` char(32) NOT NULL DEFAULT '' COMMENT '文件md5',
  `sha1` char(40) NOT NULL DEFAULT '' COMMENT '文件 sha1编码',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '状态',
  `create_time` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '创建时间',
			 */
			picture.set("path", AppConfig.IMAGE_URL+currentPath+"/"+imageName);
			picture.set("url", "");
			picture.set("md5", "");
			picture.set("sha1", "");
			picture.set("status", "1");
			picture.set("create_time", System.currentTimeMillis()/1000);
			picture.save();
			pictureId = picture.getLong("id").intValue();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return pictureId;
		
	}
	
	public void setIsTest(boolean isTest){
		this.isTest = isTest;
	}

}
