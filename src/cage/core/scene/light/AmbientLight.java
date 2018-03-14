package cage.core.scene.light;

import cage.core.scene.Node;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class AmbientLight extends Light {

    private Vector3f ambient;

    public AmbientLight(Node parent) {
        super(parent);
        this.ambient = new Vector3f();
    }

    public Vector3f getAmbientColor() {
        return new Vector3f(ambient);
    }

    public void setAmbientColor(Vector3f ambient) {
        this.ambient = ambient;
    }

    public void setAmbientColor(float r, float g, float b) {
        ambient = new Vector3f(r, g, b);
    }

    @Override
    public FloatBuffer getBufferData() {
        FloatBuffer buffer = super.getBufferData();
        ambient.get(0, buffer).put(3, 1.0f);
        buffer.rewind();
        return buffer;
    }
}
