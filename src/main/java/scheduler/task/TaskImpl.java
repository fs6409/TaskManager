package scheduler.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import scheduler.dto.TaskInfoDto;

@Component("Task")
public class TaskImpl implements Task {

    private static final Logger log = LoggerFactory.getLogger(TaskImpl.class);

    @Override
    public boolean run(TaskInfoDto taskInfo) {
        log.info("-=-=-=-=-=  工作组：{}   执行任务 : {}", taskInfo.getGroup(), taskInfo.getName());
        log.debug("-=-=-=-=-=  工作组：{}   执行任务 : {}", taskInfo.getGroup(), taskInfo.getName());
        return true;
    }
}
