package cn.yxeht.app.quartz;

import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import cn.yxeht.app.AppConfig;
import cn.yxeht.app.controller.YXEController;
import cn.yxeht.app.core.SpliderService;

public class AutoFetchJobV2 implements Job {

	public static final String TAG = "auto_fetch_job_v2";
	
	private static final Logger log = Logger.getLogger(AutoFetchJobV2.class);
	
	private boolean isRunning = false;
	
	private boolean refresh = true;
	
	private String path = "";
	
	public AutoFetchJobV2(){
		this.refresh = YXEController.IS_REFRESH;
		this.path = YXEController.WEB_ROOT_PATH;
	}
	
	public AutoFetchJobV2(boolean refresh, String path) {
		// TODO Auto-generated constructor stub
		this.refresh = refresh;
		this.path = path;
	}
	
	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		isRunning = true;
		AppConfig.initCache();
		synchronized (jobExecutionContext) {
			log.info(AppConfig.formatLog(TAG+" start executing on "+new Date(System.currentTimeMillis())+"..."));
			//开始执行自动抓取动作
			SpliderService.fetchGoodLinkByRules(refresh, path);
		}
	}
	
	public boolean isRunning(){
		return isRunning;
	}
	
}
