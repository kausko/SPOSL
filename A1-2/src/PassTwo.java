import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class PassTwo {
	ArrayList<Row> SYMTAB, LITTAB;

	public PassTwo() {
		SYMTAB = new ArrayList<>();
		LITTAB = new ArrayList<>();
	}

	public static void main(String[] args) {
		PassTwo passTwo = new PassTwo();
		try {
			passTwo.processIC("IC.txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void readTables() {
		BufferedReader br;
		String line;
		try {
			br = new BufferedReader(new FileReader("SYMTAB.txt"));

			while (null != (line = br.readLine())) {
				String[] parts = line.split("\\s+");
				SYMTAB.add(new Row(parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[0])));
			}
			br.close();

			br = new BufferedReader(new FileReader("LITTAB.txt"));

			while ((line = br.readLine()) != null) {
				String[] parts = line.split("\\s+");
				LITTAB.add(new Row(parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[0])));
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void processIC(String filename) throws Exception {

		readTables();
		BufferedReader br = new BufferedReader(new FileReader(filename));
		BufferedWriter bw = new BufferedWriter(new FileWriter("PASS2.txt"));
		String line, code;

		while ((line = br.readLine()) != null) {

			String[] parts = line.split("\\s+");
			bw.write(parts[0] + "\t");

			if (parts[1].contains("AD") || parts[1].contains("DL,02")) {
				bw.write("\n");

			} else if (parts.length == 3) {
				if (parts[1].contains("DL"))
				{
					parts[1] = parts[1].replaceAll("[^0-9]", "");
					if (Integer.parseInt(parts[1]) == 1) {

						int constant = Integer.parseInt(parts[2].replaceAll("[^0-9]", ""));
						code = "00\t0\t" + String.format("%03d", constant) + "\n";
						bw.write(code);
					}

				} else if (parts[1].contains("IS")) {

					int opcode = Integer.parseInt(parts[1].replaceAll("[^0-9]", ""));

					if (opcode == 10) {
						if (parts[2].contains("S")) {

							int symIndex = Integer.parseInt(parts[2].replaceAll("[^0-9]", ""));
							code = String.format("%02d", opcode) + "\t0\t" + String.format("%03d", SYMTAB.get(symIndex - 1).getAddress()) + "\n";
							bw.write(code);

						} else if (parts[2].contains("L")) {

							int symIndex = Integer.parseInt(parts[2].replaceAll("[^0-9]", ""));
							code = String.format("%02d", opcode) + "\t0\t" + String.format("%03d", LITTAB.get(symIndex - 1).getAddress()) + "\n";
							bw.write(code);

						}
					}
				}
			} else if (parts.length == 2 && parts[1].contains("IS")) {

				int opcode = Integer.parseInt(parts[1].replaceAll("[^0-9]", ""));
				code = String.format("%02d", opcode) + "\t0\t" + String.format("%03d", 0) + "\n";
				bw.write(code);

			} else if (parts[1].contains("IS") && parts.length == 4) {

				int opcode = Integer.parseInt(parts[1].replaceAll("[^0-9]", ""));
				int regcode = Integer.parseInt(parts[2]);

				if (parts[3].contains("S")) {

					int symIndex = Integer.parseInt(parts[3].replaceAll("[^0-9]", ""));
					code = String.format("%02d", opcode) + "\t" + regcode + "\t" + String.format("%03d", SYMTAB.get(symIndex - 1).getAddress()) + "\n";
					bw.write(code);

				} else if (parts[3].contains("L")) {

					int symIndex = Integer.parseInt(parts[3].replaceAll("[^0-9]", ""));
					code = String.format("%02d", opcode) + "\t" + regcode + "\t" + String.format("%03d", LITTAB.get(symIndex - 1).getAddress()) + "\n";
					bw.write(code);
				}
			}
		}
		bw.close();
		br.close();
	}
}