package ra;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL43.*;

public class ProgramData {
    public static int globalMatricesUBO;
    public static final int globalMatricesBindingIndex = 0;
    
    public int program;
    public int orientationMatrixUnif;
    public int modelToCameraMatrixUnif;
    public int globalUniformBlockIndex;
    
    public int normalModelToCameraMatrixUnif;
    public int cameraSpaceLightPosUnif;
    
    public int lightIntensityUnif;
    public int ambientIntensityUnif;
    public int lightAttenuationUnif;
    public int shininessFactorUnif;
    public int baseDiffuseColorUnif;
    public int specularColorUnif;
    
    public int posOffsetUnif;
    public int dirToLight;
    public int deltaUnif;
    
    public static ProgramData loadProgram(String vertLocation, String fragLocation) {
        ArrayList<Integer> shaderList = new ArrayList<>();
        shaderList.add(loadShader(GL_VERTEX_SHADER, vertLocation));
        shaderList.add(loadShader(GL_FRAGMENT_SHADER, fragLocation));
        
        ProgramData data = new ProgramData();
        data.program = createProgram(shaderList);
        data.modelToCameraMatrixUnif = glGetUniformLocation(data.program, "modelToCameraMatrix");
        data.normalModelToCameraMatrixUnif = glGetUniformLocation(data.program, "normalModelToCameraMatrix");
        data.cameraSpaceLightPosUnif = glGetUniformLocation(data.program, "cameraSpaceLightPos");
        data.lightIntensityUnif = glGetUniformLocation(data.program, "lightIntensity");
        data.ambientIntensityUnif = glGetUniformLocation(data.program, "ambientIntensity");
        data.lightAttenuationUnif = glGetUniformLocation(data.program, "lightAttenuation");
        data.shininessFactorUnif = glGetUniformLocation(data.program, "shininessFactor");
        data.baseDiffuseColorUnif = glGetUniformLocation(data.program, "baseDiffuseColor");
        data.specularColorUnif = glGetUniformLocation(data.program, "specularColor");
        data.globalUniformBlockIndex = glGetUniformBlockIndex(data.program, "GlobalMatrices");
        
        shaderList.stream().forEach((shader) -> {
            glDeleteShader(shader);
        });
        return data;
    }
    
    public static ProgramData loadTextProgram(String vertLocation, String fragLocation) {
        ArrayList<Integer> shaderList = new ArrayList<>();
        shaderList.add(loadShader(GL_VERTEX_SHADER, vertLocation));
        shaderList.add(loadShader(GL_FRAGMENT_SHADER, fragLocation));
        
        ProgramData data = new ProgramData();
        data.program = createProgram(shaderList);
        data.posOffsetUnif = glGetUniformLocation(data.program, "posOffset");
        
        shaderList.stream().forEach((shader) -> {
            glDeleteShader(shader);
        });
        return data;
    }
    
    public static ProgramData loadParticleProgram(String vertLocation, String fragLocation) {
        ArrayList<Integer> shaderList = new ArrayList<>();
        shaderList.add(loadShader(GL_VERTEX_SHADER, vertLocation));
        shaderList.add(loadShader(GL_FRAGMENT_SHADER, fragLocation));
        
        ProgramData data = new ProgramData();
        data.program = createProgram(shaderList);
        data.modelToCameraMatrixUnif = glGetUniformLocation(data.program, "modelToCameraMatrix");
        data.globalUniformBlockIndex = glGetUniformBlockIndex(data.program, "GlobalMatrices");
        
        shaderList.stream().forEach((shader) -> {
            glDeleteShader(shader);
        });
        return data;
    }
    
    public static ProgramData loadGeometryProgram(String vertLocation, String geomLocation, String fragLocation) {
        ArrayList<Integer> shaderList = new ArrayList<>();
        shaderList.add(loadShader(GL_VERTEX_SHADER, vertLocation));
        shaderList.add(loadShader(GL_GEOMETRY_SHADER, geomLocation));
        shaderList.add(loadShader(GL_FRAGMENT_SHADER, fragLocation));
        
        ProgramData data = new ProgramData();
        data.program = createProgram(shaderList);
        data.orientationMatrixUnif = glGetUniformLocation(data.program, "orientationMatrix");
        data.modelToCameraMatrixUnif = glGetUniformLocation(data.program, "modelToCameraMatrix");
        data.globalUniformBlockIndex = glGetUniformBlockIndex(data.program, "GlobalMatrices");
        
        shaderList.stream().forEach((shader) -> {
            glDeleteShader(shader);
        });
        return data;
    }
    
    public static ProgramData loadInstancedProgram(String vertLocation, String fragLocation) {
        ArrayList<Integer> shaderList = new ArrayList<>();
        shaderList.add(loadShader(GL_VERTEX_SHADER, vertLocation));
        shaderList.add(loadShader(GL_FRAGMENT_SHADER, fragLocation));
        
        ProgramData data = new ProgramData();
        data.program = createProgram(shaderList);
        data.orientationMatrixUnif = glGetUniformLocation(data.program, "orientationMatrix");
        data.modelToCameraMatrixUnif = glGetUniformLocation(data.program, "modelToCameraMatrix");
        data.globalUniformBlockIndex = glGetUniformBlockIndex(data.program, "GlobalMatrices");
        
        shaderList.stream().forEach((shader) -> {
            glDeleteShader(shader);
        });
        return data;
    }
    
    public static ProgramData loadComputeProgram(String compLocation) {
        ArrayList<Integer> shaderList = new ArrayList<>();
        shaderList.add(loadShader(GL_COMPUTE_SHADER, compLocation));
        
        ProgramData data = new ProgramData();
        data.program = createProgram(shaderList);
        data.deltaUnif = glGetUniformLocation(data.program, "delta");
        
        shaderList.stream().forEach((shader) -> {
            glDeleteShader(shader);
        });
        return data;
    }
    
    private static int createProgram(ArrayList<Integer> shaderList) {
        int programId = glCreateProgram();
        shaderList.stream().forEach((shader) -> {
            glAttachShader(programId, shader);
        });
        glLinkProgram(programId);
        int status = glGetProgrami(programId, GL_LINK_STATUS);
        if (status == GL_FALSE) {
            System.err.println("Failed to link program");
            System.exit(1);
        }
        shaderList.stream().forEach((shader) -> {
            glDetachShader(programId, shader);
        });
        return programId;
    }
    
    private static int loadShader(int type, String location) {
        int shader = glCreateShader(type);
        String source = readShader(location);
        glShaderSource(shader, source);
        glCompileShader(shader);
        int status = glGetShaderi(shader, GL_COMPILE_STATUS);
        if (status == GL_FALSE) {
            System.err.println("Failed to compile shader");
            System.exit(1);
        }
        return shader;
    }
    
    private static String readShader(String location) {
        StringBuilder source = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(Consts.SHADERS_PATH + location))) {
            String line;
            while ((line = reader.readLine()) != null) {
                source.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
        return source.toString();
    }
}
