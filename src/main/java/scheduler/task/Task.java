package scheduler.task;

import scheduler.dto.TaskInfoDto;

public interface Task {
	
	boolean run(TaskInfoDto taskInfo);

}
