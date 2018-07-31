package oceanFFT;

import core.Complex;
import core.Vec2;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

public class HGenerator {
    public int N = 256; // grad size
    public int T = 10;

    public int L = 350;
    public float g = 9.81f;
    public float A = 0.0000038f;
    public float PI = 3.1415926535897932384626433832795f;
    public float wSpeed = 50f;
    public float minWaveSize = 0.1f ;
    public Vec2 wDirection = new Vec2(1.0f, 0); // wind (1, 0)
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
        float kw2 = kw * kw;
        // Ph(k)
        float Phk = A / mag4 * (float)Math.exp(-1.0/(mag2 * L2)) * (float)Math.exp(-mag2*Math.pow(minWaveSize, 2)) * kw2;
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
                int n = -N/2+i;
                int m = -N/2+j;
                if(n > N/2) n -= N ;
                if(m > N/2) m -= N ;
                Vec2 k = new Vec2(2 * PI * n / L, 2 * PI * m / L);
                Vec2 minusk = new Vec2(-2 * PI * n / L, -2 * PI * m / L);
                float epsilon1 = (float) random.nextGaussian(), epsilon2 = (float) random.nextGaussian();
                Complex r = new Complex(epsilon1 / log_2, epsilon2 / log_2);
                h0k.set(i * N + j, r.mul(phillips(k)));
                h0minusk.set(i * N + j, r.mul(phillips(minusk)).conj());
            }
        }
    }

    public void generateHkt(H h0k, H h0minusk, float t, H hkt) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int n = -N/2+i;
                int m = -N/2+j;
                if(n > N/2) n -= N ;
                if(m > N/2) m -= N ;

                Vec2 k = new Vec2(2*PI*n/L, 2*PI*m/L);
                float omega_t = dispersion(k) * t;
                float real = (float)Math.cos(omega_t);
                float im = (float)Math.sin(omega_t);
                Complex c0 = new Complex(real, im);
                Complex c1 = new Complex(real, -im);
                hkt.set(i*N+j, c0.mul(h0k.get(i*N+j)).add(c1.mul(h0k.get((N-i)%N*N+(N-j)%N).conj())));
                //System.out.println(c0.mul(h0k.get(i*N+j)).toString() + " " + c1.mul(h0minusk.get(i*N+j)).toString());
            }
        }
    }


    public void generateInitialData() throws IOException {
        H h0k = new H(N), h0minusk = new H(N);
        generateH0k(h0k, h0minusk);
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        for (int t = 0; t < T; t++) {
            H hkt = new H(N);
            generateHkt(h0k, h0minusk, t / 3.0f, hkt);

            OutputStream out = fs.create(new Path("frontend/Hdata/" + "frame_" + t + ".txt"));
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < N; i++) {
                buffer.append(String.valueOf(i));
                buffer.append("\t");
                for (int j = 0; j < N; j++) {
                    if (j > 0)
                        buffer.append(" ");
                    buffer.append(hkt.get(i * N + j).toString());
                    //System.out.println("i="+i+" j="+j+":"+hkt[i*N+j].getReal() + " i"+hkt[i*N+j].getIm());
                }
                buffer.append("\n");
                //System.out.println(buffer.toString());
            }
            out.write(buffer.toString().getBytes());
            out.close();

            if (t == 0)
                for (int i = 0; i < N; i++)
                    System.out.println(hkt.get(i));
        }
    }
}
