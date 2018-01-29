package math;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

public class Matrix3f {
    public float m00;
    public float m01;
    public float m02;
    public float m10;
    public float m11;
    public float m12;
    public float m20;
    public float m21;
    public float m22;
    
    public Matrix3f() {
        this.m00 = 1.0f;
        this.m01 = 0.0f;
        this.m02 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = 1.0f;
        this.m12 = 0.0f;
        this.m20 = 0.0f;
        this.m21 = 0.0f;
        this.m22 = 1.0f;
    }
    
    public Matrix3f load(Matrix3f src) {
        this.m00 = src.m00;
        this.m10 = src.m10;
        this.m20 = src.m20;
        this.m01 = src.m01;
        this.m11 = src.m11;
        this.m21 = src.m21;
        this.m02 = src.m02;
        this.m12 = src.m12;
        this.m22 = src.m22;

        return this;
    }
    
    public FloatBuffer fillAndFlipBuffer() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(9);
        buffer.put(m00);
        buffer.put(m01);
        buffer.put(m02);
        buffer.put(m10);
        buffer.put(m11);
        buffer.put(m12);
        buffer.put(m20);
        buffer.put(m21);
        buffer.put(m22);
        buffer.flip();
        return buffer;
    }
    
    @Override
    public String toString() {
        String s = "[" + m00 + ", " + m01 + ", " + m02 + ", \n";
        s += m10 + ", " + m11 + ", " + m12 + ", \n";
        s += m20 + ", " + m21 + ", " + m22 + "]";
        return s;
    }
}
