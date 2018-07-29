package mapreduce;

import core.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.FileWriter;
import java.io.IOException;

public class main {

    public static int T = 10;

    private static void generateInitialData() throws IOException {
        HGenerator hGenerator = HGenerator.getInstance();
        int N = hGenerator.N;
        H h0k = new H(N), h0minusk = new H(N);
        hGenerator.generateH0k(h0k, h0minusk);
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        for (int t = 0; t < T; t++) {
            H hkt = new H(N);
            hGenerator.generateHkt(h0k, h0minusk, t, hkt);
            FSDataOutputStream out = fs.create(new Path("OceanFFT/Hdata/" + "frame_"+ t + ".txt"));
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < N; i++) {
                buffer.append(i);
                buffer.append("\t");
                for (int j = 0; j < N; j++) {
                    if (j > 0)
                        buffer.append(" ");
                    buffer.append(hkt.get(i * N + j).toString());
                    //System.out.println("i="+i+" j="+j+":"+hkt[i*N+j].getReal() + " i"+hkt[i*N+j].getIm());
                }
                buffer.append("\n");
                //System.out.println(buffer.toString());
            }
            out.writeChars(buffer.toString());
            out.close();
        }
    }

    public static void main(String[] args) throws Exception {
        Integer rowId = Integer.valueOf("0") ;
        System.out.println(rowId);
        //generateInitialData();
        //OceanFFTDriver.run();
    }

}
