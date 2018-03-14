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

    Texture2D createTexture2D(int width, int height);
    Texture2D createTexture2D(int width, int height, FormatType format);
    Texture2D createTexture2D(int width, int height, boolean mipmapping);
    Texture2D createTexture2D(int width, int height, FormatType format, boolean mipmapping);

    /* TODO: Texture3D */

    TextureMS createTextureMS(int width, int height, int samples);
    TextureMS createTextureMS(int width, int height, int samples, FormatType format);

    Sampler createSampler();
    RenderTarget2D createRenderTarget2D(int width, int height);
    RenderTargetMS createRenderTargetMS(int width, int height, int samples);
    VertexArray createVertexArray();

    Rasterizer getDefaultRasterizer();
    Sampler getDefaultSampler();
    Blender getDefaultBlender();
}
