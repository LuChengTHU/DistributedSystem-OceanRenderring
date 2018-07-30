package renderer.main;


import renderer.tracer.RayTracer;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        RayTracer rayTracer = new RayTracer();
        rayTracer.setInput(new String("scene.txt"));
        rayTracer.setOutput(new String("pic.jpg"));
        rayTracer.run();
    }
}
