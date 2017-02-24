package cn.yxeht.app.table;

import com.jfinal.plugin.activerecord.Model;

//@T
public class GoodInfo extends Model<GoodInfo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -502452045381404547L;
	
	public static final GoodInfo me = new GoodInfo();
	
	/*
	 * CREATE TABLE `h_goodsinfo` (
  `goodsid` mediumint(8) unsigned NOT NULL,
  `descs` text,
  `goodsjson` text COMMENT 'type=2活动时存在多个商品',
  UNIQUE KEY `goodsid` (`goodsid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
	 */

}
