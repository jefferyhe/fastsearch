import java.io.*;
import java.util.Map;
import java.util.TreeMap;

public class SortFile {

    public static void sortFiles(String input_path, String output_path) throws IOException {

        File inputFolder = new File(input_path);
        File[] listOfFiles = inputFolder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".txt");
            }
        });

        int len = listOfFiles.length;
        File[] sortedFiles = new File[len];

        for (int i = 1; i <= len; i++) {
            sortedFiles[i-1] = new File(output_path + "temp" + i + ".txt");
        }

        for (int i = 0; i < len; i++) {

            BufferedReader reader = new BufferedReader(new FileReader(listOfFiles[i]));
            Map<String, String> map = new TreeMap<String, String>();
            String line = "";
            while((line = reader.readLine()) != null && !empty(line)){
                map.put(getField(line),removeTab(line));
            }
            reader.close();

            FileWriter writer = new FileWriter(sortedFiles[i]);
            for(String val : map.values()){
                writer.write(val);
                writer.write('\n');
            }
            writer.close();
        }

    }

    // check if a line only has spaces
    private static boolean empty(String s) {
        if (s == null || s.length() == 0) return true;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != ' ') return false;
        }
        return true;
    }

    private static String removeTab(String s) {
        String[] items = s.split("\t", -1);
        StringBuilder sb = new StringBuilder();

        sb.append(items[0]);
        sb.append(items[1]);
        sb.append(" ");
        sb.append(items[2]);
        return sb.toString();
    }
    private static String getField(String line) {
        String res = line.split("\t", -1)[0] + " " + line.split("\t", -1)[1];
        return res; //extract value that to sort on
    }
}