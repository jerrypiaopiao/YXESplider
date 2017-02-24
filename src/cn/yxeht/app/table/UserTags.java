package cn.yxeht.app.table;

import com.jfinal.plugin.activerecord.Model;

public class UserTags extends Model<UserTags> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5296851486611693874L;
	
	public static UserTags me = new UserTags();
	
	/*
	 * CREATE TABLE `h_userstags` (
  `id` mediumint(8) unsigned NOT NULL AUTO_INCREMENT,
  `uid` mediumint(8) NOT NULL,
  `username` char(16) NOT NULL,
  `urlid` tinyint(1) NOT NULL,
  `url` char(30) NOT NULL,
  `tags` varchar(100) DEFAULT NULL COMMENT '替换标签',
  `truetags` varchar(50) NOT NULL COMMENT '真正标签',
  `time` int(11) NOT NULL,
  `realname` varchar(255) DEFAULT NULL COMMENT '真实姓名',
  `factor` decimal(2,1) DEFAULT NULL COMMENT '绩效工资系数',
  `is_show` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否显示工资系数 1  --是 0--否',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uidUrlid` (`uid`,`urlid`)
) ENGINE=MyISAM AUTO_INCREMENT=65 DEFAULT CHARSET=utf8;
	 */

}
