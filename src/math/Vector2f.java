package math;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

public class Vector2f {
    public float x;
    public float y;
    
    public Vector2f() {
        this.x = 0.0f;
        this.y = 0.0f;
    }
    
    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    public Vector2f(Vector2f vec) {
        this.x = vec.x;
        this.y = vec.y;
    }
    
    public static Vector2f add(Vector2f left, Vector2f right) {
        return new Vector2f(left.x + right.x, left.y + right.y);
    }
    
    public static Vector2f sub(Vector2f left, Vector2f right) {
        return new Vector2f(left.x - right.x, left.y - right.y);
    }
    
    public Vector2f scale(float s) {
        return new Vector2f(x * s, y * s);
    }
    
    public static Vector2f scale(Vector2f v, float s) {
        return new Vector2f(v.x * s, v.y * s);
    }
    
    public float lengthSquared() {
        return x * x + y * y;
    }
    
    public float length() {
        return (float) Math.sqrt(lengthSquared());
    }
    
    public Vector2f normalise() {
        float l = length();
        return new Vector2f(x / l, y / l);
    }
    
    public FloatBuffer fillAndFlipBuffer() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(3);
        buffer.put(x);
        buffer.put(y);
        buffer.flip();
        return buffer;
    }
    
    public Vector2f negate() {
        Vector2f v = new Vector2f();
        v.x = -x;
        v.y = -y;
        return v;
    }
    
    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public String toString() {
        return x + ", " + y;
    }
}
