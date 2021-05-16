import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class PassOneLE {

    private BufferedReader br;
    private FileWriter MNT, MDT, KPDT, PNT, IC;
    private LinkedHashMap<String, Integer> PNTAB;
    private String line;
    private String macroName;
    private String [] parts;
    private int MDTP, KPDTP, paramNo, PP, KP, flag;

    public PassOneLE() throws IOException {
        br = new BufferedReader(new FileReader("input.asm"));
        MNT = new FileWriter("MNT.txt");
        MNT.write("N\tPP\tKP\tMDTP|KPDTP\n");
        MDT = new FileWriter("MDT.txt");
        KPDT = new FileWriter("KPDT.txt");
        PNT = new FileWriter("PNTAB.txt");
        IC = new FileWriter("IC.txt");
        PNTAB = new LinkedHashMap<>();
        macroName = null;
        MDTP = 1;
        KPDTP = 0;
        paramNo = 1;
        PP = 0;
        KP = 0;
        flag = 0;
    }

    public static void main(String[] args) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
	// write your code here
        PassOneLE passone = new PassOneLE();
        passone.parseFile();
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
        br.close();
        MNT.close();
        MDT.close();
        IC.close();
        PNT.close();
        KPDT.close();
        System.out.println("MACRO PASS-1 PROCESSING COMPLETED");
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
            }
            else {
                PP++;
                PNTAB.put(parts[i], paramNo++);
            }
        }
        MNT.write(
        parts[0] + "\t" +
            PP + "\t" +
            KP + "\t" +
            MDTP + "\t" +
            ( KP == 0 ? KPDTP : KPDTP+1) + "\n"
        );
        KPDTP += KP;
    }

    public void MEND() throws IOException {
        MDT.write(
                String.join(
                        " ",
                        Arrays.stream(line.split("\\s+")).filter(part -> part.length() > 0).toArray(String[]::new)) + "\n");
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
        for (String part : parts) {
            if (part.contains("&")) {
                Integer pt = PNTAB.get(part.replaceAll("[&,]", ""));
                MDT.write(
                        pt == null ? part : "(P," + pt + ")\t"
                );
            } else
                MDT.write(part + "\t");
        }
        MDT.write("\n");
        MDTP++;
    }
}
