package cage.opengl.graphics;

import cage.core.graphics.*;
import cage.core.graphics.blender.Blender;
import cage.core.graphics.buffer.IndexBuffer;
import cage.core.graphics.rasterizer.Rasterizer;
import cage.core.graphics.rendertarget.RenderTarget;
import cage.core.graphics.shader.Shader;
import cage.core.graphics.texture.Texture;
import cage.core.graphics.type.LayoutType;
import cage.core.graphics.type.PrimitiveType;
import cage.core.graphics.vertexarray.VertexArray;
import cage.glfw.window.GLFWWindow;
import cage.opengl.graphics.blender.GLBlender;
import cage.opengl.graphics.buffer.GLIndexBuffer;
import cage.opengl.graphics.rasterizer.GLRasterizer;
import cage.opengl.graphics.rendertarget.GLRenderTarget;
import cage.opengl.graphics.shader.GLShader;
import cage.opengl.graphics.vertexarray.GLVertexArray;

import java.awt.*;
import java.util.Map;

import static cage.opengl.utils.GLUtils.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL40.*;

public class GLGraphicsContext implements GraphicsContext {

    private GLFWWindow window;
    private int primitive;
    private int patchSize;
	private Rasterizer boundRasterizer;
	private Blender boundBlender;
	private VertexArray boundVertexArray;
	private Shader boundShader;
	private RenderTarget boundRenderTarget;

    public GLGraphicsContext(GLFWWindow window) {
		this.window = window;
		this.primitive = GL_TRIANGLES;
		this.patchSize = 1;
		this.boundRasterizer = null;
		this.boundBlender = null;
        this.boundVertexArray = null;
		this.boundShader = null;
		this.boundRenderTarget = null;
        setClearColor(new Color(0, 0, 0, 0));
    }

	@Override
	public void draw(int vertexCount) {
		draw(vertexCount, 0);
	}

	@Override
	public void draw(int vertexCount, int startIndex) {
		if(primitive == GL_PATCHES) {
			glPatchParameteri(GL_PATCH_VERTICES, patchSize);
		}
		
		glDrawArrays(primitive, startIndex, vertexCount);
		checkError("glDrawArrays");
	}

	@Override
	public void drawIndexed(IndexBuffer indexBuffer) {
		drawIndexed(indexBuffer, indexBuffer.getUnitCount(), 0);
	}

	@Override
	public void drawIndexed(IndexBuffer indexBuffer, int indexCount, int startIndex) {
		if(primitive == GL_PATCHES) {
			glPatchParameteri(GL_PATCH_VERTICES, patchSize);
		}
		
		if(indexBuffer instanceof GLIndexBuffer) {
			GLIndexBuffer glBuffer = (GLIndexBuffer)indexBuffer;
			glBuffer.bind();
			glDrawElements(
					primitive, indexCount,
                    GL_UNSIGNED_INT,
					startIndex);
			checkError("glDrawElements");
			glBuffer.unbind();
		}
	}

	@Override
	public void drawInstanced(int instances, int vertexCount) {
		drawInstanced(instances, vertexCount, 0);		
	}

	@Override
	public void drawInstanced(int instances, int vertexCount, int startIndex) {
		if(primitive == GL_PATCHES) {
			glPatchParameteri(GL_PATCH_VERTICES, patchSize);
		}
		
		glDrawArraysInstanced(primitive, startIndex, vertexCount, instances);
		checkError("glDrawArraysInstanced");
	}

	@Override
	public void drawIndexedInstanced(int instances, IndexBuffer indexBuffer) {
		drawIndexedInstanced(instances, indexBuffer, indexBuffer.getUnitCount(), 0);
	}

	@Override
	public void drawIndexedInstanced(int instances, IndexBuffer indexBuffer, int indexCount, int startIndex) {
		if(primitive == GL_PATCHES) {
			glPatchParameteri(GL_PATCH_VERTICES, patchSize);
		}
		
		if(indexBuffer instanceof GLIndexBuffer) {
			GLIndexBuffer glBuffer = (GLIndexBuffer)indexBuffer;
			glBuffer.bind();
			glDrawElementsInstanced(
					primitive, indexCount,
                    GL_UNSIGNED_INT,
					startIndex, instances);
			checkError("glDrawElements");
			glBuffer.unbind();
		}
	}
	
    @Override
    public void swapBuffers() {
        glfwSwapBuffers(window.getHandle());
    }

