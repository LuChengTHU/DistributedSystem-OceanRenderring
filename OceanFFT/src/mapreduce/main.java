package mapreduce;

import core.Complex;
import core.H;
import core.Vec2;
import java.util.Random;

public class main {

    public static int N = 256; // grad size
    public static int L = 1000;
    public static float g = 9.81f;
    public static int T = 100; // num of frames

    public static Vec2 wDirection = new Vec2(1.0f, 0); // wind (1, 0)
    public static float wSpeed = 26f;
    public static Random random = new Random();
    public static float A = 20f;

    public static void generateH0k() {

        H h0k = new H(N, L, A, wDirection, wSpeed, random);
        h0k.init();
    }

    public static void generateHkt() {

    }
    public static void main(String[] args) throws Exception {
        generateH0k();
        for (int i = 0; i < T; i++) {

        }
    }
}
