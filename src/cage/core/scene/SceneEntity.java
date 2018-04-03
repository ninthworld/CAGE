package cage.core.scene;

import cage.core.common.Readable;
import cage.core.graphics.config.LayoutConfig;
import cage.core.model.Model;
import cage.core.utils.math.AABB;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class SceneEntity extends SceneNode implements Readable {

    public static final LayoutConfig READ_LAYOUT = new LayoutConfig().mat4();
    public static final int READ_SIZE = READ_LAYOUT.getUnitSize() / 4;

    private AABB localBounds;
    private AABB worldBounds;
    private Model model;
    private boolean castShadow;
    protected FloatBuffer buffer;

    public SceneEntity(SceneManager sceneManager, Node parent, Model model, AABB bounds) {
        super(sceneManager, parent);
        this.localBounds = bounds;
        this.worldBounds = new AABB(new Vector3f(), new Vector3f());
        this.model = model;
        this.castShadow = true;
        this.buffer = BufferUtils.createFloatBuffer(READ_SIZE);
    }
    
    public SceneEntity(SceneManager sceneManager, Node parent, Model model) {
    	this(sceneManager, parent, model, new AABB(new Vector3f(-0.5f, -0.5f, -0.5f), new Vector3f(0.5f, 0.5f, 0.5f)));
    }

    @Override
    protected void updateNode() {
        super.updateNode();
        
        worldBounds.setMin(localBounds.getMin().mul(getWorldScale(), new Vector3f()).add(getWorldPosition()));
        worldBounds.setMax(localBounds.getMax().mul(getWorldScale(), new Vector3f()).add(getWorldPosition()));
        
        buffer.clear();
        getWorldTransform().get(buffer);
        buffer.rewind();
    }

    public AABB getLocalBounds() {
    	return localBounds;
    }
    
    public void setLocalBounds(AABB bounds) {
    	this.localBounds = bounds;
    }
    
    public AABB getWorldBounds() {
    	return worldBounds;
    }
    
    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public boolean isCastShadow() {
        return castShadow;
    }

    public void setCastShadow(boolean castShadow) {
        this.castShadow = castShadow;
    }

    @Override
    public FloatBuffer readData() {
        return buffer;
    }
}
