package cage.core.scene.camera;

import cage.core.common.Sizable;
import cage.core.common.listener.Listener;
import cage.core.common.listener.ResizeListener;
import cage.core.scene.Node;
import cage.core.scene.SceneManager;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PerspectiveCamera extends Camera implements Sizable {

    private int width;
    private int height;
    private Sizable sizableParent;
    private ResizeListener resizeListener;
    private List<Listener> listeners;

    private float fov;
    private Matrix4f projMatrix;

    public PerspectiveCamera(SceneManager sceneManager, Node parent) {
        super(sceneManager, parent);
        this.fov = 45.0f;
        this.projMatrix = new Matrix4f().identity();

        this.width = 1;
        this.height = 1;
        this.sizableParent = null;
        this.resizeListener = null;
        this.listeners = new ArrayList<>();
    }

    public float getFOV() {
        return fov;
    }

    public void setFOV(float fov) {
        this.fov = fov;
    }

    @Override
    protected void updateNode() {
        super.updateNode();

        projMatrix.identity();
        projMatrix.perspective(fov, (float)width / (float)height, getZNear(), getZFar());

        projMatrix.get(buffer);
        buffer.position(32);
        projMatrix.invert().get(buffer);
        buffer.rewind();
    }

    @Override
    public Matrix4f getProjectionMatrix() {
        return projMatrix;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        notifyResize();
    }

    @Override
    public void notifyResize() {
        for(Listener listener : listeners) {
            if(listener instanceof ResizeListener) {
                ((ResizeListener) listener).onResize(width, height);
            }
        }
    }

    @Override
    public Sizable getSizableParent() {
        return sizableParent;
    }

    @Override
    public ResizeListener getSizableParentListener() {
        return resizeListener;
    }

    @Override
    public void setSizableParent(Sizable parent) {
        setSizableParent(parent, this::setSize);
    }

    @Override
    public void setSizableParent(Sizable parent, ResizeListener listener) {
        if(hasSizableParent()) {
            removeSizableParent();
        }
        this.sizableParent = parent;
        this.resizeListener = listener;
        this.sizableParent.addListener(this.resizeListener);
    }

    @Override
    public void removeSizableParent() {
        this.sizableParent.removeListener(resizeListener);
        this.sizableParent = null;
        this.resizeListener = null;
    }

    @Override
    public boolean hasSizableParent() {
        return sizableParent != null;
    }

    @Override
    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void removeListener(int index) {
        this.listeners.remove(index);
    }

    @Override
    public void removeAllListeners() {
        this.listeners.clear();
    }

    @Override
    public int getListenerCount() {
        return listeners.size();
    }

    @Override
    public boolean containsListener(Listener listener) {
        return listeners.contains(listener);
    }

    @Override
    public Listener getListener(int index) {
        return listeners.get(index);
    }

    @Override
    public Iterator<Listener> getListenerIterator() {
        return listeners.iterator();
    }
}
