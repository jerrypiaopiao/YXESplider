package cn.yxeht.app.quartz.test;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import cn.yxeht.app.quartz.QuartzManagerV2;

public class QuartzJob implements Job {

	private int i = 0;
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub
		System.out.println("job_1_i:"+i);
		
		System.out.println("我是任务["+arg0.getJobDetail().getName()+"]"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+ "★★★★★★★★★★★");  
		i++;
		
			System.out.println("移除job_2");
			QuartzManagerV2.removeJob("job_2");
		
	}

}
