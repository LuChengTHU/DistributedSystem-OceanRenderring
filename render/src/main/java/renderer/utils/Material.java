package renderer.utils;

import java.io.*;
import java.util.StringTokenizer;

import static renderer.utils.Vec3d.EPS;

public class Material {

    public Color color, absor;
    private double refl, refr;
    public double diff, spec;
    public double rindex;
    public double drefl;
    public Bmp texture;
    public Bmp bump;

    public Material() {
        color = new Color(0, 0, 0);
        absor = new Color(0, 0, 0);
        refl = refr = diff = spec = 0;
        rindex = 0;
        drefl = 0;
        texture = null;
        bump = null;
    }

    public Material(Material m) {
        color = new Color(m.color);
        absor = new Color(m.absor);
        refl = m.refl;
        refr = m.refr;
        diff = m.diff;
        spec = m.spec;
        rindex = m.rindex;
        drefl = m.drefl;
        texture = m.texture;
        bump = m.bump;
    }

    public double getRefl(Vec3d rey_I, Vec3d N, double n) {
        return refl;
    }

    public double getRefr(Vec3d rey_I, Vec3d N, double n) {
        return refr;
    }

    public double BRDF(Vec3d ray_R, Vec3d N, Vec3d ray_I) {
        double ret = 0;
        ray_R = ray_R.normalize();
        ray_I = ray_I.normalize();

        double tmp = ray_R.dot(N);
        if (diff > EPS && tmp > EPS) {
            ret += diff * tmp;
        }
        tmp = ray_R.dot(ray_I.reflect(N).mul(-1));
        if (spec > EPS && tmp > EPS) {
            ret += spec * Math.pow(tmp, 50);
        }

        return ret;
    }

    public void input(String var, String value) throws IOException {
        if (var.equals("color=")) color.input(value);
        if (var.equals("absor=")) absor.input(value);
        if (var.equals("texture=")) {
            texture = new Bmp();
            texture.input(value);
        }
        if (var.equals("bump=")) {
            bump = new Bmp();
            bump.input(value);
        }
        StringTokenizer tk = new StringTokenizer(value);
        if (tk.hasMoreTokens()) {
            value = tk.nextToken();
        }
        if (var.equals("refl=")) refl = Double.parseDouble(value);
        if (var.equals("refr=")) refr = Double.parseDouble(value);
        if (var.equals("diff=")) diff = Double.parseDouble(value);
        if (var.equals("spec=")) spec = Double.parseDouble(value);
        if (var.equals("drefl=")) drefl = Double.parseDouble(value);
        if (var.equals("rindex=")) rindex = Double.parseDouble(value);
    }

    @Override public String toString() {
        return ",color=" + color.toString()
                + ",absor=" + absor.toString()
                + ",refl=" + refl
                + ",refr=" + refr
                + ",diff=" + diff
                + ",spec=" + spec
                + ",rindex=" + rindex
                + ",drefl=" + drefl;
    }

}
