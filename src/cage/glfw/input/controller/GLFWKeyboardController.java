package cage.glfw.input.controller;

import cage.core.input.ActionState;
import cage.core.input.action.IEvent;
import cage.core.input.component.IComponent;
import cage.core.input.component.Key;
import cage.core.input.controller.KeyboardController;
import cage.core.input.type.ActionType;
import cage.glfw.utils.GLFWUtils;
import org.lwjgl.glfw.GLFWKeyCallback;

import java.util.Iterator;

import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;

public class GLFWKeyboardController extends KeyboardController {

    private float deltaTime;

    public GLFWKeyboardController(long handle, int index, String name) {
        super(index, name);
        this.deltaTime = 0.0f;

        glfwSetKeyCallback(handle, new GLFWKeyCallback() {
            @Override
            public void invoke(long handle, int key, int scanCode, int action, int mods) {
                Iterator<ActionState> it = getActionStateIterator();
                while(it.hasNext()) {
                    ActionState actionState = it.next();
                    if(actionState.getComponent() instanceof Key) {
                        Key keyUsed = GLFWUtils.getKey(key);
                        ActionType actionUsed = GLFWUtils.getAction(action);
                        if(keyUsed == actionState.getComponent() &&
                                (actionUsed == actionState.getActionType() ||
                                        (actionState.getActionType() == ActionType.PRESS_AND_RELEASE &&
                                                (actionUsed == ActionType.PRESS || actionUsed == ActionType.RELEASE)))) {
                            actionState.getAction().performAction(deltaTime, new IEvent() {
                                @Override
                                public IComponent getComponent() {
                                    return actionState.getComponent();
                                }

                                @Override
                                public float getValue() {
                                    return 0.0f;
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    @Override
    public void update(float deltaTime) {
        this.deltaTime = deltaTime;
    }
}
