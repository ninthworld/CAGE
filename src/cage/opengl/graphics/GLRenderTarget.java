package cage.opengl.graphics;

import cage.core.graphics.RenderTarget;
import cage.core.graphics.Texture;
import cage.core.graphics.type.FormatType;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

import static cage.opengl.utils.GLUtils.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class GLRenderTarget extends RenderTarget<GLTexture> implements IGLRenderTarget {

    private int m_framebufferId;

    public GLRenderTarget(int width, int height) {
        super(width, height);

        int[] framebuffers = new int[1];
        glGenFramebuffers(framebuffers);
        m_framebufferId = framebuffers[0];
    }

    @Override
    public void destroy() {
        unbind();
        if(m_framebufferId > 0) {
            glDeleteFramebuffers(new int[]{ m_framebufferId });
        }
    }

    @Override
    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, m_framebufferId);
    }

    @Override
    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @Override
    public void attachColorTexture(int index, GLTexture colorTexture) {
        super.attachColorTexture(index, colorTexture);
        
        bind();
        glFramebufferTexture2D(
                GL_FRAMEBUFFER,
                GL_COLOR_ATTACHMENT0 + index,
                GL_TEXTURE_2D,
                colorTexture.getTextureId(), 0);
        checkError("glFramebufferTexture2D");

        IntBuffer colorAttachments = BufferUtils.createIntBuffer(m_colorTextures.size());
        m_colorTextures.forEach((Integer i, Texture t) -> colorAttachments.put(GL_COLOR_ATTACHMENT0 + i));
        colorAttachments.rewind();
        glDrawBuffers(colorAttachments);
        checkFramebufferStatus();
        unbind();
    }

    @Override
    public void attachDepthTexture(GLTexture depthTexture) {
            super.attachDepthTexture(depthTexture);

        bind();
        glFramebufferTexture2D(
                GL_FRAMEBUFFER,
                GL_DEPTH_ATTACHMENT,
                GL_TEXTURE_2D,
                depthTexture.getTextureId(), 0);
        checkError("glFramebufferTexture2D");
        if (m_depthTexture.getFormat() == FormatType.DEPTH_24_STENCIL_8) {
            glFramebufferTexture2D(
                    GL_FRAMEBUFFER,
                    GL_STENCIL_ATTACHMENT,
                    GL_TEXTURE_2D,
                    depthTexture.getTextureId(), 0);
            checkError("glFramebufferTexture2D");
        }
        checkFramebufferStatus();
        unbind();
    }

    @Override
    public int getFramebufferId() {
        return m_framebufferId;
    }
}
