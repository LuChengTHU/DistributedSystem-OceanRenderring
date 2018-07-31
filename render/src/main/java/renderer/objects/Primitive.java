package renderer.objects;

import renderer.utils.Vec3d;
import renderer.utils.Material;
import renderer.utils.Color;

import java.io.IOException;

public class Primitive {
    public int sample;
    public Material material;
    public Primitive next;

    public Primitive() {
        sample = (int) (Math.random() * Integer.MAX_VALUE);
        material = new Material();
        next = null;
    }

    public Primitive(Primitive primitive) {
        sample = primitive.sample;
        next = primitive.next;
        material = new Material(primitive.material);
    }

    public void input(String var, String value) throws IOException {
        material.input(var, value);
    }

    public void setSample(int _sample) {
        sample = _sample;
    }

    public int getSample() {
        return sample;
    }

    public void setMaterial(Material _material) {
        material = _material;
    }

    public Material getMaterial() {
        return material;
    }

    public Primitive getNext() {
        return next;
    }

    public void setNext(Primitive primitive) {
        next = primitive;
    }

    public int getName() {
        return 0;
    }

    public void preTreatment() {
    }

    public Collider collide(Vec3d ray_O, Vec3d ray_V) {
        return new Collider();
    }

    public Color getTexture(Vec3d C) {
        return new Color();
    }
}
