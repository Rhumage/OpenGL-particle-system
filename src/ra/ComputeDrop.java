package ra;

import java.util.Random;
import math.Vector3f;

public class ComputeDrop {
    private Vector3f position = new Vector3f();
    private Vector3f velocity = new Vector3f();
    private float size;
    private float lifespan;
    
    public ComputeDrop() {
        Random r = new Random();
        position.x = r.nextFloat() * 10 - 25;
        position.z = r.nextFloat() * 10 - 5;
        position.y = r.nextFloat() * 7 + 10;
        velocity = new Vector3f(Consts.initialVelocity);
        lifespan = 0;
    }
    
    public ComputeDrop(float x, float y, float z) {
        this();
        position.x = x;
        position.y = y;
        position.z = z;
    }
    
    public ComputeDrop(ComputeDrop b) {
        this.position = b.position;
        this.velocity = b.velocity;
        this.lifespan = b.lifespan;
    }
    
    public ComputeDrop(Vector3f position, Vector3f velocity) {
        this();
        this.position = position;
        this.velocity = velocity;
    }
    
    public Vector3f getVelocity() {
        return velocity;
    }
    
    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }
    
    public float getLifespan() {
        return lifespan;
    }
    
    public void setLifespan(float lifespan) {
        this.lifespan = lifespan;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }
    
    public void draw() {
        DropObject.draw();
    }
}
