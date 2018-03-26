package cage.core.scene.light;

import cage.core.scene.Node;
import cage.core.scene.SceneManager;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class AmbientLight extends Light {

    public AmbientLight(SceneManager sceneManager, Node parent) {
        super(sceneManager, parent);
    }

    public Vector3fc getAmbientColor() {
        return getDiffuseColor();
    }

    public void setAmbientColor(Vector3f ambient) {
        setDiffuseColor(ambient);
    }

    public void setAmbientColor(float r, float g, float b) { setDiffuseColor(r, g, b); }
}
