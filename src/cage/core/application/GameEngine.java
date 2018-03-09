package cage.core.application;

import cage.core.asset.AssetLoader;
import cage.core.graphics.IGraphicsContext;
import cage.core.graphics.IGraphicsDevice;
import cage.core.scene.SceneGraph;

public abstract class GameEngine {

    protected GameWindow m_window;
    protected IGraphicsDevice m_graphicsDevice;
    protected IGraphicsContext m_graphicsContext;
    protected AssetLoader m_assetLoader;
    protected SceneGraph m_sceneGraph;
    protected int m_fps;

    protected GameEngine(GameWindow window, IGraphicsDevice graphicsDevice, IGraphicsContext graphicsContext) {
        m_window = window;
        m_graphicsDevice = graphicsDevice;
        m_graphicsContext = graphicsContext;
        m_assetLoader = new AssetLoader(m_graphicsDevice);
        m_sceneGraph = new SceneGraph();
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

    public AssetLoader getAssetLoader() {
        return m_assetLoader;
    }

    public SceneGraph getSceneGraph() {
        return m_sceneGraph;
    }

    public int getFPS() {
        return m_fps;
    }

    public abstract GameTimer createTimer();
}
