package ra;

import java.util.Random;
import math.Vector3f;

public class Drop {
    private Vector3f position = new Vector3f();
    private Vector3f velocity = new Vector3f();
    private float size;
    private float lifespan;
    
    public Drop() {
        calcInitPosition();
        velocity = new Vector3f(Consts.initialVelocity);
        lifespan = 0;
    }
    
    public Drop(float x, float y, float z) {
        this();
        position.x = x;
        position.y = y;
        position.z = z;
    }
    
    public Drop(Drop b) {
        this.position = b.position;
        this.velocity = b.velocity;
        this.lifespan = b.lifespan;
    }
    
    public Drop(Vector3f position, Vector3f velocity) {
        this();
        this.position = position;
        this.velocity = velocity;
    }
    
    private void calcInitPosition() {
        Random r = new Random();
        position.x = r.nextFloat() * 10 - 25;
        position.z = r.nextFloat() * 10 - 5;
        position.y = r.nextFloat() * 7 + 10;
    }
    
    public Vector3f getVelocity() {
        return velocity;
    }
    
    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    public void update(float delta) {
        float x = 0, dx = Consts.updateInterval;
        while (x < delta) {
            if (x + dx > delta) dx = delta - x;
            velocity.x = velocity.x - velocity.x / 3 * dx;
            velocity.y = velocity.y - 9.81f * dx;

            position.x = position.x + velocity.x * dx;
            position.y = position.y + velocity.y * dx;
            position.z = position.z + velocity.z * dx;

            lifespan += dx;
            x += dx;
            if (lifespan > Consts.maxLifeSpan) {
                calcInitPosition();
                lifespan = 0;
                velocity = new Vector3f(Consts.initialVelocity);
            }
        }
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
