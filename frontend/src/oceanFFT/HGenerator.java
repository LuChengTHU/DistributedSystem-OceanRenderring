package oceanFFT;

import core.Complex;
import core.Conf;
import core.Vec2;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

public class HGenerator {
    public static final int N = Conf.resolution; // grad size
    public static final int L = Conf.L;
    public static final float g = Conf.g;
    public static final float A = Conf.A;
    public static final float PI = 3.1415926535897932384626433832795f;
    public static final float wSpeed = Conf.wSpeed;
    public static final float minWaveSize = Conf.minWaveSize ;
    public static final Vec2 wDirection = Conf.wDirection; // wind (1, 0)
    public Random random = new Random();

    private static HGenerator hGenerator = new HGenerator();

    private HGenerator() {}

    public static HGenerator getInstance()
    {
        return hGenerator;
    }

    private float phillips(Vec2 k) {
        //|k|
        float mag = k.length();
        if (mag < 0.0001) mag = 0.0001f;
        if(k.getX() == 0 && k.getY() == 0) return 0 ;
        //|k|^2
        float mag2 = mag * mag;
        //|k|^4
        float mag4 = mag2 * mag2;
        // L = v^2/g
        float L_ = wSpeed * wSpeed / g;
        float L2 = L_ * L_;
        // |k_normal * w_normal|
        float kw = k.normalize().dot(wDirection.normalize());
        float kw2 = kw * kw ;
        // Ph(k)
        float Phk = A / mag4 * (float)Math.exp(-1.0/(mag2 * L2)) * (float)Math.exp(-mag2*Math.pow(L/2000, 2)) * kw2;
        float PhkSqrt = (float)Math.sqrt(Phk);
        return PhkSqrt;
    }

    private float dispersion(Vec2 k) {
        float w0 = 2 * PI / 200.0f;
        return (float)Math.floor(Math.sqrt(k.length() * g) / w0) * w0;
    }

    public void generateH0k(H h0k, H h0minusk) {
        float log_2 = (float) Math.sqrt(2);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int n = i;
                int m = j;
                if(n > N/2) n -= N ;
                if(m > N/2) m -= N ;
                Vec2 k = new Vec2(2 * PI * n / L, 2 * PI * m / L);
                Vec2 minusk = new Vec2(-2 * PI * n / L, -2 * PI * m / L);
                float epsilon1 = (float) random.nextGaussian(), epsilon2 = (float) random.nextGaussian();
                Complex r = new Complex(epsilon1 / log_2, epsilon2 / log_2);
                h0k.set(i * N + j, r.mul(phillips(k)));
                epsilon1 = (float) random.nextGaussian() ;
                epsilon2 = (float) random.nextGaussian();
                r = new Complex(epsilon1 / log_2, epsilon2 / log_2);
                h0minusk.set(i * N + j, r.mul(phillips(minusk)).conj());
            }
        }
    }

    public void generateHkt(H h0k, H h0minusk, float t, H hkt) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int n = i;
                int m = j;
                if(n > N/2) n -= N ;
                if(m > N/2) m -= N ;

                Vec2 k = new Vec2(2*PI*n/L, 2*PI*m/L);
                float omega_t = dispersion(k) * t;
                float real = (float)Math.cos(omega_t);
                float im = (float)Math.sin(omega_t);
                Complex c0 = new Complex(real, im);
                Complex c1 = new Complex(real, -im);
                hkt.set(i*N+j, c0.mul(h0k.get(i*N+j)).add(c1.mul(h0minusk.get(i*N+j).conj())));
                //System.out.println(c0.mul(h0k.get(i*N+j)).toString() + " " + c1.mul(h0minusk.get(i*N+j)).toString());
            }
        }
    }

}
