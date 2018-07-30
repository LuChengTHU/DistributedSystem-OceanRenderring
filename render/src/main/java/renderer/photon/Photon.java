package renderer.photon;

import renderer.utils.Color;
import renderer.utils.Vec3d;

public class Photon {

    public Vec3d pos , dir;
    public Color power;
    public int plane;

    public Photon() {
        pos = new Vec3d();
        dir = new Vec3d();
        power = new Color();
    }
}
