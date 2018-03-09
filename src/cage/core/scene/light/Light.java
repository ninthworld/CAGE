package cage.core.scene.light;

import cage.core.scene.Node;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public abstract class Light extends Node {

    private Vector3f m_ambient;
    private Vector3f m_diffuse;
    private Vector3f m_specular;

    protected Light(Node parent) {
        super(parent);
        m_ambient = new Vector3f();
        m_diffuse = new Vector3f();
        m_specular = new Vector3f();
    }

    public Vector3f getAmbientColor() {
        return new Vector3f(m_ambient);
    }

    public void setAmbientColor(Vector3f ambient) {
        m_ambient = ambient;
    }

    public Vector3f getDiffuseColor() {
        return new Vector3f(m_diffuse);
    }

    public void setDiffuseColor(Vector3f diffuse) {
        m_diffuse = diffuse;
    }

    public Vector3f getSpecularColor() {
        return new Vector3f(m_specular);
    }

    public void setSpecularColor(Vector3f specular) {
        m_specular = specular;
    }

    public FloatBuffer getBufferData() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(20);
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
