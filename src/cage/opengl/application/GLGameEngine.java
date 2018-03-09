package cage.opengl.application;

import cage.core.application.GameEngine;
import cage.core.application.GameTimer;
import cage.core.application.IGame;
import cage.opengl.graphics.GLGraphicsDevice;

public class GLGameEngine extends GameEngine {

    private GameTimer m_timer;

    public GLGameEngine(GLGameWindow window) {
        super(window, new GLGraphicsDevice(window), null);
        m_graphicsContext = ((GLGraphicsDevice)m_graphicsDevice).getGraphicsContext();
        m_timer = createTimer();
    }

    @Override
    public void run(IGame game) {
        game.initialize(this);

        int frames = 0;
        GameTimer fpsTimer = createTimer();
        double deltaTime;
        while(!m_window.isClosed()) {
            deltaTime = m_timer.getElapsed();
            m_timer.reset();

            game.update(this, deltaTime);
            game.render(this);
            m_graphicsContext.swapBuffers();

            m_window.update();

            if(fpsTimer.getElapsed() > 1.0) {
                m_fps = frames;
                frames = 0;
                fpsTimer.reset();
            }
            else {
                ++frames;
            }
        }
    }

    @Override
    public GameTimer createTimer() {
        return new GLGameTimer();
    }

    public void destroy() {
        ((GLGraphicsDevice)m_graphicsDevice).destroy();
    }
}
