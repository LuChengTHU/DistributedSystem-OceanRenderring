package physicsEngine;

import oceanFFT.OceanFFTMapper;
import oceanFFT.OceanFFTReducer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class EngineDriver {
    public static void run(Integer iterationTime) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Physics Engine");
        job.setJarByClass(EngineDriver.class);
        job.setMapperClass(OceanFFTMapper.class);
        job.setReducerClass(OceanFFTReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        System.out.println("Physics Engine Running\n") ;

        TextInputFormat.addInputPath(job, new Path("frontend/ModelData"+(iterationTime-1)));
        FileOutputFormat.setOutputPath(job, new Path("frontend/ModelData"+iterationTime));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
