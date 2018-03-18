package cage.core.graphics;

import cage.core.common.IDestroyable;
import cage.core.graphics.blender.Blender;
import cage.core.graphics.buffer.IndexBuffer;
import cage.core.graphics.buffer.UniformBuffer;
import cage.core.graphics.buffer.VertexBuffer;
import cage.core.graphics.rasterizer.Rasterizer;
import cage.core.graphics.rendertarget.RenderTarget2D;
import cage.core.graphics.rendertarget.RenderTargetMS;
import cage.core.graphics.sampler.Sampler;
import cage.core.graphics.shader.Shader;
import cage.core.graphics.texture.Texture2D;
import cage.core.graphics.texture.TextureMS;
import cage.core.graphics.type.FormatType;
import cage.core.graphics.vertexarray.VertexArray;

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

    RenderTarget2D createRenderTarget2D();
    RenderTarget2D createRenderTarget2D(int width, int height);

    RenderTargetMS createRenderTargetMS(int samples);
    RenderTargetMS createRenderTargetMS(int width, int height, int samples);

    VertexArray createVertexArray();

    Rasterizer getDefaultRasterizer();
    Rasterizer getDefaultFXRasterizer();
    Sampler getDefaultSampler();
    Blender getDefaultBlender();

    void destroy(IDestroyable object);
}
