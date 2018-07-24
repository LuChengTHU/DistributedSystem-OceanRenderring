package mapreduce;

import java.util.Random;

public class main {

    public static int N = 256; // grad size
    public static int L = 1000;
    public static float g = 9.81f;
    public static int T = 100; // num of frames
    public static float A = 20f;
    public static float PI = 3.1415926535897932384626433832795f;
    public static float wSpeed = 26f;
    public static Vec2 wDirection = new Vec2(1.0f, 0); // wind (1, 0)

    public static Random random = new Random();

    public static float phillips(Vec2 k) {
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
        return PhkSqrt;
    }
    public static float dispersion(Vec2 k) {
        float w0 = 2 * PI / 200.0f;
        return (float)Math.floor(Math.sqrt(k.length() * g) / w0) * w0;
    }
    public static void generateH0k() {
        Complex[] h0k = new Complex[N*N];
        Complex[] h0minusk = new Complex[N*N];
        float log_2 = (float)Math.log(2);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int n = -N / 2 + i;
                int m = -N / 2 + j;
                Vec2 k = new Vec2(2*PI*n/L, 2*PI*m/L);
                Vec2 minusk = new Vec2(-2*PI*n/L, -2*PI*m/L);
                float epsilon1 = (float)random.nextGaussian(), epsilon2 = (float)random.nextGaussian();
                Complex r = new Complex(epsilon1 / log_2, epsilon2 / log_2);
                h0k[i*N+j] = r.mul(phillips(k));
                h0minusk[i*N+j] = r.mul(phillips(minusk)).conj();
            }
        }

        for (int t = 0; t < T; t++) {
            Complex[] hkt = new Complex[N*N];
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    int n = -N / 2 + i;
                    int m = -N / 2 + j;
                    Vec2 k = new Vec2(2*PI*n/L, 2*PI*m/L);
                    float omega_t = dispersion(k) * t;
                    float real = (float)Math.cos(omega_t);
                    float im = (float)Math.sin(omega_t);
                    Complex c0 = new Complex(real, im);
                    Complex c1 = new Complex(real, -im);
                    hkt[i*N+j] = c0.mul(h0k[i*N+j]).add(c1.mul(h0minusk[i*N+j]));
                    System.out.println("i="+i+" j="+j+":"+hkt[i*N+j].getReal() + " i"+hkt[i*N+j].getIm());
                }
            }
        }
    }

    public static void generateHkt() {

    }

    public static void main(String[] args) throws Exception {
        generateH0k();
        Integer x;
    }

}
