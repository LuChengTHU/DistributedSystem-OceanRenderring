package core;

import java.util.Random;

public class Conf {

    //world configuration
    public static final float lenXY = 2048;
    public static final float lenZ = 1000;
    public static final float seaLevel = 200;
    public static final float g = 9.81f;
    public static final float timeSlide = 1/3.0f ;

    public static final int resolution = 256;

    public static int totalFrame = 100;

    // configuration of oceanFFT
    public static final int L = 350;
    public static final float A = 0.0000038f;
    public static final float wSpeed = 50f;
    public static final float minWaveSize = 0.1f ;
    public static final Vec2 wDirection = new Vec2(1.0f, 0); // wind (1, 0)

}
