package cage.core.scene.camera;

import cage.core.scene.Node;
import cage.core.scene.SceneManager;
import org.joml.Matrix4f;

public class PerspectiveCamera extends Camera {

    private float aspectRatio;
    private float fov;
    private Matrix4f projMatrix;

    public PerspectiveCamera(SceneManager sceneManager, Node parent) {
        super(sceneManager, parent);
        this.aspectRatio = 1.0f;
        this.fov = 45.0f;
        this.projMatrix = new Matrix4f().identity();
    }

    @Override
    protected void updateNode() {
        super.updateNode();

        projMatrix.identity();
        projMatrix.perspective(fov, aspectRatio, getZNear(), getZFar());

        projMatrix.get(bufferData);
        bufferData.position(32);
        projMatrix.invert().get(bufferData);
        bufferData.rewind();
    }

    @Override
    public Matrix4f getProjectionMatrix() {
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
