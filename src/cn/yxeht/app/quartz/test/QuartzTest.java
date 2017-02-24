package cn.yxeht.app.quartz.test;

import cn.yxeht.app.quartz.QuartzManagerV2;

public class QuartzTest {

	public static void main(String[] args) {

		try {
			String job_name = "job_1";
			System.out.println("【系统启动"+job_name+"】开始(每5秒输出一次)...");  
			QuartzManagerV2.addJob(job_name, QuartzJob.class, "0/4 * * * * ?");
			
			String job_name1 = "job_2";
			System.out.println("【系统启动"+job_name1+"】开始(每2秒输出一次)..."); 
			QuartzManagerV2.addJob(job_name1, QuartzJob2.class, "0/2 * * * * ?");
			
			Thread.sleep(30000);
			
//			System.out.println("开始5秒睡眠");
//			Thread.sleep(5000);  
//			System.out.println("结束6秒睡眠");
//			System.out.println("【修改时间】开始(每2秒输出一次)...");  
//			QuartzManagerV2.modifyJobTime(job_name, "10/2 * * * * ?");  
//			System.out.println("开始6秒睡眠");
//			Thread.sleep(6000);  
//			System.out.println("结束6秒睡眠");
//			System.out.println("【移除定时】开始...");  
//			QuartzManagerV2.removeJob(job_name);  
//			System.out.println("【移除定时】成功");  
//			
//			System.out.println("【再次添加定时任务】开始(每10秒输出一次)...");  
//			QuartzManagerV2.addJob(job_name, QuartzJob.class, "*/10 * * * * ?");  
//			Thread.sleep(60000);  
//			System.out.println("【移除定时"+job_name1+"】开始...");  
//			QuartzManagerV2.removeJob(job_name1);  
//			System.out.println("【移除定时"+job_name1+"】成功");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
