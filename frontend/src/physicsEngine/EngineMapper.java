package physicsEngine;

import core.Complex;
import core.FFT;
import core.Vec3;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class EngineMapper extends Mapper<Object, Text, IntWritable, Text> {

    private IntWritable keyID = new IntWritable();
    private Text dataText = new Text();
    private HashMap<Integer, BoundingBox> boxes = new HashMap<Integer, BoundingBox>() ;

    public void setup() {
        
    }


    public void map(Object key, Text value, Context context // Calculate collisions between balls, calculate ocean grids intefere with a ball.
    ) throws IOException, InterruptedException {

        String valStr = value.toString() ;
        String[] valArr = valStr.split("\t") ;
        Ball ball = Ball.valueOf(valArr[valArr.length-1]) ;

        System.out.println(ball) ;
        keyID.set(ball.id) ;
        dataText.set(ball.toString()) ;
        context.write(keyID, dataText) ;
    }

}
