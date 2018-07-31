package renderer.main;


import renderer.tracer.RayTracer;
import renderer.tracer.RayTracerDriver;
import renderer.utils.FileLoader;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws Exception {
        if (RayTracerDriver.rtEnv == FileLoader.ENV.NATIVE) {
            RayTracer rayTracer = new RayTracer();
            rayTracer.setInput(new String("scene.txt"));
            rayTracer.setOutput(new String("pic.jpg"));
            rayTracer.run();
        } else {
            RayTracerDriver rayTracerDriver = new RayTracerDriver();
            rayTracerDriver.run();
        }
    }
}
