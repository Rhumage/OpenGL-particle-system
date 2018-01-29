package ra;

import math.Vector3f;
import math.Vector4f;

public final class Consts {
    public static final String PATH = "C:\\Users\\Kalmah\\Documents\\NetBeansProjects\\RA2\\src\\";
    public static final String FILES_PATH = PATH + "files\\";
    public static final String SHADERS_PATH = PATH + "shaders\\";
    public static final String TEXTURES_PATH = PATH + "textures\\";
    
    public static final String VERT_SHADER = "shader.vert";
    public static final String FRAG_SHADER = "shader.frag";
    public static final String TEXT_VERT_SHADER = "text.vert";
    public static final String TEXT_FRAG_SHADER = "text.frag";
    public static final String PARTICLES_VERT_SHADER = "particles.vert";
    public static final String PARTICLES_FRAG_SHADER = "particles.frag";
    public static final String GEOMETRY_VERT_SHADER = "geometry.vert";
    public static final String GEOMETRY_GEOM_SHADER = "geometry.geom";
    public static final String GEOMETRY_FRAG_SHADER = "geometry.frag";
    public static final String INSTANCED_VERT_SHADER = "instanced.vert";
    public static final String INSTANCED_FRAG_SHADER = "instanced.frag";
    public static final String COMPUTE_COMP_SHADER = "compute.comp";
    
    public static final String A = "a.png", B = "b.png", C = "c.png", D = "d.png", E = "e.png", F = "f.png", G = "g.png", H = "h.png", I = "i.png", J = "j.png",
                               K = "k.png", L = "l.png", M = "m.png", N = "n.png", O = "o.png", P = "p.png", Q = "q.png", R = "r.png", S = "s.png", T = "t.png",
                               U = "u.png", V = "v.png", W = "w.png", X = "x.png", Y = "y.png", Z = "z.png", ONE = "1.png", TWO = "2.png", THREE = "3.png",
                               FOUR = "4.png", FIVE = "5.png", SIX = "6.png", SEVEN = "7.png", EIGHT = "8.png", NINE = "9.png", ZERO = "0.png", COLON = "colon.png", 
                               COMMA = "comma.png", DOT = "dot.png";
    public static final String WATER_PARTICLE = "water.png";
    
    public static final String BALL = "ball.obj";
    public static final String PLANE = "plane.obj";
    public static final String PLANE2 = "plane3.obj";
    public static final String TEXT = "quad.obj";
    
    public static final String PARTICLES_CL = "particles.cl";
    
    public static final int WIDTH = 1440;
    public static final int HEIGHT = 810;
    
    public static final float fovY = 45.0f;
    public static final float zNear = 0.01f;
    public static final float zFar = 100.0f;
    public static final float movementSpeed = 65.0f;
    public static final float mouseSpeed = 0.2f;
    public static final float zoomSpeed = 200.0f;
    public static final float minZoom = 0.0f;
    public static final float maxZoom = 150.0f;
    
    public static Vector4f lightIntensity = new Vector4f(0.8f, 0.8f, 0.8f, 1.0f);
    public static Vector4f ambientIntensity = new Vector4f(0.4f, 0.4f, 0.4f, 1.0f);
    public static Vector4f diffuseColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    public static Vector4f specularColor = new Vector4f(0.25f, 0.25f, 0.25f, 1.0f);
    public static float lightAttenuation = 0.01f;
    public static float shininessFactor = 4.0f;
    
    public static final int maxLifeSpan = 5;
    public static final int numberOfParticles = 128;
    public static final Vector3f initialVelocity = new Vector3f(15.0f, 0.0f, 0.0f);
    public static final float particleSize = 0.2f;
    
    public static final int WORK_GROUP_SIZE = 128;
    public static final float updateInterval = 0.0001f;
}
