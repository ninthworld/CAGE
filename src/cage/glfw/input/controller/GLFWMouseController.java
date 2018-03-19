package cage.glfw.input.controller;

import cage.core.input.ActionState;
import cage.core.input.action.IEvent;
import cage.core.input.component.Axis;
import cage.core.input.component.Button;
import cage.core.input.component.IComponent;
import cage.core.input.controller.MouseController;
import cage.core.input.type.ActionType;
import cage.glfw.utils.GLFWUtils;
import org.lwjgl.glfw.*;

import java.util.Iterator;

import static org.lwjgl.glfw.GLFW.*;

public class GLFWMouseController extends MouseController {

    private long handle;
    private float deltaTime;
    private int mouseX, mouseY;

    public GLFWMouseController(long handle, int index, String name) {
        super(index, name);
        this.handle = handle;
        this.deltaTime = 0.0f;

        double[] mX = new double[1];
        double[] mY = new double[1];
        glfwGetCursorPos(handle, mX, mY);
        this.mouseX = (int)mX[0];
        this.mouseY = (int)mY[0];

        glfwSetMouseButtonCallback(handle, new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long handle, int button, int action, int mods) {
                Iterator<ActionState> it = getActionStateIterator();
                while(it.hasNext()) {
                    ActionState actionState = it.next();
                    if(actionState.getComponent() instanceof Button) {
                        Button buttonUsed = GLFWUtils.getMouseButton(button);
                        ActionType actionUsed = GLFWUtils.getAction(action);
                        if(buttonUsed == actionState.getComponent() &&
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
                float dx = (float)(x - mouseX);
                float dy = (float)(y - mouseY);

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
        mouseX = (int)mX[0];
        mouseY = (int)mY[0];
    }
}
