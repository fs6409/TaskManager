package scheduler.taskmanager;

import java.util.ArrayList;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scheduler.dto.TaskInfoDto;
import scheduler.util.SchedulerManagerUtil;

/**
 * 任务动态刷新监视器
 *
 * @author adam
 */
public class ReschedulerJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(ReschedulerJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("+++++++任务动态刷新监视器  开始");
        refresh(jobExecutionContext);
        log.info("+++++++任务动态刷新监视器  结束");
    }

    private void refresh(JobExecutionContext jobExecutionContext) {

        try {
            // TODO 模拟任务管理DB数据读入
            List<TaskInfoDto> taskInfoDtos = assumeDataFromDB();

            Scheduler scheduler = jobExecutionContext.getScheduler();
            for (TaskInfoDto newJobInfo : taskInfoDtos) {
                // 获取job任务明细
                JobDetail job = SchedulerManagerUtil.getJobDetail(scheduler, newJobInfo);
                if (job == null) {
                    if (newJobInfo.getStatus() == 1) {
                        // TODO 新规JOB
                        log.info("------- 新JOB ------ {} --- {}", newJobInfo.getGroup(), newJobInfo.getName());
                        SchedulerManagerUtil.addJob(scheduler, newJobInfo);
                    }
                } else {
                    TaskInfoDto oldJobInfo = SchedulerManagerUtil.getJobInfo(job);
                    if (oldJobInfo.getStatus() == 1 && newJobInfo.getStatus() == 0) {
                        // TODO JOB停止
                        log.info("------- JOB停止 ------ {}", job.getKey());
                        SchedulerManagerUtil.unscheduleJob(scheduler, newJobInfo);
                    } else if ((oldJobInfo.getStatus() == 1) && newJobInfo.getStatus() == 1) {
                        if (oldJobInfo.getIntervalInSeconds() != newJobInfo.getIntervalInSeconds()) {
                            // TODO 间隔时间变更
                            log.info("-------间隔时间变更--------{}", job.getKey());
                            SchedulerManagerUtil.rescheduleJob(scheduler, newJobInfo);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 模拟任务管理DB数据读入
     *
     * @return 三个工作任务
     */
    private List<TaskInfoDto> assumeDataFromDB() {
        List<TaskInfoDto> taskInfoDtos = new ArrayList<>();
        TaskInfoDto task1 = new TaskInfoDto("WORK_GROUP", "任务一", "class1", 2, 0, 1);
        TaskInfoDto task2 = new TaskInfoDto("WORK_GROUP", "任务二", "class2", 3, 0, 1);
        TaskInfoDto task3 = new TaskInfoDto("WORK_GROUP", "任务三", "class3", 4, 0, 1);
        taskInfoDtos.add(task1);
        taskInfoDtos.add(task2);
        taskInfoDtos.add(task3);
        return taskInfoDtos;
    }
}