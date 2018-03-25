package cage.core.scene.light;

import cage.core.scene.Node;
import cage.core.scene.SceneManager;
import cage.core.scene.light.type.AttenuationType;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class DirectionalLight extends Light implements ShadowCastableLight {

    private boolean castShadow;

    public DirectionalLight(SceneManager sceneManager, Node parent) {
        super(sceneManager, parent);
        this.castShadow = false;
    }

    public Vector3fc getDirection() {
        return getLocalPosition();
    }

    public void setDirection(Vector3fc direction) {
        setLocalPosition(direction);
    }

    public void setDirection(float x, float y, float z) {
        setLocalPosition(x, y, z);
    }

    @Override
    public boolean isCastShadow() {
        return castShadow;
    }

    @Override
    public void setCastShadow(boolean castShadow) {
        this.castShadow = castShadow;
    }

    @Override
    protected void updateNode() {
        super.updateNode();
        getLocalPosition().normalize(new Vector3f()).get(12, buffer).put(15, 1.0f);
        buffer.put(16, 2.0f);
        buffer.rewind();
    }
}
