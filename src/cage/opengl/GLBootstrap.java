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
    private int samples;

    public GLBootstrap(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.samples = 1;
    }

    public GLBootstrap(String title, int width, int height, int samples) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.samples = samples;
    }

    public void run(GLGameConstructor app) {

        GLGameWindow window = null;
        GLGameEngine engine = null;
        IGame game = null;
        try {
            window = new GLGameWindow(title, width, height, samples);
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

    public interface GLGameConstructor {
        IGame game(GLGameEngine engine);
    }
}
