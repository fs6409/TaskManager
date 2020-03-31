package scheduler.taskmanager;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import scheduler.dto.TaskInfoDto;
import scheduler.task.Task;
import scheduler.util.SchedulerManagerUtil;
import scheduler.util.SpringContextUtil;


/**
 * 任务管理器
 * @author adam
 */
public class TaskJob implements Job {

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDetail jobDetail = jobExecutionContext.getJobDetail();
		TaskInfoDto jobInfo = SchedulerManagerUtil.getJobInfo(jobDetail);
		// 获取通用任务job
		Task task = SpringContextUtil.getBean("Task", Task.class);
		task.run(jobInfo);
	}
}