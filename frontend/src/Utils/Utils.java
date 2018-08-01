package Utils;

import core.Conf;
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
        int T = Conf.totalFrame;
        int N = hGenerator.N;
        String[] heightData = new String[T * N];
        while ((line = reader.readLine()) != null) {
            String[] splitString = line.split("\t");
            String index = splitString[0], heights = splitString[1];
            String[] splitIndex = index.split(" ");
            int frameIndex = Integer.parseInt(splitIndex[0]), lineIndex = Integer.parseInt(splitIndex[1]);
            heightData[frameIndex * N + lineIndex] = heights;
        }
        for (int i = 0; i < T; i++) {
            if (!fs.exists(new Path("frontend/GridHeight")))
                fs.mkdirs(new Path("frontend/GridHeight"));
            if (!fs.exists(new Path("frontend/GridHeight/" + i)))
                fs.mkdirs(new Path("frontend/GridHeight/" + i));
            OutputStream out = fs.create(new Path("frontend/GridHeight/" + i + "/gridheight.txt"));
            StringBuffer buffer = new StringBuffer();
            for (int j = 0; j < N; j++) {
                buffer.append(heightData[i * N + j]);
                buffer.append("\n");
            }
            out.write(buffer.toString().getBytes());
            out.close();
        }
    }

    public static void gridHeightToObj() throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        FSDataInputStream in = fs.open(new Path("frontend/OceanHeight/part-r-00000"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = null;
        HGenerator hGenerator = HGenerator.getInstance();
        int N = hGenerator.N;
        int T = Conf.totalFrame;
        float[] heightData = new float[T * N * N];
        while ((line = reader.readLine()) != null) {
            String[] splitString = line.split("\t");
            String index = splitString[0], heightLine = splitString[1];
            String[] splitIndex = index.split(" ");
            int frameIndex = Integer.parseInt(splitIndex[0]), lineIndex = Integer.parseInt(splitIndex[1]);
            String[] heights = heightLine.split(" ");
            for (int i = 0; i < heights.length; i++) {
                heightData[frameIndex * N * N + lineIndex * N + i] = Float.parseFloat(heights[i]);
            }
        }
        int L = hGenerator.L;
        float dx = (L + 0.0f) / N;
        for (int i = 0; i < T; i++) {
            if (!fs.exists(new Path("frontend/OceanObj/" + i)))
                fs.mkdirs(new Path("frontend/OceanObj/" + i));
            OutputStream out = fs.create(new Path("frontend/OceanObj/" + i + "/ocean.obj"));
            StringBuffer buffer = new StringBuffer();
            for (int j = 0; j < N; j++) {
                for (int k = 0; k < N; k++) {
                    buffer.append("v ");
                    buffer.append(j * dx);
                    buffer.append(" ");
                    buffer.append(k * dx);
                    buffer.append(" ");
                    buffer.append(heightData[i * N * N + j * N + k]);
                    buffer.append("\n");
                }
            }
            for (int j = 0; j < N - 1; j++) {
                for (int k = 1; k < N; k++) {
                    buffer.append("f ");
                    buffer.append(j * N + k);
                    buffer.append(" ");
                    buffer.append(j * N + k + 1);
                    buffer.append(" ");
                    buffer.append((j + 1) * N + k);
                    buffer.append("\n");

                    buffer.append("f ");
                    buffer.append(j * N + k + 1);
                    buffer.append(" ");
                    buffer.append((j + 1) * N + k);
                    buffer.append(" ");
                    buffer.append((j + 1) * N + k + 1);
                    buffer.append("\n");
                }
            }

            out.write(buffer.toString().getBytes());
            out.close();
        }
    }

}
