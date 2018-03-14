package cage.opengl.graphics;

import cage.core.graphics.RenderTarget;
import cage.core.graphics.RenderTarget2D;
import cage.core.graphics.Texture;
import cage.core.graphics.Texture2D;
import cage.core.graphics.type.FormatType;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;
import java.util.Map;

import static cage.opengl.utils.GLUtils.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class GLRenderTarget2D extends RenderTarget2D implements IGLRenderTarget {

    private int framebufferId;

    public GLRenderTarget2D(int width, int height) {
        super(width, height);

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
    public void attachColorTexture(int index, Texture2D colorTexture) {
        if(colorTexture instanceof GLTexture2D) {
            super.attachColorTexture(index, colorTexture);

            GLTexture2D glTexture = (GLTexture2D)colorTexture;
            bind();
            glFramebufferTexture2D(
                    GL_FRAMEBUFFER,
                    GL_COLOR_ATTACHMENT0 + index,
                    GL_TEXTURE_2D,
                    glTexture.getTextureId(), 0);
            checkError("glFramebufferTexture2D");

            IntBuffer colorAttachments = BufferUtils.createIntBuffer(getColorTextureCount());
            getColorTextureIterator().forEachRemaining((Map.Entry<Integer, Texture2D> entry) -> colorAttachments.put(GL_COLOR_ATTACHMENT0 + entry.getKey()));
            colorAttachments.rewind();
            glDrawBuffers(colorAttachments);
            checkFramebufferStatus();
            unbind();
        }
    }

    @Override
    public void attachDepthTexture(Texture2D depthTexture) {
        if(depthTexture instanceof GLTexture2D) {
            super.attachDepthTexture(depthTexture);

            GLTexture2D glTexture = (GLTexture2D)depthTexture;
            bind();
            glFramebufferTexture2D(
                    GL_FRAMEBUFFER,
                    GL_DEPTH_ATTACHMENT,
                    GL_TEXTURE_2D,
                    glTexture.getTextureId(), 0);
            checkError("glFramebufferTexture2D");
            if (depthTexture.getFormat() == FormatType.DEPTH_24_STENCIL_8) {
                glFramebufferTexture2D(
                        GL_FRAMEBUFFER,
                        GL_STENCIL_ATTACHMENT,
                        GL_TEXTURE_2D,
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
