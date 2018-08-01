package renderer.objects;

import renderer.utils.Pair;
import renderer.utils.Color;
import renderer.utils.IntPtr;
import renderer.utils.Vec3d;

import java.io.IOException;

import static renderer.utils.Vec3d.EPS;
import static renderer.utils.Vec3d.INF;

public class Triangle extends Primitive {

    private Polyhedron parent;
    private Vec3d N;
    private Vec3d[] pos;
    private IntPtr[] vertex, textureVertex, normalVectorID;
    private int mainCoord;
    private double nu, nv, nd, bnu, bnv, cnu, cnv;

    private Vec3d calnBump(Vec3d N, Vec3d C) {
        double totalArea = (pos[1].sub(pos[0])).cross(pos[2].sub(pos[0])).module();
        double area1 = (C.sub(pos[0])).cross(pos[2].sub(pos[0])).module();
        double area2 = (C.sub(pos[0])).cross(pos[1].sub(pos[0])).module();
        double x = 0, y = 0;
        if (totalArea > EPS) {
            x = area1 / totalArea;
            y = area2 / totalArea;
        }
        Pair p0 = parent.getPixel(textureVertex[0].get());
        Pair p1 = parent.getPixel(textureVertex[1].get());
        Pair p2 = parent.getPixel(textureVertex[2].get());
        double u1 = p1.getKey() - p0.getKey();
        double u2 = p2.getKey() - p0.getKey();
        double v1 = p1.getValue() - p0.getValue();
        double v2 = p2.getValue() - p0.getValue();
        if (u1 * v2 - u2 * v1 < EPS) return N;
        double u = p0.getKey() + u1 * x + u2 * y;
        double v = p0.getValue() + v1 * x + v2 * y;

        int H = material.bump.getH();
        int W = material.bump.getW();
        double deltaX = material.bump.getSmoothColor(u - 0.5 / H, v).power() - material.bump.getSmoothColor(u + 0.5 / H, v).power();
        double deltaY = material.bump.getSmoothColor(u, v - 0.5 / W).power() - material.bump.getSmoothColor(u, v + 0.5 / W).power();
        Vec3d tX = (pos[1].sub(pos[0]).mul(v2).sub(pos[2].sub(pos[0]).mul(v1))).div(H * (u1 * v2 - u2 * v1));
        Vec3d tY = (pos[1].sub(pos[0]).mul(u2).sub(pos[2].sub(pos[0]).mul(u1))).div(W * (v1 * u2 - v2 * u1));
        N = (N.add(tX.normalize().mul(deltaX * 10)).add(tY.normalize().mul(deltaY * 10))).normalize();

        return N;
    }

    public Triangle() {
        parent = null;
        N = new Vec3d();
        pos = new Vec3d[3];
        vertex = new IntPtr[3];
        textureVertex = new IntPtr[3];
        normalVectorID = new IntPtr[3];
        for (int i = 0; i < 3; i++) {
            pos[i] = new Vec3d();
            vertex[i] = new IntPtr();
            textureVertex[i] = new IntPtr();
            normalVectorID[i] = new IntPtr();
        }
    }

    public void setParent(Polyhedron _parent) {
        parent = _parent;
    }

    public Vec3d getPos(int i) {
        return pos[i];
    }

    public void setPos(int i, Vec3d p) {
        pos[i] = new Vec3d(p);
    }

    public IntPtr getVertex(int i) {
        return vertex[i];
    }

    public IntPtr getTextureVertex(int i) {
        return textureVertex[i];
    }

    public IntPtr getNormalVecctorID(int i) {
        return normalVectorID[i];
    }

    public Vec3d getN() {
        return N;
    }

    @Override
    public void input(String var, String value) throws IOException {
        if (var.equals("P0=")) pos[0].input(value);
        if (var.equals("P1=")) pos[1].input(value);
        if (var.equals("P2=")) pos[2].input(value);
        super.input(var, value);
    }

