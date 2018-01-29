package ra;

import math.Vector2f;
import math.Vector3f;
import math.Vector4f;
import math.Matrix3f;
import math.Matrix4f;
import math.Quaternion;
import java.io.InputStream;
import java.io.FileInputStream;
import java.nio.IntBuffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.DoubleBuffer;
import java.util.Random;
import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import java.util.Arrays;
import org.lwjgl.*;
import org.lwjgl.opengl.*;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWErrorCallback;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL42.*;
import static org.lwjgl.opengl.GL43.*;

public class RA {
    // Programs
    private static ProgramData program;
    private static ProgramData textProgram;
    private static ProgramData particleProgram;
    private static ProgramData geometryProgram;
    private static ProgramData instancedProgram;
    private static ProgramData computeProgram;
    
    // Window
    private long window;
    
    // Mode and info
    private final String[] modeName = {"Basic display", "Geometry shader", "Instanced arrays", "Compute shader", "OpenCL"};
    private boolean showInfo = true;
    private boolean vsync = true;
    private boolean pause = false;
    private float FPS[] = new float[100];
    private int fpsPointer = 0;
    private int mode = 0;
    
    // Objects
    private Plane plane;
    private Ball ball;
    private Ball light;
    private Drop[] drops = new Drop[Consts.numberOfParticles];
    private ComputeDrop[] computeDrops = new ComputeDrop[Consts.numberOfParticles];
    private Vector4f lightPos = new Vector4f(-10.0f, 30.0f, 0.0f, 0.1f);
    
    // Time
    private float lastFrameDuration;
    private double lastFrameTimeStamp;
    private double now;
    
    // Input
    private float prevMouseX;
    private float prevMouseY;
    
    // View
    private ViewData view = new ViewData(new Vector3f(0.0f, 5.0f, 60.0f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f), 0.0f, 0.0f);
    
    // Textures
    public static int[] texIds = new int[40];
    
    // Geometry buffers
    private int imposterVAO;
    private int imposterVBO;
    
    // Compute buffers
    private int posSSBO;
    private int velSSBO;
    private int lifSSBO;
    private int posBinding;
    private int velBinding;
    private int lifBinding;
    private FloatBuffer posBuffer, velBuffer, lifBuffer;
    
    private void start() {
        try {
            init();
            loop();
            glfwFreeCallbacks(window);
            glfwDestroyWindow(window);
        }
        finally {
            glfwTerminate();
            glfwSetErrorCallback(null).free();
        }
    }
    
    private void init() {
        initWindow();
        initOpenGL();
        initializePrograms();
        initObjects();
        initInput();
        initTextures();
        initGeomVao();
        initCompute();
        
        view.reshape(Consts.WIDTH, Consts.HEIGHT);
        lastFrameTimeStamp = System.nanoTime();
        Arrays.fill(FPS, 1 / 60f);
    }
    
