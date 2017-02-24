package cn.yxeht.app.table;

import com.jfinal.plugin.activerecord.Model;

public class Picture extends Model<Picture> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4015611023723260118L;
	
	/*
	 *   `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id自增',
  `path` varchar(255) NOT NULL DEFAULT '' COMMENT '路径',
  `url` varchar(255) NOT NULL DEFAULT '' COMMENT '图片链接',
  `md5` char(32) NOT NULL DEFAULT '' COMMENT '文件md5',
  `sha1` char(40) NOT NULL DEFAULT '' COMMENT '文件 sha1编码',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '状态',
  `create_time` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '创建时间',
	 */
	
	public static final Picture me = new Picture();
	
}
