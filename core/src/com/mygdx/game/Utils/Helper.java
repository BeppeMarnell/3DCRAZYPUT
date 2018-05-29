package com.mygdx.game.Utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Helper {

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

    public static float clamp(float min, float max, float closest) {
        if (closest < min) { return min; }
        if (closest > max) { return max; }
        return closest;
    }

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

        return  Math.abs(MathUtils.radDeg * MathUtils.atan2((one.y-two.y),(one.x-two.x)));
    }
}
