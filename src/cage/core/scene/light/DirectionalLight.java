package cage.core.scene.light;

import cage.core.scene.Node;
import cage.core.scene.SceneManager;
import cage.core.scene.light.type.AttenuationType;
import cage.core.utils.math.Direction;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class DirectionalLight extends Light implements ShadowCastableLight {

    private Vector3f direction;
    private boolean castShadow;

    public DirectionalLight(SceneManager sceneManager, Node parent) {
        super(sceneManager, parent);
        this.direction = new Vector3f();
        this.castShadow = false;
    }

    public Vector3fc getDirection() {
        return direction;
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

        direction = new Vector3f(Direction.FORWARD).mul(getLocalRotation());

        //getLocalPosition().normalize(new Vector3f()).get(8, buffer);
        direction.get(8, buffer);
        buffer.put(12, 2.0f); // Type
        buffer.put(15, (isCastShadow() ? 1.0f : 0.0f)); // Cast Shadow
        buffer.rewind();
    }
}
