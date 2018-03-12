package cage.core.application;

import cage.core.asset.AssetManager;
import cage.core.graphics.IGraphicsContext;
import cage.core.graphics.IGraphicsDevice;
import cage.core.render.RenderManager;
import cage.core.scene.SceneManager;

public abstract class GameEngine {

    protected GameWindow m_window;
    protected IGraphicsDevice m_graphicsDevice;
    protected IGraphicsContext m_graphicsContext;
    protected AssetManager m_assetManager;
    protected SceneManager m_sceneManager;
    protected RenderManager m_renderManager;
    protected int m_fps;

    protected GameEngine(GameWindow window, IGraphicsDevice graphicsDevice) {
        m_window = window;
        m_graphicsDevice = graphicsDevice;
        m_graphicsContext = graphicsDevice.getGraphicsContext();
        m_assetManager = new AssetManager(m_graphicsDevice);
        m_sceneManager = new SceneManager(m_window);
        m_renderManager = new RenderManager(m_graphicsDevice, m_graphicsContext, m_window, m_sceneManager, m_assetManager);
        m_fps = 0;
    }

    public abstract void run(IGame game);

    public GameWindow getWindow() {
        return m_window;
    }

    public IGraphicsDevice getGraphicsDevice() {
        return m_graphicsDevice;
    }

    public IGraphicsContext getGraphicsContext() {
        return m_graphicsContext;
    }

    public AssetManager getAssetManager() {
        return m_assetManager;
    }

    public SceneManager getSceneManager() {
        return m_sceneManager;
    }

    public RenderManager getRenderManager() {
        return m_renderManager;
    }

    public int getFPS() {
        return m_fps;
    }

    public abstract GameTimer createTimer();
}
