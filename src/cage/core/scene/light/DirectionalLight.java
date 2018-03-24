package cage.core.scene.light;

import cage.core.scene.Node;
import cage.core.scene.SceneManager;
import cage.core.scene.light.type.AttenuationType;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class DirectionalLight extends Light {

    public DirectionalLight(SceneManager sceneManager, Node parent) {
        super(sceneManager, parent);
    }

    public Vector3fc getDirection() {
        return getLocalPosition();
    }

    public void setDirection(Vector3f direction) {
        setLocalPosition(direction);
    }

    public void setDirection(float x, float y, float z) {
        setLocalPosition(x, y, z);
    }

    @Override
    protected void updateNode() {
        super.updateNode();
        getWorldPosition().normalize(new Vector3f()).get(12, buffer).put(15, 1.0f);
        buffer.put(16, 2.0f);
        buffer.rewind();
    }
}
