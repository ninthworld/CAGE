package cage.glfw.input.controller;

import cage.core.input.ActionState;
import cage.core.input.action.IEvent;
import cage.core.input.component.Axis;
import cage.core.input.component.IComponent;
import cage.core.input.component.Key;
import cage.core.input.controller.KeyboardController;
import cage.core.input.type.ActionType;
import cage.glfw.utils.GLFWUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWKeyCallback;

import java.nio.ByteBuffer;
import java.util.Iterator;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LAST;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;

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

                ActionType actionUsed = ActionType.NONE;
                if(lastInput > 0) {
                    if(currInput > 0) {
                        actionUsed = ActionType.REPEAT;
                    }
                    else {
                        actionUsed = ActionType.RELEASE;
                    }
                }
                else if(currInput > 0) {
                    actionUsed = ActionType.PRESS;
                }

                if(actionUsed == actionState.getActionType() ||
                        (actionState.getActionType() == ActionType.REPEAT && actionUsed == ActionType.PRESS)) {
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

        lastKeys = currKeys;
    }
}
