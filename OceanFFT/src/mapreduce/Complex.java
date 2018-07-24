package mapreduce;

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
    public Complex(String str)
    {
        String[] strArr = str.replaceAll("[\\(\\)]", "").split(",") ;
        this.real = Float.valueOf(strArr[0]);
        this.im   = Float.valueOf(strArr[1]);
    }

    public Complex add(Complex c)
    {
        return new Complex(real + c.real, im + c.im);
    }

    public Complex sub(Complex c)
    {
        return new Complex(real - c.real, im - c.im);
    }

    public Complex mul(Complex c)
    {
        return new Complex(real * c.real - im * c.im, real * c.im   + im * c.real);
    }

    public Complex mul(float i)
    {
        return new Complex(real * i, im * i);
    }

    public Complex conj() {
        return new Complex(real, -im);
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