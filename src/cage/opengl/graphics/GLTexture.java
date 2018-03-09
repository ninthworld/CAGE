package cage.opengl.graphics;

import cage.core.graphics.Texture;
import cage.core.graphics.type.FormatType;

import java.nio.*;

import static cage.opengl.graphics.type.GLTypeUtils.*;
import static cage.opengl.utils.GLUtils.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL30.*;

public class GLTexture extends Texture implements IGLTexture {

    private int m_textureId;
    private Buffer m_data;

    public GLTexture(int width, int height, FormatType format, boolean mipmapping) {
        super(width, height, format, mipmapping);
        m_data = null;
        generate();
    }

    public GLTexture(int width, int height, boolean mipmapping) {
        super(width, height, mipmapping);
        m_data = null;
        generate();
    }

    public GLTexture(int width, int height, FormatType format) {
        super(width, height, format);
        m_data = null;
        generate();
    }

    public GLTexture(int width, int height) {
        super(width, height);
        m_data = null;
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
        glBindTexture(GL_TEXTURE_2D, m_textureId);
    }

    @Override
    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void initialize() {
        bind();
        glTexImage2D(
                GL_TEXTURE_2D,
                0,
                getGLInternalFormatType(m_format),
                m_width, m_height,
                0,
                getGLFormatType(m_format),
                getGLDataType(m_format),
                0);
        checkError("glTexImage2D");

        if(m_mipmapping) {
            glGenerateMipmap(GL_TEXTURE_2D);
            checkError("glGenerateMipmap");
        }
        else {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL, 0);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, 0);
        }
        unbind();

        if(m_data != null) {
            if(m_data instanceof ByteBuffer) {
                setData((ByteBuffer)m_data);
            }
            else if(m_data instanceof ShortBuffer) {
                setData((ShortBuffer)m_data);
            }
            else if(m_data instanceof IntBuffer) {
                setData((IntBuffer)m_data);
            }
            else if(m_data instanceof FloatBuffer) {
                setData((FloatBuffer)m_data);
            }
            else if(m_data instanceof DoubleBuffer) {
                setData((DoubleBuffer)m_data);
            }
        }
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
    public void setMipmapping(boolean mipmapping) {
        super.setMipmapping(mipmapping);
        initialize();
    }
    
    @Override
    public void setData(ByteBuffer data) {
        m_data = data;
        bind();
        glTexSubImage2D(
                GL_TEXTURE_2D,
                0,
                0,0,
                m_width, m_height,
                getGLFormatType(m_format),
                getGLDataType(m_format),
                data);
        checkError("glTexSubImage2D");
        unbind();
    }

    @Override
    public void setData(ShortBuffer data) {
        m_data = data;
        bind();
        glTexSubImage2D(
                GL_TEXTURE_2D,
                0,
                0,0,
                m_width, m_height,
                getGLFormatType(m_format),
                getGLDataType(m_format),
                data);
        checkError("glTexSubImage2D");
        unbind();
    }

    @Override
    public void setData(IntBuffer data) {
        m_data = data;
        bind();
        glTexSubImage2D(
                GL_TEXTURE_2D,
                0,
                0,0,
                m_width, m_height,
                getGLFormatType(m_format),
                getGLDataType(m_format),
                data);
        checkError("glTexSubImage2D");
        unbind();
    }

    @Override
    public void setData(FloatBuffer data) {
        m_data = data;
        bind();
        glTexSubImage2D(
                GL_TEXTURE_2D,
                0,
                0,0,
                m_width, m_height,
                getGLFormatType(m_format),
                getGLDataType(m_format),
                data);
        checkError("glTexSubImage2D");
        unbind();
    }

    @Override
    public void setData(DoubleBuffer data) {
        m_data = data;
        bind();
        glTexSubImage2D(
                GL_TEXTURE_2D,
                0,
                0,0,
                m_width, m_height,
                getGLFormatType(m_format),
                getGLDataType(m_format),
                data);
        checkError("glTexSubImage2D");
        unbind();
    }

    @Override
    public int getTextureId() {
        return m_textureId;
    }
}
