package renderer.tracer;

import renderer.objects.Collider;
import renderer.objects.Primitive;
import renderer.scene.Camera;
import renderer.scene.Light;
import renderer.scene.LightCollider;
import renderer.scene.Scene;
import renderer.utils.*;

import java.io.IOException;

import static renderer.scene.Camera.RT_MAX_THREADS;
import static renderer.utils.Vec3d.EPS;

public class RayTracer {

    public static final int HASH_MOD = 10000007;
    public static final int MAX_DREFL_DEP = 2;
    public static final int MAX_RAYTRACING_DEP = 5;
    public static final int HASH_FAC = 7;

    String inputName, outputName;
    Scene scene;
    Camera camera;

    int H, W;
    IntPtr[][] sample;
    // BooleanPtr completeThread;

    public RayTracer() {
        scene = new Scene();
        camera = new Camera();
    }

    public void setInput(String filename) {
        inputName = filename;
    }

    public void setOutput(String filename) {
        outputName = filename;
    }

    public Color calnDiffusion(Collider collider, IntPtr hash) {
        Primitive pri = collider.getPrimitive();
        Color color = new Color(pri.getMaterial().color);
        if (pri.getMaterial().texture != null) {
            if (pri.getName() == 0) {
                color.mulToThis(pri.getTexture(collider.C));
            } else {
                color.mulToThis(pri.getTexture(new Vec3d(collider.u, collider.v, 0.0)));
            }
        }

        Color ret = color.mul(scene.getBackgroundColor().mul(pri.getMaterial().diff));
        for (Light light = scene.getLightHead(); light != null; light = light.getNext()) {
            ret.addToThis(color.mul(light.getIrradiance(collider, scene.getPrimitiveHead(), scene.getCamera().getShadeQuality(), hash)));
        }

        return ret;
    }

    public Color calnReflection(Collider collider, Vec3d ray_V, int dep, boolean refracted , IntPtr hash) {
        Primitive pri = collider.getPrimitive();
        double n = pri.getMaterial().rindex;
        if (!refracted) n = 1.0 / n;
        double refl = pri.getMaterial().getRefl(ray_V, collider.N, n);

        ray_V = ray_V.reflect(collider.N);

        if (pri.getMaterial().drefl < EPS || dep > MAX_DREFL_DEP) {
            Color alpha = pri.getMaterial().color.mul(refl);
            return rayTracing(collider.C, ray_V, dep + 1, refracted, hash).mul(alpha);
        }

        Vec3d Dx = ray_V.getAnVerticalVector();
        Vec3d Dy = ray_V.cross(Dx);
        Dx = Dx.normalize().mul(pri.getMaterial().drefl);
        Dy = Dy.normalize().mul(pri.getMaterial().drefl);

        int totalSample = camera.getDreflQuality();
        Color rcol = new Color();
        Color alpha = pri.getMaterial().color.mul(refl).div(totalSample);
        for (int k = 0; k < totalSample; k++) {
            double x, y;
            do {
                x = Math.random() * 2 - 1;
                y = Math.random() * 2 - 1;
            } while (x * x + y * y > 1);
            x *= pri.getMaterial().drefl;
            y *= pri.getMaterial().drefl;

            rcol.addToThis(rayTracing(collider.C, ray_V.add(Dx.mul(x)).add(Dy.mul(y)), dep + MAX_DREFL_DEP, refracted, null));
        }
        return rcol.mul(alpha);
    }

    public Color calnRefraction(Collider collider, Vec3d ray_V, int dep, boolean refracted, IntPtr hash) {
        Primitive pri = collider.getPrimitive();
        double n = pri.getMaterial().rindex;
        if (!refracted) n = 1.0 / n;
        double refr = pri.getMaterial().getRefr(ray_V, collider.N, n);

        BooleanPtr nextRefracted = new BooleanPtr(refracted);
        ray_V = ray_V.refract(collider.N, n, nextRefracted);

        Color alpha = new Color(1.0, 1.0, 1.0);
        alpha.mulToThis(refr);
        if (refracted) {
            alpha.mulToThis((pri.getMaterial().absor.mul(-collider.dist)).exp());
        }
        Color rcol = rayTracing(collider.C, ray_V, dep + 1, nextRefracted.get(), hash);
        return rcol.mul(alpha);
    }


