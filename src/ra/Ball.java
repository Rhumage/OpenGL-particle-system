package ra;
public class Ball extends Object {
    
    public Ball(float size) {
        super(size, Consts.BALL);
    }
    
    public Ball(float x, float y, float z, float size) {
        this(size);
        position.x = x;
        position.y = y;
        position.z = z;
    }
    
    public Ball(Ball b) {
        this(b.size);
        this.position = b.position;
        this.vao = b.vao;
        this.indicesCount = b.indicesCount;
    }
}
