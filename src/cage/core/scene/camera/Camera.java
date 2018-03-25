package cage.core.scene.camera;

import cage.core.common.Readable;
import cage.core.graphics.config.LayoutConfig;
import cage.core.scene.Node;
import cage.core.scene.SceneManager;
import cage.core.scene.SceneNode;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public abstract class Camera extends SceneNode implements Readable {

    public static final LayoutConfig READ_LAYOUT = new LayoutConfig().mat4().mat4().mat4().mat4();
    public static final int READ_SIZE = READ_LAYOUT.getUnitSize() / 4;

    private float zNear;
    private float zFar;
    private Matrix4f viewMatrix;
    protected FloatBuffer buffer;

    public Camera(SceneManager sceneManager, Node parent) {
        super(sceneManager, parent);
        this.zNear = 0.1f;
        this.zFar = 1000.0f;
        this.viewMatrix = new Matrix4f().identity();
        this.buffer = BufferUtils.createFloatBuffer(READ_SIZE);
    }

    @Override
    protected void updateNode() {
        super.updateNode();

        viewMatrix.identity();
        viewMatrix.mul(new Matrix4f().identity().set(getWorldRotation()));
        viewMatrix.translate(getWorldPosition().mul(-1.0f, new Vector3f()));

        buffer.clear();
        viewMatrix.get(16, buffer);
        getViewMatrix().invert(new Matrix4f()).get(48, buffer);
        buffer.rewind();
    }

    public Matrix4fc getViewMatrix() {
        return viewMatrix;
    }

    public abstract Matrix4fc getProjectionMatrix();

    public float getZNear() {
        return zNear;
    }

    public void setZNear(float zNear) {
        this.zNear = zNear;
        notifyUpdate();
    }

    public float getZFar() {
        return zFar;
    }

    public void setZFar(float zFar) {
        this.zFar = zFar;
        notifyUpdate();
    }

    @Override
    public FloatBuffer readData() {
        return buffer;
    }

    @Override
    public void destroy() {
        super.destroy();
        getSceneManager().unregisterCamera(this);
    }
}
