package math;

public class Quaternion {
    public float x;
    public float y;
    public float z;
    public float w;
    
    public Quaternion() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.w = 1;
    }
    
    public Quaternion(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    public Quaternion(Quaternion q) {
        this.x = q.x;
        this.y = q.y;
        this.z = q.z;
        this.w = q.w;
    }
    
    public Quaternion(Matrix4f m) {
	w = (float) Math.sqrt(1.0 + m.m00 + m.m11 + m.m22) / 2.0f;
	double w4 = (4.0 * w);
	x = (m.m21 - m.m12) / (float) w4 ;
	y = (m.m02 - m.m20) / (float) w4 ;
	z = (m.m10 - m.m01) / (float) w4 ;
    }
    
    public static Quaternion mul(Quaternion left, Quaternion right) {
        return new Quaternion(left.x * right.w + left.w * right.x + left.y * right.z - left.z * right.y,
                              left.y * right.w + left.w * right.y + left.z * right.x - left.x * right.z,
                              left.z * right.w + left.w * right.z + left.x * right.y - left.y * right.x,
                              left.w * right.w - left.x * right.x - left.y * right.y - left.z * right.z);
    }
    
    public float lengthSquared() {
        return x * x + y * y + z * z + w * w;
    }
    
    public float length() {
        return (float) Math.sqrt(lengthSquared());
    }
    
    public Quaternion normalise() {
        return new Quaternion(x / length(), y / length(), z / length(), w / length());
    }
    
    public Matrix4f matCast() {
        Matrix4f res = new Matrix4f();

        res.m00	= 1 - 2 * y * y - 2 * z * z;
        res.m01	= 2 * x * y + 2 * w * z;
        res.m02	= 2 * x * z - 2 * w * y;

        res.m10	= 2 * x * y - 2 * w * z;
        res.m11	= 1 - 2 * x * x - 2 * z * z;
        res.m12	= 2 * y * z + 2 * w * x;

        res.m20	= 2 * x * z + 2 * w * y;
        res.m21	= 2 * y * z - 2 * w * x;
        res.m22	= 1 - 2 * x * x - 2 * y * y;
        
        return res;
    }
    
    public Quaternion conjugate() {
        Quaternion q = new Quaternion(-x, -y, -z, w);
        return q;
    }
    
    public Quaternion setAngleAxis(float angle, float x, float y, float z) {
        float s = (float) Math.sin(angle * 0.5);
        this.x = x * s;
        this.y = y * s;
        this.z = z * s;
        this.w = (float) Math.cos(angle * 0.5);
        return this;
    }
    
    public Vector3f transform(Vector3f vec){
        return transform(vec.x, vec.y, vec.z, vec);
    }
    
    public Vector3f transform(float x, float y, float z, Vector3f dest) {
        float num = this.x + this.x;
        float num2 = this.y + this.y;
        float num3 = this.z + this.z;
        float num4 = this.x * num;
        float num5 = this.y * num2;
        float num6 = this.z * num3;
        float num7 = this.x * num2;
        float num8 = this.x * num3;
        float num9 = this.y * num3;
        float num10 = this.w * num;
        float num11 = this.w * num2;
        float num12 = this.w * num3;
        dest.set((1.0f - (num5 + num6)) * x + (num7 - num12) * y + (num8 + num11) * z,
                 (num7 + num12) * x + (1.0f - (num4 + num6)) * y + (num9 - num10) * z,
                 (num8 - num11) * x + (num9 + num10) * y + (1.0f - (num4 + num5)) * z);
        return dest;
    }
    
    
    public static Quaternion setFromNormalized(Matrix4f mat) {
        Quaternion q = new Quaternion();
        float t;
        float tr = mat.m00 + mat.m11 + mat.m22;
        if (tr >= 0.0f) {
            t = (float) Math.sqrt(tr + 1.0f);
            q.w = t * 0.5f;
            t = 0.5f / t;
            q.x = (mat.m12 - mat.m21) * t;
            q.y = (mat.m20 - mat.m02) * t;
            q.z = (mat.m01 - mat.m10) * t;
        } else {
            if (mat.m00 >= mat.m11 && mat.m00 >= mat.m22) {
                t = (float) Math.sqrt(mat.m00 - (mat.m11 + mat.m22) + 1.0);
                q.x = t * 0.5f;
                t = 0.5f / t;
                q.y = (mat.m10 + mat.m01) * t;
                q.z = (mat.m02 + mat.m20) * t;
                q.w = (mat.m12 - mat.m21) * t;
            } else if (mat.m11 > mat.m22) {
                t = (float) Math.sqrt(mat.m11 - (mat.m22 + mat.m00) + 1.0);
                q.y = t * 0.5f;
                t = 0.5f / t;
                q.z = (mat.m21 + mat.m12) * t;
                q.x = (mat.m10 + mat.m01) * t;
                q.w = (mat.m20 - mat.m02) * t;
            } else {
                t = (float) Math.sqrt(mat.m22 - (mat.m00 + mat.m11) + 1.0);
                q.z = t * 0.5f;
                t = 0.5f / t;
                q.x = (mat.m02 + mat.m20) * t;
                q.y = (mat.m21 + mat.m12) * t;
                q.w = (mat.m01 - mat.m10) * t;
            }
        }
        return q;
    }
    
    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ", " + w + ")";
    }
}
