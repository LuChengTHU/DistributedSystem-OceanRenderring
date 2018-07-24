package renderer.utils;

public class IntPtr {

    public int val;

    public IntPtr() {
        val = 0;
    }

    public IntPtr set(int v) {
        val = v;
        return this;
    }

    public int get() {
        return val;
    }
}
