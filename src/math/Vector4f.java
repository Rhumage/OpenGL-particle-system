package math;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

public class Vector4f {
    public float x;
    public float y;
    public float z;
    public float w;
    
    public Vector4f() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
        this.w = 0.0f;
    }
    
    public Vector4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    public static Vector4f mul(Matrix4f matrix, Vector4f vec) {
        Vector4f res = new Vector4f();
        res.x = matrix.m00 * vec.x + matrix.m10 * vec.y + matrix.m20 * vec.z + matrix.m30 * vec.w;
        res.y = matrix.m01 * vec.x + matrix.m11 * vec.y + matrix.m21 * vec.z + matrix.m31 * vec.w;
        res.z = matrix.m02 * vec.x + matrix.m12 * vec.y + matrix.m22 * vec.z + matrix.m32 * vec.w;
        res.w = matrix.m03 * vec.x + matrix.m13 * vec.y + matrix.m23 * vec.z + matrix.m33 * vec.w;
        return res;
    }
    
    public FloatBuffer fillAndFlipBuffer() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(4);
        buffer.put(x);
        buffer.put(y);
        buffer.put(z);
        buffer.put(w);
        buffer.flip();
        return buffer;
    }
    
    @Override
    public String toString() {
        return x + ", " + y + ", " + z + ", " + w;
    }
}
