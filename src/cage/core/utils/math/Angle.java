package cage.core.utils.math;

public class Angle {

    public static final float PI = (float)Math.PI;

    public static float fromDegrees(float degrees) {
        return degrees * PI / 180.0f;
    }

    public static float fromRadians(float radians) {
        return radians;
    }
}
