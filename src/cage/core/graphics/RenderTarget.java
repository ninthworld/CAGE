package cage.core.graphics;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class RenderTarget<T extends Texture> {

    private Map<Integer, T> colorTextures;
    private T depthTexture;
    private int width;
    private int height;

    public RenderTarget(int width, int height) {
        this.colorTextures = new HashMap<>();
        this.depthTexture = null;
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
        colorTextures.forEach((Integer i, T t) -> t.setWidth(width));
        if(containsDepthTexture()) {
            depthTexture.setWidth(width);
        }
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
        colorTextures.forEach((Integer i, T t) -> t.setHeight(height));
        if(containsDepthTexture()) {
            depthTexture.setHeight(height);
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
}
