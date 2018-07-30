package renderer.scene;

import renderer.objects.Collider;
import renderer.objects.Primitive;
import renderer.photon.Photon;
import renderer.utils.Color;
import renderer.utils.IntPtr;
import renderer.utils.Vec3d;

import static renderer.utils.Vec3d.EPS;

public class AreaLight extends Light{

    public Vec3d O, Dx, Dy;

    public AreaLight() {
        super();
        O = new Vec3d();
        Dx = new Vec3d();
        Dy = new Vec3d();
    }

    @Override
    public Vec3d GetO() {
        return O;
    }

    @Override
    public void input(String var, String value) {
        if (var.equals("O=")) O.input(value);
        if (var.equals("Dx=")) Dx.input(value);
        if (var.equals("Dy=")) Dy.input(value);
        super.input(var, value);
    }

    @Override
    public LightCollider collide(Vec3d ray_O, Vec3d ray_V) {
        LightCollider ret = new LightCollider();
        ret.setLight(this);

        ray_V = ray_V.normalize();
        Vec3d N = (Dx.cross(Dy)).normalize();
        double d = N.dot(ray_V);
        if (Math.abs(d) < EPS) return ret;
        double l = (N.mul(O.dot(N)).sub(ray_O).dot(N)) / d;
        if (l < EPS) return ret;

        Vec3d C = ray_O.add(ray_V.mul(l)).sub(O);
        if (Math.abs(Dx.dot(C)) > Dx.dot(Dx)) return ret;
        if (Math.abs(Dy.dot(C)) > Dy.dot(Dy)) return ret;

        ret.crash = true;
        ret.dist = l;
        return ret;
    }

    @Override
    public Photon emitPhoton() {
        Photon ret = new Photon();
        ret.power = color.div(color.power());
        ret.pos = O.add(Dx.mul(Math.random() * 2 - 1)).add(Dy.mul(Math.random() * 2 - 1));
        ret.dir.asRandomVector();
        return ret;
    }

    @Override
    public Color getIrradiance(Collider collider, Primitive primitive_head,
                               int shade_quality, IntPtr hash) {
        Primitive pri = collider.getPrimitive();
        Color ret = new Color();

        for (int i = -2; i < 2; i++) {
            for (int j = -2; j < 2; j++) {
                for (int k = 0; k < shade_quality; k++) {
                    Vec3d V = O.add(Dx.mul((Math.random() + i) / 2.0)).add(Dy.mul((Math.random() + j) / 2.0)).sub(collider.C);
                    double dist = V.module();

                    boolean shade = false;
                    for (Primitive now = primitive_head; now != null; now = now.getNext()) {
                        Collider thisCollider = now.collide(collider.C, V);
                        if (thisCollider.crash && thisCollider.dist < dist) {
                            shade = true;
                            break;
                        }
                    }

                    if (shade == false) ret.addToThis(calnIrradiance(collider, V, null));
                }
            }
        }

        ret.divToThis(16.0 * shade_quality);
        return ret;
    }
}