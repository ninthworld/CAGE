package cage.core.scene.light;

import cage.core.common.Readable;
import cage.core.graphics.config.LayoutConfig;
import cage.core.scene.Node;
import cage.core.scene.SceneManager;
import cage.core.scene.SceneNode;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public abstract class Light extends SceneNode implements Readable {

    public static final LayoutConfig READ_LAYOUT = new LayoutConfig().float4().float4().float4().float1().float1().float1().float1();
    public static final int READ_SIZE = READ_LAYOUT.getUnitSize() / 4;

    private Vector3f diffuse;
    private Vector3f specular;
    protected FloatBuffer buffer;

    public Light(SceneManager sceneManager, Node parent) {
        super(sceneManager, parent);
        this.diffuse = new Vector3f();
        this.specular = new Vector3f();
        this.buffer = BufferUtils.createFloatBuffer(READ_SIZE);
    }

    @Override
    protected void updateNode() {
        super.updateNode();
        buffer.clear();
        diffuse.get(0, buffer);
        specular.get(4, buffer);
        getWorldPosition().get(8, buffer);
        buffer.put(12, 0.0f);   // Type
        buffer.put(13, 0.0f);   // Range
        buffer.put(14, 0.0f);   // Attenuation
        buffer.put(15, 0.0f);   // Cast Shadow
        buffer.rewind();
    }

    public Vector3fc getDiffuseColor() {
        return diffuse;
    }

    public void setDiffuseColor(Vector3f diffuse) {
        this.diffuse = diffuse;
    }

    public void setDiffuseColor(float r, float g, float b) {
        diffuse = new Vector3f(r, g, b);
    }

    public Vector3fc getSpecularColor() {
        return specular;
    }

    public void setSpecularColor(Vector3f specular) {
        this.specular = specular;
    }

    public void setSpecularColor(float r, float g, float b) {
        specular = new Vector3f(r, g, b);
    }

    @Override
    public FloatBuffer readData() {
        return buffer;
    }

    @Override
    public void destroy() {
        super.destroy();
        getSceneManager().unregisterLight(this);
    }
}
