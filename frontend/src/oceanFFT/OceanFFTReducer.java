package oceanFFT;

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
        ArrayList<String> valArr = new ArrayList<String>() ;
        for (Text val : values)
        {
            valArr.add(val.toString()) ;
            in.add(new Complex(0, 0)) ;
        }

        for(String val : valArr)
        {
            Integer index = Integer.valueOf(val.split(" ")[0]) ;
            Complex comp = new Complex(val.split(" ")[1]) ;
            in.set(index, comp) ;
        }
        FFT.calcFFT(in);

        StringBuffer sBuf = new StringBuffer() ;
        for(Complex comp : in)
            sBuf.append(Float.valueOf(comp.getNorm()).toString()+" ") ;
        result.set(sBuf.toString()) ;
        context.write(key, result);
    }
}

