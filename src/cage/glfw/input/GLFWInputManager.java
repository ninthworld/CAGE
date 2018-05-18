package cage.glfw.input;

import cage.core.input.InputManager;
import cage.core.input.controller.JoystickController;
import cage.core.input.controller.KeyboardController;
import cage.core.input.controller.MouseController;
import cage.glfw.input.controller.GLFWJoystickController;
import cage.glfw.input.controller.GLFWKeyboardController;
import cage.glfw.input.controller.GLFWMouseController;

import java.util.*;

import static org.lwjgl.glfw.GLFW.*;

public class GLFWInputManager extends InputManager {

    private KeyboardController keyboardController;
    private MouseController mouseController;
    private Map<Integer, JoystickController> joystickControllers;

    public GLFWInputManager() {
        super();
        this.joystickControllers = new HashMap<>();
    }

    public void initialize(long handle) {
        this.keyboardController = new GLFWKeyboardController(handle, 0, "keyboard");
        this.mouseController = new GLFWMouseController(handle, 0, "mouse");
        for(int i=0; i<=GLFW_JOYSTICK_LAST; ++i) {
            String name = glfwGetJoystickName(GLFW_JOYSTICK_1 + i);
            if(name != null) {
                this.joystickControllers.put(i, new GLFWJoystickController(handle, i, name));
            }
        }
    }

    @Override
    public void update(float deltaTime) {
        glfwPollEvents();
        keyboardController.update(deltaTime);
        mouseController.update(deltaTime);
        for(JoystickController controller : joystickControllers.values()) {
            controller.update(deltaTime);
        }
    }

    @Override
    public KeyboardController getKeyboardController() {
        return keyboardController;
    }

    @Override
    public MouseController getMouseController() {
        return mouseController;
    }

    @Override
    public JoystickController getJoystickController(int index) {
        if(joystickControllers.containsKey(index)) {
            return joystickControllers.get(index);
        }
        return null;
    }

    @Override
    public Iterator<JoystickController> getJoystickControllerIterator() {
        return joystickControllers.values().iterator();
    }
}
