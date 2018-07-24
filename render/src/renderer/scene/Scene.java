package renderer.scene;

import renderer.objects.Collider;
import renderer.objects.Plane;
import renderer.objects.Primitive;
import renderer.objects.Sphere;
import renderer.utils.Color;
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

    public void createScene(String filename) throws IOException{
        if (filename.equals("")) return;
        BufferedReader br = new BufferedReader(new FileReader(filename));
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
                addPrimitive(new_primitive);
            } else if (var.equals("light")) {
                String type = tk.nextToken();
                if (type.equals("area")) new_light = new AreaLight();
                addLight(new_light);
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

                if (var.equals("background")) backgroundInput(innerVar, innerValue);
                if (var.equals("primitive") && new_primitive != null) new_primitive.input(innerVar, innerValue);
                if (var.equals("light") && new_light != null) new_light.input(innerVar, innerValue);
                if (var.equals("camera")) camera.input(innerVar, innerValue);
            }
        }

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
