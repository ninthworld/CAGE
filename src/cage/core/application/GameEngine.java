package cage.core.application;

import cage.core.asset.AssetManager;
import cage.core.graphics.IGraphicsContext;
import cage.core.graphics.IGraphicsDevice;
import cage.core.render.RenderManager;
import cage.core.scene.SceneManager;

public abstract class GameEngine {

    private GameWindow window;
    private IGraphicsDevice graphicsDevice;
    private IGraphicsContext graphicsContext;
    private AssetManager assetManager;
    private SceneManager sceneManager;
    private RenderManager renderManager;
    protected int fps;

    public GameEngine(GameWindow window, IGraphicsDevice graphicsDevice) {
        this.window = window;
        this.graphicsDevice = graphicsDevice;
        this.graphicsContext = graphicsDevice.getGraphicsContext();
        this.assetManager = new AssetManager(graphicsDevice);
        this.sceneManager = new SceneManager(window);
        this.renderManager = new RenderManager(this.graphicsDevice, this.graphicsContext, this.window, this.sceneManager, this.assetManager);
        this.fps = 0;
    }

    public abstract void run(IGame game);

    public GameWindow getWindow() {
        return window;
    }

    public IGraphicsDevice getGraphicsDevice() {
        return graphicsDevice;
    }

    public IGraphicsContext getGraphicsContext() {
        return graphicsContext;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public RenderManager getRenderManager() {
        return renderManager;
    }

    public int getFPS() {
        return fps;
    }

    public abstract GameTimer createTimer();
}
