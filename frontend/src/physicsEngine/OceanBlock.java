package physicsEngine;

import core.Conf;
import core.Vec2;
import core.Vec3;

import java.util.ArrayList;

// the volume of the world is
public class OceanBlock {
    Integer row, lt ;
    ArrayList<Float> height ;
    float maxHeight ;

    public OceanBlock(String str)
    {
        String[] strArr = str.split(" ") ;
        this.row = Integer.valueOf(strArr[0]) ;
        this.lt = Integer.valueOf(strArr[1]) ;
        maxHeight = 0 ;
        height = new ArrayList<>() ;
        for(int i = 2; i < strArr.length; i ++)
        {
            Float tmp = Float.valueOf(strArr[i])*Conf.worldSeaScale;
            height.add(tmp);
            maxHeight = Math.max(maxHeight, tmp);
        }
    }

    public Vec3 calcBuoyant(Ball b, Float areaPerGrid)
    {
        Vec3 buoyant = new Vec3(0, 0, 0) ;
        Vec3 push = new Vec3(0, 0, 0);
        Integer col ;
        for (int i = 0; i < height.size(); i++) {
            col = i + lt;
            Vec2 oceanPosition = new Vec2((row + 0.5f) * Conf.lenXY / Conf.resolution, (col + 0.5f) * Conf.lenXY / Conf.resolution);
            Vec2 xyDt = oceanPosition.sub(b.position.toVec2());
            float dist = xyDt.length();
            if (dist <= b.radius) {
                float D = (float) Math.sqrt(b.radius * b.radius - dist * dist);
                float bottom = b.position.getZ() - D;
                if (bottom > height.get(i)) continue;
                float top = Math.min(height.get(i), b.position.getZ() + D);
                float buoyantValue = (top - bottom) * Conf.g * areaPerGrid;
                Vec3 surfPoint = new Vec3(oceanPosition.getX(), oceanPosition.getY(), bottom);
                Vec3 buoyantDir = new Vec3(0, 0, 1);
                Vec3 pushDir = b.position.sub(surfPoint).normalize();
                pushDir = pushDir.add(Conf.wDirection.transform().toVec3().mul(0.0f)).normalize();
                pushDir.setZ(0);
                buoyant = buoyant.add(buoyantDir.mul(buoyantValue));
                push = push.add(pushDir.mul(buoyantValue));
            }
        }
        buoyant = buoyant.add(push) ;
        System.out.println("buoyant: "+buoyant) ;
        return buoyant ;
    }

    public Vec3 calcForce(Ball b) // the force of the water flow
    {
        return new Vec3(0, 0, 0) ;
    }
}
