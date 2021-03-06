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
    private Buffer data;

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
        generateMipmap();
        unbind();

        if(data != null) {
            if(data instanceof ByteBuffer) {
                writeData((ByteBuffer)data);
            }
            else if(data instanceof ShortBuffer) {
                writeData((ShortBuffer)data);
            }
            else if(data instanceof IntBuffer) {
                writeData((IntBuffer)data);
            }
            else if(data instanceof FloatBuffer) {
                writeData((FloatBuffer)data);
            }
            else if(data instanceof DoubleBuffer) {
                writeData((DoubleBuffer)data);
            }
        }
    }

    private void generateMipmap() {
        if(isMipmapping()) {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, 1000);
            glGenerateMipmap(GL_TEXTURE_2D);
            checkError("glGenerateMipmap");
        }
        else {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL, 0);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, 0);
        }
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
    public void writeData(ByteBuffer data) {
        this.data = data;
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
        generateMipmap();
        unbind();
    }

    @Override
    public void writeData(ShortBuffer data) {
        this.data = data;
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
        generateMipmap();
        unbind();
    }

    @Override
    public void writeData(IntBuffer data) {
        this.data = data;
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
        generateMipmap();
        unbind();
    }

    @Override
    public void writeData(FloatBuffer data) {
        this.data = data;
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
        generateMipmap();
        unbind();
    }

    @Override
    public void writeData(DoubleBuffer data) {
        this.data = data;
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
        generateMipmap();
        unbind();
    }

    @Override
    public int getTextureId() {
        return textureId;
    }
}
