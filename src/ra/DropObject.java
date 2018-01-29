package ra;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL33.*;

public class DropObject {
    public static final int vao;
    public static final int indicesCount;
    public static final int instancedVBO;
    public static final int instancedVAO;
    public static final int computeVBO;
    public static final int computeVAO;
    
    private static final float[] data;
    
    static {
        BufferedReader reader;
        String line;
        ArrayList<float[]> v = new ArrayList<>();
        ArrayList<float[]> vn = new ArrayList<>();
        ArrayList<float[]> vt = new ArrayList<>();
        ArrayList<Short> fV = new ArrayList<>();
        ArrayList<Short> fVn = new ArrayList<>();
        ArrayList<Short> fVt = new ArrayList<>();
        try {
            reader = new BufferedReader(new FileReader(Consts.FILES_PATH + Consts.PLANE2));
            while ((line = reader.readLine()) != null) {
                line = line.replace("  ", " ");
                String values[] = line.split(" ");
                switch (values[0]) {
                    case "v":
                        v.add(new float[] {Float.parseFloat(values[1]), Float.parseFloat(values[2]), Float.parseFloat(values[3])});
                        break;
                    case "vn":
                        vn.add(new float[] {Float.parseFloat(values[1]), Float.parseFloat(values[2]), Float.parseFloat(values[3])});
                        break;
                    case "vt":
                        vt.add(new float[] {Float.parseFloat(values[1]), Float.parseFloat(values[2])});
                        break;
                    case "f":
                        fV.add(Short.parseShort((Integer.parseInt(values[1].split("/")[0]) - 1) + ""));
                        fV.add(Short.parseShort((Integer.parseInt(values[2].split("/")[0]) - 1) + ""));
                        fV.add(Short.parseShort((Integer.parseInt(values[3].split("/")[0]) - 1) + ""));
                        fVn.add(Short.parseShort((Integer.parseInt(values[1].split("/")[1]) - 1) + ""));
                        fVn.add(Short.parseShort((Integer.parseInt(values[2].split("/")[1]) - 1) + ""));
                        fVn.add(Short.parseShort((Integer.parseInt(values[3].split("/")[1]) - 1) + ""));
                        fVt.add(Short.parseShort((Integer.parseInt(values[1].split("/")[2]) - 1) + ""));
                        fVt.add(Short.parseShort((Integer.parseInt(values[2].split("/")[2]) - 1) + ""));
                        fVt.add(Short.parseShort((Integer.parseInt(values[3].split("/")[2]) - 1) + ""));
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
        
        data = new float[fV.size() * 3 + fVn.size() * 3 + fVt.size() * 2];
        indicesCount = fV.size();
        int k = 0;
        for (Short fV1 : fV) {
            float[] vertex = v.get(fV1);
            data[k++] = vertex[0] * Consts.particleSize;
            data[k++] = vertex[1] * Consts.particleSize;
            data[k++] = vertex[2] * Consts.particleSize;
        }
        for (Short fVn1 : fVn) {
            float[] normal = vn.get(fVn1);
            data[k++] = normal[0];
            data[k++] = normal[1];
            data[k++] = normal[2];
        }
        for (Short fVt1 : fVt) {
            float[] texture = vt.get(fVt1 % vt.size());
            data[k++] = texture[0];
            data[k++] = texture[1];
        }
        
        int vertexBufferObject = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject);
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        
        vao = glGenVertexArrays();
        glBindVertexArray(vao);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(5);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, fV.size() * 3 * Float.BYTES);
        glVertexAttribPointer(5, 2, GL_FLOAT, false, 0, fV.size() * 3 * Float.BYTES + fVn.size() * 3 * Float.BYTES);
        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        instancedVBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, instancedVBO);
        glBufferData(GL_ARRAY_BUFFER, data.length * Float.BYTES + Consts.numberOfParticles * 3 * Float.BYTES, GL_STATIC_DRAW);
        glBufferSubData(GL_ARRAY_BUFFER, 0, data);
        
        instancedVAO = glGenVertexArrays();
        glBindVertexArray(instancedVAO);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(5);
        glEnableVertexAttribArray(7);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, fV.size() * 3 * Float.BYTES);
        glVertexAttribPointer(5, 2, GL_FLOAT, false, 0, fV.size() * 3 * Float.BYTES + fVn.size() * 3 * Float.BYTES);
        glVertexAttribPointer(7, 3, GL_FLOAT, false, 0, data.length * Float.BYTES);
        glVertexAttribDivisor(0, 0);
        glVertexAttribDivisor(2, 0);
        glVertexAttribDivisor(5, 0);
        glVertexAttribDivisor(7, 1);
        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        computeVBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, computeVBO);
        glBufferData(GL_ARRAY_BUFFER, data.length * Float.BYTES, GL_STATIC_DRAW);
        glBufferSubData(GL_ARRAY_BUFFER, 0, data);
        
        computeVAO = glGenVertexArrays();
        glBindVertexArray(computeVAO);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(5);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, fV.size() * 3 * Float.BYTES);
        glVertexAttribPointer(5, 2, GL_FLOAT, false, 0, fV.size() * 3 * Float.BYTES + fVn.size() * 3 * Float.BYTES);
        glVertexAttribDivisor(0, 0);
        glVertexAttribDivisor(2, 0);
        glVertexAttribDivisor(5, 0);
        glVertexAttribDivisor(7, 1);
        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
    
    public static void draw() {
        glBindVertexArray(vao);
        glDrawArrays(GL_TRIANGLES, 0, indicesCount);
        glBindVertexArray(0);
    }
    
    public static void drawInstanced(Drop[] drops) {
       FloatBuffer buffer = BufferUtils.createFloatBuffer(Consts.numberOfParticles * 3);
        for (Drop d : drops) {
            buffer.put(d.getPosition().x);
            buffer.put(d.getPosition().y);
            buffer.put(d.getPosition().z);
        }
        buffer.flip();

        glBindBuffer(GL_ARRAY_BUFFER, instancedVBO);
        glBufferSubData(GL_ARRAY_BUFFER, data.length * Float.BYTES, buffer);
        glBindVertexArray(instancedVAO);
        glDrawArraysInstanced(GL_TRIANGLE_STRIP, 0, 4, Consts.numberOfParticles);
        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
}
