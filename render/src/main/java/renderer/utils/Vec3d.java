package renderer.utils;

import java.util.StringTokenizer;

public class Vec3d {
    public static double INF = 1e8;
    public static double EPS = 1e-8;

    public double x, y, z;

    public Vec3d() {
        x = 0.0;
        y = 0.0;
        z = 0.0;
    }

    public Vec3d(double v) {
        x = v;
        y = v;
        z = v;
    }

    public Vec3d(double _x, double _y, double _z) {
        x = _x;
        y = _y;
        z = _z;
    }

    public Vec3d(Vec3d v) {
        x = v.x;
        y = v.y;
        z = v.z;
    }

    public void set(Vec3d v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public Vec3d normalize() {
        Vec3d ret = new Vec3d(0.0, 0.0, 1.0);
        double nor = module();
        if (nor > EPS) {
            ret = div(nor);
        }
        return ret;
    }

    public double module2() {
        return x * x + y * y + z * z;
    }

    public double module() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public double distance2(Vec3d term) {
        return term.sub(this).module2();
    }

    public double distance(Vec3d term) {
        return term.sub(this).module();
    }

    public double getCoord(int axis) {
        if (axis == 0) return x;
        if (axis == 1) return y;
        if (axis == 2) return z;
        return 0.0;
    }

    public Vec3d rotate(Vec3d axis, double theta) {
        Vec3d ret = new Vec3d();
        double cost = Math.cos( theta );
        double sint = Math.sin( theta );
        ret.x += x * ( axis.x * axis.x + ( 1 - axis.x * axis.x ) * cost );
        ret.x += y * ( axis.x * axis.y * ( 1 - cost ) - axis.z * sint );
        ret.x += z * ( axis.x * axis.z * ( 1 - cost ) + axis.y * sint );
        ret.y += x * ( axis.y * axis.x * ( 1 - cost ) + axis.z * sint );
        ret.y += y * ( axis.y * axis.y + ( 1 - axis.y * axis.y ) * cost );
        ret.y += z * ( axis.y * axis.z * ( 1 - cost ) - axis.x * sint );
        ret.z += x * ( axis.z * axis.x * ( 1 - cost ) - axis.y * sint );
        ret.z += y * ( axis.z * axis.y * ( 1 - cost ) + axis.x * sint );
        ret.z += z * ( axis.z * axis.z + ( 1 - axis.z * axis.z ) * cost );
        return ret;
    }

    public void asRandomVector() {
        do {
            x = 2 * Math.random() - 1;
            y = 2 * Math.random() - 1;
            z = 2 * Math.random() - 1;
        } while (x * x + y * y + z * z > 1 || x * x + y * y + z * z < EPS);
        Vec3d ret = normalize();
        x = ret.x;
        y = ret.y;
        z = ret.z;
    }

    public Vec3d getAnVerticalVector() {
        Vec3d ret = cross(new Vec3d(0.0, 0.0, 1.0));
        if (ret.isZeroVector()) ret = new Vec3d(1.0, 0.0, 0.0);
        else ret = ret.normalize();
        return ret;
    }

    public boolean isZeroVector() {
        return Math.abs(x) < EPS && Math.abs(y) < EPS && Math.abs(z) < EPS;
    }

    public Vec3d reflect(Vec3d N) {
        return sub(N.mul( 2 * this.dot(N)));
    }

    public Vec3d refract(Vec3d N, double n, BooleanPtr refracted) {
        Vec3d V = normalize();
        double cosI = -N.dot(V);
        double cosT2 = 1 - ( n * n ) * ( 1 - cosI * cosI );
        if (cosT2 > EPS) {
            if (refracted != null) {
                refracted.set(!refracted.get());
            }
            return V.mul(n).add(N.mul(n * cosI - Math.sqrt( cosT2 )));
        }
        return V.reflect(N);
    }

    public Vec3d diffuse() {
        Vec3d Vert = getAnVerticalVector();
        double theta = Math.acos(Math.sqrt(Math.random()));
        double phi = Math.random() * 2 * Math.PI;
        return rotate(Vert, theta).rotate(this, phi);
    }

    public Vec3d cross(Vec3d v) {
        return new Vec3d(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
    }

    public Vec3d mul(double d) {
        return new Vec3d(x * d, y * d, z * d);
    }

    public Vec3d mul(Vec3d v) {
        return new Vec3d(x * v.x, y * v.y, z * v.z);
    }

    public Vec3d div(double d) {
        return new Vec3d(x / d, y / d, z / d);
    }

    public double dot(Vec3d v) {
        return x * v.x + y * v.y + z * v.z;
    }

    public Vec3d add(double d) {
        return new Vec3d(x + d, y + d, z + d);
    }

    public Vec3d add(Vec3d v) {
        return new Vec3d(x + v.x, y + v.y, z + v.z);
    }

    public Vec3d sub(double d) {
        return add(-d);
    }

    public Vec3d sub(Vec3d v) {
        return add(v.inv());
    }

    public Vec3d addToThis(Vec3d v) {
        x += v.x;
        y += v.y;
        z += v.z;
        return this;
    }

    public Vec3d addToThis(double d) {
        x += d;
        y += d;
        z += d;
        return this;
    }

    public Vec3d subToThis(Vec3d v) {
        x -= v.x;
        y -= v.y;
        z -= v.z;
        return this;
    }

    public Vec3d subToThis(double d) {
        x -= d;
        y -= d;
        z -= d;
        return this;
    }

    public Vec3d mulToThis(Vec3d v) {
        x *= v.x;
        y *= v.y;
        z *= v.z;
        return this;
    }

    public Vec3d mulToThis(double d) {
        x *= d;
        y *= d;
        z *= d;
        return this;
    }

    public Vec3d divToThis(double d) {
        x /= d;
        y /= d;
        z /= d;
        return this;
    }

    public Vec3d inv() {
        return new Vec3d(-x, -y, -z);
    }

    public String serialize() {
        return "[" + x + "," + y + "," + z + "]";
    }

    public static Vec3d deSerialize(String ser) {
        Vec3d vec = new Vec3d();
        String con = ser.substring(1, ser.length() - 1);
        String[] ds = con.split(",");
        vec.x = Double.parseDouble(ds[0]);
        vec.y = Double.parseDouble(ds[1]);
        vec.z = Double.parseDouble(ds[2]);
        return vec;
    }

    public boolean isParallel(Vec3d v) {
        Vec3d cro = cross(v);
        Vec3d zero = new Vec3d();
        if (cro.equals(zero))
            return true;
        return false;
    }

    public void input(String value) {
        StringTokenizer tk = new StringTokenizer(value);
        x = Double.parseDouble(tk.nextToken());
        y = Double.parseDouble(tk.nextToken());
        z = Double.parseDouble(tk.nextToken());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vec3d) {
            Vec3d v = (Vec3d) obj;
            return (x == v.x) && (y == v.y) && (z == v.z);
        }
        return false;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + "," + z + ")";
    }
}