    @Override
    public void bindBackBuffer() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        setViewport(new Rectangle(0, 0, window.getWidth(), window.getHeight()));
        boundRenderTarget = null;
    }

    @Override
    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    }

    @Override
    public void resolveToBackBuffer(RenderTarget renderTarget) {
        resolveToBackBuffer(
                renderTarget,
                new Rectangle(0, 0, renderTarget.getWidth(), renderTarget.getHeight()),
                new Rectangle(0, 0, window.getWidth(), window.getHeight()));
    }

	@Override
	public void resolveToBackBuffer(RenderTarget renderTarget, Rectangle clipTo) {
		resolveToBackBuffer(
				renderTarget,
				new Rectangle(0, 0, renderTarget.getWidth(), renderTarget.getHeight()),
				clipTo);
	}

	@Override
	public void resolveToBackBuffer(RenderTarget renderTarget, Rectangle clipFrom, Rectangle clipTo) {
		if(renderTarget instanceof GLRenderTarget) {
            glBindFramebuffer(GL_READ_FRAMEBUFFER, ((GLRenderTarget)renderTarget).getFramebufferId());
	        glReadBuffer(GL_COLOR_ATTACHMENT0);
	        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
	        glDrawBuffer(GL_BACK);
	
	        glBlitFramebuffer(
	                clipFrom.x, clipFrom.y, clipFrom.x + clipFrom.width, clipFrom.y + clipFrom.height,
	                clipTo.x, clipTo.y, clipTo.x + clipTo.width, clipTo.y + clipTo.height,
	                GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT,
	                GL_NEAREST);
	        checkError("glBlitFramebuffer");

	        bindRenderTarget(boundRenderTarget);
		}
	}

    @Override
    public void resolveToRenderTarget(RenderTarget renderTargetFrom, RenderTarget renderTargetTo) {
        resolveToRenderTarget(
                renderTargetFrom,
                renderTargetTo,
                new Rectangle(0, 0, renderTargetFrom.getWidth(), renderTargetFrom.getHeight()),
                new Rectangle(0, 0, renderTargetTo.getWidth(), renderTargetTo.getHeight()));
    }

	@Override
	public void resolveToRenderTarget(RenderTarget renderTargetFrom, RenderTarget renderTargetTo, Rectangle clipFrom, Rectangle clipTo) {
		if(renderTargetFrom instanceof GLRenderTarget && renderTargetTo instanceof GLRenderTarget) {
            ((RenderTarget<Texture>)renderTargetFrom).getColorTextureIterator().forEachRemaining((Map.Entry<Integer, Texture> entry) -> {
	        	glBindFramebuffer(GL_READ_FRAMEBUFFER, ((GLRenderTarget)renderTargetFrom).getFramebufferId());
	            glReadBuffer(GL_COLOR_ATTACHMENT0 + entry.getKey());
	            glBindFramebuffer(GL_DRAW_FRAMEBUFFER, ((GLRenderTarget)renderTargetTo).getFramebufferId());
	            glDrawBuffer(GL_COLOR_ATTACHMENT0 + entry.getKey());

	            glBlitFramebuffer(
	                    clipFrom.x, clipFrom.y, clipFrom.x + clipFrom.width, clipFrom.y + clipFrom.height,
	                    clipTo.x, clipTo.y, clipTo.x + clipTo.width, clipTo.y + clipTo.height,
	                    GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT,
	                    GL_NEAREST);
	            checkError("glBlitFramebuffer");
	        });

            bindRenderTarget(boundRenderTarget);
		}
	}

	@Override
	public void bindRasterizer(Rasterizer rasterizer) {
		if(rasterizer != boundRasterizer && rasterizer instanceof GLRasterizer) {
            ((GLRasterizer) rasterizer).bind();
            boundRasterizer = rasterizer;
		}
	}

	@Override
	public void bindBlender(Blender blender) {
        if(blender != boundBlender && blender instanceof GLBlender) {
            ((GLBlender) blender).bind();
            boundBlender = blender;
        }
	}

	@Override
	public void bindRenderTarget(RenderTarget renderTarget) {
        if(renderTarget != boundRenderTarget && renderTarget instanceof GLRenderTarget) {
            ((GLRenderTarget) renderTarget).bind();
            setViewport(new Rectangle(0, 0, renderTarget.getWidth(), renderTarget.getHeight()));
            boundRenderTarget = renderTarget;
        }
	}

	@Override
	public void bindShader(Shader shader) {
		if(shader instanceof GLShader) {
			GLShader glShader = (GLShader)shader;
			if(shader != boundShader) {
                glShader.bind();
                boundShader = shader;
            }
            glShader.bindTextures();
		}
	}

	@Override
	public void bindVertexArray(VertexArray vertexArray) {
		if(vertexArray != boundVertexArray && vertexArray instanceof GLVertexArray) {
			GLVertexArray glVertexArray = (GLVertexArray)vertexArray;
			glVertexArray.bind();
			boundVertexArray = vertexArray;
		}
	}

	@Override
	public void unbindBlender(Blender blender) {
        if(blender instanceof GLBlender) {
            ((GLBlender) blender).unbind();
            boundBlender = null;
        }
	}

	@Override
	public void unbindShader(Shader shader) {
		if(shader instanceof GLShader) {
			GLShader glShader = (GLShader)shader;
			glShader.unbind();
			boundShader = null;
		}
	}

	@Override
	public void unbindVertexArray(VertexArray vertexArray) {
		if(vertexArray instanceof GLVertexArray) {
			GLVertexArray glVertexArray = (GLVertexArray)vertexArray;
			glVertexArray.unbind();
			boundVertexArray = null;
		}
	}

    @Override
    public void setClearColor(Color color) {
        glClearColor(
                color.getRed() / 255.0f,
                color.getGreen() / 255.0f,
                color.getBlue() / 255.0f,
                color.getAlpha() / 255.0f);
    }

    @Override
    public void setViewport(Rectangle viewport) {
        glViewport(viewport.x, viewport.y, viewport.width, viewport.height);
    }

    @Override
    public void setPrimitive(PrimitiveType primitive) {
        switch(primitive) {
            case POINTS: this.primitive = GL_POINTS; break;
            case LINES: this.primitive = GL_LINES; break;
            case LINE_STRIP: this.primitive = GL_LINE_STRIP; break;
            case TRIANGLES: this.primitive = GL_TRIANGLES; break;
            case TRIANGLE_STRIP: this.primitive = GL_TRIANGLE_STRIP; break;
            case PATCHES: this.primitive = GL_PATCHES; break;
        }
    }
    
    @Override
    public void setPrimitive(PrimitiveType primitive, int patchSize) {
		this.patchSize = patchSize;
    	setPrimitive(primitive);
    }
}
