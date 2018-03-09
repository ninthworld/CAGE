package cage.core.scene.camera;

import cage.core.scene.Node;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public abstract class Camera extends Node {

    private float m_zNear;
    private float m_zFar;

    public Camera(Node parent) {
        super(parent);
        m_zNear = 0.1f;
        m_zFar = 1000.0f;
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
        return m_zNear;
    }

    public void setZNear(float zNear) {
        m_zNear = zNear;
    }

    public float getZFar() {
        return m_zFar;
    }

    public void setZFar(float zFar) {
        m_zFar = zFar;
    }


    public FloatBuffer getBufferData() {
    	Matrix4f projMatrix = getProjectionMatrix();
    	Matrix4f viewMatrix = getViewMatrix();
        FloatBuffer buffer = BufferUtils.createFloatBuffer(64);
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
