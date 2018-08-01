package renderer.utils;

public class OceanMaterial extends Material {
    @Override
    public double getRefl(Vec3d ray_I, Vec3d N, double n) {
        N = N.normalize();
        ray_I = ray_I.normalize();
        double cosThetaI = Math.cos(ray_I.dot(N));
        double thetaI = Math.acos(cosThetaI);
        double sinThetaT = Math.sin(thetaI) / n;
        double thetaT = Math.asin(sinThetaT);
        double refl;
        if (thetaI == 0.0) {
            refl =  (n - 1) / (n + 1);
            refl = refl * refl;
        }
        else {
            double fs = Math.sin(thetaT - thetaI) / Math.sin(thetaT + thetaI);
            double ts = Math.tan(thetaT - thetaI) / Math.tan(thetaT + thetaI);
            refl = 0.5 * (fs * fs + ts * ts);
        }

        return refl;
    }

    @Override
    public double getRefr(Vec3d ray_I, Vec3d N, double n) {
        return 1.0 - getRefl(ray_I, N, n);
    }
}
