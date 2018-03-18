package cage.core.graphics.shader;

import cage.core.common.IDestroyable;
import cage.core.graphics.buffer.UniformBuffer;
import cage.core.graphics.texture.Texture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Shader implements IDestroyable {

    protected Map<Integer, UniformBuffer> uniformBuffers;
    protected Map<Integer, Texture> textures;
    private String vertexShaderSrc;
    private String fragmentShaderSrc;

    public Shader() {
        this.uniformBuffers = new HashMap<>();
        this.textures = new HashMap<>();
    }

    public void attachUniformBuffer(int index, UniformBuffer buffer) {
        this.uniformBuffers.put(index, buffer);
    }

    public abstract void attachUniformBuffer(String name, UniformBuffer buffer);

    public void detachUniformBuffer(int index) {
        this.uniformBuffers.remove(index);
    }

    public abstract void detachUniformBuffer(String name);

    public void detachUniformBuffer(UniformBuffer buffer) {
        List<Integer> keys = new ArrayList<>();
        for(Integer i : this.uniformBuffers.keySet()) {
            if(this.uniformBuffers.get(i).equals(buffer)) {
                keys.add(i);
            }
        }
        keys.forEach((Integer i) -> this.uniformBuffers.remove(i));
    }

    public boolean containsUniformBuffer(int index) {
        return uniformBuffers.containsKey(index);
    }

    public abstract boolean containsUniformBuffer(String name);

    public boolean containsUniformBuffer(UniformBuffer buffer) {
        return uniformBuffers.containsValue(buffer);
    }

    public UniformBuffer getUniformBuffer(int index) {
        return uniformBuffers.get(index);
    }

    public abstract UniformBuffer getUniformBuffer(String name);

    public void attachTexture(int index, Texture texture) {
        this.textures.put(index, texture);
    }

    public abstract void attachTexture(String name, Texture texture);

    public void detachTexture(int index) {
        this.textures.remove(index);
    }

    public abstract void detachTexture(String name);

    public void detachTexture(Texture texture) {
        List<Integer> keys = new ArrayList<>();
        for(Integer i : this.textures.keySet()) {
            if(this.textures.get(i).equals(texture)) {
                keys.add(i);
            }
        }
        keys.forEach((Integer i) -> this.textures.remove(i));
    }

    public boolean containsTexture(int index) {
        return textures.containsKey(index);
    }

    public abstract boolean containsTexture(String name);

    public boolean containsTexture(Texture texture) {
        return textures.containsValue(texture);
    }

    public Texture getTexture(int index) {
        return textures.get(index);
    }

    public abstract Texture getTexture(String name);

    public void setVertexShaderSource(String src) {
        this.vertexShaderSrc = src;
    }

    public String getVertexShaderSource() {
        return vertexShaderSrc;
    }

    public void setFragmentShaderSource(String src) {
        this.fragmentShaderSrc = src;
    }

    public String getFragmentShaderSource() {
        return fragmentShaderSrc;
    }

    public abstract void compile();
}
