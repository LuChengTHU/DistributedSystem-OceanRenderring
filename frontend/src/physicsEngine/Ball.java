package physicsEngine;

import core.Conf;
import core.Vec3;

public class Ball extends Body {

    class collision {
        Vec3 velocity ; // relative velocity
        float mass ; // mass of the other stuff
        Vec3 normal ;// direction of the collision
    }

    float radius ;
    float density ;
    int id ;
    public Ball(int id, float radius, float density, Vec3 position, Vec3 velocity)
    {
        super((float)Math.pow(radius, 3)*pi/3*4*density, position, velocity) ;
        this.density = density ;
        this.radius = radius ;
        this.id = id ;
    }
    static Ball valueOf(String str)
    {
        str = str.substring(6) ;
        String[] arr = str.split(" ") ;
        int id = Integer.valueOf(arr[0]) ;
        float radius = Float.valueOf(arr[1]) ;
        float density = Float.valueOf(arr[2]) ;
        Vec3 position = new Vec3(Float.valueOf(arr[3]), Float.valueOf(arr[4]), Float.valueOf(arr[5])) ;
        Vec3 velocity = new Vec3(Float.valueOf(arr[6]), Float.valueOf(arr[7]), Float.valueOf(arr[8])) ;
        return new Ball(id, radius, density, position, velocity) ;
    }

    float collideTimeWithBall(Ball other) // if return value is less than 0, means that there will be no collision at all.
    {
        Vec3 v = position.sub(other.position) ;
        Vec3 ray = velocity.sub(other.velocity) ;
        Vec3 rayDir = ray.normalize() ;
        Float r = other.radius+radius ;

        if(r >= v.length()) return 0 ;

        float b = 2.0f * rayDir.dot(v);
        float c = v.dot(v) - r*r;
        float discriminant = (b * b) - (4.0f * c);

        if (discriminant < 0.0f)
            return -100 ;

        discriminant = (float)Math.sqrt(discriminant);

        System.out.println(b) ;
        System.out.println(discriminant) ;

        return (-b - discriminant) / 2.0f / ray.length();
    }

    Vec3 collideForce(Ball other, Float collideTime)
    {
        Vec3 v = other.position.sub(position) ;
        Vec3 ray = velocity.sub(other.velocity) ;
        Float r = other.radius+radius ;
        Vec3 returnForce = new Vec3(0, 0, 0) ;
        Vec3 n ;
        if(r >= v.length()) // means that this two ball have already intersected
        {
            Float interDist = (r-v.length())*20000 ;
            returnForce = returnForce.add(v.normalize().mul(-interDist)) ;
            collideTime = 0f ;
        }
        n = v.sub(ray.mul(collideTime)).normalize() ;
        Vec3 nv = velocity.sub(n.mul(2f*other.mass/(other.mass+mass)*ray.dot(n))) ;
        returnForce = returnForce.add((nv.sub(velocity)).div(Conf.timeSlide-collideTime).mul(mass)) ;
        return returnForce ;
    }


    public String toString() {
        return String.format("Ball: %d %f %f %f %f %f %f %f %f", id, radius, density, position.getX(),
                position.getY(), position.getZ(), velocity.getX(), velocity.getY(), velocity.getZ()) ;
    }

}
