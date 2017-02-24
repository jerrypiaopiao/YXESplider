package cn.yxeht.app.table;

import com.jfinal.plugin.activerecord.Model;

public class SpliderGoodType extends Model<SpliderGoodType> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6373412835463150932L;
	
	/*
	 * CREATE TABLE `httest`.`h_splider_type` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `h_yxe_type_id` INT NOT NULL COMMENT '洋小二分类id',
  `h_src_type_str` TEXT NOT NULL COMMENT '目标网站分类信息，以逗号隔开',
  `h_src_id` INT NOT NULL COMMENT '目标网站id，比如55海淘对应4',
  PRIMARY KEY (`id`));
	 */
	public static final SpliderGoodType me = new SpliderGoodType();
	
	public static final int ADD = 1;
	public static final int MODIFY = 2;

}
