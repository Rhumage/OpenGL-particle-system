package math;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

public class Vector3f {
    public float x;
    public float y;
    public float z;
    
    public Vector3f() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
    }
    
    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vector3f(Vector3f vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }
    
    public static Vector3f add(Vector3f left, Vector3f right) {
        return new Vector3f(left.x + right.x, left.y + right.y, left.z + right.z);
    }
    
    public static Vector3f sub(Vector3f left, Vector3f right) {
        return new Vector3f(left.x - right.x, left.y - right.y, left.z - right.z);
    }
    
    public Vector3f scale(float s) {
        return new Vector3f(x * s, y * s, z * s);
    }
    
    public static Vector3f scale(Vector3f v, float s) {
        return new Vector3f(v.x * s, v.y * s, v.z * s);
    }
    
    public float lengthSquared() {
        return x * x + y * y + z * z;
    }
    
    public float length() {
        return (float) Math.sqrt(lengthSquared());
    }
    
    public Vector3f normalise() {
        float l = length();
        return new Vector3f(x / l, y / l, z / l);
    }
    
    public static Vector3f cross(Vector3f left, Vector3f right) {
        return new Vector3f(
            left.y * right.z - left.z * right.y,
            right.x * left.z - right.z * left.x,
            left.x * right.y - left.y * right.x
        );
    }
    
    public FloatBuffer fillAndFlipBuffer() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(3);
        buffer.put(x);
        buffer.put(y);
        buffer.put(z);
        buffer.flip();
        return buffer;
    }
    
    public Vector3f negate() {
        Vector3f v = new Vector3f();
        v.x = -x;
        v.y = -y;
        v.z = -z;
        return v;
    }
    
    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    @Override
    public String toString() {
        return x + ", " + y + ", " + z;
    }
}
