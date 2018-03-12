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

    private GameWindow m_window;
    private List<Camera> m_cameras;
    private List<Light> m_lights;
    private Camera m_defaultCamera;
    private AmbientLight m_defaultAmbientLight;

    public SceneManager(GameWindow window) {
        super(null);
        m_window = window;
        m_cameras = new ArrayList<>();
        m_lights = new ArrayList<>();
        m_defaultCamera = createPerspectiveCamera();
        m_defaultAmbientLight = createAmbientLight();
        m_defaultAmbientLight.setAmbientColor(0.1f, 0.1f, 0.1f);
    }

    public Camera getDefaultCamera() {
        return m_defaultCamera;
    }

    public AmbientLight getDefaultAmbientLight() {
        return m_defaultAmbientLight;
    }

    public PerspectiveCamera createPerspectiveCamera() {
        PerspectiveCamera camera = new PerspectiveCamera(null);
        camera.setAspectRatio((float)m_window.getWidth() / (float)m_window.getHeight());
        attachNode(camera);
        m_cameras.add(camera);
        return camera;
    }

    public OrthographicCamera createOrthographicCamera() {
        OrthographicCamera camera = new OrthographicCamera(null);
        attachNode(camera);
        m_cameras.add(camera);
        return camera;
    }

    public AmbientLight createAmbientLight() {
        AmbientLight light = new AmbientLight(null);
        attachNode(light);
        m_lights.add(light);
        return light;
    }

    public PointLight createPointLight() {
        PointLight light = new PointLight(null);
        attachNode(light);
        m_lights.add(light);
        return light;
    }

    public int getLightCount() {
        return m_lights.size();
    }

    public Iterator<Light> getLightIterator() {
        return m_lights.iterator();
    }

    public int getCameraCount() {
        return m_cameras.size();
    }

    public Iterator<Camera> getCameraIterator() {
        return m_cameras.iterator();
    }
}