    public Color rayTracing(Vec3d ray_O , Vec3d ray_V , int dep , boolean refracted , IntPtr hash) {
        if (dep > MAX_RAYTRACING_DEP) return new Color();
        if (hash != null) hash.set((hash.get() * HASH_FAC) % HASH_MOD);

        Color ret = new Color();
        Collider collider = scene.findNearestCollide(ray_O, ray_V);
        LightCollider lightCollider = scene.findNearestLight(ray_O, ray_V);

        if (lightCollider != null) {
            Light nearest_light = lightCollider.getLight();
            if (collider == null || lightCollider.dist < collider.dist) {
                if (hash != null) hash.set((hash.get() + nearest_light.getSample()) % HASH_MOD);
                ret.addToThis(nearest_light.getColor().div(nearest_light.getColor().RGBMax()));
            }
        }

        if (collider != null) {
            Primitive nearest_primitive = collider.getPrimitive();
            if (hash != null) hash.set((hash.get() + nearest_primitive.getSample()) % HASH_MOD);
            if (nearest_primitive.getMaterial().diff > EPS) ret.addToThis(calnDiffusion(collider, hash));
            double n = nearest_primitive.getMaterial().rindex;
            if (!refracted) n = 1.0 / n;

            if (!camera.getAlgorithm().equals("RC")) {
                if (nearest_primitive.getMaterial().getRefl(ray_V, collider.N, n)  > EPS) ret.addToThis(calnReflection(collider, ray_V, dep, refracted, hash));
                if (nearest_primitive.getMaterial().getRefr(ray_V, collider.N, n)  > EPS) ret.addToThis(calnRefraction(collider, ray_V, dep, refracted, hash));
            }
        }

        if (dep == 1) ret = ret.confine();
        return ret;
    }

    public void sampling(int threadID, int randID) {
        // srand(randID);

        Vec3d ray_O = camera.getO();

        for (int i = 0; i < H; i++) {
            // row mutex
            for (int j = 0; j < W; j++) {
                if (camera.getAperture() < EPS) {
                    Vec3d ray_V = camera.emit(i, j);
                    sample[i][j].set(0);
                    Color color = camera.getColor(i, j);
                    color.addToThis(rayTracing(ray_O, ray_V, 1, false, sample[i][j]));
                    camera.setColor(i, j, color.confine());
                } else {
                    int dofSample = camera.getDofSample();
                    int iteration = dofSample;
                    Color color = camera.getColor(i, j);
                    for (int k = 0; k < iteration; k++) {
                        Vec3d dof_O = new Vec3d();
                        Vec3d dof_V = new Vec3d();
                        camera.dofEmit(i, j, dof_O, dof_V);
                        color.addToThis(rayTracing(dof_O, dof_V, 1, false, null).divToThis(dofSample));
                    }
                    camera.setColor(i, j, color.confine());
                }

                if (j == W - 1) {
                    System.out.println("Sampling=" + i + "/" + H + "\n");
                }
            }
        }

        // completeThread[threadID] = true;
    }

    public void resampling(int threadID, int randID) {
        // srand(randID);
        Vec3d ray_O = camera.getO();

        for (int i = 0; i < H; i++) {
            // row mutex
            for (int j = 0; j < W; j++) {
                if (!((i == 0 || sample[i][j].get() == sample[i - 1][j].get())
                        && (i == H - 1 || sample[i][j].get() == sample[i + 1][j].get())
                        && (j == 0 || sample[i][j].get() == sample[i][j - 1].get())
                        && (j == W - 1 || sample[i][j].get() == sample[i][j + 1].get()))) {

                    Color color = camera.getColor(i, j).divToThis(5.0);
                    for (int r = -1; r <= 1; r++)
                        for (int c = -1; c <= 1; c++) {
                            if (((r + c) & 1) == 0) continue;
                            Vec3d ray_V = camera.emit( i + ( double ) r / 3.0 , j + ( double ) c / 3.0 );
                            color.addToThis(rayTracing(ray_O, ray_V, 1, false, null).divToThis(5));
                        }
                    camera.setColor(i, j, color.confine());
                }

                if (j == W - 1) {
                    System.out.println("ReSampling=" + i + "/" + H + "\n");
                }
            }
        }
    }

    public void multiThreadSampling(int randIDBase) {
        sampling(0, 0);
    }

    public void multiThreadResampling(int randIDBase) {
        resampling(0, 0);
    }

    public void generateImage(String filename) {
        Bmp bmp = new Bmp(H, W);
        camera.output(bmp);
        bmp.output(filename);
    }

    public void run() throws IOException {
        scene.createScene(inputName, FileLoader.ENV.NATIVE);
        camera = scene.getCamera();

        H = camera.getH();
        W = camera.getW();

        sample = new IntPtr[H][];
        for (int i = 0; i < H; i++) {
            sample[i] = new IntPtr[W];
            for (int j = 0; j < W; j++) {
                sample[i][j] = new IntPtr();
            }
        }

        multiThreadSampling(2 * RT_MAX_THREADS);
        if (camera.getAperture() < EPS) {
            multiThreadResampling(3 * RT_MAX_THREADS);
        }

        generateImage(outputName);
    }
}
