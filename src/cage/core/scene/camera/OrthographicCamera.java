package cage.core.scene.camera;

import cage.core.scene.Node;
import cage.core.scene.SceneManager;
import org.joml.Matrix4f;

public class OrthographicCamera extends Camera {

    private float left;
    private float right;
    private float bottom;
    private float top;
    private Matrix4f projMatrix;

    public OrthographicCamera(SceneManager sceneManager, Node parent) {
        super(sceneManager, parent);
        this.left = -1.0f;
        this.right = 1.0f;
        this.bottom = -1.0f;
        this.top = 1.0f;
        this.projMatrix = new Matrix4f().identity();
    }

    @Override
    protected void updateNode() {
        super.updateNode();

        projMatrix.identity();
        projMatrix.ortho(left, right, bottom, top, getZNear(), getZFar());

        projMatrix.get(bufferData);
        bufferData.position(32);
        projMatrix.invert().get(bufferData);
        bufferData.rewind();
    }

    @Override
    public Matrix4f getProjectionMatrix() {
        return projMatrix;
    }

    public float getLeft() {
        return left;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public float getRight() {
        return right;
    }

    public void setRight(float right) {
        this.right = right;
    }

    public float getBottom() {
        return bottom;
    }

    public void setBottom(float bottom) {
        this.bottom = bottom;
    }

    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        this.top = top;
    }
}
