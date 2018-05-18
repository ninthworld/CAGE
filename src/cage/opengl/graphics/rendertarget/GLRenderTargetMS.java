package cage.opengl.graphics.rendertarget;

import cage.core.graphics.rendertarget.RenderTargetMS;
import cage.core.graphics.texture.TextureMS;
import cage.core.graphics.type.FormatType;
import cage.opengl.graphics.texture.GLTextureMS;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;
import java.util.Map;

import static cage.opengl.utils.GLUtils.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.*;

public class GLRenderTargetMS extends RenderTargetMS implements GLRenderTarget {

    private int framebufferId;

    public GLRenderTargetMS(int width, int height, int samples) {
        super(width, height, samples);        

        int[] framebuffers = new int[1];
        glGenFramebuffers(framebuffers);
        this.framebufferId = framebuffers[0];
    }

    @Override
    public void destroy() {
        unbind();
        if(framebufferId > 0) {
            glDeleteFramebuffers(new int[]{ framebufferId });
        }
    }

    @Override
    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, framebufferId);
    }

    @Override
    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @Override
    public void addColorTexture(int index, TextureMS colorTexture) {
    	if(colorTexture instanceof GLTextureMS) {
	        super.addColorTexture(index, colorTexture);

            GLTextureMS glTexture = (GLTextureMS)colorTexture;
	        bind();
	        glFramebufferTexture2D(
	                GL_FRAMEBUFFER,
	                GL_COLOR_ATTACHMENT0 + index,
	                GL_TEXTURE_2D_MULTISAMPLE,
	                glTexture.getTextureId(), 0);
	        checkError("glFramebufferTexture2D");
	
	        IntBuffer colorAttachments = BufferUtils.createIntBuffer(getColorTextureCount());
	        getColorTextureIterator().forEachRemaining((Map.Entry<Integer, TextureMS> entry) -> colorAttachments.put(GL_COLOR_ATTACHMENT0 + entry.getKey()));
	        colorAttachments.rewind();
	        glDrawBuffers(colorAttachments);
	        checkFramebufferStatus();
	        unbind();
    	}
    }

    @Override
    public void setDepthTexture(TextureMS depthTexture) {
    	if(depthTexture instanceof GLTextureMS) {
            super.setDepthTexture(depthTexture);

            GLTextureMS glTexture = (GLTextureMS)depthTexture;
            bind();
            glFramebufferTexture2D(
                    GL_FRAMEBUFFER,
                    GL_DEPTH_ATTACHMENT,
                    GL_TEXTURE_2D_MULTISAMPLE,
                    glTexture.getTextureId(), 0);
            checkError("glFramebufferTexture2D");
            if (depthTexture.getFormat() == FormatType.DEPTH_24_STENCIL_8) {
                glFramebufferTexture2D(
                        GL_FRAMEBUFFER,
                        GL_STENCIL_ATTACHMENT,
                        GL_TEXTURE_2D_MULTISAMPLE,
                        glTexture.getTextureId(), 0);
                checkError("glFramebufferTexture2D");
            }
            checkFramebufferStatus();
            unbind();    		
    	}
    }

    @Override
    public int getFramebufferId() {
        return framebufferId;
    }
}
