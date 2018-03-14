package cage.core.scene.light;

import cage.core.scene.Node;
import cage.core.scene.light.type.AttenuationType;

import java.nio.FloatBuffer;

public class PointLight extends Light {

    private float range;
    private AttenuationType attenuationType;

    public PointLight(Node parent) {
        super(parent);
        this.range = 0.0f;
        this.attenuationType = AttenuationType.LINEAR;
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

    @Override
    public FloatBuffer getBufferData() {
        FloatBuffer buffer = super.getBufferData();
        buffer.put(16, range);
        buffer.put(17, (attenuationType == AttenuationType.CONSTANT ? 1.0f : 0.0f));
        buffer.put(18, (attenuationType == AttenuationType.LINEAR ? 1.0f : 0.0f));
        buffer.put(19, (attenuationType == AttenuationType.QUADRATIC ? 1.0f : 0.0f));
        buffer.rewind();
        return buffer;
    }
}
