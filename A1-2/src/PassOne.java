import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PassOne {
	int LC;
	int LTP = 0, PTP = 0;
	int SI = 0, LI = 0;
	LinkedHashMap<String, Row> SYMTAB;
	ArrayList<Row> LITTAB;
	ArrayList<Integer> POOLTAB;

	public PassOne() {
		SYMTAB = new LinkedHashMap<>();
		LITTAB = new ArrayList<>();
		POOLTAB = new ArrayList<>();
		LC = 0;
		POOLTAB.add(0);
	}

	public static void main(String[] args) throws IOException {
		PassOne passOne = new PassOne();
		passOne.parseFile();
	}

	private void DirectiveProcessor(BufferedWriter bw) throws IOException {
		String code;
		int ptr = POOLTAB.get(PTP);
		for (int j = ptr; j < LTP; j++) {
			LITTAB.set(j, new Row(LITTAB.get(j).getSymbol(), LC));
			code = LC + "\t(DL,01)\t(C," + LITTAB.get(j).symbol + ")";
			LC++;
			bw.write(code + "\n");
		}
		PTP++;
		POOLTAB.add(LTP);
	}

	void LITout() throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter("LITTAB.txt"));
		System.out.println("\nLITERAL TABLE");
		for (int i = 0; i < LITTAB.size(); i++) {
			Row row = LITTAB.get(i);
			System.out.println(i + 1 + "\t" + row.getSymbol() + "\t" + row.getAddress());
			bw.write(i + 1 + "\t" + row.getSymbol() + "\t" + row.getAddress() + "\n");
		}
		bw.close();
	}

	void POOLout() throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter("POOLTAB.txt"));
		System.out.println("\nPOOL TABLE");
		for (int i = 1; i < POOLTAB.size(); i++) {
			System.out.println(i + "\t" + POOLTAB.get(i));
			bw.write(i + "\t" + POOLTAB.get(i) + "\n");
		}
		bw.close();
	}

	void SYMout() throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter("SYMTAB.txt"));

		java.util.Iterator<String> iterator = SYMTAB.keySet().iterator();
		System.out.println("SYMBOL TABLE");
		while (iterator.hasNext()) {
			String key = iterator.next();
			Row value = SYMTAB.get(key);

			System.out.println(value.getIndex() + "\t" + value.getSymbol() + "\t" + value.getAddress());
			bw.write(value.getIndex() + "\t" + value.getSymbol() + "\t" + value.getAddress() + "\n");
		}
		bw.close();
	}

	public int expr(@NotNull String str) {
		int temp;
		if (str.contains("+")) {
			String[] splits = str.split("\\+");
			temp = SYMTAB.get(splits[0]).getAddress() + Integer.parseInt(splits[1]);
		} else if (str.contains("-")) {
			String[] splits = str.split("-");
			temp = SYMTAB.get(splits[0]).getAddress() - (Integer.parseInt(splits[1]));
		} else {
			temp = Integer.parseInt(str);
		}
		return temp;
	}

	public void parseFile() throws IOException {
		String inputLine, code;
		BufferedReader BR = new BufferedReader(new FileReader("input.asm"));
		BufferedWriter BW = new BufferedWriter(new FileWriter("IC.txt"));
		Table lookup = new Table();
		while ((inputLine = BR.readLine()) != null) {

			String[] split = inputLine.split("\\s+");
			if (!split[0].isEmpty())
			{
				if (SYMTAB.containsKey(split[0]))
					SYMTAB.put(split[0], new Row(split[0], LC, SYMTAB.get(split[0]).getIndex()));
				else
					SYMTAB.put(split[0], new Row(split[0], LC, ++SI));
			}

			if (split[1].equals("LTORG")) {
				code = "\t(AD,05)";
				BW.write(code + "\n");
				DirectiveProcessor(BW);
			}
			if (split[1].equals("START")) {
				LC = expr(split[2]);
				code = "\t(AD,01)\t(C," + LC + ")";
				BW.write(code + "\n");
			} else if (split[1].equals("ORIGIN")) {
				LC = expr(split[2]);
				String[] splits = split[2].split("\\+");
				code = "\t(AD,03)\t(S," + SYMTAB.get(splits[0]).getIndex() + ")+" + Integer.parseInt(splits[1]);
				BW.write(code + "\n");
			}


			if (split[1].equals("EQU")) {
				int loc = expr(split[2]);

				if (split[2].contains("+")) {
					String[] splits = split[2].split("\\+");
					code = "\t(AD,04)\t(S," + SYMTAB.get(splits[0]).getIndex() + ")+" + Integer.parseInt(splits[1]);

				} else if (split[2].contains("-")) {
					String[] splits = split[2].split("-");
					code = "\t(AD,04)\t(S," + SYMTAB.get(splits[0]).getIndex() + ")-" + Integer.parseInt(splits[1]);
				} else {
					code = "\t(AD,04)\t(C," + Integer.parseInt(split[2] + ")");
				}
				BW.write(code + "\n");
				if (SYMTAB.containsKey(split[0]))
					SYMTAB.put(split[0], new Row(split[0], loc, SYMTAB.get(split[0]).getIndex()));
				else
					SYMTAB.put(split[0], new Row(split[0], loc, ++SI));
			}

			if (split[1].equals("DC")) {
				int constant = Integer.parseInt(split[2].replace("'", ""));
				code = LC + "\t(DL,01)\t(C," + constant + ")";
				BW.write(code + "\n");
				LC++;
			} else if (split[1].equals("DS")) {

				int size = Integer.parseInt(split[2].replace("'", ""));

				code = LC + "\t(DL,02)\t(C," + size + ")";
				BW.write(code + "\n");
				LC = LC + size;
			}
			if (lookup.getType(split[1]).equals("IS")) {
				int num = lookup.getCode(split[1]);
				code = LC + "\t(IS," + (num < 10 ? "0" : "") + num + ")\t";
				int j = 2;
				StringBuilder code2 = new StringBuilder();
				while (j < split.length) {
					split[j] = split[j].replace(",", "");
					if (lookup.getType(split[j]).equals("RG")) {
						code2.append(lookup.getCode(split[j])).append("\t");
					} else {
						if (split[j].contains("=")) {
							split[j] = split[j].replace("=", "").replace("'", "");
							int finalJ = j;
							if (
									LITTAB.stream().noneMatch(row ->
											row.getSymbol().equals(split[finalJ]) &&
											row.getIndex() <= POOLTAB.get(POOLTAB.size()-1)
									)
							) {
								LITTAB.add(new Row(split[j], -1, LI++));
								LTP++;
							}
							code2.append("(L,").append(LI).append(")");
						} else if (SYMTAB.containsKey(split[j])) {
							int ind = SYMTAB.get(split[j]).getIndex();
							code2.append("(S,").append(ind).append(")");
						} else {
							SYMTAB.put(split[j], new Row(split[j], -1, ++SI));
							int ind = SYMTAB.get(split[j]).getIndex();
							code2.append("(S,").append(ind).append(")");
						}
					}
					j++;
				}
				LC++;
				code = code + code2;
				BW.write(code + "\n");
			}

			if (split[1].equals("END")) {
				code = "\t(AD,02)";
				BW.write(code + "\n");
				DirectiveProcessor(BW);
			}

		}
		BW.close();
		SYMout();
		LITout();
		POOLout();
	}
}