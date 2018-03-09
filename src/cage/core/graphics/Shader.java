package cage.core.graphics;

import java.util.HashMap;
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

    public void attachTexture(int index, Texture texture) {
        m_textures.put(index, texture);
    }

    public abstract void attachTexture(String name, Texture texture);

    public void setVertexShaderSource(String src) {
        m_vertexShaderSrc = src;
    }

    public void setFragmentShaderSource(String src) {
        m_fragmentShaderSrc = src;
    }

    public abstract void compile();
}
