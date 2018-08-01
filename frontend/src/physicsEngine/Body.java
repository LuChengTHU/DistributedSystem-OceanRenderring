package physicsEngine;

import core.Vec3;

import java.util.ArrayList;

public class Body {

    class ForceEvent {
        public float dt ;
        public Vec3 a ;
        public ForceEvent(Vec3 a, float dt) {
            this.dt = dt ;
            this.a = a ;
        }
    }

    static final float g = 9.81f ;
    static final float pi = 3.14159f ;

    Float mass ;
    Vec3 position ;
    Vec3 velocity ;
    ArrayList<ForceEvent> forces = new ArrayList<ForceEvent>() ;

    public Body(Float mass, Vec3 position, Vec3 velocity)
    {
        if(mass == 0) this.mass = 0.01f ;
        else this.mass = mass ;
        this.position = position ;
        this.velocity = velocity ;
    }


    public Vec3 getVelocity() {return velocity ;}
    public Vec3 getPosition() {return position ;}
    public void setPosition(Vec3 position) {this.position = position ;}
    public void setVelocity(Vec3 velocity) {this.position = velocity ;}

    public void addForce(Vec3 force, float dt)
    {
        forces.add(new ForceEvent(force.div(mass), dt)) ;
    }

    public void timeFlow(float time)
    {
        position = position.add(velocity.mul(time)) ;
        for(ForceEvent force : forces)
        {
            float dt = Math.max(0, time-force.dt) ;
            velocity = velocity.add(force.a.mul(dt)) ;
            position = position.add(force.a.mul(dt*dt/2)) ;
        }
    }

}
