import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Priority_Non_Preemptive extends Scheduler {
	@Override
	public void process() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter priorities: ");
		this.getRows().forEach(row -> {
			System.out.print(row.getPid() + ". ");
			try {
				row.setPriority(Integer.parseInt(br.readLine()));
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		});

		this.getRows().sort(Comparator.comparingInt(Row::getAt));

		List<Row> rows = new ArrayList<>();
		this.getRows().forEach(row ->
			rows.add(new Row(row.getPid(), row.getAt(), row.getBt()))
		);
		int time = rows.get(0).getAt();

		while (!rows.isEmpty()) {
			int finalTime = time;
			List<Row> availableRows = rows
					.stream()
					.filter(row -> row.getAt() <= finalTime)
					.sorted(Comparator.comparingInt(Row::getPriority))
					.collect(Collectors.toList());

			Row row = availableRows.get(0);
			this.getTimeline().add(new Event(row.getPid(), time, time + row.getBt()));
			time += row.getBt();

			for (int i = 0; i < rows.size(); i++) {
				if (rows.get(i).getPid() == row.getPid()) {
					rows.remove(i);
					break;
				}
			}
		}

		this.getRows().forEach(row -> {
			row.setWt(this.getEvent(row).getStartTime() - row.getAt());
			row.setTat(row.getWt() + row.getBt());
			row.setCt(row.getTat() + row.getAt());
		});
	}
}
