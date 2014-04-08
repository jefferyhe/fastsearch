import java.io.*;
import java.util.*;

/**
 * Created by jeffery on 3/18/14.
 */
public class MergerSort {

    public static void main(String[] args) throws IOException {

        String input_path = "input/";
        String temp_path = "temp/";
        String output_path = "processed/";

        long startTime = System.currentTimeMillis();

        SortFile.sortFiles(input_path, temp_path);
        mergeSort(temp_path, output_path);

        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("processed time:" + totalTime);
    }

    // get the number of lines in the file
    public static int getLines(File file) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(file));
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        } finally {
            is.close();
        }
    }

    public static void mergeSort(String input_path, String output_path) throws IOException {

        // PriorityQueue for merge sort
        PriorityQueue<BinaryFileBuffer> pq = new PriorityQueue<BinaryFileBuffer>(
                11, new Comparator<BinaryFileBuffer>() {
            @Override
            public int compare(BinaryFileBuffer i,
                               BinaryFileBuffer j) {
                return defaultcomparator.compare(i.peek(), j.peek());
            }
        });

        File inputFolder = new File(input_path);
        File[] listOfFiles = inputFolder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".txt");
            }
        });

        // add the first line from each file in the folder into pq
        for (File file : listOfFiles) {
            if (file.isFile()) {
                BufferedReader in = new BufferedReader(new FileReader(file));
                BinaryFileBuffer bfb = new BinaryFileBuffer(in);
                pq.add(bfb);
            }
        }

        // for output files
        int file_id = 1;
        int timesOfFiles = 1;
        int lines = getLines(listOfFiles[0]) / timesOfFiles; // divide the original file size by timesOfFiles
        int n_outfiles = listOfFiles.length * timesOfFiles; // number of output files

        ArrayList<BufferedWriter> fbws = new ArrayList<BufferedWriter>();
        for (int i = 0; i < n_outfiles; i++) {
            String output_file_path = output_path + "outData" + file_id + ".txt";
            File out_file = new File(output_file_path);   // file pathname
            BufferedWriter bw = new BufferedWriter(new FileWriter(out_file));
            fbws.add(bw);
            file_id++;
        }

        int rowcounter = 0;
        int i = 0; //index of fbws

        // file name, name range
        HashMap<String, ArrayList<String>> fileMap = new HashMap<String,  ArrayList<String>>();
        String topName = "";

        try {
            while (pq.size() > 0 && i < n_outfiles) {
                BinaryFileBuffer bfb = pq.poll();
                BufferedWriter fbw = fbws.get(i);
                String r = bfb.pop();  // one complete line of the file

                if(rowcounter == 0) {
                    //System.out.println("no write!! " + r);
                    topName = getName(r);
                }

                if (!r.equals(null)) {

                    int length = r.length();
                    if (length > 39) {
                        System.out.println("line length exceed!! at file:" + i);
                    }

                    // fix width of each line to 40 bytes
                    while (length < 39) {
                        r += " ";
                        length++;
                    }

                    fbw.write(r + '\n');
                    rowcounter++;

                    ArrayList<String> nameRange = new ArrayList<String>();
                    nameRange.add(topName);

                    // add the last line of the last file
                    if (fileMap.size() == n_outfiles - 1 && pq.size() == 1) {
                        nameRange.add(getName(r));
                        fileMap.put(output_path + "outData" + n_outfiles + ".txt", nameRange);
                    }

                    if (rowcounter >= lines) {
                        // create a new file
                        i++;
                        rowcounter = 0;
                        nameRange.add(getName(r));
                        // start from 1
                        String output_file_path = output_path + "outData" + i + ".txt";
                        fileMap.put(output_file_path, nameRange);
                    }

                }

                if (bfb.empty()) {
                    bfb.fbr.close();
                } else {
                    pq.add(bfb); // add it back
                }
            }
        } finally {
            for (BufferedWriter fbw : fbws)
                fbw.close();
            for (BinaryFileBuffer bfb : pq)
                bfb.close();
        }

        String hashFile = output_path + "hash.txt";
        writeHashFile(fileMap, hashFile);
    }

    // get the name from a complete line data
    public static String getName(String r) {
        if (r == null || r.length() == 0) {
            System.out.println("blank line!!!");
            return null;
        }
        return r.split(" ")[0];
    }

    // write a hashmap into a file
    public static void writeHashFile(HashMap map, String out_file) throws IOException {
        File file = new File(out_file);
        FileOutputStream f = new FileOutputStream(file);
        ObjectOutputStream s = new ObjectOutputStream(f);
        s.writeObject(map);
        s.close();
    }


    /**
     * default comparator between strings.
     */
    public static Comparator<String> defaultcomparator = new Comparator<String>() {
        @Override
        public int compare(String r1, String r2) {
            return r1.compareTo(r2);
        }
    };

}

/*
*  A thin wrapper on top of a BufferedReader, which always keeps the last line in memory
* */
final class BinaryFileBuffer {
    public BinaryFileBuffer(BufferedReader r) throws IOException {
        this.fbr = r;
        reload();
    }
    public void close() throws IOException {
        this.fbr.close();
    }

    public boolean empty() {
        return this.cache == null;
    }

    public String peek() {
        return this.cache;
    }

    public String pop() throws IOException {
        String answer = peek().toString();// make a copy
        reload();
        return answer;
    }

    private void reload() throws IOException {
        this.cache = this.fbr.readLine();
    }

    public BufferedReader fbr;

    private String cache;

}
