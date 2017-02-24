package cn.yxeht.app.table;

import com.jfinal.plugin.activerecord.Model;

public class SpliderInfo extends Model<SpliderInfo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6345668819802716871L;

	/*
	 * CREATE TABLE `h_splider_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `h_rule_name` VARCHAR(45) NOT NULL,
  `h_mer_host` TEXT NOT NULL,
  `h_good_source` varchar(120) NOT NULL,
  `h_good_link` text NOT NULL,
  `h_catch_state` int(10) unsigned NOT NULL,
  `h_catch_reson` text,
  `h_src_type` INT NOT NULL,
  `h_src_free_str` TEXT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
ALTER TABLE `httest`.`h_splider_info` 
ADD COLUMN `h_src_type` INT NOT NULL AFTER `h_catch_reson`;
	 */
	public static final SpliderInfo me = new SpliderInfo();
	
	public static final int UN_FETCH = 0;
	public static final int FETCH_SUCCESS = 1;
	public static final int FETCH_FAILED = 2;
	
}
