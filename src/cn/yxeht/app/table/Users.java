package cn.yxeht.app.table;

import com.jfinal.plugin.activerecord.Model;

/**
 * 
 * 用户表
 *
 */
public class Users extends Model<Users> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2255885274053146112L;
	
	public static Users me = new Users();
	
	/*
	 * CREATE TABLE `h_users` (
  `uid` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` char(16) NOT NULL COMMENT '昵称',
  `password` char(32) NOT NULL COMMENT '密码',
  `tele` char(11) NOT NULL COMMENT '电话',
  `integral` int(8) NOT NULL DEFAULT '0' COMMENT '积分',
  `img` varchar(100) DEFAULT NULL COMMENT '用户头像',
  `sex` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT '1男2女',
  `email` varchar(30) DEFAULT NULL COMMENT 'email',
  `login` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '登录次数',
  `reg_ip` varchar(20) DEFAULT NULL COMMENT '注册IP',
  `reg_time` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '注册时间',
  `last_login_ip` varchar(20) DEFAULT NULL COMMENT '最后登录IP',
  `last_login_time` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '最后登录时间',
  `level` tinyint(1) DEFAULT '1' COMMENT '等级1普通2达人',
  `money` int(8) DEFAULT '0' COMMENT '铜币',
  `payaccount` varchar(80) DEFAULT NULL COMMENT '帐号',
  `jxbl` double DEFAULT '1' COMMENT '积分比例(0~1)',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态1开启2未通过',
  `bjtop` int(10) NOT NULL DEFAULT '0',
  `shareUrl` varchar(255) DEFAULT NULL,
  `shareclick` int(10) NOT NULL DEFAULT '0',
  `times` int(2) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`),
  KEY `tele` (`tele`),
  KEY `username` (`username`,`password`)
) ENGINE=MyISAM AUTO_INCREMENT=781 DEFAULT CHARSET=utf8 COMMENT='前台用户表';
	 */

}
