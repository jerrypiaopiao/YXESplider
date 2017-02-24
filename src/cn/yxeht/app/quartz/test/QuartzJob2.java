package cn.yxeht.app.quartz.test;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class QuartzJob2 implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub
		System.out.println("我是任务["+arg0.getJobDetail().getName()+"]"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+ "^^^^^^^^^^^^^^");  
	}

}
