package main;

import core.Conf;
import oceanFFT.H;
import oceanFFT.HGenerator;
import oceanFFT.OceanFFTDriver;
import physicsEngine.EngineDriver;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.*;

public class main {

    private static void generateInitialData() throws IOException {
        HGenerator hGenerator = HGenerator.getInstance();
        int N = hGenerator.N;
        H h0k = new H(N), h0minusk = new H(N);
        hGenerator.generateH0k(h0k, h0minusk);
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        for (int t = 0; t < Conf.totalFrame; t++) {
            H hkt = new H(N);
            hGenerator.generateHkt(h0k, h0minusk, t* Conf.timeSlide, hkt);
            OutputStream out = fs.create(new Path("frontend/Hdata/" + "frame_"+ (t + 1) + ".txt"));
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < N; i++) {
                buffer.append(String.valueOf(i));
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
            out.write(buffer.toString().getBytes());
            out.close();

            if(t == 0)
                for(int i = 0; i < N; i ++)
                    System.out.println(hkt.get(i)) ;
        }

    }

    public static void main(String[] args) throws Exception {
        //Integer rowId = Integer.valueOf("0") ;
        //System.out.println(rowId);
        //Runtime.getRuntime().exec("rm -r -f frontend/OceanHeight") ;
        //Runtime.getRuntime().exec("rm -r -f frontend/Hdata") ;
        //generateInitialData();
        //OceanFFTDriver.run();

        //EngineDriver.run(1);

        for(Integer iter = 0; iter < Conf.totalFrame; iter ++)
            Runtime.getRuntime().exec("rm -r -f frontend/ModelData/" + (iter + 1));

        for(Integer iter = 0; iter < Conf.totalFrame; iter ++) {
            System.out.printf("Current frame: %d\n", iter) ;
            EngineDriver.run(iter + 1);
        }
        /*ArrayList<Complex> in = new ArrayList<Complex>() ;
        for (int i = 0; i < (1<<3); i ++)
            in.add(new Complex(i+1, (i+1)*2)) ;
        FFT.calcFFT(in);
        for(int i = 0; i < (1<<3); i ++)
            System.out.println(in.get(i));*/
    }

}
