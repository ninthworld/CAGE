package cage.glfw.window;

import cage.core.window.Window;
import cage.core.window.listener.*;
import org.lwjgl.glfw.*;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.Iterator;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class GLFWWindow extends Window {

    private long handle;

    public GLFWWindow(String title, int width, int height, boolean vsync, int refreshRate, int samples) {
        super(title, width, height, vsync, refreshRate, samples);
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
                Iterator<IWindowListener> it = getListenerIterator();
                while(it.hasNext()) {
                    IWindowListener listener = it.next();
                    if(listener instanceof ICloseWindowListener) {
                        ((ICloseWindowListener)listener).onWindowClose();
                    }
                }
            }
        });

        glfwSetWindowSizeCallback(handle, new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long handle, int w, int h) {
                width = w;
                height = h;
                if(!isFullscreen()) {
                    wWidth = w;
                    wHeight = h;
                }
                Iterator<IWindowListener> it = getListenerIterator();
                while(it.hasNext()) {
                    IWindowListener listener = it.next();
                    if(listener instanceof IResizeWindowListener) {
                        ((IResizeWindowListener)listener).onWindowResize(w, h);
                    }
                }
            }
        });

        glfwSetWindowPosCallback(handle, new GLFWWindowPosCallback() {
            @Override
            public void invoke(long handle, int x, int y) {
                posX = x;
                posY = y;
                if(!isFullscreen()) {
                    wPosX = x;
                    wPosY = y;
                }
                Iterator<IWindowListener> it = getListenerIterator();
                while(it.hasNext()) {
                    IWindowListener listener = it.next();
                    if(listener instanceof IMoveWindowListener) {
                        ((IMoveWindowListener)listener).onWindowMove(x, y);
                    }
                }
            }
        });

        glfwSetWindowMaximizeCallback(handle, new GLFWWindowMaximizeCallback() {
            @Override
            public void invoke(long handle, boolean maximize) {
                maximized = maximize;
                Iterator<IWindowListener> it = getListenerIterator();
                while(it.hasNext()) {
                    IWindowListener listener = it.next();
                    if(listener instanceof IMaximizeWindowListener) {
                        ((IMaximizeWindowListener)listener).onWindowMaximize(maximize);
                    }
                }
            }
        });

        glfwSetWindowFocusCallback(handle, new GLFWWindowFocusCallback() {
            @Override
            public void invoke(long handle, boolean focused) {
                Iterator<IWindowListener> it = getListenerIterator();
                while(it.hasNext()) {
                    IWindowListener listener = it.next();
                    if(listener instanceof IFocusWindowListener) {
                        ((IFocusWindowListener)listener).onWindowFocus(focused);
                    }
                }
            }
        });

        glfwMakeContextCurrent(handle);
        glfwSwapInterval((isVsync() ? 1 : 0));
        glfwShowWindow(handle);
    }

    @Override
    public void update() {
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
    public void setFullscreenSize(int width, int height) {
        super.setFullscreenSize(width, height);
        if(isFullscreen()) {
            setSize(width, height);
        }
    }

    @Override
    public void setWindowedSize(int width, int height) {
        super.setWindowedSize(width, height);
        if(!isFullscreen()) {
            setSize(width, height);
        }
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        glfwSetWindowPos(handle, x, y);
    }

    @Override
    public void setVsync(boolean vsync) {
        super.setVsync(vsync);
        glfwSwapInterval((vsync ? 1 : 0));
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
            glfwSetWindowMonitor(handle, glfwGetPrimaryMonitor(), 0, 0, getFullscreenWidth(), getFullscreenHeight(), getRefreshRate());
        }
        else {
            glfwSetWindowMonitor(handle, 0, getWindowedPositionX(), getWindowedPositionY(), getWindowedWidth(), getWindowedHeight(), getRefreshRate());
        }
    }

    @Override
    public void setMaximized(boolean maximized) {
        super.setMaximized(maximized);
        if(maximized) {
            glfwMaximizeWindow(handle);
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
