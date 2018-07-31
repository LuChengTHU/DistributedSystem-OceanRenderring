package renderer.tracer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import renderer.utils.FileLoader;

import java.io.IOException;


public class RayTracerMapper
    extends Mapper<Object, Text, Text, Text> {

    RayTracerRunner rt = new RayTracerRunner();

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        Configuration conf = context.getConfiguration();
        rt.setup(conf.get("scenePath"), FileLoader.ENV.valueOf(conf.get("ENV")));
    }

    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        int i = Integer.parseInt(value.toString().split(",")[0]);
        int j = Integer.parseInt(value.toString().split(",")[1]);
        rt.sampling(i, j);
        context.write(new Text("FINAL"),
                new Text(i + "," + j + "\t" + rt.getSample() + ";" + rt.getPixelColor()));
    }
}
