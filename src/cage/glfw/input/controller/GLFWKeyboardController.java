package cage.glfw.input.controller;

import cage.core.input.ActionState;
import cage.core.input.action.InputEvent;
import cage.core.input.component.InputComponent;
import cage.core.input.component.Key;
import cage.core.input.controller.KeyboardController;
import cage.core.input.type.InputActionType;
import cage.glfw.utils.GLFWUtils;

import java.util.Iterator;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LAST;
import static org.lwjgl.glfw.GLFW.glfwGetKey;

public class GLFWKeyboardController extends KeyboardController {

    private long handle;
    private int[] lastKeys;

    public GLFWKeyboardController(long handle, int index, String name) {
        super(index, name);
        this.handle = handle;
        this.lastKeys = new int[GLFW_KEY_LAST + 1];
    }

    @Override
    public void update(float deltaTime) {
        int[] currKeys = new int[lastKeys.length];
        for(int i=0; i<currKeys.length; ++i) {
            currKeys[i] = glfwGetKey(handle, i);
        }

        Iterator<ActionState> it = getActionStateIterator();
        while(it.hasNext()) {
            ActionState actionState = it.next();
            if (actionState.getComponent() instanceof Key) {
                int key = GLFWUtils.getGLFWKey((Key)actionState.getComponent());
                int lastInput = lastKeys[key];
                int currInput = currKeys[key];

                InputActionType actionUsed = InputActionType.NONE;
                if(lastInput > 0) {
                    if(currInput > 0) {
                        actionUsed = InputActionType.REPEAT;
                    }
                    else {
                        actionUsed = InputActionType.RELEASE;
                    }
                }
                else if(currInput > 0) {
                    actionUsed = InputActionType.PRESS;
                }

                if(actionUsed == actionState.getActionType() ||
                        (actionState.getActionType() == InputActionType.PRESS_AND_RELEASE && (actionUsed == InputActionType.PRESS || actionUsed == InputActionType.RELEASE)) ||
                        (actionState.getActionType() == InputActionType.REPEAT && actionUsed == InputActionType.PRESS)) {
                    actionState.getAction().performAction(deltaTime, new InputEvent() {
                        @Override
                        public InputComponent getComponent() {
                            return actionState.getComponent();
                        }

                        @Override
                        public float getValue() {
                            return 1.0f;
                        }
                    });
                }
            }
        }

        lastKeys = currKeys;
    }
}
