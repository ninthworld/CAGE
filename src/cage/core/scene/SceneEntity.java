package cage.core.scene;

import cage.core.common.Readable;
import cage.core.graphics.config.LayoutConfig;
import cage.core.model.Model;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class SceneEntity extends SceneNode implements Readable {

    public static final LayoutConfig READ_LAYOUT = new LayoutConfig().mat4();
    public static final int READ_SIZE = READ_LAYOUT.getUnitSize() / 4;

    private Model model;
    protected FloatBuffer buffer;

    public SceneEntity(SceneManager sceneManager, Node parent, Model model) {
        super(sceneManager, parent);
        this.model = model;
        this.buffer = BufferUtils.createFloatBuffer(READ_SIZE);
    }

    @Override
    protected void updateNode() {
        super.updateNode();
        buffer.clear();
        getWorldTransform().get(buffer);
        buffer.rewind();
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    @Override
    public FloatBuffer readData() {
        return buffer;
    }
}
