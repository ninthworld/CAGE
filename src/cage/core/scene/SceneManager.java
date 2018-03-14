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

public class SceneManager extends SceneNode {

    private GameWindow window;
    private List<Camera> cameras;
    private List<Light> lights;
    private Camera defaultCamera;
    private AmbientLight defaultAmbientLight;

    public SceneManager(GameWindow window) {
        super(null);
        this.window = window;
        this.cameras = new ArrayList<>();
        this.lights = new ArrayList<>();
        this.defaultCamera = createPerspectiveCamera();
        this.defaultAmbientLight = createAmbientLight();
        this.defaultAmbientLight.setAmbientColor(0.1f, 0.1f, 0.1f);
    }

    public Camera getDefaultCamera() {
        return defaultCamera;
    }

    public AmbientLight getDefaultAmbientLight() {
        return defaultAmbientLight;
    }

    public PerspectiveCamera createPerspectiveCamera() {
        PerspectiveCamera camera = new PerspectiveCamera(null);
        camera.setAspectRatio((float)window.getWidth() / (float)window.getHeight());
        attachNode(camera);
        cameras.add(camera);
        return camera;
    }

    public OrthographicCamera createOrthographicCamera() {
        OrthographicCamera camera = new OrthographicCamera(null);
        attachNode(camera);
        cameras.add(camera);
        return camera;
    }

    public AmbientLight createAmbientLight() {
        AmbientLight light = new AmbientLight(null);
        attachNode(light);
        lights.add(light);
        return light;
    }

    public PointLight createPointLight() {
        PointLight light = new PointLight(null);
        attachNode(light);
        lights.add(light);
        return light;
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
}
