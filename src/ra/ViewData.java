package ra;

import math.Vector3f;
import math.Quaternion;
import math.Matrix4f;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL31.*;

public class ViewData {
    public Vector3f targetPos;
    public Quaternion orientation;
    public float radius;
    public float degSpinRotation;
    
    public ViewData(Vector3f targetPos, Quaternion orientation, float radius, float degSpinRotation) {
        this.targetPos = targetPos;
        this.orientation = orientation;
        this.radius = radius;
        this.degSpinRotation = degSpinRotation;
    }

    public ViewData(ViewData viewData) {
        targetPos = new Vector3f(viewData.targetPos);
        orientation = new Quaternion(viewData.orientation);
        radius = viewData.radius;
        degSpinRotation = viewData.degSpinRotation;
    }
    
    public Matrix4f calcMatrix() {
        Matrix4f mat = new Matrix4f();
        mat = mat.translate(new Vector3f(0.0f, 0.0f, -radius));

        Quaternion angleAxis = new Quaternion().setAngleAxis((float) Math.toRadians(degSpinRotation), 0.0f, 0.0f, 1.0f);
        Quaternion fullRotation = Quaternion.mul(angleAxis, orientation);

        mat = Matrix4f.mul(mat, fullRotation.matCast());

        mat = mat.translate(new Vector3f(targetPos).negate());
        return mat;
    }
    
    public void changeRadius(float f) {
        radius = Math.min(Math.max(radius + f, Consts.minZoom), Consts.maxZoom);
    }

    public void offsetTargetPos(Vector3f cameraOffset, float delta) {
        if (InputHandler.isKeyDown(GLFW_KEY_LEFT_SHIFT)) {
            delta *= 0.1;
        }
        Matrix4f currMat = calcMatrix();
        Quaternion newOrientation = Quaternion.setFromNormalized(currMat);
        Quaternion invOrient = newOrientation.conjugate();
        Vector3f worldOffset = invOrient.transform(cameraOffset.scale(delta));
        targetPos = Vector3f.add(targetPos, worldOffset);
    }
    
    public void reshape(int width, int height) {
        glViewport(0, 0, width, height);
        Matrix4f perspectiveMatrix = new Matrix4f();
        
        float range = (float) (Math.tan(Math.toRadians(Consts.fovY / 2.0f)) * Consts.zNear);
        float aspect = (width / (float) height);
        float left = -range * aspect;
        float right = range * aspect;
        float bottom = -range;
        float top = range;

        perspectiveMatrix.m00 = (2.0f * Consts.zNear) / (right - left);
        perspectiveMatrix.m11 = (2.0f * Consts.zNear) / (top - bottom);
        perspectiveMatrix.m22 = -(Consts.zFar + Consts.zNear) / (Consts.zFar - Consts.zNear);
        perspectiveMatrix.m23 = -1.0f;
        perspectiveMatrix.m32 = -(2.0f * Consts.zFar * Consts.zNear) / (Consts.zFar - Consts.zNear);
        
        glBindBuffer(GL_UNIFORM_BUFFER, ProgramData.globalMatricesUBO);
        glBufferSubData(GL_UNIFORM_BUFFER, 0, perspectiveMatrix.fillAndFlipBuffer());
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
    }
}
