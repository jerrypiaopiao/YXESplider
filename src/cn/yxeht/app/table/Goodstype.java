package cn.yxeht.app.table;

import com.jfinal.plugin.activerecord.Model;

public class Goodstype extends Model<Goodstype> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3578660640591338583L;
	
	public static Goodstype me = new Goodstype();
	
	/*
	 * CREATE TABLE `h_goodstype` (
	 * `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
	 * `name` char(50) DEFAULT NULL COMMENT '分类名称',
	 * `father` int(11) DEFAULT NULL COMMENT '是否父级',
	 * `isson` tinyint(4) NOT NULL COMMENT '是否有下级',
	 * `path` varchar(100) NOT NULL COMMENT '路径',
	 * `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1默认2删除',
	 * PRIMARY KEY (`id`),
	 * KEY `name` (`name`)
	 * ) ENGINE=MyISAM AUTO_INCREMENT=395 DEFAULT CHARSET=utf8 COMMENT='商品分类表';
	 */

}
