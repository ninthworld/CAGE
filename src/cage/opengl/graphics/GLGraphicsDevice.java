package cage.opengl.graphics;

import cage.core.common.Destroyable;
import cage.core.graphics.*;
import cage.core.graphics.blender.Blender;
import cage.core.graphics.buffer.IndexBuffer;
import cage.core.graphics.buffer.ShaderStorageBuffer;
import cage.core.graphics.buffer.UniformBuffer;
import cage.core.graphics.buffer.VertexBuffer;
import cage.core.graphics.rasterizer.Rasterizer;
import cage.core.graphics.rendertarget.RenderTarget2D;
import cage.core.graphics.rendertarget.RenderTargetMS;
import cage.core.graphics.sampler.Sampler;
import cage.core.graphics.shader.Shader;
import cage.core.graphics.texture.Texture2D;
import cage.core.graphics.texture.TextureCubeMap;
import cage.core.graphics.texture.TextureMS;
import cage.core.graphics.type.FormatType;
import cage.core.graphics.vertexarray.VertexArray;
import cage.glfw.window.GLFWWindow;
import cage.opengl.graphics.blender.GLBlender;
import cage.opengl.graphics.buffer.GLIndexBuffer;
import cage.opengl.graphics.buffer.GLShaderStorageBuffer;
import cage.opengl.graphics.buffer.GLUniformBuffer;
import cage.opengl.graphics.buffer.GLVertexBuffer;
import cage.opengl.graphics.rasterizer.GLRasterizer;
import cage.opengl.graphics.rendertarget.GLRenderTarget2D;
import cage.opengl.graphics.rendertarget.GLRenderTargetMS;
import cage.opengl.graphics.sampler.GLSampler;
import cage.opengl.graphics.shader.GLShader;
import cage.opengl.graphics.texture.GLTexture2D;
import cage.opengl.graphics.texture.GLTextureCubeMap;
import cage.opengl.graphics.texture.GLTextureMS;
import cage.opengl.graphics.vertexarray.GLVertexArray;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class GLGraphicsDevice implements GraphicsDevice {

    private GLGraphicsContext graphicsContext;
    private GLFWWindow window;
    private List<Destroyable> glObjects;

    private GLRasterizer defaultRasterizer;
    private GLRasterizer defaultFXRasterizer;
    private GLSampler defaultSampler;
    private GLBlender defaultBlender;
    private GLTextureCubeMap defaultCubeMap;

    public GLGraphicsDevice(GLFWWindow window) {
        GLFWErrorCallback.createPrint(System.err);
        if(!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        this.window = window;
        this.window.initialize();
        GL.createCapabilities();

        this.glObjects = new ArrayList<>();

        this.defaultRasterizer = (GLRasterizer)createRasterizer();
        this.defaultFXRasterizer = (GLRasterizer)createRasterizer();
        this.defaultFXRasterizer.setDepthClipping(false);
        this.defaultSampler = (GLSampler)createSampler();
        this.defaultBlender = (GLBlender)createBlender();
        this.defaultCubeMap = (GLTextureCubeMap)createTextureCubeMap(1, 1);

        this.graphicsContext = new GLGraphicsContext(this.window, this.defaultBlender);
        this.graphicsContext.bindRasterizer(this.defaultRasterizer);
    }

    @Override
    public void destroy() {
        glObjects.forEach(Destroyable::destroy);
        glObjects.clear();
        glfwTerminate();
    }

    @Override
    public GLGraphicsContext getGraphicsContext() {
        return graphicsContext;
    }

    @Override
    public IndexBuffer createIndexBuffer() {
        GLIndexBuffer glIndexBuffer = new GLIndexBuffer();
        glObjects.add(glIndexBuffer);
        return glIndexBuffer;
    }

    @Override
    public VertexBuffer createVertexBuffer() {
        GLVertexBuffer glVertexBuffer = new GLVertexBuffer();
        glObjects.add(glVertexBuffer);
        return glVertexBuffer;
    }

    @Override
    public UniformBuffer createUniformBuffer() {
        GLUniformBuffer glUniformBuffer = new GLUniformBuffer();
        glObjects.add(glUniformBuffer);
        return glUniformBuffer;
    }

    @Override
    public ShaderStorageBuffer createShaderStorageBuffer() {
        GLShaderStorageBuffer glShaderStorageBuffer = new GLShaderStorageBuffer();
        glObjects.add(glShaderStorageBuffer);
        return glShaderStorageBuffer;
    }

    @Override
    public Shader createShader() {
        GLShader glShader = new GLShader();
        glObjects.add(glShader);
        return glShader;
    }

    @Override
    public Rasterizer createRasterizer() {
        GLRasterizer glRasterizer = new GLRasterizer();
        glObjects.add(glRasterizer);
        return glRasterizer;
    }

    @Override
    public Blender createBlender() {
        GLBlender glBlender = new GLBlender();
        glObjects.add(glBlender);
        return glBlender;
    }

    @Override
    public Texture2D createTexture2D(int width, int height) {
        GLTexture2D glTexture = new GLTexture2D(width, height);
        glTexture.setSampler(defaultSampler);
        glObjects.add(glTexture);
        return glTexture;
    }

    @Override
    public Texture2D createTexture2D(int width, int height, FormatType format) {
        GLTexture2D glTexture = new GLTexture2D(width, height, format);
        glTexture.setSampler(defaultSampler);
        glObjects.add(glTexture);
        return glTexture;
    }

    @Override
    public Texture2D createTexture2D(int width, int height, boolean mipmapping) {
        GLTexture2D glTexture = new GLTexture2D(width, height, mipmapping);
        glTexture.setSampler(defaultSampler);
        glObjects.add(glTexture);
        return glTexture;
    }

    @Override
    public Texture2D createTexture2D(int width, int height, FormatType format, boolean mipmapping) {
        GLTexture2D glTexture = new GLTexture2D(width, height, format, mipmapping);
        glTexture.setSampler(defaultSampler);
        glObjects.add(glTexture);
        return glTexture;
    }

    @Override
    public TextureCubeMap createTextureCubeMap(int width, int height) {
        GLTextureCubeMap glTexture = new GLTextureCubeMap(width, height);
        glTexture.setSampler(defaultSampler);
        glObjects.add(glTexture);
        return glTexture;
    }

    @Override
    public TextureCubeMap createTextureCubeMap(int width, int height, FormatType format) {
        GLTextureCubeMap glTexture = new GLTextureCubeMap(width, height, format);
        glTexture.setSampler(defaultSampler);
        glObjects.add(glTexture);
        return glTexture;
    }

    @Override
    public TextureCubeMap createTextureCubeMap(int width, int height, boolean mipmapping) {
        GLTextureCubeMap glTexture = new GLTextureCubeMap(width, height, mipmapping);
        glTexture.setSampler(defaultSampler);
        glObjects.add(glTexture);
        return glTexture;
    }

    @Override
    public TextureCubeMap createTextureCubeMap(int width, int height, FormatType format, boolean mipmapping) {
        GLTextureCubeMap glTexture = new GLTextureCubeMap(width, height, format, mipmapping);
        glTexture.setSampler(defaultSampler);
        glObjects.add(glTexture);
        return glTexture;
    }

    @Override
    public TextureMS createTextureMS(int width, int height, int samples) {
        GLTextureMS glTexture = new GLTextureMS(width, height, samples);
        glObjects.add(glTexture);
        return glTexture;
    }

    @Override
    public TextureMS createTextureMS(int width, int height, int samples, FormatType format) {
        GLTextureMS glTexture = new GLTextureMS(width, height, samples, format);
        glObjects.add(glTexture);
        return glTexture;
    }

    @Override
    public Sampler createSampler() {
        GLSampler glSampler = new GLSampler();
        glObjects.add(glSampler);
        return glSampler;
    }

    @Override
    public RenderTarget2D createRenderTarget2D() {
        GLRenderTarget2D glRenderTarget = new GLRenderTarget2D(window.getWidth(), window.getHeight());
        glRenderTarget.setSizableParent(window);
        glRenderTarget.setDepthTexture(createTexture2D(glRenderTarget.getWidth(), glRenderTarget.getHeight(), FormatType.DEPTH_24_STENCIL_8));
        glRenderTarget.addColorTexture(0, createTexture2D(glRenderTarget.getWidth(), glRenderTarget.getHeight()));
        glObjects.add(glRenderTarget);
        return glRenderTarget;
    }

    @Override
    public RenderTarget2D createRenderTarget2D(int width, int height) {
        GLRenderTarget2D glRenderTarget = new GLRenderTarget2D(width, height);
        glRenderTarget.setDepthTexture(createTexture2D(width, height, FormatType.DEPTH_24_STENCIL_8));
        glRenderTarget.addColorTexture(0, createTexture2D(width, height));
        glObjects.add(glRenderTarget);
        return glRenderTarget;
    }

    @Override
    public RenderTargetMS createRenderTargetMS(int samples) {
        GLRenderTargetMS glRenderTarget = new GLRenderTargetMS(window.getWidth(), window.getHeight(), samples);
        glRenderTarget.setSizableParent(window);
        glRenderTarget.setDepthTexture(createTextureMS(glRenderTarget.getWidth(), glRenderTarget.getHeight(), samples, FormatType.DEPTH_24_STENCIL_8));
        glRenderTarget.addColorTexture(0, createTextureMS(glRenderTarget.getWidth(), glRenderTarget.getHeight(), samples));
        glObjects.add(glRenderTarget);
        return glRenderTarget;
    }

    @Override
    public RenderTargetMS createRenderTargetMS(int width, int height, int samples) {
        GLRenderTargetMS glRenderTarget = new GLRenderTargetMS(width, height, samples);
        glRenderTarget.setDepthTexture(createTextureMS(width, height, samples, FormatType.DEPTH_24_STENCIL_8));
        glRenderTarget.addColorTexture(0, createTextureMS(width, height, samples));
        glObjects.add(glRenderTarget);
        return glRenderTarget;
    }
    
    @Override
    public VertexArray createVertexArray() {
        GLVertexArray glVertexArray = new GLVertexArray();
        glObjects.add(glVertexArray);
        return glVertexArray;
    }

    @Override
    public Rasterizer getDefaultRasterizer() {
        return defaultRasterizer;
    }

    @Override
    public Rasterizer getDefaultFXRasterizer() {
        return defaultFXRasterizer;
    }

    @Override
    public Sampler getDefaultSampler() {
        return defaultSampler;
    }

    @Override
    public Blender getDefaultBlender() {
        return defaultBlender;
    }

    @Override
    public TextureCubeMap getDefaultTextureCubeMap() {
        return defaultCubeMap;
    }
}
