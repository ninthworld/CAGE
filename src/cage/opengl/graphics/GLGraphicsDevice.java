package cage.opengl.graphics;

import cage.core.graphics.*;
import cage.core.graphics.type.FormatType;
import cage.opengl.application.GLGameWindow;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class GLGraphicsDevice implements IGraphicsDevice {

    private GLGraphicsContext graphicsContext;
    private List<IGLObject> glObjects;

    private GLRasterizer defaultRasterizer;
    private GLSampler defaultSampler;
    private GLBlender defaultBlender;

    public GLGraphicsDevice(GLGameWindow window) {
        GLFWErrorCallback.createPrint(System.err);
        if(!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        window.initialize();
        GL.createCapabilities();

        this.glObjects = new ArrayList<>();

        this.defaultRasterizer = (GLRasterizer)createRasterizer();
        this.defaultSampler = (GLSampler)createSampler();
        this.defaultBlender = (GLBlender)createBlender();

        this.graphicsContext = new GLGraphicsContext(window);
        this.graphicsContext.bindRasterizer(this.defaultRasterizer);
    }

    public void destroy() {
        glObjects.forEach(IGLObject::destroy);
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
    public RenderTarget2D createRenderTarget2D(int width, int height) {
        GLRenderTarget2D glRenderTarget = new GLRenderTarget2D(width, height);
        glRenderTarget.attachDepthTexture(createTexture2D(width, height, FormatType.DEPTH_24_STENCIL_8));
        glRenderTarget.attachColorTexture(0, createTexture2D(width, height));
        glObjects.add(glRenderTarget);
        return glRenderTarget;
    }

    @Override
    public RenderTargetMS createRenderTargetMS(int width, int height, int samples) {
        GLRenderTargetMS glRenderTarget = new GLRenderTargetMS(width, height, samples);
        glRenderTarget.attachDepthTexture(createTextureMS(width, height, samples, FormatType.DEPTH_24_STENCIL_8));
        glRenderTarget.attachColorTexture(0, createTextureMS(width, height, samples));
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
    public Sampler getDefaultSampler() {
        return defaultSampler;
    }

    @Override
    public Blender getDefaultBlender() {
        return defaultBlender;
    }
}
