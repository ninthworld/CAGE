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

    private long m_handle;

    public GLGameWindow(String title, int width, int height, int samples) {
        super(title, width, height, samples);
    }

    @Override
    public void initialize() {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        if(m_samples > 1) {
            glfwWindowHint(GLFW_STENCIL_BITS, m_samples);
            glfwWindowHint(GLFW_SAMPLES, m_samples);
        }

        m_handle = glfwCreateWindow(m_width, m_height, m_title, 0, 0);
        if(m_handle == 0) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwSetWindowCloseCallback(m_handle, new GLFWWindowCloseCallback() {
            @Override
            public void invoke(long l) {
                m_closed = true;
            }
        });

        try(MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(m_handle, pWidth, pHeight);
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(
                    m_handle,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2);
        }

        glfwMakeContextCurrent(m_handle);
        glfwSwapInterval(1);
        glfwShowWindow(m_handle);
    }

    @Override
    public void update() {
        glfwPollEvents();
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(title);
        glfwSetWindowTitle(m_handle, m_title);
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        glfwSetWindowSize(m_handle, m_width, m_height);
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        glfwSetWindowSize(m_handle, m_width, m_height);
    }

    public void destroy() {
        glfwFreeCallbacks(m_handle);
        glfwDestroyWindow(m_handle);
    }

    public long getHandle() {
        return m_handle;
    }
}
