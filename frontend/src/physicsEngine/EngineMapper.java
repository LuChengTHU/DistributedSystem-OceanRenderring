package physicsEngine;

import core.Complex;
import core.Conf;
import core.FFT;
import core.Vec3;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
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
    private HashMap<Integer, BoundingBox> staticBoxes = new HashMap<Integer, BoundingBox>() ;
    private Integer frameNumber = 0 ;

    private FileSystem fs ;

    public void setup(Context context) throws IOException { // Input the BoundingBox information

        frameNumber = context.getConfiguration().getInt("frameNumber", 0) ;

        Configuration conf = context.getConfiguration() ;
        fs = FileSystem.get(conf);

        Integer reducerCount = 0 ;

        while(true) {
            Path filePath = new Path("frontend/ModelData/" + frameNumber + String.format("/part-r-%05d", reducerCount)) ;
            if(fs.exists(filePath) == false) break ;
            FSDataInputStream dis = fs.open(filePath);

            while(true)
            {
                String line = dis.readLine() ;
                if(line == null || line.length() == 0) break ;
                String[] lineArr = line.split("\t") ;
                line = lineArr[lineArr.length-1] ;
                Ball ball = Ball.valueOf(line) ;
                boxes.put(ball.id, BoundingBox.valueOf(ball, Conf.timeSlide)) ;
                staticBoxes.put(ball.id, BoundingBox.valueOf(ball, 0f)) ;
            }
            reducerCount = reducerCount+1 ;
        }
        System.out.println("Finished Mapper setup.") ;
    }


    public void map(Object key, Text value, Context context // Calculate collisions between balls, calculate ocean grids intefere with a ball.
    ) throws IOException, InterruptedException {

        String valStr = value.toString() ;
        String[] valArr = valStr.split("\t") ;
        String rightStr = valArr[valArr.length-1] ;
        if(rightStr.charAt(0) == 'B') // this is a ball
        {
            Ball ball = Ball.valueOf(rightStr);
            System.out.println(ball);

            dataText.set(ball.toString());
            BoundingBox thisBox = boxes.get(ball.id);
            for (Integer i : boxes.keySet())
                if (boxes.get(i).Intersect(thisBox)) {
                    keyID.set(i);
                    System.out.printf(">>>%d %d\n", keyID.get(), i) ;
                    context.write(keyID, dataText);
                }
        }
        else // this is a row of ocean surface
        {
            Integer rowId = Integer.valueOf(valArr[0]) ;
            Float up = rowId * Conf.lenXY / Conf.resolution ;
            Float dn = (rowId+1) * Conf.lenXY / Conf.resolution ;
            for (Integer i : staticBoxes.keySet())
            {
                ArrayList<Integer> startIndex = new ArrayList<Integer>() ;
                Integer current = 0 ;
                startIndex.add(0);
                String[] rightArr = rightStr.split(" ") ;
                for(int j = 0; j < rightArr.length; j ++)
                {
                    current += rightArr[j].length()+1 ;
                    startIndex.add(current) ;
                }

                BoundingBox box = staticBoxes.get(i) ;

                if(box.p0.getX() > dn || box.p1.getX() < up) continue ;
                if(box.p0.getZ() > Conf.seaLevel) continue ;
                Integer lt = Math.max(0, (int)Math.floor(box.p0.getY() / Conf.lenXY * Conf.resolution)) ;
                Integer rt = Math.min(Conf.resolution-1, (int)Math.floor(box.p1.getY() / Conf.lenXY * Conf.resolution)) ;

                String dataStr = String.format("%d %d ", rowId, lt) ;
                dataStr = dataStr+rightStr.substring(startIndex.get(lt), startIndex.get(rt)-1) ;
                dataText.set(dataStr) ;
                keyID.set(i) ;
                context.write(keyID, dataText) ;
            }
        }
    }

}
