package cage.core.model.material;

import cage.core.graphics.Texture;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class Material {

    private Vector3f m_ambient;
    private Vector3f m_diffuse;
    private Vector3f m_specular;
    private float m_specularExp;
    private Texture m_diffuseMap;
    private Texture m_specularMap;
    private Texture m_highlightMap;
    private Texture m_normalMap;

    public Material() {
        m_ambient = new Vector3f();
        m_diffuse = new Vector3f();
        m_specular = new Vector3f();
        m_specularExp = 0.0f;
        m_diffuseMap = null;
        m_specularMap = null;
        m_highlightMap = null;
        m_normalMap = null;
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

    public Texture getDiffuseTexture() {
        return m_diffuseMap;
    }

    public void setDiffuse(Vector3f diffuse) {
        m_diffuseMap = null;
        m_diffuse = diffuse;
    }

    public void setDiffuse(Texture diffuse) {
        m_diffuseMap = diffuse;
    }

    public Vector3f getSpecularColor() {
        return new Vector3f(m_specular);
    }

    public float getSpecularExponent() {
        return m_specularExp;
    }

    public Texture getSpecularTexture() {
        return m_specularMap;
    }

    public Texture getSpecularHighlightTexture() {
        return m_highlightMap;
    }

    public void setSpecular(Vector3f specular, float exponent) {
        m_specularMap = null;
        m_highlightMap = null;
        m_specular = specular;
        m_specularExp = exponent;
    }

    public void setSpecular(Texture specular, Texture highlight) {
        m_specularMap = specular;
        m_highlightMap = highlight;
    }

    public Texture getNormalTexture() {
        return m_normalMap;
    }

    public void setNormalTexture(Texture normal) {
        m_normalMap = normal;
    }

    public FloatBuffer getBufferData() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        buffer.put(0, (m_diffuseMap == null ? 0.0f : 1.0f));
        buffer.put(1, (m_specularMap == null ? 0.0f : 1.0f));
        buffer.put(2, (m_normalMap == null ? 0.0f : 1.0f));
        buffer.put(3, m_specularExp);
        m_ambient.get(4, buffer).put(7, 1.0f);
        m_diffuse.get(8, buffer).put(11, 1.0f);
        m_specular.get(12, buffer).put(15, 1.0f);
        buffer.rewind();
        return buffer;
    }
}
