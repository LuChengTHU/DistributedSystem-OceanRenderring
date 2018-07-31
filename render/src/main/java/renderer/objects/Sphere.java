package renderer.objects;

import org.omg.CORBA.CODESET_INCOMPATIBLE;
import renderer.utils.Color;
import renderer.utils.Vec3d;

import java.io.IOException;
import java.util.StringTokenizer;

import static renderer.utils.Vec3d.EPS;

public class Sphere extends Primitive{

    public Vec3d O, De, Dc;
    public double R;

    public Sphere() {
        super();
        De = new Vec3d(0.0, 0.0, 1.0);
        Dc = new Vec3d(0.0, 1.0, 0.0);
        O = new Vec3d();
    }

    @Override
    public void input(String var, String value) throws IOException {
        if (var.equals("O=")) O.input(value);
        if (var.equals("De=")) De.input(value);
        if (var.equals("Dc=")) Dc.input(value);
        super.input(var, value);
        StringTokenizer tk = new StringTokenizer(value);
        if (tk.hasMoreTokens()) {
            value = tk.nextToken();
        }
        if (var.equals("R=")) R = Double.parseDouble(value);
    }

    @Override
    public Collider collide(Vec3d ray_O, Vec3d ray_V) {
        Collider collider = new Collider();
        ray_V = ray_V.normalize();
        Vec3d P = ray_O.sub(O);
        double b = -P.dot(ray_V);
        double det = b * b - P.module2() + R * R;

        if (det > EPS) {
            det = Math.sqrt(det);
            double x1 = b - det, x2 = b + det;

            if (x2 < EPS) return collider;
            if (x1 > EPS) {
                collider.dist = x1;
                collider.front = true;
            } else {
                collider.dist = x2;
                collider.front = false;
            }
        } else {
            return collider;
        }

        collider.crash = true;
        collider.I = ray_V;
        collider.setPrimitive(this);
        collider.C = ray_O.add(ray_V.mul(collider.dist));
        collider.N = collider.C.sub(O).normalize();
        if (!collider.front) collider.N = collider.N.inv();
        return collider;
    }

    @Override
    public Color getTexture(Vec3d C) {
        Vec3d I = C.sub(O).normalize();
        double a = Math.acos(-I.dot(De));
        double b = Math.acos(Math.min(Math.max(I.dot(Dc) / Math.sin(a), -1.0), 1.0));
        double u = a / Math.PI, v = b / 2.0 / Math.PI;
        if (I.dot(Dc.cross(De)) < 0) v = 1 - v;
        return material.texture.getSmoothColor(u, v);
    }

}
