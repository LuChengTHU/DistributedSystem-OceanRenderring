package core;
import java.util.List;
import java.util.Random;

import java.lang.Math;
public class FFT {

    private static FFT fft = null ;

    private Complex[] w ;
    private Complex[] temp ;
    private int N ;
    private int logN ;
    public static float PI = 3.1415926535897932384626433832795f;

    private FFT(int n)
    {
        logN = n ;
        N = (1<<n) ;
        w = new Complex[N] ;
        temp = new Complex[N] ;
        for(int i = 0; i < N; i ++) {
            w[i] = new Complex(Math.cos(2 * PI * i / N), Math.sin(2 * PI * i / N));
            System.out.println(w[i]);
        }
    }

    private static synchronized FFT getInstance(int n)
    {
        if(fft == null || fft.logN != n) fft = new FFT(n) ;
        return fft ;
    }

    private void calculate(List<Complex> in, int base, int deep, int flag)
    {
        if(deep == logN) return ;
        int ss, i, num = (N>>deep) ;
        int half = (num>>1), step = (1<<deep) ;
        Complex a, b ;
        calculate(in, base, deep+1, flag) ;
        calculate(in, base+step, deep+1, flag) ;
        System.out.printf("Calc: base = %d, deep = %d\n", base, deep);
        for(i = ss = 0; i < half; i ++)
        {
            a = in.get(ss+base) ;
            b = in.get(ss+base+step) ;
            System.out.printf("Calc: a = %s, b = %s, w = %s\n", a, b, w[i<<deep]);
            if(flag == 0) b.mul(w[i<<deep]) ;
            //else b = b/w[i<<deep] ;
            System.out.printf("CalcAfter: a = %s, b = %s\n", a, b);
            temp[i]  = new Complex(a.getReal(), a.getIm()) ;
            temp[i+half] = new Complex(a.getReal(), a.getIm()) ;
            temp[i].add(b) ;
            temp[i+half].sub(b) ;
            ss += (step<<1) ;
        }
        System.out.printf("\n");
        for(i = 0; i < num; i ++) {
            System.out.println(temp[i]);
            in.set((i << deep) + base, temp[i]);
        }
    }

    public static void calcFFT(List<Complex> in)
    {
        int len = Integer.toBinaryString(in.size()).length()-1 ;
        System.out.printf("Len: %d\n", len) ;
        getInstance(len).calculate(in,0, 0, 0);
    }
}
