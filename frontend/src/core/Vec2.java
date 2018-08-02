package core;

public class Vec2 {

    private float X;
    private float Y;

    public Vec2()
    {
        this.setX(0);
        this.setY(0);
    }

    public Vec2(float x, float y)
    {
        this.setX(x);
        this.setY(y);
    }

    public Vec2(Vec2 v)
    {
        this.X = v.getX();
        this.Y = v.getY();
    }

    public float length()
    {
        return (float) Math.sqrt(X*X + Y*Y);
    }

    public float dot(Vec2 r)
    {
        return X * r.getX() + Y * r.getY();
    }

    public Vec2 normalize()
    {
        float length = length();
        if (length < 0.0001) length = 0.0001f;
        X /= length;
        Y /= length;

        return this;
    }

    public Vec3 toVec3() {return new Vec3(this.X, this.Y, 0) ;}

    public Vec2 transform() {return new Vec2(this.Y, this.X) ;}

    public Vec2 add(Vec2 r)
    {
        return new Vec2(this.X + r.getX(), this.Y + r.getY());
    }

    public Vec2 add(float r)
    {
        return new Vec2(this.X + r, this.Y + r);
    }

    public Vec2 sub(Vec2 r)
    {
        return new Vec2(this.X - r.getX(), this.Y - r.getY());
    }

    public Vec2 sub(float r)
    {
        return new Vec2(this.X - r, this.Y - r);
    }

    public Vec2 mul(Vec2 r)
    {
        return new Vec2(this.X * r.getX(), this.Y * r.getY());
    }

    public Vec2 mul(float r)
    {
        return new Vec2(this.X * r, this.Y * r);
    }

    public Vec2 div(Vec2 r)
    {
        return new Vec2(this.X / r.getX(), this.Y / r.getY());
    }

    public Vec2 div(float r)
    {
        return new Vec2(this.X / r, this.Y / r);
    }

    public String toString()
    {
        return "[" + this.X + "," + this.Y + "]";
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

}