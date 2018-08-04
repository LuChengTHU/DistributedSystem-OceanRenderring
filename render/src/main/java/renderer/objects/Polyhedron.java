package renderer.objects;

import renderer.utils.ObjReader;
import renderer.utils.OceanMaterial;
import renderer.utils.Vec3d;
import renderer.utils.Pair;

import java.io.IOException;
import java.util.StringTokenizer;

public class Polyhedron extends Primitive {
    Vec3d O, size, angles;
    Vec3d[] vertexN;
    Pair[] pixel;
    String meshFile;
    TriangleTree tree;

    public Vec3d getO() {
        return O;
    }

    public Vec3d getSize() {
        return size;
    }

    public Vec3d getAngles() {
        return angles;
    }

    public Vec3d getVertexN(int i) {
        return vertexN[i];
    }

    public TriangleTree getTree() {
        return tree;
    }

    public void setVertexN(Vec3d[] _vertexN) {
        vertexN = _vertexN;
    }

    public void setPixel(Pair[] _pixel) {
        pixel = _pixel;
    }

    Pair getPixel(int i) {
        return pixel[i];
    }

    public Polyhedron() {
        super();
        size = new Vec3d(1, 1, 1);
        angles = new Vec3d(0, 0, 0);
        tree = new TriangleTree();
        O = new Vec3d();
        super.material = new OceanMaterial();
    }

    @Override
    public void input(String var, String value) throws IOException {
        if (var.equals("O=")) O.input(value);
        if (var.equals("size=")) size.input(value);
        if (var.equals("angles=")) {
            angles.input(value);
            angles.mulToThis(Math.PI / 180.0);
        }
        super.input(var, value);
        StringTokenizer tk = new StringTokenizer(value);
        if (tk.hasMoreTokens()) {
            value = tk.nextToken();
        }
        if (var.equals("mesh=")) meshFile = value;
    }

    @Override
    public void preTreatment() {
        ObjReader objReader = new ObjReader();
        objReader.setPolyhedron(this);
        try {
            objReader.readObj(meshFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Collider collide(Vec3d ray_O, Vec3d ray_V) {
        return tree.collide(ray_O, ray_V);
    }

}