    @Override
    public void preTreatment() {
        Vec3d B = pos[2].sub(pos[0]);
        Vec3d C = pos[1].sub(pos[0]);
        N = C.cross(B);
        if (N.isZeroVector()) {
            N = new Vec3d(0.0, 0.0, 1.0);
            return;
        }
        if (Math.abs(N.x) > Math.abs(N.y)) {
            mainCoord = Math.abs(N.x) > Math.abs(N.z) ? 0 : 2;
        } else {
            mainCoord = Math.abs(N.y) > Math.abs(N.z) ? 1 : 2;
        }
        int u = (mainCoord + 1) % 3;
        int v = (mainCoord + 2) % 3;
        double krec = 1.0 / N.getCoord(mainCoord);
        nu = N.getCoord(u) * krec;
        nv = N.getCoord(v) * krec;
        nd = N.dot(pos[0]) * krec;
        double reci = 1.0 / (B.getCoord(u) * C.getCoord(v) - B.getCoord(v) * C.getCoord(u));
        bnu = B.getCoord(u) * reci;
        bnv = -B.getCoord(v) * reci;
        cnu = C.getCoord(u) * reci;
        cnv = -C.getCoord(v) * reci;
        N = N.normalize();
    }

    @Override
    public Collider collide(Vec3d ray_O, Vec3d ray_V) {
        Collider collider = new Collider();
        ray_V = ray_V.normalize();
        int u = (mainCoord + 1) % 3;
        int v = (mainCoord + 2) % 3;
        double lnd = 1.0 / (ray_V.getCoord(mainCoord) + nu * ray_V.getCoord(u) + nv * ray_V.getCoord(v));
        if (lnd > INF) return collider;
        double l = lnd * (nd - ray_O.getCoord(mainCoord) - nu * ray_O.getCoord(u) - nv * ray_O.getCoord(v));
        if (l < EPS) return collider;
        double hu = ray_O.getCoord(u) + l * ray_V.getCoord(u) - pos[0].getCoord(u);
        double hv = ray_O.getCoord(v) + l * ray_V.getCoord(v) - pos[0].getCoord(v);
        double x = hv * bnu + hu * bnv;
        double y = hv * cnu + hu * cnv;
        if (x < 0 || y < 0 || x + y > 1) return collider;
        if (parent != null && !parent.getVertexN(normalVectorID[0].get()).isZeroVector()) {
            collider.N = parent.getVertexN(normalVectorID[0].get()).mul(1 - x - y).add(parent.getVertexN(normalVectorID[1].get()).mul(x)).add(parent.getVertexN(normalVectorID[2].get()).mul(y));
        } else {
            collider.N = N;
        }

        double d = collider.N.dot(ray_V);
        collider.crash = true;
        collider.I = ray_V;
        collider.setPrimitive(this);
        collider.dist = l;
        collider.front = (d < 0);
        collider.C = ray_O.add(ray_V.mul(collider.dist));

        if (material.bump != null) {
            collider.N = calnBump(collider.N, collider.C);
        }

        if (!collider.front) {
            collider.N = collider.N.inv();
        }

        return collider;
    }

    @Override
    public Color getTexture(Vec3d C) {
        double totalArea = (pos[1].sub(pos[0])).cross(pos[2].sub(pos[0])).module();
        double area1 = (C.sub(pos[0])).cross(pos[2].sub(pos[0])).module();
        double area2 = (C.sub(pos[0])).cross(pos[1].sub(pos[0])).module();
        double x = 0, y = 0;
        if (totalArea > EPS) {
            x = area1 / totalArea;
            y = area2 / totalArea;
        }
        Pair p0 = parent.getPixel(textureVertex[0].get());
        Pair p1 = parent.getPixel(textureVertex[1].get());
        Pair p2 = parent.getPixel(textureVertex[2].get());
        double u1 = p1.getKey() - p0.getKey();
        double u2 = p2.getKey() - p0.getKey();
        double v1 = p1.getValue() - p0.getValue();
        double v2 = p2.getValue() - p0.getValue();
        double u = p0.getKey() + u1 * x + u2 * y;
        double v = p0.getValue() + v1 * x + v2 * y;
        return material.texture.getSmoothColor(u, v);
    }

    public double getMinCoord(int coord) {
        double x0 = pos[0].getCoord(coord);
        double x1 = pos[1].getCoord(coord);
        double x2 = pos[2].getCoord(coord);
        return Math.min(x0, Math.min(x1, x2));
    }

    public double getMaxCoord(int coord) {
        double x0 = pos[0].getCoord(coord);
        double x1 = pos[1].getCoord(coord);
        double x2 = pos[2].getCoord(coord);
        return Math.max(x0, Math.max(x1, x2));
    }


}
