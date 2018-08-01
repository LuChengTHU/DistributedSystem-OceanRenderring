package renderer.utils;

public class Pair {
    double first;
    double second;

    public Pair(double a, double b) {
        first = a;
        second = b;
    }

    public Pair() {
        first = second = 0;
    }

    public double getKey() {
        return first;
    }

    public double getValue() {
        return second;
    }
}
