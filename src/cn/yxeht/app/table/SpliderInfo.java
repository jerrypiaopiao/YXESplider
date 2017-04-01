package cn.yxeht.app.table;

import com.jfinal.plugin.activerecord.Model;

public class SpliderInfo extends Model<SpliderInfo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6345668819802716871L;

	/*
	 *CREATE TABLE `h_splider_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `h_rule_name` varchar(45) NOT NULL,
  `h_mer_host` text NOT NULL,
  `h_good_source` varchar(120) NOT NULL,
  `h_good_link` text NOT NULL,
  `h_catch_state` int(10) unsigned NOT NULL,
  `h_catch_reson` text,
  `h_src_type` int(11) NOT NULL,
  `h_src_free_str` text,
  `h_create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `h_update_time` timestamp NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=41524 DEFAULT CHARSET=utf8;
	 */
	public static final SpliderInfo me = new SpliderInfo();
	
	public static final int UN_FETCH = 0;
	public static final int FETCH_SUCCESS = 1;
	public static final int FETCH_FAILED = 2;
	
}
