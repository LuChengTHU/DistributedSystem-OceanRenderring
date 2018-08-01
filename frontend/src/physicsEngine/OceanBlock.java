package physicsEngine;

import core.Vec2;
import core.Vec3;

import java.util.ArrayList;

// the volume of the world is
public class OceanBlock {
    float x0, y0 ;
    float x1, y1 ;
    float areaPerGrid ;
    Integer N ;
    ArrayList<Float> height ;
    float maxHeight ;

    public OceanBlock(float x0, float y0, float x1, float y1, Integer N)
    {
        this.x0 = x0 ;
        this.y0 = y0 ;
        this.x1 = x1 ;
        this.y1 = y1 ;
        this.N = N ;
        this.areaPerGrid = (x1-x0)*(y1-y0)/N/N ;
        maxHeight = 0 ;
    }

    public void inputHeight(ArrayList<Float> glHeight, int n, int m, int glN)
    {
        for(int i = 0; i < N; i ++)
            for(int j = 0; j < N; j ++) {
                float curHeight = glHeight.get((i + n) * glN + (j + m));
                height.add(curHeight);
                maxHeight = Math.max(maxHeight, curHeight);
            }
    }

    public int isInBlock(Ball b) // 0: not in the block at all 1: partially 2: totally
    {
        if(b.position.getZ()-b.radius > maxHeight) return 0 ;
        if(b.position.getX()-b.radius > x1 || b.position.getX()+b.radius < x0) return 0 ;
        if(b.position.getY()-b.radius > y1 || b.position.getY()+b.radius < y0) return 0 ;
        if(b.position.getX()-b.radius > x0 || b.position.getX()+b.radius < x1) return 1 ;
        if(b.position.getY()-b.radius > y0 || b.position.getY()+b.radius < y1) return 1 ;
        return 2 ;
    }

    public Vec3 calcBuoyant(Ball b, int inBlock)
    {
        if(inBlock == 0) return new Vec3(0, 0, 0) ;
        Vec3 buoyant = new Vec3(0, 0, 0) ;
        for(int i = 0; i < N; i ++)
            for(int j = 0; j < N; j ++)
            {
                Vec2 oceanPosition = new Vec2(x0+(x1-x0)/N, y0+(y1-y0)/N) ;
                float dist = oceanPosition.sub(b.position.toVec2()).length() ;
                if(dist <= b.radius)
                {
                    float D = (float)Math.sqrt(b.radius*b.radius-dist*dist) ;
                    float bottom = b.position.getZ()-D ;
                    if(bottom > height.get(i*N+j)) continue ;
                    float top = Math.min(height.get(i*N+j), b.position.getZ()+D) ;
                    float buoyantValue = (top-bottom)*b.g*areaPerGrid ;

                }
            }
        return buoyant ;
    }

    public Vec3 calcForce(Ball b) // the force of the water flow
    {
        return new Vec3(0, 0, 0) ;
    }
}
