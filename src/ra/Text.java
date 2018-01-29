package ra;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import math.Vector2f;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static ra.RA.texIds;

public class Text {
    private Vector2f position = new Vector2f();
    private float size;
    private int vao = 0;
    private int indicesCount;
    private String text;
    
    public Text(String text, float size) {
        this.text = text;
        this.size = size;
        BufferedReader reader;
        String line;
        ArrayList<float[]> v = new ArrayList<>();
        ArrayList<float[]> vn = new ArrayList<>();
        ArrayList<float[]> vt = new ArrayList<>();
        ArrayList<Short> fV = new ArrayList<>();
        ArrayList<Short> fVn = new ArrayList<>();
        ArrayList<Short> fVt = new ArrayList<>();
        try {
            reader = new BufferedReader(new FileReader(Consts.FILES_PATH + Consts.TEXT));
            while ((line = reader.readLine()) != null) {
                line = line.replace("  ", " ");
                String values[] = line.split(" ");
                switch (values[0]) {
                    case "v":
                        v.add(new float[] {Float.parseFloat(values[1]), Float.parseFloat(values[2]), Float.parseFloat(values[3])});
                        break;
                    case "vt":
                        vt.add(new float[] {Float.parseFloat(values[1]), Float.parseFloat(values[2])});
                        break;
                    case "f":
                        fV.add(Short.parseShort((Integer.parseInt(values[1].split("/")[0]) - 1) + ""));
                        fV.add(Short.parseShort((Integer.parseInt(values[2].split("/")[0]) - 1) + ""));
                        fV.add(Short.parseShort((Integer.parseInt(values[3].split("/")[0]) - 1) + ""));
                        fVt.add(Short.parseShort((Integer.parseInt(values[1].split("/")[2]) - 1) + ""));
                        fVt.add(Short.parseShort((Integer.parseInt(values[2].split("/")[2]) - 1) + ""));
                        fVt.add(Short.parseShort((Integer.parseInt(values[3].split("/")[2]) - 1) + ""));
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
        
        float data[] = new float[fV.size() * 3 + fVn.size() * 3 + fVt.size() * 2];
        indicesCount = fV.size();
        int k = 0;
        for (int i = 0; i < fV.size(); i++) {
            float vertex[] = v.get(fV.get(i));
            data[k++] = vertex[0] * size * 0.4f;
            data[k++] = vertex[1] * size;
            data[k++] = vertex[2] * size;
        }
        for (int i = 0; i < fVt.size(); i++) {
            float texture[] = vt.get(fVt.get(i) % vt.size());
            data[k++] = texture[0];
            data[k++] = texture[1];
        }
        
        FloatBuffer vertexDataBuffer = BufferUtils.createFloatBuffer(data.length);
        vertexDataBuffer.put(data);
        vertexDataBuffer.flip();
        
        int vertexBufferObject = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject);
        glBufferData(GL_ARRAY_BUFFER, vertexDataBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        vao = glGenVertexArrays();
        glBindVertexArray(vao);
        
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(5);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glVertexAttribPointer(5, 2, GL_FLOAT, false, 0, fV.size() * 3 * Float.BYTES);
        glBindVertexArray(0);
    }
    
    public Text(String text, float size, Vector2f vec) {
        this(text, size);
        position.x = vec.x;
        position.y = vec.y;
    }
    
    public Text(Text b) {
        this.text = b.text;
        this.position = b.position;
        this.vao = b.vao;
        this.indicesCount = b.indicesCount;
    }
    
    public void draw() {
        for (int i = 0; i < getLength(); i++) {
            if (getAt(i) == ' ') continue;
            glBindTexture(GL_TEXTURE_2D, texIds[getId(i)]);
            glUniform2f(RA.getTextProgram().posOffsetUnif, i * 90.0f * getSize() + getX(), getY());
            glBindVertexArray(vao);
            glDrawArrays(GL_TRIANGLES, 0, indicesCount);
            glBindVertexArray(0);
        }
    }
    
    public int getId(int i) {
        return getId(text.charAt(i));
    }
    
    private int getId(char c) {
        c = Character.toLowerCase(c);
        if (c == 'a') return 0;
        if (c == 'b') return 1;
        if (c == 'c') return 2;
        if (c == 'd') return 3;
        if (c == 'e') return 4;
        if (c == 'f') return 5;
        if (c == 'g') return 6;
        if (c == 'h') return 7;
        if (c == 'i') return 8;
        if (c == 'j') return 9;
        if (c == 'k') return 10;
        if (c == 'l') return 11;
        if (c == 'm') return 12;
        if (c == 'n') return 13;
        if (c == 'o') return 14;
        if (c == 'p') return 15;
        if (c == 'q') return 16;
        if (c == 'r') return 17;
        if (c == 's') return 18;
        if (c == 't') return 19;
        if (c == 'u') return 20;
        if (c == 'v') return 21;
        if (c == 'w') return 22;
        if (c == 'x') return 23;
        if (c == 'y') return 24;
        if (c == 'z') return 25;
        if (c == '1') return 26;
        if (c == '2') return 27;
        if (c == '3') return 28;
        if (c == '4') return 29;
        if (c == '5') return 30;
        if (c == '6') return 31;
        if (c == '7') return 32;
        if (c == '8') return 33;
        if (c == '9') return 34;
        if (c == '0') return 35;
        if (c == ':') return 36;
        if (c == ',') return 37;
        if (c == '.') return 38;
        return -1;
    }
    
    public int getLength() {
        return text.length();
    }
    
    public String getText() {
        return text;
    }
    
    public char getAt(int i) {
        return text.charAt(i);
    }
    
    public void setPosition(float x, float y) {
        position.x = x;
        position.y = y;
    }
    
    public float getX() {
        return position.x;
    }
    
    public float getY() {
        return position.y;
    }
    
    public float getSize() {
        return size;
    }
}
