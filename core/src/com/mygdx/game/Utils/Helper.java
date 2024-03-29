package com.mygdx.game.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.awt.*;

public final class Helper {

    /**
     * CLASS TO HOLD SOME IMPORTANT AND USEFUL MATH METHODS
     */

    final static float EPSILON = 1e-12f;

    /**
     * round a float value to a certain decimal places
     * @param value
     * @param places
     * @return rounded value
     */
    public static float round(float value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (float) tmp / factor;
    }

    public static float map(float valueCoord1, float startCoord1, float endCoord1, float startCoord2, float endCoord2) {

        if (Math.abs(endCoord1 - startCoord1) < EPSILON) {
            throw new ArithmeticException("/ 0");
        }

        float offset = startCoord2;
        float ratio = (endCoord2 - startCoord2) / (endCoord1 - startCoord1);
        return ratio * (valueCoord1 - startCoord1) + offset;
    }

    public static double map(double valueCoord1, double startCoord1, double endCoord1, double startCoord2, double endCoord2) {

        if (Math.abs(endCoord1 - startCoord1) < EPSILON) {
            throw new ArithmeticException("/ 0");
        }

        double offset = startCoord2;
        double ratio = (endCoord2 - startCoord2) / (endCoord1 - startCoord1);
        return ratio * (valueCoord1 - startCoord1) + offset;
    }

    /**
     * Redundand Method. USE --> MathUtils.clamp (provided by LIBGDX)
     */
    public static float clamp(float min, float max, float closest) {
        if (closest < min) { return min; }
        if (closest > max) { return max; }
        return closest;
    }
  
    /**
     * Method to calculate the angle between two points in a 2D environment 
     * @param vec1 1st vector
     * @param vec2 2st vector
     * @return angle in degree, always positive
     */
    public static float angleBetweenPoints(Vector2 vec1, Vector2 vec2){
        Vector2 one;
        Vector2 two;

        if(vec1.x > vec2.x){
            one = vec1.cpy();
            two = vec2.cpy();
        }else {
            one = vec2.cpy();
            two = vec1.cpy();
        }

        return  (MathUtils.radDeg * MathUtils.atan2((one.y-two.y),(one.x-two.x)));
    }

    /**
     * Method to calculate the angle between two points in a 3D environment 
     * @param vec1 1st vector
     * @param vec2 2st vector
     * @return angle in degree, always positive
     */
    public static float angleBetweenPoints3D(Vector3 vec1, Vector3 vec2){
        // create a normalized vector and take just the height of the vec1
        Vector2 from = new Vector2(0, vec1.y);
        //translate the distance between the two 3D point as distance in a 2D normalized context
        Vector2 to = new Vector2(vec1.dst(vec2), vec2.y); //
        return  angleBetweenPoints(from, to);
    }

    public static Vector3 getSlopeNormal(Vector3 v1, Vector3 v2) {
        Vector3 normal = v1.crs(v2);
        return normal;
    }


    public static void DrawDebugLine(Vector3 start, Vector3 end, int lineWidth, Color color, ShapeRenderer debugRenderer)
    {
        Gdx.gl.glLineWidth(lineWidth);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(color);
        debugRenderer.line(start, end);
        debugRenderer.end();
        Gdx.gl.glLineWidth(1);
    }

    public static void DrawDebugLine(Vector3 start, Vector3 end, ShapeRenderer debugRenderer)
    {
        Gdx.gl.glLineWidth(2);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(Color.BLACK);
        debugRenderer.line(start, end);
        debugRenderer.end();
        Gdx.gl.glLineWidth(1);
    }

    public static void DrawRectangle(Color c, ShapeRenderer rectangleRenderer, float x, float y, float z, float width, float height, float depth) {
        Gdx.gl.glLineWidth(2);
        rectangleRenderer.begin(ShapeRenderer.ShapeType.Line);
        rectangleRenderer.setColor(c);
        rectangleRenderer.box(x, y, z, width, height, depth);
        rectangleRenderer.end();
        Gdx.gl.glLineWidth(1);
    }
}
