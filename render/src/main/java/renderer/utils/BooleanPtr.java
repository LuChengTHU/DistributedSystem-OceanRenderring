package renderer.utils;

public class BooleanPtr {

    public boolean val;

    public BooleanPtr() {
        val = false;
    }

    public BooleanPtr(boolean v) {
        val = v;
    }

    public BooleanPtr set(boolean v) {
        val = v;
        return this;
    }

    public boolean get() {
        return val;
    }
}
