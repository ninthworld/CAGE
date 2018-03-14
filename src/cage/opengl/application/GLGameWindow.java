package cage.opengl.application;

import cage.core.application.GameWindow;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class GLGameWindow extends GameWindow {

    private long handle;

    public GLGameWindow(String title, int width, int height, int samples) {
        super(title, width, height, samples);
    }

    @Override
    public void initialize() {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        if(getMultisampleCount() > 1) {
            glfwWindowHint(GLFW_STENCIL_BITS, getMultisampleCount());
            glfwWindowHint(GLFW_SAMPLES, getMultisampleCount());
        }

        handle = glfwCreateWindow(getWidth(), getHeight(), getTitle(), 0, 0);
        if(handle == 0) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwSetWindowCloseCallback(handle, new GLFWWindowCloseCallback() {
            @Override
            public void invoke(long l) {
                setClosed(true);
            }
        });

        try(MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(handle, pWidth, pHeight);
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(
                    handle,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2);
        }

        glfwMakeContextCurrent(handle);
        glfwSwapInterval(1);
        glfwShowWindow(handle);
    }

    @Override
    public void update() {
        glfwPollEvents();
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(title);
        glfwSetWindowTitle(handle, title);
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        glfwSetWindowSize(handle, width, getHeight());
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        glfwSetWindowSize(handle, getWidth(), height);
    }

    public void destroy() {
        glfwFreeCallbacks(handle);
        glfwDestroyWindow(handle);
    }

    public long getHandle() {
        return handle;
    }
}
