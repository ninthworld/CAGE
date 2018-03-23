package cage.core.scene.light;

import cage.core.scene.Node;
import cage.core.scene.SceneManager;
import cage.core.scene.light.type.AttenuationType;

import java.nio.FloatBuffer;

public class PointLight extends Light {

    private float range;
    private AttenuationType attenuationType;

    public PointLight(SceneManager sceneManager, Node parent) {
        super(sceneManager, parent);
        this.range = 0.0f;
        this.attenuationType = AttenuationType.LINEAR;
    }

    @Override
    protected void updateNode() {
        super.updateNode();
        buffer.put(16, 1.0f);
        buffer.put(17, range);
        buffer.put(18, (attenuationType == AttenuationType.LINEAR ? 1.0f : 0.0f));
        buffer.put(19, (attenuationType == AttenuationType.QUADRATIC ? 1.0f : 0.0f));
        buffer.rewind();
    }

    public float getRange() {
        return range;
    }

    public void setRange(float range) {
        this.range = range;
    }

    public AttenuationType getAttenuationType() {
        return attenuationType;
    }

    public void setAttenuation(AttenuationType type) {
        this.attenuationType = type;
    }
}
