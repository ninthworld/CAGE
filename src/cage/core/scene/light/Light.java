package cage.core.scene.light;

import cage.core.common.IBufferData;
import cage.core.graphics.config.LayoutConfig;
import cage.core.scene.Node;
import cage.core.scene.SceneManager;
import cage.core.scene.SceneNode;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public abstract class Light extends SceneNode implements IBufferData {

    public static final int BUFFER_DATA_SIZE = 20;
    public static final LayoutConfig BUFFER_LAYOUT = new LayoutConfig().float4().float4().float4().float4().float1().float1().float1().float1();

    private Vector3f diffuse;
    private Vector3f specular;
    protected FloatBuffer bufferData;

    public Light(SceneManager sceneManager, Node parent) {
        super(sceneManager, parent);
        this.diffuse = new Vector3f();
        this.specular = new Vector3f();
        this.bufferData = BufferUtils.createFloatBuffer(BUFFER_DATA_SIZE);
    }

    @Override
    protected void updateNode() {
        super.updateNode();

        bufferData.clear();
        diffuse.get(4, bufferData).put(7, 1.0f);
        specular.get(8, bufferData).put(11, 1.0f);
        getWorldPosition().get(12, bufferData).put(15, 1.0f);
        bufferData.put(16, 0.0f);   // Type
        bufferData.put(17, 0.0f);   // Range
        bufferData.put(18, 0.0f);   // Attenuation Linear
        bufferData.put(19, 0.0f);   // Attenuation Quadratic
        bufferData.rewind();
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
        return bufferData;
    }

    @Override
    public void destroy() {
        super.destroy();
        getSceneManager().unregisterLight(this);
    }
}
