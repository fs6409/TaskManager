package scheduler;

import org.quartz.Scheduler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import scheduler.dto.TaskInfoDto;
import scheduler.util.SchedulerManagerUtil;
import scheduler.util.SpringContextUtil;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MySchedulerTest {
    public static void main(String[] args) throws Exception {

        // 任务管理启动
        Scheduler scheduler = SchedulerManagerUtil.getScheduler();

        // TODO 加载任务管理器动态刷新监视器
        TaskInfoDto manager = new TaskInfoDto("MANAGER_GROUP", "manager", "manager", 5, 5, 1);
        SchedulerManagerUtil.addReschedulerJob(scheduler, manager);

        // 获取applicationContext set注入
        ConfigurableApplicationContext applicationContext = SpringApplication.run(MySchedulerTest.class, args);
        SpringContextUtil.setApplicationContext(applicationContext);

        // TODO 模拟任务管理DB数据读入 see@assumeDataFromDB

        // 这个是提供好的工具
        SchedulerManagerUtil.run(scheduler, 0);

    }

}