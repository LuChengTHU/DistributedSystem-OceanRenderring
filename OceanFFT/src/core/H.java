package core;
import java.util.Random;

import java.lang.Math;
public class H {
    private int N;
    private int L;
    private float A;
    private Complex[] h;
    private Vec2 wDirection;
    private float wSpeed;
    private Random random;
    public static float PI = 3.1415926535897932384626433832795f;
    public static float g = 9.81f;

    public H(int n, int l, float a, Vec2 direction, float speed, Random r) {
        N = n;
        L = l;
        A = a;
        wDirection = direction;
        wSpeed = speed;
        h = new Complex[N * N];
        random = r;
    }

    public void init() {
        float log_2 = (float)Math.log(2);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int n = -N/2 + i;
                int m = -N/2 + j;
                //k
                Vec2 k = new Vec2(2*PI*n/L, 2*PI*m/L);
                //|k|
                float mag = k.length();
                if (mag < 0.0001) mag = 0.0001f;
                //|k|^2
                float mag2 = mag * mag;
                //|k|^4
                float mag4 = mag2 * mag2;
                // L = v^2/g
                float L_ = wSpeed * wSpeed / g;
                float L2 = L_ * L_;
                // |k_normal * w_normal|
                float kw = k.normalize().dot(wDirection.normalize());
                float kw2 = kw * kw;
                // Ph(k)
                float Phk = A / mag4 * (float)Math.exp(-1.0/(mag2 * L2)) * kw2;
                float PhkSqrt = (float)Math.sqrt(Phk);
                float epsilon1 = (float)random.nextGaussian(), epsilon2 = (float)random.nextGaussian();
                set(i, j, new Complex(epsilon1 / log_2 * PhkSqrt, epsilon2 / log_2 * PhkSqrt));
                System.out.println(epsilon1 / log_2 * PhkSqrt + "," + epsilon2 / log_2 * PhkSqrt);
            }
        }

    }
    public Complex get(int i, int j) {
        return h[i * N + j];
    }

    public void set(int i, int j, Complex c) {
        h[i * N + j] = c;
    }

}
