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
import renderer.utils.Color;
import renderer.utils.FileLoader;

import java.io.IOException;

public class RayTracerReducer
    extends Reducer<Text, Text, Text, Text> {

    RayTracerRunner rt = new RayTracerRunner();

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        Configuration conf = context.getConfiguration();
        rt.setup(conf.get("scenePath"), FileLoader.ENV.valueOf(conf.get("ENV")));
        rt.initResampling();
    }

    @Override
    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        for (Text value: values) {
            String[] contents = value.toString().split("\t");
            String[] coord = contents[0].split(",");
            int i = Integer.parseInt(coord[0]);
            int j = Integer.parseInt(coord[1]);
            String[] valueSplits = contents[1].split(";");
            int sample = Integer.parseInt(valueSplits[0]);
            String[] RGB = valueSplits[1].split(",");
            Color color = new Color(Double.parseDouble(RGB[0]), Double.parseDouble(RGB[1]), Double.parseDouble(RGB[2]));
            rt.setSample(i, j, sample);
            rt.getCamera().setColor(i, j, color);
        }
        rt.resampling();
        rt.generateImage(context.getConfiguration().get("outPath"));
    }
}
