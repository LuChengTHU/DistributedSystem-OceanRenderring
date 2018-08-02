package physicsEngine;

import core.Vec3;

public class BoundingBox {

    public Vec3 p0, p1 ;

    public BoundingBox(Vec3 p0, Vec3 p1)
    {
        this.p0 = new Vec3(p0) ;
        this.p1 = new Vec3(p1) ;
    }

    static public BoundingBox valueOf(Ball b, Float timeSlide)
    {
        Vec3 before = new Vec3(b.position) ;
        Vec3 after = b.position.add(b.velocity.mul(timeSlide)) ;
        Vec3 p0 = new Vec3(Math.min(before.getX(), after.getX()), Math.min(before.getY(), after.getY()), Math.min(before.getZ(), after.getZ())).sub(b.radius) ;
        Vec3 p1 = before.add(after).sub(p0) ;
        System.out.println("Generating boundingbox: "+p0.toString()+" "+p1.toString()) ;
        return new BoundingBox(p0, p1) ;
    }

    boolean Intersect(BoundingBox b)
    {
        if(b.p0.getX() > p1.getX() || b.p1.getX() < p0.getX()) return false ;
        if(b.p0.getY() > p1.getY() || b.p1.getY() < p0.getY()) return false ;
        if(b.p0.getZ() > p1.getZ() || b.p1.getZ() < p0.getZ()) return false ;
        return true ;
    }
}
