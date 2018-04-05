package cage.core.scene;

import cage.core.scene.controller.NodeController;
import cage.core.window.Window;
import cage.core.scene.camera.Camera;
import cage.core.scene.camera.PerspectiveCamera;
import cage.core.scene.light.AmbientLight;
import cage.core.scene.light.Light;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SceneManager {

    private Window window;
    private List<NodeController> controllers;
    private List<Camera> cameras;
    private List<Light> lights;
    private Camera defaultCamera;
    private AmbientLight defaultAmbientLight;
    private SceneNode rootNode;

    public SceneManager(Window window) {
        this.window = window;
        this.controllers = new ArrayList<>();
        this.cameras = new ArrayList<>();
        this.lights = new ArrayList<>();
        this.rootNode = new SceneNode(this, null);
        this.defaultCamera = this.rootNode.createPerspectiveCamera();
        this.defaultAmbientLight = this.rootNode.createAmbientLight();
        this.defaultAmbientLight.setAmbientColor(new Vector3f(0.2f));
    }

    public void updateControllers(float deltaTime) {
        controllers.forEach((NodeController controller) -> controller.update(deltaTime));
    }

    public void updateNodes(float deltaTime) {
        rootNode.update(false);
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

    public Window getWindow() {
        return window;
    }

    public void setWindow(Window window) {
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

    public void addController(NodeController controller) {
        controllers.add(controller);
    }

    public void removeController(NodeController controller) {
        controllers.remove(controller);
    }

    public void removeController(int index) {
        controllers.remove(index);
    }

    public void removeAllControllers() {
        controllers.forEach(this::removeController);
    }

    public int getControllerCount() {
        return controllers.size();
    }

    public boolean containsController(NodeController controller) {
        return controllers.contains(controller);
    }

    public NodeController getController(int index) {
        return controllers.get(index);
    }

    public Iterator<NodeController> getControllerIterator() {
        return controllers.iterator();
    }
}
