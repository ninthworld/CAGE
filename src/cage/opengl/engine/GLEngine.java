package cage.opengl.engine;

import cage.core.engine.Engine;
import cage.core.application.ITimer;
import cage.core.application.IGame;
import cage.core.input.InputManager;
import cage.opengl.application.GLTimer;
import cage.glfw.window.GLFWWindow;
import cage.opengl.graphics.GLGraphicsDevice;
import cage.glfw.input.GLFWInputManager;

public class GLEngine extends Engine {

    private GLTimer timer;

    public GLEngine(GLFWWindow window, InputManager inputManager) {
        super(window, inputManager, new GLGraphicsDevice(window));
        if(inputManager instanceof GLFWInputManager) {
            ((GLFWInputManager) inputManager).initialize(window.getHandle());
        }
        this.timer = (GLTimer)createTimer();
    }

    @Override
    public void run(IGame game) {
        game.initialize(this);
        getSceneManager().update(0.0f);

        int frames = 0;
        GLTimer fpsTimer = (GLTimer)createTimer();
        float deltaTime;
        while(!getWindow().isClosed()) {
            deltaTime = timer.getElapsedTime();
            timer.reset();

            game.update(this, deltaTime);
            getSceneManager().update(deltaTime);
            game.render(this);
            getRenderManager().render();
            getGraphicsContext().swapBuffers();

            getWindow().update();
            getInputManager().update(deltaTime);

            if(fpsTimer.getElapsedTime() > 1.0) {
                fps = frames;
                frames = 0;
                fpsTimer.reset();
            }
            else {
                ++frames;
            }
        }
    }

    @Override
    public ITimer createTimer() {
        return new GLTimer();
    }

    public void destroy() {
        ((GLGraphicsDevice)getGraphicsDevice()).destroy();
    }
}
