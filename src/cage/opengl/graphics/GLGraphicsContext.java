package cage.opengl.graphics;

import cage.core.graphics.*;
import cage.core.graphics.type.PrimitiveType;
import cage.opengl.application.GLGameWindow;

import java.awt.*;

import static cage.opengl.utils.GLUtils.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL40.*;

public class GLGraphicsContext implements IGraphicsContext {

    private GLGameWindow m_window;
    private int m_primitive;
    private int m_patchSize;
    private int m_boundFBOId;
    private int m_boundShaderId;

    public GLGraphicsContext(GLGameWindow window) {
        m_window = window;
        m_primitive = GL_TRIANGLES;
        m_patchSize = 1;
        m_boundFBOId = 0;
        m_boundShaderId = 0;
        setClearColor(new Color(0, 0, 0, 255));
    }

	@Override
	public void draw(int vertexCount) {
		draw(vertexCount, 0);
	}

	@Override
	public void draw(int vertexCount, int startIndex) {
		if(m_primitive == GL_PATCHES) {
			glPatchParameteri(GL_PATCH_VERTICES, m_patchSize);
		}
		
		glDrawArrays(m_primitive, startIndex, vertexCount);
		checkError("glDrawArrays");
	}

	@Override
	public void drawIndexed(IndexBuffer indexBuffer) {
		drawIndexed(indexBuffer, indexBuffer.getUnitCount(), 0);
	}

	@Override
	public void drawIndexed(IndexBuffer indexBuffer, int indexCount, int startIndex) {
		if(m_primitive == GL_PATCHES) {
			glPatchParameteri(GL_PATCH_VERTICES, m_patchSize);
		}
		
		if(indexBuffer instanceof GLIndexBuffer) {
			GLIndexBuffer glBuffer = (GLIndexBuffer)indexBuffer;
			glBuffer.bind();
			glDrawElements(
					m_primitive, indexCount,
                    GL_UNSIGNED_INT, // FIX ME
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
		if(m_primitive == GL_PATCHES) {
			glPatchParameteri(GL_PATCH_VERTICES, m_patchSize);
		}
		
		glDrawArraysInstanced(m_primitive, startIndex, vertexCount, instances);
		checkError("glDrawArraysInstanced");
	}

	@Override
	public void drawIndexedInstanced(int instances, IndexBuffer indexBuffer) {
		drawIndexedInstanced(instances, indexBuffer, indexBuffer.getUnitCount(), 0);
	}

	@Override
	public void drawIndexedInstanced(int instances, IndexBuffer indexBuffer, int indexCount, int startIndex) {
		if(m_primitive == GL_PATCHES) {
			glPatchParameteri(GL_PATCH_VERTICES, m_patchSize);
		}
		
		if(indexBuffer instanceof GLIndexBuffer) {
			GLIndexBuffer glBuffer = (GLIndexBuffer)indexBuffer;
			glBuffer.bind();
			glDrawElementsInstanced(
					m_primitive, indexCount,
                    GL_UNSIGNED_INT, // FIX ME
					startIndex, instances);
			checkError("glDrawElements");
			glBuffer.unbind();
		}
	}
	
    @Override
    public void swapBuffers() {
        glfwSwapBuffers(m_window.getHandle());
    }

    @Override
    public void bindBackBuffer() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        setViewport(new Rectangle(0, 0, m_window.getWidth(), m_window.getHeight()));
        m_boundFBOId = 0;
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
                new Rectangle(0, 0, m_window.getWidth(), m_window.getHeight()));
    }

