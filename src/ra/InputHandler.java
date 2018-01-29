package ra;

import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

public class InputHandler {
    public static short[] keys = new short[65536];
    public static short[] buttons = new short[3];

    protected static GLFWKeyCallback keyboard = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if (action == GLFW_PRESS) {
                keys[key] = 1;
            } else if (action == GLFW_RELEASE) {
                keys[key] = 2;
            } else if (action == GLFW_REPEAT) {
                keys[key] = 3;
            }
        }
    };

    public static boolean isKeyDown(int keycode) {
        return keys[keycode] == 1 || keys[keycode] == 3;
    }

    public static boolean isKeyPressed(int keycode) {
        if (keys[keycode] == 1) {
            keys[keycode] = 0;
            return true;
        }
        return false;
    }

    protected static GLFWMouseButtonCallback mouse = new GLFWMouseButtonCallback() {
        @Override
        public void invoke(long window, int button, int action, int mods) {
            if (action == GLFW_PRESS) {
                buttons[button] = 1;
            } else if (action == GLFW_RELEASE) {
                buttons[button] = 2;
            } else if (action == GLFW_REPEAT) {
                buttons[button] = 3;
            }
        }
    };
    
    public static boolean isButtonDown(int buttoncode) {
        return buttons[buttoncode] == 1 || keys[buttoncode] == 3;
    }

    public static boolean isButtonPressed(int buttoncode) {
        if (buttons[buttoncode] == 1) {
            buttons[buttoncode] = 0;
            return true;
        }
        return false;
    }
}