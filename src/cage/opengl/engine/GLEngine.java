package cage.opengl.engine;

import cage.core.application.Timer;
import cage.core.engine.Engine;
import cage.core.application.Game;
import cage.core.gui.GUIManager;
import cage.core.input.InputManager;
import cage.nanovg.gui.NVGGUIManager;
import cage.opengl.application.GLTimer;
import cage.glfw.window.GLFWWindow;
import cage.opengl.graphics.GLGraphicsDevice;
import cage.glfw.input.GLFWInputManager;

import java.nio.file.Path;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glEnable;

public class GLEngine extends Engine {

    private GLTimer timer;

    public GLEngine(GLFWWindow window, InputManager inputManager, GUIManager guiManager, Path assetProperties) {
        super(window, inputManager, guiManager, new GLGraphicsDevice(window), assetProperties);
        if(inputManager instanceof GLFWInputManager) {
            ((GLFWInputManager) inputManager).initialize(window.getHandle());
        }
        if(guiManager instanceof NVGGUIManager) {
            ((NVGGUIManager) guiManager).initialize(window);
        }
        this.timer = (GLTimer)createTimer();
        initialize();
    }

    @Override
    public void run(Game game) {
        game.initialize(this);
        getSceneManager().update(0.0f);

        float deltaTime;
        while(!getWindow().isClosed()) {
            deltaTime = timer.getElapsedTime();
            timer.reset();

            game.update(this, deltaTime);
            getSceneManager().update(deltaTime);

            game.render(this);
            getRenderManager().render();
            getGUIManager().render();

            getGraphicsContext().swapBuffers();

            getWindow().update();
            getInputManager().update(deltaTime);
        }
    }

    @Override
    public Timer createTimer() {
        return new GLTimer();
    }
}
