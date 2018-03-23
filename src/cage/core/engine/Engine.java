package cage.core.engine;

import cage.core.gui.GUIManager;
import cage.core.window.Window;
import cage.core.application.Game;
import cage.core.application.Timer;
import cage.core.asset.AssetManager;
import cage.core.graphics.GraphicsContext;
import cage.core.graphics.GraphicsDevice;
import cage.core.input.InputManager;
import cage.core.render.RenderManager;
import cage.core.scene.SceneManager;

public abstract class Engine {

    private Window window;
    private GraphicsDevice graphicsDevice;
    private GraphicsContext graphicsContext;
    private AssetManager assetManager;
    private SceneManager sceneManager;
    private RenderManager renderManager;
    private InputManager inputManager;
    private GUIManager guiManager;
    protected int fps;

    public Engine(Window window, InputManager inputManager, GUIManager guiManager, GraphicsDevice graphicsDevice) {
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

    public abstract void run(Game game);

    public Window getWindow() {
        return window;
    }

    public GraphicsDevice getGraphicsDevice() {
        return graphicsDevice;
    }

    public GraphicsContext getGraphicsContext() {
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

    public abstract Timer createTimer();
}
