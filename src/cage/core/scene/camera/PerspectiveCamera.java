package cage.core.scene.camera;

import cage.core.scene.Node;
import org.joml.Matrix4f;

public class PerspectiveCamera extends Camera {

    private float aspectRatio;
    private float fov;

    public PerspectiveCamera(Node parent) {
        super(parent);
        this.aspectRatio = 1.0f;
        this.fov = 45.0f;
    }

    @Override
    public Matrix4f getProjectionMatrix() {
        Matrix4f projMatrix = new Matrix4f();
        projMatrix.identity();
        projMatrix.perspective(fov, aspectRatio, getZNear(), getZFar());
        return projMatrix;
    }

    public float getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public float getFOV() {
        return fov;
    }

    public void setFOV(float fov) {
        this.fov = fov;
    }
}
