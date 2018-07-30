package mapreduce;

import core.Complex;
import core.FFT;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.ArrayList;

public class OceanFFTReducer extends Reducer<Text,Text,Text,Text> {
    private Text result = new Text();
    public void reduce(Text key, Iterable<Text> values,
                       Context context
    ) throws IOException, InterruptedException {
        String invList = "";

        ArrayList<Complex> in = new ArrayList<Complex>() ;
        for (Text val : values)
            in.add(new Complex(val.toString())) ;
        FFT.calcFFT(in);

        StringBuffer sBuf = new StringBuffer() ;
        for(Complex comp : in)
            sBuf.append(Float.valueOf(comp.getNorm()).toString()+" ") ;
        result.set(sBuf.toString()) ;
        context.write(key, result);
    }
}

