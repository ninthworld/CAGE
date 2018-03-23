package cage.glfw;

import cage.core.application.IGame;
import cage.core.gui.GUIManager;
import cage.core.input.InputManager;
import cage.nanovg.gui.NVGGUIManager;
import cage.opengl.engine.GLEngine;
import cage.glfw.window.GLFWWindow;
import cage.glfw.input.GLFWInputManager;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GLFWBootstrap {

    private String title;
    private int width;
    private int height;
    private boolean vsync;
    private int refreshRate;
    private int samples;
    private InputManager inputManager;
    private GUIManager guiManager;

    public GLFWBootstrap(String title, int width, int height) {this.title = title;
        this.width = width;
        this.height = height;
        this.vsync = false;
        this.refreshRate = 60;
        this.samples = 1;
        this.inputManager = new GLFWInputManager();
        this.guiManager = new NVGGUIManager();
    }

    public GLFWBootstrap setVSync(boolean vsync) {
        this.vsync = vsync;
        return this;
    }

    public GLFWBootstrap setRefreshRate(int refreshRate) {
        this.refreshRate = refreshRate;
        return this;
    }

    public GLFWBootstrap setSamples(int samples) {
        this.samples = samples;
        return this;
    }

    public GLFWBootstrap setInputManager(InputManager inputManager) {
        this.inputManager = inputManager;
        return this;
    }

    public GLFWBootstrap setGUIManager(GUIManager guiManager) {
        this.guiManager = guiManager;
        return this;
    }

    public void run(IGLGameConstructor app) {
        GLFWWindow window = null;
        GLEngine engine = null;
        IGame game = null;
        try {
            window = new GLFWWindow(title, width, height, vsync, refreshRate, samples);
            engine = new GLEngine(window, inputManager, guiManager);
            game = app.initialize(engine);
            engine.run(game);
        } catch(Exception e) {
            StringBuilder builder = new StringBuilder();
            builder.append(e.getMessage());
            Arrays.asList(e.getStackTrace()).forEach((StackTraceElement element) -> builder.append("\n").append(element.toString()));
            Logger.getGlobal().log(Level.SEVERE, builder.toString());
        } finally {
            if(game != null) game.destroy(engine);
            if(window != null) window.destroy();
            if(engine != null) engine.destroy();
        }
    }

    public interface IGLGameConstructor {
        IGame initialize(GLEngine engine);
    }
}
