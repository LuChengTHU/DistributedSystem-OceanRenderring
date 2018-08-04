package renderer.utils;

import java.util.StringTokenizer;

import static renderer.utils.Vec3d.EPS;


public class Color {

    public double r, g, b;

    public Color() {
        r = g = b = 0.0;
    }

    public Color(double R, double G, double B) {
        r = R;
        g = G;
        b = B;
    }

    public Color(int rgb) {
        int R = (rgb >> 16) & 0xFF;
        int G = (rgb >> 8) & 0xFF;
        int B = rgb & 0xFF;
        r = ((double)R) / 255.0;
        g = ((double)G) / 255.0;
        b = ((double)B) / 255.0;
    }

    public Color(Color c) {
        r = c.r;
        g = c.g;
        b = c.b;
    }

    public Color add(Color A) {
        return new Color(A.r + r, A.g + g, A.b + b);
    }

    public Color sub(Color A) {
        return new Color(A.r - r, A.g - g, A.b - b);
    }

    public Color mul(Color A) {
        return new Color(A.r * r, A.g * g, A.b * b);
    }

    public Color mul(double k) {
        return new Color(r * k, g * k, b * k);
    }

    public Color div(double k) {
        return new Color(r / k, g / k, b / k);
    }

    public Color addToThis(Color A) {
        this.r += A.r;
        this.g += A.g;
        this.b += A.b;
        return this;
    }

    public Color subToThis(Color A) {
        this.r -= A.r;
        this.g -= A.g;
        this.b -= A.b;
        return this;
    }

    public Color mulToThis(Color A) {
        this.r *= A.r;
        this.g *= A.g;
        this.b *= A.b;
        return this;
    }

    public Color mulToThis(double k) {
        this.r *= k;
        this.g *= k;
        this.b *= k;
        return this;
    }

    public Color divToThis(double k) {
        this.r /= k;
        this.g /= k;
        this.b /= k;
        return this;
    }

    public Color confine() {
        return new Color(Math.min(r, 1.0), Math.min(g, 1.0), Math.min(b, 1.0));
    }

    public Color exp() {
        return new Color(Math.exp(r), Math.exp(g), Math.exp(b));
    }

    public double power() {
        return (r + g + b) / 3;
    }

    public double RGBMax() {
        if (r > g) return (r > b) ? r : b;
        return (g > b) ? g : b;
    }

    public int getRGB() {
        int R = (int)(Math.min(1, r) * 255);
        int G = (int)(Math.min(1, g) * 255);
        int B = (int)(Math.min(1, b) * 255);
        int value = (0xFF << 24) |
                ((R & 0xFF) << 16) |
                ((G & 0xFF) << 8)  |
                ((B & 0xFF) << 0);
        return value;
    }

    public void input(String value) {
        StringTokenizer tk = new StringTokenizer(value);
        r = Double.parseDouble(tk.nextToken());
        g = Double.parseDouble(tk.nextToken());
        b = Double.parseDouble(tk.nextToken());
    }

    public boolean isZeroColor() {
        return Math.abs(r) < EPS && Math.abs(g) < EPS && Math.abs(b) < EPS;
    }

    @Override
    public String toString() {
        return r + "," + g + "," + b;
    }
}
