package renderer.utils;

import java.io.BufferedReader;
import java.io.IOException;
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

    public Color add(Color A, Color B) {
        return new Color(A.r + B.r, A.g + B.g, A.b + B.b);
    }

    public Color sub(Color A, Color B) {
        return new Color(A.r - B.r, A.g - B.g, A.b - B.b);
    }

    public Color mul(Color A, Color B) {
        return new Color(A.r * B.r, A.g * B.g, A.b * B.b);
    }

    public Color mul(Color A, double k) {
        return new Color(A.r * k, A.g * k, A.b * k);
    }

    public Color div(Color A, double k) {
        return new Color(A.r / k, A.g / k, A.b / k);
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

    public void input(String value) {
        StringTokenizer tk = new StringTokenizer(value);
        r = Double.parseDouble(tk.nextToken());
        g = Double.parseDouble(tk.nextToken());
        b = Double.parseDouble(tk.nextToken());
    }

    public boolean isZeroColor() {
        return Math.abs(r) < EPS && Math.abs(g) < EPS && Math.abs(b) < EPS;
    }
}
