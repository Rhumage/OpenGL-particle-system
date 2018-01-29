package math;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

public class Matrix4f {
    public float m00;
    public float m01;
    public float m02;
    public float m03;
    public float m10;
    public float m11;
    public float m12;
    public float m13;
    public float m20;
    public float m21;
    public float m22;
    public float m23;
    public float m30;
    public float m31;
    public float m32;
    public float m33;
    
    public Matrix4f() {
        this.m00 = 1.0f;
        this.m01 = 0.0f;
        this.m02 = 0.0f;
        this.m03 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = 1.0f;
        this.m12 = 0.0f;
        this.m13 = 0.0f;
        this.m20 = 0.0f;
        this.m21 = 0.0f;
        this.m22 = 1.0f;
        this.m23 = 0.0f;
        this.m30 = 0.0f;
        this.m31 = 0.0f;
        this.m32 = 0.0f;
        this.m33 = 1.0f;
    }
    
    public Matrix4f(Matrix4f m) {
        this.m00 = m.m00;
        this.m01 = m.m01;
        this.m02 = m.m02;
        this.m03 = m.m03;
        this.m10 = m.m10;
        this.m11 = m.m11;
        this.m12 = m.m12;
        this.m13 = m.m13;
        this.m20 = m.m20;
        this.m21 = m.m21;
        this.m22 = m.m22;
        this.m23 = m.m23;
        this.m30 = m.m30;
        this.m31 = m.m31;
        this.m32 = m.m32;
        this.m33 = m.m33;
    }
    
    public Matrix4f transpose() {
        Matrix4f t = new Matrix4f();
        
        t.m00 = this.m00;
        t.m01 = this.m10;
        t.m02 = this.m20;
        t.m03 = this.m30;
        t.m10 = this.m01;
        t.m11 = this.m11;
        t.m12 = this.m21;
        t.m13 = this.m31;
        t.m20 = this.m02;
        t.m21 = this.m12;
        t.m22 = this.m22;
        t.m23 = this.m32;
        t.m30 = this.m03;
        t.m31 = this.m13;
        t.m32 = this.m23;
        t.m33 = this.m33;
        
        return t;
    }
    
    public static Matrix4f mul(Matrix4f left, Matrix4f right) {
        Matrix4f t = new Matrix4f();
        
        t.m00 = left.m00 * right.m00 + left.m10 * right.m01 + left.m20 * right.m02 + left.m30 * right.m03;
        t.m01 = left.m01 * right.m00 + left.m11 * right.m01 + left.m21 * right.m02 + left.m31 * right.m03;
        t.m02 = left.m02 * right.m00 + left.m12 * right.m01 + left.m22 * right.m02 + left.m32 * right.m03;
        t.m03 = left.m03 * right.m00 + left.m13 * right.m01 + left.m23 * right.m02 + left.m33 * right.m03;
        t.m10 = left.m00 * right.m10 + left.m10 * right.m11 + left.m20 * right.m12 + left.m30 * right.m13;
        t.m11 = left.m01 * right.m10 + left.m11 * right.m11 + left.m21 * right.m12 + left.m31 * right.m13;
        t.m12 = left.m02 * right.m10 + left.m12 * right.m11 + left.m22 * right.m12 + left.m32 * right.m13;
        t.m13 = left.m03 * right.m10 + left.m13 * right.m11 + left.m23 * right.m12 + left.m33 * right.m13;
        t.m20 = left.m00 * right.m20 + left.m10 * right.m21 + left.m20 * right.m22 + left.m30 * right.m23;
        t.m21 = left.m01 * right.m20 + left.m11 * right.m21 + left.m21 * right.m22 + left.m31 * right.m23;
        t.m22 = left.m02 * right.m20 + left.m12 * right.m21 + left.m22 * right.m22 + left.m32 * right.m23;
        t.m23 = left.m03 * right.m20 + left.m13 * right.m21 + left.m23 * right.m22 + left.m33 * right.m23;
        t.m30 = left.m00 * right.m30 + left.m10 * right.m31 + left.m20 * right.m32 + left.m30 * right.m33;
        t.m31 = left.m01 * right.m30 + left.m11 * right.m31 + left.m21 * right.m32 + left.m31 * right.m33;
        t.m32 = left.m02 * right.m30 + left.m12 * right.m31 + left.m22 * right.m32 + left.m32 * right.m33;
        t.m33 = left.m03 * right.m30 + left.m13 * right.m31 + left.m23 * right.m32 + left.m33 * right.m33;

        return t;
    }
    
