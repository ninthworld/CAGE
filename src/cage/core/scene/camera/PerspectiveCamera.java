package cage.core.scene.camera;

import cage.core.window.Window;
import cage.core.scene.Node;
import cage.core.scene.SceneManager;
import cage.core.window.listener.IResizeWindowListener;
import org.joml.Matrix4f;

public class PerspectiveCamera extends Camera {

    private float aspectRatio;
    private float fov;
    private Matrix4f projMatrix;

    private Window window;
    private IResizeWindowListener resizeListener;

    public PerspectiveCamera(SceneManager sceneManager, Node parent) {
        super(sceneManager, parent);
        this.aspectRatio = 1.0f;
        this.fov = 45.0f;
        this.projMatrix = new Matrix4f().identity();
        this.window = null;
        this.resizeListener = null;
    }

    @Override
    protected void updateNode() {
        super.updateNode();

        projMatrix.identity();
        projMatrix.perspective(fov, aspectRatio, getZNear(), getZFar());

        projMatrix.get(buffer);
        buffer.position(32);
        projMatrix.invert().get(buffer);
        buffer.rewind();
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

    public boolean containsResizeListener() {
        return resizeListener != null;
    }

    public IResizeWindowListener getResizeListener() {
        return resizeListener;
    }

    public void setResizeListener(IResizeWindowListener resizeListener) {
        if(resizeListener != null) {
            if(window != null) {
                window.removeListener(this.resizeListener);
                if(!window.containsListener(resizeListener)) {
                    window.addListener(resizeListener);
                }
            }
            this.resizeListener = resizeListener;
        }
    }

    public void removeResizeListener() {
        if(window != null) {
            window.removeListener(this.resizeListener);
        }
        this.resizeListener = null;
    }

    public Window getWindow() {
        return window;
    }

    public void setWindow(Window window) {
        this.window = window;
    }
}
