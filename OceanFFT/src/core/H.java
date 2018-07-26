package core;

public class H {
    private int N;
    private Complex[] h;

    public H(int n) {
        N = n;
        h = new Complex[N * N];
    }

    public void set(int i, Complex c) {
        h[i] = c;
    }

    public Complex get(int i) {
        return h[i];
    }
}
