package cn.yxeht.app.quartz;

import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import cn.yxeht.app.AppConfig;
import cn.yxeht.app.core.YXEConfLoad;

public class AutoFetchJob implements Job {

	public static final String TAG = "auto_fetch_job";
	
	private static final Logger log = Logger.getLogger(AutoFetchJob.class);
	
	private boolean isRunning = false;
	
	private String filePath = null;
	
	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		isRunning = true;
		AppConfig.initCache();
		synchronized (jobExecutionContext) {
			log.info(AppConfig.formatLog(TAG+" start executing on "+new Date(System.currentTimeMillis())+"..."));
			//开始执行自动抓取动作
			YXEConfLoad.autoFetch(this.filePath);
		}
	}
	
	public boolean isRunning(){
		return isRunning;
	}
	
	public void setFilePath(String filePath){
		this.filePath = filePath;
	}

}
