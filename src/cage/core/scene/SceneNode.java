package cage.core.scene;

import cage.core.model.Model;
import cage.core.scene.camera.OrthographicCamera;
import cage.core.scene.camera.PerspectiveCamera;
import cage.core.scene.light.AmbientLight;
import cage.core.scene.light.DirectionalLight;
import cage.core.scene.light.PointLight;

public class SceneNode extends Node {

    private SceneManager sceneManager;

    public SceneNode(SceneManager sceneManager, Node parent) {
        super(parent);
        this.sceneManager = sceneManager;
    }

    public SceneNode createSceneNode() {
        SceneNode node = new SceneNode(sceneManager, this);
        return node;
    }

    public SceneEntity createSceneEntity(Model model) {
        SceneEntity node = new SceneEntity(sceneManager, this, model);
        return node;
    }
    
    public PerspectiveCamera createPerspectiveCamera() {
        PerspectiveCamera camera = new PerspectiveCamera(sceneManager, this);
        camera.setSize(getSceneManager().getWindow().getWidth(), getSceneManager().getWindow().getHeight());
        camera.setSizableParent(getSceneManager().getWindow());
        getSceneManager().registerCamera(camera);
        return camera;
    }

    public OrthographicCamera createOrthographicCamera() {
        OrthographicCamera camera = new OrthographicCamera(sceneManager, this);
        addNode(camera);
        getSceneManager().registerCamera(camera);
        return camera;
    }

    public AmbientLight createAmbientLight() {
        AmbientLight light = new AmbientLight(sceneManager, this);
        getSceneManager().registerLight(light);
        return light;
    }

    public PointLight createPointLight() {
        PointLight light = new PointLight(sceneManager, this);
        getSceneManager().registerLight(light);
        return light;
    }

    public DirectionalLight createDirectionalLight() {
        DirectionalLight light = new DirectionalLight(sceneManager, this);
        getSceneManager().registerLight(light);
        return light;
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @Override
    public void destroy() {
        if(getParentNode() != null) {
            getParentNode().removeNode(this);
        }
    }
}
