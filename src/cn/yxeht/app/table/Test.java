package cn.yxeht.app.table;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

public class Test extends Model<Test> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1433259332174075260L;
	
	public static Test me = new Test();
	
	/**
	 * CREATE TABLE `test` (
	 * `id` int(11) NOT NULL AUTO_INCREMENT,
	 * `name` varchar(32) DEFAULT NULL,
	 * `password` varchar(360) DEFAULT NULL,
	 * PRIMARY KEY (`id`)
	 * ) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;	
	 */
	public List<Test> findAll(){
		return me.find("select * from test;");
	}
	
}
