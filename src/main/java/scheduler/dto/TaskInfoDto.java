package scheduler.dto;

public class TaskInfoDto {
	String group;
	String name;
	String className;
	int intervalInSeconds;
	int delayedSeconds;
	// 定义 1：开始 0：结束
	int status;

	public TaskInfoDto(String group, String name, String className, int intervalInSeconds, int delayedSeconds,
			int status) {
		this.group = group;
		this.name = name;
		this.className = className;
		this.intervalInSeconds = intervalInSeconds;
		this.delayedSeconds = delayedSeconds;
		this.status = status;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public int getIntervalInSeconds() {
		return intervalInSeconds;
	}

	public void setIntervalInSeconds(int intervalInSeconds) {
		this.intervalInSeconds = intervalInSeconds;
	}

	public int getDelayedSeconds() {
		return delayedSeconds;
	}

	public void setDelayedSeconds(int delayedSeconds) {
		this.delayedSeconds = delayedSeconds;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	

}