    private void initWindow() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        window = glfwCreateWindow(Consts.WIDTH, Consts.HEIGHT, "Particles!", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create GLFW window");
        }
        
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(window, (vidmode.width() - Consts.WIDTH) / 2, (vidmode.height() - Consts.HEIGHT) / 2);
        glfwMakeContextCurrent(window);
        glfwSwapInterval(0);
        glfwShowWindow(window);
        GL.createCapabilities();
    }
    
    private void initOpenGL() {
        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }
    
    private void initializePrograms() {
        program = ProgramData.loadProgram(Consts.VERT_SHADER, Consts.FRAG_SHADER);
        textProgram = ProgramData.loadTextProgram(Consts.TEXT_VERT_SHADER, Consts.TEXT_FRAG_SHADER);
        particleProgram = ProgramData.loadParticleProgram(Consts.PARTICLES_VERT_SHADER, Consts.PARTICLES_FRAG_SHADER);
        geometryProgram = ProgramData.loadGeometryProgram(Consts.GEOMETRY_VERT_SHADER, Consts.GEOMETRY_GEOM_SHADER, Consts.GEOMETRY_FRAG_SHADER);
        instancedProgram = ProgramData.loadInstancedProgram(Consts.INSTANCED_VERT_SHADER, Consts.INSTANCED_FRAG_SHADER);
        computeProgram = ProgramData.loadComputeProgram(Consts.COMPUTE_COMP_SHADER);
        
        glUseProgram(program.program);
        glUniform4fv(program.lightIntensityUnif, Consts.lightIntensity.fillAndFlipBuffer());
        glUniform4fv(program.ambientIntensityUnif, Consts.ambientIntensity.fillAndFlipBuffer());
        glUniform1f(program.lightAttenuationUnif, Consts.lightAttenuation);
        glUniform1f(program.shininessFactorUnif, Consts.shininessFactor);
        glUniform4fv(program.baseDiffuseColorUnif, Consts.diffuseColor.fillAndFlipBuffer());
        glUniform4fv(program.specularColorUnif, Consts.specularColor.fillAndFlipBuffer());
        glUseProgram(0);
        
        ProgramData.globalMatricesUBO = glGenBuffers();
        glBindBuffer(GL_UNIFORM_BUFFER, ProgramData.globalMatricesUBO);
        glBufferData(GL_UNIFORM_BUFFER, Float.BYTES * 16, GL_STREAM_DRAW);
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
        glBindBufferRange(GL_UNIFORM_BUFFER, ProgramData.globalMatricesBindingIndex, ProgramData.globalMatricesUBO, 0, 16 * 4);
    }
    
    public void initObjects() {
        plane = new Plane(0.0f, -5.0f, -42.0f, 4.0f);
        ball = new Ball(25.0f, -5.0f, -50.0f, 1.0f);
        light = new Ball(lightPos.x, lightPos.y, lightPos.z, 0.1f);
        
        Random r = new Random();
        for (int i = 0; i < drops.length; i++) {
            Drop d = new Drop();
            float x = 0, dx = 0.01f;
            float lifeSpan = r.nextFloat() * Consts.maxLifeSpan;
            while (x < lifeSpan) {
                d.update(dx);
                x += dx;
            }
            drops[i] = d;
        }
        for (int i = 0; i < computeDrops.length; i++) {
            ComputeDrop d = new ComputeDrop();
            d.setLifespan(r.nextFloat() * Consts.maxLifeSpan);
            computeDrops[i] = d;
        }
    }
    
    public void initInput() {
        glfwSetKeyCallback(window, InputHandler.keyboard);
        glfwSetMouseButtonCallback(window, InputHandler.mouse);
        DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer y = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(window, x, y);
        x.rewind();
        y.rewind();
        prevMouseX = (float) x.get();
        prevMouseY = (float) y.get();
    }
    
    private void initTextures() {
        glActiveTexture(GL_TEXTURE0);
        texIds[0] = loadPNGTexture(Consts.A, GL_TEXTURE0);
        texIds[1] = loadPNGTexture(Consts.B, GL_TEXTURE0);
        texIds[2] = loadPNGTexture(Consts.C, GL_TEXTURE0);
        texIds[3] = loadPNGTexture(Consts.D, GL_TEXTURE0);
        texIds[4] = loadPNGTexture(Consts.E, GL_TEXTURE0);
        texIds[5] = loadPNGTexture(Consts.F, GL_TEXTURE0);
        texIds[6] = loadPNGTexture(Consts.G, GL_TEXTURE0);
        texIds[7] = loadPNGTexture(Consts.H, GL_TEXTURE0);
        texIds[8] = loadPNGTexture(Consts.I, GL_TEXTURE0);
        texIds[9] = loadPNGTexture(Consts.J, GL_TEXTURE0);
        texIds[10] = loadPNGTexture(Consts.K, GL_TEXTURE0);
        texIds[11] = loadPNGTexture(Consts.L, GL_TEXTURE0);
        texIds[12] = loadPNGTexture(Consts.M, GL_TEXTURE0);
        texIds[13] = loadPNGTexture(Consts.N, GL_TEXTURE0);
        texIds[14] = loadPNGTexture(Consts.O, GL_TEXTURE0);
        texIds[15] = loadPNGTexture(Consts.P, GL_TEXTURE0);
        texIds[16] = loadPNGTexture(Consts.Q, GL_TEXTURE0);
        texIds[17] = loadPNGTexture(Consts.R, GL_TEXTURE0);
        texIds[18] = loadPNGTexture(Consts.S, GL_TEXTURE0);
        texIds[19] = loadPNGTexture(Consts.T, GL_TEXTURE0);
        texIds[20] = loadPNGTexture(Consts.U, GL_TEXTURE0);
        texIds[21] = loadPNGTexture(Consts.V, GL_TEXTURE0);
        texIds[22] = loadPNGTexture(Consts.W, GL_TEXTURE0);
        texIds[23] = loadPNGTexture(Consts.X, GL_TEXTURE0);
        texIds[24] = loadPNGTexture(Consts.Y, GL_TEXTURE0);
        texIds[25] = loadPNGTexture(Consts.Z, GL_TEXTURE0);
        texIds[26] = loadPNGTexture(Consts.ONE, GL_TEXTURE0);
        texIds[27] = loadPNGTexture(Consts.TWO, GL_TEXTURE0);
        texIds[28] = loadPNGTexture(Consts.THREE, GL_TEXTURE0);
        texIds[29] = loadPNGTexture(Consts.FOUR, GL_TEXTURE0);
        texIds[30] = loadPNGTexture(Consts.FIVE, GL_TEXTURE0);
        texIds[31] = loadPNGTexture(Consts.SIX, GL_TEXTURE0);
        texIds[32] = loadPNGTexture(Consts.SEVEN, GL_TEXTURE0);
        texIds[33] = loadPNGTexture(Consts.EIGHT, GL_TEXTURE0);
        texIds[34] = loadPNGTexture(Consts.NINE, GL_TEXTURE0);
        texIds[35] = loadPNGTexture(Consts.ZERO, GL_TEXTURE0);
        texIds[36] = loadPNGTexture(Consts.COLON, GL_TEXTURE0);
        texIds[37] = loadPNGTexture(Consts.COMMA, GL_TEXTURE0);
        texIds[38] = loadPNGTexture(Consts.DOT, GL_TEXTURE0);
        texIds[39] = loadPNGTexture(Consts.WATER_PARTICLE, GL_TEXTURE0);
    }
    
    private int loadPNGTexture(String filename, int textureUnit) {
        ByteBuffer buf = null;
        int tWidth = 0;
        int tHeight = 0;
        
        try (InputStream in = new FileInputStream(Consts.TEXTURES_PATH + filename)) {
            PNGDecoder decoder = new PNGDecoder(in);
            tWidth = decoder.getWidth();
            tHeight = decoder.getHeight();
            buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
            decoder.decode(buf, decoder.getWidth() * 4, Format.RGBA);
            buf.flip();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        
        int texId = glGenTextures();
        glActiveTexture(textureUnit);
        glBindTexture(GL_TEXTURE_2D, texId);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, tWidth, tHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
        glGenerateMipmap(GL_TEXTURE_2D);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glBindImageTexture(0, texId, 0, false, 0, GL_WRITE_ONLY, GL_RGBA32F);
        
        return texId;
    }
    
    private void initGeomVao() {
        imposterVBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, imposterVBO);
        glBufferData(GL_ARRAY_BUFFER, Consts.numberOfParticles * 4 * 3 * Float.BYTES, GL_STREAM_DRAW);
        
        imposterVAO = glGenVertexArrays();
        glBindVertexArray(imposterVAO);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 16, 0);
        glVertexAttribPointer(1, 1, GL_FLOAT, false, 16, 12);
        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
    
    private void initCompute() {
        int posResourseIndex = glGetProgramResourceIndex(computeProgram.program, GL_SHADER_STORAGE_BLOCK, "Pos");
        int velResourseIndex = glGetProgramResourceIndex(computeProgram.program, GL_SHADER_STORAGE_BLOCK, "Vel");
        int lifResourseIndex = glGetProgramResourceIndex(computeProgram.program, GL_SHADER_STORAGE_BLOCK, "Lif");
        
        IntBuffer props = BufferUtils.createIntBuffer(1);
        IntBuffer params = BufferUtils.createIntBuffer(1);
        props.put(0, GL_BUFFER_BINDING);
        
        glGetProgramResourceiv(computeProgram.program, GL_SHADER_STORAGE_BLOCK, posResourseIndex, props, null, params);
        posBinding = params.get(0);
        glGetProgramResourceiv(computeProgram.program, GL_SHADER_STORAGE_BLOCK, velResourseIndex, props, null, params);
        velBinding = params.get(0);
        glGetProgramResourceiv(computeProgram.program, GL_SHADER_STORAGE_BLOCK, lifResourseIndex, props, null, params);
        lifBinding = params.get(0);
        
        posSSBO = glGenBuffers();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, posSSBO);
        glBufferData(GL_SHADER_STORAGE_BUFFER, Consts.numberOfParticles * 4 * Float.BYTES, GL_DYNAMIC_COPY);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, posBinding, posSSBO);
        
        velSSBO = glGenBuffers();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, velSSBO);
        glBufferData(GL_SHADER_STORAGE_BUFFER, Consts.numberOfParticles * 4 * Float.BYTES, GL_STATIC_DRAW);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, velBinding, velSSBO);
        
        lifSSBO = glGenBuffers();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, lifSSBO);
        glBufferData(GL_SHADER_STORAGE_BUFFER, Consts.numberOfParticles * Float.BYTES, GL_STATIC_DRAW);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, lifBinding, lifSSBO);
        
        posBuffer = BufferUtils.createFloatBuffer(Consts.numberOfParticles * 4);
        velBuffer = BufferUtils.createFloatBuffer(Consts.numberOfParticles * 4);
        lifBuffer = BufferUtils.createFloatBuffer(Consts.numberOfParticles);
        for (ComputeDrop d : computeDrops) {
            posBuffer.put(d.getPosition().x);
            posBuffer.put(d.getPosition().y);
            posBuffer.put(d.getPosition().z);
            posBuffer.put(0);
            velBuffer.put(d.getVelocity().x);
            velBuffer.put(d.getVelocity().y);
            velBuffer.put(d.getVelocity().z);
            velBuffer.put(0);
            lifBuffer.put(d.getLifespan());
        }
        posBuffer.flip();
        velBuffer.flip();
        lifBuffer.flip();
        
        updateSSBOs(Consts.maxLifeSpan);
    }

    private void loop() {
        while (!glfwWindowShouldClose(window) && !InputHandler.isKeyDown(GLFW_KEY_ESCAPE)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            
            now = System.nanoTime();
            lastFrameDuration = (float) ((now - lastFrameTimeStamp) / 1000000.0);
            lastFrameTimeStamp = now;
            
            update();
            
            glEnable(GL_DEPTH_TEST);
            Matrix4f modelView = view.calcMatrix();
            Matrix3f normMatrix = new Matrix3f().load(modelView.mat4to3());
            final Vector4f lightPosCameraSpace = Vector4f.mul(modelView, new Vector4f(lightPos.x, lightPos.y, lightPos.z, 1.0f));
            
            glUseProgram(program.program);
            glUniform3fv(program.cameraSpaceLightPosUnif, lightPosCameraSpace.fillAndFlipBuffer());
            glUniformMatrix3fv(program.normalModelToCameraMatrixUnif, false, normMatrix.fillAndFlipBuffer());
            
            glUniformMatrix4fv(program.modelToCameraMatrixUnif, false, (modelView.translate(light.getPosition())).fillAndFlipBuffer());
            light.draw();
            glUniformMatrix4fv(program.modelToCameraMatrixUnif, false, (modelView.translate(ball.getPosition())).fillAndFlipBuffer());
            ball.draw();
            glUniformMatrix4fv(program.modelToCameraMatrixUnif, false, (modelView.translate(plane.getPosition())).fillAndFlipBuffer());
            plane.draw();
            
            //glDisable(GL_DEPTH_TEST);
            
            if (mode == 0) { // Basic display
                glUseProgram(particleProgram.program);
                for (Drop d : drops) {
                    Matrix4f tempModelView = new Matrix4f(modelView).translate(d.getPosition());
                    tempModelView = Matrix4f.mul(tempModelView, view.orientation.conjugate().matCast());
                    glUniformMatrix4fv(particleProgram.modelToCameraMatrixUnif, false, tempModelView.fillAndFlipBuffer());
                    Matrix3f tempNormMatrix = new Matrix3f().load(tempModelView.mat4to3());
                    glUniformMatrix3fv(particleProgram.normalModelToCameraMatrixUnif, false, tempNormMatrix.fillAndFlipBuffer());
                    glBindTexture(GL_TEXTURE_2D, texIds[39]);
                    d.draw();
                }
            } else if (mode == 1) { // Geometry shader
                glUseProgram(geometryProgram.program);
                glUniformMatrix4fv(geometryProgram.orientationMatrixUnif, false, view.orientation.conjugate().matCast().fillAndFlipBuffer());
                glUniformMatrix4fv(geometryProgram.modelToCameraMatrixUnif, false, modelView.fillAndFlipBuffer());
                
                FloatBuffer buffer = BufferUtils.createFloatBuffer(drops.length * 4);
                for (Drop d : drops) {
                    buffer.put(d.getPosition().x);
                    buffer.put(d.getPosition().y);
                    buffer.put(d.getPosition().z);
                    buffer.put(Consts.particleSize);
                }
                buffer.flip();
                
                glBindBuffer(GL_ARRAY_BUFFER, imposterVBO);
                glBufferData(GL_ARRAY_BUFFER, buffer, GL_STREAM_DRAW);
                glBindBuffer(GL_ARRAY_BUFFER, 0);
                glBindTexture(GL_TEXTURE_2D, texIds[39]);
                glBindVertexArray(imposterVAO);
                glDrawArrays(GL_POINTS, 0, Consts.numberOfParticles);
                glBindVertexArray(0);
            } else if (mode == 2) { // Instanced array
                glUseProgram(instancedProgram.program);
                glUniformMatrix4fv(instancedProgram.orientationMatrixUnif, false, view.orientation.conjugate().matCast().fillAndFlipBuffer());
                glUniformMatrix4fv(instancedProgram.modelToCameraMatrixUnif, false, modelView.fillAndFlipBuffer());
                glBindTexture(GL_TEXTURE_2D, texIds[39]);
                DropObject.drawInstanced(drops);
            } else if (mode == 3) { // Compute shader
                glUseProgram(instancedProgram.program);
                glUniformMatrix4fv(instancedProgram.orientationMatrixUnif, false, view.orientation.conjugate().matCast().fillAndFlipBuffer());
                glUniformMatrix4fv(instancedProgram.modelToCameraMatrixUnif, false, modelView.fillAndFlipBuffer());
                glBindTexture(GL_TEXTURE_2D, texIds[39]);
                
                glBindVertexArray(DropObject.computeVAO);
                glBindBuffer(GL_ARRAY_BUFFER, posSSBO);
                glEnableVertexAttribArray(7);
                glVertexAttribPointer(7, 4, GL_FLOAT, false, 0, 0);
                glDrawArraysInstanced(GL_TRIANGLE_STRIP, 0, 4, Consts.numberOfParticles);
                glDisableVertexAttribArray(7);
                glBindBuffer(GL_ARRAY_BUFFER, 0);
                glBindVertexArray(0);
            }
            
            if (showInfo) {
                glUseProgram(textProgram.program);
                Text fpsText = new Text("fps " + getFps(), 0.02f, new Vector2f(-99.0f, -82.0f));
                fpsText.draw();
                Text modeText = new Text(modeName[mode], 0.02f, new Vector2f(-99.0f, -87.0f));
                modeText.draw();
                Text numText = new Text("Number of particles: " + Consts.numberOfParticles, 0.02f, new Vector2f(-99.0f, -97.0f));
                numText.draw();
                Text vsyncText = new Text("Vsync: " + (vsync ? "enabled" : "disabled"), 0.02f, new Vector2f(-99.0f, -92.0f));
                vsyncText.draw();
            }
            
            glUseProgram(0);
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }
    
    private void update() {
        float delta = getLastFrameDuration() / 1000.0f;
        FPS[fpsPointer++] = delta;
        if (fpsPointer == FPS.length) {
            fpsPointer = 0;
        }
        
        if (!pause) {
            if (mode == 0 || mode == 1 || mode == 2) {
                for (Drop d : drops) {
                    d.update(delta);
                }
            } else if (mode == 3) {
                updateSSBOs(delta);
            } else if (mode == 4) {
                updateCL(delta);
            }
        }
        
        DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer y = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(window, x, y);
        x.rewind();
        y.rewind();
        float mouseX = (float) x.get();
        float mouseY = (float) y.get();
        float dx = mouseX - prevMouseX;
        float dy = prevMouseY - mouseY;
        prevMouseX = mouseX;
        prevMouseY = mouseY;
        
        if (InputHandler.isButtonDown(GLFW_MOUSE_BUTTON_1)) {
            Quaternion angleAxisY = new Quaternion().setAngleAxis((float) Math.toRadians(dx) * Consts.mouseSpeed, 0.0f, 1.0f, 0.0f);
            Quaternion angleAxisX = new Quaternion().setAngleAxis((float) Math.toRadians(dy) * Consts.mouseSpeed, -1.0f, 0.0f, 0.0f);
            view.orientation = Quaternion.mul(angleAxisX, Quaternion.mul(view.orientation, angleAxisY));
        }
        
        glfwSetScrollCallback(window, (win, mwx, mwy) -> {
            view.changeRadius((float) -mwy * Consts.zoomSpeed * delta);
        });
        
        if (InputHandler.isKeyDown(GLFW_KEY_W)) view.offsetTargetPos(new Vector3f(0.0f, 0.0f, -1.0f), Consts.movementSpeed * delta);
        if (InputHandler.isKeyDown(GLFW_KEY_S)) view.offsetTargetPos(new Vector3f(0.0f, 0.0f, 1.0f), Consts.movementSpeed * delta);
        if (InputHandler.isKeyDown(GLFW_KEY_A)) view.offsetTargetPos(new Vector3f(-1.0f, 0.0f, 0.0f), Consts.movementSpeed * delta);
        if (InputHandler.isKeyDown(GLFW_KEY_D)) view.offsetTargetPos(new Vector3f(1.0f, 0.0f, 0.0f), Consts.movementSpeed * delta);
        if (InputHandler.isKeyDown(GLFW_KEY_Q)) view.offsetTargetPos(new Vector3f(0.0f, 1.0f, 0.0f), Consts.movementSpeed * delta);
        if (InputHandler.isKeyDown(GLFW_KEY_E)) view.offsetTargetPos(new Vector3f(0.0f, -1.0f, 0.0f), Consts.movementSpeed * delta);
        if (InputHandler.isKeyDown(GLFW_KEY_SPACE)) view = new ViewData(new Vector3f(0.0f, 5.0f, 60.0f), new Quaternion(0.0f, 0.0f, 0.0f, 1.0f), 0.0f, 0.0f);
        if (InputHandler.isKeyPressed(GLFW_KEY_F)) showInfo = !showInfo;
        if (InputHandler.isKeyPressed(GLFW_KEY_R)) nextMode();
        if (InputHandler.isKeyPressed(GLFW_KEY_V)) pause = !pause;
        if (InputHandler.isKeyPressed(GLFW_KEY_C)) { vsync = !vsync; glfwSwapInterval(vsync ? 1 : 0); }
    }
    
    private void updateSSBOs(float delta) {
        float x = 0, dx = Consts.updateInterval;
        glUseProgram(computeProgram.program);
        while (x < delta) {
            if (x + dx > delta) dx = delta - x;
            glBindBuffer(GL_SHADER_STORAGE_BUFFER, posSSBO);
            glBufferSubData(GL_SHADER_STORAGE_BUFFER, 0, posBuffer);
            glBindBuffer(GL_SHADER_STORAGE_BUFFER, velSSBO);
            glBufferSubData(GL_SHADER_STORAGE_BUFFER, 0, velBuffer);
            glBindBuffer(GL_SHADER_STORAGE_BUFFER, lifSSBO);
            glBufferSubData(GL_SHADER_STORAGE_BUFFER, 0, lifBuffer);
            glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);

            glUseProgram(computeProgram.program);
            glUniform1f(computeProgram.deltaUnif, dx);
            glDispatchCompute(Consts.numberOfParticles / Consts.WORK_GROUP_SIZE, 1, 1);
            glMemoryBarrier(GL_ALL_BARRIER_BITS);

            glBindBuffer(GL_SHADER_STORAGE_BUFFER, posSSBO);
            glMapBufferRange(GL_SHADER_STORAGE_BUFFER, 0, Consts.numberOfParticles * 4, GL_MAP_WRITE_BIT | GL_MAP_INVALIDATE_BUFFER_BIT);
            glBindBuffer(GL_SHADER_STORAGE_BUFFER, velSSBO);
            glMapBufferRange(GL_SHADER_STORAGE_BUFFER, 0, Consts.numberOfParticles * 4, GL_MAP_WRITE_BIT | GL_MAP_INVALIDATE_BUFFER_BIT);
            glBindBuffer(GL_SHADER_STORAGE_BUFFER, lifSSBO);
            glMapBufferRange(GL_SHADER_STORAGE_BUFFER, 0, Consts.numberOfParticles, GL_MAP_WRITE_BIT | GL_MAP_INVALIDATE_BUFFER_BIT);
            glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
            x += dx;
        }
        glUseProgram(0);
    }
    
    private void updateCL(float delta) {
        /*float x = 0, dx = Consts.updateInterval * 10;
        while (x < delta) {
            if (x + dx > delta) dx = delta - x;
            clSetKernelArg1f(clKernel, 3, dx);
            kernel2DGlobalWorkSize.put(0, Consts.numberOfParticles / Consts.WORK_GROUP_SIZE).put(1, Consts.WORK_GROUP_SIZE);
            clEnqueueNDRangeKernel(clQueue, clKernel, 2, null, kernel2DGlobalWorkSize, null, null, null);
            clEnqueueReadBuffer(clQueue, clPosBuffer, 0, 0, posBufferCL, null, null);
            clEnqueueReadBuffer(clQueue, clVelBuffer, 1, 0, velBufferCL, null, null);
            clEnqueueReadBuffer(clQueue, clLifBuffer, 2, 0, lifBufferCL, null, null);
            clFinish(clQueue);
            x += dx;
        }*/
    }
    
    private int getFps() {
        float sum = 0;
        for (int i = 0; i < FPS.length; i++) {
            sum += FPS[i];
        }
        return (int) (1 / (sum / (float) FPS.length));
    }
    
    private float getLastFrameDuration() {
        return lastFrameDuration;
    }
    
    private void nextMode() {
        mode++;
        if (mode == modeName.length) {
            mode = 0;
        }
    }
    
    public static ProgramData getTextProgram() {
        return textProgram;
    }
    
    public static void main(String[] args) {
        new RA().start();
    }
}
