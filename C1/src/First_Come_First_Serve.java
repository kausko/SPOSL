import java.util.Comparator;
import java.util.List;

public class First_Come_First_Serve extends Scheduler{
	@Override
	public void process() {

		this.getRows().sort(Comparator.comparingInt(Row::getAt));

		List<Event> timeline = this.getTimeline();

		this.getRows().forEach(row -> {
			if (timeline.isEmpty())
				timeline.add(new Event(row.getPid(), row.getAt(), row.getAt() + row.getBt()));
			else {
				Event event = timeline.get(timeline.size() - 1);
				timeline.add(new Event(row.getPid(), event.getEndTime(), event.getEndTime() + row.getBt()));
			}
		});

		this.getRows().forEach(row -> {
			row.setWt(this.getEvent(row).getStartTime() - row.getAt());
			row.setTat(row.getWt() + row.getBt());
			row.setCt(row.getTat() + row.getAt());
		});
	}
}
