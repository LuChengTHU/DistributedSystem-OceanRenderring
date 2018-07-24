package renderer.scene;

import renderer.utils.Color;
import renderer.utils.Vec3d;
import renderer.utils.Bmp;

import java.util.StringTokenizer;

public class Camera {

    public static final int KD_MAX_THREADS = 10;
    public static final int PM_MAX_THREADS = 10;
    public static final int RT_MAX_THREADS = 10;
    public static final String STD_ALGORITHM = "SPPM";
    public static final int STD_DOF_SAMPLE = 64;
    public static final double STD_APERTURE = 0;
    public static final double STD_FOCAL_LEN = 1;
    public static final double STD_LENS_WIDTH = 0.88;
    public static final double STD_LENS_HEIGHT = 0.88;
    public static final int STD_IMAGE_WIDTH = 420;
    public static final int STD_IMAGE_HEIGHT = 420;
    public static final int STD_SHADE_QUALITY = 4;
    public static final int STD_DREFL_QUALITY = 20;
    public static final int STD_MAX_HITPOINTS = 4000000;
    public static final int STD_ITERATIONS = 5000;
    public static final double STD_REDUCTION = 0.7;
    public static final int STD_MAX_PHOTONS = 500000;
    public static final int STD_EMIT_PHOTONS = 100000;
    public static final int STD_SAMPLE_PHOTONS = 10;
    public static final double STD_SAMPLE_DIST = 0.1;

    public String algorithm;
    public Vec3d O, N, Dx, Dy;
    public double aperture, focalLen;
    public double lens_W, lens_H;
    public int W, H;
    public Color[][] data;
    public int dofSample;
    public int shade_quality;
    public int drefl_quality;
    public int max_hitpoints;
    public int iterations;
    public double reduction;
    public int max_photons;
    public int emit_photons;
    public int sample_photons;
    public double sample_dist;

    public Camera() {
        algorithm = STD_ALGORITHM;
        O = new Vec3d(0.0, 0.0, 0.0);
        N = new Vec3d(0.0, 1.0, 0.0);
        dofSample = STD_DOF_SAMPLE;
        aperture = STD_APERTURE;
        focalLen = STD_FOCAL_LEN;
        lens_W = STD_LENS_WIDTH;
        lens_H = STD_LENS_HEIGHT;
        W = STD_IMAGE_WIDTH;
        H = STD_IMAGE_HEIGHT;
        shade_quality = STD_SHADE_QUALITY;
        drefl_quality = STD_DREFL_QUALITY;
        max_hitpoints = STD_MAX_HITPOINTS;
        iterations = STD_ITERATIONS;
        reduction = STD_REDUCTION;
        max_photons = STD_MAX_PHOTONS;
        emit_photons = STD_EMIT_PHOTONS;
        sample_photons = STD_SAMPLE_PHOTONS;
        sample_dist = STD_SAMPLE_DIST;
        data = null;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public Vec3d getO() {
        return O;
    }

    public Vec3d getN() {
        return N;
    }

    public int getDofSample() {
        return dofSample;
    }

    public double getAperture() {
        return aperture;
    }

    public double getFocalLen() {
        return focalLen;
    }

    public int getW() {
        return W;
    }

    public int getH() {
        return H;
    }

    public Color getColor(int i, int j) {
        return data[i][j];
    }

    public void setColor(int i, int j, Color color) {
        data[i][j] = color;
    }

    public int getShadeQuality() {
        return shade_quality;
    }

    public int getDreflQuality() {
        return drefl_quality;
    }

    public int getMaxHitpoints() {
        return max_hitpoints;
    }

    public int getIterations() {
        return iterations;
    }

    public double getReduction() {
        return reduction;
    }

    public int getMaxPhotons() {
        return max_photons;
    }

    public int getEmitPhotons() {
        return emit_photons;
    }

    public int getSamplePhotons() {
        return sample_photons;
    }

    public double getSampleDist() {
        return sample_dist;
    }

    public void initialize() {
        N = N.normalize();
        Dx = N.getAnVerticalVector();
        Dy = Dx.cross(N);

        data = new Color[H][];
        for (int i = 0; i < H; i++) {
            data[i] = new Color[W];
            for (int j = 0; j < W; j++) {
                data[i][j] = new Color();
            }
        }
    }

    public Vec3d emit(double i, double j) {
        return N.add(Dy.mul(lens_H).mul((i / (H - 1) - 0.5))).add(Dx.mul(lens_W).mul((j / (W - 1) - 0.5)));
    }

    public void dofEmit(double i, double j, Vec3d dof_O, Vec3d dof_V) {
        Vec3d focalPoint = O.add(emit(i, j).mul(focalLen));
        double x, y;
        do {
            x = Math.random() * 2 - 1;
            y = Math.random() * 2 - 1;
        } while (x * x + y * y > 1);
        dof_O.set(O.add(Dx.mul(aperture * x)).add(Dy.mul(aperture * y)));
        dof_V.set(focalPoint.sub(dof_O).normalize());
    }

    public void input(String var, String value) {
        if (var.equals("O="))
            O.input(value);
        if (var.equals("N="))
            N.input(value);
        StringTokenizer tk = new StringTokenizer(value);
        if (tk.hasMoreTokens()) {
            value = tk.nextToken();
        }
        if (var.equals("algorithm="))
            algorithm = value;
        if (var.equals("dofSample="))
            dofSample = Integer.parseInt(value);
        if (var.equals("aperture="))
            aperture = Double.parseDouble(value);
        if (var.equals("focalLen="))
            focalLen = Double.parseDouble(value);
        if (var.equals("lens_W="))
            lens_W = Double.parseDouble(value);
        if (var.equals("lens_H="))
            lens_H = Double.parseDouble(value);
        if (var.equals("image_W="))
            W = Integer.parseInt(value);
        if (var.equals("image_H="))
            H = Integer.parseInt(value);
        if (var.equals("shade_quality="))
            shade_quality = Integer.parseInt(value);
        if (var.equals("drefl_quality="))
            drefl_quality = Integer.parseInt(value);
        if (var.equals("max_hitpoints="))
            max_hitpoints = Integer.parseInt(value);
        if (var.equals("iterations="))
            iterations = Integer.parseInt(value);
        if (var.equals("reduction="))
            reduction = Double.parseDouble(value);
        if (var.equals("max_photons="))
            max_photons = Integer.parseInt(value);
        if (var.equals("emit_photons="))
            emit_photons = Integer.parseInt(value);
        if (var.equals("sample_photons="))
            sample_photons = Integer.parseInt(value);
        if (var.equals("sample_dist="))
            sample_dist = Double.parseDouble(value);
    }

    public void output(Bmp image) {
        for (int y = 0; y < H; y++)
            for (int x = 0; x < W; x++)
                image.setColor(x, y, data[y][x]);
    }

}
