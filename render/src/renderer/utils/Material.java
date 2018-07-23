package renderer.utils;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;

import static renderer.utils.Vec3d.EPS;

public class Material {

    Color color, absor;
    double refl, refr;
    double diff, spec;
    double rindex;
    double drefl;
    BufferedImage texture;

    public Material() {
        color = new Color(0, 0, 0);
        absor = new Color(0, 0, 0);
        refl = refr = diff = spec = 0;
        rindex = 0;
        drefl = 0;
        texture = null;
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
        if (var.equals("refl=")) refl = Double.parseDouble(value);
        if (var.equals("refr=")) refr = Double.parseDouble(value);
        if (var.equals("diff=")) diff = Double.parseDouble(value);
        if (var.equals("spec=")) spec = Double.parseDouble(value);
        if (var.equals("drefl=")) drefl = Double.parseDouble(value);
        if (var.equals("rindex=")) rindex = Double.parseDouble(value);
        if (var.equals("texture=")) {
            texture = ImageIO.read(new FileInputStream(value));
        }
    }

}