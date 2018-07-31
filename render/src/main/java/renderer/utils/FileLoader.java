package renderer.utils;

import java.io.*;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import renderer.objects.Plane;
import renderer.objects.Primitive;
import renderer.objects.Sphere;
import renderer.scene.AreaLight;
import renderer.scene.Light;
import renderer.scene.Scene;

public class FileLoader {

    public static enum ENV {
        NATIVE,
        HDFS
    }

    private BufferedReader br = null;

    public FileLoader(String filePath, ENV env) throws IOException {
        switch (env) {
            case NATIVE:
                br = new BufferedReader(new FileReader(filePath));
                break;
            case HDFS:
                Configuration conf = new Configuration();
                FileSystem fs = FileSystem.get(conf);
                Path path = new Path(filePath);
                br = new BufferedReader(new InputStreamReader(fs.open(path)));
                break;
        }
    }

    public void parseScene(Scene scene) throws IOException {
        String str = null;
        while ((str = br.readLine()) != null) {
            Primitive new_primitive = null;
            Light new_light = null;
            StringTokenizer tk = new StringTokenizer(str);
            String var = "";
            if (tk.hasMoreTokens()) {
                var = tk.nextToken();
            }
            if (var.equals("primitive")) {
                String type = tk.nextToken();
                if (type.equals("sphere")) new_primitive = new Sphere();
                if (type.equals("plane")) new_primitive = new Plane();
                scene.addPrimitive(new_primitive);
            } else if (var.equals("light")) {
                String type = tk.nextToken();
                if (type.equals("area")) new_light = new AreaLight();
                scene.addLight(new_light);
            } else if (!var.equals("background") && !var.equals("camera")) {
                continue;
            }

            String order = null;
            while ((order = br.readLine()) != null) {
                StringTokenizer stk = new StringTokenizer(order);
                String innerVar = stk.nextToken();
                int innerIndex = order.indexOf(innerVar) + innerVar.length();
                String innerValue = order.substring(innerIndex);
                if (innerVar.equals("end")) {
                    if (var.equals("primitive") && new_primitive != null)
                        new_primitive.preTreatment();
                    break;
                }

                if (var.equals("background")) scene.backgroundInput(innerVar, innerValue);
                if (var.equals("primitive") && new_primitive != null) new_primitive.input(innerVar, innerValue);
                if (var.equals("light") && new_light != null) new_light.input(innerVar, innerValue);
                if (var.equals("camera")) scene.getCamera().input(innerVar, innerValue);
            }
        }
    }

}
