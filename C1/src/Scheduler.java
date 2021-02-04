import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class Scheduler {
	private final List<Row> rows;
	private final List<Event> timeline;
	private int timeQuantum;

	public Scheduler() {
		rows = new ArrayList<>();
		timeline = new ArrayList<>();
		timeQuantum = 1;
	}

	public void setTimeQuantum(int timeQuantum) {
		this.timeQuantum = timeQuantum;
	}

	public int getTimeQuantum() {
		return timeQuantum;
	}

	public void add(Row row) {
		rows.add(row);
	}

	public double getAverageWaitingTime() {
		double avg = 0.0;
		for (Row row: rows)
			avg += row.getWt();
		return avg/rows.size();
	}

	public double getAverageTurnAroundTime() {
		double avg = 0.0;
		for (Row row: rows)
			avg += row.getTat();
		return avg/rows.size();
	}

	public Event getEvent(Row row) {
		for (Event event: timeline)
			if (row.getPid() == event.getPid())
				return event;
		return null;
	}

	public Row getRow(int pid) {
		for (Row row: rows)
			if (row.getPid() == pid)
				return row;
		return null;
	}

	public List<Row> getRows() {
		return rows;
	}

	public List<Event> getTimeline() {
		return timeline;
	}

	public abstract void process() throws IOException;
}
