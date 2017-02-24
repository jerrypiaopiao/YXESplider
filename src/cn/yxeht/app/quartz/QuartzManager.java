package cn.yxeht.app.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by jerry on 2015/11/30.
 */
public class QuartzManager {

    private static SchedulerFactory sf = new StdSchedulerFactory();
    private static String JOB_GROUP_NAME = "group1";
    private static String TRIGGER_GROUP_NAME = "trigger1";

    /**
     * 添加一个定时任务,使用默认的任务组名,触发器名,触发器组名
     *
     * @param jobName 任务名
     * @param job     任务
     * @param time    时间设置
     * @throws SchedulerException
     * @throws ParseException
     */
    public static void addJob(String jobName, Job job, String time) throws SchedulerException, ParseException {
        Scheduler sched = sf.getScheduler();
        //任务名.任务组,任务执行类
        JobDetail jobDetail = new JobDetail(jobName, JOB_GROUP_NAME, job.getClass());
        //触发器名,触发器组
        CronTrigger trigger = new CronTrigger(jobName, TRIGGER_GROUP_NAME);
        trigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        trigger.setStartTime(new Date(System.currentTimeMillis()));
        trigger.setCronExpression(time);
        sched.scheduleJob(jobDetail, trigger);
        //启动
        if (!sched.isShutdown()) {
            sched.start();
        }
    }

    /**
     * 添加一个定时任务
     *
     * @param jobName          任务名
     * @param jobGroupName     任务组名
     * @param triggerName      触发器名
     * @param triggerGroupName 触发器组名
     * @param job              任务
     * @param time             时间设置
     * @throws SchedulerException
     * @throws ParseException
     */
    public static void addJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName, Job job, String time) throws SchedulerException, ParseException {
        Scheduler sched = sf.getScheduler();
        //任务名.任务组,任务执行类
        JobDetail jobDetail = new JobDetail(jobName, jobGroupName, job.getClass());
        //触发器名,触发器组
        CronTrigger trigger = new CronTrigger(triggerName, triggerGroupName);
        trigger.setCronExpression(time);
        sched.scheduleJob(jobDetail, trigger);
        //启动
        if (!sched.isShutdown()) {
            sched.start();
        }
    }

    /**
     * 修改一个任务的触发时间(使用默认的任务组名,触发器名,触发器组名)
     *
     * @param jobName
     * @param time
     * @throws SchedulerException
     * @throws ParseException
     */
    public static void modifyJobTime(String jobName, String time) throws SchedulerException, ParseException {
        Scheduler sched = sf.getScheduler();
        Trigger trigger = sched.getTrigger(jobName, TRIGGER_GROUP_NAME);
        if (trigger != null) {
            CronTrigger ct = (CronTrigger) trigger;
            ct.setCronExpression(time);
            sched.resumeTrigger(jobName, TRIGGER_GROUP_NAME);
        }
    }

    /**
     * 修改一个任务的触发时间
     *
     * @param triggerName
     * @param triggerGroupName
     * @param time
     * @throws SchedulerException
     * @throws ParseException
     */
    public static void modifyJobTime(String triggerName, String triggerGroupName, String time) throws SchedulerException, ParseException {
        Scheduler sched = sf.getScheduler();
        Trigger trigger = sched.getTrigger(triggerName, triggerGroupName);
        if (trigger != null) {
            CronTrigger ct = (CronTrigger) trigger;
            ct.setCronExpression(time);
            sched.resumeTrigger(triggerName, TRIGGER_GROUP_NAME);
        }
    }

    /**
     * 移除一个任务(使用默认的任务组名,触发器名,触发器组名)
     * @param jobName
     * @throws SchedulerException
     */
    public static void removeJob(String jobName) throws SchedulerException {
        Scheduler sched = sf.getScheduler();
        sched.pauseTrigger(jobName,TRIGGER_GROUP_NAME);//停止触发器
        sched.unscheduleJob(jobName, TRIGGER_GROUP_NAME);//移除触发器
        sched.deleteJob(jobName, JOB_GROUP_NAME);//删除任务
    }

    /**
     * 移除一个任务
     * @param jobName
     * @param jobGroupName
     * @param triggerName
     * @param triggerGroupName
     * @throws SchedulerException
     */
    public static void removeJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName) throws SchedulerException {
        Scheduler sched = sf.getScheduler();
        sched.pauseTrigger(triggerName,triggerGroupName);//停止触发器
        sched.unscheduleJob(triggerName, triggerGroupName);//移除触发器
        sched.deleteJob(jobName, jobGroupName);//删除任务
    }

}
