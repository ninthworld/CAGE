package cage.opengl;

import cage.core.application.IGame;
import cage.opengl.application.GLGameEngine;
import cage.opengl.application.GLGameWindow;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GLBootstrap {

    private String title;
    private int width;
    private int height;
    private boolean vsync;
    private int refreshRate;
    private int samples;

    public GLBootstrap(String title, int width, int height, boolean vsync, int refreshRate, int samples) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.vsync = vsync;
        this.refreshRate = refreshRate;
        this.samples = samples;
    }

    public GLBootstrap(String title, int width, int height, boolean vsync, int refreshRate) {
        this(title, width, height, vsync, refreshRate, 1);
    }

    public GLBootstrap(String title, int width, int height) {
        this(title, width, height, false, 60, 1);
    }

    public void run(IGLGameConstructor app) {

        GLGameWindow window = null;
        GLGameEngine engine = null;
        IGame game = null;
        try {
            window = new GLGameWindow(title, width, height, vsync, refreshRate, samples);
            engine = new GLGameEngine(window);
            game = app.game(engine);
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
        IGame game(GLGameEngine engine);
    }
}
