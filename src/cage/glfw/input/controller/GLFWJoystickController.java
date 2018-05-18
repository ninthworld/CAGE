package cage.glfw.input.controller;

import cage.core.input.ActionState;
import cage.core.input.action.InputEvent;
import cage.core.input.component.Axis;
import cage.core.input.component.Button;
import cage.core.input.component.InputComponent;
import cage.core.input.controller.JoystickController;
import cage.core.input.type.InputActionType;
import cage.glfw.utils.GLFWUtils;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class GLFWJoystickController extends JoystickController {

    private long handle;
    ByteBuffer lastButtons;

    public GLFWJoystickController(long handle, int index, String name) {
        super(index, name);
        this.handle = handle;
        this.lastButtons = (ByteBuffer) BufferUtils.createByteBuffer(14).put(new byte[14]).flip();
    }

    @Override
    public void update(float deltaTime) {
        Map<Axis, Float> axisValues = new HashMap<>();
        FloatBuffer axes = glfwGetJoystickAxes(GLFW_JOYSTICK_1 + getIndex());
        for(int i=0; i<axes.capacity(); ++i) {
            Axis axis = GLFWUtils.getAxis(i);
            float value = axes.get(i);
            if(value != 0.0f) {
                axisValues.put(axis, value);
            }
        }

        Map<Button, InputActionType> buttonActions = new HashMap<>();
        ByteBuffer buttons = glfwGetJoystickButtons(GLFW_JOYSTICK_1 + getIndex());
        for(int i=0; i<Math.min(lastButtons.capacity(), buttons.capacity()); ++i) {
            Button btn = GLFWUtils.getGamepadButton(i);
            int lastBtn = lastButtons.get(i);
            int currBtn = buttons.get(i);
            if(lastBtn > 0) {
                if(currBtn > 0) {
                    buttonActions.put(btn, InputActionType.REPEAT);
                }
                else {
                    buttonActions.put(btn, InputActionType.RELEASE);
                }
            }
            else if(currBtn > 0) {
                buttonActions.put(btn, InputActionType.PRESS);
            }
        }
        lastButtons = buttons;

        Iterator<ActionState> it = getActionStateIterator();
        while(it.hasNext()) {
            ActionState actionState = it.next();
            if(actionState.getComponent() instanceof Axis) {
                if(axisValues.containsKey(actionState.getComponent())) {
                    actionState.getAction().performAction(deltaTime, new InputEvent() {
                        @Override
                        public InputComponent getComponent() {
                            return actionState.getComponent();
                        }

                        @Override
                        public float getValue() {
                            return axisValues.get(actionState.getComponent());
                        }
                    });
                }
            }
            else if(actionState.getComponent() instanceof Button) {
                if(buttonActions.containsKey(actionState.getComponent())) {
                    InputActionType actionUsed = buttonActions.get(actionState.getComponent());
                    if(actionUsed == actionState.getActionType() ||
                            (actionState.getActionType() == InputActionType.PRESS_AND_RELEASE && (actionUsed == InputActionType.PRESS || actionUsed == InputActionType.RELEASE)) ||
                            (actionState.getActionType() == InputActionType.REPEAT && actionUsed == InputActionType.PRESS )) {
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
        }
    }
}
