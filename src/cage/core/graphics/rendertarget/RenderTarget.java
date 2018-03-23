package cage.core.graphics.rendertarget;

import cage.core.window.Window;
import cage.core.common.Destroyable;
import cage.core.graphics.texture.Texture;
import cage.core.window.listener.IResizeWindowListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class RenderTarget<T extends Texture> implements Destroyable {

    private Map<Integer, T> colorTextures;
    private T depthTexture;
    private int width;
    private int height;

    private Window window;
    private IResizeWindowListener resizeListener;

    public RenderTarget(int width, int height) {
        this.colorTextures = new HashMap<>();
        this.depthTexture = null;
        this.width = width;
        this.height = height;
        this.window = null;
        this.resizeListener = null;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        colorTextures.forEach((Integer i, T t) -> t.setSize(width, height));
        if(containsDepthTexture()) {
            depthTexture.setSize(width, height);
        }
    }

    public int getColorTextureCount() {
        return colorTextures.size();
    }

    public void attachColorTexture(int index, T colorTexture) {
        colorTextures.put(index, colorTexture);
    }

    public void detachColorTexture(int index) {
        colorTextures.remove(index);
    }

    public void detachAllColorTextures() {
        colorTextures.clear();
    }

    public Iterator<Map.Entry<Integer, T>> getColorTextureIterator() {
        return colorTextures.entrySet().iterator();
    }

    public boolean containsColorTexture(int index) {
        return colorTextures.containsKey(index);
    }

    public T getColorTexture(int index) {
        return colorTextures.get(index);
    }

    public void attachDepthTexture(T depthTexture) {
        this.depthTexture = depthTexture;
    }

    public void detachDepthTexture(T depthTexture) {
        this.depthTexture = null;
    }

    public boolean containsDepthTexture() {
        return depthTexture != null;
    }

    public T getDepthTexture() {
        return depthTexture;
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
