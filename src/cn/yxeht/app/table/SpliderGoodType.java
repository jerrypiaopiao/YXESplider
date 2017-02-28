package cn.yxeht.app.table;

import com.jfinal.plugin.activerecord.Model;

public class SpliderGoodType extends Model<SpliderGoodType> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6373412835463150932L;
	
	/*
CREATE TABLE `h_splider_type` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `h_yxe_type_id` int(11) NOT NULL COMMENT '洋小二分类id',
  `h_yxe_type_name` text NOT NULL,
  `h_src_type_str` text NOT NULL COMMENT '目标网站分类信息，以逗号隔开',
  `h_src_id` int(11) NOT NULL COMMENT '目标网站id，比如55海淘对应4',
  `h_src_name` varchar(120) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
	 */
	public static final SpliderGoodType me = new SpliderGoodType();
	
	public static final int ADD = 1;
	public static final int MODIFY = 2;

}
