package cage.core.scene.light;

import cage.core.scene.Node;
import cage.core.scene.light.type.AttenuationType;

import java.nio.FloatBuffer;

public class PointLight extends Light {

    private float m_range;
    private AttenuationType m_attenuationType;

    public PointLight(Node parent) {
        super(parent);
        m_range = 0.0f;
        m_attenuationType = AttenuationType.LINEAR;
    }

    public float getRange() {
        return m_range;
    }

    public void setRange(float range) {
        m_range = range;
    }

    public AttenuationType getAttenuationType() {
        return m_attenuationType;
    }

    public void setAttenuation(AttenuationType type) {
        m_attenuationType = type;
    }

    @Override
    public FloatBuffer getBufferData() {
        FloatBuffer buffer = super.getBufferData();
        buffer.put(16, m_range);
        buffer.put(17, (m_attenuationType == AttenuationType.CONSTANT ? 1.0f : 0.0f));
        buffer.put(18, (m_attenuationType == AttenuationType.LINEAR ? 1.0f : 0.0f));
        buffer.put(19, (m_attenuationType == AttenuationType.QUADRATIC ? 1.0f : 0.0f));
        buffer.rewind();
        return buffer;
    }
}
