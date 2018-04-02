package cage.core.model.material;

import cage.core.common.Readable;
import cage.core.graphics.texture.Texture;
import cage.core.graphics.config.LayoutConfig;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

public class Material implements Readable {

    public static final LayoutConfig READ_LAYOUT = new LayoutConfig().float1().float1().float1().float1().float1().float1().float2().float4().float4().float4();
    public static final int READ_SIZE = READ_LAYOUT.getUnitSize() / 4;

    private Vector3f diffuse;
    private Vector3f specular;
    private Vector3f emissive;
    private float shininess;
    private Texture diffuseMap;
    private Texture specularMap;
    private Texture highlightMap;
    private Texture emissiveMap;
    private Texture normalMap;
    private FloatBuffer buffer;

    public Material() {
        this.diffuse = new Vector3f(1.0f, 1.0f, 1.0f);
        this.specular = new Vector3f(1.0f, 1.0f, 1.0f);
        this.emissive = new Vector3f();
        this.shininess = 0.0f;
        this.diffuseMap = null;
        this.specularMap = null;
        this.highlightMap = null;
        this.emissiveMap = null;
        this.normalMap = null;
        this.buffer = BufferUtils.createFloatBuffer(READ_SIZE);
    }

    public Vector3fc getDiffuseColor() {
        return diffuse;
    }

    public Texture getDiffuseTexture() {
        return diffuseMap;
    }

    public void setDiffuse(Vector3fc diffuse) {
        this.diffuse.set(diffuse);
    }

    public void setDiffuse(float r, float g, float b) {
        setDiffuse(new Vector3f(r, g, b));
    }

    public void setDiffuse(Texture diffuse) {
        this.diffuseMap = diffuse;
    }

    public Vector3fc getSpecularColor() {
        return specular;
    }

    public Texture getSpecularTexture() {
        return specularMap;
    }

    public void setSpecular(Vector3fc specular) {
        this.specular.set(specular);
    }

    public void setSpecular(float r, float g, float b) {
        setSpecular(new Vector3f(r, g, b));
    }

    public void setSpecular(Texture specular) {
        this.specularMap = specular;
    }

    public Texture getHighlightTexture() {
        return highlightMap;
    }

    public void setHighlight(Texture highlight) {
        this.highlightMap = highlight;
    }

    public float getShininess() {
        return shininess;
    }

    public void setShininess(float shininess) {
        this.shininess = shininess;
    }

    public Vector3fc getEmissiveColor() {
        return emissive;
    }

    public Texture getEmissiveTexture() {
        return emissiveMap;
    }

    public void setEmissive(Vector3fc emissive) {
        this.emissive.set(emissive);
    }

    public void setEmissive(float r, float g, float b) {
        setEmissive(new Vector3f(r, g, b));
    }

    public void setEmissive(Texture emissive) {
        this.emissiveMap = emissive;
    }

    public Texture getNormalTexture() {
        return normalMap;
    }

    public void setNormal(Texture normal) {
        this.normalMap = normal;
    }

    @Override
    public FloatBuffer readData() {
        buffer.put(0, (diffuseMap == null ? 0.0f : 1.0f));
        buffer.put(1, (specularMap == null ? 0.0f : 1.0f));
        buffer.put(2, (highlightMap == null ? 0.0f : 1.0f));
        buffer.put(3, (emissiveMap == null ? 0.0f : 1.0f));
        buffer.put(4, (normalMap == null ? 0.0f : 1.0f));
        buffer.put(5, shininess);
        // 2 Float padding
        diffuse.get(8, buffer).put(11, 1.0f);
        specular.get(12, buffer).put(15, 1.0f);
        emissive.get(16, buffer).put(19, 1.0f);
        buffer.rewind();
        return buffer;
    }
}
