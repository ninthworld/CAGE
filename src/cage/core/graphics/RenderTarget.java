package cage.core.graphics;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class RenderTarget<T extends Texture> {

    protected Map<Integer, T> m_colorTextures;
    protected T m_depthTexture;
    protected int m_width;
    protected int m_height;

    protected RenderTarget(int width, int height) {
        m_colorTextures = new HashMap<>();
        m_depthTexture = null;
        m_width = width;
        m_height = height;
    }

    public int getWidth() {
        return m_width;
    }

    public void setWidth(int width) {
        m_width = width;
        m_colorTextures.forEach((Integer i, T t) -> t.setWidth(m_width));
        if(containsDepthTexture()) {
            m_depthTexture.setWidth(m_width);
        }
    }

    public int getHeight() {
        return m_height;
    }

    public void setHeight(int height) {
        m_height = height;
        m_colorTextures.forEach((Integer i, T t) -> t.setHeight(m_height));
        if(containsDepthTexture()) {
            m_depthTexture.setHeight(m_height);
        }
    }

    public void attachColorTexture(int index, T colorTexture) {
        m_colorTextures.put(index, colorTexture);
    }

    public void detachColorTexture(int index) {
        m_colorTextures.remove(index);
    }

    public Iterator<Map.Entry<Integer, T>> getColorTextureIterator() {
        return m_colorTextures.entrySet().iterator();
    }

    public boolean containsColorTexture(int index) {
        return m_colorTextures.containsKey(index);
    }

    public Texture getColorTexture(int index) {
        return m_colorTextures.get(index);
    }

    public void attachDepthTexture(T depthTexture) {
        m_depthTexture = depthTexture;
    }

    public void detachDepthTexture(T depthTexture) {
        m_depthTexture = null;
    }

    public boolean containsDepthTexture() {
        return m_depthTexture != null;
    }

    public Texture getDepthTexture() {
        return m_depthTexture;
    }
}
