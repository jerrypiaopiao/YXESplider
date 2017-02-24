package cn.yxeht.app.table;

import com.jfinal.plugin.activerecord.Model;

public class SpliderFilterWord extends Model<SpliderFilterWord> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2087059737314000244L;
	
	/*
	 * CREATE TABLE `httest`.`h_splider_filter_word` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `h_src_id` INT NOT NULL COMMENT '目标网站对应id，比如55海淘对应4',
  `h_filter_words` TEXT NULL COMMENT '过滤词，以逗号隔开',
  PRIMARY KEY (`id`));
	 */
	public static final SpliderFilterWord me = new SpliderFilterWord();
	
	public static final int ADD = 1;
	public static final int MODIFY = 2;

}
