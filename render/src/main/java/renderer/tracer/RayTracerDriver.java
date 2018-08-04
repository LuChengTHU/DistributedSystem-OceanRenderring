package renderer.tracer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import renderer.utils.FileLoader;

public class RayTracerDriver {

    public static final FileLoader.ENV rtEnv = FileLoader.ENV.NATIVE;

    public void run() throws Exception {
        Configuration conf = new Configuration();
        conf.set("scenePath", "scene.txt");
        conf.set("outPath", "pic.jpg");
        conf.set("ENV", rtEnv.toString());
        Job job = Job.getInstance(conf, "RayTracer");
        job.setJarByClass(RayTracerDriver.class);
        job.setMapperClass(RayTracerMapper.class);
        job.setReducerClass(RayTracerReducer.class);
        job.setNumReduceTasks(1);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputFormatClass(NullOutputFormat.class);
        FileInputFormat.addInputPath(job, new Path("input/"));
        FileOutputFormat.setOutputPath(job, new Path("output/"));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
