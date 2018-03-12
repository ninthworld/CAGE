package cage.core.graphics;

import cage.core.graphics.type.FormatType;

public interface IGraphicsDevice {

    IGraphicsContext getGraphicsContext();

    IndexBuffer createIndexBuffer();
    VertexBuffer createVertexBuffer();
    UniformBuffer createUniformBuffer();
    Shader createShader();
    Rasterizer createRasterizer();
    Blender createBlender();

    Texture createTexture(int width, int height);
    Texture createTexture(int width, int height, FormatType format);
    Texture createTexture(int width, int height, boolean mipmapping);
    Texture createTexture(int width, int height, FormatType format, boolean mipmapping);

    Texture createTextureMS(int width, int height, int samples);
    Texture createTextureMS(int width, int height, int samples, FormatType format);

    Sampler createSampler();
    RenderTarget createRenderTarget(int width, int height);
    RenderTargetMS createRenderTargetMS(int width, int height, int samples);
    VertexArray createVertexArray();

    Rasterizer getDefaultRasterizer();
    Sampler getDefaultSampler();
    Blender getDefaultBlender();
}
