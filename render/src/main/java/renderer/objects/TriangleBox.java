package renderer.objects;

import renderer.utils.Vec3d;

import static renderer.utils.Vec3d.EPS;
import static renderer.utils.Vec3d.INF;

public class TriangleBox {

    public Vec3d minPos, maxPos;

    public TriangleBox() {
        minPos = new Vec3d(INF, INF, INF);
        maxPos = new Vec3d(-INF, -INF, -INF);
    }

    public TriangleBox(TriangleBox box) {
        minPos = new Vec3d(box.minPos);
        maxPos = new Vec3d(box.maxPos);
    }

    public void update(Triangle tri) {
        for (int i = 0; i < 3; i++) {
            if (tri.getMinCoord(i) < minPos.getCoord(i)) minPos.setCoord(i, tri.getMinCoord(i));
            if (tri.getMaxCoord(i) > maxPos.getCoord(i)) maxPos.setCoord(i, tri.getMaxCoord(i));
        }
    }

    public boolean contain(Vec3d O) {
        for (int i = 0; i < 3; i++) {
            if (O.getCoord(i) <= minPos.getCoord(i) - EPS || O.getCoord(i) >= maxPos.getCoord(i) + EPS) return false;
        }
        return true;
    }

    public double calnArea() {
        double a = maxPos.x - minPos.x;
        double b = maxPos.y - minPos.y;
        double c = maxPos.z - minPos.z;
        return 2 * (a * b + b * c + c * a);
    }

    public double collide(Vec3d ray_O, Vec3d ray_V) {
//        ray_V = ray_V.normalize();
//        double txmin, txmax, tymin, tymax, tzmin, tzmax;
//        double bxmin = minPos.x, bxmax = maxPos.x;
//        double bymin = minPos.y, bymax = maxPos.y;
//        double bzmin = minPos.z, bzmax = maxPos.z;
//        if (ray_V.x < EPS) {
//            txmin = -INF;
//            txmax = INF;
//        }
//        else {
//            txmin = (bxmin - ray_O.x) / ray_V.x;
//            txmax = (bxmax - ray_O.x) / ray_V.x;
//        }
//        if (ray_V.y < EPS) {
//            tymin = -INF;
//            tymax = INF;
//        }
//        else {
//            tymin = (bymin - ray_O.y) / ray_V.y;
//            tymax = (bymax - ray_O.y) / ray_V.y;
//        }
//        if (ray_V.z < EPS) {
//            tzmin = -INF;
//            tzmax = INF;
//        }
//        else {
//            tzmin = (bzmin - ray_O.z) / ray_V.z;
//            tzmax = (bzmax - ray_O.z) / ray_V.z;
//        }
//        if (txmin > txmax)
//        {
//            double tmp = txmin;
//            txmin = txmax;
//            txmax = tmp;
//        }
//        if (tymin > tymax) {
//            double tmp = tymin;
//            tymin = tymax;
//            tymax = tmp;
//        }
//        if (tzmin > tzmax) {
//            double tmp = tzmin;
//            tzmin = tzmax;
//            tzmax = tmp;
//        }
//        double tmin = Math.max(Math.max(txmin, tymin), tzmin);
//        double tmax = Math.min(Math.min(txmax, tymax), tzmax);
//        if (bxmin <= ray_O.x + 0.001 && ray_O.x <= bxmax + 0.001 && bymin <= ray_O.y + 0.001 &&ray_O.y <= bymax + 0.001 && bzmin <= ray_O.z + 0.001 && ray_O.z <= bzmax + 0.001)return tmax > EPS ? tmax : -1;
//        else if (tmin >= tmax) return -1;
//        else return tmin > EPS ? tmin : -1;


        double minDist = -1;
        for (int coord = 0; coord < 3; coord++) {
            double times = -1;
            if (ray_V.getCoord(coord) >= EPS)
                times = (minPos.getCoord(coord) -ray_O.getCoord(coord)) / ray_V.getCoord(coord);
            if (ray_V.getCoord(coord) <= -EPS)
                times = (maxPos.getCoord(coord) -ray_O.getCoord(coord)) / ray_V.getCoord(coord);
            if (times >= EPS) {
                Vec3d C = ray_O.add(ray_V.mul(times));
                if (contain(C)) {
                    double dist = ray_O.distance(C);
                    if (minDist <= -EPS || dist < minDist)
                        minDist = dist;
                }
            }
        }
        return minDist;
    }
}
