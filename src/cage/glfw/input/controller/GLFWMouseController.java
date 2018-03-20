package cage.glfw.input.controller;

import cage.core.input.ActionState;
import cage.core.input.action.IEvent;
import cage.core.input.component.Axis;
import cage.core.input.component.Button;
import cage.core.input.component.IComponent;
import cage.core.input.component.Key;
import cage.core.input.controller.MouseController;
import cage.core.input.type.ActionType;
import cage.glfw.utils.GLFWUtils;
import org.lwjgl.glfw.*;

import java.util.Iterator;

import static org.lwjgl.glfw.GLFW.*;

public class GLFWMouseController extends MouseController {

    private long handle;
    private float deltaTime;
    private int[] lastButtons;
    private double lastX, lastY;

    public GLFWMouseController(long handle, int index, String name) {
        super(index, name);
        this.handle = handle;
        this.deltaTime = 0.0f;
        this.lastButtons = new int[GLFW_MOUSE_BUTTON_LAST + 1];

        double[] mX = new double[1];
        double[] mY = new double[1];
        glfwGetCursorPos(handle, mX, mY);
        this.lastX = mX[0];
        this.lastY = mY[0];

        glfwSetScrollCallback(handle, new GLFWScrollCallback() {
            @Override
            public void invoke(long handle, double xOffset, double yOffset) {
                Iterator<ActionState> it = getActionStateIterator();
                while(it.hasNext()) {
                    ActionState actionState = it.next();
                    if(actionState.getComponent() instanceof Axis) {
                        if(actionState.getComponent() == Axis.RIGHT_X && xOffset != 0.0) {
                            actionState.getAction().performAction(deltaTime, new IEvent() {
                                @Override
                                public IComponent getComponent() {
                                    return actionState.getComponent();
                                }

                                @Override
                                public float getValue() {
                                    return (float)xOffset;
                                }
                            });
                        }
                        if(actionState.getComponent() == Axis.RIGHT_Y && yOffset != 0.0) {
                            actionState.getAction().performAction(deltaTime, new IEvent() {
                                @Override
                                public IComponent getComponent() {
                                    return actionState.getComponent();
                                }

                                @Override
                                public float getValue() {
                                    return (float)yOffset;
                                }
                            });
                        }
                    }
                }
            }
        });

        glfwSetCursorPosCallback(handle, new GLFWCursorPosCallback() {
            @Override
            public void invoke(long handle, double x, double y) {
                float dx = (float)(x - lastX);
                float dy = (float)(y - lastY);
                Iterator<ActionState> it = getActionStateIterator();
                while(it.hasNext()) {
                    ActionState actionState = it.next();
                    if(actionState.getComponent() instanceof Axis) {
                        if(actionState.getComponent() == Axis.LEFT_X && dx != 0.0f) {
                            actionState.getAction().performAction(deltaTime, new IEvent() {
                                @Override
                                public IComponent getComponent() {
                                    return actionState.getComponent();
                                }

                                @Override
                                public float getValue() {
                                    return dx;
                                }
                            });
                        }
                        if(actionState.getComponent() == Axis.LEFT_Y && dy != 0.0) {
                            actionState.getAction().performAction(deltaTime, new IEvent() {
                                @Override
                                public IComponent getComponent() {
                                    return actionState.getComponent();
                                }

                                @Override
                                public float getValue() {
                                    return dy;
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

        double[] mX = new double[1];
        double[] mY = new double[1];
        glfwGetCursorPos(handle, mX, mY);
        lastX = mX[0];
        lastY = mY[0];

        int[] currButtons = new int[lastButtons.length];
        for(int i=0; i<currButtons.length; ++i) {
            currButtons[i] = glfwGetMouseButton(handle, i);
        }

        Iterator<ActionState> it = getActionStateIterator();
        while(it.hasNext()) {
            ActionState actionState = it.next();
            if (actionState.getComponent() instanceof Button) {
                int button = GLFWUtils.getGLFWMouseButton((Button)actionState.getComponent());
                int lastInput = lastButtons[button];
                int currInput = currButtons[button];

                ActionType actionUsed = ActionType.NONE;
                if(lastInput > 0) {
                    if(currInput > 0) {
                        actionUsed = ActionType.REPEAT;
                    }
                    else {
                        actionUsed = ActionType.RELEASE;
                        if(actionState.getActionType() == ActionType.RELEASE) {
                            int i = 0;
                        }
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

        lastButtons = currButtons;
    }
}
