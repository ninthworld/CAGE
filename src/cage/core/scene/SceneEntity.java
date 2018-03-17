package cage.core.scene;

import cage.core.common.IBufferData;
import cage.core.graphics.config.LayoutConfig;
import cage.core.model.Model;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class SceneEntity extends SceneNode implements IBufferData {

    public static final int BUFFER_DATA_SIZE = 16;
    public static final LayoutConfig BUFFER_LAYOUT = new LayoutConfig().mat4();

    private Model model;
    protected FloatBuffer bufferData;

    public SceneEntity(SceneManager sceneManager, Node parent, Model model) {
        super(sceneManager, parent);
        this.model = model;
        this.bufferData = BufferUtils.createFloatBuffer(BUFFER_DATA_SIZE);
    }

    @Override
    protected void updateNode() {
        super.updateNode();

        bufferData.clear();
        getWorldTransform().get(bufferData);
        bufferData.rewind();
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    @Override
    public FloatBuffer getBufferData() {
        return bufferData;
    }
}
