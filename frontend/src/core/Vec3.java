package core;

public class Vec3 {

    private float X;
    private float Y;
    private float Z;

    public Vec3()
    {
        this.setX(0);
        this.setY(0);
        this.setZ(0);
    }

    public Vec3(float x, float y, float z)
    {
        this.setX(x);
        this.setY(y);
        this.setZ(z);
    }

    public Vec3(Vec3 v)
    {
        this.X = v.getX();
        this.Y = v.getY();
        this.Z = v.getZ();
    }

    public float length()
    {
        return (float) Math.sqrt(X*X + Y*Y + Z*Z);
    }

    public float dot(Vec3 r)
    {
        return X * r.getX() + Y * r.getY() + Z * r.getZ() ;
    }

    public Vec2 toVec2() {return new Vec2(X, Y) ;}

    public Vec3 normalize()
    {
        float length = length();
        if (length < 0.0001) length = 0.0001f;
        X /= length;
        Y /= length;
        Z /= length;
        return this;
    }

    public Vec3 add(Vec3 r)
    {
        return new Vec3(this.X + r.getX(), this.Y + r.getY(), this.Z+r.getZ());
    }

    public Vec3 add(float r)
    {
        return new Vec3(this.X + r, this.Y + r, this.Z+r);
    }

    public Vec3 sub(Vec3 r)
    {
        return new Vec3(this.X - r.getX(), this.Y - r.getY(), this.Z - r.getZ());
    }

    public Vec3 sub(float r)
    {
        return new Vec3(this.X - r, this.Y - r, this.Z - r);
    }

    public Vec3 mul(Vec3 r)
    {
        return new Vec3(this.X * r.getX(), this.Y * r.getY(), this.Z * r.getZ());
    }

    public Vec3 mul(float r)
    {
        return new Vec3(this.X * r, this.Y * r, this.Z * r);
    }

    public Vec3 div(Vec3 r)
    {
        return new Vec3(this.X / r.getX(), this.Y / r.getY(), this.Z / r.getZ());
    }

    public Vec3 div(float r)
    {
        return new Vec3(this.X / r, this.Y / r, this.Z / r);
    }

    public String toString()
    {
        return "[" + this.X + "," + this.Y + "," + this.Z + "]";
    }

    public float getX() {
        return X;
    }

    public void setX(float x) {
        X = x;
    }

    public float getY() {
        return Y;
    }

    public void setY(float y) {
        Y = y;
    }

    public float getZ() {
        return Z;
    }

    public void setZ(float z) {
        Z = z;
    }

}