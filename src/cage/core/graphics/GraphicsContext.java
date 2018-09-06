package cage.core.graphics;

import cage.core.graphics.blender.Blender;
import cage.core.graphics.buffer.IndexBuffer;
import cage.core.graphics.rasterizer.Rasterizer;
import cage.core.graphics.rendertarget.RenderTarget;
import cage.core.graphics.shader.Shader;
import cage.core.graphics.type.PrimitiveType;
import cage.core.graphics.vertexarray.VertexArray;

import java.awt.*;
import java.nio.ByteBuffer;

public interface GraphicsContext {

	void draw(int vertexCount);
	void draw(int vertexCount, int startIndex);
	void drawIndexed(IndexBuffer indexBuffer);
	void drawIndexed(IndexBuffer indexBuffer, int indexCount, int startIndex);

	void drawInstanced(int instances, int vertexCount);
	void drawInstanced(int instances, int vertexCount, int startIndex);
	void drawIndexedInstanced(int instances, IndexBuffer indexBuffer);
	void drawIndexedInstanced(int instances, IndexBuffer indexBuffer, int indexCount, int startIndex);

	void computeDispatch(int numGroupsX, int numGroupsY, int numGroupsZ);
	void computeMemoryBarrier();

    void swapBuffers();

    void bindBackBuffer();
    void clear();

    void resolveToBackBuffer(RenderTarget renderTarget);
    void resolveToBackBuffer(RenderTarget renderTarget, Rectangle clipTo);
    void resolveToBackBuffer(RenderTarget renderTarget, Rectangle clipFrom, Rectangle clipTo);
    void resolveToRenderTarget(RenderTarget renderTargetFrom, RenderTarget renderTargetTo);
    void resolveToRenderTarget(RenderTarget renderTargetFrom, RenderTarget renderTargetTo, Rectangle clipFrom, Rectangle clipTo);
    
    void bindRasterizer(Rasterizer rasterizer);
    void bindBlender(Blender blender);
    void bindRenderTarget(RenderTarget renderTarget);
    void bindShader(Shader shader);
    void bindVertexArray(VertexArray vertexArray);
    
    void unbindBlender(Blender blender);
    void unbindShader(Shader shader);
    void unbindVertexArray(VertexArray vertexArray);
    
    void setClearColor(Color color);
    void setViewport(Rectangle viewport);
    void setPrimitive(PrimitiveType primitive);
    void setPrimitive(PrimitiveType primitive, int patchSize);

    ByteBuffer getPixels(Rectangle bounds);
}
