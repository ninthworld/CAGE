package cage.opengl.application;

import cage.core.application.GameWindow;
import cage.core.input.InputState;
import org.lwjgl.glfw.*;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.Iterator;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class GLGameWindow extends GameWindow {

    private long handle;

    public GLGameWindow(String title, int width, int height, int refreshRate, int samples) {
        super(title, width, height, refreshRate, samples);
    }

    public void initialize() {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_REFRESH_RATE, getRefreshRate());
        if(getMultisampleCount() > 1) {
            glfwWindowHint(GLFW_STENCIL_BITS, getMultisampleCount());
            glfwWindowHint(GLFW_SAMPLES, getMultisampleCount());
        }

        handle = glfwCreateWindow(getWidth(), getHeight(), getTitle(), 0, 0);
        if(handle == 0) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        try(MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(handle, pWidth, pHeight);
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            super.setFullscreenSize(vidmode.width(), vidmode.height());
            setPosition(
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2);
        }

        glfwSetWindowCloseCallback(handle, new GLFWWindowCloseCallback() {
            @Override
            public void invoke(long handle) {
                closed = true;
                Iterator<IListener> it = getListenerIterator();
                while(it.hasNext()) {
                    IListener listener = it.next();
                    if(listener instanceof IWindowCloseListener) {
                        ((IWindowCloseListener)listener).onWindowClose();
                    }
                }
            }
        });

        glfwSetWindowSizeCallback(handle, new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long handle, int w, int h) {
                width = w;
                height = h;
                Iterator<IListener> it = getListenerIterator();
                while(it.hasNext()) {
                    IListener listener = it.next();
                    if(listener instanceof IWindowResizeListener) {
                        ((IWindowResizeListener)listener).onWindowResize(w, h);
                    }
                }
            }
        });

        glfwSetWindowPosCallback(handle, new GLFWWindowPosCallback() {
            @Override
            public void invoke(long handle, int x, int y) {
                posX = x;
                posY = y;
                Iterator<IListener> it = getListenerIterator();
                while(it.hasNext()) {
                    IListener listener = it.next();
                    if(listener instanceof IWindowMoveListener) {
                        ((IWindowMoveListener)listener).onWindowMove(x, y);
                    }
                }
            }
        });

        glfwSetWindowFocusCallback(handle, new GLFWWindowFocusCallback() {
            @Override
            public void invoke(long handle, boolean focused) {
                Iterator<IListener> it = getListenerIterator();
                while(it.hasNext()) {
                    IListener listener = it.next();
                    if(listener instanceof IWindowFocusListener) {
                        ((IWindowFocusListener)listener).onWindowFocus(focused);
                    }
                }
            }
        });

        glfwSetMouseButtonCallback(handle, new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long handle, int button, int action, int mods) {
                Iterator<IListener> it = getListenerIterator();
                while(it.hasNext()) {
                    IListener listener = it.next();
                    if(listener instanceof IMouseButtonListener) {
                        ((IMouseButtonListener)listener).onMouseButton(button, (action == GLFW_RELEASE ? InputState.RELEASED : InputState.PRESSED));
                    }
                }
            }
        });

        glfwSetScrollCallback(handle, new GLFWScrollCallback() {
            @Override
            public void invoke(long handle, double xOffset, double yOffset) {
                Iterator<IListener> it = getListenerIterator();
                while(it.hasNext()) {
                    IListener listener = it.next();
                    if(listener instanceof IMouseWheelListener) {
                        ((IMouseWheelListener)listener).onMouseWheel(xOffset, yOffset);
                    }
                }
            }
        });

        glfwSetCursorPosCallback(handle, new GLFWCursorPosCallback() {
            @Override
            public void invoke(long handle, double x, double y) {
                Iterator<IListener> it = getListenerIterator();
                while(it.hasNext()) {
                    IListener listener = it.next();
                    if(listener instanceof IMouseMoveListener) {
                        ((IMouseMoveListener)listener).onMouseMove(x, y);
                    }
                }
            }
        });

        glfwSetKeyCallback(handle, new GLFWKeyCallback() {
            @Override
            public void invoke(long handle, int key, int scanCode, int action, int mods) {
                Iterator<IListener> it = getListenerIterator();
                while(it.hasNext()) {
                    IListener listener = it.next();
                    if(listener instanceof IKeyboardListener) {
                        ((IKeyboardListener)listener).onKeyboard(key, (action == GLFW_RELEASE ? InputState.RELEASED : InputState.PRESSED));
                    }
                }
            }
        });

        glfwMakeContextCurrent(handle);
        glfwSwapInterval(1);
        glfwShowWindow(handle);
    }

    @Override
    public void update() {
        glfwPollEvents();
        if(isMouseCentered()) {
            glfwSetCursorPos(handle, getWidth() / 2.0, getHeight() / 2.0);
        }
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(title);
        glfwSetWindowTitle(handle, title);
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        glfwSetWindowSize(handle, width, height);
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        glfwSetWindowPos(handle, x, y);
    }

    @Override
    public void setRefreshRate(int refreshRate) {
        super.setRefreshRate(refreshRate);
        setFullscreen(isFullscreen());
    }

    @Override
    public void setClosed(boolean closed) {
        super.setClosed(closed);
        glfwSetWindowShouldClose(handle, closed);
    }

    @Override
    public void setMouseVisible(boolean mouseVisible) {
        super.setMouseVisible(mouseVisible);
        if(mouseVisible) {
            glfwSetCursor(handle, GLFW_ARROW_CURSOR);
        }
        else {
            glfwSetCursor(handle, GLFW_CURSOR_HIDDEN);
        }
    }

    @Override
    public void setFullscreen(boolean fullscreen) {
        super.setFullscreen(fullscreen);
        if(fullscreen) {
            int x = getPositionX();
            int y = getPositionY();
            int w = getWidth();
            int h = getHeight();
            setSize(getFullscreenWidth(), getFullscreenHeight());
            glfwSetWindowMonitor(handle, glfwGetPrimaryMonitor(), 0, 0, getFullscreenWidth(), getFullscreenHeight(), getRefreshRate());
            super.setPosition(x, y);
            super.setSize(w, h);
        }
        else {
            setSize(getWidth(), getHeight());
            glfwSetWindowMonitor(handle, 0, getPositionX(), getPositionY(), getWidth(), getHeight(), getRefreshRate());
        }
    }

    public void destroy() {
        glfwFreeCallbacks(handle);
        glfwDestroyWindow(handle);
    }

    public long getHandle() {
        return handle;
    }
}
