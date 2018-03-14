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

    private Vector3f diffuse;
    private Vector3f specular;

    public Light(Node parent) {
        super(parent);
        this.diffuse = new Vector3f();
        this.specular = new Vector3f();
    }

    public Vector3f getDiffuseColor() {
        return new Vector3f(diffuse);
    }

    public void setDiffuseColor(Vector3f diffuse) {
        this.diffuse = diffuse;
    }

    public void setDiffuseColor(float r, float g, float b) {
        diffuse = new Vector3f(r, g, b);
    }

    public Vector3f getSpecularColor() {
        return new Vector3f(specular);
    }

    public void setSpecularColor(Vector3f specular) {
        this.specular = specular;
    }

    public void setSpecularColor(float r, float g, float b) {
        specular = new Vector3f(r, g, b);
    }

    @Override
    public FloatBuffer getBufferData() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(BUFFER_DATA_SIZE);
        diffuse.get(4, buffer).put(7, 1.0f);
        specular.get(8, buffer).put(11, 1.0f);
        getWorldPosition().get(12, buffer).put(15, 1.0f);
        buffer.put(16, 0.0f);
        buffer.put(17, 0.0f);
        buffer.put(18, 0.0f);
        buffer.put(19, 0.0f);
        buffer.rewind();
        return buffer;
    }
}
