package cage.core.scene.camera;

import cage.core.scene.Node;
import org.joml.Matrix4f;

public class OrthographicCamera extends Camera {

    private float m_left;
    private float m_right;
    private float m_bottom;
    private float m_top;

    public OrthographicCamera(Node parent) {
        super(parent);
        m_left = -1.0f;
        m_right = 1.0f;
        m_bottom = -1.0f;
        m_top = 1.0f;
    }

    @Override
    public Matrix4f getProjectionMatrix() {
        Matrix4f orthoMatrix = new Matrix4f();
        orthoMatrix.identity();
        orthoMatrix.ortho(m_left, m_right, m_bottom, m_top, getZNear(), getZFar());
        return orthoMatrix;
    }

    public float getLeft() {
        return m_left;
    }

    public void setLeft(float left) {
        m_left = left;
    }

    public float getRight() {
        return m_right;
    }

    public void setRight(float right) {
        m_right = right;
    }

    public float getBottom() {
        return m_bottom;
    }

    public void setBottom(float bottom) {
        m_bottom = bottom;
    }

    public float getTop() {
        return m_top;
    }

    public void setTop(float top) {
        m_top = top;
    }
}
