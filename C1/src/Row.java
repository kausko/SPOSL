public class Row {
	private int pid;	// process id
	private int at;		// arrival time
	private int bt;		// burst time
	private int ct;		// completion time
	private int wt;		// waiting time
	private int tat;	// turn-around time
	private int priority;

	private Row(int pid, int at, int bt, int ct, int wt, int tat, int priority) {
		this.pid = pid;
		this.at = at;
		this.bt = bt;
		this.ct = ct;
		this.wt = wt;
		this.tat = tat;
		this.priority = priority;
	}

	public Row(int pid, int at, int bt) {
		this(pid, at, bt, 0, 0, 0, -1);
	}

	public void setBt(int bt) {
		this.bt = bt;
	}

	public void setCt(int ct) {
		this.ct = ct;
	}

	public void setWt(int wt) {
		this.wt = wt;
	}

	public void setTat(int tat) {
		this.tat = tat;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getPriority() {
		return priority;
	}

	public int getAt() {
		return at;
	}

	public int getBt() {
		return bt;
	}

	public int getCt() {
		return ct;
	}

	public int getPid() {
		return pid;
	}

	public int getTat() {
		return tat;
	}

	public int getWt() {
		return wt;
	}
}
