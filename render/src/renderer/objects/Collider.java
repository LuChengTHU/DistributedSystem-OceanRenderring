package renderer.objects;

import renderer.utils.Vec3d;

public class Collider {

    public Primitive pri;

    public double dist;
    public boolean crash, front;
    public Vec3d N, C, I;
    public double u, v;

    public Collider() {
        pri = null;
        crash = false;
        N = new Vec3d();
        C = new Vec3d();
        I = new Vec3d();
    }

    public Primitive getPrimitive() {
        return pri;
    }

    public void setPrimitive(Primitive _pri) {
        pri = _pri;
    }
}
