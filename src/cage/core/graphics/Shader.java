package cage.core.graphics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Shader {

    protected Map<Integer, UniformBuffer> m_uniformBuffers;
    protected Map<Integer, Texture> m_textures;
    protected String m_vertexShaderSrc;
    protected String m_fragmentShaderSrc;

    protected Shader() {
        m_uniformBuffers = new HashMap<>();
        m_textures = new HashMap<>();
    }

    public void attachUniformBuffer(int index, UniformBuffer buffer) {
        m_uniformBuffers.put(index, buffer);
    }

    public abstract void attachUniformBuffer(String name, UniformBuffer buffer);

    public void detachUniformBuffer(int index) {
        m_uniformBuffers.remove(index);
    }

    public abstract void detachUniformBuffer(String name);

    public void detachUniformBuffer(UniformBuffer buffer) {
        List<Integer> keys = new ArrayList<>();
        for(Integer i : m_uniformBuffers.keySet()) {
            if(m_uniformBuffers.get(i).equals(buffer)) {
                keys.add(i);
            }
        }
        keys.forEach((Integer i) -> m_uniformBuffers.remove(i));
    }

    public boolean containsUniformBuffer(int index) {
        return m_uniformBuffers.containsKey(index);
    }

    public abstract boolean containsUniformBuffer(String name);

    public boolean containsUniformBuffer(UniformBuffer buffer) {
        return m_uniformBuffers.containsValue(buffer);
    }

    public UniformBuffer getUniformBuffer(int index) {
        return m_uniformBuffers.get(index);
    }

    public abstract UniformBuffer getUniformBuffer(String name);

    public void attachTexture(int index, Texture texture) {
        m_textures.put(index, texture);
    }

    public abstract void attachTexture(String name, Texture texture);

    public void detachTexture(int index) {
        m_textures.remove(index);
    }

    public abstract void detachTexture(String name);

    public void detachTexture(Texture texture) {
        List<Integer> keys = new ArrayList<>();
        for(Integer i : m_textures.keySet()) {
            if(m_textures.get(i).equals(texture)) {
                keys.add(i);
            }
        }
        keys.forEach((Integer i) -> m_textures.remove(i));
    }

    public boolean containsTexture(int index) {
        return m_textures.containsKey(index);
    }

    public abstract boolean containsTexture(String name);

    public boolean containsTexture(Texture texture) {
        return m_textures.containsValue(texture);
    }

    public Texture getTexture(int index) {
        return m_textures.get(index);
    }

    public abstract Texture getTexture(String name);

    public void setVertexShaderSource(String src) {
        m_vertexShaderSrc = src;
    }

    public String getVertexShaderSource() {
        return m_vertexShaderSrc;
    }

    public void setFragmentShaderSource(String src) {
        m_fragmentShaderSrc = src;
    }

    public String getFragmentShaderSource() {
        return m_fragmentShaderSrc;
    }

    public abstract void compile();
}
