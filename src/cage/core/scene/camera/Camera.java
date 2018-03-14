package cage.core.scene.camera;

import cage.core.graphics.IBufferData;
import cage.core.graphics.config.LayoutConfig;
import cage.core.scene.Node;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public abstract class Camera extends Node implements IBufferData {

    public static final int BUFFER_DATA_SIZE = 64;
    public static final LayoutConfig BUFFER_LAYOUT = new LayoutConfig().mat4().mat4().mat4().mat4();

    private float zNear;
    private float zFar;

    public Camera(Node parent) {
        super(parent);
        this.zNear = 0.1f;
        this.zFar = 1000.0f;
    }

    public Matrix4f getViewMatrix() {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.identity();
        viewMatrix.rotate(getWorldRotation().x, new Vector3f(1, 0, 0));
        viewMatrix.rotate(getWorldRotation().y, new Vector3f(0, 1, 0));
        viewMatrix.rotate(getWorldRotation().z, new Vector3f(0, 0, 1));
        viewMatrix.translate(getWorldPosition().mul(-1.0f));
        return viewMatrix;
    }

    public abstract Matrix4f getProjectionMatrix();

    public float getZNear() {
        return zNear;
    }

    public void setZNear(float zNear) {
        this.zNear = zNear;
    }

    public float getZFar() {
        return zFar;
    }

    public void setZFar(float zFar) {
        this.zFar = zFar;
    }

    @Override
    public FloatBuffer getBufferData() {
    	Matrix4f projMatrix = getProjectionMatrix();
    	Matrix4f viewMatrix = getViewMatrix();
        FloatBuffer buffer = BufferUtils.createFloatBuffer(BUFFER_DATA_SIZE);
        projMatrix.get(buffer);
        buffer.position(16);
        viewMatrix.get(buffer);
        buffer.position(32);
        projMatrix.invert().get(buffer);
        buffer.position(48);
        viewMatrix.invert().get(buffer);
        buffer.rewind();        
        return buffer;
    }
}
