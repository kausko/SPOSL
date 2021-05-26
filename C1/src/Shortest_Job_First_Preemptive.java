import java.util.*;

public class Shortest_Job_First_Preemptive extends Scheduler {
	@Override
	public void process() {

		this.getRows().sort(Comparator.comparingInt(Row::getAt));

		List<Row> rows = new ArrayList<>();
		this.getRows().forEach(row ->
				rows.add(new Row(row.getPid(), row.getAt(), row.getBt()))
		);

		int time = rows.get(0).getAt();

		while (!rows.isEmpty()) {

			List<Row> availableRows = new ArrayList<>();
			for (Row row : rows) {
				if (row.getAt() <= time)
					availableRows.add(row);
			}

			availableRows.sort((Object o1, Object o2) -> {
				if (((Row) o1).getBt() == ((Row) o2).getBt())
					return 0;
				else if (((Row) o1).getBt() < ((Row) o2).getBt())
					return -1;
				return 1;
			});

			Row row = availableRows.get(0);
			this.getTimeline().add(new Event(row.getPid(), time, ++time));
			row.setBt(row.getBt() - 1);

			if (row.getBt() == 0) {
				for (int i = 0; i < rows.size(); i++) {
					if (rows.get(i).getPid() == row.getPid()) {
						rows.remove(i);
						break;
					}
				}
			}
		}

		for (int i = this.getTimeline().size() - 1; i > 0; i--) {
			List<Event> timeline = this.getTimeline();

			if (timeline.get(i - 1).getPid() == timeline.get(i).getPid()) {
				timeline.get(i - 1).setEndTime(timeline.get(i).getEndTime());
				timeline.remove(i);
			}
		}

		Map<Integer, Integer> map = new HashMap<>();

		this.getRows().forEach(row -> {
			map.clear();
			this.getTimeline().forEach(event -> {
				if (event.getPid() == row.getPid()){
					if (map.containsKey(event.getPid()))
						row.setWt(row.getWt() + (event.getStartTime() - map.get(event.getPid())));
					else
						row.setWt(event.getStartTime() - row.getAt());
					map.put(event.getPid(), event.getEndTime());
				}
			});
			row.setTat(row.getWt() + row.getBt());
			row.setCt(row.getTat() + row.getAt());
		});
	}
}