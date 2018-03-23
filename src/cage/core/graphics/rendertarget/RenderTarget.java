package cage.core.graphics.rendertarget;

import cage.core.common.Sizable;
import cage.core.common.listener.Listener;
import cage.core.common.listener.ResizeListener;
import cage.core.common.Destroyable;
import cage.core.graphics.texture.Texture;

import java.util.*;

public abstract class RenderTarget<T extends Texture> implements Destroyable, Sizable {

    private int width;
    private int height;
    private Sizable sizableParent;
    private ResizeListener resizeListener;
    private List<Listener> listeners;

    private Map<Integer, T> colorTextures;
    private T depthTexture;

    public RenderTarget(int width, int height) {
        this.colorTextures = new HashMap<>();
        this.depthTexture = null;

        this.width = width;
        this.height = height;
        this.sizableParent = null;
        this.resizeListener = null;
        this.listeners = new ArrayList<>();
    }

    public void addColorTexture(int index, T texture) {
        if(containsColorTexture(index)) {
            removeColorTexture(index);
        }
        colorTextures.put(index, texture);
        texture.setSizableParent(this);
    }

    public void removeColorTexture(int index) {
        colorTextures.remove(index).removeSizableParent();
    }

    public void removeColorTexture(int index, T texture) {
        colorTextures.remove(index, texture);
        texture.removeSizableParent();
    }

    public void removeAllColorTextures() {
        colorTextures.forEach(this::removeColorTexture);
    }

    public int getColorTextureCount() {
        return colorTextures.size();
    }

    public boolean containsColorTexture(int index) {
        return colorTextures.containsKey(index);
    }

    public boolean containsColorTexture(T texture) {
        return colorTextures.containsValue(texture);
    }

    public T getColorTexture(int index) {
        return colorTextures.get(index);
    }

    public Iterator<Map.Entry<Integer, T>> getColorTextureIterator() {
        return colorTextures.entrySet().iterator();
    }

    public T getDepthTexture() {
        return depthTexture;
    }

    public void setDepthTexture(T texture) {
        if(containsDepthTexture()) {
            removeDepthTexture();
        }
        this.depthTexture = texture;
    }

    public void removeDepthTexture() {
        depthTexture.removeSizableParent();
        depthTexture = null;
    }

    public boolean containsDepthTexture() {
        return depthTexture != null;
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
