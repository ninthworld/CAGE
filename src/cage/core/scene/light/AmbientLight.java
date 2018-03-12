package cage.core.scene.light;

import cage.core.scene.Node;
import org.joml.Vector3f;

public class AmbientLight extends Light {

    public AmbientLight(Node parent) {
        super(parent);
    }

    public Vector3f getAmbientColor() {
        return new Vector3f(m_ambient);
    }

    public void setAmbientColor(Vector3f ambient) {
        m_ambient = ambient;
    }

    public void setAmbientColor(float r, float g, float b) {
        m_ambient = new Vector3f(r, g, b);
    }
}
