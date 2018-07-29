package mapreduce;

import core.Complex;
import core.FFT;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import java.io.IOException;
import java.util.ArrayList;

public class OceanFFTMapper extends Mapper<Object, Text, Text, Text>
{
    private Text keyText = new Text();
    private Text dataText = new Text();

    public void map(Object key, Text value, Context context
    ) throws IOException, InterruptedException {

        FileSplit fileSplit = (FileSplit) context.getInputSplit() ;
        String fileName = fileSplit.getPath().getName() ;
        Integer frameIndex = Integer.valueOf(fileName.substring(fileName.indexOf('_') + 1, fileName.indexOf('.'))) ;

        String valStr = value.toString() ;
        String[] valArr = valStr.split("\t") ;
        Integer rowId = Integer.valueOf(valArr[0]) ;
        String[] dataArr = valArr[1].split(" ") ;

        ArrayList<Complex> in = new ArrayList<Complex>();
        for(String data : dataArr)
            in.add(new Complex(data)) ;

        FFT.calcFFT(in);

        for(int i = 0; i < in.size(); i ++)
        {
            keyText.set(frameIndex.toString()+' '+new Integer(i).toString()) ;
            dataText.set(in.get(i).toString()) ;
            context.write(keyText, dataText) ;
        }
    }
}
