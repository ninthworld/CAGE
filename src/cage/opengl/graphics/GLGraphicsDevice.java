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

    private GLGraphicsContext m_graphicsContext;
    private List<IGLObject> m_glObjects;

    private GLRasterizer m_defaultRasterizer;
    private GLSampler m_defaultSampler;
    private GLBlender m_defaultBlender;

    public GLGraphicsDevice(GLGameWindow window) {
        GLFWErrorCallback.createPrint(System.err);
        if(!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        window.initialize();
        GL.createCapabilities();

        m_glObjects = new ArrayList<>();

        m_defaultRasterizer = (GLRasterizer)createRasterizer();
        m_defaultSampler = (GLSampler)createSampler();
        m_defaultBlender = (GLBlender)createBlender();

        m_graphicsContext = new GLGraphicsContext(window);
        m_graphicsContext.bindRasterizer(m_defaultRasterizer);
    }

    public void destroy() {
        m_glObjects.forEach(IGLObject::destroy);
        glfwTerminate();
    }

    @Override
    public GLGraphicsContext getGraphicsContext() {
        return m_graphicsContext;
    }

    @Override
    public IndexBuffer createIndexBuffer() {
        GLIndexBuffer glIndexBuffer = new GLIndexBuffer();
        m_glObjects.add(glIndexBuffer);
        return glIndexBuffer;
    }

    @Override
    public VertexBuffer createVertexBuffer() {
        GLVertexBuffer glVertexBuffer = new GLVertexBuffer();
        m_glObjects.add(glVertexBuffer);
        return glVertexBuffer;
    }

    @Override
    public UniformBuffer createUniformBuffer() {
        GLUniformBuffer glUniformBuffer = new GLUniformBuffer();
        m_glObjects.add(glUniformBuffer);
        return glUniformBuffer;
    }

    @Override
    public Shader createShader() {
        GLShader glShader = new GLShader();
        m_glObjects.add(glShader);
        return glShader;
    }

    @Override
    public Rasterizer createRasterizer() {
        GLRasterizer glRasterizer = new GLRasterizer();
        m_glObjects.add(glRasterizer);
        return glRasterizer;
    }

    @Override
    public Blender createBlender() {
        GLBlender glBlender = new GLBlender();
        m_glObjects.add(glBlender);
        return glBlender;
    }

    @Override
    public Texture createTexture(int width, int height) {
        GLTexture glTexture = new GLTexture(width, height);
        glTexture.setSampler(m_defaultSampler);
        m_glObjects.add(glTexture);
        return glTexture;
    }

    @Override
    public Texture createTexture(int width, int height, FormatType format) {
        GLTexture glTexture = new GLTexture(width, height, format);
        glTexture.setSampler(m_defaultSampler);
        m_glObjects.add(glTexture);
        return glTexture;
    }

    @Override
    public Texture createTexture(int width, int height, boolean mipmapping) {
        GLTexture glTexture = new GLTexture(width, height, mipmapping);
        glTexture.setSampler(m_defaultSampler);
        m_glObjects.add(glTexture);
        return glTexture;
    }

    @Override
    public Texture createTexture(int width, int height, FormatType format, boolean mipmapping) {
        GLTexture glTexture = new GLTexture(width, height, format, mipmapping);
        glTexture.setSampler(m_defaultSampler);
        m_glObjects.add(glTexture);
        return glTexture;
    }

    @Override
    public TextureMS createTextureMS(int width, int height, int samples) {
        GLTextureMS glTexture = new GLTextureMS(width, height, samples);
        m_glObjects.add(glTexture);
        return glTexture;
    }

    @Override
    public TextureMS createTextureMS(int width, int height, int samples, FormatType format) {
        GLTextureMS glTexture = new GLTextureMS(width, height, samples, format);
        m_glObjects.add(glTexture);
        return glTexture;
    }

    @Override
    public Sampler createSampler() {
        GLSampler glSampler = new GLSampler();
        m_glObjects.add(glSampler);
        return glSampler;
    }

    @Override
    public RenderTarget createRenderTarget(int width, int height) {
        GLRenderTarget glRenderTarget = new GLRenderTarget(width, height);
        glRenderTarget.attachDepthTexture((GLTexture)createTexture(width, height, FormatType.DEPTH_24_STENCIL_8));
        glRenderTarget.attachColorTexture(0, (GLTexture)createTexture(width, height));
        m_glObjects.add(glRenderTarget);
        return glRenderTarget;
    }

    @Override
    public RenderTargetMS createRenderTargetMS(int width, int height, int samples) {
        GLRenderTargetMS glRenderTarget = new GLRenderTargetMS(width, height, samples);
        glRenderTarget.attachDepthTexture(createTextureMS(width, height, samples, FormatType.DEPTH_24_STENCIL_8));
        glRenderTarget.attachColorTexture(0, createTextureMS(width, height, samples));
        m_glObjects.add(glRenderTarget);
        return glRenderTarget;
    }
    
    @Override
    public VertexArray createVertexArray() {
        GLVertexArray glVertexArray = new GLVertexArray();
        m_glObjects.add(glVertexArray);
        return glVertexArray;
    }

    @Override
    public Rasterizer getDefaultRasterizer() {
        return m_defaultRasterizer;
    }

    @Override
    public Sampler getDefaultSampler() {
        return m_defaultSampler;
    }

    @Override
    public Blender getDefaultBlender() {
        return m_defaultBlender;
    }
}
