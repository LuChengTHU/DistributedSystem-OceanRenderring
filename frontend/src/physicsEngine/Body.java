package physicsEngine;

import core.Vec3;

public class Body {
    static final float g = 9.81f ;
    Float mass ;
    Vec3 position ;
    Vec3 velocity ;

    public Vec3 getVelocity() {return velocity ;}
    public Vec3 getPosition() {return position ;}
    public void setPosition(Vec3 position) {this.position = position ;}
    public void setVelocity(Vec3 velocity) {this.position = velocity ;}

    public void addForce(Vec3 force, float time)
    {
        velocity = velocity.add(force.mul(time)) ;
    }
}
