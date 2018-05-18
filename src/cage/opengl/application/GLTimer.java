package cage.opengl.application;

import cage.core.application.Timer;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class GLTimer implements Timer {

    private float startTime;
    private float lastTime;

    public GLTimer() {
        this.startTime = 0.0f;
        this.startTime = getCurrentTime();
        reset();
    }

    @Override
    public void reset() {
        lastTime = getCurrentTime();
    }

    @Override
    public float getElapsedTime() {
        return getCurrentTime() - lastTime;
    }

    @Override
    public float getCurrentTime() {
        return (float)(glfwGetTime() - startTime);
    }
}
