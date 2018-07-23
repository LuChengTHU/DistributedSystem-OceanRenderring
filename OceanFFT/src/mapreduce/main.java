package mapreduce;

import core.Complex;
import core.Vec2;

public class main {

    public static int N = 512; // grad size
    public static int L = 1000;
    public static float g = 9.81f;
    public static float PI = 3.1415926535897932384626433832795f;
    public static Vec2 w = new Vec2()


    public static void generateH0k() {
        Complex[] h0k = new Complex[N * N];
        // k = (kx, kz), kx = 2πn/Lx, kz = 2πm/Lz
        // −N/2 ≤ n < N/2
        // −M/2 ≤ m < M/2.
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int n = -N/2 + i;
                int m = -N/2 + j;
                Vec2 k = new Vec2(2*PI*n/L, 2*PI*m/L);

            }
        }
        for (int n = -N/2; n < N/2; n++) {
            for (int m = -N/2; m < N/2; m++) {

            }
        }


    }
    public static void main(String[] args) throws Exception {

    }
}
