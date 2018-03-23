package cage.core.scene.light;

import cage.core.scene.Node;
import cage.core.scene.SceneManager;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class AmbientLight extends Light {

    private Vector3f ambient;

    public AmbientLight(SceneManager sceneManager, Node parent) {
        super(sceneManager, parent);
        this.ambient = new Vector3f();
    }

    @Override
    protected void updateNode() {
        super.updateNode();
        ambient.get(0, buffer).put(3, 1.0f);
        buffer.put(16, 0.0f);
        buffer.rewind();
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
}
