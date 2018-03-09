package cage.opengl;

import cage.core.application.IGame;
import cage.opengl.application.GLGameEngine;
import cage.opengl.application.GLGameWindow;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GLBootstrap {

    private String m_title;
    private int m_width;
    private int m_height;
    private int m_samples;

    public GLBootstrap(String title, int width, int height) {
        m_title = title;
        m_width = width;
        m_height = height;
        m_samples = 1;
    }

    public GLBootstrap(String title, int width, int height, int samples) {
        m_title = title;
        m_width = width;
        m_height = height;
        m_samples = samples;
    }

    public void run(GLGameConstructor app) {

        GLGameWindow window = null;
        GLGameEngine engine = null;
        IGame game = null;
        try {
            window = new GLGameWindow(m_title, m_width, m_height, m_samples);
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
