package main;

import Utils.Utils;
import oceanFFT.H;
import oceanFFT.HGenerator;
import oceanFFT.OceanFFTDriver;
import physicsEngine.EngineDriver;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.*;

public class main {

    public static void main(String[] args) throws Exception {
        //Runtime.getRuntime().exec("rm -r -f frontend/OceanHeight") ;
        //Runtime.getRuntime().exec("rm -r -f frontend/Hdata") ;
        //HGenerator hGenerator = HGenerator.getInstance();
        System.out.println("Start Generate Init Data");
        //hGenerator.generateInitialData();
        System.out.println("Start FFT");
        //OceanFFTDriver.run();
        System.out.println("Start convert to obj");
        Utils.gridHeightToObj();

        //for(int iter = 0; iter < T; iter ++)
        //    EngineDriver.run(iter+1);
        /*ArrayList<Complex> in = new ArrayList<Complex>() ;
        for (int i = 0; i < (1<<3); i ++)
            in.add(new Complex(i+1, (i+1)*2)) ;
        FFT.calcFFT(in);
        for(int i = 0; i < (1<<3); i ++)
            System.out.println(in.get(i));*/
    }

}
