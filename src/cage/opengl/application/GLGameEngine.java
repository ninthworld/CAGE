package cage.opengl.application;

import cage.core.application.GameEngine;
import cage.core.application.GameTimer;
import cage.core.application.IGame;
import cage.opengl.graphics.GLGraphicsDevice;

public class GLGameEngine extends GameEngine {

    private GameTimer timer;

    public GLGameEngine(GLGameWindow window) {
        super(window, new GLGraphicsDevice(window));
        this.timer = createTimer();
    }

    @Override
    public void run(IGame game) {
        game.initialize(this);
        getSceneManager().update();

        int frames = 0;
        GameTimer fpsTimer = createTimer();
        double deltaTime;
        while(!getWindow().isClosed()) {
            deltaTime = timer.getElapsed();
            timer.reset();

            game.update(this, deltaTime);
            getSceneManager().update();
            game.render(this);
            getRenderManager().render();
            getGraphicsContext().swapBuffers();

            getWindow().update();

            if(fpsTimer.getElapsed() > 1.0) {
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
    public GameTimer createTimer() {
        return new GLGameTimer();
    }

    public void destroy() {
        ((GLGraphicsDevice)getGraphicsDevice()).destroy();
    }
}
