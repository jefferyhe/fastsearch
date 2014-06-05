import java.io.*;
import java.util.*;

/**
 * Created by jeffery on 3/20/14.
 */
public class Search {
    
    static String searchPath = "processed/";

    // look up the phone number by name
    public static String search(String firstName, String lastName) throws IOException, ClassNotFoundException {
        HashMap<String, ArrayList<String>> map = readHashFile(searchPath + "hash.txt");
        String name = firstName + lastName;
        String targetFile = "";

        for (Map.Entry<String, ArrayList<String>> entry : map.entrySet()) {
            String key = entry.getKey();
            ArrayList<String> range = entry.getValue();
            if ((name.compareTo(range.get(0)) >= 0) && (name.compareTo(range.get(1)) <= 0)) {
                targetFile = key;
            }
        }
        if (targetFile.length() == 0) {
            //System.out.println("no files contains!!!");
            return null;
        }

        File target = new File(targetFile);

        return binarySearch(target, name).split(" ")[1];

    }
    
    // in place binary search which doesn't need to read file into memory
    public static String binarySearch(File file, String target) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        int lineSize = 40;
        long numberOfLines = raf.length() / lineSize;
        //System.out.println(raf.length());
        byte[] lineBuffer = new byte[lineSize];
        long bottom = 0;
        long top = numberOfLines;
        long mid;

        while (bottom <= top) {
            mid = (bottom + top) / 2;
            raf.seek(mid * lineSize);
            raf.read(lineBuffer);
            String line = new String(lineBuffer);
            String name = MergerSort.getName(line);
            int cmpr = name.compareTo(target);
            if (cmpr == 0) {
                return line;
            } else if (cmpr < 0) {
                bottom = mid + 1;
            } else {
                top = mid - 1;
            }

        }
        raf.close();
        //System.out.println("can't find!!" + target);
        return null;
    }

    // read the hashmap from the hash file
    public static HashMap<String, ArrayList<String>> readHashFile(String filePath) throws IOException, ClassNotFoundException {
        File file = new File(filePath);
        FileInputStream f = new FileInputStream(file);
        ObjectInputStream s = new ObjectInputStream(f);
        HashMap<String, ArrayList<String>> fileMap = (HashMap<String, ArrayList<String>>) s.readObject();
        s.close();
        return fileMap;
    }
    
}
