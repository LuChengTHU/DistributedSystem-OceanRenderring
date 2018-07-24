package core;

import java.io.OutputStream;
import java.util.stream.Stream;

public class Complex {

    private float real;
    private float im;

    public Complex(float real, float im)
    {
        this.real = real;
        this.im   = im;
    }
    public Complex(double real, double im)
    {
        this.real = (float)real;
        this.im   = (float)im;
    }

    public void add(Complex c)
    {
        real += c.real;
        im   += c.im;
    }

    public void sub(Complex c)
    {
        real -= c.real;
        im   -= c.im;
    }

    public void mul(Complex c)
    {
        real = real * c.real - im * c.im;
        im   = real * c.im   + im * c.real;
    }

    public void mul(float i)
    {
        real *= i;
        im   *= i;
    }

    public float getReal() {
        return real;
    }

    public void setReal(float real) {
        this.real = real;
    }

    public float getIm() {
        return im;
    }

    public void setIm(float im) {
        this.im = im;
    }

    @Override
    public String toString() {
        return String.format("(%f, %f)", real, im);
    }
}