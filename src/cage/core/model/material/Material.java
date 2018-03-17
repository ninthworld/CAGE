package cage.core.model.material;

import cage.core.common.IBufferData;
import cage.core.graphics.Texture;
import cage.core.graphics.config.LayoutConfig;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class Material implements IBufferData {

    public static final int BUFFER_DATA_SIZE = 12;
    public static final LayoutConfig BUFFER_LAYOUT = new LayoutConfig().float1().float1().float1().float1().float4().float4();

    private Vector3f diffuse;
    private Vector3f specular;
    private float specularExp;
    private Texture diffuseMap;
    private Texture specularMap;
    private Texture highlightMap;
    private Texture normalMap;

    public Material() {
        this.diffuse = new Vector3f();
        this.specular = new Vector3f();
        this.specularExp = 0.0f;
        this.diffuseMap = null;
        this.specularMap = null;
        this.highlightMap = null;
        this.normalMap = null;
    }

    public Vector3f getDiffuseColor() {
        return new Vector3f(diffuse);
    }

    public Texture getDiffuseTexture() {
        return diffuseMap;
    }

    public void setDiffuse(Vector3f diffuse) {
        this.diffuseMap = null;
        this.diffuse = diffuse;
    }

    public void setDiffuse(Texture diffuse) {
        this.diffuseMap = diffuse;
    }

    public Vector3f getSpecularColor() {
        return new Vector3f(specular);
    }

    public float getSpecularExponent() {
        return specularExp;
    }

    public Texture getSpecularTexture() {
        return specularMap;
    }

    public Texture getSpecularHighlightTexture() {
        return highlightMap;
    }

    public void setSpecular(Vector3f specular, float exponent) {
        this.specularMap = null;
        this.highlightMap = null;
        this.specular = specular;
        this.specularExp = exponent;
    }

    public void setSpecular(Texture specular, Texture highlight) {
        this.specularMap = specular;
        this.highlightMap = highlight;
    }

    public Texture getNormalTexture() {
        return normalMap;
    }

    public void setNormalTexture(Texture normal) {
        this.normalMap = normal;
    }

    @Override
    public FloatBuffer getBufferData() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(BUFFER_DATA_SIZE);
        buffer.put(0, (diffuseMap == null ? 0.0f : 1.0f));
        buffer.put(1, (specularMap == null ? 0.0f : 1.0f));
        buffer.put(2, (normalMap == null ? 0.0f : 1.0f));
        buffer.put(3, specularExp);
        diffuse.get(4, buffer).put(7, 1.0f);
        specular.get(8, buffer).put(11, 1.0f);
        buffer.rewind();
        return buffer;
    }
}
