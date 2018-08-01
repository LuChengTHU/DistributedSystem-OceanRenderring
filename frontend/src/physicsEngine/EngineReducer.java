package physicsEngine;

import core.Conf;
import core.Vec2;
import core.Vec3;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.ArrayList;

public class EngineReducer extends Reducer<IntWritable,Text,IntWritable,Text> {

    private IntWritable keyValue = new IntWritable() ;
    private Text result = new Text();
    private Ball keyBall ;
    private ArrayList<Ball> others = new ArrayList<Ball>() ;
    private ArrayList<Float> oceanHeight = new ArrayList<Float>() ;
    private float lenXY = 0f ;
    private float lenZ = 0f ;
    private float seaLevel = 0f ;
    private float areaPerGrid = 0f ;
    private float timeSlide = 0f ;
    private float currentTime = 0f ;
    private Integer oceanRes = 0 ;

    public void setup(Context context) // Input the BoundingBox information
    {
        lenXY = context.getConfiguration().getFloat("lenXY", 2048) ;
        lenZ = context.getConfiguration().getFloat("lenZ", 1000) ;
        seaLevel = context.getConfiguration().getFloat("seaLevel", 200) ;
        oceanRes = context.getConfiguration().getInt("oceanRes", 256) ;
        timeSlide = context.getConfiguration().getFloat("timeSlide", 1/3.0f) ;
        currentTime = context.getConfiguration().getFloat("currentTime", 0f) ;
        areaPerGrid = lenXY*lenXY/oceanRes/oceanRes ;

        // input sea surface
        System.out.println(oceanRes);
        for(int i = 0; i < oceanRes; i ++)
            for(int j = 0; j < oceanRes; j ++) {
                oceanHeight.add(100 + 90 * (float) Math.sin(((i + j) * 8 - 2048) * 3.14159 / 400 + currentTime / 3));
                //if (i == 0) System.out.println(oceanHeight.get(i*oceanRes+j));
            }
    }

    public void reduce(IntWritable key, Iterable<Text> values,
                       Context context
    ) throws IOException, InterruptedException {

        Integer ballID = key.get() ;

        for(Text val : values) {
            String valStr = val.toString();
            if (valStr.charAt(0) == 'B') {
                Ball ball = Ball.valueOf(valStr);
                if (ball.id == ballID)
                    keyBall = ball;
                else others.add(ball);
            }
        }

        System.out.println("time: "+currentTime) ;
        Vec3 p0 = keyBall.position.sub(keyBall.radius);
        Vec3 p1 = keyBall.position.add(keyBall.radius);
        boolean contactWithWater = false ;

        if(p0.getZ() <= seaLevel) // interact with ocean
        {
            Integer lt = Math.max(0, (int) Math.floor(p0.getX() / lenXY * oceanRes));
            Integer rt = Math.min(oceanRes - 1, (int) Math.floor(p1.getX() / lenXY * oceanRes));
            Integer up = Math.max(0, (int) Math.floor(p0.getY() / lenXY * oceanRes));
            Integer dn = Math.min(oceanRes - 1, (int) Math.floor(p1.getY() / lenXY * oceanRes));
            System.out.println("coordinate: "+lt+" "+rt+" "+up+" "+dn) ;
            Vec3 buoyant = new Vec3(0, 0, 0);
            for (int i = lt; i <= rt; i++)
                for (int j = up; j <= dn; j++) {
                    Vec2 oceanPosition = new Vec2((i + 0.5f) * lenXY / oceanRes, (j + 0.5f) * lenXY / oceanRes);
                    Vec2 xyDt = oceanPosition.sub(keyBall.position.toVec2()) ;
                    //xyDt.setX(Math.abs(xyDt.getX()));
                    //xyDt.setY(Math.abs(xyDt.getY()));
                    //if(xyDt.length() > 0.0001f)
                    //   xyDt = xyDt.sub(0.5f*lenXY/oceanRes) ;
                    float dist = xyDt.length();
                    if (dist <= keyBall.radius) {
                        float D = (float) Math.sqrt(keyBall.radius * keyBall.radius - dist * dist);
                        float bottom = keyBall.position.getZ() - D;
                        if (bottom > oceanHeight.get(i * oceanRes + j)) continue;
                        contactWithWater = true ;
                        float top = Math.min(oceanHeight.get(i * oceanRes + j), keyBall.position.getZ() + D);
                        float buoyantValue = (top - bottom) * keyBall.g * areaPerGrid;
                        Vec3 surfPoint = new Vec3(oceanPosition.getX(), oceanPosition.getY(), bottom);
                        Vec3 forceDir = new Vec3(0, 0, 1) ;//keyBall.position.sub(surfPoint).normalize();
                        buoyant = buoyant.add(forceDir.mul(buoyantValue));
                    }
                }
            keyBall.addForce(buoyant, 0);
            System.out.println("buoyant: "+buoyant) ;
        }

        Vec3 gravity = new Vec3(0, 0, -Conf.g*keyBall.mass) ;
        keyBall.addForce(gravity,0);
        System.out.println("gravity: "+gravity) ;

        keyBall.timeFlow(timeSlide);
        if(contactWithWater == true)
            keyBall.velocity = keyBall.velocity.mul(0.85f) ;
        keyValue.set(ballID) ;
        result.set(keyBall.toString()) ;
        context.write(keyValue, result);
    }
}