    public Matrix3f mat4to3() {
        Matrix3f mat3 = new Matrix3f();
        mat3.m00 = this.m00;
        mat3.m01 = this.m01;
        mat3.m02 = this.m02;
        mat3.m10 = this.m10;
        mat3.m11 = this.m11;
        mat3.m12 = this.m12;
        mat3.m20 = this.m20;
        mat3.m21 = this.m21;
        mat3.m22 = this.m22;
        return mat3;
    }
    
    public FloatBuffer fillAndFlipBuffer() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        buffer.put(m00);
        buffer.put(m01);
        buffer.put(m02);
        buffer.put(m03);
        buffer.put(m10);
        buffer.put(m11);
        buffer.put(m12);
        buffer.put(m13);
        buffer.put(m20);
        buffer.put(m21);
        buffer.put(m22);
        buffer.put(m23);
        buffer.put(m30);
        buffer.put(m31);
        buffer.put(m32);
        buffer.put(m33);
        buffer.flip();
        return buffer;
    }
    
    public Matrix4f translate(Vector3f vec) {
        Matrix4f m = new Matrix4f(this);
        
        m.m30 += m00 * vec.x + m10 * vec.y + m20 * vec.z;
        m.m31 += m01 * vec.x + m11 * vec.y + m21 * vec.z;
        m.m32 += m02 * vec.x + m12 * vec.y + m22 * vec.z;
        m.m33 += m03 * vec.x + m13 * vec.y + m23 * vec.z;

        return m;
    }
    
    public Matrix4f scale(Vector3f vec) {
        Matrix4f m = new Matrix4f(this);
        m.m00 = m00 * vec.x;
        m.m01 = m01 * vec.x;
        m.m02 = m02 * vec.x;
        m.m03 = m03 * vec.x;
        m.m10 = m10 * vec.y;
        m.m11 = m11 * vec.y;
        m.m12 = m12 * vec.y;
        m.m13 = m13 * vec.y;
        m.m20 = m20 * vec.z;
        m.m21 = m21 * vec.z;
        m.m22 = m22 * vec.z;
        m.m23 = m23 * vec.z;
        return m;
    }
    
    public Matrix4f rotate(float angle, Vector3f axis) {
        Matrix4f dest = new Matrix4f();
        float c = (float) Math.cos(angle);
        float s = (float) Math.sin(angle);
        float oneminusc = 1.0f - c;
        float xy = axis.x * axis.y;
        float yz = axis.y * axis.z;
        float xz = axis.x * axis.z;
        float xs = axis.x * s;
        float ys = axis.y * s;
        float zs = axis.z * s;

        float f00 = axis.x * axis.x * oneminusc + c;
        float f01 = xy * oneminusc + zs;
        float f02 = xz * oneminusc - ys;
        float f10 = xy * oneminusc - zs;
        float f11 = axis.y * axis.y * oneminusc + c;
        float f12 = yz * oneminusc + xs;
        float f20 = xz * oneminusc + ys;
        float f21 = yz * oneminusc - xs;
        float f22 = axis.z * axis.z * oneminusc + c;

        float t00 = m00 * f00 + m10 * f01 + m20 * f02;
        float t01 = m01 * f00 + m11 * f01 + m21 * f02;
        float t02 = m02 * f00 + m12 * f01 + m22 * f02;
        float t03 = m03 * f00 + m13 * f01 + m23 * f02;
        float t10 = m00 * f10 + m10 * f11 + m20 * f12;
        float t11 = m01 * f10 + m11 * f11 + m21 * f12;
        float t12 = m02 * f10 + m12 * f11 + m22 * f12;
        float t13 = m03 * f10 + m13 * f11 + m23 * f12;
        dest.m20 = m00 * f20 + m10 * f21 + m20 * f22;
        dest.m21 = m01 * f20 + m11 * f21 + m21 * f22;
        dest.m22 = m02 * f20 + m12 * f21 + m22 * f22;
        dest.m23 = m03 * f20 + m13 * f21 + m23 * f22;
        dest.m00 = t00;
        dest.m01 = t01;
        dest.m02 = t02;
        dest.m03 = t03;
        dest.m10 = t10;
        dest.m11 = t11;
        dest.m12 = t12;
        dest.m13 = t13;
        return dest;
    }
    
    public float[] toArray() {
        float[] m = {m00, m01, m02, m03,
                    m10, m11, m12, m13,
                    m20, m21, m22, m23,
                    m30, m31, m32, m33,};
        return m;
    }
    
    @Override
    public String toString() {
        String s = "[" + m00 + ", " + m01 + ", " + m02 + ", " + m03 + ", \n";
        s += m10 + ", " + m11 + ", " + m12 + ", " + m13 + ", \n";
        s += m20 + ", " + m21 + ", " + m22 + ", " + m23 + ", \n";
        s += m30 + ", " + m31 + ", " + m32 + ", " + m33 + "]";
        return s;
    }
}
