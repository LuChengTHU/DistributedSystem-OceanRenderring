package mapreduce;

import core.FFT;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.Float.NaN;

public class main {

    public static int N = 1024; // grad size
    public static int L = 500;
    public static float g = 9.81f;
    public static int T = 100; // num of frames
    public static float A = 0.2f;
    public static float PI = 3.1415926535897932384626433832795f;
    public static float wSpeed = 5000f;
    public static Vec2 wDirection = new Vec2(1.0f, 0); // wind (1, 0)

    public static Random random = new Random();

    public static float phillips(Vec2 k) {
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
        float Phk = A / mag4 * (float)Math.exp(-1.0/(mag2 * L2)) * kw2;
        float PhkSqrt = (float)Math.sqrt(Phk);
        return PhkSqrt;
    }
    public static float dispersion(Vec2 k) {
        float w0 = 2 * PI / 200.0f;
        return (float)Math.floor(Math.sqrt(k.length() * g) / w0) * w0;
    }
    public static void generateH0k() throws IOException {
        Complex[] h0k = new Complex[N*N];
        Complex[] h0minusk = new Complex[N*N];
        float log_2 = (float)Math.sqrt(2);
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

        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);

        for (int t = 0; t < T; t++) {
            Complex[] hkt = new Complex[N*N];

            //FSDataOutputStream out = fs.create(new Path("OceanFFT/Hdata/" + "frame_"+ t + ".txt"));
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < N; i++) {
                buffer.append(i);
                buffer.append("\t");
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
                    if (j > 0)
                        buffer.append(" ");
                    buffer.append(hkt[i*N+j].getReal());
                    buffer.append(",");
                    buffer.append(hkt[i*N+j].getIm());
                    //System.out.println("i="+i+" j="+j+":"+hkt[i*N+j].getReal() + " i"+hkt[i*N+j].getIm());
                }
                buffer.append("\n");
            }
            //out.writeChars(buffer.toString());
            //out.close();
        }

        demoFFT(h0k, h0minusk) ;
    }

    public static void demoFFT(Complex[] h, Complex[] hmin) throws IOException
    {
        System.out.println("Demo: Generating demo...\n") ;
        ArrayList<Complex> in ;
        for(int i = 0; i < N; i ++)
        {
            in = new ArrayList<Complex>() ;
            for(int j = 0; j < N; j ++)
                in.add(h[i*N+j].add(hmin[i*N+j]));
            FFT.calcFFT(in);
            for(int j = 0; j < N; j ++)
                h[i*N+j] = in.get(j) ;
        }
        for(int i = 0; i < N; i ++)
        {
            in = new ArrayList<Complex>() ;
            for(int j = 0; j < N; j ++)
                in.add(h[j*N+i]) ;
            FFT.calcFFT(in);
            System.out.println(in.get(0));
            for(int j = 0; j < N; j ++)
                h[j*N+i] = in.get(j).mul((float)(1/2048.0)) ;
        }
        System.out.println("Demo: FFT finished...\n") ;
        FileWriter out = new FileWriter("OceanFFT/Hdata/" + "demo" + ".txt");
        for(int i = 0; i < N; i ++)
        {
            for(int j = 0; j < N; j ++)
                out.write(Float.valueOf(h[i*N+j].getNorm()).toString()+" ");
            out.write("\n");
        }
        out.flush();
        System.out.println("Demo: Output finished...\n") ;
    }


    public static void generateHkt() {

    }

    public static void main(String[] args) throws Exception {
        generateH0k();
        Integer x;
    }

}
