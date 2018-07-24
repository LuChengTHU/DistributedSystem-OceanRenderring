package renderer.objects;

import renderer.utils.Color;
import renderer.utils.Vec3d;

import java.io.IOException;

import static renderer.utils.Vec3d.EPS;

public class Plane extends Primitive {

    Vec3d N, Dx, Dy;
    double R;

    public Plane() {
        super();
        N = new Vec3d();
        Dx = new Vec3d();
        Dy = new Vec3d();
    }

    @Override
    public void input(String var, String value) throws IOException {
        if (var.equals("N=")) N.input(value);
        if (var.equals("R=")) R = Double.parseDouble(value);
        if (var.equals("Dx=")) Dx.input(value);
        if (var.equals("Dy=")) Dy.input(value);
        super.input(var, value);
        N = N.normalize();
    }

    @Override
    public Collider collide(Vec3d ray_O, Vec3d ray_V) {
        Collider collider = new Collider();
        ray_V = ray_V.normalize();
        double d = N.dot(ray_V);
        if (Math.abs(d) < EPS) return collider;
        double l = ((N.mul(R)).sub(ray_O)).dot(N) / d;
        if (l < EPS) return collider;

        collider.crash = true;
        collider.I = ray_V;
        collider.setPrimitive(this);
        collider.dist = l;
        collider.front = (d < 0);
        collider.C = ray_O.add(ray_V.mul(collider.dist));
        collider.N = (collider.front) ? N : N.inv();
        return collider;
    }

    public double get1(double x) {
        while (x > 1.0) x -= 1.0;
        while (x < 0) x += 1.0;
        return x;
    }

    @Override
    public Color getTexture(Vec3d C) {
        double u = C.dot(Dx) / Dx.module2();
        double v = C.dot(Dy) / Dy.module2();
        u = get1(u);
        v = get1(v);
        return material.texture.getSmoothColor(u, v);
    }
}
