package cage.opengl.graphics.texture;

import cage.core.graphics.texture.Texture2D;
import cage.core.graphics.type.FormatType;

import java.nio.*;

import static cage.opengl.graphics.type.GLTypeUtils.*;
import static cage.opengl.utils.GLUtils.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL30.*;

public class GLTexture2D extends Texture2D implements GLTexture {

    private int textureId;

    public GLTexture2D(int width, int height, FormatType format, boolean mipmapping) {
        super(width, height, format, mipmapping);
        generate();
    }

    public GLTexture2D(int width, int height, boolean mipmapping) {
        super(width, height, mipmapping);
        generate();
    }

    public GLTexture2D(int width, int height, FormatType format) {
        super(width, height, format);
        generate();
    }

    public GLTexture2D(int width, int height) {
        super(width, height);
        generate();
    }

    private void generate() {
        int[] textures = new int[1];
        glGenTextures(textures);
        textureId = textures[0];
        initialize();
    }

    @Override
    public void destroy() {
        if(textureId > 0) {
            glDeleteTextures(new int[]{ textureId });
        }
    }

    @Override
    public void bind() {
        glBindTexture(GL_TEXTURE_2D, textureId);
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
                getGLInternalFormatType(getFormat()),
                getWidth(), getHeight(),
                0,
                getGLFormatType(getFormat()),
                getGLDataType(getFormat()),
                0);
        checkError("glTexImage2D");

        if(isMipmapping()) {
            glGenerateMipmap(GL_TEXTURE_2D);
            checkError("glGenerateMipmap");
        }
        else {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL, 0);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, 0);
        }
        unbind();
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
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
        bind();
        glTexSubImage2D(
                GL_TEXTURE_2D,
                0,
                0,0,
                getWidth(), getHeight(),
                getGLFormatType(getFormat()),
                getGLDataType(getFormat()),
                data);
        checkError("glTexSubImage2D");
        unbind();
    }

    @Override
    public void setData(ShortBuffer data) {
        bind();
        glTexSubImage2D(
                GL_TEXTURE_2D,
                0,
                0,0,
                getWidth(), getHeight(),
                getGLFormatType(getFormat()),
                getGLDataType(getFormat()),
                data);
        checkError("glTexSubImage2D");
        unbind();
    }

    @Override
    public void setData(IntBuffer data) {
        bind();
        glTexSubImage2D(
                GL_TEXTURE_2D,
                0,
                0,0,
                getWidth(), getHeight(),
                getGLFormatType(getFormat()),
                getGLDataType(getFormat()),
                data);
        checkError("glTexSubImage2D");
        unbind();
    }

    @Override
    public void setData(FloatBuffer data) {
        bind();
        glTexSubImage2D(
                GL_TEXTURE_2D,
                0,
                0,0,
                getWidth(), getHeight(),
                getGLFormatType(getFormat()),
                getGLDataType(getFormat()),
                data);
        checkError("glTexSubImage2D");
        unbind();
    }

    @Override
    public void setData(DoubleBuffer data) {
        bind();
        glTexSubImage2D(
                GL_TEXTURE_2D,
                0,
                0,0,
                getWidth(), getHeight(),
                getGLFormatType(getFormat()),
                getGLDataType(getFormat()),
                data);
        checkError("glTexSubImage2D");
        unbind();
    }

    @Override
    public int getTextureId() {
        return textureId;
    }
}
