package ra;

public class Plane extends Object {
    
    public Plane(float size) {
        super(size, Consts.PLANE);
    }
    
    public Plane(float x, float y, float z, float size) {
        this(size);
        position.x = x;
        position.y = y;
        position.z = z;
    }
    
    public Plane(Plane p) {
        this(p.size);
        indicesCount = p.indicesCount;
        position = p.position;
        size = p.size;
        vao = p.vao;
    }
}
