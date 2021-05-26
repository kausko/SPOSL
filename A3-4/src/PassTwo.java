import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

class MNTEntry {
	String name;
	int PP, KP, MDTP, KPDTP;
	public MNTEntry(String name, int PP, int KP, int MDTP, int KPDTP) {
		this.name = name;
		this.PP = PP;
		this.KP = KP;
		this.MDTP = MDTP;
		this.KPDTP = KPDTP;
	}
}

public class PassTwo {
	public static void main(String[] args) throws IOException {
		BufferedReader IBR = new BufferedReader(new FileReader("IC.txt")),
				MDBR = new BufferedReader(new FileReader("MDT.txt")),
				MNBR = new BufferedReader(new FileReader("MNT.txt")),
				KBR = new BufferedReader(new FileReader("KPDTAB.txt"));

		HashMap<String, MNTEntry> MNT = new HashMap<>();
		HashMap<Integer, String> APT = new HashMap<>();
		HashMap<String, Integer> RAPT = new HashMap<>();

		ArrayList<String> MDT = new ArrayList<>(), KPDT = new ArrayList<>();

		FileWriter fw = new FileWriter("P2OP.txt");
		int pp, kp, mdtp, kpdtp, pno;
		String line;

		while ((line = MDBR.readLine()) != null) {
			MDT.add(line);
		}
		while ((line = KBR.readLine()) != null) {
			KPDT.add(line);
		}
		MNBR.readLine();
		while ((line = MNBR.readLine()) != null) {
			String [] parts = line.split("\t");
			MNT.put(
					parts[0],
					new MNTEntry(
							parts[0],
							Integer.parseInt(parts[1]),
							Integer.parseInt(parts[2]),
							Integer.parseInt(parts[3]),
							Integer.parseInt(parts[4])
					)
			);
		}
		while ((line = IBR.readLine()) != null) {
			String [] parts = line.trim().split("\\s+");
			if (!MNT.containsKey(parts[0])) {
				fw.write(line + "\n");
				continue;
			}
			MNTEntry ent = MNT.get(parts[0]);
			pp = ent.PP;
			kp = ent.KP;
			kpdtp = ent.KPDTP;
			mdtp = ent.MDTP;
			pno = 1;
			for (int i = 0; i < pp; i++) {
				parts[pno] = parts[pno].replace(",","");
				APT.put(pno, parts[pno]);
				RAPT.put(parts[pno], pno);
				pno++;
			}
			int j = kpdtp - 1;
			for (int i = 0; i < kp; i++) {
				String [] temp = KPDT.get(j).split("\t");
				APT.put(pno, temp[1]);
				RAPT.put(temp[0], pno);
				j++;
				pno++;
			}
			for (int i = pp+1; i < parts.length; i++) {
				parts[i] = parts[i].replace(",","");
				String [] temp = parts[i].split("=");
				String name = temp[0].replaceAll("&","");
				APT.put(RAPT.get(name), temp[1]);
			}
			int i = mdtp - 1;
			while (!MDT.get(i).equalsIgnoreCase("MEND")) {
				String [] temp = MDT.get(i).split("\\s+");
				fw.write("\t+");
				for (int k = 0; k < temp.length; k++) {
					if (temp[k].contains("(P,")) {
						temp[k] = temp[k].replaceAll("[^0-9]", "");
						fw.write(APT.get(Integer.parseInt(temp[k])).replaceFirst("\\+","") + "\t");
					}
					else {
						fw.write(temp[k].replaceFirst("\\+", "") + "\t");
					}
				}
				fw.write("\n");
				i++;
			}
			APT.clear();
			RAPT.clear();
		}
		fw.close();
		IBR.close();
		MNBR.close();
		MDBR.close();
		KBR.close();
	}
}
