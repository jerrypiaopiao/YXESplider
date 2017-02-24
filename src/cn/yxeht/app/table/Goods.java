package cn.yxeht.app.table;

//import sun.security.util.Length;
import cn.yxeht.app.biz.bean.GoodDetail;
import cn.yxeht.app.utils.TextUtil;

import com.jfinal.plugin.activerecord.Model;

/**
 * 
 * 商品表
 *
 */
public class Goods extends Model<Goods> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5329174795193051957L;
	
	/*
	 * CREATE TABLE `h_goods` (
	 * `id` mediumint(8) unsigned NOT NULL AUTO_INCREMENT,
	 * `type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1商品2活动',
	 * `goodstypeid` mediumint(8) NOT NULL  COMMENT '商品类型',
	 * `goodstypename` char(20) NOT NULL COMMENT '商品类型名称',
	 * 
	 * `brandid` mediumint(8) DEFAULT NULL COMMENT '暂时无用',
	 * `brandname` char(20) DEFAULT NULL COMMENT '暂时无用',
	 * 
	 * `merchantid` mediumint(8) NOT NULL COMMENT '商家id',
	 * `merchantname` char(20) NOT NULL COMMENT '商家名称',
	 * 
	 * `sizesid` mediumint(8) DEFAULT NULL COMMENT '暂时无用',
	 * `sizesname` char(20) DEFAULT NULL COMMENT '暂时无用',
	 * 
	 * `name` char(80) NOT NULL COMMENT '商品标题',
	 * 
	 * `shortname` char(50) NOT NULL COMMENT '暂时无用',
	 * `starttime` int(11) NOT NULL COMMENT '暂时无用',
	 * `endtime` int(11) NOT NULL COMMENT '暂时无用',
	 * 
	 * `express` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1转运2直邮',
	 * `pricejson` text NOT NULL COMMENT '价格json',
	 * `hrefold` varchar(1024) NOT NULL DEFAULT '0' COMMENT '原链接',
	 * `hrefnew` varchar(1024) NOT NULL,
	 * `mpic` mediumint(8) NOT NULL COMMENT '主图',
	 * `loved` mediumint(8) NOT NULL DEFAULT '0' COMMENT '喜欢数',
	 * `addthemes` mediumint(8) NOT NULL DEFAULT '0' COMMENT '加入主题次数',
	 * `comments` mediumint(8) NOT NULL DEFAULT '0' COMMENT '评论数',
	 * 
	 * `uid` mediumint(8) NOT NULL COMMENT '编辑id',
	 * `username` char(20) NOT NULL COMMENT '发布者,编辑名称',
	 * `time` int(11) NOT NULL DEFAULT '0' COMMENT "发布时间",
	 * `state` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1草稿2发布',
	 * `isaddintegral` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1默认2已添加',
	 * `clicks` int(10) NOT NULL DEFAULT '0' COMMENT '点击次数',
	 * 
	 * PRIMARY KEY (`id`)
	 * ) ENGINE=MyISAM AUTO_INCREMENT=8028 DEFAULT CHARSET=utf8;
	 */
	
	public static Goods me = new Goods();
	
	public Goods convert(GoodDetail goodDetail, String user, String uid, String typeId, String typeName, String merchantId, String merchantName){
		Goods goods = new Goods();
		goods.set("type", 1);
		goods.set("goodstypeid", typeId);
		goods.set("goodstypename", typeName);
		goods.set("merchantid", merchantId);
		if(merchantName.length() > 20){
			merchantName = merchantName.substring(0, 20);
		}
		goods.set("merchantname", merchantName);
		String name = goodDetail.getGoodTitle();
		if(TextUtil.isEmpty(name)){
			name = "-------unknown-------";
		}else if(!TextUtil.isEmpty(name) && name.length() > 80){
			name = name.substring(0, 80);
		}
		goods.set("name", name);
		if(goodDetail.getzDirect() == GoodDetail.ZDIRECT){
			goods.set("express", 2);
		}else{
			goods.set("express", 1);
		}
		
		goods.set("pricejson", "");
		
		goods.set("shortname", "");
		goods.set("starttime", 0);
		goods.set("endtime", 0);
		
		goods.set("hrefold", goodDetail.getGoodSrcLink());
		goods.set("hrefnew", goodDetail.getYxehtLink());
		goods.set("mpic", 1);
		goods.set("loved", 0);
		goods.set("addthemes", 0);
		goods.set("comments", 0);
		goods.set("uid", uid);
		goods.set("username", user);
		goods.set("time", System.currentTimeMillis()/1000);
		goods.set("state", 1);
		goods.set("isaddintegral", 2);
		goods.set("clicks", 0);
		return goods;
	}
	
}
