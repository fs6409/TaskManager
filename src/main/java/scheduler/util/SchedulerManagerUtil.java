package scheduler.util;

import java.util.Date;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

import scheduler.dto.TaskInfoDto;

public class SchedulerManagerUtil {

	private static final String  CURRENT_JOB_INFO_KEY = "CURRENT_JOB_INFO_KEY";

	/**
	 * SchedulerFactory  用于Scheduler的创建和管理
	 * getSchedule():创建调度者
	 * @return
	 * @throws Exception
	 */
	public static Scheduler getScheduler() throws Exception {
		SchedulerFactory schedulerFactory = new StdSchedulerFactory();
		return schedulerFactory.getScheduler();
	}

	/**
	 * 初始化scheduler 获取Scheduler 增加job任务
	 * @param scheduler
	 * @param jobInfos
	 * @throws Exception
	 */
	public static void init(Scheduler scheduler, List<TaskInfoDto> taskInfoDtos) throws Exception {
		for (TaskInfoDto taskInfoDto : taskInfoDtos) {
			if (taskInfoDto.getStatus() == 1) {
				addJob(scheduler, taskInfoDto);
			}
		}
	}

	/**
	 * 指定时间间隔启动调度
	 * @param scheduler
	 * @param delayedSeconds
	 * @throws Exception
	 */
	public static void run(Scheduler scheduler, int delayedSeconds) throws Exception {
		scheduler.startDelayed(delayedSeconds);
	}

	/**
	 * 代码用法解析：
	 * JobDetail
	 *  增加job执行任务。JobBuilder --建造者模式。链式建造。
	 *  newJob():定义job任务。每个JobInfoDto是真正执行逻辑所在。
	 *  withIdentity(): 定义name/group
	 *  jobDetail.getJobDataMap().put():实现了java.util.Map 接口。可以向 JobDataMap 中存入键/值对，那些数据对可在的 Job 类中传递和进行访问。这是一个向 Job 传送配置的信息便捷方法。
	 * Trigger：触发条件
	 *  一个Job可以对应多个Trigger。当多个Trigger同一时间点出发，那么根据优先级判断。数字越大，优先级越高。默认优先级为5。同一个任务有多个trigger时，触发先后顺序：时间->优先级->字母排序
	 *  newTrigger()定义触发器。TriggerBuilder --建造者模式。链式建造。
	 *  withIdentity():定义name/group
	 *  startAt():根据job任务间隔时间来触发执行job任务。  【startNow():一旦加入scheduler，立即生效】
	 *  withSchedule():增加Schedule 使用SimpleSchedule简单调度器，指定时间间隔触发。
	 *  scheduleJob():注册Trigger和job并进行调度
	 * @param scheduler
	 * @param jobInfo
	 * @throws Exception
	 */
	public static void addJob(Scheduler scheduler, TaskInfoDto taskInfoDto) throws Exception {
		Class<Job> cls = (Class<Job>) Class.forName("scheduler.taskmanager.TaskJob");
		JobDetail jobDetail = JobBuilder.newJob(cls)
				.withIdentity(taskInfoDto.getName(), taskInfoDto.getGroup())
				.build();
		jobDetail.getJobDataMap().put(CURRENT_JOB_INFO_KEY, taskInfoDto);

		Trigger trigger = TriggerBuilder.newTrigger()
				.withIdentity(taskInfoDto.getName(), taskInfoDto.getGroup())
				.startAt(getStartTime(taskInfoDto.getDelayedSeconds()))
				.withSchedule(SimpleScheduleBuilder.simpleSchedule()
						.withIntervalInSeconds(taskInfoDto.getIntervalInSeconds()).repeatForever())
				.build();
		scheduler.scheduleJob(jobDetail, trigger);
	}

	
	/**
	 * 刷新job属性
	 *  JobKey：JobKey是表明Job身份的一个对象
	 *  getJobDetail();根据jobkey获取job详情
	 *  getJobDataMap():根据创建时放入的map的key查询job配置信息
	 * 重新创建触发器
	 *   使用更新后的job间隔时间
	 * @param scheduler
	 * @param jobInfo
	 * @throws Exception
	 */
	public static void rescheduleJob(Scheduler scheduler, TaskInfoDto taskInfoDto) throws Exception {
		JobKey jobKey = JobKey.jobKey(taskInfoDto.getName(), taskInfoDto.getGroup());
		JobDetail jobDetail = scheduler.getJobDetail(jobKey);
		TaskInfoDto oldJobInfo = (TaskInfoDto) jobDetail.getJobDataMap().get(CURRENT_JOB_INFO_KEY);
		oldJobInfo.setIntervalInSeconds(taskInfoDto.getIntervalInSeconds());
		oldJobInfo.setDelayedSeconds(taskInfoDto.getDelayedSeconds());
		
		TriggerKey triggerKey = TriggerKey.triggerKey(taskInfoDto.getName(), taskInfoDto.getGroup());
		Trigger trigger = TriggerBuilder.newTrigger()
				.withIdentity(taskInfoDto.getName(), taskInfoDto.getGroup())
				.startAt(getStartTime(taskInfoDto.getIntervalInSeconds(), taskInfoDto.getDelayedSeconds()))
				.withSchedule(SimpleScheduleBuilder.simpleSchedule()
						.withIntervalInSeconds(taskInfoDto.getIntervalInSeconds()).repeatForever())
				.build();
		scheduler.rescheduleJob(triggerKey, trigger);
	}
	
