package cage.core.scene;

import cage.core.model.Model;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class SceneEntity extends Node {

    private Model m_model;

    public SceneEntity(Node parent) {
        super(parent);
        m_model = null;
    }

    public Model getModel() {
        return m_model;
    }

    public void setModel(Model model) {
        m_model = model;
    }

    public FloatBuffer getBufferData() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        getWorldMatrix().get(buffer);
        buffer.rewind();
        return buffer;
    }
}
