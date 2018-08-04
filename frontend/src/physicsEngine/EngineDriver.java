package physicsEngine;

import core.Conf;
import oceanFFT.HGenerator;
import oceanFFT.OceanFFTMapper;
import oceanFFT.OceanFFTReducer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class EngineDriver {
    public static void run(Integer iterationTime) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        conf.setFloat("lenXY", Conf.lenXY);
        conf.setFloat("lenZ", Conf.lenZ);
        conf.setFloat("seaLevel", Conf.seaLevel);
        conf.setInt("oceanRes", Conf.resolution);
        conf.setFloat("timeSlide", Conf.timeSlide);
        conf.setFloat("currentTime", (iterationTime-1)*Conf.timeSlide);
        conf.setInt("frameNumber", (iterationTime-1));
        conf.setQuietMode(true);
        Job job = Job.getInstance(conf, "Physics Engine");
        job.setJarByClass(EngineDriver.class);
        job.setMapperClass(EngineMapper.class);
        job.setReducerClass(EngineReducer.class);
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(Text.class);
        System.out.println("Physics Engine Running\n") ;

        TextInputFormat.addInputPath(job, new Path("frontend/OceanTxt/"+(iterationTime-1)));
        TextInputFormat.addInputPath(job, new Path("frontend/ModelData/"+(iterationTime-1)));
        FileOutputFormat.setOutputPath(job, new Path("frontend/ModelData/"+iterationTime));

        job.waitForCompletion(true);
    }
}
