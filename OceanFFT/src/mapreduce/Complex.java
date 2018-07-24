package mapreduce;

public class Complex {

    private float real;
    private float im;

    public Complex(float real, float im)
    {
        this.real = real;
        this.im   = im;
    }

    public Complex add(Complex c)
    {
        real += c.real;
        im   += c.im;
        return new Complex(real + c.real, im + c.im);
    }

    public void sub(Complex c)
    {
        real -= c.real;
        im   -= c.im;
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

}