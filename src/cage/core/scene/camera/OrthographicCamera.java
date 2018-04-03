package cage.core.scene.camera;

import cage.core.scene.Node;
import cage.core.scene.SceneManager;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class OrthographicCamera extends Camera {

    private float left;
    private float right;
    private float bottom;
    private float top;

    public OrthographicCamera(SceneManager sceneManager, Node parent) {
        super(sceneManager, parent);
        this.left = -1.0f;
        this.right = 1.0f;
        this.bottom = -1.0f;
        this.top = 1.0f;
    }

    @Override
    protected void updateNode() {
        super.updateNode();

        projMatrix.identity();
        projMatrix.ortho(left, right, bottom, top, getZNear(), getZFar());
        getFrustum().setFrustum(getViewMatrix(), projMatrix);

        projMatrix.get(buffer);
        getProjectionMatrix().invert(new Matrix4f()).get(32, buffer);
        buffer.rewind();
    }

    public float getLeft() {
        return left;
    }

    public void setLeft(float left) {
        this.left = left;
        notifyUpdate();
    }

    public float getRight() {
        return right;
    }

    public void setRight(float right) {
        this.right = right;
        notifyUpdate();
    }

    public float getBottom() {
        return bottom;
    }

    public void setBottom(float bottom) {
        this.bottom = bottom;
        notifyUpdate();
    }

    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        this.top = top;
        notifyUpdate();
    }
}
