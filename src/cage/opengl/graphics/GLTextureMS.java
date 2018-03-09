package cage.opengl.graphics;

import cage.core.graphics.TextureMS;
import cage.core.graphics.type.FormatType;

import java.nio.*;

import static cage.opengl.graphics.type.GLTypeUtils.*;
import static cage.opengl.utils.GLUtils.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL32.*;

public class GLTextureMS extends TextureMS implements IGLTexture {

    private int m_textureId;

    public GLTextureMS(int width, int height, int samples, FormatType format) {
        super(width, height, samples, format);
        generate();
    }

    public GLTextureMS(int width, int height, int samples) {
        super(width, height, samples);
        generate();
    }

    private void generate() {
        int[] textures = new int[1];
        glGenTextures(textures);
        m_textureId = textures[0];
        initialize();
    }

    @Override
    public void destroy() {
        if(m_textureId > 0) {
            glDeleteTextures(new int[]{ m_textureId });
        }
    }

    @Override
    public void bind() {
        glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, m_textureId);
    }

    @Override
    public void unbind() {
        glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, 0);
    }

    public void initialize() {
        bind();
        glTexImage2DMultisample(
                GL_TEXTURE_2D_MULTISAMPLE,
                m_samples,
                getGLInternalFormatType(m_format),
                m_width, m_height, false);
        checkError("glTexImage2DMultisample");
        unbind();
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        initialize();
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        initialize();
    }

    @Override
    public void setFormat(FormatType format) {
        super.setFormat(format);
        initialize();
    }

    @Override
    public void setMultisampleCount(int samples) {
        super.setMultisampleCount(samples);
        initialize();
    }

    @Override
    public void setData(ByteBuffer data) {
    }

    @Override
    public void setData(ShortBuffer data) {
    }

    @Override
    public void setData(IntBuffer data) {
    }

    @Override
    public void setData(FloatBuffer data) {
    }

    @Override
    public void setData(DoubleBuffer data) {
    }

    @Override
    public int getTextureId() {
        return m_textureId;
    }
}
