package cage.core.graphics;

import cage.core.common.Destroyable;
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

public interface GraphicsDevice extends Destroyable {

    GraphicsContext getGraphicsContext();

    IndexBuffer createIndexBuffer();
    VertexBuffer createVertexBuffer();
    UniformBuffer createUniformBuffer();
    ShaderStorageBuffer createShaderStorageBuffer();
    Shader createShader();
    Rasterizer createRasterizer();
    Blender createBlender();

    Texture2D createTexture2D(int width, int height);
    Texture2D createTexture2D(int width, int height, FormatType format);
    Texture2D createTexture2D(int width, int height, boolean mipmapping);
    Texture2D createTexture2D(int width, int height, FormatType format, boolean mipmapping);

    TextureCubeMap createTextureCubeMap(int width, int height);
    TextureCubeMap createTextureCubeMap(int width, int height, FormatType format);
    TextureCubeMap createTextureCubeMap(int width, int height, boolean mipmapping);
    TextureCubeMap createTextureCubeMap(int width, int height, FormatType format, boolean mipmapping);

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
    TextureCubeMap getDefaultTextureCubeMap(); // Necessary hack for lighting shader
}
