package cage.core.scene.light;

import cage.core.scene.Node;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class PointLight extends Light {

    private float m_range;
    private AttenuationType m_attenuationType;
    private float m_attenuation;

    public PointLight(Node parent) {
        super(parent);
        m_range = 0.0f;
        m_attenuationType = AttenuationType.CONSTANT;
        m_attenuation = 1.0f;
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

    public float getAttenuation() {
        return m_attenuation;
    }

    public void setAttenuation(AttenuationType type, float attenuation) {
        m_attenuationType = type;
        m_attenuation = attenuation;
    }

    @Override
    public FloatBuffer getBufferData() {
        FloatBuffer buffer = super.getBufferData();
        buffer.put(16, m_range);
        buffer.put(17, m_attenuation);
        buffer.put(18, (m_attenuationType == AttenuationType.LINEAR ? 1.0f : 0.0f));
        buffer.put(19, (m_attenuationType == AttenuationType.QUADRATIC ? 1.0f : 0.0f));
        buffer.rewind();
        return buffer;
    }
}