	/**
	 * 停止job任务  unscheduleJob针对TriggerKey【deleteJob针对jobKey】
	 * @param scheduler
	 * @param jobInfo
	 * @throws Exception
	 */
	public static void unscheduleJob(Scheduler scheduler, TaskInfoDto taskInfoDto) throws Exception {
		TriggerKey triggerKey = TriggerKey.triggerKey(taskInfoDto.getName(), taskInfoDto.getGroup());
		scheduler.unscheduleJob(triggerKey);  
	}

	private static Date getStartTime(int delayedSeconds) {
		Date date = new Date(System.currentTimeMillis() + 1000 * delayedSeconds);
		return date;
	}
	
	private static Date getStartTime(int intervalInSeconds, int delayedSeconds) {
		Date date = new Date(System.currentTimeMillis() + 1000 * (delayedSeconds + intervalInSeconds));
		return date;
	}
	
	public static JobDetail getJobDetail(Scheduler scheduler, TaskInfoDto taskInfoDto) throws Exception {
		JobKey jobKey = JobKey.jobKey(taskInfoDto.getName(), taskInfoDto.getGroup());
		return scheduler.getJobDetail(jobKey);
	}
	
	public static TaskInfoDto getJobInfo(JobDetail jobDetail) {
		return (TaskInfoDto) jobDetail.getJobDataMap().get(CURRENT_JOB_INFO_KEY);
	}
	
	public static void addReschedulerJob(Scheduler scheduler, TaskInfoDto taskInfoDto) throws Exception {
		Class<Job> cls = (Class<Job>) Class.forName("scheduler.taskmanager.ReschedulerJob");
		JobDetail jobDetail = JobBuilder.newJob(cls)
				.withIdentity(taskInfoDto.getName(), taskInfoDto.getGroup())
				.build();
		jobDetail.getJobDataMap().put(CURRENT_JOB_INFO_KEY, taskInfoDto);

		Trigger trigger = TriggerBuilder.newTrigger()
				.withIdentity(taskInfoDto.getName(), taskInfoDto.getGroup())
				.startAt(getStartTime(taskInfoDto.getDelayedSeconds()))
				.withSchedule(SimpleScheduleBuilder.simpleSchedule()
						.withIntervalInSeconds(taskInfoDto.getIntervalInSeconds()).repeatForever())
				.build();
		scheduler.scheduleJob(jobDetail, trigger);
	}

}