package physicsEngine;

import core.Conf;
import core.Vec2;
import core.Vec3;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
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
    private ArrayList<OceanBlock> oceans = new ArrayList<OceanBlock>() ;
    private float currentTime = 0f ;
    private float areaPerGrid ;

    public void setup(Context context)
    {
        currentTime = context.getConfiguration().getFloat("currentTime", 0f) ;
        areaPerGrid = (float)Math.pow(Conf.lenXY/Conf.resolution, 2) ;
        System.out.println(areaPerGrid);
        //oceanHeight.add(100 + 90 * (float) Math.sin(((i + j) * 8 - 2048) * 3.14159 / 400 + currentTime / 3));
    }

    public void reduce(IntWritable key, Iterable<Text> values,
                       Context context
    ) throws IOException, InterruptedException {

        Integer ballID = key.get() ;

        oceans = new ArrayList<OceanBlock>() ;
        others = new ArrayList<Ball>() ;

        for(Text val : values) {
            String valStr = val.toString();
            if (valStr.charAt(0) == 'B') {
                Ball ball = Ball.valueOf(valStr);
                if (ball.id == ballID)
                    keyBall = ball;
                else others.add(ball);
            }
            else oceans.add(new OceanBlock(valStr)) ;
        }

        boolean contactWithWater = false ;
        Vec3 sumBuoyant = new Vec3(0, 0, 0) ;
        for(OceanBlock block : oceans)
        {
            Vec3 buoyant = block.calcBuoyant(keyBall, areaPerGrid) ;
            sumBuoyant = sumBuoyant.add(buoyant) ;
            if(buoyant.length()>0f)
                contactWithWater = true ;
        }

        Vec3 gravity = new Vec3(0, 0, -Conf.g*keyBall.mass) ;
        keyBall.addForce(gravity,0);
        System.out.println("gravity: "+gravity) ;
        keyBall.addForce(sumBuoyant,0);
        System.out.println("sumBuoyant: "+sumBuoyant) ;

        //collisions

        Float miniTime = Conf.timeSlide+10f ;
        for(Ball other : others)
        {
            Float collisionTime = keyBall.collideTimeWithBall(other) ;
            System.out.println("others!") ;
            System.out.println(keyBall) ;
            System.out.println(other) ;
            System.out.println(collisionTime) ;
            if(collisionTime >= 0 && collisionTime < miniTime)
                miniTime = collisionTime ;
        }
        if(miniTime <= Conf.timeSlide) // there's a collision
        {
            for(Ball other : others)
            {
                Float collisionTime = keyBall.collideTimeWithBall(other) ;
                if(collisionTime >= 0 && collisionTime < miniTime+Conf.collisionSlide)
                {
                    System.out.println("Collision!") ;
                    System.out.println(keyBall) ;
                    System.out.println(other) ;
                    System.out.println(miniTime) ;
                    Vec3 colForce = keyBall.collideForce(other, miniTime) ;
                    System.out.println(colForce) ;
                    keyBall.addForce(colForce, miniTime);
                }
            }
        }


        keyBall.timeFlow(Conf.timeSlide);
        if(contactWithWater == true)
            keyBall.velocity = keyBall.velocity.mul(0.85f) ;

        keyValue.set(ballID) ;
        result.set(keyBall.toString()) ;
        context.write(keyValue, result);
    }
}
