import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter number of positive non-zero processes: ");
        int processCount = 0;
        while (processCount <= 0) {
            processCount = Integer.parseInt(br.readLine());
            if (processCount <= 0)
                System.out.print("Invalid value entered, try again: ");
        }

        Scheduler [] schedulers = {
                new First_Come_First_Serve(),
                new Shortest_Job_First_Preemptive(),
                new RoundRobin_Preemptive(),
                new Priority_Non_Preemptive()
        };
        ArrayList<Integer> times = new ArrayList<>(processCount * 2);
        String [] entries = { "Enter Arrival Times", "Enter Burst Times" };

        int finalProcessCount = processCount;
        Arrays.stream(entries).forEach(entry -> {
            int it = 0;
            System.out.println(entry);
            while (it < finalProcessCount) {
                System.out.print((it + 1) + ". ");
                try {
                    int time = Integer.parseInt(br.readLine());
                    if (time < 0)
                        throw new NumberFormatException("Time must be positive");
                    times.add(time);
                }
                catch (Exception e) {
                    System.out.println(e.getMessage());
                    it--;
                }
                it++;
            }
        });

        Arrays.stream(schedulers).forEach(s -> {
            int half = times.size()/2;
            for (int i = 0; i < finalProcessCount; i++) {
                s.add(new Row(i + 1, times.get(i), times.get(i + half)));
            }
        });

        Arrays.stream(schedulers).forEach(s -> {
            try {
                s.process();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            System.out.println("\n" + String.join(" ", s.getClass().getName().split("_")));
            System.out.println("PID\tAT\tBT\tCT\tWT\tTAT" + (s instanceof Priority_Non_Preemptive ? "\tPriority" : ""));

            s.getRows().forEach(row -> System.out.println(
                    row.getPid() + "\t" +
                    row.getAt() + "\t" +
                    row.getBt() + "\t" +
                    row.getCt() + "\t" +
                    row.getWt() + "\t" +
                    row.getTat() + "\t" +
                    (row.getPriority() != -1 ? row.getPriority() : "")
            ));
            System.out.println("Avg WT : " + s.getAverageWaitingTime());
            System.out.println("Avg TAT: " + s.getAverageTurnAroundTime());
            System.out.println("\nGantt chart");
            List<Event> timeline = s.getTimeline();
            int tSize = timeline.size();
            StringBuilder gantt = new StringBuilder("");
            gantt
            .append("| P")
            .append(timeline.get(0).getPid())
            .append(" 0 - ");

            for (int i = 1, j = 1; i < timeline.get(tSize - 1).getEndTime(); i++) {
                if ( j < tSize && i == timeline.get(j).getStartTime()) {
                    gantt
                            .append(i)
                            .append(" | P")
                            .append(timeline.get(j++).getPid())
                            .append(" ")
                            .append(i)
                            .append(" - ");
                }
            }
            gantt
            .append(timeline.get(tSize - 1).getEndTime())
            .append(" |");

            for (int i = 0; i < gantt.length(); i++)
                System.out.print("-");
            System.out.println("\n" + gantt);
            for (int i = 0; i < gantt.length(); i++)
                System.out.print("-");
            System.out.println();
        });

        System.out.println("Algo\tAvg WT\tAvg TAT");
        Arrays.stream(schedulers).forEach(s -> System.out.println(
                String.join(" ", s.getClass().getName().codePoints().filter(Character::isUpperCase).collect(StringBuilder::new,
                        StringBuilder::appendCodePoint, StringBuilder::append)
                        .toString()) + " \t"
                + s.getAverageWaitingTime() + "\t\t" + s.getAverageTurnAroundTime()
        ));
    }
}
