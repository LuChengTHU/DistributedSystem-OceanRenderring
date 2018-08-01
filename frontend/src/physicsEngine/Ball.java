package physicsEngine;

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
        return -1 ;
    }
/*
    float collideTimeWithWall(Wall wall) // if return value is less than 0, means that there will be no collision at all.
    {

    }
*/

    public String toString() {
        return String.format("Ball: %d %f %f %f %f %f %f %f %f", id, radius, density, position.getX(),
                position.getY(), position.getZ(), velocity.getX(), velocity.getY(), velocity.getZ()) ;
    }

}
