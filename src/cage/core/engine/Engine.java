package cage.core.engine;

import cage.core.gui.GUIManager;
import cage.core.window.Window;
import cage.core.application.IGame;
import cage.core.application.ITimer;
import cage.core.asset.AssetManager;
import cage.core.graphics.IGraphicsContext;
import cage.core.graphics.IGraphicsDevice;
import cage.core.input.InputManager;
import cage.core.render.RenderManager;
import cage.core.scene.SceneManager;

public abstract class Engine {

    private Window window;
    private IGraphicsDevice graphicsDevice;
    private IGraphicsContext graphicsContext;
    private AssetManager assetManager;
    private SceneManager sceneManager;
    private RenderManager renderManager;
    private InputManager inputManager;
    private GUIManager guiManager;
    protected int fps;

    public Engine(Window window, InputManager inputManager, GUIManager guiManager, IGraphicsDevice graphicsDevice) {
        this.window = window;
        this.inputManager = inputManager;
        this.guiManager = guiManager;
        this.graphicsDevice = graphicsDevice;
        this.graphicsContext = graphicsDevice.getGraphicsContext();
        this.assetManager = new AssetManager(graphicsDevice);
        this.sceneManager = new SceneManager(window);
        this.renderManager = new RenderManager(this.graphicsDevice, this.graphicsContext, this.window, this.sceneManager, this.assetManager);
        this.fps = 0;
    }

    public abstract void run(IGame game);

    public Window getWindow() {
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

    public InputManager getInputManager() {
        return inputManager;
    }

    public GUIManager getGUIManager() {
        return guiManager;
    }

    public int getFPS() {
        return fps;
    }

    public abstract ITimer createTimer();
}
