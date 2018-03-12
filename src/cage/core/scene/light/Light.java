package cage.core.scene.light;

import cage.core.graphics.IBufferData;
import cage.core.graphics.config.LayoutConfig;
import cage.core.scene.Node;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public abstract class Light extends Node implements IBufferData {

    public static final int BUFFER_DATA_SIZE = 20;
    public static final LayoutConfig BUFFER_LAYOUT = new LayoutConfig().float4().float4().float4().float4().float1().float1().float1().float1();

    protected Vector3f m_ambient;
    private Vector3f m_diffuse;
    private Vector3f m_specular;

    protected Light(Node parent) {
        super(parent);
        m_ambient = new Vector3f();
        m_diffuse = new Vector3f();
        m_specular = new Vector3f();
    }

    public Vector3f getDiffuseColor() {
        return new Vector3f(m_diffuse);
    }

    public void setDiffuseColor(Vector3f diffuse) {
        m_diffuse = diffuse;
    }

    public void setDiffuseColor(float r, float g, float b) {
        m_diffuse = new Vector3f(r, g, b);
    }

    public Vector3f getSpecularColor() {
        return new Vector3f(m_specular);
    }

    public void setSpecularColor(Vector3f specular) {
        m_specular = specular;
    }

    public void setSpecularColor(float r, float g, float b) {
        m_specular = new Vector3f(r, g, b);
    }

    @Override
    public FloatBuffer getBufferData() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(BUFFER_DATA_SIZE);
        m_ambient.get(0, buffer).put(3, 1.0f);
        m_diffuse.get(4, buffer).put(7, 1.0f);
        m_specular.get(8, buffer).put(11, 1.0f);
        getWorldPosition().get(12, buffer).put(15, 1.0f);
        buffer.put(16, 0.0f);
        buffer.put(17, 0.0f);
        buffer.put(18, 0.0f);
        buffer.put(19, 0.0f);
        buffer.rewind();
        return buffer;
    }
}
