package cage.core.scene;

import cage.core.application.GameWindow;
import cage.core.scene.camera.Camera;
import cage.core.scene.camera.OrthographicCamera;
import cage.core.scene.camera.PerspectiveCamera;
import cage.core.scene.light.AmbientLight;
import cage.core.scene.light.Light;
import cage.core.scene.light.PointLight;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SceneManager {

    private GameWindow window;
    private List<Camera> cameras;
    private List<Light> lights;
    private Camera defaultCamera;
    private AmbientLight defaultAmbientLight;
    private SceneNode rootNode;

    public SceneManager(GameWindow window) {
        this.window = window;
        this.cameras = new ArrayList<>();
        this.lights = new ArrayList<>();
        this.rootNode = new SceneNode(this, null);
        this.defaultCamera = this.rootNode.createPerspectiveCamera();
        this.defaultAmbientLight = this.rootNode.createAmbientLight();
        this.defaultAmbientLight.setAmbientColor(new Vector3f(0.2f));
    }

    public SceneNode getRootSceneNode() {
        return rootNode;
    }

    public Camera getDefaultCamera() {
        return defaultCamera;
    }

    public AmbientLight getDefaultAmbientLight() {
        return defaultAmbientLight;
    }

    public int getLightCount() {
        return lights.size();
    }

    public Iterator<Light> getLightIterator() {
        return lights.iterator();
    }

    public int getCameraCount() {
        return cameras.size();
    }

    public Iterator<Camera> getCameraIterator() {
        return cameras.iterator();
    }

    public GameWindow getWindow() {
        return window;
    }

    public void setWindow(GameWindow window) {
        this.window = window;
    }

    public void registerLight(Light light) {
        lights.add(light);
    }

    public void unregisterLight(Light light) {
        lights.remove(light);
    }

    public void registerCamera(Camera camera) {
        cameras.add(camera);
    }

    public void unregisterCamera(Camera camera) {
        cameras.remove(camera);
    }

    public void update() {
        rootNode.update(false);
    }
}
