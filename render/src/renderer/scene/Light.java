package renderer.scene;


import renderer.objects.Collider;
import renderer.objects.Primitive;
import renderer.photon.Photon;
import renderer.utils.Color;
import renderer.utils.IntPtr;
import renderer.utils.Vec3d;

import static renderer.tracer.RayTracer.HASH_MOD;

public class Light {

    public int sample;
    public Color color;
    public Light next;

    public Light() {
        sample = (int)(Math.random() * Integer.MAX_VALUE);
        color = new Color();
        next = null;
    }

    public int getSample() {
        return sample;
    }

    public Color getColor() {
        return color;
    }

    public Light getNext() {
        return next;
    }

    public void setNext(Light light) {
        next = light;
    }

    public void input(String var, String value) {
        if (var.equals("color=")) color.input(value);
    }

    public Color calnIrradiance(Collider collider, Vec3d V, IntPtr hash) {
        Primitive pri = collider.getPrimitive();
        Color ret = color.mul(pri.getMaterial().BRDF(V, collider.N, collider.I.inv()));
        // FIXME, for path tracing
        ret.divToThis(V.module2()); //rt adapts to ppm

        if (!ret.isZeroColor() && hash != null) {
            hash.set((hash.get() + getSample()) % HASH_MOD);
        }

        return ret;
    }

    public Vec3d GetO() {
        return new Vec3d();
    }

    public LightCollider collide(Vec3d ray_O, Vec3d ray_V) {
        return new LightCollider();
    }

    public Color getIrradiance(Collider collider, Primitive primitive_head,
                               int shade_quality, IntPtr hash) {
        return new Color();
    }

    public Photon emitPhoton() {
        return new Photon();
    }
}
