package cn.yxeht.app.table;

import com.jfinal.plugin.activerecord.Model;

/**
 * 
 * 商家表
 *
 */
public class Merchant extends Model<Merchant> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 728703697843008913L;
	
	public static Merchant me = new Merchant();
	
	/*
	 * CREATE TABLE `h_merchant` (
	 * `id` mediumint(8) unsigned NOT NULL AUTO_INCREMENT,
	 * `name` char(20) NOT NULL,
	 * `url` varchar(255) DEFAULT NULL COMMENT '商家地址',
	 * `coin` char(10) DEFAULT NULL,
	 * `sort` int(5) DEFAULT '100',
	 * `interUrl` varchar(255) NOT NULL DEFAULT '',
	 * `typeM` int(1) NOT NULL DEFAULT '1',
	 * `display` tinyint(1) NOT NULL DEFAULT '1',
	 * PRIMARY KEY (`id`)
	 * ) ENGINE=MyISAM AUTO_INCREMENT=86 DEFAULT CHARSET=utf8 COMMENT='商家表';
	 */

}
