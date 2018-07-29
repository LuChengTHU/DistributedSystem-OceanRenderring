package mapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class OceanFFTDriver {
    public static void run() throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "OceanFFT");
        job.setJarByClass(OceanFFTDriver.class);
        job.setMapperClass(OceanFFTMapper.class);
        job.setReducerClass(OceanFFTReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        TextInputFormat.addInputPath(job, new Path("OceanFFT/Hdata"));
        FileOutputFormat.setOutputPath(job, new Path("OceanFFT/output"));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
