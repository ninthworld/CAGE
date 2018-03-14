package cage.core.scene.camera;

import cage.core.scene.Node;
import org.joml.Matrix4f;

public class OrthographicCamera extends Camera {

    private float left;
    private float right;
    private float bottom;
    private float top;

    public OrthographicCamera(Node parent) {
        super(parent);
        this.left = -1.0f;
        this.right = 1.0f;
        this.bottom = -1.0f;
        this.top = 1.0f;
    }

    @Override
    public Matrix4f getProjectionMatrix() {
        Matrix4f orthoMatrix = new Matrix4f();
        orthoMatrix.identity();
        orthoMatrix.ortho(left, right, bottom, top, getZNear(), getZFar());
        return orthoMatrix;
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
