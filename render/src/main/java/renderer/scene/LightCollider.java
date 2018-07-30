package renderer.scene;

public class LightCollider {

    public Light light;
    public boolean crash;
    public double dist;

    public LightCollider() {
        light = null;
        crash = false;
    }

    public Light getLight() {
        return light;
    }

    public void setLight(Light _light) {
        light = _light;
    }
}
