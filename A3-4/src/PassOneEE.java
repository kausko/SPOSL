import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

class MacroNameRow {
	public int PP, KP, MDTP, KPDTP;
	public MacroNameRow(int PP, int KP, int MDTP, int KPDTP) {
		this.PP = PP;
		this.KP = KP;
		this.MDTP = MDTP;
		this.KPDTP = KPDTP;
	}
}

public class PassOneEE {
	private BufferedReader br;
	private FileWriter MNT, MDT, KPDT, PNT, IC, APT;
	private LinkedHashMap<String, Integer> PNTAB;
	private LinkedHashMap<String, MacroNameRow> MNTAB;
	private LinkedHashMap<String, ArrayList<String>> MDTAB;
	private LinkedHashMap<String, String> KPDTAB;
	private String line;
	private String macroName;
	private String [] parts;
	private int MDTP, KPDTP, paramNo, PP, KP, flag;


	public PassOneEE() throws IOException {
		br = new BufferedReader(new FileReader("input.asm"));
		MNT = new FileWriter("MNT.txt");
		MNT.write("N\tPP\tKP\tMDTP|KPDTP\n");
		MDT = new FileWriter("MDT.txt");
		KPDT = new FileWriter("KPDTAB.txt");
		PNT = new FileWriter("PNTAB.txt");
		APT = new FileWriter("APTAB.txt");
		IC = new FileWriter("IC.txt");
		MNTAB = new LinkedHashMap<>();
		PNTAB = new LinkedHashMap<>();
		MDTAB = new LinkedHashMap<>();
		KPDTAB = new LinkedHashMap<>();
		macroName = null;
		MDTP = 1;
		KPDTP = 0;
		paramNo = 1;
		PP = 0;
		KP = 0;
		flag = 0;
	}

	public static void main(String[] args) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		PassOneEE passOneEE = new PassOneEE();
		passOneEE.parseFile();
	}

	public void parseFile() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		while ((line = br.readLine()) != null) {
			parts = Arrays.stream(line.split("\\s+")).filter(part -> part.length() > 0).toArray(String[]::new);
			if (parts[0].matches("MACRO|MEND")) {
				getClass().getMethod(parts[0]).invoke(this);
			}
			else if (flag == 1) {
				handleFlag();
			}
			else {
				IC.write(line + "\n");
			}
		}
		for (ArrayList<String> MDTEntry : MDTAB.values()) {
			for (String entry : MDTEntry) {
				MDT.write(entry + "\n");
			}
		}
		br.close();
		MNT.close();
		MDT.close();
		IC.close();
		PNT.close();
		KPDT.close();
		APT.close();
		System.out.println("MACRO PASS-1 EARLY EXPANSION PROCESSING COMPLETED");
	}

	public void MACRO() throws IOException {
		flag = 1;
		line = br.readLine();
		parts = Arrays.stream(line.split("\\s+")).filter(part -> part.length() > 0).toArray(String[]::new);
		macroName = parts[0];
		for (int i = 1; i < parts.length; i++) {
			parts[i] = parts[i].replaceAll("[&,]", "");
			if (parts[i].contains("=")) {
				KP++;
				String [] keywordParam = parts[i].split("=");
				PNTAB.put(keywordParam[0], paramNo++);
				KPDT.write(
						keywordParam[0] +
								"\t" +
								( keywordParam.length == 2 ? keywordParam[1] : "-" ) +
								"\n"
				);
				KPDTAB.put(keywordParam[0], ( keywordParam.length == 2 ? keywordParam[1] : "-" ));
			}
			else {
				PP++;
				PNTAB.put(parts[i], paramNo++);
			}
		}
		int MNTKPDTP = ( KP == 0 ? KPDTP : KPDTP+1);
		MDTAB.put(macroName, new ArrayList<>());
		MNTAB.put(parts[0], new MacroNameRow(PP, KP, MDTP, MNTKPDTP));
		MNT.write(
				parts[0] + "\t" +
						PP + "\t" +
						KP + "\t" +
						MDTP + "\t" +
						MNTKPDTP + "\n"
		);
		KPDTP += KP;
	}

	public void MEND() throws IOException {
		ArrayList<String> temp = MDTAB.get(macroName);
		temp.add(
				String.join(
						" ",
						Arrays.stream(line.split("\\s+")).filter(part -> part.length() > 0).toArray(String[]::new)
				)
		);
		MDTAB.put(macroName, temp);
		flag = KP = PP = 0;
		MDTP++;
		paramNo = 1;
		PNT.write(macroName + ":\t");
		for (String key : PNTAB.keySet()) {
			PNT.write(key + "\t");
		}
		PNT.write("\n");
		PNTAB.clear();
	}

	public void handleFlag() throws IOException {
		StringBuilder MDTLine = new StringBuilder("");
		ArrayList<String> temp = MDTAB.get(macroName);
		for (String part : parts) {
			if (MDTAB.containsKey(part)) {
				ArrayList<String> definition = MDTAB.get(part);
				APT.write(part + ":\t");
				for (int i = 1; i < parts.length; i++) {
					if (parts[i].contains("&")) {
						if (parts[i].contains("="))
							APT.write(parts[i].split("=")[1] + "\t");
						else {
							Integer pt = PNTAB.get(parts[i].replaceAll("[&,]", ""));
							APT.write("(P," + pt + ")\t");
						}
					}
					else
						APT.write(parts[i] + "\t");
				}
				APT.write("\n");
				for (String defLine : definition) {
					if (defLine.contains("MEND"))
						break;
					MDTLine.append("+");
					String [] defLineParts = defLine.split("\t");
					MDTLine.append(defLineParts[0]);
					for (int i = 1; i < defLineParts.length; i++) {
						String s = defLineParts[i];
						int argIndex = Integer.parseInt(s.substring(s.indexOf(",") + 1, s.indexOf(")")));
						String arg = "";
						try {
							arg = parts[argIndex].replaceAll(",", "");
						}
						catch (ArrayIndexOutOfBoundsException ignored) {
							int kIndex = MNTAB.get(part).KPDTP - 1;
							arg = KPDTAB.entrySet().toArray()[kIndex].toString().split("=")[1];
						}
						if (arg.contains("&")) {
							if (arg.contains("="))
								arg = arg.split("=")[1];
							else {
								Integer pt = PNTAB.get(arg.replaceAll("[&,]", ""));
								arg = "(P," + pt + ")\t";
							}
						}
						MDTLine.append("\t").append(arg);
					}
					temp.add(MDTLine.toString());
					MDTLine.setLength(0);
					MDTP++;
				}
				MDTAB.put(macroName, temp);
				return;
			}
			if (part.contains("&")) {
				Integer pt = PNTAB.get(part.replaceAll("[&,]", ""));
				if (pt == null) {
					MDTLine.append(part);
				}
				else
					MDTLine.append("(P,").append(pt).append(")");
			} else
				MDTLine.append(part);
			MDTLine.append("\t");
		}
		temp.add(MDTLine.toString());
		MDTAB.put(macroName, temp);
		MDTP++;
	}
}
