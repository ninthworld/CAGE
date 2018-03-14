package cage.core.scene;

import cage.core.graphics.IBufferData;
import cage.core.graphics.config.LayoutConfig;
import cage.core.model.Model;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class SceneEntity extends SceneNode implements IBufferData {

    public static final int BUFFER_DATA_SIZE = 16;
    public static final LayoutConfig BUFFER_LAYOUT = new LayoutConfig().mat4();

    private Model model;

    public SceneEntity(Node parent, Model model) {
        super(parent);
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    @Override
    public FloatBuffer getBufferData() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(BUFFER_DATA_SIZE);
        getWorldMatrix().get(buffer);
        buffer.rewind();
        return buffer;
    }
}
