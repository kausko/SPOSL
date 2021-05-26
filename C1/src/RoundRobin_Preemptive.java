import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class RoundRobin_Preemptive extends Scheduler{
	@Override
	public void process() throws IOException {
		this.getRows().sort(Comparator.comparingInt(Row::getAt));

		List<Row> rows = new ArrayList<>();
		this.getRows().forEach(row ->
				rows.add(new Row(row.getPid(), row.getAt(), row.getBt()))
		);
		int time = rows.get(0).getAt();

		System.out.print("Enter Time Quantum: ");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		this.setTimeQuantum(Integer.parseInt(br.readLine()));

		int tQ = this.getTimeQuantum();

		while (!rows.isEmpty()) {
			Row row = rows.get(0);
			int bt = Math.min(row.getBt(), tQ);
			this.getTimeline().add(new Event(row.getPid(), time, time + bt));
			time += bt;
			rows.remove(0);

			if (row.getBt() > tQ) {
				row.setBt(row.getBt() - tQ);
				for (int i = 0; i < rows.size(); i++) {
					if (rows.get(i).getAt() > time) {
						rows.add(i, row);
						break;
					}
					else if (i == rows.size() - 1) {
						rows.add(row);
						break;
					}
				}
			}
		}

		Map<Integer, Integer> map = new HashMap<>();

		this.getRows().forEach(row -> {
			map.clear();
			this.getTimeline().forEach(event -> {
				if (event.getPid() == row.getPid()) {
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
