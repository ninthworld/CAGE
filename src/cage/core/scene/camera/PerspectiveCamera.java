package cage.core.scene.camera;

import cage.core.scene.Node;
import org.joml.Matrix4f;

public class PerspectiveCamera extends Camera {

    private float m_aspectRatio;
    private float m_fov;

    public PerspectiveCamera(Node parent) {
        super(parent);
        m_aspectRatio = 1.0f;
        m_fov = 45.0f;
    }

    @Override
    public Matrix4f getProjectionMatrix() {
        Matrix4f projMatrix = new Matrix4f();
        projMatrix.identity();
        projMatrix.perspective(m_fov, m_aspectRatio, getZNear(), getZFar());
        return projMatrix;
    }

    public float getAspectRatio() {
        return m_aspectRatio;
    }

    public void setAspectRatio(float aspectRatio) {
        m_aspectRatio = aspectRatio;
    }

    public float getFOV() {
        return m_fov;
    }

    public void setFOV(float fov) {
        m_fov = fov;
    }
}