	@Override
	public void resolveToBackBuffer(RenderTarget renderTarget, Rectangle clipFrom, Rectangle clipTo) {
		if(renderTarget instanceof IGLRenderTarget) {
            glBindFramebuffer(GL_READ_FRAMEBUFFER, ((IGLRenderTarget)renderTarget).getFramebufferId());            
	        glReadBuffer(GL_COLOR_ATTACHMENT0);
	        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
	        glDrawBuffer(GL_BACK);
	
	        glBlitFramebuffer(
	                clipFrom.x, clipFrom.y, clipFrom.x + clipFrom.width, clipFrom.y + clipFrom.height,
	                clipTo.x, clipTo.y, clipTo.x + clipTo.width, clipTo.y + clipTo.height,
	                GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT,
	                GL_NEAREST);
	        checkError("glBlitFramebuffer");
	
	        glBindFramebuffer(GL_FRAMEBUFFER, m_boundFBOId);
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
		if(renderTargetFrom instanceof IGLRenderTarget && renderTargetTo instanceof IGLRenderTarget) {

	        ((RenderTarget<Texture>)renderTargetFrom).forEachColorTexture((Integer i, Texture texture) -> {
	        	glBindFramebuffer(GL_READ_FRAMEBUFFER, ((IGLRenderTarget)renderTargetFrom).getFramebufferId());
	            glReadBuffer(GL_COLOR_ATTACHMENT0 + i);
	            glBindFramebuffer(GL_DRAW_FRAMEBUFFER, ((IGLRenderTarget)renderTargetTo).getFramebufferId());
	            glDrawBuffer(GL_COLOR_ATTACHMENT0 + i);

	            glBlitFramebuffer(
	                    clipFrom.x, clipFrom.y, clipFrom.x + clipFrom.width, clipFrom.y + clipFrom.height,
	                    clipTo.x, clipTo.y, clipTo.x + clipTo.width, clipTo.y + clipTo.height,
	                    GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT,
	                    GL_NEAREST);
	            checkError("glBlitFramebuffer");
	        });

	        glBindFramebuffer(GL_FRAMEBUFFER, m_boundFBOId);
		}
	}

	@Override
	public void bindRasterizer(Rasterizer rasterizer) {
		if(rasterizer instanceof GLRasterizer) {
            ((GLRasterizer) rasterizer).bind();
		}
	}

	@Override
	public void bindBlender(Blender blender) {
        if(blender instanceof GLBlender) {
            ((GLBlender) blender).bind();
        }
	}

	@Override
	public void bindRenderTarget(RenderTarget renderTarget) {
        if(renderTarget instanceof IGLRenderTarget) {
            ((IGLRenderTarget) renderTarget).bind();
            setViewport(new Rectangle(0, 0, renderTarget.getWidth(), renderTarget.getHeight()));
            m_boundFBOId = ((IGLRenderTarget) renderTarget).getFramebufferId();
        }
	}

	@Override
	public void bindShader(Shader shader) {
		if(shader instanceof GLShader) {
			GLShader glShader = (GLShader)shader;
			if(glShader.getProgramId() != m_boundShaderId) {
                glShader.bind();
                m_boundShaderId = glShader.getProgramId();
            }
            glShader.bindTextures();
		}
	}

	@Override
	public void bindVertexArray(VertexArray vertexArray) {
		if(vertexArray instanceof GLVertexArray) {
			GLVertexArray glVertexArray = (GLVertexArray)vertexArray;
			glVertexArray.bind();
		}
	}

	@Override
	public void unbindBlender(Blender blender) {
        if(blender instanceof GLBlender) {
            ((GLBlender) blender).unbind();
        }
	}

	@Override
	public void unbindShader(Shader shader) {
		if(shader instanceof GLShader) {
			GLShader glShader = (GLShader)shader;
			glShader.unbind();
            m_boundShaderId = 0;
		}
	}

	@Override
	public void unbindVertexArray(VertexArray vertexArray) {
		if(vertexArray instanceof GLVertexArray) {
			GLVertexArray glVertexArray = (GLVertexArray)vertexArray;
			glVertexArray.unbind();
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
            case POINTS: m_primitive = GL_POINTS; break;
            case LINES: m_primitive = GL_LINES; break;
            case LINE_STRIP: m_primitive = GL_LINE_STRIP; break;
            case TRIANGLES: m_primitive = GL_TRIANGLES; break;
            case TRIANGLE_STRIP: m_primitive = GL_TRIANGLE_STRIP; break;
            case PATCHES: m_primitive = GL_PATCHES; break;
        }
    }
    
    @Override
    public void setPrimitive(PrimitiveType primitive, int patchSize) {
    	m_patchSize = patchSize;
    	setPrimitive(primitive);
    }
}
