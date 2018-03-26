package cage.core.graphics.shader;

import cage.core.common.Destroyable;
import cage.core.graphics.buffer.ShaderStorageBuffer;
import cage.core.graphics.buffer.UniformBuffer;
import cage.core.graphics.texture.Texture;

import java.util.*;

public abstract class Shader implements Destroyable {

    protected Map<Integer, ShaderStorageBuffer> shaderStorageBuffers;
    protected Map<Integer, UniformBuffer> uniformBuffers;
    protected Map<Integer, Texture> textures;
    private String vertexShaderSrc;
    private String geometryShaderSrc;
    private String fragmentShaderSrc;

    public Shader() {
        this.shaderStorageBuffers = new HashMap<>();
        this.uniformBuffers = new HashMap<>();
        this.textures = new HashMap<>();
    }
    
    public void addShaderStorageBuffer(int index, ShaderStorageBuffer buffer) {
        shaderStorageBuffers.put(index, buffer);
    }
    
    public void removeShaderStorageBuffer(int index) {
        shaderStorageBuffers.remove(index);
    }

    public void removeShaderStorageBuffer(int index, ShaderStorageBuffer buffer) {
        shaderStorageBuffers.remove(index, buffer);
    }

    public void removeAllShaderStorageBuffers() {
        shaderStorageBuffers.forEach(this::removeShaderStorageBuffer);
    }

    public int getShaderStorageBufferCount() {
        return shaderStorageBuffers.size();
    }

    public boolean containsShaderStorageBuffer(int index) {
        return shaderStorageBuffers.containsKey(index);
    }

    public boolean containsShaderStorageBuffer(ShaderStorageBuffer buffer) {
        return shaderStorageBuffers.containsValue(buffer);
    }

    public ShaderStorageBuffer getShaderStorageBuffer(int index) {
        return shaderStorageBuffers.get(index);
    }

    public Iterator<Map.Entry<Integer, ShaderStorageBuffer>> getShaderStorageBufferIterator() {
        return shaderStorageBuffers.entrySet().iterator();
    }

    public abstract void addShaderStorageBuffer(String name, ShaderStorageBuffer buffer);

    public abstract void removeShaderStorageBuffer(String name);
    
    public abstract void removeShaderStorageBuffer(String name, ShaderStorageBuffer buffer);

    public abstract boolean containsShaderStorageBuffer(String name);
    
    public abstract ShaderStorageBuffer getShaderStorageBuffer(String name);

    public void addUniformBuffer(int index, UniformBuffer buffer) {
        uniformBuffers.put(index, buffer);
    }

    public void removeUniformBuffer(int index) {
        uniformBuffers.remove(index);
    }

    public void removeUniformBuffer(int index, UniformBuffer buffer) {
        uniformBuffers.remove(index, buffer);
    }

    public void removeAllUniformBuffers() {
        uniformBuffers.forEach(this::removeUniformBuffer);
    }

    public int getUniformBufferCount() {
        return uniformBuffers.size();
    }

    public boolean containsUniformBuffer(int index) {
        return uniformBuffers.containsKey(index);
    }

    public boolean containsUniformBuffer(UniformBuffer buffer) {
        return uniformBuffers.containsValue(buffer);
    }

    public UniformBuffer getUniformBuffer(int index) {
        return uniformBuffers.get(index);
    }

    public Iterator<Map.Entry<Integer, UniformBuffer>> getUniformBufferIterator() {
        return uniformBuffers.entrySet().iterator();
    }

    public abstract void addUniformBuffer(String name, UniformBuffer buffer);

    public abstract void removeUniformBuffer(String name);

    public abstract void removeUniformBuffer(String name, UniformBuffer buffer);

    public abstract boolean containsUniformBuffer(String name);

    public abstract UniformBuffer getUniformBuffer(String name);

    public void addTexture(int index, Texture texture) {
        textures.put(index, texture);
    }

    public void removeTexture(int index) {
        textures.remove(index);
    }

    public void removeTexture(int index, Texture texture) {
        textures.remove(index, texture);
    }

    public void removeAllTextures() {
        textures.forEach(this::removeTexture);
    }

    public int getTextureCount() {
        return textures.size();
    }

    public boolean containsTexture(int index) {
        return textures.containsKey(index);
    }

    public boolean containsTexture(Texture texture) {
        return textures.containsValue(texture);
    }

    public Texture getTexture(int index) {
        return textures.get(index);
    }

    public Iterator<Map.Entry<Integer, Texture>> getTextureIterator() {
        return textures.entrySet().iterator();
    }

    public abstract void addTexture(String name, Texture texture);

    public abstract void removeTexture(String name);

    public abstract void removeTexture(String name, Texture texture);

    public abstract boolean containsTexture(String name);

    public abstract Texture getTexture(String name);

    public void setVertexShaderSource(String src) {
        this.vertexShaderSrc = src;
    }

    public String getVertexShaderSource() {
        return vertexShaderSrc;
    }

    public void setGeometryShaderSrc(String src) {
        this.geometryShaderSrc = src;
    }

    public String getGeometryShaderSrc() {
        return geometryShaderSrc;
    }

    public void setFragmentShaderSource(String src) {
        this.fragmentShaderSrc = src;
    }

    public String getFragmentShaderSource() {
        return fragmentShaderSrc;
    }

    public abstract void compile();
}
