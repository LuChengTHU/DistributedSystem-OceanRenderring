package Utils;

import oceanFFT.HGenerator;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class Utils
{
    public static void gridHeightToTxt() throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        FSDataInputStream in = fs.open(new Path("frontend/OceanHeight/part-r-00000"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = null;
        HGenerator hGenerator = HGenerator.getInstance();
        int N = hGenerator.N;
        int T = hGenerator.T;
        String[] heightData = new String[N * T];
        while ((line = in.readLine()) != null) {
            String[] splitString = line.split("\t");
            String index = splitString[0], heights = splitString[1];
            String[] splitIndex = index.split(" ");
            int frameIndex = Integer.parseInt(splitIndex[0]), lineIndex = Integer.parseInt(splitIndex[1]);
            heightData[frameIndex * N + lineIndex] = heights;
        }
        for (int i = 0; i < T; i++) {
            if (!fs.exists(new Path("frontend/GridHeight_" + i)))
                fs.mkdirs(new Path("frontend/GridHeight_" + i));
            OutputStream out = fs.create(new Path("frontend/GridHeight_"  + i + "/gridheight.txt"));
            StringBuffer buffer = new StringBuffer();
            for (int j = 0; j < N; j++) {
                buffer.append(heightData[i * N + j]);
                buffer.append("\n");
            }
            out.write(buffer.toString().getBytes());
            out.close();
        }
    }

}
