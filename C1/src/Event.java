public class Event {
	private final int pid;
	private final int startTime;
	private int endTime;

	public Event(int pid, int startTime, int endTime) {
		this.pid = pid;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public int getPid() {
		return pid;
	}

	public int getStartTime() {
		return startTime;
	}

	public int getEndTime() {
		return endTime;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}
}
