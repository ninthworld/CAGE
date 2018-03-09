package cage.opengl.application;

import cage.core.application.GameTimer;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class GLGameTimer extends GameTimer {

    public GLGameTimer() {
        super();
    }

    @Override
    public double getTime() {
        return glfwGetTime() - m_startTime;
    }
}
