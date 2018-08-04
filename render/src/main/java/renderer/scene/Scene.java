package renderer.scene;

import renderer.objects.Collider;
import renderer.objects.Plane;
import renderer.objects.Primitive;
import renderer.objects.Sphere;
import renderer.utils.Color;
import renderer.utils.FileLoader;
import renderer.utils.Vec3d;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class Scene {
    public Primitive primitive_head;
    public Light light_head;
    public Camera camera;
    public Color background_color;

    public Scene() {
        primitive_head = null;
        light_head = null;
        background_color = new Color();
        camera = new Camera();
    }

    public Primitive getPrimitiveHead() {
        return primitive_head;
    }

    public Light getLightHead() {
        return light_head;
    }

    public Camera getCamera() {
        return camera;
    }

    public Color getBackgroundColor() {
        return background_color;
    }

    public void backgroundInput(String var, String value) {
        if (var.equals("color=")) background_color.input(value);
    }

    public void addPrimitive(Primitive pri) {
        if (pri != null) {
            pri.setNext(primitive_head);
            primitive_head = pri;
        }
    }

    public void addLight(Light light) {
        if (light != null) {
            light.setNext(light_head);
            light_head = light;
        }
    }

    public void createScene(String filename, FileLoader.ENV env) throws IOException{
        FileLoader fl = new FileLoader(filename, env);
        fl.parseScene(this);
        camera.initialize();
    }

    public Collider findNearestCollide(Vec3d ray_O, Vec3d ray_V) {
        Collider ret = null;
        for (Primitive now = primitive_head; now != null; now = now.getNext()) {
            Collider collider = now.collide(ray_O, ray_V);
            if (collider.crash && (ret == null || collider.dist < ret.dist)) {
                ret = collider;
            }
        }
        return ret;
    }

    public LightCollider findNearestLight(Vec3d ray_O, Vec3d ray_V) {
        LightCollider ret = null;
        for (Light now = light_head; now != null; now = now.getNext()) {
            LightCollider lightCollider = now.collide(ray_O, ray_V);
            if (lightCollider.crash && (ret == null || lightCollider.dist < ret.dist)) {
                ret = lightCollider;
            }
        }
        return ret;
    }

}
